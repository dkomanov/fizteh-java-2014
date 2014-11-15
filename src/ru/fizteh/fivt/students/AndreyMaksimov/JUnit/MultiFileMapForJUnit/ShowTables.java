package ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit;


import java.util.Map;

public class ShowTables extends Command {

    protected int numberOfArguments() {
        return 0;
    }

    @Override
    public void startNeedInstruction(DataBaseDir base) throws Exception {
        for (Map.Entry<String, Table> entry: base.tables.entrySet()) {
            String name = entry.getKey();
            int size = entry.getValue().recordsNumber();
            System.out.println(name + " " + size);
        }
    }
}

