package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table;

import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TableRowTest {
    private TableRow row;
    private List<Object> values = new LinkedList<>();

    @Before
    public void setUp() throws Exception {
        values.add("string");
        values.add(true);
        values.add(19);
        values.add("+7-925-869-81-34");
        row = new TableRow(values);
    }

    @Test
    public void getCorrectness() {
        assertEquals("string", row.getStringAt(0));
        assertEquals(true, row.getBooleanAt(1));
        assertEquals(new Integer(19), row.getIntAt(2));
        assertEquals("+7-925-869-81-34", row.getStringAt(3));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void exceptionOnInvalidIndex() {
        row.getColumnAt(4);
    }

    @Test(expected = ColumnFormatException.class)
    public void exceptionOnGetWithInvalidType() {
        row.getIntAt(0);
    }

    @Test
    public void setAndGetCorrectness() {
        row.setColumnAt(0, null);
        assertEquals(null, row.getStringAt(0));
        row.setColumnAt(0, "hi");
        assertEquals("hi", row.getStringAt(0));
    }

}