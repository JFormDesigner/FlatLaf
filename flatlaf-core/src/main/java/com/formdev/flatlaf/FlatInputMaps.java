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
	static void initInputMaps( UIDefaults defaults, UIDefaults baseDefaults ) {
		if( !SystemInfo.IS_MAC )
			initBasicInputMaps( defaults );
		else
			initMacInputMaps( defaults, baseDefaults );
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

			"alt UP", "togglePopup",
			"alt DOWN", "togglePopup",
			"alt KP_UP", "togglePopup",
			"alt KP_DOWN", "togglePopup"
		);

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
			"ctrl LEFT", previousWordAction,
			"ctrl RIGHT", nextWordAction,
			"ctrl KP_LEFT", previousWordAction,
			"ctrl KP_RIGHT", nextWordAction,

			// move caret to word and select text
			"ctrl shift LEFT", selectionPreviousWordAction,
			"ctrl shift RIGHT", selectionNextWordAction,
			"ctrl shift KP_LEFT", selectionPreviousWordAction,
			"ctrl shift KP_RIGHT", selectionNextWordAction,

			// move caret to line begin/end (without selecting text)
			"HOME", beginLineAction,
			"END", endLineAction,

			// move caret to line begin/end and select text
			"shift HOME", selectionBeginLineAction,
			"shift END", selectionEndLineAction,

			// select all/none
			"ctrl A", selectAllAction,
			"ctrl BACK_SLASH", "unselect", // DefaultEditorKit.unselectAction

			// delete previous/next character
			"BACK_SPACE", deletePrevCharAction,
			"shift BACK_SPACE", deletePrevCharAction,
			"ctrl H", deletePrevCharAction,
			"DELETE", deleteNextCharAction,

			// delete previous/next word
			"ctrl BACK_SPACE", deletePrevWordAction,
			"ctrl DELETE", deleteNextWordAction,

			// clipboard
			"ctrl X", cutAction,
			"ctrl C", copyAction,
			"ctrl V", pasteAction,
			"CUT", cutAction,
			"COPY", copyAction,
			"PASTE", pasteAction,
			"shift DELETE", cutAction,
			"control INSERT", copyAction,
			"shift INSERT", pasteAction,

			// misc
			"control shift O", "toggle-componentOrientation", // DefaultEditorKit.toggleComponentOrientation
		};

		Object[] singleLineTextComponentBindings = {
			"ENTER", JTextField.notifyAction,
		};

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
			"ctrl LEFT", beginLineAction,
			"ctrl RIGHT", endLineAction,
			"ctrl KP_LEFT", beginLineAction,
			"ctrl KP_RIGHT", endLineAction,

			// move caret to line begin/end and select text
			"ctrl shift LEFT", selectionBeginLineAction,
			"ctrl shift RIGHT", selectionEndLineAction,
			"ctrl shift KP_LEFT", selectionBeginLineAction,
			"ctrl shift KP_RIGHT", selectionEndLineAction,

			// delete previous/next word
			"ctrl BACK_SPACE", null,
			"ctrl DELETE", null,
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
			"ctrl shift PAGE_UP", "selection-page-left", // DefaultEditorKit.selectionPageLeftAction
			"ctrl shift PAGE_DOWN", "selection-page-right", // DefaultEditorKit.selectionPageRightAction

			// move caret to document begin/end (without selecting text)
			"ctrl HOME", beginAction,
			"ctrl END", endAction,

			// move caret to document begin/end and select text
			"ctrl shift HOME", selectionBeginAction,
			"ctrl shift END", selectionEndAction,

			// misc
			"ENTER", insertBreakAction,
			"TAB", insertTabAction,

			// links
			"ctrl T", "next-link-action",
			"ctrl shift T", "previous-link-action",
			"ctrl SPACE", "activate-link-action",
		};

		defaults.put( "TextField.focusInputMap", new LazyInputMapEx(
			commonTextComponentBindings,
			singleLineTextComponentBindings
		) );
		defaults.put( "FormattedTextField.focusInputMap", new LazyInputMapEx(
			commonTextComponentBindings,
			singleLineTextComponentBindings,
			formattedTextComponentBindings
		) );
		defaults.put( "PasswordField.focusInputMap", new LazyInputMapEx(
			commonTextComponentBindings,
			singleLineTextComponentBindings,
			passwordTextComponentBindings
		) );

		Object multiLineInputMap = new LazyInputMapEx(
			commonTextComponentBindings,
			multiLineTextComponentBindings
		);
		defaults.put( "TextArea.focusInputMap", multiLineInputMap );
		defaults.put( "TextPane.focusInputMap", multiLineInputMap );
		defaults.put( "EditorPane.focusInputMap", multiLineInputMap );
	}

	private static void initMacInputMaps( UIDefaults defaults, UIDefaults baseDefaults ) {
		// copy Aqua LaF input maps
		copyInputMaps( baseDefaults, defaults,
			"Button.focusInputMap",
			"EditorPane.focusInputMap",
			"FormattedTextField.focusInputMap",
			"List.focusInputMap",
			"PasswordField.focusInputMap",
			"ScrollBar.focusInputMap.RightToLeft",
			"ScrollBar.focusInputMap",
			"ScrollPane.ancestorInputMap.RightToLeft",
			"ScrollPane.ancestorInputMap",
			"Table.ancestorInputMap.RightToLeft",
			"Table.ancestorInputMap",
			"TextArea.focusInputMap",
			"TextField.focusInputMap",
			"TextPane.focusInputMap",
			"Tree.focusInputMap" );


		// AquaLookAndFeel (the base for UI defaults on macOS) uses special
		// action keys (e.g. "aquaExpandNode") for some macOS specific behaviour.
		// Those action keys are not available in FlatLaf, which makes it
		// necessary to make some modifications.

		// combobox
		defaults.put( "ComboBox.ancestorInputMap", new UIDefaults.LazyInputMap( new Object[] {
			"SPACE", "spacePopup",
			"ENTER", "enterPressed",
			"ESCAPE", "hidePopup",

			"HOME", "homePassThrough",
			"END", "endPassThrough",
			"PAGE_UP", "pageUpPassThrough",
			"PAGE_DOWN", "pageDownPassThrough",

			"UP", "selectPrevious",
			"DOWN", "selectNext",
			"KP_UP", "selectPrevious",
			"KP_DOWN", "selectNext",
		} ) );

		// tree node expanding/collapsing
		modifyInputMap( defaults, "Tree.focusInputMap",
			"LEFT", "selectParent",
			"RIGHT", "selectChild",
			"KP_LEFT", "selectParent",
			"KP_RIGHT", "selectChild",

			"ctrl LEFT", null,
			"ctrl RIGHT", null,
			"ctrl KP_LEFT", null,
			"ctrl KP_RIGHT", null,

			"shift LEFT", null,
			"shift RIGHT", null,
			"shift KP_LEFT", null,
			"shift KP_RIGHT", null
		);
		defaults.put( "Tree.focusInputMap.RightToLeft", new UIDefaults.LazyInputMap( new Object[] {
			"LEFT", "selectChild",
			"RIGHT", "selectParent",
			"KP_LEFT", "selectChild",
			"KP_RIGHT", "selectParent"
		} ) );
	}

	private static void copyInputMaps( UIDefaults src, UIDefaults dest, String... keys ) {
		// Note: not using `src.get(key)` here because this would resolve the lazy value
		for( String key : keys )
			dest.put( key, src.remove( key ) );
	}

	private static void modifyInputMap( UIDefaults defaults, String key, Object... bindings ) {
		// Note: not using `defaults.get(key)` here because this would resolve the lazy value
		defaults.put( key, new LazyModifyInputMap( defaults.remove( key ), bindings ) );
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
