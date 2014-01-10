package com.flipstudio.pluma;

import static com.flipstudio.pluma.Pluma.SQLITE_DONE;
import static com.flipstudio.pluma.Pluma.SQLITE_OK;
import static com.flipstudio.pluma.Pluma.SQLITE_ROW;

/**
 * Created by Pietro Caselani
 * On 14/11/13
 * Pluma
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
  //endregion

  //region Public methods
  public boolean next() throws SQLiteException {
    int rc = mStatement.step();

    if (rc != SQLITE_ROW && close()) {

      if (rc != SQLITE_DONE) {
        throw new SQLiteException(rc, mDatabase.getLastErrorMessage());
      }
    }

    return true;
  }

  public boolean close() throws SQLiteException {
    int rc = mStatement.close();
    if (rc != SQLITE_OK) {
      throw new SQLiteException(rc, mDatabase.getLastErrorMessage());
    }
    return true;
  }

  public double getDouble(int columnIndex) throws SQLiteException {
    return mStatement.getDouble(columnIndex);
  }

  public int getInt(int columnIndex) throws SQLiteException {
    return mStatement.getInt(columnIndex);
  }

  public long getLong(int columnIndex) throws SQLiteException {
    return mStatement.getLong(columnIndex);
  }

  public String getString(int columnIndex) throws SQLiteException {
    return mStatement.getString(columnIndex);
  }
  //endregion
}
