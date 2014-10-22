package ru.fizteh.fivt.students.irina_karatsapova.filemap.database;

import ru.fizteh.fivt.students.irina_karatsapova.filemap.utils.DataBaseException;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class LoadDataBase {
    public static void start() throws Exception {
        DataBase.map.clear();
        DataBase.keys.clear();

        DataInputStream inStream = new DataInputStream(new FileInputStream(DataBase.file));
        try {
            int filePos = 0;
            while (filePos < DataBase.file.length()) {
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
                    throw new DataBaseException("Database: load: Wrong format of file");
                }

                if (DataBase.map.containsKey(key)) {
                    throw new DataBaseException("DataBase: load: Two same keys in the file");
                }
                DataBase.keys.add(key);
                DataBase.map.put(key, value);

                filePos += 8 + keyLength + valueLength;
            }
        } finally {
            try {
                inStream.close();
            } catch (IOException e) {
                throw new DataBaseException("DataBase: load: Error while closing the file");
            }
        }
    }

    public static String readBytes(DataInputStream inStream, int length) throws IOException {
        byte[] stringInBytes = new byte[length];
        inStream.readFully(stringInBytes);
        return new String(stringInBytes, "UTF-8");
    }
}
