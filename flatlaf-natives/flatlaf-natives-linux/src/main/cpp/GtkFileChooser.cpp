/*
 * Copyright 2025 FormDev Software GmbH
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

#include <jawt.h>
#include <linux/jawt_md.h>
#include <gtk/gtk.h>
#include <gdk/gdkx.h>
#include <glib/gi18n.h>
#include "JNIUtils.h"
#include "com_formdev_flatlaf_ui_FlatNativeLinuxLibrary.h"

/**
 * @author Karl Tauber
 * @since 3.6
 */

// declare external methods
extern Window getWindowHandle( JNIEnv* env, JAWT* awt, jobject window, Display** display_return );

// declare internal methods
static jobjectArray fileListToStringArray( JNIEnv* env, GSList* fileList );

//---- helper -----------------------------------------------------------------

#define isOptionSet( option ) ((optionsSet & com_formdev_flatlaf_ui_FlatNativeLinuxLibrary_ ## option) != 0)
#define isOptionClear( option ) ((optionsClear & com_formdev_flatlaf_ui_FlatNativeLinuxLibrary_ ## option) != 0)
#define isOptionSetOrClear( option ) isOptionSet( option ) || isOptionClear( option )

static jobjectArray newJavaStringArray( JNIEnv* env, jsize count ) {
	jclass stringClass = env->FindClass( "java/lang/String" );
	return env->NewObjectArray( count, stringClass, NULL );
}

static void initFilters( GtkFileChooser* chooser, JNIEnv* env, jint fileTypeIndex, jobjectArray fileTypes ) {
	jint length = env->GetArrayLength( fileTypes );
	if( length <= 0 )
		return;

	GtkFileFilter* filter = NULL;
	int filterIndex = 0;
	for( int i = 0; i < length; i++ ) {
		jstring jstr = (jstring) env->GetObjectArrayElement( fileTypes, i );
		if( jstr == NULL ) {
			if( filter != NULL ) {
				gtk_file_chooser_add_filter( chooser, filter );
				if( fileTypeIndex == filterIndex )
					gtk_file_chooser_set_filter( chooser, filter );
				filter = NULL;
				filterIndex++;
			}
			continue;
		}

		AutoReleaseStringUTF8 str( env, jstr );
		if( filter == NULL ) {
			filter = gtk_file_filter_new();
			gtk_file_filter_set_name( filter, str );
		} else
			gtk_file_filter_add_pattern( filter, str );
	}
}

static GdkWindow* getGdkWindow( JNIEnv* env, jobject window ) {
	// get the AWT
	JAWT awt;
	awt.version = JAWT_VERSION_1_4;
	if( !JAWT_GetAWT( env, &awt ) )
		return NULL;

	// get Xlib window and display from AWT window
	Display* display;
	Window w = getWindowHandle( env, &awt, window, &display );
	if( w == 0 )
		return NULL;

	// based on GetAllocNativeWindowHandle() from https://github.com/btzy/nativefiledialog-extended
	// https://github.com/btzy/nativefiledialog-extended/blob/29e3bcb578345b9fa345d1d7683f00c150565ca3/src/nfd_gtk.cpp#L384-L437
	GdkDisplay* gdkDisplay = gdk_x11_lookup_xdisplay( display );
	if( gdkDisplay == NULL ) {
		// search for existing X11 display (there should only be one, even if multiple screens are connected)
		GdkDisplayManager* displayManager = gdk_display_manager_get();
		GSList* displays = gdk_display_manager_list_displays( displayManager );
		for( GSList* l = displays; l; l = l->next ) {
			if( GDK_IS_X11_DISPLAY( l->data ) ) {
				gdkDisplay = GDK_DISPLAY( l->data );
				break;
			}
		}
		g_slist_free( displays );

		// create our own X11 display
		if( gdkDisplay == NULL ) {
			gdk_set_allowed_backends( "x11" );
			gdkDisplay = gdk_display_manager_open_display( displayManager, NULL );
			gdk_set_allowed_backends( NULL );

			if( gdkDisplay == NULL )
				return NULL;
		}
	}

	return gdk_x11_window_foreign_new_for_display( gdkDisplay, w );
}

