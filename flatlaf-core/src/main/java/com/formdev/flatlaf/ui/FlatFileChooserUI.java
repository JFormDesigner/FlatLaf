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

import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalFileChooserUI;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JFileChooser}.
 *
 * TODO document used UI defaults of superclass
 *
 * @author Karl Tauber
 */
public class FlatFileChooserUI
	extends MetalFileChooserUI
{
	public static ComponentUI createUI( JComponent c ) {
		return new FlatFileChooserUI( (JFileChooser) c );
	}

	public FlatFileChooserUI( JFileChooser filechooser ) {
		super( filechooser );
	}

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		return UIScale.scale( super.getPreferredSize( c ) );
	}

	@Override
	public Dimension getMinimumSize( JComponent c ) {
		return UIScale.scale( super.getMinimumSize( c ) );
	}
}
