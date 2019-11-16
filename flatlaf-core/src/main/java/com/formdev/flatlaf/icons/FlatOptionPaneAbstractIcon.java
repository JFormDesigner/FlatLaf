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

package com.formdev.flatlaf.icons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Path2D;
import javax.swing.UIManager;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * Base class for icons for {@link javax.swing.JOptionPane}.
 *
 * @uiDefault OptionPane.icon.foreground			Color	default is transparent
 *
 * @author Karl Tauber
 */
public abstract class FlatOptionPaneAbstractIcon
	extends FlatAbstractIcon
{
	protected final Color foreground = UIManager.getColor( "OptionPane.icon.foreground" );

	protected FlatOptionPaneAbstractIcon( String colorKey, String defaultColorKey ) {
		super( 32, 32, FlatUIUtils.getUIColor( colorKey, defaultColorKey ) );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		if( foreground != null ) {
			g.fill( createOutside() );

			g.setColor( foreground );
			g.fill( createInside() );
		} else {
			Path2D path = new Path2D.Float( Path2D.WIND_EVEN_ODD );
			path.append( createOutside(), false );
			path.append( createInside(), false );
			g.fill( path );
		}
	}

	protected abstract Shape createOutside();
	protected abstract Shape createInside();
}
