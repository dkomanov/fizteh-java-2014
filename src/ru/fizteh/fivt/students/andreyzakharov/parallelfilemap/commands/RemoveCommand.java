package ru.fizteh.fivt.students.andreyzakharov.parallelfilemap.commands;

import ru.fizteh.fivt.students.andreyzakharov.parallelfilemap.CommandInterruptException;
import ru.fizteh.fivt.students.andreyzakharov.parallelfilemap.MultiFileTableProvider;

public class RemoveCommand implements Command {
    @Override
    public String execute(MultiFileTableProvider connector, String... args) throws CommandInterruptException {
        if (args.length < 2) {
            throw new CommandInterruptException("remove: too few arguments");
        }
        if (args.length > 2) {
            throw new CommandInterruptException("remove: too many arguments");
        }
        if (connector.getCurrent() == null) {
            throw new CommandInterruptException("no table");
        }
        String old = connector.serialize(connector.getCurrent(), connector.getCurrent().remove(args[1]));
        if (old != null) {
            return "removed";
        } else {
            return "not found";
        }
    }

    @Override
    public String toString() {
        return "remove";
    }
}
