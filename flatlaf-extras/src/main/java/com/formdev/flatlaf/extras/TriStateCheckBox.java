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

package com.formdev.flatlaf.extras;

import java.awt.event.ItemEvent;
import javax.swing.JCheckBox;

/**
 * A tri-state check box.
 *
 * To display the third state, this component requires an LaF that supports painting
 * the indeterminate state if client property {@code "JButton.selectedState"} has the
 * value {@code "indeterminate"}.
 *
 * FlatLaf and Mac Aqua LaF support the third state.
 *
 * @author Karl Tauber
 */
public class TriStateCheckBox
	extends JCheckBox
{
	public enum State { INDETERMINATE, SELECTED, UNSELECTED }

	private State state;
	private boolean thirdStateEnabled = true;

	public TriStateCheckBox() {
		this( null );
	}

	public TriStateCheckBox( String text ) {
		this( text, State.INDETERMINATE );
	}

	public TriStateCheckBox( String text, State initialState ) {
		super( text );

		setModel( new ToggleButtonModel() {
			@Override
			public boolean isSelected() {
				return state != State.UNSELECTED;
			}

			@Override
			public void setSelected( boolean b ) {
				switch( state ) {
					case INDETERMINATE:			setState( State.SELECTED ); break;
					case SELECTED:		setState( State.UNSELECTED ); break;
					case UNSELECTED:	setState( thirdStateEnabled ? State.INDETERMINATE : State.SELECTED ); break;
				}

				fireStateChanged();
				fireItemStateChanged( new ItemEvent( this, ItemEvent.ITEM_STATE_CHANGED, this,
					isSelected() ? ItemEvent.SELECTED : ItemEvent.DESELECTED ) );
			}
		} );

		setState( initialState );
	}

	public State getState() {
		return state;
	}

	public void setState( State state ) {
		if( this.state == state )
			return;

		State oldState = this.state;
		this.state = state;

		putClientProperty( "JButton.selectedState", state == State.INDETERMINATE ? "indeterminate" : null );

		firePropertyChange( "state", oldState, state );
		repaint();
	}

	public boolean isThirdStateEnabled() {
		return thirdStateEnabled;
	}

	public void setThirdStateEnabled( boolean thirdStateEnabled ) {
		this.thirdStateEnabled = thirdStateEnabled;

		if( state == State.INDETERMINATE )
			setState( State.UNSELECTED );
	}

	@Override
	public void setSelected( boolean b ) {
		setState( b ? State.SELECTED : State.UNSELECTED );
	}
}
