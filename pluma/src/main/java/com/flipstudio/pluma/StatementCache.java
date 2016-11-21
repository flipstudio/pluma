package com.flipstudio.pluma;

import java.util.LinkedHashMap;

import static com.flipstudio.pluma.Pluma.SQLITE_OK;

/**
 * Created by Maurício Feijó
 * On 11/21/16
 * Pluma
 */
public class StatementCache {
	//region Fields
	private static int MAX_CHACHED_STATEMENTS = 15;
	private final LinkedHashMap<String, Statement> mStatementsCache;
	final private Database mDatabase;
	//endregion

	//region Constructor
	StatementCache(Database database) {
		if (database == null) throw new RuntimeException("Cannot create a StatementCache without a database");

		mDatabase = database;
		mStatementsCache = new LinkedHashMap<>(MAX_CHACHED_STATEMENTS);
	}
	//endregion

	//region Public
	public Statement getStatement(String query) {
		Statement statement = mStatementsCache.get(query);

		if (statement == null || statement.isClosed()) {
			if (statement == null && mStatementsCache.size() == MAX_CHACHED_STATEMENTS) {
				final String key = mStatementsCache.keySet().iterator().next();
				mStatementsCache.get(key).close();
				mStatementsCache.remove(key);
			}

			try {
				statement = mDatabase.prepareStatement(query);
			} catch (SQLiteException e) {
				throw new RuntimeException(e);
			}

			mStatementsCache.put(query, statement);
		}

		return statement;
	}
	//endregion
}
