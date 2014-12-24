package ru.fizteh.fivt.students.kinanAlsarmini.filemap.base;

import java.io.*;

public class FilemapReader implements Closeable {
    private RandomAccessFile file;
    private int valuesOffset = -1;

    public FilemapReader(String filePath) throws IOException {
        try {
            file = new RandomAccessFile(filePath, "r");
        } catch (FileNotFoundException e) {
            file = null;
            valuesOffset = 0;

            return;
        }

        if (file.length() == 0) {
            throw new IllegalArgumentException("empty file: " + filePath);
        }

        // initializing beginning of value section
        skipKey();
        valuesOffset = readOffset();
        file.seek(0);
    }

    public static void loadFromFile(String filePath, TableBuilder builder) throws IOException {
        if (!FileMapUtils.checkFileExists(filePath)) {
            return;
        }

        FilemapReader reader = new FilemapReader(filePath);

        while (!reader.endOfFile()) {
            String key = reader.readKey();
            String value = reader.readValue();
            builder.put(key, value);
        }

        reader.close();
    }

    public void close() throws IOException {
        file.close();
    }

    private String readKey() throws IOException {
        if (file.getFilePointer() >= valuesOffset) {
            return null;
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte b = file.readByte();

        while (b != 0) {
            stream.write(b);
            b = file.readByte();
        }

        return new String(stream.toByteArray(), AbstractStorage.CHARSET);
    }

    private String readValue() throws IOException {
        int offset = readOffset();
        int nextOffset = readNextOffset();
        long currentOffset = file.getFilePointer();

        file.seek(offset);

        int valueLength = nextOffset - offset;
        byte[] bytes = new byte[valueLength];

        file.read(bytes, 0, valueLength);
        file.seek(currentOffset);

        return new String(bytes, AbstractStorage.CHARSET);
    }

    private boolean endOfFile() {
        if (file == null) {
            return true;
        }

        boolean result = true;

        try {
            result = (file.getFilePointer() == valuesOffset);
        } catch (IOException e) {
            // SAD
        }

        return result;
    }

    private int readNextOffset() throws IOException {
        long currentOffset = file.getFilePointer();
        int nextOffset;

        if (readKey() == null) {
            nextOffset = (int) file.length();
        } else {
            nextOffset = readOffset();
        }

        file.seek(currentOffset);

        return nextOffset;
    }

    private void skipKey() throws IOException {
        byte b;

        do {
            b = file.readByte();
        } while (b != 0);
    }

    private int readOffset() throws IOException {
        return file.readInt();
    }
}
