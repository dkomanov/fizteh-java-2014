package ru.fizteh.fivt.students.MaximGotovchits.JUnit.Tests;

import org.junit.Test;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.MaximGotovchits.JUnit.ObjectTableProvider;

import static org.junit.Assert.assertEquals;

public class SizeTest {
    public int randomNum() {
        int min = 0;
        int max = 100;
        return min + (int) (Math.random() * ((max - min) + 1));
    }
    @Test
    public void sizeTest() {
        String tableName = "table1";
        Table table = new ObjectTableProvider().createTable(tableName);
        int size = randomNum();
        int result = table.size();
        for (Integer ind = 0; ind < size; ++ind) {
            String k = "K" + ind.toString();
            String v = "V" + ind.toString();
            if (table.put(k, v) == null) {
                ++result;
            }
        }
        table.commit();
        assertEquals(table.size(), result);
        new ObjectTableProvider().removeTable(tableName);
    }
}
