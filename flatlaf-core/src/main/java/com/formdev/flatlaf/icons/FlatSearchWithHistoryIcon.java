/*
 * Copyright 2021 FormDev Software GmbH
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

import java.awt.Component;
import java.awt.Graphics2D;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * "search with history" icon for search fields.
 *
 * @author Karl Tauber
 * @since 1.5
 */
public class FlatSearchWithHistoryIcon
	extends FlatSearchIcon
{
	public FlatSearchWithHistoryIcon() {
		this( false );
	}

	/** @since 2 */
	public FlatSearchWithHistoryIcon( boolean ignoreButtonState ) {
		super( ignoreButtonState );
	}

	@Override
	protected void paintIcon( Component c, Graphics2D g ) {
		/*
			<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
			  <g fill="none" fill-opacity=".9" fill-rule="evenodd">
			    <polygon fill="#7F8B91" points="8.813 9.75 12 12.938 10.938 14 7.75 10.813"/>
			    <path fill="#7F8B91" d="M5,2 C7.76142375,2 10,4.23857625 10,7 C10,9.76142375 7.76142375,12 5,12 C2.23857625,12 0,9.76142375 0,7 C0,4.23857625 2.23857625,2 5,2 Z M5,3 C2.790861,3 1,4.790861 1,7 C1,9.209139 2.790861,11 5,11 C7.209139,11 9,9.209139 9,7 C9,4.790861 7.209139,3 5,3 Z"/>
			    <polygon fill="#7F8B91" points="11 7 16 7 13.5 10"/>
			  </g>
			</svg>
		*/

		// paint magnifier
		g.translate( -2, 0 );
		super.paintIcon( c, g );
		g.translate( 2, 0 );

		// paint history arrow
		g.fill( FlatUIUtils.createPath( 11,7, 16,7, 13.5,10 ) );
	}
}