static void handle_realize( GtkWidget* dialog, gpointer data ) {
	GdkWindow* gdkOwner = static_cast<GdkWindow*>( data );

	// make file dialog a transient of owner window,
	// which centers file dialog on owner and keeps file dialog above owner
	gdk_window_set_transient_for( gtk_widget_get_window( dialog ), gdkOwner );

	// necessary because gdk_x11_window_foreign_new_for_display() increases the reference counter
	g_object_unref( gdkOwner );
}

struct ResponseData {
	JNIEnv* env;
	jobject callback;
	GSList* fileList;

	ResponseData( JNIEnv* _env, jobject _callback ) {
		env = _env;
		callback = _callback;
		fileList = NULL;
	}
};

static void handle_response( GtkWidget* dialog, gint responseId, gpointer data ) {
	// get filenames if user pressed OK
	if( responseId == GTK_RESPONSE_ACCEPT ) {
		ResponseData *response = static_cast<ResponseData*>( data );
		if( response->callback != NULL ) {
			GSList* fileList = gtk_file_chooser_get_filenames( GTK_FILE_CHOOSER( dialog ) );
			jobjectArray files = fileListToStringArray( response->env, fileList );

			GtkWindow* window = GTK_WINDOW( dialog );

			// invoke callback: boolean approve( String[] files, long hwnd );
			jclass cls = response->env->GetObjectClass( response->callback );
			jmethodID approveID = response->env->GetMethodID( cls, "approve", "([Ljava/lang/String;J)Z" );
			if( approveID != NULL && !response->env->CallBooleanMethod( response->callback, approveID, files, window ) )
				return; // keep dialog open
		}

		response->fileList = gtk_file_chooser_get_filenames( GTK_FILE_CHOOSER( dialog ) );
	}

	// hide/destroy file dialog and quit loop
	gtk_widget_hide( dialog );
	gtk_widget_destroy( dialog );
	gtk_main_quit();
}

//---- JNI methods ------------------------------------------------------------

