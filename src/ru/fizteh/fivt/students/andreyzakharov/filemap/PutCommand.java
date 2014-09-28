package ru.fizteh.fivt.students.andreyzakharov.filemap;

public class PutCommand implements Command {

    @Override
    public String execute(DbConnector connector, String... args) {
        String value = connector.db.get(args[1]);
        connector.db.put(args[1], args[2]);
        return value == null ? "new" : "overwrite\n"+value;
    }
}
