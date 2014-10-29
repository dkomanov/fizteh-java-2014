package ru.fizteh.fivt.students.PotapovaSofia.MultiFileHashMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Vector;

public class CmdCreate implements Command {
    @Override
    public void execute(Vector<String> args, DataBase db) throws IOException {
        String tableName = args.get(1);
        Path tablePath = db.getDbPath().resolve(tableName);
        if (db.getDataBase().containsKey(tableName)) {
            System.out.println(tableName + " exists");
        } else {
            String tableToCreatePath = db.getDbPath().toString() + File.separator + tableName;
            File tableDirectory = new File(tableToCreatePath);
            if (!tableDirectory.mkdir()) {
                throw new IOException("Can't create table directory");
            }
            System.out.println("created");
            db.getDataBase().put(tableName, new Table(tablePath));
        }
    }
}
