package ru.fizteh.fivt.students.MaximGotovchits.JUnit.Tests;

import org.junit.Test;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.MaximGotovchits.JUnit.ObjectTableProvider;

import java.util.LinkedList;

import static org.junit.Assert.assertTrue;

public class ListTest {
    @Test
    public void listTest() {
        LinkedList<String> list = new LinkedList<String>();
        String tableName = "table1";
        Table table = new ObjectTableProvider().createTable(tableName);
        int size = 20;
        for (Integer ind = 0; ind < size; ++ind) {
            String k = "K" + ind.toString();
            String v = "V" + ind.toString();
            if (table.put(k, v) == null) {
                list.remove(k);
                list.push(k);
            }
        }
        table.commit();
        assertTrue(table.list().containsAll(list));
        new ObjectTableProvider().removeTable(tableName);
    }
}
