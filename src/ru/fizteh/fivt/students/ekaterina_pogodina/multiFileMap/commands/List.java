package ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.commands;

import ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.BaseTable;
import ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.TableManager;

public class List extends Command {
    @Override
    public void execute(String[] args, TableManager table) throws Exception {
        if (table.currentTable == null) {
            throw new Exception("no table");
        }
        BaseTable entryTable = table.tables.get(table.currentTable);
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (entryTable.tableDateBase[i][j] != null) {
                    entryTable.tableDateBase[i][j].list(args);
                }
            }
        }
    }
}
