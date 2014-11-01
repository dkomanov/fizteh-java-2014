package ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.commands;

import ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.TableManager;

public class Get extends Command {

    private static final int SIZE = 16;

    @Override
    public void execute(String[] args, TableManager table) throws Exception {
        if (table.currentTable == null) {
            throw new Exception("no table");
        }
        if (args.length > 2) {
            table.manyArgs(args[0]);
        }
        if (args.length < 2) {
            table.missingOperand(args[0]);
        }
        String key = args[1];
        byte b = key.getBytes()[0];
        int nDirectory = b % SIZE;
        int nFile = b / SIZE % SIZE;
        table.usingTable.tableDateBase[nDirectory][nFile].get(args);
    }
}
