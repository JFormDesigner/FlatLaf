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
import static com.formdev.flatlaf.util.UIScale.scale;
import static com.formdev.flatlaf.util.UIScale.unscale;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.CellRendererPane;
import javax.swing.DefaultListCellRenderer;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.text.JTextComponent;
import com.formdev.flatlaf.icons.FlatCheckBoxMenuItemIcon;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableField;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableLookupProvider;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JComboBox}.
 *
 * <!-- BasicComboBoxUI -->
 *
 * @uiDefault ComboBox.font						Font
 * @uiDefault ComboBox.background				Color
 * @uiDefault ComboBox.foreground				Color
 * @uiDefault ComboBox.border					Border
 * @uiDefault ComboBox.padding					Insets
 * @uiDefault ComboBox.squareButton				boolean	default is true
 *
 * <!-- BasicComboPopup -->
 *
 * @uiDefault ComboBox.selectionBackground		Color
 * @uiDefault ComboBox.selectionForeground		Color
 *
 * <!-- FlatComboBoxUI -->
 *
 * @uiDefault ComboBox.minimumWidth				int
 * @uiDefault ComboBox.editorColumns			int
 * @uiDefault ComboBox.maximumRowCount			int
 * @uiDefault ComboBox.buttonStyle				String	auto (default), button, mac or none
 * @uiDefault Component.arrowType				String	chevron (default) or triangle
 * @uiDefault Component.isIntelliJTheme			boolean
 * @uiDefault ComboBox.editableBackground		Color	optional; defaults to ComboBox.background
 * @uiDefault ComboBox.focusedBackground		Color	optional
 * @uiDefault ComboBox.disabledBackground		Color
 * @uiDefault ComboBox.disabledForeground		Color
 * @uiDefault ComboBox.buttonBackground			Color	optional
 * @uiDefault ComboBox.buttonEditableBackground	Color	optional
 * @uiDefault ComboBox.buttonFocusedBackground	Color	optional; defaults to ComboBox.focusedBackground
 * @uiDefault ComboBox.buttonSeparatorWidth		int or float	optional; defaults to Component.borderWidth
 * @uiDefault ComboBox.buttonSeparatorColor		Color	optional
 * @uiDefault ComboBox.buttonDisabledSeparatorColor Color	optional
 * @uiDefault ComboBox.buttonArrowColor			Color
 * @uiDefault ComboBox.buttonDisabledArrowColor	Color
 * @uiDefault ComboBox.buttonHoverArrowColor	Color
 * @uiDefault ComboBox.buttonPressedArrowColor	Color
 * @uiDefault ComboBox.popupBackground			Color	optional
 * @uiDefault ComboBox.popupInsets				Insets
 * @uiDefault ComboBox.selectionInsets			Insets
 * @uiDefault ComboBox.selectionArc				int
 *
 * @author Karl Tauber
 */
@StyleableField( cls=BasicComboBoxUI.class, key="padding" )

