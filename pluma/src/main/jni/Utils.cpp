//
// Created by Pietro Caselani on 4/23/15.
//

#include "Utils.h"

#ifdef __ANDROID__
#include "stdio.h"
#endif

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

	char message[100];
	sprintf(message, "Unable to find class %s", className);

	assertion(env, clazz != nullptr, message);
	return clazz;
}

jclass findObjClassOrDie(JNIEnv* env, jobject obj) {
	const jclass javaclass = env->GetObjectClass(obj);
	assertion(env, javaclass != nullptr, "Unable to find class.");
	return javaclass;
}

jfieldID findFieldOrDie(JNIEnv* env, jclass javaClass, const char* fieldName, const char* fieldSignature) {
	jfieldID field = env->GetFieldID(javaClass, fieldName, fieldSignature);

	char message[100];
	sprintf(message, "Unable to find field %s", fieldName);

	assertion(env, field != nullptr, message)
	return field;
}

jmethodID findMethodOrDie(JNIEnv* env, jclass javaClass, const char* methodName, const char* methodSignature) {
	jmethodID methodID = env->GetMethodID(javaClass, methodName, methodSignature);

	char message[100];
	sprintf(message, "Unable to find method %s", methodName);

	assertion(env, methodID != nullptr, message);
	return methodID;
}

JNIEnv* getEnv() {
	JNIEnv* env;
	javaVM->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6);
	return env;
}