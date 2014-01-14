package com.flipstudio.pluma;

import java.util.HashMap;

import static com.flipstudio.pluma.Pluma.SQLITE_NULL;

/**
 * Created by Pietro Caselani
 * On 13/11/13
 * Pluma
 */
final class Statement {
  //region Fields
  private final long mStmt;
  private final int mColumnCount;
  private final HashMap<String, Integer> mColumnNameIndexes;
  //endregion

  //region Constructors
  Statement(long stmt) {
    mStmt = stmt;
    mColumnCount = getColumnCount(stmt);

    mColumnNameIndexes = new HashMap<String, Integer>(mColumnCount);

    for (int i = 0; i < mColumnCount; i++) {
      mColumnNameIndexes.put(getColumnName(mStmt, i), i);
    }
  }
  //endregion

  //region Native
  private native int bind(long stmt, int index, int value);
  private native int bind(long stmt, int index, long value);
  private native int bind(long stmt, int index, double value);
  private native int bind(long stmt, int index, String value);
  private native int bindNull(long stmt, int index);
  private native int getInt(long stmt, int columnIndex);
  private native long getLong(long stmt, int columnIndex);
  private native double getDouble(long stmt, int columnIndex);
  private native String getText(long stmt, int columnIndex);
  private native int getColumnType(long stmt, int columnIndex);
  private native String getColumnName(long stmt, int columnIndex);
  private native int bindParameterCount(long stmt);
  private native int bindParameterIndex(long stmt, String parameterName);
  private native int getColumnCount(long stmt);
  private native int step(long stmt);
  private native int finalize(long stmt);
  //TODO getBlob(), getObject(), reset(Clear binds)...
  //endregion

  //region Public
  //region Binds
  public int bind(int index, int value) {
    return bind(mStmt, index, value);
  }

  public int bind(int index, long value) {
    return bind(mStmt, index, value);
  }

  public int bind(int index, double value) {
    return bind(mStmt, index, value);
  }

  public int bind(int index, String value) {
    return bind(mStmt, index, value);
  }

  public int bindNull(int index) {
    return bindNull(mStmt, index);
  }
  //endregion

  //region Columns
  public int getInt(int columnIndex) {
    return getInt(mStmt, columnIndex);
  }

  public long getLong(int columnIndex) {
    return getLong(mStmt, columnIndex);
  }

  public double getDouble(int columnIndex) {
    return getDouble(mStmt, columnIndex);
  }

  public String getString(int columnIndex) {
    return getText(mStmt, columnIndex);
  }

  public int getColumnType(int columnIndex) {
    return getColumnType(mStmt, columnIndex);
  }

  public boolean isNull(int columnIndex) {
    return getColumnType(mStmt, columnIndex) == SQLITE_NULL;
  }

  public String getColumnName(int columnIndex) {
    return getColumnName(mStmt, columnIndex);
  }

  public int getColumnIndex(String columnName) {
    if (mColumnNameIndexes.containsKey(columnName)) {
      return mColumnNameIndexes.get(columnName);
    }

    return -1;
  }

  public int getColumnCount() {
    return mColumnCount;
  }
  //endregion

  public int step() {
    return step(mStmt);
  }

  public int getBindParameterCount() {
    return bindParameterCount(mStmt);
  }

  public int getParameterIndex(String parameterName) {
    return bindParameterIndex(mStmt, parameterName);
  }

  public int close() throws SQLiteException {
    return finalize(mStmt);
  }
  //endregion
}
