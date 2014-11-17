package ru.fizteh.fivt.students.MaximGotovchits.JUnit.Tests;

import org.junit.Test;
import ru.fizteh.fivt.students.MaximGotovchits.JUnit.ObjectTableProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TableProviderFactoryTest {
    @Test
    public void createTest() {
        String dbName = "db1";
        if (dbName.length() > 200 || dbName == null) {
            assertEquals(new ObjectTableProvider(dbName), null);
        } else {
            assertNotNull(new ObjectTableProvider(dbName));
        }
        ObjectTableProvider tp = new ObjectTableProvider(dbName);
    }
}
