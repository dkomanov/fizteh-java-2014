package ru.fizteh.fivt.students.alexpodkin.Storeable;

import ru.fizteh.fivt.storage.structured.Storeable;

import java.io.*;
import java.text.ParseException;
import java.util.HashMap;

public class MultiLauncher {

    private final int mod = 16;
    private String parentDir;
    private String currentTable = null;
    private Reader reader;
    private ru.fizteh.fivt.students.alexpodkin.Storeable.Writer writer;
    private boolean exitFlag = false;
    private HashMap<String, Integer> tableNames;
    private PrintStream printStream = System.out;
    private StoreableTableProvider storeableTableProvider;
    private StoreableTable storeableTable;
    private HashMap<String, Storeable> table;
    private int uncommitedChanches = 0;

    public MultiLauncher(HashMap<String, Integer> tables, String path) {
        tableNames = tables;
        parentDir = path;
    }

    public boolean launch(String[] arguments) throws IOException {
        if (arguments.length == 0) {
            return exec(System.in, false);
        } else {
            StringBuilder builder = new StringBuilder();
            for (String argument : arguments) {
                builder.append(argument);
                builder.append(" ");
            }
            String request = builder.toString().replaceAll(";", "\n");
            InputStream inputStream = new ByteArrayInputStream(request.getBytes("UTF-8"));
            return exec(inputStream, true);
        }
    }

    private boolean checkTable(String name) {
        File dircheck = new File(parentDir + File.separator + name);
        if (!dircheck.exists() || !dircheck.isDirectory()) {
            return false;
        }
        for (int i = 0; i < 16; i++) {
            File subdircheck = new File(dircheck.getAbsolutePath() + File.separator + Integer.toString(i) + ".dir");
            if (!subdircheck.exists() || !subdircheck.isDirectory()) {
                return false;
            }
        }
        return true;
    }

