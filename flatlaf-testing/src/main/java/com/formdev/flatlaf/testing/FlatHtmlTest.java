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
import javax.swing.text.JTextComponent;
import com.formdev.flatlaf.util.StringUtils;
import com.formdev.flatlaf.util.UIScale;
import com.jidesoft.swing.*;
import net.miginfocom.swing.*;
import org.jdesktop.swingx.*;

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

	private void changeHtmlText() {
		changeHtmlText( this );
	}

	private void changeHtmlText( Component c ) {
		if( c instanceof AbstractButton )
			((AbstractButton)c).setText( changeHtmlText( ((AbstractButton)c).getText() ) );
		else if( c instanceof JLabel )
			((JLabel)c).setText( changeHtmlText( ((JLabel)c).getText() ) );
		else if( c instanceof JTextComponent )
			((JTextComponent)c).setText( changeHtmlText( ((JTextComponent)c).getText() ) );
		else if( c instanceof JToolTip )
			((JToolTip)c).setTipText( changeHtmlText( ((JToolTip)c).getTipText() ) );
		else if( c instanceof JComboBox ) {
			@SuppressWarnings( "unchecked" )
			JComboBox<String> cb = (JComboBox<String>) c;
			DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) cb.getModel();
			String text = model.getElementAt( 0 );
			String newText = changeHtmlText( text );
			if( newText != text ) {
				model.insertElementAt( newText, 1 );
				model.removeElementAt( 0 );
			}
		}

		if( c instanceof Container ) {
			for( Component child : ((Container)c).getComponents() )
				changeHtmlText( child );
		}
	}

	private String changeHtmlText( String text ) {
		String htmlTag = "<html>";
		if( !text.startsWith( htmlTag ) )
			return text;

		String bodyTag = "<body>";
		int bodyIndex = text.indexOf( bodyTag );
		if( bodyIndex < 0 )
			bodyIndex = htmlTag.length();
		else
			bodyIndex += bodyTag.length();

		int insertIndex = text.indexOf( '>', bodyIndex );
		if( insertIndex < 0 )
			insertIndex = bodyIndex;
		else
			insertIndex++;

		return text.substring( 0, insertIndex ) + "X" + text.substring( insertIndex );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel labelLabel = new JLabel();
		JLabel editorPaneLabel = new JLabel();
		JLabel textPaneLabel = new JLabel();
		JLabel toolTipLabel = new JLabel();
		JPanel panel1 = new JPanel();
		JLabel label5 = new JLabel();
		JLabel label6 = new JLabel();
		JLabel label7 = new JLabel();
		JLabel label3 = new JLabel();
		JButton button1 = new JButton();
		JButton button2 = new JButton();
		JLabel label11 = new JLabel();
		JToggleButton toggleButton1 = new JToggleButton();
		JToggleButton toggleButton2 = new JToggleButton();
		JLabel label12 = new JLabel();
		JCheckBox checkBox1 = new JCheckBox();
		JCheckBox checkBox2 = new JCheckBox();
		JLabel label13 = new JLabel();
		JRadioButton radioButton1 = new JRadioButton();
		JRadioButton radioButton2 = new JRadioButton();
		JLabel label8 = new JLabel();
		JMenu menu1 = new JMenu();
		JMenu menu2 = new JMenu();
		JLabel label4 = new JLabel();
		JMenuItem menuItem1 = new JMenuItem();
		JMenuItem menuItem2 = new JMenuItem();
		JLabel label9 = new JLabel();
		JCheckBoxMenuItem checkBoxMenuItem1 = new JCheckBoxMenuItem();
		JCheckBoxMenuItem checkBoxMenuItem2 = new JCheckBoxMenuItem();
		JLabel label10 = new JLabel();
		JRadioButtonMenuItem radioButtonMenuItem1 = new JRadioButtonMenuItem();
		JRadioButtonMenuItem radioButtonMenuItem2 = new JRadioButtonMenuItem();
		JLabel label14 = new JLabel();
		JToolTip toolTip3 = new JToolTip();
		JToolTip toolTip4 = new JToolTip();
		JLabel label17 = new JLabel();
		JComboBox<String> comboBox1 = new JComboBox<>();
		JComboBox<String> comboBox2 = new JComboBox<>();
		JLabel label56 = new JLabel();
		JXBusyLabel xBusyLabel1 = new JXBusyLabel();
		JXBusyLabel xBusyLabel2 = new JXBusyLabel();
		JLabel label18 = new JLabel();
		JXHyperlink xHyperlink1 = new JXHyperlink();
		JXHyperlink xHyperlink2 = new JXHyperlink();
		JLabel label33 = new JLabel();
		JideLabel jideLabel1 = new JideLabel();
		JideLabel jideLabel2 = new JideLabel();
		JLabel label16 = new JLabel();
		JideButton jideButton1 = new JideButton();
		JideButton jideButton2 = new JideButton();
		JLabel label54 = new JLabel();
		JideToggleButton jideToggleButton1 = new JideToggleButton();
		JideToggleButton jideToggleButton2 = new JideToggleButton();
		JButton changeHtmlTextButton = new JButton();
		JLabel label15 = new JLabel();
		label1 = new JLabel();
		JScrollPane scrollPane15 = new JScrollPane();
		editorPane1 = new JEditorPane();
		JScrollPane scrollPane16 = new JScrollPane();
		textPane1 = new JTextPane();
		toolTip1 = new JToolTip();
		label2 = new JLabel();
		JScrollPane scrollPane17 = new JScrollPane();
		editorPane2 = new JEditorPane();
		JScrollPane scrollPane18 = new JScrollPane();
		textPane2 = new JTextPane();
		toolTip2 = new JToolTip();
		panel2 = new JPanel();
		JLabel label22 = new JLabel();
		JLabel label19 = new JLabel();
		JLabel label20 = new JLabel();
		JLabel label25 = new JLabel();
		JLabel label28 = new JLabel();
		JLabel label31 = new JLabel();
		JLabel label23 = new JLabel();
		JLabel label35 = new JLabel();
		JLabel label41 = new JLabel();
		JLabel label37 = new JLabel();
		JLabel label32 = new JLabel();
		JLabel label36 = new JLabel();
		JLabel label30 = new JLabel();
		JLabel label29 = new JLabel();
		JLabel label34 = new JLabel();
		JLabel label40 = new JLabel();
		JLabel label39 = new JLabel();
		JLabel label49 = new JLabel();
		JLabel label50 = new JLabel();
		JLabel label43 = new JLabel();
		JLabel label42 = new JLabel();
		JLabel label51 = new JLabel();
		JLabel label26 = new JLabel();
		JLabel label38 = new JLabel();
		JLabel label27 = new JLabel();
		JLabel label45 = new JLabel();
		JLabel label52 = new JLabel();
		JLabel label44 = new JLabel();
		JLabel label21 = new JLabel();
		JLabel label24 = new JLabel();
		JLabel label46 = new JLabel();
		JLabel label47 = new JLabel();
		JLabel label53 = new JLabel();
		JLabel label48 = new JLabel();

		//======== this ========
		setLayout(new MigLayout(
			"flowy,ltr,insets dialog,hidemode 3",
			// columns
			"[grow,sizegroup 1,fill]" +
			"[grow,sizegroup 1,fill]" +
			"[grow,sizegroup 1,fill]" +
			"[grow,sizegroup 1,fill]" +
			"[fill]",
			// rows
			"[]" +
			"[fill]" +
			"[grow,fill]"));

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
				"[]" +
				"[]unrel" +
				"[]" +
				"[]unrel" +
				"[]" +
				"[]" +
				"[]para" +
				"[]" +
				"[]"));

			//---- label5 ----
			label5.setText("JLabel:");
			panel1.add(label5, "cell 0 0");

			//---- label6 ----
			label6.setText("<html>Some <b>Bold</b> Text <kbd>kbd</kbd>");
			panel1.add(label6, "cell 1 0");

			//---- label7 ----
			label7.setText("Some text");
			panel1.add(label7, "cell 2 0");

			//---- label3 ----
			label3.setText("JButton:");
			panel1.add(label3, "cell 0 1");

			//---- button1 ----
			button1.setText("<html>Some <b>Bold</b> Text <kbd>kbd</kbd>");
			panel1.add(button1, "cell 1 1");

			//---- button2 ----
			button2.setText("Some text");
			panel1.add(button2, "cell 2 1");

			//---- label11 ----
			label11.setText("JToggleButton:");
			panel1.add(label11, "cell 0 2");

			//---- toggleButton1 ----
			toggleButton1.setText("<html>Some <b>Bold</b> Text <kbd>kbd</kbd>");
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
			checkBox1.setText("<html>Some <b>Bold</b> Text <kbd>kbd</kbd>");
			panel1.add(checkBox1, "cell 1 3");

			//---- checkBox2 ----
			checkBox2.setText("Some text");
			panel1.add(checkBox2, "cell 2 3");

			//---- label13 ----
			label13.setText("JRadioButton:");
			panel1.add(label13, "cell 0 4");

			//---- radioButton1 ----
			radioButton1.setText("<html>Some <b>Bold</b> Text <kbd>kbd</kbd>");
			panel1.add(radioButton1, "cell 1 4");

			//---- radioButton2 ----
			radioButton2.setText("Some text");
			panel1.add(radioButton2, "cell 2 4");

			//---- label8 ----
			label8.setText("JMenu:");
			panel1.add(label8, "cell 0 5");

			//======== menu1 ========
			{
				menu1.setText("<html>Some <b>Bold</b> Text <kbd>kbd</kbd>");
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
			menuItem1.setText("<html>Some <b>Bold</b> Text <kbd>kbd</kbd>");
			panel1.add(menuItem1, "cell 1 6");

			//---- menuItem2 ----
			menuItem2.setText("Some text");
			panel1.add(menuItem2, "cell 2 6");

			//---- label9 ----
			label9.setText("JCheckBoxMenuItem:");
			panel1.add(label9, "cell 0 7");

			//---- checkBoxMenuItem1 ----
			checkBoxMenuItem1.setText("<html>Some <b>Bold</b> Text <kbd>kbd</kbd>");
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
			radioButtonMenuItem1.setText("<html>Some <b>Bold</b> Text <kbd>kbd</kbd>");
			radioButtonMenuItem1.setSelected(true);
			panel1.add(radioButtonMenuItem1, "cell 1 8");

			//---- radioButtonMenuItem2 ----
			radioButtonMenuItem2.setText("Some text");
			radioButtonMenuItem2.setSelected(true);
			panel1.add(radioButtonMenuItem2, "cell 2 8");

			//---- label14 ----
			label14.setText("JToolTip:");
			panel1.add(label14, "cell 0 9");

			//---- toolTip3 ----
			toolTip3.setTipText("<html>Some <b>Bold</b> Text <kbd>kbd</kbd>");
			panel1.add(toolTip3, "cell 1 9");

			//---- toolTip4 ----
			toolTip4.setTipText("Some text");
			panel1.add(toolTip4, "cell 2 9");

			//---- label17 ----
			label17.setText("JComboBox:");
			panel1.add(label17, "cell 0 10");

			//---- comboBox1 ----
			comboBox1.setModel(new DefaultComboBoxModel<>(new String[] {
				"<html>Some <b>Bold</b> Text <kbd>kbd</kbd>",
				"abc",
				"def"
			}));
			panel1.add(comboBox1, "cell 1 10");

			//---- comboBox2 ----
			comboBox2.setModel(new DefaultComboBoxModel<>(new String[] {
				"Some Text",
				"abc",
				"def"
			}));
			panel1.add(comboBox2, "cell 2 10");

			//---- label56 ----
			label56.setText("JXBusyLabel:");
			panel1.add(label56, "cell 0 11");

			//---- xBusyLabel1 ----
			xBusyLabel1.setText("<html>Some <b>Bold</b> Text <kbd>kbd</kbd>");
			panel1.add(xBusyLabel1, "cell 1 11");

			//---- xBusyLabel2 ----
			xBusyLabel2.setText("Some text");
			panel1.add(xBusyLabel2, "cell 2 11");

			//---- label18 ----
			label18.setText("JXHyperlink:");
			panel1.add(label18, "cell 0 12");

			//---- xHyperlink1 ----
			xHyperlink1.setText("<html>Some <b>Bold</b> Text <kbd>kbd</kbd>");
			panel1.add(xHyperlink1, "cell 1 12");

			//---- xHyperlink2 ----
			xHyperlink2.setText("Some text");
			panel1.add(xHyperlink2, "cell 2 12");

			//---- label33 ----
			label33.setText("JideLabel:");
			panel1.add(label33, "cell 0 13");

			//---- jideLabel1 ----
			jideLabel1.setText("<html>Some <b>Bold</b> Text <kbd>kbd</kbd>");
			panel1.add(jideLabel1, "cell 1 13");

			//---- jideLabel2 ----
			jideLabel2.setText("Some text");
			panel1.add(jideLabel2, "cell 2 13");

			//---- label16 ----
			label16.setText("JideButton:");
			panel1.add(label16, "cell 0 14");

			//---- jideButton1 ----
			jideButton1.setText("<html>Some <b>Bold</b> Text <kbd>kbd</kbd>");
			panel1.add(jideButton1, "cell 1 14");

			//---- jideButton2 ----
			jideButton2.setText("Some text");
			panel1.add(jideButton2, "cell 2 14");

			//---- label54 ----
			label54.setText("JideToggleButton:");
			panel1.add(label54, "cell 0 15");

			//---- jideToggleButton1 ----
			jideToggleButton1.setText("<html>Some <b>Bold</b> Text <kbd>kbd</kbd>");
			panel1.add(jideToggleButton1, "cell 1 15");

			//---- jideToggleButton2 ----
			jideToggleButton2.setText("Some text");
			panel1.add(jideToggleButton2, "cell 2 15");

			//---- changeHtmlTextButton ----
			changeHtmlTextButton.setText("Change HTML Text");
			changeHtmlTextButton.addActionListener(e -> changeHtmlText());
			panel1.add(changeHtmlTextButton, "cell 0 16");

			//---- label15 ----
			label15.setText("(use to check whether CSS is updated on text changes)");
			panel1.add(label15, "cell 0 17 3 1");
		}
		add(panel1, "cell 4 0 1 3,aligny top,growy 0");

		//---- label1 ----
		label1.setText("<html>HTML<br>Sample <b>content</b><br> <u>text</u> with <a href=\"#\">link</a><h1>Header 1</h1><h2>Header 2</h2><h3>Header 3</h3><h4>Header 4</h4><h5>Header 5</h5><h6>Header 6</h6><p>Paragraph</p><address>Address</address><hr><table border=\"1\"><tr><th>Col 1</th><th>Col 2</th></tr><tr><td>abc</td><td>def</td></tr></table><ul><li>item 1</li><li>item 2</li></ul></html>");
		label1.setVerticalAlignment(SwingConstants.TOP);
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
		label2.setVerticalAlignment(SwingConstants.TOP);
		add(label2, "cell 0 2");

		//======== scrollPane17 ========
		{

			//---- editorPane2 ----
			editorPane2.setContentType("text/html");
			editorPane2.setText("text");
			scrollPane17.setViewportView(editorPane2);
		}
		add(scrollPane17, "cell 1 2");

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

		//======== panel2 ========
		{
			panel2.setLayout(new MigLayout(
				"insets 0,hidemode 3",
				// columns
				"[fill]para" +
				"[fill]" +
				"[fill]" +
				"[fill]" +
				"[fill]" +
				"[fill]" +
				"[fill]",
				// rows
				"[]" +
				"[]" +
				"[]" +
				"[]" +
				"[]" +
				"[]"));

			//---- label22 ----
			label22.setText("plain");
			panel2.add(label22, "cell 0 0");

			//---- label19 ----
			label19.setText("<html><h1>h1</h1></html>");
			panel2.add(label19, "cell 1 0");

			//---- label20 ----
			label20.setText("<html><h2>h2</h2></html>");
			panel2.add(label20, "cell 2 0");

			//---- label25 ----
			label25.setText("<html><h3>h3</h3></html>");
			panel2.add(label25, "cell 3 0");

			//---- label28 ----
			label28.setText("<html><h4>h4</h4></html>");
			panel2.add(label28, "cell 4 0");

			//---- label31 ----
			label31.setText("<html><h5>h5</h5></html>");
			panel2.add(label31, "cell 5 0");

			//---- label23 ----
			label23.setText("<html><h6>h6</h6></html>");
			panel2.add(label23, "cell 6 0");

			//---- label35 ----
			label35.setText("plain");
			panel2.add(label35, "cell 0 1");

			//---- label41 ----
			label41.setText("<html><strong>strong</strong></html>");
			panel2.add(label41, "cell 1 1");

			//---- label37 ----
			label37.setText("<html><b>b</b></html>");
			panel2.add(label37, "cell 2 1");

			//---- label32 ----
			label32.setText("<html><em>em</em></html>");
			panel2.add(label32, "cell 3 1");

			//---- label36 ----
			label36.setText("<html><i>i</i></html>");
			panel2.add(label36, "cell 4 1");

			//---- label30 ----
			label30.setText("<html><cite>cite</cite></html>");
			panel2.add(label30, "cell 5 1");

			//---- label29 ----
			label29.setText("<html><dfn>dfn</dfn></html>");
			panel2.add(label29, "cell 6 1");

			//---- label34 ----
			label34.setText("plain");
			panel2.add(label34, "cell 0 2");

			//---- label40 ----
			label40.setText("<html><strike>strike</strike></html>");
			panel2.add(label40, "cell 1 2");

			//---- label39 ----
			label39.setText("<html><s>s</s></html>");
			panel2.add(label39, "cell 2 2");

			//---- label49 ----
			label49.setText("<html><body>body</body></html>");
			panel2.add(label49, "cell 3 2");

			//---- label50 ----
			label50.setText("<html><a href=\"#\">a</a></html>");
			panel2.add(label50, "cell 4 2");

			//---- label43 ----
			label43.setText("<html><u>u</u></html>");
			panel2.add(label43, "cell 5 2");

			//---- label42 ----
			label42.setText("<html><var>var</var></html>");
			panel2.add(label42, "cell 6 2");

			//---- label51 ----
			label51.setText("plain");
			panel2.add(label51, "cell 0 3");

			//---- label26 ----
			label26.setText("<html><code>code</code></html>");
			panel2.add(label26, "cell 1 3");

			//---- label38 ----
			label38.setText("<html><kbd>kbd</kbd></html>");
			panel2.add(label38, "cell 2 3");

			//---- label27 ----
			label27.setText("<html><samp>samp</samp></html>");
			panel2.add(label27, "cell 3 3");

			//---- label45 ----
			label45.setText("<html><tt>tt</tt></html>");
			panel2.add(label45, "cell 4 3");

			//---- label52 ----
			label52.setText("plain");
			panel2.add(label52, "cell 0 4");

			//---- label44 ----
			label44.setText("<html><pre>pre</pre></html>");
			panel2.add(label44, "cell 1 4");

			//---- label21 ----
			label21.setText("<html><big>big</big></html>");
			panel2.add(label21, "cell 2 4");

			//---- label24 ----
			label24.setText("<html><small>small</small></html>");
			panel2.add(label24, "cell 3 4");

			//---- label46 ----
			label46.setText("<html><sub>sub</sub></html>");
			panel2.add(label46, "cell 4 4");

			//---- label47 ----
			label47.setText("<html><sup>sup</sup></html>");
			panel2.add(label47, "cell 5 4");

			//---- label53 ----
			label53.setText("plain");
			panel2.add(label53, "cell 0 5");

			//---- label48 ----
			label48.setText("<html><address>address</address></html>");
			panel2.add(label48, "cell 1 5");
		}
		add(panel2, "cell 4 0 1 3");
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

		Font largeLabelFont = increaseFontSize( UIManager.getFont( "Label.font" ) );
		for( Component c : panel2.getComponents() )
			c.setFont( largeLabelFont );
	}

	private void increaseFontSize( JComponent c, Font baseFont ) {
		c.setFont( increaseFontSize( baseFont ) );
	}

	private Font increaseFontSize( Font baseFont ) {
		return baseFont.deriveFont( Font.PLAIN, baseFont.getSize() + UIScale.scale( 10f ) );
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel label1;
	private JEditorPane editorPane1;
	private JTextPane textPane1;
	private JToolTip toolTip1;
	private JLabel label2;
	private JEditorPane editorPane2;
	private JTextPane textPane2;
	private JToolTip toolTip2;
	private JPanel panel2;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
