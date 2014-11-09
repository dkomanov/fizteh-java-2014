package ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.commands;

import ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.TableManager;

public class Use extends Command {
    @Override
    public void execute(String[] args, TableManager table) throws Exception {
        String jTable = table.currentTable;
        if (jTable == null) {
            table.use(args[1]);
        } else {
                table.use(args[1]);
        }
    }

    @Override
    public void checkArgs(String[] args, TableManager table) throws Exception {
        if (args.length < 2) {
            table.missingOperand(args[0]);
        }
        if (args.length > 3) {
            table.manyArgs(args[0]);
        }
        execute(args, table);
    }
}
