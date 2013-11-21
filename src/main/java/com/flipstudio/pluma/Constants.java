package com.flipstudio.pluma;

/**
 * Created by Pietro Caselani
 * On 14/11/13
 * SQLite
 */
public final class Constants {
  //region Fields
  static final int SQLITE_OK = 0; /* Successful result */
  static final int SQLITE_ROW = 100; /* sqlite3_step() has another row ready */
  static final int SQLITE_DONE = 101; /* sqlite3_step() has finished executing */
  static final int SQLITE_ERROR = 1; /* SQL error or missing database */
  static final int SQLITE_MISUSE = 21; /* Library used incorrectly */

  public static final int SQLITE_OPEN_READONLY = 0x00000001;  /* Ok for sqlite3_open_v2() */
  public static final int SQLITE_OPEN_READWRITE = 0x00000002;  /* Ok for sqlite3_open_v2() */
  public static final int SQLITE_OPEN_CREATE = 0x00000004;  /* Ok for sqlite3_open_v2() */
  //endregion

  //region Constructors
  private Constants() {}
  //endregion
}
