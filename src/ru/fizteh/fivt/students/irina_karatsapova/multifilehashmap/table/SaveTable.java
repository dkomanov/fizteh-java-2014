package ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.table;

import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.DataBase;
import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.utils.Utils;
import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.utils.TableException;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class SaveTable {
    public static void start() throws TableException, IOException {
        if (!Table.loaded) {
            return;
        }

        if (!Table.dir.exists()) {
            throw new TableException("save: Dir (" + Table.dir + ") missed");
        }


        for (int dir = 0; dir < 16; dir++) {
            for (int file = 0; file < 16; file++) {
                saveFile(dir, file);
            }
        }

        DataBase.saveInf(Table.dir);

        deleteEmptyDirs();

        Table.loaded = false;
    }

    private static void saveFile(int dir, int file) throws TableException {
        File dirPath = Paths.get(Table.dir.toString(), dir + ".dir").toFile();
        File filePath = Paths.get(dirPath.toString(), file + ".dat").toFile();

        if (Table.keys[dir][file].isEmpty()) {
            if (filePath.exists()) {
                Utils.delete(filePath);
            }
        } else {
            if (!dirPath.exists()) {
                dirPath.mkdir();
            }
            if (!filePath.exists()) {
                try {
                    filePath.createNewFile();
                } catch (Exception e) {
                    throw new TableException("save: Error while creating new file");
                }

            }
            try (DataOutputStream outStream =  new DataOutputStream(new FileOutputStream(filePath))) {
                for (String key : Table.keys[dir][file]) {
                    writeBytes(outStream, key, Table.map.get(key));
                }
            } catch (Exception e) {
                throw new TableException("save: Error while writing into the file");
            }
        }
    }

    private static void writeBytes(DataOutputStream outStream, String key, String value) throws Exception {
        byte[] keyInBytes = key.getBytes("UTF-8");
        byte[] valueInBytes = value.getBytes("UTF-8");
        outStream.writeInt(keyInBytes.length);
        outStream.write(keyInBytes);
        outStream.writeInt(valueInBytes.length);
        outStream.write(valueInBytes);
        outStream.flush();
    }

    private static void deleteEmptyDirs() throws TableException {
        for (int dir = 0; dir < 16; dir++) {
            File dirPath = Paths.get(Table.dir.toString(), dir + ".dir").toFile();
            if (dirPath.exists() && dirPath.list().length == 0) {
                Utils.delete(dirPath);
            }
        }
    }
}
