package ru.fizteh.fivt.students.andreyzakharov.stringfilemap;

public class CreateCommand implements Command {
    @Override
    public String execute(DbConnector connector, String... args) throws CommandInterruptException {
        if (args.length < 2) {
            throw new CommandInterruptException("create: too few arguments");
        }
        if (args.length > 2) {
            throw new CommandInterruptException("create: too many arguments");
        }
        if (connector.tables.get(args[1]) != null) {
            return args[1] + " exists";
        }

        FileMap table = new FileMap(connector.dbRoot.resolve(args[1]));
        connector.tables.put(args[1], table);
        return "created";
    }
}
