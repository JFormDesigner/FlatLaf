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

package com.formdev.flatlaf;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import com.formdev.flatlaf.util.SystemInfo;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatTestFrame
	extends JFrame
{
	private static final String PREFS_ROOT_PATH = "/flatlaf-test";
	private static final String KEY_LAF = "laf";

	private JComponent content;

	public static FlatTestFrame create( String[] args, String title ) {
		// set look and feel
		try {
			if( args.length > 0 )
				UIManager.setLookAndFeel( args[0] );
			else {
				String lafClassName = Preferences.userRoot().node( PREFS_ROOT_PATH )
					.get( KEY_LAF, FlatLightLaf.class.getName() );
				UIManager.setLookAndFeel( lafClassName );
			}
		} catch( Exception ex ) {
			ex.printStackTrace();

			// fallback
			try {
				UIManager.setLookAndFeel( new FlatLightLaf() );
			} catch( Exception ex2 ) {
				ex2.printStackTrace();
			}
		}

		// create frame
		FlatTestFrame frame = new FlatTestFrame();
		frame.setTitle( title + " (Java " + System.getProperty( "java.version" ) + ")" );
		return frame;
	}

	private FlatTestFrame() {
		initComponents();

		// initialize look and feels combo box
		DefaultComboBoxModel<LafInfo> lafModel = new DefaultComboBoxModel<>();
		lafModel.addElement( new LafInfo( "Flat Light", FlatLightLaf.class.getName() ) );
		lafModel.addElement( new LafInfo( "Flat Dark", FlatDarkLaf.class.getName() ) );
		lafModel.addElement( new LafInfo( "Flat Test", FlatTestLaf.class.getName() ) );
		lafModel.addElement( new LafInfo( "Flat IntelliJ", FlatIntelliJLaf.class.getName() ) );
		lafModel.addElement( new LafInfo( "Flat Darcula", FlatDarculaLaf.class.getName() ) );

		UIManager.LookAndFeelInfo[] lookAndFeels = UIManager.getInstalledLookAndFeels();
		for( UIManager.LookAndFeelInfo lookAndFeel : lookAndFeels ) {
			String name = lookAndFeel.getName();
			String className = lookAndFeel.getClassName();
			if( className.equals( "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel" ) ||
				className.equals( "com.sun.java.swing.plaf.motif.MotifLookAndFeel" ) )
			  continue;

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

		// register F1, F2 and F3 keys to switch to Light, Dark or Test LaF
		registerSwitchToLookAndFeel( KeyEvent.VK_F1, FlatLightLaf.class.getName() );
		registerSwitchToLookAndFeel( KeyEvent.VK_F2, FlatDarkLaf.class.getName() );
		registerSwitchToLookAndFeel( KeyEvent.VK_F3, FlatTestLaf.class.getName() );
		registerSwitchToLookAndFeel( KeyEvent.VK_F4, FlatIntelliJLaf.class.getName() );
		registerSwitchToLookAndFeel( KeyEvent.VK_F5, FlatDarculaLaf.class.getName() );

		registerSwitchToLookAndFeel( KeyEvent.VK_F7, MetalLookAndFeel.class.getName() );
		if( SystemInfo.IS_WINDOWS )
			registerSwitchToLookAndFeel( KeyEvent.VK_F8, "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" );

		// register ESC key to close frame
		((JComponent)getContentPane()).registerKeyboardAction(
			e -> {
				dispose();
			},
			KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false ),
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );

		// close frame
		closeButton.addActionListener(e -> dispose());
	}

	private void registerSwitchToLookAndFeel( int keyCode, String lafClassName ) {
		((JComponent)getContentPane()).registerKeyboardAction(
			e -> {
				selectLookAndFeel( lafClassName );
			},
			KeyStroke.getKeyStroke( keyCode, 0, false ),
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
	}

	protected void showFrame( JComponent content ) {
		this.content = content;

		contentPanel.add( content );
		pack();
		setLocationRelativeTo( null );
		setVisible( true );
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

		Preferences.userRoot().node( PREFS_ROOT_PATH ).put( KEY_LAF, newLaf.className );

		EventQueue.invokeLater( () -> {
			try {
				// change look and feel
				UIManager.setLookAndFeel( newLaf.className );

				// update all components
				SwingUtilities.updateComponentTreeUI( this );

				// increase size of frame if necessary
				int width = getWidth();
				int height = getHeight();
				Dimension prefSize = getPreferredSize();
				if( prefSize.width > width || prefSize.height > height )
					setSize( Math.max( prefSize.width, width ), Math.max( prefSize.height, height ) );

			} catch( Exception ex ) {
				ex.printStackTrace();
			}
		} );
	}

	private void explicitColorsChanged() {
		EventQueue.invokeLater( () -> {
			boolean explicit = explicitColorsCheckBox.isSelected();
			ColorUIResource restoreColor = new ColorUIResource( Color.white );

			explicitColors( content, explicit, restoreColor );

			// because colors may depend on state (e.g. disabled JTextField)
			// it is best to update all UI delegates to get correct result
			if( !explicit )
				SwingUtilities.updateComponentTreeUI( content );
		} );
	}

	private void explicitColors( Container container, boolean explicit, ColorUIResource restoreColor ) {
		for( Component c : container.getComponents() ) {
			if( c instanceof JPanel ) {
				explicitColors( (JPanel) c, explicit, restoreColor );
				continue;
			}

			c.setForeground( explicit ? Color.blue : restoreColor );
			c.setBackground( explicit ? Color.red : restoreColor );

			if( c instanceof JScrollPane ) {
				Component view = ((JScrollPane)c).getViewport().getView();
				if( view != null ) {
					view.setForeground( explicit ? Color.magenta : restoreColor );
					view.setBackground( explicit ? Color.orange : restoreColor );
				}
			} else if( c instanceof JTabbedPane ) {
				JTabbedPane tabPane = (JTabbedPane)c;
				int tabCount = tabPane.getTabCount();
				for( int i = 0; i < tabCount; i++ ) {
					Component tab = tabPane.getComponentAt( i );
					if( tab != null ) {
						tab.setForeground( explicit ? Color.magenta : restoreColor );
						tab.setBackground( explicit ? Color.orange : restoreColor );
					}
				}
			}
		}

	}

	private void rightToLeftChanged() {
		contentPanel.applyComponentOrientation( rightToLeftCheckBox.isSelected()
			? ComponentOrientation.RIGHT_TO_LEFT
			: ComponentOrientation.LEFT_TO_RIGHT );
		contentPanel.revalidate();
		contentPanel.repaint();
	}

	private void enabledChanged() {
		enabledDisable( content, enabledCheckBox.isSelected() );
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
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		buttonBar = new JPanel();
		lookAndFeelComboBox = new JComboBox<>();
		explicitColorsCheckBox = new JCheckBox();
		rightToLeftCheckBox = new JCheckBox();
		enabledCheckBox = new JCheckBox();
		closeButton = new JButton();

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
					"insets dialog,hidemode 3",
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
					"[grow,fill]" +
					"[button,fill]",
					// rows
					null));

				//---- lookAndFeelComboBox ----
				lookAndFeelComboBox.addActionListener(e -> lookAndFeelChanged());
				buttonBar.add(lookAndFeelComboBox, "cell 0 0");

				//---- explicitColorsCheckBox ----
				explicitColorsCheckBox.setText("explicit colors");
				explicitColorsCheckBox.setMnemonic('X');
				explicitColorsCheckBox.addActionListener(e -> explicitColorsChanged());
				buttonBar.add(explicitColorsCheckBox, "cell 1 0");

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

				//---- closeButton ----
				closeButton.setText("Close");
				buttonBar.add(closeButton, "cell 5 0");
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JPanel buttonBar;
	private JComboBox<LafInfo> lookAndFeelComboBox;
	private JCheckBox explicitColorsCheckBox;
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
