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

import static com.formdev.flatlaf.util.UIScale.scale;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSeparatorUI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.LoggingFacade;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JSeparator}.
 *
 * <!-- BasicSeparatorUI -->
 *
 * @uiDefault Separator.background		Color	unused
 * @uiDefault Separator.foreground		Color
 *
 * <!-- FlatSeparatorUI -->
 *
 * @uiDefault Separator.height			int		height (or width) of the component; may be larger than stripe
 * @uiDefault Separator.stripeWidth		int		width of the stripe
 * @uiDefault Separator.stripeIndent	int		indent of stripe from top (or left); allows positioning of stripe within component
 *
 * @author Karl Tauber
 */
public class FlatSeparatorUI
	extends BasicSeparatorUI
	implements StyleableUI, PropertyChangeListener
{
	@Styleable protected int height;
	@Styleable protected int stripeWidth;
	@Styleable protected int stripeIndent;

	private final boolean shared;
	private boolean defaults_initialized = false;
	private Map<String, Object> oldStyleValues;

	public static ComponentUI createUI( JComponent c ) {
		return FlatUIUtils.canUseSharedUI( c )
			? FlatUIUtils.createSharedUI( FlatSeparatorUI.class, () -> new FlatSeparatorUI( true ) )
			: new FlatSeparatorUI( false );
	}

	/** @since 2 */
	protected FlatSeparatorUI( boolean shared ) {
		this.shared = shared;
	}

	protected String getPropertyPrefix() {
		return "Separator";
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		installStyle( (JSeparator) c );
	}

	@Override
	protected void installDefaults( JSeparator s ) {
		super.installDefaults( s );

		if( !defaults_initialized ) {
			String prefix = getPropertyPrefix();
			height = UIManager.getInt( prefix + ".height" );
			stripeWidth = UIManager.getInt( prefix + ".stripeWidth" );
			stripeIndent = UIManager.getInt( prefix + ".stripeIndent" );

			defaults_initialized = true;
		}
	}

	@Override
	protected void uninstallDefaults( JSeparator s ) {
		super.uninstallDefaults( s );

		defaults_initialized = false;
		oldStyleValues = null;
	}

	@Override
	protected void installListeners( JSeparator s ) {
		super.installListeners( s );

		s.addPropertyChangeListener( this );
	}

	@Override
	protected void uninstallListeners( JSeparator s ) {
		super.uninstallListeners( s );

		s.removePropertyChangeListener( this );
	}

	/** @since 2.0.1 */
	@Override
	public void propertyChange( PropertyChangeEvent e ) {
		switch( e.getPropertyName() ) {
			case FlatClientProperties.STYLE:
			case FlatClientProperties.STYLE_CLASS:
				JSeparator s = (JSeparator) e.getSource();
				if( shared && FlatStylingSupport.hasStyleProperty( s ) ) {
					// unshare component UI if necessary
					// updateUI() invokes installStyle() from installUI()
					s.updateUI();
				} else
					installStyle( s );
				s.revalidate();
				HiDPIUtils.repaint( s );
				break;
		}
	}

	/** @since 2 */
	protected void installStyle( JSeparator s ) {
		try {
			applyStyle( s, FlatStylingSupport.getResolvedStyle( s, getStyleType() ) );
		} catch( RuntimeException ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	/** @since 2 */
	String getStyleType() {
		return "Separator";
	}

	/** @since 2 */
	protected void applyStyle( JSeparator s, Object style ) {
		oldStyleValues = FlatStylingSupport.parseAndApply( oldStyleValues, style,
			(key, value) -> applyStyleProperty( s, key, value ) );
	}

	/** @since 2 */
	protected Object applyStyleProperty( JSeparator s, String key, Object value ) {
		return FlatStylingSupport.applyToAnnotatedObjectOrComponent( this, s, key, value );
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
	public void paint( Graphics g, JComponent c ) {
		Graphics2D g2 = (Graphics2D) g.create();
		try {
			FlatUIUtils.setRenderingHints( g2 );
			g2.setColor( c.getForeground() );

			float width = scale( (float) stripeWidth );
			float indent = scale( (float) stripeIndent );

			if( ((JSeparator)c).getOrientation() == JSeparator.VERTICAL )
				g2.fill( new Rectangle2D.Float( indent, 0, width, c.getHeight() ) );
			else
				g2.fill( new Rectangle2D.Float( 0, indent, c.getWidth(), width ) );
		} finally {
			g2.dispose();
		}
	}

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		if( ((JSeparator) c).getOrientation() == JSeparator.VERTICAL )
			return new Dimension( scale( height ), 0 );
		else
			return new Dimension( 0, scale( height ) );
	}
}
