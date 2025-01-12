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

// declare internal methods
static jobjectArray newJavaStringArray( JNIEnv* env, jsize count );
static jobjectArray urlsToStringArray( JNIEnv* env, NSArray* urls );
static NSArray* getDialogURLs( NSSavePanel* dialog );

//---- class FileChooserDelegate ----------------------------------------------

@interface FileChooserDelegate : NSObject <NSOpenSavePanelDelegate> {
		NSArray* _filters;

		JavaVM* _jvm;
		jobject _callback;
		NSMutableSet* _urlsSet;
	}

	@property (nonatomic, assign) NSSavePanel* dialog;

	- (void)initFilterAccessoryView: (NSMutableArray*)filters :(int)filterIndex
		:(NSString*)filterFieldLabel :(bool)showSingleFilterField;
	- (void)selectFormat: (id)sender;
	- (void)selectFormatAtIndex: (int)index;
@end

@implementation FileChooserDelegate

	- (void)initFilterAccessoryView: (NSMutableArray*)filters :(int)filterIndex
		:(NSString*)filterFieldLabel :(bool)showSingleFilterField
	{
		_filters = filters;

		// get filter names
		NSArray* filterNames = filters.lastObject;
		[filters removeLastObject];

		// do not add filter/format combobox if there is only one filter
		if( filters.count <= 1 && !showSingleFilterField ) {
			[self selectFormatAtIndex:0];
			return;
		}

		// create label
		NSTextField* label = [[NSTextField alloc] initWithFrame:NSZeroRect];
		label.stringValue = (filterFieldLabel != NULL) ? filterFieldLabel : @"Format:";
		label.editable = NO;
		label.bordered = NO;
		label.bezeled = NO;
		label.drawsBackground = NO;

		// create combobox
		NSPopUpButton* popupButton = [[NSPopUpButton alloc] initWithFrame:NSZeroRect pullsDown:NO];
		[popupButton addItemsWithTitles:filterNames];
		[popupButton selectItemAtIndex:MIN( MAX( filterIndex, 0 ), filterNames.count - 1 )];
		[popupButton setTarget:self];
		[popupButton setAction:@selector(selectFormat:)];

		// create view
		NSView* accessoryView = [[NSView alloc] initWithFrame:NSZeroRect];
		[accessoryView addSubview:label];
		[accessoryView addSubview:popupButton];

		// autolayout
		label.translatesAutoresizingMaskIntoConstraints = NO;
		popupButton.translatesAutoresizingMaskIntoConstraints = NO;
		int labelWidth = label.intrinsicContentSize.width;
		int gap = 12;
		int popupButtonWidth = popupButton.intrinsicContentSize.width;
		int popupButtonMinimumWidth = 140;
		int totalWidth = labelWidth + gap + MAX( popupButtonWidth, popupButtonMinimumWidth );
		[accessoryView addConstraints:@[
			// horizontal layout
			[label.leadingAnchor constraintEqualToAnchor:accessoryView.centerXAnchor constant:-(totalWidth / 2)],
			[popupButton.leadingAnchor constraintEqualToAnchor:label.trailingAnchor constant:gap],
			[popupButton.widthAnchor constraintGreaterThanOrEqualToConstant:popupButtonMinimumWidth],

			// vertical layout
			[popupButton.topAnchor constraintEqualToAnchor:accessoryView.topAnchor constant:8],
			[popupButton.bottomAnchor constraintEqualToAnchor:accessoryView.bottomAnchor constant:-8],
			[label.firstBaselineAnchor constraintEqualToAnchor:popupButton.firstBaselineAnchor],
		]];

		[_dialog setAccessoryView:accessoryView];

		// initial filter
		[self selectFormatAtIndex:filterIndex];
	}

	- (void)selectFormat:(id)sender {
		NSPopUpButton* popupButton = (NSPopUpButton*) sender;
		[self selectFormatAtIndex:popupButton.indexOfSelectedItem];
	}

	- (void)selectFormatAtIndex: (int)index {
		index = MIN( MAX( index, 0 ), _filters.count - 1 );
		NSArray* fileTypes = [_filters objectAtIndex:index];

		// use deprecated allowedFileTypes instead of newer allowedContentTypes (since macOS 11+)
		// to support older macOS versions 10.14+ and because of some problems with allowedContentTypes:
		// https://github.com/chromium/chromium/blob/d8e0032963b7ca4728ff4117933c0feb3e479b7a/components/remote_cocoa/app_shim/select_file_dialog_bridge.mm#L209-232
		_dialog.allowedFileTypes = [fileTypes containsObject:@"*"] ? nil : fileTypes;
	}

	//---- NSOpenSavePanelDelegate ----

	- (void)initCallback: (JavaVM*)jvm :(jobject)callback {
		_jvm = jvm;
		_callback = callback;
	}

	- (BOOL) panel:(id) sender validateURL:(NSURL*) url error:(NSError**) outError {
		JNI_COCOA_TRY()

		NSArray* urls = getDialogURLs( sender );

		// if multiple files are selected for opening, then the validateURL method
		// is invoked for earch file, but our callback should be invoked only once for all files
		if( urls != NULL && urls.count > 1 ) {
			if( _urlsSet == NULL ) {
				// invoked for first selected file --> invoke callback
				_urlsSet = [NSMutableSet setWithArray:urls];
				[_urlsSet removeObject:url];
			} else {
				// invoked for other selected files --> do not invoke callback
				[_urlsSet removeObject:url];
				if( _urlsSet.count == 0 )
					_urlsSet = NULL;
				return true;
			}
		}

		JNI_THREAD_ENTER( _jvm, true )

		jobjectArray files = urlsToStringArray( env, urls );
		jlong window = (jlong) sender;

		// invoke callback: boolean approve( String[] files, long hwnd );
		jclass cls = env->GetObjectClass( _callback );
		jmethodID approveID = env->GetMethodID( cls, "approve", "([Ljava/lang/String;J)Z" );
		if( approveID != NULL && !env->CallBooleanMethod( _callback, approveID, files, window ) ) {
			_urlsSet = NULL;
			return false; // keep dialog open
		}

		JNI_THREAD_EXIT( _jvm )
		JNI_COCOA_CATCH()

		return true;
	}

