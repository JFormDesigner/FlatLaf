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

// avoid inlining of printf()
#define _NO_CRT_STDIO_INLINE

#include <windows.h>
#include <shobjidl.h>
#include "com_formdev_flatlaf_ui_FlatNativeWindowsLibrary.h"

/**
 * @author Karl Tauber
 * @since 3.6
 */

// see FlatWndProc.cpp
HWND getWindowHandle( JNIEnv* env, jobject window );

//---- class AutoReleasePtr ---------------------------------------------------

template<class T> class AutoReleasePtr {
	T* ptr;

public:
	AutoReleasePtr() {
		ptr = NULL;
	}
	~AutoReleasePtr() {
		if( ptr != NULL )
			ptr->Release();
	}
	T** operator&() { return &ptr; }
	T* operator->() { return ptr; }
	operator T*() { return ptr; }
};

//---- class AutoReleaseString ------------------------------------------------

class AutoReleaseString {
	JNIEnv* env;
	jstring javaString;
	const jchar* chars;

public:
	AutoReleaseString( JNIEnv* _env, jstring _javaString ) {
		env = _env;
		javaString = _javaString;
		chars = (javaString != NULL) ? env->GetStringChars( javaString, NULL ) : NULL;
	}
	~AutoReleaseString() {
		if( chars != NULL )
			env->ReleaseStringChars( javaString, chars );
	}
	operator LPCWSTR() { return (LPCWSTR) chars; }
};

//---- class AutoReleaseIShellItem --------------------------------------------

class AutoReleaseIShellItem : public AutoReleasePtr<IShellItem> {
public:
	AutoReleaseIShellItem( JNIEnv* env, jstring path ) {
		AutoReleaseString cpath( env, path );
		::SHCreateItemFromParsingName( cpath, NULL, IID_IShellItem, reinterpret_cast<void**>( &*this ) );
	}
};

//---- class FilterSpec -------------------------------------------------------

class FilterSpec {
	JNIEnv* env = NULL;
	jstring* jnames = NULL;
	jstring* jspecs = NULL;

public:
	UINT count = 0;
	COMDLG_FILTERSPEC* specs = NULL;

public:
	FilterSpec( JNIEnv* _env, jobjectArray fileTypes ) {
		if( fileTypes == NULL )
			return;

		env = _env;
		count = env->GetArrayLength( fileTypes ) / 2;
		if( count <= 0 )
			return;

		specs = new COMDLG_FILTERSPEC[count];
		jnames = new jstring[count];
		jspecs = new jstring[count];

		for( int i = 0; i < count; i++ ) {
			jnames[i] = (jstring) env->GetObjectArrayElement( fileTypes, i * 2 );
			jspecs[i] = (jstring) env->GetObjectArrayElement( fileTypes, (i * 2) + 1 );
			specs[i].pszName = (LPCWSTR) env->GetStringChars( jnames[i] , NULL );
			specs[i].pszSpec = (LPCWSTR) env->GetStringChars( jspecs[i], NULL );
		}
	}
	~FilterSpec() {
		if( specs == NULL )
			return;

		for( int i = 0; i < count; i++ ) {
			env->ReleaseStringChars( jnames[i], (jchar *) specs[i].pszName );
			env->ReleaseStringChars( jspecs[i], (jchar *) specs[i].pszSpec );
			env->DeleteLocalRef( jnames[i] );
			env->DeleteLocalRef( jspecs[i] );
		}

		delete[] jnames;
		delete[] jspecs;
		delete[] specs;
	}
};

//---- class CoInitializer ----------------------------------------------------

class CoInitializer {
public:
	bool initialized;

	CoInitializer() {
		HRESULT result = ::CoInitializeEx( NULL, COINIT_APARTMENTTHREADED | COINIT_DISABLE_OLE1DDE );
		initialized = SUCCEEDED( result );
	}
	~CoInitializer() {
		if( initialized )
			::CoUninitialize();
	}
};

//---- helper -----------------------------------------------------------------

#define isOptionSet( option ) ((optionsSet & com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_ ## option) != 0)
#define CHECK_HRESULT( code ) { if( (code) != S_OK ) return NULL; }

jobjectArray newJavaStringArray( JNIEnv* env, jsize count ) {
	jclass stringClass = env->FindClass( "java/lang/String" );
	return env->NewObjectArray( count, stringClass, NULL );
}

jstring newJavaString( JNIEnv* env, LPWSTR str ) {
	return env->NewString( reinterpret_cast<jchar*>( str ), static_cast<jsize>( wcslen( str ) ) );
}

//---- JNI methods ------------------------------------------------------------

