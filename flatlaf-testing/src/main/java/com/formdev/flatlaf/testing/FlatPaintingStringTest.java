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

package com.formdev.flatlaf.testing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.AffineTransform;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.formdev.flatlaf.FlatSystemProperties;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.Graphics2DProxy;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.JavaCompatibility;
import com.formdev.flatlaf.util.SystemInfo;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatPaintingStringTest
	extends JPanel
{
	public static void main( String[] args ) {
		System.setProperty( FlatSystemProperties.UI_SCALE, "1x" );
		System.setProperty( "sun.java2d.uiScale", "1x" );

		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatPaintingStringTest" );

			ToolTipManager.sharedInstance().setInitialDelay( 0 );
			ToolTipManager.sharedInstance().setDismissDelay( 10000 );

			frame.showFrame( FlatPaintingStringTest::new );
		} );
	}

	FlatPaintingStringTest() {
		initComponents();

		add( new JLabel() );
		add( new JLabel( "none" ) );
		add( new JLabel( "flatlaf" ) );
		add( new JLabel( "0.25*scale" ) );
		add( new JLabel( "0.5*scale" ) );
		if( SystemInfo.isJava_9_orLater ) {
			add( new JLabel( "0.25" ) );
			add( new JLabel( "0.5" ) );
			add( new JLabel( "0.625" ) );
			add( new JLabel( "0.75" ) );
			add( new JLabel( "0.875" ) );
		} else {
			add( new JLabel( "0.625*scale" ) );
			add( new JLabel( "0.75*scale" ) );
			add( new JLabel( "0.875*scale" ) );
		}

		YCorrectionFunction none = (g, scaleFactor) -> 0;
		YCorrectionFunction flatlaf = (g, scaleFactor) -> {
			return SystemInfo.isJava_9_orLater
				? HiDPIUtils.computeTextYCorrection( g )
				: (scaleFactor > 1 ? -(0.625f * scaleFactor) : 0);
		};
		YCorrectionFunction oneQSysScale = (g, scaleFactor) -> -(0.25f * scaleFactor);
		YCorrectionFunction halfSysScale = (g, scaleFactor) -> -(0.5f * scaleFactor);
		YCorrectionFunction fiveEightsQSysScale = (g, scaleFactor) -> -(0.625f * scaleFactor);
		YCorrectionFunction threeQSysScale = (g, scaleFactor) -> -(0.75f * scaleFactor);
		YCorrectionFunction sevenEightsSysScale = (g, scaleFactor) -> -(0.875f * scaleFactor);
		YCorrectionFunction oneQ = (g, scaleFactor) -> -0.25f;
		YCorrectionFunction half = (g, scaleFactor) -> -0.5f;
		YCorrectionFunction fiveEights = (g, scaleFactor) -> -0.625f;
		YCorrectionFunction threeQ = (g, scaleFactor) -> -0.75f;
		YCorrectionFunction sevenEights = (g, scaleFactor) -> -0.875f;

		float[] scaleFactors = new float[] { 1f, 1.25f, 1.5f, 1.75f, 2f, 2.25f, 2.5f, 3f, 3.5f, 4f };

		for( float scaleFactor : scaleFactors ) {
			add( new JLabel( String.valueOf( scaleFactor ) ), "newLine" );

			add( scaleFactor, none );
			add( scaleFactor, flatlaf );
			add( scaleFactor, oneQSysScale );
			add( scaleFactor, halfSysScale );
			if( SystemInfo.isJava_9_orLater ) {
				add( scaleFactor, oneQ );
				add( scaleFactor, half );
				add( scaleFactor, fiveEights );
				add( scaleFactor, threeQ );
				add( scaleFactor, sevenEights );
			} else {
				add( scaleFactor, fiveEightsQSysScale );
				add( scaleFactor, threeQSysScale );
				add( scaleFactor, sevenEightsSysScale );
			}
		}
	}

	private void add( float scaleFactor, YCorrectionFunction correctionFunction ) {
		if( SystemInfo.isJava_9_orLater ) {
			add( new Painter( scaleFactor, correctionFunction, 0 ), "split 4, gapx 0 0" );
			add( new Painter( scaleFactor, correctionFunction, 0.25f ), "gapx 0 0" );
			add( new Painter( scaleFactor, correctionFunction, 0.5f ), "gapx 0 0" );
			add( new Painter( scaleFactor, correctionFunction, 0.75f ), "gapx 0 0" );
		} else
			add( new Painter( scaleFactor, correctionFunction, 0 ) );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents

		//======== this ========
		setBorder(null);
		setLayout(new MigLayout(
			"insets dialog,hidemode 3",
			// columns
			"[fill]",
			// rows
			"[top]"));
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	private interface YCorrectionFunction {
		float computeTextYCorrection( Graphics2D g, float scaleFactor );
	}

	//---- class Painter ------------------------------------------------------

	public static class Painter
		extends JLabel
	{
		private final float scaleFactor;
		private final YCorrectionFunction correctionFunction;
		private final float yOffset;

		public Painter( float scaleFactor, YCorrectionFunction correctionFunction, float yOffset ) {
			super( "E" );
			this.scaleFactor = scaleFactor;
			this.correctionFunction = correctionFunction;
			this.yOffset = yOffset;
			setBorder( new EmptyBorder( 2, 0, 2, 0 ) );

			if( !SystemInfo.isJava_9_orLater ) {
				Font font = getFont();
				setFont( font.deriveFont( (float) Math.round( font.getSize() * scaleFactor ) ) );
			}
		}

		@Override
		public Dimension getPreferredSize() {
			Dimension size = super.getPreferredSize();
			Insets insets = getInsets();
			int leftRight = insets.left + insets.right;
			return new Dimension(
				scale( size.width -leftRight ) + leftRight,
				scale( size.height ) );
		}

		@Override
		protected void paintComponent( Graphics g ) {
			Graphics2D g2 = (Graphics2D) g;
			Object[] oldRenderingHints = FlatUIUtils.setRenderingHints( g2 );

			// simulate component y position at a fraction
			if( scaleFactor > 1 && SystemInfo.isJava_9_orLater )
				g2.translate( 0, yOffset );

			int width = getWidth();
			int height = getHeight();
			Insets insets = getInsets();
			FontMetrics fm = getFontMetrics( getFont() );

			// paint lines at 1x
			HiDPIUtils.paintAtScale1x( g2, 0, 0, width, height,
				(g2d, x2, y2, width2, height2, scaleFactor2) -> {
//					g.setColor( Color.blue );
//					g.drawLine( 0, 0, width2, 0 );
//					g.drawLine( 0, height2 - 1, width2, height2 - 1 );

					int baseline = (int) Math.round( (insets.top + fm.getAscent()) * scaleFactor2
						* (SystemInfo.isJava_9_orLater ? scaleFactor : 1f) ) - 1;
					int topline = height2 - baseline - 1;

					g.setColor( Color.red );
					g.drawLine( 0, baseline, width2, baseline );
					g.drawLine( 0, topline, width2, topline );
				} );

			// move x before scaling to have same left inset at all scale factors
			g.translate( insets.left, 0 );

			// scale
			if( SystemInfo.isJava_9_orLater )
				((Graphics2D)g).scale( scaleFactor, scaleFactor );

			// compute Y correction
			float yCorrection = correctionFunction.computeTextYCorrection( g2, scaleFactor );

			// create graphics that applies Y correction
			Graphics2D cg = new Graphics2DProxy( g2 ) {
				@Override
				public void drawString( String str, int x, int y ) {
					super.drawString( str, x, y + yCorrection );
				}

				@Override
				public void drawString( String str, float x, float y ) {
					super.drawString( str, x, y + yCorrection );
				}
			};

			// draw string
			g.setColor( getForeground() );
			int y = insets.top + fm.getAscent();
			JavaCompatibility.drawStringUnderlineCharAt( this, cg, "E", -1, 0, y );

			// set tooltip text
			if( getToolTipText() == null ) {
				AffineTransform t = g2.getTransform();
				double textY = t.getTranslateY() + (y * t.getScaleY());
				setToolTipText( textY + " + " + yCorrection + " = " + (textY + yCorrection) );
			}

			FlatUIUtils.resetRenderingHints( g2, oldRenderingHints );
		}

		private int scale( int value ) {
			return SystemInfo.isJava_9_orLater ? Math.round( value * scaleFactor ) : value;
		}
	}
}
