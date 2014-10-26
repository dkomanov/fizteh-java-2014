package ru.fizteh.fivt.students.andreyzakharov.structuredfilemap;

public class UseCommand implements Command {
    @Override
    public String execute(DbConnector connector, String... args) throws CommandInterruptException {
        if (args.length < 2) {
            throw new CommandInterruptException("use: too few arguments");
        }
        if (args.length > 2) {
            throw new CommandInterruptException("use: too many arguments");
        }

        FileMap table = connector.tables.get(args[1]);
        if (table != null) {
            if (connector.activeTable != null) {
                if (connector.activeTable.pending() > 0) {
                    return connector.activeTable.pending() + " unsaved changes";
                }
                try {
                    connector.activeTable.unload();
                } catch (ConnectionInterruptException e) {
                    throw new CommandInterruptException("use: can't unload active table");
                }
            }
            connector.activeTable = table;
            return "using " + args[1];
        } else {
            return args[1] + " not exists";
        }
    }
}
