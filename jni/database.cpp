#include "database.h"
#include "Utils.h"
#include "SQLiteFunction.h"
#include <sqlite3.h>
#include "character_tokenizer.h"
#include "fts3_tokenizer.h"

void sqliteFunctionCallback(sqlite3_context* context, int argc, sqlite3_value** argv) {
	SQLiteFunction* function = reinterpret_cast<SQLiteFunction*>(sqlite3_user_data(context));

	function->run(context, argc, argv);
}

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
	JNIEnv *env;
	if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
		return -1;
	}

	javaVM = vm;

	return JNI_VERSION_1_6;
}

void registerTokenizer(const sqlite3_tokenizer_module *module, sqlite3 *handle, const char *tokenName){
	sqlite3_stmt *pStmt;
    const char *tokenSQL = "SELECT fts3_tokenizer(?, ?)";

    sqlite3_prepare_v2(handle, tokenSQL, -1, &pStmt, 0);

    sqlite3_bind_text(pStmt, 1, tokenName, -1, nullptr);
    sqlite3_bind_blob(pStmt, 2, &module, sizeof(module), nullptr);
    sqlite3_step(pStmt);
    sqlite3_finalize(pStmt);
}

JNIEXPORT jlong JNICALL Java_com_flipstudio_pluma_Database_open
		(JNIEnv *jenv, jobject thiz, jstring jfilepath, jint jflags, jintArray jCodeArray, jobjectArray jErrorArray) {
	const char *dbPath;
	jlong result;
	sqlite3 *db;
	int rc;

	dbPath = jenv->GetStringUTFChars(jfilepath, 0);

	rc = sqlite3_open_v2(dbPath, &db, (int) jflags, 0);

	jenv->ReleaseStringUTFChars(jfilepath, dbPath);

	if (rc == SQLITE_OK) {
		result = reinterpret_cast<jlong>(db);

		const sqlite3_tokenizer_module *module;

		get_character_tokenizer_module(&module);
		registerTokenizer(module, db, "character");
	}
	else {
		result = 0;

		const char *errmsg;
		jstring error;

		errmsg = sqlite3_errmsg(db);
		if (errmsg) {
			error = jenv->NewStringUTF(errmsg);
			if (error) {
				jenv->SetObjectArrayElement(jErrorArray, 0, error);
			}
		}

		if (db) {
			sqlite3_close_v2(db);
			db = 0;
		}
	}

	jenv->SetIntArrayRegion(jCodeArray, 0, 1, &rc);

	return result;
}

JNIEXPORT jlong JNICALL Java_com_flipstudio_pluma_Database_prepare
		(JNIEnv *jenv, jobject thiz, jlong jdb, jstring jsql, jintArray jCodeArray) {
	sqlite3 *db = reinterpret_cast<sqlite3 *>(jdb);
	sqlite3_stmt *stmt;
	int rc;
	jlong result;

	jsize sqlLength = jenv->GetStringLength(jsql);
	const jchar *sql = jenv->GetStringCritical(jsql, 0);

	rc = sqlite3_prepare16_v2(db, sql, sqlLength * sizeof(jchar), &stmt, 0);

	jenv->ReleaseStringCritical(jsql, sql);

	if (stmt != 0 && rc == SQLITE_OK) {
		result = reinterpret_cast<jlong>(stmt);
	}
	else {
		result = 0;

		sqlite3_finalize(stmt);
	}

	jenv->SetIntArrayRegion(jCodeArray, 0, 1, &rc);

	return result;
}

JNIEXPORT jint JNICALL Java_com_flipstudio_pluma_Database_exec
		(JNIEnv *jenv, jobject thiz, jlong jdb, jstring jsql, jobjectArray joutError) {
	sqlite3 *db = reinterpret_cast<sqlite3 *>(jdb);
	const char *sql = jenv->GetStringUTFChars(jsql, 0);
	char *outError;

	int rc = sqlite3_exec(db, sql, 0, 0, &outError);

	jenv->ReleaseStringUTFChars(jsql, sql);

	if (rc != SQLITE_OK) {
		jstring error = jenv->NewStringUTF(outError);
		jenv->SetObjectArrayElement(joutError, 0, error);
	}

	sqlite3_free(outError);

	return rc;
}

JNIEXPORT jint JNICALL Java_com_flipstudio_pluma_Database_close
		(JNIEnv *jenv, jobject thiz, jlong jdb) {
	sqlite3 *db = reinterpret_cast<sqlite3 *>(jdb);

	return sqlite3_close_v2(db);
}

JNIEXPORT jlong JNICALL Java_com_flipstudio_pluma_Database_lastInsertId
		(JNIEnv *jenv, jobject thiz, jlong jdb) {
	sqlite3 *db = reinterpret_cast<sqlite3 *>(jdb);

	return sqlite3_last_insert_rowid(db);
}

JNIEXPORT jstring JNICALL Java_com_flipstudio_pluma_Database_lastErrorMessage
		(JNIEnv *jenv, jobject thiz, jlong jdb) {
	sqlite3 *db = reinterpret_cast<sqlite3 *>(jdb);

	const char *errmsg = sqlite3_errmsg(db);

	return jenv->NewStringUTF(errmsg);
}

JNIEXPORT void JNICALL Java_com_flipstudio_pluma_Database_setTempDir
		(JNIEnv *jenv, jobject thiz, jstring jtmpDir) {
	sqlite3_temp_directory = (char *) jenv->GetStringUTFChars(jtmpDir, 0);
}

JNIEXPORT jint JNICALL Java_com_flipstudio_pluma_Database_registerFunction
		(JNIEnv *jenv, jobject jthiz, jlong jdb, jstring jname, jint jnumArgs, jlong jfunctionPtr) {
	sqlite3 *db = reinterpret_cast<sqlite3 *>(jdb);

	SQLiteFunction* function = reinterpret_cast<SQLiteFunction*>(jfunctionPtr);

	const char *name = jenv->GetStringUTFChars(jname, nullptr);

	int rc = sqlite3_create_function_v2(db, name, jnumArgs, SQLITE_UTF16, function,
																			&sqliteFunctionCallback, nullptr, nullptr, nullptr);

	jenv->ReleaseStringUTFChars(jname, name);

	return rc;
}