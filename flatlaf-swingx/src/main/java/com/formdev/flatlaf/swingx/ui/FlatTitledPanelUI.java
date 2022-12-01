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

package com.formdev.flatlaf.swingx.ui;

import static com.formdev.flatlaf.util.UIScale.scale;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.SwingXUtilities;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.plaf.PainterUIResource;
import org.jdesktop.swingx.plaf.basic.BasicTitledPanelUI;
import com.formdev.flatlaf.ui.FlatLineBorder;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link org.jdesktop.swingx.JXTitledPanel}.
 *
 * @author Karl Tauber
 */
public class FlatTitledPanelUI
	extends BasicTitledPanelUI
{
	public static ComponentUI createUI( JComponent c ) {
		return new FlatTitledPanelUI();
	}

	@Override
	protected void installDefaults( JXTitledPanel titledPanel ) {
		super.installDefaults( titledPanel );

		// replace gradient background
		installProperty( titledPanel, "titlePainter", new PainterUIResource<>(
			new TitlePainter( UIManager.getColor( "JXTitledPanel.titleBackground" ),
				UIManager.getColor( "JXTitledPanel.borderColor" ) ) ) );
	}

	@Override
	protected void installComponents( JXTitledPanel titledPanel ) {
		super.installComponents( titledPanel );

		if (SwingXUtilities.isUIInstallable(titledPanel.getBorder()))
			titledPanel.setBorder( new FlatLineBorder( new Insets( 1, 1, 1, 1 ),
				UIManager.getColor( "JXTitledPanel.borderColor" ) ) );
	}

	@Override
	protected Insets getCaptionInsets() {
		return UIScale.scale( super.getCaptionInsets() );
	}

	//---- class TitlePainter -------------------------------------------------

	private static class TitlePainter
		extends MattePainter
	{
		private final Color borderColor;

		TitlePainter( Color color, Color borderColor ) {
			super( color );
			this.borderColor = borderColor;
		}

		@Override
		protected void doPaint( Graphics2D g, Object component, int width, int height ) {
			super.doPaint( g, component, width, height );

			float h = scale( 1f );
			g.setColor( borderColor );
			g.fill( new Rectangle2D.Float( 0, height - h, width, h ) );
		}
	}
}
