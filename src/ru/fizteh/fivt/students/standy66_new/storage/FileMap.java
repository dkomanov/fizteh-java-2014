package ru.fizteh.fivt.students.standy66_new.storage;

import ru.fizteh.fivt.students.standy66_new.exceptions.FileCorruptedException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 * Created by astepanov on 26.09.14.
 */
public class FileMap implements Map<String, String>, AutoCloseable {
    private final String charsetName = "UTF-8";
    private File mapFile;
    private Map<String, String> cache;
    private Set<String> changed;

    public FileMap(File mapFile) throws IOException {
        if (mapFile == null) {
            throw new IllegalArgumentException("mapFile must not be null");
        }
        this.mapFile = mapFile.getAbsoluteFile();
        cache = new HashMap<>();
        changed = new HashSet<>();
        reload();
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public boolean isEmpty() {
        return cache.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return cache.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return cache.containsValue(value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        cache.putAll(m);
        changed.addAll(m.keySet());
    }

    @Override
    public Collection<String> values() {
        return cache.values();
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return cache.entrySet();
    }

    @Override
    public Set<String> keySet() {
        return cache.keySet();
    }

    @Override
    public String get(Object key) {
        return cache.get(key);
    }

    @Override
    public String put(String key, String value) {
        changed.add(key);
        return cache.put(key, value);
    }

    @Override
    public String remove(Object key) {
        if (key instanceof String) {
            changed.add((String) key);
            return cache.remove(key);
        } else {
            return null;
        }
    }

    @Override
    public void clear() {
        changed.addAll(cache.keySet());
        cache.clear();
    }

    public int commit() throws IOException {
        int keyChangedCount = changed.size();
        changed.clear();
        if (cache.size() == 0) {
            if (mapFile.exists()) {
                mapFile.delete();
            }
            return keyChangedCount;
        }
        if (!mapFile.getParentFile().exists()) {
            mapFile.getParentFile().mkdirs();
        }
        if (!mapFile.exists()) {
            mapFile.createNewFile();
        }
        try (FileOutputStream fos = new FileOutputStream(mapFile, false)) {
            ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
            for (String key : cache.keySet()) {
                String value = cache.get(key);
                byte[] keyEncoded = key.getBytes(charsetName);
                fos.write(lengthBuffer.putInt(0, keyEncoded.length).array());
                fos.write(keyEncoded);
                byte[] valueEncoded = value.getBytes(charsetName);
                fos.write(lengthBuffer.putInt(0, valueEncoded.length).array());
                fos.write(valueEncoded);
            }
        }
        return keyChangedCount;
    }

    public int unsavedChangesCount() {
        return changed.size();
    }

    public int rollback() throws IOException {
        int keyChangedCount = changed.size();
        reload();
        return keyChangedCount;
    }

    @Override
    public void close() throws Exception {
        commit();
    }

    private void reload() throws IOException {
        cache.clear();
        changed.clear();
        if (!mapFile.exists()) {
            return;
        }

        try (FileChannel channel = new FileInputStream(mapFile).getChannel()) {
            ByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            try {

                while (buffer.remaining() > 0) {
                    int keySize = buffer.getInt();
                    if (keySize > channel.size()) {
                        throw new FileCorruptedException(String.format("%s is corrupted", mapFile.getName()));
                    }
                    byte[] key = new byte[keySize];
                    buffer.get(key);
                    int valueSize = buffer.getInt();
                    if (valueSize > channel.size()) {
                        throw new FileCorruptedException(String.format("%s is corrupted", mapFile.getName()));
                    }
                    byte[] value = new byte[valueSize];
                    buffer.get(value);
                    cache.put(new String(key, charsetName), new String(value, charsetName));
                }
            } catch (BufferUnderflowException e) {
                throw new FileCorruptedException(String.format("%s is corrupted", mapFile.getName()));
            } catch (NegativeArraySizeException e) {
                throw new FileCorruptedException(String.format("%s is corrupted", mapFile.getName()));
            }
        }
    }
}
