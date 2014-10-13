/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.filemap.table;

import java.io.Closeable;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author shakarim
 */
public class TableReader implements Closeable {

    private RandomAccessFile dbFile;
    private String fileName;

    public TableReader(String fileName) throws FileNotFoundException {
        dbFile = new RandomAccessFile(fileName, "rw");
        this.fileName = fileName;
    }

    @Override
    public void close() throws IOException {
        dbFile.close();
    }

    public String read() throws IOException {
        byte[] word = null;
        try {
            int length = dbFile.readInt();
            word = new byte[length];
            dbFile.read(word, 0, length);
        } catch (EOFException | OutOfMemoryError e) {
            throw new IOException(fileName + ": Invalid file format");
        }
        return new String(word, "UTF-8");
    }

    public boolean eof() throws IOException {
        return dbFile.getFilePointer() >= dbFile.length();
    }
}
