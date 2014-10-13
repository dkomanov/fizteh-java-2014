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
public class TableReader implements Closeable {

    private RandomAccessFile dbFile;

    public TableReader(String fileName) throws FileNotFoundException {
        dbFile = new RandomAccessFile(fileName, "rw");
    }

    @Override
    public void close() throws IOException {
        dbFile.close();
    }

    public String read() throws IOException {
        int length = dbFile.readInt();
        byte[] word = new byte[length];
        dbFile.read(word, 0, length);
        return new String(word);
    }

    public boolean eof() throws IOException {
        return dbFile.getFilePointer() >= dbFile.length();
    }
}
