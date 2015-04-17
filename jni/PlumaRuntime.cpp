//
// Created by Pietro Caselani on 4/16/15.
//

#include <string.h>
#include "PlumaRuntime.h"

#define assertion(expression, msg) if (!(expression)) jniThrowRuntimeException(msg);

static PlumaRuntime* runtime = nullptr;

PlumaRuntime* PlumaRuntime::getRuntime() {
	if (runtime == nullptr) {
		runtime = new PlumaRuntime();
	}
	return runtime;
}

void PlumaRuntime::init(JNIEnv* env) {
	mJNIEnv = env;
}

void PlumaRuntime::jniThrowException(const char* className, const char* msg) {
	jclass javaClass = findClassOrDie(className);
	jmethodID constructor = findMethodOrDie(javaClass, "<init>", "(Ljava/lang/String;)V");
	jobject exception = mJNIEnv->NewObject(javaClass, constructor, msg);

	mJNIEnv->Throw(reinterpret_cast<jthrowable>(exception));
}

void PlumaRuntime::jniThrowRuntimeException(const char* msg) {
	jniThrowException("java/lang/RuntimeException", msg);
}

jclass PlumaRuntime::findClassOrDie(const char* className) {
	jclass clazz = mJNIEnv->FindClass(className);
	assertion(clazz != nullptr, strcat((char*) "Unable to find class ", className));
	return clazz;
}

jclass PlumaRuntime::findObjClassOrDie(jobject obj) {
	const jclass javaclass = mJNIEnv->GetObjectClass(obj);
	assertion(javaclass != nullptr, "Unable to find class.");
	return javaclass;
}

jfieldID PlumaRuntime::findFieldOrDie(jclass javaClass, const char* fieldName, const char* fieldSignature) {
	jfieldID field = mJNIEnv->GetFieldID(javaClass, fieldName, fieldSignature);
	assertion(field != nullptr, strcat((char*) "Unable to find field ", fieldName))
	return field;
}

jmethodID PlumaRuntime::findMethodOrDie(jclass javaClass, const char* methodName, const char* methodSignature) {
	jmethodID methodID = mJNIEnv->GetMethodID(javaClass, methodName, methodSignature);
	assertion(methodID != nullptr, strcat((char*) "Unable to find method ", methodName));
	return methodID;
}
