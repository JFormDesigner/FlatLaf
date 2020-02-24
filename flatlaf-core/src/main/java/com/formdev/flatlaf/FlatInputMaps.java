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

import javax.swing.InputMap;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIDefaults.LazyValue;
import javax.swing.plaf.InputMapUIResource;
import com.formdev.flatlaf.util.SystemInfo;
import static javax.swing.text.DefaultEditorKit.*;

/**
 * @author Karl Tauber
 */
class FlatInputMaps
{
	static void initInputMaps( UIDefaults defaults ) {
		initBasicInputMaps( defaults );
		initTextComponentInputMaps( defaults );

		if( SystemInfo.IS_MAC )
			initMacInputMaps( defaults );
	}

	private static void initBasicInputMaps( UIDefaults defaults ) {
		defaults.put( "Button.focusInputMap", new UIDefaults.LazyInputMap( new Object[] {
			"SPACE", "pressed",
			"released SPACE", "released"
		} ) );

		modifyInputMap( defaults, "ComboBox.ancestorInputMap",
			"SPACE", "spacePopup",

			"UP", "selectPrevious",
			"DOWN", "selectNext",
			"KP_UP", "selectPrevious",
			"KP_DOWN", "selectNext",

			mac( "alt UP", null ), "togglePopup",
			mac( "alt DOWN", null ), "togglePopup",
			mac( "alt KP_UP", null ), "togglePopup",
			mac( "alt KP_DOWN", null ), "togglePopup"
		);

		if( !SystemInfo.IS_MAC ) {
			modifyInputMap( defaults, "FileChooser.ancestorInputMap",
				"F2", "editFileName",
				"BACK_SPACE", "Go Up"
			);

			modifyInputMap( defaults, "Slider.focusInputMap",
				"ctrl PAGE_DOWN", "negativeBlockIncrement",
				"ctrl PAGE_UP", "positiveBlockIncrement"
			);

			modifyInputMap( defaults, "Tree.focusInputMap",
				"ADD", "expand",
				"SUBTRACT", "collapse"
			);
		}
	}

