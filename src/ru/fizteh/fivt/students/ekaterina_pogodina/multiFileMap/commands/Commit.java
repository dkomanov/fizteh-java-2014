package ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.commands;

import ru.fizteh.fivt.students.ekaterina_pogodina.basicclasses.FactoryException;
import ru.fizteh.fivt.students.ekaterina_pogodina.basicclasses.Table;
import ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.TableManager;

public class Commit extends Command {
    @Override
    public void execute(String[] args, TableManager table) throws Exception {
        if (table.currentTable == null) {
            FactoryException.throwNullArgumentException("no table");
        } else {
            String jTable = table.currentTable;
            Table dBaseTable = table.basicTables.get(jTable);
            System.out.println(dBaseTable.commit());
        }
    }
}
