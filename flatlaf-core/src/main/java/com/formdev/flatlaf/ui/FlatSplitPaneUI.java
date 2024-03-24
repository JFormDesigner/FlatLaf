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

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.ui.FlatStylingSupport.UnknownStyleException;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JSplitPane}.
 *
 * <!-- BasicSplitPaneUI -->
 *
 * @uiDefault SplitPane.background						Color
 * @uiDefault SplitPane.foreground						Color unused
 * @uiDefault SplitPane.dividerSize						int
 * @uiDefault SplitPane.border							Border
 * @uiDefault SplitPaneDivider.border					Border
 * @uiDefault SplitPaneDivider.draggingColor			Color	only used if continuousLayout is false
 *
 * <!-- BasicSplitPaneDivider -->
 *
 * @uiDefault SplitPane.oneTouchButtonSize				int
 * @uiDefault SplitPane.oneTouchButtonOffset			int
 * @uiDefault SplitPane.centerOneTouchButtons			boolean
 * @uiDefault SplitPane.supportsOneTouchButtons			boolean	optional; default is true
 *
 * <!-- JSplitPane -->
 *
 * @uiDefault SplitPane.continuousLayout				boolean
 *
 * <!-- FlatSplitPaneUI -->
 *
 * @uiDefault Component.arrowType						String	chevron (default) or triangle
 * @uiDefault SplitPaneDivider.hoverColor				Color	optional
 * @uiDefault SplitPaneDivider.pressedColor				Color	optional
 * @uiDefault SplitPaneDivider.oneTouchArrowColor		Color
 * @uiDefault SplitPaneDivider.oneTouchHoverArrowColor	Color
 * @uiDefault SplitPaneDivider.oneTouchPressedArrowColor Color
 * @uiDefault SplitPaneDivider.style					String	grip (default) or plain
 * @uiDefault SplitPaneDivider.gripColor				Color
 * @uiDefault SplitPaneDivider.gripDotCount				int
 * @uiDefault SplitPaneDivider.gripDotSize				int
 * @uiDefault SplitPaneDivider.gripGap					int
 *
 * @author Karl Tauber
 */
