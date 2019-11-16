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

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

/**
 * "Question" icon for {@link javax.swing.JOptionPane}.
 *
 * @uiDefault OptionPane.icon.questionColor			Color	optional; defaults to Actions.Blue
 * @uiDefault Actions.Blue							Color
 *
 * @author Karl Tauber
 */
public class FlatOptionPaneQuestionIcon
	extends FlatOptionPaneAbstractIcon
{
	public FlatOptionPaneQuestionIcon() {
		super( "OptionPane.icon.questionColor", "Actions.Blue" );
	}

	/*
		<svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 32 32">
		  <g fill="none" fill-rule="evenodd">
		    <circle cx="16" cy="16" r="14" fill="#389FD6"/>
		    <rect width="4" height="4" x="14" y="22" fill="#FFF"/>
		    <path fill="#FFF" d="M14,20 C14,20 18,20 18,20 C18,16 23,16 23,12 C23,8 20,6 16,6 C12,6 9,8 9,12 C9,12 13,12 13,12 C13,10 14,9 16,9 C18,9 19,10 19,12 C19,15 14,15 14,20 Z"/>
		  </g>
		</svg>
	*/

	@Override
	protected Shape createOutside() {
		return new Ellipse2D.Float( 2, 2, 28, 28 );
	}

	@Override
	protected Shape createInside() {
		Path2D q = new Path2D.Float();
		q.moveTo( 14, 20 );
		q.lineTo( 18, 20 );
		q.curveTo( 18, 16, 23, 16, 23, 12 );
		q.curveTo( 23, 8, 20, 6, 16, 6 );
		q.curveTo( 12, 6, 9, 8, 9, 12 );
		q.curveTo( 9, 12, 13, 12, 13, 12 );
		q.curveTo( 13, 10, 14, 9, 16, 9 );
		q.curveTo( 18, 9, 19, 10, 19, 12 );
		q.curveTo( 19, 15, 14, 15, 14, 20 );
		q.closePath();

		Path2D inside = new Path2D.Float( Path2D.WIND_EVEN_ODD );
		inside.append( new Rectangle2D.Float( 14, 22, 4, 4 ), false );
		inside.append( q, false );
		return inside;
	}
}
