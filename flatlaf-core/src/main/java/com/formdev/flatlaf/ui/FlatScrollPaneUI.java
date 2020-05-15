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

/*
 * Smooth scrolling code partly based on code from IntelliJ IDEA Community Edition,
 * which is licensed under the Apache 2.0 license. Copyright 2000-2016 JetBrains s.r.o.
 * See: https://github.com/JetBrains/intellij-community/blob/31e1b5a8e43219b9571951bab6457cfb3012e3ef/platform/platform-api/src/com/intellij/ui/components/SmoothScrollPane.java#L141-L185
 *
 */
package com.formdev.flatlaf.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.LookAndFeel;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollPaneUI;
import com.formdev.flatlaf.FlatClientProperties;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JScrollPane}.
 *
 * <!-- BasicScrollPaneUI -->
 *
 * @uiDefault ScrollPane.font				Font	unused
 * @uiDefault ScrollPane.background			Color
 * @uiDefault ScrollPane.foreground			Color	unused
 * @uiDefault ScrollPane.border				Border
 * @uiDefault ScrollPane.viewportBorder		Border
 *
 * <!-- FlatScrollPaneUI -->
 *
 * @uiDefault ScrollPane.smoothScrolling		boolean
 *
 * @author Karl Tauber
 */
public class FlatScrollPaneUI
	extends BasicScrollPaneUI
{
	private Handler handler;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatScrollPaneUI();
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		int focusWidth = UIManager.getInt( "Component.focusWidth" );
		LookAndFeel.installProperty( c, "opaque", focusWidth == 0 );

		MigLayoutVisualPadding.install( scrollpane );
	}

	@Override
	public void uninstallUI( JComponent c ) {
		MigLayoutVisualPadding.uninstall( scrollpane );

		super.uninstallUI( c );
	}

	@Override
	protected void installListeners( JScrollPane c ) {
		super.installListeners( c );

		addViewportListeners( scrollpane.getViewport() );
	}

	@Override
	protected void uninstallListeners( JComponent c ) {
		super.uninstallListeners( c );

		removeViewportListeners( scrollpane.getViewport() );

		handler = null;
	}

	@Override
	protected MouseWheelListener createMouseWheelListener() {
		return new BasicScrollPaneUI.MouseWheelHandler() {
			@Override
			public void mouseWheelMoved( MouseWheelEvent e ) {
				// Note: Getting UI value "ScrollPane.smoothScrolling" here to allow
				// applications to turn smooth scrolling on or off at any time
				// (e.g. in application options dialog).
				if( UIManager.getBoolean( "ScrollPane.smoothScrolling" ) &&
					scrollpane.isWheelScrollingEnabled() &&
					e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL &&
					e.getPreciseWheelRotation() != 0 &&
					e.getPreciseWheelRotation() != e.getWheelRotation() )
				{
					mouseWheelMovedSmooth( e );
				} else
					super.mouseWheelMoved( e );
			}
		};
	}

	private static final double EPSILON = 1e-5d;

	private void mouseWheelMovedSmooth( MouseWheelEvent e ) {
		// return if there is no viewport
		JViewport viewport = scrollpane.getViewport();
		if( viewport == null )
			return;

		// find scrollbar to scroll
		JScrollBar scrollbar = scrollpane.getVerticalScrollBar();
		if( scrollbar == null || !scrollbar.isVisible() || e.isShiftDown() ) {
			scrollbar = scrollpane.getHorizontalScrollBar();
			if( scrollbar == null || !scrollbar.isVisible() )
				return;
		}

		// consume event
		e.consume();

		// get precise wheel rotation
		double rotation = e.getPreciseWheelRotation();

		// get unit and block increment
		int unitIncrement;
		int blockIncrement;
		int orientation = scrollbar.getOrientation();
		Component view = viewport.getView();
		if( view instanceof Scrollable ) {
			Scrollable scrollable = (Scrollable) view;

			// Use (0, 0) view position to obtain constant unit increment of first item
			// (which might otherwise be variable on smaller-than-unit scrolling).
			Rectangle visibleRect = new Rectangle( viewport.getViewSize() );
			unitIncrement = scrollable.getScrollableUnitIncrement( visibleRect, orientation, 1 );
			blockIncrement = scrollable.getScrollableBlockIncrement( visibleRect, orientation, 1 );

			if( unitIncrement > 0 ) {
				// For the case that the first item (e.g. in a list) is larger
				// than the other items, get the unit increment of the second item
				// and use the smaller one.
				if( orientation == SwingConstants.VERTICAL ) {
					visibleRect.y += unitIncrement;
					visibleRect.height -= unitIncrement;
				} else {
					visibleRect.x += unitIncrement;
					visibleRect.width -= unitIncrement;
				}
				int unitIncrement2 = scrollable.getScrollableUnitIncrement( visibleRect, orientation, 1 );
				if( unitIncrement2 > 0 )
					unitIncrement = Math.min( unitIncrement, unitIncrement2 );
			}
		} else {
			int direction = rotation < 0 ? -1 : 1;
			unitIncrement = scrollbar.getUnitIncrement( direction );
			blockIncrement = scrollbar.getBlockIncrement( direction );
		}

		// limit scroll amount (number of units to scroll) for small viewports
		// (e.g. vertical scrolling in file chooser)
		int scrollAmount = e.getScrollAmount();
		int viewportWH = (orientation == SwingConstants.VERTICAL)
			? viewport.getHeight()
			: viewport.getWidth();
		if( unitIncrement * scrollAmount > viewportWH )
			scrollAmount = Math.max( viewportWH / unitIncrement, 1 );

		// compute relative delta
		double delta = rotation * scrollAmount * unitIncrement;
		boolean adjustDelta = Math.abs( rotation ) < (1.0 + EPSILON);
		double adjustedDelta = adjustDelta
			? Math.max( -blockIncrement, Math.min( delta, blockIncrement ) )
			: delta;

		// compute new value
		int value = scrollbar.getValue();
		double minDelta = scrollbar.getMinimum() - value;
		double maxDelta = scrollbar.getMaximum() - scrollbar.getModel().getExtent() - value;
		double boundedDelta = Math.max( minDelta, Math.min( adjustedDelta, maxDelta ) );
		int newValue = value + (int) Math.round( boundedDelta );

		// set new value
		if( newValue != value )
			scrollbar.setValue( newValue );

/*debug
		System.out.println( String.format( "%4d  %9f / %4d %4d / %12f %5s %12f / %4d %4d %4d / %12f %12f %12f / %4d",
			e.getWheelRotation(),
			e.getPreciseWheelRotation(),
			unitIncrement,
			blockIncrement,
			delta,
			adjustDelta,
			adjustedDelta,
			value,
			scrollbar.getMinimum(),
			scrollbar.getMaximum(),
			minDelta,
			maxDelta,
			boundedDelta,
			newValue ) );
*/
	}

	@Override
	protected PropertyChangeListener createPropertyChangeListener() {
		return new BasicScrollPaneUI.PropertyChangeHandler() {
			@Override
			public void propertyChange( PropertyChangeEvent e ) {
				super.propertyChange( e );

				switch( e.getPropertyName() ) {
					case FlatClientProperties.SCROLL_BAR_SHOW_BUTTONS:
						JScrollBar vsb = scrollpane.getVerticalScrollBar();
						JScrollBar hsb = scrollpane.getHorizontalScrollBar();
						if( vsb != null ) {
							vsb.revalidate();
							vsb.repaint();
						}
						if( hsb != null ) {
							hsb.revalidate();
							hsb.repaint();
						}
						break;

					case ScrollPaneConstants.LOWER_LEFT_CORNER:
					case ScrollPaneConstants.LOWER_RIGHT_CORNER:
					case ScrollPaneConstants.UPPER_LEFT_CORNER:
					case ScrollPaneConstants.UPPER_RIGHT_CORNER:
						// remove border from buttons added to corners
						Object corner = e.getNewValue();
						if( corner instanceof JButton &&
							((JButton)corner).getBorder() instanceof FlatButtonBorder &&
							scrollpane.getViewport() != null &&
							scrollpane.getViewport().getView() instanceof JTable )
						{
							((JButton)corner).setBorder( BorderFactory.createEmptyBorder() );
							((JButton)corner).setFocusable( false );
						}
					break;
				}
			}
		};
	}

	private Handler getHandler() {
		if( handler == null )
			handler = new Handler();
		return handler;
	}

	@Override
	protected void updateViewport( PropertyChangeEvent e ) {
		super.updateViewport( e );

		JViewport oldViewport = (JViewport) (e.getOldValue());
		JViewport newViewport = (JViewport) (e.getNewValue());

		removeViewportListeners( oldViewport );
		addViewportListeners( newViewport );
	}

	private void addViewportListeners( JViewport viewport ) {
		if( viewport == null )
			return;

		viewport.addContainerListener( getHandler() );

		Component view = viewport.getView();
		if( view != null )
			view.addFocusListener( getHandler() );
	}

	private void removeViewportListeners( JViewport viewport ) {
		if( viewport == null )
			return;

		viewport.removeContainerListener( getHandler() );

		Component view = viewport.getView();
		if( view != null )
			view.removeFocusListener( getHandler() );
	}

	@Override
	public void update( Graphics g, JComponent c ) {
		if( c.isOpaque() ) {
			FlatUIUtils.paintParentBackground( g, c );

			// paint background so that corners have same color as scroll bars
			Insets insets = c.getInsets();
			g.setColor( c.getBackground() );
			g.fillRect( insets.left, insets.top,
				c.getWidth() - insets.left - insets.right,
				c.getHeight() - insets.top - insets.bottom );
		}

		paint( g, c );
	}

	//---- class Handler ------------------------------------------------------

	/**
	 * ContainerListener is added to JViewport to keep focus listener on view up-to-date.
	 * FocusListener is added to view for repainting when view gets focused.
	 */
	private class Handler
		implements ContainerListener, FocusListener
	{
		@Override
		public void componentAdded( ContainerEvent e ) {
			e.getChild().addFocusListener( this );
		}

		@Override
		public void componentRemoved( ContainerEvent e ) {
			e.getChild().removeFocusListener( this );
		}

		@Override
		public void focusGained( FocusEvent e ) {
			scrollpane.repaint();
		}

		@Override
		public void focusLost( FocusEvent e ) {
			scrollpane.repaint();
		}
	}
}
