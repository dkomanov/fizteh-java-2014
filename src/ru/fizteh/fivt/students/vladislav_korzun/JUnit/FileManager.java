package ru.fizteh.fivt.students.vladislav_korzun.JUnit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.*;

public class FileManager {
    
    public Map<String, String> fileMap;
    private static final String CODING = "UTF-8";
    private static final String FILE_EXTENTION = ".dat";
    private static final String DIR_EXTENTION = ".dir";

    public FileManager() {
        fileMap = new HashMap<>();
    }

    void readTable(Path tableDir) {
        fileMap.clear();
        File[] dirs = tableDir.toFile().listFiles();
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
        List<Integer> offset = new LinkedList<>() ;
        int counter = 0;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        List<String> keys = new LinkedList<>();
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
                keys.add(buffer.toString(CODING));
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
                fileMap.put(keys.get(i - 1), buffer.toString(CODING));
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
    
    void writeTable(Path tableDir) {
        
        Map<String, String> buffer = new TreeMap<String, String>();
        removeDat(tableDir);
        Set<String> keys = fileMap.keySet();
        int numberDirectory = 16;
        int numberFile = 16;
        try {
            for (int i = 0; i < numberDirectory; i++) {
                for (int j = 0; j < numberFile; j++) {
                    for (String key : keys) {
                        byte bt = key.getBytes("UTF-8")[0];
                        int nDirectory = Math.abs(bt % 16);
                        int nFile =  Math.abs(bt / 16 % 16);
                        if (nDirectory == i && nFile == j) {
                            buffer.put(key, fileMap.get(key));
                        }
                    }
                    if (!buffer.isEmpty()) {
                        Integer newDirectory = i;
                        Integer newFile = j;
                        String path = new String(newDirectory.toString() + DIR_EXTENTION);
                        
                        File dbdir = new File(tableDir.resolve(path).toString());
                        if (!dbdir.exists()) {
                            if (!dbdir.mkdir()) {
                                throw new IOException("Can't create directory");
                            }
                        }
                        path = new String(newFile.toString() + FILE_EXTENTION);
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
    
    void removeDat(Path tableDir) {
        File table = new File(tableDir.toString());
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
            LinkedList<Integer> offset = new LinkedList<>();
            Set<String> keys = map.keySet();
            for (String key : keys) {
                db.write(key.getBytes(CODING));
                db.write('\0');
                offset.add((int) db.getFilePointer());
                db.writeInt(0);
            }
            Collection<String> vals = map.values();
            int i = 0;
            int pointer;
            for (String val: vals) {
                pointer = (int) db.getFilePointer(); 
                db.seek(offset.get(i));
                i++;
                db.writeInt(pointer);
                db.seek(pointer);
                db.write(val.getBytes(CODING));
            }
        } catch (UnsupportedEncodingException e) {
            System.err.println("Named charset is not supported ");
        } catch (IOException e) {
            System.err.println("Pos is less than 0 or if an I/O error occurs");
        } 
    }
}
