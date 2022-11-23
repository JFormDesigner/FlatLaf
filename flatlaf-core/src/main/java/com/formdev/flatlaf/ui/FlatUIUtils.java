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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.SystemColor;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.IdentityHashMap;
import java.util.WeakHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatSystemProperties;
import com.formdev.flatlaf.util.DerivedColor;
import com.formdev.flatlaf.util.Graphics2DProxy;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.SystemInfo;
import com.formdev.flatlaf.util.UIScale;

/**
 * Utility methods for UI delegates.
 *
 * @author Karl Tauber
 */
public class FlatUIUtils
{
	private static boolean useSharedUIs = true;
	private static final WeakHashMap<LookAndFeel, IdentityHashMap<Object, ComponentUI>> sharedUIinstances = new WeakHashMap<>();

	public static Rectangle addInsets( Rectangle r, Insets insets ) {
		return new Rectangle(
			r.x - insets.left,
			r.y - insets.top,
			r.width + insets.left + insets.right,
			r.height + insets.top + insets.bottom );
	}

	public static Rectangle subtractInsets( Rectangle r, Insets insets ) {
		return new Rectangle(
			r.x + insets.left,
			r.y + insets.top,
			r.width - insets.left - insets.right,
			r.height - insets.top - insets.bottom );
	}

	public static Dimension addInsets( Dimension dim, Insets insets ) {
		return new Dimension(
			dim.width + insets.left + insets.right,
			dim.height + insets.top + insets.bottom );
	}

	public static Insets addInsets( Insets insets1, Insets insets2 ) {
		if( insets1 == null )
			return insets2;
		if( insets2 == null )
			return insets1;

		return new Insets(
			insets1.top + insets2.top,
			insets1.left + insets2.left,
			insets1.bottom + insets2.bottom,
			insets1.right + insets2.right );
	}

	public static void setInsets( Insets dest, Insets src ) {
		dest.top = src.top;
		dest.left = src.left;
		dest.bottom = src.bottom;
		dest.right = src.right;
	}

	public static Color getUIColor( String key, int defaultColorRGB ) {
		Color color = UIManager.getColor( key );
		return (color != null) ? color : new Color( defaultColorRGB );
	}

	public static Color getUIColor( String key, Color defaultColor ) {
		Color color = UIManager.getColor( key );
		return (color != null) ? color : defaultColor;
	}

	public static Color getUIColor( String key, String defaultKey ) {
		Color color = UIManager.getColor( key );
		return (color != null) ? color : UIManager.getColor( defaultKey );
	}

	/** @since 1.1 */
	public static boolean getUIBoolean( String key, boolean defaultValue ) {
		Object value = UIManager.get( key );
		return (value instanceof Boolean) ? (Boolean) value : defaultValue;
	}

	public static int getUIInt( String key, int defaultValue ) {
		Object value = UIManager.get( key );
		return (value instanceof Integer) ? (Integer) value : defaultValue;
	}

	public static float getUIFloat( String key, float defaultValue ) {
		Object value = UIManager.get( key );
		return (value instanceof Number) ? ((Number)value).floatValue() : defaultValue;
	}

	/** @since 2 */
	public static <T extends Enum<T>> T getUIEnum( String key, Class<T> enumType, T defaultValue ) {
		Object value = UIManager.get( key );
		if( value instanceof String ) {
			try {
				return Enum.valueOf( enumType, (String) value );
			} catch( IllegalArgumentException ex ) {
				// ignore
			}
		}
		return defaultValue;
	}

	/** @since 1.1.2 */
	public static boolean getBoolean( JComponent c, String systemPropertyKey,
		String clientPropertyKey, String uiKey, boolean defaultValue )
	{
		// check whether forced to true/false via system property
		Boolean value = FlatSystemProperties.getBooleanStrict( systemPropertyKey, null );
		if( value != null )
			return value;

		// check whether forced to true/false via client property
		value = FlatClientProperties.clientPropertyBooleanStrict( c, clientPropertyKey, null );
		if( value != null )
			return value;

		return getUIBoolean( uiKey, defaultValue );
	}

	public static boolean isChevron( String arrowType ) {
		return !"triangle".equals( arrowType );
	}

	public static Color nonUIResource( Color c ) {
		return (c instanceof UIResource) ? new Color( c.getRGB(), true ) : c;
	}

	public static Font nonUIResource( Font font ) {
		return (font instanceof UIResource) ? font.deriveFont( font.getStyle() ) : font;
	}

	/** @since 2 */
	public static Border nonUIResource( Border border ) {
		return (border instanceof UIResource) ? new NonUIResourceBorder( border ) : border;
	}

	static Border unwrapNonUIResourceBorder( Border border ) {
		return (border instanceof NonUIResourceBorder) ? ((NonUIResourceBorder)border).delegate : border;
	}

	public static int minimumWidth( JComponent c, int minimumWidth ) {
		return FlatClientProperties.clientPropertyInt( c, FlatClientProperties.MINIMUM_WIDTH, minimumWidth );
	}

	public static int minimumHeight( JComponent c, int minimumHeight ) {
		return FlatClientProperties.clientPropertyInt( c, FlatClientProperties.MINIMUM_HEIGHT, minimumHeight );
	}

	public static boolean isCellEditor( Component c ) {
		if( c == null )
			return false;

		// check whether used in cell editor (check 3 levels up)
		Component c2 = c;
		for( int i = 0; i <= 2 && c2 != null; i++ ) {
			Container parent = c2.getParent();
			if( parent instanceof JTable && ((JTable)parent).getEditorComponent() == c2 )
				return true;

			c2 = parent;
		}

		// check whether used as cell editor
		//   Table.editor is set in JTable.GenericEditor constructor
		//   Tree.cellEditor is set in sun.swing.FilePane.editFileName()
		String name = c.getName();
		if( "Table.editor".equals( name ) || "Tree.cellEditor".equals( name ) )
			return true;

		// for using combo box as cell editor in table
		//   JComboBox.isTableCellEditor is set in javax.swing.DefaultCellEditor(JComboBox) constructor
		return c instanceof JComponent && Boolean.TRUE.equals( ((JComponent)c).getClientProperty( "JComboBox.isTableCellEditor" ) );
	}

