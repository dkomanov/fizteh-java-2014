package ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.commands;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.CommandInterruptException;
import ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.MultiFileTableProvider;

import java.io.IOException;

public class CreateCommand implements Command {
    @Override
    public String execute(MultiFileTableProvider connector, String... args) throws CommandInterruptException {
        if (args.length < 2) {
            throw new CommandInterruptException("create: too few arguments");
        }
        if (args.length > 2) {
            throw new CommandInterruptException("create: too many arguments");
        }

        Table newTable;
        try {
            newTable = connector.createTable(args[1], null);
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
