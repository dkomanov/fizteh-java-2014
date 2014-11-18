package ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.commands;

import ru.fizteh.fivt.students.ekaterina_pogodina.basicclasses.errorMessageException;
import ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.TableManager;

public class Get extends Command {

    private static final int SIZE = 16;

    @Override
    public void execute(String[] args, TableManager table) throws Exception {
        if (table.currentTable == null) {
            errorMessageException.exception("no table");
        }
        String key = args[1];
        if (table.usingTable.keys.containsKey(key)) {
            System.out.println("found");
            System.out.println(table.usingTable.keys.get(key));
        } else {
            System.out.println("not found");
        }
    }

    @Override
    public void checkArgs(String[] args, TableManager table) throws Exception {
        if (args.length > 2) {
            table.manyArgs(args[0]);
        }
        if (args.length < 2) {
            table.missingOperand(args[0]);
        }
        execute(args, table);
    }
}
