package ru.fizteh.fivt.students.andreyzakharov.multifilehashmap;

import java.nio.file.Files;

public class ShowCommand implements Command {
    @Override
    public String execute(DbConnector connector, String... args) throws CommandInterruptException {
        if (args.length < 2) {
            throw new CommandInterruptException("show: too few arguments");
        }
        if (args.length > 2) {
            throw new CommandInterruptException("show: too many arguments");
        }

        if (!args[1].equals("tables")) {
            throw new CommandInterruptException("show: unknown argument");
        }

        return "";
    }
}
