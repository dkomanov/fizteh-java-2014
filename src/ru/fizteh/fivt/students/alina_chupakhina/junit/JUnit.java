package ru.fizteh.fivt.students.alina_chupakhina.junit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

public class JUnit {
    public static String path;
    public static Map<String, Table> tableList;
    public static BdTable currentTable;
    public static TableProvider pv;

    public static void main(final String[] args) {
        try {
            path = "C:\\Ololo";
            TablePF pf = new TablePF();
            pv = pf.create(path);
            if (path == null) {
                throw new Exception("Enter directory");
            }
            tableList = new TreeMap<>();
            File dir = new File(path);
            if (!dir.exists() || !dir.isDirectory()) {
                throw new Exception("directory not exist");
            }
            File[] children = dir.listFiles();
            if (children != null) {
                for (File child : children) {
                    if (child.isDirectory()) {
                        tableList.put(child.getName(), pv.getTable(child.getName()));
                    }
                }
            }
            Mode mode = new Mode();
            if (args.length > 0) {
                mode.batch(args);
            } else {
                mode.interactive();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }
}
