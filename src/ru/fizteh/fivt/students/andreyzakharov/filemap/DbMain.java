package ru.fizteh.fivt.students.andreyzakharov.filemap;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DbMain {
    public static void main(String[] args) {
        Path dbPath = Paths.get(System.getProperty("user.dir")).resolve(System.getProperty("db.file"));
        System.out.println(dbPath.toString());
        DbConnector conn = new DbConnector(dbPath);
    }
}
