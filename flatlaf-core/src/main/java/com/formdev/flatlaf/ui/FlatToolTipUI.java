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

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicToolTipUI;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.StringUtils;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JToolTip}.
 *
 * <!-- BasicToolTipUI -->
 *
 * @uiDefault ToolTip.font						Font
 * @uiDefault ToolTip.background				Color
 * @uiDefault ToolTip.foreground				Color
 * @uiDefault ToolTip.backgroundInactive		Color
 * @uiDefault ToolTip.foregroundInactive		Color
 * @uiDefault ToolTip.border					Border
 * @uiDefault ToolTip.borderInactive			Border
 *
 * @author Karl Tauber
 */
public class FlatToolTipUI
	extends BasicToolTipUI
	implements PropertyChangeListener
{
	public static ComponentUI createUI( JComponent c ) {
		return FlatUIUtils.createSharedUI( FlatToolTipUI.class, FlatToolTipUI::new );
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		// update HTML renderer if necessary
		FlatLabelUI.updateHTMLRenderer( c, ((JToolTip)c).getTipText(), false );
	}

	@Override
	protected void installListeners( JComponent c ) {
		super.installListeners( c );

		c.addPropertyChangeListener( this );
	}

	@Override
	protected void uninstallListeners( JComponent c ) {
		super.uninstallListeners( c );

		c.removePropertyChangeListener( this );
	}

	/** @since 2.0.1 */
	@Override
	public void propertyChange( PropertyChangeEvent e ) {
		String name = e.getPropertyName();
		if( name == "tiptext" || name == "font" || name == "foreground" ) {
			JToolTip toolTip = (JToolTip) e.getSource();
			FlatLabelUI.updateHTMLRenderer( toolTip, toolTip.getTipText(), false );
		}
	}

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		// do not show tool tip if text is empty
		String text = ((JToolTip)c).getTipText();
		if( text == null || text.isEmpty() )
			return new Dimension();

		if( isMultiLine( c ) ) {
			FontMetrics fm = c.getFontMetrics( c.getFont() );
			Insets insets = c.getInsets();

			List<String> lines = StringUtils.split( ((JToolTip)c).getTipText(), '\n' );
			int width = 0;
			int height = fm.getHeight() * Math.max( lines.size(), 1 );
			for( String line : lines )
				width = Math.max( width, SwingUtilities.computeStringWidth( fm, line ) );

			return new Dimension( insets.left + width + insets.right + 6, insets.top + height + insets.bottom );
		} else
			return super.getPreferredSize( c );
	}

	@Override
	public void paint( Graphics g, JComponent c ) {
		if( isMultiLine( c ) ) {
			FontMetrics fm = c.getFontMetrics( c.getFont() );
			Insets insets = c.getInsets();

			g.setColor( c.getForeground() );

			List<String> lines = StringUtils.split( ((JToolTip)c).getTipText(), '\n' );

			int x = insets.left + 3;
			int x2 = c.getWidth() - insets.right - 3;
			int y = insets.top - fm.getDescent();
			int lineHeight = fm.getHeight();
			JComponent comp = ((JToolTip)c).getComponent();
			boolean leftToRight = (comp != null ? comp : c).getComponentOrientation().isLeftToRight();
			for( String line : lines ) {
				y += lineHeight;
				FlatUIUtils.drawString( c, g, line, leftToRight ? x : x2 - SwingUtilities.computeStringWidth( fm, line ), y );
			}
		} else
			super.paint( HiDPIUtils.createGraphicsTextYCorrection( (Graphics2D) g ), c );
	}

	private boolean isMultiLine( JComponent c ) {
		String text = ((JToolTip)c).getTipText();
		return c.getClientProperty( BasicHTML.propertyKey ) == null && text != null && text.indexOf( '\n' ) >= 0;
	}
}
