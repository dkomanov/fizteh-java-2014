/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.filemap.table;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author shakarim
 */
public class TableWriter implements Closeable {

    private RandomAccessFile dbFile;

    public TableWriter(String fileName) throws FileNotFoundException, IOException {
        dbFile = new RandomAccessFile(fileName, "rw");
    }

    @Override
    public void close() {
        try {
            dbFile.close();
        } catch (Exception e) {
            //nothing
        }
    }

    private long write(String word) throws IOException {
        byte[] bytes = word.getBytes();
        long retVal = 0;
        try {
            dbFile.writeInt(bytes.length);
            dbFile.write(bytes);
            retVal =  4 + bytes.length;
        } catch (IOException ex) {
            throw new IOException("cannot write to File");
        }
        return retVal;
    }

    public static void saveTable(String fileName, HashMap<String, String> table)
            throws FileNotFoundException, IOException {
        try (TableWriter writer = new TableWriter(fileName)) {
            Set<Entry<String, String>> list = table.entrySet();
            long fileLen = 0;
            for (Entry<String, String> entry : list) {
                fileLen += writer.write(entry.getKey());
                fileLen += writer.write(entry.getValue());
            }
            writer.dbFile.setLength(fileLen);
        }
    }
}
