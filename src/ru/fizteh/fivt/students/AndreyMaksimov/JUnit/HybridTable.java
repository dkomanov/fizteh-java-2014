package ru.fizteh.fivt.students.MaksimovAndrey.JUnit;

import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.Command;
import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.Table;

import java.util.ArrayList;

public class HybridTable {
    Table virginTable;
    DampingTable dirtyTable;
    ArrayList<Command> changes;

    public HybridTable(Table ordinaryTable) throws Exception {
        virginTable = ordinaryTable;
        dirtyTable = new DampingTable();
        changes = new ArrayList<>();
        rollBack();
    }

    public int rollBack() {
        int ans = changes.size();
        dirtyTable.importMap(virginTable);
        changes.clear();
        return ans;
    }

    public int commit() throws Exception {
        int ans = changes.size();
        for (Command command: changes) {
            command.executeOnTable(virginTable);
        }
        changes.clear();
        return ans;
    }
}
