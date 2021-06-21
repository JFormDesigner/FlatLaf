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
import java.beans.PropertyChangeListener;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSeparatorUI;
import com.formdev.flatlaf.ui.FlatStyleSupport.Styleable;

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
{
	@Styleable protected int height;
	@Styleable protected int stripeWidth;
	@Styleable protected int stripeIndent;

	private final boolean shared;
	private boolean defaults_initialized = false;
	private PropertyChangeListener propertyChangeListener;
	private Map<String, Object> oldStyleValues;

	public static ComponentUI createUI( JComponent c ) {
		return FlatUIUtils.canUseSharedUI( c )
			? FlatUIUtils.createSharedUI( FlatSeparatorUI.class, () -> new FlatSeparatorUI( true ) )
			: new FlatSeparatorUI( false );
	}

	/**
	 * @since TODO
	 */
	protected FlatSeparatorUI( boolean shared ) {
		this.shared = shared;
	}

	protected String getPropertyPrefix() {
		return "Separator";
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		applyStyle( FlatStyleSupport.getStyle( c ) );
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

		propertyChangeListener = FlatStyleSupport.createPropertyChangeListener(
			s, style -> applyStyle( s, this, style ), null );
		s.addPropertyChangeListener( propertyChangeListener );
	}

	@Override
	protected void uninstallListeners( JSeparator s ) {
		super.uninstallListeners( s );

		s.removePropertyChangeListener( propertyChangeListener );
		propertyChangeListener = null;
	}

	private static void applyStyle( JSeparator s, FlatSeparatorUI ui, Object style ) {
		// unshare component UI if necessary
		if( style != null && ui.shared ) {
			s.updateUI();
			ui = (FlatSeparatorUI) s.getUI();
		}

		ui.applyStyle( style );
		s.revalidate();
		s.repaint();
	}

	/**
	 * @since TODO
	 */
	protected void applyStyle( Object style ) {
		oldStyleValues = FlatStyleSupport.parseAndApply( oldStyleValues, style, this::applyStyleProperty );
	}

	/**
	 * @since TODO
	 */
	protected Object applyStyleProperty( String key, Object value ) {
		return FlatStyleSupport.applyToAnnotatedObject( this, key, value );
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
