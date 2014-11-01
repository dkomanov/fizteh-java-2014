package ru.fizteh.fivt.students.standy66_new.storage.strings;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by astepanov on 20.10.14.
 */
public class StringTable implements Table {
    private File tableDirectory;
    private Map<File, FileMap> openedFiles;

    public StringTable(File tableDirectory) {
        //TODO: load all files here instead of easy loading
        this.tableDirectory = tableDirectory;
        openedFiles = new HashMap<>();
    }

    @Override
    public String getName() {
        return tableDirectory.getName();
    }

    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        FileMap fm = getFileMapByKey(key);
        if (fm == null) {
            return null;
        } else {
            return fm.get(key);
        }
    }

    @Override
    public String put(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        FileMap fm = getFileMapByKey(key);
        if (fm == null) {
            return null;
        } else {
            return fm.put(key, value);
        }
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        FileMap fm = getFileMapByKey(key);
        if (fm == null) {
            return null;
        } else {
            return fm.remove(key);
        }
    }

    public int unsavedChangesCount() {
        int unsavedChangesCount = 0;
        for (FileMap fm : openedFiles.values()) {
            unsavedChangesCount += fm.unsavedChangesCount();
        }
        return unsavedChangesCount;
    }

    @Override
    public int size() {
        int ans = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                File dir = new File(tableDirectory, String.format("%d.dir", i));
                File file = new File(dir, String.format("%d.dat", j));
                if (openedFiles.get(file) == null) {
                    try {
                        openedFiles.put(file, new FileMap(file));
                    } catch (IOException e) {
                        System.err.println("IOException during opening file");
                        //TODO: do something here
                    }
                }
                ans += openedFiles.get(file).size();
            }
        }
        return ans;
    }

    @Override
    public int commit() {
        int keyChangedCount = 0;
        for (FileMap fm : openedFiles.values()) {
            try {
                keyChangedCount += fm.commit();
            } catch (IOException e) {
                System.err.println("IOException during commit");
                //TODO: do something here
            }
        }
        for (int i = 0; i < 16; i++) {
            File dir = new File(tableDirectory, String.format("%d.dir", i));
            if (dir.exists() && dir.listFiles().length == 0) {
                dir.delete();
            }
        }
        return keyChangedCount;
    }

    @Override
    public int rollback() {
        int keyChangedCount = 0;
        for (FileMap fm : openedFiles.values()) {
            try {
                keyChangedCount += fm.rollback();
            } catch (IOException e) {
                System.err.println("IOException during rollback");
                //TODO: do something here
            }
        }
        return keyChangedCount;
    }

    @Override
    public List<String> list() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                File dir = new File(tableDirectory, String.format("%d.dir", i));
                File file = new File(dir, String.format("%d.dat", j));
                if (openedFiles.get(file) == null) {
                    try {
                        openedFiles.put(file, new FileMap(file));
                    } catch (IOException e) {
                        System.err.println("IOException during opening file");
                        //TODO: do something here
                    }
                }
                list.addAll(openedFiles.get(file).keySet());
            }
        }
        return list;
    }

    private File getFileByKey(String key) {
        int hashcode = key.hashCode();
        int nDirectory = hashcode % 16;
        int nFile = (hashcode / 16) % 16;
        File dir = new File(tableDirectory, String.format("%d.dir", nDirectory));
        File file = new File(dir, String.format("%d.dat", nFile));
        return file;
    }

    private FileMap getFileMapByKey(String key) {
        File f = getFileByKey(key);
        if (openedFiles.get(f) == null) {
            try {
                openedFiles.put(f, new FileMap(f));
            } catch (IOException e) {
                System.err.println("IOException during opening file");
                //TODO: do something here
                return null;
            }
        }
        return openedFiles.get(f);
    }
}
