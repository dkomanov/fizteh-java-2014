package ru.fizteh.fivt.students.RadimZulkarneev.DataBase;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ru.fizteh.fivt.storage.strings.*;

public class TableRealize implements Table {
    private Path tablePath;
    private Map<String, FileMap> data;
    private Map<String, String> diff;
    private static final String DIRNAME = "(0[0-9]|1[0-5])\\.dir";
    private static final String FILENAME = "(0[0-9]|1[0-5])\\.dat";
    private static final int SIXTEEN = 16;
    private static final int TEN = 10;
    private static final int FOUR = 4;

    public TableRealize(Path tablePath) throws Exception {
        this.tablePath = tablePath;
        data = new HashMap<>();
        diff = new HashMap<>();
        loadTable();
    }

    private void loadTable() throws Exception {

        String[] dirList = tablePath.toFile().list();
        if (dirList ==  null) {
            tablePath.toFile().mkdir();
        }
        if (!tablePath.toFile().isDirectory()) {
            throw new RuntimeException("Path is not directory " + tablePath);
        }
        for (String dir : tablePath.toFile().list()) {
            Path currentDir = tablePath.resolve(dir);
            if (!dir.matches(DIRNAME) || !Files.isDirectory(currentDir)) {
                throw new RuntimeException("Directory contains non-directory files");
            }
            String[] fileList = currentDir.toFile().list();

            if (fileList.length == 0) {
                throw new RuntimeException("Directory '" + dir + "' is empty.", null);
            }

            for (String file : fileList) {
                Path filePath = currentDir.resolve(file);
                if (!file.matches(FILENAME)
                        || !filePath.toFile().isFile()) {
                    throw new RuntimeException("File '" + file + "' in directory '" + dir
                            + "' is not a file or doesn't match required name '[0-15].dat'", null);
                }
                FileMap newFileMap = new FileMap(filePath);
                if (newFileMap.size() == 0) {
                    //newFileMap.close();
                    newFileMap.close();
                    throw new RuntimeException("Table contains empty files");
                }
                newFileMap.close();
                data.put(newFileMap.fileMapCode(), newFileMap);
            }
        }
    }

    @Override
    public final String getName() {
        // TODO Auto-generated method stub
        return tablePath.getFileName().toString();
    }

    @Override
    public final String get(String key) {
        nameIsNullAssertion(key);
        if (diff.containsKey(key)) {
            return diff.get(key);
        } else {
            String keyFileMap = keyDestination(key);
            FileMap fileMap = data.get(keyFileMap);
            if (fileMap == null) {
                return null;
            } else {
                return fileMap.get(key);
            }
        }
    }

    private String keyDestination(String key) {
        int hashcode = Math.abs(key.hashCode());
        int nDir = hashcode % SIXTEEN;
        int nFile = hashcode / SIXTEEN % SIXTEEN;
        String retVal = "";
        if (nDir < TEN) {
            retVal += ("0" + nDir);
        } else {
            retVal += nDir;
        }
        if (nFile < TEN) {
            retVal += ("0" + nFile);
        } else {
            retVal += nFile;
        }
        return retVal;
    }

    @Override
    public String put(String key, String value) {
        // TODO Auto-generated method stub
        nameIsNullAssertion(key);
        nameIsNullAssertion(value);
        if (diff.containsKey(key)) {
            return diff.put(key, value);
        } else {
            String keyFileMap = keyDestination(key);
            FileMap fileMap = data.get(keyFileMap);
            if (fileMap == null) {
                diff.put(key, value);
                return null;
            } else {
                String currentValue = fileMap.get(key);
                diff.put(key, value);
                return currentValue;
            }
        }
    }

    @Override
    public String remove(String key) {
        nameIsNullAssertion(key);
        if (diff.containsKey(key)) {
            return diff.remove(key);
        } else {
            FileMap fileMap = data.get(keyDestination(key));
            if (fileMap == null) {
                return null;
            } else {
                diff.put(key, null);
                return fileMap.get(key);
            }
        }
    }

    @Override
    public int size() {
        // TODO Auto-generated method stub
        int size = 0;
        for (Entry<String, FileMap> entry : data.entrySet()) {
            size += entry.getValue().size();
        }
        for (Entry<String, String> entry : diff.entrySet()) {
            if (entry.getValue() == null) {
                size--;
            } else {
                size++;
            }
        }
        return size;
    }

    @Override
    public int commit() {
        int savedKeyCount = diff.size();
        for (Entry<String, String> entry : diff.entrySet()) {
            String currentKeyDestination = keyDestination(entry.getKey());
            FileMap fileMap = data.get(currentKeyDestination);
            if (entry.getValue() == null) {
                fileMap.remove(entry.getKey());
            } else {
                if (fileMap == null) {
                    try {
                        if (!Files.exists(tablePath.resolve(getDirName(currentKeyDestination)))) {
                            Files.createDirectory(tablePath.resolve(getDirName(currentKeyDestination)));
                        }
                        fileMap = new FileMap(Paths.get(tablePath.resolve(
                                getDirName(currentKeyDestination)).resolve(
                                        getFileName(currentKeyDestination)).toString()));
                    } catch (IOException ex) {
                        // It's abnormally.
                    }
                }
                fileMap.put(entry.getKey(), entry.getValue());
                try {
                    fileMap.close();
                } catch (Exception e) {
                    throw new RuntimeException();
                }
            }
        }

        diff.clear();
        try {
            saveTable();
            for (File directory : tablePath.toFile().listFiles()) {
                if (directory.list().length == 0) {
                    directory.delete();
                }
            }
        } catch (Exception ex) {
            // It's abnormally.
            throw new RuntimeException("Some incomprehensible errors in commiting of the table");
        }
        return savedKeyCount;
    }

    private void saveTable() throws Exception {
        Iterator<Entry<String, FileMap>> iterator = data.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, FileMap> fileMap = iterator.next();
            fileMap.getValue().close();
            if (fileMap.getValue().size() == 0) {
                Files.deleteIfExists(fileMap.getValue().getPath());
            }
        }
    }

    @Override
    public int rollback() {
        int retValue = diff.size();
        diff.clear();
        return retValue;
    }

    @Override
    public List<String> list() {
        List<String> set = new LinkedList<>();
        for (Entry<String, FileMap> entry : data.entrySet()) {
            set.addAll(entry.getValue().keySet());
        }
        for (Entry<String, String> entry : diff.entrySet()) {
            if (entry.getValue() == null) {
                set.remove(entry.getKey());
            } else {
                set.add(entry.getKey());
            }
        }
        List<String> list = new LinkedList<>();
        list.addAll(set);
        return list;
    }

    public void drop() throws IOException {
        String[] directoryList = tablePath.toFile().list();
        for (String directory : directoryList) {
            String[] directoryContent = tablePath.resolve(directory).toFile().list();
            for (String currentFileMap : directoryContent) {
                Files.delete(tablePath.resolve(directory).resolve(currentFileMap));
            }
            Files.delete(tablePath.resolve(directory));
        }
        Files.delete(tablePath);
    }

    private void nameIsNullAssertion(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
    }

    private String getDirName(String dest) {
            return new String(dest.substring(0, 2) + ".dir");
    }

    private String getFileName(String dest) {
            return new String(dest.substring(2, FOUR) + ".dat");
    }

    public int getUncommitedChanges() {
        return diff.size();
    }

}

