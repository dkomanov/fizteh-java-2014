package ru.fizteh.fivt.students.kinanAlsarmini.filemap;

import java.io.RandomAccessFile;
import java.io.File;
import java.io.IOException;

class TableReader {
    private RandomAccessFile inputStream;

    public TableReader(File databasePath) {
        try {
            inputStream = new RandomAccessFile(databasePath, "r");
        } catch (IOException e) {
            throw new IllegalArgumentException("TableReader: file can't be opened for reading.");
        }
    }

    public void readTable(Table table) throws IOException {
        long bytesLeft = inputStream.length();
        while (bytesLeft > 0) {
            int keyLength = inputStream.readInt();

            bytesLeft -= 4;

            if (keyLength < 0 ||  keyLength > bytesLeft) {
                throw new IllegalArgumentException("TableReader: bad file format: invalid length for key / value.");
            }

            byte[] bKey = new byte[keyLength];
            int tRead = inputStream.read(bKey);
            if (tRead < 0 || tRead != keyLength) {
                throw new IllegalArgumentException("TableReader: bad file format.");
            }
            String key = new String(bKey, "UTF-8");
            if (table.exists(key)) {
                throw new IllegalArgumentException("TableReader: file contains duplicate key.");
            }

            bytesLeft -= keyLength;

            int valueLength = inputStream.readInt();

            bytesLeft -= 4;

            if (valueLength < 0 ||  valueLength > bytesLeft) {
                throw new IllegalArgumentException("TableReader: bad file format: invalid length for key / value.");
            }

            byte[] bValue = new byte[valueLength];
            tRead = inputStream.read(bValue);
            if (tRead < 0 || tRead != valueLength) {
                throw new IllegalArgumentException("TableReader: bad file format.");
            }

            String value = new String(bValue, "UTF-8");
            table.put(key, value);

            bytesLeft -= valueLength;
        }
    }

    public void close() throws IOException {
        inputStream.close();
    }
}
