Pluma
=====

Pluma is a SQLite wrapper for Java and Android, based on [iOS FMDB][1].

#Usage
---
There are three main classes in Pluma:
1. ```Database``` - Represents a single SQLite database. Used for executing SQL statements.
2. ```ResultSet``` - Represents the results of a query executed by ```Database```.
3. ```Statement``` - Represents a single compiled SQL statement. Use ```Database.prepareStatement```.

Almost all operations performed by the ```Database``` and ```ResultSet``` throws a ```SQLiteException```.

Database creation
--
```java
Database database = new Database("/path/db.sqlite");
database.open();
```

Executing Updates
--
Any sort of SQL statement which is not a SELECT statement qualifies as an update.

```java
database.executeUpdate("CREATE TABLE people (id INTEGER PRIMARY KEY, name TEXT)");
```

Executing Queries
--
```java
ResultSet resultSet = database.executeQuery("SELECT id, name FROM people");
while (resultSet.next()) {
  int id = resultSet.getInt(0);
  String name = resultSet.getString(1);
}
```

In a loop, the ```next()``` closes the ```ResultSet```automatically when
the statement is done. Otherwise you should always call
```close()``` on ```ResultSet```

Statement Parameters
--
* List
```java
List<Object> args = Arrays.asList(5, 10);
ResultSet resultSet = database.executeQuery("SELECT name FROM people WHERE id = ? AND id = ?", args);
```
or
```java
ResultSet resultSet = database.executeQuery("SELECT name FROM people WHERE id = ? AND id = ?", 5, 10);
```

* Named parameters
```java
Map<String, Object> argsMap = new HashMap<String, Object>();
argsMap.put("personId", 5);
ResultSet resultSet = database.executeQuery("SELECT name FROM people WHERE id = :personId", argsMap);
```

[1]: https://github.com/ccgus/fmdb