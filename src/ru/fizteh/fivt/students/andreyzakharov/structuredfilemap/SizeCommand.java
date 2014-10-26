package ru.fizteh.fivt.students.andreyzakharov.structuredfilemap;

public class SizeCommand implements Command {
    @Override
    public String execute(DbConnector connector, String... args) throws CommandInterruptException {
        if (args.length > 1) {
            throw new CommandInterruptException("size: too many arguments");
        }
        if (connector.activeTable == null) {
            throw new CommandInterruptException("no table");
        }
        return Integer.toString(connector.activeTable.size());
    }
}
