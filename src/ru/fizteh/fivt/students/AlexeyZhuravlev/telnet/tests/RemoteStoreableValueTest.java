package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.AlexeyZhuravlev.proxy.AdvancedTableProviderFactory;
import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.MyRemoteTableProviderFactory;
import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.ServerLogic;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class RemoteStoreableValueTest {

    RemoteTableProviderFactory factory;
    private ServerLogic server;
    RemoteTableProvider provider;
    String address = "localhost";
    int portNumber = 3000;
    public Table table;
    Storeable storeable;
    List<Class<?>> types;

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();


    @Before
    public void before() throws Exception {
        factory = new MyRemoteTableProviderFactory();
        TableProviderFactory serverFactory = new AdvancedTableProviderFactory();
        TableProvider serverProvider = serverFactory.create(tmpFolder.newFolder().getAbsolutePath());
        server = new ServerLogic(serverProvider);
        server.start(portNumber);
        provider = factory.connect(address, portNumber);
        Class<?>[] arrayTypes = {Integer.class, Long.class, Float.class, Double.class,
                Boolean.class, String.class, Byte.class};
        types = Arrays.asList(arrayTypes);
        table = provider.createTable("table", types);
        storeable = provider.createFor(table);
    }

    @After
    public void after() throws Exception {
        server.stop();
    }

    @Test
    public void testSetAndGetColumns() {
        storeable.setColumnAt(0, 3);
        storeable.setColumnAt(1, 3L);
        storeable.setColumnAt(2, 3.2f);
        storeable.setColumnAt(3, 5.4);
        storeable.setColumnAt(4, true);
        storeable.setColumnAt(5, "hello");
        storeable.setColumnAt(6, (byte) 1);
        assertEquals(storeable.getIntAt(0), (Integer) 3);
        assertEquals(storeable.getLongAt(1), (Long) 3L);
        assertEquals(storeable.getFloatAt(2), (Float) 3.2f);
        assertEquals(storeable.getDoubleAt(3), (Double) 5.4);
        assertEquals(storeable.getBooleanAt(4), true);
        assertEquals(storeable.getStringAt(5), "hello");
        assertEquals(storeable.getByteAt(6), (Byte) (byte) 1);
        assertEquals(storeable.getColumnAt(4), true);
        storeable.setColumnAt(3, null);
        assertNull(storeable.getColumnAt(3));
        assertNull(storeable.getDoubleAt(3));
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void testGetOutOfBounds() {
        storeable.getColumnAt(7);
    }

    @Test (expected = ColumnFormatException.class)
    public void testGetIncorrectType() {
        storeable.getLongAt(0);
    }

    @Test
    public void testToString() {
        storeable.setColumnAt(0, 3);
        storeable.setColumnAt(1, 3L);
        storeable.setColumnAt(2, 3.2f);
        storeable.setColumnAt(3, 5.4);
        storeable.setColumnAt(4, true);
        storeable.setColumnAt(5, "hello");
        storeable.setColumnAt(6, (byte) 1);
        assertEquals(storeable.toString(), "RemoteStoreableValue[3,3,3.2,5.4,true,hello,1]");
        storeable.setColumnAt(2, null);
        storeable.setColumnAt(3, null);
        assertEquals(storeable.toString(), "RemoteStoreableValue[3,3,,,true,hello,1]");
    }
}
