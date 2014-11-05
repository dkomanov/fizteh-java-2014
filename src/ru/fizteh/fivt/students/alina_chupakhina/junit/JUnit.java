package ru.fizteh.fivt.students.alina_chupakhina.junit;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

public class JUnit {
    public static String path;
    public static Map<String, BdTable> tableList;
    public static BdTable currentTable;
    public static PvTable pv;

    public static void main(final String[] args) {
        try {
            path = System.getProperty("fizteh.db.dir");
            if (path == null) {
                throw new Exception("Enter directory");
            }
            tableList = new TreeMap<>();
            File dir = new File(path);
            if (!dir.exists() || !dir.isDirectory()) {
                throw new Exception("directory not exist");
            }
            File[] children = dir.listFiles();
            for (File child : children) {
                TablePF pf = new TablePF();
                PvTable pv = pf.create(path);
                tableList.put(child.getName(), pv.getTable(child.getName()));
            }
            if (args.length > 0) {
                Mode.batch(args);
            } else {
                Mode.interactive();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }
}
