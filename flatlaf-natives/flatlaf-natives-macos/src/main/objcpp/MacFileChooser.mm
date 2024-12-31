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
 */

#define isOptionSet( option ) ((optionsSet & com_formdev_flatlaf_ui_FlatNativeMacLibrary_ ## option) != 0)
#define isOptionClear( option ) ((optionsClear & com_formdev_flatlaf_ui_FlatNativeMacLibrary_ ## option) != 0)
#define isOptionSetOrClear( option ) isOptionSet( option ) || isOptionClear( option )

// declare internal methods
NSWindow* getNSWindow( JNIEnv* env, jclass cls, jobject window );

//---- JNI methods ------------------------------------------------------------

extern "C"
JNIEXPORT jobjectArray JNICALL Java_com_formdev_flatlaf_ui_FlatNativeMacLibrary_showFileChooser
	( JNIEnv* env, jclass cls, jboolean open,
		jstring title, jstring prompt, jstring message, jstring nameFieldLabel,
		jstring nameFieldStringValue, jstring directoryURL,
		jint optionsSet, jint optionsClear, jobjectArray allowedFileTypes )
{
	JNI_COCOA_ENTER()

	// convert Java strings to NSString (on Java thread)
	NSString* nsTitle = JavaToNSString( env, title );
	NSString* nsPrompt = JavaToNSString( env, prompt );
	NSString* nsMessage = JavaToNSString( env, message );
	NSString* nsNameFieldLabel = JavaToNSString( env, nameFieldLabel );
	NSString* nsNameFieldStringValue = JavaToNSString( env, nameFieldStringValue );
	NSString* nsDirectoryURL = JavaToNSString( env, directoryURL );

	NSArray* nsAllowedFileTypes = NULL;
	jsize len = env->GetArrayLength( allowedFileTypes );
	if( len > 0 ) {
		NSMutableArray* nsArray = [NSMutableArray arrayWithCapacity:len];
		for( int i = 0; i < len; i++ ) {
			jstring str = (jstring) env->GetObjectArrayElement( allowedFileTypes, i );
			nsArray[i] = JavaToNSString( env, str );
			env->DeleteLocalRef( str );
		}
		nsAllowedFileTypes = nsArray;
	}

	NSArray* urls = NULL;
	NSArray** purls = &urls;
	NSURL* url = NULL;
	NSURL** purl = &url;

	// show file dialog on macOS thread
	[FlatJNFRunLoop performOnMainThreadWaiting:YES withBlock:^(){
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

		// use deprecated allowedFileTypes instead of newer allowedContentTypes (since macOS 11+)
		// to support older macOS versions 10.14+ and because of some problems with allowedContentTypes:
		// https://github.com/chromium/chromium/blob/d8e0032963b7ca4728ff4117933c0feb3e479b7a/components/remote_cocoa/app_shim/select_file_dialog_bridge.mm#L209-232
		if( nsAllowedFileTypes != NULL )
			dialog.allowedFileTypes = nsAllowedFileTypes;

		if( [dialog runModal] != NSModalResponseOK )
			return;

		if( open )
			*purls = ((NSOpenPanel*)dialog).URLs;
		else
			*purl = dialog.URL;
	}];

	if( url != NULL )
		urls = @[url];

	if( urls == NULL )
		return NULL;

	// convert URLs to Java string array
	jsize count = urls.count;
	jclass stringClass = env->FindClass( "java/lang/String" );
	jobjectArray result = env->NewObjectArray( count, stringClass, NULL );
	for( int i = 0; i < count; i++ ) {
		jstring filename = NormalizedPathJavaFromNSString( env, [urls[i] path] );
		env->SetObjectArrayElement( result, i, filename );
		env->DeleteLocalRef( filename );
	}

	return result;

	JNI_COCOA_EXIT()
}

