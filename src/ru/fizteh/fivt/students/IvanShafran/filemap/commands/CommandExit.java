package ru.fizteh.fivt.students.IvanShafran.filemap.commands;


import ru.fizteh.fivt.students.IvanShafran.filemap.DBFile;
import ru.fizteh.fivt.students.IvanShafran.filemap.abstractShell.Command;

import java.util.ArrayList;

public class CommandExit extends Command {
    private DBFile dbFile;

    public void execute(ArrayList<String> args) {
        try {
            dbFile.writeHashMapToFile();
        } catch (Exception e) {
            System.err.println("error during writing file");
            System.exit(-1);
        }
        System.exit(0);
    }

    public CommandExit(DBFile file) {
        dbFile = file;
    }
}
