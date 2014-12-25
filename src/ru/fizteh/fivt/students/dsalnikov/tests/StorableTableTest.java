package ru.fizteh.fivt.students.dsalnikov.tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.dsalnikov.storable.StorableTable;
import ru.fizteh.fivt.students.dsalnikov.storable.StorableTableProvider;
import ru.fizteh.fivt.students.dsalnikov.utils.FileMapUtils;
import ru.fizteh.fivt.students.dsalnikov.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class StorableTableTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    StorableTableProvider provider;
    Table engTestTable;
    Table rusTestTable;
    List<Class<?>> engTypesList;
    List<Class<?>> rusTypesList;
    File directory;

    @Before
    public void setUpTestObject() throws IOException {
        directory = folder.newFolder();
        provider = new StorableTableProvider(directory);
        engTypesList = FileMapUtils.createListOfTypesFromString("int int int");
        rusTypesList = FileMapUtils.createListOfTypesFromString("int double boolean String");

        engTestTable = provider.createTable("testTable", engTypesList);
        rusTestTable = provider.createTable("русскоеНазвание", rusTypesList);
    }

    @After
    public void tearDownTestObject() throws IOException {
        FileUtils.forceRemoveDirectory(directory);
    }


    @Test
    public void getNameTest() {
        Assert.assertEquals("testTable", engTestTable.getName());
        Assert.assertEquals("русскоеНазвание", rusTestTable.getName());
    }

    @Test
    public void getTest() throws ParseException {
        engTestTable.put("key", provider.deserialize(engTestTable, "[1, 2, 3]"));
        Assert.assertEquals("[1,2,3]", provider.serialize(engTestTable, engTestTable.get("key")));

        Assert.assertNull(engTestTable.get("nonExistentKey"));
        engTestTable.remove("key");

        rusTestTable.put("ключ", provider.deserialize(rusTestTable, "[1, 2.043, true, \"Hello World!\"]"));
        Assert.assertEquals("[1,2.043,true,\"Hello World!\"]",
                provider.serialize(rusTestTable, rusTestTable.get("ключ")));
        Assert.assertNull(rusTestTable.get("несуществующийКлюч"));
        rusTestTable.remove("ключ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullTest() {
        engTestTable.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getEmptyTest() {
        engTestTable.get("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getEmptySpaceTest() {
        engTestTable.get("    ");
    }

    @Test
    public void putTest() throws IOException, ParseException {
        Assert.assertNull(engTestTable.put("key", provider.deserialize(engTestTable, "[1, 2, 3]")));
        engTestTable.commit();

        ((StorableTable) engTestTable).close();
        engTestTable = provider.createTable("testTable", engTypesList);

        Assert.assertEquals("[1,2,3]", provider.serialize(engTestTable, engTestTable.put("key",
                provider.deserialize(engTestTable, "[1, 2, 3]"))));
        Assert.assertEquals("[1,2,3]", provider.serialize(engTestTable, engTestTable.put("key",
                provider.deserialize(engTestTable, "[-1, -2, -3]"))));
        engTestTable.remove("key");

        Assert.assertNull(rusTestTable.put("ключ",
                provider.deserialize(rusTestTable, "[1, 2.043, true, \"Hello World!\"]")));
        Assert.assertEquals("[1,2.043,true,\"Hello World!\"]",
                provider.serialize(rusTestTable, rusTestTable.put("ключ",
                        provider.deserialize(rusTestTable, "[1, 2.043, true, \"Hello World!\"]"))));
        Assert.assertEquals("[1,2.043,true,\"Hello World!\"]",
                provider.serialize(rusTestTable, rusTestTable.put("ключ",
                        provider.deserialize(rusTestTable, "[-1, -2.043, false, \"Bye Bye World!\"]"))));
        rusTestTable.remove("ключ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNullKeyTest() throws IOException, ParseException {
        engTestTable.put(null, engTestTable.put("key", provider.deserialize(engTestTable, "[1, 2, 3]")));
    }

    @Test
    public void putNullValueTest() {
        Assert.assertNull(engTestTable.put("key", null));

    }

    @Test(expected = IllegalArgumentException.class)
    public void putNullBothTest() {
        engTestTable.put(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putKeyWithWhitespaces1Test() {
        List<Object> valuesToPut = new ArrayList<>();
        valuesToPut.add(1);
        valuesToPut.add(2);
        valuesToPut.add(3);
        Storeable needToPut = provider.createFor(engTestTable, valuesToPut);
        engTestTable.put("key with whitespaces", needToPut);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putKeyWithWhitespaces2Test() {
        List<Object> valuesToPut = new ArrayList<>();
        valuesToPut.add(1);
        valuesToPut.add(2);
        valuesToPut.add(3);
        Storeable needToPut = provider.createFor(engTestTable, valuesToPut);
        engTestTable.put(" key ", needToPut);
    }

    @Test
    public void removeTest() throws IOException, ParseException {
        engTestTable.put("key", provider.deserialize(engTestTable, "[1, 2, 3]"));
        Assert.assertNull(engTestTable.remove("nonExistentKey"));
        Assert.assertEquals("[1,2,3]", provider.serialize(engTestTable, engTestTable.remove("key")));

        rusTestTable.put("ключ", provider.deserialize(rusTestTable, "[1, 2.043, true, \"Hello World!\"]"));
        Assert.assertNull(rusTestTable.remove("несуществующийКлюч"));
        Assert.assertEquals("[1,2.043,true,\"Hello World!\"]",
                provider.serialize(rusTestTable, rusTestTable.remove("ключ")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullTest() {
        engTestTable.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeEmptyTest() {
        engTestTable.remove("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNlTest() {
        engTestTable.remove("    ");
    }

    @Test
    public void sizeTest() throws IOException, ParseException {
        Assert.assertEquals(0, engTestTable.size());
        engTestTable.put("key1", provider.deserialize(engTestTable, "[1, 2, 3]"));
        Assert.assertEquals(1, engTestTable.size());
        engTestTable.put("key2", provider.deserialize(engTestTable, "[4, 5, 6]"));
        engTestTable.put("key3", provider.deserialize(engTestTable, "[7, 8, 9]"));
        Assert.assertEquals(3, engTestTable.size());
        engTestTable.put("key4", provider.deserialize(engTestTable, "[10, 11, 12]"));
        engTestTable.put("key5", provider.deserialize(engTestTable, "[13, 14, 15]"));
        Assert.assertEquals(5, engTestTable.size());
        engTestTable.commit();
        Assert.assertEquals(5, engTestTable.size());

        for (int i = 1; i <= 5; ++i) {
            engTestTable.remove("key" + i);
        }
    }

    @Test
    public void commitTest() throws IOException, ParseException {
        Assert.assertEquals(0, engTestTable.commit());
        for (int i = 1; i <= 5; ++i) {
            engTestTable.put("key" + i, provider.deserialize(engTestTable, "[1, 2, 3" + i + "]"));
        }
        Assert.assertEquals(5, engTestTable.commit());
        for (int i = 1; i <= 5; ++i) {
            engTestTable.remove("key" + i);
        }
    }

    @Test
    public void commitHardTest() throws IOException, ParseException {
        engTestTable.put("key1", provider.deserialize(engTestTable, "[1, 2, 3]"));
        engTestTable.put("key2", provider.deserialize(engTestTable, "[4, 5, 6]"));
        Assert.assertEquals(2, engTestTable.commit());
        engTestTable.put("key1", provider.deserialize(engTestTable, "[7, 8, 9]"));
        engTestTable.put("key3", provider.deserialize(engTestTable, "[10, 11, 12]"));
        engTestTable.remove("key2");
        Assert.assertEquals(3, engTestTable.commit());
        engTestTable.remove("key1");
        engTestTable.remove("key3");
    }

    @Test
    public void rollbackTest() throws IOException, ParseException {
        Assert.assertEquals(0, engTestTable.rollback());
        for (int i = 1; i <= 5; ++i) {
            engTestTable.put("key" + i, provider.deserialize(engTestTable, "[1, 2, 3" + i + "]"));
        }
        engTestTable.commit();
        engTestTable.put("key2", provider.deserialize(engTestTable, "[1, 2, 0]"));
        engTestTable.put("key4", provider.deserialize(engTestTable, "[1, 2, 1]"));
        Assert.assertEquals(2, engTestTable.rollback());
        Assert.assertEquals("[1,2,32]", provider.serialize(engTestTable, engTestTable.get("key2")));
        Assert.assertEquals("[1,2,34]", provider.serialize(engTestTable, engTestTable.get("key4")));
        for (int i = 1; i <= 5; ++i) {
            engTestTable.remove("key" + i);
        }
    }

    @Test
    public void rollbackHardTest() throws IOException, ParseException {
        engTestTable.put("key1", provider.deserialize(engTestTable, "[1, 2, 3]"));
        engTestTable.put("key2", provider.deserialize(engTestTable, "[4, 5, 6]"));
        Assert.assertEquals(2, engTestTable.commit());
        engTestTable.remove("key2");
        engTestTable.put("key2", provider.deserialize(engTestTable, "[4, 5, 6]"));
        Assert.assertEquals(0, engTestTable.rollback());
        engTestTable.remove("key1");
        engTestTable.remove("key2");
    }

    @Test
    public void getColumnsCountTest() {
        Assert.assertEquals(3, engTestTable.getColumnsCount());
        Assert.assertEquals(4, rusTestTable.getColumnsCount());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getColumnsTypeBadIndex1Test() {
        engTestTable.getColumnType(4);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getColumnsTypeBadIndex2Test() {
        rusTestTable.getColumnType(5);
    }

    @Test
    public void getColumnsType1Test() {
        Assert.assertEquals(Integer.class, engTestTable.getColumnType(0));
    }

    @Test
    public void getColumnsType2Test() {
        Assert.assertEquals(Boolean.class, rusTestTable.getColumnType(2));
    }

    @Test
    public void doubleCloseTest() throws IOException {
        ((StorableTable) engTestTable).close();
        ((StorableTable) engTestTable).close();
        engTestTable = provider.createTable("testTable20", engTypesList);
        Assert.assertNotNull(engTestTable);
    }

    @Test(expected = IllegalStateException.class)
    public void getAfterCloseTest() {
        ((StorableTable) engTestTable).close();
        engTestTable.get("key");
    }

    @Test(expected = IllegalStateException.class)
    public void putAfterCloseTest() throws ParseException {
        ((StorableTable) engTestTable).close();
        engTestTable.put("key", provider.deserialize(engTestTable, "[1, 2, 3]"));
    }

    @Test(expected = IllegalStateException.class)
    public void removeAfterCloseTest() {
        ((StorableTable) engTestTable).close();
        engTestTable.remove("key");
    }

    @Test(expected = IllegalStateException.class)
    public void getNameAfterCloseTest() {
        ((StorableTable) engTestTable).close();
        engTestTable.getName();
    }

    @Test(expected = IllegalStateException.class)
    public void rollbackAfterCloseTest() {
        ((StorableTable) engTestTable).close();
        engTestTable.rollback();
    }

    @Test(expected = IllegalStateException.class)
    public void commitAfterCloseTest() throws IOException {
        ((StorableTable) engTestTable).close();
        engTestTable.commit();
    }

    @Test(expected = IllegalStateException.class)
    public void getColumnTypeAfterCloseTest() {
        ((StorableTable) engTestTable).close();
        engTestTable.getColumnType(0);
    }

    @Test(expected = IllegalStateException.class)
    public void getColumnsCountAfterCloseTest() {
        ((StorableTable) engTestTable).close();
        engTestTable.getColumnsCount();
    }

    @Test(expected = IllegalStateException.class)
    public void toStringAfterCloseTest() {
        ((StorableTable) engTestTable).close();
        engTestTable.toString();
    }
}
