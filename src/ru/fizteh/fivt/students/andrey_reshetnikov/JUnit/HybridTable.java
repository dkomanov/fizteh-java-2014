package ru.fizteh.fivt.students.andrey_reshetnikov.JUnit;


import ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap.Table;

import java.util.ArrayList;

public class HybridTable {
    Table cleanTable;
    FancyTable dirtyTable;
    ArrayList<Command> changes;

    public HybridTable(Table ordinaryTable) throws Exception {
        cleanTable = ordinaryTable;
        dirtyTable = new FancyTable();
        changes = new ArrayList<>();
        rollBack();
    }

    public int rollBack() {
        int ans = changes.size();
        dirtyTable.importMap(cleanTable);
        changes.clear();
        return ans;
    }

    public int commit() throws Exception {
        int ans = changes.size();
        for (Command command: changes) {
            command.executeOnTable(cleanTable);
        }
        changes.clear();
        return ans;
    }
}
