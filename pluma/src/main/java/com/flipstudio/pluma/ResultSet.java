package com.flipstudio.pluma;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

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
		boolean returnValue;
		int rc = mStatement.step();

		if (returnValue = rc != SQLITE_ROW && close()) {
			if (rc != SQLITE_DONE) {
				throw new SQLiteException(rc, mDatabase.getLastErrorMessage());
			}
		}

		return !returnValue;
	}

	public boolean close() throws SQLiteException {
		if (mStatement.isClosed()) return true;

		int rc = mStatement.close();
		if (rc != SQLITE_OK) {
			throw new SQLiteException(rc, mDatabase.getLastErrorMessage());
		}
		return true;
	}

	public int getColumnCount() {
		return mStatement.getColumnCount();
	}

	public int getColumnIndex(String columnName) {
		return mStatement.getColumnIndex(columnName);
	}

	public String getColumnName(int columnIndex) {
		return mStatement.getColumnName(columnIndex);
	}

	public int getColumnType(int columnIndex) {
		return mStatement.getColumnType(columnIndex);
	}

	public boolean isNull(int columnIndex) {
		return mStatement.isNull(columnIndex);
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

	public String getString(int columnIndex) {
		return mStatement.getString(columnIndex);
	}

	public Date getDate(int columnIndex) {
		return new Date(getLong(columnIndex));
	}

	public BigDecimal getBigDecimal(int columnIndex) {
		return new BigDecimal(getDouble(columnIndex));
	}

	public BigInteger getBigInteger(int columnIndex) {
		return getBigDecimal(columnIndex).toBigInteger();
	}

	public Object getObject(int columnIndex) {
		return mStatement.getObject(columnIndex);
	}

	public String getSQL() {
		return mStatement.getSQL();
	}
	//endregion
}
