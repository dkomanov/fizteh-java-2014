package ru.fizteh.fivt.students.deserg.telnet;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
/**
 * Created by deserg on 04.10.14.
 */
public class DbTable implements Table, AutoCloseable {

    private List<Class<?>> signature = new ArrayList<>();
    private Map<String, Storeable> committedData = new HashMap<>();
    private boolean closed = false;

    class TableDiff {
        public Map<String, Storeable> addedData = new HashMap<>();
        public Map<String, Storeable> changedData = new HashMap<>();
        public Set<String> removedData = new HashSet<>();
    }

    private ThreadLocal<TableDiff> diff = new ThreadLocal<TableDiff>() {
        @Override
        protected TableDiff initialValue() {
            return new TableDiff();
        }
    };

    private ThreadLocal<Integer> version = new ThreadLocal<Integer>() {

        @Override
        protected Integer initialValue() {
            return 0;
        }

    };
    private Integer committedVersion = 0;


    private String tableName;
    private Path tablePath;
    private ReadWriteLock lock = new ReentrantReadWriteLock(true);


    public DbTable(Path path, List<Class<?>> signature) {
        tablePath = path;
        tableName = path.getFileName().toString();
        this.signature = signature;
        try {
            read();
        } catch (MyIOException ex) {
            System.out.println("Table \"" + tableName + "\": error while reading");
            System.exit(1);
        }
    }

    /**
     * Возвращает название базы данных.
     */
    @Override
    public String getName() {

        checkClosed();

        return tableName;
    }

    /**
     * Получает значение по указанному ключу.
     *
     * @param key Ключ.
     * @return Значение. Если не найдено, возвращает null.
     *
     * @throws IllegalArgumentException Если значение параметра key является null.
     */
    @Override
    public Storeable get(String key) throws IllegalArgumentException {

        checkClosed();

        if (key == null) {
            throw new IllegalArgumentException("Table \"" + tableName + "\": get: null key");
        }

        if (key.isEmpty()) {
            throw new IllegalArgumentException("Table \"" + tableName + "\": get: empty key");
        }


        lock.readLock().lock();
        sync();
        lock.readLock().unlock();

        Storeable value = diff.get().addedData.get(key);
        if (value != null) {
            return value;
        }

        value = diff.get().changedData.get(key);
        if (value != null) {
            return value;
        }

        lock.readLock().lock();
        value = committedData.get(key);
        lock.readLock().unlock();

        if (value != null) {
            return value;
        }

        return null;
    }

    /**
     * Устанавливает значение по указанному ключу.
     *
     * @param key Ключ.
     * @param value Значение.
     * @return Значение, которое было записано по этому ключу ранее. Если ранее значения не было записано,
     * возвращает null.
     *
     * @throws IllegalArgumentException Если значение параметров key или value является null.
     */
    @Override
    public Storeable put(String key, Storeable value) {

        checkClosed();

        if (key == null) {
            throw new IllegalArgumentException("Table \"" + tableName + "\": put: null key");
        }

        if (value == null) {
            throw new IllegalArgumentException("Table \"" + tableName + "\": put: null value");
        }

        if (key.isEmpty()) {
            throw new IllegalArgumentException("Table \"" + tableName + "\": put: empty key");
        }

        lock.readLock().lock();
        sync();
        lock.readLock().unlock();

        lock.readLock().lock();
        if (committedData.containsKey(key)) {

            if (diff.get().removedData.contains(key)) {
                diff.get().removedData.remove(key);
                if (!committedData.get(key).equals(value)) {
                    diff.get().changedData.put(key, value);
                }

                lock.readLock().unlock();
                return null;
            } else if (diff.get().changedData.containsKey(key)) {

                if (committedData.get(key).equals(value)) {

                    lock.readLock().unlock();
                    return diff.get().changedData.remove(key);
                } else {

                    lock.readLock().unlock();
                    return diff.get().changedData.put(key, value);
                }
            } else {

                diff.get().changedData.put(key, value);

                lock.readLock().unlock();
                return committedData.get(key);

            }

        } else {

            lock.readLock().unlock();
            return diff.get().addedData.put(key, value);
        }

    }

