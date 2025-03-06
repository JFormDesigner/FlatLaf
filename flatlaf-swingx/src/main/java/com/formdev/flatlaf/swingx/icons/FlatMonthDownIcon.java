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

package com.formdev.flatlaf.swingx.icons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Shape;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import com.formdev.flatlaf.icons.FlatAbstractIcon;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * "month down" icon for {@link org.jdesktop.swingx.JXMonthView}.
 *
 * @uiDefault Component.arrowType				String	chevron (default) or triangle
 * @uiDefault JXMonthView.arrowColor			Color
 * @uiDefault JXMonthView.disabledArrowColor	Color
 *
 * @author Karl Tauber
 */
public class FlatMonthDownIcon
	extends FlatAbstractIcon
{
	protected final boolean chevron = FlatUIUtils.isChevron( UIManager.getString( "Component.arrowType" ) );
	protected final Color arrowColor = UIManager.getColor( "JXMonthView.arrowColor" );
	protected final Color disabledArrowColor = UIManager.getColor( "JXMonthView.disabledArrowColor" );

	private final int direction;

	public FlatMonthDownIcon() {
		this( SwingConstants.WEST );
	}

	protected FlatMonthDownIcon( int direction ) {
		super( 20, 20, null );
		this.direction = direction;
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		int w = chevron ? 4 : 5;
		int h = chevron ? 8 : 9;
		int x = Math.round( (width - w) / 2f );
		int y = Math.round( (height - h) / 2f );

		g.setColor( c.isEnabled() ? arrowColor : disabledArrowColor );
		g.translate( x, y );
		Shape arrowShape = FlatUIUtils.createArrowShape( direction, chevron, w, h );
		if( chevron ) {
			// chevron arrow
			g.setStroke( new BasicStroke( 1f ) );
			g.draw( arrowShape );
		} else {
			// triangle arrow
			g.fill( arrowShape );
		}
		g.translate( -x, -y );
	}
}
