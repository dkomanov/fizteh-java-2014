package ru.fizteh.fivt.students.titov.multifilehashmap;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.Arrays;

public class MultiFileHashMap {

    private static class ArrayOfHashMaps {

        private HashMap<String, String> arrayOfHashMaps;

    }

    private static Integer DEFAULT16 = 16;
    private static File table;
    private static String nameOfDir = "";
    private static ArrayOfHashMaps[][] arrayOfHashMaps;

    static void newArrayOfHashMaps() {

        arrayOfHashMaps = new ArrayOfHashMaps[DEFAULT16][DEFAULT16];
        for (int i = 0; i < DEFAULT16; i++) {
            for (int j = 0; j < DEFAULT16; j++) {
                arrayOfHashMaps[i][j] = new ArrayOfHashMaps();
                try {
                    arrayOfHashMaps[i][j].arrayOfHashMaps = new HashMap<>();
                } catch (Exception e) {
                    System.err
                            .println("Fatal error. Can't create bigHashtable");
                    System.exit(1);
                }
            }
        }
    }

    static void createArrayOfHashMaps() throws Exception {

        newArrayOfHashMaps();
        File hashTable = new File(table.getPath() + File.separator + nameOfDir);
        String[] files = hashTable.list();
        for (String filesIter : files) {
            File hashTableDir = new File(hashTable.getPath() + File.separator
                    + filesIter);
            String[] files2 = hashTableDir.list();
            for (String filesIter2 : files2) {
                File hashTableFile = new File(hashTableDir.getPath()
                        + File.separator + filesIter2);
                DataInputStream readFileStream = new DataInputStream(
                        new FileInputStream(hashTableFile));
                try {
                    while (true) {
                        String key = readWordFromFile(readFileStream);
                        String value = readWordFromFile(readFileStream);
                        int hashcode = key.hashCode();
                        int ndirectory = hashcode % DEFAULT16;
                        int nfile = hashcode / DEFAULT16 % DEFAULT16;
                        arrayOfHashMaps[ndirectory][nfile].arrayOfHashMaps.put(
                                key, value);
                    }
                } catch (EOFException e) {
                    break;
                } catch (Exception e) {
                    System.err.println("Fatal error. Can't read file");
                }
            }
        }
    }

    static void writeArrayOfHashMaps() {

        File hashTable = new File(table.getPath() + File.separator + nameOfDir);
        String[] files = hashTable.list();
        for (String filesIter : files) {
            File hashTableDir = new File(hashTable.getPath() + File.separator
                    + filesIter);
            String[] files2 = hashTableDir.list();
            for (String filesIter2 : files2) {
                File hashTableFile = new File(hashTableDir.getPath()
                        + File.separator + filesIter2);
                if (hashTableFile.exists()) {
                    hashTableFile.delete();
                }
            }
        }
        for (int i = 0; i < DEFAULT16; i++) {
            for (int j = 0; j < DEFAULT16; j++) {
                if (!arrayOfHashMaps[i][j].arrayOfHashMaps.isEmpty()) {
                    File newDirs = new File(table.getPath() + File.separator
                            + nameOfDir + File.separator + String.valueOf(i)
                            + ".dir");
                    if (!newDirs.exists()) {
                        try {
                            newDirs.mkdir();
                        } catch (Exception e) {
                            System.err
                                    .println("Fatal error. Cannot create new dir");
                            System.exit(1);
                        }
                    }
                    File newFiles = new File(newDirs.getPath() + File.separator
                            + String.valueOf(j) + ".dat");
                    if (!newFiles.exists()) {
                        try {
                            newFiles.createNewFile();
                        } catch (Exception e) {
                            System.err
                                    .println("Fatal error. Cannot create new file");
                            System.exit(1);
                        }
                    }
                    try {
                        writeToFile(arrayOfHashMaps[i][j].arrayOfHashMaps,
                                newFiles.getPath());
                    } catch (Exception e) {
                        System.err
                                .println("Fatal error. Cannot save hashtable into file");
                    }
                }
            }
        }
    }

