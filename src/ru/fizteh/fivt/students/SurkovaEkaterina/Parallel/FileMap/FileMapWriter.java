package ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.FileMap;

import ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.TableSystem.DatabaseTableCreator;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Set;

public class FileMapWriter implements Closeable {
        private RandomAccessFile file;

    public FileMapWriter(final String filePath) throws IOException {
        try {
            file = new RandomAccessFile(filePath, "rw");
        } catch (FileNotFoundException e) {
            throw new IOException(String.format(
                    this.getClass().toString() + ": Can not create file!: '%s'", filePath));
        }
        file.setLength(0);
    }

    public static void saveToFile(String filePath, Set<String> keys, DatabaseTableCreator creator)
            throws IOException {
        FileMapWriter writer = new FileMapWriter(filePath);
        int offset = FileMapUsage.getKeysLength(keys, ThreadSafeAbstractTable.CHARSET);

        for (final String key : keys) {
            writer.writeKey(key);
            writer.writeOffset(offset);
            offset += FileMapUsage.getBytesNumber(creator.get(key), ThreadSafeAbstractTable.CHARSET);
        }
        for (final String key : keys) {
            writer.writeValue(creator.get(key));
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
        byte[] bytes = key.getBytes(ThreadSafeAbstractTable.CHARSET);
        file.write(bytes);
        file.writeByte(0);
    }

    private void writeOffset(final int offset) throws IOException {
        file.writeInt(offset);
    }

    private void writeValue(final String value) throws IOException {
        byte[] bytes = value.getBytes(ThreadSafeAbstractTable.CHARSET);
        file.write(bytes);
    }
}
