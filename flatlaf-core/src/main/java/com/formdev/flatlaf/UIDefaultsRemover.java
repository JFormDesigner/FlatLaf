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

import javax.swing.UIDefaults;

/**
 * Removes UI defaults that are defined in "base" LaF (Aqua), but not used in FlatLaf.
 *
 * This is a temporary class that can be removed when dropping "base" LaF.
 *
 * @author Karl Tauber
 */
class UIDefaultsRemover
{
	static final String[] REMOVE_KEYS = {
		"Button.select",

		"CheckBox.select",

		"RadioButton.select",

		"Tree.line",
	};

	static void removeDefaults( UIDefaults defaults ) {
		for( String key : REMOVE_KEYS )
			defaults.remove( key );

/*
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
*/
	}
}
