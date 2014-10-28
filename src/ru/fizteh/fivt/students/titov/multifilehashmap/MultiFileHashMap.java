package ru.fizteh.fivt.students.titov.multifilehashmap;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.Arrays;

public class MultiFileHashMap {

    private static class ArrayOfHashMaps {

        private Map<String, String> arrayOfHashMaps;

    }

    private static final int KEYNUMBER = 16;
    private static File table;
    private static String nameOfDir = "";
    private static ArrayOfHashMaps[][] arrayOfHashMaps;
    
    static void argumentsCountError(String command) throws Exception {
        System.err.println("Error: " + command + " : wrong count of arguments");
    }

    static void newArrayOfHashMaps() throws Exception {

        arrayOfHashMaps = new ArrayOfHashMaps[KEYNUMBER][KEYNUMBER];
        for (int i = 0; i < KEYNUMBER; i++) {
            for (int j = 0; j < KEYNUMBER; j++) {
                arrayOfHashMaps[i][j] = new ArrayOfHashMaps();
                try {
                    arrayOfHashMaps[i][j].arrayOfHashMaps = new HashMap<>();
                } catch (Exception e) {
                    throw new Exception("Can't create bigHashtable");
                }
            }
        }
    }

    static void fatalError(String error) {
        System.err.println("Fatal error: " + error);
    }
    
    static void createArrayOfHashMaps() throws Exception {

        newArrayOfHashMaps();
        File hashTable = new File(table.getPath() + File.separator + nameOfDir);
        String[] files = hashTable.list();
        for (String filesIter : files) {
            File hashTableDir = new File(hashTable.getPath() + File.separator + filesIter);
            String[] files2 = hashTableDir.list();
            for (String filesIter2 : files2) {
                File hashTableFile = new File(hashTableDir.getPath() + File.separator + filesIter2);
                DataInputStream readFileStream = new DataInputStream(
                        new FileInputStream(hashTableFile));
                try {
                    while (true) {
                        String key = readWordFromFile(readFileStream);
                        String value = readWordFromFile(readFileStream);
                        int hashcode = key.hashCode();
                        int ndirectory = Math.abs(hashcode) % KEYNUMBER;
                        int nfile = hashcode / KEYNUMBER % KEYNUMBER;
                        arrayOfHashMaps[ndirectory][nfile].arrayOfHashMaps.put(key, value);
                    }
                } catch (EOFException e) {
                    readFileStream.close();
                    break;
                } catch (Exception e) {
                    readFileStream.close();
                    throw new Exception("Fatal error. Can't read file");
                }
            }
        }
    }

    static void writeArrayOfHashMaps() throws Exception {

        File hashTable = new File(table.getPath() + File.separator + nameOfDir);
        String[] files = hashTable.list();
        for (String filesIter : files) {
            File hashTableDir = new File(hashTable.getPath() + File.separator + filesIter);
            String[] files2 = hashTableDir.list();
            for (String filesIter2 : files2) {
                File hashTableFile = new File(hashTableDir.getPath() + File.separator + filesIter2);
                if (hashTableFile.exists()) {
                    hashTableFile.delete();
                }
            }
        }
        for (int i = 0; i < KEYNUMBER; i++) {
            for (int j = 0; j < KEYNUMBER; j++) {
                if (!arrayOfHashMaps[i][j].arrayOfHashMaps.isEmpty()) {
                    File newDirs = new File(table.getPath() + File.separator + nameOfDir + File.separator + String.valueOf(i) + ".dir");
                    if (!newDirs.exists()) {
                        try {
                            newDirs.mkdir();
                        } catch (Exception e) {
                            throw new Exception("Fatal error. Cannot create new dir");
                        }
                    }
                    File newFiles = new File(newDirs.getPath() + File.separator + String.valueOf(j) + ".dat");
                    if (!newFiles.exists()) {
                        try {
                            newFiles.createNewFile();
                        } catch (Exception e) {
                            throw new Exception ("Fatal error. Cannot create new file");
                        }
                    }
                    try {
                        writeToFile(arrayOfHashMaps[i][j].arrayOfHashMaps, newFiles.getPath());
                    } catch (Exception e) {
                        throw new Exception("Fatal error. Cannot save hashtable into file");
                    }
                }
            }
        }
    }

