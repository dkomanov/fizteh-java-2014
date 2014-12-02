package ru.fizteh.fivt.students.EgorLunichkin.filemap;

public class GetCommand implements Command {
    public GetCommand(DataBase db, String key) {
        this.key = key;
        this.dataBase = db;
    }
    
    private String key;
    private DataBase dataBase;

    public void run() {
        if (!dataBase.getDataBase().containsKey(key)) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(dataBase.getDataBase().get(key));
        }
    }
}
