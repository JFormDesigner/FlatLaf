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

#import <stdlib.h>
#import "JNIUtils.h"

/**
 * @author Karl Tauber
 */

jclass findClass( JNIEnv *env, const char* className, bool globalRef ) {
//	NSLog( @"findClass %s", className );

	jclass cls = env->FindClass( className );
	if( cls == NULL ) {
		NSLog( @"FlatLaf: failed to lookup class '%s'", className );
		env->ExceptionDescribe(); // print stack trace
		env->ExceptionClear();
		return NULL;
	}

	if( globalRef )
		cls = reinterpret_cast<jclass>( env->NewGlobalRef( cls ) );

	return cls;
}

jfieldID getFieldID( JNIEnv *env, jclass cls, const char* fieldName, const char* fieldSignature, bool staticField ) {
//	NSLog( @"getFieldID %s %s", fieldName, fieldSignature );

	if( cls == NULL )
		return NULL;

	jfieldID fieldID = staticField
		? env->GetStaticFieldID( cls, fieldName, fieldSignature )
		: env->GetFieldID( cls, fieldName, fieldSignature );
	if( fieldID == NULL ) {
		NSLog( @"FlatLaf: failed to lookup field '%s' of type '%s'", fieldName, fieldSignature );
		env->ExceptionDescribe(); // print stack trace
		env->ExceptionClear();
		return NULL;
	}

	return fieldID;
}

jmethodID getMethodID( JNIEnv *env, jclass cls, const char* methodName, const char* methodSignature, bool staticMethod ) {
//	NSLog( @"getMethodID %s %s", methodName, methodSignature );

	if( cls == NULL )
		return NULL;

	jmethodID methodID = staticMethod
		? env->GetStaticMethodID( cls, methodName, methodSignature )
		: env->GetMethodID( cls, methodName, methodSignature );
	if( methodID == NULL ) {
		NSLog( @"FlatLaf: failed to lookup method '%s' of type '%s'", methodName, methodSignature );
		env->ExceptionDescribe(); // print stack trace
		env->ExceptionClear();
		return NULL;
	}

	return methodID;
}
