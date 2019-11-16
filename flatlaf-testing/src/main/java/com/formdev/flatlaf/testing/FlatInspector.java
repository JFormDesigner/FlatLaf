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

package com.formdev.flatlaf.testing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.lang.reflect.Field;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.JToolBar;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;
import com.formdev.flatlaf.ui.FlatToolTipUI;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;

/**
 * @author Karl Tauber
 */
public class FlatInspector
{
	private static final Integer HIGHLIGHT_LAYER = 401;
	private static final Integer TOOLTIP_LAYER = 402;

	private final JRootPane rootPane;

	private Component lastComponent;
	private int lastX;
	private int lastY;
	private boolean inspectParent;

	private JComponent highlightFigure;
	private JToolTip tip;

	public FlatInspector( JRootPane rootPane ) {
		this.rootPane = rootPane;

		rootPane.getGlassPane().addMouseMotionListener( new MouseMotionAdapter() {
			@Override
			public void mouseMoved( MouseEvent e ) {
				lastX = e.getX();
				lastY = e.getY();
				inspectParent = e.isShiftDown();
				inspect( lastX, lastY );
			}
		} );
	}

	public void setEnabled( boolean enabled ) {
		rootPane.getGlassPane().setVisible( enabled );

		if( !enabled ) {
			lastComponent = null;

			if( highlightFigure != null )
				highlightFigure.getParent().remove( highlightFigure );
			highlightFigure = null;

			if( tip != null )
				tip.getParent().remove( tip );
			tip = null;
		}
	}

	public void update() {
		if( !rootPane.getGlassPane().isVisible() )
			return;

		EventQueue.invokeLater( () -> {
			setEnabled( false );
			setEnabled( true );
			inspect( lastX, lastY );
		} );
	}

	private void inspect( int x, int y ) {
		Container contentPane = rootPane.getContentPane();
		Component c = SwingUtilities.getDeepestComponentAt( contentPane, x, y );
		if( inspectParent && c != null && c != contentPane )
			c = c.getParent();
		if( c == contentPane || (c != null && c.getParent() == contentPane) )
			c = null;

		if( c == lastComponent )
			return;

		lastComponent = c;

		highlight( c );
		showToolTip( c, x, y );
	}

	private void highlight( Component c ) {
		if( highlightFigure == null ) {
			highlightFigure = createHighlightFigure();
			rootPane.getLayeredPane().add( highlightFigure, HIGHLIGHT_LAYER );
		}

		highlightFigure.setVisible( c != null );

		if( c != null ) {
			Rectangle bounds = c.getBounds();
			Rectangle highlightBounds = SwingUtilities.convertRectangle( c.getParent(), bounds, rootPane );
			highlightFigure.setBounds( highlightBounds );
		}
	}

	private JComponent createHighlightFigure() {
		JComponent c = new JComponent() {
			@Override
			protected void paintComponent( Graphics g ) {
				g.setColor( getBackground() );
				g.fillRect( 0, 0, getWidth(), getHeight() );
			}

			@Override
			protected void paintBorder( Graphics g ) {
				FlatUIUtils.setRenderingHints( (Graphics2D) g );
				super.paintBorder( g );
			}
		};
		c.setBackground( new Color( 255, 0, 0, 32 ) );
		c.setBorder( new LineBorder( Color.red ) );
		return c;
	}

	private void showToolTip( Component c, int x, int y ) {
		if( c == null ) {
			if( tip != null )
				tip.setVisible( false );
			return;
		}

		if( tip == null ) {
			tip = new JToolTip() {
				@Override
				public void updateUI() {
					setUI( FlatToolTipUI.createUI( this ) );
				}
			};
			rootPane.getLayeredPane().add( tip, TOOLTIP_LAYER );
		} else
			tip.setVisible( true );

		tip.setTipText( buildToolTipText( c ) );

		int tx = x + UIScale.scale( 8 );
		int ty = y + UIScale.scale( 16 );
		Dimension size = tip.getPreferredSize();

		// position the tip in the visible area
		Rectangle visibleRect = rootPane.getVisibleRect();
		if( tx + size.width > visibleRect.x + visibleRect.width )
			tx = visibleRect.x + visibleRect.width - size.width;
		if( ty + size.height > visibleRect.y + visibleRect.height )
			ty = visibleRect.y + visibleRect.height - size.height;
		if( tx < visibleRect.x )
			tx = visibleRect.x;
		if( ty < visibleRect.y )
			ty = visibleRect.y;

		tip.setBounds( tx, ty, size.width, size.height );
		tip.repaint();
	}

