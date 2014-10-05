package ru.fizteh.fivt.students.standy66.filemap;

import com.sun.istack.internal.NotNull;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Simple database implementation. <br>
 * Working mechanism is specified at {@link ru.fizteh.fivt.students.standy66.filemap} <br>
 * Created by astepanov on 26.09.14.
 */
public class Database {

    private String dbFilePath;
    private Map<String, String> cache;

    public Database(@NotNull final String databaseFilePath) {
        dbFilePath = databaseFilePath;
        cache = new HashMap<>();
        reload();
    }

    private void reload() {
        FileChannel channel = null;
        try {
            channel = new FileInputStream(dbFilePath).getChannel();
            ByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            try {
                while (buffer.remaining() > 0) {
                    int keySize = buffer.getInt();
                    byte[] key = new byte[keySize];
                    buffer.get(key);
                    int valueSize = buffer.getInt();
                    byte[] value = new byte[valueSize];
                    buffer.get(value);
                    cache.put(new String(key, "UTF-8"), new String(value, "UTF-8"));
                }
            } catch (BufferUnderflowException e) {
                System.err.println("Corrupted file");
                System.exit(1);
            }
            channel.close();
        } catch (IOException e) {
            System.err.println("Error reading file");
            System.exit(2);
        }
    }

    public Set<String> list() {
        return cache.keySet();
    }

    public String get(@NotNull final String key) {
        return cache.get(key);
    }

    public void put(@NotNull final String key, @NotNull final String value) {
        cache.put(key, value);
    }

    public boolean remove(@NotNull final String key) {
        if (cache.containsKey(key)) {
            cache.remove(key);
            return true;
        } else {
            return false;
        }
    }

    public void flush() {
        try {
            FileOutputStream fos = new FileOutputStream(dbFilePath, false);
            ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
            for (String key : cache.keySet()) {
                String value = cache.get(key);
                byte[] keyEncoded = key.getBytes("UTF-8");
                fos.write(lengthBuffer.putInt(0, keyEncoded.length).array());
                fos.write(keyEncoded);
                byte[] valueEncoded = value.getBytes("UTF-8");
                fos.write(lengthBuffer.putInt(0, valueEncoded.length).array());
                fos.write(valueEncoded);
            }
            fos.close();
        } catch (IOException e) {
            System.err.println("Error writing file");
            System.exit(4);
        }
    }
}
