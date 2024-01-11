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

package com.formdev.flatlaf.extras.components;

import static com.formdev.flatlaf.FlatClientProperties.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ItemEvent;
import javax.swing.JCheckBox;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import com.formdev.flatlaf.FlatLaf;

/**
 * A tri-state check box.
 * <p>
 * The initial state is {@link State#INDETERMINATE}.
 * <p>
 * By default, the third state is allowed and clicking on the checkbox cycles through all
 * three states. If you want that the user can cycle only through two states, disallow
 * intermediate state using {@link #setAllowIndeterminate(boolean)}. Then you can still
 * set the indeterminate state via API if necessary, but the user can not.
 * <p>
 * The default state cycle order is {@link State#UNSELECTED} to {@link State#INDETERMINATE}
 * to {@link State#SELECTED}.
 * This is the same order as used by macOS, win32, IntelliJ IDEA and on the web as recommended by W3C in
 * <a href="https://www.w3.org/TR/wai-aria-practices-1.1/examples/checkbox/checkbox-2/checkbox-2.html">Tri-State Checkbox Example</a>).
 * <p>
 * If {@link #isAltStateCycleOrder()} returns {@code true},
 * the state cycle order is {@link State#UNSELECTED} to {@link State#SELECTED}
 * to {@link State#INDETERMINATE}. This order is used by Windows 10 UWP apps.
 * <p>
 * If you prefer the alternative state cycle order for all tri-state check boxes, enable it using:
 * <pre>
 * UIManager.put( "FlatTriStateCheckBox.altStateCycleOrder", true );
 * </pre>
 * <p>
 * To display the third state, this component requires an LaF that supports painting
 * the indeterminate state if client property {@code "JButton.selectedState"} has the
 * value {@code "indeterminate"}.
 * FlatLaf and macOS Aqua LaF support the third state.
 * For other LaFs a magenta rectangle is painted around the component for the third state.
 *
 * @author Karl Tauber
 */
