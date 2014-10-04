package ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap;

import ru.fizteh.fivt.students.LebedevAleksey.FileMap.LoadOrSaveError;

import java.io.File;
import java.nio.file.Path;

public class TablePart extends ru.fizteh.fivt.students.LebedevAleksey.FileMap.TablePart {
    private Table table;
    private int folderNum;
    private int fileNum;

    public TablePart(Table parentTable, int folderNumber, int fileNumber) {
        super();
        table = parentTable;
        folderNum = folderNumber;
        fileNum = fileNumber;
    }


    @Override
    public void loadWork() throws LoadOrSaveError {
        File file = getSaveFilePath();
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new DatabaseFileStructureException("'" + file.getAbsolutePath() + "' is directory");
            } else {
                super.loadWork();
            }
            for (String key : data.keySet()) {
                if (table.getPartForKey(key) != this) {
                    throw new LoadOrSaveError("Wrong key '" + key + "' in file " + file.getAbsolutePath());
                }
            }
        }
    }

    @Override
    public void save() throws LoadOrSaveError {
        super.save();
    }

    @Override
    public File getSaveFilePath() throws DatabaseFileStructureException {
        return getFolderPath().resolve(fileNum + ".dat").toFile();
    }

    private Path getFolderPath() throws DatabaseFileStructureException {
        return table.getDirectory().resolve(folderNum + ".dir");
    }
}
