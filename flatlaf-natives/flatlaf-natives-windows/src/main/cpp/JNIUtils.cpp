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

#include "JNIUtils.h"

/**
 * @author Karl Tauber
 */

//---- class AutoReleaseString ------------------------------------------------

AutoReleaseString::AutoReleaseString( JNIEnv* _env, jstring _javaString ) {
	env = _env;
	javaString = _javaString;
	chars = (javaString != NULL) ? env->GetStringChars( javaString, NULL ) : NULL;
}

AutoReleaseString::~AutoReleaseString() {
	if( chars != NULL )
		env->ReleaseStringChars( javaString, chars );
}

//---- class AutoReleaseStringArray -------------------------------------------

AutoReleaseStringArray::AutoReleaseStringArray( JNIEnv* _env, jobjectArray _javaStringArray ) {
	env = _env;
	count = (_javaStringArray != NULL) ? env->GetArrayLength( _javaStringArray ) : 0;
	if( count <= 0 )
		return;

	javaStringArray = new jstring[count];
	charsArray = new const jchar*[count];

	for( int i = 0; i < count; i++ ) {
		javaStringArray[i] = (jstring) env->GetObjectArrayElement( _javaStringArray, i );
		charsArray[i] = env->GetStringChars( javaStringArray[i] , NULL );
	}
}

AutoReleaseStringArray::~AutoReleaseStringArray() {
	if( count == 0 )
		return;

	for( int i = 0; i < count; i++ ) {
		env->ReleaseStringChars( javaStringArray[i], charsArray[i] );
		env->DeleteLocalRef( javaStringArray[i] );
	}

	delete[] javaStringArray;
	delete[] charsArray;
}
