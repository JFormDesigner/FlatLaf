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

package com.formdev.flatlaf.swingx.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.plaf.basic.BasicHeaderUI;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link org.jdesktop.swingx.JXHeader}.
 *
 * @author Karl Tauber
 */
public class FlatHeaderUI
	extends BasicHeaderUI
{
	public static ComponentUI createUI( JComponent c ) {
		return new FlatHeaderUI();
	}

	@Override
	protected void installComponents( JXHeader header ) {
		super.installComponents( header );

		scaleLayout( header );
	}

	@Override
	protected void onPropertyChange( JXHeader h, String propertyName, Object oldValue, Object newValue ) {
		super.onPropertyChange( h, propertyName, oldValue, newValue );

		if( "iconPosition".equals( propertyName ) )
			scaleLayout( h );
	}

	private void scaleLayout( JXHeader header ) {
		if( UIScale.getUserScaleFactor() == 1f )
			return;

		LayoutManager layout = header.getLayout();
		if( !(layout instanceof GridBagLayout) )
			return;

		GridBagLayout gbl = (GridBagLayout) layout;
		for( Component c : header.getComponents() ) {
			GridBagConstraints cons = gbl.getConstraints( c );
			cons.insets = UIScale.scale( cons.insets );
			gbl.setConstraints( c, cons );
		}
	}
}
