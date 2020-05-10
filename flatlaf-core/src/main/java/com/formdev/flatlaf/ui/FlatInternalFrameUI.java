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
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicInternalFrameUI;

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
{
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
	}

	@Override
	protected JComponent createNorthPane( JInternalFrame w ) {
		return new FlatInternalFrameTitlePane( w );
	}

	//---- class FlatInternalFrameBorder --------------------------------------

	public static class FlatInternalFrameBorder
		extends FlatEmptyBorder
	{
		private final Color activeBorderColor = UIManager.getColor( "InternalFrame.activeBorderColor" );
		private final Color inactiveBorderColor = UIManager.getColor( "InternalFrame.inactiveBorderColor" );
		private final int borderLineWidth = FlatUIUtils.getUIInt( "InternalFrame.borderLineWidth", 1 );
		private final boolean dropShadowPainted = UIManager.getBoolean( "InternalFrame.dropShadowPainted" );

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
}
