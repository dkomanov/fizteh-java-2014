package ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.commands;

import ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.TableManager;

public class Size extends Command {

    @Override
    public void execute(String[] args, TableManager table) throws Exception {
        String jTable = table.currentTable;
        System.out.println(table.basicTables.get(jTable).size());
    }

    @Override
    public void checkArgs(String[] args, TableManager table) throws Exception {
        if (args.length > 1) {
            table.manyArgs(args[1]);
        }
        execute(args, table);
    }
}
