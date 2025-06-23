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
#include "JNIUtils.h"
#include "com_formdev_flatlaf_ui_FlatNativeWindowsLibrary.h"

/**
 * @author Karl Tauber
 * @since 3.6
 */

// declare external methods
extern HWND getWindowHandle( JNIEnv* env, jobject window );

// declare internal methods
static jobjectArray getFiles( JNIEnv* env, jboolean open, IFileDialog* dialog );

//---- class AutoReleasePtr ---------------------------------------------------

template<class T> class AutoReleasePtr {
	T* ptr;

public:
	AutoReleasePtr() {
		ptr = NULL;
	}
	AutoReleasePtr( T* p ) {
		ptr = p;
		ptr->AddRef();
	}
	~AutoReleasePtr() {
		if( ptr != NULL )
			ptr->Release();
	}
	T** operator&() { return &ptr; }
	T* operator->() { return ptr; }
	operator T*() { return ptr; }
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
	AutoReleaseStringArray fileTypes;

public:
	UINT count = 0;
	COMDLG_FILTERSPEC* specs = NULL;

public:
	FilterSpec( JNIEnv* _env, jobjectArray _fileTypes )
		: fileTypes( _env, _fileTypes )
	{
		if( fileTypes.count == 0 )
			return;

		count = fileTypes.count / 2;
		specs = new COMDLG_FILTERSPEC[fileTypes.count];

		for( int i = 0; i < count; i++ ) {
			specs[i].pszName = fileTypes[i * 2];
			specs[i].pszSpec = fileTypes[(i * 2) + 1];
		}
	}
	~FilterSpec() {
		if( specs != NULL )
			delete[] specs;
	}
};

//---- class DialogEventHandler -----------------------------------------------

// see https://github.com/microsoft/Windows-classic-samples/blob/main/Samples/Win7Samples/winui/shell/appplatform/commonfiledialog/CommonFileDialogApp.cpp

class DialogEventHandler : public IFileDialogEvents {
	JNIEnv* env;
	jboolean open;
	jobject callback;
	LONG refCount = 1;

public:
	DialogEventHandler( JNIEnv* _env, jboolean _open, jobject _callback ) {
		env = _env;
		open = _open;
		callback = _callback;
	}

	//---- IFileDialogEvents methods ----

	IFACEMETHODIMP OnFileOk( IFileDialog* dialog ) {
		if( callback == NULL )
			return S_OK;

		// get files
		jobjectArray files;
		if( open ) {
			AutoReleasePtr<IFileOpenDialog> openDialog;
			HRESULT hr = dialog->QueryInterface( &openDialog );
			files = SUCCEEDED( hr ) ? getFiles( env, true, openDialog ) : getFiles( env, false, dialog );
		} else
			files = getFiles( env, false, dialog );

		// get hwnd of file dialog
		HWND hwndFileDialog = 0;
		AutoReleasePtr<IOleWindow> window;
		if( SUCCEEDED( dialog->QueryInterface( &window ) ) )
			window->GetWindow( &hwndFileDialog );

		// invoke callback: boolean approve( String[] files, long hwnd );
		jclass cls = env->GetObjectClass( callback );
		jmethodID approveID = env->GetMethodID( cls, "approve", "([Ljava/lang/String;J)Z" );
		if( approveID == NULL )
			return S_OK;
		return env->CallBooleanMethod( callback, approveID, files, hwndFileDialog ) ? S_OK : S_FALSE;
	}

	IFACEMETHODIMP OnFolderChange( IFileDialog* ) { return S_OK; }
	IFACEMETHODIMP OnFolderChanging( IFileDialog*, IShellItem* ) { return S_OK; }
	IFACEMETHODIMP OnHelp( IFileDialog* ) { return S_OK; }
	IFACEMETHODIMP OnSelectionChange( IFileDialog* ) { return S_OK; }
	IFACEMETHODIMP OnShareViolation( IFileDialog*, IShellItem*, FDE_SHAREVIOLATION_RESPONSE* ) { return S_OK; }
	IFACEMETHODIMP OnTypeChange( IFileDialog*pfd ) { return S_OK; }
	IFACEMETHODIMP OnOverwrite( IFileDialog*, IShellItem*, FDE_OVERWRITE_RESPONSE* ) { return S_OK; }

	//---- IUnknown methods ----

	IFACEMETHODIMP QueryInterface( REFIID riid, void** ppv ) {
		if( riid != IID_IFileDialogEvents && riid != IID_IUnknown )
			return E_NOINTERFACE;

		*ppv = static_cast<IFileDialogEvents*>( this );
		AddRef();
		return S_OK;
	}

	IFACEMETHODIMP_(ULONG) AddRef() {
		return InterlockedIncrement( &refCount );
	}

	IFACEMETHODIMP_(ULONG) Release() {
		LONG newRefCount = InterlockedDecrement( &refCount );
		if( newRefCount == 0 )
			delete this;
		return newRefCount;
	}

private:
	~DialogEventHandler() {}
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

static jobjectArray newJavaStringArray( JNIEnv* env, jsize count ) {
	jclass stringClass = env->FindClass( "java/lang/String" );
	return env->NewObjectArray( count, stringClass, NULL );
}

static jstring newJavaString( JNIEnv* env, LPWSTR str ) {
	return env->NewString( reinterpret_cast<jchar*>( str ), static_cast<jsize>( wcslen( str ) ) );
}

//---- JNI methods ------------------------------------------------------------

extern "C"
JNIEXPORT jobjectArray JNICALL Java_com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_showFileChooser
	( JNIEnv* env, jclass cls, jobject owner, jboolean open,
		jstring title, jstring okButtonLabel, jstring fileNameLabel, jstring fileName,
		jstring folder, jstring saveAsItem, jstring defaultFolder, jstring defaultExtension,
		jint optionsSet, jint optionsClear, jobject callback, jint fileTypeIndex, jobjectArray fileTypes )
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

	// add event handler
	AutoReleasePtr<DialogEventHandler> handler( new DialogEventHandler( env, open, callback ) );
	DWORD dwCookie = 0;
	CHECK_HRESULT( dialog->Advise( handler, &dwCookie ) );

	// show dialog
	HWND hwndOwner = (owner != NULL) ? getWindowHandle( env, owner ) : NULL;
	HRESULT hr = dialog->Show( hwndOwner );
	dialog->Unadvise( dwCookie );
	if( hr == HRESULT_FROM_WIN32(ERROR_CANCELLED) )
		return newJavaStringArray( env, 0 );
	CHECK_HRESULT( hr );

	// get selected files as Java string array
	return getFiles( env, open, dialog );
}

static jobjectArray getFiles( JNIEnv* env, jboolean open, IFileDialog* dialog ) {
	if( open ) {
		AutoReleasePtr<IShellItemArray> shellItems;
		DWORD count;
		CHECK_HRESULT( ((IFileOpenDialog*)(IFileDialog*)dialog)->GetResults( &shellItems ) );
		CHECK_HRESULT( shellItems->GetCount( &count ) );

		// convert shell items to Java string array
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

		// convert shell item to Java string array
		jstring jpath = newJavaString( env, path );
		CoTaskMemFree( path );

		jobjectArray array = newJavaStringArray( env, 1 );
		env->SetObjectArrayElement( array, 0, jpath );
		env->DeleteLocalRef( jpath );

		return array;
	}
}
