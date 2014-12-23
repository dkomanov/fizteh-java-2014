package ru.fizteh.fivt.students.irina_karatsapova.parallel;

import java.io.IOException;

public class ServerMain {
    public static String mainDir = "fizteh.db.dir";

    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.startWork();
        } catch (IOException e) {
            System.err.println("Something went wrong with the server");
            System.exit(-1);
        }
    }
}
