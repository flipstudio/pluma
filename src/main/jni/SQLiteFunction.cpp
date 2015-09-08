//
// Created by Pietro Caselani on 4/16/15.
//

#include "SQLiteFunction.h"
#include "Utils.h"

static jmethodID runMethod;

SQLiteFunction::SQLiteFunction(jobject object) {
	mObject = getEnv()->NewGlobalRef(object);
}

void SQLiteFunction::run(sqlite3_context* context, int numArgs, sqlite3_value** value) {
	mContext = context;
	mValue = value;

	JNIEnv* env = getEnv();

	if (runMethod == nullptr) {
		const jclass functionClass = findObjClassOrDie(env, mObject);
		runMethod = findMethodOrDie(env, functionClass, "run", "(I)V");
	}

	env->CallVoidMethod(mObject, runMethod, numArgs);
}

SQLiteFunction::~SQLiteFunction() {
	getEnv()->DeleteGlobalRef(mObject);
	mObject = nullptr;
}
