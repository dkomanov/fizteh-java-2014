package ru.fizteh.fivt.students.LevkovMiron.Proxy;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Мирон on 08.11.2014 ru.fizteh.fivt.students.LevkovMiron.Storeable.
 */
public class CTable implements Table, AutoCloseable {

    private boolean closed = false;

    private HashMap<String, Storeable> table = new HashMap<>();

    private ThreadLocal<HashMap<String, Storeable>> changed = new ThreadLocal<>();
    private ThreadLocal<HashMap<String, Storeable>> put = new ThreadLocal<>();
    private ThreadLocal<HashSet<String>> removed = new ThreadLocal<>();

    private ArrayList<Class<?>> signature = new ArrayList<>();
    private Path dbPath;
    private String name;

    private ReentrantReadWriteLock lock;

    private static Pattern fileNamePattern = Pattern.compile("^([0-9]|1[0-5])\\.dat$");
    private static Pattern directoryNamePattern = Pattern.compile("^([0-9]|1[0-5])\\.dir$");

    public CTable(Path path, ReentrantReadWriteLock thisLock) throws IOException {
        removed.set(new HashSet<>());
        put.set(new HashMap<>());
        changed.set(new HashMap<>());
        dbPath = path;
        lock = thisLock;
        name = path.getFileName().toString();
        try {
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
        } catch (IOException e) {
            throw new IOException("Directory creation failed: " + e.getMessage());
        }
        loadData();
    }

    public CTable(Path path, List<Class<?>> columnTypes, ReentrantReadWriteLock thisLock) throws IOException {
        removed.set(new HashSet<>());
        put.set(new HashMap<>());
        changed.set(new HashMap<>());
        dbPath = path;
        lock = thisLock;
        name = path.getFileName().toString();
        try {
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
        } catch (IOException e) {
            throw new IOException("Directory creation failed: " + e.getMessage());
        }
        loadData();
        signature = new ArrayList<>(columnTypes);
        writeSignature();
    }

    void open() {
        closed = false;
    }

    @Override
    public void close() throws Exception {
        rollback();
        closed = true;
    }

    @Override
    public String toString() throws IllegalStateException {
        if (closed) {
            throw new IllegalStateException("Table is closed");
        }
        return getClass().getSimpleName() + "[" + dbPath.toAbsolutePath() + "]";
    }

    @Override
    public int commit() throws IOException, IllegalStateException {
        if (closed) {
            throw new IllegalStateException("Table is closed");
        }
        try {
            lock.writeLock().lock();
            int result = changesNumber();
            for (String s : removed.get()) {
                table.remove(s);
            }
            table.putAll(put.get());
            table.putAll(changed.get());
            drop();
            try {
                rewrite();
            } catch (IOException e) {
                throw new IOException("Can't commit");
            }
            return result;
        } finally {
            put.get().clear();
            changed.get().clear();
            removed.get().clear();
            lock.writeLock().unlock();
        }
    }

