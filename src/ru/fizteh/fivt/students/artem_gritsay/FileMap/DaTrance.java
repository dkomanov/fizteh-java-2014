package ru.fizteh.fivt.students.artem_gritsay.FileMap;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class DaTrance {
    public static void putD(RandomAccessFile dataFile, HashMap<String, String> fileMap)
            throws IOException {
        List<Integer> reserveplace = new LinkedList<>();
        dataFile.setLength(0);
        Set<String> key = fileMap.keySet();
        List<Integer> offset = new LinkedList<>();

        for (String i : key) {
            dataFile.write(i.getBytes("UTF-8"));
            dataFile.write('\0');
            reserveplace.add((int) dataFile.getFilePointer());
            dataFile.writeInt(0);
        }

        for (String i : key) {
            offset.add((int) dataFile.getFilePointer());
            dataFile.write(fileMap.get(i).getBytes("UTF-8"));
        }

        Iterator<Integer> k = offset.iterator();

        for (int i : reserveplace) {
            dataFile.seek(i);
            dataFile.writeInt(k.next());
        }
    }

    public static void getNewData(RandomAccessFile dataFile, HashMap<String, String> filemap)
            throws IOException {
        byte b;
        int bytenumber = 0;
        int first = 1;
        List<String> key = new LinkedList<>();
        List<Integer> offset = new LinkedList<>();
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();

        while (bytenumber < first) {
            while ((b = dataFile.readByte()) > 0) {
                bytestream.write(b);
                bytenumber++;
            }
            key.add(bytestream.toString("UTF-8"));
            bytestream.reset();
            if (first == 1) {
                first = dataFile.readInt();
            } else {
                offset.add(dataFile.readInt());
            }
            bytenumber += 5;
        }

        offset.add((int) dataFile.length());
        Iterator<String> keyIterator = key.iterator();

        for (Integer i : offset) {
            while (i > bytenumber) {
                bytestream.write(dataFile.readByte());
                bytenumber++;
            }
            if (bytestream.size() > 0) {
                filemap.put(keyIterator.next(), bytestream.toString("UTF-8"));
                bytestream.reset();
            } else {
                throw new IOException();
            }
        }
        bytestream.close();
    }
}
