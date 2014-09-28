package ru.fizteh.fivt.students.andreyzakharov.filemap;

public class GetCommand implements Command {
    @Override
    public String execute(DbConnector connector, String... args) throws CommandInterruptException {
        if (args.length < 2) {
            throw new CommandInterruptException("get: too few arguments");
        }
        if (args.length > 2) {
            throw new CommandInterruptException("get: too many arguments");
        }
        String value = connector.db.get(args[1]);
        return value == null ? "not found" : "found\n" + value;
    }
}
