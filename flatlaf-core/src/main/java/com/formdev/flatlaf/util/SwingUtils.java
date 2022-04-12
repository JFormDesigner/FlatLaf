/*
 * Copyright 2022 FormDev Software GmbH
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

package com.formdev.flatlaf.util;

import java.awt.Component;
import java.awt.Container;

/**
 * Utility methods for Swing.
 *
 * @author Karl Tauber
 * @since 2
 */
public class SwingUtils
{
	/**
	 * Search for a (grand) child component with the given name.
	 *
	 * @return a component; or {@code null}
	 */
	@SuppressWarnings( "unchecked" )
	public static <T extends Component> T getComponentByName( Container parent, String name ) {
		for( Component child : parent.getComponents() ) {
			if( name.equals( child.getName() ) )
				return (T) child;

			if( child instanceof Container ) {
				T c = getComponentByName( (Container) child, name );
				if( c != null )
					return c;
			}
		}

		return null;
	}
}
