package ru.fizteh.fivt.students.andreyzakharov.multifilehashmap;

public class ListCommand implements Command {
    @Override
    public String execute(DbConnector connector, String... args) throws CommandInterruptException {
        if (args.length > 1) {
            throw new CommandInterruptException("list: too many arguments");
        }
        if (connector.activeTable == null) {
            throw new CommandInterruptException("no table");
        }
        if (connector.activeTable.isEmpty()) {
            return "";
        } else {
            return String.join(", ", connector.activeTable.keySet());
        }
    }
}
