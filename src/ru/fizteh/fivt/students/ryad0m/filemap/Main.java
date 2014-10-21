package ru.fizteh.fivt.students.ryad0m.filemap;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {

    public static TableNode tableNode;

    public static void main(String[] args) {
        if (System.getProperty("db.file") == null) {
            System.out.println("Param db.file not set");
            System.exit(1);
        }
        tableNode = new TableNode(
                Paths.get(System.getProperty("db.file")).toAbsolutePath().normalize());
        Shell shell = new Shell();
        try {
            Main.tableNode.load();
        } catch (IOException e) {
            System.out.println("IO error");
        } catch (BadFormatException e) {
            System.out.println("Format error");
        }
        shell.start(args);
    }
}
