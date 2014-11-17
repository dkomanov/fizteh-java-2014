package ru.fizteh.fivt.students.PotapovaSofia.MultiFileHashMap;

import java.util.Map;
import java.util.Vector;

public class CmdShow implements Command {
    @Override
    public void execute(Vector<String> args, DataBase db) {
        if (!args.get(1).equals("tables")) {
            throw new IllegalArgumentException("Wrong command");
        } else {
            System.out.println("table_name row_count");
            for (Map.Entry<String, Table> entry : db.getDataBase().entrySet()) {
                System.out.println(entry.getKey() + " " + entry.getValue().size());
            }
        }
    }
}
