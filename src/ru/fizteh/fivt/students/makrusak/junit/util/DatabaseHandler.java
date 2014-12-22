package junit.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHandler {
    private File path;
    private HashMap<String, TableHandler> tableHandlers;
    private String currentTable;

    public DatabaseHandler(File getPath) throws IOException {
        path = getPath;
        currentTable = null;
        tableHandlers = new HashMap<String, TableHandler>();
        if (!path.exists()) {
            path.mkdirs();
        } else if (!path.isDirectory()) {
            throw new IOException("Incorrect path to directory.");
        }
        if (path.listFiles() != null) {
            for (File inFile : path.listFiles()) {
                tableHandlers.put(inFile.getName(), new TableHandler(inFile));
            }
        }
    }

    public TableHandler getTableHandler(String key) {
        return tableHandlers.get(key);
    }

    private String size() {
        int res = 0;
        for (Map.Entry<String, TableHandler> entry : tableHandlers.entrySet()) {
            res += entry.getValue().getCount();
        }
        return Integer.toString(res);
    }

    public String create(String tableName) throws IOException {
        if (tableName == null) {
            throw new IOException();
        }
        if (tableHandlers.containsKey(tableName)) {
            return tableName + " exists";
        } else {
            File dir = new File(path, tableName);
            dir.mkdirs();
            tableHandlers.put(dir.getName(), new TableHandler(dir));
            return "created";
        }
    }

    public String drop(String tableName) {
        if (!tableHandlers.containsKey(tableName)) {
            return tableName + " not exists";
        } else {
            File dir = new File(path, tableName);
            for (File binDir : dir.listFiles()) {
                for (File bin : binDir.listFiles()) {
                    bin.delete();
                }
                binDir.delete();
            }
            dir.delete();
            tableHandlers.remove(tableName);
            if (currentTable != null && currentTable.equals(tableName)) {
                currentTable = null;
            }
            return "dropped";
        }
    }

    private String useTable(String tableName) {
        if (!tableHandlers.containsKey(tableName)) {
            return tableName + " not exists";
        } else {
            if (currentTable != null) {
                int commandsCount = tableHandlers.get(currentTable)
                        .getCommandsCount();
                if (commandsCount != 0) {
                    return Integer.toString(commandsCount) + " unsaved changes";
                }
            }
            currentTable = tableName;
            return "using " + currentTable;
        }
    }

    private String showTables() {
        String res = "";
        for (Map.Entry<String, TableHandler> entry : tableHandlers.entrySet()) {
            res += entry.getKey() + " " + entry.getValue().getCount() + "\n";
        }
        return res;
    }

    public String work(String[] tokens) throws IOException {
        if (tokens.length == 1 && tokens[0].equals("size")) {
            return size();
        } else if (tokens.length == 2 && tokens[0].equals("create")) {
            return create(tokens[1]);
        } else if (tokens.length == 2 && tokens[0].equals("drop")) {
            return drop(tokens[1]);
        } else if (tokens.length == 1 && tokens[0].equals("exit")) {
            System.exit(0);
        } else if (tokens.length == 2 && tokens[0].equals("use")) {
            return useTable(tokens[1]);
        } else if (tokens.length == 2 && tokens[0].equals("show")
                && tokens[1].equals("tables")) {
            return showTables();
        } else {
            if (currentTable == null) {
                return "no table";
            } else {
                return tableHandlers.get(currentTable).work(tokens);
            }
        }
        throw new IOException("Incorrect command");
    }
}
