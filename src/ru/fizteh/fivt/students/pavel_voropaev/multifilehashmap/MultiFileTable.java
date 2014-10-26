package ru.fizteh.fivt.students.pavel_voropaev.multifilehashmap;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Set;

public class MultiFileTable implements Closeable {
    private static final int FOLDERS = 16;
    private static final int FILES = 16;
    
    private String name;
    private Path directory;
    private MyMap[] content;
    
    public MultiFileTable(Path dbDir, String tableName) throws IllegalArgumentException, IOException {
        name = tableName;
        directory = dbDir.resolve(tableName);
        content = new MyMap[FOLDERS * FILES];
        for (int i = 0; i < FOLDERS * FILES; ++i) {
            content[i] = new MyMap();
        }
        if (!Files.exists(directory)) {
            Files.createDirectory(directory);
        } else {
            load();
        }
    }

    public String put(String key, String value) throws IllegalArgumentException {
        int place = getPlace(key);
        return content[place].map.put(key, value);
    }

    public String get(String key) throws IllegalArgumentException {
        return content[getPlace(key)].map.get(key);
    }

    public String remove(String key) throws IllegalArgumentException {
        return content[getPlace(key)].map.remove(key);
    }

    public String name() {
        return name;
    }
    
    public int size() {
        int size = 0;
        for (MyMap file : content) {
            size += file.map.size();
        }
        return size;
    }

    public String[] list() {
        String[] retVal = new String[size()];
        int written = 0;
        for (MyMap file : content) {
            Set<String> keysList = file.map.keySet();
            Iterator<String> it = keysList.iterator();
            
            int i;
            for (i = written; it.hasNext(); ++i) {
                retVal[i] = it.next();
            }
            written = i;
        }

        return retVal;
    }

    private void load() throws IOException, IllegalArgumentException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path entry : stream) {
                if (!Files.isDirectory(entry)) {
                    ThrowExc.containsWrongFiles(directory.toString());
                }
                String entryStr = entry.toString();
                boolean correctDir = false;
                for (int i = 0; i < FOLDERS; ++i) {
                    if (entryStr.endsWith(File.separator + Integer.toString(i) + ".dir")) {
                        readDir(entry, i);
                        correctDir = true;
                        break;
                    }
                }
                if (!correctDir) {
                    ThrowExc.containsWrongFiles(directory.toString());
                }
            }
        }
    }
    
    private void readDir(Path entry, int dirNum) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(entry)) {
            for (Path path : stream) {
                String fileName = path.toString();       
                boolean correctFile = false;
                for (int i = 0; i < FILES; ++i) {
                    if (fileName.endsWith(File.separator + Integer.toString(i) + ".dat")) {
                        readFile(fileName, dirNum, i);
                        correctFile = true;
                        break;
                    }
                }
                if (!correctFile) {
                    ThrowExc.containsWrongFiles(directory.toString());
                }
            }
        }
    }

    private void readFile(String fileName, int dirNum, int fileNum) throws IOException {
        DataInputStream stream = new DataInputStream(new FileInputStream(fileName));
        while (true) {
            try {
                String key = readWord(stream);
                String value = readWord(stream);
                int hashcode = Math.abs(key.hashCode());
                if (hashcode % FOLDERS != dirNum || hashcode / FOLDERS % FILES != fileNum) {
                    throw new IOException("Wrong file format.");
                }
 
                content[dirNum * 16 + fileNum].map.put(key, value);
            } catch (EOFException e) {
                break;
            }
        }
        stream.close();
    }

    private String readWord(DataInputStream stream) throws EOFException,
            IOException {
        int length = stream.readInt();
        byte[] word = new byte[length];
        stream.readFully(word);
        return new String(word, "UTF-8");
    }
    
    private void save() throws IOException {
        Utils.rm(directory);
        Files.createDirectory(directory);
        for (int i = 0; i < FOLDERS * FILES; ++i) {
            if (content[i].map.size() > 0) {
                Path dir = getDirectoryPath(i);
                if (!Files.exists(dir)) {
                    Files.createDirectory(dir);
                }
                writeFile(i);
            }
        }
    }

   private void writeFile(int fileNum) throws IOException {
       Path filePath = getFilePath(fileNum);
       if (!Files.exists(filePath)) {
           Files.createFile(filePath);
       }
        FileOutputStream output = new FileOutputStream(getFilePath(fileNum).toString());
        Set<String> keyList = content[fileNum].map.keySet();
        ByteBuffer buffer = ByteBuffer.allocate(4);
        Iterator<String> it = keyList.iterator();
        try {
            while (it.hasNext()) {
                String key = it.next();
                byte[] keyByte = key.getBytes("UTF-8");
                byte[] valueByte = content[fileNum].map.get(key).getBytes("UTF-8");

                output.write(buffer.putInt(0, keyByte.length).array());
                output.write(keyByte);

                output.write(buffer.putInt(0, valueByte.length).array());
                output.write(valueByte);
            }
        } catch (Exception e) {
            output.close();
            throw new IOException("Cannot write into a file: " + getFilePath(fileNum).toString());
        }
        output.close();
    }
    
    private int getPlace(String key) {
        int hashcode = Math.abs(key.hashCode());
        return hashcode % FOLDERS * 16 + hashcode / FOLDERS % FILES;
    }
    
    private Path getDirectoryPath(int fileNumber) {
        String directoryName = new StringBuilder().append(fileNumber / FOLDERS).append(".dir").toString();
        return directory.resolve(directoryName);
    }
    
    private Path getFilePath(int fileNumber) {
        String fileName = new StringBuilder().append(fileNumber % FOLDERS).append(".dat").toString();
        return getDirectoryPath(fileNumber).resolve(fileName);
    }

    @Override
    public void close() throws IOException {
        save();
    }
    
}
