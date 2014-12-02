package ru.fizteh.fivt.students.oscar_nasibullin.MultiFileHashMap;


import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DataBase {

    private static Map<String, Table> tables;
    private static String usingTable;

    public DataBase() throws Exception {
        tables = new TreeMap<>();
        usingTable = "";
        String dbDir = System.getProperty("fizteh.db.dir");

        try {
            if (!Paths.get(dbDir).toFile().exists()) {
                throw new Exception("not found");
            }
        } catch (Exception e) {
            throw new FileNotFoundException("database root directory error: " + e.getMessage());
        }
        File[] tableFiles = Paths.get(dbDir).toFile().listFiles();
        for (File newTableFile : tableFiles) {
            if (newTableFile.isDirectory()) {
                List<String> args = new ArrayList<>();
                args.add("create");
                args.add(newTableFile.getName());
                create(args);
            }
        }
    }

    public Table getTable() throws Exception {
        if (!tables.containsKey(usingTable)) {
            throw new Exception("no table");
        }
        return tables.get(usingTable);
    }

    public final String create(final List<String> args) throws Exception {
        if (args.size() != 2) {
            throw new IllegalArgumentException("Illegal arguments for create");
        }
        String resultMessage;
        if (tables.containsKey(args.get(1))) {
            resultMessage = args.get(1) + " exists";
        } else {
            tables.put(args.get(1), new Table(args.get(1)));
            resultMessage = "created";
        }

        return resultMessage;
    }

    public final String drop(final List<String> args)
            throws Exception {
        if (args.size() != 2) {
            throw new IllegalArgumentException("Illegal arguments for drop");
        }
        String resultMessage;
        if (!tables.containsKey(args.get(1))) {
            resultMessage = args.get(1) + " not exists";
        } else {
            tables.get(args.get(1)).clear();
            tables.remove(args.get(1));
            resultMessage = "dropped";
        }
        return resultMessage;
    }

    public final String use(final List<String> args)
            throws Exception {
        if (args.size() != 2) {
            throw new IllegalArgumentException("Illegal arguments for use");
        }
        String resultMessage;
        if (tables.containsKey(args.get(1))) {
            if (tables.containsKey(usingTable)) {
                tables.get(usingTable).close();
            }
            usingTable = args.get(1);
            tables.get(usingTable).open();
            resultMessage = "using " + usingTable;
        } else {
            resultMessage = args.get(1) + " not exists";
        }

        return resultMessage;
    }

    public final String showTables(final List<String> args)
            throws Exception {
        if (args.size() != 2) {
            throw new IllegalArgumentException(
                    "Illegal arguments for show tables");
        }
        String resultMessage = "";
        for (Map.Entry<String, Table> entry : tables.entrySet()) {
            if (!entry.getValue().name.equals(usingTable)) {
                entry.getValue().open();
            }
            if (!entry.getValue().name.equals("") && entry.getValue().name != null) {
                resultMessage += entry.getValue().name + " " + entry.getValue().size() + "\n";
            }
            if (!entry.getValue().name.equals(usingTable)) {
                entry.getValue().close();
            }
        }
        if (resultMessage.equals("")) {
            resultMessage = "no tables found";
        } else {
            resultMessage = resultMessage.substring(0, resultMessage.length() - 1);
        }
        return resultMessage;
    }


    public void close() throws Exception {
        tables.get(usingTable).close();
    }
}
