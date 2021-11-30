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
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.util.Map;
import java.util.Objects;
import javax.swing.AbstractButton;
import javax.swing.CellRendererPane;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicRadioButtonUI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.icons.FlatCheckBoxIcon;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.ui.FlatStylingSupport.UnknownStyleException;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JRadioButton}.
 *
 * <!-- BasicRadioButtonUI -->
 *
 * @uiDefault RadioButton.font						Font
 * @uiDefault RadioButton.background				Color
 * @uiDefault RadioButton.foreground				Color
 * @uiDefault RadioButton.border					Border
 * @uiDefault RadioButton.margin					Insets
 * @uiDefault RadioButton.rollover					boolean
 * @uiDefault RadioButton.icon						Icon
 *
 * <!-- FlatRadioButtonUI -->
 *
 * @uiDefault RadioButton.iconTextGap				int
 * @uiDefault RadioButton.disabledText				Color
 *
 * @author Karl Tauber
 */
public class FlatRadioButtonUI
	extends BasicRadioButtonUI
	implements StyleableUI
{
	protected int iconTextGap;
	@Styleable protected Color disabledText;

	private Color defaultBackground;

	private final boolean shared;
	private boolean iconShared = true;
	private boolean defaults_initialized = false;
	private Map<String, Object> oldStyleValues;

	public static ComponentUI createUI( JComponent c ) {
		return FlatUIUtils.canUseSharedUI( c )
			? FlatUIUtils.createSharedUI( FlatRadioButtonUI.class, () -> new FlatRadioButtonUI( true ) )
			: new FlatRadioButtonUI( false );
	}

	/** @since 2 */
	protected FlatRadioButtonUI( boolean shared ) {
		this.shared = shared;
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		installStyle( (AbstractButton) c );
	}

	@Override
	public void installDefaults( AbstractButton b ) {
		super.installDefaults( b );

		if( !defaults_initialized ) {
			String prefix = getPropertyPrefix();

			iconTextGap = FlatUIUtils.getUIInt( prefix + "iconTextGap", 4 );
			disabledText = UIManager.getColor( prefix + "disabledText" );

			defaultBackground = UIManager.getColor( prefix + "background" );

			iconShared = true;
			defaults_initialized = true;
		}

		LookAndFeel.installProperty( b, "opaque", false );
		LookAndFeel.installProperty( b, "iconTextGap", scale( iconTextGap ) );

		MigLayoutVisualPadding.install( b, null );
	}

	@Override
	protected void uninstallDefaults( AbstractButton b ) {
		super.uninstallDefaults( b );

		oldStyleValues = null;

		MigLayoutVisualPadding.uninstall( b );
		defaults_initialized = false;
	}

	@Override
	protected BasicButtonListener createButtonListener( AbstractButton b ) {
		return new FlatRadioButtonListener( b );
	}

	/** @since 2 */
	protected void propertyChange( AbstractButton b, PropertyChangeEvent e ) {
		switch( e.getPropertyName() ) {
			case FlatClientProperties.STYLE:
			case FlatClientProperties.STYLE_CLASS:
				if( shared && FlatStylingSupport.hasStyleProperty( b ) ) {
					// unshare component UI if necessary
					// updateUI() invokes installStyle() from installUI()
					b.updateUI();
				} else
					installStyle( b );
				b.revalidate();
				b.repaint();
				break;
		}
	}

	/** @since 2 */
	protected void installStyle( AbstractButton b ) {
		try {
			applyStyle( b, FlatStylingSupport.getResolvedStyle( b, getStyleType() ) );
		} catch( RuntimeException ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	/** @since 2 */
	String getStyleType() {
		return "RadioButton";
	}

	/** @since 2 */
	protected void applyStyle( AbstractButton b, Object style ) {
		oldStyleValues = FlatStylingSupport.parseAndApply( oldStyleValues, style,
			(key, value) -> applyStyleProperty( b, key, value ) );
	}

	/** @since 2 */
	protected Object applyStyleProperty( AbstractButton b, String key, Object value ) {
		// style icon
		if( key.startsWith( "icon." ) ) {
			if( !(icon instanceof FlatCheckBoxIcon) )
				return new UnknownStyleException( key );

			if( iconShared ) {
				icon = FlatStylingSupport.cloneIcon( icon );
				iconShared = false;
			}

			key = key.substring( "icon.".length() );
			return ((FlatCheckBoxIcon)icon).applyStyleProperty( key, value );
		}

		return FlatStylingSupport.applyToAnnotatedObjectOrComponent( this, b, key, value );
	}

	/** @since 2 */
	@Override
	public Map<String, Class<?>> getStyleableInfos( JComponent c ) {
		Map<String, Class<?>> infos = FlatStylingSupport.getAnnotatedStyleableInfos( this );
		if( icon instanceof FlatCheckBoxIcon ) {
			for( Map.Entry<String, Class<?>> e : ((FlatCheckBoxIcon)icon).getStyleableInfos().entrySet() )
				infos.put( "icon.".concat( e.getKey() ), e.getValue() );
		}
		return infos;
	}

	private static Insets tempInsets = new Insets( 0, 0, 0, 0 );

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		Dimension size = super.getPreferredSize( c );
		if( size == null )
			return null;

		// small insets fix
		int focusWidth = getIconFocusWidth( c );
		if( focusWidth > 0 ) {
			// Increase preferred width and height if insets were explicitly reduced (e.g. with
			// an EmptyBorder) and icon has a focus width, which is not included in icon size.
			// Otherwise the component may be too small and outer focus border may be cut off.
			Insets insets = c.getInsets( tempInsets );
			size.width += Math.max( focusWidth - insets.left, 0 ) + Math.max( focusWidth - insets.right, 0 );
			size.height += Math.max( focusWidth - insets.top, 0 ) + Math.max( focusWidth - insets.bottom, 0 );
		}

		return size;
	}

	@Override
	public void paint( Graphics g, JComponent c ) {
		// fill background even if not opaque if
		// - contentAreaFilled is true and
		// - if background color is different to default background color
		// (this paints selection if using the component as cell renderer)
		if( !c.isOpaque() &&
			((AbstractButton)c).isContentAreaFilled() &&
			!Objects.equals( c.getBackground(), getDefaultBackground( c ) ) )
		{
			g.setColor( c.getBackground() );
			g.fillRect( 0, 0, c.getWidth(), c.getHeight() );
		}

		// small insets fix
		int focusWidth = getIconFocusWidth( c );
		if( focusWidth > 0 ) {
			boolean ltr = c.getComponentOrientation().isLeftToRight();
			Insets insets = c.getInsets( tempInsets );
			int leftOrRightInset = ltr ? insets.left : insets.right;
			if( focusWidth > leftOrRightInset ) {
				// The left (or right) inset is smaller than the focus width, which may be
				// the case if insets were explicitly reduced (e.g. with an EmptyBorder).
				// In this case the width has been increased in getPreferredSize() and
				// here it is necessary to fix icon and text painting location.
				int offset = focusWidth - leftOrRightInset;
				if( !ltr )
					offset = -offset;

				// move the graphics origin to the left (or right)
				g.translate( offset, 0 );
				super.paint( g, c );
				g.translate( -offset, 0 );
				return;
			}
		}

		super.paint( FlatLabelUI.createGraphicsHTMLTextYCorrection( g, c ), c );
	}

	@Override
	protected void paintText( Graphics g, AbstractButton b, Rectangle textRect, String text ) {
		FlatButtonUI.paintText( g, b, textRect, text, b.isEnabled() ? b.getForeground() : disabledText );
	}

	/**
	 * Returns the default background color of the component.
	 * If the component is used as cell renderer (e.g. in JTable),
	 * then the background color of the renderer container is returned.
	 */
	private Color getDefaultBackground( JComponent c ) {
		Container parent = c.getParent();
		return (parent instanceof CellRendererPane && parent.getParent() != null)
			? parent.getParent().getBackground()
			: defaultBackground;
	}

	private int getIconFocusWidth( JComponent c ) {
		AbstractButton b = (AbstractButton) c;
		Icon icon = b.getIcon();
		if( icon == null )
			icon = getDefaultIcon();

		return (icon instanceof FlatCheckBoxIcon)
			? Math.round( UIScale.scale( ((FlatCheckBoxIcon)icon).getFocusWidth() ) )
			: 0;
	}

	//---- class FlatRadioButtonListener --------------------------------------

	/** @since 2 */
	protected class FlatRadioButtonListener
		extends BasicButtonListener
	{
		private final AbstractButton b;

		protected FlatRadioButtonListener( AbstractButton b ) {
			super( b );
			this.b = b;
		}

		@Override
		public void propertyChange( PropertyChangeEvent e ) {
			super.propertyChange( e );
			FlatRadioButtonUI.this.propertyChange( b, e );
		}
	}
}
