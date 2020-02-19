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
import javax.swing.text.DefaultEditorKit;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * @author Karl Tauber
 */
class FlatInputMaps
{
	static void initInputMaps( UIDefaults defaults ) {
		if( !SystemInfo.IS_MAC )
			initBasicInputMaps( defaults );
		else
			initMacInputMaps( defaults );
	}

	private static void initBasicInputMaps( UIDefaults defaults ) {
		defaults.put( "Button.focusInputMap", new UIDefaults.LazyInputMap( new Object[] {
			         "SPACE", "pressed",
			"released SPACE", "released"
		} ) );

		modifyInputMap( defaults, "ComboBox.ancestorInputMap",
			"UP", "selectPrevious",
			"KP_UP", "selectPrevious",
			"DOWN", "selectNext",
			"KP_DOWN", "selectNext",

			"alt UP", "togglePopup",
			"alt KP_UP", "togglePopup",
			"alt DOWN", "togglePopup",
			"alt KP_DOWN", "togglePopup",

			"SPACE", "spacePopup"
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
			"LEFT", DefaultEditorKit.backwardAction,
			"KP_LEFT", DefaultEditorKit.backwardAction,
			"RIGHT", DefaultEditorKit.forwardAction,
			"KP_RIGHT", DefaultEditorKit.forwardAction,

			// move caret one character and select text
			"shift LEFT", DefaultEditorKit.selectionBackwardAction,
			"shift KP_LEFT", DefaultEditorKit.selectionBackwardAction,
			"shift RIGHT", DefaultEditorKit.selectionForwardAction,
			"shift KP_RIGHT", DefaultEditorKit.selectionForwardAction,

			// move caret to word (without selecting text)
			"ctrl LEFT", DefaultEditorKit.previousWordAction,
			"ctrl KP_LEFT", DefaultEditorKit.previousWordAction,
			"ctrl RIGHT", DefaultEditorKit.nextWordAction,
			"ctrl KP_RIGHT", DefaultEditorKit.nextWordAction,

			// move caret to word and select text
			"ctrl shift LEFT", DefaultEditorKit.selectionPreviousWordAction,
			"ctrl shift KP_LEFT", DefaultEditorKit.selectionPreviousWordAction,
			"ctrl shift RIGHT", DefaultEditorKit.selectionNextWordAction,
			"ctrl shift KP_RIGHT", DefaultEditorKit.selectionNextWordAction,

			// move caret to line begin/end (without selecting text)
			"HOME", DefaultEditorKit.beginLineAction,
			"END", DefaultEditorKit.endLineAction,

			// move caret to line begin/end and select text
			"shift HOME", DefaultEditorKit.selectionBeginLineAction,
			"shift END", DefaultEditorKit.selectionEndLineAction,

			// select all/none
			"ctrl A", DefaultEditorKit.selectAllAction,
			"ctrl BACK_SLASH", "unselect", // DefaultEditorKit.unselectAction

			// delete previous/next character
			"BACK_SPACE", DefaultEditorKit.deletePrevCharAction,
			"shift BACK_SPACE", DefaultEditorKit.deletePrevCharAction,
			"ctrl H", DefaultEditorKit.deletePrevCharAction,
			"DELETE", DefaultEditorKit.deleteNextCharAction,

			// delete previous/next word
			"ctrl BACK_SPACE", DefaultEditorKit.deletePrevWordAction,
			"ctrl DELETE", DefaultEditorKit.deleteNextWordAction,

			// clipboard
			"ctrl X", DefaultEditorKit.cutAction,
			"ctrl C", DefaultEditorKit.copyAction,
			"ctrl V", DefaultEditorKit.pasteAction,
			"CUT", DefaultEditorKit.cutAction,
			"COPY", DefaultEditorKit.copyAction,
			"PASTE", DefaultEditorKit.pasteAction,
			"shift DELETE", DefaultEditorKit.cutAction,
			"control INSERT", DefaultEditorKit.copyAction,
			"shift INSERT", DefaultEditorKit.pasteAction,

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
			"KP_UP", "increment",
			"DOWN", "decrement",
			"KP_DOWN", "decrement",
		};

		Object[] passwordTextComponentBindings = {
			// move caret to line begin/end (without selecting text)
			"ctrl LEFT", DefaultEditorKit.beginLineAction,
			"ctrl KP_LEFT", DefaultEditorKit.beginLineAction,
			"ctrl RIGHT", DefaultEditorKit.endLineAction,
			"ctrl KP_RIGHT", DefaultEditorKit.endLineAction,

			// move caret to line begin/end and select text
			"ctrl shift LEFT", DefaultEditorKit.selectionBeginLineAction,
			"ctrl shift KP_LEFT", DefaultEditorKit.selectionBeginLineAction,
			"ctrl shift RIGHT", DefaultEditorKit.selectionEndLineAction,
			"ctrl shift KP_RIGHT", DefaultEditorKit.selectionEndLineAction,

			// delete previous/next word
			"ctrl BACK_SPACE", null,
			"ctrl DELETE", null,
		};

		Object[] multiLineTextComponentBindings = {
			// move caret one line (without selecting text)
			"UP", DefaultEditorKit.upAction,
			"KP_UP", DefaultEditorKit.upAction,
			"DOWN", DefaultEditorKit.downAction,
			"KP_DOWN", DefaultEditorKit.downAction,

			// move caret one line and select text
			"shift UP", DefaultEditorKit.selectionUpAction,
			"shift KP_UP", DefaultEditorKit.selectionUpAction,
			"shift DOWN", DefaultEditorKit.selectionDownAction,
			"shift KP_DOWN", DefaultEditorKit.selectionDownAction,

			// move caret one page (without selecting text)
			"PAGE_UP", DefaultEditorKit.pageUpAction,
			"PAGE_DOWN", DefaultEditorKit.pageDownAction,

			// move caret one page and select text
			"shift PAGE_UP", "selection-page-up",
			"shift PAGE_DOWN", "selection-page-down",
			"ctrl shift PAGE_UP", "selection-page-left",
			"ctrl shift PAGE_DOWN", "selection-page-right",

			// move caret to document begin/end (without selecting text)
			"ctrl HOME", DefaultEditorKit.beginAction,
			"ctrl END", DefaultEditorKit.endAction,

			// move caret to document begin/end and select text
			"ctrl shift HOME", DefaultEditorKit.selectionBeginAction,
			"ctrl shift END", DefaultEditorKit.selectionEndAction,

			// misc
			"ENTER", DefaultEditorKit.insertBreakAction,
			"TAB", DefaultEditorKit.insertTabAction,

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

	private static void initMacInputMaps( UIDefaults defaults ) {
		// AquaLookAndFeel (the base for UI defaults on macOS) uses special
		// action keys (e.g. "aquaExpandNode") for some macOS specific behaviour.
		// Those action keys are not available in FlatLaf, which makes it
		// necessary to make some modifications.

		// combobox
		defaults.put( "ComboBox.ancestorInputMap", new UIDefaults.LazyInputMap( new Object[] {
			     "ESCAPE", "hidePopup",
			    "PAGE_UP", "pageUpPassThrough",
			  "PAGE_DOWN", "pageDownPassThrough",
			       "HOME", "homePassThrough",
			        "END", "endPassThrough",
			       "DOWN", "selectNext",
			    "KP_DOWN", "selectNext",
			      "SPACE", "spacePopup",
			      "ENTER", "enterPressed",
			         "UP", "selectPrevious",
			      "KP_UP", "selectPrevious"
		} ) );

		// tree node expanding/collapsing
		modifyInputMap( defaults, "Tree.focusInputMap",
			         "RIGHT", "selectChild",
			      "KP_RIGHT", "selectChild",
			          "LEFT", "selectParent",
			       "KP_LEFT", "selectParent",
			   "shift RIGHT", null,
			"shift KP_RIGHT", null,
			    "shift LEFT", null,
			 "shift KP_LEFT", null,
			     "ctrl LEFT", null,
			  "ctrl KP_LEFT", null,
			    "ctrl RIGHT", null,
			 "ctrl KP_RIGHT", null
		);
		defaults.put( "Tree.focusInputMap.RightToLeft", new UIDefaults.LazyInputMap( new Object[] {
                     "RIGHT", "selectParent",
                  "KP_RIGHT", "selectParent",
                      "LEFT", "selectChild",
                   "KP_LEFT", "selectChild"
		} ) );
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
