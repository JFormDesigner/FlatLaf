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
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import javax.swing.UIManager;
import com.formdev.flatlaf.ui.FlatButtonUI;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * Help button icon for {@link javax.swing.JButton}.
 *
 * @uiDefault Component.focusWidth						int
 * @uiDefault Component.focusColor						Color
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
	protected final int focusWidth = UIManager.getInt( "Component.focusWidth" );
	protected final Color focusColor = UIManager.getColor( "Component.focusColor" );

	protected final Color borderColor = UIManager.getColor( "HelpButton.borderColor" );
	protected final Color disabledBorderColor = UIManager.getColor( "HelpButton.disabledBorderColor" );
	protected final Color focusedBorderColor = UIManager.getColor( "HelpButton.focusedBorderColor" );
	protected final Color hoverBorderColor = UIManager.getColor( "HelpButton.hoverBorderColor" );
	protected final Color background = UIManager.getColor( "HelpButton.background" );
	protected final Color disabledBackground = UIManager.getColor( "HelpButton.disabledBackground" );
	protected final Color focusedBackground = UIManager.getColor( "HelpButton.focusedBackground" );
	protected final Color hoverBackground = UIManager.getColor( "HelpButton.hoverBackground" );
	protected final Color pressedBackground = UIManager.getColor( "HelpButton.pressedBackground" );
	protected final Color questionMarkColor = UIManager.getColor( "HelpButton.questionMarkColor" );
	protected final Color disabledQuestionMarkColor = UIManager.getColor( "HelpButton.disabledQuestionMarkColor" );

	protected final int iconSize = 22 + (focusWidth * 2);

	public FlatHelpButtonIcon() {
		super( 0, 0, null );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g2 ) {
		/*
			<svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 22 22">
			  <g fill="none" fill-rule="evenodd">
			    <circle cx="11" cy="11" r="10.5" fill="#6E6E6E"/>
			    <circle cx="11" cy="11" r="9.5" fill="#FFF"/>
			    <path fill="#6E6E6E" d="M10,17 L12,17 L12,15 L10,15 L10,17 Z M11,5 C8.8,5 7,6.8 7,9 L9,9 C9,7.9 9.9,7 11,7 C12.1,7 13,7.9 13,9 C13,11 10,10.75 10,14 L12,14 C12,11.75 15,11.5 15,9 C15,6.8 13.2,5 11,5 Z"/>
			  </g>
			</svg>
		*/

		boolean enabled = c.isEnabled();
		boolean focused = FlatUIUtils.isPermanentFocusOwner( c );

		// paint focused border
		if( focused ) {
			g2.setColor( focusColor );
			g2.fill( new Ellipse2D.Float( 0.5f, 0.5f, iconSize - 1, iconSize - 1 ) );
		}

		// paint border
		g2.setColor( FlatButtonUI.buttonStateColor( c,
			borderColor,
			disabledBorderColor,
			focusedBorderColor,
			hoverBorderColor,
			null ) );
		g2.fill( new Ellipse2D.Float( focusWidth + 0.5f, focusWidth + 0.5f, 21, 21 ) );

		// paint background
		g2.setColor( FlatUIUtils.deriveColor( FlatButtonUI.buttonStateColor( c,
			background,
			disabledBackground,
			focusedBackground,
			hoverBackground,
			pressedBackground ), background ) );
		g2.fill( new Ellipse2D.Float( focusWidth + 1.5f, focusWidth + 1.5f, 19, 19 ) );

		// paint question mark
		Path2D q = new Path2D.Float();
		q.moveTo( 11, 5 );
		q.curveTo( 8.8,5, 7,6.8, 7,9 );
		q.lineTo( 9, 9 );
		q.curveTo( 9,7.9, 9.9,7, 11,7 );
		q.curveTo( 12.1,7, 13,7.9, 13,9 );
		q.curveTo( 13,11, 10,10.75, 10,14 );
		q.lineTo( 12, 14 );
		q.curveTo( 12,11.75, 15,11.5, 15,9 );
		q.curveTo( 15,6.8, 13.2,5, 11,5 );
		q.closePath();

		g2.translate( focusWidth, focusWidth );
		g2.setColor( enabled ? questionMarkColor : disabledQuestionMarkColor );
		g2.fill( q );
		g2.fillRect( 10, 15, 2, 2 );
	}

	@Override
	public int getIconWidth() {
		return scale( iconSize );
	}

	@Override
	public int getIconHeight() {
		return scale( iconSize );
	}
}
