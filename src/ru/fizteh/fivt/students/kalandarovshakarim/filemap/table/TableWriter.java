/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.filemap.table;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author shakarim
 */
public class TableWriter implements Closeable {

    private RandomAccessFile dbFile;

    public TableWriter(String fileName) throws FileNotFoundException {
        dbFile = new RandomAccessFile(fileName, "rw");
    }

    @Override
    public void close() throws IOException {
        dbFile.close();
    }

    public long write(String word) throws IOException {
        byte[] bytes = word.getBytes();
        long retVal = 4 + bytes.length;
        dbFile.writeInt(bytes.length);
        dbFile.write(bytes);
        return retVal;
    }

    public void setLength(long length) throws IOException {
        dbFile.setLength(length);
    }
}
