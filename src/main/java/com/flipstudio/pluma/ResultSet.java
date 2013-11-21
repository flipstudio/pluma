package com.flipstudio.pluma;

import static com.flipstudio.pluma.Constants.SQLITE_ERROR;
import static com.flipstudio.pluma.Constants.SQLITE_MISUSE;
import static com.flipstudio.pluma.Constants.SQLITE_OK;
import static com.flipstudio.pluma.Constants.SQLITE_ROW;

/**
 * Created by Pietro Caselani
 * On 14/11/13
 * SQLite
 */
public final class ResultSet {
  //region Fields
  private final Statement mStatement;
  private final Database mDatabase;
  //endregion

  //region Constructors
  ResultSet(Database database, Statement statement) {
    mDatabase = database;
    mStatement = statement;
  }

  private ResultSet() {
    throw new RuntimeException(String.format("Use %s(%s) constructor",
        ResultSet.class.getName(), Statement.class.getName()));
  }
  //endregion

  ////region Public methods
  public boolean next() {
    int rc = mStatement.step();

    if (rc != SQLITE_ROW) {
      close();

      if (rc == SQLITE_ERROR || rc == SQLITE_MISUSE) {
        throw new RuntimeException(String.format("Error: %s - Code: %d",
            mDatabase.getLastErrorMessage(), mDatabase.getLastErrorCode()));
      }
    }

    return rc == SQLITE_ROW;
  }

  public boolean close() {
    return mStatement.close() == SQLITE_OK;
  }

  public double getDouble(int columnIndex) {
    return mStatement.getDouble(columnIndex);
  }

  public int getInt(int columnIndex) {
    return mStatement.getInt(columnIndex);
  }

  public long getLong(int columnIndex) {
    return mStatement.getLong(columnIndex);
  }

  public String getText(int columnIndex) {
    return mStatement.getText(columnIndex);
  }
  //endregion
}
