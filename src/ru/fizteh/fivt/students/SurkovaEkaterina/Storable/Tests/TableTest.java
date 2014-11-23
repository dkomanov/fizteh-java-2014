package ru.fizteh.fivt.students.SurkovaEkaterina.Storable.Tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.SurkovaEkaterina.Storable.TableSystem.DatabaseTableProviderFactory;
import ru.fizteh.fivt.students.SurkovaEkaterina.Storable.TableSystem.DatabaseTableRow;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class TableTest {
    private static final String STORAGE = "\\temp\\storeable_table_test";
    TableProvider provider;
    Table currentTable;

    @Before
    public void setUp() {
        DatabaseTableProviderFactory factory = new DatabaseTableProviderFactory();
        try {
            provider = factory.create(STORAGE);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        columnTypes.add(Integer.class);
        columnTypes.add(String.class);

        try {
            currentTable = provider.createTable("testTable", columnTypes);
        } catch (IOException e) {
            System.out.println("Unexpected error! Something has gone wrong!");
        }
    }

    @After
    public void cleanUp() {
        try {
            provider.removeTable("testTable");
        } catch (IOException e) {
            System.out.println("Unexpected error! Something has gone wrong!");
        }
    }

    @Test
    public void putRemoveShouldNotFail() throws Exception {
        currentTable.commit();
        currentTable.put("key", provider.deserialize(currentTable, getXml(1, "2")));
        currentTable.remove("key");
        Assert.assertEquals(0, currentTable.commit());
    }

    @Test(expected = ParseException.class)
    public void putEmptyValueTest() throws ParseException {
        Storeable storeable = provider.deserialize(currentTable, getXml(1, ""));
    }

    @Test(expected = ParseException.class)
    public void putNlValueTest() throws ParseException {
        Storeable storeable = provider.deserialize(currentTable, getXml(1, "   "));
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNlKeyShouldFail() {
        currentTable.put("  ", provider.createFor(currentTable));
    }


    @Test(expected = IllegalArgumentException.class)
    public void testPutValueWithWhiteSpaces() {
        Storeable newValue = provider.createFor(currentTable);
        DatabaseTableRow row = (DatabaseTableRow) newValue;
        List<Object> values = new ArrayList<Object>() { {
            add(1);
            add("     ");
        } };
        row.setColumns(values);
        currentTable.put("akey", row);
    }

    private String getXml(int value1, String value2) {
        return String.format("<row><col>%d</col><col>%s</col></row>", value1, value2);
    }
}
