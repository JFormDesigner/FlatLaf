/*
 * Copyright 2023 FormDev Software GmbH
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
import java.awt.Insets;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.util.UIScale;

/**
 * Border for {@link javax.swing.JScrollPane}.
 *
 * @uiDefault ScrollPane.arc				int
 * @uiDefault ScrollPane.List.arc			int
 * @uiDefault ScrollPane.Table.arc			int
 * @uiDefault ScrollPane.TextComponent.arc	int
 * @uiDefault ScrollPane.Tree.arc			int

 * @author Karl Tauber
 * @since 3.3
 */
public class FlatScrollPaneBorder
	extends FlatBorder
{
	@Styleable protected int arc = UIManager.getInt( "ScrollPane.arc" );

	private boolean isArcStyled;
	private final int listArc = FlatUIUtils.getUIInt( "ScrollPane.List.arc", -1 );
	private final int tableArc = FlatUIUtils.getUIInt( "ScrollPane.Table.arc", -1 );
	private final int textComponentArc = FlatUIUtils.getUIInt( "ScrollPane.TextComponent.arc", -1 );
	private final int treeArc = FlatUIUtils.getUIInt( "ScrollPane.Tree.arc", -1 );

	@Override
	public Object applyStyleProperty( String key, Object value ) {
		Object oldValue = super.applyStyleProperty( key, value );

		if( "arc".equals( key ) )
			isArcStyled = true;

		return oldValue;
	}

	@Override
	public Insets getBorderInsets( Component c, Insets insets ) {
		insets = super.getBorderInsets( c, insets );

		// if view is rounded, increase left and right insets to avoid that the viewport
		// is painted over the rounded border on the corners
		int padding = getLeftRightPadding( c );
		if( padding > 0 ) {
			insets.left += padding;
			insets.right += padding;
		}

		return insets;
	}

	@Override
	protected int getArc( Component c ) {
		if( isCellEditor( c ) )
			return 0;

		if( isArcStyled )
			return arc;

		if( c instanceof JScrollPane ) {
			Component view = FlatScrollPaneUI.getView( (JScrollPane) c );
			if( listArc >= 0 && view instanceof JList )
				return listArc;
			if( tableArc >= 0 && view instanceof JTable )
				return tableArc;
			if( textComponentArc >= 0&& view instanceof JTextComponent )
				return textComponentArc;
			if( treeArc >= 0 && view instanceof JTree )
				return treeArc;
		}

		return arc;
	}

	/**
	 * Returns the scaled left/right padding used when arc is larger than zero.
	 * <p>
	 * This is the distance from the inside of the left border to the left side of the view component.
	 * On the right side, this is the distance between the right side of the view component and
	 * the vertical scrollbar. Or the inside of the right border if the scrollbar is hidden.
	 */
	public int getLeftRightPadding( Component c ) {
		// Subtract lineWidth from radius because radius is given for the outside
		// of the painted line, but insets from super already include lineWidth.
		// Reduce padding by 10% to make padding slightly smaller because it is not recognizable
		// when the view is minimally painted over the beginning of the border curve.
		int arc = getArc( c );
		return (arc > 0)
			? Math.max( Math.round( UIScale.scale( ((arc / 2f) - getLineWidth( c )) * 0.9f ) ), 0 )
			: 0;
	}
}
