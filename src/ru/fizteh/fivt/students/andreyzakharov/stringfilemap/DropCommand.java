package ru.fizteh.fivt.students.andreyzakharov.stringfilemap;

import java.io.IOException;
import java.nio.file.Files;

public class DropCommand implements Command {
    @Override
    public String execute(DbConnector connector, String... args) throws CommandInterruptException {
        if (args.length < 2) {
            throw new CommandInterruptException("drop: too few arguments");
        }
        if (args.length > 2) {
            throw new CommandInterruptException("drop: too many arguments");
        }

        FileMap table = connector.tables.get(args[1]);
        if (table == null) {
            return args[1] + " not exists";
        }
        if (connector.activeTable == table) {
            connector.activeTable = null;
        }
        connector.tables.remove(args[1]);

        try {
            table.delete();
        } catch (ConnectionInterruptException e) {
            throw new CommandInterruptException("drop: " + e.getMessage());
        }
        return "deleted";
    }
}