extern "C"
JNIEXPORT jobjectArray JNICALL Java_com_formdev_flatlaf_ui_FlatNativeLinuxLibrary_showFileChooser
	( JNIEnv* env, jclass cls, jobject owner, jboolean open,
		jstring title, jstring okButtonLabel, jstring currentName, jstring currentFolder,
		jint optionsSet, jint optionsClear, jobject callback, jint fileTypeIndex, jobjectArray fileTypes )
{
	// initialize GTK
	if( !gtk_init_check( NULL, NULL ) )
		return NULL;

	// convert Java strings to C strings
	AutoReleaseStringUTF8 ctitle( env, title );
	AutoReleaseStringUTF8 cokButtonLabel( env, okButtonLabel );
	AutoReleaseStringUTF8 ccurrentName( env, currentName );
	AutoReleaseStringUTF8 ccurrentFolder( env, currentFolder );

	// create GTK file chooser dialog
	// https://docs.gtk.org/gtk3/class.FileChooserDialog.html
	bool selectFolder = isOptionSet( FC_select_folder );
	bool multiSelect = isOptionSet( FC_select_multiple );
	GtkWidget* dialog = gtk_file_chooser_dialog_new(
		(ctitle != NULL) ? ctitle
			: (selectFolder ? (multiSelect ? _("Select Folders") : _("Select Folder"))
				: (open ? ((multiSelect ? _("Open Files") : _("Open File"))) : _("Save File"))),
		NULL, // can not use AWT X11 window as parent because GtkWindow is required
		selectFolder ? GTK_FILE_CHOOSER_ACTION_SELECT_FOLDER
			: (open ? GTK_FILE_CHOOSER_ACTION_OPEN : GTK_FILE_CHOOSER_ACTION_SAVE),
		_("_Cancel"), GTK_RESPONSE_CANCEL,
		(cokButtonLabel != NULL) ? cokButtonLabel
			: (selectFolder ? _("_Select") : (open ? _("_Open") : _("_Save"))), GTK_RESPONSE_ACCEPT,
		NULL ); // marks end of buttons
	GtkFileChooser* chooser = GTK_FILE_CHOOSER( dialog );

	// set current name and folder
	if( !open && ccurrentName != NULL )
		gtk_file_chooser_set_current_name( chooser, ccurrentName );
	if( ccurrentFolder != NULL )
		gtk_file_chooser_set_current_folder( chooser, ccurrentFolder );

	// set options
	if( isOptionSetOrClear( FC_select_multiple ) )
		gtk_file_chooser_set_select_multiple( chooser, isOptionSet( FC_select_multiple ) );
	if( isOptionSetOrClear( FC_show_hidden ) )
		gtk_file_chooser_set_show_hidden( chooser, isOptionSet( FC_show_hidden ) );
	if( isOptionSetOrClear( FC_local_only ) )
		gtk_file_chooser_set_local_only( chooser, isOptionSet( FC_local_only ) );
	if( isOptionSetOrClear( FC_do_overwrite_confirmation ) )
		gtk_file_chooser_set_do_overwrite_confirmation( chooser, isOptionSet( FC_do_overwrite_confirmation ) );
	if( isOptionSetOrClear( FC_create_folders ) )
		gtk_file_chooser_set_create_folders( chooser, isOptionSet( FC_create_folders ) );

	// initialize filter
	initFilters( chooser, env, fileTypeIndex, fileTypes );

	// setup modality
	GdkWindow* gdkOwner = (owner != NULL) ? getGdkWindow( env, owner ) : NULL;
	if( gdkOwner != NULL ) {
		gtk_window_set_modal( GTK_WINDOW( dialog ), true );

		// file dialog should use same screen as owner
		gtk_window_set_screen( GTK_WINDOW( dialog ), gdk_window_get_screen( gdkOwner ) );

		// set the transient when the file dialog is realized
		g_signal_connect( dialog, "realize", G_CALLBACK( handle_realize ), gdkOwner );
	}

	// show dialog
	// (similar to what's done in sun_awt_X11_GtkFileDialogPeer.c)
	ResponseData responseData( env, callback );
	g_signal_connect( dialog, "response", G_CALLBACK( handle_response ), &responseData );
	gtk_widget_show( dialog );

	// necessary to bring file dialog to the front (and make it active)
	// see issues:
	//     https://github.com/btzy/nativefiledialog-extended/issues/31
	//     https://github.com/mlabbe/nativefiledialog/pull/92
	//     https://github.com/guillaumechereau/noc/pull/11
	if( GDK_IS_X11_DISPLAY( gtk_widget_get_display( GTK_WIDGET( dialog ) ) ) ) {
		GdkWindow* gdkWindow = gtk_widget_get_window( GTK_WIDGET( dialog ) );
		gdk_window_set_events( gdkWindow, static_cast<GdkEventMask>( gdk_window_get_events( gdkWindow ) | GDK_PROPERTY_CHANGE_MASK ) );
		gtk_window_present_with_time( GTK_WINDOW( dialog ), gdk_x11_get_server_time( gdkWindow ) );
	}

	// start event loop (will be quit in respone handler)
	gtk_main();

	// canceled?
	if( responseData.fileList == NULL )
		return newJavaStringArray( env, 0 );

	// convert GSList to Java string array
	return fileListToStringArray( env, responseData.fileList );
}

static jobjectArray fileListToStringArray( JNIEnv* env, GSList* fileList ) {
	guint count = g_slist_length( fileList );
	jobjectArray array = newJavaStringArray( env, count );
	GSList* it = fileList;
	for( int i = 0; i < count; i++, it = it->next ) {
		gchar* path = (gchar*) it->data;
		jstring jpath = env->NewStringUTF( path );
		g_free( path );

		env->SetObjectArrayElement( array, i, jpath );
		env->DeleteLocalRef( jpath );
	}
	g_slist_free( fileList );
	return array;
}
