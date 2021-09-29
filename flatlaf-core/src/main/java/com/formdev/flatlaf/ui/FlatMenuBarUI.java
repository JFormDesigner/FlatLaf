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
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.LookAndFeel;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicMenuBarUI;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JMenuBar}.
 *
 * <!-- BasicMenuBarUI -->
 *
 * @uiDefault MenuBar.font								Font
 * @uiDefault MenuBar.background						Color
 * @uiDefault MenuBar.foreground						Color
 * @uiDefault MenuBar.border							Border
 *
 * <!-- FlatMenuBarUI -->
 *
 * @uiDefault TitlePane.unifiedBackground				boolean
 *
 * @author Karl Tauber
 */
public class FlatMenuBarUI
	extends BasicMenuBarUI
	implements StyleableUI
{
	// used in FlatMenuItemBorder
	/** @since 2 */ @Styleable protected Insets itemMargins;

	// used in FlatMenuUI
	/** @since 2 */ @Styleable protected Color hoverBackground;
	/** @since 2 */ @Styleable protected Color underlineSelectionBackground;
	/** @since 2 */ @Styleable protected Color underlineSelectionColor;
	/** @since 2 */ @Styleable protected int underlineSelectionHeight = -1;

	private PropertyChangeListener propertyChangeListener;
	private Map<String, Object> oldStyleValues;
	private AtomicBoolean borderShared;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatMenuBarUI();
	}

	/*
	 * WARNING: This class is not used on macOS if screen menu bar is enabled.
	 *          Do not add any functionality here.
	 */

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		installStyle();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		LookAndFeel.installProperty( menuBar, "opaque", false );
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		oldStyleValues = null;
		borderShared = null;
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		propertyChangeListener = FlatStylingSupport.createPropertyChangeListener( menuBar, this::installStyle, null );
		menuBar.addPropertyChangeListener( propertyChangeListener );
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		menuBar.removePropertyChangeListener( propertyChangeListener );
		propertyChangeListener = null;
	}

	@Override
	protected void installKeyboardActions() {
		super.installKeyboardActions();

		ActionMap map = SwingUtilities.getUIActionMap( menuBar );
		if( map == null ) {
			map = new ActionMapUIResource();
			SwingUtilities.replaceUIActionMap( menuBar, map );
		}
		map.put( "takeFocus", new TakeFocus() );
	}

	/** @since 2 */
	protected void installStyle() {
		try {
			applyStyle( FlatStylingSupport.getResolvedStyle( menuBar, "MenuBar" ) );
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
		if( borderShared == null )
			borderShared = new AtomicBoolean( true );
		return FlatStylingSupport.applyToAnnotatedObjectOrBorder( this, key, value, menuBar, borderShared );
	}

	/** @since 2 */
	@Override
	public Map<String, Class<?>> getStyleableInfos( JComponent c ) {
		return FlatStylingSupport.getAnnotatedStyleableInfos( this, menuBar.getBorder() );
	}

	@Override
	public void update( Graphics g, JComponent c ) {
		// paint background
		Color background = getBackground( c );
		if( background != null ) {
			g.setColor( background );
			g.fillRect( 0, 0, c.getWidth(), c.getHeight() );
		}

		paint( g, c );
	}

	protected Color getBackground( JComponent c ) {
		Color background = c.getBackground();

		// paint background if opaque or if having custom background color
		if( c.isOpaque() || !(background instanceof UIResource) )
			return background;

		// paint background if menu bar is not the "main" menu bar
		JRootPane rootPane = SwingUtilities.getRootPane( c );
		if( rootPane == null || !(rootPane.getParent() instanceof Window) || rootPane.getJMenuBar() != c )
			return background;

		// use parent background for unified title pane
		// (not storing value of "TitlePane.unifiedBackground" in class to allow changing at runtime)
		if( UIManager.getBoolean( "TitlePane.unifiedBackground" ) &&
			FlatNativeWindowBorder.hasCustomDecoration( (Window) rootPane.getParent() ) )
		  background = FlatUIUtils.getParentBackground( c );

		// paint background in full screen mode
		if( FlatUIUtils.isFullScreen( rootPane ) )
			return background;

		// do not paint background if menu bar is embedded into title pane
		return FlatRootPaneUI.isMenuBarEmbedded( rootPane ) ? null : background;
	}

	//---- class TakeFocus ----------------------------------------------------

	/**
	 * Activates the menu bar and shows mnemonics.
	 * On Windows, the popup of the first menu is not shown.
	 * On other platforms, the popup of the first menu is shown.
	 */
	private static class TakeFocus
		extends AbstractAction
	{
		@Override
		public void actionPerformed( ActionEvent e ) {
			JMenuBar menuBar = (JMenuBar) e.getSource();
			JMenu menu = menuBar.getMenu( 0 );
			if( menu != null ) {
				MenuSelectionManager.defaultManager().setSelectedPath( SystemInfo.isWindows
					? new MenuElement[] { menuBar, menu }
					: new MenuElement[] { menuBar, menu, menu.getPopupMenu() } );

				FlatLaf.showMnemonics( menuBar );
			}
		}
	}
}
