package ru.fizteh.fivt.students.MaksimovAndrey.MultiFileMap;

import java.util.Map;

public class ShowTables extends Command {

    @Override
    public void startNeedMultiInstruction(DataBaseDir base) throws Exception {
        for (Map.Entry<String, Table> entry : base.tables.entrySet()) {
            String name = entry.getKey();
            int size = entry.getValue().recordsNumber();
            System.out.println(name + " " + size);
        }
    }
}

