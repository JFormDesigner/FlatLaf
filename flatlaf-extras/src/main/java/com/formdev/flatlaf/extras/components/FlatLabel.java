/*
 * Copyright 2021 FormDev Software GmbH
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

package com.formdev.flatlaf.extras.components;

import static com.formdev.flatlaf.FlatClientProperties.STYLE_CLASS;
import javax.swing.JLabel;

/**
 * Subclass of {@link JLabel} that provides easy access to FlatLaf specific client properties.
 *
 * @author Karl Tauber
 * @since 2
 */
public class FlatLabel
	extends JLabel
	implements FlatComponentExtension, FlatStyleableComponent
{
	// NOTE: enum names must be equal to typography/font styles
	public enum LabelType { h00, h0, h1, h2, h3, h4, large, regular, medium, small, mini, monospaced }

	/**
	 * Returns type of the label.
	 */
	public LabelType getLabelType() {
		return getClientPropertyEnumString( STYLE_CLASS, LabelType.class, null, LabelType.regular );
	}

	/**
	 * Specifies type of the label.
	 */
	public void setLabelType( LabelType labelType ) {
		if( labelType == LabelType.regular )
			labelType = null;
		putClientPropertyEnumString( STYLE_CLASS, labelType );
	}
}
