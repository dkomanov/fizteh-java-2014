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

    public TableWriter(String fileName) {
        try {
            dbFile = new RandomAccessFile(fileName, "rw");
        } catch (FileNotFoundException e) {
            dbFile = null;
        }
    }

    @Override
    public void close() {
        try {
            dbFile.close();
        } catch (Exception e) {
            //nothing
        }
    }

    public long write(String word) throws IOException {
        byte[] bytes = word.getBytes();
        long retVal = 0;
        try {
            dbFile.writeInt(bytes.length);
            dbFile.write(bytes);
            retVal = 4 + bytes.length;
        } catch (Exception ex) {
            throw new IOException("cannot write to File");
        }
        return retVal;
    }

    public void setLength(long length) {
        try {
            dbFile.setLength(length);
        } catch (Exception ex) {
            //nothing
        }
    }
}
