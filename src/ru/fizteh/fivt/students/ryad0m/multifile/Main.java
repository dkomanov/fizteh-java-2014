package ru.fizteh.fivt.students.ryad0m.multifile;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {

    public static DataBase database;

    public static void main(String[] args) {
        Shell shell = new Shell();
        if (System.getProperty("fizteh.db.dir") != null) {
            database = new DataBase(Paths.get(System.getProperty("fizteh.db.dir")).toAbsolutePath().normalize());
        } else {
            System.out.println("Please set fizteh.db.dir property!");
            System.exit(1);
        }
        try {
            Main.database.load();
        } catch (IOException e) {
            System.out.println("IO error");
        } catch (BadFormatException e) {
            System.out.println("Format error");
        }
        shell.start(args);
    }
}
