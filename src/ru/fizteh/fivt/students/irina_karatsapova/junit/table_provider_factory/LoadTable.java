package ru.fizteh.fivt.students.irina_karatsapova.junit.table_provider_factory;

import ru.fizteh.fivt.students.irina_karatsapova.junit.utils.TableException;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class LoadTable {
    public static void start(MyTable table) throws TableException {
        table.currentMap.clear();
        keysClear(table);
        if (!table.tablePath.exists()) {
            throw new TableException("load: The table does not exist");
        } else {
            for (File dir : table.tablePath.listFiles()) {
                if (dir.isDirectory()) {
                    for (File file : dir.listFiles()) {
                        loadFile(table, file);
                    }
                }
            }
        }
        table.loaded = true;
    }

    private static void loadFile(MyTable table, File file) throws TableException {

        try (FileInputStream fileStream = new FileInputStream(file)) {
            try (DataInputStream inStream = new DataInputStream(fileStream)) {
                int filePos = 0;
                while (filePos < file.length()) {
                    int keyLength;
                    int valueLength;
                    String key;
                    String value;

                    try {
                        keyLength = inStream.readInt();
                        key = readBytes(inStream, keyLength);
                        valueLength = inStream.readInt();
                        value = readBytes(inStream, valueLength);
                    } catch (IOException e) {
                        throw new TableException("load: Wrong format of providerDir");
                    }

                    if (table.currentMap.containsKey(key)) {
                        throw new TableException("load: Two same currentKeys in the providerDir");
                    }
                    table.addKey(key);
                    table.currentMap.put(key, value);

                    filePos += 8 + keyLength + valueLength;
                }
            }
        }  catch (IOException e) {
            throw new TableException("load: Can't read from the file");
        }
    }

    private static String readBytes(DataInputStream inStream, int length) throws IOException {
        byte[] stringInBytes = new byte[length];
        inStream.readFully(stringInBytes);
        return new String(stringInBytes, "UTF-8");
    }

    static void keysClear(MyTable table) {
        for (int dir = 0; dir < 16; dir++) {
            for (int file = 0; file < 16; file++) {
                table.currentKeys[dir][file].clear();
            }
        }
    }
}