	private static void initTextComponentInputMaps( UIDefaults defaults ) {
		Object[] commonTextComponentBindings = {
			// move caret one character (without selecting text)
			"LEFT", backwardAction,
			"RIGHT", forwardAction,
			"KP_LEFT", backwardAction,
			"KP_RIGHT", forwardAction,

			// move caret one character and select text
			"shift LEFT", selectionBackwardAction,
			"shift RIGHT", selectionForwardAction,
			"shift KP_LEFT", selectionBackwardAction,
			"shift KP_RIGHT", selectionForwardAction,

			// move caret to word (without selecting text)
			mac( "ctrl LEFT", "alt LEFT" ), previousWordAction,
			mac( "ctrl RIGHT", "alt RIGHT" ), nextWordAction,
			mac( "ctrl KP_LEFT", "alt KP_LEFT" ), previousWordAction,
			mac( "ctrl KP_RIGHT", "alt KP_RIGHT" ), nextWordAction,

			// move caret to word and select text
			mac( "ctrl shift LEFT", "shift alt LEFT" ), selectionPreviousWordAction,
			mac( "ctrl shift RIGHT", "shift alt RIGHT" ), selectionNextWordAction,
			mac( "ctrl shift KP_LEFT", "shift alt KP_LEFT" ), selectionPreviousWordAction,
			mac( "ctrl shift KP_RIGHT", "shift alt KP_RIGHT" ), selectionNextWordAction,

			// move caret to line begin/end (without selecting text)
			mac( "HOME", "meta LEFT" ), beginLineAction,
			mac( "END", "meta RIGHT" ), endLineAction,

			// move caret to line begin/end and select text
			mac( "shift HOME", "shift meta LEFT" ), selectionBeginLineAction,
			mac( "shift END", "shift meta RIGHT" ), selectionEndLineAction,

			// select all/none
			mac( "ctrl A", "meta A" ), selectAllAction,
			mac( "ctrl BACK_SLASH", "meta BACK_SLASH" ), "unselect", // DefaultEditorKit.unselectAction

			// delete previous/next character
			"BACK_SPACE", deletePrevCharAction,
			"shift BACK_SPACE", deletePrevCharAction,
			"ctrl H", deletePrevCharAction,
			"DELETE", deleteNextCharAction,

			// delete previous/next word
			mac( "ctrl BACK_SPACE", "alt BACK_SPACE" ), deletePrevWordAction,
			mac( "ctrl DELETE", "alt DELETE" ), deleteNextWordAction,

			// clipboard
			mac( "ctrl X", "meta X" ), cutAction,
			mac( "ctrl C", "meta C" ), copyAction,
			mac( "ctrl V", "meta V" ), pasteAction,
			"CUT", cutAction,
			"COPY", copyAction,
			"PASTE", pasteAction,
			mac( "shift DELETE", null ), cutAction,
			mac( "control INSERT", null ), copyAction,
			mac( "shift INSERT", null ), pasteAction,

			// misc
			"control shift O", "toggle-componentOrientation", // DefaultEditorKit.toggleComponentOrientation
		};

		Object[] macCommonTextComponentBindings = SystemInfo.IS_MAC ? new Object[] {
			// move caret one character (without selecting text)
			"ctrl B", backwardAction,
			"ctrl F", forwardAction,

			// move caret to document begin/end (without selecting text)
			"HOME", beginAction,
			"END", endAction,
			"meta UP", beginAction,
			"meta DOWN", endAction,
			"meta KP_UP", beginAction,
			"meta KP_DOWN", endAction,
			"ctrl P", beginAction,
			"ctrl N", endAction,
			"ctrl V", endAction,

			// move caret to line begin/end (without selecting text)
			"meta KP_LEFT", beginLineAction,
			"meta KP_RIGHT", endLineAction,
			"ctrl A", beginLineAction,
			"ctrl E", endLineAction,

			// move caret to document begin/end (without selecting text)
			"shift meta UP", selectionBeginAction,
			"shift meta DOWN", selectionEndAction,
			"shift meta KP_UP", selectionBeginAction,
			"shift meta KP_DOWN", selectionEndAction,
			"shift HOME", selectionBeginAction,
			"shift END", selectionEndAction,

			// move caret to line begin/end and select text
			"shift meta KP_LEFT", selectionBeginLineAction,
			"shift meta KP_RIGHT", selectionEndLineAction,
			"shift UP", selectionBeginLineAction,
			"shift DOWN", selectionEndLineAction,
			"shift KP_UP", selectionBeginLineAction,
			"shift KP_DOWN", selectionEndLineAction,

			// move caret one page (without selecting text)
			"PAGE_UP", pageUpAction,
			"PAGE_DOWN", pageDownAction,

			// move caret one page and select text
			"shift PAGE_UP", "selection-page-up", // DefaultEditorKit.selectionPageUpAction
			"shift PAGE_DOWN", "selection-page-down", // DefaultEditorKit.selectionPageDownAction
			"shift meta PAGE_UP", "selection-page-left", // DefaultEditorKit.selectionPageLeftAction
			"shift meta PAGE_DOWN", "selection-page-right", // DefaultEditorKit.selectionPageRightAction

			// delete previous/next word
			"ctrl W", deletePrevWordAction,
			"ctrl D", deleteNextCharAction,
		} : null;

		Object[] singleLineTextComponentBindings = {
			"ENTER", JTextField.notifyAction,
		};

		Object[] macSingleLineTextComponentBindings = SystemInfo.IS_MAC ? new Object[] {
			// move caret to line begin/end (without selecting text)
			"UP", beginLineAction,
			"DOWN", endLineAction,
			"KP_UP", beginLineAction,
			"KP_DOWN", endLineAction,
		} : null;

		Object[] formattedTextComponentBindings = {
			// reset
			"ESCAPE", "reset-field-edit",

			// increment/decrement
			"UP", "increment",
			"DOWN", "decrement",
			"KP_UP", "increment",
			"KP_DOWN", "decrement",
		};

		Object[] passwordTextComponentBindings = {
			// move caret to line begin/end (without selecting text)
			mac( "ctrl LEFT", "alt LEFT" ), beginLineAction,
			mac( "ctrl RIGHT", "alt RIGHT" ), endLineAction,
			mac( "ctrl KP_LEFT", "alt KP_LEFT" ), beginLineAction,
			mac( "ctrl KP_RIGHT", "alt KP_RIGHT" ), endLineAction,

			// move caret to line begin/end and select text
			mac( "ctrl shift LEFT", "shift alt LEFT" ), selectionBeginLineAction,
			mac( "ctrl shift RIGHT", "shift alt RIGHT" ), selectionEndLineAction,
			mac( "ctrl shift KP_LEFT", "shift alt KP_LEFT" ), selectionBeginLineAction,
			mac( "ctrl shift KP_RIGHT", "shift alt KP_RIGHT" ), selectionEndLineAction,

			// delete previous/next word
			mac( "ctrl BACK_SPACE", "alt BACK_SPACE" ), null,
			mac( "ctrl DELETE", "alt DELETE" ), null,
		};

		Object[] multiLineTextComponentBindings = {
			// move caret one line (without selecting text)
			"UP", upAction,
			"DOWN", downAction,
			"KP_UP", upAction,
			"KP_DOWN", downAction,

			// move caret one line and select text
			"shift UP", selectionUpAction,
			"shift DOWN", selectionDownAction,
			"shift KP_UP", selectionUpAction,
			"shift KP_DOWN", selectionDownAction,

			// move caret one page (without selecting text)
			"PAGE_UP", pageUpAction,
			"PAGE_DOWN", pageDownAction,

			// move caret one page and select text
			"shift PAGE_UP", "selection-page-up", // DefaultEditorKit.selectionPageUpAction
			"shift PAGE_DOWN", "selection-page-down", // DefaultEditorKit.selectionPageDownAction
			mac( "ctrl shift PAGE_UP", "shift meta PAGE_UP" ), "selection-page-left", // DefaultEditorKit.selectionPageLeftAction
			mac( "ctrl shift PAGE_DOWN", "shift meta PAGE_DOWN" ), "selection-page-right", // DefaultEditorKit.selectionPageRightAction

			// move caret to document begin/end (without selecting text)
			mac( "ctrl HOME", "meta UP" ), beginAction,
			mac( "ctrl END", "meta DOWN" ), endAction,

			// move caret to document begin/end and select text
			mac( "ctrl shift HOME", "shift meta UP" ), selectionBeginAction,
			mac( "ctrl shift END", "shift meta DOWN" ), selectionEndAction,

			// misc
			"ENTER", insertBreakAction,
			"TAB", insertTabAction,

			// links
			mac( "ctrl T", "meta T" ), "next-link-action",
			mac( "ctrl shift T", "shift meta T" ), "previous-link-action",
			mac( "ctrl SPACE", "meta SPACE" ), "activate-link-action",
		};

		Object[] macMultiLineTextComponentBindings = SystemInfo.IS_MAC ? new Object[] {
			// move caret one line (without selecting text)
			"ctrl N", downAction,
			"ctrl P", upAction,

			// move caret to beginning/end of paragraph and select text
			"shift alt UP", selectionBeginParagraphAction,
			"shift alt DOWN", selectionEndParagraphAction,
			"shift alt KP_UP", selectionBeginParagraphAction,
			"shift alt KP_DOWN", selectionEndParagraphAction,

			// move caret one page (without selecting text)
			"ctrl V", pageDownAction,
		} : null;

		defaults.put( "TextField.focusInputMap", new LazyInputMapEx(
			commonTextComponentBindings,
			macCommonTextComponentBindings,
			singleLineTextComponentBindings,
			macSingleLineTextComponentBindings
		) );
		defaults.put( "FormattedTextField.focusInputMap", new LazyInputMapEx(
			commonTextComponentBindings,
			macCommonTextComponentBindings,
			singleLineTextComponentBindings,
			macSingleLineTextComponentBindings,
			formattedTextComponentBindings
		) );
		defaults.put( "PasswordField.focusInputMap", new LazyInputMapEx(
			commonTextComponentBindings,
			macCommonTextComponentBindings,
			singleLineTextComponentBindings,
			macSingleLineTextComponentBindings,
			passwordTextComponentBindings
		) );

		Object multiLineInputMap = new LazyInputMapEx(
			commonTextComponentBindings,
			macCommonTextComponentBindings,
			multiLineTextComponentBindings,
			macMultiLineTextComponentBindings
		);
		defaults.put( "TextArea.focusInputMap", multiLineInputMap );
		defaults.put( "TextPane.focusInputMap", multiLineInputMap );
		defaults.put( "EditorPane.focusInputMap", multiLineInputMap );
	}