	/**
	 * Returns whether the given component is the permanent focus owner and
	 * is in the active window or in a popup window owned by the active window.
	 * Used to paint focus indicators.
	 */
	@SuppressWarnings( "unchecked" )
	public static boolean isPermanentFocusOwner( Component c ) {
		KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();

		if( c instanceof JComponent ) {
			Object value = ((JComponent)c).getClientProperty( FlatClientProperties.COMPONENT_FOCUS_OWNER );
			if( value instanceof Predicate ) {
				return ((Predicate<JComponent>)value).test( (JComponent) c ) &&
					isInActiveWindow( c, keyboardFocusManager.getActiveWindow() );
			}
		}

		// invoke hasFocus() here because components may have overridden this method
		// (e.g. Swing delegate components used for AWT components on macOS)
		if( c.hasFocus() )
			return true;

		return keyboardFocusManager.getPermanentFocusOwner() == c &&
			isInActiveWindow( c, keyboardFocusManager.getActiveWindow() );
	}

	static boolean isInActiveWindow( Component c, Window activeWindow ) {
		Window window = SwingUtilities.windowForComponent( c );
		return window == activeWindow ||
			(window != null && window.getType() == Window.Type.POPUP && window.getOwner() == activeWindow);
	}

	static boolean isAWTPeer( Component c ) {
		// on macOS, Swing components are used for AWT components
		if( SystemInfo.isMacOS )
			return c.getClass().getName().startsWith( "sun.lwawt.LW" );
		return false;
	}

	/**
	 * Checks whether component is used as peer for AWT (on macOS) and
	 * whether a dark FlatLaf theme is active, which requires special handling
	 * because AWT always uses light colors.
	 */
	static boolean needsLightAWTPeer( JComponent c ) {
		return FlatUIUtils.isAWTPeer( c ) && FlatLaf.isLafDark();
	}

	private static UIDefaults lightAWTPeerDefaults;

	static void runWithLightAWTPeerUIDefaults( Runnable runnable ) {
		if( lightAWTPeerDefaults == null ) {
			FlatLaf lightLaf = UIManager.getInt( "Component.focusWidth" ) >= 2
				? new FlatIntelliJLaf()
				: new FlatLightLaf();
			lightAWTPeerDefaults = lightLaf.getDefaults();
		}

		FlatLaf.runWithUIDefaultsGetter( key -> {
			Object value = lightAWTPeerDefaults.get( key );
			return (value != null) ? value : FlatLaf.NULL_VALUE;
		}, runnable );
	}

	/**
	 * Returns whether the given component is in a window that is in full-screen mode.
	 */
	public static boolean isFullScreen( Component c ) {
		GraphicsConfiguration gc = c.getGraphicsConfiguration();
		GraphicsDevice gd = (gc != null) ? gc.getDevice() : null;
		Window fullScreenWindow = (gd != null) ? gd.getFullScreenWindow() : null;
		return (fullScreenWindow != null && fullScreenWindow == SwingUtilities.windowForComponent( c ));
	}

	public static Boolean isRoundRect( Component c ) {
		return (c instanceof JComponent)
			? FlatClientProperties.clientPropertyBooleanStrict(
				(JComponent) c, FlatClientProperties.COMPONENT_ROUND_RECT, null )
			: null;
	}

	/**
	 * Returns the scaled thickness of the outer focus border for the given component.
	 */
	public static float getBorderFocusWidth( JComponent c ) {
		FlatBorder border = getOutsideFlatBorder( c );
		return (border != null)
			? UIScale.scale( (float) border.getFocusWidth( c ) )
			: 0;
	}

	/**
	 * Returns the scaled line thickness used to compute the border insets.
	 *
	 * @since 2
	 */
	public static float getBorderLineWidth( JComponent c ) {
		FlatBorder border = getOutsideFlatBorder( c );
		return (border != null)
			? UIScale.scale( (float) border.getLineWidth( c ) )
			: 0;
	}

	/**
	 * Returns the scaled thickness of the border.
	 * This includes the outer focus border and the actual component border.
	 *
	 * @since 2
	 */
	public static int getBorderFocusAndLineWidth( JComponent c ) {
		FlatBorder border = getOutsideFlatBorder( c );
		return (border != null)
			? Math.round( UIScale.scale( (float) border.getFocusWidth( c ) )
				+ UIScale.scale( (float) border.getLineWidth( c ) ) )
			: 0;
	}

	/**
	 * Returns the scaled arc diameter of the border for the given component.
	 */
	public static float getBorderArc( JComponent c ) {
		FlatBorder border = getOutsideFlatBorder( c );
		return (border != null)
			? UIScale.scale( (float) border.getArc( c ) )
			: 0;
	}

	public static boolean hasRoundBorder( JComponent c ) {
		return getBorderArc( c ) >= c.getHeight();
	}

	public static FlatBorder getOutsideFlatBorder( JComponent c ) {
		Border border = c.getBorder();
		for(;;) {
			if( border instanceof FlatBorder )
				return (FlatBorder) border;
			else if( border instanceof CompoundBorder )
				border = ((CompoundBorder)border).getOutsideBorder();
			else
				return null;
		}
	}

	/**
	 * Sets rendering hints used for painting.
	 */
	public static Object[] setRenderingHints( Graphics g ) {
		Graphics2D g2 = (Graphics2D) g;
		Object[] oldRenderingHints = {
			g2.getRenderingHint( RenderingHints.KEY_ANTIALIASING ),
			g2.getRenderingHint( RenderingHints.KEY_STROKE_CONTROL ),
		};

		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		g2.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE );

