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

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JSplitPane}.
 *
 * @uiDefault SplitPane.background						Color
 * @uiDefault SplitPane.foreground						Color unused
 * @uiDefault SplitPane.dividerSize						int
 * @uiDefault SplitPane.continuousLayout				boolean
 * @uiDefault SplitPane.border							Border
 * @uiDefault SplitPaneDivider.border					Border
 * @uiDefault SplitPaneDivider.draggingColor			Color	only used if continuousLayout is false
 *
 * @author Karl Tauber
 */
public class FlatSplitPaneUI
	extends BasicSplitPaneUI
{
	private Boolean continuousLayout;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatSplitPaneUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		continuousLayout = (Boolean) UIManager.get( "SplitPane.continuousLayout" );
	}

	@Override
	public boolean isContinuousLayout() {
		return super.isContinuousLayout() || (continuousLayout != null && Boolean.TRUE.equals( continuousLayout ));
	}

	@Override
	public BasicSplitPaneDivider createDefaultDivider() {
		return new FlatSplitPaneDivider( this );
	}

	//---- class FlatSplitPaneDivider -----------------------------------------

	private static class FlatSplitPaneDivider
		extends BasicSplitPaneDivider
	{
		public FlatSplitPaneDivider( BasicSplitPaneUI ui ) {
			super( ui );
		}

		@Override
		public void setDividerSize( int newSize ) {
			super.setDividerSize( UIScale.scale( newSize ) );
		}
	}
}
