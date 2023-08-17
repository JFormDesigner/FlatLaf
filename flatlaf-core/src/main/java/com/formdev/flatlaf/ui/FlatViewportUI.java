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
import java.lang.reflect.Method;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicViewportUI;
import com.formdev.flatlaf.SmoothScrollingHelper;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JViewport}.
 *
 * <!-- BasicViewportUI -->
 *
 * @uiDefault Viewport.font					Font	unused
 * @uiDefault Viewport.background			Color
 * @uiDefault Viewport.foreground			Color	unused
 *
 * @author Karl Tauber
 */
public class FlatViewportUI
	extends BasicViewportUI implements AncestorListener
{
	public static ComponentUI createUI( JComponent c ) {
		return FlatUIUtils.createSharedUI( FlatViewportUI.class, FlatViewportUI::new );
	}

	@Override
	public void paint( Graphics g, JComponent c ) {
		super.paint( g, c );

		Component view = ((JViewport)c).getView();
		if( view instanceof JComponent ) {
			try {
				Method m = view.getClass().getMethod( "getUI" );
				Object ui = m.invoke( view );
				if( ui instanceof ViewportPainter )
					((ViewportPainter)ui).paintViewport( g, (JComponent) view, (JViewport) c );
			} catch( Exception ex ) {
				// ignore
			}
		}
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );
		c.addAncestorListener( this );
	}
	
	@Override
	public void uninstallUI( JComponent c ) {
		super.uninstallUI( c );
		c.removeAncestorListener( this );
	}
	
	@Override
	public void ancestorAdded( AncestorEvent event ) {
		SmoothScrollingHelper.registerViewport( (JViewport)event.getComponent() );
	}
	
	@Override
	public void ancestorMoved( AncestorEvent event ) {
		JViewport viewport = (JViewport)event.getComponent();
		if(viewport.isShowing()) {
			SmoothScrollingHelper.registerViewport( viewport );
		} else {
			SmoothScrollingHelper.unregisterViewport( viewport );
		}
	}

	@Override
	public void ancestorRemoved( AncestorEvent event ) {
		SmoothScrollingHelper.unregisterViewport( (JViewport)event.getComponent() );
	}
	
	//---- interface ViewportPainter ------------------------------------------

	/**
	 * @since 2.3
	 */
	public interface ViewportPainter {
		void paintViewport( Graphics g, JComponent c, JViewport viewport );
	}
}