    public Storeable get(String key) throws IllegalStateException {
        if (closed) {
            throw new IllegalStateException("Table is closed");
        }
        if (key == null) {
            throw new IllegalArgumentException();
        }
        Storeable value = null;
        lock.readLock().lock();
        if (table.keySet().contains(key)) {
            value = table.get(key);
        }
        if (removed.get().contains(key)) {
            value = null;
        }
        if (put.get().keySet().contains(key)) {
            value = put.get().get(key);
        }
        if (changed.get().keySet().contains(key)) {
            value = changed.get().get(key);
        }
        lock.readLock().unlock();
        return value;
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException, IllegalStateException {
        if (closed) {
            throw new IllegalStateException("Table is closed");
        }
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        Storeable valueBefore = null;
        lock.readLock().lock();
        if (table.keySet().contains(key)) {
            valueBefore = table.get(key);
        }
        if (removed.get().contains(key)) {
            valueBefore = null;
            removed.get().remove(key);
        }
        if (put.get().keySet().contains(key)) {
            valueBefore = put.get().get(key);
        }
        if (changed.get().keySet().contains(key)) {
            valueBefore = changed.get().get(key);
        }
        if (table.keySet().contains(key)) {
            changed.get().put(key, value);
        } else {
            put.get().put(key, value);
        }
        lock.readLock().unlock();
        return value;
    }

    @Override
    public Storeable remove(String key) throws IllegalStateException {
        if (closed) {
            throw new IllegalStateException("Table is closed");
        }
        if (key == null) {
            throw new IllegalArgumentException();
        }
        Storeable value = null;
        lock.readLock().lock();
        if (table.keySet().contains(key)) {
            value = table.get(key);
        }
        if (removed.get().contains(key)) {
            value = null;
        }
        if (put.get().keySet().contains(key)) {
            value = put.get().remove(key);
        }
        if (changed.get().keySet().contains(key)) {
            value = changed.get().remove(key);
        }
        removed.get().add(key);
        lock.readLock().unlock();
        return value;
    }

    public String getName() throws IllegalStateException {
        if (closed) {
            throw new IllegalStateException("Table is closed");
        }
        return name;
    }

    @Override
    public int size() throws IllegalStateException {
        if (closed) {
            throw new IllegalStateException("Table is closed");
        }
        try {
            lock.readLock().lock();
            int sz = put.get().size()  + table.size();
            for (String key : removed.get()) {
                if (table.containsKey(key)) {
                    sz--;
                }
            }
            return sz;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int getColumnsCount() throws IllegalStateException {
        if (closed) {
            throw new IllegalStateException("Table is closed");
        }
        return signature.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException, IllegalStateException {
        if (closed) {
            throw new IllegalStateException("Table is closed");
        }
        return signature.get(columnIndex);
    }

    @Override
    public int rollback() throws IllegalStateException {
        if (closed) {
            throw new IllegalStateException("Table is closed");
        }
        int result = changesNumber();
        removed.get().clear();
        put.get().clear();
        changed.get().clear();
        return result;
    }

    int changesNumber() {
        int res = changed.get().size() + put.get().size() + removed.get().size();
        return res;
    }

    public List<String> list() {
        if (closed) {
            throw new IllegalStateException("Table is closed");
        }
        lock.readLock().lock();
        Set<String> res = new HashSet<String>();
        res.addAll(table.keySet());
        res.addAll(put.get().keySet());
        res.removeAll(removed.get());
        lock.readLock().unlock();
        return new ArrayList<String>(res);
    }

    public void drop() throws IOException {
        if (closed) {
            throw new IllegalStateException("Table is closed");
        }
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
            throw new IOException("Dropping failed: " + e.getMessage());
        }
    }

    private String readString(DataInputStream inStream, int size) throws IOException {
        ArrayList<Byte> tempData = new ArrayList<Byte>();
        for (int i = 0; i < size; i++) {
            byte[] oneByte = new byte[1];
            if (inStream.read(oneByte) == -1) {
                throw new IOException("Unexpected end of file.");
            }
            tempData.add(oneByte[0]);
        }
        try {
            byte[] utfData = new byte[size];
            for (int i = 0; i < tempData.size(); i++) {
                utfData[i] = tempData.get(i);
            }
            return new String(utfData, "UTF-8");
        } catch (OutOfMemoryError e) {
            throw new IOException("Data is too large.");
        }
    }

    private int readInt(DataInputStream inStream) throws IOException {
        byte[] utfData = new byte[4];
        if (inStream.read(utfData) < 4) {
            throw new IOException("Unexpected end of file.");
        }
        int value = ByteBuffer.wrap(utfData).getInt();
        if (value < 0) {
            throw new IOException();
        }
        return value;
    }

    private Storeable readStoreable(DataInputStream inStream, int size) throws IOException {
        String value = readString(inStream, size);
        try {
            return new Parser().deserialize(this, value);
        } catch (ParseException e) {
            throw new IOException("Invalid JSON format");
        }
    }

    private void writeIntAndString(FileOutputStream stream, String key) throws IOException {
        byte[] data = ByteBuffer.allocate(4).putInt(key.length()).array();
        stream.write(data);
        stream.write(key.getBytes("UTF-8"));
    }

    private void writeStoreable(FileOutputStream stream, Storeable value) throws IOException {
        String stringValue = new Parser().serialize(this, value);
        writeIntAndString(stream, stringValue);
    }

    private void readSignature() throws IOException {
        Map<String, Class> mp = new HashMap<>();
        mp.put("int", Integer.class);
        mp.put("long", Long.class);
        mp.put("float", Float.class);
        mp.put("double", Double.class);
        mp.put("byte", Byte.class);
        mp.put("boolean", Boolean.class);
        mp.put("String", String.class);
        signature.clear();
        try (Scanner scanner = new Scanner(new FileInputStream(dbPath.resolve("signature.tsv").toString()))) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                for (String linePart : line.split("\t")) {
                    signature.add(mp.get(linePart));
                }
            }
        }
    }

    private void writeSignature() throws IOException {
        Map<Class, String> mp = new HashMap<>();
        mp.put(Integer.class, "int");
        mp.put(Long.class, "long");
        mp.put(Float.class, "float");
        mp.put(Double.class, "double");
        mp.put(Byte.class, "byte");
        mp.put(Boolean.class, "boolean");
        mp.put(String.class, "String");
        try (PrintWriter writer = new PrintWriter(dbPath.resolve("signature.tsv").toString())) {
            for (Class<?> cl : signature) {
                writer.print(mp.get(cl));
                writer.print("\t");
            }
        }
    }

    private void loadData() throws IOException {
        try {
            if (!Files.exists(dbPath.resolve("signature.tsv"))) {
                Files.createFile(dbPath.resolve("signature.tsv"));
            }
            readSignature();
        } catch (IOException e) {
            throw new IOException("Failed while reading: " + e.getMessage());
        }
        try (DirectoryStream<Path> tableStream = Files.newDirectoryStream(dbPath)) {
            for (Path dir : tableStream) {
                if (dir.getFileName().toString().equals("signature.tsv")) {
                    continue;
                }
                Matcher matcher = directoryNamePattern.matcher(dir.getFileName().toString());
                if (!Files.isDirectory(dir) || !matcher.find()) {
                    throw new IOException("Other files in the table directory");
                }
                int d = Integer.decode(matcher.group(1));
                try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(dir)) {
                    for (Path file : dirStream) {
                        Matcher fileMatcher = fileNamePattern.matcher(file.getFileName().toString());
                        if (!fileMatcher.find()) {
                            throw new IOException("Other files in the table directory");
                        }
                        int f = Integer.decode(fileMatcher.group(1));
                        try (DataInputStream stream = new DataInputStream(Files.newInputStream(file))) {
                            while (stream.available() > 0) {
                                int sz = readInt(stream);
                                String key = readString(stream, sz);
                                sz = readInt(stream);
                                Storeable value = readStoreable(stream, sz);
                                int h1 = key.hashCode() % 16;
                                int h2 = (key.hashCode() / 16) % 16;
                                table.put(key, value);
                                if (d != h1 || f != h2) {
                                    throw new IOException("Wrong key type found in the file");
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new IOException("Failed while reading: " + e.getMessage());
        }
    }

    private void rewrite() throws IOException {
        drop();
        Files.createDirectory(dbPath);
        Files.createFile(dbPath.resolve("signature.tsv"));
        writeSignature();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int count = 0;
                for (String key : table.keySet()) {
                    if (key.hashCode() % 16 == i && (key.hashCode() / 16) % 16 == j) {
                        count++;
                    }
                }
                if (count > 0) {
                    if (!Files.exists(dbPath.resolve(i + ".dir"))) {
                        Files.createDirectory(dbPath.resolve(i + ".dir"));
                    }
                    Files.createFile(dbPath.resolve(i + ".dir/" + j + ".dat"));
                    File file = new File(dbPath.resolve(i + ".dir/" + j + ".dat").toString());
                    rewriteFile(file, i, j);
                }
            }
        }
    }

    private void rewriteFile(File file, int i, int j) throws IOException, OutOfMemoryError {
        try (FileOutputStream outStream = new FileOutputStream(file)) {
            for (Map.Entry<String, Storeable> pair : table.entrySet()) {
                if (pair.getKey().hashCode() % 16 == i && (pair.getKey().hashCode() / 16) % 16 == j) {
                    writeIntAndString(outStream, pair.getKey());
                    writeStoreable(outStream, pair.getValue());
                }
            }
        }
    }
}
