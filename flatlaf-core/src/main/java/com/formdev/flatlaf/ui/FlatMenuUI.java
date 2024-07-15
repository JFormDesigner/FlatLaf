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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.function.Function;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRootPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.MenuBarUI;
import javax.swing.plaf.basic.BasicMenuItemUI;
import javax.swing.plaf.basic.BasicMenuUI;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableField;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableLookupProvider;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.LoggingFacade;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JMenu}.
 *
 * <!-- BasicMenuUI -->
 *
 * @uiDefault Menu.font												Font
 * @uiDefault Menu.background										Color
 * @uiDefault Menu.foreground										Color
 * @uiDefault Menu.disabledForeground								Color
 * @uiDefault Menu.selectionBackground								Color
 * @uiDefault Menu.selectionForeground								Color
 * @uiDefault Menu.acceleratorForeground							Color
 * @uiDefault Menu.acceleratorSelectionForeground					Color
 * @uiDefault MenuItem.acceleratorFont								Font		defaults to MenuItem.font
 * @uiDefault MenuItem.acceleratorDelimiter							String
 * @uiDefault Menu.border											Border
 * @uiDefault Menu.borderPainted									boolean
 * @uiDefault Menu.margin											Insets
 * @uiDefault Menu.arrowIcon										Icon
 * @uiDefault Menu.checkIcon										Icon
 * @uiDefault Menu.opaque											boolean
 * @uiDefault Menu.crossMenuMnemonic								boolean	default is false
 * @uiDefault Menu.useMenuBarBackgroundForTopLevel					boolean	default is false
 * @uiDefault MenuBar.background									Color	used if Menu.useMenuBarBackgroundForTopLevel is true
 *
 * <!-- FlatMenuUI -->
 *
 * @uiDefault MenuItem.iconTextGap									int
 *
 * <!-- FlatMenuRenderer -->
 *
 * @uiDefault MenuBar.selectionInsets								Insets
 * @uiDefault MenuBar.selectionEmbeddedInsets						Insets
 * @uiDefault MenuBar.selectionArc									int
 * @uiDefault MenuBar.hoverBackground								Color
 * @uiDefault MenuBar.selectionBackground							Color	optional; defaults to Menu.selectionBackground
 * @uiDefault MenuBar.selectionForeground							Color	optional; defaults to Menu.selectionForeground
 * @uiDefault MenuBar.underlineSelectionBackground					Color
 * @uiDefault MenuBar.underlineSelectionColor						Color
 * @uiDefault MenuBar.underlineSelectionHeight						int
 *
 * @author Karl Tauber
 */
@StyleableField( cls=BasicMenuItemUI.class, key="selectionBackground" )
@StyleableField( cls=BasicMenuItemUI.class, key="selectionForeground" )
@StyleableField( cls=BasicMenuItemUI.class, key="disabledForeground" )
@StyleableField( cls=BasicMenuItemUI.class, key="acceleratorForeground" )
@StyleableField( cls=BasicMenuItemUI.class, key="acceleratorSelectionForeground" )

