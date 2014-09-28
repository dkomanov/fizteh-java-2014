package ru.fizteh.fivt.students.andreyzakharov.filemap;

public class RemoveCommand  implements Command{
    @Override
    public String execute(DbConnector connector, String... args) {
        boolean exists = connector.db.containsKey(args[1]);
        if (exists) {
            connector.db.remove(args[1]);
            return "removed";
        } else {
            return "not found";
        }
    }
}
