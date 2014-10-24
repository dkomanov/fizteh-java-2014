package ru.fizteh.fivt.students.isalysultan.MultiFileHashMap;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Table {

    private Path TableDirectory;

    private int numberRecords;

    private Map<Integer, FileTable> files;

    private HashMap<Integer, Path> subDirectsMap;

    public Table() {
        // Disable instantiation to this class.
    }

    void read() throws IOException {
        files = new HashMap<Integer, FileTable>();
        String[] subDirects = TableDirectory.toFile().list();
        for (String nameSubDirect : subDirects) {
            Path subDirect = TableDirectory.resolve(nameSubDirect);
            int numberDirectory;
            int numberFile;
            if (!subDirect.toFile().isDirectory()
                    || !nameSubDirect.matches("([0-9]|1[0-5])\\.dir")) {
                System.err
                        .println("Error, subdirectory in table is file, but it is wrong.");
                System.exit(1);
            }
            String[] filesList = subDirect.toFile().list();
            if (filesList.length == 0) {
                System.err.println("Direct not delete.");
                System.exit(2);
            }
            numberDirectory = Integer.parseInt(nameSubDirect.substring(0,
                    nameSubDirect.length() - 4));
            subDirectsMap.put(numberDirectory, subDirect);
            for (String fileName : filesList) {
                Path filePath = subDirect.resolve(fileName);
                if (!filePath.toFile().isFile()
                        || !fileName.matches("([0-9]|1[0-5])\\.dat")) {
                    System.err.println("filePath.File() is not file.");
                    System.exit(3);
                }
                numberFile = Integer.parseInt(fileName.substring(0,
                        fileName.length() - 4));
                Integer numberFileMap = numberDirectory * 16 + numberFile;
                FileTable currentFileTable = new FileTable(filePath);
                files.put(numberFileMap, currentFileTable);
            }
        }

    }

    public Table(RootDirectory direct, String tableName) throws IOException {
        TableDirectory = direct.get().resolve(tableName);
        TableDirectory.toFile().mkdir();
        Set<Integer> numberSubDirect = subDirectsMap.keySet();
        for (Integer key : numberSubDirect) {
            subDirectsMap.put(key, null);
        }
        if (!TableDirectory.toFile().isDirectory()) {
            System.err.println("Directory doesn't exist.");
            return;
        }
        System.out.println("created");
    }

    public int get() {
        return numberRecords;
    }

    public boolean equalityTable(Table argv) {
        if (TableDirectory.equals(argv.TableDirectory)) {
            return true;
        }
        return false;
    }

    public void dropTable() {
        String[] subDirects = TableDirectory.toFile().list();
        if (subDirects.length == 0) {
            TableDirectory.toFile().delete();
            return;
        }
        for (String subDirect : subDirects) {
            Path subDirectPath = TableDirectory.resolve(subDirect);
            String[] fileList = subDirectPath.toFile().list();
            if (fileList.length == 0) {
                subDirectPath.toFile().delete();
            } else {
                for (String fileString : fileList) {
                    Path filePath = subDirectPath.resolve(fileString);
                    filePath.toFile().delete();
                }
                subDirectPath.toFile().delete();
            }
        }
        TableDirectory.toFile().delete();
    }

    public void incrementNumberRecords() {
        ++numberRecords;
    }

    public void tableOperationPut(String key, String value)
            throws UnsupportedEncodingException {
        byte externalKey = key.getBytes("UTF-8")[0];
        int ndirectory = externalKey % 16;
        int nfile = (externalKey / 16) % 16;
        int numberFile = ndirectory * 16 + nfile;
        FileTable currTable = files.get(numberFile);
        CommandForMap.put(key, value, currTable, this);
    }

    public void tableOperationGet(String key)
            throws UnsupportedEncodingException {
        byte externalKey = key.getBytes("UTF-8")[0];
        int ndirectory = externalKey % 16;
        int nfile = (externalKey / 16) % 16;
        int numberFile = ndirectory * 16 + nfile;
        FileTable currTable = files.get(numberFile);
        CommandForMap.get(key, currTable);
    }

    public void tableOperationRemove(String key)
            throws UnsupportedEncodingException {
        byte externalKey = key.getBytes("UTF-8")[0];
        int ndirectory = externalKey % 16;
        int nfile = (externalKey / 16) % 16;
        int numberFile = ndirectory * 16 + nfile;
        FileTable currTable = files.get(numberFile);
        CommandForMap.remove(key, currTable);
    }

    public void tableOperationList() {
        // Derive all the keys of the table.
        Set<Integer> keys = files.keySet();
        for (Integer key : keys) {
            FileTable table = files.get(key);
            if (!table.empty()) {
                CommandForMap.list(table);
            }
        }
    }

    public void write() throws IOException {
        Set<Integer> keys = files.keySet();
        int countDir = 0;
        boolean createDir = false;
        boolean endDir = false;
        Path endDirectory = null;
        Path subDirectPath = null;
        for (Integer key : keys) {
            if (countDir != key % 16) {
                if (!createDir && endDir) {
                    endDirectory.toFile().delete();
                }
                countDir = key % 16;
                createDir = false;
            }
            FileTable currentFileTable = files.get(key);
            if (currentFileTable.getOpenRead()) {
                endDir = true;
                endDirectory = subDirectsMap.get((key % 16));
                if (currentFileTable.emptyMap()) {
                    Path deletePath = currentFileTable.getPath();
                    deletePath.toFile().delete();
                } else {
                    // This is main,that subDir exist.
                    createDir = true;
                    subDirectPath = subDirectsMap.get(countDir);
                    currentFileTable.writeFile();
                }
            } else {
                if (!currentFileTable.emptyMap()) {
                    if (createDir) {
                        String fileId = Integer.toString((key / 16) % 16);
                        fileId += ".";
                        fileId += "dat";
                        Path arg = subDirectPath.resolve(fileId);
                        currentFileTable.changePath(arg);
                        arg.toFile().createNewFile();
                        currentFileTable.writeFile();
                    } else {
                        createDir = true;
                        String dirId = Integer.toString(countDir);
                        dirId += ".";
                        dirId += "dir";
                        subDirectPath = TableDirectory.resolve(dirId);
                        subDirectPath.toFile().mkdir();
                        String fileId = Integer.toString((key / 16) % 16);
                        fileId += ".";
                        fileId += "dat";
                        Path arg = subDirectPath.resolve(fileId);
                        currentFileTable.changePath(arg);
                        arg.toFile().createNewFile();
                        currentFileTable.writeFile();
                    }
                }
            }
        }
    }
}
