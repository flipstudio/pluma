//
// Created by Pietro Caselani on 4/16/15.
//

#ifndef PLUMA_PLUMARUNTIME_H
#define PLUMA_PLUMARUNTIME_H


#include <JavaVM/JavaVM.h>
#include <jni.h>
#include <string>

using namespace std;

class PlumaRuntime {
public:
	static JavaVM *getJavaVM() const {
		return mJavaVM;
	}

	static void loadVM(JavaVM* javaVM) {
		mJavaVM = javaVM;
	}

	static JNIEnv *getJNIEnv() const;

	static inline void jniThrowException(string className, string msg);

	static inline void jniThrowRuntimeException(string msg);

	static inline jclass findClassOrDie(string className);

	static inline jfieldID findFieldOrDie(jclass javaClass, string fieldName, string fieldSignature);

	static inline jmethodID findMethodOrDie(jclass javaClass, string methodName, string methodSignature);

private:
	static JavaVM *mJavaVM;

	PlumaRuntime() { };
};


#endif //PLUMA_PLUMARUNTIME_H
