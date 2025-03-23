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

package com.formdev.flatlaf.demo;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.demo.HintManager.Hint;
import com.formdev.flatlaf.demo.extras.*;
import com.formdev.flatlaf.demo.intellijthemes.*;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.extras.FlatDesktop;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.extras.components.FlatButton.ButtonType;
import com.formdev.flatlaf.icons.FlatAbstractIcon;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.formdev.flatlaf.extras.FlatSVGUtils;
import com.formdev.flatlaf.util.ColorFunctions;
import com.formdev.flatlaf.util.FontUtils;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.SystemInfo;
import net.miginfocom.layout.ConstraintParser;
import net.miginfocom.layout.LC;
import net.miginfocom.layout.UnitValue;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
class DemoFrame
	extends JFrame
{
	private final String[] availableFontFamilyNames;
	private int initialFontMenuItemCount = -1;

	DemoFrame() {
		int tabIndex = DemoPrefs.getState().getInt( FlatLafDemo.KEY_TAB, 0 );

		availableFontFamilyNames = FontUtils.getAvailableFontFamilyNames().clone();
		Arrays.sort( availableFontFamilyNames );

		initComponents();
		updateFontMenuItems();
		initAccentColors();
		initFullWindowContent();
		controlBar.initialize( this, tabbedPane );

		setIconImages( FlatSVGUtils.createWindowIconImages( "/com/formdev/flatlaf/demo/FlatLaf.svg" ) );

		if( tabIndex >= 0 && tabIndex < tabbedPane.getTabCount() && tabIndex != tabbedPane.getSelectedIndex() )
			tabbedPane.setSelectedIndex( tabIndex );

		// macOS  (see https://www.formdev.com/flatlaf/macos/)
		if( SystemInfo.isMacOS ) {
			// hide menu items that are in macOS application menu
			exitMenuItem.setVisible( false );
			aboutMenuItem.setVisible( false );

			// do not use HTML text in menu items because this is not supported in macOS screen menu
			htmlMenuItem.setText( "some text" );

			JRootPane rootPane = getRootPane();
			if( SystemInfo.isMacFullWindowContentSupported ) {
				// expand window content into window title bar and make title bar transparent
				rootPane.putClientProperty( "apple.awt.fullWindowContent", true );
				rootPane.putClientProperty( "apple.awt.transparentTitleBar", true );
				rootPane.putClientProperty( FlatClientProperties.MACOS_WINDOW_BUTTONS_SPACING, FlatClientProperties.MACOS_WINDOW_BUTTONS_SPACING_LARGE );

				// hide window title
				if( SystemInfo.isJava_17_orLater )
					rootPane.putClientProperty( "apple.awt.windowTitleVisible", false );
				else
					setTitle( null );
			}

			// enable full screen mode for this window (for Java 8 - 10; not necessary for Java 11+)
			if( !SystemInfo.isJava_11_orLater )
				rootPane.putClientProperty( "apple.awt.fullscreenable", true );
		}

		// integrate into macOS screen menu
		FlatDesktop.setAboutHandler( this::aboutActionPerformed );
		FlatDesktop.setPreferencesHandler( this::showPreferences );
		FlatDesktop.setQuitHandler( response -> {
			response.performQuit();
		} );

		SwingUtilities.invokeLater( () -> {
			showHints();
		} );
	}

	@Override
	public void dispose() {
		super.dispose();

		FlatUIDefaultsInspector.hide();
	}

	private void showHints() {
		Hint fontMenuHint = new Hint(
			"Use 'Font' menu to increase/decrease font size or try different fonts.",
			fontMenu, SwingConstants.BOTTOM, "hint.fontMenu", null );

		Hint optionsMenuHint = new Hint(
			"Use 'Options' menu to try out various FlatLaf options.",
			optionsMenu, SwingConstants.BOTTOM, "hint.optionsMenu", fontMenuHint );

		Hint themesHint = new Hint(
			"Use 'Themes' list to try out various themes.",
			themesPanel, SwingConstants.LEFT, "hint.themesPanel", optionsMenuHint );

		HintManager.showHint( themesHint );
	}

	private void clearHints() {
		HintManager.hideAllHints();

		Preferences state = DemoPrefs.getState();
		state.remove( "hint.fontMenu" );
		state.remove( "hint.optionsMenu" );
		state.remove( "hint.themesPanel" );
	}

	private void showUIDefaultsInspector() {
		FlatUIDefaultsInspector.show();
	}

	private void newActionPerformed() {
		NewDialog newDialog = new NewDialog( this );
		newDialog.setVisible( true );
	}

	private void openActionPerformed() {
		JFileChooser chooser = new JFileChooser();
		chooser.showOpenDialog( this );
	}

	private void saveAsActionPerformed() {
		JFileChooser chooser = new JFileChooser();
		chooser.showSaveDialog( this );
	}

	private void exitActionPerformed() {
		dispose();
	}

	private void aboutActionPerformed() {
		JLabel titleLabel = new JLabel( "FlatLaf Demo" );
		titleLabel.putClientProperty( FlatClientProperties.STYLE_CLASS, "h1" );

		String link = "https://www.formdev.com/flatlaf/";
		JLabel linkLabel = new JLabel( "<html><a href=\"#\">" + link + "</a></html>" );
		linkLabel.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
		linkLabel.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseClicked( MouseEvent e ) {
				try {
					Desktop.getDesktop().browse( new URI( link ) );
				} catch( IOException | URISyntaxException ex ) {
					JOptionPane.showMessageDialog( linkLabel,
						"Failed to open '" + link + "' in browser.",
						"About", JOptionPane.PLAIN_MESSAGE );
				}
			}
		} );


		JOptionPane.showMessageDialog( this,
			new Object[] {
				titleLabel,
				"Demonstrates FlatLaf Swing look and feel",
				" ",
				"Copyright 2019-" + Year.now() + " FormDev Software GmbH",
				linkLabel,
			},
			"About", JOptionPane.PLAIN_MESSAGE );
	}

	private void showPreferences() {
		JOptionPane.showMessageDialog( this,
			"Sorry, but FlatLaf Demo does not have preferences. :(\n"
				+ "This dialog is here to demonstrate usage of class 'FlatDesktop' on macOS.",
			"Preferences", JOptionPane.PLAIN_MESSAGE );
	}

	private void selectedTabChanged() {
		DemoPrefs.getState().putInt( FlatLafDemo.KEY_TAB, tabbedPane.getSelectedIndex() );
	}

	private void menuItemActionPerformed( ActionEvent e ) {
		SwingUtilities.invokeLater( () -> {
			JOptionPane.showMessageDialog( this, e.getActionCommand(), "Menu Item", JOptionPane.PLAIN_MESSAGE );
		} );
	}

	private void windowDecorationsChanged() {
		boolean windowDecorations = windowDecorationsCheckBoxMenuItem.isSelected();

		if( SystemInfo.isLinux ) {
			// enable/disable custom window decorations
			JFrame.setDefaultLookAndFeelDecorated( windowDecorations );
			JDialog.setDefaultLookAndFeelDecorated( windowDecorations );

			// dispose frame, update decoration and re-show frame
			dispose();
			setUndecorated( windowDecorations );
			getRootPane().setWindowDecorationStyle( windowDecorations ? JRootPane.FRAME : JRootPane.NONE );
			setVisible( true );
		} else {
			// change window decoration of all frames and dialogs
			FlatLaf.setUseNativeWindowDecorations( windowDecorations );
		}

		menuBarEmbeddedCheckBoxMenuItem.setEnabled( windowDecorations );
		unifiedTitleBarMenuItem.setEnabled( windowDecorations );
		showTitleBarIconMenuItem.setEnabled( windowDecorations );
	}

	private void menuBarEmbeddedChanged() {
		UIManager.put( "TitlePane.menuBarEmbedded", menuBarEmbeddedCheckBoxMenuItem.isSelected() );
		FlatLaf.revalidateAndRepaintAllFramesAndDialogs();
	}

	private void unifiedTitleBar() {
		UIManager.put( "TitlePane.unifiedBackground", unifiedTitleBarMenuItem.isSelected() );
		FlatLaf.repaintAllFramesAndDialogs();
	}

	private void showTitleBarIcon() {
		boolean showIcon = showTitleBarIconMenuItem.isSelected();

		// for main frame (because already created)
		getRootPane().putClientProperty( FlatClientProperties.TITLE_BAR_SHOW_ICON, showIcon );

		// for other not yet created frames/dialogs
		UIManager.put( "TitlePane.showIcon", showIcon );
	}

	private void underlineMenuSelection() {
		UIManager.put( "MenuItem.selectionType", underlineMenuSelectionMenuItem.isSelected() ? "underline" : null );
	}

	private void alwaysShowMnemonics() {
		UIManager.put( "Component.hideMnemonics", !alwaysShowMnemonicsMenuItem.isSelected() );
		repaint();
	}

	private void animatedLafChangeChanged() {
		System.setProperty( "flatlaf.animatedLafChange", String.valueOf( animatedLafChangeMenuItem.isSelected() ) );
	}

	private void showHintsChanged() {
		clearHints();
		showHints();
	}

	private void fontFamilyChanged( ActionEvent e ) {
		String fontFamily = e.getActionCommand();

		FlatAnimatedLafChange.showSnapshot();

		Font font = UIManager.getFont( "defaultFont" );
		Font newFont = FontUtils.getCompositeFont( fontFamily, font.getStyle(), font.getSize() );
		UIManager.put( "defaultFont", newFont );

		FlatLaf.updateUI();
		FlatAnimatedLafChange.hideSnapshotWithAnimation();
	}

	private void fontSizeChanged( ActionEvent e ) {
		String fontSizeStr = e.getActionCommand();

		Font font = UIManager.getFont( "defaultFont" );
		Font newFont = font.deriveFont( (float) Integer.parseInt( fontSizeStr ) );
		UIManager.put( "defaultFont", newFont );

		FlatLaf.updateUI();
	}

	private void restoreFont() {
		UIManager.put( "defaultFont", null );
		updateFontMenuItems();
		FlatLaf.updateUI();
	}

	private void incrFont() {
		Font font = UIManager.getFont( "defaultFont" );
		Font newFont = font.deriveFont( (float) (font.getSize() + 1) );
		UIManager.put( "defaultFont", newFont );

		updateFontMenuItems();
		FlatLaf.updateUI();
	}

	private void decrFont() {
		Font font = UIManager.getFont( "defaultFont" );
		Font newFont = font.deriveFont( (float) Math.max( font.getSize() - 1, 10 ) );
		UIManager.put( "defaultFont", newFont );

		updateFontMenuItems();
		FlatLaf.updateUI();
	}

	void updateFontMenuItems() {
		if( initialFontMenuItemCount < 0 )
			initialFontMenuItemCount = fontMenu.getItemCount();
		else {
			// remove old font items
			for( int i = fontMenu.getItemCount() - 1; i >= initialFontMenuItemCount; i-- )
				fontMenu.remove( i );
		}

		// get current font
		Font currentFont = UIManager.getFont( "Label.font" );
		String currentFamily = currentFont.getFamily();
		String currentSize = Integer.toString( currentFont.getSize() );

		// add font families
		fontMenu.addSeparator();
		ArrayList<String> families = new ArrayList<>( Arrays.asList(
			"Arial", "Cantarell", "Comic Sans MS", "DejaVu Sans",
			"Dialog", "Inter", "Liberation Sans", "Noto Sans", "Open Sans", "Roboto",
			"SansSerif", "Segoe UI", "Serif", "Tahoma", "Ubuntu", "Verdana" ) );
		if( !families.contains( currentFamily ) )
			families.add( currentFamily );
		families.sort( String.CASE_INSENSITIVE_ORDER );

		ButtonGroup familiesGroup = new ButtonGroup();
		for( String family : families ) {
			if( Arrays.binarySearch( availableFontFamilyNames, family ) < 0 )
				continue; // not available

			JCheckBoxMenuItem item = new JCheckBoxMenuItem( family );
			item.setSelected( family.equals( currentFamily ) );
			item.addActionListener( this::fontFamilyChanged );
			fontMenu.add( item );

			familiesGroup.add( item );
		}

		// add font sizes
		fontMenu.addSeparator();
		ArrayList<String> sizes = new ArrayList<>( Arrays.asList(
			"10", "11", "12", "14", "16", "18", "20", "24", "28" ) );
		if( !sizes.contains( currentSize ) )
			sizes.add( currentSize );
		sizes.sort( String.CASE_INSENSITIVE_ORDER );

		ButtonGroup sizesGroup = new ButtonGroup();
		for( String size : sizes ) {
			JCheckBoxMenuItem item = new JCheckBoxMenuItem( size );
			item.setSelected( size.equals( currentSize ) );
			item.addActionListener( this::fontSizeChanged );
			fontMenu.add( item );

			sizesGroup.add( item );
		}

		// enabled/disable items
		boolean enabled = UIManager.getLookAndFeel() instanceof FlatLaf;
		for( Component item : fontMenu.getMenuComponents() )
			item.setEnabled( enabled );
	}

	// the real colors are defined in
	// flatlaf-demo/src/main/resources/com/formdev/flatlaf/demo/FlatLightLaf.properties and
	// flatlaf-demo/src/main/resources/com/formdev/flatlaf/demo/FlatDarkLaf.properties
	private static String[] accentColorKeys = {
		"Demo.accent.default", "Demo.accent.blue", "Demo.accent.purple", "Demo.accent.red",
		"Demo.accent.orange", "Demo.accent.yellow", "Demo.accent.green",
	};
	private static String[] accentColorNames = {
		"Default", "Blue", "Purple", "Red", "Orange", "Yellow", "Green",
	};
	private final JToggleButton[] accentColorButtons = new JToggleButton[accentColorKeys.length];
	private JLabel accentColorLabel;
	private Color accentColor;

	private void initAccentColors() {
		accentColorLabel = new JLabel( "Accent color: " );

		toolBar.add( Box.createHorizontalGlue() );
		toolBar.add( accentColorLabel );

		ButtonGroup group = new ButtonGroup();
		for( int i = 0; i < accentColorButtons.length; i++ ) {
			accentColorButtons[i] = new JToggleButton( new AccentColorIcon( accentColorKeys[i] ) );
			accentColorButtons[i].setToolTipText( accentColorNames[i] );
			accentColorButtons[i].addActionListener( this::accentColorChanged );
			toolBar.add( accentColorButtons[i] );
			group.add( accentColorButtons[i] );
		}

		accentColorButtons[0].setSelected( true );

		FlatLaf.setSystemColorGetter( name -> {
			return name.equals( "accent" ) ? accentColor : null;
		} );

		UIManager.addPropertyChangeListener( e -> {
			if( "lookAndFeel".equals( e.getPropertyName() ) )
				updateAccentColorButtons();
		} );
		updateAccentColorButtons();
	}

	private void accentColorChanged( ActionEvent e ) {
		String accentColorKey = null;
		for( int i = 0; i < accentColorButtons.length; i++ ) {
			if( accentColorButtons[i].isSelected() ) {
				accentColorKey = accentColorKeys[i];
				break;
			}
		}

		accentColor = (accentColorKey != null && accentColorKey != accentColorKeys[0])
			? UIManager.getColor( accentColorKey )
			: null;

		Class<? extends LookAndFeel> lafClass = UIManager.getLookAndFeel().getClass();
		try {
			FlatLaf.setup( lafClass.getDeclaredConstructor().newInstance() );
			FlatLaf.updateUI();
		} catch( Exception ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	private void updateAccentColorButtons() {
		Class<? extends LookAndFeel> lafClass = UIManager.getLookAndFeel().getClass();
		boolean isAccentColorSupported =
			lafClass == FlatLightLaf.class ||
			lafClass == FlatDarkLaf.class ||
			lafClass == FlatIntelliJLaf.class ||
			lafClass == FlatDarculaLaf.class ||
			lafClass == FlatMacLightLaf.class ||
			lafClass == FlatMacDarkLaf.class;

		accentColorLabel.setVisible( isAccentColorSupported );
		for( int i = 0; i < accentColorButtons.length; i++ )
			accentColorButtons[i].setVisible( isAccentColorSupported );
	}

	private void initFullWindowContent() {
		if( !supportsFlatLafWindowDecorations() )
			return;

		// create fullWindowContent mode toggle button
		Icon expandIcon = new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/expand.svg" );
		Icon collapseIcon = new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/collapse.svg" );
		JToggleButton fullWindowContentButton = new JToggleButton( expandIcon );
		fullWindowContentButton.setToolTipText( "Toggle full window content" );
		fullWindowContentButton.addActionListener( e -> {
			boolean fullWindowContent = fullWindowContentButton.isSelected();
			fullWindowContentButton.setIcon( fullWindowContent ? collapseIcon : expandIcon );
			menuBar.setVisible( !fullWindowContent );
			toolBar.setVisible( !fullWindowContent );
			getRootPane().putClientProperty( FlatClientProperties.FULL_WINDOW_CONTENT, fullWindowContent );
		} );

		// add fullWindowContent mode toggle button to tabbed pane
		JToolBar trailingToolBar = new JToolBar();
		trailingToolBar.add( Box.createGlue() );
		trailingToolBar.add( fullWindowContentButton );
		tabbedPane.putClientProperty( FlatClientProperties.TABBED_PANE_TRAILING_COMPONENT, trailingToolBar );
	}

	private boolean supportsFlatLafWindowDecorations() {
		return FlatLaf.supportsNativeWindowDecorations() || SystemInfo.isLinux;
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu();
		JMenuItem newMenuItem = new JMenuItem();
		JMenuItem openMenuItem = new JMenuItem();
		JMenuItem saveAsMenuItem = new JMenuItem();
		JMenuItem closeMenuItem = new JMenuItem();
		exitMenuItem = new JMenuItem();
		JMenu editMenu = new JMenu();
		JMenuItem undoMenuItem = new JMenuItem();
		JMenuItem redoMenuItem = new JMenuItem();
		JMenuItem cutMenuItem = new JMenuItem();
		JMenuItem copyMenuItem = new JMenuItem();
		JMenuItem pasteMenuItem = new JMenuItem();
		JMenuItem deleteMenuItem = new JMenuItem();
		JMenu viewMenu = new JMenu();
		JCheckBoxMenuItem checkBoxMenuItem1 = new JCheckBoxMenuItem();
		JMenu menu1 = new JMenu();
		JMenu subViewsMenu = new JMenu();
		JMenu subSubViewsMenu = new JMenu();
		JMenuItem errorLogViewMenuItem = new JMenuItem();
		JMenuItem searchViewMenuItem = new JMenuItem();
		JMenuItem projectViewMenuItem = new JMenuItem();
		JMenuItem structureViewMenuItem = new JMenuItem();
		JMenuItem propertiesViewMenuItem = new JMenuItem();
		scrollingPopupMenu = new JMenu();
		JMenuItem menuItem2 = new JMenuItem();
		htmlMenuItem = new JMenuItem();
		JRadioButtonMenuItem radioButtonMenuItem1 = new JRadioButtonMenuItem();
		JRadioButtonMenuItem radioButtonMenuItem2 = new JRadioButtonMenuItem();
		JRadioButtonMenuItem radioButtonMenuItem3 = new JRadioButtonMenuItem();
		fontMenu = new JMenu();
		JMenuItem restoreFontMenuItem = new JMenuItem();
		JMenuItem incrFontMenuItem = new JMenuItem();
		JMenuItem decrFontMenuItem = new JMenuItem();
		optionsMenu = new JMenu();
		windowDecorationsCheckBoxMenuItem = new JCheckBoxMenuItem();
		menuBarEmbeddedCheckBoxMenuItem = new JCheckBoxMenuItem();
		unifiedTitleBarMenuItem = new JCheckBoxMenuItem();
		showTitleBarIconMenuItem = new JCheckBoxMenuItem();
		underlineMenuSelectionMenuItem = new JCheckBoxMenuItem();
		alwaysShowMnemonicsMenuItem = new JCheckBoxMenuItem();
		animatedLafChangeMenuItem = new JCheckBoxMenuItem();
		JMenuItem showHintsMenuItem = new JMenuItem();
		JMenuItem showUIDefaultsInspectorMenuItem = new JMenuItem();
		JMenu helpMenu = new JMenu();
		aboutMenuItem = new JMenuItem();
		JPanel toolBarPanel = new JPanel();
		JPanel macFullWindowContentButtonsPlaceholder = new JPanel();
		toolBar = new JToolBar();
		JButton backButton = new JButton();
		JButton forwardButton = new JButton();
		JButton cutButton = new JButton();
		JButton copyButton = new JButton();
		JButton pasteButton = new JButton();
		JButton refreshButton = new JButton();
		JToggleButton showToggleButton = new JToggleButton();
		JPanel contentPanel = new JPanel();
		tabbedPane = new JTabbedPane();
		BasicComponentsPanel basicComponentsPanel = new BasicComponentsPanel();
		MoreComponentsPanel moreComponentsPanel = new MoreComponentsPanel();
		DataComponentsPanel dataComponentsPanel = new DataComponentsPanel();
		TabsPanel tabsPanel = new TabsPanel();
		OptionPanePanel optionPanePanel = new OptionPanePanel();
		ExtrasPanel extrasPanel = new ExtrasPanel();
		controlBar = new ControlBar();
		JPanel themesPanelPanel = new JPanel();
		JPanel winFullWindowContentButtonsPlaceholder = new JPanel();
		themesPanel = new IJThemesPanel();

		//======== this ========
		setTitle("FlatLaf Demo");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== menuBar ========
		{

			//======== fileMenu ========
			{
				fileMenu.setText("File");
				fileMenu.setMnemonic('F');

				//---- newMenuItem ----
				newMenuItem.setText("New");
				newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				newMenuItem.setMnemonic('N');
				newMenuItem.addActionListener(e -> newActionPerformed());
				fileMenu.add(newMenuItem);

				//---- openMenuItem ----
				openMenuItem.setText("Open...");
				openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				openMenuItem.setMnemonic('O');
				openMenuItem.addActionListener(e -> openActionPerformed());
				fileMenu.add(openMenuItem);

				//---- saveAsMenuItem ----
				saveAsMenuItem.setText("Save As...");
				saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				saveAsMenuItem.setMnemonic('S');
				saveAsMenuItem.addActionListener(e -> saveAsActionPerformed());
				fileMenu.add(saveAsMenuItem);
				fileMenu.addSeparator();

				//---- closeMenuItem ----
				closeMenuItem.setText("Close");
				closeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				closeMenuItem.setMnemonic('C');
				closeMenuItem.addActionListener(e -> menuItemActionPerformed(e));
				fileMenu.add(closeMenuItem);
				fileMenu.addSeparator();

				//---- exitMenuItem ----
				exitMenuItem.setText("Exit");
				exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				exitMenuItem.setMnemonic('X');
				exitMenuItem.addActionListener(e -> exitActionPerformed());
				fileMenu.add(exitMenuItem);
			}
			menuBar.add(fileMenu);

			//======== editMenu ========
			{
				editMenu.setText("Edit");
				editMenu.setMnemonic('E');

				//---- undoMenuItem ----
				undoMenuItem.setText("Undo");
				undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				undoMenuItem.setMnemonic('U');
				undoMenuItem.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/undo.svg"));
				undoMenuItem.addActionListener(e -> menuItemActionPerformed(e));
				editMenu.add(undoMenuItem);

				//---- redoMenuItem ----
				redoMenuItem.setText("Redo");
				redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				redoMenuItem.setMnemonic('R');
				redoMenuItem.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/redo.svg"));
				redoMenuItem.addActionListener(e -> menuItemActionPerformed(e));
				editMenu.add(redoMenuItem);
				editMenu.addSeparator();

				//---- cutMenuItem ----
				cutMenuItem.setText("Cut");
				cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				cutMenuItem.setMnemonic('C');
				cutMenuItem.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/menu-cut.svg"));
				editMenu.add(cutMenuItem);

				//---- copyMenuItem ----
				copyMenuItem.setText("Copy");
				copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				copyMenuItem.setMnemonic('O');
				copyMenuItem.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/copy.svg"));
				editMenu.add(copyMenuItem);

				//---- pasteMenuItem ----
				pasteMenuItem.setText("Paste");
				pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				pasteMenuItem.setMnemonic('P');
				pasteMenuItem.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/menu-paste.svg"));
				editMenu.add(pasteMenuItem);
				editMenu.addSeparator();

				//---- deleteMenuItem ----
				deleteMenuItem.setText("Delete");
				deleteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
				deleteMenuItem.setMnemonic('D');
				deleteMenuItem.addActionListener(e -> menuItemActionPerformed(e));
				editMenu.add(deleteMenuItem);
			}
			menuBar.add(editMenu);

			//======== viewMenu ========
			{
				viewMenu.setText("View");
				viewMenu.setMnemonic('V');

				//---- checkBoxMenuItem1 ----
				checkBoxMenuItem1.setText("Show Toolbar");
				checkBoxMenuItem1.setSelected(true);
				checkBoxMenuItem1.setMnemonic('T');
				checkBoxMenuItem1.addActionListener(e -> menuItemActionPerformed(e));
				viewMenu.add(checkBoxMenuItem1);

				//======== menu1 ========
				{
					menu1.setText("Show View");
					menu1.setMnemonic('V');

					//======== subViewsMenu ========
					{
						subViewsMenu.setText("Sub Views");
						subViewsMenu.setMnemonic('S');

						//======== subSubViewsMenu ========
						{
							subSubViewsMenu.setText("Sub sub Views");
							subSubViewsMenu.setMnemonic('U');

							//---- errorLogViewMenuItem ----
							errorLogViewMenuItem.setText("Error Log");
							errorLogViewMenuItem.setMnemonic('E');
							errorLogViewMenuItem.addActionListener(e -> menuItemActionPerformed(e));
							subSubViewsMenu.add(errorLogViewMenuItem);
						}
						subViewsMenu.add(subSubViewsMenu);

						//---- searchViewMenuItem ----
						searchViewMenuItem.setText("Search");
						searchViewMenuItem.setMnemonic('S');
						searchViewMenuItem.addActionListener(e -> menuItemActionPerformed(e));
						subViewsMenu.add(searchViewMenuItem);
					}
					menu1.add(subViewsMenu);

					//---- projectViewMenuItem ----
					projectViewMenuItem.setText("Project");
					projectViewMenuItem.setMnemonic('P');
					projectViewMenuItem.addActionListener(e -> menuItemActionPerformed(e));
					menu1.add(projectViewMenuItem);

					//---- structureViewMenuItem ----
					structureViewMenuItem.setText("Structure");
					structureViewMenuItem.setMnemonic('T');
					structureViewMenuItem.addActionListener(e -> menuItemActionPerformed(e));
					menu1.add(structureViewMenuItem);

					//---- propertiesViewMenuItem ----
					propertiesViewMenuItem.setText("Properties");
					propertiesViewMenuItem.setMnemonic('O');
					propertiesViewMenuItem.addActionListener(e -> menuItemActionPerformed(e));
					menu1.add(propertiesViewMenuItem);
				}
				viewMenu.add(menu1);

				//======== scrollingPopupMenu ========
				{
					scrollingPopupMenu.setText("Scrolling Popup Menu");
				}
				viewMenu.add(scrollingPopupMenu);

				//---- menuItem2 ----
				menuItem2.setText("Disabled Item");
				menuItem2.setEnabled(false);
				viewMenu.add(menuItem2);

				//---- htmlMenuItem ----
				htmlMenuItem.setText("<html>some <b color=\"red\">HTML</b> <i color=\"blue\">text</i></html>");
				viewMenu.add(htmlMenuItem);
				viewMenu.addSeparator();

				//---- radioButtonMenuItem1 ----
				radioButtonMenuItem1.setText("Details");
				radioButtonMenuItem1.setSelected(true);
				radioButtonMenuItem1.setMnemonic('D');
				radioButtonMenuItem1.addActionListener(e -> menuItemActionPerformed(e));
				viewMenu.add(radioButtonMenuItem1);

				//---- radioButtonMenuItem2 ----
				radioButtonMenuItem2.setText("Small Icons");
				radioButtonMenuItem2.setMnemonic('S');
				radioButtonMenuItem2.addActionListener(e -> menuItemActionPerformed(e));
				viewMenu.add(radioButtonMenuItem2);

				//---- radioButtonMenuItem3 ----
				radioButtonMenuItem3.setText("Large Icons");
				radioButtonMenuItem3.setMnemonic('L');
				radioButtonMenuItem3.addActionListener(e -> menuItemActionPerformed(e));
				viewMenu.add(radioButtonMenuItem3);
			}
			menuBar.add(viewMenu);

			//======== fontMenu ========
			{
				fontMenu.setText("Font");

				//---- restoreFontMenuItem ----
				restoreFontMenuItem.setText("Restore Font");
				restoreFontMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				restoreFontMenuItem.addActionListener(e -> restoreFont());
				fontMenu.add(restoreFontMenuItem);

				//---- incrFontMenuItem ----
				incrFontMenuItem.setText("Increase Font Size");
				incrFontMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				incrFontMenuItem.addActionListener(e -> incrFont());
				fontMenu.add(incrFontMenuItem);

				//---- decrFontMenuItem ----
				decrFontMenuItem.setText("Decrease Font Size");
				decrFontMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				decrFontMenuItem.addActionListener(e -> decrFont());
				fontMenu.add(decrFontMenuItem);
			}
			menuBar.add(fontMenu);

			//======== optionsMenu ========
			{
				optionsMenu.setText("Options");

				//---- windowDecorationsCheckBoxMenuItem ----
				windowDecorationsCheckBoxMenuItem.setText("Window decorations");
				windowDecorationsCheckBoxMenuItem.addActionListener(e -> windowDecorationsChanged());
				optionsMenu.add(windowDecorationsCheckBoxMenuItem);

				//---- menuBarEmbeddedCheckBoxMenuItem ----
				menuBarEmbeddedCheckBoxMenuItem.setText("Embedded menu bar");
				menuBarEmbeddedCheckBoxMenuItem.addActionListener(e -> menuBarEmbeddedChanged());
				optionsMenu.add(menuBarEmbeddedCheckBoxMenuItem);

				//---- unifiedTitleBarMenuItem ----
				unifiedTitleBarMenuItem.setText("Unified window title bar");
				unifiedTitleBarMenuItem.addActionListener(e -> unifiedTitleBar());
				optionsMenu.add(unifiedTitleBarMenuItem);

				//---- showTitleBarIconMenuItem ----
				showTitleBarIconMenuItem.setText("Show window title bar icon");
				showTitleBarIconMenuItem.addActionListener(e -> showTitleBarIcon());
				optionsMenu.add(showTitleBarIconMenuItem);

				//---- underlineMenuSelectionMenuItem ----
				underlineMenuSelectionMenuItem.setText("Use underline menu selection");
				underlineMenuSelectionMenuItem.addActionListener(e -> underlineMenuSelection());
				optionsMenu.add(underlineMenuSelectionMenuItem);

				//---- alwaysShowMnemonicsMenuItem ----
				alwaysShowMnemonicsMenuItem.setText("Always show mnemonics");
				alwaysShowMnemonicsMenuItem.addActionListener(e -> alwaysShowMnemonics());
				optionsMenu.add(alwaysShowMnemonicsMenuItem);

				//---- animatedLafChangeMenuItem ----
				animatedLafChangeMenuItem.setText("Animated Laf Change");
				animatedLafChangeMenuItem.setSelected(true);
				animatedLafChangeMenuItem.addActionListener(e -> animatedLafChangeChanged());
				optionsMenu.add(animatedLafChangeMenuItem);

				//---- showHintsMenuItem ----
				showHintsMenuItem.setText("Show hints");
				showHintsMenuItem.addActionListener(e -> showHintsChanged());
				optionsMenu.add(showHintsMenuItem);

				//---- showUIDefaultsInspectorMenuItem ----
				showUIDefaultsInspectorMenuItem.setText("Show UI Defaults Inspector");
				showUIDefaultsInspectorMenuItem.addActionListener(e -> showUIDefaultsInspector());
				optionsMenu.add(showUIDefaultsInspectorMenuItem);
			}
			menuBar.add(optionsMenu);

			//======== helpMenu ========
			{
				helpMenu.setText("Help");
				helpMenu.setMnemonic('H');

				//---- aboutMenuItem ----
				aboutMenuItem.setText("About");
				aboutMenuItem.setMnemonic('A');
				aboutMenuItem.addActionListener(e -> aboutActionPerformed());
				helpMenu.add(aboutMenuItem);
			}
			menuBar.add(helpMenu);
		}
		setJMenuBar(menuBar);

		//======== toolBarPanel ========
		{
			toolBarPanel.setLayout(new BorderLayout());

			//======== macFullWindowContentButtonsPlaceholder ========
			{
				macFullWindowContentButtonsPlaceholder.setLayout(new FlowLayout());
			}
			toolBarPanel.add(macFullWindowContentButtonsPlaceholder, BorderLayout.WEST);

			//======== toolBar ========
			{
				toolBar.setMargin(new Insets(3, 3, 3, 3));

				//---- backButton ----
				backButton.setToolTipText("Back");
				backButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/back.svg"));
				toolBar.add(backButton);

				//---- forwardButton ----
				forwardButton.setToolTipText("Forward");
				forwardButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/forward.svg"));
				toolBar.add(forwardButton);
				toolBar.addSeparator();

				//---- cutButton ----
				cutButton.setToolTipText("Cut");
				cutButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/menu-cut.svg"));
				toolBar.add(cutButton);

				//---- copyButton ----
				copyButton.setToolTipText("Copy");
				copyButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/copy.svg"));
				toolBar.add(copyButton);

				//---- pasteButton ----
				pasteButton.setToolTipText("Paste");
				pasteButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/menu-paste.svg"));
				toolBar.add(pasteButton);
				toolBar.addSeparator();

				//---- refreshButton ----
				refreshButton.setToolTipText("Refresh");
				refreshButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/refresh.svg"));
				toolBar.add(refreshButton);
				toolBar.addSeparator();

				//---- showToggleButton ----
				showToggleButton.setSelected(true);
				showToggleButton.setToolTipText("Show Details");
				showToggleButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/show.svg"));
				toolBar.add(showToggleButton);
			}
			toolBarPanel.add(toolBar, BorderLayout.CENTER);
		}
		contentPane.add(toolBarPanel, BorderLayout.PAGE_START);

		//======== contentPanel ========
		{
			contentPanel.setLayout(new MigLayout(
				"insets dialog,hidemode 3",
				// columns
				"[grow,fill]",
				// rows
				"[grow,fill]"));

			//======== tabbedPane ========
			{
				tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
				tabbedPane.addChangeListener(e -> selectedTabChanged());
				tabbedPane.addTab("Basic Components", basicComponentsPanel);
				tabbedPane.addTab("More Components", moreComponentsPanel);
				tabbedPane.addTab("Data Components", dataComponentsPanel);
				tabbedPane.addTab("Tabs", tabsPanel);
				tabbedPane.addTab("Option Pane", optionPanePanel);
				tabbedPane.addTab("Extras", extrasPanel);
			}
			contentPanel.add(tabbedPane, "cell 0 0");
		}
		contentPane.add(contentPanel, BorderLayout.CENTER);
		contentPane.add(controlBar, BorderLayout.PAGE_END);

		//======== themesPanelPanel ========
		{
			themesPanelPanel.setLayout(new BorderLayout());

			//======== winFullWindowContentButtonsPlaceholder ========
			{
				winFullWindowContentButtonsPlaceholder.setLayout(new FlowLayout());
			}
			themesPanelPanel.add(winFullWindowContentButtonsPlaceholder, BorderLayout.NORTH);
			themesPanelPanel.add(themesPanel, BorderLayout.CENTER);
		}
		contentPane.add(themesPanelPanel, BorderLayout.LINE_END);

		//---- buttonGroup1 ----
		ButtonGroup buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(radioButtonMenuItem1);
		buttonGroup1.add(radioButtonMenuItem2);
		buttonGroup1.add(radioButtonMenuItem3);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents

		// add "Users" button to menubar
		FlatButton usersButton = new FlatButton();
		usersButton.setIcon( new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/users.svg" ) );
		usersButton.setButtonType( ButtonType.toolBarButton );
		usersButton.setFocusable( false );
		usersButton.addActionListener( e -> JOptionPane.showMessageDialog( null, "Hello User! How are you?", "User", JOptionPane.INFORMATION_MESSAGE ) );
		menuBar.add( Box.createGlue() );
		menuBar.add( usersButton );

		cutMenuItem.addActionListener( new DefaultEditorKit.CutAction() );
		copyMenuItem.addActionListener( new DefaultEditorKit.CopyAction() );
		pasteMenuItem.addActionListener( new DefaultEditorKit.PasteAction() );

		scrollingPopupMenu.add( "Large menus are scrollable" );
		scrollingPopupMenu.add( "Use mouse wheel to scroll" );
		scrollingPopupMenu.add( "Or use up/down arrows at top/bottom" );
		for( int i = 1; i <= 100; i++ )
			scrollingPopupMenu.add( "Item " + i );

		if( supportsFlatLafWindowDecorations() ) {
			windowDecorationsCheckBoxMenuItem.setSelected( SystemInfo.isLinux
				? JFrame.isDefaultLookAndFeelDecorated()
				: FlatLaf.isUseNativeWindowDecorations() );
			menuBarEmbeddedCheckBoxMenuItem.setSelected( UIManager.getBoolean( "TitlePane.menuBarEmbedded" ) );
			unifiedTitleBarMenuItem.setSelected( UIManager.getBoolean( "TitlePane.unifiedBackground" ) );
			showTitleBarIconMenuItem.setSelected( UIManager.getBoolean( "TitlePane.showIcon" ) );
		} else {
			unsupported( windowDecorationsCheckBoxMenuItem );
			unsupported( menuBarEmbeddedCheckBoxMenuItem );
			unsupported( unifiedTitleBarMenuItem );
			unsupported( showTitleBarIconMenuItem );
		}

		if( SystemInfo.isMacOS )
			unsupported( underlineMenuSelectionMenuItem );

		if( "false".equals( System.getProperty( "flatlaf.animatedLafChange" ) ) )
			animatedLafChangeMenuItem.setSelected( false );


		// on macOS, panel left to toolBar is a placeholder for title bar buttons in fullWindowContent mode
		macFullWindowContentButtonsPlaceholder.putClientProperty( FlatClientProperties.FULL_WINDOW_CONTENT_BUTTONS_PLACEHOLDER, "mac zeroInFullScreen" );

		// on Windows/Linux, panel above themesPanel is a placeholder for title bar buttons in fullWindowContent mode
		winFullWindowContentButtonsPlaceholder.putClientProperty( FlatClientProperties.FULL_WINDOW_CONTENT_BUTTONS_PLACEHOLDER, "win" );

		// uncomment this line to see title bar buttons placeholders in fullWindowContent mode
//		UIManager.put( "FlatLaf.debug.panel.showPlaceholders", true );


		// remove contentPanel bottom insets
		MigLayout layout = (MigLayout) contentPanel.getLayout();
		LC lc = ConstraintParser.parseLayoutConstraint( (String) layout.getLayoutConstraints() );
		UnitValue[] insets = lc.getInsets();
		lc.setInsets( new UnitValue[] {
			insets[0],
			insets[1],
			new UnitValue( 0, UnitValue.PIXEL, null ),
			insets[3]
		} );
		layout.setLayoutConstraints( lc );
	}

	private void unsupported( JCheckBoxMenuItem menuItem ) {
		menuItem.setEnabled( false );
		menuItem.setSelected( false );
		menuItem.setToolTipText( "Not supported on this platform." );
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JMenuBar menuBar;
	private JMenuItem exitMenuItem;
	private JMenu scrollingPopupMenu;
	private JMenuItem htmlMenuItem;
	private JMenu fontMenu;
	private JMenu optionsMenu;
	private JCheckBoxMenuItem windowDecorationsCheckBoxMenuItem;
	private JCheckBoxMenuItem menuBarEmbeddedCheckBoxMenuItem;
	private JCheckBoxMenuItem unifiedTitleBarMenuItem;
	private JCheckBoxMenuItem showTitleBarIconMenuItem;
	private JCheckBoxMenuItem underlineMenuSelectionMenuItem;
	private JCheckBoxMenuItem alwaysShowMnemonicsMenuItem;
	private JCheckBoxMenuItem animatedLafChangeMenuItem;
	private JMenuItem aboutMenuItem;
	private JToolBar toolBar;
	private JTabbedPane tabbedPane;
	private ControlBar controlBar;
	IJThemesPanel themesPanel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class AccentColorIcon ----------------------------------------------

	private static class AccentColorIcon
		extends FlatAbstractIcon
	{
		private final String colorKey;

		AccentColorIcon( String colorKey ) {
			super( 16, 16, null );
			this.colorKey = colorKey;
		}

		@Override
		protected void paintIcon( Component c, Graphics2D g ) {
			Color color = UIManager.getColor( colorKey );
			if( color == null )
				color = Color.lightGray;
			else if( !c.isEnabled() ) {
				color = FlatLaf.isLafDark()
					? ColorFunctions.shade( color, 0.5f )
					: ColorFunctions.tint( color, 0.6f );
			}

			g.setColor( color );
			g.fillRoundRect( 1, 1, width - 2, height - 2, 5, 5 );
		}
	}
}
