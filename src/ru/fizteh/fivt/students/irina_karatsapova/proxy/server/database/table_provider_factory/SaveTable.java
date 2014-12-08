package ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.table_provider_factory;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.exceptions.ColumnFormatException;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.exceptions.ThreadInterruptException;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.interfaces.Storeable;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.utils.Utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;

public class SaveTable {
    public static void start(MyTable table) throws ThreadInterruptException {
        try {
            if (!table.tablePath.exists()) {
                throw new ThreadInterruptException("save: Dir (" + table.tablePath + ") missed");
            }


            for (int dir = 0; dir < 16; dir++) {
                for (int file = 0; file < 16; file++) {
                    saveFile(table, dir, file);
                }
            }

            deleteEmptyDirs(table);
        } catch (ThreadInterruptException e) {
            throw new ThreadInterruptException("Save: " + e.getMessage());
        }
    }

    private static void saveFile(MyTable table, int dir, int file) throws ThreadInterruptException {
        File dirPath = Paths.get(table.tablePath.toString(), dir + ".dir").toFile();
        File filePath = Paths.get(dirPath.toString(), file + ".dat").toFile();

        if (table.currentKeys()[dir][file].isEmpty()) {
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
                    throw new ThreadInterruptException("Error while creating new file");
                }

            }
            try (DataOutputStream outStream = new DataOutputStream(new FileOutputStream(filePath))) {
                for (String key : table.currentKeys()[dir][file]) {
                    Storeable tableRawValue = table.currentMap().get(key);
                    String stringValue = table.tableProvider.serialize(table, tableRawValue);
                    writeBytes(outStream, key, stringValue);
                }
            } catch (ColumnFormatException e) {
                throw new ThreadInterruptException(e.getMessage());
            } catch (Exception e) {
                throw new ThreadInterruptException("Error while writing into the file" + filePath);
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

    private static void deleteEmptyDirs(MyTable table) throws ThreadInterruptException {
        for (int dir = 0; dir < 16; dir++) {
            File dirPath = Paths.get(table.tablePath.toString(), dir + ".dir").toFile();
            if (dirPath.exists() && dirPath.list().length == 0) {
                Utils.delete(dirPath);
            }
        }
    }
}
