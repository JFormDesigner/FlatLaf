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
#import "JNIUtils.h"
#import "JNFRunLoop.h"
#import "MacWindowInternal.h"
#import "com_formdev_flatlaf_ui_FlatNativeMacLibrary.h"

/**
 * Title bar drag delegation for fullWindowContent windows.
 *
 * A FlatTitleBarDragView is kept as topmost subview of NSTitlebarView,
 * because AppKit adds or re-inserts other subviews (e.g. the title text field
 * when appearance changes, or NSToolbarView). Clicks on standard window buttons
 * and on the window resize border are passed through by -hitTest: and all other
 * clicks in the title bar area land on us.
 *
 * On mouse down we ask the registered Java callback whether the click is over
 * a caption region. If yes, Swing does not see the click, and if the user starts
 * dragging with the left button, we hand off via
 * -[NSWindow performWindowDragWithEvent:] so AppKit performs the drag with
 * native edge-snap, magnetic alignment, etc. Otherwise the mouse events are
 * forwarded to the contentView and Swing sees them as normal.
 * Mouse wheel events are always forwarded.
 *
 * Mirrors the JetBrains Runtime AWTWindowDragView pattern.
 *
 * @since 3.8
 */


static JavaVM* _jvm = NULL;
static jmethodID _isTitleBarCaptionAtMethodID = NULL;

// Looks up the callback method ID once and caches it. Returns false if the
// interface or method cannot be resolved.
static bool ensureMethodIDs( JNIEnv* env ) {
	if( _isTitleBarCaptionAtMethodID != NULL )
		return true;

	jclass cls = findClass( env, "com/formdev/flatlaf/ui/FlatNativeMacLibrary$FullWindowContentTitleBarCaptionCallback", false );
	_isTitleBarCaptionAtMethodID = getMethodID( env, cls, "isTitleBarCaptionAt", "(II)Z", false );
	return _isTitleBarCaptionAtMethodID != NULL;
}

// Asks the registered Java callback whether the event location is over a
// caption region. Always invoked on the AppKit main thread.
static bool isCaptionEvent( NSWindow* nsWindow, NSEvent* event ) {
	if( nsWindow == NULL || _jvm == NULL || _isTitleBarCaptionAtMethodID == NULL )
		return false;

	WindowData* windowData = getWindowData( nsWindow, false );
	if( windowData == NULL || windowData.titleBarCaptionCallback == NULL )
		return false;

	// convert event location from NSWindow (bottom-up) to AWT window (top-down) coordinates
	NSPoint loc = event.locationInWindow;
	jint x = (jint) loc.x;
	jint y = (jint) (nsWindow.frame.size.height - loc.y);

	jboolean result = JNI_FALSE;
	JNI_THREAD_ENTER( _jvm, false )
	result = env->CallBooleanMethod( windowData.titleBarCaptionCallback,
		_isTitleBarCaptionAtMethodID, x, y );
	if( env->ExceptionCheck() ) {
		env->ExceptionDescribe();
		env->ExceptionClear();
		result = JNI_FALSE;
	}
	JNI_THREAD_EXIT( _jvm )
	return (bool) result;
}


//---- class FlatTitleBarDragView ---------------------------------------------

@interface FlatTitleBarDragView : NSView {
	// whether mouse button was pressed on caption; if true, then the mouse events
	// of this button are not forwarded to the contentView until the next press
	BOOL _leftOnCaption;
	BOOL _rightOnCaption;
	BOOL _otherOnCaption;

	// whether window was moved since left mouse button was pressed
	BOOL _windowDragged;

	// whether -hitTest: is currently checking what is below this view
	BOOL _inHitTest;
}
@end

