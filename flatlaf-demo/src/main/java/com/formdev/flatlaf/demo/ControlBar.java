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
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import com.formdev.flatlaf.*;
import com.formdev.flatlaf.util.SystemInfo;
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
		DefaultComboBoxModel<LafInfo> lafModel = new DefaultComboBoxModel<>();
		lafModel.addElement( new LafInfo( "Flat Light (F1)", FlatLightLaf.class.getName() ) );
		lafModel.addElement( new LafInfo( "Flat Dark (F2)", FlatDarkLaf.class.getName() ) );

		UIManager.LookAndFeelInfo[] lookAndFeels = UIManager.getInstalledLookAndFeels();
		for( UIManager.LookAndFeelInfo lookAndFeel : lookAndFeels ) {
			String name = lookAndFeel.getName();
			String className = lookAndFeel.getClassName();
			if( className.equals( "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel" ) ||
				className.equals( "com.sun.java.swing.plaf.motif.MotifLookAndFeel" ) )
			  continue;

			if( className.equals( NimbusLookAndFeel.class.getName() ) )
				name += " (F10)";
			else if( className.equals( MetalLookAndFeel.class.getName() ) )
				name += " (F11)";
			else if( className.equals( "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" ) )
				name += " (F12)";

			lafModel.addElement( new LafInfo( name, className ) );
		}

		LookAndFeel activeLaf = UIManager.getLookAndFeel();
		String activeLafClassName = activeLaf.getClass().getName();
		int sel = lafModel.getIndexOf( new LafInfo( null, activeLafClassName ) );
		if( sel < 0 ) {
			lafModel.addElement( new LafInfo( activeLaf.getName(), activeLafClassName ) );
			sel = lafModel.getSize() - 1;
		}
		lafModel.setSelectedItem( lafModel.getElementAt( sel ) );

		lookAndFeelComboBox.setModel( lafModel );
	}

	void initialize( JFrame frame, JTabbedPane tabbedPane ) {
		this.frame = frame;
		this.tabbedPane = tabbedPane;

		// register F1, F2, ... keys to switch to Light, Dark or other LaFs
		registerSwitchToLookAndFeel( KeyEvent.VK_F1, FlatLightLaf.class.getName() );
		registerSwitchToLookAndFeel( KeyEvent.VK_F2, FlatDarkLaf.class.getName() );

		registerSwitchToLookAndFeel( KeyEvent.VK_F10, NimbusLookAndFeel.class.getName() );
		registerSwitchToLookAndFeel( KeyEvent.VK_F11, MetalLookAndFeel.class.getName() );
		if( SystemInfo.IS_WINDOWS )
			registerSwitchToLookAndFeel( KeyEvent.VK_F12, "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" );

		// register ESC key to close frame
		((JComponent)frame.getContentPane()).registerKeyboardAction(
			e -> {
				frame.dispose();
			},
			KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false ),
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );

		// make the "close" button the default button
		frame.getRootPane().setDefaultButton( closeButton );

		// move focus to "close" button
		frame.addWindowListener( new WindowAdapter() {
			@Override
			public void windowOpened( WindowEvent e ) {
				closeButton.requestFocusInWindow();
			}
		} );
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
		DefaultComboBoxModel<LafInfo> lafModel = (DefaultComboBoxModel<LafInfo>) lookAndFeelComboBox.getModel();
		int sel = lafModel.getIndexOf( new LafInfo( null, lafClassName ) );
		if( sel >= 0 )
			lookAndFeelComboBox.setSelectedIndex( sel );
	}

	private void lookAndFeelChanged() {
		LafInfo newLaf = (LafInfo) lookAndFeelComboBox.getSelectedItem();
		if( newLaf == null )
			return;

		if( newLaf.className.equals( UIManager.getLookAndFeel().getClass().getName() ) )
			return;

		FlatLafDemo.prefs.put( FlatLafDemo.KEY_LAF, newLaf.className );

		EventQueue.invokeLater( () -> {
			try {
				// change look and feel
				UIManager.setLookAndFeel( newLaf.className );

				// update all components
				SwingUtilities.updateComponentTreeUI( frame );

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
		lookAndFeelComboBox = new JComboBox<>();
		rightToLeftCheckBox = new JCheckBox();
		enabledCheckBox = new JCheckBox();
		closeButton = new JButton();

		//======== this ========
		setLayout(new MigLayout(
			"insets 0",
			// columns
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[grow,fill]" +
			"[button,fill]",
			// rows
			"[20,bottom]" +
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

		//---- closeButton ----
		closeButton.setText("Close");
		closeButton.addActionListener(e -> closePerformed());
		add(closeButton, "cell 4 1");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JSeparator separator1;
	private JComboBox<LafInfo> lookAndFeelComboBox;
	private JCheckBox rightToLeftCheckBox;
	private JCheckBox enabledCheckBox;
	private JButton closeButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class LafInfo ------------------------------------------------------

	static class LafInfo
	{
		final String name;
		final String className;

		LafInfo( String name, String className ) {
			this.name = name;
			this.className = className;
		}

		@Override
		public boolean equals( Object obj ) {
			return obj instanceof LafInfo && className.equals( ((LafInfo)obj).className );
		}

		@Override
		public String toString() {
			return name;
		}
	}
}
