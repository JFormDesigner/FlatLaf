package com.formdev.flatlaf.jideoss.ui;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import com.formdev.flatlaf.ui.FlatPopupMenuUI;
import com.jidesoft.plaf.basic.BasicJidePopupMenuUI;

/**
 * Provides the Flat LaF UI delegate for {@link com.jidesoft.swing.JidePopupMenu}.
 */
public class FlatJidePopupMenuUI
	extends FlatPopupMenuUI
{
	public static ComponentUI createUI( JComponent c ) {
		return new FlatJidePopupMenuUI();
	}

	@Override
	public Popup getPopup( JPopupMenu popupMenu, int x, int y ) {
		Popup popup = BasicJidePopupMenuUI.addScrollPaneIfNecessary( popupMenu, x, y );
		return popup == null ? super.getPopup( popupMenu, x, y ) : popup;
	}

}
