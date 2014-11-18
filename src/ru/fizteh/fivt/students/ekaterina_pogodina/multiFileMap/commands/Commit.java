package ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.commands;

import ru.fizteh.fivt.students.ekaterina_pogodina.basicclasses.errorMessageException;
import ru.fizteh.fivt.students.ekaterina_pogodina.basicclasses.Table;
import ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.TableManager;

public class Commit extends Command {
    @Override
    public void execute(String[] args, TableManager table) throws Exception {
        if (table.currentTable == null) {
            errorMessageException.exception("no table");
        } else {
            String jTable = table.currentTable;
            Table dBaseTable = table.basicTables.get(jTable);
            System.out.println(dBaseTable.commit());
        }
    }
    @Override
    public void checkArgs(String[] args, TableManager table) throws Exception {
        if (args.length > 1) {
            table.manyArgs(args[1]);
        }
    }
}
