/*
 * Copyright 2021 FormDev Software GmbH
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.UIManager;
import com.formdev.flatlaf.ui.FlatStylingSupport;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * "clear" icon for search fields.
 *
 * @uiDefault SearchField.clearIconColor				Color
 * @uiDefault SearchField.clearIconHoverColor			Color
 * @uiDefault SearchField.clearIconPressedColor			Color
 *
 * @author Karl Tauber
 * @since 1.5
 */
public class FlatClearIcon
	extends FlatAbstractIcon
{
	@Styleable protected Color clearIconColor = UIManager.getColor( "SearchField.clearIconColor" );
	@Styleable protected Color clearIconHoverColor = UIManager.getColor( "SearchField.clearIconHoverColor" );
	@Styleable protected Color clearIconPressedColor = UIManager.getColor( "SearchField.clearIconPressedColor" );

	private final boolean ignoreButtonState;

	public FlatClearIcon() {
		this( false );
	}

	/** @since 2 */
	public FlatClearIcon( boolean ignoreButtonState ) {
		super( 16, 16, null );
		this.ignoreButtonState = ignoreButtonState;
	}

	/** @since 2 */
	public Object applyStyleProperty( String key, Object value ) {
		return FlatStylingSupport.applyToAnnotatedObject( this, key, value );
	}

	/** @since 2 */
	public Map<String, Class<?>> getStyleableInfos() {
		return FlatStylingSupport.getAnnotatedStyleableInfos( this );
	}

	/** @since 2.5 */
	public Object getStyleableValue( String key ) {
		return FlatStylingSupport.getAnnotatedStyleableValue( this, key );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		if( !ignoreButtonState && c instanceof AbstractButton ) {
			ButtonModel model = ((AbstractButton)c).getModel();
			if( model.isPressed() || model.isRollover() ) {
				/*
					<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
					  <path fill="#7F8B91" fill-opacity=".5" fill-rule="evenodd" d="M8,1.75 C11.4517797,1.75 14.25,4.54822031 14.25,8 C14.25,11.4517797 11.4517797,14.25 8,14.25 C4.54822031,14.25 1.75,11.4517797 1.75,8 C1.75,4.54822031 4.54822031,1.75 8,1.75 Z M10.5,4.5 L8,7 L5.5,4.5 L4.5,5.5 L7,8 L4.5,10.5 L5.5,11.5 L8,9 L10.5,11.5 L11.5,10.5 L9,8 L11.5,5.5 L10.5,4.5 Z"/>
					</svg>
				*/

				// paint filled circle with cross
				g.setColor( model.isPressed() ? clearIconPressedColor : clearIconHoverColor );
				Path2D path = new Path2D.Float( Path2D.WIND_EVEN_ODD );
				path.append( new Ellipse2D.Float( 1.75f, 1.75f, 12.5f, 12.5f ), false );
				path.append( FlatUIUtils.createPath( 4.5,5.5, 5.5,4.5, 8,7,  10.5,4.5, 11.5,5.5, 9,8,  11.5,10.5, 10.5,11.5, 8,9,  5.5,11.5, 4.5,10.5, 7,8 ), false );
				g.fill( path );
				return;
			}
		}

		/*
			<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
			  <path fill="none" stroke="#7F8B91" stroke-linecap="square" stroke-opacity=".5" d="M5,5 L11,11 M5,11 L11,5"/>
			</svg>
		*/

		// paint cross
		g.setColor( clearIconColor );
		Path2D path = new Path2D.Float( Path2D.WIND_EVEN_ODD, 4 );
		path.moveTo( 5, 5 );
		path.lineTo( 11, 11 );
		path.moveTo( 5, 11 );
		path.lineTo( 11, 5 );
		g.draw( path );
	}
}
