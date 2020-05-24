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
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.StyleContext;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.demo.extras.*;
import com.formdev.flatlaf.demo.intellijthemes.*;
import com.formdev.flatlaf.extras.FlatSVGIcon;
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

		availableFontFamilyNames = GraphicsEnvironment.getLocalGraphicsEnvironment()
			.getAvailableFontFamilyNames().clone();
		Arrays.sort( availableFontFamilyNames );

		initComponents();
		updateFontMenuItems();
		controlBar.initialize( this, tabbedPane );

		if( tabIndex >= 0 && tabIndex < tabbedPane.getTabCount() && tabIndex != tabbedPane.getSelectedIndex() )
			tabbedPane.setSelectedIndex( tabIndex );
	}

	private void exitActionPerformed() {
		dispose();
	}

	private void aboutActionPerformed() {
		JOptionPane.showMessageDialog( this, "FlatLaf Demo", "About", JOptionPane.PLAIN_MESSAGE );
	}

	private void selectedTabChanged() {
		DemoPrefs.getState().putInt( FlatLafDemo.KEY_TAB, tabbedPane.getSelectedIndex() );
	}

	private void menuItemActionPerformed( ActionEvent e ) {
		SwingUtilities.invokeLater( () -> {
			JOptionPane.showMessageDialog( this, e.getActionCommand(), "Menu Item", JOptionPane.PLAIN_MESSAGE );
		} );
	}

	private void underlineMenuSelection() {
		UIManager.put( "MenuItem.selectionType", underlineMenuSelectionMenuItem.isSelected() ? "underline" : null );
	}

	private void alwaysShowMnemonics() {
		UIManager.put( "Component.hideMnemonics", !alwaysShowMnemonicsMenuItem.isSelected() );
		repaint();
	}

	private void fontFamilyChanged( ActionEvent e ) {
		String fontFamily = e.getActionCommand();

		Font font = UIManager.getFont( "defaultFont" );
		Font newFont = StyleContext.getDefaultStyleContext().getFont( fontFamily, font.getStyle(), font.getSize() );
		UIManager.put( "defaultFont", newFont );

		FlatLaf.updateUI();
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
			"Arial", "Cantarell", "Comic Sans MS", "Courier New", "DejaVu Sans",
			"Dialog", "Liberation Sans", "Monospaced", "Noto Sans", "Roboto",
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
			"10", "12", "14", "16", "18", "20", "24", "28" ) );
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

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JMenuBar menuBar1 = new JMenuBar();
		JMenu fileMenu = new JMenu();
		JMenuItem newMenuItem = new JMenuItem();
		JMenuItem openMenuItem = new JMenuItem();
		JMenuItem closeMenuItem = new JMenuItem();
		JMenuItem exitMenuItem = new JMenuItem();
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
		JMenuItem menuItem1 = new JMenuItem();
		JRadioButtonMenuItem radioButtonMenuItem1 = new JRadioButtonMenuItem();
		JRadioButtonMenuItem radioButtonMenuItem2 = new JRadioButtonMenuItem();
		JRadioButtonMenuItem radioButtonMenuItem3 = new JRadioButtonMenuItem();
		fontMenu = new JMenu();
		JMenuItem restoreFontMenuItem = new JMenuItem();
		JMenuItem incrFontMenuItem = new JMenuItem();
		JMenuItem decrFontMenuItem = new JMenuItem();
		JMenu optionsMenu = new JMenu();
		underlineMenuSelectionMenuItem = new JCheckBoxMenuItem();
		alwaysShowMnemonicsMenuItem = new JCheckBoxMenuItem();
		JMenu helpMenu = new JMenu();
		JMenuItem aboutMenuItem = new JMenuItem();
		JToolBar toolBar1 = new JToolBar();
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
		ExtrasPanel extrasPanel1 = new ExtrasPanel();
		controlBar = new ControlBar();
		IJThemesPanel themesPanel = new IJThemesPanel();

		//======== this ========
		setTitle("FlatLaf Demo");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== menuBar1 ========
		{

			//======== fileMenu ========
			{
				fileMenu.setText("File");
				fileMenu.setMnemonic('F');

				//---- newMenuItem ----
				newMenuItem.setText("New");
				newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				newMenuItem.setMnemonic('N');
				newMenuItem.addActionListener(e -> menuItemActionPerformed(e));
				fileMenu.add(newMenuItem);

				//---- openMenuItem ----
				openMenuItem.setText("Open");
				openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				openMenuItem.setMnemonic('O');
				openMenuItem.addActionListener(e -> menuItemActionPerformed(e));
				fileMenu.add(openMenuItem);
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
			menuBar1.add(fileMenu);

			//======== editMenu ========
			{
				editMenu.setText("Edit");
				editMenu.setMnemonic('E');

				//---- undoMenuItem ----
				undoMenuItem.setText("Undo");
				undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				undoMenuItem.setMnemonic('U');
				undoMenuItem.addActionListener(e -> menuItemActionPerformed(e));
				editMenu.add(undoMenuItem);

				//---- redoMenuItem ----
				redoMenuItem.setText("Redo");
				redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				redoMenuItem.setMnemonic('R');
				redoMenuItem.addActionListener(e -> menuItemActionPerformed(e));
				editMenu.add(redoMenuItem);
				editMenu.addSeparator();

				//---- cutMenuItem ----
				cutMenuItem.setText("Cut");
				cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				cutMenuItem.setMnemonic('C');
				editMenu.add(cutMenuItem);

				//---- copyMenuItem ----
				copyMenuItem.setText("Copy");
				copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				copyMenuItem.setMnemonic('O');
				editMenu.add(copyMenuItem);

				//---- pasteMenuItem ----
				pasteMenuItem.setText("Paste");
				pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				pasteMenuItem.setMnemonic('P');
				editMenu.add(pasteMenuItem);
				editMenu.addSeparator();

				//---- deleteMenuItem ----
				deleteMenuItem.setText("Delete");
				deleteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
				deleteMenuItem.setMnemonic('D');
				deleteMenuItem.addActionListener(e -> menuItemActionPerformed(e));
				editMenu.add(deleteMenuItem);
			}
			menuBar1.add(editMenu);

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

				//---- menuItem1 ----
				menuItem1.setText("<html>some <b color=\"red\">HTML</b> <i color=\"blue\">text</i></html>");
				viewMenu.add(menuItem1);
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
			menuBar1.add(viewMenu);

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
			menuBar1.add(fontMenu);

			//======== optionsMenu ========
			{
				optionsMenu.setText("Options");

				//---- underlineMenuSelectionMenuItem ----
				underlineMenuSelectionMenuItem.setText("Use underline menu selection");
				underlineMenuSelectionMenuItem.addActionListener(e -> underlineMenuSelection());
				optionsMenu.add(underlineMenuSelectionMenuItem);

				//---- alwaysShowMnemonicsMenuItem ----
				alwaysShowMnemonicsMenuItem.setText("Always show mnemonics");
				alwaysShowMnemonicsMenuItem.addActionListener(e -> alwaysShowMnemonics());
				optionsMenu.add(alwaysShowMnemonicsMenuItem);
			}
			menuBar1.add(optionsMenu);

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
			menuBar1.add(helpMenu);
		}
		setJMenuBar(menuBar1);

		//======== toolBar1 ========
		{
			toolBar1.setMargin(new Insets(3, 3, 3, 3));

			//---- backButton ----
			backButton.setToolTipText("Back");
			toolBar1.add(backButton);

			//---- forwardButton ----
			forwardButton.setToolTipText("Forward");
			toolBar1.add(forwardButton);
			toolBar1.addSeparator();

			//---- cutButton ----
			cutButton.setToolTipText("Cut");
			toolBar1.add(cutButton);

			//---- copyButton ----
			copyButton.setToolTipText("Copy");
			toolBar1.add(copyButton);

			//---- pasteButton ----
			pasteButton.setToolTipText("Paste");
			toolBar1.add(pasteButton);
			toolBar1.addSeparator();

			//---- refreshButton ----
			refreshButton.setToolTipText("Refresh");
			toolBar1.add(refreshButton);
			toolBar1.addSeparator();

			//---- showToggleButton ----
			showToggleButton.setSelected(true);
			showToggleButton.setToolTipText("Show Details");
			toolBar1.add(showToggleButton);
		}
		contentPane.add(toolBar1, BorderLayout.NORTH);

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
				tabbedPane.addChangeListener(e -> selectedTabChanged());
				tabbedPane.addTab("Basic Components", basicComponentsPanel);
				tabbedPane.addTab("More Components", moreComponentsPanel);
				tabbedPane.addTab("Data Components", dataComponentsPanel);
				tabbedPane.addTab("SplitPane & Tabs", tabsPanel);
				tabbedPane.addTab("Option Pane", optionPanePanel);
				tabbedPane.addTab("Extras", extrasPanel1);
			}
			contentPanel.add(tabbedPane, "cell 0 0");
		}
		contentPane.add(contentPanel, BorderLayout.CENTER);
		contentPane.add(controlBar, BorderLayout.SOUTH);
		contentPane.add(themesPanel, BorderLayout.EAST);

		//---- buttonGroup1 ----
		ButtonGroup buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(radioButtonMenuItem1);
		buttonGroup1.add(radioButtonMenuItem2);
		buttonGroup1.add(radioButtonMenuItem3);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents

		undoMenuItem.setIcon( new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/undo.svg" ) );
		redoMenuItem.setIcon( new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/redo.svg" ) );

		cutMenuItem.setIcon( new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/menu-cut.svg" ) );
		copyMenuItem.setIcon( new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/copy.svg" ) );
		pasteMenuItem.setIcon( new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/menu-paste.svg" ) );

		backButton.setIcon( new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/back.svg" ) );
		forwardButton.setIcon( new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/forward.svg" ) );
		cutButton.setIcon( new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/menu-cut.svg" ) );
		copyButton.setIcon( new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/copy.svg" ) );
		pasteButton.setIcon( new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/menu-paste.svg" ) );
		refreshButton.setIcon( new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/refresh.svg" ) );
		showToggleButton.setIcon( new FlatSVGIcon( "com/formdev/flatlaf/demo/icons/show.svg" ) );

		cutMenuItem.addActionListener( new DefaultEditorKit.CutAction() );
		copyMenuItem.addActionListener( new DefaultEditorKit.CopyAction() );
		pasteMenuItem.addActionListener( new DefaultEditorKit.PasteAction() );
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JMenu fontMenu;
	private JCheckBoxMenuItem underlineMenuSelectionMenuItem;
	private JCheckBoxMenuItem alwaysShowMnemonicsMenuItem;
	private JTabbedPane tabbedPane;
	private ControlBar controlBar;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
