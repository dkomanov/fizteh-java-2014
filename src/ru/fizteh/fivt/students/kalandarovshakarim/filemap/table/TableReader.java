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

    public TableReader(String fileName) {
        try {
            dbFile = new RandomAccessFile(fileName, "r");
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

    public String read() throws IOException {
        try {
            int length = dbFile.readInt();
            byte[] word = new byte[length];
            dbFile.read(word, 0, length);
            return new String(word);
        } catch (Exception ex) {
            throw new IOException("cannot read from file");
        }
    }

    public boolean eof() {
        if (dbFile == null) {
            return true;
        }
        boolean retVal = false;
        try {
            retVal = dbFile.getFilePointer() >= dbFile.length();
        } catch (Exception e) {
            return true;
        }
        return retVal;
    }
}
