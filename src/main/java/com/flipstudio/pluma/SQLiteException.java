package com.flipstudio.pluma;

/**
 * Created by Pietro Caselani
 * On 08/01/14
 * Pluma
 */
public final class SQLiteException extends Exception {
  //region Fields
  private final int mErrorCode;
  private final String mErrorMessage, mQuery;
  //endregion

  //region Constructors
  SQLiteException(int errorCode, String errorMessage) {
    this(errorCode, errorMessage, null);
  }

  SQLiteException(int errorCode, String errorMessage, String query) {
    mErrorCode = errorCode;
    mErrorMessage = errorMessage;
    mQuery = query;
  }
  //endregion

  //region Getters and Setters
  public int getErrorCode() {
    return mErrorCode;
  }

  public String getErrorMessage() {
    return mErrorMessage;
  }

  public String getQuery() {
    return mQuery;
  }
  //endregion

  //region Object
  @Override public String toString() {
    return "SQLiteException\nError code: " + mErrorCode + "\nError Message: " + mErrorMessage + (mQuery != null ? "\nQuery: " + mQuery : "");
  }
  //endregion
}