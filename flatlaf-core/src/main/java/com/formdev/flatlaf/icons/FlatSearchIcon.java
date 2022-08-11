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
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.Map;
import javax.swing.UIManager;
import com.formdev.flatlaf.ui.FlatButtonUI;
import com.formdev.flatlaf.ui.FlatStylingSupport;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * "search" icon for search fields.
 *
 * @uiDefault SearchField.searchIconColor				Color
 * @uiDefault SearchField.searchIconHoverColor			Color
 * @uiDefault SearchField.searchIconPressedColor		Color
 *
 * @author Karl Tauber
 * @since 1.5
 */
public class FlatSearchIcon
	extends FlatAbstractIcon
{
	@Styleable protected Color searchIconColor = UIManager.getColor( "SearchField.searchIconColor" );
	@Styleable protected Color searchIconHoverColor = UIManager.getColor( "SearchField.searchIconHoverColor" );
	@Styleable protected Color searchIconPressedColor = UIManager.getColor( "SearchField.searchIconPressedColor" );

	private final boolean ignoreButtonState;
	private Area area;

	public FlatSearchIcon() {
		this( false );
	}

	/** @since 2 */
	public FlatSearchIcon( boolean ignoreButtonState ) {
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
		/*
			<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
			  <g fill="none" fill-opacity=".9" fill-rule="evenodd">
			    <polygon fill="#7F8B91" points="10.813 9.75 14 12.938 12.938 14 9.75 10.813"/>
			    <path fill="#7F8B91" d="M7,2 C9.76142375,2 12,4.23857625 12,7 C12,9.76142375 9.76142375,12 7,12 C4.23857625,12 2,9.76142375 2,7 C2,4.23857625 4.23857625,2 7,2 Z M7,3 C4.790861,3 3,4.790861 3,7 C3,9.209139 4.790861,11 7,11 C9.209139,11 11,9.209139 11,7 C11,4.790861 9.209139,3 7,3 Z"/>
			  </g>
			</svg>
		*/

		g.setColor( ignoreButtonState
			? searchIconColor
			: FlatButtonUI.buttonStateColor( c, searchIconColor, searchIconColor,
				null, searchIconHoverColor, searchIconPressedColor ) );

		// paint magnifier
		if( area == null ) {
			area = new Area( new Ellipse2D.Float( 2, 2, 10, 10 ) );
			area.subtract( new Area( new Ellipse2D.Float( 3, 3, 8, 8 ) ) );
			area.add( new Area( FlatUIUtils.createPath( 10.813,9.75, 14,12.938, 12.938,14, 9.75,10.813 ) ) );
		}
		g.fill( area );
	}
}
