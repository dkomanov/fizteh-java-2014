package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.Tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.RealRemoteTableProviderFactory;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.MFileHashMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.Server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class RealRemoteTableProviderFactoryTest {
    private static final String HOST_NAME = "localhost";
    private static final int PORT = 10001;

    private static MFileHashMap tableProvider;

    private static Server myServer;
    private static RealRemoteTableProviderFactory remoteFactory;

    @Before
    public void setUp() {
        try {
            Path tmpDir = Files.createTempDirectory("telnet");
            tableProvider = new MFileHashMap(tmpDir.toString());

            myServer = new Server(tableProvider);
            myServer.startServer(PORT);

            remoteFactory = new RealRemoteTableProviderFactory();
        } catch (IOException e) {
            //suppress
        }
    }

    @After
    public void tearDown() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        myServer.stopServer();
    }

    @Test
    public void testConnect() throws Exception {
        assertNotNull(remoteFactory.connect(HOST_NAME, PORT));
    }

    @Test
    public void testDisconnectCurrentProvider() throws Exception {
        remoteFactory.connect(HOST_NAME, PORT);
        assertNotNull(remoteFactory.getCurrentProvider());
        remoteFactory.disconnectCurrentProvider();
        assertNull(remoteFactory.getCurrentProvider());
    }

    @Test
    public void testGetCurrentProvider() throws Exception {
        assertNull(remoteFactory.getCurrentProvider());
        remoteFactory.connect(HOST_NAME, PORT);
        assertNotNull(remoteFactory.getCurrentProvider());
    }
}
