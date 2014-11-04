package ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.commands;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.CommandInterruptException;
import ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.MultiFileTableProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.MultiFileTableUtils.*;

public class CreateCommand implements Command {
    @Override
    public String execute(MultiFileTableProvider connector, String... args) throws CommandInterruptException {
        if (args.length < 3) {
            throw new CommandInterruptException("create: too few arguments");
        }
        if (args[2].charAt(0) != '(' || args[args.length-1].charAt(args[args.length-1].length()-1) != ')') {
            return "wrong type (invalid type specification)";
        }
        List<Class<?>> signature = new ArrayList<>();
        args[2] = args[2].substring(1);
        args[args.length-1] = args[args.length-1].substring(0, args[args.length-1].length()-1);
        for (int i = 2; i < args.length; ++i) {
            try {
                signature.add(stringToClass(args[i]));
            } catch (ClassNotFoundException e) {
                return "wrong type (invalid type specification)";
            }
        }

        Table newTable;
        try {
            newTable = connector.createTable(args[1], signature);
        } catch (IOException e) {
            throw new CommandInterruptException("create: cannot create table: " + e.getMessage());
        }
        if (newTable == null) {
            return args[1] + " exists";
        } else {
            return "created";
        }
    }
}
