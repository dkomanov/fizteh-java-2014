package ru.fizteh.fivt.students.LevkovMiron.shell;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.lang.String;
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
        }
    }

    static String readString(final FileInputStream inStream, int size) throws IOException {
        byte[] utfData = new byte[size];
        inStream.read(utfData);
        String returned = new String(utfData, "UTF-8");
        return new String(utfData, "UTF-8");
    }
    static int readInt(final FileInputStream inStream) throws IOException {
        byte[] utfData = new byte[4];
        inStream.read(utfData);
        return ByteBuffer.wrap(utfData).getInt();
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
            System.out.print(pair.getKey() + ";  ");
        }
        System.out.println();
    }

    void runCommand(final String inString, final PrintStream printStream) {
        try {
            inString.trim();
            String[] command = inString.split(" ");
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
            } else { printStream.println("Unknown command"); }
        } catch (ArrayIndexOutOfBoundsException e) {
            printStream.println("Wrong command format: to few arguments\n");
        } catch (IOException e) {
            printStream.println(e.getMessage());
            System.exit(0);
        }
    }
}
