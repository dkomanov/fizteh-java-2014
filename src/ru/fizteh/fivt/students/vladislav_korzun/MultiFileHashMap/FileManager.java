package ru.fizteh.fivt.students.vladislav_korzun.MultiFileHashMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class FileManager {
    
    public Map<String, String> filemap;
    
    public FileManager() {
        filemap = new TreeMap<String, String>();
    }

    void readTable(Path tabledir) {
        filemap.clear();
        File[] dirs = tabledir.toFile().listFiles();
        for (File dir: dirs) {
            if (dir.isDirectory()) {
                readDir(dir);
            }
        }
    }
    
    void readDir(File dir) {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                try {
                    readFile(file);
                } catch (FileNotFoundException e) {
                    System.err.println("File not found");
                } catch (IOException e) {
                    System.err.println("Can't close file");
                }
            }
        }
    }
    
    void readFile(File file) throws IOException {
        RandomAccessFile db = new RandomAccessFile(file, "r");
        List<Integer> offset = new LinkedList<Integer>() ;
        int counter = 0;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        List<String> keys = new LinkedList<String>();
        Byte bt = null;
        try {
            do {
                do {
                    bt = db.readByte();
                    counter++;
                    if (bt != 0) {
                        buffer.write(bt);
                    }
                } while (bt != 0);
                keys.add(buffer.toString("UTF-8"));
                buffer.reset();
                offset.add(db.readInt());
                counter += 4;
            } while (counter != offset.get(0));
            offset.add((int) db.length());
            for (int i = 1; i < offset.size(); i++) {
                while (offset.get(i) > counter) {
                    bt = db.readByte();
                    counter++;
                    buffer.write(bt);
                }
                filemap.put(keys.get(i - 1), buffer.toString("UTF-8"));
                buffer.reset();
            }
            buffer.close();
        } catch (UnsupportedEncodingException e) {
            System.err.println("Named charset is not supported");
        } catch (IOException e) {
            System.err.println("Invalid input");
        } finally {
            db.close();
        }
    }
    
    void writeTable(Path tabledir) {
        
        Map<String, String> buffer = new TreeMap<String, String>();
        removedat(tabledir); 
        Set<String> keys = filemap.keySet();
        try {
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    for (String key : keys) {
                        byte bt = key.getBytes("UTF-8")[0];
                        int ndirectory = Math.abs(bt % 16);
                        int nfile =  Math.abs(bt / 16 % 16);
                        if (ndirectory == i && nfile == j) {
                            buffer.put(key, filemap.get(key));
                        }
                    }
                    if (!buffer.isEmpty()) {
                        Integer nd = i;
                        Integer nf = j;
                        String path = new String(nd.toString() + ".dir");
                        
                        File dbdir = new File(tabledir.resolve(path).toString());
                        if (!dbdir.exists()) {
                            if (!dbdir.mkdir()) {
                                throw new IOException("Can't create directory");
                            }
                        }
                        path = new String(nf.toString() + ".dat");
                        File dbfile = new File(dbdir.toPath().resolve(path).toString());
                        if (!dbfile.exists()) {
                            if (!dbfile.createNewFile()) {
                                throw new IOException("Can't create file");
                            }
                        }
                        writeFile(dbfile, buffer);
                        buffer.clear();
                    }
                }
            }
        } catch (IOException e) { 
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.println("Writing failed");
        }
                
    }
    
    void removedat(Path tabledir) {
        File table = new File(tabledir.toString());
        File[] dirs = table.listFiles();
        for (File dir : dirs) {
            File[] fls = dir.listFiles();
            for (File fl : fls) {
                fl.delete();
            }
            dir.delete();
        }
    }
    
    void writeFile(File file, Map<String, String> map) throws IOException {
        
        try (RandomAccessFile db = new RandomAccessFile(file, "rw")) {
            db.setLength(0);
            LinkedList<Integer> offset = new LinkedList<Integer>();
            Set<String> keys = map.keySet();
            for (String key : keys) {
                db.write(key.getBytes("UTF-8"));
                db.write('\0');
                offset.add((int) db.getFilePointer());
                db.writeInt(0);
            }
            Collection<String> vals = map.values();
            int i = 0;
            int pointer = 0;
            for (String val: vals) {
                pointer = (int) db.getFilePointer(); 
                db.seek(offset.get(i));
                i++;
                db.writeInt(pointer);
                db.seek(pointer);
                db.write(val.getBytes("UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            System.err.println("Named charset is not supported ");
        } catch (IOException e) {
            System.err.println("Pos is less than 0 or if an I/O error occurs");
        } 
    }
}
