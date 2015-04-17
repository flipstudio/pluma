//
// Created by Pietro Caselani on 4/16/15.
//

#include "SQLiteFunction.h"
#include "PlumaRuntime.h"

static jmethodID runMethod;

SQLiteFunction::SQLiteFunction(jobject object) {
	mObject = PlumaRuntime::getRuntime()->getJNIEnv()->NewGlobalRef(object);
}

void SQLiteFunction::run(sqlite3_context* context, int numArgs, sqlite3_value** value) {
	mContext = context;
	mValue = value;

	PlumaRuntime* const runtime = PlumaRuntime::getRuntime();

	if (runMethod == nullptr) {
		const jclass functionClass = runtime->findObjClassOrDie(mObject);
		runMethod = runtime->findMethodOrDie(functionClass, "run", "(I)V");
	}

	runtime->getJNIEnv()->CallVoidMethod(mObject, runMethod, numArgs);
}

SQLiteFunction::~SQLiteFunction() {
	PlumaRuntime::getRuntime()->getJNIEnv()->DeleteGlobalRef(mObject);
	mObject = nullptr;
}
