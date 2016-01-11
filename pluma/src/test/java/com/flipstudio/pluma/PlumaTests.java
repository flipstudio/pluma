package com.flipstudio.pluma;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Pietro Caselani
 * On 13/01/14
 * Pluma
 */

@RunWith(JUnit4.class)
public class PlumaTests {
	//region Fields
	private static final File DATABASE_FILE = new File(System.getProperty("java.io.tmpdir"), "TestDB.sqlite");
	private static final ArrayList<String> QUERIES = new ArrayList<String>();
	private Database mDatabase;
	//endregion

	//region Setup
	@Before public void setUp() throws Exception {
		if (DATABASE_FILE.exists() && !DATABASE_FILE.delete()) {
			throw new RuntimeException("Can not delete database file.");
		}

		mDatabase = new Database(DATABASE_FILE.getPath());
		mDatabase.open();

		assertTrue("Can not open database.", mDatabase.isOpen() && DATABASE_FILE.exists());

		mDatabase.execute(
				"CREATE TABLE people (id INTEGER PRIMARY KEY, name TEXT, lastName text, birth datetime);\n"
						+ "INSERT INTO people (name,lastName,birth) VALUES ('Jeremy','Xyla','1179129666000');\n"
						+ "INSERT INTO people (name,lastName,birth) VALUES ('Damon','Althea','1029576914000');\n"
						+ "INSERT INTO people (name,lastName,birth) VALUES ('Reese','Kalia','763347737000');");

		mDatabase.setDatabaseListener(new Database.DatabaseListener() {
			@Override public void onExecuteQuery(String query) {
				recordQuery(query);
			}
		});
	}
	//endregion

	//region Updates
	@Test public void testUpdate() throws Exception {
		startTest("Testing updates.");

		String insertListQuery = "INSERT INTO people (name, lastName, birth) VALUES (?, ?, ?)";

		ArrayList<Object> listArgs = new ArrayList<Object>();
		listArgs.add("Arden");
		listArgs.add("Winter");
		listArgs.add(new Date(638340461000L));

		assertTrue("Could not insert record.", mDatabase.executeUpdate(insertListQuery, listArgs));

		assertTrue("Could not insert second record.",
				mDatabase.executeUpdate(insertListQuery, "Damian", "Ifeoma", new Date(1021405116000L)));

		HashMap<String, Object> mapArgs = new HashMap<String, Object>();
		mapArgs.put("name", "Chaney");
		mapArgs.put("lastName", "Kathleen");
		mapArgs.put("birth", new Date(812710812000L));

		assertTrue("Could not insert third record.", mDatabase.executeUpdate(
				"INSERT INTO people (name, lastName, birth) VALUES (:name, :lastName, :birth)", mapArgs));

		assertQueries("INSERT INTO people (name, lastName, birth) VALUES (?, ?, ?)",
				"INSERT INTO people (name, lastName, birth) VALUES (?, ?, ?)",
				"INSERT INTO people (name, lastName, birth) VALUES (:name, :lastName, :birth)");
	}
	//endregion

	//region Queries
	@Test public void testQuery() throws Exception {
		startTest("Testing queries.");

		ResultSet rs = mDatabase.executeQuery("SELECT id, name, lastName, birth FROM people");

		assertEquals("Unexpected column count.", 4, rs.getColumnCount());

		ArrayList<HashMap<String, Object>> people = new ArrayList<HashMap<String, Object>>();
		while (rs.next()) {
			HashMap<String, Object> person = new HashMap<String, Object>();
			person.put("id", rs.getInt(0));
			person.put("name", rs.getString(1));
			person.put("lastName", rs.getString(2));
			person.put("birth", rs.getDate(3));
			people.add(person);
		}

		assertEquals("Unexpected people count.", 3, people.size());
		assertEquals("Unexpected person id.", 2, people.get(1).get("id"));
		assertEquals("Unexpected person name.", "Jeremy", people.get(0).get("name"));
		assertEquals("Unexpected person last name.", "Kalia", people.get(2).get("lastName"));
		assertEquals("Unexpected person birth.", new Date(763347737000L), people.get(2).get("birth"));

		rs = mDatabase.executeQuery("SELECT id, name FROM people WHERE id = ?", 2);
		assertEquals("Unexpected column count.", 2, rs.getColumnCount());
		if (rs.next()) {
			assertEquals("Unexpected name.", "Damon", rs.getString(1));
			assertEquals("Unexpected id.", 2, rs.getBigInteger(0).intValue());
		}

		Map<String, Object> args = new TreeMap<String, Object>();
		args.put("FirstName", "Damon");

		rs = mDatabase.executeQuery("SELECT id FROM people WHERE name = :FirstName", args);
		if (rs.next()) {
			assertEquals("Unexpected id.", 2, rs.getInt(0));
		}

		assertQueries(
				"SELECT id, name, lastName, birth FROM people",
				"SELECT id, name FROM people WHERE id = ?",
				"SELECT id FROM people WHERE name = :FirstName");
	}
	//endregion