	private static void initMacInputMaps( UIDefaults defaults ) {
		// list
		modifyInputMap( defaults, "List.focusInputMap",
			"meta A", "selectAll",
			"meta C", "copy",
			"meta V", "paste",
			"meta X", "cut",

			"ctrl A", null,
			"ctrl BACK_SLASH", null,
			"ctrl C", null,
			"ctrl DOWN", null,
			"ctrl END", null,
			"ctrl HOME", null,
			"ctrl INSERT", null,
			"ctrl KP_DOWN", null,
			"ctrl KP_LEFT", null,
			"ctrl KP_RIGHT", null,
			"ctrl KP_UP", null,
			"ctrl LEFT", null,
			"ctrl PAGE_DOWN", null,
			"ctrl PAGE_UP", null,
			"ctrl RIGHT", null,
			"ctrl SLASH", null,
			"ctrl SPACE", null,
			"ctrl UP", null,
			"ctrl V", null,
			"ctrl X", null,
			"PAGE_DOWN", null,
			"PAGE_UP", null,
			"SPACE", null,
			"shift ctrl DOWN", null,
			"shift ctrl END", null,
			"shift ctrl HOME", null,
			"shift ctrl KP_DOWN", null,
			"shift ctrl KP_LEFT", null,
			"shift ctrl KP_RIGHT", null,
			"shift ctrl KP_UP", null,
			"shift ctrl LEFT", null,
			"shift ctrl PAGE_DOWN", null,
			"shift ctrl PAGE_UP", null,
			"shift ctrl RIGHT", null,
			"shift ctrl SPACE", null,
			"shift ctrl UP", null,
			"shift DELETE", null,
			"shift INSERT", null,
			"shift SPACE", null
		);

		// scrollbar
		copyInputMap( defaults, "ScrollBar.ancestorInputMap", "ScrollBar.focusInputMap" );
		copyInputMap( defaults, "ScrollBar.ancestorInputMap.RightToLeft", "ScrollBar.focusInputMap.RightToLeft" );

		// scrollpane
		modifyInputMap( defaults, "ScrollPane.ancestorInputMap",
			"END", "scrollEnd",
			"HOME", "scrollHome",

			"ctrl END", null,
			"ctrl HOME", null,
			"ctrl PAGE_DOWN", null,
			"ctrl PAGE_UP", null
		);
		modifyInputMap( defaults, "ScrollPane.ancestorInputMap.RightToLeft",
			"ctrl PAGE_DOWN", null,
			"ctrl PAGE_UP", null
		);

		// table
		modifyInputMap( defaults, "Table.ancestorInputMap",
			"alt TAB", "focusHeader",
			"shift alt TAB", "focusHeader",
			"meta A", "selectAll",
			"meta C", "copy",
			"meta V", "paste",
			"meta X", "cut",

			"ctrl A", null,
			"ctrl BACK_SLASH", null,
			"ctrl C", null,
			"ctrl DOWN", null,
			"ctrl END", null,
			"ctrl HOME", null,
			"ctrl INSERT", null,
			"ctrl KP_DOWN", null,
			"ctrl KP_LEFT", null,
			"ctrl KP_RIGHT", null,
			"ctrl KP_UP", null,
			"ctrl LEFT", null,
			"ctrl PAGE_DOWN", null,
			"ctrl PAGE_UP", null,
			"ctrl RIGHT", null,
			"ctrl SLASH", null,
			"ctrl SPACE", null,
			"ctrl UP", null,
			"ctrl V", null,
			"ctrl X", null,
			"F2", null,
			"F8", null,
			"SPACE", null,
			"shift ctrl DOWN", null,
			"shift ctrl END", null,
			"shift ctrl HOME", null,
			"shift ctrl KP_DOWN", null,
			"shift ctrl KP_LEFT", null,
			"shift ctrl KP_RIGHT", null,
			"shift ctrl KP_UP", null,
			"shift ctrl LEFT", null,
			"shift ctrl PAGE_DOWN", null,
			"shift ctrl PAGE_UP", null,
			"shift ctrl RIGHT", null,
			"shift ctrl SPACE", null,
			"shift ctrl UP", null,
			"shift DELETE", null,
			"shift INSERT", null,
			"shift SPACE", null
		);
		modifyInputMap( defaults, "Table.ancestorInputMap.RightToLeft",
			"ctrl KP_LEFT", null,
			"ctrl KP_RIGHT", null,
			"ctrl LEFT", null,
			"ctrl RIGHT", null,
			"shift ctrl KP_LEFT", null,
			"shift ctrl KP_RIGHT", null,
			"shift ctrl LEFT", null,
			"shift ctrl RIGHT", null
		);

		// tree node expanding/collapsing
		modifyInputMap( defaults, "Tree.focusInputMap",
			"LEFT", "selectParent",
			"RIGHT", "selectChild",
			"KP_LEFT", "selectParent",
			"KP_RIGHT", "selectChild",

		    "meta A", "selectAll",
		    "meta C", "copy",
		    "meta V", "paste",
		    "meta X", "cut",

		    "ctrl LEFT", null,
			"ctrl RIGHT", null,
			"ctrl KP_LEFT", null,
			"ctrl KP_RIGHT", null,

			"shift LEFT", null,
			"shift RIGHT", null,
			"shift KP_LEFT", null,
			"shift KP_RIGHT", null,

		    "alt LEFT", null,
		    "alt RIGHT", null,
		    "alt KP_LEFT", null,
		    "alt KP_RIGHT", null,

			"ctrl A", null,
			"ctrl BACK_SLASH", null,
			"ctrl C", null,
			"ctrl DOWN", null,
			"ctrl END", null,
			"ctrl HOME", null,
			"ctrl INSERT", null,
			"ctrl KP_DOWN", null,
			"ctrl KP_UP", null,
			"ctrl PAGE_DOWN", null,
			"ctrl PAGE_UP", null,
			"ctrl SLASH", null,
			"ctrl SPACE", null,
			"ctrl UP", null,
			"ctrl V", null,
			"ctrl X", null,
			"END", null,
			"F2", null,
			"HOME", null,
			"PAGE_DOWN", null,
			"PAGE_UP", null,
			"SPACE", null,
			"shift ctrl DOWN", null,
			"shift ctrl END", null,
			"shift ctrl HOME", null,
			"shift ctrl KP_DOWN", null,
			"shift ctrl KP_UP", null,
			"shift ctrl PAGE_DOWN", null,
			"shift ctrl PAGE_UP", null,
			"shift ctrl SPACE", null,
			"shift ctrl UP", null,
			"shift DELETE", null,
			"shift END", null,
			"shift HOME", null,
			"shift INSERT", null,
			"shift PAGE_DOWN", null,
			"shift PAGE_UP", null,
			"shift SPACE", null
		);
		defaults.put( "Tree.focusInputMap.RightToLeft", new UIDefaults.LazyInputMap( new Object[] {
			"LEFT", "selectChild",
			"RIGHT", "selectParent",
			"KP_LEFT", "selectChild",
			"KP_RIGHT", "selectParent"
		} ) );
	}

