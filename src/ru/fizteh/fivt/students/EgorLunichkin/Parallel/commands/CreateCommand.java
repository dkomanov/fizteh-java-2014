package ru.fizteh.fivt.students.EgorLunichkin.Parallel.commands;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.EgorLunichkin.Parallel.*;
import ru.fizteh.fivt.students.EgorLunichkin.Storeable.TypeManager;

import java.io.IOException;
import java.util.List;

public class CreateCommand implements Command {
    private static String removeFirst(String s) {
        return s.substring(1);
    }

    private static String removeLast(String s) {
        return s.substring(0, s.length() - 1);
    }

    public CreateCommand(ParallelTableProvider ptp, String givenName, String[] givenTypeNames)
            throws ParallelException {
        base = ptp;
        tableName = givenName;
        StringBuilder typeNames = new StringBuilder();
        if (!givenTypeNames[0].startsWith("(") || !givenTypeNames[givenTypeNames.length - 1].endsWith(")")) {
            throw new ParallelException("wrong type (Typename list must be in brackets)");
        }
        givenTypeNames[0] = removeFirst(givenTypeNames[0]);
        givenTypeNames[givenTypeNames.length - 1] = removeLast(givenTypeNames[givenTypeNames.length - 1]);
        for (int ind = 0; ind < givenTypeNames.length; ++ind) {
            typeNames.append(givenTypeNames[ind] + ' ');
        }
        types = TypeManager.getClasses(typeNames.toString().trim());
    }

    private ParallelTableProvider base;
    private String tableName;
    private List<Class<?>> types;

    @Override
    public void run() throws ParallelException {
        try {
            Table table = base.createTable(tableName, types);
            if (table == null) {
                System.out.println(tableName + " exists");
            } else {
                System.out.println("created");
            }
        } catch (IOException ex) {
            throw new ParallelException(ex.getMessage());
        }
    }
}
