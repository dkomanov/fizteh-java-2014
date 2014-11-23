package ru.fizteh.fivt.students.ilivanov.Parallel;

import ru.fizteh.fivt.students.ilivanov.Parallel.Interfaces.ColumnFormatException;
import ru.fizteh.fivt.students.ilivanov.Parallel.Interfaces.Storeable;
import ru.fizteh.fivt.students.ilivanov.Parallel.Interfaces.Table;

import java.io.*;
import java.text.ParseException;
import java.util.*;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MultiFileMap implements Table, AutoCloseable {
    private File location;
    private FileUsing[][] map;
    private ThreadLocal<TransactionHandler> transaction;
    private TransactionPool transactionPool;
    private ArrayList<Class<?>> columnTypes;
    private FileMapProvider tableProvider;
    private final int arraySize;
    private ReentrantReadWriteLock lock;
    private volatile boolean isClosed;

    public MultiFileMap(File location, int arraySize, FileMapProvider tableProvider, TransactionPool pool) {
        if (location == null) {
            throw new IllegalArgumentException("Null location");
        }
        if (tableProvider == null) {
            throw new IllegalArgumentException("Null tableProvider");
        }
        this.transactionPool = pool;
        this.tableProvider = tableProvider;
        this.location = location;
        this.arraySize = arraySize;
        isClosed = false;
        lock = new ReentrantReadWriteLock();
        columnTypes = new ArrayList<>();
        map = new FileUsing[arraySize][arraySize];
        transaction = new ThreadLocal<TransactionHandler>() {
            @Override
            public TransactionHandler initialValue() {
                return transactionPool.createHandler(MultiFileMap.this);
            }
        };
        for (int i = 0; i < arraySize; i++) {
            for (int j = 0; j < arraySize; j++) {
                String relative = String.format("%d.dir/%d.dat", i, j);
                File path = new File(location, relative);
                map[i][j] = new FileUsing(path);
            }
        }
    }

    public MultiFileMap(File location, int arraySize, FileMapProvider tableProvider,
                        TransactionPool pool, List<Class<?>> columnTypes) {
        this(location, arraySize, tableProvider, pool);
        this.columnTypes = new ArrayList<>(columnTypes);
    }

    public void setColumnTypes(List<Class<?>> columnTypes) {
        checkState();
        this.columnTypes = new ArrayList<>(columnTypes);
    }

    public boolean checkColumnTypes(Storeable list) {
        checkState();
        try {
            for (int i = 0; i < columnTypes.size(); i++) {
                if (list.getColumnAt(i) != null && columnTypes.get(i) != list.getColumnAt(i).getClass()) {
                    return false;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        try {
            list.getColumnAt(columnTypes.size());
            return false;
        } catch (IndexOutOfBoundsException e) {
            return true;
        }
    }

    private boolean newLineCheck(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '\n') {
                return false;
            }
        }
        return true;
    }

    private boolean whiteSpaceCheck(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (Character.isWhitespace(string.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public String getName() {
        checkState();
        return location.getName();
    }

    private void clear() {
        for (int i = 0; i < arraySize; i++) {
            for (int j = 0; j < arraySize; j++) {
                map[i][j].clear();
            }
        }
    }

    public File getFile() {
        checkState();
        return location;
    }

    public int size(HashMap<String, Storeable> diff) {
        checkState();
        int size = 0;
        lock.readLock().lock();
        try {
            for (int i = 0; i < arraySize; i++) {
                for (int j = 0; j < arraySize; j++) {
                    size += map[i][j].size();
                }
            }
            for (Map.Entry<String, Storeable> entry : diff.entrySet()) {
                int hashCode = Math.abs(entry.getKey().hashCode());
                int dir = (hashCode % 16 + 16) % 16;
                int file = ((hashCode / 16 % 16) + 16) % 16;
                if (entry.getValue() == null) {
                    if (map[dir][file].get(entry.getKey()) != null) {
                        size--;
                    }
                } else if (map[dir][file].get(entry.getKey()) == null) {
                    size++;
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return size;
    }

    public int size() {
        return size(transaction.get().getDiff());
    }

    private boolean validateData() {
        for (int i = 0; i < arraySize; i++) {
            for (int j = 0; j < arraySize; j++) {
                for (String key : map[i][j].getKeysList()) {
                    int hashCode = Math.abs(key.hashCode());
                    int dir = (hashCode % 16 + 16) % 16;
                    int file = ((hashCode / 16 % 16) + 16) % 16;
                    if (dir != i || file != j) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void validateDirectory() {
        checkState();
        File[] files = location.listFiles();
        if (files == null) {
            throw new RuntimeException("Path specifies invalid location");
        }
        for (File f : files) {
            if (f.getName().equals("signature.tsv")) {
                continue;
            }
            if (!f.getName().matches("((1[0-5])|[0-9])\\.dir")) {
                throw new RuntimeException("Directory is invalid: unexpected files or directories found");
            } else {
                File[] subfiles = f.listFiles();
                if (subfiles == null) {
                    throw new RuntimeException("Path specifies invalid location");
                }
                if (subfiles.length == 0) {
                    throw new RuntimeException("Directory shouldn't be empty");
                }
                for (File sf : subfiles) {
                    if (!sf.getName().matches("((1[0-5])|[0-9])\\.dat")) {
                        throw new RuntimeException("Directory is invalid: unexpected files or directories found");
                    } else {
                        if (sf.length() == 0) {
                            throw new RuntimeException("File shouldn't be empty");
                        }
                    }
                }
            }
        }
    }

    /**
     * Method is not synchronized, use methods of TableFactory instead
     */
    public void loadFromDisk() throws IOException, ParseException {
        checkState();
        columnTypes.clear();
        clear();
        if (!location.getParentFile().exists() || !location.getParentFile().isDirectory()) {
            throw new RuntimeException("Unable to create a table in specified directory: directory doesn't exist");
        }
        if (!location.exists()) {
            return;
        }
        if (location.exists() && !location.isDirectory()) {
            throw new RuntimeException("Specified location is not a directory");
        }
        validateDirectory();
        File signature = new File(location, "signature.tsv");
        try (BufferedReader reader = new BufferedReader(new FileReader(signature))) {
            String[] typeNames = reader.readLine().split("\\s+");
            for (String typeName : typeNames) {
                if (typeName.equals("int")) {
                    columnTypes.add(Integer.class);
                } else if (typeName.equals("long")) {
                    columnTypes.add(Long.class);
                } else if (typeName.equals("byte")) {
                    columnTypes.add(Byte.class);
                } else if (typeName.equals("float")) {
                    columnTypes.add(Float.class);
                } else if (typeName.equals("double")) {
                    columnTypes.add(Double.class);
                } else if (typeName.equals("boolean")) {
                    columnTypes.add(Boolean.class);
                } else if (typeName.equals("String")) {
                    columnTypes.add(String.class);
                } else {
                    throw new RuntimeException(String.format("Unknown type %s", typeName));
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No signature file found");
        } catch (IOException e) {
            throw new IOException("Error reading a signature file", e);
        }
        for (int dir = 0; dir < arraySize; dir++) {
            String relative = String.format("%d.dir", dir);
            File directory = new File(location, relative);
            if (directory.exists() && !directory.isDirectory()) {
                throw new RuntimeException(String.format("%s is not a directory", relative));
            }
            if (directory.exists()) {
                for (int file = 0; file < arraySize; file++) {
                    File db = map[dir][file].getFile();
                    if (db.exists()) {
                        try {
                            map[dir][file].loadFromDisk(this, tableProvider);
                        } catch (RuntimeException e) {
                            throw new RuntimeException(String.format("Error in file %d.dir/%d.dat", dir, file), e);
                        }
                    }
                }
            }
        }
        if (!validateData()) {
            throw new RuntimeException("Wrong data format: key distribution among files is incorrect");
        }
    }

    /**
     * Method not synchronized use commit instead
     */
    public void writeToDisk() throws IOException {
        checkState();
        if (location.exists() && !location.isDirectory()) {
            throw new RuntimeException("Database can't be written to the specified location");
        }
        if (!location.exists()) {
            if (!location.mkdir()) {
                throw new IOException("Unable to create a directory for database");
            }
        }
        File signature = new File(location, "signature.tsv");
        if (!signature.exists()) {
            if (!signature.createNewFile()) {
                throw new IOException("Unable to create a file");
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(signature))) {
            for (int i = 0; i < columnTypes.size(); i++) {
                if (columnTypes.get(i) == Integer.class) {
                    writer.write("int");
                } else if (columnTypes.get(i) == Long.class) {
                    writer.write("long");
                } else if (columnTypes.get(i) == Byte.class) {
                    writer.write("byte");
                } else if (columnTypes.get(i) == Float.class) {
                    writer.write("float");
                } else if (columnTypes.get(i) == Double.class) {
                    writer.write("double");
                } else if (columnTypes.get(i) == Boolean.class) {
                    writer.write("boolean");
                } else if (columnTypes.get(i) == String.class) {
                    writer.write("String");
                }
                if (i != columnTypes.size() - 1) {
                    writer.write(" ");
                }
            }
        } catch (IOException e) {
            throw new IOException("Error writing a signature file", e);
        }
        for (int dir = 0; dir < arraySize; dir++) {
            boolean dirRequired = false;
            for (int file = 0; file < arraySize; file++) {
                if (!map[dir][file].empty()) {
                    dirRequired = true;
                    break;
                }
            }
            String relative = String.format("%d.dir", dir);
            File directory = new File(location, relative);
            if (directory.exists() && !directory.isDirectory()) {
                throw new RuntimeException(String.format("%s is not a directory", relative));
            }
            if (!directory.exists() && dirRequired) {
                if (!directory.mkdir()) {
                    throw new RuntimeException(String.format("Can't create directory %s", relative));
                }
            }
            if (directory.exists()) {
                for (int file = 0; file < arraySize; file++) {
                    File db = map[dir][file].getFile();
                    if (map[dir][file].empty()) {
                        if (db.exists()) {
                            if (!db.delete()) {
                                throw new RuntimeException(String.format("Can't delete file %s",
                                        db.getCanonicalPath()));
                            }
                        }
                    } else {
                        try {
                            map[dir][file].writeToDisk(this, tableProvider);
                        } catch (RuntimeException e) {
                            throw new RuntimeException(String.format("Error in file %d.dir/%d.dat", dir, file), e);
                        }
                    }
                }
                File[] files = directory.listFiles();
                if (files != null && files.length == 0) {
                    if (!directory.delete()) {
                        throw new RuntimeException(String.format("Can't delete directory %s",
                                directory.getCanonicalPath()));
                    }
                }
            }
        }
    }

    public boolean storeableEqual(Storeable first, Storeable second) {
        checkState();
        if (first == null && second == null) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }
        for (int i = 0; i < columnTypes.size(); i++) {
            if (first.getColumnAt(i) == null) {
                if (second.getColumnAt(i) != null) {
                    return false;
                }
            } else if (!first.getColumnAt(i).equals(second.getColumnAt(i))) {
                return false;
            }
        }
        return true;
    }

    public Storeable put(HashMap<String, Storeable> diff, String key, Storeable value)
            throws ColumnFormatException {
        checkState();
        if (diff == null) {
            throw new IllegalArgumentException("Null diff");
        }
        if (key == null) {
            throw new IllegalArgumentException("Null pointer instead of string");
        }
        if (value == null) {
            throw new IllegalArgumentException("Null value");
        }
        if (key.equals("")) {
            throw new IllegalArgumentException("Empty key");
        }
        if (!newLineCheck(key)) {
            throw new IllegalArgumentException("New-line in key or value");
        }
        if (!checkColumnTypes(value)) {
            throw new ColumnFormatException("Type mismatch");
        }
        if (!whiteSpaceCheck(key)) {
            throw new IllegalArgumentException("Whitespace not allowed in key");
        }
        int hashCode = Math.abs(key.hashCode());
        int dir = (hashCode % 16 + 16) % 16;
        int file = ((hashCode / 16 % 16) + 16) % 16;
        if (diff.containsKey(key)) {
            return diff.put(key, value);
        }
        Storeable result = null;
        lock.readLock().lock();
        try {
            result = map[dir][file].get(key);
            diff.put(key, value);
        } finally {
            lock.readLock().unlock();
        }
        return result;
    }

    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        return put(transaction.get().getDiff(), key, value);
    }

    public Storeable get(HashMap<String, Storeable> diff, String key) {
        checkState();
        if (diff == null) {
            throw new IllegalArgumentException("Null diff");
        }
        if (key == null) {
            throw new IllegalArgumentException("Null pointer instead of string");
        }
        if (key.equals("")) {
            throw new IllegalArgumentException("Empty key");
        }
        int hashCode = Math.abs(key.hashCode());
        int dir = (hashCode % 16 + 16) % 16;
        int file = ((hashCode / 16 % 16) + 16) % 16;
        if (diff.containsKey(key)) {
            return diff.get(key);
        }
        Storeable result = null;
        lock.readLock().lock();
        try {
            result = map[dir][file].get(key);
        } finally {
            lock.readLock().unlock();
        }
        return result;
    }

    public Storeable get(String key) {
        return get(transaction.get().getDiff(), key);
    }

    public Storeable remove(HashMap<String, Storeable> diff, String key) {
        checkState();
        if (diff == null) {
            throw new IllegalArgumentException("Null diff");
        }
        if (key == null) {
            throw new IllegalArgumentException("Null pointer instead of string");
        }
        if (key.equals("")) {
            throw new IllegalArgumentException("Empty key");
        }
        int hashCode = Math.abs(key.hashCode());
        int dir = (hashCode % 16 + 16) % 16;
        int file = ((hashCode / 16 % 16) + 16) % 16;
        if (diff.containsKey(key)) {
            return diff.put(key, null);
        }
        Storeable result = null;
        lock.readLock().lock();
        try {
            result = map[dir][file].get(key);
            if (result != null) {
                diff.put(key, null);
            }
        } finally {
            lock.readLock().unlock();
        }
        return result;
    }

    public Storeable remove(String key) {
        return remove(transaction.get().getDiff(), key);
    }

    public int uncommittedChanges(HashMap<String, Storeable> diff) {
        checkState();
        if (diff == null) {
            throw new IllegalArgumentException("Null diff");
        }
        int result = 0;
        lock.readLock().lock();
        try {
            for (Map.Entry<String, Storeable> entry : diff.entrySet()) {
                int hashCode = Math.abs(entry.getKey().hashCode());
                int dir = (hashCode % 16 + 16) % 16;
                int file = ((hashCode / 16 % 16) + 16) % 16;
                if (entry.getValue() == null) {
                    if (map[dir][file].get(entry.getKey()) != null) {
                        result++;
                    }
                } else if (!storeableEqual(entry.getValue(), map[dir][file].get(entry.getKey()))) {
                    result++;
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return result;
    }

    public int uncommittedChanges() {
        return uncommittedChanges(transaction.get().getDiff());
    }

    public int commit(HashMap<String, Storeable> diff) throws IOException {
        checkState();
        if (diff == null) {
            throw new IllegalArgumentException("Null diff");
        }
        int changes = 0;
        lock.writeLock().lock();
        try {
            changes = uncommittedChanges(diff);
            for (Map.Entry<String, Storeable> entry : diff.entrySet()) {
                int hashCode = Math.abs(entry.getKey().hashCode());
                int dir = (hashCode % 16 + 16) % 16;
                int file = ((hashCode / 16 % 16) + 16) % 16;
                if (entry.getValue() == null) {
                    map[dir][file].remove(entry.getKey());
                } else {
                    map[dir][file].put(entry.getKey(), entry.getValue());
                }
            }
            writeToDisk();
        } finally {
            lock.writeLock().unlock();
        }
        return changes;
    }

    public int commit() throws IOException {
        int result = commit(transaction.get().getDiff());
        transaction.get().clear();
        return result;
    }

    public int rollback(HashMap<String, Storeable> diff) {
        checkState();
        if (diff == null) {
            throw new IllegalArgumentException("Null diff");
        }
        return uncommittedChanges(diff);
    }

    public int rollback() {
        int result = rollback(transaction.get().getDiff());
        transaction.get().clear();
        return result;
    }

    public int getColumnsCount() {
        checkState();
        return columnTypes.size();
    }

    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        checkState();
        if (columnIndex >= getColumnsCount() || columnIndex < 0) {
            throw new IndexOutOfBoundsException(String.format("Index out of bounds: array size %d, found %d",
                    columnTypes.size(), columnIndex));
        }
        return columnTypes.get(columnIndex);
    }

    @Override
    public String toString() {
        checkState();
        try {
            return String.format("%s[%s]", this.getClass().getSimpleName(), location.getCanonicalPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkState() {
        if (isClosed) {
            if (transaction.get().isSet()) {
                transaction.get().clear();
            }
            throw new IllegalStateException("Table is closed");
        }
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void close() {
        transaction.get().clear();
        isClosed = true;
    }

    public List<String> list() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < arraySize; i++) {
            for (int j = 0; j < arraySize; j++) {
                list.addAll(Arrays.asList(map[i][j].getKeysList()));
            }
        }
        return list;
    }
}



