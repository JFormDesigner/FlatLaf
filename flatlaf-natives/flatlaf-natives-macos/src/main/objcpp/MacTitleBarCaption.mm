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
 * A FlatTitleBarDragView is inserted as a sibling of the standard window
 * buttons inside NSTitlebarView, ordered below them. AppKit's normal
 * hit-testing therefore continues to route traffic-light and contentView
 * clicks correctly; only clicks on the bare title-bar background land on us.
 *
 * On mouseDown we ask the registered Java callback whether the click is over
 * a caption region. If yes and the user starts dragging, we hand off via
 * -[NSWindow performWindowDragWithEvent:] so AppKit performs the drag with
 * native edge-snap, magnetic alignment, etc. Otherwise the click is
 * forwarded to the contentView and Swing sees it as normal.
 *
 * Mirrors the JetBrains Runtime AWTWindowDragView pattern.
 *
 * @since 3.7.2
 */


static JavaVM* _jvm = NULL;
static jmethodID _isTitleBarCaptionAtMethodID = NULL;

// Looks up the callback method ID once and caches it. Returns false if the
// interface or method cannot be resolved.
static bool ensureMethodIDs( JNIEnv* env ) {
	if( _isTitleBarCaptionAtMethodID != NULL )
		return true;
	jclass cls = findClass( env,
		"com/formdev/flatlaf/ui/FlatNativeMacLibrary$FullWindowContentTitleBarCaptionCallback",
		false );
	if( cls == NULL )
		return false;
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
	BOOL _captionTracking;
}
@end

@implementation FlatTitleBarDragView

	// don't let AppKit start an automatic background drag; we own drag dispatch
	- (BOOL) mouseDownCanMoveWindow { return NO; }

	// allow first click on an inactive window to be a single activate-and-drag gesture
	- (BOOL) acceptsFirstMouse:(NSEvent*)event { return YES; }

	// claim every click in our bounds; forwarding to the contentView happens
	// in the mouse handlers below. Returning nil would let NSTitlebarView treat
	// the click as a background drag.
	- (NSView*) hitTest:(NSPoint)pointInSuper {
		NSPoint local = [self.superview convertPoint:pointInSuper toView:self];
		return NSPointInRect( local, self.bounds ) ? self : nil;
	}

	- (void) mouseDown:(NSEvent*)event {
		NSWindow* w = self.window;
		_captionTracking = isCaptionEvent( w, event );

		// always forward so Swing sees the click. If the user starts dragging
		// a caption point, -mouseDragged: hands off to AppKit; otherwise the
		// full click/release sequence reaches Swing as normal.
		[w.contentView mouseDown:event];
	}

	- (void) mouseDragged:(NSEvent*)event {
		if( _captionTracking ) {
			// performWindowDragWithEvent: is a synchronous modal loop; the
			// next mouseDragged: cannot arrive until it returns, so a re-entry
			// guard isn't needed
			_captionTracking = NO;
			NSWindow* w = self.window;
			[w performWindowDragWithEvent:event];

			// AppKit's modal loop ate the mouseUp, so the original mouseDown
			// we forwarded never sees a release. Synthesize an off-screen
			// mouseUp so Swing cancels the press without it counting as a
			// click on the originally-pressed component.
			NSEvent* synthMouseUp = [NSEvent mouseEventWithType:NSEventTypeLeftMouseUp
				location:NSMakePoint( -1, -1 )
				modifierFlags:event.modifierFlags
				timestamp:[NSDate timeIntervalSinceReferenceDate]
				windowNumber:event.windowNumber
				context:nil
				eventNumber:event.eventNumber
				clickCount:0
				pressure:0.0];
			[w.contentView mouseUp:synthMouseUp];
			return;
		}
		[self.window.contentView mouseDragged:event];
	}

	- (void) mouseUp:(NSEvent*)event {
		[self.window.contentView mouseUp:event];

		if( !_captionTracking || event.clickCount != 2 ) {
			_captionTracking = NO;
			return;
		}
		_captionTracking = NO;

		// run the user's "double click on title bar" preference
		NSWindow* w = self.window;
		NSString* action = [[NSUserDefaults standardUserDefaults]
			stringForKey:@"AppleActionOnDoubleClick"];
		if( action == nil || [action caseInsensitiveCompare:@"Maximize"] == NSOrderedSame )
			[w zoom:nil];
		else if( [action caseInsensitiveCompare:@"Minimize"] == NSOrderedSame )
			[w miniaturize:nil];
	}

@end


// Idempotent. Must be invoked on the main thread.
static void attachTitleBarDragViewIfNeeded( NSWindow* nsWindow ) {
	WindowData* windowData = getWindowData( nsWindow, true );
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
	dragView.translatesAutoresizingMaskIntoConstraints = NO;
	// insert below the buttons so they hit-test first
	[titlebar addSubview:dragView positioned:NSWindowBelow relativeTo:closeButton];
	[NSLayoutConstraint activateConstraints:@[
		[dragView.leadingAnchor constraintEqualToAnchor:titlebar.leadingAnchor],
		[dragView.trailingAnchor constraintEqualToAnchor:titlebar.trailingAnchor],
		[dragView.topAnchor constraintEqualToAnchor:titlebar.topAnchor],
		[dragView.bottomAnchor constraintEqualToAnchor:titlebar.bottomAnchor]
	]];
	windowData.titleBarDragView = dragView;
}

// Must be invoked on the main thread.
static void detachTitleBarDragView( NSWindow* nsWindow ) {
	WindowData* windowData = getWindowData( nsWindow, false );
	if( windowData == nil || windowData.titleBarDragView == nil )
		return;
	[windowData.titleBarDragView removeFromSuperview];
	windowData.titleBarDragView = nil;
}

// Releases a JNI global ref using the JNIEnv attached to the current thread.
// No-op if 'ref' or the cached JavaVM is NULL.
static void releaseGlobalRefOnMainThread( jobject ref ) {
	if( ref == NULL || _jvm == NULL )
		return;
	JNIEnv* mainEnv;
	if( _jvm->GetEnv( (void**) &mainEnv, JNI_VERSION_1_6 ) == JNI_OK )
		mainEnv->DeleteGlobalRef( ref );
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

	jobject globalCallback = env->NewGlobalRef( callback );
	if( globalCallback == NULL )
		return FALSE;

	[FlatJNFRunLoop performOnMainThreadWaiting:YES withBlock:^(){
		JNI_COCOA_TRY()

		WindowData* windowData = getWindowData( nsWindow, true );

		// release previous callback (if any)
		jobject previous = windowData.titleBarCaptionCallback;
		windowData.titleBarCaptionCallback = globalCallback;
		releaseGlobalRefOnMainThread( previous );

		attachTitleBarDragViewIfNeeded( nsWindow );

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

	[FlatJNFRunLoop performOnMainThreadWaiting:YES withBlock:^(){
		JNI_COCOA_TRY()

		WindowData* windowData = getWindowData( nsWindow, false );
		if( windowData != NULL ) {
			detachTitleBarDragView( nsWindow );

			jobject previous = windowData.titleBarCaptionCallback;
			windowData.titleBarCaptionCallback = NULL;
			releaseGlobalRefOnMainThread( previous );
		}

		JNI_COCOA_CATCH()
	}];

	return TRUE;

	JNI_COCOA_EXIT()
	return FALSE;
}
