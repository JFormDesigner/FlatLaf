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
		label22 = new JLabel();
		label23 = new JLabel();
		label28 = new JLabel();
		label24 = new JLabel();
		label29 = new JLabel();
		label17 = new JLabel();
		testStateCheckBox1 = new FlatThemePreviewSwitches.TestStateCheckBox();
		testStateCheckBox8 = new FlatThemePreviewSwitches.TestStateCheckBox();
		testStateCheckBox5 = new FlatThemePreviewSwitches.TestStateCheckBox();
		testStateCheckBox12 = new FlatThemePreviewSwitches.TestStateCheckBox();
		label18 = new JLabel();
		testStateCheckBox2 = new FlatThemePreviewSwitches.TestStateCheckBox();
		testStateCheckBox9 = new FlatThemePreviewSwitches.TestStateCheckBox();
		testStateCheckBox6 = new FlatThemePreviewSwitches.TestStateCheckBox();
		testStateCheckBox13 = new FlatThemePreviewSwitches.TestStateCheckBox();
		label19 = new JLabel();
		testStateCheckBox3 = new FlatThemePreviewSwitches.TestStateCheckBox();
		testStateCheckBox10 = new FlatThemePreviewSwitches.TestStateCheckBox();
		testStateCheckBox7 = new FlatThemePreviewSwitches.TestStateCheckBox();
		testStateCheckBox14 = new FlatThemePreviewSwitches.TestStateCheckBox();
		label20 = new JLabel();
		testStateCheckBox4 = new FlatThemePreviewSwitches.TestStateCheckBox();
		testStateCheckBox11 = new FlatThemePreviewSwitches.TestStateCheckBox();
		label21 = new JLabel();
		checkBox1 = new JCheckBox();
		checkBox2 = new JCheckBox();
		label27 = new JLabel();
		label25 = new JLabel();
		label30 = new JLabel();
		label26 = new JLabel();
		label31 = new JLabel();
		label36 = new JLabel();
		testStateRadioButton1 = new FlatThemePreviewSwitches.TestStateRadioButton();
		testStateRadioButton8 = new FlatThemePreviewSwitches.TestStateRadioButton();
		testStateRadioButton5 = new FlatThemePreviewSwitches.TestStateRadioButton();
		testStateRadioButton9 = new FlatThemePreviewSwitches.TestStateRadioButton();
		label35 = new JLabel();
		testStateRadioButton2 = new FlatThemePreviewSwitches.TestStateRadioButton();
		testStateRadioButton10 = new FlatThemePreviewSwitches.TestStateRadioButton();
		testStateRadioButton6 = new FlatThemePreviewSwitches.TestStateRadioButton();
		testStateRadioButton11 = new FlatThemePreviewSwitches.TestStateRadioButton();
		label34 = new JLabel();
		testStateRadioButton3 = new FlatThemePreviewSwitches.TestStateRadioButton();
		testStateRadioButton12 = new FlatThemePreviewSwitches.TestStateRadioButton();
		testStateRadioButton7 = new FlatThemePreviewSwitches.TestStateRadioButton();
		testStateRadioButton13 = new FlatThemePreviewSwitches.TestStateRadioButton();
		label33 = new JLabel();
		testStateRadioButton4 = new FlatThemePreviewSwitches.TestStateRadioButton();
		testStateRadioButton14 = new FlatThemePreviewSwitches.TestStateRadioButton();
		label32 = new JLabel();
		radioButton1 = new JRadioButton();
		radioButton2 = new JRadioButton();

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
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]unrel" +
			"[]para" +
			"[]" +
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

		//---- label23 ----
		label23.setText("unsel.");
		add(label23, "cell 1 1");

		//---- label28 ----
		label28.setText("selected");
		add(label28, "cell 2 1");

		//---- label24 ----
		label24.setText("focused");
		add(label24, "cell 3 1");

		//---- label29 ----
		label29.setText("selected");
		add(label29, "cell 4 1");

		//---- label17 ----
		label17.setText("none");
		add(label17, "cell 0 2");

		//---- testStateCheckBox1 ----
		testStateCheckBox1.setText("text");
		add(testStateCheckBox1, "cell 1 2");

		//---- testStateCheckBox8 ----
		testStateCheckBox8.setText("text");
		testStateCheckBox8.setStateSelected(true);
		add(testStateCheckBox8, "cell 2 2");

		//---- testStateCheckBox5 ----
		testStateCheckBox5.setText("text");
		testStateCheckBox5.setStateFocused(true);
		add(testStateCheckBox5, "cell 3 2");

		//---- testStateCheckBox12 ----
		testStateCheckBox12.setText("text");
		testStateCheckBox12.setStateFocused(true);
		testStateCheckBox12.setStateSelected(true);
		add(testStateCheckBox12, "cell 4 2");

		//---- label18 ----
		label18.setText("hover");
		add(label18, "cell 0 3");

		//---- testStateCheckBox2 ----
		testStateCheckBox2.setText("text");
		testStateCheckBox2.setStateHover(true);
		add(testStateCheckBox2, "cell 1 3");

		//---- testStateCheckBox9 ----
		testStateCheckBox9.setText("text");
		testStateCheckBox9.setStateHover(true);
		testStateCheckBox9.setStateSelected(true);
		add(testStateCheckBox9, "cell 2 3");

		//---- testStateCheckBox6 ----
		testStateCheckBox6.setText("text");
		testStateCheckBox6.setStateFocused(true);
		testStateCheckBox6.setStateHover(true);
		add(testStateCheckBox6, "cell 3 3");

		//---- testStateCheckBox13 ----
		testStateCheckBox13.setText("text");
		testStateCheckBox13.setStateFocused(true);
		testStateCheckBox13.setStateHover(true);
		testStateCheckBox13.setStateSelected(true);
		add(testStateCheckBox13, "cell 4 3");

		//---- label19 ----
		label19.setText("pressed");
		add(label19, "cell 0 4");

		//---- testStateCheckBox3 ----
		testStateCheckBox3.setText("text");
		testStateCheckBox3.setStatePressed(true);
		add(testStateCheckBox3, "cell 1 4");

		//---- testStateCheckBox10 ----
		testStateCheckBox10.setText("text");
		testStateCheckBox10.setStatePressed(true);
		testStateCheckBox10.setStateSelected(true);
		add(testStateCheckBox10, "cell 2 4");

		//---- testStateCheckBox7 ----
		testStateCheckBox7.setText("text");
		testStateCheckBox7.setStateFocused(true);
		testStateCheckBox7.setStatePressed(true);
		add(testStateCheckBox7, "cell 3 4");

		//---- testStateCheckBox14 ----
		testStateCheckBox14.setText("text");
		testStateCheckBox14.setStateFocused(true);
		testStateCheckBox14.setStatePressed(true);
		testStateCheckBox14.setStateSelected(true);
		add(testStateCheckBox14, "cell 4 4");

		//---- label20 ----
		label20.setText("disabled");
		add(label20, "cell 0 5");

		//---- testStateCheckBox4 ----
		testStateCheckBox4.setText("text");
		testStateCheckBox4.setEnabled(false);
		add(testStateCheckBox4, "cell 1 5");

		//---- testStateCheckBox11 ----
		testStateCheckBox11.setText("text");
		testStateCheckBox11.setEnabled(false);
		testStateCheckBox11.setStateSelected(true);
		add(testStateCheckBox11, "cell 2 5");

		//---- label21 ----
		label21.setText("try me");
		add(label21, "cell 0 6");

		//---- checkBox1 ----
		checkBox1.setText("text");
		add(checkBox1, "cell 1 6");

		//---- checkBox2 ----
		checkBox2.setText("text");
		checkBox2.setSelected(true);
		add(checkBox2, "cell 2 6");

		//---- label27 ----
		label27.setText("JRadioButton");
		label27.setFont(label27.getFont().deriveFont(label27.getFont().getSize() + 4f));
		add(label27, "cell 0 7 3 1");

		//---- label25 ----
		label25.setText("unsel.");
		add(label25, "cell 1 8");

		//---- label30 ----
		label30.setText("selected");
		add(label30, "cell 2 8");

		//---- label26 ----
		label26.setText("focused");
		add(label26, "cell 3 8");

		//---- label31 ----
		label31.setText("selected");
		add(label31, "cell 4 8");

		//---- label36 ----
		label36.setText("none");
		add(label36, "cell 0 9");

		//---- testStateRadioButton1 ----
		testStateRadioButton1.setText("text");
		add(testStateRadioButton1, "cell 1 9");

		//---- testStateRadioButton8 ----
		testStateRadioButton8.setText("text");
		testStateRadioButton8.setStateSelected(true);
		add(testStateRadioButton8, "cell 2 9");

		//---- testStateRadioButton5 ----
		testStateRadioButton5.setText("text");
		testStateRadioButton5.setStateFocused(true);
		add(testStateRadioButton5, "cell 3 9");

		//---- testStateRadioButton9 ----
		testStateRadioButton9.setText("text");
		testStateRadioButton9.setStateFocused(true);
		testStateRadioButton9.setStateSelected(true);
		add(testStateRadioButton9, "cell 4 9");

		//---- label35 ----
		label35.setText("hover");
		add(label35, "cell 0 10");

		//---- testStateRadioButton2 ----
		testStateRadioButton2.setText("text");
		testStateRadioButton2.setStateHover(true);
		add(testStateRadioButton2, "cell 1 10");

		//---- testStateRadioButton10 ----
		testStateRadioButton10.setText("text");
		testStateRadioButton10.setStateHover(true);
		testStateRadioButton10.setStateSelected(true);
		add(testStateRadioButton10, "cell 2 10");

		//---- testStateRadioButton6 ----
		testStateRadioButton6.setText("text");
		testStateRadioButton6.setStateFocused(true);
		testStateRadioButton6.setStateHover(true);
		add(testStateRadioButton6, "cell 3 10");

		//---- testStateRadioButton11 ----
		testStateRadioButton11.setText("text");
		testStateRadioButton11.setStateFocused(true);
		testStateRadioButton11.setStateHover(true);
		testStateRadioButton11.setStateSelected(true);
		add(testStateRadioButton11, "cell 4 10");

		//---- label34 ----
		label34.setText("pressed");
		add(label34, "cell 0 11");

		//---- testStateRadioButton3 ----
		testStateRadioButton3.setText("text");
		testStateRadioButton3.setStatePressed(true);
		add(testStateRadioButton3, "cell 1 11");

		//---- testStateRadioButton12 ----
		testStateRadioButton12.setText("text");
		testStateRadioButton12.setStatePressed(true);
		testStateRadioButton12.setStateSelected(true);
		add(testStateRadioButton12, "cell 2 11");

		//---- testStateRadioButton7 ----
		testStateRadioButton7.setText("text");
		testStateRadioButton7.setStateFocused(true);
		testStateRadioButton7.setStatePressed(true);
		add(testStateRadioButton7, "cell 3 11");

		//---- testStateRadioButton13 ----
		testStateRadioButton13.setText("text");
		testStateRadioButton13.setStateFocused(true);
		testStateRadioButton13.setStatePressed(true);
		testStateRadioButton13.setStateSelected(true);
		add(testStateRadioButton13, "cell 4 11");

		//---- label33 ----
		label33.setText("disabled");
		add(label33, "cell 0 12");

		//---- testStateRadioButton4 ----
		testStateRadioButton4.setText("text");
		testStateRadioButton4.setEnabled(false);
		add(testStateRadioButton4, "cell 1 12");

		//---- testStateRadioButton14 ----
		testStateRadioButton14.setText("text");
		testStateRadioButton14.setEnabled(false);
		testStateRadioButton14.setStateSelected(true);
		add(testStateRadioButton14, "cell 2 12");

		//---- label32 ----
		label32.setText("try me");
		add(label32, "cell 0 13");

		//---- radioButton1 ----
		radioButton1.setText("text");
		add(radioButton1, "cell 1 13");

		//---- radioButton2 ----
		radioButton2.setText("text");
		radioButton2.setSelected(true);
		add(radioButton2, "cell 2 13");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel label22;
	private JLabel label23;
	private JLabel label28;
	private JLabel label24;
	private JLabel label29;
	private JLabel label17;
	private FlatThemePreviewSwitches.TestStateCheckBox testStateCheckBox1;
	private FlatThemePreviewSwitches.TestStateCheckBox testStateCheckBox8;
	private FlatThemePreviewSwitches.TestStateCheckBox testStateCheckBox5;
	private FlatThemePreviewSwitches.TestStateCheckBox testStateCheckBox12;
	private JLabel label18;
	private FlatThemePreviewSwitches.TestStateCheckBox testStateCheckBox2;
	private FlatThemePreviewSwitches.TestStateCheckBox testStateCheckBox9;
	private FlatThemePreviewSwitches.TestStateCheckBox testStateCheckBox6;
	private FlatThemePreviewSwitches.TestStateCheckBox testStateCheckBox13;
	private JLabel label19;
	private FlatThemePreviewSwitches.TestStateCheckBox testStateCheckBox3;
	private FlatThemePreviewSwitches.TestStateCheckBox testStateCheckBox10;
	private FlatThemePreviewSwitches.TestStateCheckBox testStateCheckBox7;
	private FlatThemePreviewSwitches.TestStateCheckBox testStateCheckBox14;
	private JLabel label20;
	private FlatThemePreviewSwitches.TestStateCheckBox testStateCheckBox4;
	private FlatThemePreviewSwitches.TestStateCheckBox testStateCheckBox11;
	private JLabel label21;
	private JCheckBox checkBox1;
	private JCheckBox checkBox2;
	private JLabel label27;
	private JLabel label25;
	private JLabel label30;
	private JLabel label26;
	private JLabel label31;
	private JLabel label36;
	private FlatThemePreviewSwitches.TestStateRadioButton testStateRadioButton1;
	private FlatThemePreviewSwitches.TestStateRadioButton testStateRadioButton8;
	private FlatThemePreviewSwitches.TestStateRadioButton testStateRadioButton5;
	private FlatThemePreviewSwitches.TestStateRadioButton testStateRadioButton9;
	private JLabel label35;
	private FlatThemePreviewSwitches.TestStateRadioButton testStateRadioButton2;
	private FlatThemePreviewSwitches.TestStateRadioButton testStateRadioButton10;
	private FlatThemePreviewSwitches.TestStateRadioButton testStateRadioButton6;
	private FlatThemePreviewSwitches.TestStateRadioButton testStateRadioButton11;
	private JLabel label34;
	private FlatThemePreviewSwitches.TestStateRadioButton testStateRadioButton3;
	private FlatThemePreviewSwitches.TestStateRadioButton testStateRadioButton12;
	private FlatThemePreviewSwitches.TestStateRadioButton testStateRadioButton7;
	private FlatThemePreviewSwitches.TestStateRadioButton testStateRadioButton13;
	private JLabel label33;
	private FlatThemePreviewSwitches.TestStateRadioButton testStateRadioButton4;
	private FlatThemePreviewSwitches.TestStateRadioButton testStateRadioButton14;
	private JLabel label32;
	private JRadioButton radioButton1;
	private JRadioButton radioButton2;
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
