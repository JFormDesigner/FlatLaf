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
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
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
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.HiDPIUtils;
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
 * @uiDefault Tree.alternateRowColor				Color
 * @uiDefault Tree.selectionInsets					Insets
 * @uiDefault Tree.selectionArc						int
 * @uiDefault Tree.wideSelection					boolean
 * @uiDefault Tree.wideCellRenderer					boolean
 * @uiDefault Tree.showCellFocusIndicator			boolean
 * @uiDefault Tree.showDefaultIcons					boolean
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
	/** @since 3.6 */ @Styleable protected Color alternateRowColor;
	/** @since 3 */ @Styleable protected Insets selectionInsets;
	/** @since 3 */ @Styleable protected int selectionArc;
	@Styleable protected boolean wideSelection;
	/** @since 3.6 */ @Styleable protected boolean wideCellRenderer;
	@Styleable protected boolean showCellFocusIndicator;
	/** @since 3 */ protected boolean showDefaultIcons;

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

	private Icon defaultLeafIcon;
	private Icon defaultClosedIcon;
	private Icon defaultOpenIcon;

	private boolean paintLines;
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
		alternateRowColor = UIManager.getColor( "Tree.alternateRowColor" );
		selectionInsets = UIManager.getInsets( "Tree.selectionInsets" );
		selectionArc = UIManager.getInt( "Tree.selectionArc" );
		wideSelection = UIManager.getBoolean( "Tree.wideSelection" );
		wideCellRenderer = UIManager.getBoolean( "Tree.wideCellRenderer" );
		showCellFocusIndicator = UIManager.getBoolean( "Tree.showCellFocusIndicator" );
		showDefaultIcons = UIManager.getBoolean( "Tree.showDefaultIcons" );

		defaultLeafIcon = UIManager.getIcon( "Tree.leafIcon" );
		defaultClosedIcon = UIManager.getIcon( "Tree.closedIcon" );
		defaultOpenIcon = UIManager.getIcon( "Tree.openIcon" );

		paintLines = UIManager.getBoolean( "Tree.paintLines" );
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
		alternateRowColor = null;

		defaultLeafIcon = null;
		defaultClosedIcon = null;
		defaultOpenIcon = null;

		defaultCellNonSelectionBackground = null;
		defaultSelectionBackground = null;
		defaultSelectionForeground = null;
		defaultSelectionBorderColor = null;
		oldStyleValues = null;
	}

	@Override
	protected void updateRenderer() {
		super.updateRenderer();

		// remove default leaf/closed/opened icons
		if( !showDefaultIcons && currentCellRenderer instanceof DefaultTreeCellRenderer ) {
			DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) currentCellRenderer;
			if( renderer.getLeafIcon() == defaultLeafIcon &&
				renderer.getClosedIcon() == defaultClosedIcon &&
				renderer.getOpenIcon() == defaultOpenIcon )
			{
				renderer.setLeafIcon( null );
				renderer.setClosedIcon( null );
				renderer.setOpenIcon( null );
			}
		}
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
					case TREE_WIDE_CELL_RENDERER:
					case TREE_PAINT_SELECTION:
						HiDPIUtils.repaint( tree );
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
						HiDPIUtils.repaint( tree );
						break;

					case "enabled":
						// if default icons are not shown and the renderer is a subclass
						// of DefaultTreeCellRenderer, then invalidate tree node sizes
						// because the custom renderer may use an icon for enabled state
						// but none for disabled state
						if( !showDefaultIcons &&
							currentCellRenderer instanceof DefaultTreeCellRenderer &&
							currentCellRenderer.getClass() != DefaultTreeCellRenderer.class &&
							treeState != null )
						{
							treeState.invalidateSizes();
							updateSize();
						}
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
			HiDPIUtils.repaint( tree, 0, r.y, tree.getWidth(), r.height );
	}

	@Override
	protected TreeSelectionListener createTreeSelectionListener() {
		TreeSelectionListener superListener = super.createTreeSelectionListener();
		return e -> {
			superListener.valueChanged( e );

			// for united rounded selection, repaint parts of the rows that adjoin to the changed rows
			TreePath[] changedPaths;
			if( useUnitedRoundedSelection() &&
				tree.getSelectionCount() > 1 &&
				(changedPaths = e.getPaths()) != null )
			{
				if( changedPaths.length > 4 ) {
					// same is done in BasicTreeUI.Handler.valueChanged()
					HiDPIUtils.repaint( tree );
				} else {
					int arc = (int) Math.ceil( UIScale.scale( selectionArc / 2f ) );

					for( TreePath path : changedPaths ) {
						Rectangle r = getPathBounds( tree, path );
						if( r != null )
							HiDPIUtils.repaint( tree, r.x, r.y - arc, r.width, r.height + (arc * 2) );
					}
				}
			}
		};
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
		if( "rowHeight".equals( key ) && value instanceof Integer )
			value = UIScale.scale( (Integer) value );

		return FlatStylingSupport.applyToAnnotatedObjectOrComponent( this, tree, key, value );
	}

	/** @since 2 */
	@Override
	public Map<String, Class<?>> getStyleableInfos( JComponent c ) {
		return FlatStylingSupport.getAnnotatedStyleableInfos( this );
	}

	/** @since 2.5 */
	@Override
	public Object getStyleableValue( JComponent c, String key ) {
		return FlatStylingSupport.getAnnotatedStyleableValue( this, key );
	}

	@Override
	public void paint( Graphics g, JComponent c ) {
		if( treeState == null )
			return;

		// use clip bounds to limit painting to needed rows
		Rectangle clipBounds = g.getClipBounds();
		TreePath firstPath = getClosestPathForLocation( tree, 0, clipBounds.y );
		Enumeration<TreePath> visiblePaths = treeState.getVisiblePathsFrom( firstPath );

		if( visiblePaths != null ) {
			Insets insets = tree.getInsets();

			HashSet<TreePath> verticalLinePaths = paintLines ? new HashSet<>() : null;
			ArrayList<Runnable> paintLinesLater = paintLines ? new ArrayList<>() : null;
			ArrayList<Runnable> paintExpandControlsLater = paintLines ? new ArrayList<>() : null;

			// add parents for later painting of vertical lines
			if( paintLines ) {
				for( TreePath path = firstPath.getParentPath(); path != null; path = path.getParentPath() )
					verticalLinePaths.add( path );
			}

			Rectangle boundsBuffer = new Rectangle();
			boolean rootVisible = isRootVisible();
			int row = treeState.getRowForPath( firstPath );
			boolean leftToRight = tree.getComponentOrientation().isLeftToRight();
			int treeWidth = tree.getWidth();

			// iterate over visible rows and paint rows, expand control and lines
			while( visiblePaths.hasMoreElements() ) {
				TreePath path = visiblePaths.nextElement();
				if( path == null )
					break;

				// compute path bounds
				Rectangle bounds = treeState.getBounds( path, boundsBuffer );
				if( bounds == null )
					break;

				// add tree insets to path bounds
				if( leftToRight )
					bounds.x += insets.left;
				else
					bounds.x = treeWidth - insets.right - (bounds.x + bounds.width);
				bounds.y += insets.top;

				boolean isLeaf = treeModel.isLeaf( path.getLastPathComponent() );
				boolean isExpanded = isLeaf ? false : treeState.getExpandedState( path );
				boolean hasBeenExpanded = isLeaf ? false : tree.hasBeenExpanded( path );

				// paint row (including selection)
				paintRow( g, clipBounds, insets, bounds, path, row, isExpanded, hasBeenExpanded, isLeaf );

				// collect lines for later painting
				if( paintLines ) {
					TreePath parentPath = path.getParentPath();

					// add parent for later painting of vertical lines
					if( parentPath != null )
						verticalLinePaths.add( parentPath );

					// paint horizontal line later (for using rendering hints)
					if( parentPath != null || (rootVisible && row == 0) ) {
						Rectangle bounds2 = new Rectangle( bounds );
						int row2 = row;
						paintLinesLater.add( () -> {
							paintHorizontalPartOfLeg( g, clipBounds, insets, bounds2, path, row2, isExpanded, hasBeenExpanded, isLeaf );
						} );
					}
				}

				// paint expand control
				if( shouldPaintExpandControl( path, row, isExpanded, hasBeenExpanded, isLeaf ) ) {
					if( paintLines ) {
						// need to paint after painting lines
						Rectangle bounds2 = new Rectangle( bounds );
						int row2 = row;
						paintExpandControlsLater.add( () -> {
							paintExpandControl( g, clipBounds, insets, bounds2, path, row2, isExpanded, hasBeenExpanded, isLeaf );
						} );
					} else
						paintExpandControl( g, clipBounds, insets, bounds, path, row, isExpanded, hasBeenExpanded, isLeaf );
				}

				if( bounds.y + bounds.height >= clipBounds.y + clipBounds.height )
					break;

				row++;
			}

			if( paintLines ) {
				// enable antialiasing for line painting
				Object[] oldRenderingHints = FlatUIUtils.setRenderingHints( g );

				// paint horizontal lines
				for( Runnable r : paintLinesLater )
					r.run();

				// paint vertical lines
				g.setColor( Color.green );
				for( TreePath path : verticalLinePaths )
					paintVerticalPartOfLeg( g, clipBounds, insets, path );

				// restore rendering hints
				if( oldRenderingHints != null )
					FlatUIUtils.resetRenderingHints( g, oldRenderingHints );

				// paint expand controls
				for( Runnable r : paintExpandControlsLater )
					r.run();
			}
		}

		paintDropLine( g );

		rendererPane.removeAll();
	}

	/**
	 * Similar to super.paintRow(), but supports wide selection and uses
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

		// paint alternating rows
		if( alternateRowColor != null && row % 2 != 0 ) {
			g.setColor( alternateRowColor );

			float arc = UIScale.scale( selectionArc / 2f );
			FlatUIUtils.paintSelection( (Graphics2D) g, 0, bounds.y, tree.getWidth(), bounds.height,
				UIScale.scale( selectionInsets ), arc, arc, arc, arc, 0 );
		}

		// update bounds for wide cell renderer
		if( isWideSelection() && isWideCellRenderer() ) {
			Rectangle wideBounds = new Rectangle( bounds );
			if( tree.getComponentOrientation().isLeftToRight() )
				wideBounds.width = tree.getWidth() - bounds.x - insets.right;
			else {
				wideBounds.x = insets.left;
				wideBounds.width = bounds.x + bounds.width - insets.left;
			}
			bounds = wideBounds;
		}

		// do not paint row if editing
		if( isEditing ) {
			// paint wide selection
			// (do not access cell renderer here to avoid side effect
			// if renderer component is also used as editor component)
			if( isSelected && isWideSelection() ) {
				Color oldColor = g.getColor();
				g.setColor( selectionInactiveBackground );
				paintWideSelection( g, bounds, row );
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
				paintWideSelection( g, bounds, row );
			} else {
				// non-wide selection
				paintCellBackground( g, rendererComponent, bounds, row, true );
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
					paintCellBackground( g, rendererComponent, bounds, row, false );
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

	private void paintWideSelection( Graphics g, Rectangle bounds, int row ) {
		float arcTop, arcBottom;
		arcTop = arcBottom = UIScale.scale( selectionArc / 2f );

		if( useUnitedRoundedSelection() ) {
			if( row > 0 && tree.isRowSelected( row - 1 ) )
				arcTop = 0;
			if( row < tree.getRowCount() - 1 && tree.isRowSelected( row + 1 ) )
				arcBottom = 0;
		}

		FlatUIUtils.paintSelection( (Graphics2D) g, 0, bounds.y, tree.getWidth(), bounds.height,
			UIScale.scale( selectionInsets ), arcTop, arcTop, arcBottom, arcBottom, 0 );
	}

	private void paintCellBackground( Graphics g, Component rendererComponent, Rectangle bounds,
		int row, boolean paintSelection )
	{
		int xOffset = 0;
		int imageOffset = 0;

		if( rendererComponent instanceof JLabel ) {
			JLabel label = (JLabel) rendererComponent;
			Icon icon = label.isEnabled() ? label.getIcon() : label.getDisabledIcon();
			imageOffset = (icon != null && label.getText() != null)
				? icon.getIconWidth() + Math.max( label.getIconTextGap() - 1, 0 )
				: 0;
			xOffset = label.getComponentOrientation().isLeftToRight() ? imageOffset : 0;
		}

		if( paintSelection ) {
			float arcTopLeft, arcTopRight, arcBottomLeft, arcBottomRight;
			arcTopLeft = arcTopRight = arcBottomLeft = arcBottomRight = UIScale.scale( selectionArc / 2f );

			if( useUnitedRoundedSelection() ) {
				if( row > 0 && tree.isRowSelected( row - 1 ) ) {
					Rectangle r = getPathBounds( tree, tree.getPathForRow( row - 1 ) );
					arcTopLeft = Math.min( arcTopLeft, r.x - bounds.x );
					arcTopRight = Math.min( arcTopRight, (bounds.x + bounds.width) - (r.x + r.width) );
				}
				if( row < tree.getRowCount() - 1 && tree.isRowSelected( row + 1 ) ) {
					Rectangle r = getPathBounds( tree, tree.getPathForRow( row + 1 ) );
					arcBottomLeft = Math.min( arcBottomLeft, r.x - bounds.x );
					arcBottomRight = Math.min( arcBottomRight, (bounds.x + bounds.width) - (r.x + r.width) );
				}
			}

			FlatUIUtils.paintSelection( (Graphics2D) g, bounds.x + xOffset, bounds.y, bounds.width - imageOffset, bounds.height,
				UIScale.scale( selectionInsets ), arcTopLeft, arcTopRight, arcBottomLeft, arcBottomRight, 0 );
		} else
			g.fillRect( bounds.x + xOffset, bounds.y, bounds.width - imageOffset, bounds.height );
	}

	private boolean useUnitedRoundedSelection() {
		return selectionArc > 0 &&
			(selectionInsets == null || (selectionInsets.top == 0 && selectionInsets.bottom == 0));
	}

	@Override
	protected void paintVerticalLine( Graphics g, JComponent c, int x, int top, int bottom ) {
		((Graphics2D)g).fill( new Rectangle2D.Float( x, top, UIScale.scale( 1f ), bottom - top ) );
	}

	@Override
	protected void paintHorizontalLine( Graphics g, JComponent c, int y, int left, int right ) {
		((Graphics2D)g).fill( new Rectangle2D.Float( left, y, right - left, UIScale.scale( 1f ) ) );
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

	/** @since 3.6 */
	protected boolean isWideCellRenderer() {
		return clientPropertyBoolean( tree, TREE_WIDE_CELL_RENDERER, wideCellRenderer );
	}

	protected boolean isPaintSelection() {
		return clientPropertyBoolean( tree, TREE_PAINT_SELECTION, paintSelection );
	}
}
