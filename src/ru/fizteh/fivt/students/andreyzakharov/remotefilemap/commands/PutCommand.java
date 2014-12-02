package ru.fizteh.fivt.students.andreyzakharov.remotefilemap.commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.andreyzakharov.remotefilemap.CommandInterruptException;
import ru.fizteh.fivt.students.andreyzakharov.remotefilemap.MultiFileTableProvider;

import java.text.ParseException;

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
        try {
            String key = args[1];
            Storeable value = connector.deserialize(connector.getCurrent(), args[2]);
            Storeable oldValue = connector.getCurrent().put(key, value);
            if (oldValue == null) {
                return "new";
            } else {
                return "overwrite\n" + connector.serialize(connector.getCurrent(), oldValue);
            }
        } catch (ParseException e) {
            return "wrong type (" + e.getMessage() + ")";
        }
    }

    @Override
    public String toString() {
        return "put";
    }
}
