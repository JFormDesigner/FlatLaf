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
import javax.swing.KeyStroke;
import javax.swing.UIDefaults;
import javax.swing.UIDefaults.LazyValue;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * @author Karl Tauber
 */
class FlatInputMaps
{
	static void initInputMaps( UIDefaults defaults ) {
		if( SystemInfo.IS_MAC ) {
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
	}

	private static void modifyInputMap( UIDefaults defaults, String key, Object... bindings ) {
		// Note: not using `defaults.get(key)` here because this would resolve the lazy value
		defaults.put( key, new LazyModifyInputMap( defaults.remove( key ), bindings ) );
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

		public LazyModifyInputMap( Object baseInputMap, Object[] bindings ) {
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
