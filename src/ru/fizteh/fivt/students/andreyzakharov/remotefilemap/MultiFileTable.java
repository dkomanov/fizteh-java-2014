package ru.fizteh.fivt.students.andreyzakharov.remotefilemap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static ru.fizteh.fivt.students.andreyzakharov.remotefilemap.MultiFileTableUtils.classToString;
import static ru.fizteh.fivt.students.andreyzakharov.remotefilemap.MultiFileTableUtils.stringToClass;

public class MultiFileTable implements Table, AutoCloseable {
    private class MultiFileTableDiff {
        Map<String, Storeable> added = new HashMap<>();
        Map<String, Storeable> changed = new HashMap<>();
        Set<String> removed = new HashSet<>();
        int pending = 0;
    }

    private List<Class<?>> signature = new ArrayList<>();
    private Map<String, Storeable> stableData = new HashMap<>();
    private ThreadLocal<MultiFileTableDiff> diff = new ThreadLocal<MultiFileTableDiff>() {
        @Override
        protected MultiFileTableDiff initialValue() {
            return new MultiFileTableDiff();
        }
    };
    private int stableVersion = 0;
    private ThreadLocal<Integer> version = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };
    private ReadWriteLock lock = new ReentrantReadWriteLock(true);
    private Path dbPath;
    private String name;

    private TableEntrySerializer serializer;
    private boolean closed;

    private static Pattern fileNamePattern = Pattern.compile("^([0-9]|1[0-5])\\.dat$");
    private static Pattern directoryNamePattern = Pattern.compile("^([0-9]|1[0-5])\\.dir$");

    /**
     * Creates a MultiFileTable instance and loads data from disk. Table signature is inferred from loaded data.
     *
     * @param path       Root for the store.
     * @param serializer An object that transforms TableEntry rows to String values and back.
     * @throws ConnectionInterruptException An I/O error occurred during disk operations.
     */
    public MultiFileTable(Path path, TableEntrySerializer serializer) throws ConnectionInterruptException {
        dbPath = path;
        name = path.getFileName().toString();
        try {
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
        } catch (IOException e) {
            throw new ConnectionInterruptException("database: directory creation failed: " + e.getMessage());
        }
        this.serializer = serializer;
        load();
    }

    /**
     * Creates a MultiFileTable instance and unloads it to disk.
     *
     * @param path       Root for the store.
     * @param signature  Types for table columns.
     * @param serializer An object that transforms TableEntry rows to String values and back.
     * @throws ConnectionInterruptException An I/O error occurred during disk operations.
     */
    public MultiFileTable(Path path, List<Class<?>> signature, TableEntrySerializer serializer)
            throws ConnectionInterruptException {
        dbPath = path;
        name = path.getFileName().toString();
        try {
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
        } catch (IOException e) {
            throw new ConnectionInterruptException("database: directory creation failed: " + e.getMessage());
        }
        this.serializer = serializer;
        this.signature = new ArrayList<>(signature);
        unload();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + dbPath.toAbsolutePath().normalize().toString() + "]";
    }

    @Override
    public void close() throws Exception {
        rollback();
        closed = true;
    }

    private void checkClosed() throws IllegalStateException {
        if (closed) {
            throw new IllegalStateException("database: db was closed");
        }
    }

    @Override
    public String getName() {
        checkClosed();
        return name;
    }

    @Override
    public Storeable get(String key) throws IllegalStateException {
        checkClosed();
        if (key == null) {
            throw new IllegalArgumentException("null argument");
        } else if (key.isEmpty()) {
            throw new IllegalArgumentException("empty argument");
        }

        try {
            lock.readLock().lock();
            sync();

            if (diff.get().removed.contains(key)) {
                return null;
            } else if (diff.get().added.containsKey(key)) {
                return diff.get().added.get(key);
            } else if (diff.get().changed.containsKey(key)) {
                return diff.get().changed.get(key);
            } else {
                return stableData.get(key);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Storeable put(String key, Storeable value) {
        checkClosed();
        if (key == null || value == null) {
            throw new IllegalArgumentException("null argument");
        } else if (key.isEmpty()) {
            throw new IllegalArgumentException("empty argument");
        }

        try {
            lock.readLock().lock();
            sync();

            if (diff.get().removed.remove(key)) {
                if (stableData.get(key).equals(value)) {
                    --diff.get().pending;
                } else {
                    diff.get().changed.put(key, value);
                }
                return null;
            } else {
                if (stableData.containsKey(key)) {
                    if (diff.get().changed.containsKey(key)) {
                        if (stableData.get(key).equals(value)) {
                            --diff.get().pending;
                            return diff.get().changed.remove(key);
                        } else {
                            return diff.get().changed.put(key, value);
                        }
                    } else {
                        ++diff.get().pending;
                        diff.get().changed.put(key, value);
                        return stableData.get(key);
                    }
                } else {
                    if (!diff.get().added.containsKey(key)) {
                        ++diff.get().pending;
                    }
                    return diff.get().added.put(key, value);
                }
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Storeable remove(String key) {
        checkClosed();
        if (key == null) {
            throw new IllegalArgumentException("null argument");
        } else if (key.isEmpty()) {
            throw new IllegalArgumentException("empty argument");
        }

        try {
            lock.readLock().lock();
            sync();

            if (stableData.containsKey(key)) {
                if (diff.get().removed.add(key)) {
                    if (diff.get().changed.containsKey(key)) {
                        return diff.get().changed.remove(key);
                    } else {
                        ++diff.get().pending;
                        return stableData.get(key);
                    }
                } else {
                    return null;
                }
            } else {
                if (diff.get().added.containsKey(key)) {
                    --diff.get().pending;
                    return diff.get().added.remove(key);
                } else {
                    return null;
                }
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int size() {
        checkClosed();
        try {
            lock.readLock().lock();
            sync();
            return stableData.size() + diff.get().added.size() - diff.get().removed.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        checkClosed();
        try {
            lock.readLock().lock();
            sync();
            return diff.get().pending;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int commit() {
        checkClosed();
        try {
            lock.writeLock().lock();
            sync();

            stableData.keySet().removeAll(diff.get().removed);
            stableData.putAll(diff.get().changed);
            stableData.putAll(diff.get().added);

            try {
                unload();
            } catch (ConnectionInterruptException e) {
                lock.writeLock().unlock();
                return -1;
            }

            diff.get().removed.clear();
            diff.get().changed.clear();
            diff.get().added.clear();
            int p = diff.get().pending;
            diff.get().pending = 0;

            ++stableVersion;
            version.set(version.get() + 1);

            return p;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int rollback() {
        checkClosed();
        try {
            lock.readLock().lock();
            sync();

            diff.get().removed.clear();
            diff.get().changed.clear();
            diff.get().added.clear();
            int p = diff.get().pending;
            diff.get().pending = 0;
            return p;
        } finally {
            lock.readLock().unlock();
        }
    }

    private void sync() {
        if (version.get() < stableVersion) {
            for (String key : diff.get().removed) {
                if (stableData.containsKey(key)) {
                    diff.get().removed.remove(key);
                    --diff.get().pending;
                }
            }
            for (Map.Entry<String, Storeable> entry : diff.get().added.entrySet()) {
                if (stableData.containsKey(entry.getKey())) {
                    if (stableData.get(entry.getKey()) == entry.getValue()) {
                        diff.get().added.remove(entry.getKey());
                        --diff.get().pending;
                    } else {
                        diff.get().added.remove(entry.getKey());
                        diff.get().changed.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            for (Map.Entry<String, Storeable> entry : diff.get().changed.entrySet()) {
                Storeable value = stableData.get(entry.getKey());
                if (value == null) {
                    diff.get().changed.remove(entry.getKey());
                    diff.get().added.put(entry.getKey(), entry.getValue());
                } else if (value.equals(entry.getValue())) {
                    diff.get().changed.remove(entry.getKey());
                    --diff.get().pending;
                }
            }
            version.set(stableVersion);
        }
    }

    @Override
    public int getColumnsCount() {
        checkClosed();
        return signature.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        checkClosed();
        return signature.get(columnIndex);
    }

    @Override
    public List<String> list() {
        checkClosed();
        try {
            lock.readLock().lock();
            List<String> keySet = new ArrayList<>(stableData.keySet());
            keySet.removeAll(diff.get().removed);
            keySet.addAll(diff.get().added.keySet());
            return keySet;
        } finally {
            lock.readLock().unlock();
        }
    }

    private void clear() {
        stableData.clear();
    }

    private static class DirFilePair {
        public int d;
        public int f;
    }

    private static DirFilePair getHash(String key) {
        DirFilePair p = new DirFilePair();
        int hash = key.hashCode();
        p.d = (hash % 16 < 0) ? hash % 16 + 16 : hash % 16;
        hash /= 16;
        p.f = (hash % 16 < 0) ? hash % 16 + 16 : hash % 16;
        return p;
    }

    private void readSignature() throws IOException {
        signature.clear();
        try (BufferedReader reader = Files.newBufferedReader(dbPath.resolve("signature.tsv"))) {
            String line = reader.readLine();
            for (String token : line.split("\t")) {
                signature.add(stringToClass(token));
            }
        }
    }

    private String readKeyValue(DataInputStream is) throws IOException, ConnectionInterruptException {
        int keyLen = is.readInt();
        byte[] keyBytes = new byte[keyLen];
        int keyRead = is.read(keyBytes, 0, keyLen);
        if (keyRead != keyLen) {
            throw new ConnectionInterruptException("database: db file is invalid");
        }
        int valLen = is.readInt();
        byte[] valueBytes = new byte[valLen];
        int valRead = is.read(valueBytes, 0, valLen);
        if (valRead != valLen) {
            throw new ConnectionInterruptException("database: db file is invalid");
        }

        try {
            String key = new String(keyBytes, "UTF-8");
            Storeable value;
            value = serializer.deserialize(this, new String(valueBytes, "UTF-8"));
            stableData.put(key, value);
            return key;
        } catch (ParseException e) {
            throw new ConnectionInterruptException("database: JSON structure is invalid");
        }
    }

    public void load() throws ConnectionInterruptException {
        checkClosed();
        clear();
        try {
            readSignature();
        } catch (IOException e) {
            throw new ConnectionInterruptException("database: read failed: " + e.getMessage());
        }
        try (DirectoryStream<Path> tableStream = Files.newDirectoryStream(dbPath)) {
            for (Path dir : tableStream) {
                if (dir.getFileName().toString().equals("signature.tsv")) {
                    continue;
                }
                Matcher dirMatcher = directoryNamePattern.matcher(dir.getFileName().toString());
                if (!Files.isDirectory(dir)) {
                    throw new ConnectionInterruptException("database: extra objects in table folder");
                }
                if (!dirMatcher.find()) {
                    throw new ConnectionInterruptException("database: extra objects in table folder");
                }
                int d = Integer.decode(dirMatcher.group(1));
                try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(dir)) {
                    for (Path file : dirStream) {
                        Matcher fileMatcher = fileNamePattern.matcher(file.getFileName().toString());
                        if (!fileMatcher.find()) {
                            throw new ConnectionInterruptException("database: extra objects in table folder");
                        }
                        int f = Integer.decode(fileMatcher.group(1));
                        try (DataInputStream fileStream = new DataInputStream(Files.newInputStream(file))) {
                            while (fileStream.available() > 0) {
                                String key = readKeyValue(fileStream);
                                DirFilePair p = getHash(key);
                                if (d != p.d || f != p.f) {
                                    throw new ConnectionInterruptException(
                                            "database: key/file correspondence is invalid");
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new ConnectionInterruptException("database: read failed: " + e.getMessage());
        }
    }

    private void writeSignature() throws IOException {
        PrintWriter out = new PrintWriter(dbPath.resolve("signature.tsv").toString());
        for (Class<?> type : signature) {
            out.print(classToString(type));
            out.print("\t");
        }
        out.close();
    }

    private void writeKeyValue(DataOutputStream os, String key, Storeable value) throws IOException {
        byte[] keyBytes = key.getBytes("UTF-8");
        byte[] valueBytes = serializer.serialize(this, value).getBytes("UTF-8");
        os.writeInt(keyBytes.length);
        os.write(keyBytes);
        os.writeInt(valueBytes.length);
        os.write(valueBytes);
    }

    public void unload() throws ConnectionInterruptException {
        checkClosed();
        int[][] status = new int[16][16];

        DirFilePair p;
        for (String key : diff.get().added.keySet()) {
            p = getHash(key);
            status[p.d][p.f] = 1;
        }
        for (String key : diff.get().removed) {
            p = getHash(key);
            status[p.d][p.f] = -2;
        }
        for (String key : diff.get().changed.keySet()) {
            p = getHash(key);
            status[p.d][p.f] = -1;
        }
        for (String key : stableData.keySet()) {
            p = getHash(key);
            if (status[p.d][p.f] < 0) {
                status[p.d][p.f] = -1;
            }
        }

        String error = null;
        DataOutputStream[][] streams = new DataOutputStream[16][16];
        try {
            if (!Files.exists(dbPath)) {
                Files.createDirectory(dbPath);
            }

            writeSignature();

            streams = new DataOutputStream[16][16];
            for (int i = 0; i < 16; i++) {
                if (!Files.exists(dbPath.resolve(i + ".dir/"))) {
                    Files.createDirectory(dbPath.resolve(i + ".dir/"));
                }
                for (int j = 0; j < 16; j++) {
                    if (status[i][j] < 0) {
                        Files.delete(dbPath.resolve(i + ".dir/" + j + ".dat"));
                    }
                    if (Math.abs(status[i][j]) == 1) {
                        streams[i][j] = new DataOutputStream(
                                Files.newOutputStream(dbPath.resolve(i + ".dir/" + j + ".dat"),
                                        APPEND, CREATE));
                    }
                }
            }

            for (HashMap.Entry<String, Storeable> entry : stableData.entrySet()) {
                p = getHash(entry.getKey());
                if (status[p.d][p.f] != 0) {
                    writeKeyValue(streams[p.d][p.f], entry.getKey(), entry.getValue());
                }
            }

            for (int i = 0; i < 16; i++) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(dbPath.resolve(i + ".dir/"))) {
                    if (!stream.iterator().hasNext()) {
                        Files.delete(dbPath.resolve(i + ".dir/"));
                    }
                }
            }
        } catch (IOException e) {
            error = e.getMessage();
        } finally {
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    if (streams[i][j] != null) {
                        try {
                            streams[i][j].close();
                        } catch (IOException e1) {
                            //
                        }
                    }
                }
            }
        }
        if (error != null) {
            throw new ConnectionInterruptException("database: write failed: " + error);
        }
    }

    public void delete() throws ConnectionInterruptException {
        try {
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    if (Files.exists(dbPath.resolve(i + ".dir/" + j + ".dat"))) {
                        Files.delete(dbPath.resolve(i + ".dir/" + j + ".dat"));
                    }
                }
                if (Files.exists(dbPath.resolve(i + ".dir/"))) {
                    Files.delete(dbPath.resolve(i + ".dir/"));
                }
            }
            if (Files.exists(dbPath.resolve("signature.tsv"))) {
                Files.delete(dbPath.resolve("signature.tsv"));
            }
            if (Files.exists(dbPath)) {
                Files.delete(dbPath);
            }
        } catch (IOException e) {
            throw new ConnectionInterruptException("database: delete failed: " + e.getMessage());
        }
    }
}
