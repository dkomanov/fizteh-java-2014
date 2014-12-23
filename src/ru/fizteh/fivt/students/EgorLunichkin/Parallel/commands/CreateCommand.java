package ru.fizteh.fivt.students.EgorLunichkin.Parallel.commands;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.EgorLunichkin.Parallel.*;
import ru.fizteh.fivt.students.EgorLunichkin.Storeable.TypeManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CreateCommand implements Command {
    private static String removeFirst(String s) {
        return s.substring(1);
    }

    private static String removeLast(String s) {
        return s.substring(0, s.length() - 1);
    }

    public CreateCommand() {}

    private ParallelTableProvider base;
    private String tableName;
    private List<Class<?>> types;

    @Override
    public void run() throws IOException {
        Table table = base.createTable(tableName, types);
        if (table == null) {
            System.out.println(tableName + " exists");
        } else {
            System.out.println("created");
        }
    }

    @Override
    public void putArguments(ParallelTableProvider ptp, String[] args) throws ParallelException {
        if (args.length < minArguments()) {
            throw new ParallelException("create: Too few arguments");
        }
        String givenName = args[0];
        String[] givenTypeNames = Arrays.copyOfRange(args, 1, args.length);
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

    @Override
    public int minArguments() {
        return 2;
    }

    @Override
    public int maxArguments() {
        return Integer.MAX_VALUE;
    }
}
