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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JScrollBar}.
 *
 * <!-- BasicScrollBarUI -->
 *
 * @uiDefault ScrollBar.background			Color
 * @uiDefault ScrollBar.foreground			Color
 * @uiDefault ScrollBar.track				Color
 * @uiDefault ScrollBar.thumb				Color
 * @uiDefault ScrollBar.width				int
 * @uiDefault ScrollBar.minimumThumbSize	Dimension
 * @uiDefault ScrollBar.maximumThumbSize	Dimension
 * @uiDefault ScrollBar.allowsAbsolutePositioning	boolean
 *
 * <!-- FlatScrollBarUI -->
 *
 * @uiDefault ScrollBar.trackInsets					Insets
 * @uiDefault ScrollBar.thumbInsets					Insets
 * @uiDefault ScrollBar.trackArc					int
 * @uiDefault ScrollBar.thumbArc					int
 * @uiDefault ScrollBar.hoverTrackColor				Color	optional
 * @uiDefault ScrollBar.hoverThumbColor				Color	optional
 * @uiDefault ScrollBar.hoverThumbWithTrack			boolean
 * @uiDefault ScrollBar.pressedTrackColor			Color	optional
 * @uiDefault ScrollBar.pressedThumbColor			Color	optional
 * @uiDefault ScrollBar.pressedThumbWithTrack		boolean
 * @uiDefault Component.arrowType					String	triangle (default) or chevron
 * @uiDefault ScrollBar.showButtons					boolean
 * @uiDefault ScrollBar.buttonArrowColor			Color
 * @uiDefault ScrollBar.buttonDisabledArrowColor	Color
 * @uiDefault ScrollBar.hoverButtonBackground		Color	optional
 * @uiDefault ScrollBar.pressedButtonBackground		Color	optional
 *
 * @author Karl Tauber
 */
