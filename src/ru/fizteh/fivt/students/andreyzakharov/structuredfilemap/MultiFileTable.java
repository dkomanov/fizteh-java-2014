package ru.fizteh.fivt.students.andreyzakharov.structuredfilemap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import static ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.MultiFileTableUtils.*;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.StandardOpenOption.*;

public class MultiFileTable implements Table {
    private ArrayList<Class<?>> signature = new ArrayList<>();
    private Map<String, Storeable> stableData = new HashMap<>();
    private Map<String, Storeable> added = new HashMap<>();
    private Map<String, Storeable> changed = new HashMap<>();
    private Set<String> removed = new HashSet<>();
    private int pending = 0;
    private Path dbPath;
    private String name;

    private TableEntrySerializer serializer;

    private static Pattern fileNamePattern = Pattern.compile("^([0-9]|1[0-5])\\.dat$");
    private static Pattern directoryNamePattern = Pattern.compile("^([0-9]|1[0-5])\\.dir$");

    /**
     * Creates a MultiFileTable instance and loads data from disk. Table signature is inferred from loaded data.
     * @param path Root for the store.
     * @param serializer An object that transforms TableEntry rows to String values and back.
     *
     * @throws ConnectionInterruptException An I/O error occured during disk operations.
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
     * @param path Root for the store.
     * @param signature Types for table columns.
     * @param serializer An object that transforms TableEntry rows to String values and back.
     *
     * @throws ConnectionInterruptException An I/O error occured during disk operations.
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
    public String getName() {
        return name;
    }

    @Override
    public Storeable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("null argument");
        }

        if (removed.contains(key)) {
            return null;
        } else if (added.containsKey(key)) {
            return added.get(key);
        } else if (changed.containsKey(key)) {
            return changed.get(key);
        } else {
            return stableData.get(key);
        }
    }

    @Override
    public Storeable put(String key, Storeable value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("null argument");
        }

        if (removed.remove(key)) {
            if (stableData.get(key).equals(value)) {
                --pending;
            } else {
                changed.put(key, value);
            }
            return null;
        } else {
            if (stableData.containsKey(key)) {
                if (changed.containsKey(key)) {
                    if (stableData.get(key).equals(value)) {
                        --pending;
                        return changed.remove(key);
                    } else {
                        return changed.put(key, value);
                    }
                } else {
                    ++pending;
                    changed.put(key, value);
                    return stableData.get(key);
                }
            } else {
                if (!added.containsKey(key)) {
                    ++pending;
                }
                return added.put(key, value);
            }
        }
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("null argument");
        }

        if (stableData.containsKey(key)) {
            if (removed.add(key)) {
                if (changed.containsKey(key)) {
                    return changed.remove(key);
                } else {
                    ++pending;
                    return stableData.get(key);
                }
            } else {
                return null;
            }
        } else {
            if (added.containsKey(key)) {
                --pending;
                return added.remove(key);
            } else {
                return null;
            }
        }
    }

    @Override
    public int size() {
        return stableData.size() + added.size() - removed.size();
    }

    public int getPending() {
        return pending;
    }

    @Override
    public int commit() {
        stableData.keySet().removeAll(removed);
        stableData.putAll(changed);
        stableData.putAll(added);

        try {
            unload();
        } catch (ConnectionInterruptException e) {
            return -1;
        }

        removed.clear();
        changed.clear();
        added.clear();
        int p = pending;
        pending = 0;

        return p;
    }

    @Override
    public int rollback() {
        removed.clear();
        changed.clear();
        added.clear();
        int p = pending;
        pending = 0;
        return p;
    }

    @Override
    public int getColumnsCount() {
        return signature.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return signature.get(columnIndex);
    }

    /**
     * Returns list of keys in current version of the table.
     * @return List of keys.
     */
    public List<String> list() {
        List<String> keySet = new ArrayList<>(stableData.keySet());
        keySet.removeAll(removed);
        keySet.addAll(added.keySet());
        return keySet;
    }

    public void clear() {
        stableData.clear();
        added.clear();
        changed.clear();
        removed.clear();
        pending = 0;
    }

    private static class DfPair {
        public int d;
        public int f;
    }

    private static DfPair getHash(String key) {
        DfPair p = new DfPair();
        int hash = key.hashCode();
        p.d = (hash % 16 < 0) ? hash % 16 + 16 : hash % 16;
        hash /= 16;
        p.f = (hash % 16 < 0) ? hash % 16 + 16 : hash % 16;
        return p;
    }

    private void readSignature() throws IOException {
        signature.clear();
        try (BufferedReader reader = Files.newBufferedReader(dbPath.resolve("signature.tsv"))){
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
                        try (DataInputStream fileStream = new DataInputStream(
                                Files.newInputStream(dir.resolve(file)))) {
                            while (fileStream.available() > 0) {
                                String key = readKeyValue(fileStream);
                                DfPair p = getHash(key);
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
        int[][] status = new int[16][16];

        DfPair p;
        for (String key : added.keySet()) {
            p = getHash(key);
            status[p.d][p.f] = 1;
        }
        for (String key : removed) {
            p = getHash(key);
            status[p.d][p.f] = -2;
        }
        for (String key : changed.keySet()) {
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
