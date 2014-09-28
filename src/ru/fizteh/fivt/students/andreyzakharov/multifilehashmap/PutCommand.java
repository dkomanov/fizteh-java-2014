package ru.fizteh.fivt.students.andreyzakharov.multifilehashmap;

public class PutCommand implements Command {
    @Override
    public String execute(DbConnector connector, String... args) throws CommandInterruptException {
        if (args.length < 3) {
            throw new CommandInterruptException("put: too few arguments");
        }
        if (args.length > 3) {
            throw new CommandInterruptException("put: too many arguments");
        }
        String value = connector.db.get(args[1]);
        connector.db.put(args[1], args[2]);
        return value == null ? "new" : "overwrite\n" + value;
    }
}
