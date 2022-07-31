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

package com.formdev.flatlaf.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JPanel}.
 *
 * <!-- BasicPanelUI -->
 *
 * @uiDefault Panel.font				Font	unused
 * @uiDefault Panel.background			Color	only used if opaque
 * @uiDefault Panel.foreground			Color	unused
 * @uiDefault Panel.border				Border
 *
 * @author Karl Tauber
 */
public class FlatPanelUI
	extends BasicPanelUI
	implements StyleableUI, PropertyChangeListener
{
	// only used via styling (not in UI defaults)
	/** @since 2 */ @Styleable protected int arc = -1;

	private final boolean shared;
	private Map<String, Object> oldStyleValues;

	public static ComponentUI createUI( JComponent c ) {
		return FlatUIUtils.canUseSharedUI( c )
			? FlatUIUtils.createSharedUI( FlatPanelUI.class, () -> new FlatPanelUI( true ) )
			: new FlatPanelUI( false );
	}

	/** @since 2 */
	protected FlatPanelUI( boolean shared ) {
		this.shared = shared;
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		c.addPropertyChangeListener( this );

		installStyle( (JPanel) c );
	}

	@Override
	public void uninstallUI( JComponent c ) {
		super.uninstallUI( c );

		c.removePropertyChangeListener( this );

		oldStyleValues = null;
	}

	/** @since 2.0.1 */
	@Override
	public void propertyChange( PropertyChangeEvent e ) {
		switch( e.getPropertyName() ) {
			case FlatClientProperties.STYLE:
			case FlatClientProperties.STYLE_CLASS:
				JPanel c = (JPanel) e.getSource();
				if( shared && FlatStylingSupport.hasStyleProperty( c ) ) {
					// unshare component UI if necessary
					// updateUI() invokes installStyle() from installUI()
					c.updateUI();
				} else
					installStyle( c );
				c.revalidate();
				c.repaint();
				break;
		}
	}

	/** @since 2 */
	protected void installStyle( JPanel c ) {
		try {
			applyStyle( c, FlatStylingSupport.getResolvedStyle( c, "Panel" ) );
		} catch( RuntimeException ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	/** @since 2 */
	protected void applyStyle( JPanel c, Object style ) {
		oldStyleValues = FlatStylingSupport.parseAndApply( oldStyleValues, style,
			(key, value) -> applyStyleProperty( c, key, value ) );
	}

	/** @since 2 */
	protected Object applyStyleProperty( JPanel c, String key, Object value ) {
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
		// fill background
		if( c.isOpaque() ) {
			int width = c.getWidth();
			int height = c.getHeight();
			int arc = (this.arc >= 0)
				? this.arc
				: ((c.getBorder() instanceof FlatLineBorder)
					? ((FlatLineBorder)c.getBorder()).getArc()
					: 0);

			// fill background with parent color to avoid garbage in rounded corners
			if( arc > 0 )
				FlatUIUtils.paintParentBackground( g, c );

			g.setColor( c.getBackground() );
			if( arc > 0 ) {
				// fill rounded rectangle if having rounded corners
				Object[] oldRenderingHints = FlatUIUtils.setRenderingHints( g );
				FlatUIUtils.paintComponentBackground( (Graphics2D) g, 0, 0, width, height,
					0, UIScale.scale( arc ) );
				FlatUIUtils.resetRenderingHints( g, oldRenderingHints );
			} else
				g.fillRect( 0, 0, width, height );
		}

		paint( g, c );
	}
}
