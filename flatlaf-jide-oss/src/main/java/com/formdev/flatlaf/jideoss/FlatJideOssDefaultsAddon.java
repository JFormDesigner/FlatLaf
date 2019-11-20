/*
 * Copyright 2019 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.formdev.flatlaf.jideoss;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.swing.UIDefaults;
import com.formdev.flatlaf.FlatDefaultsAddon;
import com.formdev.flatlaf.FlatLaf;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.LookAndFeelFactory.UIDefaultsCustomizer;
import com.jidesoft.plaf.LookAndFeelFactory.UIDefaultsInitializer;

/**
 * JIDE Common Layer addon for FlatLaf.
 *
 * @author Karl Tauber
 */
public class FlatJideOssDefaultsAddon
	extends FlatDefaultsAddon
{
	/**
	 * Finds JIDE Common Layer addon .properties file for the given LaF class
	 * in the same package as this class.
	 */
	@Override
	public InputStream getDefaults( Class<?> lafClass ) {
		LookAndFeelFactory.registerDefaultInitializer( FlatLaf.class.getName(), FlatJideUIDefaultsCustomizer.class.getName() );
		LookAndFeelFactory.registerDefaultCustomizer( FlatLaf.class.getName(), FlatJideUIDefaultsCustomizer.class.getName() );

		Class<?> addonClass = this.getClass();
		String propertiesName = "/" + addonClass.getPackage().getName().replace( '.', '/' )
			+ '/' + lafClass.getSimpleName() + ".properties";
		return addonClass.getResourceAsStream( propertiesName );
	}

	//---- class FlatJideUIDefaultsCustomizer ---------------------------------

	/**
	 * Because JIDE overwrites our UI defaults (from properties files) with its
	 * own UI defaults, we have to first remember our UI defaults in the initializer
	 * (invoked before JIDE overwrites UI defaults) and then restore them in the customizer.
	 */
	public static class FlatJideUIDefaultsCustomizer
		implements UIDefaultsInitializer, UIDefaultsCustomizer
	{
		private static HashMap<Object, Object> jideDefaults;

		@Override
		public void initialize( UIDefaults defaults ) {
			jideDefaults = new HashMap<>();

			for( Map.Entry<Object, Object> e : defaults.entrySet() ) {
				Object key = e.getKey();
				if( key instanceof String &&
					(((String)key).startsWith( "Jide" ) ||
					 ((String)key).equals( "Resizable.resizeBorder" )) )
				{
					jideDefaults.put( key, e.getValue() );
				}
			}
		}

		@Override
		public void customize( UIDefaults defaults ) {
			if( jideDefaults != null ) {
				defaults.putAll( jideDefaults );
				jideDefaults = null;
			}
		}
	}
}
