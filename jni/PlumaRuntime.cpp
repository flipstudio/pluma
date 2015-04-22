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

void PlumaRuntime::init(JavaVM* javaVM) {
	mJavaVM = javaVM;
}

void PlumaRuntime::jniThrowException(const char* className, const char* msg) {
	jclass javaClass = findClassOrDie(className);
	jmethodID constructor = findMethodOrDie(javaClass, "<init>", "(Ljava/lang/String;)V");
	jobject exception = getJNIEnv()->NewObject(javaClass, constructor, msg);

	getJNIEnv()->Throw(reinterpret_cast<jthrowable>(exception));
}

void PlumaRuntime::jniThrowRuntimeException(const char* msg) {
	jniThrowException("java/lang/RuntimeException", msg);
}

jclass PlumaRuntime::findClassOrDie(const char* className) {
	jclass clazz = getJNIEnv()->FindClass(className);
	assertion(clazz != nullptr, strcat((char*) "Unable to find class ", className));
	return clazz;
}

jclass PlumaRuntime::findObjClassOrDie(jobject obj) {
	const jclass javaclass = getJNIEnv()->GetObjectClass(obj);
	assertion(javaclass != nullptr, "Unable to find class.");
	return javaclass;
}

jfieldID PlumaRuntime::findFieldOrDie(jclass javaClass, const char* fieldName, const char* fieldSignature) {
	jfieldID field = getJNIEnv()->GetFieldID(javaClass, fieldName, fieldSignature);
	assertion(field != nullptr, strcat((char*) "Unable to find field ", fieldName))
	return field;
}

jmethodID PlumaRuntime::findMethodOrDie(jclass javaClass, const char* methodName, const char* methodSignature) {
	jmethodID methodID = getJNIEnv()->GetMethodID(javaClass, methodName, methodSignature);
	assertion(methodID != nullptr, strcat((char*) "Unable to find method ", methodName));
	return methodID;
}

JNIEnv* PlumaRuntime::getJNIEnv() {
	JNIEnv* env;
	if (mJavaVM->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
		jniThrowRuntimeException("Unable to get JNIEnv");
	}

	return env;
}
