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

import java.awt.Color;
import java.awt.Insets;
import java.util.Calendar;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.plaf.basic.BasicMonthViewUI;
import org.jdesktop.swingx.plaf.basic.CalendarRenderingHandler;
import org.jdesktop.swingx.plaf.basic.CalendarState;
import com.formdev.flatlaf.ui.FlatEmptyBorder;
import com.formdev.flatlaf.ui.FlatLineBorder;

/**
 * Provides the Flat LaF UI delegate for {@link org.jdesktop.swingx.JXMonthView}.
 *
 * @author Karl Tauber
 */
public class FlatMonthViewUI
	extends BasicMonthViewUI
{
	public static ComponentUI createUI( JComponent c ) {
		return new FlatMonthViewUI();
	}

	@Override
	protected CalendarRenderingHandler createRenderingHandler() {
		return new FlatRenderingHandler();
	}

	//---- class FlatRenderingHandler -----------------------------------------

	private static class FlatRenderingHandler
		extends RenderingHandler
	{
		private final Color todayColor = UIManager.getColor( "JXMonthView.todayColor" );

		@Override
		public JComponent prepareRenderingComponent( JXMonthView monthView, Calendar calendar,
			CalendarState dayState )
		{
			JComponent c = super.prepareRenderingComponent( monthView, calendar, dayState );

			int px = monthView.getBoxPaddingX();
			int py = monthView.getBoxPaddingY();

			// scale borders
			Border border = null;
			if( dayState == CalendarState.TITLE &&  monthView.isTraversable() ) {
				Border b = c.getBorder();
				if( b instanceof CompoundBorder && ((CompoundBorder)b).getInsideBorder() instanceof EmptyBorder )
					border = new CompoundBorder( ((CompoundBorder)b).getOutsideBorder(), new FlatEmptyBorder( py * 2, 0, py * 2, 0 ) );
			} else if( dayState == CalendarState.TODAY ) {
				Color lineColor = monthView.getTodayBackground();
				if( lineColor == null )
					lineColor = todayColor;
				border = new FlatLineBorder( new Insets( py, px, py, px ), lineColor );
			}

			if( border == null )
				border = new FlatEmptyBorder( py, px, py, px );
			c.setBorder( border );

			return c;
		}
	}
}
