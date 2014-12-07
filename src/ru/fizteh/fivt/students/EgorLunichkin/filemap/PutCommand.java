package ru.fizteh.fivt.students.EgorLunichkin.filemap;

public class PutCommand implements Command {
    public PutCommand(DataBase db, String key, String value) {
        this.key = key;
        this.value = value;
        this.dataBase = db;
    }

    private String key;
    private String value;
    private DataBase dataBase;

    public void run() throws Exception {
        if (!dataBase.getDataBase().containsKey(key)) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(dataBase.getDataBase().remove(key));
        }
        dataBase.getDataBase().put(key, value);
        dataBase.writeDataBase();
    }
}
