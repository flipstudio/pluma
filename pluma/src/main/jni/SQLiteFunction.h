//
// Created by Pietro Caselani on 4/16/15.
//

#ifndef PLUMA_SQLITEFUNCTION_H
#define PLUMA_SQLITEFUNCTION_H

#include <sqlite3.h>
#include <jni.h>

class SQLiteFunction {
private:
	jobject mObject;

public:
	sqlite3_context* mContext;
	sqlite3_value** mValue;

	void run(sqlite3_context*, int, sqlite3_value**);

	SQLiteFunction(jobject object);
	~SQLiteFunction();
};


#endif //PLUMA_SQLITEFUNCTION_H
