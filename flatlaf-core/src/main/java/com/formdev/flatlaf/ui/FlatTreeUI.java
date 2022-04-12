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

import static com.formdev.flatlaf.FlatClientProperties.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.util.Map;
import javax.swing.CellRendererPane;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JTree.DropLocation;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JTree}.
 *
 * <!-- BasicTreeUI -->
 *
 * @uiDefault Tree.font								Font
 * @uiDefault Tree.background						Color
 * @uiDefault Tree.foreground						Color	unused
 * @uiDefault Tree.hash								Color
 * @uiDefault Tree.dropLineColor					Color
 * @uiDefault Tree.expandedIcon						Icon
 * @uiDefault Tree.collapsedIcon					Icon
 * @uiDefault Tree.leftChildIndent					int
 * @uiDefault Tree.rightChildIndent					int
 * @uiDefault Tree.rowHeight						int
 * @uiDefault Tree.scrollsOnExpand					boolean
 * @uiDefault Tree.scrollsHorizontallyAndVertically	boolean
 * @uiDefault Tree.paintLines						boolean
 * @uiDefault Tree.lineTypeDashed					boolean
 * @uiDefault Tree.showsRootHandles					boolean
 * @uiDefault Tree.repaintWholeRow					boolean
 *
 * <!-- DefaultTreeCellRenderer -->
 *
 * @uiDefault Tree.leafIcon							Icon
 * @uiDefault Tree.closedIcon						Icon
 * @uiDefault Tree.openIcon							Icon
 * @uiDefault Tree.textBackground					Color
 * @uiDefault Tree.textForeground					Color
 * @uiDefault Tree.selectionBackground				Color
 * @uiDefault Tree.selectionForeground				Color
 * @uiDefault Tree.selectionBorderColor				Color	focus indicator border color
 * @uiDefault Tree.drawsFocusBorderAroundIcon		boolean
 * @uiDefault Tree.drawDashedFocusIndicator			boolean
 * @uiDefault Tree.rendererFillBackground			boolean	default is true
 * @uiDefault Tree.rendererMargins					Insets
 * @uiDefault Tree.dropCellBackground				Color
 * @uiDefault Tree.dropCellForeground				Color
 *
 * <!-- DefaultTreeCellEditor -->
 *
 * @uiDefault Tree.editorBorder						Border
 * @uiDefault Tree.editorBorderSelectionColor		Color
 *
 * <!-- FlatTreeUI -->
 *
 * @uiDefault Tree.border							Border
 * @uiDefault Tree.selectionBackground				Color
 * @uiDefault Tree.selectionForeground				Color
 * @uiDefault Tree.selectionInactiveBackground		Color
 * @uiDefault Tree.selectionInactiveForeground		Color
 * @uiDefault Tree.wideSelection					boolean
 * @uiDefault Tree.showCellFocusIndicator			boolean
 *
 * <!-- FlatTreeExpandedIcon -->
 *
 * @uiDefault Component.arrowType					String	chevron (default) or triangle
 * @uiDefault Tree.icon.expandedColor				Color
 *
 * <!-- FlatTreeCollapsedIcon -->
 *
 * @uiDefault Component.arrowType					String	chevron (default) or triangle
 * @uiDefault Tree.icon.collapsedColor				Color
 *
 * <!-- FlatTreeLeafIcon -->
 *
 * @uiDefault Tree.icon.leafColor					Color
 *
 * <!-- FlatTreeClosedIcon -->
 *
 * @uiDefault Tree.icon.closedColor					Color
 *
 * <!-- FlatTreeOpenIcon -->
 *
 * @uiDefault Tree.icon.openColor					Color
 *
 * @author Karl Tauber
 */
