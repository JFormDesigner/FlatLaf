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

import java.awt.Component;
import javax.swing.JTable;
import org.jdesktop.swingx.JXDatePicker;
import com.formdev.flatlaf.ui.FlatRoundBorder;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * Border for {@link org.jdesktop.swingx.JXDatePicker}.
 *
 * @author Karl Tauber
 */
public class FlatDatePickerBorder
	extends FlatRoundBorder
{
	@Override
	protected boolean isFocused( Component c ) {
		if( c instanceof JXDatePicker )
			return FlatUIUtils.isPermanentFocusOwner( ((JXDatePicker)c).getEditor() );

		return super.isFocused( c );
	}

	@Override
	protected boolean isCellEditor( Component c ) {
		return c.getParent() instanceof JTable;
	}
}
