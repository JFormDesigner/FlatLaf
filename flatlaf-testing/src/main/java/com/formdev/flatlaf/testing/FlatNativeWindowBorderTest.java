/*
 * Copyright 2021 FormDev Software GmbH
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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.WeakHashMap;
import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.extras.components.FlatTriStateCheckBox;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.formdev.flatlaf.ui.FlatLineBorder;
import com.formdev.flatlaf.util.SystemInfo;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatNativeWindowBorderTest
	extends JPanel
{
	private static JFrame mainFrame;
	private static WeakHashMap<Window, Object> hiddenWindowsMap = new WeakHashMap<>();
	private static int nextWindowId = 1;

	private final Window window;
	private final int windowId;

	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatLightLaf.setup();
			FlatInspector.install( "ctrl shift alt X" );
			UIManager.put( "FlatLaf.debug.titlebar.showRectangles", true );

			mainFrame = showFrame();
		} );
	}

	private static JFrame showFrame() {
		JFrame frame = new MyJFrame( "FlatNativeWindowBorderTest" );
		frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		frame.add( new FlatNativeWindowBorderTest( frame ) );

		((JComponent) frame.getContentPane()).registerKeyboardAction( e -> {
			frame.dispose();
		}, KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false ), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );

		frame.setSize( new Dimension( 800, 600 ) );
		frame.setLocationRelativeTo( null );
		int offset = 20 * Window.getWindows().length;
		frame.setLocation( frame.getX() + offset, frame.getY() + offset );
		frame.setVisible( true );
		return frame;
	}

	private static void showDialog( Window owner ) {
		JDialog dialog = new MyJDialog( owner, "FlatNativeWindowBorderTest Dialog", ModalityType.DOCUMENT_MODAL );
		dialog.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		dialog.add( new FlatNativeWindowBorderTest( dialog ) );

		((JComponent) dialog.getContentPane()).registerKeyboardAction( e -> {
			dialog.dispose();
		}, KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false ), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );

		dialog.setSize( new Dimension( 800, 600 ) );
		dialog.setLocationRelativeTo( owner );
		dialog.setLocation( dialog.getX() + 20, dialog.getY() + 20 );
		dialog.setVisible( true );
	}

	private FlatNativeWindowBorderTest( Window window ) {
		this.window = window;
		this.windowId = nextWindowId++;

		initComponents();

		if( mainFrame == null )
			hideWindowButton.setEnabled( false );

		setBorder( new FlatLineBorder( new Insets( 0, 0, 0, 0 ), Color.red ) );

		updateInfo();

		ComponentListener componentListener = new ComponentAdapter() {
			@Override
			public void componentMoved( ComponentEvent e ) {
				updateInfo();
			}

			@Override
			public void componentResized( ComponentEvent e ) {
				updateInfo();
			}
		};
		window.addComponentListener( componentListener );
		addComponentListener( componentListener );

		window.addWindowListener( new WindowListener() {
			@Override
			public void windowOpened( WindowEvent e ) {
				System.out.println( windowId + " windowOpened" );
			}
			@Override
			public void windowClosing( WindowEvent e ) {
				System.out.println( windowId + " windowClosing" );
			}
			@Override
			public void windowClosed( WindowEvent e ) {
				System.out.println( windowId + " windowClosed" );
			}
			@Override
			public void windowIconified( WindowEvent e ) {
				System.out.println( windowId + " windowIconified" );
			}
			@Override
			public void windowDeiconified( WindowEvent e ) {
				System.out.println( windowId + " windowDeiconified" );
			}
			@Override
			public void windowActivated( WindowEvent e ) {
				System.out.println( windowId + " windowActivated" );
			}
			@Override
			public void windowDeactivated( WindowEvent e ) {
				System.out.println( windowId + " windowDeactivated" );
			}
		} );

		window.addWindowStateListener( e -> {
			System.out.println( windowId + " windowStateChanged  " + e.getOldState() + " --> " + e.getNewState() );
		} );

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
	}

	private void updateInfo() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		GraphicsConfiguration gc = window.getGraphicsConfiguration();
		DisplayMode dm = gc.getDevice().getDisplayMode();
		Rectangle screenBounds = gc.getBounds();
		Rectangle windowBounds = window.getBounds();
		Rectangle clientBounds = new Rectangle( isShowing() ? getLocationOnScreen() : getLocation(), getSize() );

		StringBuilder buf = new StringBuilder( 1500 );
		buf.append( "<html><style>" );
		buf.append( "td { padding: 0 10 0 0; }" );
		buf.append( "</style><table>" );

		appendRow( buf, "Window bounds", toString( windowBounds ) );
		appendRow( buf, "Client bounds", toString( clientBounds ) );
		appendRow( buf, "Window / Panel gap", toString( diff( windowBounds, clientBounds ) ) );
		if( window instanceof Frame && (((Frame)window).getExtendedState() & Frame.MAXIMIZED_BOTH) != 0 )
			appendRow( buf, "Screen / Window gap", toString( diff( screenBounds, windowBounds ) ) );

		appendEmptyRow( buf );

		if( window instanceof Frame ) {
			Rectangle maximizedBounds = ((Frame)window).getMaximizedBounds();
			if( maximizedBounds != null ) {
				appendRow( buf, "Maximized bounds", toString( maximizedBounds ) );
				appendEmptyRow( buf );
			}
		}

		appendRow( buf, "Physical screen size", dm.getWidth() + ", " + dm.getHeight() + "  (" + dm.getBitDepth() + " Bit)" );
		appendRow( buf, "Screen bounds", toString( screenBounds ) );
		appendRow( buf, "Screen insets", toString( toolkit.getScreenInsets( gc ) ) );
		appendRow( buf, "Scale factor", (int) (gc.getDefaultTransform().getScaleX() * 100) + "%" );

		appendEmptyRow( buf );

		appendRow( buf, "Java version", System.getProperty( "java.version" ) + " / " + System.getProperty( "java.vendor" ) );

		buf.append( "</td></tr>" );
		buf.append( "</table></html>" );

		info.setText( buf.toString() );
	}

	private static void appendRow( StringBuilder buf, String key, String value ) {
		buf.append( "<tr><td>" )
			.append( key )
			.append( ":</td><td>" )
			.append( value )
			.append( "</td></tr>" );
	}

	private static void appendEmptyRow( StringBuilder buf ) {
		buf.append( "<tr><td></td><td></td></tr>" );
	}

	private static String toString( Rectangle r ) {
		if( r == null )
			return "null";
		return r.x + ", " + r.y + ", " + r.width + ", " + r.height;
	}

	private static String toString( Insets insets ) {
		return insets.top + ", " + insets.left + ", " + insets.bottom + ", " + insets.right;
	}

	private static Rectangle diff( Rectangle r1, Rectangle r2 ) {
		return new Rectangle(
			r2.x - r1.x,
			r2.y - r1.y,
			(r1.x + r1.width) - (r2.x + r2.width),
			(r1.y + r1.height) - (r2.y + r2.height) );
	}

	private void registerSwitchToLookAndFeel( String keyStrokeStr, String lafClassName ) {
		KeyStroke keyStroke = KeyStroke.getKeyStroke( keyStrokeStr );
		if( keyStroke == null )
			throw new IllegalArgumentException( "Invalid key stroke '" + keyStrokeStr + "'" );

		((JComponent)((RootPaneContainer)window).getContentPane()).registerKeyboardAction(
			e -> applyLookAndFeel( lafClassName ),
			keyStroke,
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
	}

	private void applyLookAndFeel( String lafClassName ) {
		EventQueue.invokeLater( () -> {
			try {
				UIManager.setLookAndFeel( lafClassName );
				FlatLaf.updateUI();
			} catch( Exception ex ) {
				ex.printStackTrace();
			}
		} );
	}

	private void resizableChanged() {
		if( window instanceof Frame )
			((Frame)window).setResizable( resizableCheckBox.isSelected() );
		else if( window instanceof Dialog )
			((Dialog)window).setResizable( resizableCheckBox.isSelected() );
	}

	private void undecoratedChanged() {
		window.dispose();

		if( window instanceof Frame )
			((Frame)window).setUndecorated( undecoratedCheckBox.isSelected() );
		else if( window instanceof Dialog )
			((Dialog)window).setUndecorated( undecoratedCheckBox.isSelected() );

		window.setVisible( true );
	}

	private void maximizedBoundsChanged() {
		if( window instanceof Frame ) {
			((Frame)window).setMaximizedBounds( maximizedBoundsCheckBox.isSelected()
				? new Rectangle( 50, 100, 1000, 700 )
				: null );
			updateInfo();
		}
	}

	private void fullScreenChanged() {
		boolean fullScreen = fullScreenCheckBox.isSelected();

		GraphicsDevice gd = getGraphicsConfiguration().getDevice();
		gd.setFullScreenWindow( fullScreen ? window : null );
	}

	private void nativeChanged() {
		FlatLaf.setUseNativeWindowDecorations( nativeCheckBox.isSelected() );
	}

	private void native2Changed() {
		((RootPaneContainer)window).getRootPane().putClientProperty( FlatClientProperties.USE_WINDOW_DECORATIONS, native2CheckBox.getChecked() );
	}

	private void revalidateLayout() {
		window.revalidate();
	}

	private void replaceRootPane() {
		JRootPane rootPane = new JRootPane();
		if( window instanceof RootPaneContainer )
			rootPane.setWindowDecorationStyle( ((RootPaneContainer)window).getRootPane().getWindowDecorationStyle() );
		rootPane.getContentPane().add( new FlatNativeWindowBorderTest( window ) );

		if( window instanceof MyJFrame )
			((MyJFrame)window).setRootPane( rootPane );
		else if( window instanceof MyJDialog )
			((MyJDialog)window).setRootPane( rootPane );

		window.revalidate();
		window.repaint();
	}

	private void openDialog() {
		showDialog( window );
	}

	private void openFrame() {
		showFrame();
	}

	private void hideWindow() {
		window.setVisible( false );
		hiddenWindowsMap.put( window, null );
	}

	private void showHiddenWindow() {
		for( Window w : hiddenWindowsMap.keySet() ) {
			hiddenWindowsMap.remove( w );
			w.setVisible( true );
			break;
		}
	}

	private void reopen() {
		window.dispose();
		window.setVisible( true );
	}

	private void reshow() {
		window.setVisible( false );

		try {
			Thread.sleep( 100 );
		} catch( InterruptedException ex ) {
			// ignore
		}

		window.setVisible( true );
	}

	private void close() {
		window.dispose();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		info = new JLabel();
		resizableCheckBox = new JCheckBox();
		maximizedBoundsCheckBox = new JCheckBox();
		undecoratedCheckBox = new JCheckBox();
		fullScreenCheckBox = new JCheckBox();
		nativeCheckBox = new JCheckBox();
		native2CheckBox = new FlatTriStateCheckBox();
		openDialogButton = new JButton();
		hideWindowButton = new JButton();
		reopenButton = new JButton();
		replaceRootPaneButton = new JButton();
		openFrameButton = new JButton();
		showHiddenWindowButton = new JButton();
		reshowButton = new JButton();
		revalidateButton = new JButton();
		hSpacer1 = new JPanel(null);
		closeButton = new JButton();

		//======== this ========
		setLayout(new MigLayout(
			"insets dialog,hidemode 3",
			// columns
			"[]" +
			"[]" +
			"[grow,fill]",
			// rows
			"[grow,top]para" +
			"[]0" +
			"[]0" +
			"[]" +
			"[]" +
			"[]"));

		//---- info ----
		info.setText("text");
		add(info, "cell 0 0 2 1");

		//---- resizableCheckBox ----
		resizableCheckBox.setText("resizable");
		resizableCheckBox.setSelected(true);
		resizableCheckBox.setMnemonic('R');
		resizableCheckBox.addActionListener(e -> resizableChanged());
		add(resizableCheckBox, "cell 0 1");

		//---- maximizedBoundsCheckBox ----
		maximizedBoundsCheckBox.setText("maximized bounds (50,100, 1000,700)");
		maximizedBoundsCheckBox.setMnemonic('M');
		maximizedBoundsCheckBox.addActionListener(e -> maximizedBoundsChanged());
		add(maximizedBoundsCheckBox, "cell 1 1");

		//---- undecoratedCheckBox ----
		undecoratedCheckBox.setText("undecorated");
		undecoratedCheckBox.setMnemonic('U');
		undecoratedCheckBox.addActionListener(e -> undecoratedChanged());
		add(undecoratedCheckBox, "cell 0 2");

		//---- fullScreenCheckBox ----
		fullScreenCheckBox.setText("full screen");
		fullScreenCheckBox.setMnemonic('F');
		fullScreenCheckBox.addActionListener(e -> fullScreenChanged());
		add(fullScreenCheckBox, "cell 1 2");

		//---- nativeCheckBox ----
		nativeCheckBox.setText("FlatLaf native window decorations");
		nativeCheckBox.setSelected(true);
		nativeCheckBox.addActionListener(e -> nativeChanged());
		add(nativeCheckBox, "cell 0 3 3 1");

		//---- native2CheckBox ----
		native2CheckBox.setText("JRootPane.useWindowDecorations");
		native2CheckBox.addActionListener(e -> native2Changed());
		add(native2CheckBox, "cell 0 3 3 1");

		//---- openDialogButton ----
		openDialogButton.setText("Open Dialog");
		openDialogButton.setMnemonic('D');
		openDialogButton.addActionListener(e -> openDialog());
		add(openDialogButton, "cell 0 4 3 1");

		//---- hideWindowButton ----
		hideWindowButton.setText("Hide");
		hideWindowButton.addActionListener(e -> hideWindow());
		add(hideWindowButton, "cell 0 4 3 1");

		//---- reopenButton ----
		reopenButton.setText("Dispose and Reopen");
		reopenButton.addActionListener(e -> reopen());
		add(reopenButton, "cell 0 4 3 1");

		//---- replaceRootPaneButton ----
		replaceRootPaneButton.setText("replace rootpane");
		replaceRootPaneButton.addActionListener(e -> replaceRootPane());
		add(replaceRootPaneButton, "cell 0 4 3 1");

		//---- openFrameButton ----
		openFrameButton.setText("Open Frame");
		openFrameButton.setMnemonic('A');
		openFrameButton.addActionListener(e -> openFrame());
		add(openFrameButton, "cell 0 5 3 1");

		//---- showHiddenWindowButton ----
		showHiddenWindowButton.setText("Show hidden");
		showHiddenWindowButton.addActionListener(e -> showHiddenWindow());
		add(showHiddenWindowButton, "cell 0 5 3 1");

		//---- reshowButton ----
		reshowButton.setText("Hide and Show");
		reshowButton.addActionListener(e -> reshow());
		add(reshowButton, "cell 0 5 3 1");

		//---- revalidateButton ----
		revalidateButton.setText("revalidate");
		revalidateButton.addActionListener(e -> revalidateLayout());
		add(revalidateButton, "cell 0 5 3 1");
		add(hSpacer1, "cell 0 5 3 1,growx");

		//---- closeButton ----
		closeButton.setText("Close");
		closeButton.addActionListener(e -> close());
		add(closeButton, "cell 0 5 3 1");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel info;
	private JCheckBox resizableCheckBox;
	private JCheckBox maximizedBoundsCheckBox;
	private JCheckBox undecoratedCheckBox;
	private JCheckBox fullScreenCheckBox;
	private JCheckBox nativeCheckBox;
	private FlatTriStateCheckBox native2CheckBox;
	private JButton openDialogButton;
	private JButton hideWindowButton;
	private JButton reopenButton;
	private JButton replaceRootPaneButton;
	private JButton openFrameButton;
	private JButton showHiddenWindowButton;
	private JButton reshowButton;
	private JButton revalidateButton;
	private JPanel hSpacer1;
	private JButton closeButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class MyJFrame -----------------------------------------------------

	private static class MyJFrame
		extends JFrame
	{
		MyJFrame( String title ) {
			super( title );
		}

		@Override
		public void setRootPane( JRootPane root ) {
			super.setRootPane( root );
		}
	}

	//---- class MyJDialog ----------------------------------------------------

	private static class MyJDialog
		extends JDialog
	{
		MyJDialog( Window owner, String title, Dialog.ModalityType modalityType ) {
			super( owner, title, modalityType );
		}

		@Override
		public void setRootPane( JRootPane root ) {
			super.setRootPane( root );
		}
	}
}
