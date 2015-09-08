//
// Created by Pietro Caselani on 4/23/15.
//

#ifndef PLUMA_UTILS_H
#define PLUMA_UTILS_H

#include <jni.h>

extern JavaVM* javaVM;

void jniThrowException(JNIEnv* env, const char* className, const char* msg);

void jniThrowRuntimeException(JNIEnv* env, const char* msg);

jclass findClassOrDie(JNIEnv* env, const char* className);

jclass findObjClassOrDie(JNIEnv* env, jobject obj);

jfieldID findFieldOrDie(JNIEnv* env, jclass javaClass, const char* fieldName, const char* fieldSignature);

jmethodID findMethodOrDie(JNIEnv* env, jclass javaClass, const char* methodName, const char* methodSignature);

JNIEnv* getEnv();

#endif //PLUMA_UTILS_H