public class FlatMenuUI
	extends BasicMenuUI
	implements StyleableUI, StyleableLookupProvider
{
	private FlatMenuItemRenderer renderer;
	private Map<String, Object> oldStyleValues;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatMenuUI();
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		installStyle();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		LookAndFeel.installProperty( menuItem, "iconTextGap", FlatUIUtils.getUIInt( "MenuItem.iconTextGap", 4 ) );

		menuItem.setRolloverEnabled( true );

		renderer = createRenderer();
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		FlatMenuItemRenderer.clearClientProperties( menuItem.getParent() );
		renderer = null;
		oldStyleValues = null;
	}

	@Override
	protected void installComponents( JMenuItem menuItem ) {
		super.installComponents( menuItem );

		// update HTML renderer if necessary
		FlatHTML.updateRendererCSSFontBaseSize( menuItem );
	}

	protected FlatMenuItemRenderer createRenderer() {
		return new FlatMenuRenderer( menuItem, checkIcon, arrowIcon, acceleratorFont, acceleratorDelimiter );
	}

	@Override
	protected MouseInputListener createMouseInputListener( JComponent c ) {
		return new BasicMenuUI.MouseInputHandler() {
			@Override
			public void mouseEntered( MouseEvent e ) {
				super.mouseEntered( e );
				rollover( e, true );
			}

			@Override
			public void mouseExited( MouseEvent e ) {
				super.mouseExited( e );
				rollover( e, false );
			}

			private void rollover( MouseEvent e, boolean rollover ) {
				JMenu menu = (JMenu) e.getSource();
				if( menu.isTopLevelMenu() && menu.isRolloverEnabled() ) {
					menu.getModel().setRollover( rollover );
					HiDPIUtils.repaint( menu );
				}
			}
		};
	}

	@Override
	protected PropertyChangeListener createPropertyChangeListener( JComponent c ) {
		return FlatHTML.createPropertyChangeListener(
			FlatStylingSupport.createPropertyChangeListener( c, this::installStyle,
				super.createPropertyChangeListener( c ) ) );
	}

	/** @since 2 */
	protected void installStyle() {
		try {
			applyStyle( FlatStylingSupport.getResolvedStyle( menuItem, "Menu" ) );
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
		return FlatMenuItemUI.applyStyleProperty( menuItem, this, renderer, key, value );
	}

	/** @since 2 */
	@Override
	public Map<String, Class<?>> getStyleableInfos( JComponent c ) {
		return FlatMenuItemUI.getStyleableInfos( this, renderer );
	}

	/** @since 2.5 */
	@Override
	public Object getStyleableValue( JComponent c, String key ) {
		return FlatMenuItemUI.getStyleableValue( this, renderer, key );
	}

	/** @since 2.5 */
	@Override
	public MethodHandles.Lookup getLookupForStyling() {
		// MethodHandles.lookup() is caller sensitive and must be invoked in this class,
		// otherwise it is not possible to access protected fields in JRE superclass
		return MethodHandles.lookup();
	}

	@Override
	public Dimension getMinimumSize( JComponent c ) {
		// avoid that top-level menus (in menu bar) are made smaller if horizontal space is rare
		// same code is in BasicMenuUI since Java 10
		// see https://bugs.openjdk.java.net/browse/JDK-8178430
		return ((JMenu)menuItem).isTopLevelMenu() ? c.getPreferredSize() : null;
	}

	@Override
	protected Dimension getPreferredMenuItemSize( JComponent c, Icon checkIcon, Icon arrowIcon, int defaultTextIconGap ) {
		return renderer.getPreferredMenuItemSize();
	}

	@Override
	public void paint( Graphics g, JComponent c ) {
		renderer.paintMenuItem( g, selectionBackground, selectionForeground, disabledForeground,
			acceleratorForeground, acceleratorSelectionForeground );
	}

	//---- class FlatMenuRenderer ---------------------------------------------

	protected class FlatMenuRenderer
		extends FlatMenuItemRenderer
	{
		/** @since 3 */ protected Insets menuBarSelectionInsets = UIManager.getInsets( "MenuBar.selectionInsets" );
		/** @since 3 */ protected Insets menuBarSelectionEmbeddedInsets = UIManager.getInsets( "MenuBar.selectionEmbeddedInsets" );
		/** @since 3 */ protected int menuBarSelectionArc = UIManager.getInt( "MenuBar.selectionArc" );
		protected Color hoverBackground = UIManager.getColor( "MenuBar.hoverBackground" );
		/** @since 2.5 */ protected Color menuBarSelectionBackground = UIManager.getColor( "MenuBar.selectionBackground" );
		/** @since 2.5 */ protected Color menuBarSelectionForeground = UIManager.getColor( "MenuBar.selectionForeground" );
		protected Color menuBarUnderlineSelectionBackground = UIManager.getColor( "MenuBar.underlineSelectionBackground" );
		protected Color menuBarUnderlineSelectionColor = UIManager.getColor( "MenuBar.underlineSelectionColor" );
		protected int menuBarUnderlineSelectionHeight = FlatUIUtils.getUIInt( "MenuBar.underlineSelectionHeight", -1 );

		protected FlatMenuRenderer( JMenuItem menuItem, Icon checkIcon, Icon arrowIcon,
			Font acceleratorFont, String acceleratorDelimiter )
		{
			super( menuItem, checkIcon, arrowIcon, acceleratorFont, acceleratorDelimiter );
		}

		/** @since 3 */
		@Override
		protected void paintBackground( Graphics g ) {
			super.paintBackground( g );

			if( ((JMenu)menuItem).isTopLevelMenu() && isHover() ) {
				// paint hover background
				Color color = deriveBackground( getStyleFromMenuBarUI( ui -> ui.hoverBackground, hoverBackground ) );
				if( isUnderlineSelection() ) {
					g.setColor( color );
					g.fillRect( 0, 0, menuItem.getWidth(), menuItem.getHeight() );
				} else
					paintSelection( g, color, selectionInsets, selectionArc );
			}
		}

		/** @since 3 */
		@Override
		protected void paintSelection( Graphics g, Color selectionBackground, Insets selectionInsets, int selectionArc ) {
			if( ((JMenu)menuItem).isTopLevelMenu() ) {
				if( !isHover() )
					selectionBackground = getStyleFromMenuBarUI( ui -> ui.selectionBackground, menuBarSelectionBackground, selectionBackground );

				Container menuBar = menuItem.getParent();
				JRootPane rootPane = SwingUtilities.getRootPane( menuBar );
				if( rootPane != null && rootPane.getParent() instanceof Window &&
					rootPane.getJMenuBar() == menuBar &&
					FlatRootPaneUI.isMenuBarEmbedded( rootPane ) )
				{
					selectionInsets = getStyleFromMenuBarUI( ui -> ui.selectionEmbeddedInsets, menuBarSelectionEmbeddedInsets );
				} else
					selectionInsets = getStyleFromMenuBarUI( ui -> ui.selectionInsets, menuBarSelectionInsets );

				selectionArc = getStyleFromMenuBarUI( ui -> (ui.selectionArc != -1)
					? ui.selectionArc : null, menuBarSelectionArc );
			}

			super.paintSelection( g, selectionBackground, selectionInsets, selectionArc );
		}

		/** @since 3 */
		@Override
		protected void paintUnderlineSelection( Graphics g, Color underlineSelectionBackground,
			Color underlineSelectionColor, int underlineSelectionHeight )
		{
			if( ((JMenu)menuItem).isTopLevelMenu() ) {
				underlineSelectionBackground = getStyleFromMenuBarUI( ui -> ui.underlineSelectionBackground, menuBarUnderlineSelectionBackground, underlineSelectionBackground );
				underlineSelectionColor = getStyleFromMenuBarUI( ui -> ui.underlineSelectionColor, menuBarUnderlineSelectionColor, underlineSelectionColor );
				underlineSelectionHeight = getStyleFromMenuBarUI( ui -> (ui.underlineSelectionHeight != -1) ? ui.underlineSelectionHeight : null,
					 (menuBarUnderlineSelectionHeight != -1) ? menuBarUnderlineSelectionHeight : underlineSelectionHeight );
			}

			super.paintUnderlineSelection( g, underlineSelectionBackground, underlineSelectionColor, underlineSelectionHeight );
		}

		@Override
		protected void paintText( Graphics g, Rectangle textRect, String text, Color selectionForeground, Color disabledForeground ) {
			if( ((JMenu)menuItem).isTopLevelMenu() && !isUnderlineSelection() )
				selectionForeground = getStyleFromMenuBarUI( ui -> ui.selectionForeground, menuBarSelectionForeground, selectionForeground );

			super.paintText( g, textRect, text, selectionForeground, disabledForeground );
		}

		private boolean isHover() {
			ButtonModel model = menuItem.getModel();
			return model.isRollover() && !model.isArmed() && !model.isSelected() && model.isEnabled();
		}

		private <T> T getStyleFromMenuBarUI( Function<FlatMenuBarUI, T> f, T defaultValue, T defaultValue2 ) {
			return getStyleFromMenuBarUI( f, (defaultValue != null) ? defaultValue : defaultValue2 );
		}

		private <T> T getStyleFromMenuBarUI( Function<FlatMenuBarUI, T> f, T defaultValue ) {
			Container menuItemParent = menuItem.getParent();
			if( menuItemParent instanceof JMenuBar ) {
				MenuBarUI ui = ((JMenuBar) menuItemParent).getUI();
				if( ui instanceof FlatMenuBarUI ) {
					T value = f.apply( (FlatMenuBarUI) ui );
					if( value != null ) {
						return value;
					}
				}
			}
			return defaultValue;
		}
	}
}
