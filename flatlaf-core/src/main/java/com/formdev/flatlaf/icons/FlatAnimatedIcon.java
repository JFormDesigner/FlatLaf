/*
 * Copyright 2020 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.formdev.flatlaf.icons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import com.formdev.flatlaf.util.AnimatedIcon;

/**
 * Base class for animated icons that scale width and height, creates and initializes
 * a scaled graphics context for icon painting.
 * <p>
 * Subclasses do not need to scale icon painting.
 * <p>
 * This class does not store any state information (needed for animation) in its instance.
 * Instead, a client property is set on the painted component.
 * This makes it possible to use a share icon instance for multiple components.
 *
 * @author Karl Tauber
 */
public abstract class FlatAnimatedIcon
	extends FlatAbstractIcon
	implements AnimatedIcon
{
	public FlatAnimatedIcon( int width, int height, Color color ) {
		super( width, height, color );
	}

	@Override
	public void paintIcon( Component c, Graphics g, int x, int y ) {
		super.paintIcon( c, g, x, y );
		AnimatedIcon.AnimationSupport.saveIconLocation( this, c, x, y );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		AnimatedIcon.AnimationSupport.paintIcon( this, c, g, 0, 0 );
	}
}
