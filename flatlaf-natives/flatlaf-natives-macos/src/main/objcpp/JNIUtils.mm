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

jfieldID getFieldID( JNIEnv *env, const char* className, const char* fieldName, const char* fieldSignature ) {
//	NSLog( @"getFieldID %s %s %s", className, fieldName, fieldSignature );

	jclass cls = env->FindClass( className );
	if( cls == NULL ) {
		NSLog( @"FlatLaf: failed to lookup class '%s'", className );
		env->ExceptionDescribe(); // print stack trace
		env->ExceptionClear();
		return NULL;
	}

	jfieldID fieldID = env->GetFieldID( cls, fieldName, fieldSignature );
	if( fieldID == NULL ) {
		NSLog( @"FlatLaf: failed to lookup field '%s' of type '%s' in class '%s'", fieldName, fieldSignature, className );
		env->ExceptionDescribe(); // print stack trace
		env->ExceptionClear();
		return NULL;
	}

	return fieldID;
}