    /**
     * Удаляет значение по указанному ключу.
     *
     * @param key Ключ.
     * @return Значение. Если не найдено, возвращает null.
     *
     * @throws IllegalArgumentException Если значение параметра key является null.
     */
    @Override
    public Storeable remove(String key) {

        checkClosed();

        if (key == null) {
            throw new IllegalArgumentException("Table \"" + tableName + "\": remove: null key");
        }

        if (key.isEmpty()) {
            throw new IllegalArgumentException("Table \"" + tableName + "\": remove: empty key");
        }

        lock.readLock().lock();
        sync();
        lock.readLock().unlock();


        if (diff.get().addedData.containsKey(key)) {
            return diff.get().addedData.remove(key);
        }

        if (diff.get().changedData.containsKey(key)) {
            Storeable value = diff.get().changedData.get(key);
            diff.get().changedData.remove(key);
            diff.get().removedData.add(key);
            return value;
        }

        if (diff.get().removedData.contains(key)) {
            return null;
        }

        if (committedData.containsKey(key)) {
            Storeable value = committedData.get(key);
            diff.get().removedData.add(key);
            return value;
        }

        return null;
    }

    /**
     * Возвращает количество ключей в таблице.
     *
     * @return Количество ключей в таблице.
     */
    @Override
    public int size() {
        checkClosed();
        return committedData.size() - diff.get().removedData.size() + diff.get().addedData.size();
    }

    /**
     * Выполняет фиксацию изменений.
     *
     * @return Количество сохранённых ключей.
     */
    @Override
    public int commit() {

        checkClosed();

        lock.writeLock().lock();

        committedData.keySet().removeAll(diff.get().removedData);
        committedData.putAll(diff.get().addedData);
        committedData.putAll(diff.get().changedData);

        int changedKeys = getNumberOfUncommittedChanges();
        diff.get().addedData.clear();
        diff.get().removedData.clear();
        diff.get().changedData.clear();
        version.set(version.get() + 1);
        committedVersion++;

        try {
            write();
        } catch (MyIOException ex) {
            lock.writeLock().unlock();
            throw new MyException("Table \"" + tableName + "\": errors while saving commit");
        }

        lock.writeLock().unlock();
        return changedKeys;
    }

    /**
     * Выполняет откат изменений с момента последней фиксации.
     *
     * @return Количество отменённых ключей.
     */
    @Override
    public int rollback() {

        checkClosed();

        int changedKeys = getNumberOfUncommittedChanges();
        diff.get().addedData.clear();
        diff.get().removedData.clear();
        diff.get().changedData.clear();

        return changedKeys;
    }

    /**
     * Выводит список ключей таблицы
     *
     * @return Список ключей.
     */
    @Override
    public List<String> list() {

        checkClosed();

        List<String> keyList = new LinkedList<>();
        keyList.addAll(committedData.keySet());
        keyList.addAll(diff.get().addedData.keySet());

        return keyList;
    }


    /**
     * Возвращает количество изменений, ожидающих фиксации.
     *
     * @return Количество изменений, ожидающих фиксации.
     */
    @Override
    public int getNumberOfUncommittedChanges() {

        checkClosed();
        return diff.get().addedData.size() + diff.get().removedData.size() + diff.get().changedData.size();
    }


    /**
     * Возвращает количество колонок в таблице.
     *
     * @return Количество колонок в таблице.
     */
    @Override
    public int getColumnsCount() {

        checkClosed();
        return signature.size();
    }


    /**
     * Возвращает тип значений в колонке.
     *
     * @param columnIndex Индекс колонки. Начинается с нуля.
     * @return Класс, представляющий тип значения.
     *
     * @throws IndexOutOfBoundsException - неверный индекс колонки
     */
    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {

        checkClosed();

        if (columnIndex < 0 || columnIndex >= signature.size()) {
            throw new IndexOutOfBoundsException("DbTable: getColumnType: index \""
                    + columnIndex + "\" is out of bounds");
        }

        return signature.get(columnIndex);

    }


    @Override
    public void close() {
        checkClosed();
        rollback();
        closed = true;
    }

    @Override
    public String toString() {

        return getClass().getSimpleName() + "[" + tablePath + "]";

    }

    /**
     * Not-interface methods begin here
     */

    public boolean isClosed() {
        return closed;
    }

    private void sync() {

        if (version.get() < committedVersion) {
            for (String key : diff.get().removedData) {
                if (committedData.containsKey(key)) {
                    diff.get().removedData.remove(key);
                }
            }


            for (HashMap.Entry<String, Storeable> entry: diff.get().addedData.entrySet()) {
                String key = entry.getKey();
                Storeable value = entry.getValue();
                if (value.equals(committedData.get(key))) {
                    diff.get().addedData.remove(key);
                }
            }

            for (HashMap.Entry<String, Storeable> entry: diff.get().changedData.entrySet()) {
                String key = entry.getKey();
                Storeable value = entry.getValue();

                if (value.equals(committedData.get(key))) {
                    diff.get().changedData.remove(key);
                }

            }

        }

    }