	private static void copyInputMap( UIDefaults defaults, String srcKey, String destKey ) {
		// Note: not using `defaults.get(key)` here because this would resolve the lazy value
		Object inputMap = defaults.remove( srcKey );
		defaults.put( srcKey, inputMap );
		defaults.put( destKey, inputMap );
	}

	private static void modifyInputMap( UIDefaults defaults, String key, Object... bindings ) {
		// Note: not using `defaults.get(key)` here because this would resolve the lazy value
		defaults.put( key, new LazyModifyInputMap( defaults.remove( key ), bindings ) );
	}

	private static <T> T mac( T value, T macValue ) {
		return SystemInfo.IS_MAC ? macValue : value;
	}

	//---- class LazyInputMapEx -----------------------------------------------

	/**
	 * Lazily creates a input map.
	 * Similar to {@link UIDefaults.LazyInputMap}, but can use multiple bindings arrays.
	 */
	private static class LazyInputMapEx
		implements LazyValue
	{
		private final Object[][] bindingsArray;

		LazyInputMapEx( Object[]... bindingsArray ) {
			this.bindingsArray = bindingsArray;
		}

		@Override
		public Object createValue( UIDefaults table ) {
			InputMap inputMap = new InputMapUIResource();
			for( Object[] bindings : bindingsArray )
				LookAndFeel.loadKeyBindings( inputMap, bindings );
			return inputMap;
		}
	}

	//---- class LazyModifyInputMap -------------------------------------------

	/**
	 * Takes a (lazy) base input map and lazily applies modifications to it specified in bindings.
	 */
	private static class LazyModifyInputMap
		implements LazyValue
	{
		private final Object baseInputMap;
		private final Object[] bindings;

		LazyModifyInputMap( Object baseInputMap, Object[] bindings ) {
			this.baseInputMap = baseInputMap;
			this.bindings = bindings;
		}

		@Override
		public Object createValue( UIDefaults table ) {
			// get base input map
			InputMap inputMap = (baseInputMap instanceof LazyValue)
				? (InputMap) ((LazyValue)baseInputMap).createValue( table )
				: (InputMap) baseInputMap;

			// modify input map (replace or remove)
			for( int i = 0; i < bindings.length; i += 2 ) {
				KeyStroke keyStroke = KeyStroke.getKeyStroke( (String) bindings[i] );
				if( bindings[i + 1] != null )
					inputMap.put( keyStroke, bindings[i + 1] );
				else
					inputMap.remove( keyStroke );
			}

			return inputMap;
		}
	}
}
