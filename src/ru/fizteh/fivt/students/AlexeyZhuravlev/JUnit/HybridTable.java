package ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit;

import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.Table;

import java.util.ArrayList;

/**
 * @author AlexeyZhuravlev
 */
public class HybridTable {
    public Table virginTable;
    FancyTable dirtyTable;
    ArrayList<Command> changes;

    public HybridTable(Table ordinaryTable) throws Exception {
        virginTable = ordinaryTable;
        dirtyTable = new FancyTable();
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
