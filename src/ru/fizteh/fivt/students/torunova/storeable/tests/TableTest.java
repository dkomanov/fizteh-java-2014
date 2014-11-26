package ru.fizteh.fivt.students.torunova.storeable.tests;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.students.torunova.storeable.DatabaseWrapper;
import ru.fizteh.fivt.students.torunova.storeable.TableWrapper;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TableTest{

	TableWrapper testTable;
	DatabaseWrapper db;
	private static final Object[] VALUE = {"String value", 42, false, 1.42f, 42L, (byte) 42, 1.42};
	private static final Object[] VALUE_1 = {"String value 1", 38, true, 1.38f, 38L, (byte) 38, 1.38};
	private static final Object[] VALUE_2 = {"String value 2", 31, true, 1.31f, null, (byte) 31, 1.31};
	private static final Object[] WRONG_VALUE = {"String value", false, 23, 1.42f, null, (byte) 42, 1.38};
 	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	@Before
	public void setUp() throws Exception {
		File testDirectory = folder.newFolder("db");
		db = new DatabaseWrapper(testDirectory.getAbsolutePath());
		testTable = (TableWrapper) db.createTable("table", Arrays.asList(String.class, Integer.class, Boolean.class, Float.class, Long.class, Byte.class, Double.class));
	}

	@Test
	public void testPutNew() throws Exception {
		testTable.put("key", db.createFor(testTable, Arrays.asList(VALUE)));
		assertEquals(db.createFor(testTable, Arrays.asList(VALUE)), testTable.get("key"));
	}
	@Test public void testPutExisting() throws Exception {
		testTable.put("key", db.createFor(testTable, Arrays.asList(VALUE_1)));
		assertEquals(db.createFor(testTable, Arrays.asList(VALUE_1)), testTable.put("key", db.createFor(testTable, Arrays.asList(VALUE_2))));
	}
	@Test(expected = ColumnFormatException.class)
	public void testPutWrongValue() {
		testTable.put("key", db.createFor(testTable, Arrays.asList(WRONG_VALUE)));
	}

	@Test
	public void testRemoveExistingValue() throws Exception {
		testTable.put("key", db.createFor(testTable, Arrays.asList(VALUE)));
		assertEquals(db.createFor(testTable, Arrays.asList(VALUE)), testTable.remove("key"));
	}

	@Test
	public void testRemoveNotExistingValue() throws Exception {
		assertEquals(null, testTable.remove("key"));
	}

	@Test
	public void testSize() throws Exception {
		testTable.put("key 1", db.createFor(testTable, Arrays.asList(VALUE_1)));
		testTable.put("key 2", db.createFor(testTable, Arrays.asList(VALUE_2)));
		testTable.remove("key 1");
		assertEquals(1, testTable.size());
	}

	@Test
	public void testList() throws Exception {
		testTable.put("key 1", db.createFor(testTable, Arrays.asList(VALUE_1)));
		testTable.put("key 2", db.createFor(testTable, Arrays.asList(VALUE_2)));
		List<String> expectedKeys = Arrays.asList("key 1", "key 2");
		List<String> realKeys = testTable.list();
		Collections.sort(expectedKeys);
		Collections.sort(realKeys);
		assertEquals(expectedKeys, realKeys);
	}

	@Test
	public void testCommit() throws Exception {
		testTable.put("key 1", db.createFor(testTable, Arrays.asList(VALUE_1)));
		testTable.put("key 2", db.createFor(testTable, Arrays.asList(VALUE_2)));
		assertEquals(2, testTable.commit());
	}

	@Test
	public void testRollback() throws Exception {
		testTable.put("key 1", db.createFor(testTable, Arrays.asList(VALUE_1)));
		testTable.put("key 2", db.createFor(testTable, Arrays.asList(VALUE_2)));
		assertEquals(2, testTable.rollback());
	}

	@Test
	public void testGetNumberOfUncommittedChanges() throws Exception {
		testTable.put("key 1", db.createFor(testTable, Arrays.asList(VALUE_1)));
		testTable.put("key 2", db.createFor(testTable, Arrays.asList(VALUE_2)));
		assertEquals(2, testTable.getNumberOfUncommittedChanges());
	}

	@Test
	public void testGetColumnsCount() throws Exception {
		assertEquals(7, testTable.getColumnsCount());
	}

	@Test
	public void testGetColumnType() throws Exception {
		assertEquals(Boolean.class, testTable.getColumnType(2));
	}
}