    public boolean removeData(File toRemove) {
        if (toRemove.isDirectory()) {
            File[] files = toRemove.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (!removeData(file)) {
                        return false;
                    }
                }
            }
        }
        try {
            if (!toRemove.delete()) {
                throw new RuntimeException(toRemove.getAbsolutePath() + ": couldn't remove file");
            }
            return true;
        } catch (SecurityException e) {
            throw new RuntimeException(toRemove.getAbsolutePath() + ": couldn't remove file");
        }
    }

    private void writeData() {
        removeData(new File(parentDir));
        try {
            HashMap<String, Storeable>[][] maps = new HashMap[mod][mod];
            for (int i = 0; i < mod; i++) {
                for (int j = 0; j < mod; j++) {
                    maps[i][j] = new HashMap<>();
                }
            }
            for (String key : table.keySet()) {
                int hash = key.hashCode();
                int dirNum = (hash % mod + mod) % mod;
                int fNum = (hash / mod % mod + mod) % mod;
                maps[dirNum][fNum].put(key, table.get(key));
            }
            for (int i = 0; i < mod; i++) {
                String path = parentDir + File.separator + Integer.toString(i) + ".dir";
                File subDir = new File(path);
                for (int j = 0; j < mod; j++) {
                    String subPath = path + File.separator + Integer.toString(j) + ".dat";
                    File file = new File(subPath);
                    writer = new Writer(subPath, storeableTableProvider, storeableTableProvider.getTable(currentTable));
                    writer.writeDataToFile(maps[i][j], subPath);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Errors in writing");
        }
    }

    private boolean checkSubDirectory(File file) {
        for (File subFile : file.listFiles()) {
            if (subFile.isDirectory() || !checkName(subFile.getName(), ".dat")) {
                return false;
            }
        }
        return true;
    }

    private boolean checkName(String name, String s) {
        try {
            if (name.length() < 4) {
                return false;
            }
            if (!name.endsWith(s)) {
                return false;
            }
            int num = Integer.parseInt(name.replace(s, ""));
            if (num < 0 || num > 16) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private void readData() {
        table = new HashMap<>();
        File tableDir = new File(parentDir);
        for (File file : tableDir.listFiles()) {
            if ("signature.tsv".equals(file.getName())) {
                continue;
            }
            if (!file.isDirectory() || !checkName(file.getName(), ".dir") || !checkSubDirectory(file)) {
                throw new RuntimeException("Wrong data");
            }
            for (File subFile : file.listFiles()) {
                reader = new Reader(subFile.getAbsolutePath(),
                        storeableTableProvider, storeableTableProvider.getTable(currentTable));
                try {
                    HashMap<String, Storeable> subTable = reader.readDataFromFile();
                    for (HashMap.Entry<String, Storeable> entry : subTable.entrySet()) {
                        table.put(entry.getKey(), entry.getValue());
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Bad reading");
                }
            }
        }
    }

    private boolean exec(InputStream inputStream, boolean isPackage) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        while (true) {
            String request = bufferedReader.readLine();
            if (request == null) {
                break;
            }

            String[] arguments = request.split(" ");
            if (arguments.length == 0 || arguments[0].equals("")) {
                continue;
            }

            boolean commandCheck;

            switch (arguments[0]) {
                case "put":
                    commandCheck = putCommand(arguments);
                    break;
                case "get":
                    commandCheck = getCommand(arguments);
                    break;
                case "list":
                    commandCheck = listCommand(arguments);
                    break;
                case "remove":
                    commandCheck = removeCommand(arguments);
                    break;
                case "create":
                    commandCheck = createCommand(arguments);
                    break;
                case "drop":
                    commandCheck = dropCommand(arguments);
                    break;
                case "use":
                    commandCheck = useCommand(arguments);
                    break;
                case "show":
                    commandCheck = showCommand(arguments);
                    break;
                case "size":
                    commandCheck = sizeCommand(arguments);
                    break;
                case "commit":
                    commandCheck = commitCommand(arguments);
                    break;
                case "rollback":
                    commandCheck = rollbackCommand(arguments);
                    break;
                default:
                    commandCheck = exitCommand(arguments);
            }

            if (exitFlag) {
                return true;
            }

            if (!commandCheck && isPackage) {
                exitFlag = true;
                return false;
            }
        }
        exitFlag = true;
        return true;
    }

    private boolean sizeCommand(String[] arguments) {
        if (arguments.length != 1) {
            return false;
        }
        if (currentTable == null) {
            printStream.println("no table");
            return false;
        }
        printStream.println(table.size());
        return true;
    }

    private boolean putCommand(String[] arguments) {
        if (arguments.length != 3) {
            return false;
        }
        if (currentTable == null) {
            printStream.println("no table");
            return false;
        }
        if (table.containsKey(arguments[1])) {
            printStream.print("overwrite\n" + table.get(arguments[1]) + "\n");
            table.remove(arguments[1]);
        } else {
            int num = tableNames.get(currentTable);
            tableNames.remove(currentTable);
            tableNames.put(currentTable, num + 1);
            printStream.print("new\n");
            uncommitedChanches++;
        }
        try {
            table.put(arguments[1], storeableTableProvider.deserialize(storeableTable, arguments[2]));
        } catch (ParseException e) {
            throw new IllegalArgumentException("Bad data");
        }
        return true;
    }

    private boolean getCommand(String[] arguments) {
        if (arguments.length != 2) {
            return false;
        }
        if (currentTable == null) {
            printStream.println("no table");
            return false;
        }
        if (table.containsKey(arguments[1])) {
            printStream.print("found\n" + table.get(arguments[1]) + "\n");
        } else {
            printStream.print("not found\n");
        }
        return true;
    }

    private boolean removeCommand(String[] arguments) {
        if (arguments.length != 2) {
            return false;
        }
        if (currentTable == null) {
            printStream.println("no table");
            return false;
        }
        if (table.containsKey(arguments[1])) {
            table.remove(arguments[1]);
            int num = tableNames.get(currentTable);
            tableNames.remove(currentTable);
            tableNames.put(currentTable, num - 1);
            printStream.print("removed\n");
            uncommitedChanches++;
        } else {
            printStream.print("not found\n");
        }
        return true;
    }

    private boolean createCommand(String[] arguments) throws IOException {
        if (arguments.length != 2) {
            return false;
        }
        if (tableNames != null && tableNames.containsKey(arguments[1])) {
            printStream.println(arguments[1] + " exists");
        }
        File table = new File(parentDir + File.separator + arguments[1]);
        if (!table.mkdir()) {
            return false;
        }
        for (int i = 0; i < 16; i++) {
            File subdir = new File(table.getAbsolutePath() + File.separator + Integer.toString(i) + ".dir");
            if (!subdir.mkdir()) {
                return false;
            }
            for (int j = 0; j < 16; j++) {
                File data = new File(subdir.getAbsolutePath() + File.separator + Integer.toString(j) + ".dat");
                if (!data.createNewFile()) {
                    return false;
                }
            }
        }
        tableNames.put(arguments[1], 0);
        printStream.println("created");
        return true;
    }

    private boolean recursiveRemove(File toRemove) {
        if (toRemove.isDirectory()) {
            File[] files = toRemove.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (!recursiveRemove(file)) {
                        return false;
                    }
                }
            }
        }
        try {
            if (!toRemove.delete()) {
                return false;
            }
            return true;
        } catch (SecurityException e) {
            System.err.println("Security exeption");
        }
        return false;
    }

    private boolean dropCommand(String[] arguments) {
        if (arguments.length != 2) {
            return false;
        }
        if (tableNames == null || !tableNames.containsKey(arguments[1])) {
            printStream.println(arguments[1] + " not exists");
            return false;
        }
        if (!recursiveRemove(new File(parentDir + File.separator + arguments[1]))) {
            return false;
        }
        tableNames.remove(arguments[1]);
        printStream.println("dropped");
        return true;
    }

    private boolean showCommand(String[] arguments) throws IOException {
        if (arguments.length != 2) {
            return false;
        }
        if (!arguments[1].equals("tables")) {
            return false;
        }
        printStream.println("table_name row_count");
        if (tableNames == null) {
            return true;
        }
        for (HashMap.Entry<String, Integer> name : tableNames.entrySet()) {
            printStream.println(name.getKey() + " " + name.getValue());
        }
        return true;
    }

    private boolean useCommand(String[] arguments) throws IOException {
        if (arguments.length != 2) {
            return false;
        }
        if (tableNames == null || !tableNames.containsKey(arguments[1])) {
            printStream.println(arguments[1] + " not exists");
            return false;
        }
        if (currentTable != null) {
            int cc = uncommitedChanches;
            if (cc != 0) {
                printStream.println(cc + "unsaved changes");
                return false;
            }
        }
        currentTable = arguments[1];
        checkTable(arguments[1]);
        readData();
        printStream.println("using " + arguments[1]);
        return true;
    }

    private boolean listCommand(String[] arguments) {
        if (arguments.length != 1) {
            return false;
        }
        if (currentTable == null || table == null) {
            printStream.println("no table");
            return false;
        }
        for (String key : table.keySet()) {
            printStream.print(key + " ");
        }
        printStream.print("\n");
        return true;
    }

    private boolean commitCommand(String[] arguments) throws IOException {
        if (arguments.length != 1) {
            return false;
        }
        if (currentTable == null) {
            return false;
        }
        int cc = uncommitedChanches;
        printStream.println(cc);
        uncommitedChanches = 0;
        writeData();
        return true;
    }

    private boolean rollbackCommand(String[] arguments) {
        if (arguments.length != 1) {
            return false;
        }
        if (currentTable == null) {
            return false;
        }
        int cc = uncommitedChanches;
        printStream.println(cc);
        uncommitedChanches = 0;
        readData();
        return true;
    }

    private boolean exitCommand(String[] arguments) {
        if (arguments.length != 1) {
            return false;
        }
        exitFlag = true;
        return true;
    }
}
