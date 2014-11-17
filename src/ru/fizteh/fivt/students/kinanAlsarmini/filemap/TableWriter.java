package ru.fizteh.fivt.students.kinanAlsarmini.filemap;

import java.io.RandomAccessFile;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Set;

class TableWriter {
    private RandomAccessFile outputStream;

    public TableWriter(File databasePath) {
        try {
            outputStream = new RandomAccessFile(databasePath, "rw");
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("TableWriter: received non-existent file.");
        }
    }

    public void writeTable(Table table) throws IOException {
        outputStream.setLength(0);

        Set<Map.Entry<String, String>> rows = table.listRows();

        for (Map.Entry<String, String> row : rows) {
            outputStream.writeInt(row.getKey().getBytes("UTF-8").length);
            outputStream.write(row.getKey().getBytes("UTF-8"));
            outputStream.writeInt(row.getValue().getBytes("UTF-8").length);
            outputStream.write(row.getValue().getBytes("UTF-8"));
        }
    }

    public void close() throws IOException {
        outputStream.close();
    }
}
