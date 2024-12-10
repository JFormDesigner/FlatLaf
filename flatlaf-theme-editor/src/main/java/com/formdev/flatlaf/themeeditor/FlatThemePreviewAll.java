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
import java.awt.event.KeyEvent;
import java.beans.Beans;
import java.beans.PropertyVetoException;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.*;
import com.formdev.flatlaf.icons.FlatSearchWithHistoryIcon;
import com.formdev.flatlaf.ui.FlatTabbedPaneUI;
import com.formdev.flatlaf.ui.FlatUIUtils;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
class FlatThemePreviewAll
	extends JPanel
{
	private static final String KEY_ENABLED = "preview.enabled";
	private static final String KEY_EDITABLE = "preview.editable";
	private static final String KEY_FOCUSED = "preview.focused";
	private static final String KEY_MENU_UNDERLINE_SELECTION = "preview.menuUnderlineSelection";

	private final FlatThemePreview preview;

	FlatThemePreviewAll( FlatThemePreview preview ) {
		this.preview = preview;

		initComponents();

		textField2.setLeadingComponent( new JButton( new FlatSearchWithHistoryIcon( true ) ) );

		// whole words button
		JToggleButton wordsButton = new JToggleButton( new FlatSVGIcon( "com/formdev/flatlaf/themeeditor/icons/preview/words.svg" ) );
		wordsButton.setRolloverIcon( new FlatSVGIcon( "com/formdev/flatlaf/themeeditor/icons/preview/wordsHovered.svg" ) );
		wordsButton.setSelectedIcon( new FlatSVGIcon( "com/formdev/flatlaf/themeeditor/icons/preview/wordsSelected.svg" ) );
		wordsButton.setToolTipText( "Whole Words" );

		// regex button
		JToggleButton regexButton = new JToggleButton( new FlatSVGIcon( "com/formdev/flatlaf/themeeditor/icons/preview/regex.svg" ) );
		regexButton.setRolloverIcon( new FlatSVGIcon( "com/formdev/flatlaf/themeeditor/icons/preview/regexHovered.svg" ) );
		regexButton.setSelectedIcon( new FlatSVGIcon( "com/formdev/flatlaf/themeeditor/icons/preview/regexSelected.svg" ) );
		regexButton.setSelected( true );
		regexButton.setToolTipText( "Regular Expression" );

		// search toolbar
		JToolBar searchToolbar = new JToolBar();
		searchToolbar.add( wordsButton );
		searchToolbar.addSeparator();
		searchToolbar.add( regexButton );
		textField2.setTrailingComponent( searchToolbar );

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
//		list1.setSelectedIndices( new int[] { 0, 1 } );
		list1.uiDefaultsGetter = preview::getUIDefaultProperty;
		tree1.setSelectionRow( 1 );
//		tree1.setSelectionRows( new int[] { 0, 1 } );
		table1.setRowSorter( new TableRowSorter<>( table1.getModel() ) );
		table1.getRowSorter().toggleSortOrder( 0 );
		table1.setRowSelectionInterval( 1, 1 );
		table1.setColumnSelectionInterval( 0, 0 );
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

	void activated() {
		boolean enabled = preview.state.getBoolean( KEY_ENABLED, true );
		boolean editable = preview.state.getBoolean( KEY_EDITABLE, true );
		boolean focused = preview.state.getBoolean( KEY_FOCUSED, false );
		boolean menuUnderlineSelection = preview.state.getBoolean( KEY_MENU_UNDERLINE_SELECTION, false );

		if( enabled != enabledCheckBox.isSelected() ) {
			enabledCheckBox.setSelected( enabled );
			enabledChanged();
		}

		if( editable != editableCheckBox.isSelected() ) {
			editableCheckBox.setSelected( editable );
			editableChanged();
		}

		if( focused != focusedCheckBox.isSelected() ) {
			focusedCheckBox.setSelected( focused );
			focusedChanged();
		}

		if( menuUnderlineSelection != menuUnderlineSelectionButton.isSelected() ) {
			menuUnderlineSelectionButton.setSelected( menuUnderlineSelection );
			menuUnderlineSelectionChanged();
		}
	}

	private void enabledChanged() {
		boolean enabled = enabledCheckBox.isSelected();

		// disable "focused" checkbox because disabled components are not focusable
		focusedCheckBox.setEnabled( enabled );
		if( focusedCheckBox.isSelected() )
			focusedChanged();

		preview.runWithUIDefaultsGetter( () -> {
			enableDisable( this, enabled );
		} );

		FlatThemeFileEditor.putPrefsBoolean( preview.state, KEY_ENABLED, enabled, true );
	}

	private void enableDisable( Component comp, boolean enabled ) {
		if( comp instanceof JScrollPane )
			comp = ((JScrollPane)comp).getViewport().getView();

		if( comp == null || (comp instanceof JLabel && comp != label1) )
			return;

		// enable/disable component
		if( !isControlComponent( comp ) && comp != menu2 )
			comp.setEnabled( enabled );

		// enable/disable children
		if( comp instanceof JPanel || comp instanceof JToolBar || comp instanceof JMenuBar ) {
			for( Component c : ((Container)comp).getComponents() )
				enableDisable( c, enabled );
		} else if( comp instanceof JSplitPane ) {
			JSplitPane splitPane = (JSplitPane) comp;
			enableDisable( splitPane.getLeftComponent(), enabled );
			enableDisable( splitPane.getRightComponent(), enabled );
		} else if( comp instanceof JMenu ) {
			JMenu menu = (JMenu) comp;
			int count = menu.getMenuComponentCount();
			for( int i = 0; i < count; i++ )
				enableDisable( menu.getMenuComponent( i ), enabled );
		}
	}

	private void editableChanged() {
		boolean editable = editableCheckBox.isSelected();

		preview.runWithUIDefaultsGetter( () -> {
			textField1.setEditable( editable );
			textField2.setEditable( editable );
			formattedTextField1.setEditable( editable );
			passwordField1.setEditable( editable );
			textArea1.setEditable( editable );
			editorPane1.setEditable( editable );
			textPane1.setEditable( editable );
		} );

		FlatThemeFileEditor.putPrefsBoolean( preview.state, KEY_EDITABLE, editable, true );
	}

	private void focusedChanged() {
		boolean focused = focusedCheckBox.isSelected();

		Predicate<JComponent> value = focused && enabledCheckBox.isSelected()
			? value = c -> true
			: null;
		focusComponent( this, value );
		repaint();

		FlatThemeFileEditor.putPrefsBoolean( preview.state, KEY_FOCUSED, focused,false );
	}

	private void focusComponent( Component comp, Object value ) {
		if( comp instanceof JScrollPane )
			comp = ((JScrollPane)comp).getViewport().getView();

		if( comp == null )
			return;

		// focus component
		if( !isControlComponent( comp ) && comp instanceof JComponent )
			((JComponent)comp).putClientProperty( FlatClientProperties.COMPONENT_FOCUS_OWNER, value );

		// focus children
		if( comp instanceof JPanel || comp instanceof JToolBar ) {
			for( Component c : ((Container)comp).getComponents() )
				focusComponent( c, value );
		} else if( comp instanceof JSplitPane ) {
			JSplitPane splitPane = (JSplitPane) comp;
			focusComponent( splitPane.getLeftComponent(), value );
			focusComponent( splitPane.getRightComponent(), value );
		}
	}

	private boolean isControlComponent( Component c ) {
		return c == enabledCheckBox ||
			c == editableCheckBox ||
			c == focusedCheckBox ||
			c == menuUnderlineSelectionButton;
	}

	private void menuUnderlineSelectionChanged() {
		boolean menuUnderlineSelection = menuUnderlineSelectionButton.isSelected();
		UIManager.put( "MenuItem.selectionType", menuUnderlineSelection ? "underline" : null );

		FlatThemeFileEditor.putPrefsBoolean( preview.state, KEY_MENU_UNDERLINE_SELECTION, menuUnderlineSelection, false );
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
		JPanel hSpacer1 = new JPanel(null);
		enabledCheckBox = new JCheckBox();
		editableCheckBox = new JCheckBox();
		focusedCheckBox = new JCheckBox();
		JLabel labelLabel = new JLabel();
		label1 = new JLabel();
		FlatButton flatButton1 = new FlatButton();
		JLabel buttonLabel = new JLabel();
		JButton button1 = new JButton();
		FlatThemePreviewAll.PreviewDefaultButton testDefaultButton1 = new FlatThemePreviewAll.PreviewDefaultButton();
		JLabel toggleButtonLabel = new JLabel();
		JToggleButton toggleButton1 = new JToggleButton();
		JToggleButton toggleButton3 = new JToggleButton();
		JLabel checkBoxLabel = new JLabel();
		JCheckBox checkBox1 = new JCheckBox();
		JCheckBox checkBox3 = new JCheckBox();
		JLabel radioButtonLabel = new JLabel();
		JRadioButton radioButton1 = new JRadioButton();
		JRadioButton radioButton3 = new JRadioButton();
		JLabel comboBoxLabel = new JLabel();
		FlatComboBox<String> comboBox1 = new FlatComboBox<>();
		JComboBox<String> comboBox3 = new JComboBox<>();
		JLabel spinnerLabel = new JLabel();
		JSpinner spinner1 = new JSpinner();
		JLabel textFieldLabel = new JLabel();
		textField1 = new FlatTextField();
		textField2 = new FlatTextField();
		formattedTextField1 = new FlatFormattedTextField();
		passwordField1 = new FlatPasswordField();
		JLabel textAreaLabel = new JLabel();
		JScrollPane scrollPane1 = new JScrollPane();
		textArea1 = new JTextArea();
		JScrollPane scrollPane5 = new JScrollPane();
		editorPane1 = new JEditorPane();
		JScrollPane scrollPane9 = new JScrollPane();
		textPane1 = new JTextPane();
		JLabel menuBarLabel = new JLabel();
		menuUnderlineSelectionButton = new FlatToggleButton();
		JMenuBar menuBar1 = new JMenuBar();
		menu2 = new JMenu();
		JMenuItem menuItem3 = new JMenuItem();
		JMenuItem menuItem4 = new JMenuItem();
		JCheckBoxMenuItem checkBoxMenuItem2 = new JCheckBoxMenuItem();
		JCheckBoxMenuItem checkBoxMenuItem3 = new JCheckBoxMenuItem();
		JRadioButtonMenuItem radioButtonMenuItem4 = new JRadioButtonMenuItem();
		JRadioButtonMenuItem radioButtonMenuItem5 = new JRadioButtonMenuItem();
		JMenu menu4 = new JMenu();
		JMenuItem menuItem6 = new JMenuItem();
		JMenu menu5 = new JMenu();
		JMenuItem menuItem7 = new JMenuItem();
		JMenu menu3 = new JMenu();
		JMenuItem menuItem5 = new JMenuItem();
		JMenuItem menuItem8 = new JMenuItem();
		JMenuItem menuItem9 = new JMenuItem();
		JLabel scrollBarLabel = new JLabel();
		JScrollBar scrollBar1 = new JScrollBar();
		FlatScrollBar scrollBar5 = new FlatScrollBar();
		JLabel separatorLabel = new JLabel();
		JSeparator separator1 = new JSeparator();
		JLabel sliderLabel = new JLabel();
		slider1 = new JSlider();
		slider3 = new JSlider();
		JLabel progressBarLabel = new JLabel();
		progressBar1 = new FlatProgressBar();
		progressBar2 = new FlatProgressBar();
		JLabel toolTipLabel = new JLabel();
		JToolTip toolTip1 = new JToolTip();
		JLabel toolBarLabel = new JLabel();
		toolBar1 = new JToolBar();
		JButton button4 = new JButton();
		JButton button6 = new JButton();
		JToggleButton button7 = new JToggleButton();
		JToggleButton button8 = new JToggleButton();
		JToggleButton button9 = new JToggleButton();
		JToggleButton button10 = new JToggleButton();
		JLabel tabbedPaneLabel = new JLabel();
		tabbedPane1 = new FlatThemePreviewAll.PreviewTabbedPane();
		JLabel listTreeLabel = new JLabel();
		JSplitPane splitPane1 = new JSplitPane();
		JScrollPane scrollPane2 = new JScrollPane();
		list1 = new FlatThemePreviewAll.PreviewList();
		JScrollPane scrollPane3 = new JScrollPane();
		tree1 = new JTree();
		JLabel tableLabel = new JLabel();
		JScrollPane scrollPane4 = new JScrollPane();
		table1 = new FlatThemePreviewAll.PreviewTable();
		JLabel internalFrameLabel = new JLabel();
		desktopPane1 = new JDesktopPane();
		internalFrame1 = new JInternalFrame();
		internalFrame2 = new JInternalFrame();

		//======== this ========
		setLayout(new MigLayout(
			"insets dialog,hidemode 3",
			// columns
			"[fill]" +
			"[60,sizegroup 1,fill]" +
			"[60,sizegroup 1,fill]",
			// rows
			"[]para" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[fill]" +
			"[]" +
			"[]4" +
			"[]" +
			"[]" +
			"[]0" +
			"[]" +
			"[]0" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[100,fill]" +
			"[grow]"));
		add(hSpacer1, "cell 0 0 3 1,growx");

		//---- enabledCheckBox ----
		enabledCheckBox.setText("Enabled");
		enabledCheckBox.setSelected(true);
		enabledCheckBox.addActionListener(e -> enabledChanged());
		add(enabledCheckBox, "cell 0 0 3 1");

		//---- editableCheckBox ----
		editableCheckBox.setText("Editable");
		editableCheckBox.setSelected(true);
		editableCheckBox.addActionListener(e -> editableChanged());
		add(editableCheckBox, "cell 0 0 3 1");

		//---- focusedCheckBox ----
		focusedCheckBox.setText("Focused");
		focusedCheckBox.addActionListener(e -> focusedChanged());
		add(focusedCheckBox, "cell 0 0 3 1");

		//---- labelLabel ----
		labelLabel.setText("JLabel:");
		add(labelLabel, "cell 0 1");

		//---- label1 ----
		label1.setText("Some Text");
		label1.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-label");
		add(label1, "cell 1 1 2 1");

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
		button1.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-button");
		add(button1, "cell 1 2");

		//---- testDefaultButton1 ----
		testDefaultButton1.setText("Default");
		testDefaultButton1.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-button");
		add(testDefaultButton1, "cell 2 2");

		//---- toggleButtonLabel ----
		toggleButtonLabel.setText("JToggleButton:");
		add(toggleButtonLabel, "cell 0 3");

		//---- toggleButton1 ----
		toggleButton1.setText("Unselected");
		toggleButton1.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-togglebutton");
		add(toggleButton1, "cell 1 3");

		//---- toggleButton3 ----
		toggleButton3.setText("Selected");
		toggleButton3.setSelected(true);
		toggleButton3.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-togglebutton");
		add(toggleButton3, "cell 2 3");

		//---- checkBoxLabel ----
		checkBoxLabel.setText("JCheckBox");
		add(checkBoxLabel, "cell 0 4");

		//---- checkBox1 ----
		checkBox1.setText("Unselected");
		checkBox1.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-checkbox");
		add(checkBox1, "cell 1 4,alignx left,growx 0");

		//---- checkBox3 ----
		checkBox3.setText("Selected");
		checkBox3.setSelected(true);
		checkBox3.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-checkbox");
		add(checkBox3, "cell 2 4,alignx left,growx 0");

		//---- radioButtonLabel ----
		radioButtonLabel.setText("JRadioButton:");
		add(radioButtonLabel, "cell 0 5");

		//---- radioButton1 ----
		radioButton1.setText("Unselected");
		radioButton1.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-radiobutton");
		add(radioButton1, "cell 1 5,alignx left,growx 0");

		//---- radioButton3 ----
		radioButton3.setText("Selected");
		radioButton3.setSelected(true);
		radioButton3.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-radiobutton");
		add(radioButton3, "cell 2 5,alignx left,growx 0");

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
		comboBox1.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-combobox");
		add(comboBox1, "cell 1 6");

		//---- comboBox3 ----
		comboBox3.setModel(new DefaultComboBoxModel<>(new String[] {
			"Not edit",
			"a",
			"bb",
			"ccc",
			"dd",
			"e"
		}));
		comboBox3.setMaximumRowCount(6);
		comboBox3.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-combobox");
		add(comboBox3, "cell 2 6");

		//---- spinnerLabel ----
		spinnerLabel.setText("JSpinner:");
		add(spinnerLabel, "cell 0 7");

		//---- spinner1 ----
		spinner1.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-spinner");
		add(spinner1, "cell 1 7 2 1");

		//---- textFieldLabel ----
		textFieldLabel.setText("<html>JTextField:<br>JFormattedTextF.:<br>JPasswordField:</html>");
		add(textFieldLabel, "cell 0 8 1 2");

		//---- textField1 ----
		textField1.setText("Some Text");
		textField1.setPlaceholderText("placeholder text");
		textField1.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-textfield");
		add(textField1, "cell 1 8");

		//---- textField2 ----
		textField2.setText("Txt");
		textField2.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-textfield");
		add(textField2, "cell 2 8");

		//---- formattedTextField1 ----
		formattedTextField1.setText("Some Text");
		formattedTextField1.setPlaceholderText("placeholder text");
		formattedTextField1.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-formattedtextfield");
		add(formattedTextField1, "cell 1 9");

		//---- passwordField1 ----
		passwordField1.setText("Some Text");
		passwordField1.setPlaceholderText("placeholder text");
		passwordField1.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-passwordfield");
		passwordField1.setShowClearButton(true);
		add(passwordField1, "cell 2 9");

		//---- textAreaLabel ----
		textAreaLabel.setText("<html>JTextArea:<br>JEditorPane:<br>JTextPane:</html>");
		add(textAreaLabel, "cell 0 10");

		//======== scrollPane1 ========
		{
			scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- textArea1 ----
			textArea1.setText("Text");
			textArea1.setRows(2);
			textArea1.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-textarea");
			scrollPane1.setViewportView(textArea1);
		}
		add(scrollPane1, "cell 1 10 2 1,width 40");

		//======== scrollPane5 ========
		{
			scrollPane5.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane5.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- editorPane1 ----
			editorPane1.setText("Text");
			editorPane1.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-editorpane");
			scrollPane5.setViewportView(editorPane1);
		}
		add(scrollPane5, "cell 1 10 2 1,width 40");

		//======== scrollPane9 ========
		{
			scrollPane9.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane9.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- textPane1 ----
			textPane1.setText("Text");
			textPane1.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-textpane");
			scrollPane9.setViewportView(textPane1);
		}
		add(scrollPane9, "cell 1 10 2 1,width 40");

		//---- menuBarLabel ----
		menuBarLabel.setText("JMenuBar:");
		add(menuBarLabel, "cell 0 11");

		//---- menuUnderlineSelectionButton ----
		menuUnderlineSelectionButton.setText("_");
		menuUnderlineSelectionButton.setButtonType(FlatButton.ButtonType.toolBarButton);
		menuUnderlineSelectionButton.setToolTipText("menu underline selection");
		menuUnderlineSelectionButton.setFocusable(false);
		menuUnderlineSelectionButton.putClientProperty(FlatClientProperties.STYLE_CLASS, "small");
		menuUnderlineSelectionButton.addActionListener(e -> menuUnderlineSelectionChanged());
		add(menuUnderlineSelectionButton, "cell 0 11");

		//======== menuBar1 ========
		{
			menuBar1.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-menubar");

			//======== menu2 ========
			{
				menu2.setText("JMenu");
				menu2.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-menu");

				//---- menuItem3 ----
				menuItem3.setText("JMenuItem");
				menuItem3.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-menuitem");
				menu2.add(menuItem3);

				//---- menuItem4 ----
				menuItem4.setText("JMenuItem");
				menuItem4.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-menuitem");
				menu2.add(menuItem4);
				menu2.addSeparator();

				//---- checkBoxMenuItem2 ----
				checkBoxMenuItem2.setText("JCheckBoxMenuItem");
				checkBoxMenuItem2.setSelected(true);
				checkBoxMenuItem2.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-checkboxmenuitem");
				menu2.add(checkBoxMenuItem2);

				//---- checkBoxMenuItem3 ----
				checkBoxMenuItem3.setText("JCheckBoxMenuItem");
				checkBoxMenuItem3.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-checkboxmenuitem");
				menu2.add(checkBoxMenuItem3);
				menu2.addSeparator();

				//---- radioButtonMenuItem4 ----
				radioButtonMenuItem4.setText("JRadioButtonMenuItem");
				radioButtonMenuItem4.setSelected(true);
				radioButtonMenuItem4.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-radiobuttonmenuitem");
				menu2.add(radioButtonMenuItem4);

				//---- radioButtonMenuItem5 ----
				radioButtonMenuItem5.setText("JRadioButtonMenuItem");
				radioButtonMenuItem5.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-radiobuttonmenuitem");
				menu2.add(radioButtonMenuItem5);
				menu2.addSeparator();

				//======== menu4 ========
				{
					menu4.setText("JMenu");
					menu4.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-menu");

					//---- menuItem6 ----
					menuItem6.setText("JMenuItem");
					menuItem6.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-menuitem");
					menu4.add(menuItem6);
				}
				menu2.add(menu4);

				//======== menu5 ========
				{
					menu5.setText("JMenu");
					menu5.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-menu");

					//---- menuItem7 ----
					menuItem7.setText("JMenuItem");
					menuItem7.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-menuitem");
					menu5.add(menuItem7);
				}
				menu2.add(menu5);
			}
			menuBar1.add(menu2);

			//======== menu3 ========
			{
				menu3.setText("JMenu");
				menu3.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-menu");

				//---- menuItem5 ----
				menuItem5.setText("JMenuItem");
				menuItem5.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK|KeyEvent.ALT_DOWN_MASK|KeyEvent.SHIFT_DOWN_MASK));
				menuItem5.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-menuitem");
				menu3.add(menuItem5);

				//---- menuItem8 ----
				menuItem8.setText("JMenuItem");
				menuItem8.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-menuitem");
				menu3.add(menuItem8);

				//---- menuItem9 ----
				menuItem9.setText("JMenuItem");
				menuItem9.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-menuitem");
				menu3.add(menuItem9);
			}
			menuBar1.add(menu3);
		}
		add(menuBar1, "cell 1 11 2 1");

		//---- scrollBarLabel ----
		scrollBarLabel.setText("JScrollBar:");
		add(scrollBarLabel, "cell 0 12 1 2,aligny top,growy 0");

		//---- scrollBar1 ----
		scrollBar1.setOrientation(Adjustable.HORIZONTAL);
		scrollBar1.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-scrollbar");
		scrollBar1.setVisibleAmount(50);
		add(scrollBar1, "cell 1 12 2 1");

		//---- scrollBar5 ----
		scrollBar5.setOrientation(Adjustable.HORIZONTAL);
		scrollBar5.setShowButtons(true);
		scrollBar5.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-scrollbar");
		scrollBar5.setVisibleAmount(50);
		add(scrollBar5, "cell 1 13 2 1");

		//---- separatorLabel ----
		separatorLabel.setText("JSeparator:");
		add(separatorLabel, "cell 0 14");

		//---- separator1 ----
		separator1.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-separator");
		add(separator1, "cell 1 14 2 1");

		//---- sliderLabel ----
		sliderLabel.setText("JSlider:");
		add(sliderLabel, "cell 0 15");

		//---- slider1 ----
		slider1.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-slider");
		add(slider1, "cell 1 15 2 1,width 100");

		//---- slider3 ----
		slider3.setMinorTickSpacing(10);
		slider3.setPaintTicks(true);
		slider3.setMajorTickSpacing(50);
		slider3.setPaintLabels(true);
		slider3.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-slider");
		slider3.addChangeListener(e -> changeProgress());
		add(slider3, "cell 1 16 2 1,width 100");

		//---- progressBarLabel ----
		progressBarLabel.setText("JProgressBar:");
		add(progressBarLabel, "cell 0 17");

		//---- progressBar1 ----
		progressBar1.setValue(50);
		progressBar1.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-progressbar");
		add(progressBar1, "cell 1 17 2 1");

		//---- progressBar2 ----
		progressBar2.setValue(50);
		progressBar2.setStringPainted(true);
		progressBar2.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-progressbar");
		add(progressBar2, "cell 1 18 2 1");

		//---- toolTipLabel ----
		toolTipLabel.setText("JToolTip:");
		add(toolTipLabel, "cell 0 19");

		//---- toolTip1 ----
		toolTip1.setTipText("Some text in tool tip.");
		toolTip1.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-tooltip");
		add(toolTip1, "cell 1 19 2 1,alignx left,growx 0");

		//---- toolBarLabel ----
		toolBarLabel.setText("JToolBar:");
		add(toolBarLabel, "cell 0 20");

		//======== toolBar1 ========
		{
			toolBar1.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-toolbar");

			//---- button4 ----
			button4.setIcon(UIManager.getIcon("Tree.closedIcon"));
			button4.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-toolbar-button");
			toolBar1.add(button4);

			//---- button6 ----
			button6.setIcon(UIManager.getIcon("Tree.openIcon"));
			button6.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-toolbar-button");
			toolBar1.add(button6);
			toolBar1.addSeparator();

			//---- button7 ----
			button7.setIcon(UIManager.getIcon("Tree.leafIcon"));
			button7.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-toolbar-togglebutton");
			toolBar1.add(button7);

			//---- button8 ----
			button8.setIcon(UIManager.getIcon("Tree.leafIcon"));
			button8.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-toolbar-togglebutton");
			toolBar1.add(button8);

			//---- button9 ----
			button9.setIcon(UIManager.getIcon("Tree.leafIcon"));
			button9.setSelected(true);
			button9.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-toolbar-togglebutton");
			toolBar1.add(button9);

			//---- button10 ----
			button10.setIcon(UIManager.getIcon("Tree.leafIcon"));
			button10.setSelected(true);
			button10.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-toolbar-togglebutton");
			toolBar1.add(button10);
		}
		add(toolBar1, "cell 1 20 2 1");

		//---- tabbedPaneLabel ----
		tabbedPaneLabel.setText("JTabbedPane:");
		add(tabbedPaneLabel, "cell 0 21");

		//======== tabbedPane1 ========
		{
			tabbedPane1.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-tabbedpane");
		}
		add(tabbedPane1, "cell 1 21 2 1");

		//---- listTreeLabel ----
		listTreeLabel.setText("<html>JList / JTree:<br>JSplitPane:</html>");
		add(listTreeLabel, "cell 0 22,aligny top,growy 0");

		//======== splitPane1 ========
		{
			splitPane1.setResizeWeight(0.5);
			splitPane1.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-splitpane");

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
				list1.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-list");
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
							DefaultMutableTreeNode node1 = new DefaultMutableTreeNode("Item 3");
								node1.add(new DefaultMutableTreeNode("Item 4"));
								node1.add(new DefaultMutableTreeNode("Item 5"));
							add(node1);
						}
					}));
				tree1.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-tree");
				scrollPane3.setViewportView(tree1);
			}
			splitPane1.setRightComponent(scrollPane3);
		}
		add(splitPane1, "cell 1 22 2 1,height 50");

		//---- tableLabel ----
		tableLabel.setText("JTable:");
		add(tableLabel, "cell 0 23");

		//======== scrollPane4 ========
		{

			//---- table1 ----
			table1.setModel(new DefaultTableModel(
				new Object[][] {
					{"Item 1a", "Item 2a"},
					{"Item 1b", "Item 2b"},
				},
				new String[] {
					"Column 1", "Column 2"
				}
			));
			table1.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-table");
			scrollPane4.setViewportView(table1);
		}
		add(scrollPane4, "cell 1 23 2 1,width 100,height 70");

		//---- internalFrameLabel ----
		internalFrameLabel.setText("<html>JDesktopPane:<br>JInternalFrame:</html>");
		add(internalFrameLabel, "cell 0 24,aligny top,growy 0");

		//======== desktopPane1 ========
		{
			desktopPane1.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-desktoppane");

			//======== internalFrame1 ========
			{
				internalFrame1.setVisible(true);
				internalFrame1.setTitle("Active");
				internalFrame1.setClosable(true);
				internalFrame1.setMaximizable(true);
				internalFrame1.setIconifiable(true);
				internalFrame1.setResizable(true);
				internalFrame1.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-internalframe");
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
				internalFrame2.putClientProperty(FlatClientProperties.STYLE_CLASS, "flatlaf-preview-internalframe");
				Container internalFrame2ContentPane = internalFrame2.getContentPane();
				internalFrame2ContentPane.setLayout(new BorderLayout());
			}
			desktopPane1.add(internalFrame2, JLayeredPane.DEFAULT_LAYER);
			internalFrame2.setBounds(new Rectangle(new Point(5, 50), internalFrame2.getPreferredSize()));
		}
		add(desktopPane1, "cell 1 24 2 1");

		//---- buttonGroup1 ----
		ButtonGroup buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(radioButton1);
		buttonGroup1.add(radioButton3);

		//---- buttonGroup2 ----
		ButtonGroup buttonGroup2 = new ButtonGroup();
		buttonGroup2.add(radioButtonMenuItem4);
		buttonGroup2.add(radioButtonMenuItem5);

		//---- buttonGroup3 ----
		ButtonGroup buttonGroup3 = new ButtonGroup();
		buttonGroup3.add(button7);
		buttonGroup3.add(button8);
		buttonGroup3.add(button9);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JCheckBox enabledCheckBox;
	private JCheckBox editableCheckBox;
	private JCheckBox focusedCheckBox;
	private JLabel label1;
	private FlatTextField textField1;
	private FlatTextField textField2;
	private FlatFormattedTextField formattedTextField1;
	private FlatPasswordField passwordField1;
	private JTextArea textArea1;
	private JEditorPane editorPane1;
	private JTextPane textPane1;
	private FlatToggleButton menuUnderlineSelectionButton;
	private JMenu menu2;
	private JSlider slider1;
	private JSlider slider3;
	private FlatProgressBar progressBar1;
	private FlatProgressBar progressBar2;
	private JToolBar toolBar1;
	private FlatThemePreviewAll.PreviewTabbedPane tabbedPane1;
	private FlatThemePreviewAll.PreviewList list1;
	private JTree tree1;
	private FlatThemePreviewAll.PreviewTable table1;
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
			if( !Beans.isDesignTime() )
				setUI( new PreviewFlatTabbedPaneUI( uiDefaultsGetter ) );
			else
				super.updateUI();
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

	//---- class PreviewList --------------------------------------------------

	private static class PreviewList
		extends JList<String>
	{
		Function<Object, Object> uiDefaultsGetter;

		@Override
		public void paint( Graphics g ) {
			if( !Beans.isDesignTime() ) {
				// needed for DefaultListCellRenderer
				FlatLaf.runWithUIDefaultsGetter( uiDefaultsGetter, () -> {
					super.paint( g );
				} );
			} else
				super.paint( g );
		}
	}

	//---- class PreviewTable -------------------------------------------------

	private static class PreviewTable
		extends JTable
	{
		Function<Object, Object> uiDefaultsGetter;
		private boolean inPrepareRenderer;

		@Override
		protected JTableHeader createDefaultTableHeader() {
			return new PreviewTableHeader( columnModel );
		}

		@Override
		public void paint( Graphics g ) {
			if( !Beans.isDesignTime() ) {
				// needed for DefaultTableCellRenderer
				FlatLaf.runWithUIDefaultsGetter( uiDefaultsGetter, () -> {
					super.paint( g );
				} );
			} else
				super.paint( g );
		}

		@Override
		public Component prepareRenderer( TableCellRenderer renderer, int row, int column ) {
			inPrepareRenderer = true;
			try {
				return super.prepareRenderer( renderer, row, column );
			} finally {
				inPrepareRenderer = false;
			}
		}

		@Override
		public boolean isFocusOwner() {
			// needed because FlatUIUtils.isPermanentFocusOwner() is not used in FlatTableUI
			return inPrepareRenderer
				? FlatUIUtils.isPermanentFocusOwner( this )
				: super.isFocusOwner();
		}

		//---- class PreviewTableHeader ----

		private class PreviewTableHeader
			extends JTableHeader
		{
			PreviewTableHeader( TableColumnModel columnModel ) {
				super( columnModel );
			}

			@Override
			public void paint( Graphics g ) {
				if( !Beans.isDesignTime() ) {
					// needed for DefaultTableCellHeaderRenderer
					FlatLaf.runWithUIDefaultsGetter( uiDefaultsGetter, () -> {
						super.paint( g );
					} );
				} else
					super.paint( g );
			}
		}
	}
}
