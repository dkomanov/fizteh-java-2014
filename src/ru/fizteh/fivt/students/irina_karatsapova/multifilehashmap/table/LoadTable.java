package ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.table;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class LoadTable {
    public static void start(File tableDir) throws Exception {
        Table.dir = tableDir;
        Table.map.clear();
        Table.keysClear();

        if (!Table.dir.exists()) {
            throw new Exception("Database: load: The table does not exist");
        } else {
            for (File dir : Table.dir.listFiles()) {
                if (dir.isDirectory()) {
                    for (File file : dir.listFiles()) {
                        loadFile(file);
                    }
                }
            }
        }

        Table.loaded = true;
    }

    private static void loadFile(File file) throws Exception {
        DataInputStream inStream = new DataInputStream(new FileInputStream(file));
        try {
            int filePos = 0;
            while (filePos < file.length()) {
                int keyLength;
                int valueLength;
                String key;
                String value;

                try {
                    keyLength = inStream.readInt();
                    valueLength = inStream.readInt();
                    key = readBytes(inStream, keyLength);
                    value = readBytes(inStream, valueLength);
                } catch (IOException e) {
                    throw new Exception("Database: load: Wrong format of dir");
                }

                if (Table.map.containsKey(key)) {
                    throw new Exception("DataBase: load: Two same keys in the dir");
                }
                Table.addKey(key);
                Table.map.put(key, value);

                filePos += 8 + keyLength + valueLength;
            }
        } finally {
            try {
                inStream.close();
            } catch (IOException e) {
                throw new Exception("DataBase: load: Error while closing the dir");
            }
        }
    }

    private static String readBytes(DataInputStream inStream, int length) throws IOException {
        byte[] stringInBytes = new byte[length];
        inStream.readFully(stringInBytes);
        return new String(stringInBytes, "UTF-8");
    }
}
