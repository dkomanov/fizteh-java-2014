package ru.fizteh.fivt.students.andreyzakharov.stringfilemap;

public class PutCommand implements Command {
    @Override
    public String execute(DbConnector connector, String... args) throws CommandInterruptException {
        if (args.length < 3) {
            throw new CommandInterruptException("put: too few arguments");
        }
        if (args.length > 3) {
            throw new CommandInterruptException("put: too many arguments");
        }
        if (connector.activeTable == null) {
            throw new CommandInterruptException("no table");
        }
        String value = connector.activeTable.get(args[1]);
        connector.activeTable.put(args[1], args[2]);
        return value == null ? "new" : "overwrite\n" + value;
    }
}
