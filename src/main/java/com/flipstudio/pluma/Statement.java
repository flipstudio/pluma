package com.flipstudio.pluma;

/**
 * Created by Pietro Caselani
 * On 13/11/13
 * SQLite
 */
final class Statement {
  //region Fields
  private final long mStmt;
  //endregion

  //region Constructors
  Statement(long stmt) {
    mStmt = stmt;
  }

  private Statement() {
    throw new RuntimeException(String.format("Use %s(%s) constructor",
        Statement.class.getName(), Long.class.getName()));
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
  private native int bindParameterCount(long stmt);
  private native int bindParameterIndex(long stmt, String parameterName);
  private native int step(long stmt);
  private native int finalize(long stmt);
  //TODO getBlob(), getObject(), getColumnType(), isNull(), getColumnName, getColumnIndex, reset(Clear binds)...
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

  public String getText(int columnIndex) {
    return getText(mStmt, columnIndex);
  }
  //endregion

  public int step() {
    return step(mStmt);
  }

  public int getBindParameterCount() {
    return bindParameterCount(mStmt);
  }

  public int getParamenterIndex(String parameterName) {
    return bindParameterIndex(mStmt, parameterName);
  }

  public int close() {
    return finalize(mStmt);
  }
  //endregion
}
