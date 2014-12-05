package ru.fizteh.fivt.students.torunova.proxy.actions;

import ru.fizteh.fivt.students.torunova.proxy.CurrentTable;
import ru.fizteh.fivt.students.torunova.proxy.TableWrapper;
import ru.fizteh.fivt.students.torunova.proxy.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.proxy.exceptions.TableNotCreatedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nastya on 21.10.14.
 */
public class CreateTable extends Action {
    @Override
    public boolean run(String[] args, CurrentTable currentTable)
                                throws IOException,
            IncorrectFileException,
            TableNotCreatedException {
        if (!checkNumberOfArguments(2, args.length)) {
            return false;
        }
        String tableName = args[0];
        TableWrapper table = null;
        List<Class<?>> classes = new ArrayList<>();
        for (int i = 1; i < args.length; i++) {
            classes.add(currentTable.getDb().classForName(args[i]));
        }
        try {
            table = (TableWrapper) currentTable.getDb().createTable(tableName, classes);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return false;
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
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
    boolean checkNumberOfArguments(int expected, int real) {
      return real >= expected;
    }

    @Override
    public String getName() {
        return "create";
    }
}
