package ru.fizteh.fivt.students.isalysultan.MultiFileHashMap;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Set;

public class Table {

    private String nameTable;

    private Path tableDirectory;

    private int numberRecords;

    private FileTable[][] files = new FileTable[16][16];

    private HashMap<Integer, Path> subDirectsMap = new HashMap<Integer, Path>();

    public Table() {
        // Disable instantiation to this class.
    }

    String getName() {
        return nameTable;
    }

    void nullNumberRecords() {
        numberRecords = 0;
    }

    void setName(String name) {
        nameTable = name;
    }

    void read() throws IOException {
        String[] subDirects = tableDirectory.toFile().list();
        for (String nameSubDirect : subDirects) {
            Path subDirect = tableDirectory.resolve(nameSubDirect);
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
                // Integer numberFileMap = numberDirectory * 16 + numberFile;
                FileTable currentFileTable = new FileTable(filePath, this);
                files[numberDirectory][numberFile] = currentFileTable;
            }
        }

    }

    public Table(RootDirectory direct, String tableName, boolean dummyArg) {
        tableDirectory = direct.get().resolve(tableName);
        numberRecords = 0;
        nameTable = tableName;
    }

    public Table(RootDirectory direct, String tableName) throws IOException {
        tableDirectory = direct.get().resolve(tableName);
        tableDirectory.toFile().mkdir();
        numberRecords = 0;
        Set<Integer> numberSubDirect = subDirectsMap.keySet();
        for (Integer key : numberSubDirect) {
            subDirectsMap.put(key, null);
        }
        if (!tableDirectory.toFile().isDirectory()) {
            System.err.println("Directory doesn't exist.");
            return;
        }
        System.out.println("created");
    }

    public int get() {
        return numberRecords;
    }

    public boolean equalityTable(Table argv) {
        if (tableDirectory.equals(argv.tableDirectory)) {
            return true;
        }
        return false;
    }

    public void dropTable() {
        String[] subDirects = tableDirectory.toFile().list();
        if (subDirects.length == 0) {
            tableDirectory.toFile().delete();
            return;
        }
        for (String subDirect : subDirects) {
            Path subDirectPath = tableDirectory.resolve(subDirect);
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
        tableDirectory.toFile().delete();
    }

    public void incrementNumberRecords() {
        ++numberRecords;
    }

    public void dicrementNumberRecords() {
        --numberRecords;
    }

    public void tableOperationPut(String key, String value) throws IOException {
        byte externalKey = key.getBytes("UTF-8")[0];
        int ndirectory = (externalKey % 16);
        if (ndirectory < 0) {
            ndirectory = -ndirectory;
        }
        int nfile = (externalKey / 16) % 16;
        if (nfile < 0) {
            nfile = -nfile;
        }
        if (files[ndirectory][nfile] == null) {
            files[ndirectory][nfile] = new FileTable();
        }
        FileTable currTable = files[ndirectory][nfile];
        Path file = tableDirectory.resolve(Integer.toString(ndirectory) + "."
                + "dir");
        file = file.resolve(Integer.toString(ndirectory) + "." + "dat");
        currTable.setPath(file);
        CommandForMap.put(key, value, currTable, this);
    }

    public void tableOperationGet(String key)
            throws UnsupportedEncodingException {
        byte externalKey = key.getBytes("UTF-8")[0];
        int ndirectory = externalKey % 16;
        int nfile = (externalKey / 16) % 16;
        FileTable currTable = files[ndirectory][nfile];
        if (currTable == null) {
            System.out.println("not found");
            return;
        }
        CommandForMap.get(key, currTable);
    }

    public void tableOperationRemove(String key)
            throws UnsupportedEncodingException {
        byte externalKey = key.getBytes("UTF-8")[0];
        int ndirectory = externalKey % 16;
        int nfile = (externalKey / 16) % 16;
        FileTable currTable = files[ndirectory][nfile];
        if (currTable == null) {
            System.out.println("not found");
            return;
        }
        CommandForMap.remove(key, currTable, this);
    }

    public void tableOperationList() {
        // Derive all the keys of the table.
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                if (files[i][j] != null) {
                    CommandForMap.list(files[i][j]);
                }
            }
        }
    }

    public void write() throws IOException {
        for (int i = 0; i < 16; ++i) {
            Path subDirect = tableDirectory;
            subDirect = subDirect.resolve((Integer.toString(i) + "." + "dir"));
            boolean directExist = false;
            for (int j = 0; j < 16; ++j) {
                if (files[i][j] == null) {
                    continue;
                } else if (files[i][j].needToDeleteFile()) {
                    files[i][j].deleteFile();
                } else if (files[i][j].fileOpenAndNotExist()) {
                    directExist = true;
                    files[i][j].writeFile();
                } else if (!files[i][j].open() && !files[i][j].empty()) {
                    if (!directExist) {
                        subDirect.toFile().mkdir();
                        subDirectsMap.put(i, subDirect);
                        directExist = true;
                        Path filePath = subDirect.resolve((Integer.toString(j)
                                + "." + "dat"));
                        files[i][j].setPath(filePath);
                        filePath.toFile().createNewFile();
                        files[i][j].writeFile();
                    } else {
                        Path filePath = subDirectsMap.get(i).resolve(
                                (Integer.toString(j) + "." + "dat"));
                        files[i][j].setPath(filePath);
                        filePath.toFile().createNewFile();
                        files[i][j].writeFile();
                    }
                }
            }
            if (!directExist && subDirect.toFile().exists()) {
                subDirectsMap.get(i).toFile().delete();
            }
        }
    }
}

