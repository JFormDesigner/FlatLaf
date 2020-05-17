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

package com.formdev.flatlaf.testing;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.IntelliJTheme;
import com.formdev.flatlaf.demo.LookAndFeelsComboBox;
import com.formdev.flatlaf.demo.DemoPrefs;
import com.formdev.flatlaf.demo.intellijthemes.*;
import com.formdev.flatlaf.extras.*;
import com.formdev.flatlaf.extras.TriStateCheckBox.State;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.SystemInfo;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatTestFrame
	extends JFrame
{
	private static final String PREFS_ROOT_PATH = "/flatlaf-test";
	private static final String KEY_SCALE_FACTOR = "scaleFactor";

	private final String title;
	private Supplier<JComponent> contentFactory;
	private JComponent content;
	private FlatInspector inspector;

	public boolean useApplyComponentOrientation;

	public static FlatTestFrame create( String[] args, String title ) {
		DemoPrefs.init( PREFS_ROOT_PATH );

		// set scale factor
		if( System.getProperty( "flatlaf.uiScale", System.getProperty( "sun.java2d.uiScale" ) ) == null ) {
			String scaleFactor = DemoPrefs.getState().get( KEY_SCALE_FACTOR, null );
			if( scaleFactor != null )
				System.setProperty( "flatlaf.uiScale", scaleFactor );
		}

		// set look and feel
		DemoPrefs.initLaf( args );

		// create frame
		return new FlatTestFrame( title );
	}

	private FlatTestFrame( String title ) {
		this.title = title;

		initComponents();

		// initialize look and feels combo box
		DefaultComboBoxModel<LookAndFeelInfo> lafModel = new DefaultComboBoxModel<>();
		lafModel.addElement( new LookAndFeelInfo( "Flat Light (F1)", FlatLightLaf.class.getName() ) );
		lafModel.addElement( new LookAndFeelInfo( "Flat Dark (F2)", FlatDarkLaf.class.getName() ) );
		lafModel.addElement( new LookAndFeelInfo( "Flat IntelliJ (F3)", FlatIntelliJLaf.class.getName() ) );
		lafModel.addElement( new LookAndFeelInfo( "Flat Darcula (F4)", FlatDarculaLaf.class.getName() ) );
		lafModel.addElement( new LookAndFeelInfo( "Flat Test (F8)", FlatTestLaf.class.getName() ) );

		UIManager.LookAndFeelInfo[] lookAndFeels = UIManager.getInstalledLookAndFeels();
		for( UIManager.LookAndFeelInfo lookAndFeel : lookAndFeels ) {
			String name = lookAndFeel.getName();
			String className = lookAndFeel.getClassName();
			if( className.equals( "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel" ) ||
				className.equals( "com.sun.java.swing.plaf.motif.MotifLookAndFeel" ) )
			  continue;

			if( (SystemInfo.IS_WINDOWS && className.equals( "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" )) ||
				(SystemInfo.IS_MAC && className.equals( "com.apple.laf.AquaLookAndFeel") ) ||
				(SystemInfo.IS_LINUX && className.equals( "com.sun.java.swing.plaf.gtk.GTKLookAndFeel") ) )
				name += " (F9)";
			else if( className.equals( MetalLookAndFeel.class.getName() ) )
				name += " (F12)";
			else if( className.equals( NimbusLookAndFeel.class.getName() ) )
				name += " (F11)";

			lafModel.addElement( new LookAndFeelInfo( name, className ) );
		}

		String substanceClassName = "org.pushingpixels.substance.api.skin.SubstanceGraphiteAquaLookAndFeel";
		if( SystemInfo.IS_JAVA_9_OR_LATER && isClassAvailable( substanceClassName ) ) {
			lafModel.addElement( new LookAndFeelInfo( "Substance (F5)", substanceClassName ) );
			registerSwitchToLookAndFeel( "F5", substanceClassName );
		}

		String webLafClassName = "com.alee.laf.WebLookAndFeel";
		if( isClassAvailable( webLafClassName ) ) {
			lafModel.addElement( new LookAndFeelInfo( "WebLaf (Ctrl+F12)", webLafClassName ) );
			registerSwitchToLookAndFeel( "ctrl F12", webLafClassName );
		}

		String looksPlasticClassName = "com.jgoodies.looks.plastic.PlasticLookAndFeel";
		if( isClassAvailable( looksPlasticClassName ) ) {
			lafModel.addElement( new LookAndFeelInfo( "JGoodies Looks Plastic (F6)", looksPlasticClassName ) );
			registerSwitchToLookAndFeel( "F6", looksPlasticClassName );
		}

		String looksWindowsClassName = "com.jgoodies.looks.windows.WindowsLookAndFeel";
		if( SystemInfo.IS_WINDOWS && isClassAvailable( looksWindowsClassName ) ) {
			lafModel.addElement( new LookAndFeelInfo( "JGoodies Looks Windows (F7)", looksWindowsClassName ) );
			registerSwitchToLookAndFeel( "F7", looksWindowsClassName );
		}

		lookAndFeelComboBox.setModel( lafModel );

		updateScaleFactorComboBox();
		String scaleFactor = System.getProperty( "flatlaf.uiScale", System.getProperty( "sun.java2d.uiScale" ) );
		if( scaleFactor != null )
			scaleFactorComboBox.setSelectedItem( scaleFactor );

		updateSizeVariantComboBox();

		// register F1, F2, ... keys to switch to Light, Dark or other LaFs
		registerSwitchToLookAndFeel( "F1", FlatLightLaf.class.getName() );
		registerSwitchToLookAndFeel( "F2", FlatDarkLaf.class.getName() );
		registerSwitchToLookAndFeel( "F3", FlatIntelliJLaf.class.getName() );
		registerSwitchToLookAndFeel( "F4", FlatDarculaLaf.class.getName() );

		registerSwitchToLookAndFeel( "F8", FlatTestLaf.class.getName() );

		if( SystemInfo.IS_WINDOWS )
			registerSwitchToLookAndFeel( "F9", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" );
		else if( SystemInfo.IS_MAC )
			registerSwitchToLookAndFeel( "F9", "com.apple.laf.AquaLookAndFeel" );
		else if( SystemInfo.IS_LINUX )
			registerSwitchToLookAndFeel( "F9", "com.sun.java.swing.plaf.gtk.GTKLookAndFeel" );
		registerSwitchToLookAndFeel( "F12", MetalLookAndFeel.class.getName() );
		registerSwitchToLookAndFeel( "F11", NimbusLookAndFeel.class.getName() );

		// register ESC key to close frame
		((JComponent)getContentPane()).registerKeyboardAction(
			e -> {
				dispose();
			},
			KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false ),
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );

		// make the "close" button the default button
		getRootPane().setDefaultButton( closeButton );

		// close frame
		closeButton.addActionListener(e -> dispose());

		// update title
		addWindowListener( new WindowAdapter() {
			@Override
			public void windowOpened( WindowEvent e ) {
				updateTitle();
			}
		} );

		// update title when moved to another screen
		addComponentListener( new ComponentAdapter() {
			@Override
			public void componentMoved( ComponentEvent e ) {
				updateTitle();
			}
		} );

		UIManager.addPropertyChangeListener( e -> {
			if( "lookAndFeel".equals( e.getPropertyName() ) ) {
				EventQueue.invokeLater( () -> {
					// update title because user scale factor may change
					updateTitle();

					// enable/disable scale factor combobox
					updateScaleFactorComboBox();

					// show/hide size variant combobox
					updateSizeVariantComboBox();

					// this is necessary because embedded JOptionPane's "steal" the default button
					getRootPane().setDefaultButton( closeButton );
				} );
			}
		} );

		UIScale.addPropertyChangeListener( e -> {
			// update title because user scale factor may change
			updateTitle();
		} );
	}

	private void updateTitle() {
		double systemScaleFactor = UIScale.getSystemScaleFactor( getGraphicsConfiguration() );
		float userScaleFactor = UIScale.getUserScaleFactor();
		String newTitle = title + " (Java " + System.getProperty( "java.version" )
			+ (systemScaleFactor != 1 ? (";  system scale factor " + systemScaleFactor) : "")
			+ (userScaleFactor != 1 ? (";  user scale factor " + userScaleFactor) : "")
			+ (systemScaleFactor == 1 && userScaleFactor == 1 ? "; no scaling" : "")
			+ ")";

		if( !newTitle.equals( getTitle() ) )
			setTitle( newTitle );
	}

	private void registerSwitchToLookAndFeel( String keyStrokeStr, String lafClassName ) {
		KeyStroke keyStroke = KeyStroke.getKeyStroke( keyStrokeStr );
		if( keyStroke == null )
			throw new IllegalArgumentException( "Invalid key stroke '" + keyStrokeStr + "'" );

		((JComponent)getContentPane()).registerKeyboardAction(
			e -> {
				selectLookAndFeel( lafClassName );
			},
			keyStroke,
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
	}

	private boolean isClassAvailable( String className ) {
		try {
			Class.forName( className, false, getClass().getClassLoader() );
			return true;
		} catch( Throwable ex ) {
			return false;
		}
	}

	public void showFrame( Supplier<JComponent> contentFactory ) {
		showFrame( contentFactory, null );
	}

	public void showFrame( Supplier<JComponent> contentFactory, Function<JComponent, JMenuBar> menuBarFactory ) {
		this.contentFactory = contentFactory;
		this.content = contentFactory.get();

		if( menuBarFactory != null )
			setJMenuBar( menuBarFactory.apply( content ) );

		contentPanel.getContentPane().add( content );
		pack();
		setLocationRelativeTo( null );
		setVisible( true );

		EventQueue.invokeLater( () -> {
			closeButton.requestFocusInWindow();
		} );
	}

	private void selectLookAndFeel( String lafClassName ) {
		lookAndFeelComboBox.setSelectedLookAndFeel( lafClassName );
	}

	private void lookAndFeelChanged() {
		String lafClassName = lookAndFeelComboBox.getSelectedLookAndFeel();
		if( lafClassName == null )
			return;

		if( lafClassName.equals( UIManager.getLookAndFeel().getClass().getName() ) )
			return;

		// hide popup to avoid occasional StackOverflowError when updating UI
		lookAndFeelComboBox.setPopupVisible( false );

		applyLookAndFeel( lafClassName, null, false );
	}

	private void applyLookAndFeel( String lafClassName, IntelliJTheme theme, boolean pack ) {
		EventQueue.invokeLater( () -> {
			try {
				// change look and feel
				if( theme != null )
					UIManager.setLookAndFeel( IntelliJTheme.createLaf( theme ) );
				else
					UIManager.setLookAndFeel( lafClassName );

				// update all components
				FlatLaf.updateUI();

				// increase size of frame if necessary
				if( pack )
					pack();
				else {
					int width = getWidth();
					int height = getHeight();
					Dimension prefSize = getPreferredSize();
					if( prefSize.width > width || prefSize.height > height )
						setSize( Math.max( prefSize.width, width ), Math.max( prefSize.height, height ) );
				}

				// limit frame size to screen size
				Rectangle screenBounds = getGraphicsConfiguration().getBounds();
				screenBounds = FlatUIUtils.subtractInsets( screenBounds, getToolkit().getScreenInsets( getGraphicsConfiguration() ) );
				Dimension frameSize = getSize();
				if( frameSize.width > screenBounds.width || frameSize.height > screenBounds.height )
					setSize( Math.min( frameSize.width, screenBounds.width ), Math.min( frameSize.height, screenBounds.height ) );

				// move frame to left/top if necessary
				if( getX() + getWidth() > screenBounds.x + screenBounds.width ||
					getY() + getHeight() > screenBounds.y + screenBounds.height )
				{
					setLocation( Math.min( getX(), screenBounds.x + screenBounds.width - getWidth() ),
								 Math.min( getY(), screenBounds.y + screenBounds.height - getHeight() ) );
				}

				if( inspector != null )
					inspector.update();

			} catch( Exception ex ) {
				ex.printStackTrace();
			}
		} );
	}

	private void explicitColorsChanged() {
		EventQueue.invokeLater( () -> {
			boolean explicit = explicitColorsCheckBox.isSelected();
			ColorUIResource restoreColor = new ColorUIResource( Color.white );

			updateComponentsRecur( content, (c, type) -> {
				if( type == "view" || type == "tab" ) {
					c.setForeground( explicit ? Color.magenta : restoreColor );
					c.setBackground( explicit ? Color.orange : restoreColor );
				} else {
					c.setForeground( explicit ? Color.blue : restoreColor );
					c.setBackground( explicit ? Color.green : restoreColor );
				}
			} );

			// because colors may depend on state (e.g. disabled JTextField)
			// it is best to update all UI delegates to get correct result
			if( !explicit )
				SwingUtilities.updateComponentTreeUI( content );
		} );
	}

	private void backgroundChanged() {
		contentPanel.repaint();
	}

	boolean isPaintBackgroundPattern() {
		return backgroundCheckBox.isSelected();
	}

	private void rightToLeftChanged() {
		ComponentOrientation orientation = rightToLeftCheckBox.isSelected()
			? ComponentOrientation.RIGHT_TO_LEFT
			: ComponentOrientation.LEFT_TO_RIGHT;

		if( useApplyComponentOrientation )
			content.applyComponentOrientation( orientation );
		else {
			updateComponentsRecur( content, (c, type) -> {
				c.setComponentOrientation( orientation );
			} );
		}
		contentPanel.revalidate();
		contentPanel.repaint();
	}

	private void enabledChanged() {
		boolean enabled = enabledCheckBox.isSelected();
		updateComponentsRecur( content, (c, type) -> {
			c.setEnabled( enabled );
		} );
	}

	private void inspectChanged() {
		if( inspector == null )
			inspector = new FlatInspector( contentPanel );
		inspector.setEnabled( inspectCheckBox.isSelected() );
	}

	private void scaleFactorChanged() {
		String scaleFactor = (String) scaleFactorComboBox.getSelectedItem();
		if( "default".equals( scaleFactor ) )
			scaleFactor = null;

		// hide popup to avoid occasional StackOverflowError when updating UI
		scaleFactorComboBox.setPopupVisible( false );

		if( scaleFactor != null ) {
			System.setProperty( "flatlaf.uiScale", scaleFactor );
			DemoPrefs.getState().put( KEY_SCALE_FACTOR, scaleFactor );
		} else {
			System.clearProperty( "flatlaf.uiScale" );
			DemoPrefs.getState().remove( KEY_SCALE_FACTOR );
		}

		LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
		IntelliJTheme theme = (lookAndFeel instanceof IntelliJTheme.ThemeLaf)
			? ((IntelliJTheme.ThemeLaf)lookAndFeel).getTheme()
			: null;
		applyLookAndFeel( lookAndFeel.getClass().getName(), theme, true );
	}

	private void updateScaleFactorComboBox() {
		scaleFactorComboBox.setEnabled( UIManager.getLookAndFeel() instanceof FlatLaf );
	}

	private void sizeVariantChanged() {
		String sel = (String) sizeVariantComboBox.getSelectedItem();
		String sizeVariant = "default".equals( sel ) ? null : sel;

		updateComponentsRecur( content, (c, type) -> {
			if( c instanceof JComponent )
				((JComponent)c).putClientProperty( "JComponent.sizeVariant", sizeVariant );
		} );
	}

	private void updateSizeVariantComboBox() {
		LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
		boolean visible = lookAndFeel instanceof NimbusLookAndFeel ||
			"com.apple.laf.AquaLookAndFeel".equals( lookAndFeel.getClass().getName() );
		sizeVariantComboBox.setVisible( visible );
	}

	void updateComponentsRecur( Container container, BiConsumer<Component, String> action ) {
		for( Component c : container.getComponents() ) {
			if( c instanceof JPanel || c instanceof JDesktopPane ) {
				updateComponentsRecur( (Container) c, action );
				continue;
			}

			action.accept( c, null );

			if( c instanceof JScrollPane ) {
				Component view = ((JScrollPane)c).getViewport().getView();
				if( view != null )
					action.accept( view, "view" );
			} else if( c instanceof JTabbedPane ) {
				JTabbedPane tabPane = (JTabbedPane)c;
				int tabCount = tabPane.getTabCount();
				for( int i = 0; i < tabCount; i++ ) {
					Component tab = tabPane.getComponentAt( i );
					if( tab != null )
						action.accept( tab, "tab" );
				}
			}

			if( c instanceof JToolBar )
				updateComponentsRecur( (JToolBar) c, action );
		}
	}

	private void opaqueChanged() {
		State opaque = opaqueTriStateCheckBox.getState();
		if( opaque == State.INDETERMINATE )
			recreateContent();
		else {
			updateComponentsRecur( content, (c, type) -> {
				if( c instanceof JComponent )
					((JComponent)c).setOpaque( opaque == State.SELECTED );
			} );
			contentPanel.repaint();
		}
	}

	private void recreateContent() {
		contentPanel.getContentPane().remove( content );
		content = contentFactory.get();
		contentPanel.getContentPane().add( content );

		if( rightToLeftCheckBox.isSelected() )
			rightToLeftChanged();
		if( !enabledCheckBox.isSelected() )
			enabledChanged();
		if( explicitColorsCheckBox.isSelected() )
			explicitColorsChanged();

		contentPanel.revalidate();
		contentPanel.repaint();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		dialogPane = new JPanel();
		contentPanel = new JRootPane();
		buttonBar = new JPanel();
		lookAndFeelComboBox = new LookAndFeelsComboBox();
		scaleFactorComboBox = new JComboBox<>();
		rightToLeftCheckBox = new JCheckBox();
		enabledCheckBox = new JCheckBox();
		inspectCheckBox = new JCheckBox();
		explicitColorsCheckBox = new JCheckBox();
		backgroundCheckBox = new JCheckBox();
		opaqueTriStateCheckBox = new TriStateCheckBox();
		sizeVariantComboBox = new JComboBox<>();
		closeButton = new JButton();
		themesPanel = new IJThemesPanel();

		//======== this ========
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				Container contentPanelContentPane = contentPanel.getContentPane();
				contentPanelContentPane.setLayout(new MigLayout(
					"insets 0,hidemode 3",
					// columns
					"[grow,fill]",
					// rows
					"[grow,fill]"));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setLayout(new MigLayout(
					"insets dialog",
					// columns
					"[fill]" +
					"[fill]" +
					"[fill]" +
					"[fill]" +
					"[fill]" +
					"[fill]" +
					"[fill]" +
					"[fill]" +
					"[fill]" +
					"[grow,fill]" +
					"[button,fill]",
					// rows
					null));

				//---- lookAndFeelComboBox ----
				lookAndFeelComboBox.setMaximumRowCount(20);
				lookAndFeelComboBox.addActionListener(e -> lookAndFeelChanged());
				buttonBar.add(lookAndFeelComboBox, "cell 0 0");

				//---- scaleFactorComboBox ----
				scaleFactorComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
					"default",
					"1",
					"1.25",
					"1.5",
					"1.75",
					"2.0",
					"2.25",
					"2.5",
					"3",
					"3.5",
					"4"
				}));
				scaleFactorComboBox.setMaximumRowCount(20);
				scaleFactorComboBox.addActionListener(e -> scaleFactorChanged());
				buttonBar.add(scaleFactorComboBox, "cell 1 0");

				//---- rightToLeftCheckBox ----
				rightToLeftCheckBox.setText("right-to-left");
				rightToLeftCheckBox.setMnemonic('R');
				rightToLeftCheckBox.addActionListener(e -> rightToLeftChanged());
				buttonBar.add(rightToLeftCheckBox, "cell 2 0");

				//---- enabledCheckBox ----
				enabledCheckBox.setText("enabled");
				enabledCheckBox.setMnemonic('E');
				enabledCheckBox.setSelected(true);
				enabledCheckBox.addActionListener(e -> enabledChanged());
				buttonBar.add(enabledCheckBox, "cell 3 0");

				//---- inspectCheckBox ----
				inspectCheckBox.setText("inspect");
				inspectCheckBox.setMnemonic('I');
				inspectCheckBox.addActionListener(e -> inspectChanged());
				buttonBar.add(inspectCheckBox, "cell 4 0");

				//---- explicitColorsCheckBox ----
				explicitColorsCheckBox.setText("explicit colors");
				explicitColorsCheckBox.setMnemonic('X');
				explicitColorsCheckBox.addActionListener(e -> explicitColorsChanged());
				buttonBar.add(explicitColorsCheckBox, "cell 5 0");

				//---- backgroundCheckBox ----
				backgroundCheckBox.setText("background");
				backgroundCheckBox.setMnemonic('B');
				backgroundCheckBox.addActionListener(e -> backgroundChanged());
				buttonBar.add(backgroundCheckBox, "cell 6 0");

				//---- opaqueTriStateCheckBox ----
				opaqueTriStateCheckBox.setText("opaque");
				opaqueTriStateCheckBox.setMnemonic('O');
				opaqueTriStateCheckBox.addActionListener(e -> opaqueChanged());
				buttonBar.add(opaqueTriStateCheckBox, "cell 7 0");

				//---- sizeVariantComboBox ----
				sizeVariantComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
					"mini",
					"small",
					"default",
					"large"
				}));
				sizeVariantComboBox.setSelectedIndex(2);
				sizeVariantComboBox.addActionListener(e -> sizeVariantChanged());
				buttonBar.add(sizeVariantComboBox, "cell 8 0");

				//---- closeButton ----
				closeButton.setText("Close");
				buttonBar.add(closeButton, "cell 10 0");
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);
			dialogPane.add(themesPanel, BorderLayout.EAST);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel dialogPane;
	private JRootPane contentPanel;
	private JPanel buttonBar;
	private LookAndFeelsComboBox lookAndFeelComboBox;
	private JComboBox<String> scaleFactorComboBox;
	private JCheckBox rightToLeftCheckBox;
	private JCheckBox enabledCheckBox;
	private JCheckBox inspectCheckBox;
	private JCheckBox explicitColorsCheckBox;
	private JCheckBox backgroundCheckBox;
	private TriStateCheckBox opaqueTriStateCheckBox;
	private JComboBox<String> sizeVariantComboBox;
	private JButton closeButton;
	private IJThemesPanel themesPanel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
