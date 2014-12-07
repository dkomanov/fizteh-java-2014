package ru.fizteh.fivt.students.VasilevKirill.junit.multimap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.VasilevKirill.junit.multimap.db.shell.RmCommand;
import ru.fizteh.fivt.students.VasilevKirill.junit.multimap.db.shell.Status;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kirill on 19.10.2014.
 */
public class MultiMap implements TableProvider {
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

    @Override
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        return tables.get(name);
    }

    @Override
    public Table createTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        try {
            if (!addTable(name)) {
                return null;
            }
            MultiTable retTable = new MultiTable(new File(workingDirectory + File.separator + name));
            tables.put(name, retTable);
            return retTable;
        } catch (IOException e) {
            if (e.getMessage().substring(0, 5).equals("Can't")) {
                throw new IllegalArgumentException();
            }
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (tables.get(name) == null) {
            throw new IllegalStateException();
        }
        try {
            oldRemoveTable(name);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void setTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (tables.get(name) == null) {
            throw new IllegalStateException();
        } else {
            try {
                if (workingTable != null) {
                    MultiTable currentTable = tables.get(workingTable);
                    if (currentTable == null) {
                        throw new IOException("Multimap: current table is null");
                    }
                    if (currentTable.getNumUnsavedChanges() != 0) {
                        System.out.println(currentTable.getNumUnsavedChanges() + " unsaved changes");
                    }
                }
                setWorkingTable(name);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    //Old version of method. Saved for compatibility.
    public boolean addTable(String name) throws IOException {
        if (name == null) {
            throw new IOException("Wrong arguments");
        }
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

    //Old version of method. Saved for compatibility.
    public boolean oldRemoveTable(String name) throws IOException {
        if (name == null) {
            throw new IOException("Wrong argument");
        }
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

    //Old version of method. Saved for compatibility.
    public boolean setWorkingTable(String name) throws IOException {
        if (name == null) {
            throw new IOException("Wrong argument");
        }
        if (tables.containsKey(name)) {
            workingTable = name;
            return true;
        } else {
            return false;
        }
    }

    //Old version of method. Saved for compatibility.
    public boolean handleTable(String[] args) throws IOException {
        if (workingTable == null) {
            return false;
        }
        MultiTable multiTable = tables.get(workingTable);
        if (multiTable == null) {
            throw new IOException("Unknown error");
        }
        String result = "";
        switch (args[0]) {
            case "put":
                result = multiTable.put(args[1], args[2]);
                if (result == null) {
                    System.out.println("new");
                } else {
                    System.out.println("overwrite\n" + result);
                }
                break;
            case "get":
                result = multiTable.get(args[1]);
                if (result == null) {
                    System.out.println("not found");
                } else {
                    System.out.println("found\n" + result);
                }
                break;
            case "remove":
                result = multiTable.remove(args[1]);
                if (result == null) {
                    System.out.println("not found");
                } else {
                    System.out.println("removed");
                }
                break;
            default:
                multiTable.handle(args);
        }
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

    public MultiTable getMultiTable(String name) {
        return tables.get(name);
    }
}
