package com.flipstudio.pluma;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.flipstudio.pluma.Pluma.SQLITE_DONE;
import static com.flipstudio.pluma.Pluma.SQLITE_MISUSE;
import static com.flipstudio.pluma.Pluma.SQLITE_OK;
import static com.flipstudio.pluma.Pluma.SQLITE_OPEN_CREATE;
import static com.flipstudio.pluma.Pluma.SQLITE_OPEN_READWRITE;
import static java.util.Arrays.asList;

/**
 * Created by Pietro Caselani
 * On 13/11/13
 * Pluma
 */
public final class Database {
  //region Fields
  private long mDB;
  private DatabaseListener mDatabaseListener;
  //endregion

  //region Static
  static {
    System.loadLibrary("pluma");
  }

  public static Database open(String filePath) throws SQLiteException {
    return open(filePath, SQLITE_OPEN_CREATE | SQLITE_OPEN_READWRITE);
  }

  public static Database open(String filePath, int flags) throws SQLiteException {
    long[] dbArray = new long[] {0};
    String[] errors = new String[1];

    int rc = open(filePath, dbArray, flags, errors);

    long db = dbArray[0];

    if (rc != SQLITE_OK || db == 0 || errors[0] != null) {
      throw new SQLiteException(rc, errors[0]);
    }

    return new Database(db);
  }
  //endregion

  //region Constructors
  private Database(long db) {
    mDB = db;
  }
  //endregion

  //region Native
  private static native int open(String filePath, long[] ppDB, int flags, String[] ppOpenError);
  private native int prepare(long db, String sql, long[] ppStmt);
  private native int exec(long db, String sql, String[] ppOutError);
  private native int close(long db);
  private native long lastInsertId(long db);
  private native String lastErrorMessage(long db);
  //endregion

  //region Public
  public void execute(String sql) throws SQLiteException {
    String[] errors = new String[1];

    int rc = exec(mDB, sql, errors);

    if (rc != SQLITE_OK) {
      throw new SQLiteException(rc, errors[0]);
    }

    notifyListenerOnExecuteQuery(sql);
  }

  public boolean executeUpdate(String sql) throws SQLiteException {
    return executeUpdate(sql, (Object[]) null);
  }

  public boolean executeUpdate(String sql, Map<String, Object> arguments) throws SQLiteException {
    return executeUpdate(sql, null, arguments);
  }

  public boolean executeUpdate(String sql, Object... arguments) throws SQLiteException {
    return executeUpdate(sql, arguments == null ? null : asList(arguments));
  }

  public boolean executeUpdate(String sql, List<Object> arguments) throws SQLiteException {
    return executeUpdate(sql, arguments, null);
  }

  public ResultSet executeQuery(String sql) throws SQLiteException {
    return executeQuery(sql, (Object[]) null);
  }

  public ResultSet executeQuery(String sql, Map<String, Object> arguments) throws SQLiteException {
    return executeQuery(sql, null, arguments);
  }

  public ResultSet executeQuery(String sql, Object... arguments) throws SQLiteException {
    return executeQuery(sql, arguments == null ? null : asList(arguments));
  }

  public ResultSet executeQuery(String sql, List<Object> arguments) throws SQLiteException {
    return executeQuery(sql, arguments, null);
  }

  public long getLastInsertId() {
    return lastInsertId(mDB);
  }

  public boolean close() throws SQLiteException {
    int rc = close(mDB);
    if (rc != SQLITE_OK) {
      throw new SQLiteException(rc, lastErrorMessage(mDB));
    }

    mDB = 0;
    return true;
  }

  public boolean isClosed() {
    return mDB == 0;
  }

  public boolean isOpen() {
    return mDB > 0;
  }
  //endregion

  //region Package
  String getLastErrorMessage() {
    return lastErrorMessage(mDB);
  }
  //endregion

  //region Private
  private boolean executeUpdate(String sql, List<Object> listArgs, Map<String, Object> mapArgs) throws SQLiteException {
    notifyListenerOnExecuteQuery(sql);

    return compileStatement(sql, listArgs, mapArgs).step() == SQLITE_DONE;
  }

  private ResultSet executeQuery(String sql, List<Object> listArgs, Map<String, Object> mapArgs) throws SQLiteException {
    notifyListenerOnExecuteQuery(sql);

    return new ResultSet(this, compileStatement(sql, listArgs, mapArgs));
  }

  private void notifyListenerOnExecuteQuery(String sql) {
    if (mDatabaseListener != null) {
      mDatabaseListener.onExecuteQuery(sql);
    }
  }

  private Statement compileStatement(String sql, List<Object> listArgs, Map<String, Object> mapArgs) throws SQLiteException {
    long[] stmtArray = new long[] {0};

    int rc = prepare(mDB, sql, stmtArray);

    long stmt = stmtArray[0];

    stmtArray[0] = 0;

    if (rc != SQLITE_OK || stmt <= 0) {
      throw new SQLiteException(rc, lastErrorMessage(mDB));
    }

    Statement statement = new Statement(stmt);

    int index = 1, bindsCount = statement.getBindParameterCount() + 1;

    if (mapArgs != null && mapArgs.size() > 0) {
      String parameterName;
      int parameterIndex;

      for (String key : mapArgs.keySet()) {
        parameterName = ":" + key;

        if ((parameterIndex = statement.getParameterIndex(parameterName)) > 0) {
          bindObject(mapArgs.get(key), parameterIndex, statement);
          index++;
        } else {
          throw new SQLiteException(SQLITE_MISUSE, "Parameter index not found for name " + key + "' in query:\n" + sql);
        }
      }
    } else if (listArgs != null && listArgs.size() > 0) {
      for (Object object : listArgs) {
        bindObject(object, index++, statement);
      }
    }

    if (index != bindsCount) {
      rc = statement.close();
      if (rc != SQLITE_OK) {
        throw new SQLiteException(rc, lastErrorMessage(mDB));
      }

      throw new SQLiteException(SQLITE_MISUSE, "The bind count is not correct for the number of variables");
    }

    return statement;
  }

  private void bindObject(Object object, int index, Statement statement) throws SQLiteException {
    int rc;
    if (object == null) {
      rc = statement.bindNull(index);
    } else if (object instanceof Integer) {
      rc = statement.bind(index, (Integer) object);
    } else if (object instanceof Boolean) {
      rc = statement.bind(index, (Boolean) object ? 1 : 0);
    } else if (object instanceof Long) {
      rc = statement.bind(index, (Long) object);
    } else if (object instanceof Date) {
      rc = statement.bind(index, ((Date) object).getTime());
    } else if (object instanceof Double) {
      rc = statement.bind(index, (Double) object);
    } else if (object instanceof Float) {
      rc = statement.bind(index, ((Float) object).doubleValue());
    } else if (object instanceof String) {
      rc = statement.bind(index, (String) object);
    } else if (object instanceof BigDecimal) {
      rc = statement.bind(index, ((BigDecimal) object).doubleValue());
    } else if (object instanceof BigInteger) {
      rc = statement.bind(index, ((BigInteger) object).longValue());
    } else {
      rc = -1;
    }

    if (rc != SQLITE_OK) {
      throw new SQLiteException(rc, lastErrorMessage(mDB));
    }
  }
  //endregion

  //region Getters and Setters
  public DatabaseListener getDatabaseListener() {
    return mDatabaseListener;
  }

  public void setDatabaseListener(DatabaseListener databaseListener) {
    mDatabaseListener = databaseListener;
  }
  //endregion

  public interface DatabaseListener {
    public void onExecuteQuery(String query);
  }
}