@implementation FlatTitleBarDragView

	// don't let AppKit start an automatic background drag; we own drag dispatch
	- (BOOL) mouseDownCanMoveWindow { return NO; }

	// allow first click on an inactive window to be a single activate-and-drag gesture
	- (BOOL) acceptsFirstMouse:(NSEvent*)event { return YES; }

	// claim every click in our bounds; forwarding to the contentView happens
	// in the mouse handlers below. Returning nil would let NSTitlebarView treat
	// the click as a background drag. The exceptions are the standard window
	// buttons and the window resize border, which AppKit must handle.
	- (NSView*) hitTest:(NSPoint)pointInSuper {
		NSPoint local = [self.superview convertPoint:pointInSuper toView:self];
		if( _inHitTest || !NSPointInRect( local, self.bounds ) )
			return nil;

		// check what AppKit would hit without this view; the theme frame itself
		// is hit in the window resize border (size depends on macOS version)
		NSView* themeFrame = self.window.contentView.superview;
		_inHitTest = YES;
		NSView* hit = [themeFrame hitTest:[self.superview convertPoint:pointInSuper toView:nil]];
		_inHitTest = NO;

		return (hit == themeFrame || [hit isKindOfClass:[NSButton class]]) ? nil : self;
	}

	- (void) viewWillMoveToWindow:(NSWindow*)newWindow {
		NSNotificationCenter* center = [NSNotificationCenter defaultCenter];
		if( self.window != nil )
			[center removeObserver:self name:NSWindowDidUpdateNotification object:self.window];
		if( newWindow != nil ) {
			[center addObserver:self selector:@selector(windowDidUpdate:)
				name:NSWindowDidUpdateNotification object:newWindow];
		}
	}

	// keep this view topmost
	- (void) windowDidUpdate:(NSNotification*)notification {
		NSView* parent = self.superview;
		if( parent != nil && parent.subviews.lastObject != self )
			[parent addSubview:self positioned:NSWindowAbove relativeTo:nil];
	}

	//---- left mouse button ----

	- (void) mouseDown:(NSEvent*)event {
		_leftOnCaption = isCaptionEvent( self.window, event );
		_windowDragged = NO;

		// same as on Windows, caption does not receive mouse pressed/released/clicked/dragged events
		if( !_leftOnCaption )
			[self.window.contentView mouseDown:event];
	}

	- (void) mouseDragged:(NSEvent*)event {
		if( !_leftOnCaption ) {
			[self.window.contentView mouseDragged:event];
			return;
		}

		if( !_windowDragged ) {
			_windowDragged = YES;

			// this is a synchronous modal loop, which also consumes the mouseUp
			[self.window performWindowDragWithEvent:event];
		}
	}

	- (void) mouseUp:(NSEvent*)event {
		if( !_leftOnCaption ) {
			[self.window.contentView mouseUp:event];
			return;
		}

		_leftOnCaption = NO;
		if( _windowDragged || event.clickCount != 2 )
			return;

		// run the user's "double click on title bar" preference
		NSWindow* w = self.window;
		NSString* action = [[NSUserDefaults standardUserDefaults]
			stringForKey:@"AppleActionOnDoubleClick"];
		if( action == nil ||
			[action caseInsensitiveCompare:@"Maximize"] == NSOrderedSame ||
			[action caseInsensitiveCompare:@"Fill"] == NSOrderedSame )
			[w zoom:nil];
		else if( [action caseInsensitiveCompare:@"Minimize"] == NSOrderedSame )
			[w miniaturize:nil];
	}

	//---- right mouse button ----

	- (void) rightMouseDown:(NSEvent*)event {
		_rightOnCaption = isCaptionEvent( self.window, event );
		if( !_rightOnCaption )
			[self.window.contentView rightMouseDown:event];
	}

	- (void) rightMouseDragged:(NSEvent*)event {
		if( !_rightOnCaption )
			[self.window.contentView rightMouseDragged:event];
	}

	- (void) rightMouseUp:(NSEvent*)event {
		if( !_rightOnCaption )
			[self.window.contentView rightMouseUp:event];
		_rightOnCaption = NO;
	}

	//---- other mouse buttons ----

	- (void) otherMouseDown:(NSEvent*)event {
		_otherOnCaption = isCaptionEvent( self.window, event );
		if( !_otherOnCaption )
			[self.window.contentView otherMouseDown:event];
	}

	- (void) otherMouseDragged:(NSEvent*)event {
		if( !_otherOnCaption )
			[self.window.contentView otherMouseDragged:event];
	}

	- (void) otherMouseUp:(NSEvent*)event {
		if( !_otherOnCaption )
			[self.window.contentView otherMouseUp:event];
		_otherOnCaption = NO;
	}

	//---- mouse wheel ----

	- (void) scrollWheel:(NSEvent*)event {
		[self.window.contentView scrollWheel:event];
	}

