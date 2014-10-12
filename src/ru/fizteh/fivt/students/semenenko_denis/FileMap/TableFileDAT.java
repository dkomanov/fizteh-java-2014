package ru.fizteh.fivt.students.semenenko_denis.FileMap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

/**
 * Created by denny_000 on 08.10.2014.
 */
public class TableFileDAT implements Table, SaveInMemoryInterface {

    private RandomAccessFile binFile;
    private Map<String, String> data = new TreeMap< >();

    @Override
    public String put(String key, String value) {
        String result = data.put(key, value);
        if (result == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(result);
        }
        return result;
    }

    @Override
    public String get(String key) {
        String result = data.get(key);
        if (result == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(result);
        }
        return result;
    }

    @Override
    public boolean remove(String key) {
        String result = data.remove(key);
        if (result == null) {
            System.out.println("not found");
            return false;
        } else {
            System.out.println("removed");
            return true;
        }
    }

    @Override
    public List<String> list() {
        ArrayList<String> result = new ArrayList<>(data.size());
        for (String key : data.keySet()) {
            System.out.print(key + ", ");
        }
        System.out.println();
        return result;
    }

    @Override
    public void write(final RandomAccessFile whereTo) {
        try {
            whereTo.setLength(0);
            Set<String> keys = data.keySet();
            List<Integer> offsetsPos = new LinkedList<Integer>();
            for (String currentKey : keys) {
                whereTo.write(currentKey.getBytes("UTF-8"));
                whereTo.write('\0');
                offsetsPos.add((int) whereTo.getFilePointer());
                whereTo.writeInt(0);
            }
            List<Integer> offsets = new LinkedList<Integer>();
            for (String currentKey : keys) {
                offsets.add((int) whereTo.getFilePointer());
                whereTo.write(data.get(currentKey).getBytes("UTF-8"));
            }
            Iterator<Integer> offIter = offsets.iterator();
            for (int offsetPos : offsetsPos) {
                whereTo.seek(offsetPos);
                whereTo.writeInt(offIter.next());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void read(final RandomAccessFile whereFrom) {
        try {
            ByteArrayOutputStream bytesBuffer = new ByteArrayOutputStream();
            List<Integer> offsets = new LinkedList<Integer>();
            List<String> keys = new LinkedList<String>();
            byte b;
            int bytesCounter = 0;
            int firstOffset = -1;
            do {
                while ((b = whereFrom.readByte()) != 0) {
                    bytesCounter++;
                    bytesBuffer.write(b);
                }
                bytesCounter++;
                if (firstOffset == -1) {
                    firstOffset = whereFrom.readInt();
                } else {
                    offsets.add(whereFrom.readInt());
                }
                bytesCounter += 4;
                keys.add(bytesBuffer.toString("UTF-8"));
                bytesBuffer.reset();
            } while (bytesCounter < firstOffset);
            // Reading values until reaching the end of file.
            offsets.add((int) whereFrom.length());
            Iterator<String> keyIter = keys.iterator();
            Iterator<Integer> offIter = offsets.iterator();
            while (offIter.hasNext()) {
                int nextOffset = offIter.next();
                while (bytesCounter < nextOffset) {
                    bytesBuffer.write(whereFrom.readByte());
                    bytesCounter++;
                }
                if (bytesBuffer.size() > 0) {
                    data.put(keyIter.next(), bytesBuffer.toString("UTF-8"));
                    bytesBuffer.reset();
                } else {
                    // If file ends before reading last value.
                    throw new IOException();
                }
            }
            bytesBuffer.close();
            binFile = whereFrom;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public RandomAccessFile getBinFile() {
        return binFile;
    }

    public void setBinFile(RandomAccessFile dbFile) {
        binFile = dbFile;
    }

    private void create() {
    }
}


