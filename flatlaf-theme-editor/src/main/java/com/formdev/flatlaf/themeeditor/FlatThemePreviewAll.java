/*
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

package com.formdev.flatlaf.themeeditor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.util.function.Function;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.components.*;
import com.formdev.flatlaf.ui.FlatTabbedPaneUI;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
class FlatThemePreviewAll
	extends JPanel
{
	private final FlatThemePreview preview;

	FlatThemePreviewAll( FlatThemePreview preview ) {
		this.preview = preview;

		initComponents();

		tabbedPane1.uiDefaultsGetter = preview::getUIDefaultProperty;
		tabbedPane1.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT );
		tabbedPane1.addTab( "Tab 1", null );
		tabbedPane1.addTab( "Tab 2", null );
		tabbedPane1.addTab( "Tab 3", null );
		tabbedPane1.addTab( "Tab 4", null );
		tabbedPane1.addTab( "Tab 5", null );
		tabbedPane1.addTab( "Tab 6", null );
		tabbedPane1.addTab( "Tab 7", null );
		tabbedPane1.addTab( "Tab 8", null );

		list1.setSelectedIndex( 1 );
		tree1.setSelectionRow( 1 );
		table1.setRowSelectionInterval( 1, 1 );
		table1.setRowSorter( new TableRowSorter<>( table1.getModel() ) );
		table1.getRowSorter().toggleSortOrder( 0 );
		table1.uiDefaultsGetter = preview::getUIDefaultProperty;

		EventQueue.invokeLater( () -> {
			int width = desktopPane1.getWidth();
			int height = desktopPane1.getHeight() / 2;
			internalFrame1.setBounds( 0, 0, width, height );
			internalFrame2.setBounds( 0, height, width, height );

			try {
				internalFrame1.setSelected( true );
			} catch( PropertyVetoException ex ) {
				// ignore
			}
		} );
	}

	private void enabledChanged() {
		preview.runWithUIDefaultsGetter( () -> {
			enableDisable( this, enabledCheckBox.isSelected() );
		} );
	}

	private void enableDisable( Component comp, boolean enabled ) {
		if( comp != enabledCheckBox && comp != menu2 )
			comp.setEnabled( enabled );

		if( !(comp instanceof Container) || comp instanceof JInternalFrame )
			return;

		for( Component c : ((Container)comp).getComponents() ) {
			if( c instanceof JScrollPane )
				c = ((JScrollPane)c).getViewport().getView();

			// make sure that background is updated correctly in BasicTextUI.updateBackground()
			if( c instanceof JTextPane )
				c.setBackground( UIManager.getColor( "TextPane.background" ) );
			else if( c instanceof JEditorPane )
				c.setBackground( UIManager.getColor( "EditorPane.background" ) );

			enableDisable( c, enabled );
		}

		if( comp instanceof JMenu ) {
			JMenu menu = (JMenu) comp;
			int count = menu.getMenuComponentCount();
			for( int i = 0; i < count; i++ )
				enableDisable( menu.getMenuComponent( i ), enabled );
		}
	}

	private void changeProgress() {
		int value = slider3.getValue();
		progressBar1.setValue( value );
		progressBar2.setValue( value );
	}

	private Object toolbarCons;

	@Override
	protected void addImpl( Component comp, Object constraints, int index ) {
		// if floating toolbar window is closed, then place toolbar at original location
		if( comp == toolBar1 ) {
			if( toolbarCons == null )
				toolbarCons = constraints;
			else if( comp.getParent() == null && toolbarCons != null )
				constraints = toolbarCons;
		}

		super.addImpl( comp, constraints, index );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		enabledCheckBox = new JCheckBox();
		labelLabel = new JLabel();
		label1 = new JLabel();
		flatButton1 = new FlatButton();
		buttonLabel = new JLabel();
		button1 = new JButton();
		testDefaultButton1 = new FlatThemePreviewAll.PreviewDefaultButton();
		helpButton = new FlatButton();
		hSpacer2 = new JPanel(null);
		toggleButtonLabel = new JLabel();
		toggleButton1 = new JToggleButton();
		toggleButton3 = new JToggleButton();
		hSpacer1 = new JPanel(null);
		checkBoxLabel = new JLabel();
		checkBox1 = new JCheckBox();
		checkBox3 = new JCheckBox();
		hSpacer3 = new JPanel(null);
		radioButtonLabel = new JLabel();
		radioButton1 = new JRadioButton();
		radioButton3 = new JRadioButton();
		hSpacer4 = new JPanel(null);
		comboBoxLabel = new JLabel();
		comboBox1 = new FlatComboBox<>();
		comboBox3 = new JComboBox<>();
		spinnerLabel = new JLabel();
		spinner1 = new JSpinner();
		textFieldLabel = new JLabel();
		textField1 = new FlatTextField();
		formattedTextField1 = new FlatFormattedTextField();
		passwordField1 = new FlatPasswordField();
		textAreaLabel = new JLabel();
		scrollPane1 = new JScrollPane();
		textArea1 = new JTextArea();
		scrollPane5 = new JScrollPane();
		editorPane1 = new JEditorPane();
		scrollPane9 = new JScrollPane();
		textPane1 = new JTextPane();
		menuBarLabel = new JLabel();
		menuBar1 = new JMenuBar();
		menu2 = new JMenu();
		menuItem3 = new JMenuItem();
		menuItem4 = new JMenuItem();
		checkBoxMenuItem2 = new JCheckBoxMenuItem();
		checkBoxMenuItem3 = new JCheckBoxMenuItem();
		radioButtonMenuItem4 = new JRadioButtonMenuItem();
		radioButtonMenuItem5 = new JRadioButtonMenuItem();
		menu4 = new JMenu();
		menuItem6 = new JMenuItem();
		menu5 = new JMenu();
		menuItem7 = new JMenuItem();
		menu3 = new JMenu();
		menuItem5 = new JMenuItem();
		menuItem8 = new JMenuItem();
		menuItem9 = new JMenuItem();
		scrollBarLabel = new JLabel();
		scrollBar1 = new JScrollBar();
		scrollBar5 = new FlatScrollBar();
		separatorLabel = new JLabel();
		separator1 = new JSeparator();
		sliderLabel = new JLabel();
		slider1 = new JSlider();
		slider3 = new JSlider();
		progressBarLabel = new JLabel();
		progressBar1 = new FlatProgressBar();
		progressBar2 = new FlatProgressBar();
		toolTipLabel = new JLabel();
		toolTip1 = new JToolTip();
		toolBarLabel = new JLabel();
		toolBar1 = new JToolBar();
		button4 = new JButton();
		button6 = new JButton();
		button7 = new JToggleButton();
		button8 = new JToggleButton();
		tabbedPaneLabel = new JLabel();
		tabbedPane1 = new FlatThemePreviewAll.PreviewTabbedPane();
		listTreeLabel = new JLabel();
		splitPane1 = new JSplitPane();
		scrollPane2 = new JScrollPane();
		list1 = new JList<>();
		scrollPane3 = new JScrollPane();
		tree1 = new JTree();
		tableLabel = new JLabel();
		scrollPane4 = new JScrollPane();
		table1 = new FlatThemePreviewAll.PreviewTable();
		internalFrameLabel = new JLabel();
		desktopPane1 = new JDesktopPane();
		internalFrame1 = new JInternalFrame();
		internalFrame2 = new JInternalFrame();

		//======== this ========
		setLayout(new MigLayout(
			"insets dialog,hidemode 3",
			// columns
			"[fill]" +
			"[130,fill]",
			// rows
			"[]unrel" +
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
			"[]" +
			"[]" +
			"[100,fill]" +
			"[grow]"));

		//---- enabledCheckBox ----
		enabledCheckBox.setText("Enabled");
		enabledCheckBox.setSelected(true);
		enabledCheckBox.addActionListener(e -> enabledChanged());
		add(enabledCheckBox, "cell 0 0 2 1,alignx left,growx 0");

		//---- labelLabel ----
		labelLabel.setText("JLabel:");
		add(labelLabel, "cell 0 1");

		//---- label1 ----
		label1.setText("Some Text");
		add(label1, "cell 1 1");

		//---- flatButton1 ----
		flatButton1.setText("Help");
		flatButton1.setButtonType(FlatButton.ButtonType.help);
		flatButton1.setVisible(false);
		add(flatButton1, "cell 1 1,alignx right,growx 0");

		//---- buttonLabel ----
		buttonLabel.setText("JButton:");
		add(buttonLabel, "cell 0 2");

		//---- button1 ----
		button1.setText("OK");
		add(button1, "cell 1 2,alignx left,growx 0");

		//---- testDefaultButton1 ----
		testDefaultButton1.setText("Default");
		add(testDefaultButton1, "cell 1 2");

		//---- helpButton ----
		helpButton.setButtonType(FlatButton.ButtonType.help);
		add(helpButton, "cell 1 2");
		add(hSpacer2, "cell 1 2");

		//---- toggleButtonLabel ----
		toggleButtonLabel.setText("JToggleButton:");
		add(toggleButtonLabel, "cell 0 3");

		//---- toggleButton1 ----
		toggleButton1.setText("Unselected");
		add(toggleButton1, "cell 1 3,alignx left,growx 0");

		//---- toggleButton3 ----
		toggleButton3.setText("Selected");
		toggleButton3.setSelected(true);
		add(toggleButton3, "cell 1 3");
		add(hSpacer1, "cell 1 3");

		//---- checkBoxLabel ----
		checkBoxLabel.setText("JCheckBox");
		add(checkBoxLabel, "cell 0 4");

		//---- checkBox1 ----
		checkBox1.setText("Unselected");
		add(checkBox1, "cell 1 4,alignx left,growx 0");

		//---- checkBox3 ----
		checkBox3.setText("Selected");
		checkBox3.setSelected(true);
		add(checkBox3, "cell 1 4,alignx left,growx 0");
		add(hSpacer3, "cell 1 4");

		//---- radioButtonLabel ----
		radioButtonLabel.setText("JRadioButton:");
		add(radioButtonLabel, "cell 0 5");

		//---- radioButton1 ----
		radioButton1.setText("Unselected");
		add(radioButton1, "cell 1 5,alignx left,growx 0");

		//---- radioButton3 ----
		radioButton3.setText("Selected");
		radioButton3.setSelected(true);
		add(radioButton3, "cell 1 5,alignx left,growx 0");
		add(hSpacer4, "cell 1 5");

		//---- comboBoxLabel ----
		comboBoxLabel.setText("JComboBox:");
		add(comboBoxLabel, "cell 0 6");

		//---- comboBox1 ----
		comboBox1.setEditable(true);
		comboBox1.setModel(new DefaultComboBoxModel<>(new String[] {
			"Editable",
			"a",
			"bb",
			"ccc",
			"dd",
			"e",
			"ff",
			"ggg",
			"hh",
			"i",
			"jj",
			"kkk"
		}));
		comboBox1.setMaximumRowCount(6);
		comboBox1.setPlaceholderText("placeholder text");
		add(comboBox1, "cell 1 6,width 50");

		//---- comboBox3 ----
		comboBox3.setModel(new DefaultComboBoxModel<>(new String[] {
			"Not edit",
			"a",
			"bb",
			"ccc",
			"dd",
			"e",
			"ff",
			"ggg",
			"hh",
			"i",
			"jj",
			"kkk"
		}));
		comboBox3.setMaximumRowCount(6);
		add(comboBox3, "cell 1 6,width 50");

		//---- spinnerLabel ----
		spinnerLabel.setText("JSpinner:");
		add(spinnerLabel, "cell 0 7");
		add(spinner1, "cell 1 7");

		//---- textFieldLabel ----
		textFieldLabel.setText("<html>JTextField:<br>JFormattedTextF.:<br>JPasswordField:</html>");
		add(textFieldLabel, "cell 0 8 1 2");

		//---- textField1 ----
		textField1.setText("Some Text");
		textField1.setPlaceholderText("placeholder text");
		add(textField1, "cell 1 8");

		//---- formattedTextField1 ----
		formattedTextField1.setText("Some Text");
		formattedTextField1.setPlaceholderText("placeholder text");
		add(formattedTextField1, "cell 1 9,width 50");

		//---- passwordField1 ----
		passwordField1.setText("Some Text");
		passwordField1.setPlaceholderText("placeholder text");
		add(passwordField1, "cell 1 9,width 50");

		//---- textAreaLabel ----
		textAreaLabel.setText("<html>JTextArea:<br><br>JEditorPane:<br>JTextPane:</html>");
		add(textAreaLabel, "cell 0 10 1 2");

		//======== scrollPane1 ========
		{
			scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- textArea1 ----
			textArea1.setText("Some Text");
			textArea1.setRows(2);
			scrollPane1.setViewportView(textArea1);
		}
		add(scrollPane1, "cell 1 10");

		//======== scrollPane5 ========
		{
			scrollPane5.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane5.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- editorPane1 ----
			editorPane1.setText("Some Text");
			scrollPane5.setViewportView(editorPane1);
		}
		add(scrollPane5, "cell 1 11,width 50");

		//======== scrollPane9 ========
		{
			scrollPane9.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane9.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- textPane1 ----
			textPane1.setText("Some Text");
			scrollPane9.setViewportView(textPane1);
		}
		add(scrollPane9, "cell 1 11,width 50");

		//---- menuBarLabel ----
		menuBarLabel.setText("JMenuBar:");
		add(menuBarLabel, "cell 0 12");

		//======== menuBar1 ========
		{

			//======== menu2 ========
			{
				menu2.setText("JMenu");

				//---- menuItem3 ----
				menuItem3.setText("JMenuItem");
				menu2.add(menuItem3);

				//---- menuItem4 ----
				menuItem4.setText("JMenuItem");
				menu2.add(menuItem4);
				menu2.addSeparator();

				//---- checkBoxMenuItem2 ----
				checkBoxMenuItem2.setText("JCheckBoxMenuItem");
				checkBoxMenuItem2.setSelected(true);
				menu2.add(checkBoxMenuItem2);

				//---- checkBoxMenuItem3 ----
				checkBoxMenuItem3.setText("JCheckBoxMenuItem");
				menu2.add(checkBoxMenuItem3);
				menu2.addSeparator();

				//---- radioButtonMenuItem4 ----
				radioButtonMenuItem4.setText("JRadioButtonMenuItem");
				radioButtonMenuItem4.setSelected(true);
				menu2.add(radioButtonMenuItem4);

				//---- radioButtonMenuItem5 ----
				radioButtonMenuItem5.setText("JRadioButtonMenuItem");
				menu2.add(radioButtonMenuItem5);
				menu2.addSeparator();

				//======== menu4 ========
				{
					menu4.setText("JMenu");

					//---- menuItem6 ----
					menuItem6.setText("JMenuItem");
					menu4.add(menuItem6);
				}
				menu2.add(menu4);

				//======== menu5 ========
				{
					menu5.setText("JMenu");

					//---- menuItem7 ----
					menuItem7.setText("JMenuItem");
					menu5.add(menuItem7);
				}
				menu2.add(menu5);
			}
			menuBar1.add(menu2);

			//======== menu3 ========
			{
				menu3.setText("JMenu");

				//---- menuItem5 ----
				menuItem5.setText("JMenuItem");
				menu3.add(menuItem5);

				//---- menuItem8 ----
				menuItem8.setText("JMenuItem");
				menu3.add(menuItem8);

				//---- menuItem9 ----
				menuItem9.setText("JMenuItem");
				menu3.add(menuItem9);
			}
			menuBar1.add(menu3);
		}
		add(menuBar1, "cell 1 12");

		//---- scrollBarLabel ----
		scrollBarLabel.setText("JScrollBar:");
		add(scrollBarLabel, "cell 0 13");

		//---- scrollBar1 ----
		scrollBar1.setOrientation(Adjustable.HORIZONTAL);
		add(scrollBar1, "cell 1 13");

		//---- scrollBar5 ----
		scrollBar5.setOrientation(Adjustable.HORIZONTAL);
		scrollBar5.setShowButtons(true);
		add(scrollBar5, "cell 1 14");

		//---- separatorLabel ----
		separatorLabel.setText("JSeparator:");
		add(separatorLabel, "cell 0 15");
		add(separator1, "cell 1 15");

		//---- sliderLabel ----
		sliderLabel.setText("JSlider:");
		add(sliderLabel, "cell 0 16");

		//---- slider1 ----
		slider1.setValue(30);
		add(slider1, "cell 1 16");

		//---- slider3 ----
		slider3.setMinorTickSpacing(10);
		slider3.setPaintTicks(true);
		slider3.setMajorTickSpacing(50);
		slider3.setPaintLabels(true);
		slider3.setValue(30);
		slider3.addChangeListener(e -> changeProgress());
		add(slider3, "cell 1 17");

		//---- progressBarLabel ----
		progressBarLabel.setText("JProgressBar:");
		add(progressBarLabel, "cell 0 18");

		//---- progressBar1 ----
		progressBar1.setValue(60);
		add(progressBar1, "cell 1 18");

		//---- progressBar2 ----
		progressBar2.setValue(50);
		progressBar2.setStringPainted(true);
		add(progressBar2, "cell 1 19");

		//---- toolTipLabel ----
		toolTipLabel.setText("JToolTip:");
		add(toolTipLabel, "cell 0 20");

		//---- toolTip1 ----
		toolTip1.setTipText("Some text in tool tip.");
		add(toolTip1, "cell 1 20,alignx left,growx 0");

		//---- toolBarLabel ----
		toolBarLabel.setText("JToolBar:");
		add(toolBarLabel, "cell 0 21");

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

			//---- button8 ----
			button8.setIcon(UIManager.getIcon("Tree.leafIcon"));
			button8.setSelected(true);
			toolBar1.add(button8);
		}
		add(toolBar1, "cell 1 21");

		//---- tabbedPaneLabel ----
		tabbedPaneLabel.setText("JTabbedPane:");
		add(tabbedPaneLabel, "cell 0 22");
		add(tabbedPane1, "cell 1 22");

		//---- listTreeLabel ----
		listTreeLabel.setText("<html>JList / JTree:<br>JSplitPane:</html>");
		add(listTreeLabel, "cell 0 23,aligny top,growy 0");

		//======== splitPane1 ========
		{
			splitPane1.setResizeWeight(0.5);

			//======== scrollPane2 ========
			{
				scrollPane2.setPreferredSize(new Dimension(50, 50));

				//---- list1 ----
				list1.setModel(new AbstractListModel<String>() {
					String[] values = {
						"Item 1",
						"Item 2",
						"Item 3"
					};
					@Override
					public int getSize() { return values.length; }
					@Override
					public String getElementAt(int i) { return values[i]; }
				});
				scrollPane2.setViewportView(list1);
			}
			splitPane1.setLeftComponent(scrollPane2);

			//======== scrollPane3 ========
			{
				scrollPane3.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				scrollPane3.setPreferredSize(new Dimension(50, 50));

				//---- tree1 ----
				tree1.setModel(new DefaultTreeModel(
					new DefaultMutableTreeNode("Item 1") {
						{
							add(new DefaultMutableTreeNode("Item 2"));
							add(new DefaultMutableTreeNode("Item 3"));
						}
					}));
				scrollPane3.setViewportView(tree1);
			}
			splitPane1.setRightComponent(scrollPane3);
		}
		add(splitPane1, "cell 1 23,height 50");

		//---- tableLabel ----
		tableLabel.setText("JTable:");
		add(tableLabel, "cell 0 24");

		//======== scrollPane4 ========
		{

			//---- table1 ----
			table1.setModel(new DefaultTableModel(
				new Object[][] {
					{"Item 1a", "Item 1b"},
					{"Item 2a", "Item 2b"},
				},
				new String[] {
					"Column 1", "Column 2"
				}
			));
			scrollPane4.setViewportView(table1);
		}
		add(scrollPane4, "cell 1 24,height 70");

		//---- internalFrameLabel ----
		internalFrameLabel.setText("<html>JDesktopPane:<br>JInternalFrame:</html>");
		add(internalFrameLabel, "cell 0 25,aligny top,growy 0");

		//======== desktopPane1 ========
		{

			//======== internalFrame1 ========
			{
				internalFrame1.setVisible(true);
				internalFrame1.setTitle("Active");
				internalFrame1.setClosable(true);
				internalFrame1.setMaximizable(true);
				internalFrame1.setIconifiable(true);
				internalFrame1.setResizable(true);
				Container internalFrame1ContentPane = internalFrame1.getContentPane();
				internalFrame1ContentPane.setLayout(new BorderLayout());
			}
			desktopPane1.add(internalFrame1, JLayeredPane.DEFAULT_LAYER);
			internalFrame1.setBounds(new Rectangle(new Point(5, 5), internalFrame1.getPreferredSize()));

			//======== internalFrame2 ========
			{
				internalFrame2.setVisible(true);
				internalFrame2.setClosable(true);
				internalFrame2.setIconifiable(true);
				internalFrame2.setMaximizable(true);
				internalFrame2.setResizable(true);
				internalFrame2.setTitle("Inactive");
				Container internalFrame2ContentPane = internalFrame2.getContentPane();
				internalFrame2ContentPane.setLayout(new BorderLayout());
			}
			desktopPane1.add(internalFrame2, JLayeredPane.DEFAULT_LAYER);
			internalFrame2.setBounds(new Rectangle(new Point(5, 50), internalFrame2.getPreferredSize()));
		}
		add(desktopPane1, "cell 1 25");

		//---- buttonGroup1 ----
		ButtonGroup buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(radioButton1);
		buttonGroup1.add(radioButton3);

		//---- buttonGroup2 ----
		ButtonGroup buttonGroup2 = new ButtonGroup();
		buttonGroup2.add(radioButtonMenuItem4);
		buttonGroup2.add(radioButtonMenuItem5);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JCheckBox enabledCheckBox;
	private JLabel labelLabel;
	private JLabel label1;
	private FlatButton flatButton1;
	private JLabel buttonLabel;
	private JButton button1;
	private FlatThemePreviewAll.PreviewDefaultButton testDefaultButton1;
	private FlatButton helpButton;
	private JPanel hSpacer2;
	private JLabel toggleButtonLabel;
	private JToggleButton toggleButton1;
	private JToggleButton toggleButton3;
	private JPanel hSpacer1;
	private JLabel checkBoxLabel;
	private JCheckBox checkBox1;
	private JCheckBox checkBox3;
	private JPanel hSpacer3;
	private JLabel radioButtonLabel;
	private JRadioButton radioButton1;
	private JRadioButton radioButton3;
	private JPanel hSpacer4;
	private JLabel comboBoxLabel;
	private FlatComboBox<String> comboBox1;
	private JComboBox<String> comboBox3;
	private JLabel spinnerLabel;
	private JSpinner spinner1;
	private JLabel textFieldLabel;
	private FlatTextField textField1;
	private FlatFormattedTextField formattedTextField1;
	private FlatPasswordField passwordField1;
	private JLabel textAreaLabel;
	private JScrollPane scrollPane1;
	private JTextArea textArea1;
	private JScrollPane scrollPane5;
	private JEditorPane editorPane1;
	private JScrollPane scrollPane9;
	private JTextPane textPane1;
	private JLabel menuBarLabel;
	private JMenuBar menuBar1;
	private JMenu menu2;
	private JMenuItem menuItem3;
	private JMenuItem menuItem4;
	private JCheckBoxMenuItem checkBoxMenuItem2;
	private JCheckBoxMenuItem checkBoxMenuItem3;
	private JRadioButtonMenuItem radioButtonMenuItem4;
	private JRadioButtonMenuItem radioButtonMenuItem5;
	private JMenu menu4;
	private JMenuItem menuItem6;
	private JMenu menu5;
	private JMenuItem menuItem7;
	private JMenu menu3;
	private JMenuItem menuItem5;
	private JMenuItem menuItem8;
	private JMenuItem menuItem9;
	private JLabel scrollBarLabel;
	private JScrollBar scrollBar1;
	private FlatScrollBar scrollBar5;
	private JLabel separatorLabel;
	private JSeparator separator1;
	private JLabel sliderLabel;
	private JSlider slider1;
	private JSlider slider3;
	private JLabel progressBarLabel;
	private FlatProgressBar progressBar1;
	private FlatProgressBar progressBar2;
	private JLabel toolTipLabel;
	private JToolTip toolTip1;
	private JLabel toolBarLabel;
	private JToolBar toolBar1;
	private JButton button4;
	private JButton button6;
	private JToggleButton button7;
	private JToggleButton button8;
	private JLabel tabbedPaneLabel;
	private FlatThemePreviewAll.PreviewTabbedPane tabbedPane1;
	private JLabel listTreeLabel;
	private JSplitPane splitPane1;
	private JScrollPane scrollPane2;
	private JList<String> list1;
	private JScrollPane scrollPane3;
	private JTree tree1;
	private JLabel tableLabel;
	private JScrollPane scrollPane4;
	private FlatThemePreviewAll.PreviewTable table1;
	private JLabel internalFrameLabel;
	private JDesktopPane desktopPane1;
	private JInternalFrame internalFrame1;
	private JInternalFrame internalFrame2;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class PreviewDefaultButton -----------------------------------------

	private static class PreviewDefaultButton
		extends JButton
	{
		@Override
		public boolean isDefaultButton() {
			return true;
		}
	}

	//---- class PreviewTabbedPane --------------------------------------------

	private static class PreviewTabbedPane
		extends JTabbedPane
	{
		Function<Object, Object> uiDefaultsGetter;

		@Override
		public void updateUI() {
			setUI( new PreviewFlatTabbedPaneUI( uiDefaultsGetter ) );
		}
	}

	//---- class PreviewFlatTabbedPaneUI --------------------------------------

	private static class PreviewFlatTabbedPaneUI
		extends FlatTabbedPaneUI
	{
		private final Function<Object, Object> uiDefaultsGetter;

		PreviewFlatTabbedPaneUI( Function<Object, Object> uiDefaultsGetter ) {
			this.uiDefaultsGetter = uiDefaultsGetter;
		}

		@Override
		protected JButton createMoreTabsButton() {
			return new PreviewFlatMoreTabsButton();
		}

		//---- class PreviewFlatMoreTabsButton --------------------------------

		protected class PreviewFlatMoreTabsButton
			extends FlatMoreTabsButton
		{
			@Override
			public void actionPerformed( ActionEvent e ) {
				// needed for "more tabs" popup creation
				FlatLaf.runWithUIDefaultsGetter( uiDefaultsGetter, () -> {
					super.actionPerformed( e );
				} );
			}
		}
	}

	//---- class PreviewTable -------------------------------------------------

	private static class PreviewTable
		extends JTable
	{
		Function<Object, Object> uiDefaultsGetter;

		@Override
		public void paint( Graphics g ) {
			// needed for DefaultTableCellRenderer
			FlatLaf.runWithUIDefaultsGetter( uiDefaultsGetter, () -> {
				super.paint( g );
			} );
		}
	}
}
