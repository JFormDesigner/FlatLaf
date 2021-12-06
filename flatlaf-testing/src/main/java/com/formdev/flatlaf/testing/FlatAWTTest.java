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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import com.formdev.flatlaf.FlatLightLaf;

/**
 * Used to test AWT components on macOS, which internally use Swing.
 *
 * @author Karl Tauber
 */
public class FlatAWTTest
{
	public static void main( String[] args ) {
		EventQueue.invokeLater( () -> {
			FlatLightLaf.setup();

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
			choice.add( "item 1" );
			choice.add( "item 2" );
			choice.add( "item 3" );
			frame.add( choice );

			frame.add( new TextField( "text" ) );
			frame.add( new TextArea( "text" ) );

			List list = new List();
			list.add( "item 1" );
			list.add( "item 2" );
			frame.add( list );

			frame.add( new Scrollbar() );
			frame.add( new ScrollPane() );
			frame.add( new Panel() );
			frame.add( new Canvas() );

			frame.setSize( 800, 600 );
			frame.setVisible( true );
		});
	}
}
