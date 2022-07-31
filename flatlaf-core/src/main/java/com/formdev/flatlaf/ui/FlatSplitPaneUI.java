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

import java.awt.Color;
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
	implements StyleableUI
{
	@Styleable protected String arrowType;
	@Styleable protected Color oneTouchArrowColor;
	@Styleable protected Color oneTouchHoverArrowColor;
	@Styleable protected Color oneTouchPressedArrowColor;

	private PropertyChangeListener propertyChangeListener;
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

		oneTouchArrowColor = null;
		oneTouchHoverArrowColor = null;
		oneTouchPressedArrowColor = null;

		oldStyleValues = null;
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		propertyChangeListener = FlatStylingSupport.createPropertyChangeListener( splitPane, this::installStyle, null );
		splitPane.addPropertyChangeListener( propertyChangeListener );
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		splitPane.removePropertyChangeListener( propertyChangeListener );
		propertyChangeListener = null;
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

	//---- class FlatSplitPaneDivider -----------------------------------------

	protected class FlatSplitPaneDivider
		extends BasicSplitPaneDivider
	{
		@Styleable protected String style = UIManager.getString( "SplitPaneDivider.style" );
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
			}
		}

		@Override
		public void paint( Graphics g ) {
			super.paint( g );

			if( "plain".equals( style ) )
				return;

			Object[] oldRenderingHints = FlatUIUtils.setRenderingHints( g );

			g.setColor( gripColor );
			paintGrip( g, 0, 0, getWidth(), getHeight() );

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
