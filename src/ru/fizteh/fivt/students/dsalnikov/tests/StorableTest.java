package ru.fizteh.fivt.students.dsalnikov.tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dsalnikov.storable.StorableTableProvider;
import ru.fizteh.fivt.students.dsalnikov.utils.FileMapUtils;
import ru.fizteh.fivt.students.dsalnikov.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class StorableTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    TableProvider tableProvider;
    Table table;
    Storeable storeable;
    File directory;

    @Before
    public void setUpTestObject() throws IOException, ParseException {
        directory = folder.newFolder();

        tableProvider = new StorableTableProvider(directory);
        List<Class<?>> typesLine = FileMapUtils.createListOfTypesFromString("int long byte float double boolean String");
        table = tableProvider.createTable("testTable", typesLine);
        storeable = tableProvider.deserialize(table, "[15, 111111555555, 0, 3.1415, 1.1005001314, true, null]");
    }

    @After
    public void tearDownTestObject() throws IOException {
        tableProvider.removeTable("testTable");
        FileUtils.forceRemoveDirectory(directory);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void outOfBoundsIndexTest() {
        storeable.setColumnAt(10, "incorrect index");
    }

    @Test(expected = ColumnFormatException.class)
    public void wrongTypeSetTest() {
        storeable.setColumnAt(0, 2.313131);
    }

    @Test
    public void correctSetTest() {
        storeable.setColumnAt(0, 2);
        Assert.assertEquals(new Integer(2), storeable.getIntAt(0));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void outOfBoundsIntGetTest() {
        storeable.getIntAt(100);
    }

    @Test(expected = ColumnFormatException.class)
    public void getIntAtWrongType() {
        storeable.getIntAt(3);
    }

    @Test
    public void correctGetInt() {
        Assert.assertEquals(new Integer(15), storeable.getIntAt(0));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void outOfBoundsLongGetTest() {
        storeable.getLongAt(100);
    }

    @Test(expected = ColumnFormatException.class)
    public void getLongAtWrongType() {
        storeable.getLongAt(0);
    }

    @Test
    public void correctGetLong() {
        Assert.assertEquals(new Long("111111555555"), storeable.getLongAt(1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void outOfBoundsByteGetTest() {
        storeable.getByteAt(100);
    }

    @Test(expected = ColumnFormatException.class)
    public void getByteAtWrongTypeTest() {
        storeable.getByteAt(0);
    }

    @Test
    public void correctgetByteTest() {
        Assert.assertEquals(new Byte("0"), storeable.getByteAt(2));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void outOfBoundsFloatTest() {
        storeable.getFloatAt(100);
    }

    @Test(expected = ColumnFormatException.class)
    public void getFloatAtWrongTypeTest() {
        storeable.getFloatAt(0);
    }

    @Test
    public void correctFloatTest() {
        Assert.assertEquals(new Float(3.1415), storeable.getFloatAt(3));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void outOfBoundsDoubleTest() {
        storeable.getDoubleAt(100);
    }

    @Test(expected = ColumnFormatException.class)
    public void getDoubleAtWrongTypeTest() {
        storeable.getDoubleAt(0);
    }

    @Test
    public void correctDoubleTest() {
        Assert.assertEquals(new Double(1.1005001314), storeable.getDoubleAt(4));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void outofBoundsBooleanTest() {
        storeable.getDoubleAt(100);
    }

    @Test(expected = ColumnFormatException.class)
    public void getBooleanAtWrongTypeTest() {
        storeable.getBooleanAt(0);
    }

    @Test
    public void correctBooleanTest() {
        Assert.assertEquals(true, storeable.getBooleanAt(5));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void outOfBoundsStringTest() {
        storeable.getStringAt(100);
    }

    @Test(expected = ColumnFormatException.class)
    public void getStringAtWrongTypeTest() {
        storeable.getStringAt(0);
    }

    @Test
    public void correctStringGet() {
        Assert.assertEquals(null, storeable.getStringAt(6));
    }
}
