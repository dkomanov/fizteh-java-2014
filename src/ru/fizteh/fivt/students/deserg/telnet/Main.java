package ru.fizteh.fivt.students.deserg.telnet;

import ru.fizteh.fivt.students.deserg.telnet.client.Client;
import ru.fizteh.fivt.students.deserg.telnet.server.Server;
import ru.fizteh.fivt.students.deserg.telnet.exceptions.MyException;

/**
 * Created by deserg on 03.10.14.
 */
public class Main {

    public static void main(String[] args) {

        String mode = System.getProperty("mode");
        if (mode != null) {
            Program server = new Server();
            server.work(args);
        } else {
            Program client = new Client();
            client.work(args);
        }

    }

}
