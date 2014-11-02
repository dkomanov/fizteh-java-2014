package ru.fizteh.fivt.students.torunova.junit.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.torunova.junit.Table;


import java.io.File;

import static org.junit.Assert.*;

public class TableTest {
	Table testTable;
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	@Before
	public void setUp() throws Exception {
		File testDirectory = folder.newFolder("table");
		testTable = new Table(testDirectory.getAbsolutePath());
	}

	@Test
	public void testGetName() throws Exception {
		assertEquals("table", testTable.getName());
	}

	@Test
	public void testGetOrdinaryKey() throws Exception {
		testTable.put("key", "value");
		assertEquals("value", testTable.get("key"));
	}
	@Test(expected = IllegalArgumentException.class)
	public void testGetWithException() throws Exception {
		testTable.get(null);
	}
	@Test
	public void testGetNotExistingKey() throws Exception {
		assertEquals(null, testTable.get("notExistingKey"));
	}

	@Test
	public void testPut() throws Exception {

	}

	@Test
	public void testRemove() throws Exception {

	}

	@Test
	public void testSize() throws Exception {

	}

	@Test
	public void testCommit() throws Exception {

	}

	@Test
	public void testRollback() throws Exception {

	}

	@Test
	public void testList() throws Exception {

	}
}