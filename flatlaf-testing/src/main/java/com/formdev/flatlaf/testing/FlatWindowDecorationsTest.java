/*
 * Copyright 2020 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.formdev.flatlaf.testing;

import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatWindowDecorationsTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			// enable custom window decoration (if LaF supports it)
			JFrame.setDefaultLookAndFeelDecorated( true );
			JDialog.setDefaultLookAndFeelDecorated( true );

			FlatTestFrame frame = FlatTestFrame.create( args, "FlatWindowDecorationsTest" );

			// WARNING: Do not this in real-world programs.
//			frame.setUndecorated( true );
//			frame.getRootPane().setWindowDecorationStyle( JRootPane.FRAME );

			Class<?> cls = FlatWindowDecorationsTest.class;
			List<Image> images = Arrays.asList(
				new ImageIcon( cls.getResource( "/com/formdev/flatlaf/testing/test16.png" ) ).getImage(),
				new ImageIcon( cls.getResource( "/com/formdev/flatlaf/testing/test24.png" ) ).getImage(),
				new ImageIcon( cls.getResource( "/com/formdev/flatlaf/testing/test32.png" ) ).getImage(),
				new ImageIcon( cls.getResource( "/com/formdev/flatlaf/testing/test48.png" ) ).getImage(),
				new ImageIcon( cls.getResource( "/com/formdev/flatlaf/testing/test64.png" ) ).getImage(),
				new ImageIcon( cls.getResource( "/com/formdev/flatlaf/testing/test128.png" ) ).getImage()
			);
			// shuffle to test whether FlatLaf chooses the right size
			Collections.shuffle( images );
			frame.setIconImages( images );

			frame.showFrame( FlatWindowDecorationsTest::new, panel -> ((FlatWindowDecorationsTest)panel).menuBar );
		} );
	}

	private List<Image> images;

	FlatWindowDecorationsTest() {
		initComponents();
	}

	@Override
	public void addNotify() {
		super.addNotify();

		Window window = SwingUtilities.windowForComponent( this );
		menuBarCheckBox.setEnabled( window instanceof JFrame );
		menuBarEmbeddedCheckBox.setEnabled( window instanceof JFrame );
		maximizedBoundsCheckBox.setEnabled( window instanceof Frame );

		boolean windowHasIcons = (window != null && !window.getIconImages().isEmpty());
		iconNoneRadioButton.setEnabled( windowHasIcons );
		iconTestAllRadioButton.setEnabled( windowHasIcons );
		iconTestRandomRadioButton.setEnabled( windowHasIcons );

		if( window instanceof Frame )
			undecoratedCheckBox.setSelected( ((Frame)window).isUndecorated() );
		else if( window instanceof Dialog )
			undecoratedCheckBox.setSelected( ((Dialog)window).isUndecorated() );

		JRootPane rootPane = getWindowRootPane();
		if( rootPane != null ) {
			int style = rootPane.getWindowDecorationStyle();
			if( style == JRootPane.NONE )
				styleNoneRadioButton.setSelected( true );
			else if( style == JRootPane.FRAME )
				styleFrameRadioButton.setSelected( true );
			else if( style == JRootPane.PLAIN_DIALOG )
				stylePlainRadioButton.setSelected( true );
			else if( style == JRootPane.INFORMATION_DIALOG )
				styleInfoRadioButton.setSelected( true );
			else
				throw new RuntimeException(); // not used
		}
	}

	private void menuBarChanged() {
		Window window = SwingUtilities.windowForComponent( this );
		if( window instanceof JFrame ) {
			((JFrame)window).setJMenuBar( menuBarCheckBox.isSelected() ? menuBar : null );
			window.revalidate();
			window.repaint();
		}
	}

	private void menuBarEmbeddedChanged() {
		JRootPane rootPane = getWindowRootPane();
		if( rootPane != null )
			rootPane.putClientProperty( FlatClientProperties.MENU_BAR_EMBEDDED, menuBarEmbeddedCheckBox.isSelected() );
	}

	private void resizableChanged() {
		Window window = SwingUtilities.windowForComponent( this );
		if( window instanceof Frame )
			((Frame)window).setResizable( resizableCheckBox.isSelected() );
		else if( window instanceof Dialog )
			((Dialog)window).setResizable( resizableCheckBox.isSelected() );
	}

	private void undecoratedChanged() {
		Window window = SwingUtilities.windowForComponent( this );
		if( window == null )
			return;

		window.dispose();

		if( window instanceof Frame )
			((Frame)window).setUndecorated( undecoratedCheckBox.isSelected() );
		else if( window instanceof Dialog )
			((Dialog)window).setUndecorated( undecoratedCheckBox.isSelected() );

		window.setVisible( true );
	}

	private void maximizedBoundsChanged() {
		Window window = SwingUtilities.windowForComponent( this );
		if( window instanceof Frame ) {
			((Frame)window).setMaximizedBounds( maximizedBoundsCheckBox.isSelected()
				? new Rectangle( 50, 100, 1000, 700 )
				: null );
		}
	}

	private void menuItemActionPerformed(ActionEvent e) {
		SwingUtilities.invokeLater( () -> {
			JOptionPane.showMessageDialog( this, e.getActionCommand(), "Menu Item", JOptionPane.PLAIN_MESSAGE );
		} );
	}

	private void openDialog() {
		Window owner = SwingUtilities.windowForComponent( this );
		JDialog dialog = new JDialog( owner, "Dialog", ModalityType.APPLICATION_MODAL );
		dialog.add( new FlatWindowDecorationsTest() );
		dialog.pack();
		dialog.setLocationRelativeTo( this );
		dialog.setVisible( true );
	}

	private void decorationStyleChanged() {
		int style;
		if( styleFrameRadioButton.isSelected() )
			style = JRootPane.FRAME;
		else if( stylePlainRadioButton.isSelected() )
			style = JRootPane.PLAIN_DIALOG;
		else if( styleInfoRadioButton.isSelected() )
			style = JRootPane.INFORMATION_DIALOG;
		else if( styleErrorRadioButton.isSelected() )
			style = JRootPane.ERROR_DIALOG;
		else if( styleQuestionRadioButton.isSelected() )
			style = JRootPane.QUESTION_DIALOG;
		else if( styleWarningRadioButton.isSelected() )
			style = JRootPane.WARNING_DIALOG;
		else if( styleColorChooserRadioButton.isSelected() )
			style = JRootPane.COLOR_CHOOSER_DIALOG;
		else if( styleFileChooserRadioButton.isSelected() )
			style = JRootPane.FILE_CHOOSER_DIALOG;
		else
			style = JRootPane.NONE;

		JRootPane rootPane = getWindowRootPane();
		if( rootPane != null )
			rootPane.setWindowDecorationStyle( style );
	}

	private void iconChanged() {
		Window window = SwingUtilities.windowForComponent( this );
		if( window == null )
			return;

		if( images == null )
			images = window.getIconImages();

		if( iconNoneRadioButton.isSelected() )
			window.setIconImage( null );
		else if( iconTestAllRadioButton.isSelected() )
			window.setIconImages( images );
		else if( iconTestRandomRadioButton.isSelected() )
			window.setIconImage( images.get( (int) (Math.random() * images.size()) ) );
	}

	private JRootPane getWindowRootPane() {
		Window window = SwingUtilities.windowForComponent( this );
		if( window instanceof JFrame )
			return ((JFrame)window).getRootPane();
		else if( window instanceof JDialog )
			return ((JDialog)window).getRootPane();
		return null;
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		menuBarCheckBox = new JCheckBox();
		menuBarEmbeddedCheckBox = new JCheckBox();
		resizableCheckBox = new JCheckBox();
		maximizedBoundsCheckBox = new JCheckBox();
		undecoratedCheckBox = new JCheckBox();
		JLabel label1 = new JLabel();
		JLabel label2 = new JLabel();
		JPanel panel1 = new JPanel();
		styleNoneRadioButton = new JRadioButton();
		styleFrameRadioButton = new JRadioButton();
		stylePlainRadioButton = new JRadioButton();
		styleInfoRadioButton = new JRadioButton();
		styleErrorRadioButton = new JRadioButton();
		styleQuestionRadioButton = new JRadioButton();
		styleWarningRadioButton = new JRadioButton();
		styleColorChooserRadioButton = new JRadioButton();
		styleFileChooserRadioButton = new JRadioButton();
		JPanel panel2 = new JPanel();
		iconNoneRadioButton = new JRadioButton();
		iconTestAllRadioButton = new JRadioButton();
		iconTestRandomRadioButton = new JRadioButton();
		JButton openDialogButton = new JButton();
		menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu();
		JMenuItem newMenuItem = new JMenuItem();
		JMenuItem openMenuItem = new JMenuItem();
		JMenuItem closeMenuItem = new JMenuItem();
		JMenuItem closeMenuItem2 = new JMenuItem();
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
		JMenu helpMenu = new JMenu();
		JMenuItem aboutMenuItem = new JMenuItem();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[left]para" +
			"[fill]",
			// rows
			"para[]0" +
			"[]0" +
			"[]0" +
			"[]" +
			"[]" +
			"[top]" +
			"[]"));

		//---- menuBarCheckBox ----
		menuBarCheckBox.setText("menu bar");
		menuBarCheckBox.setSelected(true);
		menuBarCheckBox.addActionListener(e -> menuBarChanged());
		add(menuBarCheckBox, "cell 0 0");

		//---- menuBarEmbeddedCheckBox ----
		menuBarEmbeddedCheckBox.setText("embedded menu bar");
		menuBarEmbeddedCheckBox.setSelected(true);
		menuBarEmbeddedCheckBox.addActionListener(e -> menuBarEmbeddedChanged());
		add(menuBarEmbeddedCheckBox, "cell 0 1");

		//---- resizableCheckBox ----
		resizableCheckBox.setText("resizable");
		resizableCheckBox.setSelected(true);
		resizableCheckBox.addActionListener(e -> resizableChanged());
		add(resizableCheckBox, "cell 0 2");

		//---- maximizedBoundsCheckBox ----
		maximizedBoundsCheckBox.setText("maximized bounds (50,100, 1000,700)");
		maximizedBoundsCheckBox.addActionListener(e -> maximizedBoundsChanged());
		add(maximizedBoundsCheckBox, "cell 1 2");

		//---- undecoratedCheckBox ----
		undecoratedCheckBox.setText("undecorated");
		undecoratedCheckBox.addActionListener(e -> undecoratedChanged());
		add(undecoratedCheckBox, "cell 0 3");

		//---- label1 ----
		label1.setText("Style:");
		add(label1, "cell 0 4");

		//---- label2 ----
		label2.setText("Icon:");
		add(label2, "cell 1 4");

		//======== panel1 ========
		{
			panel1.setLayout(new MigLayout(
				"ltr,insets 0,hidemode 3,gap 0 0",
				// columns
				"[fill]",
				// rows
				"[]" +
				"[]" +
				"[]" +
				"[]" +
				"[]" +
				"[]" +
				"[]" +
				"[]" +
				"[]"));

			//---- styleNoneRadioButton ----
			styleNoneRadioButton.setText("none");
			styleNoneRadioButton.addActionListener(e -> decorationStyleChanged());
			panel1.add(styleNoneRadioButton, "cell 0 0");

			//---- styleFrameRadioButton ----
			styleFrameRadioButton.setText("frame");
			styleFrameRadioButton.setSelected(true);
			styleFrameRadioButton.addActionListener(e -> decorationStyleChanged());
			panel1.add(styleFrameRadioButton, "cell 0 1");

			//---- stylePlainRadioButton ----
			stylePlainRadioButton.setText("plain dialog");
			stylePlainRadioButton.addActionListener(e -> decorationStyleChanged());
			panel1.add(stylePlainRadioButton, "cell 0 2");

			//---- styleInfoRadioButton ----
			styleInfoRadioButton.setText("info dialog");
			styleInfoRadioButton.addActionListener(e -> decorationStyleChanged());
			panel1.add(styleInfoRadioButton, "cell 0 3");

			//---- styleErrorRadioButton ----
			styleErrorRadioButton.setText("error dialog");
			styleErrorRadioButton.addActionListener(e -> decorationStyleChanged());
			panel1.add(styleErrorRadioButton, "cell 0 4");

			//---- styleQuestionRadioButton ----
			styleQuestionRadioButton.setText("question dialog");
			styleQuestionRadioButton.addActionListener(e -> decorationStyleChanged());
			panel1.add(styleQuestionRadioButton, "cell 0 5");

			//---- styleWarningRadioButton ----
			styleWarningRadioButton.setText("warning dialog");
			styleWarningRadioButton.addActionListener(e -> decorationStyleChanged());
			panel1.add(styleWarningRadioButton, "cell 0 6");

			//---- styleColorChooserRadioButton ----
			styleColorChooserRadioButton.setText("color chooser dialog");
			styleColorChooserRadioButton.addActionListener(e -> decorationStyleChanged());
			panel1.add(styleColorChooserRadioButton, "cell 0 7");

			//---- styleFileChooserRadioButton ----
			styleFileChooserRadioButton.setText("file chooser dialog");
			styleFileChooserRadioButton.addActionListener(e -> decorationStyleChanged());
			panel1.add(styleFileChooserRadioButton, "cell 0 8");
		}
		add(panel1, "cell 0 5");

		//======== panel2 ========
		{
			panel2.setLayout(new MigLayout(
				"ltr,insets 0,hidemode 3,gap 0 0",
				// columns
				"[fill]",
				// rows
				"[]" +
				"[]" +
				"[]"));

			//---- iconNoneRadioButton ----
			iconNoneRadioButton.setText("none");
			iconNoneRadioButton.addActionListener(e -> iconChanged());
			panel2.add(iconNoneRadioButton, "cell 0 0");

			//---- iconTestAllRadioButton ----
			iconTestAllRadioButton.setText("test all");
			iconTestAllRadioButton.setSelected(true);
			iconTestAllRadioButton.addActionListener(e -> iconChanged());
			panel2.add(iconTestAllRadioButton, "cell 0 1");

			//---- iconTestRandomRadioButton ----
			iconTestRandomRadioButton.setText("test random");
			iconTestRandomRadioButton.addActionListener(e -> iconChanged());
			panel2.add(iconTestRandomRadioButton, "cell 0 2");
		}
		add(panel2, "cell 1 5");

		//---- openDialogButton ----
		openDialogButton.setText("Open Dialog");
		openDialogButton.addActionListener(e -> openDialog());
		add(openDialogButton, "cell 0 6");

		//======== menuBar ========
		{

			//======== fileMenu ========
			{
				fileMenu.setText("File");
				fileMenu.setMnemonic('F');

				//---- newMenuItem ----
				newMenuItem.setText("New");
				newMenuItem.setMnemonic('N');
				newMenuItem.addActionListener(e -> menuItemActionPerformed(e));
				fileMenu.add(newMenuItem);

				//---- openMenuItem ----
				openMenuItem.setText("Open");
				openMenuItem.setMnemonic('O');
				openMenuItem.addActionListener(e -> menuItemActionPerformed(e));
				fileMenu.add(openMenuItem);
				fileMenu.addSeparator();

				//---- closeMenuItem ----
				closeMenuItem.setText("Close");
				closeMenuItem.setMnemonic('C');
				closeMenuItem.addActionListener(e -> menuItemActionPerformed(e));
				fileMenu.add(closeMenuItem);

				//---- closeMenuItem2 ----
				closeMenuItem2.setText("Close All");
				closeMenuItem2.addActionListener(e -> menuItemActionPerformed(e));
				fileMenu.add(closeMenuItem2);
				fileMenu.addSeparator();

				//---- exitMenuItem ----
				exitMenuItem.setText("Exit");
				exitMenuItem.setMnemonic('X');
				exitMenuItem.addActionListener(e -> menuItemActionPerformed(e));
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
			}
			menuBar.add(viewMenu);

			//======== helpMenu ========
			{
				helpMenu.setText("Help");
				helpMenu.setMnemonic('H');

				//---- aboutMenuItem ----
				aboutMenuItem.setText("About");
				aboutMenuItem.setMnemonic('A');
				aboutMenuItem.addActionListener(e -> menuItemActionPerformed(e));
				helpMenu.add(aboutMenuItem);
			}
			menuBar.add(helpMenu);
		}

		//---- styleButtonGroup ----
		ButtonGroup styleButtonGroup = new ButtonGroup();
		styleButtonGroup.add(styleNoneRadioButton);
		styleButtonGroup.add(styleFrameRadioButton);
		styleButtonGroup.add(stylePlainRadioButton);
		styleButtonGroup.add(styleInfoRadioButton);
		styleButtonGroup.add(styleErrorRadioButton);
		styleButtonGroup.add(styleQuestionRadioButton);
		styleButtonGroup.add(styleWarningRadioButton);
		styleButtonGroup.add(styleColorChooserRadioButton);
		styleButtonGroup.add(styleFileChooserRadioButton);

		//---- iconButtonGroup ----
		ButtonGroup iconButtonGroup = new ButtonGroup();
		iconButtonGroup.add(iconNoneRadioButton);
		iconButtonGroup.add(iconTestAllRadioButton);
		iconButtonGroup.add(iconTestRandomRadioButton);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JCheckBox menuBarCheckBox;
	private JCheckBox menuBarEmbeddedCheckBox;
	private JCheckBox resizableCheckBox;
	private JCheckBox maximizedBoundsCheckBox;
	private JCheckBox undecoratedCheckBox;
	private JRadioButton styleNoneRadioButton;
	private JRadioButton styleFrameRadioButton;
	private JRadioButton stylePlainRadioButton;
	private JRadioButton styleInfoRadioButton;
	private JRadioButton styleErrorRadioButton;
	private JRadioButton styleQuestionRadioButton;
	private JRadioButton styleWarningRadioButton;
	private JRadioButton styleColorChooserRadioButton;
	private JRadioButton styleFileChooserRadioButton;
	private JRadioButton iconNoneRadioButton;
	private JRadioButton iconTestAllRadioButton;
	private JRadioButton iconTestRandomRadioButton;
	private JMenuBar menuBar;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
