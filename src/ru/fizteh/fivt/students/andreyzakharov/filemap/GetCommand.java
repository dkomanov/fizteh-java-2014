package ru.fizteh.fivt.students.andreyzakharov.filemap;

public class GetCommand implements Command {
    @Override
    public String execute(DbConnector connector, String... args) {
        String value = connector.db.get(args[1]);
        return value == null ? "not found" : "found\n"+value;
    }
}
