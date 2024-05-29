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

package com.formdev.flatlaf.swingx.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.plaf.basic.BasicBusyLabelUI;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatHTML;
import com.formdev.flatlaf.ui.FlatUIUtils;

//TODO scale busy spinner

/**
 * Provides the Flat LaF UI delegate for {@link org.jdesktop.swingx.JXBusyLabel}.
 *
 * @author Karl Tauber
 */
public class FlatBusyLabelUI
	extends BasicBusyLabelUI
{
	private Color disabledForeground;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatBusyLabelUI( (JXBusyLabel) c );
	}

	public FlatBusyLabelUI( JXBusyLabel busyLabel ) {
		super( busyLabel );
	}

	@Override
	protected void installDefaults( JLabel c ) {
		super.installDefaults( c );

		disabledForeground = UIManager.getColor( "Label.disabledForeground" );

		// force recreation of busy painter for correct colors when switching LaF
		if( c.getIcon() != null ) {
			JXBusyLabel busyLabel = (JXBusyLabel) c;
			boolean oldBusy = busyLabel.isBusy();
			busyLabel.setBusy( false );
			busyLabel.setBusyPainter( null );
			busyLabel.setBusy( oldBusy );
		}
	}

	@Override
	protected void uninstallDefaults( JLabel c ) {
		super.uninstallDefaults( c );

		disabledForeground = null;
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
	protected void paintDisabledText( JLabel l, Graphics g, String s, int textX, int textY ) {
		int mnemIndex = FlatLaf.isShowMnemonics() ? l.getDisplayedMnemonicIndex() : -1;
		g.setColor( disabledForeground );
		FlatUIUtils.drawStringUnderlineCharAt( l, g, s, mnemIndex, textX, textY );
	}
}
