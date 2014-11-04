package ru.fizteh.fivt.students.torunova.junit.tests;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.torunova.junit.Database;
import ru.fizteh.fivt.students.torunova.junit.Table;

import java.io.File;

import static org.junit.Assert.*;

public class TableProviderTest {
	TableProvider db;
	File testDirectory;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	@Before
	public void setUp() throws Exception {
		testDirectory = folder.newFolder("db");
		db = new Database(testDirectory.getAbsolutePath());
	}
	@Test
	public void testGetTableWithNormalName() throws Exception {
		File table = new File(testDirectory, "table");
		Table t = new Table(table.getAbsolutePath());
		db.createTable("table");
		assertEquals(t, db.getTable("table"));
	}
	@Test(expected = IllegalArgumentException.class)
	public void testGetTableWithNullName() throws Exception {
		db.getTable(null);
	}
	@Test(expected = IllegalArgumentException.class)
	public void testGetTableWithIllegalName1() throws Exception {
		db.getTable("efhgeoiu" + File.separator + "fiuhwoiu");
	}
	@Test(expected = IllegalArgumentException.class)
	public void testGetTableWithIllegalName2() throws Exception {
		db.getTable("..");
	}
	@Test(expected = IllegalArgumentException.class)
	public void testGetTableWithIllegalName3() throws Exception {
		db.getTable(".");
	}

	@Test
	public void testCreateNotExistingTable() throws Exception {
		File table = new File(testDirectory, "table");
		Table t = new Table(table.getAbsolutePath());
		assertEquals(t, db.createTable("table"));
	}

	@Test
	public void testCreateExistingTable() throws Exception {
		db.createTable("table");
		assertEquals(null, db.createTable("table"));
	}
	@Test(expected = IllegalArgumentException.class)
	public void testCreateTableWithNullName() throws Exception {
		db.createTable(null);
	}
	@Test(expected = IllegalArgumentException.class)
	public void testCreateTableWithIllegalName1() throws Exception {
		db.createTable("iwyrgiqeuy" + File.separator + "sifughwoieuhvi");
	}
	@Test(expected = IllegalArgumentException.class)
	public void testCreateTableWithIllegalName2() throws Exception {
		db.createTable("..");
	}
	@Test(expected = IllegalArgumentException.class)
	public void testCreateTableWithIllegalName3() throws Exception {
		db.createTable(".");
	}
	@Test
	public void testRemoveExistingTable() throws Exception {
		db.createTable("table");
		db.removeTable("table");
		assertNotEquals(null, db.createTable("table"));
	}
	@Test(expected = IllegalStateException.class)
	public void testRemoveNotExistingTable() throws Exception {
		db.removeTable("table");
	}
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveTableWithNullName() throws Exception {
		db.removeTable(null);
	}
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveTableWithIllegalName1() throws Exception {
		db.removeTable("qjhfoqpoiger" + File.separator + "wiuhrogiuhwo");
	}
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveTableWithIllegalName2() throws Exception {
		db.removeTable("..");
	}
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveTableWithIllegalName3() throws Exception {
		db.removeTable(".");
	}

}