extern "C"
JNIEXPORT jobjectArray JNICALL Java_com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_showFileChooser
	( JNIEnv* env, jclass cls, jobject owner, jboolean open,
		jstring title, jstring okButtonLabel, jstring fileNameLabel, jstring fileName,
		jstring folder, jstring saveAsItem, jstring defaultFolder, jstring defaultExtension,
		jint optionsSet, jint optionsClear, jint fileTypeIndex, jobjectArray fileTypes )
{
	// initialize COM library
	CoInitializer coInitializer;
	if( !coInitializer.initialized )
		return NULL;

	// handle limitations (without this, some Win32 method fails and this method returns NULL)
	if( isOptionSet( FOS_PICKFOLDERS ) ) {
		open = true; // always use IFileOpenDialog for picking folders
		fileTypes = NULL; // no filter allowed for picking folders
	}
	if( !open && isOptionSet( FOS_ALLOWMULTISELECT ) )
		optionsSet &= ~FOS_ALLOWMULTISELECT;

	// convert Java strings to C strings
	AutoReleaseString ctitle( env, title );
	AutoReleaseString cokButtonLabel( env, okButtonLabel );
	AutoReleaseString cfileNameLabel( env, fileNameLabel );
	AutoReleaseString cfileName( env, fileName );
	AutoReleaseIShellItem cfolder( env, folder );
	AutoReleaseIShellItem csaveAsItem( env, saveAsItem );
	AutoReleaseIShellItem cdefaultFolder( env, defaultFolder );
	AutoReleaseString cdefaultExtension( env, defaultExtension );
	FilterSpec specs( env, fileTypes );

	// create IFileOpenDialog or IFileSaveDialog
	// https://learn.microsoft.com/en-us/windows/win32/shell/common-file-dialog
	AutoReleasePtr<IFileDialog> dialog;
	CHECK_HRESULT( ::CoCreateInstance( open ? CLSID_FileOpenDialog : CLSID_FileSaveDialog,
		NULL, CLSCTX_INPROC_SERVER, open ? IID_IFileOpenDialog : IID_IFileSaveDialog,
		reinterpret_cast<LPVOID*>( &dialog ) ) );

	// set title, etc.
	if( ctitle != NULL )
		CHECK_HRESULT( dialog->SetTitle( ctitle ) );
	if( cokButtonLabel != NULL )
		CHECK_HRESULT( dialog->SetOkButtonLabel( cokButtonLabel ) );
	if( cfileNameLabel != NULL )
		CHECK_HRESULT( dialog->SetFileNameLabel( cfileNameLabel ) );
	if( cfileName != NULL )
		CHECK_HRESULT( dialog->SetFileName( cfileName ) );
	if( cfolder != NULL )
		CHECK_HRESULT( dialog->SetFolder( cfolder ) );
	if( !open && csaveAsItem != NULL )
		CHECK_HRESULT( ((IFileSaveDialog*)(IFileDialog*)dialog)->SetSaveAsItem( csaveAsItem ) );
	if( cdefaultFolder != NULL )
		CHECK_HRESULT( dialog->SetDefaultFolder( cdefaultFolder ) );
	if( cdefaultExtension != NULL )
		CHECK_HRESULT( dialog->SetDefaultExtension( cdefaultExtension ) );

	// set options
	FILEOPENDIALOGOPTIONS existingOptions;
	CHECK_HRESULT( dialog->GetOptions( &existingOptions ) );
	CHECK_HRESULT( dialog->SetOptions ( (existingOptions & ~optionsClear) | optionsSet ) );

	// initialize filter
	if( specs.count > 0 ) {
		CHECK_HRESULT( dialog->SetFileTypes( specs.count, specs.specs ) );
		if( fileTypeIndex > 0 )
			CHECK_HRESULT( dialog->SetFileTypeIndex( min( fileTypeIndex + 1, specs.count ) ) );
	}

	// show dialog
	HWND hwndOwner = (owner != NULL) ? getWindowHandle( env, owner ) : NULL;
	HRESULT hr = dialog->Show( hwndOwner );
	if( hr == HRESULT_FROM_WIN32(ERROR_CANCELLED) )
		return newJavaStringArray( env, 0 );
	CHECK_HRESULT( hr );

	// convert shell items to Java string array
	if( open ) {
		AutoReleasePtr<IShellItemArray> shellItems;
		DWORD count;
		CHECK_HRESULT( ((IFileOpenDialog*)(IFileDialog*)dialog)->GetResults( &shellItems ) );
		CHECK_HRESULT( shellItems->GetCount( &count ) );

		jobjectArray array = newJavaStringArray( env, count );
		for( int i = 0; i < count; i++ ) {
			AutoReleasePtr<IShellItem> shellItem;
			LPWSTR path;
			CHECK_HRESULT( shellItems->GetItemAt( i, &shellItem ) );
			CHECK_HRESULT( shellItem->GetDisplayName( SIGDN_FILESYSPATH, &path ) );

			jstring jpath = newJavaString( env, path );
			CoTaskMemFree( path );

			env->SetObjectArrayElement( array, i, jpath );
			env->DeleteLocalRef( jpath );
		}
		return array;
	} else {
		AutoReleasePtr<IShellItem> shellItem;
		LPWSTR path;
		CHECK_HRESULT( dialog->GetResult( &shellItem ) );
		CHECK_HRESULT( shellItem->GetDisplayName( SIGDN_FILESYSPATH, &path ) );

		jstring jpath = newJavaString( env, path );
		CoTaskMemFree( path );

		jobjectArray array = newJavaStringArray( env, 1 );
		env->SetObjectArrayElement( array, 0, jpath );
		env->DeleteLocalRef( jpath );

		return array;
	}
}
