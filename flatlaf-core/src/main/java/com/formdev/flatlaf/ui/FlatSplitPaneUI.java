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

import java.awt.Color;
import java.awt.Cursor;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JSplitPane}.
 *
 * <!-- BasicSplitPaneUI -->
 *
 * @uiDefault SplitPane.background						Color
 * @uiDefault SplitPane.foreground						Color unused
 * @uiDefault SplitPane.dividerSize						int
 * @uiDefault SplitPane.border							Border
 * @uiDefault SplitPaneDivider.border					Border
 * @uiDefault SplitPaneDivider.draggingColor			Color	only used if continuousLayout is false
 *
 * <!-- FlatSplitPaneUI -->
 *
 * @uiDefault Component.arrowType						String	triangle (default) or chevron
 * @uiDefault SplitPane.continuousLayout				boolean
 * @uiDefault SplitPaneDivider.oneTouchArrowColor		Color
 * @uiDefault SplitPaneDivider.oneTouchHoverArrowColor	Color
 *
 * @author Karl Tauber
 */
public class FlatSplitPaneUI
	extends BasicSplitPaneUI
{
	protected String arrowType;
	private Boolean continuousLayout;
	private Color oneTouchArrowColor;
	private Color oneTouchHoverArrowColor;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatSplitPaneUI();
	}

	@Override
	protected void installDefaults() {
		arrowType = UIManager.getString( "Component.arrowType" );

		// get one-touch colors before invoking super.installDefaults() because they are
		// used in there on LaF switching
		oneTouchArrowColor = UIManager.getColor( "SplitPaneDivider.oneTouchArrowColor" );
		oneTouchHoverArrowColor = UIManager.getColor( "SplitPaneDivider.oneTouchHoverArrowColor" );

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

	private class FlatSplitPaneDivider
		extends BasicSplitPaneDivider
	{
		public FlatSplitPaneDivider( BasicSplitPaneUI ui ) {
			super( ui );
		}

		@Override
		public void setDividerSize( int newSize ) {
			super.setDividerSize( UIScale.scale( newSize ) );
		}

		@Override
		protected JButton createLeftOneTouchButton() {
			return new FlatOneTouchButton( true );
		}

		@Override
		protected JButton createRightOneTouchButton() {
			return new FlatOneTouchButton( false );
		}

		//---- class FlatOneTouchButton ---------------------------------------

		private class FlatOneTouchButton
			extends FlatArrowButton
		{
			private final boolean left;

			public FlatOneTouchButton( boolean left ) {
				super( SwingConstants.NORTH, arrowType, oneTouchArrowColor, null, oneTouchHoverArrowColor, null );
				setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );

				this.left = left;
			}

			@Override
			public int getDirection() {
				return (orientation == JSplitPane.VERTICAL_SPLIT)
					? (left ? SwingConstants.NORTH : SwingConstants.SOUTH)
					: (left ? SwingConstants.WEST : SwingConstants.EAST);
			}
		}
	}

}
