package ru.fizteh.fivt.students.dnovikov.filemap;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DataBaseConnector {
    private Table table = null;

    public DataBaseConnector() throws NullPointerException, IOException {

        try {
            Path dataBasePath = Paths.get(System.getProperty("user.dir")).resolve(System.getProperty("db.file"));
            table = new SingleTable(dataBasePath);
        } catch (NullPointerException e) {
            throw new NullPointerException("database file not set");
        }
    }

    public Table getState() {
        return table;
    }

    public void loadTable() throws IOException {
        if (table != null) {
            table.load();
        }
    }

    public void saveTable() throws IOException {
        if (table != null) {
            table.save();
        }
    }

}
