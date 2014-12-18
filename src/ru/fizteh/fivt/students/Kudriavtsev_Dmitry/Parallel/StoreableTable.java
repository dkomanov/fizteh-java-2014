package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Created by Дмитрий on 25.11.2014.
 */
public class StoreableTable implements Table {

    protected static final int FILES_COUNT = 16;
    protected static final int DIRECTORIES_COUNT = 16;
    private static final String ENCODING = "UTF-8";
    private static final String SIGNATUREFILENAME = "signature.tsv"; 

    public final Path dbPath;
    public String name;
    private final StoreableTableProvider provider;
    private List<Class<?>> signature;
    private final ReentrantReadWriteLock lock;

    private final ThreadLocal<Map<String, Integer>> changedFiles = new ThreadLocal<Map<String, Integer>>() {
            @Override
            protected Map<String, Integer> initialValue() {
                return new TreeMap<>();
            }
    };
    private final ThreadLocal<Map<String, Storeable>> activeTable = new ThreadLocal<Map<String, Storeable>>() {
        @Override
        protected Map<String, Storeable> initialValue() {
            return new HashMap<>();
        }
    };
    private final ThreadLocal<Map<String, Storeable>> removed = new ThreadLocal<Map<String, Storeable>>() {
        @Override
        protected Map<String, Storeable> initialValue() {
            return new HashMap<>();
        }
    };
    private final ThreadLocal<Map<String, Storeable>> newKey = new ThreadLocal<Map<String, Storeable>>() {
        @Override
        protected Map<String, Storeable> initialValue() {
            return new HashMap<>();
        }
    };

    public final Map<String, Class<?>> types;


    public Map<String, Integer> getChangedFiles() {
        return changedFiles.get();
    }

    public Map<String, Storeable> getActiveTable() {
        return activeTable.get();
    }

    public Map<String, Storeable> getNewKey() {
        return newKey.get();
    }

    public Map<String, Storeable> getRemoved() {
        return removed.get();
    }

    private Class<?> classByName(final String name) throws IOException {
        if (!types.containsKey(name)) {
            throw new IOException("Unknown type name: " + name);
        }
        return types.get(name);
    }

    public StoreableTable(final Path path) throws IOException {
        dbPath = path.normalize();
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        types = new StoreableTableProvider().getRevClassNames();
        provider = null;
        lock = new ReentrantReadWriteLock();
    }

    public StoreableTable(final StoreableTableProvider newProvider, final String dbName,
                          final String parentDir, final List<Class<?>> newSignature) {
        lock = new ReentrantReadWriteLock();
        name = dbName;
        dbPath = new File(parentDir + File.separator + dbName).toPath();
        signature = newSignature;
        provider = newProvider;
        types = provider.getRevClassNames();
        readDb();
    }

