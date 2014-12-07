package ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap;

import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.FileMap.ATable;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.FileMap.FileMapReader;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.FileMap.FileMapWriter;

import java.util.HashSet;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class MultiFileTable extends ATable {
    private static final int MAX_DIRECTORIES_NUMBER = 16;
    private static final int MAX_FILES_NUMBER = 16;

    public MultiFileTable(final String directory,
                          final String tableName) {
        super(directory, tableName);
    }

    protected final void save() throws IOException {
        File tableDirectory = getTableDirectory();
        ArrayList<Set<String>> unsavedKeys = new ArrayList<Set<String>>();
        boolean fileIsEmpty;

        for (int currentFileNumber = 0;
             currentFileNumber < MAX_DIRECTORIES_NUMBER;
             ++currentFileNumber) {
            String currentFileName =
                    String.format("%d.dir", currentFileNumber);
            File currentFileDirectory =
                    new File(tableDirectory, currentFileName);

            unsavedKeys.clear();
            for (int i = 0; i < MAX_FILES_NUMBER; ++i) {
                unsavedKeys.add(new HashSet<String>());
            }
            fileIsEmpty = true;

            for (final String key : oldData.keySet()) {
                if (getDirNumber(key) == currentFileNumber) {
                    int fileNumber = getFileNumber(key);
                    unsavedKeys.get(fileNumber).add(key);
                    fileIsEmpty = false;
                }
            }

            if (fileIsEmpty) {
                MultiFileHashMapTableProvider.
                        deleteFile(currentFileDirectory);
            }

            for (int fileNumber = 0;
                 fileNumber < MAX_FILES_NUMBER; ++fileNumber) {
                String fileName = String.format("%d.dat", fileNumber);
                File file = new File(currentFileDirectory, fileName);
                if (unsavedKeys.get(fileNumber).isEmpty()) {
                    MultiFileHashMapTableProvider.deleteFile(file);
                    continue;
                }
                if (!currentFileDirectory.exists()) {
                    currentFileDirectory.mkdir();
                }
                FileMapWriter.saveToFile(file.getAbsolutePath(),
                        unsavedKeys.get(fileNumber), oldData);
            }
        }
    }

    protected final void load() throws IOException {
        File tableDirectory = getTableDirectory();
        for (final File dirs : tableDirectory.listFiles()) {
            for (final File file : dirs.listFiles()) {
                FileMapReader.loadFromFile(file.getAbsolutePath(), oldData);
            }
        }
    }

    private File getTableDirectory() {
        File tableDirectory = new File(getDirectory(), getName());
        if (!tableDirectory.exists()) {
            tableDirectory.mkdir();
        }
        return tableDirectory;
    }

    private int getDirNumber(final String key) {
        int hashCode = key.hashCode();
        return hashCode % MAX_DIRECTORIES_NUMBER;
    }

    private int getFileNumber(final String key) {
        int hashCode = key.hashCode();
        return hashCode / MAX_DIRECTORIES_NUMBER % MAX_FILES_NUMBER;
    }
}