public class FlatTreeUI
	extends BasicTreeUI
	implements StyleableUI
{
	@Styleable protected Color selectionBackground;
	@Styleable protected Color selectionForeground;
	@Styleable protected Color selectionInactiveBackground;
	@Styleable protected Color selectionInactiveForeground;
	@Styleable protected Color selectionBorderColor;
	@Styleable protected boolean wideSelection;
	@Styleable protected boolean showCellFocusIndicator;

	// for icons
	// (needs to be public because icon classes are in another package)
	/** @since 2 */ @Styleable(dot=true) public String iconArrowType;
	/** @since 2 */ @Styleable(dot=true) public Color iconExpandedColor;
	/** @since 2 */ @Styleable(dot=true) public Color iconCollapsedColor;
	/** @since 2 */ @Styleable(dot=true) public Color iconLeafColor;
	/** @since 2 */ @Styleable(dot=true) public Color iconClosedColor;
	/** @since 2 */ @Styleable(dot=true) public Color iconOpenColor;

	// only used via styling (not in UI defaults, but has likewise client properties)
	/** @since 2 */ @Styleable protected boolean paintSelection = true;

	private Color defaultCellNonSelectionBackground;
	private Color defaultSelectionBackground;
	private Color defaultSelectionForeground;
	private Color defaultSelectionBorderColor;
	private Map<String, Object> oldStyleValues;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatTreeUI();
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		installStyle();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		LookAndFeel.installBorder( tree, "Tree.border" );

		selectionBackground = UIManager.getColor( "Tree.selectionBackground" );
		selectionForeground = UIManager.getColor( "Tree.selectionForeground" );
		selectionInactiveBackground = UIManager.getColor( "Tree.selectionInactiveBackground" );
		selectionInactiveForeground = UIManager.getColor( "Tree.selectionInactiveForeground" );
		selectionBorderColor = UIManager.getColor( "Tree.selectionBorderColor" );
		wideSelection = UIManager.getBoolean( "Tree.wideSelection" );
		showCellFocusIndicator = UIManager.getBoolean( "Tree.showCellFocusIndicator" );

		defaultCellNonSelectionBackground = UIManager.getColor( "Tree.textBackground" );
		defaultSelectionBackground = selectionBackground;
		defaultSelectionForeground = selectionForeground;
		defaultSelectionBorderColor = selectionBorderColor;

		// scale
		int rowHeight = FlatUIUtils.getUIInt( "Tree.rowHeight", 16 );
		if( rowHeight > 0 )
			LookAndFeel.installProperty( tree, "rowHeight", UIScale.scale( rowHeight ) );
		setLeftChildIndent( UIScale.scale( getLeftChildIndent() ) );
		setRightChildIndent( UIScale.scale( getRightChildIndent() ) );
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		LookAndFeel.uninstallBorder( tree );

		selectionBackground = null;
		selectionForeground = null;
		selectionInactiveBackground = null;
		selectionInactiveForeground = null;
		selectionBorderColor = null;

		defaultCellNonSelectionBackground = null;
		defaultSelectionBackground = null;
		defaultSelectionForeground = null;
		defaultSelectionBorderColor = null;
		oldStyleValues = null;
	}

	@Override
	protected MouseListener createMouseListener() {
		return new BasicTreeUI.MouseHandler() {
			@Override
			public void mousePressed( MouseEvent e ) {
				super.mousePressed( handleWideMouseEvent( e ) );
			}

			@Override
			public void mouseReleased( MouseEvent e ) {
				super.mouseReleased( handleWideMouseEvent( e ) );
			}

			@Override
			public void mouseDragged( MouseEvent e ) {
				super.mouseDragged( handleWideMouseEvent( e ) );
			}

			private MouseEvent handleWideMouseEvent( MouseEvent e ) {
				if( !isWideSelection() || !tree.isEnabled() || !SwingUtilities.isLeftMouseButton( e ) || e.isConsumed() )
					return e;

				int x = e.getX();
				int y = e.getY();
				TreePath path = getClosestPathForLocation( tree, x, y );
				if( path == null || isLocationInExpandControl( path, x, y ) )
					return e;

				Rectangle bounds = getPathBounds( tree, path );
				if( bounds == null || y < bounds.y || y >= (bounds.y + bounds.height) )
					return e;

				int newX = Math.max( bounds.x, Math.min( x, bounds.x + bounds.width - 1 ) );
				if( newX == x )
					return e;

				// clone mouse event, but with new X coordinate
				return new MouseEvent( e.getComponent(), e.getID(), e.getWhen(),
					e.getModifiers() | e.getModifiersEx(), newX, e.getY(),
					e.getClickCount(), e.isPopupTrigger(), e.getButton() );
			}
		};
	}

	@Override
	protected PropertyChangeListener createPropertyChangeListener() {
		PropertyChangeListener superListener = super.createPropertyChangeListener();
		return e -> {
			superListener.propertyChange( e );

			if( e.getSource() == tree ) {
				switch( e.getPropertyName() ) {
					case TREE_WIDE_SELECTION:
					case TREE_PAINT_SELECTION:
						tree.repaint();
						break;

					case "dropLocation":
						if( isWideSelection() ) {
							JTree.DropLocation oldValue = (JTree.DropLocation) e.getOldValue();
							repaintWideDropLocation( oldValue );
							repaintWideDropLocation( tree.getDropLocation() );
						}
						break;

					case STYLE:
					case STYLE_CLASS:
						installStyle();
						tree.revalidate();
						tree.repaint();
						break;
				}
			}
		};
	}

	private void repaintWideDropLocation(JTree.DropLocation loc) {
		if( loc == null || isDropLine( loc ) )
			return;

		Rectangle r = tree.getPathBounds( loc.getPath() );
		if( r != null )
			tree.repaint( 0, r.y, tree.getWidth(), r.height );
	}

	@Override
	public Rectangle getPathBounds( JTree tree, TreePath path ) {
		Rectangle bounds = super.getPathBounds( tree, path );

		// If this method was invoked from JTree.getPathForLocation(int x, int y) to check whether
		// the location is within tree node bounds, then return the bounds of a wide node.
		// This changes the behavior of JTree.getPathForLocation(int x, int y) and
		// JTree.getRowForLocation(int x, int y), which now return the path/row even
		// if [x,y] is in the wide row area outside of the actual tree node.
		if( bounds != null &&
			isWideSelection() &&
			UIManager.getBoolean( "FlatLaf.experimental.tree.widePathForLocation" ) &&
			StackUtils.wasInvokedFrom( JTree.class.getName(), "getPathForLocation", 5 ) )
		{
			bounds.x = 0;
			bounds.width = tree.getWidth();
		}
		return bounds;
	}

	/** @since 2 */
	protected void installStyle() {
		try {
			applyStyle( FlatStylingSupport.getResolvedStyle( tree, "Tree" ) );
		} catch( RuntimeException ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	/** @since 2 */
	protected void applyStyle( Object style ) {
		oldStyleValues = FlatStylingSupport.parseAndApply( oldStyleValues, style, this::applyStyleProperty );
	}

	/** @since 2 */
	protected Object applyStyleProperty( String key, Object value ) {
		return FlatStylingSupport.applyToAnnotatedObjectOrComponent( this, tree, key, value );
	}

	/** @since 2 */
	@Override
	public Map<String, Class<?>> getStyleableInfos( JComponent c ) {
		return FlatStylingSupport.getAnnotatedStyleableInfos( this );
	}

	/**
	 * Same as super.paintRow(), but supports wide selection and uses
	 * inactive selection background/foreground if tree is not focused.
	 */
	@Override
	protected void paintRow( Graphics g, Rectangle clipBounds, Insets insets, Rectangle bounds,
		TreePath path, int row, boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf )
	{
		boolean isEditing = (editingComponent != null && editingRow == row);
		boolean isSelected = tree.isRowSelected( row );
		boolean isDropRow = isDropRow( row );
		boolean needsSelectionPainting = (isSelected || isDropRow) && isPaintSelection();

		// do not paint row if editing
		if( isEditing ) {
			// paint wide selection
			// (do not access cell renderer here to avoid side effect
			// if renderer component is also used as editor component)
			if( isSelected && isWideSelection() ) {
				Color oldColor = g.getColor();
				g.setColor( selectionInactiveBackground );
				paintWideSelection( g, clipBounds, insets, bounds, path, row, isExpanded, hasBeenExpanded, isLeaf );
				g.setColor( oldColor );
			}
			return;
		}

		boolean hasFocus = FlatUIUtils.isPermanentFocusOwner( tree );
		boolean cellHasFocus = hasFocus && (row == getLeadSelectionRow());

		// if tree is used as cell renderer in another component (e.g. in Rhino JavaScript debugger),
		// check whether that component is focused to get correct selection colors
		if( !hasFocus && isSelected && tree.getParent() instanceof CellRendererPane )
			hasFocus = FlatUIUtils.isPermanentFocusOwner( tree.getParent().getParent() );

		// get renderer component
		Component rendererComponent = currentCellRenderer.getTreeCellRendererComponent( tree,
			path.getLastPathComponent(), isSelected, isExpanded, isLeaf, row, cellHasFocus );

		// renderer background/foreground
		Color oldBackgroundSelectionColor = null;
		if( isSelected && !hasFocus && !isDropRow ) {
			// apply inactive selection background/foreground if tree is not focused
			oldBackgroundSelectionColor = setRendererBackgroundSelectionColor( rendererComponent, selectionInactiveBackground );
			setRendererForeground( rendererComponent, selectionInactiveForeground );

		} else if( isSelected ) {
			// update background/foreground if set via style
			if( selectionBackground != defaultSelectionBackground )
				oldBackgroundSelectionColor = setRendererBackgroundSelectionColor( rendererComponent, selectionBackground );
			if( selectionForeground != defaultSelectionForeground )
				setRendererForeground( rendererComponent, selectionForeground );
		}

		// update focus selection border
		Color oldBorderSelectionColor = null;
		if( isSelected && hasFocus &&
			(!showCellFocusIndicator || tree.getMinSelectionRow() == tree.getMaxSelectionRow()) )
		{
			// remove focus selection border if exactly one item is selected or if showCellFocusIndicator is false
			oldBorderSelectionColor = setRendererBorderSelectionColor( rendererComponent, null );

		} else if( hasFocus && selectionBorderColor != defaultSelectionBorderColor ) {
			// update focus selection border if set via style
			oldBorderSelectionColor = setRendererBorderSelectionColor( rendererComponent, selectionBorderColor );
		}

		// paint selection background
		if( needsSelectionPainting ) {
			// set selection color
			Color oldColor = g.getColor();
			g.setColor( isDropRow
				? UIManager.getColor( "Tree.dropCellBackground" )
				: (rendererComponent instanceof DefaultTreeCellRenderer
					? ((DefaultTreeCellRenderer)rendererComponent).getBackgroundSelectionColor()
					: (hasFocus ? selectionBackground : selectionInactiveBackground)) );

			if( isWideSelection() ) {
				// wide selection
				paintWideSelection( g, clipBounds, insets, bounds, path, row, isExpanded, hasBeenExpanded, isLeaf );
			} else {
				// non-wide selection
				paintCellBackground( g, rendererComponent, bounds );
			}

			// this is actually not necessary because renderer should always set color
			// before painting, but doing anyway to avoid any side effect (in bad renderers)
			g.setColor( oldColor );
		} else {
			// paint cell background if DefaultTreeCellRenderer.getBackgroundNonSelectionColor() is set
			if( rendererComponent instanceof DefaultTreeCellRenderer ) {
				DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) rendererComponent;
				Color bg = renderer.getBackgroundNonSelectionColor();
				if( bg != null && !bg.equals( defaultCellNonSelectionBackground ) ) {
					Color oldColor = g.getColor();
					g.setColor( bg );
					paintCellBackground( g, rendererComponent, bounds );
					g.setColor( oldColor );
				}
			}
		}

		// paint renderer
		rendererPane.paintComponent( g, rendererComponent, tree, bounds.x, bounds.y, bounds.width, bounds.height, true );

		// restore background selection color and border selection color
		if( oldBackgroundSelectionColor != null )
			((DefaultTreeCellRenderer)rendererComponent).setBackgroundSelectionColor( oldBackgroundSelectionColor );
		if( oldBorderSelectionColor != null )
			((DefaultTreeCellRenderer)rendererComponent).setBorderSelectionColor( oldBorderSelectionColor );
	}

	private Color setRendererBackgroundSelectionColor( Component rendererComponent, Color color ) {
		Color oldColor = null;

		if( rendererComponent instanceof DefaultTreeCellRenderer ) {
			DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) rendererComponent;
			if( renderer.getBackgroundSelectionColor() == defaultSelectionBackground ) {
				oldColor = renderer.getBackgroundSelectionColor();
				renderer.setBackgroundSelectionColor( color );
			}
		} else {
			if( rendererComponent.getBackground() == defaultSelectionBackground )
				rendererComponent.setBackground( color );
		}

		return oldColor;
	}

	private void setRendererForeground( Component rendererComponent, Color color ) {
		if( rendererComponent.getForeground() == defaultSelectionForeground )
			rendererComponent.setForeground( color );
	}

	private Color setRendererBorderSelectionColor( Component rendererComponent, Color color ) {
		Color oldColor = null;

		if( rendererComponent instanceof DefaultTreeCellRenderer ) {
			DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) rendererComponent;
			if( renderer.getBorderSelectionColor() == defaultSelectionBorderColor ) {
				oldColor = renderer.getBorderSelectionColor();
				renderer.setBorderSelectionColor( color );
			}
		}

		return oldColor;
	}

	private void paintWideSelection( Graphics g, Rectangle clipBounds, Insets insets, Rectangle bounds,
		TreePath path, int row, boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf )
	{
		g.fillRect( 0, bounds.y, tree.getWidth(), bounds.height );

		// paint expand/collapse icon
		// (was already painted before, but painted over with wide selection)
		if( shouldPaintExpandControl( path, row, isExpanded, hasBeenExpanded, isLeaf ) ) {
			paintExpandControl( g, clipBounds, insets, bounds,
				path, row, isExpanded, hasBeenExpanded, isLeaf );
		}
	}

	private void paintCellBackground( Graphics g, Component rendererComponent, Rectangle bounds ) {
		int xOffset = 0;
		int imageOffset = 0;

		if( rendererComponent instanceof JLabel ) {
			JLabel label = (JLabel) rendererComponent;
			Icon icon = label.getIcon();
			imageOffset = (icon != null && label.getText() != null)
				? icon.getIconWidth() + Math.max( label.getIconTextGap() - 1, 0 )
				: 0;
			xOffset = label.getComponentOrientation().isLeftToRight() ? imageOffset : 0;
		}

		g.fillRect( bounds.x + xOffset, bounds.y, bounds.width - imageOffset, bounds.height );
	}

	/**
	 * Checks whether dropping on a row.
	 * See DefaultTreeCellRenderer.getTreeCellRendererComponent().
	 */
	private boolean isDropRow( int row ) {
		JTree.DropLocation dropLocation = tree.getDropLocation();
		return dropLocation != null &&
			dropLocation.getChildIndex() == -1 &&
			tree.getRowForPath( dropLocation.getPath() ) == row;
	}

	@Override
	protected Rectangle getDropLineRect( DropLocation loc ) {
		Rectangle r = super.getDropLineRect( loc );
		return isWideSelection() ? new Rectangle( 0, r.y, tree.getWidth(), r.height ) : r;
	}

	protected boolean isWideSelection() {
		return clientPropertyBoolean( tree, TREE_WIDE_SELECTION, wideSelection );
	}

	protected boolean isPaintSelection() {
		return clientPropertyBoolean( tree, TREE_PAINT_SELECTION, paintSelection );
	}
}
