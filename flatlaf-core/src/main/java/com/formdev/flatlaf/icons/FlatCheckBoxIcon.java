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

import static com.formdev.flatlaf.FlatClientProperties.*;
import static com.formdev.flatlaf.ui.FlatUIUtils.stateColor;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import com.formdev.flatlaf.ui.FlatButtonUI;
import com.formdev.flatlaf.ui.FlatStylingSupport;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * Icon for {@link javax.swing.JCheckBox}.
 * <p>
 * <strong>Note</strong>:
 *       If Component.focusWidth is greater than zero, then the outer focus border
 *       is painted outside of the icon bounds. Make sure that the checkbox
 *       has margins, which are equal or greater than focusWidth.
 *
 * @uiDefault CheckBox.icon.style						String	optional; "outlined"/null (default) or "filled"
 * @uiDefault Component.focusWidth						int
 * @uiDefault Component.borderWidth						int
 * @uiDefault Component.focusColor						Color
 * @uiDefault CheckBox.icon.focusWidth					int or float	optional; defaults to Component.focusWidth
 * @uiDefault CheckBox.icon.borderWidth					int or float	optional; defaults to Component.borderWidth
 * @uiDefault CheckBox.icon.selectedBorderWidth			int or float	optional; defaults to CheckBox.icon.borderWidth
 * @uiDefault CheckBox.icon.disabledSelectedBorderWidth	int or float	optional; defaults to CheckBox.icon.selectedBorderWidth
 * @uiDefault CheckBox.icon.indeterminateBorderWidth			int or float	optional; defaults to CheckBox.icon.selectedBorderWidth
 * @uiDefault CheckBox.icon.disabledIndeterminateBorderWidth	int or float	optional; defaults to CheckBox.icon.disabledSelectedBorderWidth
 * @uiDefault CheckBox.arc								int
 *
 * @uiDefault CheckBox.icon.focusColor					Color	optional; defaults to Component.focusColor
 * @uiDefault CheckBox.icon.borderColor					Color
 * @uiDefault CheckBox.icon.background					Color
 * @uiDefault CheckBox.icon.selectedBorderColor			Color
 * @uiDefault CheckBox.icon.selectedBackground			Color
 * @uiDefault CheckBox.icon.checkmarkColor				Color
 * @uiDefault CheckBox.icon.indeterminateBorderColor	Color	optional; defaults to CheckBox.icon.selectedBorderColor
 * @uiDefault CheckBox.icon.indeterminateBackground		Color	optional; defaults to CheckBox.icon.selectedBackground
 * @uiDefault CheckBox.icon.indeterminateCheckmarkColor	Color	optional; defaults to CheckBox.icon.checkmarkColor
 *
 * @uiDefault CheckBox.icon.disabledBorderColor					Color
 * @uiDefault CheckBox.icon.disabledBackground					Color
 * @uiDefault CheckBox.icon.disabledSelectedBorderColor			Color	optional; defaults to CheckBox.icon.disabledBorderColor
 * @uiDefault CheckBox.icon.disabledSelectedBackground			Color	optional; defaults to CheckBox.icon.disabledBackground
 * @uiDefault CheckBox.icon.disabledCheckmarkColor				Color
 * @uiDefault CheckBox.icon.disabledIndeterminateBorderColor	Color	optional; defaults to CheckBox.icon.disabledSelectedBorderColor
 * @uiDefault CheckBox.icon.disabledIndeterminateBackground		Color	optional; defaults to CheckBox.icon.disabledSelectedBackground
 * @uiDefault CheckBox.icon.disabledIndeterminateCheckmarkColor	Color	optional; defaults to CheckBox.icon.disabledCheckmarkColor
 *
 * @uiDefault CheckBox.icon.focusedBorderColor					Color	optional
 * @uiDefault CheckBox.icon.focusedBackground					Color	optional
 * @uiDefault CheckBox.icon.focusedSelectedBorderColor			Color	optional; defaults to CheckBox.icon.focusedBorderColor
 * @uiDefault CheckBox.icon.focusedSelectedBackground			Color	optional; defaults to CheckBox.icon.focusedBackground
 * @uiDefault CheckBox.icon.focusedCheckmarkColor				Color	optional; defaults to CheckBox.icon.checkmarkColor
 * @uiDefault CheckBox.icon.focusedIndeterminateBorderColor		Color	optional; defaults to CheckBox.icon.focusedSelectedBorderColor
 * @uiDefault CheckBox.icon.focusedIndeterminateBackground		Color	optional; defaults to CheckBox.icon.focusedSelectedBackground
 * @uiDefault CheckBox.icon.focusedIndeterminateCheckmarkColor	Color	optional; defaults to CheckBox.icon.focusedCheckmarkColor
 *
 * @uiDefault CheckBox.icon.hoverBorderColor					Color	optional
 * @uiDefault CheckBox.icon.hoverBackground						Color	optional
 * @uiDefault CheckBox.icon.hoverSelectedBorderColor			Color	optional; defaults to CheckBox.icon.hoverBorderColor
 * @uiDefault CheckBox.icon.hoverSelectedBackground				Color	optional; defaults to CheckBox.icon.hoverBackground
 * @uiDefault CheckBox.icon.hoverCheckmarkColor					Color	optional; defaults to CheckBox.icon.checkmarkColor
 * @uiDefault CheckBox.icon.hoverIndeterminateBorderColor		Color	optional; defaults to CheckBox.icon.hoverSelectedBorderColor
 * @uiDefault CheckBox.icon.hoverIndeterminateBackground		Color	optional; defaults to CheckBox.icon.hoverSelectedBackground
 * @uiDefault CheckBox.icon.hoverIndeterminateCheckmarkColor	Color	optional; defaults to CheckBox.icon.hoverCheckmarkColor
 *
 * @uiDefault CheckBox.icon.pressedBorderColor					Color	optional
 * @uiDefault CheckBox.icon.pressedBackground					Color	optional
 * @uiDefault CheckBox.icon.pressedSelectedBorderColor			Color	optional; defaults to CheckBox.icon.pressedBorderColor
 * @uiDefault CheckBox.icon.pressedSelectedBackground			Color	optional; defaults to CheckBox.icon.pressedBackground
 * @uiDefault CheckBox.icon.pressedCheckmarkColor				Color	optional; defaults to CheckBox.icon.checkmarkColor
 * @uiDefault CheckBox.icon.pressedIndeterminateBorderColor		Color	optional; defaults to CheckBox.icon.pressedSelectedBorderColor
 * @uiDefault CheckBox.icon.pressedIndeterminateBackground		Color	optional; defaults to CheckBox.icon.pressedSelectedBackground
 * @uiDefault CheckBox.icon.pressedIndeterminateCheckmarkColor	Color	optional; defaults to CheckBox.icon.pressedCheckmarkColor
 *
 * @author Karl Tauber
 */
