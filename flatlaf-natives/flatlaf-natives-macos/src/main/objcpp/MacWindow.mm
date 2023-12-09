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

#import <Cocoa/Cocoa.h>
#import <jni.h>
#import "JNIUtils.h"
#import "JNFRunLoop.h"
#import "com_formdev_flatlaf_ui_FlatNativeMacLibrary.h"

/**
 * @author Karl Tauber
 */

extern "C"
JNIEXPORT jlong JNICALL Java_com_formdev_flatlaf_ui_FlatNativeMacLibrary_getWindowPtr
	( JNIEnv* env, jclass cls, jobject window )
{
	if( window == NULL )
		return NULL;
	
	JNI_COCOA_ENTER()

	// get field java.awt.Component.peer
	jfieldID peerID = env->GetFieldID( env->GetObjectClass( window ), "peer", "Ljava/awt/peer/ComponentPeer;" );
	jobject peer = (peerID != NULL) ? env->GetObjectField( window, peerID ) : NULL;
	if( peer == NULL )
		return NULL;

	// get field sun.lwawt.LWWindowPeer.platformWindow
	jfieldID platformWindowID = env->GetFieldID( env->GetObjectClass( peer ), "platformWindow", "Lsun/lwawt/PlatformWindow;" );
	jobject platformWindow = (platformWindowID != NULL) ? env->GetObjectField( peer, platformWindowID ) : NULL;
	if( platformWindow == NULL )
		return NULL;

	// get field sun.lwawt.macosx.CFRetainedResource.ptr
	jfieldID ptrID = env->GetFieldID( env->GetObjectClass( platformWindow ), "ptr", "J" );
	return (ptrID != NULL) ? env->GetLongField( platformWindow, ptrID ) : NULL;

	JNI_COCOA_EXIT()
}

extern "C"
JNIEXPORT void JNICALL Java_com_formdev_flatlaf_ui_FlatNativeMacLibrary_setWindowRoundedBorder
	( JNIEnv* env, jclass cls, jlong windowPtr, jfloat radius, jfloat borderWidth, jint borderColor )
{
	if( windowPtr == 0 )
		return;
	
	JNI_COCOA_ENTER()

	[FlatJNFRunLoop performOnMainThreadWaiting:NO withBlock:^(){
		NSWindow* window = (NSWindow *) jlong_to_ptr( windowPtr );

		window.hasShadow = YES;
		window.contentView.wantsLayer = YES;
		window.contentView.layer.cornerRadius = radius;
		window.contentView.layer.masksToBounds = YES;

		window.contentView.layer.borderWidth = borderWidth;
		if( borderWidth > 0 ) {
			CGFloat red   = ((borderColor >> 16) & 0xff) / 255.;
			CGFloat green = ((borderColor >> 8) & 0xff) / 255.;
			CGFloat blue  = (borderColor & 0xff) / 255.;
			CGFloat alpha = ((borderColor >> 24) & 0xff) / 255.;

			window.contentView.layer.borderColor = [[NSColor colorWithDeviceRed:red green:green blue:blue alpha:alpha] CGColor];
		}

		window.backgroundColor = NSColor.clearColor;
		window.opaque = NO;

		[window.contentView.layer removeAllAnimations];
		[window invalidateShadow];
	}];

	JNI_COCOA_EXIT()
}
