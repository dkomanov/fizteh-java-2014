package ru.fizteh.fivt.students.AlexanderKhalyapov.Storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DBTable implements Table {
    public static final String DIR_SUFFIX = ".dir";
    public static final String FILE_SUFFIX = ".dat";
    public static final String DIR_TYPE = "Directory ";
    public static final String FILE_TYPE = "File ";

    public static final int START_P = 0;
    public static final int DIR_AMOUNT = 16;
    public static final int FILES_AMOUNT = 16;
    private TableProvider tableProvider;
    private int size;
    private List<Class<?>> signature;
    private Map<String, Storeable> allRecords;
    private Map<String, Storeable> sessionChanges;
    private Path dbPath;
    private String name;

    public DBTable(final Path rootPath, final String name, final TableProvider provider) throws IOException {
        dbPath = rootPath.resolve(name);
        this.name = name;
        allRecords = new HashMap<>();
        sessionChanges = new HashMap<>();
        signature = new ArrayList<>();
        tableProvider = provider;
        readExistingTablesFromDisk();
        size = allRecords.size();
    }

    public DBTable(final Path rootPath, final String name,
                   final Map<String, Storeable> records, List<Class<?>> columnTypes, final TableProvider provider) {
        dbPath = rootPath.resolve(name);
        this.name = name;
        allRecords = records;
        tableProvider = provider;
        sessionChanges = new HashMap<>();
        signature = columnTypes;
        size = allRecords.size();
    }

    public final Path getDBTablePath() {
        return dbPath;
    }

    private void readExistingTablesFromDisk() throws IOException {
        Utility.checkTableDirectoryContent(dbPath);
        Utility.fillSignature(dbPath, signature);
        DirectoryStream.Filter<Path> filter = (Path file) -> Files.isDirectory(file);
        try (DirectoryStream<Path>
                     tableDirectoryStream = Files.newDirectoryStream(dbPath, filter)) {
            for (Path tableSubdirectory : tableDirectoryStream) {
                String tableSubdirectoryName = tableSubdirectory.getFileName().toString();
                int k = tableSubdirectoryName.indexOf('.');
                if ((k < 0) || !(tableSubdirectoryName.endsWith(DIR_SUFFIX))) {
                    throw new DatabaseFormatException(DIR_TYPE
                            + tableSubdirectoryName
                            + Utility.NOT_EXIST_MSG);
                }
                int nDirectory;
                try {
                    nDirectory = Integer.parseInt(tableSubdirectoryName.substring(START_P, k));
                } catch (NumberFormatException n) {
                    throw new DatabaseFormatException(DIR_TYPE
                            + tableSubdirectoryName
                            + Utility.NOT_EXIST_MSG);
                }
                Utility.checkBoundsForFileNames(nDirectory, DIR_SUFFIX, DIR_TYPE);
                try (DirectoryStream<Path> tableSubdirectoryStream
                             = Files.newDirectoryStream(tableSubdirectory)) {
                    boolean empty = true;
                    for (Path file : tableSubdirectoryStream) {
                        empty = false;
                        String fileName = file.getFileName().toString();
                        k = fileName.indexOf('.');
                        if ((k < 0)
                                || !(fileName.
                                endsWith(FILE_SUFFIX))) {
                            throw new DatabaseFormatException(
                                    FILE_TYPE + fileName
                                            + Utility.NOT_EXIST_MSG);
                        }
                        int nFile;
                        try {
                            nFile = Integer.parseInt(fileName.substring(START_P, k));
                        } catch (NumberFormatException n) {
                            throw new DatabaseFormatException(FILE_TYPE
                                    + fileName
                                    + Utility.NOT_EXIST_MSG, n);
                        }
                        Utility.checkBoundsForFileNames(nFile, FILE_SUFFIX, FILE_TYPE);
                        try (RandomAccessFile dbFile =
                                     new RandomAccessFile(
                                             file.toAbsolutePath().toString(), "r")) {
                            if (dbFile.length() > 0) {
                                while (dbFile.getFilePointer()
                                        < dbFile.length()) {
                                    String key = Utility.readUtil(dbFile, fileName);
                                    int expectedNDirectory = Math.abs(key.getBytes(Utility.ENCODING)[0]
                                            % DIR_AMOUNT);
                                    int expectedNFile = Math.abs((key.getBytes(Utility.ENCODING)[0] / DIR_AMOUNT)
                                            % FILES_AMOUNT);
                                    String value = Utility.readUtil(dbFile, fileName);
                                    if (expectedNDirectory == nDirectory
                                            && expectedNFile == nFile) {
                                        allRecords.put(key, tableProvider.deserialize(this, value));
                                    } else {
                                        throw new DatabaseFormatException(Utility.WRONG_LOCATION_MSG);
                                    }
                                }
                            } else {
                                throw new DatabaseFormatException(FILE_TYPE
                                        + fileName
                                        + Utility.NOT_EXIST_MSG);
                            }
                        }
                    }
                    if (empty) {
                        throw new DatabaseFormatException(
                                DIR_TYPE + tableSubdirectoryName + Utility.NOT_EXIST_MSG);
                    }
                }
            }
        } catch (ParseException e) {
            throw new DatabaseFormatException(name
                    + Utility.SIGNATURE_CONFLICT_MSG, e);
        } catch (IndexOutOfBoundsException | ColumnFormatException e) {
            throw new DatabaseFormatException(name
                    + Utility.SIGNATURE_CONFLICT_MSG
                    + e.getMessage(), e);
        }
    }

    private void writeTablesToDisk() throws IOException {
        Map<String, String>[][] db = new Map[DIR_AMOUNT][FILES_AMOUNT];
        for (int i = 0; i < DIR_AMOUNT; i++) {
            for (int j = 0; j < FILES_AMOUNT; j++) {
                db[i][j] = new HashMap<>();
            }
        }

        for (Map.Entry<String, Storeable> entry : allRecords.entrySet()) {
            String key = entry.getKey();
            String value = tableProvider.serialize(this, entry.getValue());
            try {
                int nDirectory = Math.abs(key.getBytes(Utility.ENCODING)[0]
                        % DIR_AMOUNT);
                int nFile = Math.abs((key.getBytes(Utility.ENCODING)[0] / DIR_AMOUNT)
                        % FILES_AMOUNT);
                db[nDirectory][nFile].put(key, value);
            } catch (UnsupportedEncodingException e) {
                throw new IOException(e.getMessage(), e);
            }
        }
        for (int i = 0; i < DIR_AMOUNT; i++) {
            for (int j = 0; j < FILES_AMOUNT; j++) {
                if (!db[i][j].isEmpty()) {
                    int nDirectory = i;
                    int nFile = j;
                    Path newPath = dbPath.resolve(nDirectory + DIR_SUFFIX);
                    if (!Files.exists(newPath)) {
                        Files.createDirectory(newPath);
                    }
                    Path newFilePath = newPath.
                            resolve(nFile + FILE_SUFFIX);
                    Files.deleteIfExists(newFilePath);
                    Files.createFile(newFilePath);
                    try (RandomAccessFile dbFile = new
                            RandomAccessFile(newFilePath.toFile(), "rw")) {
                        dbFile.setLength(0);
                        for (Map.Entry<String, String> entry
                                : db[i][j].entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            Utility.writeUtil(key, dbFile);
                            Utility.writeUtil(value, dbFile);
                        }
                    }
                } else {
                    /*
                    *Deleting empty files and directories.
                    */
                    int nDirectory = i;
                    int nFile = j;
                    Path newPath = dbPath.resolve(nDirectory + DIR_SUFFIX);
                    if (Files.exists(newPath)) {
                        Path newFilePath = newPath.
                                resolve(nFile + FILE_SUFFIX);
                        Files.deleteIfExists(newFilePath);
                        if (newPath.toFile().list().length == 0) {
                            Files.delete(newPath);
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<String> list() {
        Map<String, Storeable> relevantMap = makeRelevantVersion();
        List<String> list = new ArrayList<>();
        list.addAll(relevantMap.keySet());
        return list;
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        Utility.checkIfObjectsNotNull(key);
        Utility.checkIfObjectsNotNull(value);

        if (sessionChanges.containsKey(key)) {
            return sessionChanges.put(key, value);
        }

        if (allRecords.containsKey(key)) {
            sessionChanges.put(key, value);
            size += 1;
            return allRecords.get(key);
        }

        size += 1;
        return sessionChanges.put(key, value);
    }

    @Override
    public Storeable remove(String key) {
        Utility.checkIfObjectsNotNull(key);
        if (sessionChanges.containsKey(key)) {

            if (sessionChanges.get(key) == null) {
                return null;
            } else {

                size -= 1;
                return sessionChanges.put(key, null);
            }
        } else {
            if (allRecords.containsKey(key)) {
                size -= 1;
                sessionChanges.put(key, null);
                return allRecords.get(key);
            }
            return null;
        }
    }

    private Map<String, Storeable> makeRelevantVersion() {
        Map<String, Storeable> resultMap = new HashMap<>();
        resultMap.putAll(allRecords);
        for (Map.Entry<String, Storeable> entry : sessionChanges.entrySet()) {
            if (resultMap.containsKey(entry.getKey())) {

                if (entry.getValue() == null) {
                    resultMap.remove(entry.getKey());
                } else {

                    resultMap.put(entry.getKey(),
                            entry.getValue());
                }
            } else {
                if (entry.getValue() != null) {
                    resultMap.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return resultMap;
    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public int commit() throws IOException {
        int numberOfChanges = sessionChanges.size();

        if (numberOfChanges != 0) {
            allRecords = makeRelevantVersion();
            size = allRecords.size();
            writeTablesToDisk();
            sessionChanges.clear();
        }
        return numberOfChanges;
    }

    @Override
    public int rollback() {
        int numberOfChanges = sessionChanges.size();
        size = allRecords.size();
        sessionChanges.clear();
        return numberOfChanges;
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return sessionChanges.size();
    }

    @Override
    public int getColumnsCount() {
        return signature.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return signature.get(columnIndex);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Storeable get(String key) {
        Utility.checkIfObjectsNotNull(key);
        if (sessionChanges.containsKey(key)) {
            return sessionChanges.get(key);
        } else {
            return allRecords.get(key);
        }
    }
}
