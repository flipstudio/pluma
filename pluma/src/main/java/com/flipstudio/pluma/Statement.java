package com.flipstudio.pluma;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.TreeMap;

import static com.flipstudio.pluma.Pluma.SQLITE_BLOB;
import static com.flipstudio.pluma.Pluma.SQLITE_FLOAT;
import static com.flipstudio.pluma.Pluma.SQLITE_INTEGER;
import static com.flipstudio.pluma.Pluma.SQLITE_MISUSE;
import static com.flipstudio.pluma.Pluma.SQLITE_NULL;
import static com.flipstudio.pluma.Pluma.SQLITE_OK;
import static com.flipstudio.pluma.Pluma.SQLITE_TEXT;

/**
 * Created by Pietro Caselani
 * On 13/11/13
 * Pluma
 */
public final class Statement {
	//region Fields
	private final int mColumnCount;
	private final TreeMap<String, Integer> mColumnNameIndexes;
	private long mStmt;
	//endregion

	//region Constructors
	Statement(long stmt) {
		mStmt = stmt;
		mColumnCount = getColumnCount(stmt);

		mColumnNameIndexes = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);

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

	private native int clearBindings(long stmt);

	private native int reset(long stmt);

	private native String getSQL(long stmt);
	//TODO getBlob()...
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

	public int bind(Object[] arguments) {
		return bind(arguments, arguments.length);
	}

	public int bind(Object[] arguments, int end) {
		int rc = SQLITE_OK;
		for (int i = 0; i < end && rc == SQLITE_OK; ++i) {
			rc = this.bindObject(i + 1, arguments[i]);
		}
		return rc;
	}

	public int bindNull(int index) {
		return bindNull(mStmt, index);
	}

	public int bindObject(int index, Object object) {
		int rc;
		if (object == null) {
			rc = bindNull(index);
		} else if (object instanceof Integer) {
			rc = bind(index, (Integer) object);
		} else if (object instanceof Boolean) {
			rc = bind(index, (Boolean) object ? 1 : 0);
		} else if (object instanceof Long) {
			rc = bind(index, (Long) object);
		} else if (object instanceof Date) {
			rc = bind(index, ((Date) object).getTime() / 1000);
		} else if (object instanceof Double) {
			rc = bind(index, (Double) object);
		} else if (object instanceof Float) {
			rc = bind(index, ((Float) object).doubleValue());
		} else if (object instanceof String) {
			rc = bind(index, (String) object);
		} else if (object instanceof BigDecimal) {
			rc = bind(index, ((BigDecimal) object).doubleValue());
		} else if (object instanceof BigInteger) {
			rc = bind(index, ((BigInteger) object).longValue());
		} else {
			rc = SQLITE_MISUSE;
		}

		return rc;
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

	public Object getObject(int columnIndex) {
		int columnType = getColumnType(mStmt, columnIndex);
		if (columnType == SQLITE_NULL) {
			return null;
		} else if (columnType == SQLITE_INTEGER) {
			return getLong(mStmt, columnIndex);
		} else if (columnType == SQLITE_FLOAT) {
			return getDouble(mStmt, columnIndex);
		} else if (columnType == SQLITE_TEXT) {
			return getText(mStmt, columnIndex);
		} else if (columnType == SQLITE_BLOB) {
			//TODO getBlob()
			return null;
		}

		return null;
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

	public int clearBindings() {
		return clearBindings(mStmt);
	}

	public int reset() {
		return reset(mStmt);
	}

	public int getBindParameterCount() {
		return bindParameterCount(mStmt);
	}

	public int getParameterIndex(String parameterName) {
		return bindParameterIndex(mStmt, parameterName);
	}

	public boolean isClosed() {
		return mStmt == 0;
	}

	public int close() {
		int rc = finalize(mStmt);

		if (rc == SQLITE_OK) mStmt = 0;

		return rc;
	}

	public String getSQL() {
		return getSQL(mStmt);
	}
	//endregion
}