	//region Next
	@Test public void testNext() throws Exception {
		startTest("Next");

		ResultSet rs = mDatabase.executeQuery("SELECT name FROM people WHERE id = ?", 2);
		if (rs.next()) {
			assertEquals("Unexpected name", "Damon", rs.getString(0));
		}
		assertTrue("Unable to close result set ", rs.close());

		assertTrue("Unable to execute update", mDatabase.executeUpdate("DELETE FROM people"));

		rs = mDatabase.executeQuery("SELECT id, name FROM people");
		assertFalse("Unexpected value", rs.next());
		assertTrue("Unexpected value", rs.close());
	}
	//endregion

	//region Last Insert Id
	@Test public void testLastInsertId() throws Exception {
		String insert = "INSERT INTO people (name, lastName, birth) VALUES (?, ?, ?)";

		boolean result = mDatabase.executeUpdate(insert, "Carl", "Ifeoma", 757399978000L);

		assertTrue("Can not insert record.", result);

		long lastId = mDatabase.getLastInsertId();

		assertEquals("Unexpected last id.", 4, lastId);
	}
	//endregion

	//region Exec
	@Test public void testExec() throws Exception {
		mDatabase.execute("PRAGMA foreign_keys = ON");

		ResultSet rs = mDatabase.executeQuery("PRAGMA foreign_keys");
		if (rs.next()) {
			assertEquals("Unexpected foreign keys.", 1, rs.getInt(0));
		}

		assertTrue("Can not close result set.", rs.close());

		mDatabase.execute("PRAGMA foreign_keys = OFF");

		rs = mDatabase.executeQuery("PRAGMA foreign_keys");
		if (rs.next()) {
			assertEquals("Unexpected foreign keys.", 0, rs.getInt(0));
		}

		assertTrue("Can not close result set.", rs.close());
	}
	//endregion

	//region Statements
	@Test public void testResetStatement() throws Exception {
		Statement statement = mDatabase.prepareStatement("INSERT INTO people (name, lastName, birth) VALUES (?, ?, ?)");

		List<Object> binds = Arrays.<Object>asList(
				"Donna", "Hope", new Date(1374143861000L),
				"Judith", "Maia", new Date(1415985639000L),
				"Willa", "Janna", new Date(1381038472000L)
		);
		int bindIndex = 1;

		for (Object object : binds) {
			assertEquals("Could not bind object", Pluma.SQLITE_OK, statement.bindObject(bindIndex++, object));
			if (bindIndex == 4) {
				assertEquals("Could not execute statement", Pluma.SQLITE_DONE, statement.step());
				bindIndex = 1;
				assertEquals("Could not reset statement", Pluma.SQLITE_OK, statement.reset());
			}
		}

		assertEquals("Could not close statement", Pluma.SQLITE_OK, statement.close());
	}

	@Test public void testClearStatement() throws Exception {
		Statement statement = mDatabase.prepareStatement("INSERT INTO people (name, lastName, birth) VALUES (?, ?, ?)");

		assertEquals("Could not bind first object", Pluma.SQLITE_OK, statement.bind(1, "Cecilia"));
		assertEquals("Could not bind second object", Pluma.SQLITE_OK, statement.bind(2, "Marshall"));
		assertEquals("Could not bind third object", Pluma.SQLITE_OK, statement.bind(3, 1419088806000L));

		assertEquals("Could not clear statement", Pluma.SQLITE_OK, statement.clearBindings());

		assertEquals("Could not bind first object", Pluma.SQLITE_OK, statement.bind(1, "Ivory"));
		assertEquals("Could not bind second object", Pluma.SQLITE_OK, statement.bind(2, "Mariko"));
		assertEquals("Could not bind third object", Pluma.SQLITE_OK, statement.bind(3, 1421394493000L));

		assertEquals("Could not execute statement", Pluma.SQLITE_DONE, statement.step());

		long id = mDatabase.getLastInsertId();

		ResultSet rs = mDatabase.executeQuery("SELECT name FROM people WHERE id = ?", id);
		assertTrue("Empty result set", rs.next());
		assertEquals("Invalid name", "Ivory", rs.getString(0));
		assertTrue("Could not close result set", rs.close());

		assertEquals("Could not close statement", Pluma.SQLITE_OK, statement.close());
	}
	//endregion

