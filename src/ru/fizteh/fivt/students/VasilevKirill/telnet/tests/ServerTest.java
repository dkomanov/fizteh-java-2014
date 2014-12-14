package ru.fizteh.fivt.students.VasilevKirill.telnet.tests;

import jdk.internal.util.xml.impl.Input;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.students.VasilevKirill.telnet.ClientMain;
import ru.fizteh.fivt.students.VasilevKirill.telnet.ServerMain;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by Kirill on 14.12.2014.
 */
public class ServerTest {
    private static Thread serverMain;
    private static Thread clientMain;
    private static byte[] inputServerLine;
    private static byte[] inputClientLine;
    private static InputStream oldInput = System.in;
    private static InputStream newInput;

    @Test
    public void connectTests() throws Exception {
        inputServerLine = "start\nstop".getBytes();
        inputClientLine = "connect 127.0.0.1 10001\ndisconnect".getBytes();
        serverMain = new Thread(() -> {
            newInput = new ByteArrayInputStream(inputServerLine);
            System.setIn(newInput);
            String[] args = {};
            ServerMain.main(args);
            System.setIn(oldInput);
        });
        clientMain = new Thread(() -> {
            newInput = new ByteArrayInputStream(inputClientLine);
            System.setIn(newInput);
            String[] args = {};
            ClientMain.main(args);
            System.setIn(oldInput);
        });
        serverMain.start();
        clientMain.start();
        serverMain.join();
        clientMain.join();
    }

}
