/*
 * Copyright 2019 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.formdev.flatlaf.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.LookAndFeel;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollPaneUI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatSystemProperties;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.Animator;
import com.formdev.flatlaf.util.LoggingFacade;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JScrollPane}.
 *
 * <!-- BasicScrollPaneUI -->
 *
 * @uiDefault ScrollPane.font				Font	unused
 * @uiDefault ScrollPane.background			Color
 * @uiDefault ScrollPane.foreground			Color	unused
 * @uiDefault ScrollPane.border				Border
 * @uiDefault ScrollPane.viewportBorder		Border
 *
 * <!-- FlatScrollPaneUI -->
 *
 * @uiDefault ScrollPane.smoothScrolling		boolean
 *
 * @author Karl Tauber
 */
public class FlatScrollPaneUI
	extends BasicScrollPaneUI
	implements StyleableUI
{
	// only used via styling (not in UI defaults, but has likewise client properties)
	/** @since 2 */ @Styleable protected Boolean showButtons;

	private Handler handler;

	private Map<String, Object> oldStyleValues;
	private AtomicBoolean borderShared;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatScrollPaneUI();
	}

	@Override
	public void installUI( JComponent c ) {
		if( FlatUIUtils.needsLightAWTPeer( c ) )
			FlatUIUtils.runWithLightAWTPeerUIDefaults( () -> installUIImpl( c ) );
		else
			installUIImpl( c );
	}

	private void installUIImpl( JComponent c ) {
		super.installUI( c );

		int focusWidth = UIManager.getInt( "Component.focusWidth" );
		LookAndFeel.installProperty( c, "opaque", focusWidth == 0 );

		installStyle();

		MigLayoutVisualPadding.install( scrollpane );
	}

	@Override
	public void uninstallUI( JComponent c ) {
		MigLayoutVisualPadding.uninstall( scrollpane );

		super.uninstallUI( c );

		oldStyleValues = null;
		borderShared = null;
	}

	@Override
	protected void installListeners( JScrollPane c ) {
		super.installListeners( c );

		addViewportListeners( scrollpane.getViewport() );
	}

	@Override
	protected void uninstallListeners( JComponent c ) {
		super.uninstallListeners( c );

		removeViewportListeners( scrollpane.getViewport() );

		handler = null;
	}

	@Override
	protected MouseWheelListener createMouseWheelListener() {
		MouseWheelListener superListener = super.createMouseWheelListener();
		return e -> {
			if( isSmoothScrollingEnabled() &&
				scrollpane.isWheelScrollingEnabled() )
			{
				if( e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL &&
					isPreciseWheelEvent( e ) )
				{
					// precise scrolling
					mouseWheelMovedPrecise( e );
				} else {
					// smooth scrolling
					JScrollBar scrollBar = findScrollBarToScroll( e );
					if( scrollBar != null && scrollBar.getUI() instanceof FlatScrollBarUI ) {
						FlatScrollBarUI ui = (FlatScrollBarUI) scrollBar.getUI();
						ui.runAndSetValueAnimated( () -> {
							superListener.mouseWheelMoved( e );
						} );
					} else
						superListener.mouseWheelMoved( e );
				}
			} else
				superListener.mouseWheelMoved( e );
		};
	}

	protected boolean isSmoothScrollingEnabled() {
		if( !Animator.useAnimation() || !FlatSystemProperties.getBoolean( FlatSystemProperties.SMOOTH_SCROLLING, true ) )
			return false;

		Object smoothScrolling = scrollpane.getClientProperty( FlatClientProperties.SCROLL_PANE_SMOOTH_SCROLLING );
		if( smoothScrolling instanceof Boolean )
			return (Boolean) smoothScrolling;

		// Note: Getting UI value "ScrollPane.smoothScrolling" here to allow
		// applications to turn smooth scrolling on or off at any time
		// (e.g. in application options dialog).
		return UIManager.getBoolean( "ScrollPane.smoothScrolling" );
	}

	private long lastPreciseWheelWhen;

	private boolean isPreciseWheelEvent( MouseWheelEvent e ) {
		double preciseWheelRotation = e.getPreciseWheelRotation();
		if( preciseWheelRotation != 0 && preciseWheelRotation != e.getWheelRotation() ) {
			// precise wheel event
			lastPreciseWheelWhen = e.getWhen();
			return true;
		}

		// If a non-precise wheel event occurs shortly after a precise wheel event,
		// then it is probably still a precise wheel but the precise value
		// is by chance an integer value (e.g. 1.0 or 2.0).
		// Not handling this special case, would start an animation for smooth scrolling,
		// which would be interrupted soon when the next precise wheel event occurs.
		// This would result in jittery scrolling. E.g. on a MacBook using Trackpad or Magic Mouse.
		if( e.getWhen() - lastPreciseWheelWhen < 1000 )
			return true;

		// non-precise wheel event
		lastPreciseWheelWhen = 0;
		return false;
	}

	private void mouseWheelMovedPrecise( MouseWheelEvent e ) {
		// return if there is no viewport
		JViewport viewport = scrollpane.getViewport();
		if( viewport == null )
			return;

		// find scrollbar to scroll
		JScrollBar scrollbar = findScrollBarToScroll( e );
		if( scrollbar == null )
			return;

		// consume event
		e.consume();

		// get precise wheel rotation
		double rotation = e.getPreciseWheelRotation();

		// get unit increment
		int unitIncrement;
		int orientation = scrollbar.getOrientation();
		Component view = viewport.getView();
		if( view instanceof Scrollable ) {
			Scrollable scrollable = (Scrollable) view;

			// Use (0, 0) view position to obtain a constant unit increment of first item.
			// Unit increment may be different for each item.
			Rectangle visibleRect = new Rectangle( viewport.getViewSize() );
			unitIncrement = scrollable.getScrollableUnitIncrement( visibleRect, orientation, 1 );

			if( unitIncrement > 0 ) {
				// For the case that the first item (e.g. in a list) is larger
				// than the other items (e.g. themes list in FlatLaf Demo),
				// get the unit increment of the second item and use the smaller one.
				if( orientation == SwingConstants.VERTICAL ) {
					visibleRect.y += unitIncrement;
					visibleRect.height -= unitIncrement;
				} else {
					visibleRect.x += unitIncrement;
					visibleRect.width -= unitIncrement;
				}
				int unitIncrement2 = scrollable.getScrollableUnitIncrement( visibleRect, orientation, 1 );
				if( unitIncrement2 > 0 )
					unitIncrement = Math.min( unitIncrement, unitIncrement2 );
			}
		} else {
			int direction = rotation < 0 ? -1 : 1;
			unitIncrement = scrollbar.getUnitIncrement( direction );
		}

		// get viewport width/height (the visible width/height)
		int viewportWH = (orientation == SwingConstants.VERTICAL)
			? viewport.getHeight()
			: viewport.getWidth();

		// limit scroll increment to viewport width/height
		// - if scroll amount is set to a large value in OS settings
		// - for large unit increments in small viewports (e.g. horizontal scrolling in file chooser)
		int scrollIncrement = Math.min( unitIncrement * e.getScrollAmount(), viewportWH );

		// compute relative delta
		double delta = rotation * scrollIncrement;
		int idelta = (int) Math.round( delta );

		// scroll at least one pixel to avoid "hanging"
		// - for "super-low-speed" scrolling (move fingers very slowly on trackpad)
		// - if unit increment is very small (e.g. 1 if scroll view does not implement
		//   javax.swing.Scrollable interface)
		if( idelta == 0 ) {
			if( rotation > 0 )
				idelta = 1;
			else if( rotation < 0 )
				idelta = -1;
		}

		// compute new value
		int value = scrollbar.getValue();
		int minValue = scrollbar.getMinimum();
		int maxValue = scrollbar.getMaximum() - scrollbar.getModel().getExtent();
		int newValue = Math.max( minValue, Math.min( value + idelta, maxValue ) );

		// set new value
		if( newValue != value )
			scrollbar.setValue( newValue );

/*debug
		System.out.println( String.format( "%s  %4d  %9f  /  %3d * %d = %3d  [%3d]  /  %8.2f %5d  /  %4d --> %4d  [%d, %d]",
			(orientation == SwingConstants.VERTICAL) ? "V" : "H",
			e.getWheelRotation(),
			e.getPreciseWheelRotation(),
			unitIncrement,
			e.getScrollAmount(),
			scrollIncrement,
			viewportWH,
			delta,
			idelta,
			value,
			newValue,
			minValue,
			maxValue ) );
*/
	}

	private JScrollBar findScrollBarToScroll( MouseWheelEvent e ) {
		JScrollBar scrollBar = scrollpane.getVerticalScrollBar();
		if( scrollBar == null || !scrollBar.isVisible() || e.isShiftDown() ) {
			scrollBar = scrollpane.getHorizontalScrollBar();
			if( scrollBar == null || !scrollBar.isVisible() )
				return null;
		}
		return scrollBar;
	}

	@Override
	protected PropertyChangeListener createPropertyChangeListener() {
		PropertyChangeListener superListener = super.createPropertyChangeListener();
		return e -> {
			superListener.propertyChange( e );

			switch( e.getPropertyName() ) {
				case FlatClientProperties.SCROLL_BAR_SHOW_BUTTONS:
					JScrollBar vsb = scrollpane.getVerticalScrollBar();
					JScrollBar hsb = scrollpane.getHorizontalScrollBar();
					if( vsb != null ) {
						vsb.revalidate();
						vsb.repaint();
					}
					if( hsb != null ) {
						hsb.revalidate();
						hsb.repaint();
					}
					break;

				case ScrollPaneConstants.LOWER_LEFT_CORNER:
				case ScrollPaneConstants.LOWER_RIGHT_CORNER:
				case ScrollPaneConstants.UPPER_LEFT_CORNER:
				case ScrollPaneConstants.UPPER_RIGHT_CORNER:
					// remove border from buttons added to corners
					Object corner = e.getNewValue();
					if( corner instanceof JButton &&
						((JButton)corner).getBorder() instanceof FlatButtonBorder &&
						scrollpane.getViewport() != null &&
						scrollpane.getViewport().getView() instanceof JTable )
					{
						((JButton)corner).setBorder( BorderFactory.createEmptyBorder() );
						((JButton)corner).setFocusable( false );
					}
					break;

				case FlatClientProperties.OUTLINE:
					scrollpane.repaint();
					break;

				case FlatClientProperties.STYLE:
				case FlatClientProperties.STYLE_CLASS:
					installStyle();
					scrollpane.revalidate();
					scrollpane.repaint();
					break;
			}
		};
	}

	private Handler getHandler() {
		if( handler == null )
			handler = new Handler();
		return handler;
	}

	/** @since 2 */
	protected void installStyle() {
		try {
			applyStyle( FlatStylingSupport.getResolvedStyle( scrollpane, "ScrollPane" ) );
		} catch( RuntimeException ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	/** @since 2 */
	protected void applyStyle( Object style ) {
		oldStyleValues = FlatStylingSupport.parseAndApply( oldStyleValues, style, this::applyStyleProperty );
	}

	/** @since 2 */
	protected Object applyStyleProperty( String key, Object value ) {
		if( key.equals( "focusWidth" ) ) {
			int focusWidth = (value instanceof Integer) ? (int) value : UIManager.getInt( "Component.focusWidth" );
			LookAndFeel.installProperty( scrollpane, "opaque", focusWidth == 0 );
		}

		if( borderShared == null )
			borderShared = new AtomicBoolean( true );
		return FlatStylingSupport.applyToAnnotatedObjectOrBorder( this, key, value, scrollpane, borderShared );
	}

	/** @since 2 */
	@Override
	public Map<String, Class<?>> getStyleableInfos( JComponent c ) {
		return FlatStylingSupport.getAnnotatedStyleableInfos( this, scrollpane.getBorder() );
	}

	/** @since 2.5 */
	@Override
	public Object getStyleableValue( JComponent c, String key ) {
		return FlatStylingSupport.getAnnotatedStyleableValue( this, scrollpane.getBorder(), key );
	}

	@Override
	protected void updateViewport( PropertyChangeEvent e ) {
		super.updateViewport( e );

		JViewport oldViewport = (JViewport) e.getOldValue();
		JViewport newViewport = (JViewport) e.getNewValue();

		removeViewportListeners( oldViewport );
		addViewportListeners( newViewport );
	}

	private void addViewportListeners( JViewport viewport ) {
		if( viewport == null )
			return;

		viewport.addContainerListener( getHandler() );

		Component view = viewport.getView();
		if( view != null )
			view.addFocusListener( getHandler() );
	}

	private void removeViewportListeners( JViewport viewport ) {
		if( viewport == null )
			return;

		viewport.removeContainerListener( getHandler() );

		Component view = viewport.getView();
		if( view != null )
			view.removeFocusListener( getHandler() );
	}

	@Override
	public void update( Graphics g, JComponent c ) {
		if( c.isOpaque() ) {
			FlatUIUtils.paintParentBackground( g, c );

			// paint background so that corners have same color as scroll bars
			Insets insets = c.getInsets();
			g.setColor( c.getBackground() );
			g.fillRect( insets.left, insets.top,
				c.getWidth() - insets.left - insets.right,
				c.getHeight() - insets.top - insets.bottom );
		}

		paint( g, c );
	}

	/** @since 1.3 */
	public static boolean isPermanentFocusOwner( JScrollPane scrollPane ) {
		JViewport viewport = scrollPane.getViewport();
		Component view = (viewport != null) ? viewport.getView() : null;
		if( view == null )
			return false;

		// check whether view is focus owner
		if( FlatUIUtils.isPermanentFocusOwner( view ) )
			return true;

		// check whether editor component in JTable or JTree is focus owner
		if( (view instanceof JTable && ((JTable)view).isEditing()) ||
			(view instanceof JTree && ((JTree)view).isEditing()) )
		{
			Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
			if( focusOwner != null )
				return SwingUtilities.isDescendingFrom( focusOwner, view );
		}

		return false;
	}

	@Override
	protected void syncScrollPaneWithViewport() {
		// if the viewport has been scrolled by using JComponent.scrollRectToVisible()
		// (e.g. by moving selection), then it is necessary to update the scroll bar values
		if( isSmoothScrollingEnabled() ) {
			runAndSyncScrollBarValueAnimated( scrollpane.getVerticalScrollBar(), 0, false, () -> {
				runAndSyncScrollBarValueAnimated( scrollpane.getHorizontalScrollBar(), 1, false, () -> {
					super.syncScrollPaneWithViewport();
				} );
			} );
		} else
			super.syncScrollPaneWithViewport();
	}

	/**
	 * Runs the given runnable, if smooth scrolling is enabled, with disabled
	 * viewport blitting mode and with scroll bar value set to "target" value.
	 * This is necessary when calculating new view position during animation.
	 * Otherwise calculation would use wrong view position and (repeating) scrolling
	 * would be much slower than without smooth scrolling.
	 */
	private void runWithScrollBarsTargetValues( boolean blittingOnly, Runnable r ) {
		if( isSmoothScrollingEnabled() ) {
			runWithoutBlitting( scrollpane, () -> {
				if( blittingOnly )
					r.run();
				else {
					runAndSyncScrollBarValueAnimated( scrollpane.getVerticalScrollBar(), 0, true, () -> {
						runAndSyncScrollBarValueAnimated( scrollpane.getHorizontalScrollBar(), 1, true, r );
					} );
				}
			} );
		} else
			r.run();
	}

	private void runAndSyncScrollBarValueAnimated( JScrollBar sb, int i, boolean useTargetValue, Runnable r ) {
		if( inRunAndSyncValueAnimated[i] || sb == null || !(sb.getUI() instanceof FlatScrollBarUI) ) {
			r.run();
			return;
		}

		inRunAndSyncValueAnimated[i] = true;

		int oldValue = sb.getValue();
		int oldVisibleAmount = sb.getVisibleAmount();
		int oldMinimum = sb.getMinimum();
		int oldMaximum = sb.getMaximum();

		FlatScrollBarUI ui = (FlatScrollBarUI) sb.getUI();
		if( useTargetValue && ui.getTargetValue() != Integer.MIN_VALUE )
			sb.setValue( ui.getTargetValue() );

		r.run();

		int newValue = sb.getValue();

		if( newValue != oldValue &&
			sb.getVisibleAmount() == oldVisibleAmount &&
			sb.getMinimum() == oldMinimum &&
			sb.getMaximum() == oldMaximum &&
			sb.getUI() instanceof FlatScrollBarUI )
		{
			ui.setValueAnimated( oldValue, newValue );
		}

		inRunAndSyncValueAnimated[i] = false;
	}

	private final boolean[] inRunAndSyncValueAnimated = new boolean[2];

	/**
	 * Runs the given runnable with disabled viewport blitting mode.
	 * If blitting mode is enabled, the viewport immediately repaints parts of the
	 * view if the view position is changed via JViewport.setViewPosition().
	 * This causes scrolling artifacts if smooth scrolling is enabled and the view position
	 * is "temporary" changed to its new target position, changed back to its old position
	 * and again moved animated to the target position.
	 */
	static void runWithoutBlitting( Container scrollPane, Runnable r ) {
		// prevent the viewport to immediately repaint using blitting
		JViewport viewport = (scrollPane instanceof JScrollPane) ? ((JScrollPane)scrollPane).getViewport() : null;
		boolean isBlitScrollMode = (viewport != null) ? viewport.getScrollMode() == JViewport.BLIT_SCROLL_MODE : false;
		if( isBlitScrollMode )
			viewport.setScrollMode( JViewport.SIMPLE_SCROLL_MODE );

		try {
			r.run();
		} finally {
			if( isBlitScrollMode )
				viewport.setScrollMode( JViewport.BLIT_SCROLL_MODE );
		}
	}

	public static void installSmoothScrollingDelegateActions( JComponent c, boolean blittingOnly, String... actionKeys ) {
		// get shared action map, used for all components of same type
		ActionMap map = SwingUtilities.getUIActionMap( c );
		if( map == null )
			return;

		// install actions, but only if not already installed
		for( String actionKey : actionKeys )
			installSmoothScrollingDelegateAction( map, blittingOnly, actionKey );
	}

	private static void installSmoothScrollingDelegateAction( ActionMap map, boolean blittingOnly, String actionKey ) {
		Action oldAction = map.get( actionKey );
		if( oldAction == null || oldAction instanceof SmoothScrollingDelegateAction )
			return; // not found or already installed

		map.put( actionKey, new SmoothScrollingDelegateAction( oldAction, blittingOnly ) );
	}

	//---- class Handler ------------------------------------------------------

	/**
	 * ContainerListener is added to JViewport to keep focus listener on view up-to-date.
	 * FocusListener is added to view for repainting when view gets focused.
	 */
	private class Handler
		implements ContainerListener, FocusListener
	{
		@Override
		public void componentAdded( ContainerEvent e ) {
			e.getChild().addFocusListener( this );
		}

		@Override
		public void componentRemoved( ContainerEvent e ) {
			e.getChild().removeFocusListener( this );
		}

		@Override
		public void focusGained( FocusEvent e ) {
			// necessary to update focus border
			scrollpane.repaint();
		}

		@Override
		public void focusLost( FocusEvent e ) {
			// necessary to update focus border
			scrollpane.repaint();
		}
	}

	//---- class SmoothScrollingDelegateAction --------------------------------

	/**
	 * Used to run component actions with disabled blitting mode and
	 * with scroll bar target values.
	 */
	private static class SmoothScrollingDelegateAction
		extends FlatUIAction
	{
		private final boolean blittingOnly;

		private SmoothScrollingDelegateAction( Action delegate, boolean blittingOnly ) {
			super( delegate );
			this.blittingOnly = blittingOnly;
		}

		@Override
		public void actionPerformed( ActionEvent e ) {
			Object source = e.getSource();
			JScrollPane scrollPane = (source instanceof Component)
				? (JScrollPane) SwingUtilities.getAncestorOfClass( JScrollPane.class, (Component) source )
				: null;
			if( scrollPane != null && scrollPane.getUI() instanceof FlatScrollPaneUI ) {
				((FlatScrollPaneUI)scrollPane.getUI()).runWithScrollBarsTargetValues( blittingOnly,
					() -> delegate.actionPerformed( e ) );
			} else
				delegate.actionPerformed( e );
		}
	}
}
