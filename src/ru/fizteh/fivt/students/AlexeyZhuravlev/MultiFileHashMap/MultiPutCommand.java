package ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap;

import ru.fizteh.fivt.students.AlexeyZhuravlev.filemap.DataBase;
import ru.fizteh.fivt.students.AlexeyZhuravlev.filemap.PutCommand;

import java.io.File;

/**
 * @author AlexeyZhuravlev
 */
public class MultiPutCommand extends Command {

    private String key;
    private String value;

    protected void putArguments(String[] args) {
        key = args[1];
        value = args[2];
    }

    protected int numberOfArguments() {
        return 2;
    }

    @Override
    public void execute(DataBaseDir base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            int hashCode = Math.abs(key.hashCode());
            int dir = hashCode % 16;
            int file = hashCode / 16 % 16;
            PutCommand put = new PutCommand(key, value);
            if (base.getUsing().databases[dir][file] == null) {
                File subDir = new File(base.getUsing().mainDir, String.valueOf(dir) + ".dir");
                if (!subDir.exists()) {
                    if (!subDir.mkdir()) {
                        throw new Exception("Unable to create directories in working catalog");
                    }
                }
                File dbFile = new File(subDir, String.valueOf(file) + ".dat");
                if (!dbFile.exists()) {
                    if (!dbFile.createNewFile()) {
                        throw new Exception("Unable to create database files in working catalog");
                    }
                }
                base.getUsing().databases[dir][file] = new DataBase(dbFile.toString());
            }
            put.execute(base.getUsing().databases[dir][file]);
        }
    }
}
