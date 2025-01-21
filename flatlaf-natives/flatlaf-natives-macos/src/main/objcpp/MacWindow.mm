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
#import <objc/runtime.h>
#import <jni.h>
#import "JNIUtils.h"
#import "JNFRunLoop.h"
#import "com_formdev_flatlaf_ui_FlatNativeMacLibrary.h"

/**
 * @author Karl Tauber
 */

@interface WindowData : NSObject
	// used when window is full screen
	@property (nonatomic) int lastWindowButtonAreaWidth;
	@property (nonatomic) int lastWindowTitleBarHeight;

	// full screen observers
	@property (nonatomic) id willEnterFullScreenObserver;
	@property (nonatomic) id willExitFullScreenObserver;
	@property (nonatomic) id didExitFullScreenObserver;
@end

@implementation WindowData
@end

// declare internal methods
NSWindow* getNSWindow( JNIEnv* env, jclass cls, jobject window );
WindowData* getWindowData( NSWindow* nsWindow, bool allocate );
void setWindowButtonsHidden( NSWindow* nsWindow, bool hidden );
int getWindowButtonAreaWidth( NSWindow* nsWindow );
int getWindowTitleBarHeight( NSWindow* nsWindow );
bool isWindowFullScreen( NSWindow* nsWindow );


NSWindow* getNSWindow( JNIEnv* env, jclass cls, jobject window ) {
	if( window == NULL )
		return NULL;

	// initialize class IDs (done only once because variables are static)
	static jclass lwWindowPeerClass = findClass( env, "sun/lwawt/LWWindowPeer", true );
	static jclass cfRetainedResourceClass = findClass( env, "sun/lwawt/macosx/CFRetainedResource", true );
	if( lwWindowPeerClass == NULL || cfRetainedResourceClass == NULL )
		return NULL;

	// initialize field IDs (done only once because variables are static)
	static jfieldID peerID = getFieldID( env, findClass( env, "java/awt/Component", false ), "peer", "Ljava/awt/peer/ComponentPeer;", false );
	static jfieldID platformWindowID = getFieldID( env, lwWindowPeerClass, "platformWindow", "Lsun/lwawt/PlatformWindow;", false );
	static jfieldID ptrID = getFieldID( env, cfRetainedResourceClass, "ptr", "J", false );
	if( peerID == NULL || platformWindowID == NULL || ptrID == NULL )
		return NULL;

	// get field java.awt.Component.peer
	jobject peer = env->GetObjectField( window, peerID );
	if( peer == NULL || !env->IsInstanceOf( peer, lwWindowPeerClass ) )
		return NULL;

	// get field sun.lwawt.LWWindowPeer.platformWindow
	jobject platformWindow = env->GetObjectField( peer, platformWindowID );
	if( platformWindow == NULL || !env->IsInstanceOf( platformWindow, cfRetainedResourceClass ) )
		return NULL;

	// get field sun.lwawt.macosx.CFRetainedResource.ptr
	return (NSWindow *) jlong_to_ptr( env->GetLongField( platformWindow, ptrID ) );
}

WindowData* getWindowData( NSWindow* nsWindow, bool allocate ) {
	static char key;
	WindowData* windowData = objc_getAssociatedObject( nsWindow, &key );
	if( windowData == NULL && allocate ) {
		windowData = [WindowData new];
		objc_setAssociatedObject( nsWindow, &key, windowData, OBJC_ASSOCIATION_RETAIN_NONATOMIC );
	}
	return windowData;
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
		JNI_COCOA_TRY()

		nsWindow.hasShadow = YES;
		nsWindow.contentView.wantsLayer = YES;
		nsWindow.contentView.layer.cornerRadius = radius;
		nsWindow.contentView.layer.masksToBounds = YES;
		nsWindow.contentView.layer.opaque = NO;

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

		JNI_COCOA_CATCH()
	}];

	return TRUE;

	JNI_COCOA_EXIT()
	return FALSE;
}

