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

    private String key;
    private String value;
    private MultiDataBase multiDataBase;

    public void run() throws Exception {
        if (multiDataBase.getUsing().equals(null)) {
            System.out.println("no table");
        } else {
            int hashCode = Math.abs(key.hashCode());
            int dir = hashCode % 16;
            int file = hashCode / 16 % 16;
            if (multiDataBase.getUsing().dataBases[dir][file] == null) {
                File subDirectory = new File(multiDataBase.getUsing().tableDirectory, String.valueOf(dir) + ".dir");
                if (!subDirectory.exists() && !subDirectory.mkdir()) {
                    throw new MultiFileHashMapException("Unable to create directory");
                }
                File dbFile = new File(subDirectory, String.valueOf(file) + ".dat");
                if (!dbFile.exists() && !dbFile.createNewFile()) {
                    throw new MultiFileHashMapException("Unable to create file");
                }
                multiDataBase.getUsing().dataBases[dir][file] = new DataBase(dbFile.toString());
            }
            DataBase dataBase = multiDataBase.getUsing().dataBases[dir][file];
            new PutCommand(dataBase, key, value);
        }
    }
}
