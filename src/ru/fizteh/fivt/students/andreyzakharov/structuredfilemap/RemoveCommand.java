package ru.fizteh.fivt.students.andreyzakharov.structuredfilemap;

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
        String old = connector.activeTable.remove(args[1]);
        if (old != null) {
            return "removed";
        } else {
            return "not found";
        }
    }
}