public class FlatTriStateCheckBox
	extends JCheckBox
{
	public enum State { UNSELECTED, INDETERMINATE, SELECTED }

	private State state;
	private boolean allowIndeterminate = true;
	private boolean altStateCycleOrder = UIManager.getBoolean( "FlatTriStateCheckBox.altStateCycleOrder" );

	public FlatTriStateCheckBox() {
		this( null );
	}

	public FlatTriStateCheckBox( String text ) {
		this( text, State.INDETERMINATE );
	}

	public FlatTriStateCheckBox( String text, State initialState ) {
		super( text );

		setModel( new ToggleButtonModel() {
			@Override
			public boolean isSelected() {
				return state != State.UNSELECTED;
			}

			@Override
			public void setSelected( boolean b ) {
				setState( nextState( state ) );

				fireStateChanged();
				fireItemStateChanged( new ItemEvent( this, ItemEvent.ITEM_STATE_CHANGED, this,
					isSelected() ? ItemEvent.SELECTED : ItemEvent.DESELECTED ) );
			}
		} );

		setState( initialState );
	}

	/**
	 * Returns the state as {@link State} enum.
	 * <p>
	 * Alternatively you can use {@link #getChecked()} to get all three states as {@link Boolean}
	 * or {@link #isIndeterminate()} to check only for indeterminate state.
	 */
	public State getState() {
		return state;
	}

	/**
	 * Sets the state as {@link State} enum.
	 */
	public void setState( State state ) {
		if( this.state == state )
			return;

		State oldState = this.state;
		this.state = state;

		putClientProperty( SELECTED_STATE, (state == State.INDETERMINATE) ? SELECTED_STATE_INDETERMINATE : null );

		firePropertyChange( "state", oldState, state );
		repaint();
	}

	/**
	 * Returns the next state that follows the given state, depending on
	 * {@link #isAllowIndeterminate()} and {@link #isAltStateCycleOrder()}.
	 */
	protected State nextState( State state ) {
		if( !altStateCycleOrder ) {
			// default cycle order: UNSELECTED --> INDETERMINATE --> SELECTED
			switch( state ) {
				default:
				case UNSELECTED:		return allowIndeterminate ? State.INDETERMINATE : State.SELECTED;
				case INDETERMINATE:	return State.SELECTED;
				case SELECTED:		return State.UNSELECTED;
			}
		} else {
			// alternative cycle order: INDETERMINATE --> UNSELECTED --> SELECTED
			switch( state ) {
				default:
				case UNSELECTED:		return State.SELECTED;
				case INDETERMINATE:	return State.UNSELECTED;
				case SELECTED:		return allowIndeterminate ? State.INDETERMINATE : State.UNSELECTED;
			}
		}
	}

	/**
	 * Returns the state as {@link Boolean}.
	 * Returns {@code null} if the state is {@link State#INDETERMINATE}.
	 * <p>
	 * Alternatively you can use {@link #getState()} to get state as {@link State} enum
	 * or {@link #isIndeterminate()} to check only for indeterminate state.
	 */
	public Boolean getChecked() {
		switch( state ) {
			default:
			case UNSELECTED:	return false;
			case INDETERMINATE:	return null;
			case SELECTED:		return true;
		}
	}

	/**
	 * Sets the state as {@link Boolean}.
	 * Passing {@code null} sets state to {@link State#INDETERMINATE}.
	 */
	public void setChecked( Boolean value ) {
		setState( (value == null) ? State.INDETERMINATE : (value ? State.SELECTED : State.UNSELECTED) );
	}

	@Override
	public void setSelected( boolean b ) {
		setState( b ? State.SELECTED : State.UNSELECTED );
	}

	/**
	 * Returns whether state is indeterminate.
	 */
	public boolean isIndeterminate() {
		return state == State.INDETERMINATE;
	}

	/**
	 * Sets indeterminate state.
	 */
	public void setIndeterminate( boolean indeterminate ) {
		if( indeterminate )
			setState( State.INDETERMINATE );
		else if( state == State.INDETERMINATE )
			setState( State.UNSELECTED );
	}

	/**
	 * Returns whether indeterminate state is allowed.
	 * <p>
	 * This affects only the user when clicking on the checkbox.
	 * Setting state to indeterminate via API is always allowed.
	 */
	public boolean isAllowIndeterminate() {
		return allowIndeterminate;
	}

	/**
	 * Sets whether indeterminate state is allowed.
	 * <p>
	 * This affects only the user when clicking on the checkbox.
	 * Setting state to indeterminate via API is always allowed.
	 */
	public void setAllowIndeterminate( boolean allowIndeterminate ) {
		this.allowIndeterminate = allowIndeterminate;
	}

	/**
	 * Returns whether alternative state cycle order should be used.
	 */
	public boolean isAltStateCycleOrder() {
		return altStateCycleOrder;
	}

	/**
	 * Sets whether alternative state cycle order should be used.
	 */
	public void setAltStateCycleOrder( boolean altStateCycleOrder ) {
		this.altStateCycleOrder = altStateCycleOrder;
	}

	@Override
	protected void paintComponent( Graphics g ) {
		super.paintComponent( g );

		if( state == State.INDETERMINATE && !isIndeterminateStateSupported() )
			paintIndeterminateState( g );
	}

	/**
	 * Paints the indeterminate state if the current LaF does not support displaying
	 * the indeterminate state.
	 * The default implementation draws a magenta rectangle around the component.
	 */
	protected void paintIndeterminateState( Graphics g ) {
		g.setColor( Color.magenta );
		g.drawRect( 0, 0, getWidth() - 1, getHeight() - 1 );
	}

	/**
	 * Returns whether the current LaF supports displaying the indeterminate state.
	 * Returns {@code true} for FlatLaf and macOS Aqua.
	 */
	protected boolean isIndeterminateStateSupported() {
		LookAndFeel laf = UIManager.getLookAndFeel();
		return laf instanceof FlatLaf || laf.getClass().getName().equals( "com.apple.laf.AquaLookAndFeel" );
	}
}
