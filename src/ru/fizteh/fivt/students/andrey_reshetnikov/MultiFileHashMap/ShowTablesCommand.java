package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

import java.util.Map;

public class ShowTablesCommand extends Command {

    protected int numberOfArguments() {
        return 0;
    }

    @Override
    public void execute(DataBaseDir base) throws Exception {
        for (Map.Entry<String, Table> entry: base.tables.entrySet()) {
            String name = entry.getKey();
            int size = entry.getValue().recordsNumber();
            System.out.println(name + " " + size);
        }
    }
}
