package ru.fizteh.fivt.students.MaksimovAndrey.MultiFileMap;

import ru.fizteh.fivt.students.MaksimovAndrey.MultiFileMap.FileMapForMultiFileMap.DataBase;
import ru.fizteh.fivt.students.MaksimovAndrey.MultiFileMap.FileMapForMultiFileMap.Put;

import java.io.File;

public class PutMulti extends Command {
    private final String key;
    private final String value;

    public PutMulti(String passedKey, String passedValue) {
        key = passedKey;
        value = passedValue;
    }

    @Override
    public void startNeedMultiInstruction(DataBaseDir base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("ERROR: No such table");
        } else {
            int hashCode = key.hashCode();
            int dir = hashCode % 16;
            int file = hashCode / 16 % 16;

            String[] s = new String[3];
            s[0] = "put";
            s[1] = key;
            s[2] = value;

            Put put = new Put();

            if (base.getUsing().databases[dir][file] == null) {
                File subdirectory = new File(base.getUsing().mainDirectory, String.valueOf(dir) + ".dir");
                if (!subdirectory.exists()) {
                    if (!subdirectory.mkdir()) {
                        throw new Exception("ERROR: Problems with the creation of a directory");
                    }
                }
                File dbFile = new File(subdirectory, String.valueOf(file) + ".dat");
                if (!dbFile.exists()) {
                    if (!dbFile.createNewFile()) {
                        throw new Exception("ERROR: Problems with the creation of a directory");
                    }
                }
                base.getUsing().databases[dir][file] = new DataBase(dbFile.toString());
            }
            put.startNeedInstruction(s, base.getUsing().databases[dir][file]);
        }
    }
}