public class FlatScrollBarUI
	extends BasicScrollBarUI
{
	protected Insets trackInsets;
	protected Insets thumbInsets;
	protected int trackArc;
	protected int thumbArc;
	protected Color hoverTrackColor;
	protected Color hoverThumbColor;
	protected boolean hoverThumbWithTrack;
	protected Color pressedTrackColor;
	protected Color pressedThumbColor;
	protected boolean pressedThumbWithTrack;

	protected boolean showButtons;
	protected String arrowType;
	protected Color buttonArrowColor;
	protected Color buttonDisabledArrowColor;
	protected Color hoverButtonBackground;
	protected Color pressedButtonBackground;

	private MouseAdapter hoverListener;
	protected boolean hoverTrack;
	protected boolean hoverThumb;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatScrollBarUI();
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		hoverListener = new ScrollBarHoverListener();
		scrollbar.addMouseListener( hoverListener );
		scrollbar.addMouseMotionListener( hoverListener );
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		scrollbar.removeMouseListener( hoverListener );
		scrollbar.removeMouseMotionListener( hoverListener );
		hoverListener = null;
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		trackInsets = UIManager.getInsets( "ScrollBar.trackInsets" );
		thumbInsets = UIManager.getInsets( "ScrollBar.thumbInsets" );
		trackArc = UIManager.getInt( "ScrollBar.trackArc" );
		thumbArc = UIManager.getInt( "ScrollBar.thumbArc" );
		hoverTrackColor = UIManager.getColor( "ScrollBar.hoverTrackColor" );
		hoverThumbColor = UIManager.getColor( "ScrollBar.hoverThumbColor" );
		hoverThumbWithTrack = UIManager.getBoolean( "ScrollBar.hoverThumbWithTrack" );
		pressedTrackColor = UIManager.getColor( "ScrollBar.pressedTrackColor" );
		pressedThumbColor = UIManager.getColor( "ScrollBar.pressedThumbColor" );
		pressedThumbWithTrack = UIManager.getBoolean( "ScrollBar.pressedThumbWithTrack" );

		showButtons = UIManager.getBoolean( "ScrollBar.showButtons" );
		arrowType = UIManager.getString( "Component.arrowType" );
		buttonArrowColor = UIManager.getColor( "ScrollBar.buttonArrowColor" );
		buttonDisabledArrowColor = UIManager.getColor( "ScrollBar.buttonDisabledArrowColor" );
		hoverButtonBackground = UIManager.getColor( "ScrollBar.hoverButtonBackground" );
		pressedButtonBackground = UIManager.getColor( "ScrollBar.pressedButtonBackground" );
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		trackInsets = null;
		thumbInsets = null;
		hoverTrackColor = null;
		hoverThumbColor = null;
		pressedTrackColor = null;
		pressedThumbColor = null;

		buttonArrowColor = null;
		buttonDisabledArrowColor = null;
		hoverButtonBackground = null;
		pressedButtonBackground = null;
	}

	@Override
	protected PropertyChangeListener createPropertyChangeListener() {
		return new BasicScrollBarUI.PropertyChangeHandler() {
			@Override
			public void propertyChange( PropertyChangeEvent e ) {
				super.propertyChange( e );

				switch( e.getPropertyName() ) {
					case FlatClientProperties.SCROLL_BAR_SHOW_BUTTONS:
						scrollbar.revalidate();
						scrollbar.repaint();
						break;

					case "componentOrientation":
						// this is missing in BasicScrollBarUI.Handler.propertyChange()
						InputMap inputMap = (InputMap) UIManager.get( "ScrollBar.ancestorInputMap" );
						if( !scrollbar.getComponentOrientation().isLeftToRight() ) {
							InputMap rtlInputMap = (InputMap) UIManager.get( "ScrollBar.ancestorInputMap.RightToLeft" );
							if( rtlInputMap != null ) {
								rtlInputMap.setParent( inputMap );
								inputMap = rtlInputMap;
							}
						}
						SwingUtilities.replaceUIInputMap( scrollbar, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inputMap );
						break;
				}
			}
		};
	}

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		return UIScale.scale( super.getPreferredSize( c ) );
	}

	@Override
	protected JButton createDecreaseButton( int orientation ) {
		return createArrowButton( orientation );
	}

	@Override
	protected JButton createIncreaseButton( int orientation ) {
		return createArrowButton( orientation );
	}

	private JButton createArrowButton( int orientation ) {
		FlatArrowButton button = new FlatArrowButton( orientation, arrowType, buttonArrowColor,
			buttonDisabledArrowColor, null, hoverButtonBackground, pressedButtonBackground )
		{
			@Override
			protected Color deriveBackground( Color background ) {
				return FlatUIUtils.deriveColor( background, scrollbar.getBackground() );
			}

			@Override
			public Dimension getPreferredSize() {
				if( isShowButtons() ) {
					int w = UIScale.scale( scrollBarWidth );
					return new Dimension( w, w );
				} else
					return new Dimension();
			}

			@Override
			public Dimension getMinimumSize() {
				return isShowButtons() ? super.getMinimumSize() : new Dimension();
			}

			@Override
			public Dimension getMaximumSize() {
				return isShowButtons() ? super.getMaximumSize() : new Dimension();
			}
		};
		button.setArrowWidth( FlatArrowButton.DEFAULT_ARROW_WIDTH - 2 );
		button.setFocusable( false );
		button.setRequestFocusEnabled( false );
		return button;
	}

	protected boolean isShowButtons() {
		Object showButtons = scrollbar.getClientProperty( FlatClientProperties.SCROLL_BAR_SHOW_BUTTONS );
		if( showButtons == null && scrollbar.getParent() instanceof JScrollPane )
			showButtons = ((JScrollPane)scrollbar.getParent()).getClientProperty( FlatClientProperties.SCROLL_BAR_SHOW_BUTTONS );
		return (showButtons != null) ? Objects.equals( showButtons, true ) : this.showButtons;
	}

	@Override
	public void paint( Graphics g, JComponent c ) {
		FlatUIUtils.setRenderingHints( (Graphics2D) g );
		super.paint( g, c );
	}

	@Override
	protected void paintTrack( Graphics g, JComponent c, Rectangle trackBounds ) {
		g.setColor( getTrackColor( c, hoverTrack, isPressed && hoverTrack && !hoverThumb ) );
		paintTrackOrThumb( g, c, trackBounds, trackInsets, trackArc );
	}

	@Override
	protected void paintThumb( Graphics g, JComponent c, Rectangle thumbBounds ) {
		if( thumbBounds.isEmpty() || !scrollbar.isEnabled() )
			return;

		g.setColor( getThumbColor( c, hoverThumb || (hoverThumbWithTrack && hoverTrack),
			isPressed && (hoverThumb || (pressedThumbWithTrack && hoverTrack)) ) );
		paintTrackOrThumb( g, c, thumbBounds, thumbInsets, thumbArc );
	}

	protected void paintTrackOrThumb( Graphics g, JComponent c, Rectangle bounds, Insets insets, int arc ) {
		// rotate insets for horizontal orientation because they are given for vertical orientation
		if( scrollbar.getOrientation() == JScrollBar.HORIZONTAL )
			insets = new Insets( insets.right, insets.top, insets.left, insets.bottom );

		// subtract insets from bounds
		bounds = FlatUIUtils.subtractInsets( bounds, UIScale.scale( insets ) );

		if( arc <= 0 ) {
			// paint rectangle
			g.fillRect( bounds.x, bounds.y, bounds.width, bounds.height );
		} else {
			// paint round rectangle
			arc = Math.min( UIScale.scale( arc ), Math.min( bounds.width, bounds.height ) );
			g.fillRoundRect( bounds.x, bounds.y, bounds.width, bounds.height, arc, arc );
		}
	}

	@Override
	protected void paintDecreaseHighlight( Graphics g ) {
		// do not paint
	}

	@Override
	protected void paintIncreaseHighlight( Graphics g ) {
		// do not paint
	}

	protected Color getTrackColor( JComponent c, boolean hover, boolean pressed ) {
		Color trackColor = FlatUIUtils.deriveColor( this.trackColor, c.getBackground() );
		return (pressed && pressedTrackColor != null)
			? FlatUIUtils.deriveColor( pressedTrackColor, trackColor )
			: ((hover && hoverTrackColor != null)
				? FlatUIUtils.deriveColor( hoverTrackColor, trackColor )
				: trackColor);
	}

	protected Color getThumbColor( JComponent c, boolean hover, boolean pressed ) {
		Color trackColor = FlatUIUtils.deriveColor( this.trackColor, c.getBackground() );
		Color thumbColor = FlatUIUtils.deriveColor( this.thumbColor, trackColor );
		return (pressed && pressedThumbColor != null)
			? FlatUIUtils.deriveColor( pressedThumbColor, thumbColor )
			: ((hover && hoverThumbColor != null)
				? FlatUIUtils.deriveColor( hoverThumbColor, thumbColor )
				: thumbColor);
	}

	@Override
	protected Dimension getMinimumThumbSize() {
		return UIScale.scale( super.getMinimumThumbSize() );
	}

	@Override
	protected Dimension getMaximumThumbSize() {
		return UIScale.scale( super.getMaximumThumbSize() );
	}

	//---- class ScrollBarHoverListener ---------------------------------------

	// using static field to disabling hover for other scroll bars
	private static boolean isPressed;

	private class ScrollBarHoverListener
		extends MouseAdapter
	{
		@Override
		public void mouseExited( MouseEvent e ) {
			if( !isPressed ) {
				hoverTrack = hoverThumb = false;
				repaint();
			}
		}

		@Override
		public void mouseMoved( MouseEvent e ) {
			if( !isPressed )
				update( e.getX(), e.getY() );
		}

		@Override
		public void mousePressed( MouseEvent e ) {
			isPressed = true;
			repaint();
		}

		@Override
		public void mouseReleased( MouseEvent e ) {
			isPressed = false;
			repaint();

			update( e.getX(), e.getY() );
		}

		private void update( int x, int y ) {
			boolean inTrack = getTrackBounds().contains( x, y );
			boolean inThumb = getThumbBounds().contains( x, y );
			if( inTrack != hoverTrack || inThumb != hoverThumb ) {
				hoverTrack = inTrack;
				hoverThumb = inThumb;
				repaint();
			}
		}

		private void repaint() {
			if( scrollbar.isEnabled() )
				scrollbar.repaint();
		}
	}
}
