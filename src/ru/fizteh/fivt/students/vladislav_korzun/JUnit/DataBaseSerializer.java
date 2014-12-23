package ru.fizteh.fivt.students.vladislav_korzun.JUnit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.*;

public class DataBaseSerializer {
    
    private Map<String, String> fileMap;
    private static final String CODING = "UTF-8";
    private static final String FILE_EXTENTION = ".dat";
    private static final String DIR_EXTENTION = ".dir";
    private static final int NUMBER_OF_FOLDERS = 16;
    private static final int NUMBER_OF_FILES = 16;

    public DataBaseSerializer() {
        fileMap = new HashMap<>();
    }

    public Map<String, String> getMap() {
        return fileMap;
    }

    public void setMap(Map<String, String> map) {
        fileMap = new HashMap<>(map);
    }

    void readTable(Path tableDir) throws DataBaseException {
        fileMap.clear();
        File[] dirs = tableDir.toFile().listFiles();
        for (File dir: dirs) {
            if (dir.isDirectory()) {
                readDir(dir);
            }
        }
    }
    
    void readDir(File dir) throws DataBaseException {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                    readFile(file);
            }
        }
    }
    
    void readFile(File file) throws DataBaseException {
        List<Integer> offset = new LinkedList<>() ;
        int counter = 0;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        List<String> keys = new LinkedList<>();
        Byte bt = null;
        try (RandomAccessFile db = new RandomAccessFile(file, "r")) {
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
            throw new DataBaseException("Named charset is not supported");
        } catch (IOException e) {
            throw new DataBaseException("Invalid input");
        }
    }
    
    void writeTable(Path tableDir) throws DataBaseException {
        
        Map<String, String> buffer = new HashMap<>();
        removeDat(tableDir);
        Set<String> keys = fileMap.keySet();
        try {
            for (int i = 0; i < NUMBER_OF_FOLDERS; i++) {
                for (int j = 0; j < NUMBER_OF_FILES; j++) {
                    for (String key : keys) {
                        byte bt = key.getBytes(CODING)[0];
                        int nDirectory = Math.abs(bt % NUMBER_OF_FOLDERS);
                        int nFile =  Math.abs(bt / NUMBER_OF_FOLDERS % NUMBER_OF_FILES);
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
            throw new DataBaseException(e.getMessage());
        } catch (Exception e) {
            throw new DataBaseException("Writing failed");
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
    
    void writeFile(File file, Map<String, String> map) throws DataBaseException {
        
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
            throw new DataBaseException("Named charset is not supported ");
        } catch (IOException e) {
            throw new DataBaseException("Pos is less than 0 or if an I/O error occurs");
        } 
    }
}
