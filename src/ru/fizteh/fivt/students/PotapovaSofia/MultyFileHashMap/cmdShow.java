package ru.fizteh.fivt.students.PotapovaSofia.MultyFileHashMap;

import java.util.Map;
import java.util.Vector;

public class cmdShow implements Command {
    @Override
    public void execute(Vector<String> args, DataBase db) {
        if (args.size() < 2) {
            commandParser.fewArgs("show");
        } else if (args.size() > 2) {
            commandParser.tooMuchArgs("show");
        } else {
            if (!args.get(1).equals("tables")) {
                commandParser.wrongCmd();
            }
            System.out.println("table_name row_count");
            for (Map.Entry<String, Table> entry : db.tables.entrySet()) {
                System.out.println(entry.getKey() + " " + entry.getValue().size());
            }
        }
    }
}
