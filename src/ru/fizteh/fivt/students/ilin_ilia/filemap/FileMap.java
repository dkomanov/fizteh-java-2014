package ru.fiztech.fivt.students.theronsg.filemap;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FileMap {
    
    private static Map<String, String> map;
    private static RandomAccessFile file;
    
    FileMap(final String path) throws FileNotFoundException, IOException {
        map = new HashMap<>();
        file = new RandomAccessFile(path, "rw");
        if (file.length() > 0) {
            getFile();
        }
    }
    
    public void put(final String key, final String value) {
        String old = map.put(key, value);
        if (old != null) {
            System.out.println("owerwrite\nold " + old);
        } else {
            System.out.println("new");
        }
    }
    
    public void get(final String key) {
        String value = map.get(key);
        if (key == null) {
            System.out.println("not found");
        } else {
            System.out.println("found\n" + value);
        }
    }
    
    public void remove(final String key) {
        if (map.containsKey(key)) {
            map.remove(key);
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }
    
    public void list() {
        int count = 0;
        for (Map.Entry<String, String> pair : map.entrySet()) {
            if (pair.getValue() != null) {
                count++;
                System.out.println(pair.getKey());
            }
        }
        if (count == 0) {
            System.out.println();
        }
    }
    
    public void exit() {
        putFile();
        System.exit(0);
    }
    
    public void getFile() throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        List<Integer> offsets = new LinkedList<Integer>();
        List<String> keys = new LinkedList<String>();
        byte b;
        int bytesCounter = 0;
        int off = -1;
        do {
            while ((b = file.readByte()) != 0) {
                bytesCounter++;
                buf.write(b);
            }
            bytesCounter++;
            if (off == -1) {
                off = file.readInt();
            } else {
                offsets.add(file.readInt());
            }
            bytesCounter += 4;
            keys.add((buf.toString("UTF-8")));
        } while (bytesCounter < off);
        try {
            offsets.add((int) file.length());
            Iterator<String> keyIter = keys.iterator();
            Iterator<Integer> offIter = offsets.iterator();
            for (int nextOffset : offsets) {
                while (bytesCounter < nextOffset) {
                    buf.write(file.read());
                    bytesCounter++;
                }
                if (buf.size() > 0) {
                    map.put(keyIter.next(), buf.toString("UTF-8"));
                    buf.reset();
                } else {
                    throw new Exception();
                }
            }
        } catch (IOException e) {
            System.err.println("Can't read db file");
            System.exit(-1);
        } catch (Exception e) {
            System.err.println("Wrong input file");
            System.exit(-1);
        }
        try {
            buf.close();
        } catch (IOException e) {
            System.err.println("Can't close db file");
            System.exit(-1);
        }
    }
    
    public void putFile() {
        try {
            file.setLength(0);
            Set<String> keys = map.keySet();
            List<Integer> offsetsPos = new LinkedList<Integer>();
            List<Integer> offsets = new LinkedList<Integer>();
            Iterator<Integer> offIter = offsets.iterator();
            for (String cur : keys) {
                file.write(cur.getBytes("UTF-8"));
                file.write('\0');
                offsetsPos.add(((int) file.getFilePointer()));
                file.writeInt(0);
            }
            for (String cur : keys) {
                offsets.add((int) file.getFilePointer());
                file.write(map.get(cur).getBytes("UTF-8"));
            }
            for (int pos :offsetsPos) {
                file.seek(pos);
                file.writeInt(pos);
            }
        } catch (IOException e) {
            System.err.println("Can't write into a db file");
            System.exit(-1);
        }
    }
}
