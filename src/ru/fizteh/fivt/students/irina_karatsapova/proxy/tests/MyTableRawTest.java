package ru.fizteh.fivt.students.irina_karatsapova.proxy.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.interfaces.Storeable;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.interfaces.Table;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.interfaces.TableProvider;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.interfaces.TableProviderFactory;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.table_provider_factory.MyTableProviderFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class MyTableRawTest {
    TemporaryFolder tempFolder = new TemporaryFolder();
    String tableName = "table";
    String anotherTableName = "another table name";
    TableProvider provider;
    Table table;
    List<Class<?>> types = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        tempFolder.create();
        File providerDir = tempFolder.newFolder();
        TableProviderFactory factory = new MyTableProviderFactory();
        provider = factory.create(providerDir.toString());
        Class[] classes = {Integer.class, String.class, Boolean.class};
        for (Class type: classes) {
            types.add(type);
        }
        table = provider.createTable(tableName, types);
    }

    @After
    public void tearDown() throws Exception {
        tempFolder.delete();
    }

    @Test
    public void testToString() throws Exception {
        Object[] valuesArray = {5, "hello", false};
        List<Object> values = new ArrayList<>();
        for (Object value: valuesArray) {
            values.add(value);
        }
        Storeable tableRaw = provider.createFor(table, values);
        assertEquals("MyTableRaw[5,hello,false]", tableRaw.toString());
    }

    @Test
    public void testToStringWithNull() throws Exception {
        Object[] valuesArray = {5, null, false};
        List<Object> values = new ArrayList<>();
        for (Object value: valuesArray) {
            values.add(value);
        }
        Storeable tableRaw = provider.createFor(table, values);
        assertEquals("MyTableRaw[5,,false]", tableRaw.toString());
    }
}
