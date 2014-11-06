package ru.fizteh.fivt.students.andreyzakharov.parallelfilemap.commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.andreyzakharov.parallelfilemap.CommandInterruptException;
import ru.fizteh.fivt.students.andreyzakharov.parallelfilemap.MultiFileTableProvider;

public class GetCommand implements Command {
    @Override
    public String execute(MultiFileTableProvider connector, String... args) throws CommandInterruptException {
        if (args.length < 2) {
            throw new CommandInterruptException("get: too few arguments");
        }
        if (args.length > 2) {
            throw new CommandInterruptException("get: too many arguments");
        }
        if (connector.getCurrent() == null) {
            throw new CommandInterruptException("no table");
        }
        Storeable value = connector.getCurrent().get(args[1]);
        if (value == null) {
            return "not found";
        } else {
            return "found\n" + connector.serialize(connector.getCurrent(), value);
        }
    }

    @Override
    public String toString() {
        return "get";
    }
}