    public static Integer countOfTable(final String path) {

        Integer count = 0;
        for (int i = 0; i < DEFAULT16; i++) {
            for (int j = 0; j < DEFAULT16; j++) {
                File newDirs = new File(table.getPath() + File.separator + path
                        + File.separator + String.valueOf(i) + ".dir");
                if (newDirs.exists()) {
                    File newFiles = new File(newDirs.getPath() + File.separator
                            + String.valueOf(j) + ".dat");
                    if (newFiles.exists()) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public static String readWordFromFile(final DataInputStream dataInputStream)
            throws Exception {
        int length = dataInputStream.readInt();
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < length; i++) {
            buf.append(dataInputStream.readChar());
        }
        return buf.toString();
    }

    public static HashMap<String, String> createHashTable(final String path)
            throws Exception {
        HashMap<String, String> hashTable = new HashMap<>();
        DataInputStream readFileStream = new DataInputStream(
                new FileInputStream(path));
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

    public static void writeToFile(final HashMap<String, String> hashTable,
            final String path) throws Exception {
        DataOutputStream writeToFileStream = new DataOutputStream(
                new FileOutputStream(path));

        Set<String> keySet = hashTable.keySet();
        for (String iteratorSet : keySet) {
            writeToFileStream.writeInt(iteratorSet.length());
            writeToFileStream.writeChars(iteratorSet);
            writeToFileStream.writeInt(hashTable.get(iteratorSet).length());
            writeToFileStream.writeChars(hashTable.get(iteratorSet));
        }
        writeToFileStream.close();
    }

    static boolean put(final String key, final String value) throws Exception {

        if (nameOfDir.equals("")) {
            System.err.println("no table");
            return true;
        }
        int hashcode = key.hashCode();
        int ndirectory = hashcode % DEFAULT16;
        int nfile = hashcode / DEFAULT16 % DEFAULT16;
        if (!arrayOfHashMaps[ndirectory][nfile].arrayOfHashMaps
                .containsKey(key)) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out
                    .println(arrayOfHashMaps[ndirectory][nfile].arrayOfHashMaps
                            .get(key));
            arrayOfHashMaps[ndirectory][nfile].arrayOfHashMaps.remove(key);
        }
        arrayOfHashMaps[ndirectory][nfile].arrayOfHashMaps.put(key, value);

        return true;
    }

    static boolean get(final String key) {

        if (nameOfDir.equals("")) {
            System.err.println("no table");
            return true;
        }
        int hashcode = key.hashCode();
        int ndirectory = hashcode % DEFAULT16;
        int nfile = hashcode / DEFAULT16 % DEFAULT16;
        if (arrayOfHashMaps[ndirectory][nfile].arrayOfHashMaps.containsKey(key)) {
            System.out.println("found");
            System.out
                    .println(arrayOfHashMaps[ndirectory][nfile].arrayOfHashMaps
                            .get(key));
        } else {
            System.out.println("not found");
        }
        return true;
    }

    static boolean create(final String key) {

        if (Paths.get(table.toString(), key).toFile().exists()) {
            System.out.println(key + " exists");
            return true;
        }
        try {
            Files.createDirectory(Paths.get(table.toString(), key));
        } catch (Exception e) {
            System.err.println("Fatal error. Cannot create new dir");
            System.exit(1);
        }
        System.out.println("created");
        return true;
    }

    static boolean drop(final String key) {

        String[] files = table.list();
        for (String filesIter : files) {
            if (filesIter.equals(key)) {
                File newFileBuf = new File(table.getPath() + File.separator
                        + key);
                if (key.equals(nameOfDir)) {
                    newArrayOfHashMaps();
                }
                try {
                    String[] file = newFileBuf.list();
                    for (String fileIter : file) {
                        File hashTableDir = new File(newFileBuf.getPath()
                                + File.separator + fileIter);
                        String[] file2 = hashTableDir.list();
                        for (String fileIter2 : file2) {
                            File hashTableFile = new File(
                                    hashTableDir.getPath() + File.separator
                                            + fileIter2);
                            if (hashTableFile.exists()) {
                                hashTableFile.delete();
                            }
                        }
                        if (hashTableDir.exists()) {
                            hashTableDir.delete();
                        }
                    }
                    newFileBuf.delete();
                } catch (Exception e) {
                    System.err.println("Fatal error. Cannot delete dir");
                    System.exit(1);
                }
                if (key.equals(nameOfDir)) {
                    nameOfDir = "";
                }
                System.out.println("dropped");
                return true;
            }
        }
        System.out.println(key + " not exists");
        return true;
    }

    static boolean use(final String key) throws Exception {

        String[] files = table.list();
        for (String filesIter : files) {
            if (filesIter.equals(key)) {
                if (!nameOfDir.equals("")) {
                    writeArrayOfHashMaps();
                }
                nameOfDir = filesIter;
                System.out.println("using " + key);
                try {
                    createArrayOfHashMaps();
                } catch (Exception e) {
                    return true;
                }
                return true;
            }
        }
        System.out.println(key + " not exists");
        return true;
    }

    static boolean showTables() {

        writeArrayOfHashMaps();
        String[] files = table.list();
        for (String filesIter : files) {
            System.out.println(filesIter + " " + countOfTable(filesIter));
        }
        return true;
    }

    static boolean remove(final String key) throws Exception {

        if (nameOfDir.equals("")) {
            System.err.println("no table");
            return true;
        }
        int hashcode = key.hashCode();
        int ndirectory = hashcode % DEFAULT16;
        int nfile = hashcode / DEFAULT16 % DEFAULT16;
        if (arrayOfHashMaps[ndirectory][nfile].arrayOfHashMaps.containsKey(key)) {
            arrayOfHashMaps[ndirectory][nfile].arrayOfHashMaps.remove(key);
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }

        return true;
    }

    static boolean list() throws Exception {

        if (nameOfDir.equals("")) {
            System.err.println("no table");
            return true;
        }
        for (ArrayOfHashMaps[] dirs : arrayOfHashMaps) {
            for (ArrayOfHashMaps dirs2 : dirs) {
                Set<String> keySet = dirs2.arrayOfHashMaps.keySet();
                Iterator<String> it = keySet.iterator();
                while (it.hasNext()) {
                    System.out.print(it.next() + " ");
                }
            }
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
                return get(tokens[1]);
            } else {
                System.err.println("Error: get: wrong count of arguments");
                return false;
            }
        } else if (tokens[0].equals("remove")) {
            if (tokens.length == 2) {
                return remove(tokens[1]);
            } else {
                System.err.println("Error: remove: wrong count of arguments");
                return false;
            }
        } else if (tokens[0].equals("create")) {
            if (tokens.length == 2) {
                return create(tokens[1]);
            } else {
                System.err.println("Error: create: wrong count of arguments");
                return false;
            }
        } else if (tokens[0].equals("drop")) {
            if (tokens.length == 2) {
                return drop(tokens[1]);
            } else {
                System.err.println("Error: drop: wrong count of arguments");
                return false;
            }
        } else if (tokens[0].equals("use")) {
            if (tokens.length == 2) {
                return use(tokens[1]);
            } else {
                System.err.println("Error: use: wrong count of arguments");
                return false;
            }
        } else if (tokens[0].equals("show") && tokens[1].equals("tables")) {
            if (tokens.length == 2) {
                return showTables();
            } else {
                System.err
                        .println("Error: show tables: wrong count of arguments");
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
                if (!nameOfDir.equals("")) {
                    writeArrayOfHashMaps();
                }
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
        newArrayOfHashMaps();
        try {
            table = new File(System.getProperty("fizteh.db.dir"));
        } catch (Exception e) {
            System.err.println("Fatal error. Cannot read database");
            System.exit(1);
        }
        if (!table.exists()) {
            try {
                table.mkdir();
            } catch (Exception e) {
                System.err.println("Fatal error. Cannot create new dir");
                System.exit(1);
            }
        }
        if (table.isFile()) {
            System.err.println("Fatal error. Root folder is not a directory");
            System.exit(1);
        }

        String[] files = table.list();
        for (String filesIter : files) {
            File bufFile = new File(table.getPath() + File.separator
                    + filesIter);
            if (bufFile.isFile()) {
                System.err.println("Fatal error. Not all members are dirs");
                System.exit(1);
            }
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
