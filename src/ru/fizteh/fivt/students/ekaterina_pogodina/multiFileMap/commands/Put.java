package ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.commands;

//import ru.fizteh.fivt.students.ekaterina_pogodina.filemap.DataBase;
import ru.fizteh.fivt.students.ekaterina_pogodina.basicclasses.MyException;
import ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.TableManager;

import java.nio.file.Path;

public class Put extends Command {

    private static final int SIZE = 16;

    @Override
    public void execute(String[] args, TableManager table) throws Exception {
        String tableName = table.currentTable;
        if (table.currentTable == null) {
            MyException.exception("no table");
        }
        Path path = table.path;
        String key = args[1];
        String value = args[2];
        table.usingTable.puted.put(key, value);
        if (table.usingTable.keys.containsKey(key)) {
            System.out.println("overwrite");
            System.out.println(table.usingTable.keys.get(key));
        } else {
            System.out.println("new");
        }
    }

    @Override
    public void checkArgs(String[] args, TableManager table) throws Exception {
        if (args.length > 3) {
            table.manyArgs(args[0]);
        }
        if (args.length < 3) {
            table.missingOperand(args[0]);
        }
        execute(args, table);
    }
}
