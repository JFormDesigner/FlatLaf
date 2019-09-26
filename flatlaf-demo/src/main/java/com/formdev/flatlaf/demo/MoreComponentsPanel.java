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
		JSeparator separator2 = new JSeparator();
		JSlider slider2 = new JSlider();
		JSlider slider4 = new JSlider();
		JScrollPane scrollPane14 = new JScrollPane();
		progressBar3 = new JProgressBar();
		progressBar4 = new JProgressBar();
		JToolBar toolBar2 = new JToolBar();
		JButton button9 = new JButton();
		JButton button10 = new JButton();
		JButton button11 = new JButton();
		JToggleButton toggleButton7 = new JToggleButton();
		JLabel scrollBarLabel = new JLabel();
		JScrollBar scrollBar1 = new JScrollBar();
		JScrollBar scrollBar4 = new JScrollBar();
		JLabel separatorLabel = new JLabel();
		JSeparator separator1 = new JSeparator();
		JPanel panel2 = new JPanel();
		JLabel sliderLabel = new JLabel();
		JSlider slider1 = new JSlider();
		JSlider slider6 = new JSlider();
		JSlider slider3 = new JSlider();
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

		//======== this ========
		setLayout(new MigLayout(
			"hidemode 3",
			// columns
			"[]" +
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
		add(scrollPane13, "cell 1 0,grow,width 70,height 70");
		add(scrollBar2, "cell 2 0 1 4,growy");

		//---- scrollBar3 ----
		scrollBar3.setEnabled(false);
		add(scrollBar3, "cell 2 0 1 4,growy");

		//---- separator2 ----
		separator2.setOrientation(SwingConstants.VERTICAL);
		add(separator2, "cell 2 0 1 4,growy");

		//---- slider2 ----
		slider2.setOrientation(SwingConstants.VERTICAL);
		slider2.setValue(30);
		add(slider2, "cell 2 0 1 4,growy");

		//---- slider4 ----
		slider4.setMinorTickSpacing(10);
		slider4.setPaintTicks(true);
		slider4.setMajorTickSpacing(50);
		slider4.setPaintLabels(true);
		slider4.setOrientation(SwingConstants.VERTICAL);
		slider4.setValue(30);
		add(slider4, "cell 2 0 1 4,growy");
		add(scrollPane14, "cell 3 0,grow");

		//---- progressBar3 ----
		progressBar3.setOrientation(SwingConstants.VERTICAL);
		progressBar3.setValue(50);
		add(progressBar3, "cell 4 0 1 4,growy");

		//---- progressBar4 ----
		progressBar4.setOrientation(SwingConstants.VERTICAL);
		progressBar4.setValue(55);
		progressBar4.setStringPainted(true);
		add(progressBar4, "cell 4 0 1 4,growy");

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
		add(toolBar2, "cell 4 0 1 4,growy");

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

		//---- separatorLabel ----
		separatorLabel.setText("JSeparator:");
		add(separatorLabel, "cell 0 3");
		add(separator1, "cell 1 3,growx");

		//======== panel2 ========
		{
			panel2.setBorder(new TitledBorder("TitledBorder"));
			panel2.setLayout(new FlowLayout());
		}
		add(panel2, "cell 3 3,grow");

		//---- sliderLabel ----
		sliderLabel.setText("JSlider:");
		add(sliderLabel, "cell 0 4");

		//---- slider1 ----
		slider1.setValue(30);
		add(slider1, "cell 1 4 3 1,aligny top,grow 100 0");

		//---- slider6 ----
		slider6.setEnabled(false);
		slider6.setValue(30);
		add(slider6, "cell 1 4 3 1,aligny top,growy 0");

		//---- slider3 ----
		slider3.setMinorTickSpacing(10);
		slider3.setPaintTicks(true);
		slider3.setMajorTickSpacing(50);
		slider3.setPaintLabels(true);
		slider3.setValue(30);
		add(slider3, "cell 1 5 3 1,aligny top,grow 100 0");

		//---- slider5 ----
		slider5.setMinorTickSpacing(10);
		slider5.setPaintTicks(true);
		slider5.setMajorTickSpacing(50);
		slider5.setPaintLabels(true);
		slider5.setEnabled(false);
		slider5.setValue(30);
		add(slider5, "cell 1 5 3 1,aligny top,growy 0");

		//---- progressBarLabel ----
		progressBarLabel.setText("JProgressBar:");
		add(progressBarLabel, "cell 0 6");

		//---- progressBar1 ----
		progressBar1.setValue(50);
		add(progressBar1, "cell 1 6 3 1,growx");

		//---- progressBar2 ----
		progressBar2.setStringPainted(true);
		progressBar2.setValue(55);
		add(progressBar2, "cell 1 6 3 1,growx");

		//---- indeterminateCheckBox ----
		indeterminateCheckBox.setText("indeterminate");
		indeterminateCheckBox.addActionListener(e -> indeterminateCheckBoxActionPerformed());
		add(indeterminateCheckBox, "cell 4 6");

		//---- toolTipLabel ----
		toolTipLabel.setText("JToolTip:");
		add(toolTipLabel, "cell 0 7");

		//---- toolTip1 ----
		toolTip1.setTipText("Some text in tool tip.");
		add(toolTip1, "cell 1 7 3 1");

		//---- toolTip2 ----
		toolTip2.setTipText("Tool tip with\nmultiple\nlines.");
		add(toolTip2, "cell 1 7 3 1");

		//---- toolBarLabel ----
		toolBarLabel.setText("JToolBar:");
		add(toolBarLabel, "cell 0 8");

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
		}
		add(toolBar1, "cell 1 8 3 1,growx");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JProgressBar progressBar3;
	private JProgressBar progressBar4;
	private JProgressBar progressBar1;
	private JProgressBar progressBar2;
	private JCheckBox indeterminateCheckBox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
