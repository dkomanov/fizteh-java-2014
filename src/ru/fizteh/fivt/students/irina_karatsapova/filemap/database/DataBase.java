package ru.fizteh.fivt.students.irina_karatsapova.filemap.database;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBase {
    public static File file = new File(System.getProperty("db.file"));
    public static Map<String, String> map = new HashMap<String, String>();
    public static List<String> keys = new ArrayList<String>();
}
