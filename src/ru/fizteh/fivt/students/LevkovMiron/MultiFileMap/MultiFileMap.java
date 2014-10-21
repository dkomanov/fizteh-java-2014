package ru.fizteh.fivt.students.LevkovMiron.MultiFileMap;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Мирон on 19.09.2014 ${PACKAGE_NAME}.
 */
class MultiFileMap {

    HashMap<String, String>[][] map;
    File parentDirectory;
    File tableDirectory;
    HashMap<File, Integer> tableRowCounter;
    PrintStream printStream;

    MultiFileMap(PrintStream stream) {
        map = new HashMap[16][16];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                map[i][j] = new HashMap<String, String>();
            }
        }
        printStream = stream;
        parentDirectory = new File(System.getProperty("fizteh.db.dir"));
        if (!parentDirectory.exists()) {
            printStream.println("Parent directory doesn't exist");
            System.exit(-1);
        }
        if (!parentDirectory.isDirectory()) {
            printStream.println("Parent directory is a file actually");
            System.exit(-2);
        }
        for (File table : parentDirectory.listFiles()) {
            if (!table.isDirectory()) {
                printStream.println("Table directory is a file actually");
                System.exit(-5);
            }
        }
        tableDirectory = null;
        tableRowCounter = new HashMap<File, Integer>();
        for (File table : parentDirectory.listFiles()) {
            int counter = 0;
            for (File tablePart : table.listFiles()) {
                for (File tablePartFile : tablePart.listFiles()) {
                    try (FileInputStream inputStream = new FileInputStream(tablePartFile)) {
                        while (inputStream.available() > 0) {
                            int size = readInt(inputStream);
                            String temp = readString(inputStream, size);
                            size = readInt(inputStream);
                            temp = readString(inputStream, size);
                            counter++;
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        System.exit(-6);
                    }
                }
            }
            tableRowCounter.put(table, counter);
        }
    }

    void exit() {
        for (File table : parentDirectory.listFiles()) {
            if (tableRowCounter.containsKey(table) && tableRowCounter.get(table) == 0) {
                drop(table);
            }
        }
        if (tableDirectory != null) {
            try {
                rewrite();
            } catch (IOException e) {
                printStream.println("Error while exiting");
                System.exit(-7);
            }
        }
        System.exit(1);
    }
    void runCommand(String inString, final PrintStream stream) {
        try {
            inString = inString.trim();
            if (inString.equals("show tables")) {
                showTables();
                return;
            }
            String[] command = inString.split(" ");
            if (command[0].equals("use")) {
                use(command[1]);
            } else if (command[0].equals("exit")) {
                exit();
            } else if (command[0].equals("drop")) {
                File f = new File(parentDirectory.getAbsolutePath() + "/" + command[1]);
                if (f.exists()) {
                    drop(f);
                    tableRowCounter.remove(f);
                    System.out.println("dropped");
                } else {
                    System.out.println(command[1] + " doesn't exist");
                }
            } else if (command[0].equals("create")) {
                create(command[1]);
            } else {
                if (tableDirectory == null && (command[0].equals("put") || command[0].equals("get")
                    || command[0].equals("list") || command[0].equals("remove"))) {
                    printStream.println("No tables in usage");
                    return;
                }
                if (command[0].equals("put")) {
                    put(command[1], command[2]);
                } else if (command[0].equals("get")) {
                    get(command[1]);
                } else if (command[0].equals("remove")) {
                    remove(command[1]);
                } else if (command[0].equals("list")) {
                    list();
                } else {
                    printStream.println("Unknown command");
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            printStream.println("Wrong command format: to few arguments\n");
        } catch (IOException e) {
            printStream.println(e.getMessage());
            System.exit(0);
        }
    }
    static String readString(final FileInputStream inStream, int size) throws IOException, OutOfMemoryError {
        ArrayList<Byte> tempData = new ArrayList<Byte>();
        for (int i = 0; i < size; i++) {
            byte[] oneByte = new byte[1];
            if (inStream.read(oneByte) == -1) {
                throw new OutOfMemoryError("Incorrect data. Unexpected end of file.");
            }
            tempData.add(oneByte[0]);
        }
        try {
            byte[] utfData = new byte[size];
            for (int i = 0; i < tempData.size(); i++) {
                utfData[i] = tempData.get(i);
            }
            String returned = new String(utfData, "UTF-8");
            return new String(utfData, "UTF-8");
        } catch (OutOfMemoryError e) {
            throw new OutOfMemoryError("Data is too large.");
        }
    }
    static int readInt(final FileInputStream inStream) throws IOException, OutOfMemoryError {
        byte[] utfData = new byte[4];
        if (inStream.read(utfData) < 4) {
            throw new OutOfMemoryError("Unexpected end of file.");
        }
        int value = ByteBuffer.wrap(utfData).getInt();
        if (value < 0) {
            throw new IOException();
        }
        return value;
    }
    static void writeIntAndString(FileOutputStream stream, String key) throws IOException {
        byte[] data = ByteBuffer.allocate(4).putInt(key.length()).array();
        stream.write(data);
        stream.write(key.getBytes("UTF-8"));
    }
    void showTables() {
        for (File table : parentDirectory.listFiles()) {
            if (table.isDirectory()) {
                System.out.println(table.getName() + " " + tableRowCounter.get(table));
            }
        }
    }
    void create(String name) {
        File fileToCreate = new File(parentDirectory.getAbsolutePath() + "/" + name);
        if (fileToCreate.exists()) {
            System.out.println(name + " exists");
            return;
        }
        fileToCreate.mkdir();
        tableRowCounter.put(fileToCreate, 0);
        System.out.println("created");
    }
    void drop(File deletedFile) {
        if (tableDirectory != null && tableDirectory.equals(deletedFile)) {
            tableDirectory = null;
        }
        if (!deletedFile.isDirectory()) {
            deletedFile.delete();
            return;
        }
        for (File f : deletedFile.listFiles()) {
            drop(f);
        }
        deletedFile.delete();
    }
    void remove(String key) {
        int h1 = key.hashCode() % 16;
        int h2 = (key.hashCode() / 16) % 16;
        if (!map[h1][h2].containsKey(key)) {
            System.out.println("not found");
        } else {
            System.out.println(map[h1][h2].get(key));
            map[h1][h2].remove(key);
            tableRowCounter.replace(tableDirectory, tableRowCounter.get(tableDirectory) - 1);
            if (map[h1][h2].size() == 0) {
                File f = new File(tableDirectory.getAbsolutePath() + "/" + h1 + ".dir/" + h2 + ".dat");
                drop(f);
            }
            File f = new File(tableDirectory.getAbsolutePath() + "/" + h1 + ".dir");
            if (f.listFiles().length == 0) {
                drop(f);
            }
            System.out.println("removed");
        }
    }
    void get(String key) {
        int h1 = key.hashCode() % 16;
        int h2 = (key.hashCode() / 16) % 16;
        if (map[h1][h2].containsKey(key)) {
            System.out.println("found");
            System.out.println(map[h1][h2].get(key));
        } else {
            System.out.println("not found");
        }
    }
    void list() {
        for (HashMap<String, String>[] mapDir : map) {
            for (HashMap<String, String> mp : mapDir) {
                for (HashMap.Entry<String, String> pair : mp.entrySet()) {
                    System.out.print(pair.getKey() + ";  ");
                }
            }
        }
        System.out.println();
    }
    void put(final String key, final String value) throws IOException {
        tableRowCounter.replace(tableDirectory, tableRowCounter.get(tableDirectory) + 1);
        int h1 = key.hashCode() % 16;
        int h2 = (key.hashCode() / 16) % 16;
        int amount = 0;
        for (HashMap<String, String> mp : map[h1]) {
            amount += mp.size();
        }
        if (amount == 0) {
            File hashDirectory = new File(tableDirectory.getAbsolutePath() + "/" + h1 + ".dir");
            hashDirectory.mkdir();
        }
        if (map[h1][h2].size() == 0) {
            File hashFile = new File(tableDirectory.getAbsolutePath() + "/" + h1 + ".dir/" + h2 + ".dat");
            hashFile.createNewFile();
        }
        if (map[h1][h2].containsKey(key)) {
            System.out.println("overwrite");
            System.out.println(map[h1][h2].get(key));
        } else {
            System.out.println("new");
        }
        map[h1][h2].put(key, value);
    }
    void use(String name) throws IOException {
        File newTable = new File(parentDirectory.getAbsolutePath() + "/" + name);
        if (!newTable.exists()) {
            printStream.println("Table " + name + " doesn't exist");
            return;
        }
        if (tableRowCounter.containsKey(tableDirectory) && tableRowCounter.get(tableDirectory) == 0) {
            drop(tableDirectory);
        } else if (tableDirectory != null) {
            try {
                rewrite();
            } catch (IOException e) {
                throw new IOException("Counldn't rewrite the file");
            }
        }
        tableDirectory = newTable;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                map[i][j].clear();
                try (FileInputStream inputStream = new FileInputStream(
                        new File(tableDirectory.getAbsolutePath() + "/" + i + ".dir/" + j + ".dat"))) {
                    int sz = readInt(inputStream);
                    String key = readString(inputStream, sz);
                    int sz2 = readInt(inputStream);
                    String value = readString(inputStream, sz2);
                    map[i][j].put(key, value);
                } catch (IOException e) {
                    throw new IOException("Can't read the file");
                }
            }
        }
        System.out.println("Using " + name);
    }
    void rewrite() throws IOException {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (map[i][j].size() > 0) {
                    File f = new File(tableDirectory.getAbsolutePath() + "/" + i + ".dir/" + j + ".dat");
                    rewriteFile(f, i, j);
                }
            }
        }
    }
    void rewriteFile(File file, int i, int j) throws IOException {
        try (FileOutputStream outStream = new FileOutputStream(file)) {
            for (Map.Entry<String, String> pair : map[i][j].entrySet()) {
                writeIntAndString(outStream, pair.getKey());
                writeIntAndString(outStream, pair.getValue());
            }
        }
        catch (IOException e) {
            throw new IOException("Can't rewrite the file");
        } catch (OutOfMemoryError e) {
            System.out.println(e.getMessage());
            System.exit(-5);
        }
    }

}
