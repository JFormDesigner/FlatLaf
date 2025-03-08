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

package com.formdev.flatlaf.ui;

import java.util.function.BiPredicate;

/**
 * @author Karl Tauber
 */
public class StackUtils
{
	private static final StackUtils INSTANCE = new StackUtilsImpl();

	// hide from javadoc
	StackUtils() {
	}

	/**
	 * Checks whether current method was invoked from the given class and method.
	 */
	public static boolean wasInvokedFrom( String className, String methodName, int limit ) {
		return wasInvokedFrom( (c,m) -> c.equals( className ) && m.equals( methodName ), limit );
	}

	/**
	 * Checks whether current method was invoked from a class and method using the given predicate,
	 * which gets the class name of the stack frame as first parameter and the method name as second parameter.
	 */
	public static boolean wasInvokedFrom( BiPredicate<String, String> predicate, int limit ) {
		return INSTANCE.wasInvokedFromImpl( predicate, limit );
	}

	boolean wasInvokedFromImpl( BiPredicate<String, String> predicate, int limit ) {
		throw new UnsupportedOperationException();
	}
}
