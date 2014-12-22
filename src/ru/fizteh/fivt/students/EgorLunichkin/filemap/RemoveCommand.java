package ru.fizteh.fivt.students.EgorLunichkin.filemap;

public class RemoveCommand implements Command {
    public RemoveCommand(DataBase db, String key) {
        this.key = key;
        this.dataBase = db;
    }

    private String key;
    private DataBase dataBase;

    public void run() throws Exception {
        if (!dataBase.getDataBase().containsKey(key)) {
            //System.out.println("not found");
        } else {
            dataBase.getDataBase().remove(key);
            //System.out.println("removed");
        }
        dataBase.writeDataBase();
    }
}
