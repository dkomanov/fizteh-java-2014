package ru.fizteh.fivt.students.andreyzakharov.stringfilemap;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileMap implements Table {
    private Map<String, String> stableData = new HashMap<>();
    private Map<String, String> added = new HashMap<>();
    private Map<String, String> changed = new HashMap<>();
    private Set<String> removed = new HashSet<>();
    private Path dbPath;
    private String name;
    private int pending = 0;

    public FileMap(Path path) {
        dbPath = path;
        name = path.getFileName().toString();
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                //
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("null argument");
        }
        return stableData.get(key);
    }

    @Override
    public String put(String key, String value) {
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
                ++pending;
                added.put(key, value);
                return null;
            }
        }
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("null argument");
        }

        if (removed.add(key)) {
            if (stableData.containsKey(key)) {
                if (changed.containsKey(key)) {
                    return changed.remove(key);
                } else {
                    ++pending;
                    return stableData.get(key);
                }
            } else {
                if (added.containsKey(key)) {
                    return added.remove(key);
                } else {
                    ++pending;
                    return stableData.get(key);
                }
            }
        } else {
            return null;
        }
    }

    @Override
    public int size() {
        return stableData.size() + added.size() - removed.size();
    }

    public int pending() {
        return pending;
    }

    @Override
    public int commit() {
        stableData.keySet().removeAll(removed);
        stableData.putAll(changed);
        stableData.putAll(added);

        removed.clear();
        changed.clear();
        added.clear();
        int p = pending;
        pending = 0;

        try {
            unload();
        } catch (ConnectionInterruptException e) {
            return -1;
        }
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
    public List<String> list() {
        return new ArrayList<>(stableData.keySet());
    }

    public void clear() {
        stableData.clear();
        added.clear();
        changed.clear();
        removed.clear();
        pending = 0;
    }

    private void readKeyValue(DataInputStream is) throws IOException, ConnectionInterruptException {
        int keyLen = is.readInt();
        byte[] key = new byte[keyLen];
        int keyRead = is.read(key, 0, keyLen);
        if (keyRead != keyLen) {
            throw new ConnectionInterruptException("database: db file is invalid");
        }
        int valLen = is.readInt();
        byte[] value = new byte[valLen];
        int valRead = is.read(value, 0, valLen);
        if (valRead != valLen) {
            throw new ConnectionInterruptException("database: db file is invalid");
        }

        stableData.put(new String(key, "UTF-8"), new String(value, "UTF-8"));
    }

    public void load() throws ConnectionInterruptException {
        clear();
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                try (DataInputStream stream = new DataInputStream(
                        Files.newInputStream(dbPath.resolve(i + ".dir/" + j + ".dat")))) {
                    while (stream.available() > 0) {
                        readKeyValue(stream);
                    }
                } catch (IOException e) {
                    // empty folder is a valid table
                }
            }
        }
    }

    private void writeKeyValue(DataOutputStream os, String keyString, String valueString) throws IOException {
        byte[] key = keyString.getBytes("UTF-8");
        byte[] value = valueString.getBytes("UTF-8");
        os.writeInt(key.length);
        os.write(key);
        os.writeInt(value.length);
        os.write(value);
    }

    public void unload() throws ConnectionInterruptException {
        if (pending == 0) {
            return;
        }

        try {
            clearFiles();
        } catch (ConnectionInterruptException e) {
            //
        }

        boolean[] dirUsed = new boolean[16];
        boolean[][] fileUsed = new boolean[16][16];
        DataOutputStream[][] streams = new DataOutputStream[16][16];
        try {
            if (!Files.exists(dbPath)) {
                Files.createDirectory(dbPath);
            }
            for (HashMap.Entry<String, String> entry : stableData.entrySet()) {
                int hash = entry.getKey().hashCode();
                int d = (hash % 16 < 0) ? hash % 16 + 16 : hash % 16;
                hash /= 16;
                int f = (hash % 16 < 0) ? hash % 16 + 16 : hash % 16;
                if (!fileUsed[d][f]) {
                    if (!dirUsed[d]) {
                        Files.createDirectory(dbPath.resolve(d + ".dir/"));
                        dirUsed[d] = true;
                    }
                    streams[d][f] = new DataOutputStream(
                            Files.newOutputStream(dbPath.resolve(d + ".dir/" + f + ".dat")));
                    fileUsed[d][f] = true;
                }
                writeKeyValue(streams[d][f], entry.getKey(), entry.getValue());
            }
        } catch (IOException e) {
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    if (streams[i][j] != null) {
                        try {
                            streams[i][j].close();
                        } catch (IOException e1) {
                            continue;
                        }
                    }
                }
            }
            throw new ConnectionInterruptException("database: writing to disk failed");
        } finally {
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    if (streams[i][j] != null) {
                        try {
                            streams[i][j].close();
                        } catch (IOException e) {
                            continue;
                        }
                    }
                }
            }
        }
    }

    private void clearFiles() throws ConnectionInterruptException {
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
        } catch (IOException e) {
            throw new ConnectionInterruptException("database: deleting table failed");
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
            Files.delete(dbPath);
        } catch (IOException e) {
            throw new ConnectionInterruptException("database: deleting table failed");
        }
    }
}
