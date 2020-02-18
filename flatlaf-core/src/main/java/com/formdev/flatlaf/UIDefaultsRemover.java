/*
 * Copyright 2020 FormDev Software GmbH
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

package com.formdev.flatlaf;

import java.util.Iterator;
import javax.swing.UIDefaults;

/**
 * Removes UI defaults that are defined in "base" LaF (Metal or Aqua), but not used in FlatLaf.
 *
 * This is a temporary class that can be removed when dropping "base" LaF.
 *
 * @author Karl Tauber
 */
class UIDefaultsRemover
{
	static final String[] REMOVE_KEYS = {
		"AuditoryCues.defaultCueList",

		"Button.disabledToolBarBorderBackground",
		"Button.focus",
		"Button.rolloverIconType",
		"Button.select",
		"Button.toolBarBorderBackground",

		"CheckBox.focus",
		"CheckBox.select",
		"Checkbox.select", // this is a typo in MetalLookAndFeel
		"CheckBox.totalInsets",

		"DesktopIcon.font",
		"DesktopIcon.width",

		"InternalFrame.activeTitleGradient",
		"InternalFrame.optionDialogBorder",
		"InternalFrame.paletteBorder",
		"InternalFrame.paletteCloseIcon",
		"InternalFrame.paletteTitleHeight",

		"Menu.checkIcon",

		"MenuItem.checkIcon",

		"OptionPane.errorDialog.border.background",
		"OptionPane.errorDialog.titlePane.background",
		"OptionPane.errorDialog.titlePane.foreground",
		"OptionPane.errorDialog.titlePane.shadow",
		"OptionPane.questionDialog.border.background",
		"OptionPane.questionDialog.titlePane.background",
		"OptionPane.questionDialog.titlePane.foreground",
		"OptionPane.questionDialog.titlePane.shadow",
		"OptionPane.warningDialog.border.background",
		"OptionPane.warningDialog.titlePane.background",
		"OptionPane.warningDialog.titlePane.foreground",
		"OptionPane.warningDialog.titlePane.shadow",

		"RadioButton.focus",
		"RadioButton.select",
		"RadioButton.totalInsets",

		"RootPane.colorChooserDialogBorder",
		"RootPane.errorDialogBorder",
		"RootPane.fileChooserDialogBorder",
		"RootPane.frameBorder",
		"RootPane.informationDialogBorder",
		"RootPane.plainDialogBorder",
		"RootPane.questionDialogBorder",
		"RootPane.warningDialogBorder",

		"ScrollBar.darkShadow",
		"ScrollBar.highlight",
		"ScrollBar.shadow",

		"Slider.altTrackColor",
		"Slider.focusGradient",
		"Slider.horizontalThumbIcon",
		"Slider.majorTickLength",
		"Slider.verticalThumbIcon",

		"Spinner.arrowButtonBorder",
		"Spinner.arrowButtonInsets",

		"SplitPane.dividerFocusColor",
		"SplitPane.oneTouchButtonsOpaque",

		"TabbedPane.borderHightlightColor",
		"TabbedPane.unselectedBackground",
		"TabbedPane.tabAreaBackground",
		"TabbedPane.selectHighlight",
		"TabbedPane.selected",

		"ToggleButton.focus",
		"ToggleButton.select",

		"ToolBar.borderColor",
		"ToolBar.nonrolloverBorder",
		"ToolBar.rolloverBorder",

		"ToolTip.hideAccelerator",

		"Tree.line",
	};

	static void removeDefaults( UIDefaults defaults ) {
		for( String key : REMOVE_KEYS )
			defaults.remove( key );

		Iterator<Object> itr = defaults.keySet().iterator();
		while( itr.hasNext() ) {
			Object key = itr.next();
			if( key instanceof String &&
				(((String)key).endsWith( ".gradient" ) ||
				 ((String)key).endsWith( "Sound" )) )
			{
				itr.remove();
			}
		}
	}
}
