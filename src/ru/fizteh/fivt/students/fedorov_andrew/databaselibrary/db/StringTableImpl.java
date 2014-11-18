package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.db;

import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DBFileCorruptIOException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseIOException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.TableCorruptIOException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Log;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Utility;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;

/**
 * This class represents table stored in file system and parted into directories and files.<br/>
 * Each part of data is read on require.<br/>
 * Null keys/values are not permitted.
 * @author phoenix
 */
public final class StringTableImpl {
    private static final int DIRECTORIES_COUNT = 16;
    private static final int FILES_COUNT = 16;

    private static final String DIRECTORY_EXTENSION = "dir";
    private static final String FILE_EXTENSION = "dat";

    private final Path tableRoot;
    private final String tableName;
    /**
     * Mapping between table parts and local hashes of keys that can be stored inside them.
     * @see #getHash(String)
     */
    private HashMap<Integer, TablePart> tableParts;

    /**
     * Constructor for cloning and safe table creation/obtaining.
     */
    private StringTableImpl(Path tableRoot) {
        this.tableName = tableRoot.getFileName().toString();
        this.tableRoot = tableRoot;
        this.tableParts = new HashMap<>();
    }

    /**
     * Builds table part relative filename hashcode;
     * @param directory
     *         Not limited due to hashing algorithm.
     * @param file
     *         Must be strictly less than {@link #FILES_COUNT}.
     */
    private static int buildHash(int directory, int file) {
        return directory * FILES_COUNT + file;
    }

    /**
     * Extracts directory hashcode from relative filename hashcode
     */
    private static int getDirectoryFromHash(int hash) {
        return hash / FILES_COUNT;
    }

    /**
     * Extracts filename hashcode from relative filename hashcode.
     */
    private static int getFileFromHash(int hash) {
        return hash % FILES_COUNT;
    }

    private static int getHash(String key) {
        byte byte0 = key.getBytes()[0];

        int dir = (byte0 % DIRECTORIES_COUNT);
        if (dir < 0) {
            dir += DIRECTORIES_COUNT;
        }

        int file = (byte0 / DIRECTORIES_COUNT) % FILES_COUNT;
        if (file < 0) {
            file += FILES_COUNT;
        }

        return buildHash(dir, file);
    }

    /**
     * Constructs a new clear table.
     * @param tableRoot
     *         Path to table root directory.
     */
    public static StringTableImpl createTable(Path tableRoot) throws DatabaseIOException {
        try {
            try {
                Files.createDirectory(tableRoot);
            } catch (IOException exc) {
                throw new DatabaseIOException(
                        "Failed to create table directory: " + tableRoot.getFileName(), exc);
            }

            StringTableImpl table = new StringTableImpl(tableRoot);
            for (int dir = 0; dir < DIRECTORIES_COUNT; dir++) {
                for (int file = 0; file < FILES_COUNT; file++) {
                    int hash = buildHash(dir, file);
                    table.tableParts.put(hash, new TablePart(table.makeTablePartFilePath(hash)));
                }
            }
            return table;
        } catch (DatabaseIOException exc) {
            try {
                Utility.rm(tableRoot);
            } catch (Throwable thr) {
                Log.log(StringTableImpl.class, thr, "Failed to cleanup after table creation failure");
            }
            throw exc;
        }
    }

    /**
     * Constructs table by reading its data from file system.
     * @param tableRoot
     *         Path to the root directory of the table.
     * @param extraFilesFilter
     *         Filter that returns true if this extra file's existence can be ignored. Path is given relative
     *         to the table root directory.
     * @throws DatabaseIOException
     */
    public static StringTableImpl getTable(Path tableRoot, Predicate<Path> extraFilesFilter)
            throws DatabaseIOException {
        StringTableImpl table = new StringTableImpl(tableRoot);
        table.checkFileSystem(extraFilesFilter);
        table.readFromFileSystem();
        return table;
    }

    public Path getTableRoot() {
        return tableRoot;
    }

    private void checkNameFormat(String name, String extensionRegex, int minID, int maxID)
            throws DBFileCorruptIOException {
        if (!name.matches("(0|([1-9][0-9]*))\\." + extensionRegex)) {
            throw new DBFileCorruptIOException("Invalid database element format: " + name);
        }
        int dotIndex = name.indexOf('.');
        int id = Integer.parseInt(name.substring(0, dotIndex));
        if (id < minID || id > maxID) {
            throw new DBFileCorruptIOException("Invalid database element id: " + name);
        }
    }

