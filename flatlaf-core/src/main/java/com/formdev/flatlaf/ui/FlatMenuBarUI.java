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
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
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
import javax.swing.plaf.basic.DefaultMenuLayout;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.SystemInfo;
import com.formdev.flatlaf.util.UIScale;

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
	/** @since 3 */ @Styleable protected Insets selectionInsets;
	/** @since 3 */ @Styleable protected Insets selectionEmbeddedInsets;
	/** @since 3 */ @Styleable protected int selectionArc = -1;
	/** @since 2 */ @Styleable protected Color hoverBackground;
	/** @since 2.5 */ @Styleable protected Color selectionBackground;
	/** @since 2.5 */ @Styleable protected Color selectionForeground;
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

		LayoutManager layout = menuBar.getLayout();
		if( layout == null || layout instanceof UIResource )
			menuBar.setLayout( new FlatMenuBarLayout( menuBar ) );
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

	/** @since 2.5 */
	@Override
	public Object getStyleableValue( JComponent c, String key ) {
		return FlatStylingSupport.getAnnotatedStyleableValue( this, menuBar.getBorder(), key );
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

		// paint background if opaque
		if( c.isOpaque() )
			return background;

		// do not paint background if non-opaque and having custom background color
		if( !(background instanceof UIResource) )
			return null;

		// paint background if menu bar is not the "main" menu bar (e.g. in internal frame)
		JRootPane rootPane = SwingUtilities.getRootPane( c );
		if( rootPane == null || !(rootPane.getParent() instanceof Window) || rootPane.getJMenuBar() != c )
			return background;

		// use parent background for unified title pane
		if( useUnifiedBackground( c ) )
			background = FlatUIUtils.getParentBackground( c );

		// paint background in full screen mode
		if( FlatUIUtils.isFullScreen( rootPane ) )
			return background;

		// do not paint background if menu bar is embedded into title pane
		return FlatRootPaneUI.isMenuBarEmbedded( rootPane ) ? null : background;
	}

	/**@since 2 */
	static boolean useUnifiedBackground( Component c ) {
		// check whether:
		// - TitlePane.unifiedBackground is true and
		// - menu bar is the "main" menu bar and
		// - window root pane has custom decoration style

		JRootPane rootPane;
		// (not storing value of "TitlePane.unifiedBackground" in class to allow changing at runtime)
		return UIManager.getBoolean( "TitlePane.unifiedBackground" ) &&
			(rootPane = SwingUtilities.getRootPane( c )) != null &&
			rootPane.getParent() instanceof Window &&
			rootPane.getJMenuBar() == c &&
			rootPane.getWindowDecorationStyle() != JRootPane.NONE;
	}

	//---- class FlatMenuBarLayout --------------------------------------------

	/**
	 * @since 2.4
	 */
	protected static class FlatMenuBarLayout
		extends DefaultMenuLayout
	{
		public FlatMenuBarLayout( Container target ) {
			super( target, BoxLayout.LINE_AXIS );
		}

		@Override
		public void layoutContainer( Container target ) {
			super.layoutContainer( target );


			// The only purpose of the code below is to make sure that a horizontal glue,
			// which can be used to move window and displays the window title in embedded menu bar,
			// is always visible within the menu bar bounds and has a minimum width.
			// If this is not the case, the horizontal glue is made larger and
			// components that are on the left side of the glue are made smaller.


			// get root pane and check whether this menu bar is the root pane menu bar
			JRootPane rootPane = SwingUtilities.getRootPane( target );
			if( rootPane == null || rootPane.getJMenuBar() != target )
				return;

			// get title pane and check whether menu bar is embedded
			FlatTitlePane titlePane = FlatRootPaneUI.getTitlePane( rootPane );
			if( titlePane == null || !titlePane.isMenuBarEmbedded() )
				return;

			// check whether there is a horizontal glue (used for window title in embedded menu bar)
			// and check minimum width of horizontal glue
			Component horizontalGlue = titlePane.findHorizontalGlue( (JMenuBar) target );
			int minTitleWidth = UIScale.scale( titlePane.titleMinimumWidth );
			if( horizontalGlue != null && horizontalGlue.getWidth() < minTitleWidth ) {
				// get index of glue component
				int glueIndex = -1;
				Component[] components = target.getComponents();
				for( int i = components.length - 1; i >= 0; i-- ) {
					if( components[i] == horizontalGlue ) {
						glueIndex = i;
						break;
					}
				}
				if( glueIndex < 0 )
					return; // should never happen

				if( target.getComponentOrientation().isLeftToRight() ) {
					// left-to-right

					// make horizontal glue wider (minimum title width)
					int offset = minTitleWidth - horizontalGlue.getWidth();
					horizontalGlue.setSize( minTitleWidth, horizontalGlue.getHeight() );

					// check whether glue is fully visible
					int minGlueX = target.getWidth() - target.getInsets().right - minTitleWidth;
					if( minGlueX < horizontalGlue.getX() ) {
						// move glue to the left to make it fully visible
						offset -= (horizontalGlue.getX() - minGlueX);
						horizontalGlue.setLocation( minGlueX, horizontalGlue.getY() );

						// shrink and move components that are on the left side of the glue
						for( int i = glueIndex - 1; i >= 0; i-- ) {
							Component c = components[i];
							if( c.getX() > minGlueX ) {
								// move component and set width to zero
								c.setBounds( minGlueX, c.getY(), 0, c.getHeight() );
							} else {
								// reduce size of component
								c.setSize( minGlueX - c.getX(), c.getHeight() );
								break;
							}
						}
					}

					// move components that are on the right side of the glue
					for( int i = glueIndex + 1; i < components.length; i++ ) {
						Component c = components[i];
						c.setLocation( c.getX() + offset, c.getY() );
					}
				} else {
					// right-to-left

					// make horizontal glue wider (minimum title width)
					int offset = minTitleWidth - horizontalGlue.getWidth();
					horizontalGlue.setBounds( horizontalGlue.getX() - offset, horizontalGlue.getY(),
						minTitleWidth, horizontalGlue.getHeight() );

					// check whether glue is fully visible
					int minGlueX = target.getInsets().left;
					if( minGlueX > horizontalGlue.getX() ) {
						// move glue to the right to make it fully visible
						offset -= (horizontalGlue.getX() - minGlueX);
						horizontalGlue.setLocation( minGlueX, horizontalGlue.getY() );

						// shrink and move components that are on the right side of the glue
						int x = horizontalGlue.getX() + horizontalGlue.getWidth();
						for( int i = glueIndex - 1; i >= 0; i-- ) {
							Component c = components[i];
							if( c.getX() + c.getWidth() < x ) {
								// move component and set width to zero
								c.setBounds( x, c.getY(), 0, c.getHeight() );
							} else {
								// move component and reduce size
								c.setBounds( x, c.getY(), c.getWidth() - (x - c.getX()), c.getHeight() );
								break;
							}
						}
					}

					// move components that are on the left side of the glue
					for( int i = glueIndex + 1; i < components.length; i++ ) {
						Component c = components[i];
						c.setLocation( c.getX() - offset, c.getY() );
					}
				}
			}
		}
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
