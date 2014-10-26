package ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.commands;

import ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.CommandInterruptException;
import ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.MultiFileTable;
import ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.MultiFileTableProvider;

import java.util.HashMap;
import java.util.Map;

public class ShowCommand implements Command {
    @Override
    public String execute(MultiFileTableProvider connector, String... args) throws CommandInterruptException {
        if (args.length < 2) {
            throw new CommandInterruptException("show: too few arguments");
        }
        if (args.length > 2) {
            throw new CommandInterruptException("show: too many arguments");
        }
        if (!args[1].equals("tables")) {
            throw new CommandInterruptException("show: unknown argument");
        }
        Map<String, MultiFileTable> tables = connector.getAllTables();
        if (tables.isEmpty()) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (HashMap.Entry<String, MultiFileTable> e : tables.entrySet()) {
                sb.append(e.getKey());
                sb.append(' ');
                sb.append(e.getValue().size());
                sb.append('\n');
            }
            return sb.substring(0, sb.length() - 1);
        }
    }
}
