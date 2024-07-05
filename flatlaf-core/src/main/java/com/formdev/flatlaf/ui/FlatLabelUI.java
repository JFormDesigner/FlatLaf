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

package com.formdev.flatlaf.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicLabelUI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JLabel}.
 *
 * <!-- BasicLabelUI -->
 *
 * @uiDefault Label.font				Font
 * @uiDefault Label.background			Color	only used if opaque
 * @uiDefault Label.foreground			Color
 *
 * <!-- FlatLabelUI -->
 *
 * @uiDefault Label.disabledForeground	Color
 *
 * @author Karl Tauber
 */
public class FlatLabelUI
	extends BasicLabelUI
	implements StyleableUI
{
	@Styleable protected Color disabledForeground;

	// only used via styling (not in UI defaults)
	/** @since 3.5 */ @Styleable protected int arc = -1;

	private final boolean shared;
	private boolean defaults_initialized = false;
	private Map<String, Object> oldStyleValues;

	public static ComponentUI createUI( JComponent c ) {
		return FlatUIUtils.canUseSharedUI( c )
			? FlatUIUtils.createSharedUI( FlatLabelUI.class, () -> new FlatLabelUI( true ) )
			: new FlatLabelUI( false );
	}

	/** @since 2 */
	protected FlatLabelUI( boolean shared ) {
		this.shared = shared;
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		installStyle( (JLabel) c );
	}

	@Override
	protected void installDefaults( JLabel c ) {
		super.installDefaults( c );

		if( !defaults_initialized ) {
			disabledForeground = UIManager.getColor( "Label.disabledForeground" );

			defaults_initialized = true;
		}
	}

	@Override
	protected void uninstallDefaults( JLabel c ) {
		super.uninstallDefaults( c );

		defaults_initialized = false;
		oldStyleValues = null;
	}

	@Override
	protected void installComponents( JLabel c ) {
		super.installComponents( c );

		// update HTML renderer if necessary
		FlatHTML.updateRendererCSSFontBaseSize( c );
	}

	@Override
	public void propertyChange( PropertyChangeEvent e ) {
		String name = e.getPropertyName();
		if( name.equals( FlatClientProperties.STYLE ) || name.equals( FlatClientProperties.STYLE_CLASS ) ) {
			JLabel label = (JLabel) e.getSource();
			if( shared && FlatStylingSupport.hasStyleProperty( label ) ) {
				// unshare component UI if necessary
				// updateUI() invokes installStyle() from installUI()
				label.updateUI();
			} else
				installStyle( label );
			label.revalidate();
			HiDPIUtils.repaint( label );
		}

		super.propertyChange( e );
		FlatHTML.propertyChange( e );
	}

	/** @since 2 */
	protected void installStyle( JLabel c ) {
		try {
			applyStyle( c, FlatStylingSupport.getResolvedStyle( c, "Label" ) );
		} catch( RuntimeException ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	/** @since 2 */
	protected void applyStyle( JLabel c, Object style ) {
		oldStyleValues = FlatStylingSupport.parseAndApply( oldStyleValues, style,
			(key, value) -> applyStyleProperty( c, key, value ) );
	}

	/** @since 2 */
	protected Object applyStyleProperty( JLabel c, String key, Object value ) {
		return FlatStylingSupport.applyToAnnotatedObjectOrComponent( this, c, key, value );
	}

	/** @since 2 */
	@Override
	public Map<String, Class<?>> getStyleableInfos( JComponent c ) {
		return FlatStylingSupport.getAnnotatedStyleableInfos( this );
	}

	/** @since 2.5 */
	@Override
	public Object getStyleableValue( JComponent c, String key ) {
		return FlatStylingSupport.getAnnotatedStyleableValue( this, key );
	}

	@Override
	public void update( Graphics g, JComponent c ) {
		FlatPanelUI.fillRoundedBackground( g, c, arc );
		paint( g, c );
	}

	static Graphics createGraphicsHTMLTextYCorrection( Graphics g, JComponent c ) {
		return (c.getClientProperty( BasicHTML.propertyKey ) != null)
			? HiDPIUtils.createGraphicsTextYCorrection( (Graphics2D) g )
			: g;
	}

	@Override
	public void paint( Graphics g, JComponent c ) {
		super.paint( createGraphicsHTMLTextYCorrection( g, c ), c );
	}

	@Override
	protected void paintEnabledText( JLabel l, Graphics g, String s, int textX, int textY ) {
		int mnemIndex = FlatLaf.isShowMnemonics() ? l.getDisplayedMnemonicIndex() : -1;
		g.setColor( l.getForeground() );
		FlatUIUtils.drawStringUnderlineCharAt( l, g, s, mnemIndex, textX, textY );
	}

	@Override
	protected void paintDisabledText( JLabel l, Graphics g, String s, int textX, int textY ) {
		int mnemIndex = FlatLaf.isShowMnemonics() ? l.getDisplayedMnemonicIndex() : -1;
		g.setColor( disabledForeground );
		FlatUIUtils.drawStringUnderlineCharAt( l, g, s, mnemIndex, textX, textY );
	}

	/**
	 * Overridden to scale iconTextGap.
	 */
	@Override
	protected String layoutCL( JLabel label, FontMetrics fontMetrics, String text, Icon icon, Rectangle viewR,
		Rectangle iconR, Rectangle textR )
	{
		return SwingUtilities.layoutCompoundLabel( label, fontMetrics, text, icon,
			label.getVerticalAlignment(), label.getHorizontalAlignment(),
			label.getVerticalTextPosition(), label.getHorizontalTextPosition(),
			viewR, iconR, textR,
			UIScale.scale( label.getIconTextGap() ) );
	}
}
