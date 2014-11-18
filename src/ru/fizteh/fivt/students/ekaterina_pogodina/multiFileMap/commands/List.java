package ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.commands;

import ru.fizteh.fivt.students.ekaterina_pogodina.basicclasses.FactoryException;
import ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.TableManager;

public class List extends Command {
    @Override
    public void execute(String[] args, TableManager table) throws Exception {
        if (table.currentTable == null) {
            FactoryException.throwNullArgumentException("no table");
        }
        System.out.println(String.join("; ", table.usingTable.keys.keySet()));
    }
}
