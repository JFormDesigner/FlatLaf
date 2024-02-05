/*
 * Copyright 2024 FormDev Software GmbH
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

import java.beans.PropertyChangeListener;
import javax.swing.Action;

/**
 * Base class for UI actions used in ActionMap.
 * (similar to class sun.swing.UIAction)
 *
 * @author Karl Tauber
 * @since 3.4
 */
public abstract class FlatUIAction
	implements Action
{
	protected final String name;
	protected final Action delegate;

	protected FlatUIAction( String name ) {
		this.name = name;
		this.delegate = null;
	}

	protected FlatUIAction( Action delegate ) {
		this.name = null;
		this.delegate = delegate;
	}

	@Override
	public Object getValue( String key ) {
		if( key == NAME && delegate == null )
			return name;
		return (delegate != null) ? delegate.getValue( key ) : null;
	}

	@Override
	public boolean isEnabled() {
		return (delegate != null) ? delegate.isEnabled() : true;
	}

	// do nothing in following methods because this class is immutable
	@Override public void putValue( String key, Object value ) {}
	@Override public void setEnabled( boolean b ) {}
	@Override public void addPropertyChangeListener( PropertyChangeListener listener ) {}
	@Override public void removePropertyChangeListener( PropertyChangeListener listener ) {}
}
