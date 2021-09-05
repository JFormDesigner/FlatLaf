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

import java.util.function.Predicate;
import javax.swing.*;
import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
class FlatThemePreviewSwitches
	extends JPanel
{
	FlatThemePreviewSwitches() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel label22 = new JLabel();
		JLabel label1 = new JLabel();
		JLabel label2 = new JLabel();
		JLabel label23 = new JLabel();
		JLabel label28 = new JLabel();
		JLabel label24 = new JLabel();
		JLabel label29 = new JLabel();
		JLabel label17 = new JLabel();
		FlatThemePreviewSwitches.TestStateCheckBox testStateCheckBox1 = new FlatThemePreviewSwitches.TestStateCheckBox();
		FlatThemePreviewSwitches.TestStateCheckBox testStateCheckBox8 = new FlatThemePreviewSwitches.TestStateCheckBox();
		FlatThemePreviewSwitches.TestStateCheckBox testStateCheckBox5 = new FlatThemePreviewSwitches.TestStateCheckBox();
		FlatThemePreviewSwitches.TestStateCheckBox testStateCheckBox12 = new FlatThemePreviewSwitches.TestStateCheckBox();
		JLabel label18 = new JLabel();
		FlatThemePreviewSwitches.TestStateCheckBox testStateCheckBox2 = new FlatThemePreviewSwitches.TestStateCheckBox();
		FlatThemePreviewSwitches.TestStateCheckBox testStateCheckBox9 = new FlatThemePreviewSwitches.TestStateCheckBox();
		FlatThemePreviewSwitches.TestStateCheckBox testStateCheckBox6 = new FlatThemePreviewSwitches.TestStateCheckBox();
		FlatThemePreviewSwitches.TestStateCheckBox testStateCheckBox13 = new FlatThemePreviewSwitches.TestStateCheckBox();
		JLabel label19 = new JLabel();
		FlatThemePreviewSwitches.TestStateCheckBox testStateCheckBox3 = new FlatThemePreviewSwitches.TestStateCheckBox();
		FlatThemePreviewSwitches.TestStateCheckBox testStateCheckBox10 = new FlatThemePreviewSwitches.TestStateCheckBox();
		FlatThemePreviewSwitches.TestStateCheckBox testStateCheckBox7 = new FlatThemePreviewSwitches.TestStateCheckBox();
		FlatThemePreviewSwitches.TestStateCheckBox testStateCheckBox14 = new FlatThemePreviewSwitches.TestStateCheckBox();
		JLabel label20 = new JLabel();
		FlatThemePreviewSwitches.TestStateCheckBox testStateCheckBox4 = new FlatThemePreviewSwitches.TestStateCheckBox();
		FlatThemePreviewSwitches.TestStateCheckBox testStateCheckBox11 = new FlatThemePreviewSwitches.TestStateCheckBox();
		JLabel label21 = new JLabel();
		JCheckBox checkBox1 = new JCheckBox();
		JCheckBox checkBox2 = new JCheckBox();
		JLabel label27 = new JLabel();
		JLabel label3 = new JLabel();
		JLabel label4 = new JLabel();
		JLabel label25 = new JLabel();
		JLabel label30 = new JLabel();
		JLabel label26 = new JLabel();
		JLabel label31 = new JLabel();
		JLabel label36 = new JLabel();
		FlatThemePreviewSwitches.TestStateRadioButton testStateRadioButton1 = new FlatThemePreviewSwitches.TestStateRadioButton();
		FlatThemePreviewSwitches.TestStateRadioButton testStateRadioButton8 = new FlatThemePreviewSwitches.TestStateRadioButton();
		FlatThemePreviewSwitches.TestStateRadioButton testStateRadioButton5 = new FlatThemePreviewSwitches.TestStateRadioButton();
		FlatThemePreviewSwitches.TestStateRadioButton testStateRadioButton9 = new FlatThemePreviewSwitches.TestStateRadioButton();
		JLabel label35 = new JLabel();
		FlatThemePreviewSwitches.TestStateRadioButton testStateRadioButton2 = new FlatThemePreviewSwitches.TestStateRadioButton();
		FlatThemePreviewSwitches.TestStateRadioButton testStateRadioButton10 = new FlatThemePreviewSwitches.TestStateRadioButton();
		FlatThemePreviewSwitches.TestStateRadioButton testStateRadioButton6 = new FlatThemePreviewSwitches.TestStateRadioButton();
		FlatThemePreviewSwitches.TestStateRadioButton testStateRadioButton11 = new FlatThemePreviewSwitches.TestStateRadioButton();
		JLabel label34 = new JLabel();
		FlatThemePreviewSwitches.TestStateRadioButton testStateRadioButton3 = new FlatThemePreviewSwitches.TestStateRadioButton();
		FlatThemePreviewSwitches.TestStateRadioButton testStateRadioButton12 = new FlatThemePreviewSwitches.TestStateRadioButton();
		FlatThemePreviewSwitches.TestStateRadioButton testStateRadioButton7 = new FlatThemePreviewSwitches.TestStateRadioButton();
		FlatThemePreviewSwitches.TestStateRadioButton testStateRadioButton13 = new FlatThemePreviewSwitches.TestStateRadioButton();
		JLabel label33 = new JLabel();
		FlatThemePreviewSwitches.TestStateRadioButton testStateRadioButton4 = new FlatThemePreviewSwitches.TestStateRadioButton();
		FlatThemePreviewSwitches.TestStateRadioButton testStateRadioButton14 = new FlatThemePreviewSwitches.TestStateRadioButton();
		JLabel label32 = new JLabel();
		JRadioButton radioButton1 = new JRadioButton();
		JRadioButton radioButton2 = new JRadioButton();

		//======== this ========
		setLayout(new MigLayout(
			"insets dialog,hidemode 3",
			// columns
			"[fill]" +
			"[fill]" +
			"[fill]para" +
			"[fill]" +
			"[fill]para",
			// rows
			"[]" +
			"[]0" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]unrel" +
			"[]para" +
			"[]" +
			"[]0" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]unrel" +
			"[]"));

		//---- label22 ----
		label22.setText("JCheckBox");
		label22.setFont(label22.getFont().deriveFont(label22.getFont().getSize() + 4f));
		add(label22, "cell 0 0 3 1");

		//---- label1 ----
		label1.setText("unfocused");
		add(label1, "cell 1 1 2 1,alignx center,growx 0");

		//---- label2 ----
		label2.setText("focused");
		add(label2, "cell 3 1 2 1,alignx center,growx 0");

		//---- label23 ----
		label23.setText("unsel.");
		label23.setFont(label23.getFont().deriveFont(label23.getFont().getSize() - 2f));
		add(label23, "cell 1 2");

		//---- label28 ----
		label28.setText("selected");
		label28.setFont(label28.getFont().deriveFont(label28.getFont().getSize() - 2f));
		add(label28, "cell 2 2");

		//---- label24 ----
		label24.setText("unsel.");
		label24.setFont(label24.getFont().deriveFont(label24.getFont().getSize() - 2f));
		add(label24, "cell 3 2");

		//---- label29 ----
		label29.setText("selected");
		label29.setFont(label29.getFont().deriveFont(label29.getFont().getSize() - 2f));
		add(label29, "cell 4 2");

		//---- label17 ----
		label17.setText("none");
		add(label17, "cell 0 3");

		//---- testStateCheckBox1 ----
		testStateCheckBox1.setText("text");
		add(testStateCheckBox1, "cell 1 3");

		//---- testStateCheckBox8 ----
		testStateCheckBox8.setText("text");
		testStateCheckBox8.setStateSelected(true);
		add(testStateCheckBox8, "cell 2 3");

		//---- testStateCheckBox5 ----
		testStateCheckBox5.setText("text");
		testStateCheckBox5.setStateFocused(true);
		add(testStateCheckBox5, "cell 3 3");

		//---- testStateCheckBox12 ----
		testStateCheckBox12.setText("text");
		testStateCheckBox12.setStateFocused(true);
		testStateCheckBox12.setStateSelected(true);
		add(testStateCheckBox12, "cell 4 3");

		//---- label18 ----
		label18.setText("hover");
		add(label18, "cell 0 4");

		//---- testStateCheckBox2 ----
		testStateCheckBox2.setText("text");
		testStateCheckBox2.setStateHover(true);
		add(testStateCheckBox2, "cell 1 4");

		//---- testStateCheckBox9 ----
		testStateCheckBox9.setText("text");
		testStateCheckBox9.setStateHover(true);
		testStateCheckBox9.setStateSelected(true);
		add(testStateCheckBox9, "cell 2 4");

		//---- testStateCheckBox6 ----
		testStateCheckBox6.setText("text");
		testStateCheckBox6.setStateFocused(true);
		testStateCheckBox6.setStateHover(true);
		add(testStateCheckBox6, "cell 3 4");

		//---- testStateCheckBox13 ----
		testStateCheckBox13.setText("text");
		testStateCheckBox13.setStateFocused(true);
		testStateCheckBox13.setStateHover(true);
		testStateCheckBox13.setStateSelected(true);
		add(testStateCheckBox13, "cell 4 4");

		//---- label19 ----
		label19.setText("pressed");
		add(label19, "cell 0 5");

		//---- testStateCheckBox3 ----
		testStateCheckBox3.setText("text");
		testStateCheckBox3.setStatePressed(true);
		add(testStateCheckBox3, "cell 1 5");

		//---- testStateCheckBox10 ----
		testStateCheckBox10.setText("text");
		testStateCheckBox10.setStatePressed(true);
		testStateCheckBox10.setStateSelected(true);
		add(testStateCheckBox10, "cell 2 5");

		//---- testStateCheckBox7 ----
		testStateCheckBox7.setText("text");
		testStateCheckBox7.setStateFocused(true);
		testStateCheckBox7.setStatePressed(true);
		add(testStateCheckBox7, "cell 3 5");

		//---- testStateCheckBox14 ----
		testStateCheckBox14.setText("text");
		testStateCheckBox14.setStateFocused(true);
		testStateCheckBox14.setStatePressed(true);
		testStateCheckBox14.setStateSelected(true);
		add(testStateCheckBox14, "cell 4 5");

		//---- label20 ----
		label20.setText("disabled");
		add(label20, "cell 0 6");

		//---- testStateCheckBox4 ----
		testStateCheckBox4.setText("text");
		testStateCheckBox4.setEnabled(false);
		add(testStateCheckBox4, "cell 1 6");

		//---- testStateCheckBox11 ----
		testStateCheckBox11.setText("text");
		testStateCheckBox11.setEnabled(false);
		testStateCheckBox11.setStateSelected(true);
		add(testStateCheckBox11, "cell 2 6");

		//---- label21 ----
		label21.setText("try me");
		add(label21, "cell 0 7");

		//---- checkBox1 ----
		checkBox1.setText("text");
		add(checkBox1, "cell 1 7");

		//---- checkBox2 ----
		checkBox2.setText("text");
		checkBox2.setSelected(true);
		add(checkBox2, "cell 2 7");

		//---- label27 ----
		label27.setText("JRadioButton");
		label27.setFont(label27.getFont().deriveFont(label27.getFont().getSize() + 4f));
		add(label27, "cell 0 8 3 1");

		//---- label3 ----
		label3.setText("unfocused");
		add(label3, "cell 1 9 2 1,alignx center,growx 0");

		//---- label4 ----
		label4.setText("focused");
		add(label4, "cell 3 9 2 1,alignx center,growx 0");

		//---- label25 ----
		label25.setText("unsel.");
		label25.setFont(label25.getFont().deriveFont(label25.getFont().getSize() - 2f));
		add(label25, "cell 1 10");

		//---- label30 ----
		label30.setText("selected");
		label30.setFont(label30.getFont().deriveFont(label30.getFont().getSize() - 2f));
		add(label30, "cell 2 10");

		//---- label26 ----
		label26.setText("unsel.");
		label26.setFont(label26.getFont().deriveFont(label26.getFont().getSize() - 2f));
		add(label26, "cell 3 10");

		//---- label31 ----
		label31.setText("selected");
		label31.setFont(label31.getFont().deriveFont(label31.getFont().getSize() - 2f));
		add(label31, "cell 4 10");

		//---- label36 ----
		label36.setText("none");
		add(label36, "cell 0 11");

		//---- testStateRadioButton1 ----
		testStateRadioButton1.setText("text");
		add(testStateRadioButton1, "cell 1 11");

		//---- testStateRadioButton8 ----
		testStateRadioButton8.setText("text");
		testStateRadioButton8.setStateSelected(true);
		add(testStateRadioButton8, "cell 2 11");

		//---- testStateRadioButton5 ----
		testStateRadioButton5.setText("text");
		testStateRadioButton5.setStateFocused(true);
		add(testStateRadioButton5, "cell 3 11");

		//---- testStateRadioButton9 ----
		testStateRadioButton9.setText("text");
		testStateRadioButton9.setStateFocused(true);
		testStateRadioButton9.setStateSelected(true);
		add(testStateRadioButton9, "cell 4 11");

		//---- label35 ----
		label35.setText("hover");
		add(label35, "cell 0 12");

		//---- testStateRadioButton2 ----
		testStateRadioButton2.setText("text");
		testStateRadioButton2.setStateHover(true);
		add(testStateRadioButton2, "cell 1 12");

		//---- testStateRadioButton10 ----
		testStateRadioButton10.setText("text");
		testStateRadioButton10.setStateHover(true);
		testStateRadioButton10.setStateSelected(true);
		add(testStateRadioButton10, "cell 2 12");

		//---- testStateRadioButton6 ----
		testStateRadioButton6.setText("text");
		testStateRadioButton6.setStateFocused(true);
		testStateRadioButton6.setStateHover(true);
		add(testStateRadioButton6, "cell 3 12");

		//---- testStateRadioButton11 ----
		testStateRadioButton11.setText("text");
		testStateRadioButton11.setStateFocused(true);
		testStateRadioButton11.setStateHover(true);
		testStateRadioButton11.setStateSelected(true);
		add(testStateRadioButton11, "cell 4 12");

		//---- label34 ----
		label34.setText("pressed");
		add(label34, "cell 0 13");

		//---- testStateRadioButton3 ----
		testStateRadioButton3.setText("text");
		testStateRadioButton3.setStatePressed(true);
		add(testStateRadioButton3, "cell 1 13");

		//---- testStateRadioButton12 ----
		testStateRadioButton12.setText("text");
		testStateRadioButton12.setStatePressed(true);
		testStateRadioButton12.setStateSelected(true);
		add(testStateRadioButton12, "cell 2 13");

		//---- testStateRadioButton7 ----
		testStateRadioButton7.setText("text");
		testStateRadioButton7.setStateFocused(true);
		testStateRadioButton7.setStatePressed(true);
		add(testStateRadioButton7, "cell 3 13");

		//---- testStateRadioButton13 ----
		testStateRadioButton13.setText("text");
		testStateRadioButton13.setStateFocused(true);
		testStateRadioButton13.setStatePressed(true);
		testStateRadioButton13.setStateSelected(true);
		add(testStateRadioButton13, "cell 4 13");

		//---- label33 ----
		label33.setText("disabled");
		add(label33, "cell 0 14");

		//---- testStateRadioButton4 ----
		testStateRadioButton4.setText("text");
		testStateRadioButton4.setEnabled(false);
		add(testStateRadioButton4, "cell 1 14");

		//---- testStateRadioButton14 ----
		testStateRadioButton14.setText("text");
		testStateRadioButton14.setEnabled(false);
		testStateRadioButton14.setStateSelected(true);
		add(testStateRadioButton14, "cell 2 14");

		//---- label32 ----
		label32.setText("try me");
		add(label32, "cell 0 15");

		//---- radioButton1 ----
		radioButton1.setText("text");
		add(radioButton1, "cell 1 15");

		//---- radioButton2 ----
		radioButton2.setText("text");
		radioButton2.setSelected(true);
		add(radioButton2, "cell 2 15");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class TestStateCheckBox --------------------------------------------

	private static class TestStateCheckBox
		extends JCheckBox
	{
		private boolean stateHover;
		private boolean statePressed;
		private boolean stateFocused;
		private boolean stateSelected;

		public TestStateCheckBox() {
			setModel( new DefaultButtonModel() {
				@Override
				public boolean isRollover() {
					return isStateHover();
				}
				@Override
				public boolean isPressed() {
					return isStatePressed();
				}
				@Override
				public boolean isSelected() {
					return isStateSelected();
				}
			} );

			putClientProperty( FlatClientProperties.COMPONENT_FOCUS_OWNER,
				(Predicate<JComponent>) c -> {
					return ((TestStateCheckBox)c).isStateFocused();
				} );
		}

		public boolean isStateHover() {
			return stateHover;
		}

		public void setStateHover( boolean stateHover ) {
			this.stateHover = stateHover;
		}

		public boolean isStatePressed() {
			return statePressed;
		}

		public void setStatePressed( boolean statePressed ) {
			this.statePressed = statePressed;
		}

		public boolean isStateFocused() {
			return stateFocused;
		}

		public void setStateFocused( boolean stateFocused ) {
			this.stateFocused = stateFocused;
		}

		public boolean isStateSelected() {
			return stateSelected;
		}

		public void setStateSelected( boolean stateSelected ) {
			this.stateSelected = stateSelected;
		}
	}

	//---- class TestStateRadioButton -----------------------------------------

	private static class TestStateRadioButton
		extends JRadioButton
	{
		private boolean stateHover;
		private boolean statePressed;
		private boolean stateFocused;
		private boolean stateSelected;

		public TestStateRadioButton() {
			setModel( new DefaultButtonModel() {
				@Override
				public boolean isRollover() {
					return isStateHover();
				}
				@Override
				public boolean isPressed() {
					return isStatePressed();
				}
				@Override
				public boolean isSelected() {
					return isStateSelected();
				}
			} );

			putClientProperty( FlatClientProperties.COMPONENT_FOCUS_OWNER,
				(Predicate<JComponent>) c -> {
					return ((TestStateRadioButton)c).isStateFocused();
				} );
		}

		public boolean isStateHover() {
			return stateHover;
		}

		public void setStateHover( boolean stateHover ) {
			this.stateHover = stateHover;
		}

		public boolean isStatePressed() {
			return statePressed;
		}

		public void setStatePressed( boolean statePressed ) {
			this.statePressed = statePressed;
		}

		public boolean isStateFocused() {
			return stateFocused;
		}

		public void setStateFocused( boolean stateFocused ) {
			this.stateFocused = stateFocused;
		}

		public boolean isStateSelected() {
			return stateSelected;
		}

		public void setStateSelected( boolean stateSelected ) {
			this.stateSelected = stateSelected;
		}
	}
}
