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

package com.formdev.flatlaf.demo;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.MutableComboBoxModel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/**
 * @author Karl Tauber
 */
public class LookAndFeelsComboBox
	extends JComboBox<UIManager.LookAndFeelInfo>
{
	private final PropertyChangeListener lafListener = this::lafChanged;

	@SuppressWarnings( "unchecked" )
	public LookAndFeelsComboBox() {
		setRenderer( new BasicComboBoxRenderer() {
			@Override
			@SuppressWarnings( "rawtypes" )
			public Component getListCellRendererComponent( JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus )
			{
				value = (value != null)
					? ((LookAndFeelInfo)value).getName()
					: UIManager.getLookAndFeel().getName();
				return super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
			}
		} );
	}

	public void addLookAndFeel( String name, String className ) {
		getMutableModel().addElement( new LookAndFeelInfo( name, className ) );
	}

	public String getSelectedLookAndFeel() {
		Object sel = getSelectedItem();
		return (sel instanceof LookAndFeelInfo) ? ((LookAndFeelInfo)sel).getClassName() : null;
	}

	public void setSelectedLookAndFeel( String className ) {
		setSelectedIndex( getIndexOfLookAndFeel( className ) );
	}

	public void selectedCurrentLookAndFeel() {
		setSelectedLookAndFeel( UIManager.getLookAndFeel().getClass().getName() );
	}

	public void removeLookAndFeel( String className ) {
		int index = getIndexOfLookAndFeel( className );
		if( index >= 0 )
			getMutableModel().removeElementAt( index );
	}

	public int getIndexOfLookAndFeel( String className ) {
		ComboBoxModel<LookAndFeelInfo> model = getModel();
		int size = model.getSize();
		for( int i = 0; i < size; i++ ) {
			if( className.equals( model.getElementAt( i ).getClassName() ) )
				return i;
		}
		return -1;
	}

	private MutableComboBoxModel<LookAndFeelInfo> getMutableModel() {
		return (MutableComboBoxModel<LookAndFeelInfo>) getModel();
	}

	@Override
	public void addNotify() {
		super.addNotify();

		selectedCurrentLookAndFeel();
		UIManager.addPropertyChangeListener( lafListener );
	}

	@Override
	public void removeNotify() {
		super.removeNotify();

		UIManager.removePropertyChangeListener( lafListener );
	}

	void lafChanged( PropertyChangeEvent e ) {
		if( "lookAndFeel".equals( e.getPropertyName() ) )
			selectedCurrentLookAndFeel();
	}
}