@end

//---- helper -----------------------------------------------------------------

#define isOptionSet( option ) ((optionsSet & com_formdev_flatlaf_ui_FlatNativeMacLibrary_ ## option) != 0)
#define isOptionClear( option ) ((optionsClear & com_formdev_flatlaf_ui_FlatNativeMacLibrary_ ## option) != 0)
#define isOptionSetOrClear( option ) isOptionSet( option ) || isOptionClear( option )

static jobjectArray newJavaStringArray( JNIEnv* env, jsize count ) {
	jclass stringClass = env->FindClass( "java/lang/String" );
	return env->NewObjectArray( count, stringClass, NULL );
}

static NSMutableArray* initFilters( JNIEnv* env, jobjectArray fileTypes ) {
	jint length = env->GetArrayLength( fileTypes );
	if( length <= 0 )
		return NULL;

	NSMutableArray* filterNames = [NSMutableArray array];
	NSMutableArray* filters = [NSMutableArray array];
	NSString* filterName = NULL;
	NSMutableArray* filter = NULL;
	for( int i = 0; i < length; i++ ) {
		jstring jstr = (jstring) env->GetObjectArrayElement( fileTypes, i );
		if( jstr == NULL ) {
			if( filter != NULL ) {
				if( filter.count > 0 ) {
					[filterNames addObject:filterName];
					[filters addObject:filter];
				}
				filterName = NULL;
				filter = NULL;
			}
			continue;
		}

		NSString* str = JavaToNSString( env, jstr );
		env->DeleteLocalRef( jstr );
		if( filter == NULL ) {
			filterName = str;
			filter = [NSMutableArray array];
		} else
			[filter addObject:str];
	}

	if( filters.count == 0 )
		return NULL;

	// add filter names to array (removed again after creating combobox)
	[filters addObject:filterNames];

	return filters;
}

//---- JNI methods ------------------------------------------------------------

