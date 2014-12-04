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

/** we have only one instance of the filemap
 *
 * Created by astepanov on 26.09.14.
 */
public class FileMap implements AutoCloseable {
    private static final String CHARSET_NAME = "UTF-8";
    private File mapFile;
    private Map<String, String> synchronizedCache;
    private ThreadLocal<Transaction> localDiff;

    public FileMap(File mapFile) throws IOException {
        if (mapFile == null) {
            throw new IllegalArgumentException("mapFile must not be null");
        }
        this.mapFile = mapFile.getAbsoluteFile();

        synchronizedCache = Collections.synchronizedMap(new HashMap<>());

        localDiff = new ThreadLocal<Transaction>() {
            @Override
            protected Transaction initialValue() {
                return new Transaction(synchronizedCache);
            }
        };

        reload();
    }

    public int size() {
        return localDiff.get().size();
    }

    public boolean isEmpty() {
        return localDiff.get().size() == 0;
    }

    public Set<String> keySet() {
        return localDiff.get().keySet();
    }

    public String get(String key) {
        return localDiff.get().get(key);
    }

    public String put(String key, String value) {
        return localDiff.get().put(key, value);
    }

    public String remove(String key) {
        return localDiff.get().remove(key);
    }

    public void clear() {
        for (String key : synchronizedCache.keySet()) {
            localDiff.get().remove(key);
        }
    }

    public int commit() throws IOException {
        int keyChangedCount = localDiff.get().diffSize();
        localDiff.get().apply();
        synchronized (synchronizedCache) {
            if (synchronizedCache.isEmpty()) {
                if (mapFile.exists()) {
                    mapFile.delete();
                }
                return keyChangedCount;
            }
            if (!mapFile.getParentFile().exists()) {
                if (!mapFile.getParentFile().mkdirs()) {
                    throw new IOException("Cannot create  " + mapFile.getParent());
                }
            }
            if (!mapFile.exists()) {

                mapFile.createNewFile();
            }
            try (FileOutputStream fos = new FileOutputStream(mapFile, false)) {
                ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
                for (Map.Entry<String, String> stringStringEntry : synchronizedCache.entrySet()) {
                    String value = stringStringEntry.getValue();
                    byte[] keyEncoded = stringStringEntry.getKey().getBytes(CHARSET_NAME);
                    fos.write(lengthBuffer.putInt(0, keyEncoded.length).array());
                    fos.write(keyEncoded);
                    byte[] valueEncoded = value.getBytes(CHARSET_NAME);
                    fos.write(lengthBuffer.putInt(0, valueEncoded.length).array());
                    fos.write(valueEncoded);
                }
            }
            return keyChangedCount;
        }
    }

    public int unsavedChangesCount() {
        return localDiff.get().diffSize();
    }

    public int rollback() throws IOException {
        int keyChangedCount = localDiff.get().diffSize();
        localDiff.get().clear();
        return keyChangedCount;
    }

    @Override
    public void close() throws Exception {
        rollback();
    }

    private void reload() throws IOException {
        synchronized (synchronizedCache) {
            synchronizedCache.clear();
            if (!mapFile.exists()) {
                return;
            }
            try (FileInputStream fis = new FileInputStream(mapFile);
                 FileChannel channel = fis.getChannel()) {
                ByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());

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

                    synchronizedCache.put(new String(key, CHARSET_NAME), new String(value, CHARSET_NAME));
                }
            } catch (BufferUnderflowException | NegativeArraySizeException e) {
                throw new FileCorruptedException(String.format("%s is corrupted", mapFile.getName()));
            }
        }
    }

    private static final class Transaction {
        private final Map<String, String> base;
        private final Map<String, String> changed = new HashMap<>();
        private final Set<String> deleted = new HashSet<>();

        public Transaction(Map<String, String> base) {
            this.base = base;
        }

        public void apply() {
            synchronized (base) {
                for (Map.Entry<String, String> entry : changed.entrySet()) {
                    base.put(entry.getKey(), entry.getValue());
                }
                for (String key : deleted) {
                    base.remove(key);
                }
            }
            changed.clear();
            deleted.clear();
        }

        public void clear() {
            changed.clear();
            deleted.clear();
        }

        public String put(String key, String value) {
            String returnValue = get(key);

            if (deleted.contains(key)) {
                deleted.remove(key);
            }
            changed.put(key, value);

            return returnValue;
        }

        public String remove(String key) {
            String returnValue = get(key);

            changed.remove(key);
            if (!deleted.contains(key)) {
                deleted.add(key);
            }
            return returnValue;
        }

        public int diffSize() {
            return changed.size() + deleted.size();
        }

        public int size() {
            return keySet().size();
        }

        public Set<String> keySet() {
            Set<String> baseSet;
            synchronized (base) {
                baseSet = new HashSet<>(base.keySet());
            }
            for (String key : changed.keySet()) {
                baseSet.add(key);
            }
            for (String key : deleted) {
                baseSet.remove(key);
            }
            return baseSet;
        }

        public String get(String key) {
            if (deleted.contains(key)) {
                return null;
            }
            if (changed.containsKey(key)) {
                return changed.get(key);
            }
            return base.get(key);
        }
    }
}
