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

#include <jni.h>
#include "com_formdev_flatlaf_ui_FlatNativeLibrary.h"

/**
 * @author Karl Tauber
 */


// increase this version if changing API or functionality of native library
// also update version in Java class com.formdev.flatlaf.ui.FlatNativeWindowsLibrary
#define API_VERSION_WINDOWS		1001


//---- JNI methods ------------------------------------------------------------

extern "C"
JNIEXPORT jint JNICALL Java_com_formdev_flatlaf_ui_FlatNativeLibrary_getApiVersion
	( JNIEnv* env, jclass cls )
{
	return API_VERSION_WINDOWS;
}
