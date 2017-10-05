package com.flipstudio.pluma;

/**
 * Created by Pietro Caselani
 * On 14/11/13
 * Pluma
 */
public final class Pluma {
	//region Errors
	public static final int SQLITE_ERROR = 1;   /* SQL error or missing database */
	public static final int SQLITE_INTERNAL = 2;   /* Internal logic error in SQLite */
	public static final int SQLITE_PERM = 3;   /* Access permission denied */
	public static final int SQLITE_ABORT = 4;   /* Callback routine requested an abort */
	public static final int SQLITE_BUSY = 5;   /* The database file is locked */
	public static final int SQLITE_LOCKED = 6;   /* A table in the database is locked */
	public static final int SQLITE_NOMEM = 7;   /* A malloc() failed */
	public static final int SQLITE_READONLY = 8;   /* Attempt to write a readonly database */
	public static final int SQLITE_INTERRUPT = 9;   /* Operation terminated by sqlite3_interrupt()*/
	public static final int SQLITE_IOERR = 10;   /* Some kind of disk I/O error occurred */
	public static final int SQLITE_CORRUPT = 11;   /* The database disk image is malformed */
	public static final int SQLITE_NOTFOUND = 12;   /* Unknown opcode in sqlite3_file_control() */
	public static final int SQLITE_FULL = 13;   /* Insertion failed because database is full */
	public static final int SQLITE_CANTOPEN = 14;   /* Unable to open the database file */
	public static final int SQLITE_PROTOCOL = 15;   /* Database lock protocol error */
	public static final int SQLITE_EMPTY = 16;   /* Database is empty */
	public static final int SQLITE_SCHEMA = 17;   /* The database schema changed */
	public static final int SQLITE_TOOBIG = 18;   /* String or BLOB exceeds size limit */
	public static final int SQLITE_CONSTRAINT = 19;   /* Abort due to constraint violation */
	public static final int SQLITE_MISMATCH = 20;   /* Data type mismatch */
	public static final int SQLITE_MISUSE = 21;   /* Library used incorrectly */
	public static final int SQLITE_NOLFS = 22;   /* Uses OS features not supported on host */
	public static final int SQLITE_AUTH = 23;   /* Authorization denied */
	public static final int SQLITE_FORMAT = 24;   /* Auxiliary database format error */
	public static final int SQLITE_RANGE = 25;   /* 2nd parameter to sqlite3_bind out of range */
	public static final int SQLITE_NOTADB = 26;   /* File opened that is not a database file */
	public static final int SQLITE_NOTICE = 27;   /* Notifications from sqlite3_log() */
	public static final int SQLITE_WARNING = 28;   /* Warnings from sqlite3_log() */
	//endregion

	//region Constants
	public static final int SQLITE_OK = 0;   /* Successful result */
	public static final int SQLITE_ROW = 100;  /* sqlite3_step() has another row ready */
	public static final int SQLITE_DONE = 101;  /* sqlite3_step() has finished executing */
	//endregion

	//region Column Types
	public static final int SQLITE_INTEGER = 1;
	public static final int SQLITE_FLOAT = 2;
	public static final int SQLITE_TEXT = 3;
	public static final int SQLITE_BLOB = 4;
	public static final int SQLITE_NULL = 5;
	//endregion

	//region Open Flags
	public static final int SQLITE_OPEN_READONLY = 0x00000001;  /* Ok for sqlite3_open_v2() */
	public static final int SQLITE_OPEN_READWRITE = 0x00000002;  /* Ok for sqlite3_open_v2() */
	public static final int SQLITE_OPEN_CREATE = 0x00000004;  /* Ok for sqlite3_open_v2() */
	public static final int SQLITE_OPEN_SHAREDCACHE = 0x00020000;  /* Ok for sqlite3_open_v2() */
	public static final int SQLITE_OPEN_FULLMUTEX = 0x00010000;  /* Ok for sqlite3_open_v2() */
	//endregion
	//endregion

	//region Constructors
	private Pluma() {
		throw new RuntimeException("No instances!");
	}
	//endregion
}
