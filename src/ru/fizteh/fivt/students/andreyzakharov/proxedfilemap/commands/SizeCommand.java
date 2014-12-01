package ru.fizteh.fivt.students.andreyzakharov.proxedfilemap.commands;

import ru.fizteh.fivt.students.andreyzakharov.proxedfilemap.CommandInterruptException;
import ru.fizteh.fivt.students.andreyzakharov.proxedfilemap.MultiFileTableProvider;

public class SizeCommand implements Command {
    @Override
    public String execute(MultiFileTableProvider connector, String... args) throws CommandInterruptException {
        if (args.length > 1) {
            throw new CommandInterruptException("size: too many arguments");
        }
        if (connector.getCurrent() == null) {
            throw new CommandInterruptException("no table");
        }
        return Integer.toString(connector.getCurrent().size());
    }

    @Override
    public String toString() {
        return "size";
    }
}
