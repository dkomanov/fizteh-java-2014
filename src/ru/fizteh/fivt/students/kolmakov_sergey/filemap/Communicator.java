package ru.fizteh.fivt.students.kolmakov_sergey.filemap;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.*;

public class Communicator {
    protected static void getData(RandomAccessFile dataBaseFile, TreeMap<String, String> fileMap)
            throws IOException {
        List<String> keys = new LinkedList<>();
        List<Integer> offsets = new LinkedList<>();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        byte b;
        int counter = 0;
        int firstOffset = 0;
        do { //reading keys
            while ((b = dataBaseFile.readByte()) > 0) { //read key
                counter++;
                bytes.write(b);
            }
            if (firstOffset == 0) { //read offset
                firstOffset = dataBaseFile.readInt();
            } else {
                offsets.add(dataBaseFile.readInt());
            }
            counter += 5; // one for '\0' and one for int
            keys.add(bytes.toString("UTF-8"));
            bytes.reset();
        } while (counter < firstOffset);
        offsets.add((int) dataBaseFile.length());
        Iterator<String> keyIterator = keys.iterator();
        for (Integer currentOffset : offsets) { //reading values
            while (currentOffset > counter) {
                bytes.write(dataBaseFile.readByte());
                counter++;
            }
            if (bytes.size() > 0) {
                fileMap.put(keyIterator.next(), bytes.toString("UTF-8"));
                bytes.reset();
            } else {
                throw new IOException();
            }
        }
        bytes.close();
    }

    protected static void putData(RandomAccessFile dataBaseFile, TreeMap<String, String> fileMap)
            throws IOException {
        dataBaseFile.setLength(0);
        Set<String> keys = fileMap.keySet();
        List<Integer> reserved = new LinkedList<>();
        List<Integer> offsets = new LinkedList<>();
        for (String currentKey : keys) {
            dataBaseFile.write(currentKey.getBytes("UTF-8"));
            dataBaseFile.write('\0');
            reserved.add((int) dataBaseFile.getFilePointer());
            dataBaseFile.writeInt(0); //place reserved for offset
        }
        for (String currentKey : keys) { //write values
            offsets.add((int) dataBaseFile.getFilePointer());
            dataBaseFile.write(fileMap.get(currentKey).getBytes("UTF-8"));
        }
        Iterator<Integer> offsetIterator = offsets.iterator();
        for (int offsetPos : reserved) { //write offsets in reserved place
            dataBaseFile.seek(offsetPos);
            dataBaseFile.writeInt(offsetIterator.next());
        }
    }
}
