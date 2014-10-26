package ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.commands;

import ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.CommandInterruptException;
import ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.MultiFileTableProvider;

public class DropCommand implements Command {
    @Override
    public String execute(MultiFileTableProvider connector, String... args) throws CommandInterruptException {
        if (args.length < 2) {
            throw new CommandInterruptException("drop: too few arguments");
        }
        if (args.length > 2) {
            throw new CommandInterruptException("drop: too many arguments");
        }

        try {
            connector.removeTable(args[1]);
        } catch (IllegalStateException e) {
            return args[1] + " not exists";
        }
        return "deleted";
    }
}
