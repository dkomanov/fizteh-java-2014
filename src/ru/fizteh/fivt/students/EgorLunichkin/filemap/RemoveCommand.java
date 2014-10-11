package ru.fizteh.fivt.students.EgorLunichkin.filemap;

public class RemoveCommand implements Command {
    public RemoveCommand(DataBase _db, String _key) {
        this.key = _key;
        this.dataBase = _db;
    }

    private String key;
    private DataBase dataBase;

    public void run() throws FileMapException {
        if (!dataBase.getDataBase().containsKey(key)) {
            System.out.println("not found");
        }
        else {
            dataBase.getDataBase().remove(key);
            System.out.println("removed");
        }
        dataBase.writeDataBase();
    }
}
