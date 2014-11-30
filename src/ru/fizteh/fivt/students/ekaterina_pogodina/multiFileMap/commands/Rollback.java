package ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.commands;

import ru.fizteh.fivt.students.ekaterina_pogodina.basicclasses.Table;
import ru.fizteh.fivt.students.ekaterina_pogodina.basicclasses.TableNullNameException;
import ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.TableManager;

public class Rollback extends Command {

    @Override
    public void execute(String[] args, TableManager table) throws Exception {
        if (table.currentTable == null) {
            throw new TableNullNameException();
        }
        String jTable = table.currentTable;
        Table dBaseTable = table.basicTables.get(jTable);
        System.out.println(dBaseTable.rollback());
        table.saved = true;
    }
}
