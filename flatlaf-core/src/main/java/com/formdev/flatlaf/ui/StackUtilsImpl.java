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
class StackUtilsImpl
	extends StackUtils
{
	@Override
	boolean wasInvokedFromImpl( BiPredicate<String, String> predicate, int limit ) {
		int count = -2;
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		for( StackTraceElement stackTraceElement : stackTrace ) {
			if( predicate.test( stackTraceElement.getClassName(), stackTraceElement.getMethodName() ) )
				return true;

			count++;
			if( limit > 0 && count > limit )
				return false;
		}
		return false;
	}
}