	private String buildToolTipText( Component c ) {
		String name = c.getClass().getSimpleName();
		if( name.isEmpty() ) {
			// anonymous class
			name = c.getClass().getName();
			name = name.substring( name.lastIndexOf( '.' ) + 1 );
		}

		String text =
			"Class: " + name + " (" + c.getClass().getPackage().getName() + ")\n" +
			"Size: " + c.getWidth() + ',' + c.getHeight() + "  @ " + c.getX() + ',' + c.getY() + '\n';

		if( c instanceof Container )
			text += "Insets: " + toString( ((Container)c).getInsets() ) + '\n';

		Insets margin = null;
		if( c instanceof AbstractButton )
			margin = ((AbstractButton) c).getMargin();
		else if( c instanceof JTextComponent )
			margin = ((JTextComponent) c).getMargin();
		else if( c instanceof JMenuBar )
			margin = ((JMenuBar) c).getMargin();
		else if( c instanceof JToolBar )
			margin = ((JToolBar) c).getMargin();

		if( margin != null )
			text += "Margin: " + toString( margin ) + '\n';

		Dimension prefSize = c.getPreferredSize();
		Dimension minSize = c.getMinimumSize();
		Dimension maxSize = c.getMaximumSize();
		text += "Pref size: " + prefSize.width + ',' + prefSize.height + '\n' +
			"Min size: " + minSize.width + ',' + minSize.height + '\n' +
			"Max size: " + maxSize.width + ',' + maxSize.height + '\n';

		if( c instanceof JComponent )
			text += "Border: " + toString( ((JComponent)c).getBorder() ) + '\n';

		text += "Background: " + toString( c.getBackground() ) + '\n' +
			"Foreground: " + toString( c.getForeground() ) + '\n' +
			"Font: " + toString( c.getFont() ) + '\n';

		if( c instanceof JComponent ) {
			try {
				Field f = JComponent.class.getDeclaredField( "ui" );
				f.setAccessible( true );
				Object ui = f.get( c );
				text += "UI: " + (ui != null ? ui.getClass().getName() : "null") + '\n';
			} catch( NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex ) {
				// ignore
			}
		}

		text += "Enabled: " + c.isEnabled() + '\n';
		text += "Opaque: " + c.isOpaque() + (c instanceof JComponent &&
			FlatUIUtils.hasOpaqueBeenExplicitlySet( (JComponent) c ) ? " EXPLICIT" : "") + '\n';
		text += "Focusable: " + c.isFocusable() + '\n';
		text += "Left-to-right: " + c.getComponentOrientation().isLeftToRight() + '\n';
		text += "Parent: " + c.getParent().getClass().getName();

		return text;
	}

	private static String toString( Insets insets ) {
		if( insets == null )
			return "null";

		return insets.top + "," + insets.left + ',' + insets.bottom + ',' + insets.right;
	}

	private static String toString( Color c ) {
		if( c == null )
			return "null";

		String s = Long.toString( c.getRGB() & 0xffffffffl, 16 );
		if( c instanceof UIResource )
			s += " UI";
		return s;
	}

	private static String toString( Font f ) {
		if( f == null )
			return "null";

		return f.getFamily() + " " + f.getSize() + " " + f.getStyle()
			+ (f instanceof UIResource ? " UI" : "");
	}

	private static String toString( Border b ) {
		if( b == null )
			return "null";

		String s = b.getClass().getName();

		if( b instanceof EmptyBorder )
			s += '(' + toString( ((EmptyBorder)b).getBorderInsets() ) + ')';

		if( b instanceof UIResource )
			s += " UI";

		return s;
	}
}
