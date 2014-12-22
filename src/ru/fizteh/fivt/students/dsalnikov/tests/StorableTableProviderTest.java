package ru.fizteh.fivt.students.dsalnikov.tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.dsalnikov.storable.StorableTable;
import ru.fizteh.fivt.students.dsalnikov.storable.StorableTableProvider;
import ru.fizteh.fivt.students.dsalnikov.utils.CorrectnessCheck;
import ru.fizteh.fivt.students.dsalnikov.utils.CountingTools;
import ru.fizteh.fivt.students.dsalnikov.utils.FileMapUtils;
import ru.fizteh.fivt.students.dsalnikov.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class StorableTableProviderTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    StorableTableProvider provider;
    File directory;
    List<Class<?>> testTypesListFirst;
    List<Class<?>> testTypesListSecond;

    @Before
    public void setUpTestObject() throws IOException {
        directory = folder.newFolder();
        provider = new StorableTableProvider(directory);
    }

    @After
    public void tearDownTestObject() throws IOException {
        FileUtils.forceRemoveDirectory(directory);
    }

    @Test
    public void createTableTest() throws IOException {
        testTypesListFirst = FileMapUtils.createListOfTypesFromString("int int int");
        testTypesListSecond = FileMapUtils.createListOfTypesFromString("int double boolean String");

        Assert.assertNotNull(provider.createTable("testTable1", testTypesListFirst));
        Assert.assertNull(provider.createTable("testTable1", testTypesListFirst));

        Assert.assertNotNull(provider.createTable("русскоеНазваниеТаблицы", testTypesListSecond));
        Assert.assertNull(provider.createTable("русскоеНазваниеТаблицы", testTypesListSecond));

        provider.removeTable("testTable1");
        provider.removeTable("русскоеНазваниеТаблицы");

    }

    @Test(expected = IllegalArgumentException.class)
    public void createTableWithNullName() throws IOException {
        testTypesListFirst = FileMapUtils.createListOfTypesFromString("int int int");
        provider.createTable(null, testTypesListFirst);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNullSignature() throws IOException {
        provider.createTable("testTable1", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithBothNull() throws IOException {
        provider.createTable(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createEmptyNameTable() throws IOException {
        provider.createTable("", testTypesListSecond);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createEmpySpaceNameTable() throws IOException {
        provider.createTable("          ", testTypesListSecond);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTableWithIncorrectName() throws IOException {
        provider.createTable("୧༼ಠ益ಠ༽୨@NOW@WE@RIOT@୧༼ಠ益ಠ༽୨", testTypesListFirst);
    }


    @Test
    public void getTableTest() throws IOException {
        Assert.assertNull(provider.getTable("nonExistentTable"));
        Assert.assertNull(provider.getTable("НесуществующееРусскоеНазвание"));
        testTypesListFirst = FileMapUtils.createListOfTypesFromString("int int int");
        provider.createTable("testTable", testTypesListFirst);
        testTypesListSecond = FileMapUtils.createListOfTypesFromString("int double boolean String");
        provider.createTable("РусскоеНазвание", testTypesListSecond);
        provider.removeTable("testTable");
        provider.removeTable("РусскоеНазвание");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullTable() {
        provider.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getEmptyNameTable() {
        provider.getTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getEmptySpaceTable() {
        provider.getTable("         ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getIncorrectTableName() {
        provider.getTable("୧༼ಠ益ಠ༽୨ NOW WE RIOT ୧༼ಠ益ಠ༽୨");
    }

    @Test
    public void removeTableTest() throws IOException {
        testTypesListFirst = FileMapUtils.createListOfTypesFromString("int int int");
        testTypesListSecond = FileMapUtils.createListOfTypesFromString("int double boolean String");

        provider.createTable("testTable7", testTypesListFirst);
        provider.createTable("тестоваяТаблица8", testTypesListSecond);

        provider.removeTable("testTable7");
        Assert.assertNull(provider.getTable("testTable7"));

        provider.removeTable("тестоваяТаблица8");
        Assert.assertNull(provider.getTable("тестоваяТаблица8"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullTableTest() {
        provider.removeTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeEmptyTableTest() {
        provider.removeTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNlTableTest() {
        provider.removeTable("    ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeBadNameTableTest() {
        provider.removeTable("not_normal-name@for$table^!");
    }

    @Test(expected = IllegalStateException.class)
    public void removeNonExcitingTable() {
        provider.removeTable("testNonExcitingTable11");
    }

    @Test(expected = ParseException.class)
    public void deserializeNullStringTest() throws IOException, ParseException {
        testTypesListFirst = FileMapUtils.createListOfTypesFromString("int int int");
        Table testTable = provider.createTable("testTable10", testTypesListFirst);
        provider.deserialize(testTable, null);
    }

    @Test(expected = ParseException.class)
    public void deserializeEmptyStringTest() throws IOException, ParseException {
        testTypesListSecond = FileMapUtils.createListOfTypesFromString("int double boolean String");
        Table testTable = provider.createTable("testTable11", testTypesListSecond);
        provider.deserialize(testTable, "");
    }

    @Test
    public void deserializeStringJSON1Test() throws IOException, ParseException {
        testTypesListFirst = FileMapUtils.createListOfTypesFromString("int int int");
        Table testTable = provider.createTable("testTable12", testTypesListFirst);
        List<Object> valuesToMakeStoreable = new ArrayList<>();
        valuesToMakeStoreable.add(1);
        valuesToMakeStoreable.add(2);
        valuesToMakeStoreable.add(3);
        Storeable testStoreable = provider.createFor(testTable, valuesToMakeStoreable);
        Assert.assertTrue(CountingTools.equals(testTable,
                provider.deserialize(testTable, "[1, 2, 3]"), testStoreable));
    }

    @Test
    public void deserializeStringWithNullFieldJSON2Test() throws IOException, ParseException {
        testTypesListSecond = FileMapUtils.createListOfTypesFromString("int double boolean String");
        Table testTable = provider.createTable("testTable13", testTypesListSecond);
        List<Object> valuesToMakeStoreable = new ArrayList<>();
        valuesToMakeStoreable.add(1);
        valuesToMakeStoreable.add(2.0432);
        valuesToMakeStoreable.add(false);
        valuesToMakeStoreable.add(null);
        Storeable testStoreable = provider.createFor(testTable, valuesToMakeStoreable);
        Assert.assertTrue(CountingTools.equals(testTable, provider.
                deserialize(testTable, "[1,2.0432,false,null]"), testStoreable));
    }

    @Test(expected = ColumnFormatException.class)
    public void serializeNullStorableTest() throws IOException {
        testTypesListFirst = FileMapUtils.createListOfTypesFromString("int int int");
        Table testTable = provider.createTable("testTable14", testTypesListFirst);
        provider.serialize(testTable, null);
    }

    @Test
    public void serializeEmptyStorableTest() throws IOException {
        testTypesListSecond = FileMapUtils.
                createListOfTypesFromString("int double boolean String");
        Table testTable = provider.createTable("testTable15", testTypesListSecond);
        Storeable testStorable = provider.createFor(testTable);
        Assert.assertTrue((provider.serialize(testTable, testStorable)).
                equals("[null,null,null,null]"));
    }

    @Test
    public void serializeMixedStorableTest() throws IOException {
        testTypesListFirst = FileMapUtils.createListOfTypesFromString("int int int");
        Table testTable = provider.createTable("testTable16", testTypesListFirst);
        List<Object> valuesToMakeStoreable = new ArrayList<>();
        valuesToMakeStoreable.add(1);
        valuesToMakeStoreable.add(null);
        valuesToMakeStoreable.add(3);
        Storeable testStorable = provider.createFor(testTable, valuesToMakeStoreable);
        Assert.assertTrue((provider.serialize(testTable, testStorable)).equals("[1,null,3]"));
    }

    @Test
    public void createForWithOneParameterTest() throws IOException {
        testTypesListFirst = FileMapUtils.createListOfTypesFromString("int int int");
        Table testTable = provider.createTable("testTable17", testTypesListFirst);
        Storeable testStoreable;
        Assert.assertNotNull(testStoreable = provider.createFor(testTable));
        Assert.assertNull(testStoreable.getColumnAt(1));
    }

    @Test
    public void createForWithTwoParametersTest() throws IOException {
        testTypesListFirst = FileMapUtils.createListOfTypesFromString("int int int");
        Table testTable = provider.createTable("testTable18", testTypesListFirst);
        List<Object> valuesToMakeStoreable = new ArrayList<>();
        valuesToMakeStoreable.add(1);
        valuesToMakeStoreable.add(null);
        valuesToMakeStoreable.add(3);
        Storeable testStoreable;
        Assert.assertNotNull(testStoreable = provider.createFor(testTable, valuesToMakeStoreable));
        List<Class<?>> columnTypes = new ArrayList<>();
        for (int i = 0; i < testTable.getColumnsCount(); i++) {
            columnTypes.add(testTable.getColumnType(i));
        }
        Assert.assertTrue(CorrectnessCheck.correctStoreable(testStoreable, columnTypes));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void createForWithTwoParametersBadObjectsListTest() throws IOException {
        testTypesListFirst = FileMapUtils.createListOfTypesFromString("int int int");
        Table testTable = provider.createTable("testTable19", testTypesListFirst);
        List<Object> valuesToMakeStoreable = new ArrayList<>();
        valuesToMakeStoreable.add(1);
        valuesToMakeStoreable.add(null);
        provider.createFor(testTable, valuesToMakeStoreable);
    }

    @Test(expected = ColumnFormatException.class)
    public void createForWithTwoParametersBadColumnTypesTest() throws IOException {
        testTypesListFirst = FileMapUtils.createListOfTypesFromString("int int int");
        Table testTable = provider.createTable("testTable19", testTypesListFirst);
        List<Object> valuesToMakeStoreable = new ArrayList<>();
        valuesToMakeStoreable.add(1);
        valuesToMakeStoreable.add(null);
        valuesToMakeStoreable.add(2.043);
        provider.createFor(testTable, valuesToMakeStoreable);
    }

    @Test(expected = IllegalStateException.class)
    public void toStringAfterCloseTest() {
        provider.close();
        provider.toString();
    }

    @Test(expected = IllegalStateException.class)
    public void getNonExistingTableAfterCloseTest() {
        provider.close();
        provider.getTable("notExistingTable");
    }

    @Test
    public void openTableUsingGetAfterClosedTable() throws IOException {
        testTypesListFirst = FileMapUtils.createListOfTypesFromString("int int int");
        StorableTable testTable = (StorableTable) provider.createTable("testTable1", testTypesListFirst);
        testTable.close();
        StorableTable testTableNewReference;
        Assert.assertNotNull(testTableNewReference = (StorableTable) provider.getTable("testTable1"));
        Assert.assertEquals("testTable1", testTableNewReference.getName());
        Assert.assertNotEquals(testTable, testTableNewReference);
    }

    @Test(expected = IllegalStateException.class)
    public void createTableAfterCloseTest() throws IOException {
        provider.close();
        testTypesListFirst = FileMapUtils.createListOfTypesFromString("int int int");
        provider.createTable("newExistingTable", testTypesListFirst);
    }

    @Test
    public void openTableUsingCreateAfterCloseTest() throws IOException {
        testTypesListFirst = FileMapUtils.createListOfTypesFromString("int int int");
        StorableTable testTable = (StorableTable) provider.createTable("testTable1", testTypesListFirst);
        testTable.close();
        StorableTable testTableNewReference;
        Assert.assertNotNull(testTableNewReference =
                (StorableTable) provider.createTable("testTable1", testTypesListFirst));
        Assert.assertEquals("testTable1", testTableNewReference.getName());
        Assert.assertNotEquals(testTable, testTableNewReference);
    }

    @Test(expected = IllegalStateException.class)
    public void removeTableAfterCloseTest() throws IOException {
        provider.close();
        provider.removeTable("testTable1");
    }

    @Test(expected = IllegalStateException.class)
    public void deserializeTableAfterCloseTest() throws ParseException, IOException {
        provider.close();
        testTypesListFirst = FileMapUtils.createListOfTypesFromString("int int int");
        Table testTable = provider.createTable("testTable12", testTypesListFirst);
        List<Object> valuesToMakeStoreable = new ArrayList<>();
        valuesToMakeStoreable.add(1);
        valuesToMakeStoreable.add(2);
        valuesToMakeStoreable.add(3);
        Storeable testStoreable = provider.createFor(testTable, valuesToMakeStoreable);
        Assert.assertTrue(CountingTools.equals(testTable,
                provider.deserialize(testTable, "[1, 2, 3]"), testStoreable));
    }


    @Test(expected = IllegalStateException.class)
    public void serializeTableAfterCloseTest() throws ParseException, IOException {
        provider.close();
        testTypesListFirst = FileMapUtils.createListOfTypesFromString("int int int");
        Table testTable = provider.createTable("testTable16", testTypesListFirst);
        List<Object> valuesToMakeStoreable = new ArrayList<>();
        valuesToMakeStoreable.add(1);
        valuesToMakeStoreable.add(null);
        valuesToMakeStoreable.add(3);
        Storeable testStorable = provider.createFor(testTable, valuesToMakeStoreable);
        Assert.assertTrue((provider.serialize(testTable, testStorable)).equals("[1,null,3]"));
    }

    @Test(expected = IllegalStateException.class)
    public void createForTableAfterCloseTest1() throws ParseException, IOException {
        provider.close();
        testTypesListFirst = FileMapUtils.createListOfTypesFromString("int int int");
        Table testTable = provider.createTable("testTable17", testTypesListFirst);
        Storeable testStoreable;
        Assert.assertNotNull(testStoreable = provider.createFor(testTable));
        Assert.assertNull(testStoreable.getColumnAt(1));
    }

    @Test(expected = IllegalStateException.class)
    public void createForTableAfterCloseTest2() throws IOException {
        provider.close();
        testTypesListFirst = FileMapUtils.createListOfTypesFromString("int int int");
        Table testTable = provider.createTable("testTable18", testTypesListFirst);
        List<Object> valuesToMakeStoreable = new ArrayList<>();
        valuesToMakeStoreable.add(1);
        valuesToMakeStoreable.add(null);
        valuesToMakeStoreable.add(3);
        Storeable testStoreable;
        Assert.assertNotNull(testStoreable = provider.createFor(testTable, valuesToMakeStoreable));
        List<Class<?>> columnTypes = new ArrayList<>();
        for (int i = 0; i < testTable.getColumnsCount(); i++) {
            columnTypes.add(testTable.getColumnType(i));
        }
        Assert.assertTrue(CorrectnessCheck.correctStoreable(testStoreable, columnTypes));

    }
}
