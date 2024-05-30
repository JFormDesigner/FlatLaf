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

import java.beans.PropertyChangeEvent;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonListener;
import com.formdev.flatlaf.ui.FlatHTML;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.basic.BasicJideButtonListener;
import com.jidesoft.plaf.basic.BasicJideButtonUI;

/**
 * Provides the Flat LaF UI delegate for {@link com.jidesoft.swing.JideButton}.
 *
 * @author Karl Tauber
 * @since 1.1
 */
public class FlatJideButtonUI
	extends BasicJideButtonUI
{
	public static ComponentUI createUI( JComponent c ) {
		// usually JIDE would invoke this in JideButton.updateUI(),
		// but it does not because FlatLaf already has added the UI class to the UI defaults
		LookAndFeelFactory.installJideExtension();

		return new FlatJideButtonUI();
	}

	@Override
	protected BasicButtonListener createButtonListener( AbstractButton b ) {
		return new FlatJideButtonListener( b );
	}

	//---- class FlatJideButtonListener ---------------------------------------

	/** @since 3.5 */
	protected static class FlatJideButtonListener
		extends BasicJideButtonListener
	{
		protected FlatJideButtonListener( AbstractButton b ) {
			super( b );
		}

		@Override
		public void propertyChange( PropertyChangeEvent e ) {
			super.propertyChange( e );
			FlatHTML.propertyChange( e );
		}
	}
}
