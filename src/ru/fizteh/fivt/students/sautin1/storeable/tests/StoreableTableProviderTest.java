package ru.fizteh.fivt.students.sautin1.storeable.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.sautin1.storeable.StoreableTable;
import ru.fizteh.fivt.students.sautin1.storeable.StoreableTableProvider;
import ru.fizteh.fivt.students.sautin1.storeable.StoreableTableProviderFactory;
import ru.fizteh.fivt.students.sautin1.storeable.TableRow;
import ru.fizteh.fivt.students.sautin1.storeable.shell.FileUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by sautin1 on 12/13/14.
 */
public class StoreableTableProviderTest {
    Path testDir;
    StoreableTableProviderFactory factory;
    StoreableTableProvider provider;
    String tableName;
    String existingDirectoryName;
    private List<Class<?>> valueTypes;

    @Before
    public void setUp() throws Exception {
        testDir = Paths.get("").resolve("test");
        factory = new StoreableTableProviderFactory();
        provider = factory.create(testDir.toString());
        tableName = "testTable";
        existingDirectoryName = "existingDir";

        valueTypes = new ArrayList<>();
        valueTypes.add(Integer.class);
        valueTypes.add(Boolean.class);
        valueTypes.add(String.class);
    }

    @After
    public void tearDown() throws Exception {
        try {
            provider.removeTable(tableName);
        } catch (Exception e) {
            // haven't created such table
        }
        FileUtils.clearDirectory(testDir);
    }