extern "C"
JNIEXPORT jboolean JNICALL Java_com_formdev_flatlaf_ui_FlatNativeMacLibrary_setWindowButtonsSpacing
	( JNIEnv* env, jclass cls, jobject window, jint buttonsSpacing )
{
	JNI_COCOA_ENTER()

	NSWindow* nsWindow = getNSWindow( env, cls, window );
	if( nsWindow == NULL )
		return FALSE;

	#define SPACING_DEFAULT com_formdev_flatlaf_ui_FlatNativeMacLibrary_BUTTONS_SPACING_DEFAULT
	#define SPACING_MEDIUM  com_formdev_flatlaf_ui_FlatNativeMacLibrary_BUTTONS_SPACING_MEDIUM
	#define SPACING_LARGE   com_formdev_flatlaf_ui_FlatNativeMacLibrary_BUTTONS_SPACING_LARGE

	bool isMacOS_11_orLater = @available( macOS 11, * );
	if( !isMacOS_11_orLater && buttonsSpacing == SPACING_LARGE )
		buttonsSpacing = SPACING_MEDIUM;
	int oldButtonsSpacing = (nsWindow.toolbar != NULL)
		? ((isMacOS_11_orLater && nsWindow.toolbarStyle == NSWindowToolbarStyleUnified)
			? SPACING_LARGE
			: SPACING_MEDIUM)
		: SPACING_DEFAULT;

	if( buttonsSpacing == oldButtonsSpacing )
		return TRUE;

	WindowData* windowData = getWindowData( nsWindow, true );

	[FlatJNFRunLoop performOnMainThreadWaiting:YES withBlock:^(){
		JNI_COCOA_TRY()

//		NSLog( @"\n%@\n\n", [nsWindow.contentView.superview _subtreeDescription] );

		// add/remove toolbar
		NSToolbar* toolbar = NULL;
		bool needsToolbar = (buttonsSpacing != SPACING_DEFAULT);
		if( needsToolbar ) {
			toolbar = [NSToolbar new];
			toolbar.showsBaselineSeparator = NO; // necessary for older macOS versions
			if( isWindowFullScreen( nsWindow ) )
				toolbar.visible = NO;
		}
		nsWindow.toolbar = toolbar;

		if( isMacOS_11_orLater ) {
			nsWindow.toolbarStyle = (buttonsSpacing == SPACING_LARGE)
				? NSWindowToolbarStyleUnified
				: (buttonsSpacing == SPACING_MEDIUM)
					? NSWindowToolbarStyleUnifiedCompact
					: NSWindowToolbarStyleAutomatic;
		}

		windowData.lastWindowButtonAreaWidth = 0;
		windowData.lastWindowTitleBarHeight = 0;

//		NSLog( @"\n%@\n\n", [nsWindow.contentView.superview _subtreeDescription] );

		// when window becomes full screen, it is necessary to hide the toolbar
		// because it otherwise is shown non-transparent and hides Swing components
		NSNotificationCenter* center = [NSNotificationCenter defaultCenter];
		if( needsToolbar && windowData.willEnterFullScreenObserver == NULL ) {
//			NSLog( @"add observers %@", nsWindow );
			windowData.willEnterFullScreenObserver = [center addObserverForName:NSWindowWillEnterFullScreenNotification
				object:nsWindow queue:nil usingBlock:^(NSNotification *note) {
//					NSLog( @"enter full screen %@", nsWindow );
					if( nsWindow.toolbar != NULL ) {
						// remember button area width, which is used later when window exits full screen
						// remembar title bar height so that "main" JToolBar keeps its height in full screen
						windowData.lastWindowButtonAreaWidth = getWindowButtonAreaWidth( nsWindow );
						windowData.lastWindowTitleBarHeight = getWindowTitleBarHeight( nsWindow );
//						NSLog( @"%d %d", windowData.lastWindowButtonAreaWidth, windowData.lastWindowTitleBarHeight );

						nsWindow.toolbar.visible = NO;
					}
				}];

			windowData.willExitFullScreenObserver = [center addObserverForName:NSWindowWillExitFullScreenNotification
				object:nsWindow queue:nil usingBlock:^(NSNotification *note) {
//					NSLog( @"will exit  full screen %@", nsWindow );
					if( nsWindow.toolbar != NULL )
						setWindowButtonsHidden( nsWindow, true );
				}];

			windowData.didExitFullScreenObserver = [center addObserverForName:NSWindowDidExitFullScreenNotification
				object:nsWindow queue:nil usingBlock:^(NSNotification *note) {
//					NSLog( @"exit  full screen %@", nsWindow );
					if( nsWindow.toolbar != NULL ) {
						setWindowButtonsHidden( nsWindow, false );
						nsWindow.toolbar.visible = YES;
					}

					windowData.lastWindowButtonAreaWidth = 0;
					windowData.lastWindowTitleBarHeight = 0;
				}];
		} else if( !needsToolbar ) {
//			NSLog( @"remove observers %@", nsWindow );
			if( windowData.willEnterFullScreenObserver != NULL ) {
				[center removeObserver:windowData.willEnterFullScreenObserver];
				windowData.willEnterFullScreenObserver = nil;
			}
			if( windowData.willExitFullScreenObserver != NULL ) {
				[center removeObserver:windowData.willExitFullScreenObserver];
				windowData.willExitFullScreenObserver = nil;
			}
			if( windowData.didExitFullScreenObserver != NULL ) {
				[center removeObserver:windowData.didExitFullScreenObserver];
				windowData.didExitFullScreenObserver = nil;
			}
		}

		JNI_COCOA_CATCH()
	}];

	return TRUE;

	JNI_COCOA_EXIT()
	return FALSE;
}

