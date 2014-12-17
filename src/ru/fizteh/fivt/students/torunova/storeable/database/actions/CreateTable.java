package ru.fizteh.fivt.students.torunova.storeable.database.actions;

import ru.fizteh.fivt.students.torunova.storeable.database.TableHolder;
import ru.fizteh.fivt.students.torunova.storeable.database.TableWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by nastya on 21.10.14.
 */
public class CreateTable extends Action{
    @Override
    public boolean run(String args, TableHolder currentTable) throws IOException {
        String[] parameters = parseArguments(args);
        if (!checkNumberOfArguments(2, parameters.length)) {
            return false;
        }
        String tableName = parameters[0];
        TableWrapper table = null;
        List<Class<?>> classes = new ArrayList<>();
        for (int i = 1; i < parameters.length; i++) {
            classes.add(currentTable.getDb().classForName(parameters[i]));
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

    @Override
    public String[] parseArguments(String args) {
            args = args.replaceAll("\\s+", " ");
            String typesWithoutBrakets;
            String tableName;
            try {
                String newArgs = args.substring(args.indexOf(' ') + 1);
                tableName = newArgs.substring(0, newArgs.indexOf(' '));
                String argsWithoutTableName = newArgs.substring(newArgs.indexOf(' '));
                typesWithoutBrakets = argsWithoutTableName.substring(argsWithoutTableName.indexOf('(') + 1,
                        argsWithoutTableName.lastIndexOf(')'));
            } catch (StringIndexOutOfBoundsException e) {
                throw new RuntimeException("create: wrong command format");
            }
            List<String> normalArgs = new ArrayList<>();
            normalArgs.add(tableName);
            normalArgs.addAll(Arrays.asList(typesWithoutBrakets.split("\\s+")));
            return normalArgs.toArray(new String[0]);

    }
}
