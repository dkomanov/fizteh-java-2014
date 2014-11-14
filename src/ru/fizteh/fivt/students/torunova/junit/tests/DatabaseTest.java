package ru.fizteh.fivt.students.torunova.junit.tests;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.torunova.junit.Database;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class DatabaseTest {
	Database db;
	File testDirectory;
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	@Before
	public void setUp() throws Exception {
		testDirectory = folder.newFolder("db");
		db = new Database(testDirectory.getAbsolutePath());
	}

	@Test
	public void testUseExistingTable() throws Exception {
		db.createTable("table");
		db.useTable("table");
		assertEquals("table", db.currentTable.getName());
	}
	@Test
	public void testUseNotExistingTable() throws Exception {
		assertEquals(false, db.useTable("table"));
	}

	@Test
	public void testShowTables() throws Exception {
		db.createTable("t1");
		db.createTable("t2");
		db.useTable("t1");
		db.currentTable.put("a", "b");
		Map<String, Integer> tables = new HashMap<>();
		tables.put("t1", 1);
		tables.put("t2", 0);
		assertEquals(tables, db.showTables());
	}
}
