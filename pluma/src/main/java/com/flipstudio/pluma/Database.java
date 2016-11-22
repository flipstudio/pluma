package com.flipstudio.pluma;

import com.flipstudio.collections.Array;
import com.flipstudio.collections.functions.Action0;
import com.flipstudio.collections.functions.Func0;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.flipstudio.pluma.Pluma.*;
import static java.util.Arrays.asList;

/**
 * Created by Pietro Caselani
 * On 13/11/13
 * Pluma
 */
public final class Database {
	//region Fields
	private final String mPath;
	private String mTempDir;
	private long mDB;
	private DatabaseListener mDatabaseListener;
	private int mTransactionCount;
	private Array<Action0> mPendingActions;
	private final HashMap<String, StatementCache> mStatementsCaches;
	//endregion

	//region Static
	static {
		System.loadLibrary("pluma");
	}
	//endregion

	//region Constructors
	public Database(String path) {
		mPath = path;
		mTempDir = new File(path).getParent();
		mPendingActions = new Array<>();
		mStatementsCaches = new HashMap<>();
	}
	//endregion

	//region Native
	private native long open(String filePath, int flags, int[] ppOpenCode, String[] ppOpenError);

	private native long prepare(long db, String sql, int[] ppPrepareCode);

	private native int exec(long db, String sql, String[] ppOutError);

	private native int close(long db);

	private native long lastInsertId(long db);

	private native String lastErrorMessage(long db);

	private native void setTempDir(String tempDir);

	private native int registerFunction(long db, String name, int numArgs, long functionPtr);
	//endregion

	//region Public
	public void open() throws SQLiteException {
		open(SQLITE_OPEN_CREATE | SQLITE_OPEN_READWRITE | SQLITE_OPEN_FULLMUTEX);
	}

	public void open(int flags) throws SQLiteException {
		int[] codes = new int[1];
		String[] errors = new String[1];

		long db = open(mPath, flags, codes, errors);

		if (codes[0] != SQLITE_OK || db == 0 || errors[0] != null) {
			throw new SQLiteException(codes[0], errors[0]);
		}

		mDB = db;

		setTempDir(mTempDir);
	}

	public void setTempDirectory(String tempDir) {
		if (isOpen()) {
			setTempDir(tempDir);
		}

		mTempDir = tempDir;
	}

	public Statement prepareStatement(String sql) throws SQLiteException {
		int[] prepareCode = new int[1];
		int rc;

		long stmt = prepare(mDB, sql, prepareCode);
		rc = prepareCode[0];

		if (rc != SQLITE_OK || stmt == 0) {
			throw new SQLiteException(rc, lastErrorMessage(mDB), sql);
		}

		return new Statement(stmt);
	}

	public StatementCache getCachedStatement(String name) {
		StatementCache statementCache = mStatementsCaches.get(name);
		if (statementCache == null) {
			statementCache = new StatementCache(this);
			mStatementsCaches.put(name, statementCache);
		}

		return statementCache;
	}

	public void execute(String sql) throws SQLiteException {
		String[] errors = new String[1];

		int rc = exec(mDB, sql, errors);

		if (rc != SQLITE_OK) {
			throw new SQLiteException(rc, errors[0], sql);
		}

		notifyListenerOnExecuteQuery(sql);
	}

