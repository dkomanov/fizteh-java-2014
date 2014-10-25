package ru.fizteh.fivt.studenrts.theronsg.multifilehashmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class FileMap {
    
    private Map<String, String> map;
    private RandomAccessFile file;
    private String name;
    
    FileMap(final String path) throws IOException {
        map = new TreeMap<>();
        
        File fil = new File(path + ".dat");
        if (!fil.exists()) {
            fil.createNewFile();
        }
        file = new RandomAccessFile(path, "rw");
        name = path;
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
        if (value == null) {
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
    
    public String list() {
        return String.join(", ", map.keySet());
    }
    
    public void exit() {
        putFile();
    }
    
    public void delete() {
        new File(name).delete();
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
            buf.reset();
        } while (bytesCounter < off);
        try {
            offsets.add((int) file.length());
            Iterator<String> keyIter = keys.iterator();
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
            if (file.readByte() == 0) {
                new File(name).delete();
                return;
            }
            file.setLength(0);
            Set<String> keys = map.keySet();
            List<Integer> offsetsPos = new LinkedList<Integer>();
            for (String cur : keys) {
                file.write(cur.getBytes("UTF-8"));
                file.write('\0');
                offsetsPos.add(((int) file.getFilePointer()));
                file.writeInt(0);
            }
            List<Integer> offsets = new LinkedList<Integer>();
            for (String cur : keys) {
                offsets.add((int) file.getFilePointer());
                file.write(map.get(cur).getBytes("UTF-8"));
            }
            Iterator<Integer> offIter = offsets.iterator();
            for (int pos :offsetsPos) {
                file.seek(pos);
                file.writeInt(offIter.next());
            }
        } catch (IOException e) {
            System.err.println("Can't write into a db file");
            System.exit(-1);
        }
    }
    
    public boolean exists() {
        return new File(name).exists();
    }
}
