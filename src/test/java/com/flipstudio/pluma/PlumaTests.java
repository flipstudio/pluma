package com.flipstudio.pluma;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Pietro Caselani
 * On 13/01/14
 * Pluma
 */
@RunWith(JUnit4.class)
public class PlumaTests {
  //region Fields
  private static final File DATABASE_FILE = new File(System.getProperty("user.dir"), "TestDB.sqlite");
  private static final ArrayList<String> QUERIES = new ArrayList<String>();
  private Database mDatabase;
  //endregion

  //region Setup
  @Before public void setUp() throws Exception {
    if (DATABASE_FILE.exists() && !DATABASE_FILE.delete()) {
      throw new RuntimeException("Could not delete database.");
    }

    mDatabase = Database.open(DATABASE_FILE.getPath());

    assertTrue("Could not open database.", mDatabase.isOpen() && DATABASE_FILE.exists());

    mDatabase.setDatabaseListener(new Database.DatabaseListener() {
      @Override public void onExecuteQuery(String query) {
        recordQuery(query);
      }
    });

    mDatabase.exec("PRAGMA foreign_keys = ON;");

    assertQueries("PRAGMA foreign_keys = ON;");
  }
  //endregion

  //region Updates
  @Test public void testUpdate() throws Exception {
    startTest("Testing updates");

    assertTrue("Could not create table 'people'.", mDatabase.executeUpdate(
        "CREATE TABLE people (id INTEGER PRIMARY KEY, name TEXT, lastName text, birth datetime)"));

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

    assertQueries(
        "CREATE TABLE people (id INTEGER PRIMARY KEY, name TEXT, lastName text, birth datetime)",
        "INSERT INTO people (name, lastName, birth) VALUES (?, ?, ?)",
        "INSERT INTO people (name, lastName, birth) VALUES (?, ?, ?)",
        "INSERT INTO people (name, lastName, birth) VALUES (:name, :lastName, :birth)");
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
}