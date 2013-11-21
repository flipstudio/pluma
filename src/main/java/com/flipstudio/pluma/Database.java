package com.flipstudio.pluma;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.flipstudio.pluma.Constants.SQLITE_DONE;
import static com.flipstudio.pluma.Constants.SQLITE_OK;
import static com.flipstudio.pluma.Constants.SQLITE_OPEN_CREATE;
import static com.flipstudio.pluma.Constants.SQLITE_OPEN_READWRITE;
import static java.util.Arrays.asList;

/**
 * Created by Pietro Caselani
 * On 13/11/13
 * SQLite
 */
public final class Database {
  //region Fields
  private long mDB;
  private String mLastErrorMessage;
  private int mLastErrorCode;
  private DatabaseListener mDatabaseListener;
  //endregion

  //region Static
  static {
    System.loadLibrary("pluma");
  }

  public static Database open(String filePath) {
    return open(filePath, SQLITE_OPEN_CREATE | SQLITE_OPEN_READWRITE);
  }

  public static Database open(String filePath, int flags) {
    long[] dbArray = new long[1];
    dbArray[0] = 0;
    String[] errors = new String[1];

    int rc = open(filePath, dbArray, flags, errors);

    long db = dbArray[0];

    if (errors.length > 0 && errors[0] != null) {
      throw new RuntimeException(errors[0]);
    }

    return rc == SQLITE_OK && db != 0 ? new Database(db) : null;
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
  private native int lastErrorCode(long db);
  private native long lastInsertId(long db);
  private native String lastErrorMessage(long db);
  //endregion

  //region Public
  public void exec(String sql) {
    String[] errors = new String[1];

    int rc = exec(mDB, sql, errors);

    if (rc != SQLITE_OK) {
      throw new RuntimeException(errors[0]);
    }
  }

  public boolean executeUpdate(String sql) {
    return executeUpdate(sql, (Object[]) null);
  }

  public boolean executeUpdate(String sql, Map<String, Object> arguments) {
    return executeUpdate(sql, null, arguments);
  }

  public boolean executeUpdate(String sql, Object... arguments) {
    return executeUpdate(sql, arguments == null ? null : asList(arguments));
  }

  public boolean executeUpdate(String sql, List<Object> arguments) {
    return executeUpdate(sql, arguments, null);
  }

  public ResultSet executeQuery(String sql) {
    return executeQuery(sql, (Object[]) null);
  }

  public ResultSet executeQuery(String sql, Map<String, Object> arguments) {
    return executeQuery(sql, null, arguments);
  }

  public ResultSet executeQuery(String sql, Object... arguments) {
    return executeQuery(sql, arguments == null ? null : asList(arguments));
  }

  public ResultSet executeQuery(String sql, List<Object> arguments) {
    return executeQuery(sql, arguments, null);
  }

  public long getLastInsertId() {
    return lastInsertId(mDB);
  }

  public String getLastErrorMessage() {
    if (mLastErrorMessage == null) {
      mLastErrorMessage = lastErrorMessage(mDB);
    }
    return mLastErrorMessage;
  }

  public int getLastErrorCode() {
    if (mLastErrorCode == SQLITE_OK) {
      mLastErrorCode = lastErrorCode(mDB);
    }
    return mLastErrorCode;
  }

  public boolean close() {
    int rc = close(mDB);
    if (rc != SQLITE_OK) {
      throw new RuntimeException("Could not close database. " + getLastErrorMessage());
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

  //region Private
  private Statement prepare(String sql) {
    long[] stmtArray = new long[] {0};

    int rc = prepare(mDB, sql, stmtArray);

    long stmt = stmtArray[0];

    stmtArray[0] = 0;

    if (rc != SQLITE_OK || stmt <= 0) {
      throw new RuntimeException(getLastErrorMessage() + " - " + getLastErrorCode());
    }

    return new Statement(stmt);
  }

  private boolean executeUpdate(String sql, List<Object> listArgs, Map<String, Object> mapArgs) {
    if (mDatabaseListener != null) {
      mDatabaseListener.onExecuteQuery(sql);
    }

    return compileStatement(sql, listArgs, mapArgs).step() == SQLITE_DONE;
  }

  private ResultSet executeQuery(String sql, List<Object> listArgs, Map<String, Object> mapArgs) {
    if (mDatabaseListener != null) {
      mDatabaseListener.onExecuteQuery(sql);
    }

    return new ResultSet(this, compileStatement(sql, listArgs, mapArgs));
  }

  private Statement compileStatement(String sql, List<Object> listArgs, Map<String, Object> mapArgs) {
    Statement statement = prepare(sql);

    int index = 1, bindsCount = statement.getBindParameterCount() + 1;

    if (mapArgs != null && mapArgs.size() > 0) {
      String parameterName;
      int paramenterIndex;

      for (String key : mapArgs.keySet()) {
        parameterName = ":" + key;

        if ((paramenterIndex = statement.getParamenterIndex(parameterName)) > 0) {
          bindObject(mapArgs.get(key), paramenterIndex, statement);
          index++;
        } else {
          throw new RuntimeException("Paramenter index not found for name " + key);
        }
      }
    } else if (listArgs != null && listArgs.size() > 0) {
      for (Object object : listArgs) {
        bindObject(object, index++, statement);
      }
    }

    if (index != bindsCount) {
      statement.close();
      throw new RuntimeException("The bind count is not correct for the number of variables");
    }

    return statement;
  }

  private void bindObject(Object object, int index, Statement statement) {
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
      throw new RuntimeException(getLastErrorMessage());
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
