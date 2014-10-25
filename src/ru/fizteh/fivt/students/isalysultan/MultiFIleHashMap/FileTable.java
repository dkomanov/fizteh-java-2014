package ru.fizteh.fivt.students.isalysultan.MultiFileHashMap;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileTable {

    private Path filePath;

    private boolean openRead = false;

    private TreeMap<String, String> storage = new TreeMap<String, String>();

    public String getForMap(String key) {
        return storage.get(key);
    }

    public boolean ContainsKey(String key) {
        return storage.containsKey(key);
    }

    public boolean ContainsValue(String value) {
        return storage.containsValue(value);
    }

    public String putMap(String key, String value) {
        return storage.put(key, value);
    }

    public String removeMap(String key) {
        return storage.remove(key);
    }

    public Set<String> keySetMap() {
        return storage.keySet();
    }

    public void setPath(Path newPath) {
        filePath = newPath;
    }

    public Path getPath() {
        return filePath;
    }

    public boolean emptyMap() {
        if (storage.isEmpty()) {
            return true;
        }
        return false;
    }

    public boolean getOpenRead() {
        return openRead;
    }

    public FileTable() throws IOException {
        filePath = null;
    }

    public FileTable(Path FilePath, Table table) throws IOException {
        filePath = FilePath;
        RandomAccessFile file = new RandomAccessFile(filePath.toString(), "r");
        if (file.length() > 0) {
            readFile(file, table);
            file.close();
            openRead = true;
            return;
        }
    }

    public void readFile(RandomAccessFile file, Table table) throws IOException {
        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        ArrayList<String> key = new ArrayList<String>();
        ArrayList<Integer> offset = new ArrayList<Integer>();
        int count = 0;
        int camelCase = -1;
        byte part;
        boolean dontReadFirstOffset = false;
        while (count < camelCase || !dontReadFirstOffset) {
            while ((part = file.readByte()) != 0) {
                ++count;
                buff.write(part);
            }
            ++count;
            if (camelCase == -1) {
                camelCase = file.readInt();
                offset.add(camelCase);
                dontReadFirstOffset = true;
            } else {
                offset.add(file.readInt());
            }
            count += 4;
            key.add(buff.toString("UTF-8"));
            buff.reset();
        }
        Iterator<String> itKey = key.iterator();
        Iterator<Integer> itOffset = offset.iterator();
        Iterator<Integer> itForEndOffset = offset.iterator();
        int size = (int) file.length();
        count = itForEndOffset.next();
        int afterCount = count;
        boolean forEnd = true;
        while (forEnd) {
            table.incrementNumberRecords();
            if (size < count) {
                System.err.println("error with offset");
                System.exit(2);
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
                afterCount = itForEndOffset.next();
                while (count < afterCount) {
                    buff.write(file.readByte());
                    ++count;
                }
            }
            String a = buff.toString("UTF-8");
            storage.put(itKey.next(), buff.toString("UTF-8"));
            buff.reset();
            count = afterCount;
        }
        buff.close();

    }

    public void writeFile() throws IOException {
        RandomAccessFile endFile = new RandomAccessFile(filePath.toString(),
                "rwd");
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
    }

    public boolean empty() {
        if (filePath == null) {
            return true;
        }
        return false;
    }

    public boolean needToDeleteFile() {
        if (this.empty() && openRead) {
            return true;
        }
        return false;
    }

    public void deleteFile() {
        filePath.toFile().delete();
    }

    public boolean FileOpenAndNotExist() {
        if (openRead && filePath != null) {
            return true;
        }
        return false;
    }

    public boolean Open() {
        return openRead;
    }
}

