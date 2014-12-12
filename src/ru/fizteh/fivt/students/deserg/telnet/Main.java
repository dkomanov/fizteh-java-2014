package ru.fizteh.fivt.students.deserg.telnet;

import ru.fizteh.fivt.students.deserg.telnet.client.Client;
import ru.fizteh.fivt.students.deserg.telnet.server.Server;

//-Dfizteh.db.dir=db

/**
 * Created by deserg on 03.10.14.
 */
public class Main {

    public static void main(String[] args) {

        String mode = System.getProperty("fizteh.db.dir");
        if (mode != null) {
            Program server = new Server();
            server.work();
        } else {
            Program client = new Client();
            client.work();
        }

    }

}
