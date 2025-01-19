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

#include <dlfcn.h>
#include "JNIUtils.h"

/**
 * @author Karl Tauber
 */

//---- class AutoReleaseStringUTF8 --------------------------------------------

AutoReleaseStringUTF8::AutoReleaseStringUTF8( JNIEnv* _env, jstring _javaString ) {
	env = _env;
	javaString = _javaString;
	chars = (javaString != NULL) ? env->GetStringUTFChars( javaString, NULL ) : NULL;
}

AutoReleaseStringUTF8::~AutoReleaseStringUTF8() {
	if( chars != NULL )
		env->ReleaseStringUTFChars( javaString, chars );
}

//---- JNI methods ------------------------------------------------------------

extern "C"
JNIEXPORT jboolean JNICALL Java_com_formdev_flatlaf_ui_FlatNativeLinuxLibrary_isLibAvailable
	( JNIEnv* env, jclass cls, jstring libname )
{
	AutoReleaseStringUTF8 clibname( env, libname );

	void* lib = dlopen( clibname, RTLD_LAZY );
	if( lib == NULL )
		return false;

	dlclose( lib );
	return true;
}
