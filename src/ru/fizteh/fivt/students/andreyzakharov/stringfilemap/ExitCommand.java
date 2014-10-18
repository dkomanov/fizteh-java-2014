package ru.fizteh.fivt.students.andreyzakharov.stringfilemap;

public class ExitCommand implements Command {
    @Override
    public String execute(DbConnector connector, String... args) throws CommandInterruptException {
        connector.close();
        System.exit(0);
        return null; // silly Java
    }
}