	public void execute(String sql, DatabaseIteractionListener listener) throws SQLiteException {
		final ResultSet resultSet = executeQuery(sql);
		if (listener != null) {
			while (resultSet.next()) {
				listener.onInteract(resultSet);
			}
		}
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

	public String getLastErrorMessage() {
		return lastErrorMessage(mDB);
	}

	public boolean executeUpdate(Statement statement) throws SQLiteException {
		int rc = statement.step();
		String query = statement.getSQL();

		statement.close();

		if (rc != SQLITE_DONE) {
			throw new SQLiteException(rc, getLastErrorMessage(), query);
		}

		notifyListenerOnExecuteQuery(query);

		return true;
	}

	public ResultSet executeQuery(Statement statement) throws SQLiteException {
		ResultSet rs = new ResultSet(this, statement);

		notifyListenerOnExecuteQuery(statement.getSQL());

		return rs;
	}
	/*
	Use with native code.
	sqlite3 *db = reinterpret_cast<sqlite3*>(jdb);
	 */
	public long getSQLiteHandler() {
		return mDB;
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

	public String getDatabasePath() {
		return mPath;
	}

	public void registerFunction(String name, int numArgs, SQLiteFunction function) throws SQLiteException {
		final int rc = registerFunction(mDB, name, numArgs, function.getNativeHandler());
		if (rc != SQLITE_OK) {
			throw new SQLiteException(rc, lastErrorMessage(mDB));
		}
	}

	public void executeAfterCommit(Action0 action) {
		if (!isInTransaction()) {
			action.call();
		} else {
			mPendingActions.add(action);
		}
	}

	public void beginTransaction() {
		try {
			executeUpdate("BEGIN");
			mTransactionCount++;
		} catch (SQLiteException e) {
			throw new RuntimeException(e.toString());
		}
	}

	public void commitTransaction() {
		try {
			executeUpdate("COMMIT");
			mTransactionCount--;
		} catch (SQLiteException e) {
			throw new RuntimeException(e.toString());
		}

		if (!isInTransaction()) {
			for (final Action0 action : mPendingActions) {
				action.call();
			}

			mPendingActions.clear();
		}
	}

	public void rollbackTransaction() {
		try {
			executeUpdate("ROLLBACK");
			mTransactionCount--;

			mPendingActions.clear();
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}

	public void executeWithinTransaction(Action0 action) {
		try {
			beginTransaction();
			action.call();
			commitTransaction();
		} catch (Exception e) {
			rollbackTransaction();
			throw new RuntimeException(e.toString());
		}
	}

	public Object executeWithinTransaction(Func0<Object> action) {
		Object result;

		try {
			beginTransaction();
			result = action.call();
			commitTransaction();
		} catch (Exception e) {
			rollbackTransaction();
			throw new RuntimeException(e.toString());
		}

		return result;
	}
	//endregion

	//region Private
	private boolean executeUpdate(String sql, List<Object> listArgs, Map<String, Object> mapArgs) throws SQLiteException {
		Statement statement = compileStatement(sql, listArgs, mapArgs);

		return executeUpdate(statement);
	}

	private ResultSet executeQuery(String query, List<Object> listArgs, Map<String, Object> mapArgs) throws SQLiteException {
		return executeQuery(compileStatement(query, listArgs, mapArgs));
	}

	private void notifyListenerOnExecuteQuery(String sql) {
		if (mDatabaseListener != null) {
			mDatabaseListener.onExecuteQuery(sql);
		}
	}

	private Statement compileStatement(String query, List<Object> listArgs, Map<String, Object> mapArgs) throws SQLiteException {
		Statement statement = prepareStatement(query);

		return compileStatement(statement, listArgs, mapArgs);
	}

	private Statement compileStatement(Statement statement, List<Object> listArgs, Map<String, Object> mapArgs) throws SQLiteException {
		int rc, index = 1, bindsCount = statement.getBindParameterCount() + 1;

		if (mapArgs != null && mapArgs.size() > 0) {
			String parameterName;
			int parameterIndex;

			for (String key : mapArgs.keySet()) {
				parameterName = ":" + key;

				if ((parameterIndex = statement.getParameterIndex(parameterName)) > 0) {
					statement.bindObject(parameterIndex, mapArgs.get(key));
					index++;
				}
			}
		} else if (listArgs != null && listArgs.size() > 0) {
			for (Object object : listArgs) {
				statement.bindObject(index++, object);
			}
		}

		if (index != bindsCount) {
			rc = statement.close();
			if (rc != SQLITE_OK) {
				throw new SQLiteException(rc, lastErrorMessage(mDB));
			}

			throw new SQLiteException(SQLITE_MISUSE, "The bind count is not correct for the number of variables", statement.getSQL());
		}

		return statement;
	}
	//endregion

	//region Getters and Setters
	public DatabaseListener getDatabaseListener() {
		return mDatabaseListener;
	}

	public void setDatabaseListener(DatabaseListener databaseListener) {
		mDatabaseListener = databaseListener;
	}

	public boolean isInTransaction() {
		return mTransactionCount > 0;
	}
	//endregion

	public interface DatabaseListener {
		void onExecuteQuery(String query);
	}

	public interface DatabaseIteractionListener {
		void onInteract(ResultSet resultSet);
	}
}
