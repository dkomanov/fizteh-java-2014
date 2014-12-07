package ru.fizteh.fivt.students.torunova.storeable;
import ru.fizteh.fivt.students.torunova.storeable.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.storeable.exceptions.TableNotCreatedException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by nastya on 19.10.14.
 */
public class TableImpl implements ru.fizteh.fivt.storage.strings.Table{
    private static final int NUMBER_OF_PARTITIONS = 16;
    private static final String DIRECTORY_PATTERN = "([0-9]|1[0-5])\\.dir";
    private static final String FILE_PATTERN = "([0-9]|1[0-5])\\.dat";
    String tableName;
    private Map<File, FileMap> files = new HashMap<>();
    int numberOfEntries;

    @Override
    public boolean equals(Object t) {
        if (!(t instanceof TableImpl)) {
            return false;
        }
        TableImpl table = (TableImpl) t;
        return tableName.equals(table.tableName)
                && files.equals(table.files)
                && numberOfEntries == table.numberOfEntries;
    }

    @Override
    public int hashCode() {
        return tableName.hashCode();
    }

    public TableImpl(String newTableName) throws TableNotCreatedException, IncorrectFileException, IOException {
        File table = new File(newTableName).getAbsoluteFile();
        checkTableName(table.getName());
        if (!table.exists()) {
            if (!table.mkdirs()) {
                throw new TableNotCreatedException();
            }
        }
        tableName = table.getAbsolutePath();
        File[] tableFiles = table.listFiles();
        File[] filesOfDir;
        if (tableFiles != null) {
            for (File nextDir : tableFiles) {
                if (!(Pattern.matches(DIRECTORY_PATTERN, nextDir.getName()) && nextDir.isDirectory())
                        && !(nextDir.isFile() && nextDir.getName().equals("signature.tsv"))) {
                    throw new TableNotCreatedException("Table " + getName()
                            + " contains illegal files: " + nextDir.getAbsolutePath());
                }
                filesOfDir = nextDir.listFiles();
                if (filesOfDir != null) {
                    for (File nextFile : filesOfDir) {
                        if (!(Pattern.matches(FILE_PATTERN, nextFile.getName()) && nextFile.isFile())) {
                            throw new TableNotCreatedException("Table " + getName()
                                    + " contains illegal files: " + nextFile.getAbsolutePath());
                        }
                        FileMap fm = new FileMap(nextFile.getAbsolutePath());
                        if (fm.isEmpty()) {
                            nextFile.delete();
                            nextDir = nextFile.getParentFile();
                            File[] nextDirFiles = nextDir.listFiles();
                            if (nextDirFiles != null && nextDirFiles.length == 0) {
                                nextDir.delete();
                            }
                        } else {
                            files.put(nextFile, fm);
                            numberOfEntries += fm.size();
                        }
                    }
                }
            }
        }
    }
    @Override
    public String put(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key or value is null");
        }
        String result;
        String fileName = getFileName(key);
        String dirName = getDirName(key);
        File dir  = new File(tableName, dirName).getAbsoluteFile();
        File file = new File(dir, fileName).getAbsoluteFile();
        if (files.containsKey(file)) {
            result = files.get(file).put(key, value);
        } else {
                file.getParentFile().mkdirs();
            FileMap fm;
            try {
                file.createNewFile();
                fm = new FileMap(file.getAbsolutePath());
            } catch (IOException | IncorrectFileException e) {
                throw new RuntimeException(e);
            }
                result = fm.put(key, value);
                files.put(file, fm);
        }
        if (result == null) {
            numberOfEntries++;
        }
        return result;
    }

    @Override
    public String getName() {
        File ourTable = new File(tableName);
        return ourTable.getName();
    }

    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }
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

    @Override
    public String remove(String key) {
        String result;
        String fileName = getFileName(key);
        String dirName = getDirName(key);
        File dir  = new File(tableName, dirName).getAbsoluteFile();
        File file = new File(dir, fileName).getAbsoluteFile();
        if (files.containsKey(file)) {
            FileMap fm = files.get(file);
            result = fm.remove(key);
            numberOfEntries--;
            return result;
        }
        return null;
    }

    @Override
    public int size() {
        return numberOfEntries;
    }

    public List<String> list() {
        List<String> listOfAllKeys = new ArrayList<>();
        for (FileMap fm : files.values()) {
            listOfAllKeys.addAll(fm.list());
        }
        return listOfAllKeys;
    }
    @Override
    public int commit()  {
        int numberOfChangedEntries = 0;
        Set<Map.Entry<File, FileMap>> entrySet = new HashSet<>(files.entrySet());
        IOException e = null;
        for (Map.Entry<File, FileMap> entry : entrySet) {
            FileMap fm = entry.getValue();
            File file = entry.getKey();
            try {
                numberOfChangedEntries += fm.commit();
            } catch (IOException e1) {
                e = e1;
                continue;
            }
            if (fm.isEmpty()) {
                File directory = file.getParentFile().getAbsoluteFile();
                file.delete();
                File[] dirFiles = directory.listFiles();
                if (dirFiles != null && dirFiles.length == 0) {
                    directory.delete();
                }
                files.remove(file);
            }
        }
        if (e != null) {
            throw new RuntimeException(e);
        }
        return numberOfChangedEntries;
    }

    @Override
    public int rollback() {
        int numberOfRevertedChanges = 0;
        numberOfEntries = 0;
        for (FileMap fm:files.values()) {
            numberOfRevertedChanges += fm.rollback();
            numberOfEntries += fm.size();
        }
        return numberOfRevertedChanges;
    }

    public int countChangedEntries() {
        int numberOfChangedEntries = 0;
        for (FileMap fm:files.values()) {
            numberOfChangedEntries += fm.countChangedEntries();
        }
        return numberOfChangedEntries;
    }

    private String getDirName(String key) {
        int hashcode = Math.abs(key.hashCode());
        int ndirectory = hashcode % NUMBER_OF_PARTITIONS;
        StringBuilder builder = new StringBuilder();
        builder.append(ndirectory).append(".dir");
        return builder.toString();
    }

    private String getFileName(String key) {
        int hashcode = Math.abs(key.hashCode());
        int nfile = hashcode / NUMBER_OF_PARTITIONS % NUMBER_OF_PARTITIONS;
        StringBuilder builder = new StringBuilder();
        builder.append(nfile).append(".dat");
        return  builder.toString();

    }
    private void checkTableName(String name) {
        if (name == null || Pattern.matches(".*" + Pattern.quote(File.separator) + ".*", name)
                || name.equals("..") || name.equals(".")) {
            throw new IllegalArgumentException("illegal table name");
        }
    }

}