    private static Integer countOfTable(final String path) {

        Integer count = 0;
        for (int i = 0; i < KEYNUMBER; i++) {
            for (int j = 0; j < KEYNUMBER; j++) {
                File newDirs = new File(table.getPath() + File.separator + path + File.separator + String.valueOf(i) + ".dir");
                if (newDirs.exists()) {
                    File newFiles = new File(newDirs.getPath() + File.separator + String.valueOf(j) + ".dat");
                    if (newFiles.exists()) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private static String readWordFromFile(final DataInputStream dataInputStream) throws Exception {
        int length = dataInputStream.readInt();
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < length; i++) {
            buf.append(dataInputStream.readChar());
        }
        return buf.toString();
    }

    private static void writeToFile(final Map<String, String> hashTable,
            final String path) throws Exception {
        DataOutputStream writeToFileStream = new DataOutputStream(new FileOutputStream(path));

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
            throw new Exception("no table");
        } else {
            int hashcode = key.hashCode();
            int ndirectory = Math.abs(hashcode) % KEYNUMBER;
            int nfile = hashcode / KEYNUMBER % KEYNUMBER;
            if (!arrayOfHashMaps[ndirectory][nfile].arrayOfHashMaps.containsKey(key)) {
            System.out.println("new");
            } else {
                System.out.println("overwrite");
                System.out.println(arrayOfHashMaps[ndirectory][nfile].arrayOfHashMaps.get(key));
                arrayOfHashMaps[ndirectory][nfile].arrayOfHashMaps.remove(key);
            }
            arrayOfHashMaps[ndirectory][nfile].arrayOfHashMaps.put(key, value);
        }
        return true;
    }

    static boolean get(final String key) throws Exception {

        if (nameOfDir.equals("")) {
            System.out.println("no table");
        } else {
            int hashcode = key.hashCode();
            int ndirectory = Math.abs(hashcode) % KEYNUMBER;
            int nfile = hashcode / KEYNUMBER % KEYNUMBER;
            if (arrayOfHashMaps[ndirectory][nfile].arrayOfHashMaps.containsKey(key)) {
                System.out.println("found");
                System.out.println(arrayOfHashMaps[ndirectory][nfile].arrayOfHashMaps.get(key));
            } else {
                System.out.println("not found");
            }
        }
        return true;
    }

    static boolean create(final String key) throws Exception {

        if (Paths.get(table.toString(), key).toFile().exists()) {
            System.out.println(key + " exists");
            return true;
        }
        try {
            Files.createDirectory(Paths.get(table.toString(), key));
        } catch (Exception e) {
            throw new Exception("Fatal error. Cannot create new dir");
        }
        System.out.println("created");
        return true;
    }

    static boolean drop(final String key) throws Exception {

        String[] files = table.list();
        for (String filesIter : files) {
            if (filesIter.equals(key)) {
                File newFileBuf = new File(table.getPath() + File.separator + key);
                if (key.equals(nameOfDir)) {
                    newArrayOfHashMaps();
                }
                try {
                    String[] fil = newFileBuf.list();
                    for (String filIter : fil) {
                        File hashTableDir = new File(newFileBuf.getPath() + File.separator + filIter);
                        String[] fil2 = hashTableDir.list();
                        for (String filIter2 : fil2) {
                            File hashTableFile = new File(hashTableDir.getPath() + File.separator + filIter2);
                            hashTableFile.delete();
                        }
                        hashTableDir.delete();
                    }
                    newFileBuf.delete();
                } catch (Exception e) {
                    throw new Exception("Fatal error. Cannot delete dir");
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
                createArrayOfHashMaps();
                return true;
            }
        }
        System.out.println(key + " not exists");
        return true;
    }

    static boolean showTables() throws Exception {

        writeArrayOfHashMaps();
        String[] files = table.list();
        for (String filesIter : files) {
            System.out.println(filesIter + " " + countOfTable(filesIter));
        }
        return true;
    }

    static boolean remove(final String key) throws Exception {

        if (nameOfDir.equals("")) {
            System.out.println("no table");
        } else {
            int hashcode = key.hashCode();
            int ndirectory = Math.abs(hashcode) % KEYNUMBER;
            int nfile = hashcode / KEYNUMBER % KEYNUMBER;
            if (arrayOfHashMaps[ndirectory][nfile].arrayOfHashMaps.containsKey(key)) {
                arrayOfHashMaps[ndirectory][nfile].arrayOfHashMaps.remove(key);
                System.out.println("removed");
            } else {
                System.out.println("not found");
            }
        }
        return true;
    }

    static boolean list() throws Exception {

        if (nameOfDir.equals("")) {
            System.out.println("no table");
        } else {
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
        }
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
                argumentsCountError(tokens[0]);
                return false;
            }
        } else if (tokens[0].equals("get")) {
            if (tokens.length == 2) {
                return get(tokens[1]);
            } else {
                argumentsCountError(tokens[0]);
                return false;
            }
        } else if (tokens[0].equals("remove")) {
            if (tokens.length == 2) {
                return remove(tokens[1]);
            } else {
                argumentsCountError(tokens[0]);
                return false;
            }
        } else if (tokens[0].equals("create")) {
            if (tokens.length == 2) {
                return create(tokens[1]);
            } else {
                argumentsCountError(tokens[0]);
                return false;
            }
        } else if (tokens[0].equals("drop")) {
            if (tokens.length == 2) {
                return drop(tokens[1]);
            } else {
                argumentsCountError(tokens[0]);
                return false;
            }
        } else if (tokens[0].equals("use")) {
            if (tokens.length == 2) {
                return use(tokens[1]);
            } else {
                argumentsCountError(tokens[0]);
                return false;
            }
        } else if (tokens[0].equals("show") && tokens[1].equals("tables")) {
            if (tokens.length == 2) {
                return showTables();
            } else {
                argumentsCountError(tokens[0] + tokens[1]);
                return false;
            }
        } else if (tokens[0].equals("list")) {
            if (tokens.length == 1) {
                return list();
            } else {
                argumentsCountError(tokens[0]);;
                return false;
            }
        } else if (tokens[0].equals("exit")) {
            if (tokens.length == 1) {
                if (!nameOfDir.equals("")) {
                    writeArrayOfHashMaps();
                }
                System.exit(0);
            } else {
                argumentsCountError(tokens[0]);
                return false;
            }
        } else {
            System.err.println("Unknown command!");
        }

        return true;
    }

    static void interactive() {
        while (true) {
            try {
                System.out.print("$ ");
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
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
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }

    public static void main(final String[] args) {
        try {
            newArrayOfHashMaps();
        } catch (Exception e) {
            System.err.println("");
        }
        try {
            table = new File(System.getProperty("fizteh.db.dir"));
        } catch (Exception e) {
            fatalError("Cannot read database");
            System.exit(1);
        }
        if (!table.exists()) {
            try {
                table.mkdir();
            } catch (Exception e) {
                fatalError("Cannot create new dir");
                System.exit(1);
            }
        }
        if (table.isFile()) {
            fatalError("Root folder is not a directory");
            System.exit(1);
        }
        String[] files = table.list();
        for (String filesIter : files) {
            File bufFile = new File(table.getPath() + File.separator + filesIter);
            if (bufFile.isFile()) {
                fatalError("Not all members are dirs");
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
                    System.err.println(e.getMessage());
                    System.exit(1);
                }
            }
        }
    }

}
