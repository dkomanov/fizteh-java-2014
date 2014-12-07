package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.RemoteTableProviderFactory;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.AlexeyZhuravlev.proxy.AdvancedTableProviderFactory;
import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.MyRemoteTableProviderFactory;
import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.ServerLogic;

import java.io.IOException;

import static org.junit.Assert.*;

public class MyRemoteTableProviderFactoryTest {
    private RemoteTableProviderFactory factory;
    private ServerLogic server;
    int portNumber = 3000;
    String address = "localhost";

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Before
    public void before() throws Exception {
        factory = new MyRemoteTableProviderFactory();
        TableProviderFactory serverFactory = new AdvancedTableProviderFactory();
        TableProvider serverProvider = serverFactory.create(tmpFolder.newFolder().getAbsolutePath());
        server = new ServerLogic(serverProvider);
        server.start(portNumber);
    }

    @After
    public void after() throws Exception {
        server.stop();
    }

    @Test
    public void connect() throws IOException {
        assertNotNull(factory.connect(address, portNumber));
    }

    @Test(expected = IllegalArgumentException.class)
    public void connectWithNullArgument() throws IOException {
        factory.connect(null, 0);
    }
}
