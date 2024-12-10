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
import javax.swing.*;
import javax.swing.border.*;
import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
class MoreComponentsPanel
	extends JPanel
{
	MoreComponentsPanel() {
		initComponents();
	}

	private void changeProgress() {
		int value = slider3.getValue();
		progressBar1.setValue( value );
		progressBar2.setValue( value );
		progressBar3.setValue( value );
		progressBar4.setValue( value );
	}

	private void indeterminateCheckBoxActionPerformed() {
		boolean indeterminate = indeterminateCheckBox.isSelected();
		progressBar1.setIndeterminate( indeterminate );
		progressBar2.setIndeterminate( indeterminate );
		progressBar3.setIndeterminate( indeterminate );
		progressBar4.setIndeterminate( indeterminate );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel scrollPaneLabel = new JLabel();
		JScrollPane scrollPane13 = new JScrollPane();
		JPanel panel1 = new JPanel();
		JScrollBar scrollBar2 = new JScrollBar();
		JScrollBar scrollBar3 = new JScrollBar();
		JScrollBar scrollBar7 = new JScrollBar();
		JScrollBar scrollBar8 = new JScrollBar();
		JSeparator separator2 = new JSeparator();
		JSlider slider2 = new JSlider();
		JSlider slider4 = new JSlider();
		progressBar3 = new JProgressBar();
		progressBar4 = new JProgressBar();
		JToolBar toolBar2 = new JToolBar();
		JButton button9 = new JButton();
		JButton button10 = new JButton();
		JButton button11 = new JButton();
		JToggleButton toggleButton7 = new JToggleButton();
		JPanel panel2 = new JPanel();
		JLabel scrollBarLabel = new JLabel();
		JScrollBar scrollBar1 = new JScrollBar();
		JScrollBar scrollBar4 = new JScrollBar();
		JPanel panel3 = new JPanel();
		JLabel label4 = new JLabel();
		JLabel label3 = new JLabel();
		JScrollPane scrollPane15 = new JScrollPane();
		JEditorPane editorPane6 = new JEditorPane();
		JScrollPane scrollPane16 = new JScrollPane();
		JTextPane textPane6 = new JTextPane();
		JScrollBar scrollBar5 = new JScrollBar();
		JScrollBar scrollBar6 = new JScrollBar();
		JLabel separatorLabel = new JLabel();
		JSeparator separator1 = new JSeparator();
		JLabel sliderLabel = new JLabel();
		JSlider slider1 = new JSlider();
		JSlider slider6 = new JSlider();
		slider3 = new JSlider();
		JSlider slider5 = new JSlider();
		JLabel progressBarLabel = new JLabel();
		progressBar1 = new JProgressBar();
		progressBar2 = new JProgressBar();
		indeterminateCheckBox = new JCheckBox();
		JLabel toolTipLabel = new JLabel();
		JToolTip toolTip1 = new JToolTip();
		JToolTip toolTip2 = new JToolTip();
		JLabel toolBarLabel = new JLabel();
		JToolBar toolBar1 = new JToolBar();
		JButton button4 = new JButton();
		JButton button6 = new JButton();
		JButton button7 = new JButton();
		JButton button8 = new JButton();
		JToggleButton toggleButton6 = new JToggleButton();
		JButton button1 = new JButton();
		JLabel label7 = new JLabel();
		JToggleButton toggleButton1 = new JToggleButton();
		JToggleButton toggleButton2 = new JToggleButton();
		JToggleButton toggleButton3 = new JToggleButton();
		JToggleButton toggleButton4 = new JToggleButton();
		JLabel splitPaneLabel = new JLabel();
		JSplitPane splitPane3 = new JSplitPane();
		JSplitPane splitPane1 = new JSplitPane();
		JPanel panel10 = new JPanel();
		JLabel label1 = new JLabel();
		JPanel panel11 = new JPanel();
		JLabel label2 = new JLabel();
		JSplitPane splitPane2 = new JSplitPane();
		JPanel panel12 = new JPanel();
		JLabel label5 = new JLabel();
		JPanel panel13 = new JPanel();
		JLabel label6 = new JLabel();
		JLabel panelLabel = new JLabel();
		JPanel panel5 = new JPanel();
		JLabel label9 = new JLabel();
		JPanel panel4 = new JPanel();
		JLabel label8 = new JLabel();
		JLabel labelLabel = new JLabel();
		JLabel label13 = new JLabel();
		JLabel label10 = new JLabel();

		//======== this ========
		setLayout(new MigLayout(
			"insets dialog,hidemode 3",
			// columns
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]",
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
			"[]" +
			"[100,top]" +
			"[50,top]" +
			"[]"));

		//---- scrollPaneLabel ----
		scrollPaneLabel.setText("JScrollPane:");
		add(scrollPaneLabel, "cell 0 0");

		//======== scrollPane13 ========
		{
			scrollPane13.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			scrollPane13.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			//======== panel1 ========
			{
				panel1.setPreferredSize(new Dimension(200, 200));
				panel1.setLayout(new BorderLayout());
			}
			scrollPane13.setViewportView(panel1);
		}
		add(scrollPane13, "cell 1 0,grow,width 70,height 40");
		add(scrollBar2, "cell 2 0 1 6,growy");

		//---- scrollBar3 ----
		scrollBar3.setEnabled(false);
		add(scrollBar3, "cell 2 0 1 6,growy");

		//---- scrollBar7 ----
		scrollBar7.putClientProperty(FlatClientProperties.SCROLL_BAR_SHOW_BUTTONS, true);
		add(scrollBar7, "cell 2 0 1 6,growy");

		//---- scrollBar8 ----
		scrollBar8.setEnabled(false);
		scrollBar8.putClientProperty(FlatClientProperties.SCROLL_BAR_SHOW_BUTTONS, true);
		add(scrollBar8, "cell 2 0 1 6,growy");

		//---- separator2 ----
		separator2.setOrientation(SwingConstants.VERTICAL);
		add(separator2, "cell 2 0 1 6,growy");

		//---- slider2 ----
		slider2.setOrientation(SwingConstants.VERTICAL);
		slider2.setValue(30);
		add(slider2, "cell 2 0 1 6,growy,height 100");

		//---- slider4 ----
		slider4.setMinorTickSpacing(10);
		slider4.setPaintTicks(true);
		slider4.setMajorTickSpacing(50);
		slider4.setPaintLabels(true);
		slider4.setOrientation(SwingConstants.VERTICAL);
		slider4.setValue(30);
		add(slider4, "cell 2 0 1 6,growy,height 100");

		//---- progressBar3 ----
		progressBar3.setOrientation(SwingConstants.VERTICAL);
		progressBar3.setValue(60);
		add(progressBar3, "cell 2 0 1 6,growy");

		//---- progressBar4 ----
		progressBar4.setOrientation(SwingConstants.VERTICAL);
		progressBar4.setValue(60);
		progressBar4.setStringPainted(true);
		add(progressBar4, "cell 2 0 1 6,growy");

		//======== toolBar2 ========
		{
			toolBar2.setOrientation(SwingConstants.VERTICAL);

			//---- button9 ----
			button9.setIcon(UIManager.getIcon("Tree.closedIcon"));
			toolBar2.add(button9);

			//---- button10 ----
			button10.setIcon(UIManager.getIcon("Tree.openIcon"));
			toolBar2.add(button10);
			toolBar2.addSeparator();

			//---- button11 ----
			button11.setIcon(UIManager.getIcon("Tree.leafIcon"));
			toolBar2.add(button11);

			//---- toggleButton7 ----
			toggleButton7.setIcon(UIManager.getIcon("Tree.closedIcon"));
			toolBar2.add(toggleButton7);
		}
		add(toolBar2, "cell 2 0 1 6,growy");

		//======== panel2 ========
		{
			panel2.setBorder(new TitledBorder("TitledBorder"));
			panel2.setLayout(new FlowLayout());
		}
		add(panel2, "cell 3 0 1 6,grow");

		//---- scrollBarLabel ----
		scrollBarLabel.setText("JScrollBar:");
		add(scrollBarLabel, "cell 0 1");

		//---- scrollBar1 ----
		scrollBar1.setOrientation(Adjustable.HORIZONTAL);
		add(scrollBar1, "cell 1 1,growx");

		//---- scrollBar4 ----
		scrollBar4.setOrientation(Adjustable.HORIZONTAL);
		scrollBar4.setEnabled(false);
		add(scrollBar4, "cell 1 2,growx");

		//======== panel3 ========
		{
			panel3.setOpaque(false);
			panel3.setLayout(new MigLayout(
				"ltr,insets 0,hidemode 3",
				// columns
				"[]",
				// rows
				"[]" +
				"[]" +
				"[]" +
				"[]"));

			//---- label4 ----
			label4.setText("HTML:");
			panel3.add(label4, "cell 0 0");

			//---- label3 ----
			label3.setText("<html>JLabel HTML<br>Sample <b>content</b><br> <u>text</u> with <a href=\"#\">link</a></html>");
			panel3.add(label3, "cell 0 1");

			//======== scrollPane15 ========
			{

				//---- editorPane6 ----
				editorPane6.setContentType("text/html");
				editorPane6.setText("JEditorPane HTML<br>Sample <b>content</b><br> <u>text</u> with <a href=\"#\">link</a>");
				scrollPane15.setViewportView(editorPane6);
			}
			panel3.add(scrollPane15, "cell 0 2,grow");

			//======== scrollPane16 ========
			{

				//---- textPane6 ----
				textPane6.setContentType("text/html");
				textPane6.setText("JTextPane HTML<br>Sample <b>content</b><br> <u>text</u> with <a href=\"#\">link</a>");
				scrollPane16.setViewportView(textPane6);
			}
			panel3.add(scrollPane16, "cell 0 3,grow");
		}
		add(panel3, "cell 4 0 1 8,aligny top,growy 0");

		//---- scrollBar5 ----
		scrollBar5.setOrientation(Adjustable.HORIZONTAL);
		scrollBar5.putClientProperty(FlatClientProperties.SCROLL_BAR_SHOW_BUTTONS, true);
		add(scrollBar5, "cell 1 3,growx");

		//---- scrollBar6 ----
		scrollBar6.setOrientation(Adjustable.HORIZONTAL);
		scrollBar6.setEnabled(false);
		scrollBar6.putClientProperty(FlatClientProperties.SCROLL_BAR_SHOW_BUTTONS, true);
		add(scrollBar6, "cell 1 4,growx");

		//---- separatorLabel ----
		separatorLabel.setText("JSeparator:");
		add(separatorLabel, "cell 0 5");
		add(separator1, "cell 1 5,growx");

		//---- sliderLabel ----
		sliderLabel.setText("JSlider:");
		add(sliderLabel, "cell 0 6");

		//---- slider1 ----
		slider1.setValue(30);
		add(slider1, "cell 1 6 3 1,aligny top,grow 100 0");

		//---- slider6 ----
		slider6.setEnabled(false);
		slider6.setValue(30);
		add(slider6, "cell 1 6 3 1,aligny top,growy 0");

		//---- slider3 ----
		slider3.setMinorTickSpacing(10);
		slider3.setPaintTicks(true);
		slider3.setMajorTickSpacing(50);
		slider3.setPaintLabels(true);
		slider3.setValue(30);
		slider3.addChangeListener(e -> changeProgress());
		add(slider3, "cell 1 7 3 1,aligny top,grow 100 0");

		//---- slider5 ----
		slider5.setMinorTickSpacing(10);
		slider5.setPaintTicks(true);
		slider5.setMajorTickSpacing(50);
		slider5.setPaintLabels(true);
		slider5.setEnabled(false);
		slider5.setValue(30);
		add(slider5, "cell 1 7 3 1,aligny top,growy 0");

		//---- progressBarLabel ----
		progressBarLabel.setText("JProgressBar:");
		add(progressBarLabel, "cell 0 8");

		//---- progressBar1 ----
		progressBar1.setValue(60);
		add(progressBar1, "cell 1 8 3 1,growx");

		//---- progressBar2 ----
		progressBar2.setStringPainted(true);
		progressBar2.setValue(60);
		add(progressBar2, "cell 1 8 3 1,growx");

		//---- indeterminateCheckBox ----
		indeterminateCheckBox.setText("indeterminate");
		indeterminateCheckBox.addActionListener(e -> indeterminateCheckBoxActionPerformed());
		add(indeterminateCheckBox, "cell 4 8");

		//---- toolTipLabel ----
		toolTipLabel.setText("JToolTip:");
		add(toolTipLabel, "cell 0 9");

		//---- toolTip1 ----
		toolTip1.setTipText("Some text in tool tip.");
		add(toolTip1, "cell 1 9 3 1");

		//---- toolTip2 ----
		toolTip2.setTipText("Tool tip with\nmultiple\nlines.");
		add(toolTip2, "cell 1 9 3 1");

		//---- toolBarLabel ----
		toolBarLabel.setText("JToolBar:");
		add(toolBarLabel, "cell 0 10");

		//======== toolBar1 ========
		{

			//---- button4 ----
			button4.setIcon(UIManager.getIcon("Tree.closedIcon"));
			toolBar1.add(button4);

			//---- button6 ----
			button6.setIcon(UIManager.getIcon("Tree.openIcon"));
			toolBar1.add(button6);
			toolBar1.addSeparator();

			//---- button7 ----
			button7.setIcon(UIManager.getIcon("Tree.leafIcon"));
			toolBar1.add(button7);
			toolBar1.addSeparator();

			//---- button8 ----
			button8.setText("Text");
			button8.setIcon(UIManager.getIcon("Tree.expandedIcon"));
			toolBar1.add(button8);

			//---- toggleButton6 ----
			toggleButton6.setText("Toggle");
			toggleButton6.setIcon(UIManager.getIcon("Tree.leafIcon"));
			toggleButton6.setSelected(true);
			toolBar1.add(toggleButton6);

			//---- button1 ----
			button1.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/demo/icons/intellij-showWriteAccess.png")));
			button1.setEnabled(false);
			toolBar1.add(button1);
			toolBar1.addSeparator();

			//---- label7 ----
			label7.setText("Button group hover:");
			toolBar1.add(label7);

			//---- toggleButton1 ----
			toggleButton1.setIcon(UIManager.getIcon("FileView.computerIcon"));
			toggleButton1.setSelected(true);
			toolBar1.add(toggleButton1);

			//---- toggleButton2 ----
			toggleButton2.setIcon(UIManager.getIcon("FileView.computerIcon"));
			toolBar1.add(toggleButton2);

			//---- toggleButton3 ----
			toggleButton3.setIcon(UIManager.getIcon("FileView.computerIcon"));
			toolBar1.add(toggleButton3);

			//---- toggleButton4 ----
			toggleButton4.setIcon(UIManager.getIcon("FileView.computerIcon"));
			toolBar1.add(toggleButton4);
		}
		add(toolBar1, "cell 1 10 4 1,growx");

		//---- splitPaneLabel ----
		splitPaneLabel.setText("JSplitPane:");
		add(splitPaneLabel, "cell 0 11");

		//======== splitPane3 ========
		{
			splitPane3.setResizeWeight(0.5);

			//======== splitPane1 ========
			{
				splitPane1.setResizeWeight(0.5);

				//======== panel10 ========
				{
					panel10.setBackground(new Color(0xd9a343));
					panel10.setLayout(new BorderLayout());

					//---- label1 ----
					label1.setText("LEFT");
					label1.setHorizontalAlignment(SwingConstants.CENTER);
					label1.setForeground(Color.white);
					panel10.add(label1, BorderLayout.CENTER);
				}
				splitPane1.setLeftComponent(panel10);

				//======== panel11 ========
				{
					panel11.setBackground(new Color(0x62b543));
					panel11.setLayout(new BorderLayout());

					//---- label2 ----
					label2.setText("RIGHT");
					label2.setHorizontalAlignment(SwingConstants.CENTER);
					label2.setForeground(Color.white);
					panel11.add(label2, BorderLayout.CENTER);
				}
				splitPane1.setRightComponent(panel11);
			}
			splitPane3.setLeftComponent(splitPane1);

			//======== splitPane2 ========
			{
				splitPane2.setOrientation(JSplitPane.VERTICAL_SPLIT);
				splitPane2.setResizeWeight(0.5);

				//======== panel12 ========
				{
					panel12.setBackground(new Color(0xf26522));
					panel12.setLayout(new BorderLayout());

					//---- label5 ----
					label5.setText("TOP");
					label5.setHorizontalAlignment(SwingConstants.CENTER);
					label5.setForeground(Color.white);
					panel12.add(label5, BorderLayout.CENTER);
				}
				splitPane2.setTopComponent(panel12);

				//======== panel13 ========
				{
					panel13.setBackground(new Color(0x40b6e0));
					panel13.setLayout(new BorderLayout());

					//---- label6 ----
					label6.setText("BOTTOM");
					label6.setHorizontalAlignment(SwingConstants.CENTER);
					label6.setForeground(Color.white);
					panel13.add(label6, BorderLayout.CENTER);
				}
				splitPane2.setBottomComponent(panel13);
			}
			splitPane3.setRightComponent(splitPane2);
		}
		add(splitPane3, "cell 1 11 4 1,grow");

		//---- panelLabel ----
		panelLabel.setText("JPanel:");
		add(panelLabel, "cell 0 12");

		//======== panel5 ========
		{
			panel5.putClientProperty(FlatClientProperties.STYLE, "arc: 16; background: darken($Panel.background,5%)");
			panel5.setLayout(new BorderLayout());

			//---- label9 ----
			label9.setText("rounded background");
			label9.setHorizontalAlignment(SwingConstants.CENTER);
			panel5.add(label9, BorderLayout.CENTER);
		}
		add(panel5, "cell 1 12 4 1,growy,width 150");

		//======== panel4 ========
		{
			panel4.putClientProperty(FlatClientProperties.STYLE, "border: 1,1,1,1,@disabledForeground,1,16; background: darken($Panel.background,5%)");
			panel4.setLayout(new BorderLayout());

			//---- label8 ----
			label8.setText("rounded border");
			label8.setHorizontalAlignment(SwingConstants.CENTER);
			panel4.add(label8, BorderLayout.CENTER);
		}
		add(panel4, "cell 1 12 4 1,growy,width 150");

		//---- labelLabel ----
		labelLabel.setText("JLabel:");
		add(labelLabel, "cell 0 13");

		//---- label13 ----
		label13.setText("rounded background");
		label13.putClientProperty(FlatClientProperties.STYLE, "arc: 999; border: 2,10,2,10");
		label13.setBackground(new Color(0xb8e4f3));
		label13.setForeground(new Color(0x135b76));
		add(label13, "cell 1 13 4 1");

		//---- label10 ----
		label10.setText("rounded border");
		label10.putClientProperty(FlatClientProperties.STYLE, "arc: 999; border: 2,10,2,10,#135b76");
		label10.setBackground(new Color(0xb8e4f3));
		label10.setForeground(new Color(0x135b76));
		add(label10, "cell 1 13 4 1");

		//---- buttonGroup1 ----
		ButtonGroup buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(toggleButton1);
		buttonGroup1.add(toggleButton2);
		buttonGroup1.add(toggleButton3);
		buttonGroup1.add(toggleButton4);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents

		if( FlatLafDemo.screenshotsMode ) {
			Component[] components = {
				indeterminateCheckBox,
				toolTipLabel, toolTip1, toolTip2,
				toolBarLabel, toolBar1, toolBar2,
				splitPaneLabel, splitPane3,
			};

			for( Component c : components )
				c.setVisible( false );
		}
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JProgressBar progressBar3;
	private JProgressBar progressBar4;
	private JSlider slider3;
	private JProgressBar progressBar1;
	private JProgressBar progressBar2;
	private JCheckBox indeterminateCheckBox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
