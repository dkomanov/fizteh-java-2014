package ru.fizteh.fivt.students.anastasia_ermolaeva.proxy;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.Utility;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.exceptions.DatabaseFormatException;

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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class DBTable implements Table, AutoCloseable {
    public static final String DIR_SUFFIX = ".dir";
    public static final String FILE_SUFFIX = ".dat";
    public static final String DIR_TYPE = "Directory ";
    public static final String FILE_TYPE = "File ";

    public static final int DIR_AMOUNT = 16;
    public static final int FILES_AMOUNT = 16;
    private TableProvider tableProvider;

    private ThreadLocal<Integer> size = ThreadLocal.withInitial(() -> 0);
    /*
    * List of column types.
     */
    private List<Class<?>> signature;
    /*
    * Records readed from real file on the disk.
    */
    private Map<String, Storeable> allRecords;
    /*
    * Writes changes during session.
    */
    private final ThreadLocal<Map<String, Storeable>> sessionChanges =
            ThreadLocal.withInitial(HashMap::new);
    private ReadWriteLock tableOperationsLock = new ReentrantReadWriteLock(true);
    /*
    * Path to table's directory.
    */
    private Path dbPath;
    /*
    * Path to root's directory.
    */
    private String name;

    private boolean valid = true;
    private ReadWriteLock validLock = new ReentrantReadWriteLock(true);

    public DBTable(final Path rootPath, final String name, final TableProvider provider) throws IOException {
        this(rootPath, name, new HashMap<>(), new ArrayList<>(), provider);
        readExistingTableFromDisk();
        size.set(allRecords.size());
    }

    public DBTable(final Path rootPath, final String name,
                   final Map<String, Storeable> records, List<Class<?>> columnTypes, final TableProvider provider) {
        dbPath = rootPath.resolve(name);
        this.name = name;
        allRecords = records;
        tableProvider = provider;
        signature = columnTypes;
        size.set(allRecords.size());
    }

    private void readExistingTableFromDisk() throws IOException {
        Utility.checkTableDirectoryContent(dbPath);
        Utility.fillSignature(dbPath, signature);
        tableOperationsLock.writeLock().lock();
        try {
            try (DirectoryStream<Path>
                         tableDirectoryStream = Files.newDirectoryStream(dbPath, Files::isDirectory)) {
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
                        nDirectory = Integer.parseInt(tableSubdirectoryName.substring(0, k));
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
                            if ((k < 0) || !(fileName.endsWith(FILE_SUFFIX))) {
                                throw new DatabaseFormatException(FILE_TYPE + fileName
                                        + Utility.NOT_EXIST_MSG);
                            }
                            int nFile;
                            try {
                                nFile = Integer.parseInt(fileName.substring(0, k));
                            } catch (NumberFormatException n) {
                                throw new DatabaseFormatException(FILE_TYPE
                                        + fileName
                                        + Utility.NOT_EXIST_MSG, n);
                            }
                            Utility.checkBoundsForFileNames(nFile, FILE_SUFFIX, FILE_TYPE);
                            try (RandomAccessFile dbFile =
                                         new RandomAccessFile(file.toAbsolutePath().toString(), "r")) {
                                if (dbFile.length() > 0) {
                                    while (dbFile.getFilePointer() < dbFile.length()) {
                                        String key = Utility.readUtil(dbFile, fileName);
                                        int expectedNDirectory = Math.abs(key.getBytes(Utility.ENCODING)[0]
                                                % DIR_AMOUNT);
                                        int expectedNFile = Math.abs((key.getBytes(Utility.ENCODING)[0]
                                                / DIR_AMOUNT) % FILES_AMOUNT);
                                        String value = Utility.readUtil(dbFile, fileName);
                                        if (expectedNDirectory == nDirectory && expectedNFile == nFile) {
                                            allRecords.put(key, tableProvider.deserialize(this, value));
                                        } else {
                                            throw new DatabaseFormatException(Utility.WRONG_LOCATION_MSG);
                                        }
                                    }
                                } else {
                                    throw new DatabaseFormatException(FILE_TYPE + fileName
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
                throw new DatabaseFormatException(name + Utility.SIGNATURE_CONFLICT_MSG, e);
            } catch (IndexOutOfBoundsException | ColumnFormatException e) {
                throw new DatabaseFormatException(name
                        + Utility.SIGNATURE_CONFLICT_MSG
                        + e.getMessage(), e);
            }
        } finally {
            tableOperationsLock.writeLock().unlock();
        }
    }

    private void writeTableToDisk() throws IOException {
        tableOperationsLock.writeLock().lock();
        try {
            Map<String, String>[][] db = new HashMap[DIR_AMOUNT][FILES_AMOUNT];

            for (int i = 0; i < DIR_AMOUNT; i++) {
                for (int j = 0; j < FILES_AMOUNT; j++) {
                    db[i][j] = new HashMap<>();
                }
            }

            for (Map.Entry<String, Storeable> entry : allRecords.entrySet()) {
                String key = entry.getKey();
                String value = tableProvider.serialize(this, entry.getValue());
                try {
                    int nDirectory = Math.abs(key.getBytes(Utility.ENCODING)[0] % DIR_AMOUNT);
                    int nFile = Math.abs((key.getBytes(Utility.ENCODING)[0] / DIR_AMOUNT) % FILES_AMOUNT);
                    db[nDirectory][nFile].put(key, value);
                } catch (UnsupportedEncodingException e) {
                    throw new IOException(e.getMessage(), e);
                }
            }
            for (int nDirectory = 0; nDirectory < DIR_AMOUNT; nDirectory++) {
                for (int nFile = 0; nFile < FILES_AMOUNT; nFile++) {
                    if (!db[nDirectory][nFile].isEmpty()) {
                        Path newPath = dbPath.resolve(nDirectory + DIR_SUFFIX);
                        if (!Files.exists(newPath)) {
                            Files.createDirectory(newPath);
                        }
                        Path newFilePath = newPath.resolve(nFile + FILE_SUFFIX);
                        Files.deleteIfExists(newFilePath);
                        Files.createFile(newFilePath);
                        try (RandomAccessFile dbFile = new RandomAccessFile(newFilePath.toFile(), "rw")) {
                            dbFile.setLength(0);
                            for (Map.Entry<String, String> entry : db[nDirectory][nFile].entrySet()) {
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
                        Path newPath = dbPath.resolve(nDirectory + DIR_SUFFIX);
                        if (Files.exists(newPath)) {
                            Path newFilePath = newPath.resolve(nFile + FILE_SUFFIX);
                            Files.deleteIfExists(newFilePath);
                            if (newPath.toFile().list().length == 0) {
                                Files.delete(newPath);
                            }
                        }
                    }
                }
            }
        } finally {
            tableOperationsLock.writeLock().unlock();
        }
    }

    @Override
    public List<String> list() {
        validLock.readLock().lock();
        try {
            checkIfValid();
            tableOperationsLock.readLock().lock();
            try {
                Map<String, Storeable> relevantMap = makeRelevantVersion();
                List<String> list = new ArrayList<>();
                list.addAll(relevantMap.keySet());
                return list;
            } finally {
                tableOperationsLock.readLock().unlock();
            }
        } finally {
            validLock.readLock().unlock();
        }
    }

    /**
     * Устанавливает значение по указанному ключу.
     *
     * @param key   Ключ для нового значения. Не может быть null.
     * @param value Новое значение. Не может быть null.
     * @return Значение, которое было записано по этому ключу ранее. Если ранее значения не было записано,
     * возвращает null.
     * @throws IllegalArgumentException.                               Если значение параметров key или value является null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - при попытке передать Storeable с колонками другого типа.
     */
    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        validLock.readLock().lock();
        try {
            checkIfValid();
            Utility.checkIfObjectsNotNull(key, value);
            tableOperationsLock.readLock().lock();
            try {
        /*
        * The record with key has already been changed
        * or added during the session.
        */
                if (sessionChanges.get().containsKey(key)) {
                    return sessionChanges.get().put(key, value);
                }
        /*
        *The record with key hasn't been changed yet.
        */
                if (allRecords.containsKey(key)) {
                    sessionChanges.get().put(key, value);
                    size.set(size.get() + 1);
                    return allRecords.get(key);
                }
        /*
        * Absolutely new record.
        */
                size.set(size.get() + 1);
                return sessionChanges.get().put(key, value);
            } finally {
                tableOperationsLock.readLock().unlock();
            }
        } finally {
            validLock.readLock().unlock();
        }
    }

    /**
     * Удаляет значение по указанному ключу.
     *
     * @param key Ключ для поиска значения. Не может быть null.
     * @return Предыдущее значение. Если не найдено, возвращает null.
     * @throws IllegalArgumentException Если значение параметра key является null.
     */
    @Override
    public Storeable remove(String key) {
        validLock.readLock().lock();
        try {
            checkIfValid();
            Utility.checkIfObjectsNotNull(key);
            tableOperationsLock.readLock().lock();
            try {
                if (sessionChanges.get().containsKey(key)) {
            /*
            * The record with key has been deleted during the session.
            */
                    if (sessionChanges.get().get(key) == null) {
                        return null;
                    } else {
                /*
                * The record with key has already been changed
                * or added during the session.
                */
                        size.set(size.get() - 1);
                        return sessionChanges.get().put(key, null);
                    }
                } else {
                    if (allRecords.containsKey(key)) {
                        size.set(size.get() - 1);
                        sessionChanges.get().put(key, null);
                        return allRecords.get(key);
                    }
                    return null;
                }
            } finally {
                tableOperationsLock.readLock().unlock();
            }
        } finally {
            validLock.readLock().unlock();
        }
    }

    private Map<String, Storeable> makeRelevantVersion() {
        Map<String, Storeable> resultMap = new HashMap<>();
        resultMap.putAll(allRecords);
        for (Map.Entry<String, Storeable> entry : sessionChanges.get().entrySet()) {
            if (resultMap.containsKey(entry.getKey())) {
                /*
                * If the record was deleted during the session.
                */
                if (entry.getValue() == null) {
                    resultMap.remove(entry.getKey());
                } else {
                    /*
                    * If the value was changed during the session.
                    */
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

    /**
     * Возвращает количество ключей в таблице. Возвращает размер текущей версии, с учётом незафиксированных изменений.
     *
     * @return Количество ключей в таблице.
     */
    @Override
    public int size() {
        validLock.readLock().lock();
        try{
            checkIfValid();
            return size.get();
        } finally {
            validLock.readLock().unlock();
        }

    }

    /**
     * Выполняет фиксацию изменений.
     *
     * @return Число записанных изменений.
     * @throws java.io.IOException если произошла ошибка ввода/вывода. Целостность таблицы не гарантируется.
     */
    @Override
    public int commit() throws IOException {
        validLock.readLock().lock();
        try {
            checkIfValid();
            tableOperationsLock.writeLock().lock();
            try {
                int numberOfChanges = sessionChanges.get().size();
                /*
                * Execute changes to disk
                */
                if (numberOfChanges != 0) {
                    allRecords = makeRelevantVersion();
                    size.set(allRecords.size());
                    writeTableToDisk();
                    sessionChanges.get().clear();
                }
                return numberOfChanges;
            } finally {
                tableOperationsLock.writeLock().unlock();
            }
        } finally {
            validLock.readLock().unlock();
        }
    }

    /**
     * Выполняет откат изменений с момента последней фиксации.
     *
     * @return Число откаченных изменений.
     */
    @Override
    public int rollback() {
        validLock.readLock().lock();
        try {
            checkIfValid();
            tableOperationsLock.readLock().lock();
            try {
                return rollbackChanges();
            } finally {
                tableOperationsLock.readLock().unlock();
            }
        } finally {
            validLock.readLock().unlock();
        }
    }

    private int rollbackChanges() {
        int numberOfChanges = sessionChanges.get().size();
        size.set(allRecords.size());
        sessionChanges.get().clear();
        return numberOfChanges;
    }

    /**
     * Возвращает количество изменений, ожидающих фиксации.
     *
     * @return Количество изменений, ожидающих фиксации.
     */
    @Override
    public int getNumberOfUncommittedChanges() {
        validLock.readLock().lock();
        try {
            checkIfValid();
            return sessionChanges.get().size();
        } finally {
            validLock.readLock().unlock();
        }
    }

    /**
     * Возвращает количество колонок в таблице.
     *
     * @return Количество колонок в таблице.
     */
    @Override
    public int getColumnsCount() {
        validLock.readLock().lock();
        try {
            checkIfValid();
            return signature.size();
        } finally {
            validLock.readLock().unlock();
        }
    }

    /**
     * Возвращает тип значений в колонке.
     *
     * @param columnIndex Индекс колонки. Начинается с нуля.
     * @return Класс, представляющий тип значения.
     * @throws IndexOutOfBoundsException - неверный индекс колонки
     */
    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        validLock.readLock().lock();
        try {
            checkIfValid();
            Utility.checkColumnIndex(columnIndex, signature.size());
            return signature.get(columnIndex);
        } finally {
            validLock.readLock().unlock();
        }
    }

    /**
     * Возвращает название таблицы или индекса.
     *
     * @return Название таблицы.
     */
    @Override
    public String getName() {
        validLock.readLock().lock();
        try {
            checkIfValid();
            return name;
        } finally {
            validLock.readLock().unlock();
        }
    }

    /**
     * Получает значение по указанному ключу.
     *
     * @param key Ключ для поиска значения. Не может быть null.
     *            Для индексов по не-строковым полям аргумент представляет собой сериализованное значение колонки.
     *            Его потребуется распарсить.
     * @return Значение. Если не найдено, возвращает null.
     * @throws IllegalArgumentException Если значение параметра key является null.
     */
    @Override
    public Storeable get(String key) {
        validLock.readLock().lock();
        try {
            checkIfValid();
            Utility.checkIfObjectsNotNull(key);
            tableOperationsLock.readLock().lock();
            try {
                if (sessionChanges.get().containsKey(key)) {
                    return sessionChanges.get().get(key);
                } else {
                    return allRecords.get(key);
                }
            } finally {
                tableOperationsLock.readLock().unlock();
            }
        } finally {
            validLock.readLock().unlock();
        }
    }

    @Override
    public void close() {
        validLock.writeLock().lock();
        try {
            checkIfValid();
            rollbackChanges();
            valid = false;
        } finally {
            validLock.writeLock().unlock();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + dbPath.toAbsolutePath().toString() + "]";
    }

    public void checkIfValid() {
        if (!valid) {
            throw new IllegalStateException("Table " + name + " was closed\n");
        }
    }
}
