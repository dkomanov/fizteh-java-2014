package ru.fizteh.fivt.students.MaximGotovchits.JUnit.Tests;

import org.junit.Test;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.MaximGotovchits.JUnit.ObjectTableProvider;

import static org.junit.Assert.assertEquals;

public class TestTableProvider {
    @Test
    public void createTableTest() {
        String randomTableName = "table";
        Table table = new ObjectTableProvider().createTable(randomTableName);
        if (table != null) {
            assertEquals(new ObjectTableProvider().getTable(randomTableName), table);
        } else {
            assertEquals(table, null);
        }
        new ObjectTableProvider().removeTable(table.getName());
    }
    @Test
    public void getTableTest() {
        String tableName1 = "table1";
        String tableName2 = "table2";
        Table table1 = new ObjectTableProvider().createTable(tableName1);
        assertEquals(table1, new ObjectTableProvider().getTable(tableName1));
        Table table2 = new ObjectTableProvider().createTable(tableName1);
        new ObjectTableProvider().removeTable(tableName2);
        assertEquals(null, new ObjectTableProvider().getTable(tableName2));
        new ObjectTableProvider().removeTable(tableName1);
    }
    @Test
    public void removeTableTest() {
        String tableName = "table3";
        Table table = new ObjectTableProvider().createTable(tableName);
        new ObjectTableProvider().removeTable(tableName);
        assertEquals(null, new ObjectTableProvider().getTable(tableName));
    }
}
