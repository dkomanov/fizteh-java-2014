package ru.fizteh.fivt.students.ilivanov.JUnit;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FileUsing {
    private File file;
    private HashMap<String, String> map;
    final private int maxLength;

    public FileUsing(final File file) {
        if (file == null) {
            throw new IllegalArgumentException("null location");
        }
        this.file = file;
        map = new HashMap<>();
        maxLength = 1 << 24;
    }

    private int readBytes(final DataInputStream input, final int bytes, final byte[] buffer) throws IOException {
        int len = 0;
        while (len != bytes) {
            int k = input.read(buffer, len, bytes - len);
            if (k == -1) {
                return len;
            }
            len += k;
        }
        return len;
    }

    public void loadFromDisk() {
        map.clear();
        if (!file.getParentFile().exists() || !file.getParentFile().isDirectory()) {
            throw new RuntimeException("unable to create a file, directory doesn't exist");
        }
        if (!file.exists()) {
            return;
        }
        if (file.exists() && !file.isFile()) {
            throw new RuntimeException(String.format("%s is not a file", file.getName()));
        }
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream(file))) {
            byte[] buffer;
            ByteBuffer cast;
            while (true) {
                buffer = new byte[4];
                int bytesRead = readBytes(inputStream, 4, buffer);
                if (bytesRead == 0) {
                    break;
                }
                if (bytesRead != 4) {
                    throw new IOException("database loading failed: wrong key length format");
                }
                cast = ByteBuffer.wrap(buffer);
                int keyLength = cast.getInt();
                if (keyLength > maxLength) {
                    throw new IOException("database loading failed: field length too big");
                }
                if (keyLength <= 0) {
                    throw new IOException("database loading failed: field length must be positive");
                }

                buffer = new byte[keyLength];
                bytesRead = readBytes(inputStream, keyLength, buffer);
                if (bytesRead != keyLength) {
                    throw new IOException("database loading failed: wrong key length");
                }
                String key = new String(buffer, StandardCharsets.UTF_8);

                buffer = new byte[4];
                bytesRead = readBytes(inputStream, 4, buffer);
                if (bytesRead != 4) {
                    throw new IOException("database loading failed: wrong value length format");
                }
                cast = ByteBuffer.wrap(buffer);
                int valueLength = cast.getInt();
                if (valueLength > maxLength) {
                    throw new IOException("database loading failed: field length too big");
                }
                if (valueLength <= 0) {
                    throw new IOException("database loading failed: field length must be positive");
                }

                buffer = new byte[valueLength];
                bytesRead = readBytes(inputStream, valueLength, buffer);
                if (bytesRead != valueLength) {
                    throw new IOException("database loading failed: wrong value length");
                }
                String value = new String(buffer, StandardCharsets.UTF_8);
                map.put(key, value);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeToDisk() {
        if (file.exists() && file.isDirectory()) {
            throw new RuntimeException("database can't be written to the specified location");
        }
        try {
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    throw new RuntimeException("database can't be written to the specified location");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("error creating the file", e);
        }
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                byte[] key = entry.getKey().getBytes(StandardCharsets.UTF_8);
                byte[] value = entry.getValue().getBytes(StandardCharsets.UTF_8);
                outputStream.write(ByteBuffer.allocate(4).putInt(key.length).array());
                outputStream.write(key);
                outputStream.write(ByteBuffer.allocate(4).putInt(value.length).array());
                outputStream.write(value);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("file was not found", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String get(final String key) {
        return map.get(key);
    }

    public String put(final String key, final String value) {
        return map.put(key, value);
    }

    public String remove(final String key) {
        return map.remove(key);
    }

    public int size() {
        return map.size();
    }

    public boolean empty() {
        return map.isEmpty();
    }

    public File getFile() {
        return file;
    }

    public void clear() {
        map.clear();
    }

    public String[] getKeysList() {
        String[] result = new String[map.size()];
        int i = 0;
        for (String key : map.keySet()) {
            result[i++] = key;
        }
        return result;
    }

}
