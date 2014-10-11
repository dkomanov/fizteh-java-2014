package ru.fizteh.fivt.students.EgorLunichkin.filemap;

public class GetCommand implements Command {
    public GetCommand(DataBase _db, String _key) {
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
            System.out.println("found");
            System.out.println(dataBase.getDataBase().get(key));
        }
    }
}
