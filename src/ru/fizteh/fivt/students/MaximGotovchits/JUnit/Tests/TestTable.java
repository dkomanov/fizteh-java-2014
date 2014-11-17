package ru.fizteh.fivt.students.MaximGotovchits.JUnit.Tests;

import org.junit.Test;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.MaximGotovchits.JUnit.ObjectTable;
import ru.fizteh.fivt.students.MaximGotovchits.JUnit.ObjectTableProvider;

import static org.junit.Assert.assertEquals;

public class TestTable extends ObjectTable { // Эти тесты следует запускать отдельно от других.
// Дело в том, что некоторые результаты дургих тестов могут оказать плохое влияение на разультаты этих тестов.
    @Test
    public void getNameTest() {
        String randomTableName = "table1";
        Table table1 = new ObjectTableProvider().createTable(randomTableName);
        assertEquals(table1.getName(), randomTableName);
        new ObjectTableProvider().removeTable(table1.getName());
    }
    @Test
    public void getAndPutTest() {
        String randomTableName = "table2";
        Table table = new ObjectTableProvider().createTable(randomTableName);
        String k = "Kq";
        String v = "Vq";
        assertEquals(table.put(k, v), null);
        assertEquals(table.get(k), v);
        assertEquals(table.put(k, v), v);
        new ObjectTableProvider().removeTable(table.getName());
    }
    @Test
    public void removeTest() {
        String randomTableName = "table3";
        Table table = new ObjectTableProvider().createTable(randomTableName);
        String k = "K";
        String v = "V";
        table.put(k, v);
        assertEquals(table.remove(k), v);
        assertEquals(table.remove(k), null);
        new ObjectTableProvider().removeTable(randomTableName);
    }
    @Test
    public void commitTest() {
        int size = 20;
        String randomTableName = "table4";
        Table table = new ObjectTableProvider().createTable(randomTableName);
        for (Integer ind = 0; ind < size; ++ind) {
            String k = "Kk" + ind.toString();
            String v = "Vv" + ind.toString();
            table.put(k, v);
        }
        assertEquals(table.commit(), size + 1);
        new ObjectTableProvider().removeTable(randomTableName);
    }
    @Test
    public void rollbackTest() {
        String randomTableName = "table5";
        Table table = new ObjectTableProvider().createTable(randomTableName);
        int size = 20;
        for (Integer ind = 0; ind < size; ++ind) {
            String k = "Key" + ind.toString();
            String v = "Val" + ind.toString();
            table.put(k, v);
        }
        int result = size;
        assertEquals(table.rollback(), result);
        new ObjectTableProvider().removeTable(randomTableName);
    }
}