		return oldRenderingHints;
	}

	/**
	 * Resets rendering hints previously set with {@link #setRenderingHints}.
	 */
	public static void resetRenderingHints( Graphics g, Object[] oldRenderingHints ) {
		Graphics2D g2 = (Graphics2D) g;
		if( oldRenderingHints[0] != null )
			g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, oldRenderingHints[0] );
		if( oldRenderingHints[1] != null )
			g2.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, oldRenderingHints[1] );
	}

	/**
	 * Temporary resets rendering hints set with {@link #setRenderingHints}
	 * and runs the given runnable.
	 * <p>
	 * This is intended for painting text while rendering hints are set.
	 * <p>
	 * If text antialiasing is disabled (in OS system settings or via
	 * {@code -Dawt.useSystemAAFontSettings=off}), but general antialiasing is enabled,
	 * then text is still painted using some kind of "grayscale" antialiasing,
	 * which may make the text look bold (depends on font and font size).
	 * To avoid this, temporary disable general antialiasing.
	 * This does not affect text rendering if text antialiasing is enabled (usually the default).
	 */
	public static void runWithoutRenderingHints( Graphics g, Object[] oldRenderingHints, Runnable runnable ) {
		if( oldRenderingHints == null ) {
			runnable.run();
			return;
		}

		Graphics2D g2 = (Graphics2D) g;
		Object[] oldRenderingHints2 = {
			g2.getRenderingHint( RenderingHints.KEY_ANTIALIASING ),
			g2.getRenderingHint( RenderingHints.KEY_STROKE_CONTROL ),
		};

		resetRenderingHints( g2, oldRenderingHints );
		runnable.run();
		resetRenderingHints( g2, oldRenderingHints2 );
	}

	public static Color deriveColor( Color color, Color baseColor ) {
		return (color instanceof DerivedColor)
			? ((DerivedColor)color).derive( baseColor )
			: color;
	}

	/**
	 * Fills the background of a component with a rounded rectangle.
	 * <p>
	 * The bounds of the painted rounded rectangle are
	 * {@code x + focusWidth, y + focusWidth, width - (focusWidth * 2), height - (focusWidth * 2)}.
	 * The given arc diameter refers to the painted rectangle (and not to {@code x,y,width,height}).
	 *
	 * @see #paintOutlinedComponent
	 */
	public static void paintComponentBackground( Graphics2D g, int x, int y, int width, int height,
		float focusWidth, float arc )
	{
		paintOutlinedComponent( g, x, y, width, height, focusWidth, 0, 0, 0, arc, null, null, g.getPaint() );
	}

	/**
	 * Paints an outlined component with rounded corners, consisting of following parts:
	 * <ul>
	 * <li>an (optional) outer border, which is usually a focus indicator
	 * <li>an (optional) component border
	 * <li>the (optional) component background
	 * </ul>
	 * <p>
	 *
	 * Each part is painted only if the corresponding part color is not {@code null}.
	 * The parts are painted in this order:
	 * <ol>
	 * <li>background
	 * <li>focus border
	 * <li>border
	 * </ol>
	 * <p>
	 *
	 * <strong>Background</strong>:
	 * The bounds of the filled rounded rectangle are
	 * {@code [x + focusWidth, y + focusWidth, width - (focusWidth * 2), height - (focusWidth * 2)]}.
	 * The focus border and the border may paint over the background.
	 * <p>
	 *
	 * <strong>Focus border</strong>:
	 * The outside bounds of the painted focus border are {@code [x, y, width, height]}.
	 * The thickness of the painted focus border is {@code (focusWidth * focusWidthFraction) + focusInnerWidth}.
	 * The border may paint over the focus border if {@code focusInnerWidth > 0}.
	 * <p>
	 *
	 * <strong>Border</strong>:
	 * The outside bounds of the painted border are
	 * {@code [x + focusWidth, y + focusWidth, width - (focusWidth * 2), height - (focusWidth * 2)]}.
	 * The thickness of the painted border is {@code borderWidth}.
	 *
	 * @param g the graphics context used for painting
	 * @param x the x coordinate of the component
	 * @param y the y coordinate of the component
	 * @param width the width of the component
	 * @param height the height of the component
	 * @param focusWidth the width of the focus border, or {@code 0}
	 * @param focusWidthFraction specified how much of the focus border is painted (in range 0 - 1);
	 *                           can be used for animation;
	 *                           the painted thickness of the focus border is {@code (focusWidth * focusWidthFraction) + focusInnerWidth}
	 * @param focusInnerWidth the inner width of the focus border, or {@code 0};
	 *                        if a border is painted then {@code focusInnerWidth} needs to be larger
	 *                        than {@code borderWidth} to be not hidden by the border
	 * @param borderWidth the width of the border, or {@code 0}
	 * @param arc the arc diameter used for the outside shape of the component border;
	 *            the other needed arc diameters are computed from this arc diameter
	 * @param focusColor the color of the focus border, or {@code null}
	 * @param borderColor the color of the border, or {@code null}
	 * @param background the background color of the component, or {@code null}
	 *
	 * @since 2
	 */
	public static void paintOutlinedComponent( Graphics2D g, int x, int y, int width, int height,
		float focusWidth, float focusWidthFraction, float focusInnerWidth, float borderWidth, float arc,
		Paint focusColor, Paint borderColor, Paint background )
	{
		double systemScaleFactor = UIScale.getSystemScaleFactor( g );
		if( systemScaleFactor != 1 && systemScaleFactor != 2 ) {
			// paint at scale 1x to avoid clipping on right and bottom edges at 125%, 150% or 175%
			HiDPIUtils.paintAtScale1x( g, x, y, width, height,
				(g2d, x2, y2, width2, height2, scaleFactor) -> {
					paintOutlinedComponentImpl( g2d, x2, y2, width2, height2,
						(float) (focusWidth * scaleFactor), focusWidthFraction, (float) (focusInnerWidth * scaleFactor),
						(float) (borderWidth * scaleFactor), (float) (arc * scaleFactor),
						focusColor, borderColor, background );
				} );
			return;
		}

		paintOutlinedComponentImpl( g, x, y, width, height, focusWidth, focusWidthFraction, focusInnerWidth,
			borderWidth, arc, focusColor, borderColor, background );
	}

	private static void paintOutlinedComponentImpl( Graphics2D g, int x, int y, int width, int height,
		float focusWidth, float focusWidthFraction, float focusInnerWidth, float borderWidth, float arc,
		Paint focusColor, Paint borderColor, Paint background )
	{
		// outside bounds of the border and the background
		float x1 = x + focusWidth;
		float y1 = y + focusWidth;
		float w1 = width - focusWidth * 2;
		float h1 = height - focusWidth * 2;

		// fill background
		//   bounds: x + focusWidth, y + focusWidth, width - (focusWidth * 2), height - (focusWidth * 2)
		//   arc diameter: arc
		if( background != null ) {
			g.setPaint( background );
			g.fill( createComponentRectangle( x1, y1, w1, h1, arc ) );
		}

		// optimization: paint focus border and border in single operation if colors are equal
		if( borderColor != null && borderColor.equals( focusColor ) ) {
			borderColor = null;
			focusInnerWidth = Math.max( focusInnerWidth, borderWidth );
		}

		// paint focus border
		//   outer bounds: x, y, width, height
		//   thickness: focusWidth + focusInnerWidth
		//   outer arc diameter: arc + (focusWidth * 2)
		//   inner arc diameter: arc - (focusInnerWidth * 2)
		float paintedFocusWidth = (focusWidth * focusWidthFraction) + focusInnerWidth;
		if( focusColor != null && paintedFocusWidth != 0 ) {
			// outside bounds of the focus border
			float inset = focusWidth - (focusWidth * focusWidthFraction);
			float x2 = x + inset;
			float y2 = y + inset;
			float w2 = width - (inset * 2);
			float h2 = height - (inset * 2);

			float outerArc = arc + (focusWidth * 2);
			float innerArc = arc - (focusInnerWidth * 2);

			// reduce outer arc slightly for small arcs to make the curve slightly wider
			if( focusWidth > 0 && arc > 0 && arc < UIScale.scale( 10 ) )
				outerArc -= UIScale.scale( 2f );

			// consider focus width fraction
			if( focusWidthFraction != 1 )
				outerArc = arc + ((outerArc - arc) * focusWidthFraction);

			g.setPaint( focusColor );
			paintOutline( g, x2, y2, w2, h2, paintedFocusWidth, outerArc, innerArc );
		}

		// paint border
		//   outer bounds: x + focusWidth, y + focusWidth, width - (focusWidth * 2), height - (focusWidth * 2)
		//   thickness: borderWidth
		//   outer arc diameter: arc
		//   inner arc diameter: arc - (borderWidth * 2)
		if( borderColor != null && borderWidth != 0 ) {
			g.setPaint( borderColor );
			paintOutline( g, x1, y1, w1, h1, borderWidth, arc );
		}
	}

	/**
	 * Paints an outline at the given bounds using the given line width.
	 * Depending on the given arc, a rectangle, rounded rectangle or circle (if w == h) is painted.
	 *
	 * @param g the graphics context used for painting
	 * @param x the x coordinate of the outline
	 * @param y the y coordinate of the outline
	 * @param w the width of the outline
	 * @param h the height of the outline
	 * @param lineWidth the width of the outline
	 * @param arc the arc diameter used for the outside shape of the outline
	 *
	 * @since 2
	 */
	public static void paintOutline( Graphics2D g, float x, float y, float w, float h,
		float lineWidth, float arc )
	{
		paintOutline( g, x, y, w, h, lineWidth, arc, arc - (lineWidth * 2) );
	}

	/**
	 * Paints an outline at the given bounds using the given line width.
	 * Depending on the given arc, a rectangle, rounded rectangle or circle (if w == h) is painted.
	 *
	 * @param g the graphics context used for painting
	 * @param x the x coordinate of the outline
	 * @param y the y coordinate of the outline
	 * @param w the width of the outline
	 * @param h the height of the outline
	 * @param lineWidth the width of the outline
	 * @param arc the arc diameter used for the outside shape of the outline
	 * @param innerArc the arc diameter used for the inside shape of the outline
	 *
	 * @since 2
	 */
	public static void paintOutline( Graphics2D g, float x, float y, float w, float h,
		float lineWidth, float arc, float innerArc )
	{
		if( lineWidth == 0 || w <= 0 || h <= 0 )
			return;

		float t = lineWidth;
		float t2x = t * 2;

		Path2D border = new Path2D.Float( Path2D.WIND_EVEN_ODD );
		border.append( createComponentRectangle( x, y, w, h, arc ), false );
		border.append( createComponentRectangle( x + t, y + t, w - t2x, h - t2x, innerArc ), false );
		g.fill( border );
	}

	/**
	 * Creates a (rounded) rectangle used to paint components (border, background, etc).
	 * The given arc diameter is limited to min(width,height).
	 */
	public static Shape createComponentRectangle( float x, float y, float w, float h, float arc ) {
		if( arc <= 0 )
			return new Rectangle2D.Float( x, y, w, h );

		if( w == h && arc >= w )
			return new Ellipse2D.Float( x, y, w, h );

		arc = Math.min( arc, Math.min( w, h ) );
		return new RoundRectangle2D.Float( x, y, w, h, arc, arc );
	}

	static void paintFilledRectangle( Graphics g, Color color, float x, float y, float w, float h ) {
		Graphics2D g2 = (Graphics2D) g.create();
		try {
			FlatUIUtils.setRenderingHints( g2 );
			g2.setColor( color );
			g2.fill( new Rectangle2D.Float( x, y, w, h ) );
		} finally {
			g2.dispose();
		}
	}

	/**
	 * Paints a selection.
	 * <p>
	 * The bounds of the painted selection (rounded) rectangle are
	 * {@code x + insets.left, y + insets.top, width - insets.left - insets.right, height - insets.top - insets.bottom}.
	 * The given arc radius refers to the painted rectangle (and not to {@code x,y,width,height}).
	 *
	 * @since 3
	 */
	public static void paintSelection( Graphics2D g, int x, int y, int width, int height, Insets insets,
		float arcTopLeft, float arcTopRight, float arcBottomLeft, float arcBottomRight, int flags )
	{
		if( insets != null ) {
			x += insets.left;
			y += insets.top;
			width -= insets.left + insets.right;
			height -= insets.top + insets.bottom;
		}

		if( arcTopLeft > 0 || arcTopRight > 0 || arcBottomLeft > 0 || arcBottomRight > 0 ) {
			double systemScaleFactor = UIScale.getSystemScaleFactor( g );
			if( systemScaleFactor != 1 && systemScaleFactor != 2 ) {
				// paint at scale 1x to avoid clipping on right and bottom edges at 125%, 150% or 175%
				HiDPIUtils.paintAtScale1x( g, x, y, width, height,
					(g2d, x2, y2, width2, height2, scaleFactor) -> {
						paintRoundedSelectionImpl( g2d, x2, y2, width2, height2,
							(float) (arcTopLeft * scaleFactor), (float) (arcTopRight * scaleFactor),
							(float) (arcBottomLeft * scaleFactor), (float) (arcBottomRight * scaleFactor) );
					} );
			} else
				paintRoundedSelectionImpl( g, x, y, width, height, arcTopLeft, arcTopRight, arcBottomLeft, arcBottomRight );

		} else
			g.fillRect( x, y, width, height );
	}

	private static void paintRoundedSelectionImpl( Graphics2D g, int x, int y, int width, int height,
		float arcTopLeft, float arcTopRight, float arcBottomLeft, float arcBottomRight )
	{
		Object[] oldRenderingHints = FlatUIUtils.setRenderingHints( g );
		g.fill( FlatUIUtils.createRoundRectanglePath( x, y, width, height, arcTopLeft, arcTopRight, arcBottomLeft, arcBottomRight ) );
		FlatUIUtils.resetRenderingHints( g, oldRenderingHints );
	}

	public static void paintGrip( Graphics g, int x, int y, int width, int height,
		boolean horizontal, int dotCount, int dotSize, int gap, boolean centerPrecise )
	{
		dotSize = UIScale.scale( dotSize );
		gap = UIScale.scale( gap );
		int gripSize = (dotSize * dotCount) + ((gap * (dotCount - 1)));

		// calculate grip position
		float gx;
		float gy;
		if( horizontal ) {
			gx = x + Math.round( (width - gripSize) / 2f );
			gy = y + ((height - dotSize) / 2f);

			if( !centerPrecise )
				gy = Math.round( gy );
		} else {
			// vertical
			gx = x + ((width - dotSize) / 2f);
			gy = y + Math.round( (height - gripSize) / 2f );

			if( !centerPrecise )
				gx = Math.round( gx );
		}

		// paint dots
		for( int i = 0; i < dotCount; i++ ) {
			((Graphics2D)g).fill( new Ellipse2D.Float( gx, gy, dotSize, dotSize ) );
			if( horizontal )
				gx += dotSize + gap;
			else
				gy += dotSize + gap;
		}
	}

	/**
	 * Fill background with parent's background color because the visible component
	 * is smaller than its bounds (for the focus decoration).
	 */
	public static void paintParentBackground( Graphics g, JComponent c ) {
		Color background = getParentBackground( c );
		if( background != null ) {
			g.setColor( background );
			g.fillRect( 0, 0, c.getWidth(), c.getHeight() );
		}
	}

	/**
	 * Gets the background color of the first opaque parent.
	 */
	public static Color getParentBackground( JComponent c ) {
		Container parent = findOpaqueParent( c );
		// parent.getBackground() may return null
		// (e.g. for Swing delegate components used for AWT components on macOS)
		Color background = (parent != null) ? parent.getBackground() : null;
		if( background != null )
			return background;

		if( isAWTPeer( c ) ) {
			// AWT peers usually use component background, except for TextField and ScrollPane
			return c instanceof JTextField || c instanceof JScrollPane || c.getBackground() == null
				? SystemColor.window
				: c.getBackground();
		}

		return UIManager.getColor( "Panel.background" );
	}

	/**
	 * Find the first parent that is opaque.
	 */
	private static Container findOpaqueParent( Container c ) {
		while( (c = c.getParent()) != null ) {
			if( c.isOpaque() )
				return c;
		}
		return null;
	}

	/**
	 * Creates a not-filled rectangle shape with the given line width.
	 */
	public static Path2D createRectangle( float x, float y, float width, float height, float lineWidth ) {
		Path2D path = new Path2D.Float( Path2D.WIND_EVEN_ODD );
		path.append( new Rectangle2D.Float( x, y, width, height ), false );
		path.append( new Rectangle2D.Float( x + lineWidth, y + lineWidth,
			width - (lineWidth * 2), height - (lineWidth * 2) ), false );
		return path;
	}

	/**
	 * Creates a not-filled rounded rectangle shape and allows specifying the line width and the radius of each corner.
	 */
	public static Path2D createRoundRectangle( float x, float y, float width, float height,
		float lineWidth, float arcTopLeft, float arcTopRight, float arcBottomLeft, float arcBottomRight )
	{
		Path2D path = new Path2D.Float( Path2D.WIND_EVEN_ODD );
		path.append( createRoundRectanglePath( x, y, width, height, arcTopLeft, arcTopRight, arcBottomLeft, arcBottomRight ), false );
		path.append( createRoundRectanglePath( x + lineWidth, y + lineWidth, width - (lineWidth * 2), height - (lineWidth * 2),
			arcTopLeft - lineWidth, arcTopRight - lineWidth, arcBottomLeft - lineWidth, arcBottomRight - lineWidth ), false );
		return path;
	}

	/**
	 * Creates a filled rounded rectangle shape and allows specifying the radius of each corner.
	 */
	public static Shape createRoundRectanglePath( float x, float y, float width, float height,
		float arcTopLeft, float arcTopRight, float arcBottomLeft, float arcBottomRight )
	{
		if( arcTopLeft <= 0 && arcTopRight <= 0 && arcBottomLeft <= 0 && arcBottomRight <= 0 )
			return new Rectangle2D.Float( x, y, width, height );

		// limit arcs to min(width,height)
		float maxArc = Math.min( width, height ) / 2;
		arcTopLeft = (arcTopLeft > 0) ? Math.min( arcTopLeft, maxArc ) : 0;
		arcTopRight = (arcTopRight > 0) ? Math.min( arcTopRight, maxArc ) : 0;
		arcBottomLeft = (arcBottomLeft > 0) ? Math.min( arcBottomLeft, maxArc ) : 0;
		arcBottomRight = (arcBottomRight > 0) ? Math.min( arcBottomRight, maxArc ) : 0;

		float x2 = x + width;
		float y2 = y + height;

		// same constant as in java.awt.geom.EllipseIterator.CtrlVal used to paint circles
		double c = 0.5522847498307933;
		double ci = 1. - c;
		double ciTopLeft = arcTopLeft * ci;
		double ciTopRight = arcTopRight * ci;
		double ciBottomLeft = arcBottomLeft * ci;
		double ciBottomRight = arcBottomRight * ci;

		Path2D rect = new Path2D.Float( Path2D.WIND_NON_ZERO, 16 );
		rect.moveTo(  x2 - arcTopRight, y );
		rect.curveTo( x2 - ciTopRight, y,
					  x2, y + ciTopRight,
					  x2, y + arcTopRight );
		rect.lineTo(  x2, y2 - arcBottomRight );
		rect.curveTo( x2, y2 - ciBottomRight,
					  x2 - ciBottomRight, y2,
					  x2 - arcBottomRight, y2 );
		rect.lineTo(  x + arcBottomLeft, y2 );
		rect.curveTo( x + ciBottomLeft, y2,
					  x, y2 - ciBottomLeft,
					  x, y2 - arcBottomLeft );
		rect.lineTo(  x, y + arcTopLeft );
		rect.curveTo( x, y + ciTopLeft,
					  x + ciTopLeft, y,
					  x + arcTopLeft, y );
		rect.closePath();

		return rect;
	}

	/**
	 * Creates a rounded triangle shape for the given points and arc radius.
	 *
	 * @since 3
	 */
	public static Shape createRoundTrianglePath( float x1, float y1, float x2, float y2,
		float x3, float y3, float arc )
	{
		double averageSideLength = (distance( x1,y1, x2,y2 ) + distance( x2,y2, x3,y3 ) + distance( x3,y3, x1,y1 )) / 3;
		double t1 = (1 / averageSideLength) * arc;
		double t2 = 1 - t1;

		return createPath(
			lerp( x3, x1, t2 ), lerp( y3, y1, t2 ),
			QUAD_TO, x1, y1, lerp( x1, x2, t1 ), lerp( y1, y2, t1 ),
			lerp( x1, x2, t2 ), lerp( y1, y2, t2 ),
			QUAD_TO, x2, y2, lerp( x2, x3, t1 ), lerp( y2, y3, t1 ),
			lerp( x2, x3, t2 ), lerp( y2, y3, t2 ),
			QUAD_TO, x3, y3, lerp( x3, x1, t1 ), lerp( y3, y1, t1 ) );
	}

	/**
	 * Paints a chevron or triangle arrow in the center of the given rectangle.
	 *
	 * @param g the graphics context used for painting
	 * @param x the x coordinate of the rectangle
	 * @param y the y coordinate of the rectangle
	 * @param width the width of the rectangle
	 * @param height the height of the rectangle
	 * @param direction the arrow direction ({@link SwingConstants#NORTH}, {@link SwingConstants#SOUTH}
	 *        {@link SwingConstants#WEST} or {@link SwingConstants#EAST})
	 * @param chevron {@code true} for chevron arrow, {@code false} for triangle arrow
	 * @param arrowSize the width of the painted arrow (for vertical direction) (will be scaled)
	 * @param arrowThickness the thickness of the painted chevron arrow (will be scaled)
	 * @param xOffset an offset added to the x coordinate of the arrow to paint it out-of-center. Usually zero. (will be scaled)
	 * @param yOffset an offset added to the y coordinate of the arrow to paint it out-of-center. Usually zero. (will be scaled)
	 *
	 * @since 3
	 */
	public static void paintArrow( Graphics2D g, int x, int y, int width, int height,
		int direction, boolean chevron, int arrowSize, float arrowThickness,
		float xOffset, float yOffset )
	{
		// compute arrow width/height
		// - make chevron arrows one pixel smaller because coordinates are based on center of pixels (0.5/0.5)
		// - make triangle arrows one pixel taller (and round height up) to make them look stronger
		float aw = UIScale.scale( arrowSize + (chevron ? -1 : 0) );
		float ah = chevron ? (aw / 2) : UIScale.scale( (arrowSize / 2) + 1 );

		// rotate arrow width/height for horizontal directions
		boolean vert = (direction == SwingConstants.NORTH || direction == SwingConstants.SOUTH);
		if( !vert ) {
			float temp = aw;
			aw = ah;
			ah = temp;
		}

		// chevron lines end 1px outside of width/height
		// --> add 1px to arrow width/height for position calculation
		int extra = chevron ? 1 : 0;

		// compute arrow location
		float ox = ((width - (aw + extra)) / 2f) + UIScale.scale( xOffset );
		float oy = ((height - (ah + extra)) / 2f) + UIScale.scale( yOffset );
		float ax = x + ((direction == SwingConstants.WEST) ? -Math.round( -(ox + aw) ) - aw : Math.round( ox ));
		float ay = y + ((direction == SwingConstants.NORTH) ? -Math.round( -(oy + ah) ) - ah : Math.round( oy ));

		// paint arrow
		g.translate( ax, ay );
/*debug
		debugPaintArrow( g, Color.red, vert, Math.round( aw + extra ), Math.round( ah + extra ) );
debug*/
		Shape arrowShape = createArrowShape( direction, chevron, aw, ah );
		if( chevron ) {
			Stroke oldStroke = g.getStroke();
			g.setStroke( new BasicStroke( UIScale.scale( arrowThickness ) ) );
			drawShapePure( g, arrowShape );
			g.setStroke( oldStroke );
		} else {
			// triangle
			g.fill( arrowShape );
		}
		g.translate( -ax, -ay );
	}

	/**
	 * Creates a chevron or triangle arrow shape for the given direction and size.
	 * <p>
	 * The chevron shape is an open path that can be painted with {@link Graphics2D#draw(Shape)}.
	 * The triangle shape is a close path that can be painted with {@link Graphics2D#fill(Shape)}.
	 *
	 * @param direction the arrow direction ({@link SwingConstants#NORTH}, {@link SwingConstants#SOUTH}
	 *        {@link SwingConstants#WEST} or {@link SwingConstants#EAST})
	 * @param chevron {@code true} for chevron arrow, {@code false} for triangle arrow
	 * @param w the width of the returned shape
	 * @param h the height of the returned shape
	 *
	 * @since 1.1
	 */
	public static Shape createArrowShape( int direction, boolean chevron, float w, float h ) {
		switch( direction ) {
			case SwingConstants.NORTH:	return createPath( !chevron, 0,h, (w / 2f),0, w,h );
			case SwingConstants.SOUTH:	return createPath( !chevron, 0,0, (w / 2f),h, w,0 );
			case SwingConstants.WEST:	return createPath( !chevron, w,0, 0,(h / 2f), w,h );
			case SwingConstants.EAST:	return createPath( !chevron, 0,0, w,(h / 2f), 0,h );
			default:					return new Path2D.Float();
		}
	}

