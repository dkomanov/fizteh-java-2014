package ru.fizteh.fivt.students.SurkovaEkaterina.FileMap;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Set;

public class FileMapWriter implements Closeable {

    private RandomAccessFile file;

    public FileMapWriter(final String filePath) throws IOException {
        try {
            file = new RandomAccessFile(filePath, "rw");
        } catch (FileNotFoundException e) {
            throw new IOException(String.format(
                    "Can not create file!: '%s'", filePath));
        }
        file.setLength(0);
    }

    public static void saveToFile(final String filePath, final Set<String> keys,
                                  final HashMap<String, String> data)
            throws IOException {
        FileMapWriter writer = new FileMapWriter(filePath);
        int offset = FileMapUsage.getKeysLength(keys, ATable.CHARSET);

        for (final String key : keys) {
            writer.writeKey(key);
            writer.writeOffset(offset);
            offset += FileMapUsage.getByteCount(data.get(key), ATable.CHARSET);
        }
        for (final String key : keys) {
            writer.writeValue(data.get(key));
        }
        try {
            writer.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public final void close() throws IOException {
        file.close();
    }

    private void writeKey(final String key) throws IOException {
        byte[] bytes = key.getBytes(ATable.CHARSET);
        file.write(bytes);
        file.writeByte(0);
    }

    private void writeOffset(final int offset) throws IOException {
        file.writeInt(offset);
    }

    private void writeValue(final String value) throws IOException {
        byte[] bytes = value.getBytes(ATable.CHARSET);
        file.write(bytes);
    }
}
