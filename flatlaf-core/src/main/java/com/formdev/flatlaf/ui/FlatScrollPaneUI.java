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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollPaneUI;

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

		MigLayoutVisualPadding.install( scrollpane, focusWidth );
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

	public Handler getHandler() {
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
