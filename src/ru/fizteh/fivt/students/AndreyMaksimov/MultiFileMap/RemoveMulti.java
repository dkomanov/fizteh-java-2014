package ru.fizteh.fivt.students.MaksimovAndrey.MultiFileMap;

import ru.fizteh.fivt.students.MaksimovAndrey.MultiFileMap.FileMapForMultiFileMap.DataBase;
import ru.fizteh.fivt.students.MaksimovAndrey.MultiFileMap.FileMapForMultiFileMap.Remove;

import java.io.File;
import java.nio.file.Files;
import java.io.IOException;

public class RemoveMulti extends Command {
    private final String key;

    public RemoveMulti(String passedKey) {
        key = passedKey;
    }

    @Override
    public void startNeedMultiInstruction(DataBaseDir base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            int hashCode = key.hashCode();
            int dir = hashCode % 16;
            int file = hashCode / 16 % 16;

            Remove remove = new Remove();
            if (base.getUsing().databases[dir][file] == null) {
                System.out.println("ERROR: Not found");
            } else {
                DataBase database = base.getUsing().databases[dir][file];

                String[] s = new String[2];
                s[0] = "remove";
                s[1] = key;

                remove.startNeedInstruction(s, database);
                if (database.sizeOfneeddatabase() == 0) {
                    File dbFile = new File(database.dataBaseName);
                    try {
                        Files.delete(dbFile.toPath());
                    } catch (IOException e) {
                        throw new Exception("ERROR: Problems while deleting the file");
                    }
                    base.getUsing().databases[dir][file] = null;
                    int check = 0;
                    for (int j = 0; j < 16; j++) {
                        if (base.getUsing().databases[dir][j] == null) {
                            check++;
                        }
                    }
                    if (check == 16) {
                        try {
                            Files.delete(dbFile.getParentFile().toPath());
                        } catch (IOException e) {
                            throw new Exception("ERROR: Problems while deleting database subdirectory");
                        }
                    }
                }
            }
        }
    }
}


