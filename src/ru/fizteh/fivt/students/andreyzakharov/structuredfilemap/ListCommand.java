package ru.fizteh.fivt.students.andreyzakharov.structuredfilemap;

import java.util.List;

public class ListCommand implements Command {
    @Override
    public String execute(DbConnector connector, String... args) throws CommandInterruptException {
        if (args.length > 1) {
            throw new CommandInterruptException("list: too many arguments");
        }
        if (connector.activeTable == null) {
            throw new CommandInterruptException("no table");
        }
        List<String> keys = connector.activeTable.list();
        if (keys.isEmpty()) {
            return "";
        } else {
            return String.join(", ", keys);
        }
    }
}
