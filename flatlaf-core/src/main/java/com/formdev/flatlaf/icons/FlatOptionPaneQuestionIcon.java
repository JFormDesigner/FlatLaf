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
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

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
		    <circle cx="16" cy="24" r="1.7" fill="#FFF"/>
		    <path stroke="#FFF" stroke-linecap="round" stroke-width="3" d="M11.5,11.75 C11.75,9.5 13.75,8 16,8 C18.25,8 20.5,9.5 20.5,11.75 C20.5,14.75 16,15.5 16,19"/>
		  </g>
		</svg>
	*/

	@Override
	protected Shape createOutside() {
		return new Ellipse2D.Float( 2, 2, 28, 28 );
	}

	@Override
	protected Shape createInside() {
		Path2D q = new Path2D.Float( Path2D.WIND_NON_ZERO, 10 );
		q.moveTo( 11.5,11.75 );
		q.curveTo( 11.75,9.5, 13.75,8, 16,8 );
		q.curveTo( 18.25,8, 20.5,9.5, 20.5,11.75 );
		q.curveTo( 20.5,14.75, 16,15.5, 16,19 );

		BasicStroke stroke = new BasicStroke( 3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER );

		Path2D inside = new Path2D.Float( Path2D.WIND_EVEN_ODD );
		inside.append( new Ellipse2D.Float( 14.3f, 22.3f, 3.4f, 3.4f ), false );
		inside.append( stroke.createStrokedShape( q ), false );
		return inside;
	}
}