    public void readDb() {
        try {
            name = dbPath.getFileName().toString();
            File dbDir = dbPath.toFile();
            String dbDirPath = dbPath.toString();
            for (String subdirectory : dbDir.list()) {
                String dirPath = dbDirPath + File.separator + subdirectory;
                File dir = new File(dirPath);
                if (SIGNATUREFILENAME.equals(dir.getName())) {
                    continue;
                }
                if (!dir.isDirectory() || !isCorrectName(subdirectory, ".dir") || !isCorrectSubdirectory(dir)) {
                    throw new RuntimeException("Incorrect database directory");
                }
                for (String file : dir.list()) {
                    readFile(new File(dirPath + File.separator + file));
                }
            }
            Scanner fw = new Scanner(new File(dbDirPath + File.separator + SIGNATUREFILENAME));
            String[] newSignature = fw.nextLine().split(" ");
            signature = new ArrayList<>();
            for (String aNewSignature : newSignature) {
                signature.add(classByName(aNewSignature));
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't read db from file");
        } catch (NullPointerException e) {
            //ignore
        }
    }

    private boolean isCorrectSubdirectory(final File dir) {
        for (String file : dir.list()) {
            if (new File(file).isDirectory() || !isCorrectName(file, ".dat")) {
                return false;
            }
        }
        return true;
    }

    private boolean isCorrectName(String name, final String suf) {
            if (!name.endsWith(suf)) {
                return false;
            }
            name = name.replace(suf, "");
            int num = Integer.parseInt(name);
            return (0 <= num && num < DIRECTORIES_COUNT);
    }

    private void readFile(final File file) throws IOException {
        try {
            try (DataInputStream stream = new DataInputStream(
                    new FileInputStream(file))) {
                while (true) {
                    String key = readToken(stream);
                    String valueStr = readToken(stream);
                    if (key == null || valueStr == null) {
                        break;
                    }
                    Storeable value = provider.deserialize(this, valueStr);
                    checkSignature(value);
                    activeTable.get().put(key, value);
                }
            }
        } catch (ParseException e) {
            throw new IOException("Invalid db directory");
        }
    }

    private void checkSignature(final Storeable storeable) {
        try {
            for (int i = 0; i < signature.size(); ++i) {
                if (storeable.getColumnAt(i) == null) {
                    continue;
                }
                if (!storeable.getColumnAt(i).getClass().equals(signature.get(i))) {
                    throw new ColumnFormatException("Invalid column format");
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new ColumnFormatException("Invalid column number");
        }
    }

    private String readToken(final DataInputStream stream) throws IOException {
        if (stream.available() == 0) {
            return null;
        }
        int size = stream.readInt();
        byte[] buf = new byte[size];
        stream.readFully(buf);
        return new String(buf, ENCODING);
    }

    public int countOfCollisionsInFile(final Path path) {
        int count = 0;
        try (DataInputStream iStream = new DataInputStream(Files.newInputStream(path))) {
            while (iStream.available() > 0) {
                readToken(iStream);
                readToken(iStream);
                ++count;
            }
        } catch (IOException e) {
            System.err.println("It was exception on creating stream or in reading from file " + path.toString());
            return -1;
        }
        return count;
    }

    @Override
    public Storeable put(final String key, final Storeable value)
            throws IllegalArgumentException {
        checkArg("key", key);
        checkArg("value", value);
        checkSignature(value);
        lock.readLock().lock();
        try {
            if (activeTable.get().containsKey(key) 
                && ((CurrentStoreable) activeTable.get().get(key)).getValues().equals(
                    ((CurrentStoreable) value).getValues())) {
                if (newKey.get().containsKey(key)) {
                    newKey.get().remove(key);
                }
                return value;
            }
            removed.get().remove(key);
            return newKey.get().put(key, value);
        } finally {
            lock.readLock().unlock();
        }
    }

    private void checkArg(final String name, final  Object value)
            throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException(name + " shouldn't be null");
        }
    }

    @Override
    public Storeable remove(final String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        lock.readLock().lock();
        try {
            if (!activeTable.get().containsKey(key) && !newKey.get().containsKey(key)) {
                return null;
            }
            if (!newKey.get().containsKey(key)) {
                removed.get().put(key, activeTable.get().get(key));
                return activeTable.get().get(key);
            } else {
                Storeable value = newKey.get().get(key);
                newKey.get().remove(key);
                return value;
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int size() {
        return list().size();
    }

    @Override
    public java.util.List<String> list() {
        lock.readLock().lock();
        try {
            java.util.List<String> result = newKey.get().keySet().stream().collect(Collectors.toList());
            result.addAll(
                    activeTable.get().keySet().stream().filter(
                            key -> !newKey.get().containsKey(key)).collect(Collectors.toList()));
            removed.get().keySet().forEach(result::remove);
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int commit() {
        lock.readLock().lock();
        try {
            removed.get().keySet().forEach(activeTable.get()::remove);
            for (Map.Entry<String, Storeable> entry : newKey.get().entrySet()) {
                activeTable.get().put(entry.getKey(), entry.getValue());
            }
            unload(this, getName());
            return remindChanges(true);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int rollback() {
        return remindChanges(false);
    }

    private int remindChanges(final boolean inCommit) {
        ThreadLocal<Integer> removedCount = new ThreadLocal<Integer>() {
            @Override
            protected Integer initialValue() {
                return 0;
            }
        };
        if (!inCommit) {
            for (String s : removed.get().keySet()) {
                if (activeTable.get().containsKey(s)) {
                    inc(removedCount);
                }
            }
        } else {
            removedCount.set(removed.get().size());
        }
        ThreadLocal<Integer> count = new ThreadLocal<Integer>() {
            @Override
            protected Integer initialValue() {
                return 0;
            }
        };
        count.set(removedCount.get() + newKey.get().size());
        removed.get().clear();
        newKey.get().clear();
        return count.get();
    }

    private Path nameOfPath(String nameOfTable, int directory, int file) {
        if (nameOfTable.equals("")) {
            return dbPath.resolve(directory + formatOfDirectory + File.separator + file + formatOfFile);
        }
        return dbPath.resolve(nameOfTable + File.separator + directory + formatOfDirectory
            + File.separator + file + formatOfFile);
    }

    private Path nameOfPath(String nameOfTable, int directory) {
        if (nameOfTable.equals("")) {
            return dbPath.resolve(directory + formatOfDirectory + File.separator);
        }
        return dbPath.resolve(nameOfTable + File.separator + directory + formatOfDirectory + File.separator);
    }

    public AbstractMap.SimpleEntry<String, AbstractMap.SimpleEntry<Integer, Integer>>
                                            whereToSave(final String nameOfTable, final String value) {
        int hashCode = value.hashCode();
        int d = hashCode % DIRECTORIES_COUNT;
        int f = hashCode / DIRECTORIES_COUNT % FILES_COUNT;
        return new AbstractMap.SimpleEntry<>(
                nameOfPath(nameOfTable, d, f).toString(),
                new AbstractMap.SimpleEntry<>(d, f));
    }

    public void deleteFiles(final String nameOfTable, final boolean all) {
        lock.readLock().lock();
        try {
            for (int i = 0; i < DIRECTORIES_COUNT; ++i) {
                for (int j = 0; j < FILES_COUNT; ++j) {
                    if (changedFiles.get().containsKey(nameOfPath(nameOfTable, i, j).toString()) || all) {
                        if (Files.exists(nameOfPath(nameOfTable, i, j))) {
                            Files.delete(nameOfPath(nameOfTable, i, j));
                        }
                        if (Files.exists(nameOfPath(nameOfTable, i))
                                && nameOfPath(nameOfTable, i).toFile().list().length == 0) {
                            Files.delete(nameOfPath(nameOfTable, i));
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Can't delete from disk: " + e.getMessage());
            System.exit(-1);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void unload(final StoreableTable currentTable, final String nameOfTable) {
        deleteFiles(nameOfTable, false);
        boolean[] dir = new boolean[DIRECTORIES_COUNT];
        DataOutputStream[][] streams = new DataOutputStream[DIRECTORIES_COUNT][FILES_COUNT];
        lock.readLock().lock();
        try {
            if (!Files.exists(dbPath)) {
                Files.createDirectory(dbPath);
            }
            for (Map.Entry<String, Storeable> entry :  currentTable.activeTable.get().entrySet()) {
                AbstractMap.SimpleEntry<String, AbstractMap.SimpleEntry<Integer, Integer>> pathOfFile =
                        whereToSave("", entry.getKey());
                if (changedFiles.get().containsKey(pathOfFile.getKey())) {
                    int d = pathOfFile.getValue().getKey();
                    int f = pathOfFile.getValue().getValue();
                    if (streams[d][f] == null) {
                        if (!dir[d]) {
                            if (!Files.exists(nameOfPath("", d))) {
                                Files.createDirectory(nameOfPath("", d));
                            }
                            dir[d] = true;
                        }
                        streams[d][f] = new DataOutputStream(Files.newOutputStream(
                                nameOfPath("", d, f)));
                    }
                    writeToFile(streams[d][f], entry.getKey(), entry.getValue());
                    Integer collisionCount = changedFiles.get().get(pathOfFile.getKey());
                    if (collisionCount > 0) {
                        --collisionCount;
                        changedFiles.get().put(pathOfFile.getKey(), collisionCount);
                    } else {
                        changedFiles.get().remove(pathOfFile.getKey());
                    }
                }
            }
            changedFiles.get().clear();
        } catch (IOException e) {
            System.err.println("Can't write to disk: " + e.getMessage());
            changedFiles.get().clear();
            System.exit(-1);
        } finally {
            for (int i = 0; i < DIRECTORIES_COUNT; ++i) {
                for (int j = 0; j < FILES_COUNT; ++j) {
                    if (streams[i][j] != null) {
                        try {
                            streams[i][j].close();
                        } catch (IOException ignored) {
                            continue;
                        }
                    }
                }
            }
            lock.readLock().unlock();
        }
    }

    private void writeToFile(final DataOutputStream oStream, final String key, final Storeable value)
            throws IOException {
        lock.readLock().lock();
        try {
            writeToken(oStream, key);
            String strValue;
            if (provider != null) {
                strValue = provider.serialize(this, value);
            } else {
                strValue = (new StoreableTableProvider(dbPath.toString()).serialize(this, value));
            }
            writeToken(oStream, strValue);
        } finally {
            lock.readLock().unlock();
        }
    }

    private void writeToken(final DataOutputStream stream, final String str) throws IOException {
        byte[] strBytes = str.getBytes(ENCODING);
        stream.writeInt(strBytes.length);
        stream.write(strBytes);
    }

    public int getNumberOfUncommittedChanges() {
        ThreadLocal<Integer> count = new ThreadLocal<Integer>() {
            @Override
            protected Integer initialValue() {
                return 0;
            }
        };
        for (String s : removed.get().keySet()) {
            if (activeTable.get().containsKey(s)) {
                inc(count);
            }
        }
        count.set(count.get() + newKey.get().size());
        return count.get();
    }

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
    public Storeable get(String key)
            throws IllegalArgumentException {
        checkArg("key", key);
        lock.readLock().lock();
        try {
            if (newKey.get().containsKey(key)) {
                return newKey.get().get(key);
            }
            if (!activeTable.get().containsKey(key) || removed.get().containsKey(key)) {
                return null;
            }
            return activeTable.get().get(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    private void inc(ThreadLocal<Integer> number) {
        number.set(number.get() + 1);
    }

}
