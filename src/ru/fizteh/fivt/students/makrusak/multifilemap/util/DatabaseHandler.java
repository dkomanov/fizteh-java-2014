package multifilemap.util;

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

    public String work(String[] tokens) throws IOException {
        if (tokens.length == 2 && tokens[0].equals("create")) {
            if (tableHandlers.containsKey(tokens[1])) {
                return tokens[1] + " exists";
            } else {
                File dir = new File(path, tokens[1]);
                dir.mkdirs();
                tableHandlers.put(dir.getName(), new TableHandler(dir));
                return "created";
            }
        } else if (tokens.length == 2 && tokens[0].equals("drop")) {
            if (!tableHandlers.containsKey(tokens[1])) {
                return tokens[1] + " not exists";
            } else {
                File dir = new File(path, tokens[1]);
                for (File binDir : dir.listFiles()) {
                    for (File bin : binDir.listFiles()) {
                        bin.delete();
                    }
                    binDir.delete();
                }
                dir.delete();
                tableHandlers.remove(tokens[1]);
                if (currentTable != null && currentTable.equals(tokens[1])) {
                    currentTable = null;
                }
                return "dropped";
            }
        } else if (tokens.length == 1 && tokens[0].equals("exit")) {
            System.exit(0);
        } else if (tokens.length == 2 && tokens[0].equals("use")) {
            if (!tableHandlers.containsKey(tokens[1])) {
                return tokens[1] + " not exists";
            } else {
                currentTable = tokens[1];
                return "using " + currentTable;
            }
        } else if (tokens.length == 2 && tokens[0].equals("show")
                && tokens[1].equals("tables")) {
            String res = "";
            for (Map.Entry<String, TableHandler> entry : tableHandlers
                    .entrySet()) {
                res += entry.getKey() + " " + entry.getValue().getCount()
                        + "\n";
            }
            return res;
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
