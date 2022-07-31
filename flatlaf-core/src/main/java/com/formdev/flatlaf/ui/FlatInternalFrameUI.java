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

import static com.formdev.flatlaf.util.UIScale.scale;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableBorder;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.LoggingFacade;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JInternalFrame}.
 *
 * <!-- BasicInternalFrameUI -->
 *
 * @uiDefault control											Color
 * @uiDefault InternalFrame.icon								Icon
 * @uiDefault InternalFrame.border								Border
 * @uiDefault InternalFrame.layoutTitlePaneAtOrigin				boolean
 *
 * <!-- BasicInternalFrameTitlePane -->
 *
 * @uiDefault InternalFrame.titleFont							Font
 * @uiDefault InternalFrame.icon								Icon
 * @uiDefault InternalFrame.maximizeIcon						Icon
 * @uiDefault InternalFrame.minimizeIcon						Icon
 * @uiDefault InternalFrame.iconifyIcon							Icon
 * @uiDefault InternalFrame.closeIcon							Icon
 * @uiDefault InternalFrame.activeTitleBackground				Color
 * @uiDefault InternalFrame.activeTitleForeground				Color
 * @uiDefault InternalFrame.inactiveTitleBackground				Color
 * @uiDefault InternalFrame.inactiveTitleForeground				Color
 * @uiDefault InternalFrame.closeButtonToolTip					String
 * @uiDefault InternalFrame.iconButtonToolTip					String
 * @uiDefault InternalFrame.restoreButtonToolTip				String
 * @uiDefault InternalFrame.maxButtonToolTip					String
 * @uiDefault InternalFrameTitlePane.closeButtonText			String
 * @uiDefault InternalFrameTitlePane.minimizeButtonText			String
 * @uiDefault InternalFrameTitlePane.restoreButtonText			String
 * @uiDefault InternalFrameTitlePane.maximizeButtonText			String
 * @uiDefault InternalFrameTitlePane.moveButtonText				String
 * @uiDefault InternalFrameTitlePane.sizeButtonText				String
 * @uiDefault InternalFrameTitlePane.closeButton.mnemonic		Integer
 * @uiDefault InternalFrameTitlePane.minimizeButton.mnemonic	Integer
 * @uiDefault InternalFrameTitlePane.restoreButton.mnemonic		Integer
 * @uiDefault InternalFrameTitlePane.maximizeButton.mnemonic	Integer
 * @uiDefault InternalFrameTitlePane.moveButton.mnemonic		Integer
 * @uiDefault InternalFrameTitlePane.sizeButton.mnemonic		Integer
 *
 * <!-- FlatInternalFrameUI -->
 *
 * @uiDefault InternalFrame.activeBorderColor					Color
 * @uiDefault InternalFrame.inactiveBorderColor					Color
 * @uiDefault InternalFrame.borderLineWidth						int
 * @uiDefault InternalFrame.borderMargins						Insets
 *
 * <!-- FlatInternalFrameTitlePane -->
 *
 * @uiDefault InternalFrameTitlePane.border						Border
 *
 * @author Karl Tauber
 */
