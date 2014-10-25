package ru.fizteh.fivt.students.torunova.multifilehashmap;

import ru.fizteh.fivt.students.torunova.multifilehashmap.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.multifilehashmap.exceptions.TableNotCreatedException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by nastya on 19.10.14.
 */
public class Table {
    public static final int MAGIC_NUMBER = 16;
    String tableName;
    Map<File, FileMap> files = new HashMap<>();
    int numberOfEntries;

    public Table(String newTableName) throws TableNotCreatedException, IncorrectFileException, IOException {
        File table = new File(newTableName).getAbsoluteFile();
        if (!table.exists()) {
            if (!table.mkdirs()) {
                throw new TableNotCreatedException();
            }
        }
        File nextDir;
        File nextFile;
        for (int i = 0; i < 16; i++) {
            nextDir = new File(table, String.valueOf(i) + ".dir").getAbsoluteFile();
            if (nextDir.isDirectory()) {
                for (int j = 0; j < 16; j++) {
                    nextFile = new File(nextDir, String.valueOf(j) + ".dat");
                    if (nextFile.isFile()) {
                        FileMap fm = new FileMap(nextFile.getAbsolutePath());
                        files.put(nextFile, fm);
                        numberOfEntries += fm.size();
                    }
                }
            }
        }
        tableName = table.getAbsolutePath();
    }

    public String put(String key, String value) throws IOException, IncorrectFileException {
        String result;
        String fileName = getFileName(key);
        String dirName = getDirName(key);
        File dir  = new File(tableName, dirName).getAbsoluteFile();
        File file = new File(dir, fileName).getAbsoluteFile();
        if (files.containsKey(file)) {
            result = files.get(file).put(key, value);
        } else {
                file.getParentFile().mkdirs();
                file.createNewFile();
                FileMap fm = new FileMap(file.getAbsolutePath());
                result = fm.put(key, value);
                files.put(file, fm);
        }
        if (result == null) {
            numberOfEntries++;
        }
        return result;
    }

    public String get(String key) throws IncorrectFileException, IOException {
        String fileName = getFileName(key);
        String dirName = getDirName(key);
        File dir  = new File(tableName, dirName).getAbsoluteFile();
        File file = new File(dir, fileName).getAbsoluteFile();
        if ((!dir.exists()) || (!file.exists())) {
            return null;
        }
        FileMap fm;
        fm = files.get(file);
        if (fm == null) {
            return null;
        }
        return fm.get(key);
    }

    public boolean remove(String key) {
        boolean result;
        String fileName = getFileName(key);
        String dirName = getDirName(key);
        File dir  = new File(tableName, dirName).getAbsoluteFile();
        File file = new File(dir, fileName).getAbsoluteFile();
        if (files.containsKey(file)) {
            FileMap fm = files.get(file);
            result = fm.remove(key);
            if (fm.isEmpty()) {
                File directory = file.getParentFile().getAbsoluteFile();
                file.delete();
                directory.delete();
                files.remove(file);
            }
            if (result) {
                numberOfEntries--;
            }
            return result;
        }
        return false;
    }

    public Set<String> list() {
        Set<String> listOfAllKeys = new HashSet<>();
        for (FileMap fm : files.values()) {
            listOfAllKeys.addAll(fm.list());
        }
        return listOfAllKeys;
    }

    public void commit() throws IOException {
        for (FileMap fm:files.values()) {
            fm.close();
        }
    }

    private String getDirName(String key) {
        int hashcode = key.hashCode();
        int ndirectory = hashcode % MAGIC_NUMBER;
        StringBuilder builder = new StringBuilder();
        builder.append(ndirectory).append(".dir");
        return builder.toString();
    }

    private String getFileName(String key) {
        int hashcode = key.hashCode();
        int nfile = hashcode / MAGIC_NUMBER % MAGIC_NUMBER;
        StringBuilder builder = new StringBuilder();
        builder.append(nfile).append(".dat");
        return  builder.toString();

    }

}
