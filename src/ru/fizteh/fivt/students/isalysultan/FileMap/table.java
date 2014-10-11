package ru.fizteh.fivt.students.IsalySultan.FileMap;

import java.lang.reflect.Array;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class table {

    private Path filePath;

    TreeMap<String, String> storage;

    public table() throws IOException {
        storage = new TreeMap<String, String>();
        filePath = Paths.get(System.getProperty("db.file"));
        try (RandomAccessFile file = new RandomAccessFile(filePath.toString(),
                "r")) {
            if (file.length() > 0) {
                readFile(file);
                file.close();
                return;
            }
        } catch (FileNotFoundException e) {
            filePath.toFile().createNewFile();
        }
    }

    public void readFile(RandomAccessFile file) {
        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        ArrayList<String> key = new ArrayList<String>();
        ArrayList<Integer> offset = new ArrayList<Integer>();
        int count = 0;
        int firstoffset = -1;
        byte part;
        boolean dontReadFirstOffset = false;
        while (count < firstoffset || !dontReadFirstOffset) {
            try {
                while ((part = file.readByte()) != 0) {
                    ++count;
                    buff.write(part);
                }
                ++count;
                if (firstoffset == -1) {
                    firstoffset = file.readInt();
                    offset.add(firstoffset);
                    dontReadFirstOffset = true;
                } else {
                    offset.add(file.readInt());
                }
                count += 4;
                key.add(buff.toString("UTF-8"));
                buff.reset();
            } catch (IOException e) {
                System.err.println("Error read file");
                return;
            }
        }
        Iterator<String> itKey = key.iterator();
        Iterator<Integer> itOffset = offset.iterator();
        Iterator<Integer> itForEndOffset = offset.iterator();
        try {
            int size = (int) file.length();
            count = itForEndOffset.next();
            int afterCount = count;
            boolean forEnd = true;
            while (itOffset.hasNext() && forEnd) {
                if (size < count) {
                    System.err.println("error with offset");
                }
                boolean endFile = false;
                if (!itForEndOffset.hasNext()) {
                    while (count < size) {
                        forEnd = false;
                        buff.write(file.readByte());
                        ++count;
                    }
                    endFile = true;
                }
                if (!endFile) {
                    afterCount = itOffset.next();
                    while (count < afterCount) {
                        buff.write(file.readByte());
                        ++count;
                    }
                }
                storage.put(itKey.next(), buff.toString("UTF-8"));
                buff.reset();
                count = afterCount;
            }
        } catch (IOException e) {
            System.err.println("error in file");
        }
        try {
            buff.close();
        } catch (IOException e) {
            System.err.println("error ByteArrayOutputStream");
        }
    }

    public void writeFile() {
        try {
            RandomAccessFile endFile = new RandomAccessFile(
                    filePath.toString(), "rwd");
            try {
                endFile.setLength(0);
                Set<String> keys = storage.keySet();
                Iterator<String> itKey = keys.iterator();
                ArrayList<Integer> offSetsForKey = new ArrayList<Integer>();
                while (itKey.hasNext()) {
                    endFile.write((itKey.next()).getBytes("UTF-8"));
                    endFile.write('\0');
                    offSetsForKey.add((int) endFile.getFilePointer());
                    endFile.writeInt(0);
                }
                itKey = keys.iterator();
                ArrayList<Integer> offSetsForValue = new ArrayList<Integer>();
                while (itKey.hasNext()) {
                    offSetsForValue.add((int) endFile.getFilePointer());
                    endFile.write(storage.get(itKey.next()).getBytes("UTF-8"));
                }
                Iterator<Integer> itOffSetKey = offSetsForKey.iterator();
                Iterator<Integer> itOffSetValue = offSetsForValue.iterator();
                while (itOffSetKey.hasNext()) {
                    endFile.seek(itOffSetKey.next());
                    endFile.writeInt(itOffSetValue.next());
                }
                endFile.close();
            } catch (IOException e) {
                System.out.println("can't open file for write");
            }
        } catch (FileNotFoundException e) {
            System.out.println("error open write");
        }
    }
}
