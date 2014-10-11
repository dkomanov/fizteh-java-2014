package ru.fizteh.fivt.students.IsalySultan.FileMap;

import java.io.IOException;

public class FileMap {

    public static void main(String[] argv) {
        try {
            table object = new table();
            if (argv.length > 0) {
                pocketParser.batchMode(object, argv);
            } else {
                Interactive.interactiveMode(object);
            }
        } catch (IOException e) {
            System.out.println("error create table");
        }
    }
}