public class FlatComboBoxUI
	extends BasicComboBoxUI
	implements StyleableUI, StyleableLookupProvider
{
	@Styleable protected int minimumWidth;
	@Styleable protected int editorColumns;
	@Styleable protected String buttonStyle;
	@Styleable protected String arrowType;
	protected boolean isIntelliJTheme;

	private Color background;
	@Styleable protected Color editableBackground;
	@Styleable protected Color focusedBackground;
	@Styleable protected Color disabledBackground;
	@Styleable protected Color disabledForeground;

	@Styleable protected Color buttonBackground;
	@Styleable protected Color buttonEditableBackground;
	@Styleable protected Color buttonFocusedBackground;
	/** @since 2 */ @Styleable protected float buttonSeparatorWidth;
	/** @since 2 */ @Styleable protected Color buttonSeparatorColor;
	/** @since 2 */ @Styleable protected Color buttonDisabledSeparatorColor;
	@Styleable protected Color buttonArrowColor;
	@Styleable protected Color buttonDisabledArrowColor;
	@Styleable protected Color buttonHoverArrowColor;
	@Styleable protected Color buttonPressedArrowColor;

	@Styleable protected Color popupBackground;
	/** @since 3 */ @Styleable protected Insets popupInsets;
	/** @since 3 */ @Styleable protected Insets selectionInsets;
	/** @since 3 */ @Styleable protected int selectionArc;

	private MouseListener hoverListener;
	protected boolean hover;
	protected boolean pressed;

	private CellPaddingBorder paddingBorder;

	private Map<String, Object> oldStyleValues;
	private AtomicBoolean borderShared;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatComboBoxUI();
	}

	@Override
	public void installUI( JComponent c ) {
		if( FlatUIUtils.needsLightAWTPeer( c ) )
			FlatUIUtils.runWithLightAWTPeerUIDefaults( () -> installUIImpl( c ) );
		else
			installUIImpl( c );
	}

	private void installUIImpl( JComponent c ) {
		super.installUI( c );

		installStyle();
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		hoverListener = new MouseAdapter() {
			@Override
			public void mouseEntered( MouseEvent e ) {
				hover = true;
				repaintArrowButton();
			}

			@Override
			public void mouseExited( MouseEvent e ) {
				hover = false;
				repaintArrowButton();
			}

			@Override
			public void mousePressed( MouseEvent e ) {
				pressed = true;
				repaintArrowButton();
			}

			@Override
			public void mouseReleased( MouseEvent e ) {
				pressed = false;
				repaintArrowButton();
			}

			private void repaintArrowButton() {
				if( arrowButton != null && !comboBox.isEditable() )
					arrowButton.repaint();
			}
		};
		comboBox.addMouseListener( hoverListener );
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		comboBox.removeMouseListener( hoverListener );
		hoverListener = null;
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		LookAndFeel.installProperty( comboBox, "opaque", false );

		minimumWidth = UIManager.getInt( "ComboBox.minimumWidth" );
		editorColumns = UIManager.getInt( "ComboBox.editorColumns" );
		buttonStyle = UIManager.getString( "ComboBox.buttonStyle" );
		arrowType = UIManager.getString( "Component.arrowType" );
		isIntelliJTheme = UIManager.getBoolean( "Component.isIntelliJTheme" );

		background = UIManager.getColor( "ComboBox.background" );
		editableBackground = UIManager.getColor( "ComboBox.editableBackground" );
		focusedBackground = UIManager.getColor( "ComboBox.focusedBackground" );
		disabledBackground = UIManager.getColor( "ComboBox.disabledBackground" );
		disabledForeground = UIManager.getColor( "ComboBox.disabledForeground" );

		buttonBackground = UIManager.getColor( "ComboBox.buttonBackground" );
		buttonFocusedBackground = UIManager.getColor( "ComboBox.buttonFocusedBackground" );
		buttonEditableBackground = UIManager.getColor( "ComboBox.buttonEditableBackground" );
		buttonSeparatorWidth = FlatUIUtils.getUIFloat( "ComboBox.buttonSeparatorWidth", FlatUIUtils.getUIFloat( "Component.borderWidth", 1 ) );
		buttonSeparatorColor = UIManager.getColor( "ComboBox.buttonSeparatorColor" );
		buttonDisabledSeparatorColor = UIManager.getColor( "ComboBox.buttonDisabledSeparatorColor" );
		buttonArrowColor = UIManager.getColor( "ComboBox.buttonArrowColor" );
		buttonDisabledArrowColor = UIManager.getColor( "ComboBox.buttonDisabledArrowColor" );
		buttonHoverArrowColor = UIManager.getColor( "ComboBox.buttonHoverArrowColor" );
		buttonPressedArrowColor = UIManager.getColor( "ComboBox.buttonPressedArrowColor" );

		popupBackground = UIManager.getColor( "ComboBox.popupBackground" );
		popupInsets = UIManager.getInsets( "ComboBox.popupInsets" );
		selectionInsets = UIManager.getInsets( "ComboBox.selectionInsets" );
		selectionArc = UIManager.getInt( "ComboBox.selectionArc" );

		// set maximumRowCount
		int maximumRowCount = UIManager.getInt( "ComboBox.maximumRowCount" );
		if( maximumRowCount > 0 && maximumRowCount != 8 && comboBox.getMaximumRowCount() == 8 )
			comboBox.setMaximumRowCount( maximumRowCount );

		paddingBorder = new CellPaddingBorder( padding );

		MigLayoutVisualPadding.install( comboBox );
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		background = null;
		editableBackground = null;
		focusedBackground = null;
		disabledBackground = null;
		disabledForeground = null;

		buttonBackground = null;
		buttonEditableBackground = null;
		buttonFocusedBackground = null;
		buttonSeparatorColor = null;
		buttonDisabledSeparatorColor = null;
		buttonArrowColor = null;
		buttonDisabledArrowColor = null;
		buttonHoverArrowColor = null;
		buttonPressedArrowColor = null;

		popupBackground = null;

		paddingBorder.uninstall();

		oldStyleValues = null;
		borderShared = null;

		MigLayoutVisualPadding.uninstall( comboBox );
	}

	@Override
	protected LayoutManager createLayoutManager() {
		return new BasicComboBoxUI.ComboBoxLayoutManager() {
			@Override
			public void layoutContainer( Container parent ) {
				super.layoutContainer( parent );

				// on macOS, a Swing combo box is used for AWT component java.awt.Choice
				// and the font may be (temporary) null

				if( arrowButton != null && comboBox.getFont() != null ) {
					// limit button width to height of a raw combobox (without insets)
					FontMetrics fm = comboBox.getFontMetrics( comboBox.getFont() );
					int maxButtonWidth = fm.getHeight() + scale( padding.top ) + scale( padding.bottom );
					int minButtonWidth = (maxButtonWidth * 3) / 4;

					// make button square (except if width is limited)
					Insets insets = getInsets();
					int buttonWidth = Math.min( Math.max( parent.getHeight() - insets.top - insets.bottom, minButtonWidth ), maxButtonWidth );

					if( buttonWidth != arrowButton.getWidth() ) {
						// set width of arrow button
						int xOffset = comboBox.getComponentOrientation().isLeftToRight()
							? arrowButton.getWidth() - buttonWidth
							: 0;
						arrowButton.setBounds( arrowButton.getX() + xOffset, arrowButton.getY(),
							buttonWidth, arrowButton.getHeight() );

						// update editor bounds
						if( editor != null )
							editor.setBounds( rectangleForCurrentValue() );
					}
				}
			}
		};
	}

	@Override
	protected FocusListener createFocusListener() {
		// repaint combobox to update focus border
		return new BasicComboBoxUI.FocusHandler() {
			@Override
			public void focusGained( FocusEvent e ) {
				super.focusGained( e );
				if( comboBox != null && comboBox.isEditable() )
					comboBox.repaint();
			}

			@Override
			public void focusLost( FocusEvent e ) {
				super.focusLost( e );
				if( comboBox != null && comboBox.isEditable() )
					comboBox.repaint();
			}
		};
	}

	@Override
	protected PropertyChangeListener createPropertyChangeListener() {
		PropertyChangeListener superListener = super.createPropertyChangeListener();
		return e -> {
			superListener.propertyChange( e );

			Object source = e.getSource();
			String propertyName = e.getPropertyName();

			if( editor != null &&
				((source == comboBox && propertyName == "foreground") ||
				 (source == editor && propertyName == "enabled")) )
			{
				// fix editor component colors
				updateEditorColors();
			} else if( editor != null && source == comboBox && propertyName == "componentOrientation" ) {
				ComponentOrientation o = (ComponentOrientation) e.getNewValue();
				editor.applyComponentOrientation( o );
			} else {
				switch( propertyName ) {
					case PLACEHOLDER_TEXT:
						if( editor != null )
							editor.repaint();
						break;

					case COMPONENT_ROUND_RECT:
					case OUTLINE:
						comboBox.repaint();
						break;

					case MINIMUM_WIDTH:
						comboBox.revalidate();
						break;

					case STYLE:
					case STYLE_CLASS:
						installStyle();
						comboBox.revalidate();
						comboBox.repaint();
						break;
				}
			}
		};
	}

	@Override
	protected ComboPopup createPopup() {
		return new FlatComboPopup( comboBox );
	}

	@Override
	protected void configureEditor() {
		super.configureEditor();

		if( editor instanceof JTextField ) {
			JTextField textField = (JTextField) editor;
			textField.setColumns( editorColumns );

			// remove default text field border from editor
			Border border = textField.getBorder();
			if( border == null || border instanceof UIResource ) {
				// assign a non-null and non-javax.swing.plaf.UIResource border to the text field,
				// otherwise it is replaced with default text field border when switching LaF
				// because javax.swing.plaf.basic.BasicComboBoxEditor.BorderlessTextField.setBorder()
				// uses "border instanceof javax.swing.plaf.basic.BasicComboBoxEditor.UIResource"
				// instead of "border instanceof javax.swing.plaf.UIResource"
				textField.setBorder( BorderFactory.createEmptyBorder() );
			}
		}

		// explicitly make non-opaque
		if( editor instanceof JComponent )
			((JComponent)editor).setOpaque( false );

		editor.applyComponentOrientation( comboBox.getComponentOrientation() );

		updateEditorPadding();
		updateEditorColors();

		// macOS
		if( SystemInfo.isMacOS && editor instanceof JTextComponent ) {
			// delegate actions from editor text field to combobox, which is necessary
			// because text field on macOS already handle those keys
			InputMap inputMap = ((JTextComponent)editor).getInputMap();
			new EditorDelegateAction( inputMap, KeyStroke.getKeyStroke( "UP" ) );
			new EditorDelegateAction( inputMap, KeyStroke.getKeyStroke( "KP_UP" ) );
			new EditorDelegateAction( inputMap, KeyStroke.getKeyStroke( "DOWN" ) );
			new EditorDelegateAction( inputMap, KeyStroke.getKeyStroke( "KP_DOWN" ) );
			new EditorDelegateAction( inputMap, KeyStroke.getKeyStroke( "HOME" ) );
			new EditorDelegateAction( inputMap, KeyStroke.getKeyStroke( "END" ) );
		}
	}

	private void updateEditorPadding() {
		if( !(editor instanceof JTextField) )
			return;

		JTextField textField = (JTextField) editor;
		Insets insets = textField.getInsets();
		Insets pad = padding;
		if( insets.top != 0 || insets.left != 0 || insets.bottom != 0 || insets.right != 0 ) {
			// if text field has custom border, subtract text field insets from padding
			pad = new Insets(
				unscale( Math.max( scale( padding.top ) - insets.top, 0 ) ),
				unscale( Math.max( scale( padding.left ) - insets.left, 0 ) ),
				unscale( Math.max( scale( padding.bottom ) - insets.bottom, 0 ) ),
				unscale( Math.max( scale( padding.right ) - insets.right, 0 ) )
			);
		}
		textField.putClientProperty( TEXT_FIELD_PADDING, pad );
	}

	private void updateEditorColors() {
		// use non-UIResource colors because when SwingUtilities.updateComponentTreeUI()
		// is used, then the editor is updated after the combobox and the
		// colors are again replaced with default colors
		boolean isTextComponent = editor instanceof JTextComponent;
		editor.setForeground( FlatUIUtils.nonUIResource( getForeground( isTextComponent || editor.isEnabled() ) ) );

		if( isTextComponent )
			((JTextComponent)editor).setDisabledTextColor( FlatUIUtils.nonUIResource( getForeground( false ) ) );
	}

	@Override
	protected JButton createArrowButton() {
		return new FlatComboBoxButton();
	}

	/** @since 2 */
	protected void installStyle() {
		try {
			applyStyle( FlatStylingSupport.getResolvedStyle( comboBox, "ComboBox" ) );
		} catch( RuntimeException ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	/** @since 2 */
	protected void applyStyle( Object style ) {
		Insets oldPadding = padding;
		int oldEditorColumns = editorColumns;

		oldStyleValues = FlatStylingSupport.parseAndApply( oldStyleValues, style, this::applyStyleProperty );

		if( !padding.equals( oldPadding ) ) {
			paddingBorder.padding = padding;
			updateEditorPadding();
		}
		if( arrowButton instanceof FlatComboBoxButton )
			((FlatComboBoxButton)arrowButton).updateStyle();
		if( popup instanceof FlatComboPopup )
			((FlatComboPopup)popup).updateStyle();
		if( editorColumns != oldEditorColumns && editor instanceof JTextField )
			((JTextField)editor).setColumns( editorColumns );
	}

	/** @since 2 */
	protected Object applyStyleProperty( String key, Object value ) {
		if( borderShared == null )
			borderShared = new AtomicBoolean( true );
		return FlatStylingSupport.applyToAnnotatedObjectOrBorder( this, key, value, comboBox, borderShared );
	}

	/** @since 2 */
	@Override
	public Map<String, Class<?>> getStyleableInfos( JComponent c ) {
		return FlatStylingSupport.getAnnotatedStyleableInfos( this, comboBox.getBorder() );
	}

	/** @since 2.5 */
	@Override
	public Object getStyleableValue( JComponent c, String key ) {
		return FlatStylingSupport.getAnnotatedStyleableValue( this, comboBox.getBorder(), key );
	}

	/** @since 2.5 */
	@Override
	public MethodHandles.Lookup getLookupForStyling() {
		// MethodHandles.lookup() is caller sensitive and must be invoked in this class,
		// otherwise it is not possible to access protected fields in JRE superclass
		return MethodHandles.lookup();
	}

	@Override
	public void update( Graphics g, JComponent c ) {
		float focusWidth = FlatUIUtils.getBorderFocusWidth( c );
		float arc = FlatUIUtils.getBorderArc( c );
		boolean paintBackground = true;

		// check whether used as cell renderer
		boolean isCellRenderer = c.getParent() instanceof CellRendererPane;
		if( isCellRenderer ) {
			focusWidth = 0;
			arc = 0;
			paintBackground = isCellRendererBackgroundChanged();
		}

		// fill background if opaque to avoid garbage if user sets opaque to true
		if( c.isOpaque() && (focusWidth > 0 || arc > 0) )
			FlatUIUtils.paintParentBackground( g, c );

		Graphics2D g2 = (Graphics2D) g;
		Object[] oldRenderingHints = FlatUIUtils.setRenderingHints( g2 );

		int width = c.getWidth();
		int height = c.getHeight();
		int arrowX = arrowButton.getX();
		int arrowWidth = arrowButton.getWidth();
		boolean paintButton = (comboBox.isEditable() || "button".equals( buttonStyle )) &&
			!"none".equals( buttonStyle ) &&
			!isMacStyle();
		boolean enabled = comboBox.isEnabled();
		boolean isLeftToRight = comboBox.getComponentOrientation().isLeftToRight();

		// paint background
		if( paintBackground || c.isOpaque() ) {
			g2.setColor( getBackground( enabled ) );
			FlatUIUtils.paintComponentBackground( g2, 0, 0, width, height, focusWidth, arc );

			// paint arrow button background
			if( enabled && !isCellRenderer ) {
				Color buttonColor = paintButton
					? buttonEditableBackground
					: (buttonFocusedBackground != null || focusedBackground != null) && isPermanentFocusOwner( comboBox )
						? (buttonFocusedBackground != null ? buttonFocusedBackground : focusedBackground)
						: buttonBackground;
				if( buttonColor != null ) {
					g2.setColor( buttonColor );
					if( isMacStyle() ) {
						Insets insets = comboBox.getInsets();
						int gap = scale( 2 );
						FlatUIUtils.paintComponentBackground( g2, arrowX + gap, insets.top + gap,
							arrowWidth - (gap * 2), height - insets.top - insets.bottom - (gap * 2),
							0, arc - focusWidth );
					} else {
						Shape oldClip = g2.getClip();
						if( isLeftToRight )
							g2.clipRect( arrowX, 0, width - arrowX, height );
						else
							g2.clipRect( 0, 0, arrowX + arrowWidth, height );
						FlatUIUtils.paintComponentBackground( g2, 0, 0, width, height, focusWidth, arc );
						g2.setClip( oldClip );
					}
				}
			}

			// paint vertical line between value and arrow button
			if( paintButton ) {
				Color separatorColor = enabled ? buttonSeparatorColor : buttonDisabledSeparatorColor;
				if( separatorColor != null && buttonSeparatorWidth > 0 ) {
					g2.setColor( separatorColor );
					float lw = scale( buttonSeparatorWidth );
					float lx = isLeftToRight ? arrowX : arrowX + arrowWidth - lw;
					g2.fill( new Rectangle2D.Float( lx, focusWidth, lw, height - 1 - (focusWidth * 2)) );
				}
			}
		}

		// avoid that the "current value" renderer is invoked with enabled antialiasing
		FlatUIUtils.resetRenderingHints( g2, oldRenderingHints );

		paint( g, c );
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public void paintCurrentValue( Graphics g, Rectangle bounds, boolean hasFocus ) {
		paddingBorder.uninstall();

		ListCellRenderer<Object> renderer = comboBox.getRenderer();
		if( renderer == null )
			renderer = new DefaultListCellRenderer();
		Component c = renderer.getListCellRendererComponent( listBox, comboBox.getSelectedItem(), -1, false, false );
		c.setFont( comboBox.getFont() );
		c.applyComponentOrientation( comboBox.getComponentOrientation() );

		boolean enabled = comboBox.isEnabled();
		c.setBackground( getBackground( enabled ) );
		c.setForeground( getForeground( enabled ) );

		// make renderer component temporary non-opaque to avoid that renderer paints
		// background outside of border if combobox uses larger arc for edges
		// (e.g. FlatClientProperties.COMPONENT_ROUND_RECT is true)
		if( c instanceof JComponent )
			((JComponent)c).setOpaque( false );

		boolean shouldValidate = (c instanceof JPanel);

		paddingBorder.install( c, 0 );
		currentValuePane.paintComponent( g, c, comboBox, bounds.x, bounds.y, bounds.width, bounds.height, shouldValidate );
		paddingBorder.uninstall();

		if( c instanceof JComponent )
			((JComponent)c).setOpaque( true );
	}

	@Override
	public void paintCurrentValueBackground( Graphics g, Rectangle bounds, boolean hasFocus ) {
		// not necessary because already painted in update()
	}

	protected Color getBackground( boolean enabled ) {
		if( enabled ) {
			if( FlatUIUtils.isAWTPeer( comboBox ) )
				return background;

			Color background = comboBox.getBackground();

			// always use explicitly set color
			if( !(background instanceof UIResource) )
				return background;

			// focused
			if( focusedBackground != null && isPermanentFocusOwner( comboBox ) )
				return focusedBackground;

			return (editableBackground != null && comboBox.isEditable()) ? editableBackground : background;
		} else
			return isIntelliJTheme ? FlatUIUtils.getParentBackground( comboBox ) : disabledBackground;
	}

	protected Color getForeground( boolean enabled ) {
		return enabled ? comboBox.getForeground() : disabledForeground;
	}

	@Override
	public Dimension getMinimumSize( JComponent c ) {
		Dimension minimumSize = super.getMinimumSize( c );
		int fw = Math.round( FlatUIUtils.getBorderFocusWidth( c ) * 2 );
		minimumSize.width = Math.max( minimumSize.width, scale( FlatUIUtils.minimumWidth( c, minimumWidth ) ) + fw );
		return minimumSize;
	}

	@Override
	protected Dimension getDefaultSize() {
		paddingBorder.uninstall();
		Dimension size = super.getDefaultSize();
		paddingBorder.uninstall();
		return size;
	}

	@Override
	protected Dimension getDisplaySize() {
		paddingBorder.uninstall();
		Dimension displaySize = super.getDisplaySize();
		paddingBorder.uninstall();

		// remove padding added in super.getDisplaySize()
		int displayWidth = displaySize.width - padding.left - padding.right;
		int displayHeight = displaySize.height - padding.top - padding.bottom;

		// recalculate width without hardcoded 100 under special conditions
		if( displayWidth == 100 &&
			comboBox.isEditable() &&
			comboBox.getItemCount() == 0 &&
			comboBox.getPrototypeDisplayValue() == null )
		{
			displayWidth = Math.max( getDefaultSize().width, editor.getPreferredSize().width );
		}

		return new Dimension( displayWidth, displayHeight );
	}

	@Override
	protected Dimension getSizeForComponent( Component comp ) {
		paddingBorder.install( comp, 0 );
		Dimension size = super.getSizeForComponent( comp );
		paddingBorder.uninstall();
		return size;
	}

	private boolean isCellRenderer() {
		return comboBox.getParent() instanceof CellRendererPane;
	}

	private boolean isCellRendererBackgroundChanged() {
		// parent is a CellRendererPane, parentParent is e.g. a JTable
		Container parentParent = comboBox.getParent().getParent();
		return parentParent != null && !comboBox.getBackground().equals( parentParent.getBackground() );
	}

	private boolean isMacStyle() {
		return "mac".equals( buttonStyle );
	}

	/** @since 1.3 */
	public static boolean isPermanentFocusOwner( JComboBox<?> comboBox ) {
		if( comboBox.isEditable() ) {
			if( FlatUIUtils.isPermanentFocusOwner( comboBox ) )
				return true;

			Component editorComponent = comboBox.getEditor().getEditorComponent();
			return editorComponent != null && FlatUIUtils.isPermanentFocusOwner( editorComponent );
		} else
			return FlatUIUtils.isPermanentFocusOwner( comboBox );
	}

	//---- class FlatComboBoxButton -------------------------------------------

	protected class FlatComboBoxButton
		extends FlatArrowButton
	{
		protected FlatComboBoxButton() {
			this( SwingConstants.SOUTH, arrowType, buttonArrowColor, buttonDisabledArrowColor,
				buttonHoverArrowColor, null, buttonPressedArrowColor, null );
		}

		protected FlatComboBoxButton( int direction, String type, Color foreground, Color disabledForeground,
			Color hoverForeground, Color hoverBackground, Color pressedForeground, Color pressedBackground )
		{
			super( direction, type, foreground, disabledForeground,
				hoverForeground, hoverBackground, pressedForeground, pressedBackground );
		}

		protected void updateStyle() {
			updateStyle( arrowType, buttonArrowColor, buttonDisabledArrowColor,
				buttonHoverArrowColor, null, buttonPressedArrowColor, null );
		}

		@Override
		public int getArrowWidth() {
			return isMacStyle() ? (getWidth() % 2 == 0 ? 6 : 7) : super.getArrowWidth();
		}

		@Override
		public float getArrowThickness() {
			return isMacStyle() ? 1.5f : super.getArrowThickness();
		}

		@Override
		public boolean isRoundBorderAutoXOffset() {
			return isMacStyle() ? false : super.isRoundBorderAutoXOffset();
		}

		@Override
		protected boolean isHover() {
			return super.isHover() || (!comboBox.isEditable() ? hover : false);
		}

		@Override
		protected boolean isPressed() {
			return super.isPressed() || (!comboBox.isEditable() ? pressed : false);
		}

		@Override
		protected Color getArrowColor() {
			if( isCellRenderer() && isCellRendererBackgroundChanged() )
				return comboBox.getForeground();

			return super.getArrowColor();
		}

		@Override
		protected void paintArrow( Graphics2D g ) {
			if( isMacStyle() && !comboBox.isEditable() ) {
				// for style "mac", paint up and down arrows if combobox is not editable
				int height = getHeight();
				int h = Math.round( height / 2f );
				FlatUIUtils.paintArrow( g, 0, 0, getWidth(), h, SwingConstants.NORTH, chevron,
					getArrowWidth(), getArrowThickness(), getXOffset(), getYOffset() + 1.25f );
				FlatUIUtils.paintArrow( g, 0, height - h, getWidth(), h, SwingConstants.SOUTH, chevron,
					getArrowWidth(), getArrowThickness(), getXOffset(), getYOffset() - 1.25f );
			} else
				super.paintArrow( g );
		}
	}

	//---- class FlatComboPopup -----------------------------------------------

	@SuppressWarnings( { "rawtypes", "unchecked" } )
	protected class FlatComboPopup
		extends BasicComboPopup
	{
		protected FlatComboPopup( JComboBox combo ) {
			super( combo );

			// BasicComboPopup listens to JComboBox.componentOrientation and updates
			// the component orientation of the list, scroll pane and popup, but when
			// switching the LaF and a new combo popup is created, the component
			// orientation is not applied.
			ComponentOrientation o = comboBox.getComponentOrientation();
			list.setComponentOrientation( o );
			scroller.setComponentOrientation( o );
			setComponentOrientation( o );
		}

		@Override
		protected Rectangle computePopupBounds( int px, int py, int pw, int ph ) {
			// get maximum display width of all items
			int displayWidth = getDisplaySize().width;

			// add border insets
			for( Border border : new Border[] { scroller.getViewportBorder(), scroller.getBorder() } ) {
				if( border != null ) {
					Insets borderInsets = border.getBorderInsets( null );
					displayWidth += borderInsets.left + borderInsets.right;
				}
			}

			// for style "mac", add width of "checked item" icon
			boolean isPopupOverComboBox = isPopupOverComboBox();
			int selectedIndex = -1;
			if( isPopupOverComboBox && (selectedIndex = comboBox.getSelectedIndex()) >= 0 )
				displayWidth += MacCheckedItemIcon.INSTANCE.getIconWidth() + scale( CellPaddingBorder.MAC_STYLE_GAP );

			// add width of vertical scroll bar
			JScrollBar verticalScrollBar = scroller.getVerticalScrollBar();
			if( verticalScrollBar != null )
				displayWidth += verticalScrollBar.getPreferredSize().width;

			// make popup wider if necessary
			int pw0 = pw;
			if( displayWidth > pw ) {
				// limit popup width to screen width
				GraphicsConfiguration gc = comboBox.getGraphicsConfiguration();
				if( gc != null ) {
					Rectangle screenBounds = gc.getBounds();
					Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets( gc );
					displayWidth = Math.min( displayWidth, screenBounds.width - screenInsets.left - screenInsets.right );
				} else {
					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					displayWidth = Math.min( displayWidth, screenSize.width );
				}

				int diff = displayWidth - pw;
				pw = displayWidth;

				if( !comboBox.getComponentOrientation().isLeftToRight() )
					px -= diff;
			}

			// for style "mac", place popup over combobox
			Rectangle cellBounds;
			if( isPopupOverComboBox && selectedIndex >= 0 &&
				(cellBounds = list.getCellBounds( 0, 0 )) != null )
			{
				Insets comboBoxInsets = comboBox.getInsets();
				Insets listInsets = list.getInsets();
				Insets popupInsets = getInsets();

				// position popup so that selected item is at same Y position as combobox
				py -= (cellBounds.height * (selectedIndex + 1)) + comboBoxInsets.top + listInsets.top + popupInsets.top;

				// position popup slightly to the left so that a small part of the right side of the combobox stays visible
				int offset = Math.min( pw - pw0, MacCheckedItemIcon.INSTANCE.getIconWidth() ) + scale( 4 );
				if( comboBox.getComponentOrientation().isLeftToRight() )
					px -= offset + comboBoxInsets.right + listInsets.right;
				else
					px += offset + comboBoxInsets.left + listInsets.left;

				// not invoking super.computePopupBounds() here to let
				// JPopupMenu.adjustPopupLocationToFitScreen() fix the location if necessary
				return new Rectangle( px, py, pw, ph );
			}

			return super.computePopupBounds( px, py, pw, ph );
		}

		@Override
		protected void configurePopup() {
			super.configurePopup();

			// make opaque to avoid that background shines thru border (e.g. at 150% scaling)
			setOpaque( true );

			// set popup border
			// use non-UIResource to avoid that it is overwritten when making
			// popup visible (see JPopupMenu.setInvoker()) in theme editor preview
			Border border = UIManager.getBorder( "PopupMenu.border" );
			if( border != null )
				setBorder( FlatUIUtils.nonUIResource( border ) );

			list.setCellRenderer( new PopupListCellRenderer() );
			updateStyle();
		}

		void updateStyle() {
			if( popupBackground != null )
				list.setBackground( popupBackground );

			// set popup background because it may shine thru when scaled (e.g. at 150%)
			// use non-UIResource to avoid that it is overwritten when making
			// popup visible (see JPopupMenu.setInvoker()) in theme editor preview
			setBackground( FlatUIUtils.nonUIResource( list.getBackground() ) );

			scroller.setViewportBorder( (popupInsets != null) ? new FlatEmptyBorder( popupInsets ) : null );
			scroller.setOpaque( false );

			if( list.getUI() instanceof FlatListUI ) {
				FlatListUI ui = (FlatListUI) list.getUI();
				ui.selectionInsets = selectionInsets;
				ui.selectionArc = selectionArc;
			}
		}

		@Override
		protected PropertyChangeListener createPropertyChangeListener() {
			PropertyChangeListener superListener = super.createPropertyChangeListener();
			return e -> {
				superListener.propertyChange( e );

				if( e.getPropertyName() == "renderer" )
					list.setCellRenderer( new PopupListCellRenderer() );
			};
		}

		@Override
		protected int getPopupHeightForRowCount( int maxRowCount ) {
			int height = super.getPopupHeightForRowCount( maxRowCount );
			paddingBorder.uninstall();
			return height;
		}

		@Override
		public void show( Component invoker, int x, int y ) {
			// Java 8: fix y coordinate if popup is shown above the combobox
			// (already fixed in Java 9+ https://bugs.openjdk.java.net/browse/JDK-7072653)
			if( y < 0 && !SystemInfo.isJava_9_orLater ) {
				Border popupBorder = getBorder();
				if( popupBorder != null ) {
					Insets insets = popupBorder.getBorderInsets( this );
					y -= insets.top + insets.bottom;
				}
			}

			super.show( invoker, x, y );
		}

		@Override
		protected void paintChildren( Graphics g ) {
			super.paintChildren( g );
			paddingBorder.uninstall();
		}

		private boolean isPopupOverComboBox() {
			return isMacStyle() &&
				!comboBox.isEditable() &&
				comboBox.getItemCount() > 0 &&
				comboBox.getItemCount() <= comboBox.getMaximumRowCount() &&
				// for compatibility with Aqua Laf
				!clientPropertyBoolean( comboBox, "JComboBox.isPopDown", false );
		}

		//---- class PopupListCellRenderer -----

		private class PopupListCellRenderer
			implements ListCellRenderer
		{
			@Override
			public Component getListCellRendererComponent( JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus )
			{
				paddingBorder.uninstall();

				ListCellRenderer renderer = comboBox.getRenderer();
				if( renderer == null )
					renderer = new DefaultListCellRenderer();
				Component c = renderer.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
				c.applyComponentOrientation( comboBox.getComponentOrientation() );

				// style "mac"
				if( isPopupOverComboBox() && c instanceof JComponent ) {
					int selectedIndex = comboBox.getSelectedIndex();
					((JComponent)c).putClientProperty( CellPaddingBorder.KEY_MAC_STYLE_HINT,
						(selectedIndex >= 0) ? (index == selectedIndex) : null );
				}

				paddingBorder.install( c, Math.round( FlatUIUtils.getBorderFocusWidth( comboBox ) ) );

				return c;
			}
		}
	}

	//---- class CellPaddingBorder --------------------------------------------

	/**
	 * Cell padding border used in popup list and for current value if not editable.
	 * <p>
	 * The insets are the union of the cell padding and the renderer border insets,
	 * which vertically aligns text in popup list with text in combobox.
	 * <p>
	 * The renderer border is painted on the outer side of this border.
	 * <p>
	 * For button style "mac", also used to increase insets on left side for
	 * "checked item" icon and to paint "checked item" icon for selected combobox item.
	 */
	private static class CellPaddingBorder
		extends AbstractBorder
	{
		static final String KEY_MAC_STYLE_HINT = "FlatLaf.internal.FlatComboBoxUI.macStyleHint";
		static final int MAC_STYLE_GAP = 4;

		private Insets padding;
		private JComponent rendererComponent;
		private Border rendererBorder;
		private int focusWidth;

		CellPaddingBorder( Insets padding ) {
			this.padding = padding;
		}

		// using synchronized to avoid problems with code that modifies combo box
		// (model, selection, etc) not on AWT thread (which should be not done)
		synchronized void install( Component c, int focusWidth ) {
			if( !(c instanceof JComponent) )
				return;

			this.focusWidth = focusWidth;

			JComponent jc = (JComponent) c;
			Border oldBorder = jc.getBorder();
			if( oldBorder == this )
				return; // already installed

			// component already has a padding border --> uninstall it
			// (may happen if single renderer instance is used in multiple comboboxes)
			if( oldBorder instanceof CellPaddingBorder )
				((CellPaddingBorder)oldBorder).uninstall();

			// this border can be installed only at one component
			// (may happen if a renderer returns varying components)
			uninstall();

			// remember component where this border was installed for uninstall
			rendererComponent = jc;

			// remember old border and replace it
			rendererBorder = jc.getBorder();
			jc.setBorder( this );
		}

		/**
		 * Uninstall border from previously installed component.
		 * Because this border is installed in PopupListCellRenderer.getListCellRendererComponent(),
		 * there is no single place to uninstall it.
		 * This is the reason why this method is called from various places.
		 */
		synchronized void uninstall() {
			if( rendererComponent == null )
				return;

			rendererComponent.putClientProperty( KEY_MAC_STYLE_HINT, null );

			if( rendererComponent.getBorder() == this )
				rendererComponent.setBorder( rendererBorder );
			rendererComponent = null;
			rendererBorder = null;
		}

		@Override
		synchronized public Insets getBorderInsets( Component c, Insets insets ) {
			Insets padding = scale( this.padding );
			if( rendererBorder != null && !(rendererBorder instanceof CellPaddingBorder) ) {
				Insets insideInsets = rendererBorder.getBorderInsets( c );
				insets.top = Math.max( padding.top, insideInsets.top );
				insets.left = Math.max( padding.left, insideInsets.left );
				insets.bottom = Math.max( padding.bottom, insideInsets.bottom );
				insets.right = Math.max( padding.right, insideInsets.right );
			} else {
				insets.top = padding.top;
				insets.left = padding.left;
				insets.bottom = padding.bottom;
				insets.right = padding.right;
			}

			// if used in popup list, add focus width for exact vertical alignment
			// of text in popup list with text in combobox
			insets.left += focusWidth;
			insets.right += focusWidth;

			// style "mac"
			if( c instanceof JComponent ) {
				Boolean macStyleHint = clientPropertyBooleanStrict( (JComponent) c, KEY_MAC_STYLE_HINT, null );
				if( macStyleHint != null ) {
					int indent = MacCheckedItemIcon.INSTANCE.getIconWidth() + scale( MAC_STYLE_GAP );
					if( c.getComponentOrientation().isLeftToRight() )
						insets.left += indent;
					else
						insets.right += indent;
				}
			}

			return insets;
		}

		@Override
		public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
			if( rendererBorder != null )
				rendererBorder.paintBorder( c, g, x, y, width, height );

			// style "mac"
			if( c instanceof JComponent ) {
				Boolean macStyleHint = clientPropertyBooleanStrict( (JComponent) c, KEY_MAC_STYLE_HINT, null );
				if( macStyleHint == Boolean.TRUE ) {
					// paint "checked item" icon
					int ix = c.getComponentOrientation().isLeftToRight()
						? x + scale( padding.left )
						: x + width - scale( padding.right ) - MacCheckedItemIcon.INSTANCE.getIconWidth();
					MacCheckedItemIcon.INSTANCE.paintIcon( c, g, ix, y + ((height - MacCheckedItemIcon.INSTANCE.getIconHeight()) / 2) );
				}
			}
		}
	}

	//---- class MacCheckedItemIcon -------------------------------------------

	/**
	 * Use for style "mac" to mark checked item.
	 */
	private static class MacCheckedItemIcon
		extends FlatCheckBoxMenuItemIcon
	{
		static MacCheckedItemIcon INSTANCE = new MacCheckedItemIcon();

		@Override
		protected void paintIcon( Component c, Graphics2D g2 ) {
			g2.setColor( c.getForeground() );
			paintCheckmark( g2 );
		}
	}

	//---- class EditorDelegateAction -----------------------------------------

	/**
	 * Delegates actions from editor text field to combobox.
	 */
	private class EditorDelegateAction
		extends AbstractAction
	{
		private final KeyStroke keyStroke;

		EditorDelegateAction( InputMap inputMap, KeyStroke keyStroke ) {
			this.keyStroke = keyStroke;

			// add to input map
			inputMap.put( keyStroke, this );
		}

		@Override
		public void actionPerformed( ActionEvent e ) {
			ActionListener action = comboBox.getActionForKeyStroke( keyStroke );
			if( action != null ) {
				action.actionPerformed( new ActionEvent( comboBox, e.getID(),
					e.getActionCommand(), e.getWhen(), e.getModifiers() ) );
			}
		}
	}
}
