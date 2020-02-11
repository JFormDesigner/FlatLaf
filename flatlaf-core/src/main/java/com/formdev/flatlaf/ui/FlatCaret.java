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

package com.formdev.flatlaf.ui;

import static com.formdev.flatlaf.FlatClientProperties.*;
import java.awt.EventQueue;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import javax.swing.JFormattedTextField;
import javax.swing.plaf.UIResource;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 * Caret that can select all text on focus gained.
 *
 * @author Karl Tauber
 */
class FlatCaret
	extends DefaultCaret
	implements UIResource
{
	private final String selectAllOnFocusPolicy;

	private boolean wasFocused;
	private boolean wasTemporaryLost;
	private boolean isMousePressed;

	FlatCaret( String selectAllOnFocusPolicy ) {
		this.selectAllOnFocusPolicy = selectAllOnFocusPolicy;
	}

	@Override
	public void install( JTextComponent c ) {
		super.install( c );

		// the dot and mark are lost when switching LaF
		// --> move dot to end of text so that all text may be selected when it gains focus
		Document doc = c.getDocument();
		if( doc != null && getDot() == 0 && getMark() == 0 ) {
			int length = doc.getLength();
			if( length > 0 )
				setDot( length );
		}
	}

	@Override
	public void focusGained( FocusEvent e ) {
		if( !wasTemporaryLost && !isMousePressed )
			selectAllOnFocusGained();
		wasTemporaryLost = false;
		wasFocused = true;

		super.focusGained( e );
	}

	@Override
	public void focusLost( FocusEvent e ) {
		wasTemporaryLost = e.isTemporary();
		super.focusLost( e );
	}

	@Override
	public void mousePressed( MouseEvent e ) {
		isMousePressed = true;
		super.mousePressed( e );
	}

	@Override
	public void mouseReleased( MouseEvent e ) {
		isMousePressed = false;
		super.mouseReleased( e );
	}

	private void selectAllOnFocusGained() {
		JTextComponent c = getComponent();
		Document doc = c.getDocument();
		if( doc == null || !c.isEnabled() || !c.isEditable() )
			return;

		Object selectAllOnFocusPolicy = c.getClientProperty( SELECT_ALL_ON_FOCUS_POLICY );
		if( selectAllOnFocusPolicy == null )
			selectAllOnFocusPolicy = this.selectAllOnFocusPolicy;

		if( SELECT_ALL_ON_FOCUS_POLICY_NEVER.equals( selectAllOnFocusPolicy ) )
			return;

		if( !SELECT_ALL_ON_FOCUS_POLICY_ALWAYS.equals( selectAllOnFocusPolicy ) ) {
			// policy is "once" (or null or unknown)

			// was already focused?
			if( wasFocused )
				return;

			// check whether selection was modified before gaining focus
			int dot = getDot();
			int mark = getMark();
			if( dot != mark || dot != doc.getLength() )
				return;
		}

		// select all
		if( c instanceof JFormattedTextField ) {
			EventQueue.invokeLater( () -> {
				setDot( 0 );
				moveDot( doc.getLength() );
			} );
		} else {
			setDot( 0 );
			moveDot( doc.getLength() );
		}
	}
}
