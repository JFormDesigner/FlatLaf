package com.formdev.flatlaf.extras.components;

import javax.swing.*;
import static com.formdev.flatlaf.FlatClientProperties.TREE_WIDE_SELECTION;
import static com.formdev.flatlaf.FlatClientProperties.clientPropertyBoolean;

/**
 * Subclass of {@link JTree} that provides easy access to FlatLaf specific client properties.
 *
 */
public class FlatTree
	extends JTree
	implements FlatComponentExtension
{
	/**
	 * Returns if the tree shows a wide selection
	 */
	public boolean isWideSelection() {
		return clientPropertyBoolean( this, TREE_WIDE_SELECTION, UIManager.getBoolean( "Tree.wideSelection" ));
	}

	/**
	 * Sets if the tree shows a wide selection
	 */
	public void setWideSelection(boolean wideSelection) {
		putClientProperty( TREE_WIDE_SELECTION, wideSelection);
	}
}
