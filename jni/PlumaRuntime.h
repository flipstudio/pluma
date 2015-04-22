//
// Created by Pietro Caselani on 4/16/15.
//

#ifndef PLUMA_PLUMARUNTIME_H
#define PLUMA_PLUMARUNTIME_H

#include <jni.h>

class PlumaRuntime {
public:
	static PlumaRuntime* getRuntime();

	void init(JavaVM*);

	JavaVM* getJavaVM() const {
		return mJavaVM;
	}

	void jniThrowException(const char* className, const char* msg);

	void jniThrowRuntimeException(const char* msg);

	jclass findClassOrDie(const char* className);

	jclass findObjClassOrDie(jobject obj);

	jfieldID findFieldOrDie(jclass javaClass, const char* fieldName, const char* fieldSignature);

	jmethodID findMethodOrDie(jclass javaClass, const char* methodName, const char* methodSignature);

	JNIEnv* getJNIEnv();

private:
	JavaVM* mJavaVM;

	PlumaRuntime() { }
};


#endif //PLUMA_PLUMARUNTIME_H
