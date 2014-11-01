package ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.commands;

import ru.fizteh.fivt.students.ekaterina_pogodina.basicclasses.Table;
import ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.TableManager;

public class Use extends Command {
    @Override
    public void execute(String[] args, TableManager table) throws Exception {
        if (args.length < 2) {
            table.missingOperand(args[0]);
        }
        if (args.length > 3) {
            table.manyArgs(args[0]);
        }
        String jTable = table.currentTable;
        if (jTable == null) {
            table.use(args[1]);
        } else {
            Table dBaseTable = table.basicTables.get(jTable);
            if (dBaseTable.size() == 0) {
                table.use(args[1]);
            } else {
                throw new Exception("unsaved changes");
            }
        }
    }
}
