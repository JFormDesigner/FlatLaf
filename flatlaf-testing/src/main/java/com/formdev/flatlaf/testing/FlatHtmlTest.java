/*
 * Copyright 2020 FormDev Software GmbH
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
import javax.swing.*;
import com.formdev.flatlaf.util.StringUtils;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatHtmlTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatHtmlTest" );
			frame.showFrame( FlatHtmlTest::new );
		} );
	}

	FlatHtmlTest() {
		initComponents();

		String html = label1.getText();
		label2.setText( html );
		toolTip1.setTipText( html );
		toolTip2.setTipText( html );

		String html2 = StringUtils.removeLeading( StringUtils.removeTrailing( html, "</html>" ), "<html>" );
		editorPane1.setText( html2 );
		editorPane2.setText( html2 );
		textPane1.setText( html2 );
		textPane2.setText( html2 );

		increaseFontSize();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		labelLabel = new JLabel();
		editorPaneLabel = new JLabel();
		textPaneLabel = new JLabel();
		toolTipLabel = new JLabel();
		label1 = new JLabel();
		scrollPane15 = new JScrollPane();
		editorPane1 = new JEditorPane();
		scrollPane16 = new JScrollPane();
		textPane1 = new JTextPane();
		toolTip1 = new JToolTip();
		label2 = new JLabel();
		scrollPane17 = new JScrollPane();
		editorPane2 = new JEditorPane();
		scrollPane18 = new JScrollPane();
		textPane2 = new JTextPane();
		toolTip2 = new JToolTip();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[fill]" +
			"[fill]" +
			"[fill]" +
			"[fill]",
			// rows
			"[]" +
			"[top]" +
			"[top]"));

		//---- labelLabel ----
		labelLabel.setText("JLabel:");
		add(labelLabel, "cell 0 0");

		//---- editorPaneLabel ----
		editorPaneLabel.setText("JEditorPane:");
		add(editorPaneLabel, "cell 1 0");

		//---- textPaneLabel ----
		textPaneLabel.setText("JTextPane:");
		add(textPaneLabel, "cell 2 0");

		//---- toolTipLabel ----
		toolTipLabel.setText("JToolTip:");
		add(toolTipLabel, "cell 3 0");

		//---- label1 ----
		label1.setText("<html>HTML<br>Sample <b>content</b><br> <u>text</u> with <a href=\"#\">link</a><h1>Header 1</h1><h2>Header 2</h2><h3>Header 3</h3><h4>Header 4</h4><h5>Header 5</h5><h6>Header 6</h6><p>Paragraph</p><hr><table border=\"1\"><tr><th>Col 1</th><th>Col 2</th></tr><tr><td>abc</td><td>def</td></tr></table><ul><li>item 1</li><li>item 2</li></ul></html>");
		add(label1, "cell 0 1");

		//======== scrollPane15 ========
		{

			//---- editorPane1 ----
			editorPane1.setContentType("text/html");
			editorPane1.setText("text");
			scrollPane15.setViewportView(editorPane1);
		}
		add(scrollPane15, "cell 1 1,grow");

		//======== scrollPane16 ========
		{

			//---- textPane1 ----
			textPane1.setContentType("text/html");
			textPane1.setText("text");
			scrollPane16.setViewportView(textPane1);
		}
		add(scrollPane16, "cell 2 1");

		//---- toolTip1 ----
		toolTip1.setTipText("text");
		add(toolTip1, "cell 3 1");

		//---- label2 ----
		label2.setText("text");
		add(label2, "cell 0 2");

		//======== scrollPane17 ========
		{

			//---- editorPane2 ----
			editorPane2.setContentType("text/html");
			editorPane2.setText("text");
			scrollPane17.setViewportView(editorPane2);
		}
		add(scrollPane17, "cell 1 2,grow");

		//======== scrollPane18 ========
		{

			//---- textPane2 ----
			textPane2.setContentType("text/html");
			textPane2.setText("text");
			scrollPane18.setViewportView(textPane2);
		}
		add(scrollPane18, "cell 2 2");

		//---- toolTip2 ----
		toolTip2.setTipText("text");
		add(toolTip2, "cell 3 2");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	@Override
	public void updateUI() {
		super.updateUI();

		EventQueue.invokeLater( () -> {
			increaseFontSize();
		} );
	}

	private void increaseFontSize() {
		increaseFontSize( label2, label1.getFont() );
		increaseFontSize( editorPane2, editorPane1.getFont() );
		increaseFontSize( textPane2, textPane1.getFont() );
		increaseFontSize( toolTip2, toolTip1.getFont() );
	}

	private void increaseFontSize( JComponent c, Font baseFont ) {
		c.setFont( baseFont.deriveFont( Font.PLAIN, baseFont.getSize() + UIScale.scale( 10f ) ) );
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel labelLabel;
	private JLabel editorPaneLabel;
	private JLabel textPaneLabel;
	private JLabel toolTipLabel;
	private JLabel label1;
	private JScrollPane scrollPane15;
	private JEditorPane editorPane1;
	private JScrollPane scrollPane16;
	private JTextPane textPane1;
	private JToolTip toolTip1;
	private JLabel label2;
	private JScrollPane scrollPane17;
	private JEditorPane editorPane2;
	private JScrollPane scrollPane18;
	private JTextPane textPane2;
	private JToolTip toolTip2;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
