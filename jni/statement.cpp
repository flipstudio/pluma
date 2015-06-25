#include "statement.h"
#include <sqlite3.h>

JNIEXPORT jint JNICALL Java_com_flipstudio_pluma_Statement_bind__JII
		(JNIEnv *jenv, jobject thiz, jlong jstmt, jint index, jint value) {
	sqlite3_stmt *stmt = reinterpret_cast<sqlite3_stmt *>(jstmt);
	return sqlite3_bind_int(stmt, index, value);
}

JNIEXPORT jint JNICALL Java_com_flipstudio_pluma_Statement_bind__JIJ
		(JNIEnv *jenv, jobject thiz, jlong jstmt, jint index, jlong value) {
	sqlite3_stmt *stmt = reinterpret_cast<sqlite3_stmt *>(jstmt);
	return sqlite3_bind_int64(stmt, index, value);
}

JNIEXPORT jint JNICALL Java_com_flipstudio_pluma_Statement_bind__JID
		(JNIEnv *jenv, jobject thiz, jlong jstmt, jint index, jdouble value) {
	sqlite3_stmt *stmt = reinterpret_cast<sqlite3_stmt *>(jstmt);
	return sqlite3_bind_double(stmt, index, value);
}

JNIEXPORT jint JNICALL Java_com_flipstudio_pluma_Statement_bind__JILjava_lang_String_2
		(JNIEnv *jenv, jobject thiz, jlong jstmt, jint index, jstring value) {
	sqlite3_stmt *stmt = reinterpret_cast<sqlite3_stmt *>(jstmt);

	const char *text = jenv->GetStringUTFChars(value, 0);

	int rc = sqlite3_bind_text(stmt, index, text, -1, SQLITE_TRANSIENT);

	jenv->ReleaseStringUTFChars(value, text);

	return rc;
}

JNIEXPORT jint JNICALL Java_com_flipstudio_pluma_Statement_bindNull
		(JNIEnv *jenv, jobject thiz, jlong jstmt, jint index) {
	sqlite3_stmt *stmt = reinterpret_cast<sqlite3_stmt *>(jstmt);
	return sqlite3_bind_null(stmt, index);
}

JNIEXPORT jint JNICALL Java_com_flipstudio_pluma_Statement_getInt
		(JNIEnv *jenv, jobject thiz, jlong jstmt, jint jcolumnIndex) {
	sqlite3_stmt *stmt = reinterpret_cast<sqlite3_stmt *>(jstmt);
	return sqlite3_column_int(stmt, jcolumnIndex);
}

JNIEXPORT jlong JNICALL Java_com_flipstudio_pluma_Statement_getLong
		(JNIEnv *jenv, jobject thiz, jlong jstmt, jint jcolumnIndex) {
	sqlite3_stmt *stmt = reinterpret_cast<sqlite3_stmt *>(jstmt);
	return sqlite3_column_int64(stmt, jcolumnIndex);
}

JNIEXPORT jdouble JNICALL Java_com_flipstudio_pluma_Statement_getDouble
		(JNIEnv *jenv, jobject thiz, jlong jstmt, jint jcolumnIndex) {
	sqlite3_stmt *stmt = reinterpret_cast<sqlite3_stmt *>(jstmt);
	return sqlite3_column_double(stmt, jcolumnIndex);
}

JNIEXPORT jstring JNICALL Java_com_flipstudio_pluma_Statement_getText
		(JNIEnv *jenv, jobject thiz, jlong jstmt, jint jcolumnIndex) {
	sqlite3_stmt *stmt = reinterpret_cast<sqlite3_stmt *>(jstmt);

	return jenv->NewStringUTF((const char *) sqlite3_column_text(stmt, jcolumnIndex));
}

JNIEXPORT jint JNICALL Java_com_flipstudio_pluma_Statement_getColumnType
		(JNIEnv *jenv, jobject thiz, jlong jstmt, jint jcolumnIndex) {
	sqlite3_stmt *stmt = reinterpret_cast<sqlite3_stmt *>(jstmt);
	return sqlite3_column_type(stmt, jcolumnIndex);
}

JNIEXPORT jstring JNICALL Java_com_flipstudio_pluma_Statement_getColumnName
		(JNIEnv *jenv, jobject thiz, jlong jstmt, jint jcolumnIndex) {
	sqlite3_stmt *stmt = reinterpret_cast<sqlite3_stmt *>(jstmt);
	return jenv->NewStringUTF(sqlite3_column_name(stmt, jcolumnIndex));
}

JNIEXPORT jint JNICALL Java_com_flipstudio_pluma_Statement_bindParameterCount
		(JNIEnv *jenv, jobject thiz, jlong jstmt) {
	sqlite3_stmt *stmt = reinterpret_cast<sqlite3_stmt *>(jstmt);
	return sqlite3_bind_parameter_count(stmt);
}

JNIEXPORT jint JNICALL Java_com_flipstudio_pluma_Statement_bindParameterIndex
		(JNIEnv *jenv, jobject thiz, jlong jstmt, jstring jparameterName) {
	sqlite3_stmt *stmt = reinterpret_cast<sqlite3_stmt *>(jstmt);

	const char *parameterName = jenv->GetStringUTFChars(jparameterName, 0);

	int rc = sqlite3_bind_parameter_index(stmt, parameterName);

	jenv->ReleaseStringUTFChars(jparameterName, parameterName);

	return rc;
}

JNIEXPORT jint JNICALL Java_com_flipstudio_pluma_Statement_getColumnCount
		(JNIEnv *jenv, jobject thiz, jlong jstmt) {
	sqlite3_stmt *stmt = reinterpret_cast<sqlite3_stmt *>(jstmt);
	return sqlite3_column_count(stmt);
}

JNIEXPORT jint JNICALL Java_com_flipstudio_pluma_Statement_step
		(JNIEnv *jenv, jobject thiz, jlong jstmt) {
	sqlite3_stmt *stmt = reinterpret_cast<sqlite3_stmt *>(jstmt);
	return sqlite3_step(stmt);
}

JNIEXPORT jint JNICALL Java_com_flipstudio_pluma_Statement_clearBindings
		(JNIEnv *jenv, jobject thiz, jlong jstmt) {
	sqlite3_stmt *stmt = reinterpret_cast<sqlite3_stmt *>(jstmt);
	return sqlite3_clear_bindings(stmt);
}

JNIEXPORT jint JNICALL Java_com_flipstudio_pluma_Statement_reset
		(JNIEnv *jenv, jobject thiz, jlong jstmt) {
	sqlite3_stmt *stmt = reinterpret_cast<sqlite3_stmt *>(jstmt);
	return sqlite3_reset(stmt);
}

JNIEXPORT jint JNICALL Java_com_flipstudio_pluma_Statement_finalize
		(JNIEnv *jenv, jobject thiz, jlong jstmt) {
	sqlite3_stmt *stmt = reinterpret_cast<sqlite3_stmt *>(jstmt);
	return sqlite3_finalize(stmt);
}

JNIEXPORT jstring JNICALL Java_com_flipstudio_pluma_Statement_getSQL
		(JNIEnv *jenv , jobject thiz, jlong jstmt) {
	sqlite3_stmt *stmt = reinterpret_cast<sqlite3_stmt *>(jstmt);
	return jenv->NewStringUTF(sqlite3_sql(stmt));
}