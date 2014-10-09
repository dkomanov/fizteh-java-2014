package ru.fizteh.fivt.students.LevkovMiron.shell;


import java.io.*;
import java.nio.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Мирон on 19.09.2014 ${PACKAGE_NAME}.
 */
class FileMap {

    HashMap<String, String> map;
    File file;

    FileMap(PrintStream printStream) {
        map = new HashMap<String, String>();
        file = new File(System.getProperty("db.file"));
        try (FileInputStream inStream = new FileInputStream(file)) {
            while (inStream.available() > 0) {
                int keySize = readInt(inStream);
                String key = readString(inStream, keySize);
                int valueSize = readInt(inStream);
                String value = readString(inStream, valueSize);
                map.put(key, value);
            }
            map.remove("");
        }
        catch (IOException e) {
            printStream.println("Incorrect file data. Didn't read");
            System.exit(-1);
        } catch (OutOfMemoryError e) {
            printStream.println(e.getMessage());
            System.exit(-4);
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
    void rewriteFile(File file) throws IOException {
        try (FileOutputStream outStream = new FileOutputStream(file)) {
            for (Map.Entry<String, String> pair : map.entrySet()) {
                writeIntAndString(outStream, pair.getKey());
                writeIntAndString(outStream, pair.getValue());
            }
        }
        catch (IOException e) {
            throw new IOException("Can't rewrite the file");
        }
    }

    void exit() throws IOException {
        System.exit(1);
    }
    void put(final String key, final String value) throws IOException {
        if (map.containsKey(key)) {
            System.out.println("overwrite");
            System.out.println(map.get(key));
        } else {
            System.out.println("new");
        }
        map.put(key, value);
        try {
            rewriteFile(file);
        } catch (IOException e) {
            throw new IOException("Counldn't rewrite the file");
        }
    }
    void get(String key) {
        if (map.containsKey(key)) {
            System.out.println("found");
            System.out.println(map.get(key));
        } else {
            System.out.println("not found");
        }
    }
    void remove(String key) throws IOException {
        if (!map.containsKey(key)) {
            System.out.println("not found");
        } else {
            System.out.println(map.get(key));
            map.remove(key);
            System.out.println("removed");
            try {
                rewriteFile(file);
            } catch (IOException e) {
                throw new IOException("Counldn't rewrite the file");
            }
        }
    }
    void list() {
        for (HashMap.Entry<String, String> pair : map.entrySet()) {
            System.out.print(pair.getKey() + "; ");
        }
        System.out.println();
    }

    void runCommand(String inString, final PrintStream printStream) {
        HashMap<String, Integer> mp = new HashMap<String, Integer>();
        mp.put("put", 3);
        mp.put("get", 2);
        mp.put("remove", 2);
        mp.put("list", 1);
        mp.put("exit", 1);
        try {
            inString = inString.trim();
            String[] command = inString.split(" ");
            if (mp.containsKey(command[0]) && mp.get(command[0]) != command.length) {
                throw new ArrayIndexOutOfBoundsException();
            }
            if (command[0].equals("put")) {
                put(command[1], command[2]);
            } else if (command[0].equals("exit")) {
                exit();
            } else if (command[0].equals("get")) {
                get(command[1]);
            } else if (command[0].equals("remove")) {
                remove(command[1]);
            } else if (command[0].equals("list")) {
                list();
            } else {
                throw new IOException("Unknown command");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            printStream.println("Wrong command format " + "\"" + inString
                        + "\" : wrong number of arguments arguments\n");
            if (printStream.equals(System.err)) {
                System.exit(-2);
            }
        } catch (IOException e) {
            printStream.println(e.getMessage());
            if (printStream.equals(System.err)) {
                System.exit(-3);
            }
        }
    }
}
