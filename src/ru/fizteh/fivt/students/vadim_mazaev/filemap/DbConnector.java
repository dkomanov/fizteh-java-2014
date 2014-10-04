package ru.fizteh.fivt.students.vadim_mazaev.filemap;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map;

public final class DbConnector implements AutoCloseable {
    public DbConnector() throws ThrowExit {
        dataBase = new TreeMap<>();
        try {
            dbFilePath = Paths.get(System.getProperty("db.file"));
            try (RandomAccessFile dbFile
                    = new RandomAccessFile(dbFilePath.toString(), "r")) {
                if (dbFile.length() > 0) {
                    readDbFromFile(dbFile);
                }
            } catch (FileNotFoundException e) {
                dbFilePath.toFile().createNewFile();
            }
        } catch (Exception e) {
            System.err.println("Error connecting database");
            throw new ThrowExit(false);
        }
    }

    public void close() {
        //System.err.println("<debug> DbConnector close method was called");
        try (RandomAccessFile dbFile
                    = new RandomAccessFile(dbFilePath.toString(), "rw")) {
            writeDbToFile(dbFile);
        } catch (Exception e) {
            System.err.println("Error writing database to file");
        }
    }

    public Map<String, String> getDataBase() {
        return dataBase;
    }

    private void readDbFromFile(final RandomAccessFile dbFile)
            throws IOException {
        ByteArrayOutputStream bytesBuffer = new ByteArrayOutputStream();
        List<Integer> offsets = new LinkedList<Integer>();
        List<String> keys = new LinkedList<String>();
        byte b;
        int bytesCounter = 0;
        int firstOffset = -1;
        //reading keys and offsets until reaching
        //the byte with first offset number
        do {
            while ((b = dbFile.readByte()) != 0) {
                bytesCounter++;
                bytesBuffer.write(b);
            }
            bytesCounter++;
            if (firstOffset == -1) {
                firstOffset = dbFile.readInt();
            } else {
                offsets.add(dbFile.readInt());
            }
            bytesCounter += 4;
            keys.add(bytesBuffer.toString("UTF-8"));
            bytesBuffer.reset();
        } while (bytesCounter < firstOffset);
        //reading values until reaching the end of file
        offsets.add((int) dbFile.length());
        Iterator<String> keyIter = keys.iterator();
        Iterator<Integer> offIter = offsets.iterator();
        while (offIter.hasNext()) {
            int nextOffset = offIter.next();
            while (bytesCounter < nextOffset) {
                bytesBuffer.write(dbFile.readByte());
                bytesCounter++;
            }
            if (bytesBuffer.size() > 0) {
                dataBase.put(keyIter.next(), bytesBuffer.toString("UTF-8"));
                bytesBuffer.reset();
            } else {
                //if file ends before reading last value
                throw new IOException();
            }
        }
        bytesBuffer.close();
    }

    private void writeDbToFile(final RandomAccessFile dbFile)
            throws IOException {
        dbFile.setLength(0);
        Set<String> keys = dataBase.keySet();
        List<Integer> offsetsPos = new LinkedList<Integer>();
        for (String currentKey : keys) {
            dbFile.write(currentKey.getBytes("UTF-8"));
            dbFile.write('\0');
            offsetsPos.add((int) dbFile.getFilePointer());
            dbFile.writeInt(0);
        }
        List<Integer> offsets = new LinkedList<Integer>();
        for (String currentKey : keys) {
            offsets.add((int) dbFile.getFilePointer());
            dbFile.write(dataBase.get(currentKey).getBytes("UTF-8"));
        }
        Iterator<Integer> offIter = offsets.iterator();
        for (int offsetPos : offsetsPos) {
            dbFile.seek(offsetPos);
            dbFile.writeInt(offIter.next());
        }
     }

    private Path dbFilePath;
    private Map<String, String> dataBase;
}
