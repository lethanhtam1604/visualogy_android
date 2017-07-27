LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

#opencv
OPENCVROOT:= D:\Freelancer\visualogyx_android_app\OpenCV-android-sdk
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED
include ${OPENCVROOT}/sdk/native/jni/OpenCV.mk

LOCAL_MODULE := MyLibrary
LOCAL_SRC_FILES := MyLibrary.cpp

include $(BUILD_SHARED_LIBRARY)