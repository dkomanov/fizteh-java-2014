package ru.fizteh.fivt.students.andreyzakharov.filemap;

public class RemoveCommand implements Command {
    @Override
    public String execute(DbConnector connector, String... args) throws CommandInterruptException {
        if (args.length < 2) {
            throw new CommandInterruptException("remove: too few arguments");
        }
        if (args.length > 2) {
            throw new CommandInterruptException("remove: too many arguments");
        }
        boolean exists = connector.db.containsKey(args[1]);
        if (exists) {
            connector.db.remove(args[1]);
            return "removed";
        } else {
            return "not found";
        }
    }
}
