package ru.fizteh.fivt.students.kinanAlsarmini.storable.tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.kinanAlsarmini.storable.database.DatabaseRow;
import ru.fizteh.fivt.students.kinanAlsarmini.storable.database.DatabaseTableProviderFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseTableTests {
    private static final String DATABASE = "C:\\temp\\storeable_test";
    TableProvider provider;
    Table currentTable;

    @Before
    public void beforeTest() {
        DatabaseTableProviderFactory factory = new DatabaseTableProviderFactory();
        try {
            provider = factory.create(DATABASE);
        } catch (IOException e) {

        }

        List<Class<?>> columnTypes = new ArrayList<>();

        columnTypes.add(Integer.class);
        columnTypes.add(String.class);

        try {
            currentTable = provider.createTable("testTable", columnTypes);
        } catch (IOException e) {
            // SAD
        }
    }

    @After
    public void afterTest() {
        try {
            provider.removeTable("testTable");
        } catch (IOException e) {
            // SAD
        }
    }

    @Test
    public void putRemoveShouldNotFail() throws Exception {
        currentTable.commit();
        currentTable.put("key1", provider.deserialize(currentTable, getXml(1, "2")));
        currentTable.remove("key1");

        Assert.assertEquals(0, currentTable.commit());
    }

    @Test(expected = ParseException.class)
    public void putEmptyValueTest() throws ParseException {
        Storeable storeable = provider.deserialize(currentTable, getXml(1, ""));
    }

    @Test(expected = ParseException.class)
    public void putNlValueTest() throws ParseException {
        Storeable storeable = provider.deserialize(currentTable, getXml(1, "    "));
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNlKeyShouldFail() {
        currentTable.put("  ", provider.createFor(currentTable));
    }


    @Test(expected = IllegalArgumentException.class)
    public void testPutValueWithWhiteSpaces()
    {
        Storeable newValue = provider.createFor(currentTable);
        DatabaseRow row = (DatabaseRow) newValue;

        List<Object> values = new ArrayList<Object>() {{
            add(1);
            add("    ");
        }};

        row.setColumns(values);
        currentTable.put("keysasdasda", row);
    }

    private String getXml(int value1, String value2) {
        return String.format("<row><col>%d</col><col>%s</col></row>", value1, value2);
    }
}
