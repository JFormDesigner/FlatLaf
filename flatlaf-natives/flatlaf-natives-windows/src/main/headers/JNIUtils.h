/*
 * Copyright 2025 FormDev Software GmbH
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
#include <jni.h>

/**
 * @author Karl Tauber
 */

//---- class AutoReleaseString ------------------------------------------------

class AutoReleaseString {
	JNIEnv* env;
	jstring javaString;
	const jchar* chars;

public:
	AutoReleaseString( JNIEnv* _env, jstring _javaString );
	~AutoReleaseString();

	operator LPCWSTR() { return (LPCWSTR) chars; }
};

//---- class AutoReleaseStringArray -------------------------------------------

class AutoReleaseStringArray {
	JNIEnv* env;
	jstring* javaStringArray;
	const jchar** charsArray;

public:
	UINT count;

public:
	AutoReleaseStringArray( JNIEnv* _env, jobjectArray _javaStringArray );
	~AutoReleaseStringArray();

	operator LPCWSTR*() { return (LPCWSTR*) charsArray; }
};
