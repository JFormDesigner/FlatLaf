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

NSWindow* getNSWindow( JNIEnv* env, jclass cls, jobject window ) {
	if( window == NULL )
		return NULL;

	// initialize field IDs (done only once because fields are static)
	static jfieldID peerID = getFieldID( env, "java/awt/Component", "peer", "Ljava/awt/peer/ComponentPeer;" );
	static jfieldID platformWindowID = getFieldID( env, "sun/lwawt/LWWindowPeer", "platformWindow", "Lsun/lwawt/PlatformWindow;" );
	static jfieldID ptrID = getFieldID( env, "sun/lwawt/macosx/CFRetainedResource", "ptr", "J" );
	if( peerID == NULL || platformWindowID == NULL || ptrID == NULL )
		return NULL;

	// get field java.awt.Component.peer
	jobject peer = env->GetObjectField( window, peerID );
	if( peer == NULL )
		return NULL;

	// get field sun.lwawt.LWWindowPeer.platformWindow
	jobject platformWindow = env->GetObjectField( peer, platformWindowID );
	if( platformWindow == NULL )
		return NULL;

	// get field sun.lwawt.macosx.CFRetainedResource.ptr
	return (NSWindow *) jlong_to_ptr( env->GetLongField( platformWindow, ptrID ) );
}

extern "C"
JNIEXPORT jboolean JNICALL Java_com_formdev_flatlaf_ui_FlatNativeMacLibrary_setWindowRoundedBorder
	( JNIEnv* env, jclass cls, jobject window, jfloat radius, jfloat borderWidth, jint borderColor )
{
	JNI_COCOA_ENTER()

	NSWindow* nsWindow = getNSWindow( env, cls, window );
	if( nsWindow == NULL )
		return FALSE;

	[FlatJNFRunLoop performOnMainThreadWaiting:NO withBlock:^(){
		nsWindow.hasShadow = YES;
		nsWindow.contentView.wantsLayer = YES;
		nsWindow.contentView.layer.cornerRadius = radius;
		nsWindow.contentView.layer.masksToBounds = YES;

		nsWindow.contentView.layer.borderWidth = borderWidth;
		if( borderWidth > 0 ) {
			CGFloat red   = ((borderColor >> 16) & 0xff) / 255.;
			CGFloat green = ((borderColor >> 8) & 0xff) / 255.;
			CGFloat blue  = (borderColor & 0xff) / 255.;
			CGFloat alpha = ((borderColor >> 24) & 0xff) / 255.;

			nsWindow.contentView.layer.borderColor = [[NSColor colorWithDeviceRed:red green:green blue:blue alpha:alpha] CGColor];
		}

		nsWindow.backgroundColor = NSColor.clearColor;
		nsWindow.opaque = NO;

		[nsWindow.contentView.layer removeAllAnimations];
		[nsWindow invalidateShadow];
	}];

	return TRUE;

	JNI_COCOA_EXIT()
	return FALSE;
}

extern "C"
JNIEXPORT void JNICALL Java_com_formdev_flatlaf_ui_FlatNativeMacLibrary_setWindowToolbar
	( JNIEnv* env, jclass cls, jobject window )
{
	JNI_COCOA_ENTER()

	NSWindow* nsWindow = getNSWindow( env, cls, window );
	if( nsWindow == NULL )
		return;

	[FlatJNFRunLoop performOnMainThreadWaiting:NO withBlock:^(){
		NSLog( @"\n%@\n\n", [nsWindow.contentView.superview _subtreeDescription] );

		NSToolbar* toolbar = [NSToolbar new];
		nsWindow.toolbar = toolbar;

		// TODO handle fullscreen

		NSLog( @"\n%@\n\n", [nsWindow.contentView.superview _subtreeDescription] );
	}];

	JNI_COCOA_EXIT()
}