@end


// Idempotent. Must be invoked on the main thread.
static void attachTitleBarDragViewIfNeeded( NSWindow* nsWindow, WindowData* windowData ) {
	if( windowData.titleBarDragView != nil ) {
		// AppKit may have detached the view from outside our control
		// (e.g. across a full-screen overlay swap). Re-attach if so.
		if( windowData.titleBarDragView.superview != nil )
			return;
		windowData.titleBarDragView = nil;
	}

	NSView* closeButton = [nsWindow standardWindowButton:NSWindowCloseButton];
	if( closeButton == nil )
		return;
	NSView* titlebar = closeButton.superview;
	if( titlebar == nil )
		return;

	FlatTitleBarDragView* dragView = [[FlatTitleBarDragView alloc] initWithFrame:titlebar.bounds];
	// using autoresizing mask because constraints would be lost when moving view to top
	dragView.autoresizingMask = NSViewWidthSizable | NSViewHeightSizable;
	[titlebar addSubview:dragView positioned:NSWindowAbove relativeTo:nil];
	windowData.titleBarDragView = dragView;
	[dragView release];
}

// Must be invoked on the main thread.
static void detachTitleBarDragView( WindowData* windowData ) {
	if( windowData.titleBarDragView == nil )
		return;

	[windowData.titleBarDragView removeFromSuperview];
	windowData.titleBarDragView = nil;
}


extern "C"
JNIEXPORT jboolean JNICALL Java_com_formdev_flatlaf_ui_FlatNativeMacLibrary_setupFullWindowContentTitleBarCaption
	( JNIEnv* env, jclass cls, jobject window, jobject callback )
{
	JNI_COCOA_ENTER()

	NSWindow* nsWindow = getNSWindow( env, cls, window );
	if( nsWindow == NULL || callback == NULL )
		return FALSE;

	// cache JavaVM and method ID once
	if( _jvm == NULL )
		env->GetJavaVM( &_jvm );
	if( !ensureMethodIDs( env ) )
		return FALSE;

	WindowData* windowData = getWindowData( nsWindow, true );

	env->DeleteGlobalRef( windowData.titleBarCaptionCallback );
	windowData.titleBarCaptionCallback = env->NewGlobalRef( callback );

	[FlatJNFRunLoop performOnMainThreadWaiting:YES withBlock:^(){
		JNI_COCOA_TRY()

		attachTitleBarDragViewIfNeeded( nsWindow, windowData );

		JNI_COCOA_CATCH()
	}];

	return TRUE;

	JNI_COCOA_EXIT()
	return FALSE;
}

extern "C"
JNIEXPORT jboolean JNICALL Java_com_formdev_flatlaf_ui_FlatNativeMacLibrary_removeFullWindowContentTitleBarCaption
	( JNIEnv* env, jclass cls, jobject window )
{
	JNI_COCOA_ENTER()

	NSWindow* nsWindow = getNSWindow( env, cls, window );
	if( nsWindow == NULL )
		return FALSE;

	WindowData* windowData = getWindowData( nsWindow, false );
	if( windowData == NULL )
		return FALSE;

	env->DeleteGlobalRef( windowData.titleBarCaptionCallback );
	windowData.titleBarCaptionCallback = NULL;

	if( windowData.titleBarDragView == nil )
		return FALSE;

	[FlatJNFRunLoop performOnMainThreadWaiting:YES withBlock:^(){
		JNI_COCOA_TRY()

		detachTitleBarDragView( windowData );

		JNI_COCOA_CATCH()
	}];

	return TRUE;

	JNI_COCOA_EXIT()
	return FALSE;
}
