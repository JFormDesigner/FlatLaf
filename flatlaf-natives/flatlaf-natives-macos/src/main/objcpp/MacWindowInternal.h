/*
 * Copyright 2026 FormDev Software GmbH
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


// per-NSWindow state shared between MacWindow.mm and MacTitleBarCaption.mm;
// associated with the NSWindow via objc_setAssociatedObject in getWindowData()
@interface WindowData : NSObject
	// used when window is full screen
	@property (nonatomic) int lastWindowButtonAreaWidth;
	@property (nonatomic) int lastWindowTitleBarHeight;

	// full screen observers
	@property (nonatomic) id willEnterFullScreenObserver;
	@property (nonatomic) id willExitFullScreenObserver;
	@property (nonatomic) id didExitFullScreenObserver;

	// title bar caption (MacTitleBarCaption.mm)
	@property (nonatomic) jobject titleBarCaptionCallback;
	@property (nonatomic) NSView* titleBarDragView;
@end


// defined in MacWindow.mm
NSWindow* getNSWindow( JNIEnv* env, jclass cls, jobject window );
WindowData* getWindowData( NSWindow* nsWindow, bool allocate );
