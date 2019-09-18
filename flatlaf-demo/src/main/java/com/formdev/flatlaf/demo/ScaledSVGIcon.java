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

package com.formdev.flatlaf.demo;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import com.kitfox.svg.app.beans.SVGIcon;

/**
 * @author Karl Tauber
 */
public class ScaledSVGIcon
	extends ImageIcon
{
	private final String name;
	private final SVGIcon svgIcon;
	private boolean dark;

	public ScaledSVGIcon( String name ) {
		this.name = name;

		svgIcon = new SVGIcon();
		svgIcon.setAntiAlias( true );
	}

	private void update() {
		if( dark == isDarkLaf() && svgIcon.getSvgURI() != null )
			return;

		dark = isDarkLaf();
		URL url = getIconURL( name, dark );
		if( url == null & dark )
			url = getIconURL( name, false );

		try {
			svgIcon.setSvgURI( url.toURI() );
		} catch( URISyntaxException ex ) {
			ex.printStackTrace();
		}
	}

	private URL getIconURL( String name, boolean dark ) {
		if( dark ) {
			int dotIndex = name.lastIndexOf( '.' );
			name = name.substring( 0, dotIndex ) + "_dark" + name.substring( dotIndex );
		}
		return getClass().getResource( name );
	}

	@Override
	public int getIconWidth() {
		update();
		return UIScale.scale( svgIcon.getIconWidth() );
	}

	@Override
	public int getIconHeight() {
		update();
		return UIScale.scale( svgIcon.getIconHeight() );
	}

	@Override
	public void paintIcon( Component c, Graphics g, int x, int y ) {
		update();

		Graphics2D g2 = (Graphics2D) g.create();
		try {
			FlatUIUtils.setRenderingHints( g2 );

			g2.translate( x, y );
			UIScale.scaleGraphics( g2 );

			svgIcon.paintIcon( c, g2, 0, 0 );
		} finally {
			g2.dispose();
		}
	}

	@Override
	public Image getImage() {
		update();

		BufferedImage image = new BufferedImage( getIconWidth(), getIconHeight(), BufferedImage.TYPE_INT_ARGB );
		Graphics2D g = image.createGraphics();
		try {
			paintIcon( null, g, 0, 0 );
		} finally {
			g.dispose();
		}
		return image;
	}

	private static Boolean darkLaf;

	private static boolean isDarkLaf() {
		if( darkLaf == null ) {
			lafChanged();

			UIManager.addPropertyChangeListener( e -> {
				lafChanged();
			} );
		}

		return darkLaf;
	}

	private static void lafChanged() {
		darkLaf = (UIManager.getLookAndFeel() instanceof FlatDarkLaf);
	}
}
