//
// Created by Pietro Caselani on 4/23/15.
//

#include "Utils.h"
#include <string.h>

JavaVM* javaVM = nullptr;

#define assertion(env, expression, msg) if (!(expression)) jniThrowRuntimeException(env, msg);

void jniThrowException(JNIEnv* env, const char* className, const char* msg) {
    jclass javaClass = findClassOrDie(env, className);
    jmethodID constructor = findMethodOrDie(env, javaClass, "<init>", "(Ljava/lang/String;)V");
    jobject exception = env->NewObject(javaClass, constructor, msg);

    env->Throw(reinterpret_cast<jthrowable>(exception));
}

void jniThrowRuntimeException(JNIEnv* env, const char* msg) {
    jniThrowException(env, "java/lang/RuntimeException", msg);
}

jclass findClassOrDie(JNIEnv* env, const char* className) {
    jclass clazz = env->FindClass(className);
    assertion(env, clazz != nullptr, strcat((char*) "Unable to find class ", className));
    return clazz;
}

jclass findObjClassOrDie(JNIEnv *env, jobject obj) {
    const jclass javaclass = env->GetObjectClass(obj);
    assertion(env, javaclass != nullptr, "Unable to find class.");
    return javaclass;
}

jfieldID findFieldOrDie(JNIEnv* env, jclass javaClass, const char* fieldName, const char* fieldSignature) {
    jfieldID field = env->GetFieldID(javaClass, fieldName, fieldSignature);
    assertion(env, field != nullptr, strcat((char*) "Unable to find field ", fieldName))
    return field;
}

jmethodID findMethodOrDie(JNIEnv* env, jclass javaClass, const char* methodName, const char* methodSignature) {
    jmethodID methodID = env->GetMethodID(javaClass, methodName, methodSignature);
    assertion(env, methodID != nullptr, strcat((char*) "Unable to find method ", methodName));
    return methodID;
}

JNIEnv* getEnv() {
    JNIEnv* env;
    javaVM->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6);
    return env;
}