//
// Created by Pietro Caselani on 4/16/15.
//

#include "PlumaRuntime.h"

#define assertion(expression, msg) if (!(expression)) jniThrowRuntimeException(msg);

JNIEnv *PlumaRuntime::getJNIEnv() const {
	JavaVM *vm = getJavaVM();

	assertion(vm != nullptr, "JavaVM is null")

	return nullptr;
}

void PlumaRuntime::jniThrowException(string className, string msg) {
	JNIEnv *jenv = PlumaRuntime::getJNIEnv();

	jclass javaClass = findClassOrDie(className);
	jmethodID constructor = findMethodOrDie(javaClass, "<init>", "(Ljava/lang/String;)V");
	jobject exception = jenv->NewObject(javaClass, constructor, msg);

	jenv->Throw(reinterpret_cast<jthrowable>(exception));
}

void PlumaRuntime::jniThrowRuntimeException(string msg) {
	PlumaRuntime::jniThrowException("java/lang/RuntimeException", msg);
}

jclass PlumaRuntime::findClassOrDie(string className) {
	jclass clazz = PlumaRuntime::getJNIEnv()->FindClass(className.c_str());
	assertion(clazz != NULL, "Unable to find class " + className);
	return clazz;
}

jfieldID PlumaRuntime::findFieldOrDie(jclass javaClass, string fieldName, string fieldSignature) {
	jfieldID field = PlumaRuntime::getJNIEnv()->GetFieldID(javaClass, fieldName.c_str(), fieldSignature.c_str());
	assertion(field != NULL, "Unable to find field " + fieldName)
	return field;
}

jmethodID PlumaRuntime::findMethodOrDie(jclass javaClass, string methodName, string methodSignature) {
	jmethodID methodID = PlumaRuntime::getJNIEnv()->GetMethodID(javaClass, methodName.c_str(), methodSignature.c_str());
	assertion(methodID != NULL, "Unable to find method " + methodName);
	return methodID;
}
