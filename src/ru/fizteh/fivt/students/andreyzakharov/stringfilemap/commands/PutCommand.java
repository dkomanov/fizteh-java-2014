package ru.fizteh.fivt.students.andreyzakharov.stringfilemap.commands;

import ru.fizteh.fivt.students.andreyzakharov.stringfilemap.CommandInterruptException;
import ru.fizteh.fivt.students.andreyzakharov.stringfilemap.MultiFileTableProvider;

public class PutCommand implements Command {
    @Override
    public String execute(MultiFileTableProvider connector, String... args) throws CommandInterruptException {
        if (args.length < 3) {
            throw new CommandInterruptException("put: too few arguments");
        }
        if (args.length > 3) {
            throw new CommandInterruptException("put: too many arguments");
        }
        if (connector.getCurrent() == null) {
            throw new CommandInterruptException("no table");
        }
        String value = connector.getCurrent().put(args[1], args[2]);
        return value == null ? "new" : "overwrite\n" + value;
    }

    @Override
    public String toString() {
        return "put";
    }
}