void setWindowButtonsHidden( NSWindow* nsWindow, bool hidden ) {
	// get buttons
	NSView* buttons[3] = {
		[nsWindow standardWindowButton:NSWindowCloseButton],
		[nsWindow standardWindowButton:NSWindowMiniaturizeButton],
		[nsWindow standardWindowButton:NSWindowZoomButton]
	};

	for( int i = 0; i < 3; i++ ) {
		NSView* button = buttons[i];
		if( button != NULL )
			button.hidden = hidden;
	}
}

extern "C"
JNIEXPORT jobject JNICALL Java_com_formdev_flatlaf_ui_FlatNativeMacLibrary_getWindowButtonsBounds
	( JNIEnv* env, jclass cls, jobject window )
{
	JNI_COCOA_ENTER()

	NSWindow* nsWindow = getNSWindow( env, cls, window );
	if( nsWindow == NULL )
		return NULL;

	WindowData* windowData = getWindowData( nsWindow, false );
	int width = 0;
	int height = 0;

	// get width
	if( isWindowFullScreen( nsWindow ) ) {
		// use zero if window is full screen because close/minimize/zoom buttons are hidden
		width = 0;
	} else if( windowData != NULL && windowData.lastWindowButtonAreaWidth > 0 ) {
		// use remembered value if window is in transition from full screen to non-full screen
		// because NSToolbar is not yet visible
		width = windowData.lastWindowButtonAreaWidth;
	} else
		width = getWindowButtonAreaWidth( nsWindow );

	// get height
	if( windowData != NULL && windowData.lastWindowTitleBarHeight > 0 ) {
		// use remembered value if window is full screen because NSToolbar is hidden
		height = windowData.lastWindowTitleBarHeight;
	} else
		height = getWindowTitleBarHeight( nsWindow );

	// initialize class and method ID (done only once because variables are static)
	static jclass cls = findClass( env, "java/awt/Rectangle", true );
	static jmethodID methodID = getMethodID( env, cls, "<init>", "(IIII)V", false );
	if( cls == NULL || methodID == NULL )
		return NULL;

	// create and return Rectangle
	return env->NewObject( cls, methodID, 0, 0, width, height );

	JNI_COCOA_EXIT()
	return NULL;
}

int getWindowButtonAreaWidth( NSWindow* nsWindow ) {
	// get buttons
	NSView* buttons[3] = {
		[nsWindow standardWindowButton:NSWindowCloseButton],
		[nsWindow standardWindowButton:NSWindowMiniaturizeButton],
		[nsWindow standardWindowButton:NSWindowZoomButton]
	};

	// get most left and right coordinates
	int left = -1;
	int right = -1;
	for( int i = 0; i < 3; i++ ) {
		NSView* button = buttons[i];
		if( button == NULL )
			continue;

		int x = [button convertRect: [button bounds] toView:button.superview].origin.x;
		int width = button.bounds.size.width;
		if( left == -1 || x < left )
			left = x;
		if( right == -1 || x + width > right )
			right = x + width;
	}

	if( left == -1 || right == -1 )
		return -1;

	// 'right' is the actual button area width (from left window edge)
	// adding 'left' to add same empty space on right side as on left side
	return right + left;
}

int getWindowTitleBarHeight( NSWindow* nsWindow ) {
	NSView* closeButton = [nsWindow standardWindowButton:NSWindowCloseButton];
	if( closeButton == NULL )
		return -1;

	NSView* titlebar = closeButton.superview;
	return titlebar.bounds.size.height;
}

extern "C"
JNIEXPORT jboolean JNICALL Java_com_formdev_flatlaf_ui_FlatNativeMacLibrary_isWindowFullScreen
	( JNIEnv* env, jclass cls, jobject window )
{
	JNI_COCOA_ENTER()

	NSWindow* nsWindow = getNSWindow( env, cls, window );
	if( nsWindow == NULL )
		return FALSE;

	return (jboolean) isWindowFullScreen( nsWindow );

	JNI_COCOA_EXIT()
	return FALSE;
}

bool isWindowFullScreen( NSWindow* nsWindow ) {
	return ((nsWindow.styleMask & NSWindowStyleMaskFullScreen) != 0);
}

extern "C"
JNIEXPORT jboolean JNICALL Java_com_formdev_flatlaf_ui_FlatNativeMacLibrary_toggleWindowFullScreen
	( JNIEnv* env, jclass cls, jobject window )
{
	JNI_COCOA_ENTER()

	NSWindow* nsWindow = getNSWindow( env, cls, window );
	if( nsWindow == NULL )
		return FALSE;

	[FlatJNFRunLoop performOnMainThreadWaiting:NO withBlock:^(){
		[nsWindow toggleFullScreen:nil];
	}];

	return TRUE;

	JNI_COCOA_EXIT()
	return FALSE;
}
