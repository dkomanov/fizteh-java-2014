package ru.fizteh.fivt.students.SurkovaEkaterina.FileMap;

import java.io.*;
import java.util.HashMap;

public class FileMapReader implements Closeable {
    private RandomAccessFile file;
    private int valuesOffset = -1;

    public FileMapReader(final String filePath) throws IOException {
        try {
            file = new RandomAccessFile(filePath, "r");
        } catch (FileNotFoundException e) {
            file = null;
            valuesOffset = 0;
            return;
        }
        skipKey();
        valuesOffset = readOffset();
        file.seek(0);
    }

    public static void loadFromFile(final String filePath,
                                    final HashMap<String, String> data)
            throws IOException {
        if (!FileMapUsage.checkFileExists(filePath)) {
            return;
        }
        FileMapReader reader = new FileMapReader(filePath);
        while (!reader.endOfFile()) {
            String key = reader.readKey();
            String value = reader.readValue();
            data.put(key, value);
        }
        reader.close();
    }

    public final void close() throws IOException {
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
        return new String(stream.toByteArray(), ATable.CHARSET);
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
        return new String(bytes, ATable.CHARSET);
    }

    private boolean endOfFile() {
        if (file == null) {
            return true;
        }

        boolean result = true;
        try {
            result = (file.getFilePointer() == valuesOffset);
        } catch (IOException e) {
            System.out.println(e.getMessage());
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
