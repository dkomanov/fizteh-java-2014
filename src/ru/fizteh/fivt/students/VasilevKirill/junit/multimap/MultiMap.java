package ru.fizteh.fivt.students.VasilevKirill.junit.multimap;

import ru.fizteh.fivt.students.VasilevKirill.junit.multimap.db.shell.RmCommand;
import ru.fizteh.fivt.students.VasilevKirill.junit.multimap.db.shell.Status;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kirill on 19.10.2014.
 */
public class MultiMap {
    private String workingDirectory;
    private String workingTable;
    private Map<String, MultiTable> tables;

    public MultiMap(String directory) throws IOException {
        workingDirectory = directory == null ? new File("").getCanonicalPath() : directory;
        File workingFile = new File(workingDirectory);
        if (!workingFile.exists()) {
            if (!workingFile.mkdir()) {
                throw new IOException("Can't create the directory");
            }
        }
        if (!workingFile.isDirectory()) {
            throw new IOException(directory + " is not a directory");
        }
        tables = new HashMap<String, MultiTable>();
        File[] tableDirectories = new File(workingDirectory).listFiles();
        for (File it : tableDirectories) {
            tables.put(it.getName(), new MultiTable(it));
        }
    }

    public boolean addTable(String name) throws IOException {
        if (name != null) {
            if (!tables.containsKey(name)) {
                File newDir = new File(workingDirectory + File.separator + name);
                if (!newDir.exists()) {
                    if (!newDir.mkdir()) {
                        throw new IOException("Can't create the directory: " + newDir.getName());
                    }
                }
                tables.put(name, new MultiTable(newDir));
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public boolean removeTable(String name) throws IOException {
        if (name != null) {
            if (tables.containsKey(name)) {
                String[] rmArgs = {"rm", "-r", tables.get(name).getTableDirectory().getAbsolutePath()};
                Status status = null;
                if (new RmCommand().execute(rmArgs, status) == 1) {
                    throw new IOException("Can't delete the table");
                }
                tables.remove(name);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public boolean setWorkingTable(String name) throws IOException {
        if (name != null) {
            if (tables.containsKey(name)) {
                workingTable = name;
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public boolean handleTable(String[] args) throws IOException {
        if (workingTable == null) {
            return false;
        }
        MultiTable multiTable = tables.get(workingTable);
        if (multiTable == null) {
            throw new IOException("Unknown error");
        }
        multiTable.handle(args);
        return true;
    }

    public void showTables() throws IOException {
        for (Map.Entry entry : tables.entrySet()) {
            Object value = entry.getValue();
            MultiTable multiTable = null;
            if (value instanceof MultiTable) {
                multiTable = (MultiTable) value;
            }
            if (multiTable != null) {
                System.out.println(entry.getKey() + " " + multiTable.getNumKeys());
            }
        }
    }
}
