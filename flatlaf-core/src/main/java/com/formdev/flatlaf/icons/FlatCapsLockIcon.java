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

package com.formdev.flatlaf.icons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.UIManager;
import com.formdev.flatlaf.ui.FlatStylingSupport.UnknownStyleException;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * "caps lock" icon for {@link javax.swing.JPasswordField}.
 *
 * @uiDefault PasswordField.capsLockIconColor			Color
 *
 * @author Karl Tauber
 */
public class FlatCapsLockIcon
	extends FlatAbstractIcon
{
	private Path2D path;

	public FlatCapsLockIcon() {
		super( 16, 16, UIManager.getColor( "PasswordField.capsLockIconColor" ) );
	}

	/** @since 2 */
	public Object applyStyleProperty( String key, Object value ) {
		Object oldValue;
		switch( key ) {
			case "capsLockIconColor": oldValue = color; color = (Color) value; return oldValue;
			default: throw new UnknownStyleException( key );
		}
	}

	/** @since 2.5 */
	public Object getStyleableValue( String key ) {
		switch( key ) {
			case "capsLockIconColor": return color;
			default: return null;
		}
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		/*
			<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
			  <g fill="none" fill-rule="evenodd">
			    <rect width="16" height="16" fill="#6E6E6E" rx="3"/>
			    <rect width="5" height="2" x="5.5" y="11.5" stroke="#FFF" stroke-linejoin="round"/>
			    <path stroke="#FFF" stroke-linejoin="round" d="M2.5,7.5 L8,2 L13.5,7.5 L10.5,7.5 L10.5,9.5 L5.5,9.5 L5.5,7.5 L2.5,7.5 Z"/>
			  </g>
			</svg>
		*/

		g.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
		BasicStroke stroke = new BasicStroke( 1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND );

		if( path == null ) {
			path = new Path2D.Float( Path2D.WIND_EVEN_ODD );
			path.append( new RoundRectangle2D.Float( 0, 0, 16, 16, 6, 6 ), false );
			path.append( new Area( stroke.createStrokedShape( new Rectangle2D.Float( 5.5f, 11.5f, 5, 2 ) ) ), false );
			path.append( new Area( stroke.createStrokedShape( FlatUIUtils.createPath(
				2.5,7.5, 8,2, 13.5,7.5, 10.5,7.5, 10.5,9.5, 5.5,9.5, 5.5,7.5, 2.5,7.5 ) ) ), false );
		}
		g.fill( path );
	}
}
