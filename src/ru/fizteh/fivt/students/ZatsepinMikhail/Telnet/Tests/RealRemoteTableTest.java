package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.Tests;

import org.junit.*;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.RealRemoteTable;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.RealRemoteTableProvider;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.RealRemoteTableProviderFactory;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.MFileHashMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.Server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RealRemoteTableTest {
    private static final String TABLE_NAME = "testTable";
    private static final String NEW_TABLE_NAME = "newTable";
    private static final String HOST_NAME = "localhost";
    private static final int PORT = 10001;

    private static MFileHashMap tableProvider;

    private static Server myServer;
    private static RealRemoteTableProviderFactory remoteFactory;
    private static RealRemoteTableProvider remoteProvider;
    private static RealRemoteTable remoteTable;
    private static RealRemoteTable newRemoteTable;

    private static List<Class<?>> typeList;
    private static List<Class<?>> newTypeList;

    private String key;
    private Storeable value;
    private Storeable newValue;

    @BeforeClass
    public static void setUpBeforeClass() {
        try {
            Path tmpDir = Files.createTempDirectory("telnet");
            tableProvider = new MFileHashMap(tmpDir.toString());
            typeList = new ArrayList<>();
            typeList.add(Integer.class);
            typeList.add(String.class);
            typeList.add(Boolean.class);
            newTypeList = new ArrayList<>();
            newTypeList.add(Double.class);
            newTypeList.add(Byte.class);
            newTypeList.add(Float.class);

            myServer = new Server(tableProvider);
            myServer.startServer(PORT);
        } catch (IOException e) {
            //suppress
        }
    }

    @Before
    public void setUp() {
        try {
            tableProvider.createTable(TABLE_NAME, typeList);
            tableProvider.createTable(NEW_TABLE_NAME, newTypeList);

            remoteFactory = new RealRemoteTableProviderFactory();
            remoteProvider = (RealRemoteTableProvider) remoteFactory.connect(HOST_NAME, PORT);
            remoteTable = (RealRemoteTable) remoteProvider.getTable(TABLE_NAME);
            newRemoteTable = (RealRemoteTable) remoteProvider.getTable(NEW_TABLE_NAME);

            key = "key";
            value = remoteProvider.createFor(remoteTable);
            newValue = remoteProvider.createFor(remoteTable);

            value.setColumnAt(0, 100);
            value.setColumnAt(1, "qwerty");
            value.setColumnAt(2, true);
            newValue.setColumnAt(0, 585);
            newValue.setColumnAt(1, "newQwerty");
            newValue.setColumnAt(2, false);
        } catch (IOException e) {
            //suppress
        }
    }

    @After
    public void tearDown() {
        try {
            tableProvider.removeTable(TABLE_NAME);
            tableProvider.removeTable(NEW_TABLE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDownAferClass() {
        myServer.stopServer();
    }

    @Test
    public void testGet() throws Exception {
        assertNull(remoteTable.get(key));
        remoteTable.put(key, value);
        assertTrue(remoteTable.get(key).toString().equals(value.toString()));
        remoteTable.put(key, newValue);
        assertTrue(remoteTable.get(key).toString().equals(newValue.toString()));
        remoteTable.remove(key);
        assertNull(remoteTable.get(key));
        remoteTable.rollback();
        remoteTable.put(key, value);
        remoteTable.commit();
        remoteTable.put(key, newValue);
        assertTrue(remoteTable.get(key).toString().equals(newValue.toString()));
        remoteTable.remove(key);
        assertNull(remoteTable.get(key));
    }

    @Test
    public void testRemove() throws Exception {
        assertNull(remoteTable.remove(key));
        remoteTable.put(key, value);
        assertTrue(remoteTable.remove(key).toString().equals(value.toString()));

        remoteTable.rollback();
        remoteTable.put(key, value);
        assertTrue(remoteTable.remove(key).toString().equals(value.toString()));
        remoteTable.put(key, value);
        remoteTable.commit();
        assertTrue(remoteTable.remove(key).toString().equals(value.toString()));
        assertNull(remoteTable.remove(key));
    }

    @Test
    public void testPutCorrectValue() throws Exception {
        assertNull(remoteTable.put(key, value));
        assertTrue(remoteTable.put(key, value).toString().equals(value.toString()));
        assertTrue(remoteTable.get(key).toString().equals(value.toString()));
        assertTrue(remoteTable.put(key, newValue).toString().equals(value.toString()));
        assertTrue(remoteTable.get(key).toString().equals(newValue.toString()));
        remoteTable.remove(key);
        assertNull(remoteTable.put(key, value));

        String keyForCommit = "keyCM";
        Storeable valueForCommit = remoteTable.getTableProvider().createFor(remoteTable);
        valueForCommit.setColumnAt(0, 999);
        valueForCommit.setColumnAt(1, "commit-value");
        valueForCommit.setColumnAt(2, false);

        int size = 5;
        for (int i = 0; i < size; ++i) {
            valueForCommit.setColumnAt(0, 999 + i);
            assertNull(remoteTable.put(keyForCommit + i, valueForCommit));
        }
        remoteTable.commit();
        for (int i = 0; i < size; ++i) {
            valueForCommit.setColumnAt(0, 999 + i);
            assertTrue(remoteTable.get(keyForCommit + i).toString()
                    .equals(valueForCommit.toString()));
        }

        Storeable freshValue = remoteTable.getTableProvider().createFor(remoteTable);
        freshValue.setColumnAt(0, 123456);
        freshValue.setColumnAt(1, "FRESH!!!");
        freshValue.setColumnAt(2, true);

        remoteTable.rollback();
        remoteTable.remove(keyForCommit + 1);

        valueForCommit.setColumnAt(0, 1000);
        assertNull(remoteTable.put(keyForCommit + 1, valueForCommit));
        valueForCommit.setColumnAt(0, 1001);
        assertTrue(remoteTable.put(keyForCommit + 2, freshValue).toString().
                equals(valueForCommit.toString()));
    }

    @Test
    public void testSize() throws Exception {
        int size = 5;
        for (int i = 0; i < size; ++i) {
            value.setColumnAt(0, i);
            remoteTable.put(key + i, value);
        }
        assertTrue(remoteTable.size() == size);
        remoteTable.remove(key + 0);
        assertTrue(remoteTable.size() == size - 1);
    }

    @Test
    public void testRollback() throws Exception {
        int size = 5;
        for (int i = 0; i < size; ++i) {
            value.setColumnAt(0, i);
            remoteTable.put(key + i, value);
        }
        remoteTable.remove(key + 0);
        remoteTable.remove(key + 2);
        remoteTable.put(key + 1, newValue);
        assertTrue(remoteTable.rollback() == size - 2);
        remoteTable.put(key, value);
        assertTrue(remoteTable.rollback() == 1);
        remoteTable.put(key, value);
        remoteTable.commit();
        assertTrue(remoteTable.rollback() == 0);
    }

    @Test
    public void testCommit() throws Exception {
        int size = 5;
        for (int i = 0; i < size; ++i) {
            value.setColumnAt(0, i);
            remoteTable.put(key + i, value);
        }
        remoteTable.remove(key + 0);
        remoteTable.remove(key + 2);
        remoteTable.put(key + 1, newValue);
        assertTrue(remoteTable.commit() == size - 2);
        assertTrue(remoteTable.rollback() == 0);
        remoteTable.put(key, newValue);
        remoteTable.remove(key);
        assertTrue(remoteTable.commit() == 0);
        newValue.setColumnAt(0, 111111);
        remoteTable.put(key + 1, newValue);
        assertTrue(remoteTable.commit() == 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNullInput() {
        remoteTable.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullInput() {
        remoteTable.put(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNullInput() {
        remoteTable.remove(null);
    }

    @Test(expected = ColumnFormatException.class)
    public void testPutIllegalColumn() {
        Storeable newValueForCheck = remoteProvider.createFor(newRemoteTable);
        newValueForCheck.setColumnAt(0, 4.4);
        newValueForCheck.setColumnAt(1, (byte) 0);
        newValueForCheck.setColumnAt(2, (float) 8.8);
        remoteTable.put("simpleKey", newValueForCheck);
    }

    @Test
    public void testGetColumnsCount() {
        assertTrue(remoteTable.getColumnsCount() == 3);
    }

    @Test
    public void testGetColumnType() {
        assertTrue(remoteTable.getColumnType(0).equals(Integer.class));
        try {
            remoteTable.getColumnType(9);
            fail();
        } catch (IndexOutOfBoundsException e) {
            //all right
        }
    }

    @Test
    public void testList() {
        for (int i = 0; i < 5; ++i) {
            remoteTable.put(key + i, value);
        }
        List<String> keys = remoteTable.list();
        assertTrue(keys.size() == 5);
        for (int i = 0; i < 5; ++i) {
            keys.contains(key + i);
        }
    }

    @Test
    public void testGetNumberOfUncommittedChanges() {
        assertTrue(remoteTable.getNumberOfUncommittedChanges() == 0);
        remoteTable.put(key, value);
        assertTrue(remoteTable.getNumberOfUncommittedChanges() == 1);
        try {
            remoteTable.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(remoteTable.getNumberOfUncommittedChanges() == 0);
    }

    @Test
    public void testGetName() {
        assertTrue(remoteTable.getName().equals(TABLE_NAME));
    }
}
