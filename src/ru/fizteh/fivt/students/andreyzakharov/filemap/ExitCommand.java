package ru.fizteh.fivt.students.andreyzakharov.filemap;

public class ExitCommand implements Command {
    @Override
    public String execute(DbConnector connector, String... args) {
        connector.db.unload();
        System.exit(0);
        return null; // silly Java
    }
}
