package ru.fizteh.fivt.students.andreyzakharov.multifilehashmap;

public class CreateCommand implements Command {
    @Override
    public String execute(DbConnector connector, String... args) throws CommandInterruptException {
        if (args.length < 2) {
            throw new CommandInterruptException("create: too few arguments");
        }
        if (args.length > 2) {
            throw new CommandInterruptException("create: too many arguments");
        }

        if (connector.tableExists(args[1])) {
            return args[1]+" exists";
        }

        connector.close();
        connector.db = new FileMap(connector.dbRoot.resolve(args[1]));
        try {
            connector.db.unload();
            return "created";
        } catch (ConnectionInterruptException e) {
            throw new CommandInterruptException("create: "+e.getMessage());
        }
    }
}
