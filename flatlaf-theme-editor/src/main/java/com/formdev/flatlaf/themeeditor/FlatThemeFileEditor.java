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

package com.formdev.flatlaf.themeeditor;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import org.fife.ui.rsyntaxtextarea.FileLocation;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.util.UIScale;

/**
 * TODO
 *
 * @author Karl Tauber
 */
public class FlatThemeFileEditor
	extends JFrame
{
	public static void main( String[] args ) {
		File file = new File( args.length > 0
			? args[0]
			: "theme-editor-test.properties" ); // TODO

		SwingUtilities.invokeLater( () -> {
			FlatLightLaf.install();

			FlatThemeFileEditor frame = new FlatThemeFileEditor();

			try {
				frame.themeEditorArea.load( FileLocation.create( file ) );
			} catch( IOException ex ) {
				ex.printStackTrace();
			}

			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setSize( Math.min( UIScale.scale( 800 ), screenSize.width ),
				screenSize.height - UIScale.scale( 100 ) );
			frame.setLocationRelativeTo( null );
			frame.setVisible( true );
		} );
	}

	public FlatThemeFileEditor() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		dialogPane = new JPanel();
		themeEditorArea = new FlatThemeEditorPane();

		//======== this ========
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("FlatLaf Theme Editor");
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setLayout(new BorderLayout());
			dialogPane.add(themeEditorArea, BorderLayout.CENTER);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel dialogPane;
	private FlatThemeEditorPane themeEditorArea;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
