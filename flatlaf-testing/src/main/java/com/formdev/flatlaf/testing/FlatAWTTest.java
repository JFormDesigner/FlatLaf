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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.SwingUtilities;
import com.formdev.flatlaf.*;

/**
 * Used to test AWT components on macOS, which internally use Swing.
 *
 * @author Karl Tauber
 */
public class FlatAWTTest
{
	private static Color oldBackground;

	public static void main( String[] args ) {
		EventQueue.invokeLater( () -> {
			FlatLightLaf.setup();
//			FlatIntelliJLaf.setup();
//			FlatDarkLaf.setup();
//			FlatDarculaLaf.setup();

			Frame frame = new Frame( "FlatAWTTest" );
			frame.addWindowListener( new WindowAdapter() {
				@Override
				public void windowClosing( WindowEvent e ) {
					System.exit( 0 );
				}
			} );
			frame.setLayout( new FlowLayout() );

			frame.add( new Label( "text" ) );
			frame.add( new Button( "text" ) );
			frame.add( new Checkbox( "text" ) );

			CheckboxGroup checkboxGroup = new CheckboxGroup();
			frame.add( new Checkbox( "radio 1", true, checkboxGroup ) );
			frame.add( new Checkbox( "radio 2", false, checkboxGroup ) );
			frame.add( new Checkbox( "radio 3", false, checkboxGroup ) );

			Choice choice = new Choice();
			for( int i = 1; i <= 20; i++ )
				choice.add( "item " + i );
			frame.add( choice );

			frame.add( new TextField( "text" ) );
			frame.add( new TextArea( "text\n1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n11\n12\n13\n14\n15" ) );

			List list = new List();
			for( int i = 1; i <= 10; i++ )
				list.add( "item " + i );
			list.select( 1 );
			frame.add( list );

			frame.add( new Scrollbar() );
			frame.add( new ScrollPane( ScrollPane.SCROLLBARS_ALWAYS ) );
			frame.add( new Panel() );
			frame.add( new Canvas() );

/*
			java.beans.PropertyChangeListener pcl = e -> {
				System.out.println( e.getSource().getClass().getName()
					+ ": " + e.getPropertyName() + "   " + e.getOldValue() + " --> " + e.getNewValue() );
			};
			for( Component c : frame.getComponents() ) {
				c.addPropertyChangeListener( "background", pcl );
				c.addPropertyChangeListener( "foreground", pcl );
			}
			frame.addPropertyChangeListener( "background", pcl );
			frame.addPropertyChangeListener( "foreground", pcl );
*/

			Panel controlPanel = new Panel();
			frame.add( controlPanel );

			Checkbox enabledCheckBox = new Checkbox( "enabled", true );
			enabledCheckBox.addItemListener( e -> {
				boolean enabled = enabledCheckBox.getState();
				for( Component c : frame.getComponents() ) {
					if( c != controlPanel )
						c.setEnabled( enabled );
				}
			} );
			controlPanel.add( enabledCheckBox );

			Checkbox explicitColorsCheckBox = new Checkbox( "explicit colors" );
			explicitColorsCheckBox.addItemListener( e -> {
				boolean explicit = explicitColorsCheckBox.getState();
				for( Component c : frame.getComponents() ) {
					if( c != controlPanel ) {
						c.setBackground( explicit ? Color.green : null );
						c.setForeground( explicit ? Color.red : null );
					}
				}
			} );
			controlPanel.add( explicitColorsCheckBox );

			Checkbox backgroundColorsCheckBox = new Checkbox( "background color" );
			backgroundColorsCheckBox.addItemListener( e -> {
				if( oldBackground == null )
					oldBackground = frame.getBackground();
				frame.setBackground( backgroundColorsCheckBox.getState() ? Color.orange : oldBackground );
			} );
			controlPanel.add( backgroundColorsCheckBox );

			Menu menu = new Menu( "File" );
			menu.add( new MenuItem( "New" ) );
			menu.add( new MenuItem( "Open" ) );
			menu.add( new MenuItem( "Save" ) );

			MenuBar menuBar = new MenuBar();
			menuBar.add( menu );
			frame.setMenuBar( menuBar );

			PopupMenu popupMenu = new PopupMenu();
			popupMenu.add( new MenuItem( "item 1" ) );
			popupMenu.add( new MenuItem( "item 2" ) );
			popupMenu.add( new MenuItem( "item 3" ) );
			list.add( popupMenu );
			list.addMouseListener( new MouseAdapter() {
				@Override
				public
				void mousePressed( MouseEvent e ) {
					if( SwingUtilities.isRightMouseButton( e ) )
						popupMenu.show( list, 0, 0 );
				}
			} );

			frame.setSize( 800, 600 );
			frame.setVisible( true );
		});
	}
}
