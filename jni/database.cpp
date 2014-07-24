#include "database.h"
#include <sqlite3.h>
#include <stdio.h>

JNIEXPORT jlong JNICALL Java_com_flipstudio_pluma_Database_open
(JNIEnv *jenv, jobject thiz, jstring jfilepath, jint jflags, jintArray jCodeArray, jobjectArray jErrorArray)
{
	const char *dbPath;
	jlong result;
	sqlite3 *db;
	int rc;

	dbPath = jenv->GetStringUTFChars(jfilepath, 0);

	rc = sqlite3_open_v2(dbPath, &db, (int) jflags, 0);

	jenv->ReleaseStringUTFChars(jfilepath, dbPath);

	if (rc == SQLITE_OK)
	{
		result = reinterpret_cast<jlong>(db);
	}
	else
	{
		result = 0;

		const char *errmsg;
		jstring error;

		errmsg = sqlite3_errmsg(db);
		if (errmsg)
		{
			error = jenv->NewStringUTF(errmsg);
			if (error)
			{
				jenv->SetObjectArrayElement(jErrorArray, 0, error);
			}
		}

		if (db)
		{
			sqlite3_close_v2(db);
			db = 0;
		}
	}

	jenv->SetIntArrayRegion(jCodeArray, 0, 1, &rc);

	return result;
}

JNIEXPORT jlong JNICALL Java_com_flipstudio_pluma_Database_prepare
(JNIEnv *jenv, jobject thiz, jlong jdb, jstring jsql, jintArray jCodeArray)
{
	sqlite3 *db = reinterpret_cast<sqlite3*>(jdb);
	sqlite3_stmt *stmt;
	int rc;
	jlong result;

	jsize sqlLength = jenv->GetStringLength(jsql);
	const jchar* sql = jenv->GetStringCritical(jsql, 0);

	rc = sqlite3_prepare16_v2(db, sql, sqlLength * sizeof(jchar), &stmt, 0);

	jenv->ReleaseStringCritical(jsql, sql);

	if (stmt != 0 && rc == SQLITE_OK)
	{
		result = reinterpret_cast<jlong>(stmt);
	}
	else
	{
		result = 0;

		sqlite3_finalize(stmt);
	}

	jenv->SetIntArrayRegion(jCodeArray, 0, 1, &rc);

	return result;
}

JNIEXPORT jint JNICALL Java_com_flipstudio_pluma_Database_exec
(JNIEnv *jenv, jobject thiz, jlong jdb, jstring jsql, jobjectArray joutError)
{
	sqlite3 *db = reinterpret_cast<sqlite3*>(jdb);
	const char *sql = jenv->GetStringUTFChars(jsql, 0);
	char *outError;

	int rc = sqlite3_exec(db, sql, 0, 0, &outError);

	jenv->ReleaseStringUTFChars(jsql, sql);

	if (rc != SQLITE_OK)
	{
		jstring error = jenv->NewStringUTF(outError);
		jenv->SetObjectArrayElement(joutError, 0, error);
	}

	sqlite3_free(outError);

	return rc;
}

JNIEXPORT jint JNICALL Java_com_flipstudio_pluma_Database_close
(JNIEnv *jenv, jobject thiz, jlong jdb)
{
	sqlite3 *db = reinterpret_cast<sqlite3*>(jdb);

	return sqlite3_close_v2(db);
}

JNIEXPORT jlong JNICALL Java_com_flipstudio_pluma_Database_lastInsertId
(JNIEnv *jenv, jobject thiz, jlong jdb)
{
	sqlite3 *db = reinterpret_cast<sqlite3*>(jdb);

	return sqlite3_last_insert_rowid(db);
}

JNIEXPORT jstring JNICALL Java_com_flipstudio_pluma_Database_lastErrorMessage
(JNIEnv *jenv, jobject thiz, jlong jdb)
{
	sqlite3 *db = reinterpret_cast<sqlite3*>(jdb);

	const char *errmsg = sqlite3_errmsg(db);

	return jenv->NewStringUTF(errmsg);
}

JNIEXPORT void JNICALL Java_com_flipstudio_pluma_Database_setTempDir
(JNIEnv *jenv, jobject thiz, jstring jtmpDir)
{
	sqlite3_temp_directory = (char*) jenv->GetStringUTFChars(jtmpDir, 0);
}