public class FlatSplitPaneUI
	extends BasicSplitPaneUI
	implements StyleableUI, FlatTitlePane.TitleBarCaptionHitTest
{
	@Styleable protected String arrowType;
	/** @since 3.3 */ @Styleable protected Color draggingColor;
	@Styleable protected Color oneTouchArrowColor;
	@Styleable protected Color oneTouchHoverArrowColor;
	@Styleable protected Color oneTouchPressedArrowColor;

	private Map<String, Object> oldStyleValues;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatSplitPaneUI();
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		installStyle();
	}

	@Override
	protected void installDefaults() {
		arrowType = UIManager.getString( "Component.arrowType" );

		draggingColor = UIManager.getColor( "SplitPaneDivider.draggingColor" );

		// get one-touch colors before invoking super.installDefaults() because they are
		// used in there on LaF switching
		oneTouchArrowColor = UIManager.getColor( "SplitPaneDivider.oneTouchArrowColor" );
		oneTouchHoverArrowColor = UIManager.getColor( "SplitPaneDivider.oneTouchHoverArrowColor" );
		oneTouchPressedArrowColor = UIManager.getColor( "SplitPaneDivider.oneTouchPressedArrowColor" );

		super.installDefaults();
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		draggingColor = null;

		oneTouchArrowColor = null;
		oneTouchHoverArrowColor = null;
		oneTouchPressedArrowColor = null;

		oldStyleValues = null;
	}

	@Override
	protected PropertyChangeListener createPropertyChangeListener() {
		return FlatStylingSupport.createPropertyChangeListener( splitPane, this::installStyle,
			super.createPropertyChangeListener() );
	}

	@Override
	public BasicSplitPaneDivider createDefaultDivider() {
		return new FlatSplitPaneDivider( this );
	}

	/** @since 2 */
	protected void installStyle() {
		try {
			applyStyle( FlatStylingSupport.getResolvedStyle( splitPane, "SplitPane" ) );
		} catch( RuntimeException ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	/** @since 2 */
	protected void applyStyle( Object style ) {
		oldStyleValues = FlatStylingSupport.parseAndApply( oldStyleValues, style, this::applyStyleProperty );

		if( divider instanceof FlatSplitPaneDivider )
			((FlatSplitPaneDivider)divider).updateStyle();
	}

	/** @since 2 */
	protected Object applyStyleProperty( String key, Object value ) {
		try {
			if( divider instanceof FlatSplitPaneDivider )
				return ((FlatSplitPaneDivider)divider).applyStyleProperty( key, value );
		} catch( UnknownStyleException ex ) {
			// ignore
		}
		return FlatStylingSupport.applyToAnnotatedObjectOrComponent( this, splitPane, key, value );
	}

	/** @since 2 */
	@Override
	public Map<String, Class<?>> getStyleableInfos( JComponent c ) {
		Map<String, Class<?>> infos = FlatStylingSupport.getAnnotatedStyleableInfos( this );
		if( divider instanceof FlatSplitPaneDivider )
			infos.putAll( ((FlatSplitPaneDivider)divider).getStyleableInfos() );
		return infos;
	}

	/** @since 2.5 */
	@Override
	public Object getStyleableValue( JComponent c, String key ) {
		if( divider instanceof FlatSplitPaneDivider ) {
			Object value = ((FlatSplitPaneDivider)divider).getStyleableValue( key );
			if( value != null )
				return value;
		}
		return FlatStylingSupport.getAnnotatedStyleableValue( this, key );
	}

	@Override
	protected Component createDefaultNonContinuousLayoutDivider() {
		// only used for non-continuous layout if left or right component is heavy weight
		return new Canvas() {
			@Override
			public void paint( Graphics g ) {
				if( !isContinuousLayout() && getLastDragLocation() != -1 )
					paintDragDivider( g, 0 );
			}
		};
	}

	@Override
	public void finishedPaintingChildren( JSplitPane sp, Graphics g ) {
		if( sp == splitPane && getLastDragLocation() != -1 && !isContinuousLayout() && !draggingHW )
			paintDragDivider( g, getLastDragLocation() );
	}

	private void paintDragDivider( Graphics g, int dividerLocation ) {
		// divider bounds
		boolean horizontal = (getOrientation() == JSplitPane.HORIZONTAL_SPLIT);
		int x = horizontal ? dividerLocation : 0;
		int y = !horizontal ? dividerLocation : 0;
		int width = horizontal ? dividerSize : splitPane.getWidth();
		int height = !horizontal ? dividerSize : splitPane.getHeight();

		// paint background
		g.setColor( FlatUIUtils.deriveColor( draggingColor, splitPane.getBackground() ) );
		g.fillRect( x, y, width, height );

		// paint divider style (e.g. grip)
		if( divider instanceof FlatSplitPaneDivider )
			((FlatSplitPaneDivider)divider).paintStyle( g, x, y, width, height );
	}

	//---- interface FlatTitlePane.TitleBarCaptionHitTest ----

	/** @since 3.4 */
	@Override
	public Boolean isTitleBarCaptionAt( int x, int y ) {
		// necessary because BasicSplitPaneDivider adds some mouse listeners for dragging divider
		return null; // check children
	}

	//---- class FlatSplitPaneDivider -----------------------------------------

	protected class FlatSplitPaneDivider
		extends BasicSplitPaneDivider
	{
		@Styleable protected String style = UIManager.getString( "SplitPaneDivider.style" );
		/** @since 3.3 */ @Styleable protected Color hoverColor = UIManager.getColor( "SplitPaneDivider.hoverColor" );
		/** @since 3.3 */ @Styleable protected Color pressedColor = UIManager.getColor( "SplitPaneDivider.pressedColor" );
		@Styleable protected Color gripColor = UIManager.getColor( "SplitPaneDivider.gripColor" );
		@Styleable protected int gripDotCount = FlatUIUtils.getUIInt( "SplitPaneDivider.gripDotCount", 3 );
		@Styleable protected int gripDotSize = FlatUIUtils.getUIInt( "SplitPaneDivider.gripDotSize", 3 );
		@Styleable protected int gripGap = FlatUIUtils.getUIInt( "SplitPaneDivider.gripGap", 2 );

		protected FlatSplitPaneDivider( BasicSplitPaneUI ui ) {
			super( ui );

			setLayout( new FlatDividerLayout() );
		}

		/** @since 2 */
		protected Object applyStyleProperty( String key, Object value ) {
			return FlatStylingSupport.applyToAnnotatedObject( this, key, value );
		}

		/** @since 2 */
		public Map<String, Class<?>> getStyleableInfos() {
			return FlatStylingSupport.getAnnotatedStyleableInfos( this );
		}

		/** @since 2.5 */
		public Object getStyleableValue( String key ) {
			return FlatStylingSupport.getAnnotatedStyleableValue( this, key );
		}

		void updateStyle() {
			if( leftButton instanceof FlatOneTouchButton )
				((FlatOneTouchButton)leftButton).updateStyle();
			if( rightButton instanceof FlatOneTouchButton )
				((FlatOneTouchButton)rightButton).updateStyle();
		}

		@Override
		public void setDividerSize( int newSize ) {
			super.setDividerSize( UIScale.scale( newSize ) );
		}

		@Override
		protected JButton createLeftOneTouchButton() {
			return new FlatOneTouchButton( true );
		}

		@Override
		protected JButton createRightOneTouchButton() {
			return new FlatOneTouchButton( false );
		}

		@Override
		public void propertyChange( PropertyChangeEvent e ) {
			super.propertyChange( e );

			switch( e.getPropertyName() ) {
				case JSplitPane.DIVIDER_LOCATION_PROPERTY:
					// necessary to show/hide one-touch buttons on expand/collapse
					doLayout();
					break;

				case FlatClientProperties.SPLIT_PANE_EXPANDABLE_SIDE:
					revalidate();
					break;
			}
		}

		@Override
		public void paint( Graphics g ) {
			// paint hover or pressed background
			Color hoverOrPressedColor = (isContinuousLayout() && dragger != null)
				? pressedColor
				: (isMouseOver() && dragger == null
					? hoverColor
					: null);
			if( hoverOrPressedColor != null ) {
				g.setColor( FlatUIUtils.deriveColor( hoverOrPressedColor, splitPane.getBackground() ) );
				g.fillRect( 0, 0, getWidth(), getHeight() );
			}

			super.paint( g );

			paintStyle( g, 0, 0, getWidth(), getHeight() );
		}

		/** @since 3.3 */
		protected void paintStyle( Graphics g, int x, int y, int width, int height ) {
			if( "plain".equals( style ) )
				return;

			Object[] oldRenderingHints = FlatUIUtils.setRenderingHints( g );

			g.setColor( gripColor );
			paintGrip( g, x, y, width, height );

			FlatUIUtils.resetRenderingHints( g, oldRenderingHints );
		}

		protected void paintGrip( Graphics g, int x, int y, int width, int height ) {
			FlatUIUtils.paintGrip( g, x, y, width, height,
				splitPane.getOrientation() == JSplitPane.VERTICAL_SPLIT,
				gripDotCount, gripDotSize, gripGap, true );
		}

		protected boolean isLeftCollapsed() {
			int location = splitPane.getDividerLocation();
			Insets insets = splitPane.getInsets();
			return (orientation == JSplitPane.VERTICAL_SPLIT)
				? location == insets.top
				: location == insets.left;
		}

		protected boolean isRightCollapsed() {
			int location = splitPane.getDividerLocation();
			Insets insets = splitPane.getInsets();
			return (orientation == JSplitPane.VERTICAL_SPLIT)
				? location == (splitPane.getHeight() - getHeight() - insets.bottom)
				: location == (splitPane.getWidth() - getWidth() - insets.right);
		}

		@Override
		protected void setMouseOver( boolean mouseOver ) {
			super.setMouseOver( mouseOver );
			repaintIfNecessary();
		}

		@Override
		protected void prepareForDragging() {
			super.prepareForDragging();
			repaintIfNecessary();
		}

		@Override
		protected void finishDraggingTo( int location ) {
			super.finishDraggingTo( location );
			repaintIfNecessary();
		}

		private void repaintIfNecessary() {
			if( hoverColor != null || pressedColor != null )
				repaint();
		}

		//---- class FlatOneTouchButton ---------------------------------------

		protected class FlatOneTouchButton
			extends FlatArrowButton
		{
			protected final boolean left;

			protected FlatOneTouchButton( boolean left ) {
				super( SwingConstants.NORTH, arrowType, oneTouchArrowColor, null,
					oneTouchHoverArrowColor, null, oneTouchPressedArrowColor, null );
				setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
				ToolTipManager.sharedInstance().registerComponent( this );

				this.left = left;
			}

			protected void updateStyle() {
				updateStyle( arrowType, oneTouchArrowColor, null,
					oneTouchHoverArrowColor, null, oneTouchPressedArrowColor, null );
			}

			@Override
			public int getDirection() {
				return (orientation == JSplitPane.VERTICAL_SPLIT)
					? (left ? SwingConstants.NORTH : SwingConstants.SOUTH)
					: (left ? SwingConstants.WEST : SwingConstants.EAST);
			}

			@Override
			public String getToolTipText( MouseEvent e ) {
				String key = (orientation == JSplitPane.VERTICAL_SPLIT)
					? (left
						? (isRightCollapsed()
							? "SplitPaneDivider.expandBottomToolTipText"
							: "SplitPaneDivider.collapseTopToolTipText")
						: (isLeftCollapsed()
							? "SplitPaneDivider.expandTopToolTipText"
							: "SplitPaneDivider.collapseBottomToolTipText"))
					: (left
						? (isRightCollapsed()
							? "SplitPaneDivider.expandRightToolTipText"
							: "SplitPaneDivider.collapseLeftToolTipText")
						: (isLeftCollapsed()
							? "SplitPaneDivider.expandLeftToolTipText"
							: "SplitPaneDivider.collapseRightToolTipText"));

				// get text from client property
				Object value = splitPane.getClientProperty( key );
				if( value instanceof String )
					return (String) value;

				// get text from bundle
				return UIManager.getString( key, getLocale() );
			}
		}

		//---- class FlatDividerLayout ----------------------------------------

		protected class FlatDividerLayout
			extends DividerLayout
		{
			@Override
			public void layoutContainer( Container c ) {
				super.layoutContainer( c );

				if( leftButton == null || rightButton == null || !splitPane.isOneTouchExpandable() )
					return;

				// increase size of buttons, which makes them easier to hit by the user
				// and avoids cut arrows at small divider sizes
				int extraSize = UIScale.scale( 4 );
				if( orientation == JSplitPane.VERTICAL_SPLIT ) {
					leftButton.setSize( leftButton.getWidth() + extraSize, leftButton.getHeight() );
					rightButton.setBounds( leftButton.getX() + leftButton.getWidth(), rightButton.getY(),
						rightButton.getWidth() + extraSize, rightButton.getHeight() );
				} else {
					leftButton.setSize( leftButton.getWidth(), leftButton.getHeight() + extraSize );
					rightButton.setBounds( rightButton.getX(), leftButton.getY() + leftButton.getHeight(),
						rightButton.getWidth(), rightButton.getHeight() + extraSize );
				}

				// hide buttons if not applicable
				boolean leftCollapsed = isLeftCollapsed();
				boolean rightCollapsed = isRightCollapsed();
				if( leftCollapsed || rightCollapsed ) {
					leftButton.setVisible( !leftCollapsed );
					rightButton.setVisible( !rightCollapsed );
				} else {
					Object expandableSide = splitPane.getClientProperty( FlatClientProperties.SPLIT_PANE_EXPANDABLE_SIDE );
					leftButton.setVisible( expandableSide == null || !FlatClientProperties.SPLIT_PANE_EXPANDABLE_SIDE_LEFT.equals( expandableSide ) );
					rightButton.setVisible( expandableSide == null || !FlatClientProperties.SPLIT_PANE_EXPANDABLE_SIDE_RIGHT.equals( expandableSide ) );
				}

				// move right button if left button is hidden
				if( !leftButton.isVisible() )
					rightButton.setLocation( leftButton.getLocation() );
			}
		}
	}
}