/*debug
	private static void debugPaintArrow( Graphics2D g, Color color, boolean vert, int w, int h ) {
		Color oldColor = g.getColor();
		g.setColor( color );
		g.fill( createRectangle( 0, 0, w, h, 1 ) );

		int xy1 = -2;
		int x2 = w + 1;
		int y2 = h + 1;
		for( int i = 0; i < 20; i++ ) {
			g.fillRect( 0, xy1, 1, 1 );
			g.fillRect( 0, y2, 1, 1 );
			g.fillRect( xy1, 0, 1, 1 );
			g.fillRect( x2, 0, 1, 1 );
			xy1 -= 2;
			x2 += 2;
			y2 += 2;
		}

		g.setColor( oldColor );
	}
debug*/

	/** @since 3 */ public static final double MOVE_TO    = -1_000_000_000_001.;
	/** @since 3 */ public static final double QUAD_TO    = -1_000_000_000_002.;
	/** @since 3 */ public static final double CURVE_TO   = -1_000_000_000_003.;
	/** @since 3 */ public static final double ROUNDED    = -1_000_000_000_004.;
	/** @since 3 */ public static final double CLOSE_PATH = -1_000_000_000_005.;

	/**
	 * Creates a closed path for the given points.
	 */
	public static Path2D createPath( double... points ) {
		return createPath( true, points );
	}

	/**
	 * Creates an open or closed path for the given points.
	 */
	public static Path2D createPath( boolean close, double... points ) {
		Path2D path = new Path2D.Float( Path2D.WIND_NON_ZERO, points.length / 2 + (close ? 1 : 0) );
		path.moveTo( points[0], points[1] );
		for( int i = 2; i < points.length; ) {
			double p = points[i];
			if( p == MOVE_TO ) {
				// move pointer to
				//    params: x, y
				path.moveTo( points[i + 1], points[i + 2] );
				i += 3;
			} else if( p == QUAD_TO ) {
				// add quadratic curve
				//    params: x1, y1, x2, y2
				path.quadTo( points[i + 1], points[i + 2], points[i + 3], points[i + 4] );
				i += 5;
			} else if( p == CURVE_TO ) {
				// add bezier curve
				//    params: x1, y1, x2, y2, x3, y3
				path.curveTo( points[i + 1], points[i + 2], points[i + 3], points[i + 4], points[i + 5], points[i + 6] );
				i += 7;
			} else if( p == ROUNDED ) {
				// add rounded corner
				//    params: x, y, arc
				double x = points[i + 1];
				double y = points[i + 2];
				double arc = points[i + 3];

				// index of next point
				int ip2 = i + 4;
				if( points[ip2] == QUAD_TO || points[ip2] == ROUNDED )
					ip2++;

				// previous and next points
				Point2D p1 = path.getCurrentPoint();
				double x1 = p1.getX();
				double y1 = p1.getY();
				double x2 = points[ip2];
				double y2 = points[ip2 + 1];

				double d1 = distance( x, y, x1, y1 );
				double d2 = distance( x, y, x2, y2 );
				double t1 = 1 - ((1 / d1) * arc);
				double t2 = (1 / d2) * arc;

				path.lineTo( lerp( x1, x, t1 ), lerp( y1, y, t1 ) );
				path.quadTo( x, y, lerp( x, x2, t2 ), lerp( y, y2, t2 ) );

				i += 4;
			} else if( p == CLOSE_PATH ) {
				// close path
				//    params: -
				path.closePath();
				i += 1;
			} else {
				// add line to
				//    params: x, y
				path.lineTo( p, points[i + 1] );
				i += 2;
			}
		}
		if( close )
			path.closePath();
		return path;
	}

	/**
	 * Calculates linear interpolation between two values.
	 *
	 * https://en.wikipedia.org/wiki/Linear_interpolation#Programming_language_support
	 */
	private static double lerp( double v1, double v2, double t ) {
		return (v1 * (1 - t)) + (v2 * t);
	}

	/**
	 * Calculates the distance between two points.
	 */
	private static double distance( double x1, double y1, double x2, double y2 ) {
		double dx = x2 - x1;
		double dy = y2 - y1;
		return Math.sqrt( (dx * dx) + (dy * dy) );
	}

	/**
	 * Draws the given shape with disabled stroke normalization.
	 * The x/y coordinates of the shape are translated by a half pixel.
	 *
	 * @since 2.1
	 */
	public static void drawShapePure( Graphics2D g, Shape shape ) {
		Object oldStrokeControl = g.getRenderingHint( RenderingHints.KEY_STROKE_CONTROL );
		g.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );

		g.translate( 0.5, 0.5 );
		g.draw( shape );
		g.translate( -0.5, -0.5 );

		g.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, oldStrokeControl );
	}

	/**
	 * Draws the given string at the specified location.
	 * The provided component is used to query text properties and anti-aliasing hints.
	 * <p>
	 * Use this method instead of {@link Graphics#drawString(String, int, int)} for correct anti-aliasing.
	 * <p>
	 * Replacement for {@code SwingUtilities2.drawString()}.
	 * Uses {@link HiDPIUtils#drawStringWithYCorrection(JComponent, Graphics2D, String, int, int)}.
	 */
	public static void drawString( JComponent c, Graphics g, String text, int x, int y ) {
		HiDPIUtils.drawStringWithYCorrection( c, (Graphics2D) g, text, x, y );
	}

	/**
	 * Draws the given string at the specified location underlining the specified character.
	 * The provided component is used to query text properties and anti-aliasing hints.
	 * <p>
	 * Replacement for {@code SwingUtilities2.drawStringUnderlineCharAt()}.
	 * Uses {@link HiDPIUtils#drawStringUnderlineCharAtWithYCorrection(JComponent, Graphics2D, String, int, int, int)}.
	 */
	public static void drawStringUnderlineCharAt( JComponent c, Graphics g,
		String text, int underlinedIndex, int x, int y )
	{
		// scale underline height if necessary
		if( underlinedIndex >= 0 && UIScale.getUserScaleFactor() > 1 ) {
			g = new Graphics2DProxy( (Graphics2D) g ) {
				@Override
				public void fillRect( int x, int y, int width, int height ) {
					if( height == 1 ) {
						// scale height and correct y position
						// (using 0.9f so that underline height is 1 at scale factor 1.5x)
						height = Math.round( UIScale.scale( 0.9f ) );
						y += height - 1;
					}

					super.fillRect( x, y, width, height );
				}
			};
		}

		HiDPIUtils.drawStringUnderlineCharAtWithYCorrection( c, (Graphics2D) g, text, underlinedIndex, x, y );
	}

	public static boolean hasOpaqueBeenExplicitlySet( JComponent c ) {
		boolean oldOpaque = c.isOpaque();
		LookAndFeel.installProperty( c, "opaque", !oldOpaque );
		boolean explicitlySet = c.isOpaque() == oldOpaque;
		LookAndFeel.installProperty( c, "opaque", oldOpaque );
		return explicitlySet;
	}

	/**
	 * Returns whether shared UI delegates are used.
	 *
	 * @since 1.6
	 */
	public static boolean isUseSharedUIs() {
		return useSharedUIs;
	}

	/**
	 * Specifies whether shared UI delegates are used.
	 * This does not change already existing UI delegates.
	 *
	 * @since 1.6
	 */
	public static boolean setUseSharedUIs( boolean useSharedUIs ) {
		boolean old = FlatUIUtils.useSharedUIs;
		FlatUIUtils.useSharedUIs = useSharedUIs;
		return old;
	}

	/**
	 * Creates a shared component UI for the given key and the current Laf.
	 * Each Laf instance has its own shared component UI instance.
	 * <p>
	 * This is for GUI builders that support Laf switching and
	 * may use multiple Laf instances at the same time.
	 */
	public static ComponentUI createSharedUI( Object key, Supplier<ComponentUI> newInstanceSupplier ) {
		if( !useSharedUIs )
			return newInstanceSupplier.get();

		return sharedUIinstances
			.computeIfAbsent( UIManager.getLookAndFeel(), k -> new IdentityHashMap<>() )
			.computeIfAbsent( key, k -> newInstanceSupplier.get() );
	}

	/**
	 * Returns whether the component UI for the given component can be shared
	 * with other components. This is only possible if it does not have styles.
	 */
	public static boolean canUseSharedUI( JComponent c ) {
		return !FlatStylingSupport.hasStyleProperty( c );
	}

	//---- class RepaintFocusListener -----------------------------------------

	public static class RepaintFocusListener
		implements FocusListener
	{
		private final Component repaintComponent;
		private final Predicate<Component> repaintCondition;

		public RepaintFocusListener( Component repaintComponent, Predicate<Component> repaintCondition ) {
			this.repaintComponent = repaintComponent;
			this.repaintCondition = repaintCondition;
		}

		@Override
		public void focusGained( FocusEvent e ) {
			if( repaintCondition == null || repaintCondition.test( repaintComponent ) )
				repaintComponent.repaint();
		}

		@Override
		public void focusLost( FocusEvent e ) {
			if( repaintCondition == null || repaintCondition.test( repaintComponent ) )
				repaintComponent.repaint();
		}
	}

	//---- class NonUIResourceBorder ------------------------------------------

	private static class NonUIResourceBorder
		implements Border
	{
		private final Border delegate;

		NonUIResourceBorder( Border delegate ) {
			this.delegate = delegate;
		}

		@Override
		public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
			delegate.paintBorder( c, g, x, y, width, height );
		}

		@Override
		public Insets getBorderInsets( Component c ) {
			return delegate.getBorderInsets( c );
		}

		@Override
		public boolean isBorderOpaque() {
			return delegate.isBorderOpaque();
		}
	}
}
