package ru.fizteh.fivt.students.titov.filemap;

import java.io.*;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.Arrays;

public class FileMap {

    // static String path = "dp.dat";
    // static File currentDir = new File(System.getProperty("db.file"));
    // static File table = new File(currentDir.getPath() + File.separator +
    // path);
    private static File table;

    public static String readWordFromFile(final DataInputStream dataInputStream)
            throws Exception {
        int length = dataInputStream.readInt();
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < length; i++) {
            buf.append(dataInputStream.readChar());
        }
        return buf.toString();
    }

    public static HashMap<String, String> createHashTable() throws Exception {
        HashMap<String, String> hashTable = new HashMap<>();
        DataInputStream readFileStream = new DataInputStream(
                new FileInputStream(table));
        while (true) {
            try {
                String key = readWordFromFile(readFileStream);
                String value = readWordFromFile(readFileStream);
                hashTable.put(key, value);
            } catch (Exception e) {
                break;
            }
        }
        return hashTable;
    }

    public static void writeToFile(final HashMap<String, String> hashTable)
            throws Exception {
        DataOutputStream writeToFileStream = new DataOutputStream(
                new FileOutputStream(table));

        Set<String> keySet = hashTable.keySet();
        for (String iteratorSet: keySet) {
            writeToFileStream.writeInt(iteratorSet.length());
            writeToFileStream.writeChars(iteratorSet);
            writeToFileStream.writeInt(hashTable.get(iteratorSet).length());
            writeToFileStream.writeChars(hashTable.get(iteratorSet));
        }
        writeToFileStream.close();
    }

    static boolean put(final String key, final String value) throws Exception {
        HashMap<String, String> hashTable = createHashTable();
        if (!hashTable.containsKey(key)) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(hashTable.get(key));
            hashTable.remove(key);
        }
        hashTable.put(key, value);
        writeToFile(hashTable);

        return true;
    }

    static boolean get(final String key, final HashMap<String, String> hashTable) {
        if (hashTable.containsKey(key)) {
            System.out.println("found");
            System.out.println(hashTable.get(key));
        } else {
            System.out.println("not found");
        }
        return true;
    }

    static boolean remove(final String key) throws Exception {
        HashMap<String, String> hashTable = createHashTable();
        if (hashTable.containsKey(key)) {
            hashTable.remove(key);
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
        writeToFile(hashTable);

        return true;
    }

    static boolean list() throws Exception {
        Set<String> keySet = createHashTable().keySet();
        Iterator<String> it = keySet.iterator();
        while (it.hasNext()) {
            System.out.print(it.next() + " ");
        }
        System.out.println();

        return true;
    }

    static boolean executeCommand(final String command) throws Exception {
        String[] tokens = command.split("[\\s]+");
        if (tokens == null || tokens.length == 0) {
            return true;
        }
        if (tokens.length == 1 && tokens[0].equals("")) {
            return true;
        }

        if (tokens[0].equals("")) {
            tokens = Arrays.copyOfRange(tokens, 1, tokens.length);
        }

        if (tokens[0].equals("put")) {
            if (tokens.length == 3) {
                return put(tokens[1], tokens[2]);
            } else {
                System.err.println("Error: put: wrong count of arguments");
                return false;
            }
        } else if (tokens[0].equals("get")) {
            if (tokens.length == 2) {
                return get(tokens[1], createHashTable());
            } else {
                System.err.println("Error: get: wrong count of arguments");
                return false;
            }
        } else if (tokens[0].equals("remove")) {
            if (tokens.length == 2) {
                return remove(tokens[1]);
            } else {
                System.err.println("Error: removie: wrong count of arguments");
                return false;
            }
        } else if (tokens[0].equals("list")) {
            if (tokens.length == 1) {
                return list();
            } else {
                System.err.println("Error: list: wrong count of arguments");
                return false;
            }
        } else if (tokens[0].equals("exit")) {
            if (tokens.length == 1) {
                System.exit(0);
            } else {
                System.err.println("Error: exit: wrong count of arguments");
                return false;
            }
        } else {
            System.err.println("Unknown command!");
            return false;
        }

        return true;
    }

    static void interactive() {
        while (true) {
            try {
                System.out.print("$ ");
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(System.in));
                String string = reader.readLine();

                String[] commands = string.split(";");
                if (commands == null) {
                    continue;
                }

                for (String command : commands) {
                    if (command.equals("")) {
                        continue;
                    }
                    if (!executeCommand(command)) {
                        break;
                    }
                }
            } catch (Exception e) {
                System.err
                        .println("Fatal error. Program has been interrupted.");
                System.exit(1);
            }
        }
    }

    public static void main(final String[] args) {
        try {
            table = new File(System.getProperty("db.file"));
        } catch (Exception e) {
            System.err.println("Fatal error. Cannot read database");
            System.exit(1);
        }
        if (!table.exists()) {
            File file = new File(table.toString());
            try {
                file.createNewFile();
            } catch (Exception e) {
                System.err.println("Fatal error. Cannot create new file");
                System.exit(1);
            }
        }
        if (!table.isFile()) {
            System.err.println("Fatal error. Cannot read database");
            System.exit(1);
        }
        if (args.length == 0) {
            interactive();
        } else {
            StringBuilder builder = new StringBuilder();
            for (String s : args) {
                builder.append(s).append(" ");
            }

            String string = new String(builder);
            String[] commands = string.split(";");
            for (String command : commands) {
                try {
                    if (!executeCommand(command)) {
                        System.exit(1);
                    }
                } catch (Exception e) {
                    System.err.println("Fatal error. Can't create hashtable");
                    System.exit(1);
                }
            }
        }
    }

}
