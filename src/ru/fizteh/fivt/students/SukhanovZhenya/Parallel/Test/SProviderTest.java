package ru.fizteh.fivt.students.SukhanovZhenya.Parallel.Test;

import org.junit.Assert;
import org.junit.Test;
import ru.fizteh.fivt.students.SukhanovZhenya.Parallel.SProvider;
import ru.fizteh.fivt.students.SukhanovZhenya.Parallel.SProviderFactory;
import ru.fizteh.fivt.students.SukhanovZhenya.Parallel.STable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SProviderTest {
    SProvider sProvider = null;
    SProviderFactory sProviderFactory = new SProviderFactory();

    @Test
    public void testCreateTable() throws Exception {
        List<Class<?>> classList = new ArrayList<Class<?>>();
        classList.add(Integer.class);
        sProvider = sProviderFactory.create("testDir");
        File tmp = new File("testDir");
        STable sTable = sProvider.createTable("testTable", classList);
        Assert.assertNotNull(sTable);

        sTable.remove();
        tmp.delete();
    }

    @Test(expected = NullPointerException.class)
    public void testGetTable() throws Exception {
        sProvider.getTable(null);
    }


}
