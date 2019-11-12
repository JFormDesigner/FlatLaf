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
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.LookAndFeel;
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
	@SuppressWarnings( "unchecked" )
	public LookAndFeelsComboBox() {
		setRenderer( new BasicComboBoxRenderer() {
			@Override
			@SuppressWarnings( "rawtypes" )
			public Component getListCellRendererComponent( JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus )
			{
				value = ((LookAndFeelInfo)value).getName();
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
		int index = getIndexOfLookAndFeel( className );
		if( index >= 0 )
			setSelectedIndex( index );
	}

	public void selectedLookAndFeel( LookAndFeel lookAndFeel ) {
		String className = lookAndFeel.getClass().getName();
		int index = getIndexOfLookAndFeel( className );
		if( index < 0 ) {
			addLookAndFeel( lookAndFeel.getName(), className );
			index = getItemCount() - 1;
		}
		setSelectedIndex( index );
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
}
