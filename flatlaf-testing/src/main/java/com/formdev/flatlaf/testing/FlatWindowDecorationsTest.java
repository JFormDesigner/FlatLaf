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
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.components.FlatTriStateCheckBox;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.MultiResolutionImageSupport;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatWindowDecorationsTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatWindowDecorationsTest" );
			frame.applyComponentOrientationToFrame = true;

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
		menuBarCheckBox.setSelected( window instanceof JFrame );
		maximizedBoundsCheckBox.setEnabled( window instanceof Frame );

		menuBarEmbeddedCheckBox.setSelected( UIManager.getBoolean( "TitlePane.menuBarEmbedded" ) );
		unifiedBackgroundCheckBox.setSelected( UIManager.getBoolean( "TitlePane.unifiedBackground" ) );

		addMenuButton.setEnabled( menuBarCheckBox.isEnabled() );
		addGlueButton.setEnabled( menuBarCheckBox.isEnabled() );
		removeMenuButton.setEnabled( menuBarCheckBox.isEnabled() );
		changeMenuButton.setEnabled( menuBarCheckBox.isEnabled() );
		changeTitleButton.setEnabled( menuBarCheckBox.isEnabled() );

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
			updateDecorationStyleRadioButtons( rootPane );
			rootPane.addPropertyChangeListener( "windowDecorationStyle", e -> {
				updateDecorationStyleRadioButtons( rootPane );
			} );
		}
	}

	private void updateDecorationStyleRadioButtons( JRootPane rootPane ) {
		int style = rootPane.getWindowDecorationStyle();
		if( style == JRootPane.NONE )
			styleNoneRadioButton.setSelected( true );
		else if( style == JRootPane.FRAME )
			styleFrameRadioButton.setSelected( true );
		else if( style == JRootPane.PLAIN_DIALOG )
			stylePlainRadioButton.setSelected( true );
		else if( style == JRootPane.INFORMATION_DIALOG )
			styleInfoRadioButton.setSelected( true );
		else if( style == JRootPane.ERROR_DIALOG )
			styleErrorRadioButton.setSelected( true );
		else if( style == JRootPane.QUESTION_DIALOG )
			styleQuestionRadioButton.setSelected( true );
		else if( style == JRootPane.WARNING_DIALOG )
			styleWarningRadioButton.setSelected( true );
		else if( style == JRootPane.COLOR_CHOOSER_DIALOG )
			styleColorChooserRadioButton.setSelected( true );
		else if( style == JRootPane.FILE_CHOOSER_DIALOG )
			styleFileChooserRadioButton.setSelected( true );
	}

	private void unifiedBackgroundChanged() {
		UIManager.put( "TitlePane.unifiedBackground", unifiedBackgroundCheckBox.isSelected() );
		Window window = SwingUtilities.windowForComponent( this );
		if( window != null )
			window.repaint();
	}

	private void menuBarChanged() {
		Window window = SwingUtilities.windowForComponent( this );
		if( window instanceof JFrame )
			((JFrame)window).setJMenuBar( menuBarCheckBox.isSelected() ? menuBar : null );
		else if( window instanceof JDialog )
			((JDialog)window).setJMenuBar( menuBarCheckBox.isSelected() ? menuBar : null );
		window.revalidate();
		window.repaint();
	}

	private void menuBarEmbeddedChanged() {
		JRootPane rootPane = getWindowRootPane();
		if( rootPane != null )
			rootPane.putClientProperty( FlatClientProperties.MENU_BAR_EMBEDDED, menuBarEmbeddedCheckBox.isSelected() );
	}

	private void menuBarVisibleChanged() {
		menuBar.setVisible( menuBarVisibleCheckBox.isSelected() );
	}

	private void rightCompChanged() {
		removeNonMenusFromMenuBar();

		if( rightCompCheckBox.isSelected() ) {
			rightStretchCompCheckBox.setSelected( false );

			JButton myButton = new JButton( "?" );
			myButton.putClientProperty( "JButton.buttonType", "toolBarButton" );
			myButton.setFocusable( false );

			menuBar.add( Box.createGlue() );
			menuBar.add( myButton );
		}

		menuBar.revalidate();
		menuBar.repaint();
	}

	private void rightStretchCompChanged() {
		removeNonMenusFromMenuBar();

		if( rightStretchCompCheckBox.isSelected() ) {
			rightCompCheckBox.setSelected( false );

			menuBar.add( Box.createGlue() );
			menuBar.add( new JProgressBar() );
		}

		menuBar.revalidate();
		menuBar.repaint();
	}

	private void removeNonMenusFromMenuBar() {
		Component[] components = menuBar.getComponents();
		for( int i = components.length - 1; i >= 0; i-- ) {
			if( !(components[i] instanceof JMenu) )
				menuBar.remove( i );
			else
				break;
		}
	}

	private void colorizeTitleBar() {
		JRootPane rootPane = getWindowRootPane();
		if( rootPane == null )
			return;

		boolean colorize = colorizeTitleBarCheckBox.isSelected();
		rootPane.putClientProperty( FlatClientProperties.TITLE_BAR_BACKGROUND, colorize ? Color.green : null );
		rootPane.putClientProperty( FlatClientProperties.TITLE_BAR_FOREGROUND, colorize ? Color.blue : null );
	}

	private void colorizeMenuBar() {
		boolean colorize = colorizeMenuBarCheckBox.isSelected();
		Color menuBarBackground = colorize ? new Color( 0xffccff ) : UIManager.getColor( "MenuBar.background" );

		menuBar.setOpaque( colorize );
		menuBar.setBackground( menuBarBackground );
	}

	private void colorizeMenus() {
		boolean colorize = colorizeMenusCheckBox.isSelected();
		Color menuBackground = colorize ? new Color( 0xaaffff ) : UIManager.getColor( "Menu.background" );

		for( Component c : menuBar.getComponents() ) {
			if( c instanceof JMenu ) {
				((JMenu)c).setOpaque( colorize );
				c.setBackground( menuBackground );
			}
		}
	}

	private void addMenu() {
		JMenu menu = new JMenu( "Hello" );
		menu.add( new JMenuItem( "world" ) );

		if( colorizeMenusCheckBox.isSelected() ) {
			menu.setOpaque( true );
			menu.setBackground( new Color( 0xaaffff ) );
		}

		menuBar.add( menu );
		menuBar.revalidate();
	}

	private void addGlue() {
		for( Component c : menuBar.getComponents() ) {
			if( c instanceof Box.Filler )
				return;
		}
		menuBar.add( Box.createGlue() );
		menuBar.revalidate();
	}

	private void removeMenu() {
		int menuCount = menuBar.getMenuCount();
		if( menuCount <= 0 )
			return;

		menuBar.remove( menuCount - 1 );
		menuBar.revalidate();
	}

	private void changeMenu() {
		int menuCount = menuBar.getMenuCount();
		if( menuCount <= 0 )
			return;

		int len = new Random().nextInt( 20 );
		String text = "1234567890abcdefghij".substring( 0, len + 1 );
		menuBar.getMenu( menuCount - 1 ).setText( text );
	}

	private void changeTitle() {
		Window window = SwingUtilities.windowForComponent( this );
		if( window instanceof Frame )
			((Frame)window).setTitle( ((Frame)window).getTitle() + " bla" );
		else if( window instanceof Dialog )
			((Dialog)window).setTitle( ((Dialog)window).getTitle() + " bla" );
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

	private void fullScreenChanged() {
		boolean fullScreen = fullScreenCheckBox.isSelected();

		GraphicsDevice gd = getGraphicsConfiguration().getDevice();
		gd.setFullScreenWindow( fullScreen ? SwingUtilities.windowForComponent( this ) : null );
	}

	private void menuItemActionPerformed(ActionEvent e) {
		SwingUtilities.invokeLater( () -> {
			JOptionPane.showMessageDialog( this, e.getActionCommand(), "Menu Item", JOptionPane.PLAIN_MESSAGE );
		} );
	}

	private void openDialog() {
		Window owner = SwingUtilities.windowForComponent( this );
		JDialog dialog = new JDialog( owner, "Dialog", ModalityType.DOCUMENT_MODAL );
		dialog.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
		dialog.add( new FlatWindowDecorationsTest() );
		dialog.pack();
		dialog.setLocationRelativeTo( this );
		dialog.setVisible( true );
	}

	private void openFrame() {
		FlatWindowDecorationsTest comp = new FlatWindowDecorationsTest();
		JFrame frame = new JFrame( "Frame" );
		frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		frame.add( comp );
		frame.setJMenuBar( comp.menuBar );
		frame.pack();
		frame.setLocationRelativeTo( this );
		frame.setVisible( true );
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
		else if( iconTestMRIRadioButton.isSelected() ) {
			ArrayList<Image> sortedImages = new ArrayList<>( images );
			sortedImages.sort( (image1, image2) -> {
				return image1.getWidth( null ) - image2.getWidth( null );
			} );
			window.setIconImage( MultiResolutionImageSupport.create( 0, sortedImages.toArray( new Image[sortedImages.size()] ) ) );
		} else if( iconTestDynMRIRadioButton.isSelected() ) {
			window.setIconImage( MultiResolutionImageSupport.create( 0,
				new Dimension[] {
					new Dimension( 16, 16 ),
			}, dim -> {
				BufferedImage image = new BufferedImage( dim.width, dim.height, BufferedImage.TYPE_INT_ARGB );
				Graphics2D g = image.createGraphics();
				try {
					g.setColor( Color.getHSBColor( (dim.width - 16) / 64f, 1, 0.8f ) );
					g.fillRect( 0, 0, dim.width, dim.height );

					g.setColor( Color.white );
					g.setFont( new Font( "Dialog", Font.PLAIN, (int) (dim.width * 0.8) ) );
					FlatUIUtils.drawString( this, g, String.valueOf( dim.width ), 0, dim.height - 2 );
				} finally {
					g.dispose();
				}
				return image;
			} ) );
		}
	}

	private void showIconChanged() {
		JRootPane rootPane = getWindowRootPane();
		if( rootPane != null )
			rootPane.putClientProperty( FlatClientProperties.TITLE_BAR_SHOW_ICON, showIconCheckBox.getChecked() );
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
		rightCompCheckBox = new JCheckBox();
		JPanel panel3 = new JPanel();
		addMenuButton = new JButton();
		addGlueButton = new JButton();
		removeMenuButton = new JButton();
		changeMenuButton = new JButton();
		changeTitleButton = new JButton();
		menuBarEmbeddedCheckBox = new JCheckBox();
		rightStretchCompCheckBox = new JCheckBox();
		menuBarVisibleCheckBox = new JCheckBox();
		colorizeMenuBarCheckBox = new JCheckBox();
		unifiedBackgroundCheckBox = new JCheckBox();
		colorizeMenusCheckBox = new JCheckBox();
		colorizeTitleBarCheckBox = new JCheckBox();
		resizableCheckBox = new JCheckBox();
		maximizedBoundsCheckBox = new JCheckBox();
		undecoratedCheckBox = new JCheckBox();
		fullScreenCheckBox = new JCheckBox();
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
		iconTestMRIRadioButton = new JRadioButton();
		iconTestDynMRIRadioButton = new JRadioButton();
		showIconCheckBox = new FlatTriStateCheckBox();
		JButton openDialogButton = new JButton();
		JButton openFrameButton = new JButton();
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
			"[left]" +
			"[fill]",
			// rows
			"para[]0" +
			"[]0" +
			"[]0" +
			"[]0" +
			"[]unrel" +
			"[]0" +
			"[]unrel" +
			"[]" +
			"[top]" +
			"[]"));

		//---- menuBarCheckBox ----
		menuBarCheckBox.setText("menu bar");
		menuBarCheckBox.setSelected(true);
		menuBarCheckBox.addActionListener(e -> menuBarChanged());
		add(menuBarCheckBox, "cell 0 0");

		//---- rightCompCheckBox ----
		rightCompCheckBox.setText("right aligned component");
		rightCompCheckBox.addActionListener(e -> rightCompChanged());
		add(rightCompCheckBox, "cell 1 0");

		//======== panel3 ========
		{
			panel3.setLayout(new MigLayout(
				"hidemode 3",
				// columns
				"[fill]",
				// rows
				"[]" +
				"[]" +
				"[]" +
				"[]unrel" +
				"[]"));

			//---- addMenuButton ----
			addMenuButton.setText("Add menu");
			addMenuButton.addActionListener(e -> addMenu());
			panel3.add(addMenuButton, "cell 0 0");

			//---- addGlueButton ----
			addGlueButton.setText("Add glue");
			addGlueButton.addActionListener(e -> addGlue());
			panel3.add(addGlueButton, "cell 0 1");

			//---- removeMenuButton ----
			removeMenuButton.setText("Remove menu");
			removeMenuButton.addActionListener(e -> removeMenu());
			panel3.add(removeMenuButton, "cell 0 2");

			//---- changeMenuButton ----
			changeMenuButton.setText("Change menu");
			changeMenuButton.addActionListener(e -> changeMenu());
			panel3.add(changeMenuButton, "cell 0 3");

			//---- changeTitleButton ----
			changeTitleButton.setText("Change title");
			changeTitleButton.addActionListener(e -> changeTitle());
			panel3.add(changeTitleButton, "cell 0 4");
		}
		add(panel3, "cell 2 0 1 8,aligny top,growy 0");

		//---- menuBarEmbeddedCheckBox ----
		menuBarEmbeddedCheckBox.setText("embedded menu bar");
		menuBarEmbeddedCheckBox.setSelected(true);
		menuBarEmbeddedCheckBox.addActionListener(e -> menuBarEmbeddedChanged());
		add(menuBarEmbeddedCheckBox, "cell 0 1");

		//---- rightStretchCompCheckBox ----
		rightStretchCompCheckBox.setText("right aligned stretching component");
		rightStretchCompCheckBox.addActionListener(e -> rightStretchCompChanged());
		add(rightStretchCompCheckBox, "cell 1 1");

		//---- menuBarVisibleCheckBox ----
		menuBarVisibleCheckBox.setText("menu bar visible");
		menuBarVisibleCheckBox.setSelected(true);
		menuBarVisibleCheckBox.addActionListener(e -> menuBarVisibleChanged());
		add(menuBarVisibleCheckBox, "cell 0 2");

		//---- colorizeMenuBarCheckBox ----
		colorizeMenuBarCheckBox.setText("colorize menu bar");
		colorizeMenuBarCheckBox.addActionListener(e -> colorizeMenuBar());
		add(colorizeMenuBarCheckBox, "cell 1 2");

		//---- unifiedBackgroundCheckBox ----
		unifiedBackgroundCheckBox.setText("unified background");
		unifiedBackgroundCheckBox.addActionListener(e -> unifiedBackgroundChanged());
		add(unifiedBackgroundCheckBox, "cell 0 3");

		//---- colorizeMenusCheckBox ----
		colorizeMenusCheckBox.setText("colorize menus");
		colorizeMenusCheckBox.addActionListener(e -> colorizeMenus());
		add(colorizeMenusCheckBox, "cell 1 3");

		//---- colorizeTitleBarCheckBox ----
		colorizeTitleBarCheckBox.setText("colorize title bar");
		colorizeTitleBarCheckBox.addActionListener(e -> colorizeTitleBar());
		add(colorizeTitleBarCheckBox, "cell 0 4");

		//---- resizableCheckBox ----
		resizableCheckBox.setText("resizable");
		resizableCheckBox.setSelected(true);
		resizableCheckBox.addActionListener(e -> resizableChanged());
		add(resizableCheckBox, "cell 0 5");

		//---- maximizedBoundsCheckBox ----
		maximizedBoundsCheckBox.setText("maximized bounds (50,100, 1000,700)");
		maximizedBoundsCheckBox.addActionListener(e -> maximizedBoundsChanged());
		add(maximizedBoundsCheckBox, "cell 1 5");

		//---- undecoratedCheckBox ----
		undecoratedCheckBox.setText("undecorated");
		undecoratedCheckBox.addActionListener(e -> undecoratedChanged());
		add(undecoratedCheckBox, "cell 0 6");

		//---- fullScreenCheckBox ----
		fullScreenCheckBox.setText("full screen");
		fullScreenCheckBox.addActionListener(e -> fullScreenChanged());
		add(fullScreenCheckBox, "cell 1 6");

		//---- label1 ----
		label1.setText("Style:");
		add(label1, "cell 0 7");

		//---- label2 ----
		label2.setText("Icon:");
		add(label2, "cell 1 7");

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
		add(panel1, "cell 0 8");

		//======== panel2 ========
		{
			panel2.setLayout(new MigLayout(
				"ltr,insets 0,hidemode 3,gap 0 0",
				// columns
				"[left]",
				// rows
				"[]" +
				"[]" +
				"[]" +
				"[]" +
				"[]rel" +
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

			//---- iconTestMRIRadioButton ----
			iconTestMRIRadioButton.setText("test multi-resolution (Java 9+)");
			iconTestMRIRadioButton.addActionListener(e -> iconChanged());
			panel2.add(iconTestMRIRadioButton, "cell 0 3");

			//---- iconTestDynMRIRadioButton ----
			iconTestDynMRIRadioButton.setText("test dynamic multi-resolution (Java 9+)");
			iconTestDynMRIRadioButton.addActionListener(e -> iconChanged());
			panel2.add(iconTestDynMRIRadioButton, "cell 0 4");

			//---- showIconCheckBox ----
			showIconCheckBox.setText("show icon");
			showIconCheckBox.addActionListener(e -> showIconChanged());
			panel2.add(showIconCheckBox, "cell 0 5");
		}
		add(panel2, "cell 1 8");

		//---- openDialogButton ----
		openDialogButton.setText("Open Dialog");
		openDialogButton.addActionListener(e -> openDialog());
		add(openDialogButton, "cell 0 9 2 1");

		//---- openFrameButton ----
		openFrameButton.setText("Open Frame");
		openFrameButton.setMnemonic('A');
		openFrameButton.addActionListener(e -> openFrame());
		add(openFrameButton, "cell 0 9 2 1");

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
		iconButtonGroup.add(iconTestMRIRadioButton);
		iconButtonGroup.add(iconTestDynMRIRadioButton);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JCheckBox menuBarCheckBox;
	private JCheckBox rightCompCheckBox;
	private JButton addMenuButton;
	private JButton addGlueButton;
	private JButton removeMenuButton;
	private JButton changeMenuButton;
	private JButton changeTitleButton;
	private JCheckBox menuBarEmbeddedCheckBox;
	private JCheckBox rightStretchCompCheckBox;
	private JCheckBox menuBarVisibleCheckBox;
	private JCheckBox colorizeMenuBarCheckBox;
	private JCheckBox unifiedBackgroundCheckBox;
	private JCheckBox colorizeMenusCheckBox;
	private JCheckBox colorizeTitleBarCheckBox;
	private JCheckBox resizableCheckBox;
	private JCheckBox maximizedBoundsCheckBox;
	private JCheckBox undecoratedCheckBox;
	private JCheckBox fullScreenCheckBox;
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
	private JRadioButton iconTestMRIRadioButton;
	private JRadioButton iconTestDynMRIRadioButton;
	private FlatTriStateCheckBox showIconCheckBox;
	private JMenuBar menuBar;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