public class FlatCheckBoxIcon
	extends FlatAbstractIcon
{
	protected final String style = UIManager.getString( getPropertyPrefix() + "icon.style" );
	@Styleable protected float focusWidth = getUIFloat( "CheckBox.icon.focusWidth", UIManager.getInt( "Component.focusWidth" ), style );
	@Styleable protected Color focusColor = FlatUIUtils.getUIColor( "CheckBox.icon.focusColor", UIManager.getColor( "Component.focusColor" ) );
	/** @since 2 */ @Styleable protected float borderWidth = getUIFloat( "CheckBox.icon.borderWidth", FlatUIUtils.getUIFloat( "Component.borderWidth", 1 ), style );
	/** @since 2 */ @Styleable protected float selectedBorderWidth = getUIFloat( "CheckBox.icon.selectedBorderWidth", Float.MIN_VALUE, style );
	/** @since 2 */ @Styleable protected float disabledSelectedBorderWidth = getUIFloat( "CheckBox.icon.disabledSelectedBorderWidth", Float.MIN_VALUE, style );
	/** @since 3.6 */ @Styleable protected float indeterminateBorderWidth = getUIFloat( "CheckBox.icon.indeterminateBorderWidth", Float.MIN_VALUE, style );
	/** @since 3.6 */ @Styleable protected float disabledIndeterminateBorderWidth = getUIFloat( "CheckBox.icon.disabledIndeterminateBorderWidth", Float.MIN_VALUE, style );
	@Styleable protected int arc = FlatUIUtils.getUIInt( "CheckBox.arc", 2 );

	// enabled
	@Styleable protected Color borderColor = getUIColor( "CheckBox.icon.borderColor", style );
	@Styleable protected Color background = getUIColor( "CheckBox.icon.background", style );
	@Styleable protected Color selectedBorderColor = getUIColor( "CheckBox.icon.selectedBorderColor", style );
	@Styleable protected Color selectedBackground = getUIColor( "CheckBox.icon.selectedBackground", style );
	@Styleable protected Color checkmarkColor = getUIColor( "CheckBox.icon.checkmarkColor", style );
	/** @since 3.6 */ @Styleable protected Color indeterminateBorderColor = getUIColor( "CheckBox.icon.indeterminateBorderColor", style );
	/** @since 3.6 */ @Styleable protected Color indeterminateBackground = getUIColor( "CheckBox.icon.indeterminateBackground", style );
	/** @since 3.6 */ @Styleable protected Color indeterminateCheckmarkColor = getUIColor( "CheckBox.icon.indeterminateCheckmarkColor", style );

	// disabled
	@Styleable protected Color disabledBorderColor = getUIColor( "CheckBox.icon.disabledBorderColor", style );
	@Styleable protected Color disabledBackground = getUIColor( "CheckBox.icon.disabledBackground", style );
	/** @since 2 */ @Styleable protected Color disabledSelectedBorderColor = getUIColor( "CheckBox.icon.disabledSelectedBorderColor", style );
	/** @since 2 */ @Styleable protected Color disabledSelectedBackground = getUIColor( "CheckBox.icon.disabledSelectedBackground", style );
	@Styleable protected Color disabledCheckmarkColor = getUIColor( "CheckBox.icon.disabledCheckmarkColor", style );
	/** @since 3.6 */ @Styleable protected Color disabledIndeterminateBorderColor = getUIColor( "CheckBox.icon.disabledIndeterminateBorderColor", style );
	/** @since 3.6 */ @Styleable protected Color disabledIndeterminateBackground = getUIColor( "CheckBox.icon.disabledIndeterminateBackground", style );
	/** @since 3.6 */ @Styleable protected Color disabledIndeterminateCheckmarkColor = getUIColor( "CheckBox.icon.disabledIndeterminateCheckmarkColor", style );

	// focused
	@Styleable protected Color focusedBorderColor = getUIColor( "CheckBox.icon.focusedBorderColor", style );
	@Styleable protected Color focusedBackground = getUIColor( "CheckBox.icon.focusedBackground", style );
	/** @since 2 */ @Styleable protected Color focusedSelectedBorderColor = getUIColor( "CheckBox.icon.focusedSelectedBorderColor", style );
	/** @since 2 */ @Styleable protected Color focusedSelectedBackground = getUIColor( "CheckBox.icon.focusedSelectedBackground", style );
	/** @since 2 */ @Styleable protected Color focusedCheckmarkColor = getUIColor( "CheckBox.icon.focusedCheckmarkColor", style );
	/** @since 3.6 */ @Styleable protected Color focusedIndeterminateBorderColor = getUIColor( "CheckBox.icon.focusedIndeterminateBorderColor", style );
	/** @since 3.6 */ @Styleable protected Color focusedIndeterminateBackground = getUIColor( "CheckBox.icon.focusedIndeterminateBackground", style );
	/** @since 3.6 */ @Styleable protected Color focusedIndeterminateCheckmarkColor = getUIColor( "CheckBox.icon.focusedIndeterminateCheckmarkColor", style );

	// hover
	@Styleable protected Color hoverBorderColor = getUIColor( "CheckBox.icon.hoverBorderColor", style );
	@Styleable protected Color hoverBackground = getUIColor( "CheckBox.icon.hoverBackground", style );
	/** @since 2 */ @Styleable protected Color hoverSelectedBorderColor = getUIColor( "CheckBox.icon.hoverSelectedBorderColor", style );
	/** @since 2 */ @Styleable protected Color hoverSelectedBackground = getUIColor( "CheckBox.icon.hoverSelectedBackground", style );
	/** @since 2 */ @Styleable protected Color hoverCheckmarkColor = getUIColor( "CheckBox.icon.hoverCheckmarkColor", style );
	/** @since 3.6 */ @Styleable protected Color hoverIndeterminateBorderColor = getUIColor( "CheckBox.icon.hoverIndeterminateBorderColor", style );
	/** @since 3.6 */ @Styleable protected Color hoverIndeterminateBackground = getUIColor( "CheckBox.icon.hoverIndeterminateBackground", style );
	/** @since 3.6 */ @Styleable protected Color hoverIndeterminateCheckmarkColor = getUIColor( "CheckBox.icon.hoverIndeterminateCheckmarkColor", style );

	// pressed
	/** @since 2 */ @Styleable protected Color pressedBorderColor = getUIColor( "CheckBox.icon.pressedBorderColor", style );
	@Styleable protected Color pressedBackground = getUIColor( "CheckBox.icon.pressedBackground", style );
	/** @since 2 */ @Styleable protected Color pressedSelectedBorderColor = getUIColor( "CheckBox.icon.pressedSelectedBorderColor", style );
	/** @since 2 */ @Styleable protected Color pressedSelectedBackground = getUIColor( "CheckBox.icon.pressedSelectedBackground", style );
	/** @since 2 */ @Styleable protected Color pressedCheckmarkColor = getUIColor( "CheckBox.icon.pressedCheckmarkColor", style );
	/** @since 3.6 */ @Styleable protected Color pressedIndeterminateBorderColor = getUIColor( "CheckBox.icon.pressedIndeterminateBorderColor", style );
	/** @since 3.6 */ @Styleable protected Color pressedIndeterminateBackground = getUIColor( "CheckBox.icon.pressedIndeterminateBackground", style );
	/** @since 3.6 */ @Styleable protected Color pressedIndeterminateCheckmarkColor = getUIColor( "CheckBox.icon.pressedIndeterminateCheckmarkColor", style );

	protected String getPropertyPrefix() {
		return "CheckBox.";
	}

	protected static Color getUIColor( String key, String style ) {
		if( style != null ) {
			Color color = UIManager.getColor( styleKey( key, style ) );
			if( color != null )
				return color;
		}
		return UIManager.getColor( key );
	}

	/** @since 2 */
	protected static float getUIFloat( String key, float defaultValue, String style ) {
		if( style != null ) {
			float value = FlatUIUtils.getUIFloat( styleKey( key, style ), Float.MIN_VALUE );
			if( value != Float.MIN_VALUE )
				return value;
		}
		return FlatUIUtils.getUIFloat( key, defaultValue );
	}

	private static String styleKey( String key, String style ) {
		return key.replace( ".icon.", ".icon[" + style + "]." );
	}

	static final int ICON_SIZE = 15;

	public FlatCheckBoxIcon() {
		super( ICON_SIZE, ICON_SIZE, null );
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
		boolean indeterminate = isIndeterminate( c );
		boolean selected = indeterminate || isSelected( c );
		boolean isFocused = FlatUIUtils.isPermanentFocusOwner( c );
		float bw = Float.MIN_VALUE;
		if( !c.isEnabled() ) {
			bw = (indeterminate && disabledIndeterminateBorderWidth != Float.MIN_VALUE)
				? disabledIndeterminateBorderWidth
				: (selected ? disabledSelectedBorderWidth : selectedBorderWidth);
		}
		if( bw == Float.MIN_VALUE ) {
			bw = (indeterminate && indeterminateBorderWidth != Float.MIN_VALUE)
				? indeterminateBorderWidth
				: ((selected && selectedBorderWidth != Float.MIN_VALUE) ? selectedBorderWidth : borderWidth);
		}

		// paint focused border
		if( isFocused && focusWidth > 0 && FlatButtonUI.isFocusPainted( c ) ) {
			g.setColor( getFocusColor( c ) );
			paintFocusBorder( c, g );
		}

		// paint border
		g.setColor( getBorderColor( c, selected, indeterminate ) );
		paintBorder( c, g, bw );

		// paint background
		Color baseBg = stateColor( indeterminate, indeterminateBackground, selected, selectedBackground, background );
		Color bg = FlatUIUtils.deriveColor( getBackground( c, selected, indeterminate ), baseBg );
		if( bg.getAlpha() < 255 ) {
			// fill background with default color before filling with non-opaque background
			g.setColor( baseBg );
			paintBackground( c, g, bw );
		}
		g.setColor( bg );
		paintBackground( c, g, bw );

		// paint checkmark
		if( selected ) {
			g.setColor( getCheckmarkColor( c, indeterminate ) );
			if( indeterminate )
				paintIndeterminate( c, g );
			else
				paintCheckmark( c, g );
		}
	}

	protected void paintFocusBorder( Component c, Graphics2D g ) {
		// the outer focus border is painted outside of the icon
		float wh = ICON_SIZE - 1 + (focusWidth * 2);
		float arcwh = arc + (focusWidth * 2);
		g.fill( new RoundRectangle2D.Float( -focusWidth + 1, -focusWidth, wh, wh, arcwh, arcwh ) );
	}

	protected void paintBorder( Component c, Graphics2D g, float borderWidth ) {
		if( borderWidth == 0 )
			return;

		int arcwh = arc;
		g.fillRoundRect( 1, 0, 14, 14, arcwh, arcwh );
	}

	protected void paintBackground( Component c, Graphics2D g, float borderWidth ) {
		float xy = borderWidth;
		float wh = 14 - (borderWidth * 2);
		float arcwh = arc - borderWidth;
		g.fill( new RoundRectangle2D.Float( 1 + xy, xy, wh, wh, arcwh, arcwh ) );
	}

	protected void paintCheckmark( Component c, Graphics2D g ) {
		Path2D.Float path = new Path2D.Float( Path2D.WIND_NON_ZERO, 3 );
		path.moveTo( 4.5f, 7.5f );
		path.lineTo( 6.6f, 10f );
		path.lineTo( 11.25f, 3.5f );

		g.setStroke( new BasicStroke( 1.9f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
		g.draw( path );
	}

	protected void paintIndeterminate( Component c, Graphics2D g ) {
		g.fill( new RoundRectangle2D.Float( 3.75f, 5.75f, 8.5f, 2.5f, 2f, 2f ) );
	}

	protected boolean isIndeterminate( Component c ) {
		return c instanceof JComponent && clientPropertyEquals( (JComponent) c, SELECTED_STATE, SELECTED_STATE_INDETERMINATE );
	}

	protected boolean isSelected( Component c ) {
		return c instanceof AbstractButton && ((AbstractButton)c).isSelected();
	}

	/** @since 2 */
	public float getFocusWidth() {
		return focusWidth;
	}

	protected Color getFocusColor( Component c ) {
		return focusColor;
	}

	/** @since 3.6 */
	protected Color getBorderColor( Component c, boolean selected, boolean indeterminate ) {
		return FlatButtonUI.buttonStateColor( c,
			stateColor( indeterminate, indeterminateBorderColor,         selected, selectedBorderColor,         borderColor ),
			stateColor( indeterminate, disabledIndeterminateBorderColor, selected, disabledSelectedBorderColor, disabledBorderColor ),
			stateColor( indeterminate, focusedIndeterminateBorderColor,  selected, focusedSelectedBorderColor,  focusedBorderColor ),
			stateColor( indeterminate, hoverIndeterminateBorderColor,    selected, hoverSelectedBorderColor,    hoverBorderColor ),
			stateColor( indeterminate, pressedIndeterminateBorderColor,  selected, pressedSelectedBorderColor,  pressedBorderColor ) );
	}

	/** @since 3.6 */
	protected Color getBackground( Component c, boolean selected, boolean indeterminate ) {
		return FlatButtonUI.buttonStateColor( c,
			stateColor( indeterminate, indeterminateBackground,          selected, selectedBackground,          background ),
			stateColor( indeterminate, disabledIndeterminateBackground,  selected, disabledSelectedBackground,  disabledBackground ),
			stateColor( indeterminate, focusedIndeterminateBackground,   selected, focusedSelectedBackground,   focusedBackground ),
			stateColor( indeterminate, hoverIndeterminateBackground,     selected, hoverSelectedBackground,     hoverBackground ),
			stateColor( indeterminate, pressedIndeterminateBackground,   selected, pressedSelectedBackground,   pressedBackground ) );
	}

	/** @since 3.6 */
	protected Color getCheckmarkColor( Component c, boolean indeterminate ) {
		return FlatButtonUI.buttonStateColor( c,
			stateColor( indeterminate, indeterminateCheckmarkColor, checkmarkColor ),
			stateColor( indeterminate, disabledIndeterminateCheckmarkColor, disabledCheckmarkColor ),
			stateColor( indeterminate, focusedIndeterminateCheckmarkColor, focusedCheckmarkColor ),
			stateColor( indeterminate, hoverIndeterminateCheckmarkColor, hoverCheckmarkColor ),
			stateColor( indeterminate, pressedIndeterminateCheckmarkColor, pressedCheckmarkColor ) );
	}
}
