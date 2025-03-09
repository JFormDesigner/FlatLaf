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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.swing.*;
import javax.swing.FocusManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatPropertiesLaf;
import com.formdev.flatlaf.FlatSystemProperties;
import com.formdev.flatlaf.IntelliJTheme;
import com.formdev.flatlaf.demo.LookAndFeelsComboBox;
import com.formdev.flatlaf.demo.DemoPrefs;
import com.formdev.flatlaf.demo.intellijthemes.*;
import com.formdev.flatlaf.extras.*;
import com.formdev.flatlaf.extras.components.FlatTriStateCheckBox;
import com.formdev.flatlaf.extras.components.FlatTriStateCheckBox.State;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
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
	public boolean applyComponentOrientationToFrame;

	public static FlatTestFrame create( String[] args, String title ) {
		// disable text antialiasing
//		System.setProperty( "awt.useSystemAAFontSettings", "off" );

		DemoPrefs.init( PREFS_ROOT_PATH );
		DemoPrefs.initSystemScale();

		// set scale factor
		if( System.getProperty( FlatSystemProperties.UI_SCALE ) == null ) {
			String scaleFactor = DemoPrefs.getState().get( KEY_SCALE_FACTOR, null );
			if( scaleFactor != null )
				System.setProperty( FlatSystemProperties.UI_SCALE, scaleFactor );
		}

		// install inspectors
		FlatInspector.install( "ctrl shift alt X" );
		FlatUIDefaultsInspector.install( "ctrl shift alt Y" );

		// disable animated Laf change
		System.setProperty( "flatlaf.animatedLafChange", "false" );

		// test loading custom defaults from package
		FlatLaf.registerCustomDefaultsSource( "com.formdev.flatlaf.testing.customdefaults" );

		// set look and feel
		DemoPrefs.setupLaf( args );

		// create frame
		return new FlatTestFrame( title );
	}

	private FlatTestFrame( String title ) {
		this.title = title;

		initComponents();

		// initialize look and feels combo box
		DefaultComboBoxModel<LookAndFeelInfo> lafModel = new DefaultComboBoxModel<>();
		lafModel.addElement( new LookAndFeelInfo( "FlatLaf Light (F1)", FlatLightLaf.class.getName() ) );
		lafModel.addElement( new LookAndFeelInfo( "FlatLaf Dark (F2)", FlatDarkLaf.class.getName() ) );
		lafModel.addElement( new LookAndFeelInfo( "FlatLaf IntelliJ (F3)", FlatIntelliJLaf.class.getName() ) );
		lafModel.addElement( new LookAndFeelInfo( "FlatLaf Darcula (F4)", FlatDarculaLaf.class.getName() ) );
		lafModel.addElement( new LookAndFeelInfo( "FlatLaf macOS Light (F5)", FlatMacLightLaf.class.getName() ) );
		lafModel.addElement( new LookAndFeelInfo( "FlatLaf macOS Dark (F6)", FlatMacDarkLaf.class.getName() ) );
		lafModel.addElement( new LookAndFeelInfo( "FlatLaf Test (F8)", FlatTestLaf.class.getName() ) );

		UIManager.LookAndFeelInfo[] lookAndFeels = UIManager.getInstalledLookAndFeels();
		for( UIManager.LookAndFeelInfo lookAndFeel : lookAndFeels ) {
			String name = lookAndFeel.getName();
			String className = lookAndFeel.getClassName();
			if( className.equals( "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel" ) ||
				className.equals( "com.sun.java.swing.plaf.motif.MotifLookAndFeel" ) )
			  continue;

			if( (SystemInfo.isWindows && className.equals( "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" )) ||
				(SystemInfo.isMacOS && className.equals( "com.apple.laf.AquaLookAndFeel" )) ||
				(SystemInfo.isLinux && className.equals( "com.sun.java.swing.plaf.gtk.GTKLookAndFeel" )) )
				name += " (F9)";
			else if( className.equals( MetalLookAndFeel.class.getName() ) )
				name += " (F12)";
			else if( className.equals( NimbusLookAndFeel.class.getName() ) )
				name += " (F11)";

			lafModel.addElement( new LookAndFeelInfo( name, className ) );
		}

		loadLafs( lafModel );

		lookAndFeelComboBox.setModel( lafModel );

		updateScaleFactorComboBox();
		String scaleFactor = System.getProperty( FlatSystemProperties.UI_SCALE );
		if( scaleFactor != null )
			scaleFactorComboBox.setSelectedItem( scaleFactor );

		updateFontSizeSpinner();
		updateSizeVariantComboBox();

		// register F1, F2, ... keys to switch to Light, Dark or other LaFs
		registerSwitchToLookAndFeel( "F1", FlatLightLaf.class.getName() );
		registerSwitchToLookAndFeel( "F2", FlatDarkLaf.class.getName() );
		registerSwitchToLookAndFeel( "F3", FlatIntelliJLaf.class.getName() );
		registerSwitchToLookAndFeel( "F4", FlatDarculaLaf.class.getName() );
		registerSwitchToLookAndFeel( "F5", FlatMacLightLaf.class.getName() );
		registerSwitchToLookAndFeel( "F6", FlatMacDarkLaf.class.getName() );

		registerSwitchToLookAndFeel( "F8", FlatTestLaf.class.getName() );

		if( SystemInfo.isWindows )
			registerSwitchToLookAndFeel( "F9", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" );
		else if( SystemInfo.isMacOS )
			registerSwitchToLookAndFeel( "F9", "com.apple.laf.AquaLookAndFeel" );
		else if( SystemInfo.isLinux )
			registerSwitchToLookAndFeel( "F9", "com.sun.java.swing.plaf.gtk.GTKLookAndFeel" );
		registerSwitchToLookAndFeel( "F11", NimbusLookAndFeel.class.getName() );
		registerSwitchToLookAndFeel( "F12", MetalLookAndFeel.class.getName() );

		// register Alt+Shift+F1, F2, ... keys to change system scale factor
		DemoPrefs.registerSystemScaleFactors( this );

		// register Alt+Shift+S to enable/disable interprocess Laf sync
		DemoPrefs.initLafSync( this );

		// register Ctrl+0, Ctrl++ and Ctrl+- to change font size
		registerKey( SystemInfo.isMacOS ? "meta 0" : "ctrl 0", () -> restoreFont() );
		registerKey( SystemInfo.isMacOS ? "meta PLUS" : "ctrl PLUS", () -> incrFont() );
		registerKey( SystemInfo.isMacOS ? "meta MINUS" : "ctrl MINUS", () -> decrFont() );

		// register Alt+UP and Alt+DOWN to switch to previous/next theme
		registerKey( "alt UP", () -> themesPanel.selectPreviousTheme() );
		registerKey( "alt DOWN", () -> themesPanel.selectNextTheme() );

		// register ESC key to close frame
		registerKey( "ESCAPE", () -> dispose() );

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

					// enable/disable font size spinner
					updateFontSizeSpinner();

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

	@Override
	public void dispose() {
		super.dispose();

		FlatUIDefaultsInspector.hide();
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

	private void registerKey( String keyStrokeStr, Runnable runnable ) {
		KeyStroke keyStroke = KeyStroke.getKeyStroke( keyStrokeStr );
		if( keyStroke == null )
			throw new IllegalArgumentException( "Invalid key stroke '" + keyStrokeStr + "'" );

		((JComponent)getContentPane()).registerKeyboardAction(
			e -> {
				runnable.run();
			},
			keyStroke,
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
	}

	private void registerSwitchToLookAndFeel( String keyStrokeStr, String lafClassName ) {
		registerKey( keyStrokeStr, () -> selectLookAndFeel( lafClassName ) );
	}

	private void loadLafs( DefaultComboBoxModel<LookAndFeelInfo> lafModel ) {
		Properties properties = new Properties();
		try( InputStream in = new FileInputStream( "lafs.properties" ) ) {
			properties.load( in );
		} catch( FileNotFoundException ex ) {
			// ignore
		} catch( IOException ex ) {
			ex.printStackTrace();
		}

		ArrayList<LookAndFeelInfo> lafs = new ArrayList<>();
		for( Map.Entry<Object, Object> entry : properties.entrySet() ) {
			String lafClassName = (String) entry.getKey();
			String[] parts = ((String)entry.getValue()).split( ";" );
			String lafName = parts[0];
			String keyStrokeStr = (parts.length >= 1) ? parts[1] : null;

			if( !isClassAvailable( lafClassName ) )
				continue;

			if( keyStrokeStr != null ) {
				registerSwitchToLookAndFeel( keyStrokeStr, lafClassName );
				lafName += " (" + keyStrokeStr + ")";
			}

			lafs.add( new LookAndFeelInfo( lafName, lafClassName ) );
		}

		lafs.sort( (laf1, laf2) -> laf1.getName().compareToIgnoreCase( laf2.getName() ) );
		for( LookAndFeelInfo laf : lafs )
			lafModel.addElement( laf );
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

		addContentToContentPanel();

		pack();
		setLocationRelativeTo( null );
		setVisible( true );

		EventQueue.invokeLater( () -> {
			closeButton.requestFocusInWindow();
		} );
	}

	private void addContentToContentPanel() {
		if( content instanceof JScrollPane ) {
			contentPanel.add( content );
			return;
		}

		Dimension contentSize = content.getPreferredSize();
		int buttonBarHeight = buttonBar.getPreferredSize().height;
		Rectangle screenBounds = getGraphicsConfiguration().getBounds();

		// add scroll pane if content is larger than screen
		if( contentSize.width > screenBounds.width ||
			contentSize.height + buttonBarHeight > screenBounds.height )
		{
			JScrollPane scrollPane = new JScrollPane( content );
			scrollPane.setBorder( BorderFactory.createEmptyBorder() );
			contentPanel.add( scrollPane );
		} else
			contentPanel.add( content );
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

		applyLookAndFeel( lafClassName, null, null, null, false );
	}

	private void applyLookAndFeel( String lafClassName, IntelliJTheme theme,
		String nameForProperties, Properties properties, boolean pack )
	{
		EventQueue.invokeLater( () -> {
			try {
				// clear custom default font before switching to other LaF
				Font defaultFont = null;
				if( UIManager.getLookAndFeel() instanceof FlatLaf ) {
					Font font = UIManager.getFont( "defaultFont" );
					if( font != UIManager.getLookAndFeelDefaults().getFont( "defaultFont" ) )
						defaultFont = font;
				}
				UIManager.put( "defaultFont", null );

				// change look and feel
				if( theme != null )
					UIManager.setLookAndFeel( IntelliJTheme.createLaf( theme ) );
				else if( properties != null )
					UIManager.setLookAndFeel( new FlatPropertiesLaf( nameForProperties, properties ) );
				else
					UIManager.setLookAndFeel( lafClassName );

				// restore custom default font when switched to other FlatLaf LaF
				if( defaultFont != null && UIManager.getLookAndFeel() instanceof FlatLaf )
					UIManager.put( "defaultFont", defaultFont );

				// update all components
				updateUI2();

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

	private static void updateUI2() {
		KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		Component permanentFocusOwner = keyboardFocusManager.getPermanentFocusOwner();
		JSpinner spinner = (permanentFocusOwner != null)
			? (JSpinner) SwingUtilities.getAncestorOfClass( JSpinner.class, permanentFocusOwner )
			: null;

		FlatLaf.updateUI();

		if( spinner != null && keyboardFocusManager.getPermanentFocusOwner() == null ) {
			JComponent editor = spinner.getEditor();
			JTextField textField = (editor instanceof JSpinner.DefaultEditor)
				? ((JSpinner.DefaultEditor)editor).getTextField()
				: null;
			if( textField != null )
				textField.requestFocusInWindow();
		}
	}

	private void explicitColorsChanged() {
		EventQueue.invokeLater( () -> {
			boolean explicit = explicitColorsCheckBox.isSelected();
			ColorUIResource restoreColor = new ColorUIResource( Color.white );

			boolean dark = FlatLaf.isLafDark();
			Color magenta = dark ? Color.magenta.darker() : Color.magenta;
			Color orange = dark ? Color.orange.darker() : Color.orange;
			Color blue = dark ? Color.blue.darker() : Color.blue;
			Color green = dark ? Color.green.darker() : Color.green;

			updateComponentsRecur( content, (c, type) -> {
				if( type == "view" || type == "tab" || c instanceof JSlider || c instanceof JInternalFrame ) {
					if( c instanceof JInternalFrame )
						c = ((JInternalFrame)c).getContentPane();
					c.setForeground( explicit ? magenta : restoreColor );
					c.setBackground( explicit ? orange : restoreColor );
				} else {
					c.setForeground( explicit ? blue : restoreColor );
					c.setBackground( explicit ? green : restoreColor );
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

		if( applyComponentOrientationToFrame )
			applyComponentOrientation( orientation );
		else if( useApplyComponentOrientation )
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
		if( inspector == null ) {
			inspector = new FlatInspector( getRootPane() );
			inspector.addPropertyChangeListener( e -> {
				inspectCheckBox.setSelected( inspector.isEnabled() );
			} );
		}
		inspector.setEnabled( inspectCheckBox.isSelected() );
	}

	private void uiDefaultsInspectorChanged() {
		getContentPane().removeAll();

		FocusManager focusManager = FocusManager.getCurrentManager();
		Component focusOwner = focusManager.getFocusOwner();

		if( uiDefaultsInspectorCheckBox.isSelected() ) {
			JComponent uiDefaultsInspector = FlatUIDefaultsInspector.createInspectorPanel();

			JSplitPane splitPane = new JSplitPane();
			splitPane.setLeftComponent( dialogPane );
			splitPane.setRightComponent( uiDefaultsInspector );
			getContentPane().add( splitPane, BorderLayout.CENTER );
		} else
			getContentPane().add( dialogPane, BorderLayout.CENTER );

		if( focusOwner != null && focusOwner.isDisplayable() )
			focusOwner.requestFocusInWindow();

		pack();
		setLocationRelativeTo( null );
	}

	private void scaleFactorChanged() {
		String scaleFactor = (String) scaleFactorComboBox.getSelectedItem();
		if( "default".equals( scaleFactor ) )
			scaleFactor = null;

		// hide popup to avoid occasional StackOverflowError when updating UI
		scaleFactorComboBox.setPopupVisible( false );

		if( scaleFactor != null ) {
			System.setProperty( FlatSystemProperties.UI_SCALE, scaleFactor );
			DemoPrefs.getState().put( KEY_SCALE_FACTOR, scaleFactor );
		} else {
			System.clearProperty( FlatSystemProperties.UI_SCALE );
			DemoPrefs.getState().remove( KEY_SCALE_FACTOR );
		}

		// always clear default font because a new font size is computed based on the scale factor
		UIManager.put( "defaultFont", null );

		LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
		IntelliJTheme theme = (lookAndFeel.getClass() == IntelliJTheme.ThemeLaf.class)
			? ((IntelliJTheme.ThemeLaf)lookAndFeel).getTheme()
			: null;
		String nameForProperties = null;
		Properties properties = null;
		if( lookAndFeel instanceof FlatPropertiesLaf ) {
			nameForProperties = lookAndFeel.getName();
			properties = ((FlatPropertiesLaf)lookAndFeel).getProperties();
		}
		applyLookAndFeel( lookAndFeel.getClass().getName(), theme, nameForProperties, properties, true );
	}

	private void updateScaleFactorComboBox() {
		scaleFactorComboBox.setEnabled( UIManager.getLookAndFeel() instanceof FlatLaf );
	}

	private void restoreFont() {
		fontSizeSpinner.setValue( 0 );
	}

	private void incrFont() {
		fontSizeSpinner.setValue( fontSizeSpinner.getNextValue() );
	}

	private void decrFont() {
		fontSizeSpinner.setValue( fontSizeSpinner.getPreviousValue() );
	}

	private void fontSizeChanged() {
		if( !(UIManager.getLookAndFeel() instanceof FlatLaf) )
			return;

		Object value = fontSizeSpinner.getValue();
		int newFontSize = (value instanceof Integer) ? (Integer) value : 0;

		Font font = UIManager.getFont( "defaultFont" );
		if( font == null || font.getSize() == newFontSize )
			return;

		Font newFont = (newFontSize >= 8) ? font.deriveFont( (float) newFontSize ) : null;
		UIManager.put( "defaultFont", newFont );

		if( newFont == null )
			updateFontSizeSpinner();

		updateUI2();
	}

	private void updateFontSizeSpinner() {
		fontSizeSpinner.setEnabled( UIManager.getLookAndFeel() instanceof FlatLaf );
		fontSizeSpinner.setValue( UIManager.getFont( "Label.font" ).getSize() );
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

	public static void updateComponentsRecur( Container container, BiConsumer<Component, String> action ) {
		for( Component c : container.getComponents() ) {
			if( c instanceof JComponent && Boolean.TRUE.equals( ((JComponent)c).getClientProperty( "FlatLaf.internal.testing.ignore" ) ) )
				continue;

			if( c instanceof JPanel ) {
				updateComponentsRecur( (Container) c, action );
				continue;
			}

			action.accept( c, null );

			if( c instanceof JScrollPane ) {
				Component view = ((JScrollPane)c).getViewport().getView();
				if( view != null )
					action.accept( view, "view" );

				JViewport columnHeader = ((JScrollPane)c).getColumnHeader();
				if( columnHeader != null )
					action.accept( columnHeader.getView(), "columnHeader" );
			} else if( c instanceof JSplitPane ) {
				JSplitPane splitPane = (JSplitPane) c;
				Component left = splitPane.getLeftComponent();
				Component right = splitPane.getRightComponent();
				if( left instanceof Container )
					updateComponentsRecur( (Container) left, action );
				if( right instanceof Container )
					updateComponentsRecur( (Container) right, action );
			} else if( c instanceof JTabbedPane ) {
				JTabbedPane tabPane = (JTabbedPane)c;
				int tabCount = tabPane.getTabCount();
				for( int i = 0; i < tabCount; i++ ) {
					Component tab = tabPane.getComponentAt( i );
					if( tab != null )
						action.accept( tab, "tab" );
				}
			} else if( c instanceof JDesktopPane ) {
				for( JInternalFrame f : ((JDesktopPane)c).getAllFrames() )
					action.accept( f, null );
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
		contentPanel.removeAll();
		content = contentFactory.get();
		addContentToContentPanel();

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
		contentPanel = new JPanel();
		buttonBar = new JPanel();
		lookAndFeelComboBox = new LookAndFeelsComboBox();
		scaleFactorComboBox = new JComboBox<>();
		fontSizeSpinner = new JSpinner();
		rightToLeftCheckBox = new JCheckBox();
		enabledCheckBox = new JCheckBox();
		inspectCheckBox = new JCheckBox();
		uiDefaultsInspectorCheckBox = new JCheckBox();
		explicitColorsCheckBox = new JCheckBox();
		backgroundCheckBox = new JCheckBox();
		opaqueTriStateCheckBox = new FlatTriStateCheckBox();
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
				contentPanel.setLayout(new MigLayout(
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
					"2",
					"2.25",
					"2.5",
					"3",
					"3.5",
					"4",
					"5",
					"6"
				}));
				scaleFactorComboBox.setMaximumRowCount(20);
				scaleFactorComboBox.addActionListener(e -> scaleFactorChanged());
				buttonBar.add(scaleFactorComboBox, "cell 1 0");

				//---- fontSizeSpinner ----
				fontSizeSpinner.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 50);
				fontSizeSpinner.setModel(new SpinnerNumberModel(0, 0, null, 1));
				fontSizeSpinner.addChangeListener(e -> fontSizeChanged());
				buttonBar.add(fontSizeSpinner, "cell 2 0");

				//---- rightToLeftCheckBox ----
				rightToLeftCheckBox.setText("right-to-left");
				rightToLeftCheckBox.setMnemonic('R');
				rightToLeftCheckBox.addActionListener(e -> rightToLeftChanged());
				buttonBar.add(rightToLeftCheckBox, "cell 3 0");

				//---- enabledCheckBox ----
				enabledCheckBox.setText("enabled");
				enabledCheckBox.setMnemonic('E');
				enabledCheckBox.setSelected(true);
				enabledCheckBox.addActionListener(e -> enabledChanged());
				buttonBar.add(enabledCheckBox, "cell 4 0");

				//---- inspectCheckBox ----
				inspectCheckBox.setText("inspect");
				inspectCheckBox.setMnemonic('I');
				inspectCheckBox.addActionListener(e -> inspectChanged());
				buttonBar.add(inspectCheckBox, "cell 5 0");

				//---- uiDefaultsInspectorCheckBox ----
				uiDefaultsInspectorCheckBox.setText("UI defaults");
				uiDefaultsInspectorCheckBox.setMnemonic('U');
				uiDefaultsInspectorCheckBox.addActionListener(e -> uiDefaultsInspectorChanged());
				buttonBar.add(uiDefaultsInspectorCheckBox, "cell 6 0");

				//---- explicitColorsCheckBox ----
				explicitColorsCheckBox.setText("explicit colors");
				explicitColorsCheckBox.setMnemonic('X');
				explicitColorsCheckBox.addActionListener(e -> explicitColorsChanged());
				buttonBar.add(explicitColorsCheckBox, "cell 7 0");

				//---- backgroundCheckBox ----
				backgroundCheckBox.setText("background");
				backgroundCheckBox.setMnemonic('B');
				backgroundCheckBox.addActionListener(e -> backgroundChanged());
				buttonBar.add(backgroundCheckBox, "cell 8 0");

				//---- opaqueTriStateCheckBox ----
				opaqueTriStateCheckBox.setText("opaque");
				opaqueTriStateCheckBox.setMnemonic('O');
				opaqueTriStateCheckBox.addActionListener(e -> opaqueChanged());
				buttonBar.add(opaqueTriStateCheckBox, "cell 9 0");

				//---- sizeVariantComboBox ----
				sizeVariantComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
					"mini",
					"small",
					"default",
					"large"
				}));
				sizeVariantComboBox.setSelectedIndex(2);
				sizeVariantComboBox.addActionListener(e -> sizeVariantChanged());
				buttonBar.add(sizeVariantComboBox, "cell 10 0");

				//---- closeButton ----
				closeButton.setText("Close");
				buttonBar.add(closeButton, "cell 12 0");
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);
			dialogPane.add(themesPanel, BorderLayout.EAST);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JPanel buttonBar;
	private LookAndFeelsComboBox lookAndFeelComboBox;
	private JComboBox<String> scaleFactorComboBox;
	private JSpinner fontSizeSpinner;
	private JCheckBox rightToLeftCheckBox;
	private JCheckBox enabledCheckBox;
	private JCheckBox inspectCheckBox;
	private JCheckBox uiDefaultsInspectorCheckBox;
	private JCheckBox explicitColorsCheckBox;
	private JCheckBox backgroundCheckBox;
	private FlatTriStateCheckBox opaqueTriStateCheckBox;
	private JComboBox<String> sizeVariantComboBox;
	private JButton closeButton;
	private IJThemesPanel themesPanel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class NoRightToLeftPanel -------------------------------------------

	public static class NoRightToLeftPanel
		extends JPanel
	{
		public NoRightToLeftPanel() {
		}

		@Override
		public void applyComponentOrientation( ComponentOrientation o ) {
			// ignore
		}
	}
}
