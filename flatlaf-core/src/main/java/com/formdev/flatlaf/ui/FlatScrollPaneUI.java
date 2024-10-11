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
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.Rectangle;
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
import javax.swing.ScrollPaneLayout;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollPaneUI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.UIScale;

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
		int arc = UIManager.getInt( "ScrollPane.arc" );
		LookAndFeel.installProperty( c, "opaque", focusWidth == 0 && arc == 0 );

		// install layout manager
		LayoutManager layout = c.getLayout();
		if( layout != null && layout.getClass() == ScrollPaneLayout.UIResource.class )
			c.setLayout( createScrollPaneLayout() );

		installStyle();

		MigLayoutVisualPadding.install( scrollpane );
	}

	@Override
	public void uninstallUI( JComponent c ) {
		MigLayoutVisualPadding.uninstall( scrollpane );

		// uninstall layout manager
		if( c.getLayout() instanceof FlatScrollPaneLayout )
			c.setLayout( new ScrollPaneLayout.UIResource() );

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

	/**
	 * @since 3.3
	 */
	protected FlatScrollPaneLayout createScrollPaneLayout() {
		return new FlatScrollPaneLayout();
	}

	@Override
	protected MouseWheelListener createMouseWheelListener() {
		MouseWheelListener superListener = super.createMouseWheelListener();
		return e -> {
			if( isSmoothScrollingEnabled() &&
				scrollpane.isWheelScrollingEnabled() &&
				e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL &&
				e.getPreciseWheelRotation() != 0 &&
				e.getPreciseWheelRotation() != e.getWheelRotation() )
			{
				mouseWheelMovedSmooth( e );
			} else
				superListener.mouseWheelMoved( e );
		};
	}

	protected boolean isSmoothScrollingEnabled() {
		Object smoothScrolling = scrollpane.getClientProperty( FlatClientProperties.SCROLL_PANE_SMOOTH_SCROLLING );
		if( smoothScrolling instanceof Boolean )
			return (Boolean) smoothScrolling;

		// Note: Getting UI value "ScrollPane.smoothScrolling" here to allow
		// applications to turn smooth scrolling on or off at any time
		// (e.g. in application options dialog).
		return UIManager.getBoolean( "ScrollPane.smoothScrolling" );
	}

	private void mouseWheelMovedSmooth( MouseWheelEvent e ) {
		// return if there is no viewport
		JViewport viewport = scrollpane.getViewport();
		if( viewport == null )
			return;

		// find scrollbar to scroll
		JScrollBar scrollbar = scrollpane.getVerticalScrollBar();
		if( scrollbar == null || !scrollbar.isVisible() || e.isShiftDown() ) {
			scrollbar = scrollpane.getHorizontalScrollBar();
			if( scrollbar == null || !scrollbar.isVisible() )
				return;
		}

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
			Rectangle visibleRect = new Rectangle( viewport.getExtentSize() );
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
						HiDPIUtils.repaint( vsb );
					}
					if( hsb != null ) {
						hsb.revalidate();
						HiDPIUtils.repaint( hsb );
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
						getView( scrollpane ) instanceof JTable )
					{
						((JButton)corner).setBorder( BorderFactory.createEmptyBorder() );
						((JButton)corner).setFocusable( false );
					}
					break;

				case FlatClientProperties.OUTLINE:
					HiDPIUtils.repaint( scrollpane );
					break;

				case FlatClientProperties.STYLE:
				case FlatClientProperties.STYLE_CLASS:
					installStyle();
					scrollpane.revalidate();
					HiDPIUtils.repaint( scrollpane );
					break;

				case "border":
					Object newBorder = e.getNewValue();
					if( newBorder != null && newBorder == UIManager.getBorder( "Table.scrollPaneBorder" ) ) {
						// JTable.configureEnclosingScrollPaneUI() replaces the scrollpane border
						// with another one --> re-apply style on new border
						borderShared = null;
						installStyle();
						scrollpane.revalidate();
						HiDPIUtils.repaint( scrollpane );
					}
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
		if( key.equals( "focusWidth" ) || key.equals( "arc" ) ) {
			int focusWidth = (value instanceof Integer) ? (int) value : UIManager.getInt( "Component.focusWidth" );
			int arc = (value instanceof Integer) ? (int) value : UIManager.getInt( "ScrollPane.arc" );
			LookAndFeel.installProperty( scrollpane, "opaque", focusWidth == 0 && arc == 0 );
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

		// if view is rounded, paint rounded background with view background color
		// to ensure that free areas at left and right have same color as view
		Component view;
		float arc = getBorderArc( scrollpane );
		if( arc > 0 && (view = getView( scrollpane )) != null ) {
			float focusWidth = FlatUIUtils.getBorderFocusWidth( c );

			g.setColor( view.getBackground() );

			Object[] oldRenderingHints = FlatUIUtils.setRenderingHints( g );
			FlatUIUtils.paintComponentBackground( (Graphics2D) g, 0, 0, c.getWidth(), c.getHeight(), focusWidth, arc );
			FlatUIUtils.resetRenderingHints( g, oldRenderingHints );
		}

		paint( g, c );
	}

	@Override
	public void paint( Graphics g, JComponent c ) {
		Border viewportBorder = scrollpane.getViewportBorder();
		if( viewportBorder != null ) {
			Rectangle r = scrollpane.getViewportBorderBounds();
			int padding = getBorderLeftRightPadding( scrollpane );
			JScrollBar vsb = scrollpane.getVerticalScrollBar();
			if( padding > 0 &&
				vsb != null && vsb.isVisible() &&
				scrollpane.getLayout() instanceof FlatScrollPaneLayout &&
				((FlatScrollPaneLayout)scrollpane.getLayout()).canIncreaseViewportWidth( scrollpane ) )
			{
				boolean ltr = scrollpane.getComponentOrientation().isLeftToRight();
				int extraWidth = Math.min( padding, vsb.getWidth() );
				viewportBorder.paintBorder( scrollpane, g, r.x - (ltr ? 0 : extraWidth), r.y, r.width + extraWidth, r.height );
			} else
				viewportBorder.paintBorder( scrollpane, g, r.x, r.y, r.width, r.height );
		}
	}

	/** @since 1.3 */
	public static boolean isPermanentFocusOwner( JScrollPane scrollPane ) {
		Component view = getView( scrollPane );
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

	static Component getView( JScrollPane scrollPane ) {
		JViewport viewport = scrollPane.getViewport();
		return (viewport != null) ? viewport.getView() : null;
	}

	private static float getBorderArc( JScrollPane scrollPane ) {
		Border border = scrollPane.getBorder();
		return (border instanceof FlatScrollPaneBorder)
			? UIScale.scale( (float) ((FlatScrollPaneBorder)border).getArc( scrollPane ) )
			: 0;
	}

	private static int getBorderLeftRightPadding( JScrollPane scrollPane ) {
		Border border = scrollPane.getBorder();
		return (border instanceof FlatScrollPaneBorder)
			? ((FlatScrollPaneBorder)border).getLeftRightPadding( scrollPane )
			: 0;
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
			if( scrollpane.getBorder() instanceof FlatBorder )
				HiDPIUtils.repaint( scrollpane );
		}

		@Override
		public void focusLost( FocusEvent e ) {
			// necessary to update focus border
			if( scrollpane.getBorder() instanceof FlatBorder )
				HiDPIUtils.repaint( scrollpane );
		}
	}

	//---- class FlatScrollPaneLayout -----------------------------------------

	/**
	 * @since 3.3
	 */
	protected static class FlatScrollPaneLayout
		extends ScrollPaneLayout.UIResource
	{
		@Override
		public void layoutContainer( Container parent ) {
			super.layoutContainer( parent );

			JScrollPane scrollPane = (JScrollPane) parent;
			int padding = getBorderLeftRightPadding( scrollPane );
			if( padding > 0 && vsb != null && vsb.isVisible() ) {
				// move vertical scrollbar to trailing edge
				Insets insets = scrollPane.getInsets();
				Rectangle r = vsb.getBounds();
				int y = Math.max( r.y, insets.top + padding );
				int y2 = Math.min( r.y + r.height, scrollPane.getHeight() - insets.bottom - padding );
				boolean ltr = scrollPane.getComponentOrientation().isLeftToRight();

				vsb.setBounds( r.x + (ltr ? padding : -padding), y, r.width, y2 - y );

				// increase width of viewport, column header and horizontal scrollbar
				if( canIncreaseViewportWidth( scrollPane ) ) {
					int extraWidth = Math.min( padding, vsb.getWidth() );
					resizeViewport( viewport, extraWidth, ltr );
					resizeViewport( colHead, extraWidth, ltr );
					resizeViewport( hsb, extraWidth, ltr );
				}
			}
		}

		boolean canIncreaseViewportWidth( JScrollPane scrollPane ) {
			return scrollPane.getComponentOrientation().isLeftToRight()
				? !isCornerVisible( upperRight ) && !isCornerVisible( lowerRight )
				: !isCornerVisible( upperLeft ) && !isCornerVisible( lowerLeft );
		}

		private static boolean isCornerVisible( Component corner ) {
			return corner != null &&
				corner.getWidth() > 0 &&
				corner.getHeight() > 0 &&
				corner.isVisible();
		}

		private static void resizeViewport( Component c, int extraWidth, boolean ltr ) {
			if( c == null )
				return;

			Rectangle vr = c.getBounds();
			c.setBounds( vr.x - (ltr ? 0 : extraWidth), vr.y, vr.width + extraWidth, vr.height );
		}
	}
}
