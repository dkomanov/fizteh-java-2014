package ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.commands;

import ru.fizteh.fivt.students.ekaterina_pogodina.basicclasses.TableNullNameException;
import ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.TableManager;

public class List extends Command {
    @Override
    public void execute(String[] args, TableManager table) throws Exception {
        if (table.currentTable == null) {
            throw new TableNullNameException();
        }
        System.out.println(String.join("; ", table.usingTable.keys.keySet()));
    }
}
