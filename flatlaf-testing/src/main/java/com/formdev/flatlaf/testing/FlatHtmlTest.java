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
		panel1 = new JPanel();
		label5 = new JLabel();
		label6 = new JLabel();
		label7 = new JLabel();
		label3 = new JLabel();
		button1 = new JButton();
		button2 = new JButton();
		label11 = new JLabel();
		toggleButton1 = new JToggleButton();
		toggleButton2 = new JToggleButton();
		label12 = new JLabel();
		checkBox1 = new JCheckBox();
		checkBox2 = new JCheckBox();
		label13 = new JLabel();
		radioButton1 = new JRadioButton();
		radioButton2 = new JRadioButton();
		label8 = new JLabel();
		menu1 = new JMenu();
		menu2 = new JMenu();
		label4 = new JLabel();
		menuItem1 = new JMenuItem();
		menuItem2 = new JMenuItem();
		label9 = new JLabel();
		checkBoxMenuItem1 = new JCheckBoxMenuItem();
		checkBoxMenuItem2 = new JCheckBoxMenuItem();
		label10 = new JLabel();
		radioButtonMenuItem1 = new JRadioButtonMenuItem();
		radioButtonMenuItem2 = new JRadioButtonMenuItem();
		label14 = new JLabel();
		label15 = new JLabel();
		label16 = new JLabel();
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

		//======== panel1 ========
		{
			panel1.setLayout(new MigLayout(
				"insets 0,hidemode 3",
				// columns
				"[fill]" +
				"[fill]" +
				"[fill]",
				// rows
				"[]" +
				"[]" +
				"[]" +
				"[]" +
				"[]" +
				"[]" +
				"[]" +
				"[]" +
				"[]" +
				"[]"));

			//---- label5 ----
			label5.setText("JLabel:");
			panel1.add(label5, "cell 0 0");

			//---- label6 ----
			label6.setText("<html>Some <b>Bold</b> Text");
			panel1.add(label6, "cell 1 0");

			//---- label7 ----
			label7.setText("Some text");
			panel1.add(label7, "cell 2 0");

			//---- label3 ----
			label3.setText("JButon:");
			panel1.add(label3, "cell 0 1");

			//---- button1 ----
			button1.setText("<html>Some <b>Bold</b> Text");
			panel1.add(button1, "cell 1 1");

			//---- button2 ----
			button2.setText("Some text");
			panel1.add(button2, "cell 2 1");

			//---- label11 ----
			label11.setText("JToggleButton:");
			panel1.add(label11, "cell 0 2");

			//---- toggleButton1 ----
			toggleButton1.setText("<html>Some <b>Bold</b> Text");
			toggleButton1.setSelected(true);
			panel1.add(toggleButton1, "cell 1 2");

			//---- toggleButton2 ----
			toggleButton2.setText("Some text");
			toggleButton2.setSelected(true);
			panel1.add(toggleButton2, "cell 2 2");

			//---- label12 ----
			label12.setText("JCheckBox:");
			panel1.add(label12, "cell 0 3");

			//---- checkBox1 ----
			checkBox1.setText("<html>Some <b>Bold</b> Text");
			panel1.add(checkBox1, "cell 1 3");

			//---- checkBox2 ----
			checkBox2.setText("Some text");
			panel1.add(checkBox2, "cell 2 3");

			//---- label13 ----
			label13.setText("JRadioButton:");
			panel1.add(label13, "cell 0 4");

			//---- radioButton1 ----
			radioButton1.setText("<html>Some <b>Bold</b> Text");
			panel1.add(radioButton1, "cell 1 4");

			//---- radioButton2 ----
			radioButton2.setText("Some text");
			panel1.add(radioButton2, "cell 2 4");

			//---- label8 ----
			label8.setText("JMenu:");
			panel1.add(label8, "cell 0 5");

			//======== menu1 ========
			{
				menu1.setText("<html>Some <b>Bold</b> Text");
			}
			panel1.add(menu1, "cell 1 5");

			//======== menu2 ========
			{
				menu2.setText("Some text");
			}
			panel1.add(menu2, "cell 2 5");

			//---- label4 ----
			label4.setText("JMenuItem:");
			panel1.add(label4, "cell 0 6");

			//---- menuItem1 ----
			menuItem1.setText("<html>Some <b>Bold</b> Text");
			panel1.add(menuItem1, "cell 1 6");

			//---- menuItem2 ----
			menuItem2.setText("Some text");
			panel1.add(menuItem2, "cell 2 6");

			//---- label9 ----
			label9.setText("JCheckBoxMenuItem:");
			panel1.add(label9, "cell 0 7");

			//---- checkBoxMenuItem1 ----
			checkBoxMenuItem1.setText("<html>Some <b>Bold</b> Text");
			checkBoxMenuItem1.setSelected(true);
			panel1.add(checkBoxMenuItem1, "cell 1 7");

			//---- checkBoxMenuItem2 ----
			checkBoxMenuItem2.setText("Some text");
			checkBoxMenuItem2.setSelected(true);
			panel1.add(checkBoxMenuItem2, "cell 2 7");

			//---- label10 ----
			label10.setText("JRadioButtonMenuItem:");
			panel1.add(label10, "cell 0 8");

			//---- radioButtonMenuItem1 ----
			radioButtonMenuItem1.setText("<html>Some <b>Bold</b> Text");
			radioButtonMenuItem1.setSelected(true);
			panel1.add(radioButtonMenuItem1, "cell 1 8");

			//---- radioButtonMenuItem2 ----
			radioButtonMenuItem2.setText("Some text");
			radioButtonMenuItem2.setSelected(true);
			panel1.add(radioButtonMenuItem2, "cell 2 8");

			//---- label14 ----
			label14.setText("JToolTip:");
			panel1.add(label14, "cell 0 9");

			//---- label15 ----
			label15.setText("(move mouse here)");
			label15.setToolTipText("<html>Some <b>Bold</b> Text");
			panel1.add(label15, "cell 1 9");

			//---- label16 ----
			label16.setText("(move mouse here)");
			label16.setToolTipText("Some text");
			panel1.add(label16, "cell 2 9");
		}
		add(panel1, "cell 4 0 1 3,aligny top,growy 0");

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
	private JPanel panel1;
	private JLabel label5;
	private JLabel label6;
	private JLabel label7;
	private JLabel label3;
	private JButton button1;
	private JButton button2;
	private JLabel label11;
	private JToggleButton toggleButton1;
	private JToggleButton toggleButton2;
	private JLabel label12;
	private JCheckBox checkBox1;
	private JCheckBox checkBox2;
	private JLabel label13;
	private JRadioButton radioButton1;
	private JRadioButton radioButton2;
	private JLabel label8;
	private JMenu menu1;
	private JMenu menu2;
	private JLabel label4;
	private JMenuItem menuItem1;
	private JMenuItem menuItem2;
	private JLabel label9;
	private JCheckBoxMenuItem checkBoxMenuItem1;
	private JCheckBoxMenuItem checkBoxMenuItem2;
	private JLabel label10;
	private JRadioButtonMenuItem radioButtonMenuItem1;
	private JRadioButtonMenuItem radioButtonMenuItem2;
	private JLabel label14;
	private JLabel label15;
	private JLabel label16;
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
