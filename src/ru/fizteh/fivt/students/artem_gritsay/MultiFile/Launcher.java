package ru.fizteh.fivt.students.artem_gritsay.MultiFile;

import java.io.*;
import java.util.HashMap;
import java.util.Set;

public class Launcher {

    private boolean exitFlag = false;
    private HashMap<String, Integer> tableNames;
    private PrintStream printStream = System.out;
    private HashMap<String, String>[][] keysPath = null;
    private String parentDir;
    private String currentTable = null;
    private MultiReader reader;
    private MultiWrite writer;


    private boolean exec(InputStream inputStream, boolean isPackage) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        while (true) {
            String request = bufferedReader.readLine();
            if (request == null) {
                writeData();
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
                default:
                    commandCheck = exitCommand(arguments);
            }

            if (exitFlag) {
                writeData();
                return true;
            }

            if (!commandCheck && isPackage) {
                writeData();
                exitFlag = true;
                return false;
            }
        }
        writeData();
        exitFlag = true;
        return true;
    }

    public Launcher(HashMap<String, Integer> tables, String path) {
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

    private boolean writeData() throws IOException {
        if (currentTable == null) {
            return false;
        }
        if (keysPath == null) {
            return true;
        }
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                writer = new Writer(parentDir + File.separator + currentTable + File.separator
                        + Integer.toString(i) + ".dir" + File.separator + Integer.toString(j) + ".dat");
                writer.writeDataToFile(keysPath[i][j]);
            }
        }
        return true;
    }

    private boolean readData() throws IOException {
        keysPath = new HashMap[16][16];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                reader = new Reader(parentDir + File.separator + currentTable + File.separator
                        + Integer.toString(i) + ".dir" + File.separator + Integer.toString(j) + ".dat");
                keysPath[i][j] = reader.readDataFromFile();
            }
        }
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
        int hash = arguments[1].hashCode();
        int ndir = hash % 16;
        int nfile = hash / 16 % 16;
        if (keysPath[ndir][nfile].containsKey(arguments[1])) {
            printStream.print("overwrite\n" + keysPath[ndir][nfile].get(arguments[1]) + "\n");
            keysPath[ndir][nfile].remove(arguments[1]);
        } else {
            int num = tableNames.get(currentTable);
            tableNames.remove(currentTable);
            tableNames.put(currentTable, num + 1);
            printStream.print("new\n");
        }
        keysPath[ndir][nfile].put(arguments[1], arguments[2]);
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
        int hash = arguments[1].hashCode();
        int ndir = hash % 16;
        int nfile = hash / 16 % 16;
        if (keysPath[ndir][nfile].containsKey(arguments[1])) {
            printStream.print("found\n" + keysPath[ndir][nfile].get(arguments[1]) + "\n");
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
        int hash = arguments[1].hashCode();
        int ndir = hash % 16;
        int nfile = hash / 16 % 16;
        if (keysPath[ndir][nfile].containsKey(arguments[1])) {
            keysPath[ndir][nfile].remove(arguments[1]);
            int num = tableNames.get(currentTable);
            tableNames.remove(currentTable);
            tableNames.put(currentTable, num - 1);
            printStream.print("removed\n");
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
            writeData();
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
        if (currentTable == null || keysPath == null) {
            printStream.println("no table");
            return false;
        }
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                Set<String> keySet = keysPath[i][j].keySet();
                for (String key : keySet) {
                    printStream.print(key + " ");
                }
            }
        }
        printStream.print("\n");
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
