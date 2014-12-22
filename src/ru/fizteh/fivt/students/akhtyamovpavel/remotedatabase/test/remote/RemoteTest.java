package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.test.remote;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.DataBaseTableProviderFactory;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.Shell;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote.Closer;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote.RemoteDataBaseTableProvider;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RemoteTest {
    private static DataBaseTableProviderFactory factory;
    private TemporaryFolder serverFolder;
    private TemporaryFolder clientFolder;
    private RemoteDataBaseTableProvider serverProvider;
    private RemoteDataBaseTableProvider clientProvider;
    private Shell serverShell;
    private Shell clientShell;
    private Closer serverCloser;
    private Closer clientCloser;

    public static final int PORT = 10026;

    @BeforeClass
    public static void initFactory() {
        factory = new DataBaseTableProviderFactory();
    }

    @Before
    public void initConnection() {
        serverFolder = new TemporaryFolder();
        clientFolder = new TemporaryFolder();
        serverShell = new Shell();
        clientShell = new Shell();
        try {
            serverProvider = new RemoteDataBaseTableProvider(factory.create(serverFolder.toString()));
            clientProvider = new RemoteDataBaseTableProvider(factory.create(clientFolder.toString()));
        } catch (IOException e) {
            fail();
        }
        serverShell.setProvider(serverProvider);
        clientShell.setProvider(clientProvider);
        serverCloser = new Closer(serverProvider);
        Runtime.getRuntime().addShutdownHook(serverCloser);
        clientCloser = new Closer(clientProvider);
        Runtime.getRuntime().addShutdownHook(clientCloser);
    }

    @Test
    public void connectTest() {
        try {
            serverProvider.startServer(PORT);
        } catch (IOException e) {
            fail();
        }
        try {
            assertEquals("connected", clientProvider.connect("localhost", PORT));
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void setCommandsTest() {
        try {
            serverProvider.startServer(PORT);
            clientProvider.connect("localhost", PORT);
        } catch (IOException e) {
            fail();
        }
        try {
            assertEquals("created", clientProvider.sendCommand("create test [int]"));
        } catch (IOException e) {
            fail();
        }
        try {
            assertEquals("using test", clientProvider.sendCommand("use test"));
        } catch (IOException e) {
            fail();
        }
        try {
            assertEquals("new", clientProvider.sendCommand("put 1 [1]"));
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void connectDisconnectTest() {
        try {
            serverProvider.startServer(PORT);
            clientProvider.connect("localhost", PORT);
        } catch (IOException e) {
            fail();
        }
        clientShell.processInteractiveRequest("create test2 [double]", true);
        try {
            clientProvider.disconnect();
        } catch (IOException e) {
            fail();
        }
        assertEquals("created", clientShell.processInteractiveRequest("create test2 [double]", true).get(0));
    }


    @After
    public void shutdown() {
        clientCloser.start();
        serverCloser.start();
    }


}
