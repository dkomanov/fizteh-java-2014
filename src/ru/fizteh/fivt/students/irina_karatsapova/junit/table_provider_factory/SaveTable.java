package ru.fizteh.fivt.students.irina_karatsapova.junit.table_provider_factory;

import ru.fizteh.fivt.students.irina_karatsapova.junit.utils.TableException;
import ru.fizteh.fivt.students.irina_karatsapova.junit.utils.Utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;

public class SaveTable {
    public static void start(MyTable table) throws TableException {
        if (!table.loaded) {
            return;
        }

        if (!table.tablePath.exists()) {
            throw new TableException("save: Dir (" + table.tablePath + ") missed");
        }


        for (int dir = 0; dir < 16; dir++) {
            for (int file = 0; file < 16; file++) {
                saveFile(table, dir, file);
            }
        }

        deleteEmptyDirs(table);
    }

    private static void saveFile(MyTable table, int dir, int file) throws TableException {
        File dirPath = Paths.get(table.tablePath.toString(), dir + ".dir").toFile();
        File filePath = Paths.get(dirPath.toString(), file + ".dat").toFile();

        if (table.currentKeys[dir][file].isEmpty()) {
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
            DataOutputStream outStream = null;
            try {
                outStream = new DataOutputStream(new FileOutputStream(filePath));
                for (String key : table.currentKeys[dir][file]) {
                    writeBytes(outStream, key, table.currentMap.get(key));
                }
            } catch (Exception e) {
                throw new TableException("save: Error while writing into the file");
            } finally {
                try {
                    if (outStream != null) {
                        outStream.close();
                    }
                } catch (Exception e) {
                    throw new TableException("save: Error while closing the file");
                }
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

    private static void deleteEmptyDirs(MyTable table) throws TableException {
        for (int dir = 0; dir < 16; dir++) {
            File dirPath = Paths.get(table.tablePath.toString(), dir + ".dir").toFile();
            if (dirPath.exists() && dirPath.list().length == 0) {
                Utils.delete(dirPath);
            }
        }
    }
}
