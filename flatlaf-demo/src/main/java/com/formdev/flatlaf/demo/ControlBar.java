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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import com.formdev.flatlaf.*;
import com.formdev.flatlaf.util.SystemInfo;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
class ControlBar
	extends JPanel
{
	private JFrame frame;
	private JTabbedPane tabbedPane;

	ControlBar() {
		initComponents();

		// initialize look and feels combo box
		DefaultComboBoxModel<LookAndFeelInfo> lafModel = new DefaultComboBoxModel<>();
		lafModel.addElement( new LookAndFeelInfo( "Flat Light (F1)", FlatLightLaf.class.getName() ) );
		lafModel.addElement( new LookAndFeelInfo( "Flat Dark (F2)", FlatDarkLaf.class.getName() ) );
		lafModel.addElement( new LookAndFeelInfo( "Flat IntelliJ (F3)", FlatIntelliJLaf.class.getName() ) );
		lafModel.addElement( new LookAndFeelInfo( "Flat Darcula (F4)", FlatDarculaLaf.class.getName() ) );

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
				name += " (F10)";
			else if( className.equals( NimbusLookAndFeel.class.getName() ) )
				name += " (F11)";

			lafModel.addElement( new LookAndFeelInfo( name, className ) );
		}

		lookAndFeelComboBox.setModel( lafModel );

		UIManager.addPropertyChangeListener( e -> {
			if( "lookAndFeel".equals( e.getPropertyName() ) ) {
				EventQueue.invokeLater( () -> {
					// update info label because user scale factor may change
					updateInfoLabel();
				} );
			}
		} );
	}

	void initialize( JFrame frame, JTabbedPane tabbedPane ) {
		this.frame = frame;
		this.tabbedPane = tabbedPane;

		// register F1, F2, ... keys to switch to Light, Dark or other LaFs
		registerSwitchToLookAndFeel( KeyEvent.VK_F1, FlatLightLaf.class.getName() );
		registerSwitchToLookAndFeel( KeyEvent.VK_F2, FlatDarkLaf.class.getName() );
		registerSwitchToLookAndFeel( KeyEvent.VK_F3, FlatIntelliJLaf.class.getName() );
		registerSwitchToLookAndFeel( KeyEvent.VK_F4, FlatDarculaLaf.class.getName() );

		if( SystemInfo.IS_WINDOWS )
			registerSwitchToLookAndFeel( KeyEvent.VK_F9, "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" );
		else if( SystemInfo.IS_MAC )
			registerSwitchToLookAndFeel( KeyEvent.VK_F9, "com.apple.laf.AquaLookAndFeel" );
		else if( SystemInfo.IS_LINUX )
			registerSwitchToLookAndFeel( KeyEvent.VK_F9, "com.sun.java.swing.plaf.gtk.GTKLookAndFeel" );
		registerSwitchToLookAndFeel( KeyEvent.VK_F10, MetalLookAndFeel.class.getName() );
		registerSwitchToLookAndFeel( KeyEvent.VK_F11, NimbusLookAndFeel.class.getName() );

		// register ESC key to close frame
		((JComponent)frame.getContentPane()).registerKeyboardAction(
			e -> {
				frame.dispose();
			},
			KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false ),
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );

		// make the "close" button the default button
		frame.getRootPane().setDefaultButton( closeButton );

		// update info label and move focus to "close" button
		frame.addWindowListener( new WindowAdapter() {
			@Override
			public void windowOpened( WindowEvent e ) {
				updateInfoLabel();
				closeButton.requestFocusInWindow();
			}
		} );

		// update info label when moved to another screen
		frame.addComponentListener( new ComponentAdapter() {
			@Override
			public void componentMoved( ComponentEvent e ) {
				updateInfoLabel();
			}
		} );
	}

	private void updateInfoLabel() {
		double systemScaleFactor = UIScale.getSystemScaleFactor( getGraphicsConfiguration() );
		float userScaleFactor = UIScale.getUserScaleFactor();
		String newInfo = "(Java " + System.getProperty( "java.version" )
			+ (systemScaleFactor != 1 ? (";  system scale factor " + systemScaleFactor) : "")
			+ (userScaleFactor != 1 ? (";  user scale factor " + userScaleFactor) : "")
			+ (systemScaleFactor == 1 && userScaleFactor == 1 ? "; no scaling" : "")
			+ ")";

		if( !newInfo.equals( infoLabel.getText() ) )
			infoLabel.setText( newInfo );
	}

	private void registerSwitchToLookAndFeel( int keyCode, String lafClassName ) {
		((JComponent)frame.getContentPane()).registerKeyboardAction(
			e -> {
				selectLookAndFeel( lafClassName );
			},
			KeyStroke.getKeyStroke( keyCode, 0, false ),
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
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

		EventQueue.invokeLater( () -> {
			try {
				// change look and feel
				UIManager.setLookAndFeel( lafClassName );

				// update all components
				FlatLaf.updateUI();

				// increase size of frame if necessary
				int width = frame.getWidth();
				int height = frame.getHeight();
				Dimension prefSize = frame.getPreferredSize();
				if( prefSize.width > width || prefSize.height > height )
					frame.setSize( Math.max( prefSize.width, width ), Math.max( prefSize.height, height ) );

			} catch( Exception ex ) {
				ex.printStackTrace();
			}
		} );
	}

	private void rightToLeftChanged() {
		boolean rightToLeft = rightToLeftCheckBox.isSelected();
		rightToLeftChanged( frame, rightToLeft );
	}

	private void rightToLeftChanged( Container c, boolean rightToLeft ) {
		c.applyComponentOrientation( rightToLeft
			? ComponentOrientation.RIGHT_TO_LEFT
			: ComponentOrientation.LEFT_TO_RIGHT );
		c.revalidate();
		c.repaint();
	}

	private void enabledChanged() {
		enabledDisable( tabbedPane, enabledCheckBox.isSelected() );
	}

	private void enabledDisable( Container container, boolean enabled ) {
		for( Component c : container.getComponents() ) {
			if( c instanceof JPanel ) {
				enabledDisable( (JPanel) c, enabled );
				continue;
			}

			c.setEnabled( enabled );

			if( c instanceof JScrollPane ) {
				Component view = ((JScrollPane)c).getViewport().getView();
				if( view != null )
					view.setEnabled( enabled );
			} else if( c instanceof JTabbedPane ) {
				JTabbedPane tabPane = (JTabbedPane)c;
				int tabCount = tabPane.getTabCount();
				for( int i = 0; i < tabCount; i++ ) {
					Component tab = tabPane.getComponentAt( i );
					if( tab != null )
						tab.setEnabled( enabled );
				}
			}

			if( c instanceof JToolBar )
				enabledDisable( (JToolBar) c, enabled );
		}
	}

	private void closePerformed() {
		frame.dispose();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		separator1 = new JSeparator();
		lookAndFeelComboBox = new LookAndFeelsComboBox();
		rightToLeftCheckBox = new JCheckBox();
		enabledCheckBox = new JCheckBox();
		infoLabel = new JLabel();
		closeButton = new JButton();

		//======== this ========
		setLayout(new MigLayout(
			"insets dialog",
			// columns
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[grow,fill]" +
			"[button,fill]",
			// rows
			"[bottom]" +
			"[]"));
		add(separator1, "cell 0 0 5 1");

		//---- lookAndFeelComboBox ----
		lookAndFeelComboBox.addActionListener(e -> lookAndFeelChanged());
		add(lookAndFeelComboBox, "cell 0 1");

		//---- rightToLeftCheckBox ----
		rightToLeftCheckBox.setText("right-to-left");
		rightToLeftCheckBox.setMnemonic('R');
		rightToLeftCheckBox.addActionListener(e -> rightToLeftChanged());
		add(rightToLeftCheckBox, "cell 1 1");

		//---- enabledCheckBox ----
		enabledCheckBox.setText("enabled");
		enabledCheckBox.setMnemonic('E');
		enabledCheckBox.setSelected(true);
		enabledCheckBox.addActionListener(e -> enabledChanged());
		add(enabledCheckBox, "cell 2 1");

		//---- infoLabel ----
		infoLabel.setText("text");
		add(infoLabel, "cell 3 1,alignx center,growx 0");

		//---- closeButton ----
		closeButton.setText("Close");
		closeButton.addActionListener(e -> closePerformed());
		add(closeButton, "cell 4 1");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JSeparator separator1;
	private LookAndFeelsComboBox lookAndFeelComboBox;
	private JCheckBox rightToLeftCheckBox;
	private JCheckBox enabledCheckBox;
	private JLabel infoLabel;
	private JButton closeButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
