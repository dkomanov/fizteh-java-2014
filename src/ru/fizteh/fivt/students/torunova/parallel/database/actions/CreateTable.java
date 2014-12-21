package ru.fizteh.fivt.students.torunova.parallel.database.actions;

import ru.fizteh.fivt.students.torunova.parallel.database.TableHolder;
import ru.fizteh.fivt.students.torunova.parallel.database.TableWrapper;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by nastya on 21.10.14.
 */
public class CreateTable extends Action{
    TableHolder currentTable;
    PrintWriter writer;
    public CreateTable(TableHolder currentTable, OutputStream os) {
        this.currentTable = currentTable;
        writer = new PrintWriter(os, true);
    }
    @Override
    public boolean run(String args) throws IOException {
        String[] parameters = parseArguments(args);
        if (!checkNumberOfArguments(2, parameters.length, writer)) {
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
            writer.println(e.getMessage());
            return false;
        } catch (RuntimeException e) {
            writer.println(e.getMessage());
        }
        if (table != null) {
            writer.println("created");
            return true;
        } else {
            writer.println(tableName + " exists");
            return false;
        }
    }

    @Override
    boolean checkNumberOfArguments(int expected, int real, PrintWriter writer) {
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
