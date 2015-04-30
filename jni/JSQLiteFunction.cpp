#include "JSQLiteFunction.h"
#include "SQLiteFunction.h"

JNIEXPORT jlong JNICALL Java_com_flipstudio_pluma_SQLiteFunction_init
		(JNIEnv* jenv, jobject jthiz) {
	SQLiteFunction* function = new SQLiteFunction(jthiz);

	return reinterpret_cast<jlong>(function);
}

JNIEXPORT void JNICALL Java_com_flipstudio_pluma_SQLiteFunction_setErrorResult
		(JNIEnv* jenv, jobject jthiz, jlong jptr, jstring jmessage, jint jcode) {
	SQLiteFunction* function = reinterpret_cast<SQLiteFunction*>(jptr);

	const char* message = jenv->GetStringUTFChars(jmessage, 0);

	sqlite3_result_error(function->mContext, message, jcode);

	jenv->ReleaseStringUTFChars(jmessage, message);
}

JNIEXPORT void JNICALL Java_com_flipstudio_pluma_SQLiteFunction_setDoubleResult
		(JNIEnv* jenv, jobject jthiz, jlong jptr, jdouble jvalue) {
	SQLiteFunction* function = reinterpret_cast<SQLiteFunction*>(jptr);

	sqlite3_result_double(function->mContext, jvalue);
}

JNIEXPORT void JNICALL Java_com_flipstudio_pluma_SQLiteFunction_setIntResult
		(JNIEnv* jenv, jobject jthiz, jlong jptr, jint jvalue) {
	SQLiteFunction* function = reinterpret_cast<SQLiteFunction*>(jptr);

	sqlite3_result_int(function->mContext, jvalue);
}

JNIEXPORT void JNICALL Java_com_flipstudio_pluma_SQLiteFunction_setLongResult
		(JNIEnv* jenv, jobject jthiz, jlong jptr, jlong jvalue) {
	SQLiteFunction* function = reinterpret_cast<SQLiteFunction*>(jptr);

	sqlite3_result_int64(function->mContext, jvalue);
}

JNIEXPORT void JNICALL Java_com_flipstudio_pluma_SQLiteFunction_setStringResult
		(JNIEnv* jenv, jobject jthiz, jlong jptr, jstring jvalue) {
	SQLiteFunction* function = reinterpret_cast<SQLiteFunction*>(jptr);

	const char *text = jenv->GetStringUTFChars(jvalue, 0);

	sqlite3_result_text(function->mContext, text, -1, SQLITE_TRANSIENT);

	jenv->ReleaseStringUTFChars(jvalue, text);
}

JNIEXPORT void JNICALL Java_com_flipstudio_pluma_SQLiteFunction_setNullResult
		(JNIEnv* jenv, jobject jthiz, jlong jptr) {
	SQLiteFunction* function = reinterpret_cast<SQLiteFunction*>(jptr);

	sqlite3_result_null(function->mContext);
}

JNIEXPORT jdouble JNICALL Java_com_flipstudio_pluma_SQLiteFunction_getDoubleArg
		(JNIEnv* jenv, jobject jthiz, jlong jptr, jint jindex) {
	SQLiteFunction* function = reinterpret_cast<SQLiteFunction*>(jptr);

	return sqlite3_value_double(function->mValue[jindex]);
}

JNIEXPORT jint JNICALL Java_com_flipstudio_pluma_SQLiteFunction_getIntArg
		(JNIEnv* jenv, jobject jthiz, jlong jptr, jint jindex) {
	SQLiteFunction* function = reinterpret_cast<SQLiteFunction*>(jptr);

	return sqlite3_value_int(function->mValue[jindex]);
}

JNIEXPORT jlong JNICALL Java_com_flipstudio_pluma_SQLiteFunction_getLongArg
		(JNIEnv* jenv, jobject jthiz, jlong jptr, jint jindex) {
	SQLiteFunction* function = reinterpret_cast<SQLiteFunction*>(jptr);

	return sqlite3_value_int64(function->mValue[jindex]);
}

JNIEXPORT jstring JNICALL Java_com_flipstudio_pluma_SQLiteFunction_getStringArg
		(JNIEnv* jenv, jobject jthiz, jlong jptr, jint jindex) {
	SQLiteFunction* function = reinterpret_cast<SQLiteFunction*>(jptr);

	const char* text = (char const*) sqlite3_value_text(function->mValue[jindex]);

	return jenv->NewStringUTF(text);
}

JNIEXPORT void JNICALL Java_com_flipstudio_pluma_SQLiteFunction_dispose
		(JNIEnv* jenv, jobject jthiz, jlong jptr) {
	SQLiteFunction* function = reinterpret_cast<SQLiteFunction*>(jptr);
	delete function;
}