	//region Functions
	@Test public void testFunctionFirstChar() throws Exception {
		mDatabase.registerFunction("FIRST_CHAR", 1, new SQLiteFunction() {
			@Override protected void run(int argc) {
				if (argc == 1) {
					final String text = getStringArg(0);
					if (text != null && text.length() > 0) {
						setStringResult(Character.toString(text.charAt(0)));
						return;
					}
				}

				setNullResult();
			}
		});

		final ResultSet resultSet = mDatabase.executeQuery("SELECT FIRST_CHAR(name) FROM people");
		final String[] strings = {"J", "D", "R"};
		int index = 0;

		while (resultSet.next()) {
			assertEquals("Unexpected result.", strings[index++], resultSet.getString(0));
		}
	}

	@Test public void testFunctionThread() throws Exception {
		mDatabase.registerFunction("LAST_CHAR", 1, new SQLiteFunction() {
			@Override protected void run(int argc) {
				assertEquals("Unexpected thread", "PlumaTestThread", Thread.currentThread().getName());
				if (argc == 1) {
					final String text = getStringArg(0);
					if (text != null && text.length() > 0) {
						setStringResult(Character.toString(text.charAt(text.length() - 1)));
						return;
					}
				}
				setNullResult();
			}
		});

		new Thread(new Runnable() {
			@Override public void run() {
				try {
					final ResultSet rs = mDatabase.executeQuery("SELECT LAST_CHAR(name) FROM people");
					final String[] strings = {"y", "n", "e"};
					int index = 0;

					while (rs.next()) {
						assertEquals("Unexpected result.", strings[index++], rs.getString(0));
					}

				} catch (SQLiteException e) {
					throw new RuntimeException(e);
				}
			}
		}, "PlumaTestThread").start();
	}
	//endregion

	//region Close
	@Test public void testClose() throws Exception {
		assertTrue("Database is not open.", mDatabase.isOpen());

		assertTrue("Can not close database.", mDatabase.close());

		assertTrue("Database is not closed.", mDatabase.isClosed());
	}
	//endregion

	//region Get SQL
	@Test public void testGetSQL() throws Exception {
		final Statement statement = mDatabase.prepareStatement("INSERT INTO people (name, lastName, birth) VALUES (?, ?, ?)");

		assertEquals("Statement SQL does not match", "INSERT INTO people (name, lastName, birth) VALUES (?, ?, ?)", statement.getSQL());

		ResultSet rs = mDatabase.executeQuery("SELECT name FROM people WHERE id = ?", 2);
		assertEquals("ResultSet SQL does not match.", "SELECT name FROM people WHERE id = ?", rs.getSQL());

		statement.close();
		rs.close();
	}
	//endregion

	//region Private
	private void startTest(String message) {
		QUERIES.clear();
		System.out.println(message);
	}

	private void assertQueries(String... queries) {
		assertEquals("Unexpected queries count", queries.length, QUERIES.size());

		for (int i = 0; i < queries.length; i++) {
			assertEquals("Unexpected query", queries[i].toLowerCase(), QUERIES.get(i).toLowerCase());
		}
	}

	private void recordQuery(String query) {
		System.out.println(">>> " + query);
		QUERIES.add(query);
	}
	//endregion

	//region FTS
	@Test public void testCreateTable() throws Exception {
		String sql = "CREATE VIRTUAL TABLE _ftsProduct USING fts4(name, price, quantity, clientId, notindexed=clientId, tokenize=character)";

		boolean result = mDatabase.executeUpdate(sql);

		assertTrue("Can create virtual table.", result);
	}
	//endregion
}
