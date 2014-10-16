package ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.table;

import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.DataBase;
import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.Utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class SaveTable {
    public static void start() throws Exception {
        if (!Table.loaded) {
            return;
        }

        if (!Table.dir.exists()) {
            throw new Exception("Database: save: Dir is missed");
        }

        for (int dir = 0; dir < 16; dir++) {
            for (int file = 0; file < 16; file++) {
                saveFile(dir, file);
            }
        }

        deleteEmptyDirs();

        DataBase.saveInf(Table.dir);

        Table.loaded = false;
    }

    private static void saveFile(int dir, int file) throws Exception {
        File dirPath = Utils.makePathAbsolute(Table.dir + "/" + dir + ".dir");
        File filePath = Utils.makePathAbsolute(dirPath.toString() + "/" + file + ".dat");

        if (Table.keys[dir][file].isEmpty()) {
            if (filePath.exists()) {
                Utils.delete(filePath);
            }
        } else {
            if (!dirPath.exists()) {
                dirPath.mkdir();
            }
            if (!filePath.exists()) {
                filePath.createNewFile();
            }
            DataOutputStream outStream = new DataOutputStream(new FileOutputStream(filePath));
            try {
                for (String key : Table.keys[dir][file]) {
                    if (Table.map.containsKey(key)) {
                        writeBytes(outStream, key, Table.map.get(key));
                    }
                }
            } catch (Exception e) {
                throw new Exception("DataBase: save: Error while writing the file");
            } finally {
                try {
                    outStream.close();
                } catch (Exception e) {
                    throw new Exception("DataBase: save: Error while closing the file");
                }
            }
        }
    }

    private static void writeBytes(DataOutputStream outStream, String key, String value) throws Exception {
        byte[] keyInBytes = key.getBytes("UTF-8");
        byte[] valueInBytes = value.getBytes("UTF-8");
        outStream.writeInt(keyInBytes.length);
        outStream.writeInt(valueInBytes.length);
        outStream.write(keyInBytes);
        outStream.write(valueInBytes);
        outStream.flush();
    }

    private static void deleteEmptyDirs() throws Exception {
        for (int dir = 0; dir < 16; dir++) {
            File dirPath = Utils.makePathAbsolute(Table.dir + "/" + dir + ".dir");
            if (dirPath.exists() && dirPath.list().length == 0) {
                Utils.delete(dirPath);
            }
        }
    }
}
