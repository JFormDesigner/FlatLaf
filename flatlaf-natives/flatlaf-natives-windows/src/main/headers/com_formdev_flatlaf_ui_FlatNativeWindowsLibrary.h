/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_formdev_flatlaf_ui_FlatNativeWindowsLibrary */

#ifndef _Included_com_formdev_flatlaf_ui_FlatNativeWindowsLibrary
#define _Included_com_formdev_flatlaf_ui_FlatNativeWindowsLibrary
#ifdef __cplusplus
extern "C" {
#endif
#undef com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_DWMWCP_DEFAULT
#define com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_DWMWCP_DEFAULT 0L
#undef com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_DWMWCP_DONOTROUND
#define com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_DWMWCP_DONOTROUND 1L
#undef com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_DWMWCP_ROUND
#define com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_DWMWCP_ROUND 2L
#undef com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_DWMWCP_ROUNDSMALL
#define com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_DWMWCP_ROUNDSMALL 3L
#undef com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_DWMWA_USE_IMMERSIVE_DARK_MODE
#define com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_DWMWA_USE_IMMERSIVE_DARK_MODE 20L
#undef com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_DWMWA_BORDER_COLOR
#define com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_DWMWA_BORDER_COLOR 34L
#undef com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_DWMWA_CAPTION_COLOR
#define com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_DWMWA_CAPTION_COLOR 35L
#undef com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_DWMWA_TEXT_COLOR
#define com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_DWMWA_TEXT_COLOR 36L
#undef com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_DWMWA_COLOR_DEFAULT
#define com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_DWMWA_COLOR_DEFAULT -1L
#undef com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_DWMWA_COLOR_NONE
#define com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_DWMWA_COLOR_NONE -2L
/*
 * Class:     com_formdev_flatlaf_ui_FlatNativeWindowsLibrary
 * Method:    getOSBuildNumberImpl
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_getOSBuildNumberImpl
  (JNIEnv *, jclass);

/*
 * Class:     com_formdev_flatlaf_ui_FlatNativeWindowsLibrary
 * Method:    getHWND
 * Signature: (Ljava/awt/Window;)J
 */
JNIEXPORT jlong JNICALL Java_com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_getHWND
  (JNIEnv *, jclass, jobject);

/*
 * Class:     com_formdev_flatlaf_ui_FlatNativeWindowsLibrary
 * Method:    setWindowCornerPreference
 * Signature: (JI)Z
 */
JNIEXPORT jboolean JNICALL Java_com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_setWindowCornerPreference
  (JNIEnv *, jclass, jlong, jint);

/*
 * Class:     com_formdev_flatlaf_ui_FlatNativeWindowsLibrary
 * Method:    dwmSetWindowAttributeBOOL
 * Signature: (JIZ)Z
 */
JNIEXPORT jboolean JNICALL Java_com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_dwmSetWindowAttributeBOOL
  (JNIEnv *, jclass, jlong, jint, jboolean);

/*
 * Class:     com_formdev_flatlaf_ui_FlatNativeWindowsLibrary
 * Method:    dwmSetWindowAttributeDWORD
 * Signature: (JII)Z
 */
JNIEXPORT jboolean JNICALL Java_com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_dwmSetWindowAttributeDWORD
  (JNIEnv *, jclass, jlong, jint, jint);

#ifdef __cplusplus
}
#endif
#endif
