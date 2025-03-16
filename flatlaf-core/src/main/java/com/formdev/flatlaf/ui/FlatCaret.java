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
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.Utilities;

/**
 * Caret that can select all text on focus gained.
 * Also fixes Swing's double-click-and-drag behavior so that dragging after
 * a double-click extends selection by whole words.
 *
 * @author Karl Tauber
 */
public class FlatCaret
	extends DefaultCaret
	implements UIResource
{
	private static final String KEY_CARET_INFO = "FlatLaf.internal.caretInfo";

	// selectAllOnFocusPolicy
	private static final int NEVER = 0, ONCE = 1, ALWAYS = 2;

	private final String selectAllOnFocusPolicy;
	private final boolean selectAllOnMouseClick;

	private boolean inInstall;
	private boolean wasFocused;
	private boolean wasFocusTemporaryLost;
	private boolean isMousePressed;
	private boolean isWordSelection;
	private boolean isLineSelection;
	private int dragSelectionStart;
	private int dragSelectionEnd;

	public FlatCaret( String selectAllOnFocusPolicy, boolean selectAllOnMouseClick ) {
		this.selectAllOnFocusPolicy = selectAllOnFocusPolicy;
		this.selectAllOnMouseClick = selectAllOnMouseClick;
	}

	@Override
	public void install( JTextComponent c ) {
		// get caret info if switched theme
		long[] ci = (long[]) c.getClientProperty( KEY_CARET_INFO );
		if( ci != null ) {
			c.putClientProperty( KEY_CARET_INFO, null );

			// if caret info is too old assume that switched from FlatLaf
			// to another Laf and back to FlatLaf
			if( System.currentTimeMillis() - 500 > ci[3] )
				ci = null;
		}
		if( ci != null ) {
			// when switching theme, it is necessary to set blink rate before
			// invoking super.install() otherwise the caret does not blink
			setBlinkRate( (int) ci[2] );
		}

		inInstall = true;
		try {
			super.install( c );
		} finally {
			inInstall = false;
		}

		if( ci != null ) {
			// restore selection
			select( (int) ci[1], (int) ci[0] );

			if( ci[4] != 0 )
				wasFocused = true;

			// if text component is focused, then caret and selection are visible,
			// but when switching theme, the component does not yet have
			// a highlighter and the selection is not painted
			// --> make selection temporary invisible later, then the caret
			//     adds selection highlights to the text component highlighter
			if( isSelectionVisible() ) {
				EventQueue.invokeLater( () -> {
					if( getComponent() == null )
						return; // was deinstalled

					if( isSelectionVisible() ) {
						setSelectionVisible( false );
						setSelectionVisible( true );
					}
				} );
			}
		}
	}

	@Override
	public void deinstall( JTextComponent c ) {
		// remember dot and mark (the selection) when switching theme
		c.putClientProperty( KEY_CARET_INFO, new long[] {
			getDot(),
			getMark(),
			getBlinkRate(),
			System.currentTimeMillis(),
			wasFocused ? 1 : 0,
		} );

		super.deinstall( c );
	}

	@Override
	protected void adjustVisibility( Rectangle nloc ) {
		JTextComponent c = getComponent();
		if( c != null && c.getUI() instanceof FlatTextFieldUI ) {
			// need to fix x location because JTextField.scrollRectToVisible() uses insets.left
			// (as BasicTextUI.getVisibleEditorRect() does),
			// but FlatTextFieldUI.getVisibleEditorRect() may add some padding
			Rectangle r = ((FlatTextFieldUI)c.getUI()).getVisibleEditorRect();
			if( r != null )
				nloc.x -= r.x - c.getInsets().left;
		}
		super.adjustVisibility( nloc );
	}

	@Override
	public void setDot( int dot ) {
		super.setDot( dot );

		// mark as focused if invoked from JTextComponent.setCaretPosition()
		// to disable SELECT_ALL_ON_FOCUS_POLICY_ONCE if application explicitly changes selection
		if( !wasFocused &&
			getSelectAllOnFocusPolicy() == ONCE &&
			StackUtils.wasInvokedFrom( JTextComponent.class.getName(), "setCaretPosition", 6 ) )
		  wasFocused = true;
	}

	@Override
	public void moveDot( int dot ) {
		super.moveDot( dot );

		// mark as focused if invoked from JTextComponent.moveCaretPosition()
		// to disable SELECT_ALL_ON_FOCUS_POLICY_ONCE if application explicitly changes selection
		if( !wasFocused &&
			getSelectAllOnFocusPolicy() == ONCE &&
			StackUtils.wasInvokedFrom( JTextComponent.class.getName(), "moveCaretPosition", 6 ) )
		  wasFocused = true;
	}

	@Override
	public void focusGained( FocusEvent e ) {
		if( !inInstall && !wasFocusTemporaryLost && (!isMousePressed || isSelectAllOnMouseClick()) )
			selectAllOnFocusGained();

		wasFocusTemporaryLost = false;
		wasFocused = true;

		super.focusGained( e );
	}

	@Override
	public void focusLost( FocusEvent e ) {
		wasFocusTemporaryLost = e.isTemporary();
		super.focusLost( e );
	}

	@Override
	public void mousePressed( MouseEvent e ) {
		isMousePressed = true;
		super.mousePressed( e );

		JTextComponent c = getComponent();

		// left double-click starts word selection
		isWordSelection = e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton( e ) && !e.isConsumed();

		// left triple-click starts line selection
		isLineSelection = e.getClickCount() == 3 && SwingUtilities.isLeftMouseButton( e ) && (!e.isConsumed() || c.getDragEnabled());

		// select line
		// (this is also done in DefaultCaret.mouseClicked(), but this event is
		// sent when the mouse is released, which is too late for triple-click-and-drag)
		if( isLineSelection ) {
			ActionMap actionMap = c.getActionMap();
			Action selectLineAction = (actionMap != null)
				? actionMap.get( DefaultEditorKit.selectLineAction )
				: null;
			if( selectLineAction != null ) {
				selectLineAction.actionPerformed( new ActionEvent( c,
					ActionEvent.ACTION_PERFORMED, null, e.getWhen(), e.getModifiers() ) );
			}
		}

		// remember selection where word/line selection starts to keep it always selected while dragging
		if( isWordSelection || isLineSelection ) {
			int mark = getMark();
			int dot = getDot();
			dragSelectionStart = Math.min( dot, mark );
			dragSelectionEnd = Math.max( dot, mark );
		}
	}

	@Override
	public void mouseReleased( MouseEvent e ) {
		isMousePressed = false;
		isWordSelection = false;
		isLineSelection = false;
		super.mouseReleased( e );
	}

	@Override
	public void mouseDragged( MouseEvent e ) {
		if( (isWordSelection || isLineSelection) &&
			!e.isConsumed() && SwingUtilities.isLeftMouseButton( e ) )
		{
			// fix Swing's double/triple-click-and-drag behavior so that dragging after
			// a double/triple-click extends selection by whole words/lines
			JTextComponent c = getComponent();
			int pos = c.viewToModel( e.getPoint() );
			if( pos < 0 )
				return;

			try {
				if( pos > dragSelectionEnd )
					select( dragSelectionStart, isWordSelection ? Utilities.getWordEnd( c, pos ) : Utilities.getRowEnd( c, pos ) );
				else if( pos < dragSelectionStart )
					select( dragSelectionEnd, isWordSelection ? Utilities.getWordStart( c, pos ) : Utilities.getRowStart( c, pos ) );
				else
					select( dragSelectionStart, dragSelectionEnd );
			} catch( BadLocationException ex ) {
				UIManager.getLookAndFeel().provideErrorFeedback( c );
			}
		} else
			super.mouseDragged( e );
	}

	protected void selectAllOnFocusGained() {
		JTextComponent c = getComponent();
		Document doc = c.getDocument();
		if( doc == null || !c.isEnabled() || !c.isEditable() || FlatUIUtils.isCellEditor( c ) )
			return;

		int selectAllOnFocusPolicy = getSelectAllOnFocusPolicy();
		if( selectAllOnFocusPolicy == NEVER )
			return;

		if( selectAllOnFocusPolicy == ONCE && !isMousePressed ) {
			// was already focused?
			if( wasFocused && !(c instanceof JFormattedTextField) )
				return;
		}

		// select all
		if( c instanceof JFormattedTextField ) {
			EventQueue.invokeLater( () -> {
				// Warning: do not use variables from outside of this runnable
				//          because they may be out-of-date when this runnable is executed

				JTextComponent c2 = getComponent();
				if( c2 == null )
					return; // was deinstalled

				select( 0, c2.getDocument().getLength() );
			} );
		} else
			select( 0, doc.getLength() );
	}

	private void select( int mark, int dot ) {
		if( mark != getMark() )
			setDot( mark, Position.Bias.Forward );
		if( dot != getDot() )
			moveDot( dot, Position.Bias.Forward );
	}

	private int getSelectAllOnFocusPolicy() {
		Object value = getClientProperty( SELECT_ALL_ON_FOCUS_POLICY );
		// Note: using String.valueOf() because selectAllOnFocusPolicy may be null
		switch( String.valueOf( value instanceof String ? value : selectAllOnFocusPolicy ) ) {
			default:
			case SELECT_ALL_ON_FOCUS_POLICY_NEVER:  return NEVER;
			case SELECT_ALL_ON_FOCUS_POLICY_ONCE:   return ONCE;
			case SELECT_ALL_ON_FOCUS_POLICY_ALWAYS: return ALWAYS;
		}
	}

	private boolean isSelectAllOnMouseClick() {
		Object value = getClientProperty( SELECT_ALL_ON_MOUSE_CLICK );
		return (value instanceof Boolean) ? (boolean) value : selectAllOnMouseClick;
	}

	private Object getClientProperty( String key ) {
		JTextComponent c = getComponent();
		if( c == null )
			return null;

		Object value = c.getClientProperty( key );
		if( value != null )
			return value;

		Container parent = c.getParent();
		if( parent instanceof JComboBox )
			return ((JComboBox<?>)parent).getClientProperty( key );
		if( parent instanceof JSpinner.DefaultEditor ) {
			parent = parent.getParent();
			if( parent instanceof JSpinner )
				return ((JSpinner)parent).getClientProperty( key );
		}
		return null;
	}

	/** @since 1.4 */
	public void scrollCaretToVisible() {
		JTextComponent c = getComponent();
		if( c == null || c.getUI() == null )
			return;

		try {
			Rectangle loc = c.getUI().modelToView( c, getDot(), getDotBias() );
			if( loc != null ) {
				adjustVisibility( loc );
				damage( loc );
			}
		} catch( BadLocationException ex ) {
			// ignore
		}
	}
}
