package ru.fizteh.fivt.students.irina_karatsapova.filemap.database;

import ru.fizteh.fivt.students.irina_karatsapova.filemap.utils.DataBaseException;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SaveDataBase {
    public static void start() throws Exception {
        if (!DataBase.file.exists()) {
            throw new DataBaseException("Database: save: Main file is missed");
        } else {
            DataOutputStream outStream = new DataOutputStream(new FileOutputStream(DataBase.file));
            try {
                for (String key : DataBase.keys) {
                    if (DataBase.map.containsKey(key)) {
                        writeBytes(outStream, key, DataBase.map.get(key));
                    }
                }
            } catch (Exception e) {
                throw new DataBaseException("DataBase: save: Error while writing the file");
            } finally {
                try {
                    outStream.close();
                } catch (IOException e) {
                    throw new DataBaseException("DataBase: save: Error while closing the file");
                }
            }
        }
    }

    public static void writeBytes(DataOutputStream outStream, String key, String value) throws Exception {
        byte[] keyInBytes = key.getBytes("UTF-8");
        byte[] valueInBytes = value.getBytes("UTF-8");
        outStream.writeInt(keyInBytes.length);
        outStream.write(keyInBytes);
        outStream.writeInt(valueInBytes.length);
        outStream.write(valueInBytes);
        outStream.flush();
    }
}
