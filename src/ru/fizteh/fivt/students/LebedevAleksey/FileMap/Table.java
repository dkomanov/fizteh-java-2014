package ru.fizteh.fivt.students.LebedevAleksey.FileMap;


import java.io.File;
import java.nio.file.Path;

public class Table {
    public Path getSaveFilePath(String key) {
        return new File(System.getProperty("db.file")).toPath();
   }


}
