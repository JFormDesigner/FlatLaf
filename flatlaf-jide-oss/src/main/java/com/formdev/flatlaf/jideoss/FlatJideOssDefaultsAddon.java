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
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UIDefaults.ActiveValue;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDefaultsAddon;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.jideoss.ui.FlatJidePainter;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.UIDefaultsLookup;
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

		return super.getDefaults( lafClass );
	}

	@Override
	public void afterDefaultsLoading( LookAndFeel laf, UIDefaults defaults ) {
		// TristateCheckBox
		defaults.put( "TristateCheckBox.icon", null );
		defaults.put( "TristateCheckBox.setMixed.clientProperty",   new Object[] { FlatClientProperties.SELECTED_STATE, FlatClientProperties.SELECTED_STATE_INDETERMINATE } );
		defaults.put( "TristateCheckBox.clearMixed.clientProperty", new Object[] { FlatClientProperties.SELECTED_STATE, null } );
	}

	@Override
	public int getPriority() {
		return 11;
	}

	//---- class FlatJideUIDefaultsCustomizer ---------------------------------

	/**
	 * Because JIDE overwrites our UI defaults (from properties files) with its
	 * own UI defaults, we have to first remember our UI defaults in the initializer
	 * (invoked before JIDE overwrites UI defaults) and then restore them in the customizer.
	 * <p>
	 * Do not register this class yourself with JIDE.
	 * It is automatically registered.
	 * <p>
	 * Invoked from {@link LookAndFeelFactory#installJideExtension()}.
	 */
	public static class FlatJideUIDefaultsCustomizer
		implements UIDefaultsInitializer, UIDefaultsCustomizer
	{
		private static HashMap<Object, Object> jideDefaults;

		@Override
		public void initialize( UIDefaults defaults ) {
			// do nothing in other Lafs if (wrongly) registered with LookAndFeelFactory.addUIDefaultsInitializer()
			if( !(UIManager.getLookAndFeel() instanceof FlatLaf) )
				return;

			jideDefaults = new HashMap<>();

			for( Map.Entry<Object, Object> e : defaults.entrySet() ) {
				Object key = e.getKey();
				if( key instanceof String &&
					(((String)key).startsWith( "Jide" ) ||
					 ((String)key).startsWith( "TristateCheckBox." ) ||
					 key.equals( "RangeSliderUI" ) ||
					 key.equals( "Resizable.resizeBorder" )) )
				{
					jideDefaults.put( key, e.getValue() );
				}
			}
		}

		@Override
		public void customize( UIDefaults defaults ) {
			// do nothing in other Lafs if (wrongly) registered with LookAndFeelFactory.addUIDefaultsCustomizer()
			if( !(UIManager.getLookAndFeel() instanceof FlatLaf) )
				return;

			if( jideDefaults != null ) {
				defaults.putAll( jideDefaults );
				jideDefaults = null;
			}

			// painter
			UIDefaultsLookup.put( defaults, "Theme.painter", FlatJidePainter.getInstance() );

			// avoid that JideButton and JideSplitButton shift icon on hover/selection
			defaults.put( "Icon.floating", false );

			// fonts
			ActiveValue font = FlatLaf.createActiveFontValue( 1f );
			defaults.put( "JideButton.font", font );
			defaults.put( "JideLabel.font", font );
			defaults.put( "JideSplitButton.font", font );
			defaults.put( "JideTabbedPane.font", font );
			defaults.put( "JideTabbedPane.selectedTabFont", font );

			// reset standard fonts modified by LookAndFeelFactory.installJideExtension()
			defaults.put( "FormattedTextField.font", font );
			defaults.put( "Spinner.font", font );
			defaults.put( "TextArea.font", font );

			// TristateCheckBox
			defaults.put( "TristateCheckBox.icon", null );
		}
	}
}
