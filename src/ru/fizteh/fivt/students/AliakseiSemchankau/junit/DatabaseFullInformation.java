package ru.fizteh.fivt.students.AliakseiSemchankau.junit;

import java.nio.file.Path;

/**
 * Created by Aliaksei Semchankau on 15.10.2014.
 */
public class DatabaseFullInformation {

    public Path pathDatabase;
    public Path currentTable;
    public String currentTableName;
    public boolean exitFlag;

    DatabaseFullInformation(Path path) {
        pathDatabase = path;
        currentTableName = null;
        currentTable = null;
        exitFlag = false;
    }
}
