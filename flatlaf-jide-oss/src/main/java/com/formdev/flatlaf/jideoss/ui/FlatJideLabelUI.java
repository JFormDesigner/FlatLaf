/*
 * Copyright 2021 FormDev Software GmbH
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

package com.formdev.flatlaf.jideoss.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatHTML;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.basic.BasicJideLabelUI;

/**
 * Provides the Flat LaF UI delegate for {@link com.jidesoft.swing.JideLabel}.
 *
 * @author Karl Tauber
 * @since 1.1
 */
public class FlatJideLabelUI
	extends BasicJideLabelUI
{
	private Color disabledForeground;

	private boolean defaults_initialized = false;

	public static ComponentUI createUI( JComponent c ) {
		// usually JIDE would invoke this in JideLabel.updateUI(),
		// but it does not because FlatLaf already has added the UI class to the UI defaults
		LookAndFeelFactory.installJideExtension();

		return FlatUIUtils.createSharedUI( FlatJideLabelUI.class, FlatJideLabelUI::new );
	}

	@Override
	protected void installDefaults( JLabel c ) {
		super.installDefaults( c );

		if( !defaults_initialized ) {
			disabledForeground = UIManager.getColor( "JideLabel.disabledForeground" );

			defaults_initialized = true;
		}
	}

	@Override
	protected void uninstallDefaults( JLabel c ) {
		super.uninstallDefaults( c );
		defaults_initialized = false;
	}

	@Override
	protected void installComponents( JLabel c ) {
		super.installComponents( c );

		// update HTML renderer if necessary
		FlatHTML.updateRendererCSSFontBaseSize( c );
	}

	@Override
	public void propertyChange( PropertyChangeEvent e ) {
		super.propertyChange( e );
		FlatHTML.propertyChange( e );
	}

	@Override
	protected void paintEnabledText( JLabel l, Graphics g, String s, int textX, int textY ) {
		int mnemIndex = FlatLaf.isShowMnemonics() ? l.getDisplayedMnemonicIndex() : -1;
		g.setColor( l.getForeground() );
		FlatUIUtils.drawStringUnderlineCharAt( l, g, s, mnemIndex, textX, textY );
	}

	@Override
	protected void paintDisabledText( JLabel l, Graphics g, String s, int textX, int textY ) {
		int mnemIndex = FlatLaf.isShowMnemonics() ? l.getDisplayedMnemonicIndex() : -1;
		g.setColor( disabledForeground );
		FlatUIUtils.drawStringUnderlineCharAt( l, g, s, mnemIndex, textX, textY );
	}
}