    /**
     * Scans this directory and its subdirectories related to this table and finds extra files that must not
     * exist.
     * @param filter
     *         Filter that returns true if this extra file's existence can be ignored. Path is given relative
     *         to the table root directory.
     */
    private void checkFileSystem(Predicate<Path> filter) throws DatabaseIOException {
        try (DirectoryStream<Path> partsDirs = Files.newDirectoryStream(tableRoot)) {
            // Checking table part directories.
            for (Path partDirectory : partsDirs) {
                Path relativePath = tableRoot.relativize(partDirectory);

                if (Files.isDirectory(partDirectory)) {
                    try {
                        checkNameFormat(
                                partDirectory.getFileName().toString(),
                                DIRECTORY_EXTENSION,
                                0,
                                DIRECTORIES_COUNT);
                    } catch (DBFileCorruptIOException exc) {
                        if (filter.test(relativePath)) {
                            continue;
                        } else {
                            throw exc;
                        }
                    }
                } else {
                    if (filter.test(relativePath)) {
                        continue;
                    } else {
                        throw new DBFileCorruptIOException(
                                "Database element must be a directory: " + partDirectory.getFileName());
                    }
                }

                // Checking files inside table part directory.
                try (DirectoryStream<Path> partFiles = Files.newDirectoryStream(partDirectory)) {
                    for (Path partFile : partFiles) {
                        relativePath = tableRoot.relativize(partFile);
                        try {
                            checkNameFormat(
                                    partFile.getFileName().toString(), FILE_EXTENSION, 0, FILES_COUNT);
                        } catch (DBFileCorruptIOException exc) {
                            if (!filter.test(relativePath)) {
                                throw exc;
                            }
                        }
                    }
                    // Further checks will be performed during an attempt to read data from the file.
                }
            }
        } catch (DatabaseIOException exc) {
            throw exc;
        } catch (IOException exc) {
            throw new TableCorruptIOException(tableName, "Cannot scan table directory", exc);
        }
    }

    public void readFromFileSystem() throws DBFileCorruptIOException, TableCorruptIOException {
        StringTableImpl thisClone = clone();
        tableParts.clear();

        try {
            for (int dir = 0; dir < DIRECTORIES_COUNT; dir++) {
                for (int file = 0; file < FILES_COUNT; file++) {
                    int partHash = buildHash(dir, file);

                    TablePart fmap = new TablePart(makeTablePartFilePath(partHash));
                    if (Files.exists(fmap.getTablePartFilePath())) {
                        fmap.readFromFile();
                    }

                    // checking keys' hashes
                    Set<String> keySet = fmap.keySet();
                    for (String key : keySet) {
                        int keyHash = getHash(key);
                        if (keyHash != partHash) {
                            throw new TableCorruptIOException(
                                    tableName, "Some keys are stored in improper places");
                        }
                    }

                    tableParts.put(partHash, fmap);
                }
            }
        } catch (Throwable thr) {
            this.tableParts = thisClone.tableParts;
            throw thr;
        }
    }

    /**
     * Clones the whole table
     */
    @Override
    protected StringTableImpl clone() {
        StringTableImpl cloneTable = new StringTableImpl(tableRoot);

        for (Entry<Integer, TablePart> entry : tableParts.entrySet()) {
            cloneTable.tableParts.put(entry.getKey(), entry.getValue().clone());
        }

        return cloneTable;
    }

    public String getName() {
        return tableName;
    }

    public String get(String key) {
        return obtainTablePart(key).get(key);
    }

    public String put(String key, String value) {
        Utility.checkNotNull(value, "Value");
        return obtainTablePart(key).put(key, value);
    }

    public String remove(String key) {
        return obtainTablePart(key).remove(key);
    }

    /**
     * Counts the number of the records stored in all table parts assigned to this table.
     */
    public int size() {
        int rowsNumber = 0;

        for (TablePart part : tableParts.values()) {
            rowsNumber += part.size();
        }

        return rowsNumber;
    }

    public int commit() throws DatabaseIOException {
        int diffsCount = 0;
        for (TablePart part : tableParts.values()) {
            diffsCount += part.commit();
        }
        return diffsCount;
    }

    public int rollback() {
        int diffsCount = 0;
        for (TablePart part : tableParts.values()) {
            diffsCount += part.rollback();
        }
        return diffsCount;
    }

    /**
     * Collects all keys from all table parts assigned to this table.
     */
    public List<String> list() {
        List<String> keySet = new LinkedList<>();

        for (TablePart part : tableParts.values()) {
            keySet.addAll(part.keySet());
        }

        return keySet;
    }

    /**
     * Builds table file path from its hash that describes directory and file name.
     */
    private Path makeTablePartFilePath(int hash) {
        return tableRoot.resolve(
                Paths.get(
                        getDirectoryFromHash(hash) + "." + DIRECTORY_EXTENSION,
                        getFileFromHash(hash) + "." + FILE_EXTENSION));
    }

    /**
     * Gets {@link TablePart} instance assigned to this {@code hash} from memory
     * @param key
     *         key that is hold by desired table.
     */
    private TablePart obtainTablePart(String key) {
        Utility.checkNotNull(key, "Key");
        return tableParts.get(getHash(key));
    }

    public int getNumberOfUncommittedChanges() {
        int diffsCount = 0;
        for (TablePart part : tableParts.values()) {
            diffsCount += part.getUncommittedChangesCount();
        }
        return diffsCount;
    }
}
