//
// Created by Pietro Caselani on 4/16/15.
//

#ifndef PLUMA_PLUMARUNTIME_H
#define PLUMA_PLUMARUNTIME_H

#include <jni.h>
#include <string>

using namespace std;

class PlumaRuntime {
public:
	PlumaRuntime(JNIEnv* JNIEnv) : mJNIEnv(JNIEnv) { }

	JNIEnv* getJNIEnv() const {
		return mJNIEnv;
	}

	void jniThrowException(const char* className, const char* msg);

	void jniThrowRuntimeException(const char* msg);

	jclass findClassOrDie(const char* className);

	jfieldID findFieldOrDie(jclass javaClass, const char* fieldName, const char* fieldSignature);

	jmethodID findMethodOrDie(jclass javaClass, const char* methodName, const char* methodSignature);

private:
	JNIEnv *mJNIEnv;
};


#endif //PLUMA_PLUMARUNTIME_H
