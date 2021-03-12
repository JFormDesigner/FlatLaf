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

#include <windows.h>
#include <winreg.h>
#include <winerror.h>
#include <jni.h>
#include "com_formdev_flatlaf_ui_FlatWindowsNativeWindowBorder.h"

/**
 * @author Karl Tauber
 */

//---- JNI methods ------------------------------------------------------------

extern "C"
JNIEXPORT jint JNICALL Java_com_formdev_flatlaf_ui_FlatWindowsNativeWindowBorder_registryGetIntValue
	( JNIEnv* env, jclass cls, jstring key, jstring valueName, jint defaultValue )
{
	const char* skey = env->GetStringUTFChars( key, NULL );
	const char* svalueName = env->GetStringUTFChars( valueName, NULL );

	DWORD data = 0;
	DWORD cbData = sizeof( data );
	int rc = ::RegGetValueA( HKEY_CURRENT_USER, skey, svalueName, RRF_RT_DWORD, NULL, &data, &cbData );

	env->ReleaseStringUTFChars( key, skey );
	env->ReleaseStringUTFChars( valueName, svalueName );

	return (rc == ERROR_SUCCESS) ? data : defaultValue;
}
