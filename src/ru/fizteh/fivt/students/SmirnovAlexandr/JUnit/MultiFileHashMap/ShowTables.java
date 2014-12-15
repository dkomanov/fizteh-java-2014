package ru.fizteh.fivt.students.SmirnovAlexandr.JUnit.MultiFileHashMap;

import java.util.Map;

public class ShowTables extends Command {

    protected int numberOfArguments() {
        return 0;
    }

    @Override
    public void execute(DataBaseDir base) throws Exception {
        for (Map.Entry<String, MultiTable> entry: base.tables.entrySet()) {
            String name = entry.getKey();
            int size = entry.getValue().recordsNumber();
            System.out.println(name + " " + size);
        }
    }
}
