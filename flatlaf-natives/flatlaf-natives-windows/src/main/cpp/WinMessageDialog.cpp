/*
 * Copyright 2024 FormDev Software GmbH
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

// avoid inlining of printf()
#define _NO_CRT_STDIO_INLINE

#include <windows.h>
#include "JNIUtils.h"
#include "com_formdev_flatlaf_ui_FlatNativeWindowsLibrary.h"

/**
 * @author Karl Tauber
 * @since 3.6
 */

extern "C"
JNIEXPORT jint JNICALL Java_com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_showMessageDialog
	( JNIEnv* env, jclass cls, jlong hwndParent, jstring text, jstring caption, jint type )
{
	// convert Java strings to C strings
	AutoReleaseString ctext( env, text );
	AutoReleaseString ccaption( env, caption );

	return ::MessageBox( reinterpret_cast<HWND>( hwndParent ), ctext, ccaption, type );
}
