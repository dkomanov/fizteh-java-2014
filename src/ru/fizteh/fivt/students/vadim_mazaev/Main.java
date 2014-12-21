package ru.fizteh.fivt.students.vadim_mazaev;

import java.io.IOException;

import ru.fizteh.fivt.students.vadim_mazaev.DataBase.TableManagerFactory;
import ru.fizteh.fivt.students.vadim_mazaev.Remote.DataBaseState;
import ru.fizteh.fivt.students.vadim_mazaev.Remote.Server;

public final class Main {
    public static void main(final String[] args) {
        String dbDirPath = System.getProperty("fizteh.db.dir");
        if (dbDirPath == null) {
            System.err.println("You must specify fizteh.db.dir via -Dfizteh.db.dir JVM parameter");
            System.exit(1);
        }
        try (TableManagerFactory factory = new TableManagerFactory()) {
            DataBaseState state = new DataBaseState(factory.create(dbDirPath));
            Server server = new Server(state);
            server.start();
            while (true) {

            }
        } catch (RuntimeException | IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
