package ru.fizteh.fivt.students.andreyzakharov.stringfilemap;

public class CommitCommand implements Command {
    @Override
    public String execute(DbConnector connector, String... args) throws CommandInterruptException {
        if (args.length > 1) {
            throw new CommandInterruptException("commit: too many arguments");
        }
        if (connector.activeTable == null) {
            throw new CommandInterruptException("no table");
        }
        return Integer.toString(connector.activeTable.commit());
    }
}
