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

import static com.formdev.flatlaf.util.UIScale.scale;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPasswordFieldUI;
import javax.swing.text.JTextComponent;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JPasswordField}.
 *
 * TODO document used UI defaults of superclass
 *
 * @uiDefault Component.focusWidth				int
 * @uiDefault Component.minimumWidth			int
 *
 * @author Karl Tauber
 */
public class FlatPasswordFieldUI
	extends BasicPasswordFieldUI
{
	protected int focusWidth;
	protected int minimumWidth;

	private Handler handler;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatPasswordFieldUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		// use other echoChar on Mac because the default is too large in SF font
		if( SystemInfo.IS_MAC )
			LookAndFeel.installProperty( getComponent(), "echoChar", '\u2022' );

		focusWidth = UIManager.getInt( "Component.focusWidth" );
		minimumWidth = UIManager.getInt( "Component.minimumWidth" );

		MigLayoutVisualPadding.install( getComponent(), focusWidth );
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		MigLayoutVisualPadding.uninstall( getComponent() );
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		getComponent().addFocusListener( getHandler() );
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		getComponent().removeFocusListener( getHandler() );

		handler = null;
	}

	public Handler getHandler() {
		if( handler == null )
			handler = new Handler();
		return handler;
	}

	@Override
	protected void paintBackground( Graphics g ) {
		JTextComponent c = getComponent();

		FlatUIUtils.paintParentBackground( g, c );

		Graphics2D g2 = (Graphics2D) g.create();
		try {
			FlatUIUtils.setRenderingHints( g2 );

			float focusWidth = (c.getBorder() instanceof FlatBorder) ? scale( (float) this.focusWidth ) : 0;

			g2.setColor( c.getBackground() );
			FlatUIUtils.fillRoundRectangle( g2, 0, 0, c.getWidth(), c.getHeight(), focusWidth, 0 );
		} finally {
			g2.dispose();
		}
	}

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		return applyMinimumWidth( super.getPreferredSize( c ) );
	}

	@Override
	public Dimension getMinimumSize( JComponent c ) {
		return applyMinimumWidth( super.getMinimumSize( c ) );
	}

	private Dimension applyMinimumWidth( Dimension size ) {
		size.width = Math.max( size.width, scale( minimumWidth + (focusWidth * 2) ) );
		return size;
	}

	//---- class Handler ------------------------------------------------------

	private class Handler
		implements FocusListener
	{
		@Override
		public void focusGained( FocusEvent e ) {
			getComponent().repaint();
		}

		@Override
		public void focusLost( FocusEvent e ) {
			getComponent().repaint();
		}
	}
}
