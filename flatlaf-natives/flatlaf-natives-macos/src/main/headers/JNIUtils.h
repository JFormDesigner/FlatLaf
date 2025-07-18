/*
 * Copyright 2023 FormDev Software GmbH
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

#import <Foundation/NSException.h>
#import <Foundation/NSString.h>
#import <jni.h>

/**
 * @author Karl Tauber
 */


// from jlong_md.h
#ifndef jlong_to_ptr
  #define jlong_to_ptr(a) ((void*)(a))
#endif


#define JNI_COCOA_TRY() \
	@try {

#define JNI_COCOA_CATCH() \
	} @catch( NSException *ex ) { \
		NSLog( @"Exception: %@\nReason: %@\nUser Info: %@\nStack: %@", \
			[ex name], [ex reason], [ex userInfo], [ex callStackSymbols] ); \
	}

#define JNI_COCOA_ENTER() \
	@autoreleasepool { \
		JNI_COCOA_TRY()

#define JNI_COCOA_EXIT() \
		JNI_COCOA_CATCH() \
	}

#define JNI_THREAD_ENTER( jvm, returnValue ) \
	JNIEnv *env; \
	bool detach = false; \
	switch( jvm->GetEnv( (void**) &env, JNI_VERSION_1_6 ) ) { \
		case JNI_OK: break; \
		case JNI_EDETACHED: \
			if( jvm->AttachCurrentThread( (void**) &env, NULL ) != JNI_OK ) \
				return returnValue; \
			detach = true; \
			break; \
		default: return returnValue; \
	} \
	@try {

#define JNI_THREAD_EXIT( jvm ) \
	} @finally { \
		if( env->ExceptionCheck() ) \
			env->ExceptionDescribe(); \
		if( detach ) \
			jvm->DetachCurrentThread(); \
	}


jclass findClass( JNIEnv *env, const char* className, bool globalRef );
jfieldID getFieldID( JNIEnv *env, jclass cls, const char* fieldName, const char* fieldSignature, bool staticField );
jmethodID getMethodID( JNIEnv *env, jclass cls, const char* methodName, const char* methodSignature, bool staticMethod );

NSString* JavaToNSString( JNIEnv *env, jstring javaString );
jstring NSToJavaString( JNIEnv *env, NSString *nsString );
jstring NormalizedPathJavaFromNSString( JNIEnv* env, NSString *nsString );
