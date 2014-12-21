package ru.fizteh.fivt.students.torunova.junit.actions;

import ru.fizteh.fivt.students.torunova.junit.CurrentTable;
import ru.fizteh.fivt.students.torunova.junit.TableImpl;
import ru.fizteh.fivt.students.torunova.junit.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.junit.exceptions.TableNotCreatedException;

import java.io.IOException;

/**
 * Created by nastya on 21.10.14.
 */
public class CreateTable extends Action{
    @Override
    public boolean run(String[] args, CurrentTable currentTable)
                                throws IOException,
                                IncorrectFileException,
                                TableNotCreatedException {
        if (!checkNumberOfArguments(1, args.length)) {
            return false;
        }
        String tableName = args[0];
        TableImpl table;
        try {
            table = (TableImpl) currentTable.getDb().createTable(tableName);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return false;
        }
        if (table != null) {
            System.out.println("created");
            return true;
        } else {
            System.out.println(tableName + " exists");
            return false;
        }
    }

    @Override
    public String getName() {
        return "create";
    }
}
