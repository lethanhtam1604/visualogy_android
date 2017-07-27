#include "com_visualogyx_activity_MyNDK.h"
#include "PipeDetection.hpp"

JNIEXPORT jint JNICALL Java_com_visualogyx_activity_MyNDK_processImage
  (JNIEnv *env, jobject, jstring in, jstring out){
    const char *input = env->GetStringUTFChars(in, JNI_FALSE);
    const char *output = env->GetStringUTFChars(out, JNI_FALSE);
    return ProcessImage(input, output);
  }