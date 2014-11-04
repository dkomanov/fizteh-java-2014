package ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.commands;

import ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.CommandInterruptException;
import ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.MultiFileTableProvider;

import java.util.List;

public class ListCommand implements Command {
    @Override
    public String execute(MultiFileTableProvider connector, String... args) throws CommandInterruptException {
        if (args.length > 1) {
            throw new CommandInterruptException("list: too many arguments");
        }
        if (connector.getCurrent() == null) {
            throw new CommandInterruptException("no table");
        }
        List<String> keys = connector.getCurrent().list();
        if (keys.isEmpty()) {
            return "";
        } else {
            return String.join(", ", keys);
        }
    }

    @Override
    public String toString() {
        return "list";
    }
}
