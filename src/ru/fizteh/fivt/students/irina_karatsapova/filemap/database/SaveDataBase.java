package ru.fizteh.fivt.students.irina_karatsapova.filemap.database;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SaveDataBase {
    public static void start() throws Exception {
        if (!DataBase.file.exists()) {
            throw new Exception("File is missed");
        } else {
            DataOutputStream outStream = new DataOutputStream(new FileOutputStream(DataBase.file));
            try {
                for (String key : DataBase.keys) {
                    if (DataBase.map.containsKey(key)) {
                        writeBytes(outStream, key, DataBase.map.get(key));
                    }
                }
            } catch (Exception e) {
                throw new Exception("DataBase: save: Error while writing the file");
            } finally {
                try {
                    outStream.close();
                } catch (IOException e) {
                    throw new Exception("DataBase: save: Error while closing the file");
                }
            }
        }
    }

    public static void writeBytes(DataOutputStream outStream, String key, String value) throws Exception {
        byte[] keyInBytes = key.getBytes("UTF-8");
        byte[] valueInBytes = value.getBytes("UTF-8");
        outStream.writeInt(keyInBytes.length);
        outStream.writeInt(valueInBytes.length);
        outStream.write(keyInBytes);
        outStream.write(valueInBytes);
        outStream.flush();
    }
}
