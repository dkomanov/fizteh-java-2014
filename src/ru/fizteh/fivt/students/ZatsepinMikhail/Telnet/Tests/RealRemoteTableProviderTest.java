package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.Tests;

import org.junit.*;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.StoreablePackage.TypesUtils;
import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.shell.FileUtils;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.RealRemoteTableProvider;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.RealRemoteTableProviderFactory;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.MFileHashMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.Server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RealRemoteTableProviderTest {
    private static final String TABLE_NAME = "testTable";
    private static final String HOST_NAME = "localhost";
    private static final int PORT = 10001;

    private static String providerDirectory;

    private static MFileHashMap tableProvider;

    private static Server myServer;
    private static RealRemoteTableProviderFactory remoteFactory;
    private static RealRemoteTableProvider remoteProvider;

    private static List<Class<?>> typeList;
    private static List<Class<?>> wrongTypeList;

    @BeforeClass
    public static void setUpBeforeClass() {
        typeList = new ArrayList<>();
        typeList.add(Integer.class);
        typeList.add(String.class);

        wrongTypeList = new ArrayList<>();
        wrongTypeList.add(Integer.class);
        wrongTypeList.add(String.class);
        wrongTypeList.add(Double.class);
    }

    @Before
    public void setUp() {
        try {
            Path tmpDir = Files.createTempDirectory("telnet");
            providerDirectory = tmpDir.toString();
            tableProvider = new MFileHashMap(tmpDir.toString());

            myServer = new Server(tableProvider);
            myServer.startServer(PORT);

            remoteFactory = new RealRemoteTableProviderFactory();
            remoteProvider = (RealRemoteTableProvider) remoteFactory.connect(HOST_NAME, PORT);
        } catch (IOException e) {
            //suppress
        }
    }

    @After
    public void tearDown() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        myServer.stopServer();
    }

    @Test
    public void testGetTable() throws Exception {
        assertNull(remoteProvider.getTable(TABLE_NAME));
        assertNotNull(remoteProvider.createTable(TABLE_NAME, typeList));
        assertNotNull(remoteProvider.getTable(TABLE_NAME));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableNull() throws Exception {
        remoteProvider.getTable(null);
    }

    @Test
    public void testCreateTableDirExists() throws Exception {
        FileUtils.mkdir(Paths.get(providerDirectory, TABLE_NAME));
        assertNull(remoteProvider.createTable(TABLE_NAME, typeList));
    }

    @Test
    public void testCreateDouble() throws Exception {
        remoteProvider.createTable(TABLE_NAME, typeList);
        assertNull(remoteProvider.createTable(TABLE_NAME, typeList));
    }

    @Test
    public void testCreateTableDirNotExists() throws Exception {
        try {
            FileUtils.rmdir(Paths.get(providerDirectory, TABLE_NAME));
        } catch (IllegalArgumentException e) {
            //suppress - means directory doesn't exist
        }
        assertNotNull(remoteProvider.createTable(TABLE_NAME, typeList));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableNullDirectory() throws Exception {
        remoteProvider.createTable(null, typeList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableNullTypeList() throws Exception {
        remoteProvider.createTable(TABLE_NAME, null);
    }

    @Test
    public void testRemoveTableExists() throws Exception {
        remoteProvider.createTable(TABLE_NAME, typeList);
        remoteProvider.removeTable(TABLE_NAME);
        assertNull(remoteProvider.getTable(TABLE_NAME));
        assertTrue(!Files.exists(Paths.get(providerDirectory, TABLE_NAME)));
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveTableNotExists() throws Exception {
        remoteProvider.removeTable(TABLE_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableNull() throws Exception {
        remoteProvider.removeTable(null);
    }

    @Test
    public void testSerialize() {
        try {
            Table newTable;
            newTable = remoteProvider.createTable(TABLE_NAME, typeList);
            Storeable value = remoteProvider.createFor(newTable);
            value.setColumnAt(0, 100);
            value.setColumnAt(1, "example");

            String serValue = "<row><col>100</col><col>example</col></row>";
            assertTrue(remoteProvider.serialize(newTable, value).equals(serValue));

            Table wrongTable;
            wrongTable = remoteProvider.createTable(TABLE_NAME + 2, wrongTypeList);
            Storeable wrongValue = remoteProvider.createFor(wrongTable);
            wrongValue.setColumnAt(0, 100);
            wrongValue.setColumnAt(1, "example");
            wrongValue.setColumnAt(2, 5.5);

            try {
                remoteProvider.serialize(newTable, wrongValue);
                fail();
            } catch (ColumnFormatException e) {
                //all right
            }
        } catch (IOException e) {
            //suppress
        }
    }

    @Test
    public void testDeserialize() {
        try {
            Table newTable = remoteProvider.createTable(TABLE_NAME, typeList);
            String stringForParse = "<row><col>100</col><col>example</col></row>";
            String stringForParse2 = "<row><col>qwerty</col><col>example</col></row>";
            try {
                Storeable deserValue = remoteProvider.deserialize(newTable, stringForParse);
                assertTrue(deserValue.getIntAt(0).equals(100));
                assertTrue(deserValue.getStringAt(1).equals("example"));
            } catch (ParseException e) {
                assertNotNull(null);
            }

            try {
                remoteProvider.deserialize(newTable, stringForParse + 2);
                fail();
            } catch (ParseException e) {
                //all right
            }

            try {
                remoteProvider.deserialize(newTable, stringForParse2);
                fail();
            } catch (ParseException e) {
                //all right
            }
        } catch (IOException e) {
            //suppress
        }
    }

    @Test
    public void testCreateFor() {
        try {
            Table newTable;
            newTable = remoteProvider.createTable(TABLE_NAME + "NEW", typeList);
            Storeable temp = remoteProvider.createFor(newTable);
            assertTrue(TypesUtils.getSizeOfStoreable(temp) == newTable.getColumnsCount());
        } catch (IOException e) {
            //suppress
        }
    }

    @Test
    public void testCreateForWithValue() {
        try {
            Table newTable;
            newTable = remoteProvider.createTable(TABLE_NAME, typeList);
            List<Object> value = new ArrayList<>();
            value.add(100);
            value.add("new");
            Storeable temp = remoteProvider.createFor(newTable, value);
            assertTrue(TypesUtils.getSizeOfStoreable(temp) == newTable.getColumnsCount());

            try {
                value = new ArrayList<>();
                value.add("new");
                value.add(100);
                remoteProvider.createFor(newTable, value);
                fail();
            } catch (ColumnFormatException e) {
                //all right
            }

            try {
                value = new ArrayList<>();
                value.add("new2");
                remoteProvider.createFor(newTable, value);
                fail();
            } catch (IndexOutOfBoundsException e) {
                //all right
            }

        } catch (IOException e) {
            //suppress
        }
    }

    @Test
    public void testSetCurrentTable() {
        try {
            remoteProvider.setCurrentTable(TABLE_NAME);
            fail();
        } catch (IllegalStateException e) {
            //all right
        }
        try {
            remoteProvider.createTable(TABLE_NAME, typeList);
        } catch (IOException e) {
            //suppress
        }
        remoteProvider.setCurrentTable(TABLE_NAME);
    }

    @Test
    public void describeTable() {
        try {
            remoteProvider.createTable(TABLE_NAME, typeList);
        } catch (IOException e) {
            //suppress
        }
        assertTrue(remoteProvider.describeTable(TABLE_NAME).equals("int String"));
    }
}

