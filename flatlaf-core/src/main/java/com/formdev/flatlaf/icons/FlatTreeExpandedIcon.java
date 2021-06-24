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

package com.formdev.flatlaf.icons;

import java.awt.Component;
import java.awt.Graphics2D;
import javax.swing.UIManager;

/**
 * "expanded" icon for {@link javax.swing.JTree}.
 *
 * @uiDefault Tree.icon.expandedColor			Color
 *
 * @author Karl Tauber
 */
public class FlatTreeExpandedIcon
	extends FlatTreeCollapsedIcon
{
	public FlatTreeExpandedIcon() {
		super( UIManager.getColor( "Tree.icon.expandedColor" ) );
	}

	@Override
	void setStyleColorFromTreeUI( Component c, Graphics2D g ) {
		setStyleColorFromTreeUI( c, g, ui -> ui.iconExpandedColor );
	}

	@Override
	void rotate( Component c, Graphics2D g ) {
		g.rotate( Math.toRadians( 90 ), width / 2., height / 2. );
	}
}
