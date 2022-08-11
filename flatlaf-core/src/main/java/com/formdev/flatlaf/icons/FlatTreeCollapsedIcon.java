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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.function.Function;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.TreeUI;
import com.formdev.flatlaf.ui.FlatTreeUI;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * "collapsed" icon for {@link javax.swing.JTree}.
 *
 * @uiDefault Component.arrowType				String	chevron (default) or triangle
 * @uiDefault Tree.icon.collapsedColor			Color
 *
 * @author Karl Tauber
 */
public class FlatTreeCollapsedIcon
	extends FlatAbstractIcon
{
	private final boolean chevron;
	private Path2D path;

	public FlatTreeCollapsedIcon() {
		this( UIManager.getColor( "Tree.icon.collapsedColor" ) );
	}

	FlatTreeCollapsedIcon( Color color ) {
		super( 11, 11, color );
		chevron = FlatUIUtils.isChevron( UIManager.getString( "Component.arrowType" ) );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		setStyleColorFromTreeUI( c, g );
		rotate( c, g );

		String arrowType = getStyleFromTreeUI( c, ui -> ui.iconArrowType );
		boolean chevron = (arrowType != null) ? FlatUIUtils.isChevron( arrowType ) : this.chevron;

		if( chevron ) {
			// chevron arrow
			g.setStroke( new BasicStroke( 1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER ) );
			if( path == null )
				path = FlatUIUtils.createPath( false, 3.5,1.5, 7.5,5.5, 3.5,9.5 );
			g.draw( path );
		} else {
			// triangle arrow
			if( path == null )
				path = FlatUIUtils.createPath( 2,1, 2,10, 10,5.5 );
			g.fill( path );
		}
	}

	void setStyleColorFromTreeUI( Component c, Graphics2D g ) {
		setStyleColorFromTreeUI( c, g, ui -> ui.iconCollapsedColor );
	}

	void rotate( Component c, Graphics2D g ) {
		if( !c.getComponentOrientation().isLeftToRight() )
			g.rotate( Math.toRadians( 180 ), width / 2., height / 2. );
	}

	/**
	 * Because this icon is always shared for all trees,
	 * get icon specific style from FlatTreeUI.
	 */
	static <T> T getStyleFromTreeUI( Component c, Function<FlatTreeUI, T> f ) {
		JTree tree = (c instanceof JTree)
			? (JTree) c
			: (JTree) SwingUtilities.getAncestorOfClass( JTree.class, c );
		if( tree != null ) {
			TreeUI ui = tree.getUI();
			if( ui instanceof FlatTreeUI )
				return f.apply( (FlatTreeUI) ui );
		}
		return null;
	}

	static void setStyleColorFromTreeUI( Component c, Graphics2D g, Function<FlatTreeUI, Color> f ) {
		Color color = getStyleFromTreeUI( c, f );
		if( color != null )
			g.setColor( color );
	}
}
