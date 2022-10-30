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

import static com.formdev.flatlaf.util.UIScale.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.Map;
import javax.swing.UIManager;
import com.formdev.flatlaf.ui.FlatButtonUI;
import com.formdev.flatlaf.ui.FlatStylingSupport;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * Help button icon for {@link javax.swing.JButton}.
 *
 * @uiDefault Component.focusWidth						int
 * @uiDefault Component.focusColor						Color
 * @uiDefault HelpButton.innerFocusWidth				int or float	optional; defaults to Component.innerFocusWidth
 * @uiDefault HelpButton.borderWidth					int		optional; default is 1
 * @uiDefault HelpButton.borderColor					Color
 * @uiDefault HelpButton.disabledBorderColor			Color
 * @uiDefault HelpButton.focusedBorderColor				Color
 * @uiDefault HelpButton.hoverBorderColor				Color	optional
 * @uiDefault HelpButton.background						Color
 * @uiDefault HelpButton.disabledBackground				Color
 * @uiDefault HelpButton.focusedBackground				Color	optional
 * @uiDefault HelpButton.hoverBackground				Color	optional
 * @uiDefault HelpButton.pressedBackground				Color	optional
 * @uiDefault HelpButton.questionMarkColor				Color
 * @uiDefault HelpButton.disabledQuestionMarkColor		Color
 *
 * @author Karl Tauber
 */
public class FlatHelpButtonIcon
	extends FlatAbstractIcon
{
	@Styleable protected int focusWidth = UIManager.getInt( "Component.focusWidth" );
	@Styleable protected Color focusColor = UIManager.getColor( "Component.focusColor" );
	@Styleable protected float innerFocusWidth = FlatUIUtils.getUIFloat( "HelpButton.innerFocusWidth", FlatUIUtils.getUIFloat( "Component.innerFocusWidth", 0 ) );
	@Styleable protected int borderWidth = FlatUIUtils.getUIInt( "HelpButton.borderWidth", 1 );

	@Styleable protected Color borderColor = UIManager.getColor( "HelpButton.borderColor" );
	@Styleable protected Color disabledBorderColor = UIManager.getColor( "HelpButton.disabledBorderColor" );
	@Styleable protected Color focusedBorderColor = UIManager.getColor( "HelpButton.focusedBorderColor" );
	@Styleable protected Color hoverBorderColor = UIManager.getColor( "HelpButton.hoverBorderColor" );
	@Styleable protected Color background = UIManager.getColor( "HelpButton.background" );
	@Styleable protected Color disabledBackground = UIManager.getColor( "HelpButton.disabledBackground" );
	@Styleable protected Color focusedBackground = UIManager.getColor( "HelpButton.focusedBackground" );
	@Styleable protected Color hoverBackground = UIManager.getColor( "HelpButton.hoverBackground" );
	@Styleable protected Color pressedBackground = UIManager.getColor( "HelpButton.pressedBackground" );
	@Styleable protected Color questionMarkColor = UIManager.getColor( "HelpButton.questionMarkColor" );
	@Styleable protected Color disabledQuestionMarkColor = UIManager.getColor( "HelpButton.disabledQuestionMarkColor" );

	public FlatHelpButtonIcon() {
		super( 0, 0, null );
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
	protected void paintIcon( Component c, Graphics2D g2 ) {
		/*
			<svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 22 22">
			  <g fill="none" fill-rule="evenodd">
			    <circle cx="11" cy="11" r="10.5" fill="#6E6E6E"/>
			    <circle cx="11" cy="11" r="9.5" fill="#FFF"/>
			    <path stroke="#6E6E6E" stroke-linecap="round" stroke-width="2" d="M8,8.5 C8.25,7 9.66585007,6 11,6 C12.5,6 14,7 14,8.5 C14,10.5 11,11 11,13"/>
			    <circle cx="11" cy="16" r="1.2" fill="#6E6E6E"/>
			  </g>
			</svg>
		*/

		boolean enabled = c == null || c.isEnabled();
		boolean focused = c != null && FlatUIUtils.isPermanentFocusOwner( c );

		float xy = 0.5f;
		float wh = iconSize() - 1;

		// paint outer focus border
		if( focused && FlatButtonUI.isFocusPainted( c ) ) {
			g2.setColor( focusColor );
			g2.fill( new Ellipse2D.Float( xy, xy, wh, wh ) );
		}

		xy += focusWidth;
		wh -= (focusWidth * 2);

		// paint border
		g2.setColor( FlatButtonUI.buttonStateColor( c,
			borderColor,
			disabledBorderColor,
			focusedBorderColor,
			hoverBorderColor,
			null ) );
		g2.fill( new Ellipse2D.Float( xy, xy, wh, wh ) );

		xy += borderWidth;
		wh -= (borderWidth * 2);

		// paint inner focus border
		if( innerFocusWidth > 0 && focused && FlatButtonUI.isFocusPainted( c ) ) {
			g2.setColor( focusColor );
			g2.fill( new Ellipse2D.Float( xy, xy, wh, wh ) );

			xy += innerFocusWidth;
			wh -= (innerFocusWidth * 2);
		}

		// paint background
		g2.setColor( FlatUIUtils.deriveColor( FlatButtonUI.buttonStateColor( c,
			background,
			disabledBackground,
			focusedBackground,
			hoverBackground,
			pressedBackground ), background ) );
		g2.fill( new Ellipse2D.Float( xy, xy, wh, wh ) );

		// paint question mark
		Path2D q = new Path2D.Float( Path2D.WIND_NON_ZERO, 10 );
		q.moveTo( 8,8.5 );
		q.curveTo( 8.25,7, 9.66585007,6, 11,6 );
		q.curveTo( 12.5,6, 14,7, 14,8.5 );
		q.curveTo( 14,10.5, 11,11, 11,13 );

		g2.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
		g2.setStroke( new BasicStroke( 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );

		g2.translate( focusWidth, focusWidth );
		g2.setColor( enabled ? questionMarkColor : disabledQuestionMarkColor );
		g2.draw( q );
		g2.fill( new Ellipse2D.Float( 9.8f, 14.8f, 2.4f, 2.4f ) );
	}

	@Override
	public int getIconWidth() {
		return scale( iconSize() );
	}

	@Override
	public int getIconHeight() {
		return scale( iconSize() );
	}

	private int iconSize() {
		return 22 + (focusWidth * 2);
	}
}
