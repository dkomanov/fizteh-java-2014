package ru.fizteh.fivt.students.andreyzakharov.multifilehashmap;

import java.util.HashMap;

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

        if (connector.tables.isEmpty()) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (HashMap.Entry<String, FileMap> e : connector.tables.entrySet()) {
                sb.append(e.getKey());
                sb.append(' ');
                sb.append(e.getValue().size());
                sb.append('\n');
            }
            return sb.substring(0, sb.length() - 1);
        }
    }
}
