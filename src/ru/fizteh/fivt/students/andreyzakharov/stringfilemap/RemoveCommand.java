package ru.fizteh.fivt.students.andreyzakharov.stringfilemap;

public class RemoveCommand implements Command {
    @Override
    public String execute(DbConnector connector, String... args) throws CommandInterruptException {
        if (args.length < 2) {
            throw new CommandInterruptException("remove: too few arguments");
        }
        if (args.length > 2) {
            throw new CommandInterruptException("remove: too many arguments");
        }
        if (connector.activeTable == null) {
            throw new CommandInterruptException("no table");
        }
        boolean exists = connector.activeTable.containsKey(args[1]);
        if (exists) {
            connector.activeTable.remove(args[1]);
            return "removed";
        } else {
            return "not found";
        }
    }
}
