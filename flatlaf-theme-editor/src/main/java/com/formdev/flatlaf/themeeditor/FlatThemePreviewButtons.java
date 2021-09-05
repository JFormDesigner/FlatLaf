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
class FlatThemePreviewButtons
	extends JPanel
{
	FlatThemePreviewButtons() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		label11 = new JLabel();
		label5 = new JLabel();
		label7 = new JLabel();
		label6 = new JLabel();
		label8 = new JLabel();
		label1 = new JLabel();
		testStateButton1 = new FlatThemePreviewButtons.TestStateButton();
		testStateButton7 = new FlatThemePreviewButtons.TestStateButton();
		testStateButton4 = new FlatThemePreviewButtons.TestStateButton();
		testStateButton10 = new FlatThemePreviewButtons.TestStateButton();
		label2 = new JLabel();
		testStateButton2 = new FlatThemePreviewButtons.TestStateButton();
		testStateButton8 = new FlatThemePreviewButtons.TestStateButton();
		testStateButton5 = new FlatThemePreviewButtons.TestStateButton();
		testStateButton11 = new FlatThemePreviewButtons.TestStateButton();
		label3 = new JLabel();
		testStateButton3 = new FlatThemePreviewButtons.TestStateButton();
		testStateButton9 = new FlatThemePreviewButtons.TestStateButton();
		testStateButton6 = new FlatThemePreviewButtons.TestStateButton();
		testStateButton12 = new FlatThemePreviewButtons.TestStateButton();
		label4 = new JLabel();
		testStateButton13 = new FlatThemePreviewButtons.TestStateButton();
		testStateButton14 = new FlatThemePreviewButtons.TestStateButton();
		label10 = new JLabel();
		button1 = new JButton();
		testDefaultButton1 = new FlatThemePreviewButtons.TestDefaultButton();
		label12 = new JLabel();
		label13 = new JLabel();
		label14 = new JLabel();
		label15 = new JLabel();
		label16 = new JLabel();
		label17 = new JLabel();
		testStateToggleButton1 = new FlatThemePreviewButtons.TestStateToggleButton();
		testStateToggleButton5 = new FlatThemePreviewButtons.TestStateToggleButton();
		testStateToggleButton9 = new FlatThemePreviewButtons.TestStateToggleButton();
		testStateToggleButton12 = new FlatThemePreviewButtons.TestStateToggleButton();
		label18 = new JLabel();
		testStateToggleButton2 = new FlatThemePreviewButtons.TestStateToggleButton();
		testStateToggleButton6 = new FlatThemePreviewButtons.TestStateToggleButton();
		testStateToggleButton10 = new FlatThemePreviewButtons.TestStateToggleButton();
		testStateToggleButton13 = new FlatThemePreviewButtons.TestStateToggleButton();
		label19 = new JLabel();
		testStateToggleButton3 = new FlatThemePreviewButtons.TestStateToggleButton();
		testStateToggleButton7 = new FlatThemePreviewButtons.TestStateToggleButton();
		testStateToggleButton11 = new FlatThemePreviewButtons.TestStateToggleButton();
		testStateToggleButton14 = new FlatThemePreviewButtons.TestStateToggleButton();
		label20 = new JLabel();
		testStateToggleButton4 = new FlatThemePreviewButtons.TestStateToggleButton();
		testStateToggleButton8 = new FlatThemePreviewButtons.TestStateToggleButton();
		label21 = new JLabel();
		toggleButton1 = new JToggleButton();
		toggleButton2 = new JToggleButton();
		label32 = new JLabel();
		label9 = new JLabel();
		label33 = new JLabel();
		label22 = new JLabel();
		testStateButton15 = new FlatThemePreviewButtons.TestStateButton();
		testStateButton19 = new FlatThemePreviewButtons.TestStateButton();
		label23 = new JLabel();
		testStateButton16 = new FlatThemePreviewButtons.TestStateButton();
		testStateButton20 = new FlatThemePreviewButtons.TestStateButton();
		label24 = new JLabel();
		testStateButton17 = new FlatThemePreviewButtons.TestStateButton();
		testStateButton21 = new FlatThemePreviewButtons.TestStateButton();
		label25 = new JLabel();
		testStateButton18 = new FlatThemePreviewButtons.TestStateButton();
		label26 = new JLabel();
		button2 = new JButton();

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
			"[]para" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]" +
			"[]unrel" +
			"[]"));

		//---- label11 ----
		label11.setText("JButton");
		label11.setFont(label11.getFont().deriveFont(label11.getFont().getSize() + 4f));
		add(label11, "cell 0 0 3 1");

		//---- label5 ----
		label5.setText("regular");
		add(label5, "cell 1 1");

		//---- label7 ----
		label7.setText("default");
		add(label7, "cell 2 1");

		//---- label6 ----
		label6.setText("focused");
		add(label6, "cell 3 1");

		//---- label8 ----
		label8.setText("default");
		add(label8, "cell 4 1");

		//---- label1 ----
		label1.setText("none");
		add(label1, "cell 0 2");

		//---- testStateButton1 ----
		testStateButton1.setText("OK");
		testStateButton1.putClientProperty("JComponent.minimumWidth", 0);
		add(testStateButton1, "cell 1 2");

		//---- testStateButton7 ----
		testStateButton7.setText("OK");
		testStateButton7.setStateDefault(true);
		testStateButton7.putClientProperty("JComponent.minimumWidth", 0);
		add(testStateButton7, "cell 2 2");

		//---- testStateButton4 ----
		testStateButton4.setText("OK");
		testStateButton4.setStateFocused(true);
		testStateButton4.putClientProperty("JComponent.minimumWidth", 0);
		add(testStateButton4, "cell 3 2");

		//---- testStateButton10 ----
		testStateButton10.setText("OK");
		testStateButton10.setStateFocused(true);
		testStateButton10.setStateDefault(true);
		testStateButton10.putClientProperty("JComponent.minimumWidth", 0);
		add(testStateButton10, "cell 4 2");

		//---- label2 ----
		label2.setText("hover");
		add(label2, "cell 0 3");

		//---- testStateButton2 ----
		testStateButton2.setText("OK");
		testStateButton2.setStateHover(true);
		testStateButton2.putClientProperty("JComponent.minimumWidth", 0);
		add(testStateButton2, "cell 1 3");

		//---- testStateButton8 ----
		testStateButton8.setText("OK");
		testStateButton8.setStateHover(true);
		testStateButton8.setStateDefault(true);
		testStateButton8.putClientProperty("JComponent.minimumWidth", 0);
		add(testStateButton8, "cell 2 3");

		//---- testStateButton5 ----
		testStateButton5.setText("OK");
		testStateButton5.setStateHover(true);
		testStateButton5.setStateFocused(true);
		testStateButton5.putClientProperty("JComponent.minimumWidth", 0);
		add(testStateButton5, "cell 3 3");

		//---- testStateButton11 ----
		testStateButton11.setText("OK");
		testStateButton11.setStateHover(true);
		testStateButton11.setStateFocused(true);
		testStateButton11.setStateDefault(true);
		testStateButton11.putClientProperty("JComponent.minimumWidth", 0);
		add(testStateButton11, "cell 4 3");

		//---- label3 ----
		label3.setText("pressed");
		add(label3, "cell 0 4");

		//---- testStateButton3 ----
		testStateButton3.setText("OK");
		testStateButton3.setStatePressed(true);
		testStateButton3.putClientProperty("JComponent.minimumWidth", 0);
		add(testStateButton3, "cell 1 4");

		//---- testStateButton9 ----
		testStateButton9.setText("OK");
		testStateButton9.setStatePressed(true);
		testStateButton9.setStateDefault(true);
		testStateButton9.putClientProperty("JComponent.minimumWidth", 0);
		add(testStateButton9, "cell 2 4");

		//---- testStateButton6 ----
		testStateButton6.setText("OK");
		testStateButton6.setStatePressed(true);
		testStateButton6.setStateFocused(true);
		testStateButton6.putClientProperty("JComponent.minimumWidth", 0);
		add(testStateButton6, "cell 3 4");

		//---- testStateButton12 ----
		testStateButton12.setText("OK");
		testStateButton12.setStatePressed(true);
		testStateButton12.setStateFocused(true);
		testStateButton12.setStateDefault(true);
		testStateButton12.putClientProperty("JComponent.minimumWidth", 0);
		add(testStateButton12, "cell 4 4");

		//---- label4 ----
		label4.setText("disabled");
		add(label4, "cell 0 5");

		//---- testStateButton13 ----
		testStateButton13.setText("OK");
		testStateButton13.setEnabled(false);
		testStateButton13.putClientProperty("JComponent.minimumWidth", 0);
		add(testStateButton13, "cell 1 5");

		//---- testStateButton14 ----
		testStateButton14.setText("OK");
		testStateButton14.setEnabled(false);
		testStateButton14.setStateDefault(true);
		testStateButton14.putClientProperty("JComponent.minimumWidth", 0);
		add(testStateButton14, "cell 2 5");

		//---- label10 ----
		label10.setText("try me");
		add(label10, "cell 0 6");

		//---- button1 ----
		button1.setText("OK");
		button1.putClientProperty("JComponent.minimumWidth", 0);
		add(button1, "cell 1 6");

		//---- testDefaultButton1 ----
		testDefaultButton1.setText("OK");
		testDefaultButton1.putClientProperty("JComponent.minimumWidth", 0);
		add(testDefaultButton1, "cell 2 6");

		//---- label12 ----
		label12.setText("JToggleButton");
		label12.setFont(label12.getFont().deriveFont(label12.getFont().getSize() + 4f));
		add(label12, "cell 0 7 3 1");

		//---- label13 ----
		label13.setText("unsel.");
		add(label13, "cell 1 8");

		//---- label14 ----
		label14.setText("selected");
		add(label14, "cell 2 8");

		//---- label15 ----
		label15.setText("focused");
		add(label15, "cell 3 8");

		//---- label16 ----
		label16.setText("selected");
		add(label16, "cell 4 8");

		//---- label17 ----
		label17.setText("none");
		add(label17, "cell 0 9");

		//---- testStateToggleButton1 ----
		testStateToggleButton1.setText("OK");
		add(testStateToggleButton1, "cell 1 9");

		//---- testStateToggleButton5 ----
		testStateToggleButton5.setText("OK");
		testStateToggleButton5.setStateSelected(true);
		add(testStateToggleButton5, "cell 2 9");

		//---- testStateToggleButton9 ----
		testStateToggleButton9.setText("OK");
		testStateToggleButton9.setStateFocused(true);
		add(testStateToggleButton9, "cell 3 9");

		//---- testStateToggleButton12 ----
		testStateToggleButton12.setText("OK");
		testStateToggleButton12.setStateSelected(true);
		testStateToggleButton12.setStateFocused(true);
		add(testStateToggleButton12, "cell 4 9");

		//---- label18 ----
		label18.setText("hover");
		add(label18, "cell 0 10");

		//---- testStateToggleButton2 ----
		testStateToggleButton2.setText("OK");
		testStateToggleButton2.setStateHover(true);
		add(testStateToggleButton2, "cell 1 10");

		//---- testStateToggleButton6 ----
		testStateToggleButton6.setText("OK");
		testStateToggleButton6.setStateHover(true);
		testStateToggleButton6.setStateSelected(true);
		add(testStateToggleButton6, "cell 2 10");

		//---- testStateToggleButton10 ----
		testStateToggleButton10.setText("OK");
		testStateToggleButton10.setStateHover(true);
		testStateToggleButton10.setStateFocused(true);
		add(testStateToggleButton10, "cell 3 10");

		//---- testStateToggleButton13 ----
		testStateToggleButton13.setText("OK");
		testStateToggleButton13.setStateHover(true);
		testStateToggleButton13.setStateSelected(true);
		testStateToggleButton13.setStateFocused(true);
		add(testStateToggleButton13, "cell 4 10");

		//---- label19 ----
		label19.setText("pressed");
		add(label19, "cell 0 11");

		//---- testStateToggleButton3 ----
		testStateToggleButton3.setText("OK");
		testStateToggleButton3.setStatePressed(true);
		add(testStateToggleButton3, "cell 1 11");

		//---- testStateToggleButton7 ----
		testStateToggleButton7.setText("OK");
		testStateToggleButton7.setStatePressed(true);
		testStateToggleButton7.setStateSelected(true);
		add(testStateToggleButton7, "cell 2 11");

		//---- testStateToggleButton11 ----
		testStateToggleButton11.setText("OK");
		testStateToggleButton11.setStatePressed(true);
		testStateToggleButton11.setStateFocused(true);
		add(testStateToggleButton11, "cell 3 11");

		//---- testStateToggleButton14 ----
		testStateToggleButton14.setText("OK");
		testStateToggleButton14.setStatePressed(true);
		testStateToggleButton14.setStateSelected(true);
		testStateToggleButton14.setStateFocused(true);
		add(testStateToggleButton14, "cell 4 11");

		//---- label20 ----
		label20.setText("disabled");
		add(label20, "cell 0 12");

		//---- testStateToggleButton4 ----
		testStateToggleButton4.setText("OK");
		testStateToggleButton4.setEnabled(false);
		add(testStateToggleButton4, "cell 1 12");

		//---- testStateToggleButton8 ----
		testStateToggleButton8.setText("OK");
		testStateToggleButton8.setEnabled(false);
		testStateToggleButton8.setStateSelected(true);
		add(testStateToggleButton8, "cell 2 12");

		//---- label21 ----
		label21.setText("try me");
		add(label21, "cell 0 13");

		//---- toggleButton1 ----
		toggleButton1.setText("OK");
		add(toggleButton1, "cell 1 13");

		//---- toggleButton2 ----
		toggleButton2.setText("OK");
		toggleButton2.setSelected(true);
		add(toggleButton2, "cell 2 13");

		//---- label32 ----
		label32.setText("Help Button");
		label32.setFont(label32.getFont().deriveFont(label32.getFont().getSize() + 4f));
		add(label32, "cell 0 14 2 1");

		//---- label9 ----
		label9.setText("regular");
		add(label9, "cell 1 15");

		//---- label33 ----
		label33.setText("focused");
		add(label33, "cell 2 15");

		//---- label22 ----
		label22.setText("none");
		add(label22, "cell 0 16");

		//---- testStateButton15 ----
		testStateButton15.putClientProperty("JComponent.minimumWidth", 0);
		testStateButton15.putClientProperty("JButton.buttonType", "help");
		add(testStateButton15, "cell 1 16");

		//---- testStateButton19 ----
		testStateButton19.setStateFocused(true);
		testStateButton19.putClientProperty("JComponent.minimumWidth", 0);
		testStateButton19.putClientProperty("JButton.buttonType", "help");
		add(testStateButton19, "cell 2 16");

		//---- label23 ----
		label23.setText("hover");
		add(label23, "cell 0 17");

		//---- testStateButton16 ----
		testStateButton16.setStateHover(true);
		testStateButton16.putClientProperty("JComponent.minimumWidth", 0);
		testStateButton16.putClientProperty("JButton.buttonType", "help");
		add(testStateButton16, "cell 1 17");

		//---- testStateButton20 ----
		testStateButton20.setStateHover(true);
		testStateButton20.setStateFocused(true);
		testStateButton20.putClientProperty("JComponent.minimumWidth", 0);
		testStateButton20.putClientProperty("JButton.buttonType", "help");
		add(testStateButton20, "cell 2 17");

		//---- label24 ----
		label24.setText("pressed");
		add(label24, "cell 0 18");

		//---- testStateButton17 ----
		testStateButton17.setStatePressed(true);
		testStateButton17.putClientProperty("JComponent.minimumWidth", 0);
		testStateButton17.putClientProperty("JButton.buttonType", "help");
		add(testStateButton17, "cell 1 18");

		//---- testStateButton21 ----
		testStateButton21.setStatePressed(true);
		testStateButton21.setStateFocused(true);
		testStateButton21.putClientProperty("JComponent.minimumWidth", 0);
		testStateButton21.putClientProperty("JButton.buttonType", "help");
		add(testStateButton21, "cell 2 18");

		//---- label25 ----
		label25.setText("disabled");
		add(label25, "cell 0 19");

		//---- testStateButton18 ----
		testStateButton18.setEnabled(false);
		testStateButton18.putClientProperty("JComponent.minimumWidth", 0);
		testStateButton18.putClientProperty("JButton.buttonType", "help");
		add(testStateButton18, "cell 1 19");

		//---- label26 ----
		label26.setText("try me");
		add(label26, "cell 0 20");

		//---- button2 ----
		button2.putClientProperty("JComponent.minimumWidth", 0);
		button2.putClientProperty("JButton.buttonType", "help");
		add(button2, "cell 1 20");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel label11;
	private JLabel label5;
	private JLabel label7;
	private JLabel label6;
	private JLabel label8;
	private JLabel label1;
	private FlatThemePreviewButtons.TestStateButton testStateButton1;
	private FlatThemePreviewButtons.TestStateButton testStateButton7;
	private FlatThemePreviewButtons.TestStateButton testStateButton4;
	private FlatThemePreviewButtons.TestStateButton testStateButton10;
	private JLabel label2;
	private FlatThemePreviewButtons.TestStateButton testStateButton2;
	private FlatThemePreviewButtons.TestStateButton testStateButton8;
	private FlatThemePreviewButtons.TestStateButton testStateButton5;
	private FlatThemePreviewButtons.TestStateButton testStateButton11;
	private JLabel label3;
	private FlatThemePreviewButtons.TestStateButton testStateButton3;
	private FlatThemePreviewButtons.TestStateButton testStateButton9;
	private FlatThemePreviewButtons.TestStateButton testStateButton6;
	private FlatThemePreviewButtons.TestStateButton testStateButton12;
	private JLabel label4;
	private FlatThemePreviewButtons.TestStateButton testStateButton13;
	private FlatThemePreviewButtons.TestStateButton testStateButton14;
	private JLabel label10;
	private JButton button1;
	private FlatThemePreviewButtons.TestDefaultButton testDefaultButton1;
	private JLabel label12;
	private JLabel label13;
	private JLabel label14;
	private JLabel label15;
	private JLabel label16;
	private JLabel label17;
	private FlatThemePreviewButtons.TestStateToggleButton testStateToggleButton1;
	private FlatThemePreviewButtons.TestStateToggleButton testStateToggleButton5;
	private FlatThemePreviewButtons.TestStateToggleButton testStateToggleButton9;
	private FlatThemePreviewButtons.TestStateToggleButton testStateToggleButton12;
	private JLabel label18;
	private FlatThemePreviewButtons.TestStateToggleButton testStateToggleButton2;
	private FlatThemePreviewButtons.TestStateToggleButton testStateToggleButton6;
	private FlatThemePreviewButtons.TestStateToggleButton testStateToggleButton10;
	private FlatThemePreviewButtons.TestStateToggleButton testStateToggleButton13;
	private JLabel label19;
	private FlatThemePreviewButtons.TestStateToggleButton testStateToggleButton3;
	private FlatThemePreviewButtons.TestStateToggleButton testStateToggleButton7;
	private FlatThemePreviewButtons.TestStateToggleButton testStateToggleButton11;
	private FlatThemePreviewButtons.TestStateToggleButton testStateToggleButton14;
	private JLabel label20;
	private FlatThemePreviewButtons.TestStateToggleButton testStateToggleButton4;
	private FlatThemePreviewButtons.TestStateToggleButton testStateToggleButton8;
	private JLabel label21;
	private JToggleButton toggleButton1;
	private JToggleButton toggleButton2;
	private JLabel label32;
	private JLabel label9;
	private JLabel label33;
	private JLabel label22;
	private FlatThemePreviewButtons.TestStateButton testStateButton15;
	private FlatThemePreviewButtons.TestStateButton testStateButton19;
	private JLabel label23;
	private FlatThemePreviewButtons.TestStateButton testStateButton16;
	private FlatThemePreviewButtons.TestStateButton testStateButton20;
	private JLabel label24;
	private FlatThemePreviewButtons.TestStateButton testStateButton17;
	private FlatThemePreviewButtons.TestStateButton testStateButton21;
	private JLabel label25;
	private FlatThemePreviewButtons.TestStateButton testStateButton18;
	private JLabel label26;
	private JButton button2;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class TestStateButton ----------------------------------------------

	private static class TestStateButton
		extends JButton
	{
		private boolean stateHover;
		private boolean statePressed;
		private boolean stateFocused;
		private boolean stateDefault;

		public TestStateButton() {
			setModel( new DefaultButtonModel() {
				@Override
				public boolean isRollover() {
					return isStateHover();
				}
				@Override
				public boolean isPressed() {
					return isStatePressed();
				}
			} );

			putClientProperty( FlatClientProperties.COMPONENT_FOCUS_OWNER,
				(Predicate<JComponent>) c -> {
					return ((TestStateButton)c).isStateFocused();
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

		public boolean isStateDefault() {
			return stateDefault;
		}

		public void setStateDefault( boolean stateDefault ) {
			this.stateDefault = stateDefault;
		}

		@Override
		public boolean isDefaultButton() {
			return isStateDefault();
		}
	}

	//---- class TestStateToggleButton ----------------------------------------

	private static class TestStateToggleButton
		extends JToggleButton
	{
		private boolean stateHover;
		private boolean statePressed;
		private boolean stateFocused;
		private boolean stateSelected;

		public TestStateToggleButton() {
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
					return ((TestStateToggleButton)c).isStateFocused();
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

	//---- class TestDefaultButton --------------------------------------------

	private static class TestDefaultButton
		extends JButton
	{
		@Override
		public boolean isDefaultButton() {
			return true;
		}
	}
}
