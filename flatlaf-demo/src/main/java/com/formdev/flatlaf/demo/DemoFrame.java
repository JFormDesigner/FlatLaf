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
import javax.swing.*;
import com.formdev.flatlaf.demo.intellijthemes.*;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
class DemoFrame
	extends JFrame
{
	DemoFrame() {
		int tabIndex = FlatLafDemo.prefs.getInt( FlatLafDemo.KEY_TAB, 0 );

		initComponents();
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
		FlatLafDemo.prefs.putInt( FlatLafDemo.KEY_TAB, tabbedPane.getSelectedIndex() );
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
		JRadioButtonMenuItem radioButtonMenuItem1 = new JRadioButtonMenuItem();
		JRadioButtonMenuItem radioButtonMenuItem2 = new JRadioButtonMenuItem();
		JRadioButtonMenuItem radioButtonMenuItem3 = new JRadioButtonMenuItem();
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

				//---- newMenuItem ----
				newMenuItem.setText("New");
				newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				fileMenu.add(newMenuItem);

				//---- openMenuItem ----
				openMenuItem.setText("Open");
				openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				fileMenu.add(openMenuItem);
				fileMenu.addSeparator();

				//---- closeMenuItem ----
				closeMenuItem.setText("Close");
				closeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				fileMenu.add(closeMenuItem);
				fileMenu.addSeparator();

				//---- exitMenuItem ----
				exitMenuItem.setText("Exit");
				exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				exitMenuItem.addActionListener(e -> exitActionPerformed());
				fileMenu.add(exitMenuItem);
			}
			menuBar1.add(fileMenu);

			//======== editMenu ========
			{
				editMenu.setText("Edit");

				//---- undoMenuItem ----
				undoMenuItem.setText("Undo");
				undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				editMenu.add(undoMenuItem);

				//---- redoMenuItem ----
				redoMenuItem.setText("Redo");
				redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				editMenu.add(redoMenuItem);
				editMenu.addSeparator();

				//---- cutMenuItem ----
				cutMenuItem.setText("Cut");
				cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				editMenu.add(cutMenuItem);

				//---- copyMenuItem ----
				copyMenuItem.setText("Copy");
				copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				editMenu.add(copyMenuItem);

				//---- pasteMenuItem ----
				pasteMenuItem.setText("Paste");
				pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
				editMenu.add(pasteMenuItem);
				editMenu.addSeparator();

				//---- deleteMenuItem ----
				deleteMenuItem.setText("Delete");
				deleteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
				editMenu.add(deleteMenuItem);
			}
			menuBar1.add(editMenu);

			//======== viewMenu ========
			{
				viewMenu.setText("View");

				//---- checkBoxMenuItem1 ----
				checkBoxMenuItem1.setText("Show Toolbar");
				checkBoxMenuItem1.setSelected(true);
				viewMenu.add(checkBoxMenuItem1);
				viewMenu.addSeparator();

				//---- radioButtonMenuItem1 ----
				radioButtonMenuItem1.setText("Details");
				radioButtonMenuItem1.setSelected(true);
				viewMenu.add(radioButtonMenuItem1);

				//---- radioButtonMenuItem2 ----
				radioButtonMenuItem2.setText("Small Icons");
				viewMenu.add(radioButtonMenuItem2);

				//---- radioButtonMenuItem3 ----
				radioButtonMenuItem3.setText("Large Icons");
				viewMenu.add(radioButtonMenuItem3);
			}
			menuBar1.add(viewMenu);

			//======== helpMenu ========
			{
				helpMenu.setText("Help");

				//---- aboutMenuItem ----
				aboutMenuItem.setText("About");
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
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JTabbedPane tabbedPane;
	private ControlBar controlBar;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
