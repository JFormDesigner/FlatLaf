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

package com.formdev.flatlaf.ui;

import java.awt.Component;
import javax.swing.UIManager;

/**
 * Border for various components (e.g. {@link javax.swing.JComboBox}).
 *
 * @uiDefault Component.arc					int
 *
 * @author Karl Tauber
 */
public class FlatRoundBorder
	extends FlatBorder
{
	protected final int arc = UIManager.getInt( "Component.arc" );

	@Override
	protected int getArc( Component c ) {
		return FlatUIUtils.isRoundRect( c ) ? Short.MAX_VALUE : arc;
	}
}