public class FlatInternalFrameUI
	extends BasicInternalFrameUI
	implements StyleableUI
{
	protected FlatWindowResizer windowResizer;

	private Map<String, Object> oldStyleValues;
	private AtomicBoolean borderShared;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatInternalFrameUI( (JInternalFrame) c );
	}

	public FlatInternalFrameUI( JInternalFrame b ) {
		super( b );
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		LookAndFeel.installProperty( frame, "opaque", false );

		windowResizer = createWindowResizer();

		installStyle();
	}

	@Override
	public void uninstallUI( JComponent c ) {
		super.uninstallUI( c );

		if( windowResizer != null ) {
			windowResizer.uninstall();
			windowResizer = null;
		}

		oldStyleValues = null;
		borderShared = null;
	}

	@Override
	protected JComponent createNorthPane( JInternalFrame w ) {
		return new FlatInternalFrameTitlePane( w );
	}

	protected FlatWindowResizer createWindowResizer() {
		return new FlatWindowResizer.InternalFrameResizer( frame, this::getDesktopManager );
	}

	@Override
	protected MouseInputAdapter createBorderListener( JInternalFrame w ) {
		return new FlatBorderListener();
	}

	@Override
	protected PropertyChangeListener createPropertyChangeListener() {
		return FlatStylingSupport.createPropertyChangeListener( frame, this::installStyle,
			super.createPropertyChangeListener() );
	}

	/** @since 2 */
	protected void installStyle() {
		try {
			applyStyle( FlatStylingSupport.getResolvedStyle( frame, "InternalFrame" ) );
		} catch( RuntimeException ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	/** @since 2 */
	protected void applyStyle( Object style ) {
		oldStyleValues = FlatStylingSupport.parseAndApply( oldStyleValues, style, this::applyStyleProperty );
	}

	/** @since 2 */
	protected Object applyStyleProperty( String key, Object value ) {
		if( borderShared == null )
			borderShared = new AtomicBoolean( true );
		return FlatStylingSupport.applyToAnnotatedObjectOrBorder( this, key, value, frame, borderShared );
	}

	/** @since 2 */
	@Override
	public Map<String, Class<?>> getStyleableInfos( JComponent c ) {
		return FlatStylingSupport.getAnnotatedStyleableInfos( this, frame.getBorder() );
	}

	/** @since 2.5 */
	@Override
	public Object getStyleableValue( JComponent c, String key ) {
		return FlatStylingSupport.getAnnotatedStyleableValue( this, frame.getBorder(), key );
	}

	@Override
	public void update( Graphics g, JComponent c ) {
		// The internal frame actually should be opaque and fill its background,
		// but it must be non-opaque to allow translucent resize handles (outside of visual bounds).
		// To avoid that parent may shine through internal frame (e.g. if menu bar is non-opaque),
		// fill background excluding insets (translucent resize handles),
		// but only if opaque was not set explicitly by application to false.
		// If applications has set internal frame opacity to false, do not fill background (for compatibility).
		if( !c.isOpaque() && !FlatUIUtils.hasOpaqueBeenExplicitlySet( c ) ) {
			Insets insets = c.getInsets();

			g.setColor( c.getBackground() );
			g.fillRect( insets.left, insets.top,
				c.getWidth() - insets.left - insets.right,
				c.getHeight() - insets.top - insets.bottom );
		}

		super.update( g, c );
	}

	//---- class FlatInternalFrameBorder --------------------------------------

	public static class FlatInternalFrameBorder
		extends FlatEmptyBorder
		implements StyleableBorder
	{
		@Styleable protected Color activeBorderColor = UIManager.getColor( "InternalFrame.activeBorderColor" );
		@Styleable protected Color inactiveBorderColor = UIManager.getColor( "InternalFrame.inactiveBorderColor" );
		@Styleable protected int borderLineWidth = FlatUIUtils.getUIInt( "InternalFrame.borderLineWidth", 1 );
		@Styleable protected boolean dropShadowPainted = UIManager.getBoolean( "InternalFrame.dropShadowPainted" );

		private final FlatDropShadowBorder activeDropShadowBorder = new FlatDropShadowBorder(
			UIManager.getColor( "InternalFrame.activeDropShadowColor" ),
			UIManager.getInsets( "InternalFrame.activeDropShadowInsets" ),
			FlatUIUtils.getUIFloat( "InternalFrame.activeDropShadowOpacity", 0.5f ) );
		private final FlatDropShadowBorder inactiveDropShadowBorder = new FlatDropShadowBorder(
			UIManager.getColor( "InternalFrame.inactiveDropShadowColor" ),
			UIManager.getInsets( "InternalFrame.inactiveDropShadowInsets" ),
			FlatUIUtils.getUIFloat( "InternalFrame.inactiveDropShadowOpacity", 0.5f ) );

		public FlatInternalFrameBorder() {
			super( UIManager.getInsets( "InternalFrame.borderMargins" ) );
		}

		@Override
		public Object applyStyleProperty( String key, Object value ) {
			switch( key ) {
				case "borderMargins": return applyStyleProperty( (Insets) value );

				case "activeDropShadowColor": return activeDropShadowBorder.applyStyleProperty( "shadowColor", value );
				case "activeDropShadowInsets": return activeDropShadowBorder.applyStyleProperty( "shadowInsets", value );
				case "activeDropShadowOpacity": return activeDropShadowBorder.applyStyleProperty( "shadowOpacity", value );
				case "inactiveDropShadowColor": return inactiveDropShadowBorder.applyStyleProperty( "shadowColor", value );
				case "inactiveDropShadowInsets": return inactiveDropShadowBorder.applyStyleProperty( "shadowInsets", value );
				case "inactiveDropShadowOpacity": return inactiveDropShadowBorder.applyStyleProperty( "shadowOpacity", value );
			}

			return FlatStylingSupport.applyToAnnotatedObject( this, key, value );
		}

		@Override
		public Map<String, Class<?>> getStyleableInfos() {
			Map<String, Class<?>> infos = new FlatStylingSupport.StyleableInfosMap<>();
			FlatStylingSupport.collectAnnotatedStyleableInfos( this, infos );
			infos.put( "borderMargins", Insets.class );
			infos.put( "activeDropShadowColor", Color.class );
			infos.put( "activeDropShadowInsets", Insets.class );
			infos.put( "activeDropShadowOpacity", float.class );
			infos.put( "inactiveDropShadowColor", Color.class );
			infos.put( "inactiveDropShadowInsets", Insets.class );
			infos.put( "inactiveDropShadowOpacity", float.class );
			return infos;
		}

		/** @since 2.5 */
		@Override
		public Object getStyleableValue( String key ) {
			switch( key ) {
				case "borderMargins": return getStyleableValue();

				case "activeDropShadowColor": return activeDropShadowBorder.getStyleableValue( "shadowColor" );
				case "activeDropShadowInsets": return activeDropShadowBorder.getStyleableValue( "shadowInsets" );
				case "activeDropShadowOpacity": return activeDropShadowBorder.getStyleableValue( "shadowOpacity" );
				case "inactiveDropShadowColor": return inactiveDropShadowBorder.getStyleableValue( "shadowColor" );
				case "inactiveDropShadowInsets": return inactiveDropShadowBorder.getStyleableValue( "shadowInsets" );
				case "inactiveDropShadowOpacity": return inactiveDropShadowBorder.getStyleableValue( "shadowOpacity" );
			}

			return FlatStylingSupport.getAnnotatedStyleableValue( this, key );
		}

		@Override
		public Insets getBorderInsets( Component c, Insets insets ) {
			if( c instanceof JInternalFrame && ((JInternalFrame)c).isMaximum() ) {
				insets.left = scale( Math.min( borderLineWidth, left ) );
				insets.top = scale( Math.min( borderLineWidth, top ) );
				insets.right = scale( Math.min( borderLineWidth, right ) );
				insets.bottom = scale( Math.min( borderLineWidth, bottom ) );
				return insets;
			}

			return super.getBorderInsets( c, insets );
		}

		@Override
		public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
			JInternalFrame f = (JInternalFrame) c;

			Insets insets = getBorderInsets( c );
			float lineWidth = scale( (float) borderLineWidth );

			float rx = x + insets.left - lineWidth;
			float ry = y + insets.top - lineWidth;
			float rwidth = width - insets.left - insets.right + (lineWidth * 2);
			float rheight = height - insets.top - insets.bottom + (lineWidth * 2);

			Graphics2D g2 = (Graphics2D) g.create();
			try {
				FlatUIUtils.setRenderingHints( g2 );
				g2.setColor( f.isSelected() ? activeBorderColor : inactiveBorderColor );

				// paint drop shadow
				if( dropShadowPainted ) {
					FlatDropShadowBorder dropShadowBorder = f.isSelected()
						? activeDropShadowBorder : inactiveDropShadowBorder;

					Insets dropShadowInsets = dropShadowBorder.getBorderInsets();
					dropShadowBorder.paintBorder( c, g2,
						(int) rx - dropShadowInsets.left,
						(int) ry - dropShadowInsets.top,
						(int) rwidth + dropShadowInsets.left + dropShadowInsets.right,
						(int) rheight + dropShadowInsets.top + dropShadowInsets.bottom );
				}

				// paint border
				g2.fill( FlatUIUtils.createRectangle( rx, ry, rwidth, rheight, lineWidth ) );
			} finally {
				g2.dispose();
			}
		}
	}

	//---- class FlatBorderListener -------------------------------------------

	/** @since 1.6 */
	protected class FlatBorderListener
		extends BorderListener
	{
		@Override
		public void mouseClicked( MouseEvent e ) {
			if( e.getClickCount() == 2 && !frame.isIcon() &&
				e.getSource() instanceof FlatInternalFrameTitlePane )
			{
				Rectangle iconBounds = ((FlatInternalFrameTitlePane)e.getSource()).getFrameIconBounds();
				if( iconBounds != null && iconBounds.contains( e.getX(), e.getY() ) ) {
					if( frame.isClosable() )
						frame.doDefaultCloseAction();
					return;
				}
			}

			super.mouseClicked( e );
		}
	}
}
