/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.filemap.table;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

/**
 *
 * @author shakarim
 */
public class TableReader implements Closeable {

    private RandomAccessFile dbFile;

    public TableReader(String fileName) throws IOException {
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

    private String read() throws IOException {
        try {
            int length = dbFile.readInt();
            byte[] word = new byte[length];
            dbFile.read(word, 0, length);
            return new String(word);
        } catch (IOException ex) {
            throw new IOException("cannot read from file");
        }
    }

    public static void loadTable(String fileName, HashMap<String, String> table)
            throws FileNotFoundException, IOException {

        try (TableReader reader = new TableReader(fileName)) {
            String key;
            String value;

            while (!reader.eof()) {
                key = reader.read();
                value = reader.read();
                table.put(key, value);
                //System.out.println(key + " " + value);
            }
        }
    }

    private boolean eof() {
        if (dbFile == null) {
            return true;
        }
        boolean retVal = false;
        try {
            retVal = dbFile.getFilePointer() >= dbFile.length();
        } catch (IOException e) {
            return true;
        }
        return retVal;
    }
}
