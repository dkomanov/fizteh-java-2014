package ru.fizteh.fivt.students.ilivanov.FileMap;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FileUsing {
    private static String filePath;
    public static HashMap<String, String> map;
    private int exitCode = 0;

    FileUsing(final String file) {
        filePath = file;
    }

    public final int run(final String... args) throws Exception {
        load();
        ShellDb shell = new ShellDb();
        if (args.length == 0) {
            shell.runInteractive();
        } else {
            exitCode = shell.runPackage(args);
        }

        return exitCode;
    }

    private void load() throws Exception {
        map = new HashMap<>();
        try (DataInputStream stream =
                     new DataInputStream(new FileInputStream(filePath))) {
            boolean eof = false;
            int length;
            String key = "", value = "";
            byte[] word;
            while (!eof) {
                try {
                    length = stream.readInt();
                    if (length < 1) {
                        System.err.println("Database is incorrect");
                        System.exit(-1);
                    }
                    try {
                        word = new byte[length];
                        stream.readFully(word);
                        key = new String(word, "UTF-8");

                        length = stream.readInt();
                        if (length < 1) {
                            System.err.println("Database is incorrect");
                            System.exit(-1);
                        }
                        word = new byte[length];
                        stream.readFully(word);
                        value = new String(word, "UTF-8");
                    } catch (EOFException e) {
                        System.err.println("Database is incorrect");
                        System.exit(-1);
                    }


                    if (map.put(key, value) != null) {
                        throw new Exception("Equal keys in the database");
                    }
                } catch (EOFException e) {
                    eof = true;
                }
            }
        }
    }

    public static void writeDb() throws Exception {
        try (DataOutputStream stream =
                     new DataOutputStream(new FileOutputStream(filePath))) {
            byte[] bytes;
            for (Map.Entry<String, String> pair : map.entrySet()) {
                bytes = pair.getKey().getBytes("UTF-8");
                stream.writeInt(bytes.length);
                stream.write(bytes);

                bytes = pair.getValue().getBytes("UTF-8");
                stream.writeInt(bytes.length);
                stream.write(bytes);
            }
        }
    }
}