    private void checkClosed() {
        if (closed) {
            throw new IllegalStateException("Table \"" + tableName + "\" is closed");
        }
    }


    public List<Class<?>> getSignature() {
        lock.readLock().lock();
        List<Class<?>> retSignature = signature;
        lock.readLock().unlock();

        return retSignature;
    }

    private void readKeyValue(Path filePath, int dir, int file) throws MyIOException {

        if (Files.exists(filePath)) {
            try (DataInputStream is = new DataInputStream(Files.newInputStream(filePath))) {

                if (is.available() == 0) {
                    throw new MyIOException("File is empty: " + filePath);
                }

                while (is.available() > 0) {

                    int keyLen = is.readInt();
                    if (is.available() < keyLen) {
                        throw new MyIOException("Wrong key size");
                    }

                    byte[] key = new byte[keyLen];
                    is.read(key, 0, keyLen);

                    int valLen = is.readInt();
                    if (is.available() < valLen) {
                        throw new MyIOException("Wrong value size");
                    }
                    byte[] value = new byte[valLen];
                    is.read(value, 0, valLen);

                    String keyStr = new String(key, "UTF-8");
                    Storeable valueStr = new TableRow(signature);
                    try {
                         valueStr = Serializer.deserialize(this, new String(value, "UTF-8"));
                    } catch (ParseException ex) {
                        System.out.println("Parse exception while reading");
                        System.exit(1);
                    }

                    int hashValue = keyStr.hashCode();
                    if (hashValue % 16 != dir || hashValue / 16 % 16 != file) {
                        throw new MyIOException("Wrong key file");
                    }

                    committedData.put(keyStr, valueStr);
                }

            } catch (IOException e) {
                throw new MyIOException("Reading from disk failed");
            }


        }
    }

    public void read() throws MyIOException {

        lock.writeLock().lock();
        for (int dir = 0; dir < 16; ++dir) {
            for (int file = 0; file < 16; ++file) {
                Path filePath = tablePath.resolve(dir + ".dir").resolve(file + ".dat");
                try {
                    readKeyValue(filePath, dir, file);
                } catch (MyException ex) {
                    System.out.println(ex.getMessage());
                    System.exit(1);
                }
            }
        }
        lock.writeLock().unlock();
    }

    private void writeKeyValue(Path filePath, String keyStr, String valueStr) throws MyIOException {

        try (DataOutputStream os = new DataOutputStream(Files.newOutputStream(filePath))) {
            byte[] key = keyStr.getBytes("UTF-8");
            byte[] value = valueStr.getBytes("UTF-8");
            os.writeInt(key.length);
            os.write(key);
            os.writeInt(value.length);
            os.write(value);

        } catch (IOException ex) {
            throw new MyIOException("Writing to dist failed");
        }


    }

    public void write() throws MyIOException {

        lock.writeLock().lock();

        if (Files.exists(tablePath)) {
            Shell.deleteContent(tablePath);
        } else {
            try {
                Files.createDirectory(tablePath);
            } catch (IOException ex) {
                lock.writeLock().unlock();
                throw new MyIOException("Error has occurred while creating table directory");
            }
        }

        Shell.writeSignature(signature, tablePath);

        for (HashMap.Entry<String, Storeable> entry : committedData.entrySet()) {

            String key = entry.getKey();
            String value = Serializer.serialize(this, entry.getValue());
            int hashCode = key.hashCode();
            int dir = hashCode % 16;
            int file = hashCode / 16 % 16;

            Path dirPath = tablePath.resolve(dir + ".dir");
            Path filePath = dirPath.resolve(file + ".dat");

            if (!Files.exists(dirPath)) {
                try {
                    Files.createDirectory(dirPath);
                } catch (IOException ex) {
                    lock.writeLock().unlock();
                    throw new MyIOException(dirPath + ": unable to create");
                }
            }
            if (!Files.exists(filePath)) {
                try {
                    Files.createFile(filePath);
                } catch (IOException ex) {
                    lock.writeLock().unlock();
                    throw new MyIOException(filePath + ": unable to create");
                }
            }

            writeKeyValue(filePath, key, value);

        }

        lock.writeLock().unlock();

    }


}