    @Test
    public void testCreateTable() throws Exception {
        StoreableTable table = provider.createTable(tableName, valueTypes);
        assertNotNull(table);

        StoreableTable table2 = provider.createTable(tableName, valueTypes);
        assertNull(table2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableExistingDirectory() throws Exception {
        Path tempDirPath = Files.createTempDirectory(testDir, existingDirectoryName);
        provider.createTable(tempDirPath.getFileName().toString(), valueTypes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableNotExistingPath() throws Exception {
        provider.createTable(existingDirectoryName + "/" + existingDirectoryName, valueTypes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableNull() throws Exception {
        provider.createTable(null, valueTypes);
    }

    @Test
    public void testGetTable() throws Exception {
        StoreableTable createdTable = provider.createTable(tableName, valueTypes);
        StoreableTable gotTable = provider.getTable(tableName);
        assertEquals(createdTable, gotTable);

        gotTable = provider.getTable(tableName + tableName);
        assertNull(gotTable);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableNull() throws Exception {
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableEmpty() throws Exception {
        provider.getTable("");
    }

    @Test
    public void testRemoveTable() throws Exception {
        provider.createTable(tableName, valueTypes);
        provider.removeTable(tableName);
        assertNull(provider.getTable(tableName));
        assertFalse(Files.exists(testDir.resolve(tableName)));
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveNotExistingTable() throws Exception {
        provider.removeTable(tableName);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableNull() throws Exception {
        provider.removeTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableDeletedDir() throws Exception {
        provider.createTable(tableName, valueTypes);
        FileUtils.removeDirectory(testDir.resolve(tableName));
        provider.removeTable(tableName);
    }

    @Test
    public void testDeserialize() throws Exception {
        StoreableTable table = provider.createTable(tableName, valueTypes);
        TableRow correctValue = new TableRow(valueTypes);
        correctValue.setColumnAt(0, 1);
        correctValue.setColumnAt(1, false);
        correctValue.setColumnAt(2, "Test");
        Storeable tableValue = provider.deserialize(table, "<row><col>1</col><col>false</col><col>Test</col></row>");
        assertEquals(correctValue, tableValue);

        correctValue.setColumnAt(1, null);
        tableValue = provider.deserialize(table, "<row><col>1</col><null/><col>Test</col></row>");
        assertEquals(correctValue, tableValue);
    }

    @Test(expected = ParseException.class)
    public void testDeserializeTypeMismatch() throws Exception {
        StoreableTable table = provider.createTable(tableName, valueTypes);
        TableRow correctValue = new TableRow(valueTypes);
        correctValue.setColumnAt(0, 1);
        correctValue.setColumnAt(1, false);
        correctValue.setColumnAt(2, "Test");
        provider.deserialize(table, "<row><col>wrong</col><col>false</col><col>Test</col></row>");
    }

    @Test(expected = ParseException.class)
    public void testDeserializeWrongXMLString() throws Exception {
        StoreableTable table = provider.createTable(tableName, valueTypes);
        TableRow correctValue = new TableRow(valueTypes);
        correctValue.setColumnAt(0, 1);
        correctValue.setColumnAt(1, false);
        correctValue.setColumnAt(2, "Test");
        provider.deserialize(table, "<col>1</col><col>false</col><col>Test</col></row>");
    }

    @Test(expected = ParseException.class)
    public void testDeserializeWrongXMLStringLength() throws Exception {
        StoreableTable table = provider.createTable(tableName, valueTypes);
        TableRow correctValue = new TableRow(valueTypes);
        correctValue.setColumnAt(0, 1);
        correctValue.setColumnAt(1, false);
        correctValue.setColumnAt(2, "Test");
        provider.deserialize(table, "<row><col>1</col><col>false</col></row>");
    }

    @Test
    public void testSerialize() throws Exception {
        StoreableTable table = provider.createTable(tableName, valueTypes);
        TableRow value = new TableRow(valueTypes);
        value.setColumnAt(0, 1);
        value.setColumnAt(1, false);
        value.setColumnAt(2, "Test");
        String correctlySerialized = "<row><col>1</col><col>false</col><col>Test</col></row>";
        String tableSerialized = provider.serialize(table, value);
        assertEquals(correctlySerialized, tableSerialized);

        value.setColumnAt(1, null);
        correctlySerialized = "<row><col>1</col><null/><col>Test</col></row>";
        tableSerialized = provider.serialize(table, value);
        assertEquals(correctlySerialized, tableSerialized);
    }

    @Test(expected = ColumnFormatException.class)
    public void testSerializeTypeMismatch() throws Exception {
        StoreableTable table = provider.createTable(tableName, valueTypes);
        valueTypes.add(Long.class);
        TableRow value = new TableRow(valueTypes);
        value.setColumnAt(0, 1);
        value.setColumnAt(1, false);
        value.setColumnAt(2, "Test");
        value.setColumnAt(3, null);
        provider.serialize(table, value);
    }

    @Test
    public void testCreateFor() throws Exception {
        StoreableTable table = provider.createTable(tableName, valueTypes);
        Storeable value = provider.createFor(table);
        for (int valueIndex = 0; valueIndex < table.getColumnsCount(); ++valueIndex) {
            assertNull(value.getColumnAt(valueIndex));
        }
        try {
            value.getColumnAt(table.getColumnsCount());
        } catch (IndexOutOfBoundsException e) {
            // everything is good
        }
    }

    @Test
    public void testCreateForArgs() throws Exception {
        StoreableTable table = provider.createTable(tableName, valueTypes);
        List<Object> list = new ArrayList<>();
        list.add(54);
        list.add(true);
        list.add("TestString");
        Storeable value = provider.createFor(table, list);
        for (int valueIndex = 0; valueIndex < table.getColumnsCount(); ++valueIndex) {
            assertEquals(value.getColumnAt(valueIndex), list.get(valueIndex));
        }
        try {
            value.getColumnAt(table.getColumnsCount());
        } catch (IndexOutOfBoundsException e) {
            // everything is good
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testCreateForArgsIndexException() throws Exception {
        StoreableTable table = provider.createTable(tableName, valueTypes);
        List<Object> list = new ArrayList<>();
        list.add(54);
        list.add(true);
        list.add("TestString");
        list.add("Odd String");
        provider.createFor(table, list);
    }

    @Test(expected = ColumnFormatException.class)
    public void testCreateForArgsFormatException() throws Exception {
        StoreableTable table = provider.createTable(tableName, valueTypes);
        List<Object> list = new ArrayList<>();
        list.add("WrongType");
        list.add(true);
        list.add("TestString");
        provider.createFor(table, list);
    }

    @Test
    public void testGetTableNames() throws Exception {
        int tableCount = 50;
        Set<String> tableNameSet = new HashSet<>();
        for (int tableIndex = 0; tableIndex < tableCount; ++tableIndex) {
            String name = tableName + tableIndex;
            tableNameSet.add(name);
            provider.createTable(name, valueTypes);
        }
        List<String> tableNames = provider.getTableNames();
        assertEquals(tableNames.size(), tableNameSet.size());
        for (int nameIndex = 0; nameIndex < tableNames.size(); ++nameIndex) {
            assertTrue(tableNameSet.contains(tableNames.get(nameIndex)));
        }
    }

}
