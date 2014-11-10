/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.filemap.table;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author shakarim
 */
public class TableWriter implements Closeable {

    private final RandomAccessFile dbFile;

    public TableWriter(String fileName) throws IOException {
        dbFile = new RandomAccessFile(fileName, "rw");
    }

    @Override
    public void close() throws IOException {
        dbFile.close();
    }

    public long write(String word) throws IOException {
        byte[] bytes = word.getBytes("UTF-8");
        dbFile.writeInt(bytes.length);
        dbFile.write(bytes);
        return 4 + bytes.length;
    }

    public void setLength(long length) throws IOException {
        dbFile.setLength(length);
    }
}
