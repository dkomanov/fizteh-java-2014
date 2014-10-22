package ru.fizteh.fivt.students.irina_karatsapova.filemap.database;

import ru.fizteh.fivt.students.irina_karatsapova.filemap.utils.DataBaseException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBase {
    public static File file = new File(System.getProperty("db.file"));
    public static Map<String, String> map = new HashMap<String, String>();
    public static List<String> keys = new ArrayList<String>();

    public static void init() {
        try {
            initFile();
        } catch (Exception e) {
            System.err.println("Incorrect database: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void initFile() throws DataBaseException, IOException {
        if (System.getProperty("db.file") == null) {
            throw new DataBaseException("Path to the database is not set up. Use -Ddb.File=...");
        } else {
            DataBase.file = new File(System.getProperty("db.file"));
        }

        try {
            if (!DataBase.file.exists()) {
                DataBase.file.createNewFile();
            }
        } catch (IOException e) {
            throw new DataBaseException("Can't create main file");
        }

        if (!DataBase.file.isFile()) {
            throw new DataBaseException("Database should be a file, not a directory");
        }
    }
}
