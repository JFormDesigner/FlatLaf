/*
 * Copyright 2023 FormDev Software GmbH
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

package com.formdev.flatlaf.testing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Beans;
import java.util.Objects;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import com.formdev.flatlaf.FlatClientProperties;

/**
 * @author Karl Tauber
 */
public class FlatTestEnumSelector<E>
	extends JPanel
{
	private final JToolBar toolBar;

	public FlatTestEnumSelector() {
		super( new BorderLayout() );

		toolBar = new JToolBar();
		toolBar.putClientProperty( FlatClientProperties.STYLE, "border: 1,1,1,1,$Component.borderColor,1,8; separatorWidth: 1" );
		add( toolBar );

		// for JFormDesigner
		if( Beans.isDesignTime() )
			toolBar.add( new JToggleButton( "(default)", true ) );
	}

	public void init( Class<E> enumType, boolean supportDefault ) {
		ButtonGroup group = new ButtonGroup();

		// create "default" button
		if( supportDefault )
			toolBar.add( createButton( "(default)", null, group ) );

		// create value buttons
		for( E value : enumType.getEnumConstants() ) {
			if( getComponentCount() > 0 )
				toolBar.addSeparator();

			toolBar.add( createButton( value.toString(), value, group ) );
		}

		// select first button in group
		group.getElements().nextElement().setSelected( true );
	}

	private JToggleButton createButton( String text, E value, ButtonGroup group ) {
		JToggleButton button = new JToggleButton( text );
		button.putClientProperty( "FlatTestEnumSelector.value", value );
		button.putClientProperty( FlatClientProperties.STYLE, "toolbar.spacingInsets: 0,0,0,0" );
		button.setEnabled( isEnabled() );
		button.addActionListener( e -> fireActionPerformed() );
		group.add( button );
		return button;
	}

	@SuppressWarnings( "unchecked" )
	public E getSelectedValue() {
		for( Component c : toolBar.getComponents() ) {
			if( c instanceof JToggleButton && ((JToggleButton)c).isSelected() )
				return (E) ((JToggleButton)c).getClientProperty( "FlatTestEnumSelector.value" );
		}
		return null;
	}

	public void setSelectedValue( E value ) {
		for( Component c : toolBar.getComponents() ) {
			if( c instanceof JToggleButton &&
				Objects.equals( value, ((JToggleButton)c).getClientProperty( "FlatTestEnumSelector.value" ) ) )
			{
				((JToggleButton)c).setSelected( true );
				return;
			}
		}
	}

	public void addActionListener( ActionListener l ) {
		listenerList.add( ActionListener.class, l );
	}

	public void removeActionListener( ActionListener l ) {
		listenerList.remove( ActionListener.class, l );
	}

	private void fireActionPerformed() {
		ActionEvent e = new ActionEvent( this, ActionEvent.ACTION_PERFORMED, null );
		Object[] listeners = listenerList.getListenerList();
		for( int i = listeners.length - 2; i >= 0; i -= 2 ) {
			if( listeners[i] == ActionListener.class )
				((ActionListener)listeners[i+1]).actionPerformed( e );
		}
	}

	@Override
	public void setEnabled( boolean enabled ) {
		super.setEnabled( enabled );

		for( Component c : toolBar.getComponents() )
			c.setEnabled( enabled );
	}
}
