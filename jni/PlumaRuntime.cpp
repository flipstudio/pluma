//
// Created by Pietro Caselani on 4/16/15.
//

#include "PlumaRuntime.h"

#define assertion(expression, msg) if (!(expression)) jniThrowRuntimeException(msg);

void PlumaRuntime::jniThrowException(const char* className, const char* msg) {
	jclass javaClass = findClassOrDie(className);
	jmethodID constructor = findMethodOrDie(javaClass, "<init>", "(Ljava/lang/String;)V");
	jobject exception = PlumaRuntime::mJNIEnv->NewObject(javaClass, constructor, msg);

	PlumaRuntime::mJNIEnv->Throw(reinterpret_cast<jthrowable>(exception));
}

void PlumaRuntime::jniThrowRuntimeException(const char* msg) {
	PlumaRuntime::jniThrowException("java/lang/RuntimeException", msg);
}

jclass PlumaRuntime::findClassOrDie(const char* className) {
	jclass clazz = PlumaRuntime::mJNIEnv->FindClass(className);
	assertion(clazz != NULL, strcat((char*) "Unable to find class ", className));
	return clazz;
}

jfieldID PlumaRuntime::findFieldOrDie(jclass javaClass, const char* fieldName, const char* fieldSignature) {
	jfieldID field = PlumaRuntime::mJNIEnv->GetFieldID(javaClass, fieldName, fieldSignature);
	assertion(field != NULL, strcat((char*) "Unable to find field ", fieldName))
	return field;
}

jmethodID PlumaRuntime::findMethodOrDie(jclass javaClass, const char* methodName, const char* methodSignature) {
	jmethodID methodID = PlumaRuntime::mJNIEnv->GetMethodID(javaClass, methodName, methodSignature);
	assertion(methodID != NULL, strcat((char*) "Unable to find method ", methodName));
	return methodID;
}
