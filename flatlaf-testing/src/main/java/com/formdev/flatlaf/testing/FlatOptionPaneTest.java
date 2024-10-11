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

package com.formdev.flatlaf.testing;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.*;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.demo.ScrollablePanel;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatOptionPaneTest
	extends JScrollPane
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatOptionPaneTest" );
			frame.setIconImage( new ImageIcon( FlatOptionPaneTest.class.getResource( "/com/formdev/flatlaf/testing/test16.png" ) ).getImage() );
			frame.showFrame( FlatOptionPaneTest::new );
		} );
	}

	FlatOptionPaneTest() {
		initComponents();

		JPanel panel = new JPanel();
		panel.setBackground( Color.green );
		panel.add( new JLabel( "label in green panel" ) );
		customOptionPane.setMessage( new Object[] {
			"string",
			"multi-\nline string",
			new JCheckBox( "check box" ),
			new JTextField( "text field" ),
			panel,
			"more text",
		} );
		customOptionPane.setOptions( new Object[] {
			new JCheckBox( "check me" ),
			"OK",
			"Cancel",
		} );
	}

	private void showTitleBarIcon() {
		UIManager.put( "OptionPane.showIcon", showTitleBarIconCheckBox.isSelected() );
	}

	private void showWithCustomIcon() {
		JOptionPane optionPane = new JOptionPane( "Hello world." );
		JDialog dialog = optionPane.createDialog( "With Custom Icon" );
		dialog.getRootPane().putClientProperty( FlatClientProperties.TITLE_BAR_SHOW_ICON, true );
		dialog.setIconImage( new ImageIcon( FlatOptionPaneTest.class.getResource( "/com/formdev/flatlaf/testing/test32.png" ) ).getImage() );
		dialog.setVisible( true );
		dialog.dispose();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		ScrollablePanel panel9 = new ScrollablePanel();
		JLabel plainLabel = new JLabel();
		JPanel panel1 = new JPanel();
		JOptionPane plainOptionPane = new JOptionPane();
		plainShowDialogLabel = new FlatOptionPaneTest.ShowDialogLinkLabel();
		showTitleBarIconCheckBox = new JCheckBox();
		JLabel errorLabel = new JLabel();
		JPanel panel2 = new JPanel();
		JOptionPane errorOptionPane = new JOptionPane();
		errorShowDialogLabel = new FlatOptionPaneTest.ShowDialogLinkLabel();
		JButton showWithCustomIconButton = new JButton();
		JLabel informationLabel = new JLabel();
		JPanel panel3 = new JPanel();
		JOptionPane informationOptionPane = new JOptionPane();
		informationShowDialogLabel = new FlatOptionPaneTest.ShowDialogLinkLabel();
		JLabel questionLabel = new JLabel();
		JPanel panel4 = new JPanel();
		JOptionPane questionOptionPane = new JOptionPane();
		FlatOptionPaneTest.ShowDialogLinkLabel questionShowDialogLabel = new FlatOptionPaneTest.ShowDialogLinkLabel();
		JLabel warningLabel = new JLabel();
		JPanel panel5 = new JPanel();
		JOptionPane warningOptionPane = new JOptionPane();
		FlatOptionPaneTest.ShowDialogLinkLabel warningShowDialogLabel = new FlatOptionPaneTest.ShowDialogLinkLabel();
		JLabel inputLabel = new JLabel();
		JPanel panel7 = new JPanel();
		JOptionPane inputOptionPane = new JOptionPane();
		FlatOptionPaneTest.ShowDialogLinkLabel inputShowDialogLabel = new FlatOptionPaneTest.ShowDialogLinkLabel();
		JLabel inputIconLabel = new JLabel();
		JPanel panel8 = new JPanel();
		JOptionPane inputIconOptionPane = new JOptionPane();
		FlatOptionPaneTest.ShowDialogLinkLabel inputIconShowDialogLabel = new FlatOptionPaneTest.ShowDialogLinkLabel();
		JLabel customLabel = new JLabel();
		JPanel panel6 = new JPanel();
		customOptionPane = new JOptionPane();
		FlatOptionPaneTest.ShowDialogLinkLabel customShowDialogLabel = new FlatOptionPaneTest.ShowDialogLinkLabel();
		JLabel rightToLeftLabel = new JLabel();
		JPanel panel10 = new JPanel();
		JOptionPane rightToLeftOptionPane = new JOptionPane();
		rightToLeftShowDialogLabel = new FlatOptionPaneTest.ShowDialogLinkLabel();

		//======== this ========
		setBorder(BorderFactory.createEmptyBorder());

		//======== panel9 ========
		{
			panel9.setLayout(new MigLayout(
				"ltr,insets dialog,hidemode 3",
				// columns
				"[]" +
				"[]" +
				"[fill]",
				// rows
				"[top]" +
				"[top]" +
				"[top]" +
				"[top]" +
				"[top]" +
				"[top]" +
				"[top]" +
				"[top]" +
				"[top]"));

			//---- plainLabel ----
			plainLabel.setText("Plain");
			panel9.add(plainLabel, "cell 0 0");

			//======== panel1 ========
			{
				panel1.setBorder(LineBorder.createGrayLineBorder());
				panel1.setLayout(new BorderLayout());

				//---- plainOptionPane ----
				plainOptionPane.setMessage("Hello world.");
				panel1.add(plainOptionPane, BorderLayout.CENTER);
			}
			panel9.add(panel1, "cell 1 0");

			//---- plainShowDialogLabel ----
			plainShowDialogLabel.setOptionPane(plainOptionPane);
			plainShowDialogLabel.setTitleLabel(plainLabel);
			panel9.add(plainShowDialogLabel, "cell 1 0");

			//---- showTitleBarIconCheckBox ----
			showTitleBarIconCheckBox.setText("Show window title bar icon");
			showTitleBarIconCheckBox.addActionListener(e -> showTitleBarIcon());
			panel9.add(showTitleBarIconCheckBox, "cell 2 0");

			//---- errorLabel ----
			errorLabel.setText("Error");
			panel9.add(errorLabel, "cell 0 1");

			//======== panel2 ========
			{
				panel2.setBorder(LineBorder.createGrayLineBorder());
				panel2.setLayout(new BorderLayout());

				//---- errorOptionPane ----
				errorOptionPane.setMessageType(JOptionPane.ERROR_MESSAGE);
				errorOptionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
				errorOptionPane.setMessage("Your PC ran into a problem. Buy a new one.");
				panel2.add(errorOptionPane, BorderLayout.CENTER);
			}
			panel9.add(panel2, "cell 1 1");

			//---- errorShowDialogLabel ----
			errorShowDialogLabel.setTitleLabel(errorLabel);
			errorShowDialogLabel.setOptionPane(errorOptionPane);
			panel9.add(errorShowDialogLabel, "cell 1 1");

			//---- showWithCustomIconButton ----
			showWithCustomIconButton.setText("Show with custom icon");
			showWithCustomIconButton.addActionListener(e -> showWithCustomIcon());
			panel9.add(showWithCustomIconButton, "cell 2 1");

			//---- informationLabel ----
			informationLabel.setText("Information");
			panel9.add(informationLabel, "cell 0 2");

			//======== panel3 ========
			{
				panel3.setBorder(LineBorder.createGrayLineBorder());
				panel3.setLayout(new BorderLayout());

				//---- informationOptionPane ----
				informationOptionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
				informationOptionPane.setOptionType(JOptionPane.YES_NO_OPTION);
				informationOptionPane.setMessage("Text with\nmultiple lines\n(use \\n to separate lines)");
				panel3.add(informationOptionPane, BorderLayout.CENTER);
			}
			panel9.add(panel3, "cell 1 2");

			//---- informationShowDialogLabel ----
			informationShowDialogLabel.setOptionPane(informationOptionPane);
			informationShowDialogLabel.setTitleLabel(informationLabel);
			panel9.add(informationShowDialogLabel, "cell 1 2");

			//---- questionLabel ----
			questionLabel.setText("Question");
			panel9.add(questionLabel, "cell 0 3");

			//======== panel4 ========
			{
				panel4.setBorder(LineBorder.createGrayLineBorder());
				panel4.setLayout(new BorderLayout());

				//---- questionOptionPane ----
				questionOptionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
				questionOptionPane.setOptionType(JOptionPane.YES_NO_CANCEL_OPTION);
				questionOptionPane.setMessage("Answer the question. What question? Don't know. Just writing useless text to make this longer than 80 characters.");
				panel4.add(questionOptionPane, BorderLayout.CENTER);
			}
			panel9.add(panel4, "cell 1 3");

			//---- questionShowDialogLabel ----
			questionShowDialogLabel.setOptionPane(questionOptionPane);
			questionShowDialogLabel.setTitleLabel(questionLabel);
			panel9.add(questionShowDialogLabel, "cell 2 3");

			//---- warningLabel ----
			warningLabel.setText("Warning");
			panel9.add(warningLabel, "cell 0 4");

			//======== panel5 ========
			{
				panel5.setBorder(LineBorder.createGrayLineBorder());
				panel5.setLayout(new BorderLayout());

				//---- warningOptionPane ----
				warningOptionPane.setMessageType(JOptionPane.WARNING_MESSAGE);
				warningOptionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
				warningOptionPane.setMessage("<html>I like <b>bold</b>,<br> and I like <i>italic</i>,<br> and I like to have<br> many lines.<br> Lots of lines.");
				panel5.add(warningOptionPane, BorderLayout.CENTER);
			}
			panel9.add(panel5, "cell 1 4");

			//---- warningShowDialogLabel ----
			warningShowDialogLabel.setOptionPane(warningOptionPane);
			warningShowDialogLabel.setTitleLabel(warningLabel);
			panel9.add(warningShowDialogLabel, "cell 1 4");

			//---- inputLabel ----
			inputLabel.setText("Input");
			panel9.add(inputLabel, "cell 0 5");

			//======== panel7 ========
			{
				panel7.setBorder(LineBorder.createGrayLineBorder());
				panel7.setLayout(new BorderLayout());

				//---- inputOptionPane ----
				inputOptionPane.setWantsInput(true);
				inputOptionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
				inputOptionPane.setMessage("Enter whatever you want:");
				panel7.add(inputOptionPane, BorderLayout.CENTER);
			}
			panel9.add(panel7, "cell 1 5");

			//---- inputShowDialogLabel ----
			inputShowDialogLabel.setOptionPane(inputOptionPane);
			inputShowDialogLabel.setTitleLabel(inputLabel);
			panel9.add(inputShowDialogLabel, "cell 1 5");

			//---- inputIconLabel ----
			inputIconLabel.setText("Input + icon");
			panel9.add(inputIconLabel, "cell 0 6");

			//======== panel8 ========
			{
				panel8.setBorder(LineBorder.createGrayLineBorder());
				panel8.setLayout(new BorderLayout());

				//---- inputIconOptionPane ----
				inputIconOptionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
				inputIconOptionPane.setWantsInput(true);
				inputIconOptionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
				inputIconOptionPane.setMessage("Enter something:");
				panel8.add(inputIconOptionPane, BorderLayout.CENTER);
			}
			panel9.add(panel8, "cell 1 6");

			//---- inputIconShowDialogLabel ----
			inputIconShowDialogLabel.setTitleLabel(inputIconLabel);
			inputIconShowDialogLabel.setOptionPane(inputIconOptionPane);
			panel9.add(inputIconShowDialogLabel, "cell 1 6");

			//---- customLabel ----
			customLabel.setText("Custom");
			panel9.add(customLabel, "cell 0 7");

			//======== panel6 ========
			{
				panel6.setBorder(LineBorder.createGrayLineBorder());
				panel6.setLayout(new BorderLayout());

				//---- customOptionPane ----
				customOptionPane.setIcon(UIManager.getIcon("Tree.leafIcon"));
				panel6.add(customOptionPane, BorderLayout.CENTER);
			}
			panel9.add(panel6, "cell 1 7");

			//---- customShowDialogLabel ----
			customShowDialogLabel.setOptionPane(customOptionPane);
			customShowDialogLabel.setTitleLabel(customLabel);
			panel9.add(customShowDialogLabel, "cell 1 7");

			//---- rightToLeftLabel ----
			rightToLeftLabel.setText("Right-to-left:");
			panel9.add(rightToLeftLabel, "cell 0 8");

			//======== panel10 ========
			{
				panel10.setBorder(LineBorder.createGrayLineBorder());
				panel10.setLayout(new BorderLayout());

				//---- rightToLeftOptionPane ----
				rightToLeftOptionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
				rightToLeftOptionPane.setMessage("\u0627\u0644\u0645\u0627\u062f\u0629 1 \u064a\u0648\u0644\u062f \u062c\u0645\u064a\u0639 \u0627\u0644\u0646\u0627\u0633 \u0623\u062d\u0631\u0627\u0631\u064b\u0627 \u0645\u062a\u0633\u0627\u0648\u064a\u0646 \u0641\u064a \u0627\u0644\u0643\u0631\u0627\u0645\u0629 \u0648\u0627\u0644\u062d\u0642\u0648\u0642. \u0648\u0642\u062f \u0648\u0647\u0628\u0648\u0627 \u0639\u0642\u0644\u0627\u064b \u0648\u0636\u0645\u064a\u0631\u064b\u0627 \u0648\u0639\u0644\u064a\u0647\u0645 \u0623\u0646 \u064a\u0639\u0627\u0645\u0644 \u0628\u0639\u0636\u0647\u0645 \u0628\u0639\u0636\u064b\u0627 \u0628\u0631\u0648\u062d \u0627\u0644\u0625\u062e\u0627\u0621.\n\u0627\u0644\u0645\u0627\u062f\u0629 1 \u064a\u0648\u0644\u062f \u062c\u0645\u064a\u0639 \u0627\u0644\u0646\u0627\u0633 \u0623\u062d\u0631\u0627\u0631\u064b\u0627 \u0645\u062a\u0633\u0627\u0648\u064a\u0646 \u0641\u064a \u0627\u0644\u0643\u0631\u0627\u0645\u0629 \u0648\u0627\u0644\u062d\u0642\u0648\u0642. \u0648\u0642\u062f \u0648\u0647\u0628\u0648\u0627 \u0639\u0642\u0644\u0627\u064b \u0648\u0636\u0645\u064a\u0631\u064b\u0627 \u0648\u0639\u0644\u064a\u0647\u0645 \u0623\u0646 \u064a\u0639\u0627\u0645\u0644 \u0628\u0639\u0636\u0647\u0645 \u0628\u0639\u0636\u064b\u0627 \u0628\u0631\u0648\u062d \u0627\u0644\u0625\u062e\u0627\u0621.\n\n\u0627\u0644\u0645\u0627\u062f\u0629 1 \u064a\u0648\u0644\u062f \u062c\u0645\u064a\u0639 \u0627\u0644\u0646\u0627\u0633 \u0623\u062d\u0631\u0627\u0631\u064b\u0627 \u0645\u062a\u0633\u0627\u0648\u064a\u0646 \u0641\u064a \u0627\u0644\u0643\u0631\u0627\u0645\u0629 \n\u0648\u0627\u0644\u062d\u0642\u0648\u0642. \u0648\u0642\u062f \u0648\u0647\u0628\u0648\u0627 \u0639\u0642\u0644\u0627\u064b \u0648\u0636\u0645\u064a\u0631\u064b\u0627 \u0648\u0639\u0644\u064a\u0647\u0645 \u0623\u0646 \u064a\u0639\u0627\u0645\u0644 \u0628\u0639\u0636\u0647\u0645 \u0628\u0639\u0636\u064b\u0627 \u0628\u0631\u0648\u062d \u0627\u0644\u0625\u062e\u0627\u0621.");
				rightToLeftOptionPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
				panel10.add(rightToLeftOptionPane, BorderLayout.CENTER);
			}
			panel9.add(panel10, "cell 1 8");

			//---- rightToLeftShowDialogLabel ----
			rightToLeftShowDialogLabel.setOptionPane(rightToLeftOptionPane);
			rightToLeftShowDialogLabel.setTitleLabel(rightToLeftLabel);
			panel9.add(rightToLeftShowDialogLabel, "cell 2 8");
		}
		setViewportView(panel9);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private FlatOptionPaneTest.ShowDialogLinkLabel plainShowDialogLabel;
	private JCheckBox showTitleBarIconCheckBox;
	private FlatOptionPaneTest.ShowDialogLinkLabel errorShowDialogLabel;
	private FlatOptionPaneTest.ShowDialogLinkLabel informationShowDialogLabel;
	private JOptionPane customOptionPane;
	private FlatOptionPaneTest.ShowDialogLinkLabel rightToLeftShowDialogLabel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class ShowDialogLinkLabel ------------------------------------------

	private static class ShowDialogLinkLabel
		extends JLabel
	{
		private JLabel titleLabel;
		private JOptionPane optionPane;

		ShowDialogLinkLabel() {
			setText( "<html><a href=\"#\">Show dialog</a></html>" );
			setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );

			addMouseListener( new MouseAdapter() {
				@Override
				public void mouseClicked( MouseEvent e ) {
					showDialog();
				}
			} );
		}

		private void showDialog() {
			Component parent = SwingUtilities.windowForComponent( this );

			// use optionPane as parent if component orientation is different
			if( parent.getComponentOrientation().isLeftToRight() != optionPane.getComponentOrientation().isLeftToRight() )
				parent = optionPane;

			if( optionPane.getWantsInput() ) {
				JOptionPane.showInputDialog(
					parent,
					optionPane.getMessage(),
					titleLabel.getText() + " Title",
					optionPane.getMessageType(),
					optionPane.getIcon(),
					null,
					null );
			} else {
				JOptionPane.showOptionDialog(
					parent,
					optionPane.getMessage(),
					titleLabel.getText() + " Title",
					optionPane.getOptionType(),
					optionPane.getMessageType(),
					optionPane.getIcon(),
					optionPane.getOptions(),
					optionPane.getInitialValue() );
			}
		}

		@SuppressWarnings( "unused" )
		public JLabel getTitleLabel() {
			return titleLabel;
		}

		public void setTitleLabel( JLabel titleLabel ) {
			this.titleLabel = titleLabel;
		}

		@SuppressWarnings( "unused" )
		public JOptionPane getOptionPane() {
			return optionPane;
		}

		public void setOptionPane( JOptionPane optionPane ) {
			this.optionPane = optionPane;
		}
	}
}
