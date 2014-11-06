package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;

public class MultiRemoveCommand extends Command {
    private String key;

    public MultiRemoveCommand(String newKey) {
       key = newKey;
    }

    @Override
    public void execute(DataBaseOneDir base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            int hashCode = Math.abs(key.hashCode());
            int ndirectory = hashCode % NUM_DIRECTORIES;
            int nfile = hashCode / NUM_FILES % NUM_FILES;
            RemoveCommand remove = new RemoveCommand(key);
            if (base.getUsing().databases[ndirectory][nfile] == null) {
                System.out.println("not found");
            } else {
                DataBaseOneFile dataBase = base.getUsing().databases[ndirectory][nfile];
                remove.execute(dataBase, false);
                if (dataBase.recordsNumber() == 0) {
                    File dbFile = new File(dataBase.dataBaseFileName);
                    try {
                        Files.delete(dbFile.toPath());
                    } catch (SecurityException | IOException e) {
                        throw new Exception("You cann't delete database file");
                    }
                    base.getUsing().databases[ndirectory][nfile] = null;
                    int k = 0;
                    for (int j = 0; j < 16; j++) {
                        if (base.getUsing().databases[ndirectory][j] == null) {
                            k++;
                        }
                    }
                    if (k == 16) {
                        try {
                            Files.delete(dbFile.getParentFile().toPath());
                        } catch (DirectoryNotEmptyException e) {
                            throw new Exception("You cann't remove directory. There are some subdirectories there");
                        } catch (IOException e) {
                            throw new Exception("You cann't delete database subdirectory");
                        }
                    }
                }
            }
        }
    }
}
