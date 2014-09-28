package ru.fizteh.fivt.students.andreyzakharov.multifilehashmap;

public class UseCommand implements Command {
    @Override
    public String execute(DbConnector connector, String... args) throws CommandInterruptException {
        if (args.length < 2) {
            throw new CommandInterruptException("use: too few arguments");
        }
        if (args.length > 2) {
            throw new CommandInterruptException("use: too many arguments");
        }

        if (!connector.tableExists(args[1])) {
            return args[1]+" not exists";
        }
        connector.close();
        connector.db = new FileMap(connector.dbRoot.resolve(args[1]));
        try {
            connector.db.load();
            return "using "+args[1];
        } catch (ConnectionInterruptException e) {
            throw new CommandInterruptException("use: "+e.getMessage());
        }
    }
}
