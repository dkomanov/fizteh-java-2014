package ru.fizteh.fivt.students.standy66_new.runners;

/**
 * @author andrew
 *         Created by andrew on 30.11.14.
 */
public class Task9Runner {
    public static void main(String... args) {
        String dbDir = System.getProperty("fizteh.db.dir");
        if (dbDir == null) {
            System.err.println("No dir specified, use -Dfizteh.db.dir=...");
            System.exit(1);
        }


    }
}
