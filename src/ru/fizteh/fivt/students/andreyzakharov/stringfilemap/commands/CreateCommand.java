package ru.fizteh.fivt.students.andreyzakharov.stringfilemap.commands;

import ru.fizteh.fivt.students.andreyzakharov.stringfilemap.CommandInterruptException;
import ru.fizteh.fivt.students.andreyzakharov.stringfilemap.MultiFileTableProvider;

public class CreateCommand implements Command {
    @Override
    public String execute(MultiFileTableProvider connector, String... args) throws CommandInterruptException {
        if (args.length < 2) {
            throw new CommandInterruptException("create: too few arguments");
        }
        if (args.length > 2) {
            throw new CommandInterruptException("create: too many arguments");
        }

        if (connector.createTable(args[1]) == null) {
            return args[1] + " exists";
        } else {
            return "created";
        }
    }

    @Override
    public String toString() {
        return "create";
    }
}
