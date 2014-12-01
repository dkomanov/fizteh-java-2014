package ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap;

import ru.fizteh.fivt.students.EgorLunichkin.filemap.DataBase;
import ru.fizteh.fivt.students.EgorLunichkin.filemap.PutCommand;

import java.io.File;

public class MultiPutCommand implements Command {
    public MultiPutCommand(MultiDataBase mdb, String key, String value) {
        this.key = key;
        this.value = value;
        this.multiDataBase = mdb;
    }

    public MultiPutCommand(String key, String value) {
        this.key = key;
        this.value = value;
        this.multiDataBase = null;
    }

    private String key;
    private String value;
    private MultiDataBase multiDataBase;

    public void run() throws Exception {
        if (multiDataBase.using == null) {
            System.out.println("no table");
        } else {
            this.runOnTable(multiDataBase.getUsing());
        }
    }

    public void runOnTable(Table table) throws Exception {
        int hashCode = Math.abs(key.hashCode());
        int dir = hashCode % 16;
        int file = hashCode / 16 % 16;
        if (table.dataBases[dir][file] == null) {
            File subDirectory = new File(table.tableDirectory, String.valueOf(dir) + ".dir");
            if (!subDirectory.exists() && !subDirectory.mkdir()) {
                throw new MultiFileHashMapException("Unable to create directory");
            }
            File dbFile = new File(subDirectory, String.valueOf(file) + ".dat");
            if (!dbFile.exists() && !dbFile.createNewFile()) {
                throw new MultiFileHashMapException("Unable to create file");
            }
            table.dataBases[dir][file] = new DataBase(dbFile.toString());
        }
        DataBase dataBase = table.dataBases[dir][file];
        new PutCommand(dataBase, key, value).run();
    }
}
