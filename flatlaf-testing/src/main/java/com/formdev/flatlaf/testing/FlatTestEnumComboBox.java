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

package com.formdev.flatlaf.testing;

import java.awt.Component;
import java.util.Objects;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/**
 * @author Karl Tauber
 */
public class FlatTestEnumComboBox<E>
	extends JComboBox<E>
{
	@SuppressWarnings( "unchecked" )
	public void init( Class<E> enumType, boolean supportDefault ) {
		setRenderer( new EnumComboBoxRenderer() );
		setModel( new EnumComboBoxModel<>( enumType, supportDefault ) );
	}

	@SuppressWarnings( "unchecked" )
	public E getSelectedValue() {
		return (E) getSelectedItem();
	}

	@Override
	public int getSelectedIndex() {
		if( getSelectedItem() == null && getItemCount() > 0 && getItemAt( 0 ) == null )
			return 0;

		return super.getSelectedIndex();
	}

	//---- class EnumComboBoxRenderer -----------------------------------------

	public static class EnumComboBoxRenderer
		extends BasicComboBoxRenderer
	{
		@SuppressWarnings( "rawtypes" )
		@Override
		public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
			setEnabled( value != null || isSelected );
			if( value == null )
				value = "(default)";
			return super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
		}
	}

	//---- class EnumComboBoxModel --------------------------------------------

	public static class EnumComboBoxModel<E>
		extends AbstractListModel<E>
		implements ComboBoxModel<E>
	{
		private final boolean supportDefault;
		private final E[] values;
		private Object selectedItem;

		public EnumComboBoxModel( Class<E> enumType, boolean supportDefault ) {
			this.supportDefault = supportDefault;
			values = enumType.getEnumConstants();

			if( !supportDefault )
				selectedItem = values[0];
		}

		@Override
		public int getSize() {
			return values.length + (supportDefault ? 1 : 0);
		}

		@Override
		public E getElementAt( int index ) {
			if( supportDefault ) {
				if( index == 0 )
					return null;
				index--;
			}
			return values[index];
		}

		@Override
		public Object getSelectedItem() {
			return selectedItem;
		}

		@Override
		public void setSelectedItem( Object anItem ) {
			if( !Objects.equals( selectedItem, anItem ) ) {
				selectedItem = anItem;
				fireContentsChanged( this, -1, -1 );
			}
		}
	}
}