extern "C"
JNIEXPORT jobjectArray JNICALL Java_com_formdev_flatlaf_ui_FlatNativeMacLibrary_showFileChooser
	( JNIEnv* env, jclass cls, jboolean open,
		jstring title, jstring prompt, jstring message, jstring filterFieldLabel,
		jstring nameFieldLabel, jstring nameFieldStringValue, jstring directoryURL,
		jint optionsSet, jint optionsClear, jobject callback, jint fileTypeIndex, jobjectArray fileTypes )
{
	JNI_COCOA_ENTER()

	JavaVM* jvm;
	if( env->GetJavaVM( &jvm ) != JNI_OK )
		return NULL;

	// convert Java strings to NSString (on Java thread)
	NSString* nsTitle = JavaToNSString( env, title );
	NSString* nsPrompt = JavaToNSString( env, prompt );
	NSString* nsMessage = JavaToNSString( env, message );
	NSString* nsFilterFieldLabel = JavaToNSString( env, filterFieldLabel );
	NSString* nsNameFieldLabel = JavaToNSString( env, nameFieldLabel );
	NSString* nsNameFieldStringValue = JavaToNSString( env, nameFieldStringValue );
	NSString* nsDirectoryURL = JavaToNSString( env, directoryURL );
	NSMutableArray* filters = initFilters( env, fileTypes );

	NSArray* urls = NULL;
	NSArray** purls = &urls;

	// show file dialog on macOS thread
	[FlatJNFRunLoop performOnMainThreadWaiting:YES withBlock:^(){
		JNI_COCOA_TRY()

		NSSavePanel* dialog = open ? [NSOpenPanel openPanel] : [NSSavePanel savePanel];

		if( nsTitle != NULL )
			dialog.title = nsTitle;
		if( nsPrompt != NULL )
			dialog.prompt = nsPrompt;
		if( nsMessage != NULL )
			dialog.message = nsMessage;
		if( nsNameFieldLabel != NULL )
			dialog.nameFieldLabel = nsNameFieldLabel;
		if( nsNameFieldStringValue != NULL )
			dialog.nameFieldStringValue = nsNameFieldStringValue;
		if( nsDirectoryURL != NULL )
			dialog.directoryURL = [NSURL fileURLWithPath:nsDirectoryURL isDirectory:YES];

		// set open options
		if( open ) {
			NSOpenPanel* openDialog = (NSOpenPanel*) dialog;

			bool canChooseFiles = isOptionSet( FC_canChooseFiles );
			bool canChooseDirectories = isOptionSet( FC_canChooseDirectories );
			if( !canChooseFiles && !canChooseDirectories )
				canChooseFiles = true;
			openDialog.canChooseFiles = canChooseFiles;
			openDialog.canChooseDirectories = canChooseDirectories;

			if( isOptionSetOrClear( FC_resolvesAliases ) )
				openDialog.resolvesAliases = isOptionSet( FC_resolvesAliases );
			if( isOptionSetOrClear( FC_allowsMultipleSelection ) )
				openDialog.allowsMultipleSelection = isOptionSet( FC_allowsMultipleSelection );
		}

		// set options
		if( isOptionSetOrClear( FC_showsTagField ) )
			dialog.showsTagField = isOptionSet( FC_showsTagField );
		if( isOptionSetOrClear( FC_canCreateDirectories ) )
			dialog.canCreateDirectories = isOptionSet( FC_canCreateDirectories );
		if( isOptionSetOrClear( FC_canSelectHiddenExtension ) )
			dialog.canSelectHiddenExtension = isOptionSet( FC_canSelectHiddenExtension );
		if( isOptionSetOrClear( FC_showsHiddenFiles) )
			dialog.showsHiddenFiles = isOptionSet( FC_showsHiddenFiles);
		if( isOptionSetOrClear( FC_extensionHidden ) )
			dialog.extensionHidden = isOptionSet( FC_extensionHidden );
		if( isOptionSetOrClear( FC_allowsOtherFileTypes ) )
			dialog.allowsOtherFileTypes = isOptionSet( FC_allowsOtherFileTypes );
		if( isOptionSetOrClear( FC_treatsFilePackagesAsDirectories ) )
			dialog.treatsFilePackagesAsDirectories = isOptionSet( FC_treatsFilePackagesAsDirectories );

		FileChooserDelegate* delegate = [FileChooserDelegate new];
		delegate.dialog = dialog;

		// initialize filter accessory view
		if( filters != NULL ) {
			[delegate initFilterAccessoryView:filters :fileTypeIndex :nsFilterFieldLabel :isOptionSet( FC_showSingleFilterField )];

			if( open && isOptionSetOrClear( FC_accessoryViewDisclosed ) )
				((NSOpenPanel*)dialog).accessoryViewDisclosed = isOptionSet( FC_accessoryViewDisclosed );
		}

		// initialize callback
		if( callback != NULL ) {
			[delegate initCallback :jvm :callback];
			dialog.delegate = delegate;
		}

		// show dialog
		NSModalResponse response = [dialog runModal];
		[delegate release];
		if( response != NSModalResponseOK ) {
			*purls = @[];
			return;
		}

		*purls = getDialogURLs( dialog );

		JNI_COCOA_CATCH()
	}];

	if( urls == NULL )
		return NULL;

	// convert URLs to Java string array
	return urlsToStringArray( env, urls );

	JNI_COCOA_EXIT()
}

static NSArray* getDialogURLs( NSSavePanel* dialog ) {
	if( [dialog isKindOfClass:[NSOpenPanel class]] )
		return static_cast<NSOpenPanel*>(dialog).URLs;

	NSURL* url = dialog.URL;
	// use '[[NSArray alloc] initWithObject:url]' here because '@[url]' crashes on macOS 10.14
	return (url != NULL) ? [[NSArray alloc] initWithObject:url] : @[];
}

static jobjectArray urlsToStringArray( JNIEnv* env, NSArray* urls ) {
	jsize count = (urls != NULL) ? urls.count : 0;
	jobjectArray array = newJavaStringArray( env, count );
	for( int i = 0; i < count; i++ ) {
		jstring filename = NormalizedPathJavaFromNSString( env, [urls[i] path] );
		env->SetObjectArrayElement( array, i, filename );
		env->DeleteLocalRef( filename );
	}
	return array;
}

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
