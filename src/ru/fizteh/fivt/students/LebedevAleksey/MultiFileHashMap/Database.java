package ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap;

import ru.fizteh.fivt.students.LebedevAleksey.FileMap.LoadOrSaveError;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

public class Database {
    protected ArrayList<Table> tables = new ArrayList<>();
    protected Table currentTable = null;

    public Database() throws DatabaseFileStructureException, LoadOrSaveError {
        load();
    }

    public File getRootDirectory() throws DatabaseFileStructureException {
        String directoryPath = System.getProperty("fizteh.db.dir");
        if (directoryPath == null) {
            throw new DatabaseFileStructureException("Database directory doesn't set.");
        } else {
            return new File(directoryPath);
        }
    }

    public final Path getRootDirectoryPath() throws DatabaseFileStructureException {
        return getRootDirectory().toPath();
    }

    public void load() throws LoadOrSaveError {
        File root = getRootDirectory();
        try {
            if (root.exists() && root.isDirectory()) {
                File[] subfolders = getTablesFromRoot(root);
                for (File folder : subfolders) {
                    tables.add(new Table(folder.getName(), this));
                }
            } else {
                throw new DatabaseFileStructureException("Root directory not found.");
            }
        } catch (SecurityException ex) {
            throw new LoadOrSaveError("Error in loading, access denied: " + ex.getMessage(), ex);
        }
    }

    protected File[] getTablesFromRoot(File root) throws DatabaseFileStructureException {
        File[] subfolders = root.listFiles();
        for (File folder : subfolders) {
            if (!folder.isDirectory()) {
                throw new DatabaseFileStructureException("There is files in root folder. File'" + folder.getName()
                        + "'");
            }
        }
        return subfolders;
    }
}
