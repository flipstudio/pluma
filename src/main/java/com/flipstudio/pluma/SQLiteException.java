package com.flipstudio.pluma;

/**
 * Created by Pietro Caselani
 * On 08/01/14
 * Pluma
 */
public final class SQLiteException extends Exception {
  //region Fields
  private int mErrorCode;
  private String mErrorMessage;
  //endregion

  //region Constructors
  SQLiteException(int errorCode, String errorMessage) {
    mErrorCode = errorCode;
    mErrorMessage = errorMessage;
  }
  //endregion

  //region Getters and Setters
  public int getErrorCode() {
    return mErrorCode;
  }

  public String getErrorMessage() {
    return mErrorMessage;
  }
  //endregion
}