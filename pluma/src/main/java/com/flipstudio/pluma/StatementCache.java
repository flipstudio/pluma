package com.flipstudio.pluma;

import com.flipstudio.collections.Array;

import java.util.HashMap;

/**
 * Created by Maurício Feijó
 * On 11/21/16
 * Pluma
 */
public class StatementCache {
	//region Fields
	private static int MAX_CHACHED_STATEMENTS = 15;
	private final Array<String> mQueriesCache;
	private final HashMap<String, Statement> mStatementsCache;
	final private Database mDatabase;
	//endregion

	//region Constructor
	StatementCache(Database database) {
		if (database == null) throw new RuntimeException("Cannot create a StatementCache without a database");

		mDatabase = database;
		mStatementsCache = new HashMap<>(MAX_CHACHED_STATEMENTS);
		mQueriesCache = new Array<>(MAX_CHACHED_STATEMENTS);
	}
	//endregion

	//region Public
	public Statement getStatement(String query) {
		Statement statement;
		synchronized(this) {
			try {
				statement = mStatementsCache.get(query);

				if (statement == null) {
					if (mQueriesCache.size() == MAX_CHACHED_STATEMENTS) {
						final String key = mQueriesCache.get(0);
						mStatementsCache.get(key).close();
						mStatementsCache.remove(key);
						mQueriesCache.removeAt(0);
					}

					statement = mDatabase.prepareStatement(query);
					mStatementsCache.put(query, statement);
				} else {
					if (statement.isClosed()) {
						statement = mDatabase.prepareStatement(query);
						mStatementsCache.put(query, statement);
					}

					mQueriesCache.remove(query);
				}

				mQueriesCache.add(query);
			} catch (SQLiteException e) {
				throw new RuntimeException(e);
			}
		}

		return statement;
	}
	//endregion
}
