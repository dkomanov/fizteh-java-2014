package ru.fizteh.fivt.students.ekaterina_pogodina.JUnit;

import org.junit.Test;
import junit.framework.Assert;
import ru.fizteh.fivt.students.ekaterina_pogodina.basicclasses.Table;
import ru.fizteh.fivt.students.ekaterina_pogodina.basicclasses.TableProvider;
import ru.fizteh.fivt.students.ekaterina_pogodina.basicclasses.TableProviderFactory;

public class TableTest {

    @Test
    public void nameEquals() throws Exception {
        TableProviderFactory tableProviderFactory = new DataBaseTableProviderFactory();
        TableProvider tProvider = tableProviderFactory.create("test_folder");
        Table table = tProvider.createTable("kate_table");
        Assert.assertEquals("kate_table", table.getName());
    }

}
