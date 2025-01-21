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

#import <Cocoa/Cocoa.h>
#import <objc/runtime.h>
#import <jni.h>
#import "JNIUtils.h"
#import "JNFRunLoop.h"
#import "com_formdev_flatlaf_ui_FlatNativeMacLibrary.h"

/**
 * @author Karl Tauber
 * @since 3.6
 */

extern "C"
JNIEXPORT jint JNICALL Java_com_formdev_flatlaf_ui_FlatNativeMacLibrary_showMessageDialog
	( JNIEnv* env, jclass cls, jlong hwndParent, jint alertStyle, jstring messageText, jstring informativeText,
		jint defaultButton, jobjectArray buttons )
{
	JNI_COCOA_ENTER()

	// convert Java strings to NSString (on Java thread)
	NSString* nsMessageText = JavaToNSString( env, messageText );
	NSString* nsInformativeText = JavaToNSString( env, informativeText );

	jint buttonCount = env->GetArrayLength( buttons );
	NSMutableArray* nsButtons = [NSMutableArray array];
	for( int i = 0; i < buttonCount; i++ ) {
		NSString* nsButton = JavaToNSString( env, (jstring) env->GetObjectArrayElement( buttons, i ) );
		[nsButtons addObject:nsButton];
	}

	jint result = -1;
	jint* presult = &result;

	// show alert on macOS thread
	[FlatJNFRunLoop performOnMainThreadWaiting:YES withBlock:^(){
		NSAlert* alert = [[NSAlert alloc] init];

		// use appearance from parent window
		NSWindow* parent = (NSWindow*) hwndParent;
		if( parent != NULL )
			alert.window.appearance = parent.appearance;

		// use empty string because if alert.messageText is not set it displays "Alert"
		alert.messageText = (nsMessageText != NULL) ? nsMessageText : @"";
		if( nsInformativeText != NULL )
			alert.informativeText = nsInformativeText;

		// alert style
		switch( alertStyle ) {
			case /* JOptionPane.ERROR_MESSAGE */       0: alert.alertStyle = NSAlertStyleCritical; break;
			default:
			case /* JOptionPane.INFORMATION_MESSAGE */ 1: alert.alertStyle = NSAlertStyleInformational; break;
			case /* JOptionPane.WARNING_MESSAGE */     2: alert.alertStyle = NSAlertStyleWarning; break;
		}

		// add buttons
		for( int i = 0; i < nsButtons.count; i++ ) {
			NSButton* b = [alert addButtonWithTitle:nsButtons[i]];
			if( i == defaultButton )
				alert.window.defaultButtonCell = b.cell;
		}

		// show alert
		NSInteger response = [alert runModal];

		// if no buttons added, which shows a single OK button, the response is 0 when clicking OK
		// if buttons added, response is 1000+buttonIndex
		*presult = MAX( response - NSAlertFirstButtonReturn, 0 );
	}];

	return result;

	JNI_COCOA_EXIT()
}
