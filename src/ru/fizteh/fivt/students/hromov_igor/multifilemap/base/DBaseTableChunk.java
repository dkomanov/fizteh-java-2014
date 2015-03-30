package ru.fizteh.fivt.students.hromov_igor.multifilemap.base;

import ru.fizteh.fivt.students.hromov_igor.multifilemap.base.exception.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class DBaseTableChunk {

    private static final String ENCODING = "UTF-8";
    private Path dBasePath;
    private Map<String, String> dBase;

    public DBaseTableChunk(String name) throws IOException {
        dBasePath = Paths.get(name);
        dBase = new HashMap<>();
        File file = new File(name);
        if (file.isDirectory()) {
            throw new FileCreateException();
        }
        if (file.exists()) {
            RandomAccessFile dbFile = new RandomAccessFile(dBasePath.toString(), "r");
            if (dbFile.length() > 0) {
                while (dbFile.getFilePointer() < dbFile.length()) {
                    readDbFromFile(dbFile);
                }
            }
            dbFile.close();
        } else {
            try {
                Files.createFile(file.toPath());
            } catch (IOException e) {
                throw new FileCreateException();
            }
        }
    }


    public void put(String key, String value) {
        if (dBase.containsKey(key)) {
            dBase.remove(key);
        }
        dBase.put(key, value);
    }


    public void remove(String key) {
        if (dBase.containsKey(key)) {
            dBase.remove(key);
        }
    }

    private void writeDbToFile(final RandomAccessFile dbFile) throws IOException {
        dbFile.setLength(0);
        Set<String> keys = dBase.keySet();
        List<Integer> offsetsPos = new LinkedList<Integer>();
        for (String currentKey : keys) {
            dbFile.write(currentKey.getBytes(ENCODING));
            dbFile.write('\0');
            offsetsPos.add((int) dbFile.getFilePointer());
            dbFile.writeInt(0);
        }
        List<Integer> offsets = new LinkedList<Integer>();
        for (String currentKey : keys) {
            offsets.add((int) dbFile.getFilePointer());
            dbFile.write(dBase.get(currentKey).getBytes(ENCODING));
        }
        Iterator<Integer> offIter = offsets.iterator();
        for (int offsetPos : offsetsPos) {
            dbFile.seek(offsetPos);
            dbFile.writeInt(offIter.next());
        }
    }

    public void readDbFromFile(final RandomAccessFile dbFile) throws IOException {
        ByteArrayOutputStream bytesBuffer = new ByteArrayOutputStream();
        List<Integer> offsets = new LinkedList<Integer>();
        List<String> keys = new LinkedList<String>();
        byte b;
        int bytesCounter = 0;
        int firstOffset = -1;
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
            keys.add(bytesBuffer.toString(ENCODING));
            bytesBuffer.reset();
        } while (bytesCounter < firstOffset);
        offsets.add((int) dbFile.length());
        Iterator<String> keyIter = keys.iterator();
        for (int nextOffset : offsets) {
            while (bytesCounter < nextOffset) {
                bytesBuffer.write(dbFile.readByte());
                bytesCounter++;
            }
            if (bytesBuffer.size() > 0) {
                put(keyIter.next(), bytesBuffer.toString(ENCODING));
                bytesBuffer.reset();
            } else {
                throw new IOException();
            }
        }
        bytesBuffer.close();
    }

    public void save() throws IOException {
        try (RandomAccessFile dbFile = new RandomAccessFile(dBasePath.toString(), "rw")) {
            writeDbToFile(dbFile);
        } catch (FileNotFoundException e) {
            throw new WriteToFileException();
        }
    }

}


