package ru.fizteh.fivt.students.IsalySultan.FileMap;

import java.io.IOException;

public class FileMap {

    public static void main(String[] argv) {
        try {
            Table object = new Table();
            if (argv.length > 0) {
                PocketParser.batchMode(object, argv);
            } else {
                Interactive.interactiveMode(object);
            }
        } catch (IOException e) {
            System.err.println("error create table");
        }
    }
}
