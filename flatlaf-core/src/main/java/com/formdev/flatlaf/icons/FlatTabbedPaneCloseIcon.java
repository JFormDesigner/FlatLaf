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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Map;
import javax.swing.UIManager;
import com.formdev.flatlaf.ui.FlatButtonUI;
import com.formdev.flatlaf.ui.FlatStylingSupport;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * "close" icon for closable tabs in {@link javax.swing.JTabbedPane}.
 *
 * @uiDefault TabbedPane.closeSize						Dimension
 * @uiDefault TabbedPane.closeArc						int
 * @uiDefault TabbedPane.closeCrossPlainSize			float
 * @uiDefault TabbedPane.closeCrossFilledSize			float
 * @uiDefault TabbedPane.closeCrossLineWidth			float
 * @uiDefault TabbedPane.closeBackground				Color
 * @uiDefault TabbedPane.closeForeground				Color
 * @uiDefault TabbedPane.closeHoverBackground			Color
 * @uiDefault TabbedPane.closeHoverForeground			Color
 * @uiDefault TabbedPane.closePressedBackground			Color
 * @uiDefault TabbedPane.closePressedForeground			Color
 *
 * @author Karl Tauber
 */
public class FlatTabbedPaneCloseIcon
	extends FlatAbstractIcon
{
	@Styleable protected Dimension closeSize = UIManager.getDimension( "TabbedPane.closeSize" );
	@Styleable protected int closeArc = UIManager.getInt( "TabbedPane.closeArc" );
	@Styleable protected float closeCrossPlainSize = FlatUIUtils.getUIFloat( "TabbedPane.closeCrossPlainSize", 7.5f );
	@Styleable protected float closeCrossFilledSize = FlatUIUtils.getUIFloat( "TabbedPane.closeCrossFilledSize", closeCrossPlainSize );
	@Styleable protected float closeCrossLineWidth = FlatUIUtils.getUIFloat( "TabbedPane.closeCrossLineWidth", 1f );
	@Styleable protected Color closeBackground = UIManager.getColor( "TabbedPane.closeBackground" );
	@Styleable protected Color closeForeground = UIManager.getColor( "TabbedPane.closeForeground" );
	@Styleable protected Color closeHoverBackground = UIManager.getColor( "TabbedPane.closeHoverBackground" );
	@Styleable protected Color closeHoverForeground = UIManager.getColor( "TabbedPane.closeHoverForeground" );
	@Styleable protected Color closePressedBackground = UIManager.getColor( "TabbedPane.closePressedBackground" );
	@Styleable protected Color closePressedForeground = UIManager.getColor( "TabbedPane.closePressedForeground" );

	public FlatTabbedPaneCloseIcon() {
		super( 16, 16, null );
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
		// paint background
		Color bg = FlatButtonUI.buttonStateColor( c, closeBackground, null, null, closeHoverBackground, closePressedBackground );
		if( bg != null ) {
			g.setColor( FlatUIUtils.deriveColor( bg, c.getBackground() ) );
			g.fillRoundRect( (width - closeSize.width) / 2, (height - closeSize.height) / 2,
				closeSize.width, closeSize.height, closeArc, closeArc );
		}

		// set cross color
		Color fg = FlatButtonUI.buttonStateColor( c, closeForeground, null, null, closeHoverForeground, closePressedForeground );
		g.setColor( FlatUIUtils.deriveColor( fg, c.getForeground() ) );

		float mx = width / 2;
		float my = height / 2;
		float r = ((bg != null) ? closeCrossFilledSize : closeCrossPlainSize) / 2;

		// paint cross
		Path2D path = new Path2D.Float( Path2D.WIND_EVEN_ODD, 4 );
		path.moveTo( mx - r, my - r );
		path.lineTo( mx + r, my + r );
		path.moveTo( mx - r, my + r );
		path.lineTo( mx + r, my - r );
		g.setStroke( new BasicStroke( closeCrossLineWidth ) );
		g.draw( path );
	}
}
