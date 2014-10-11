package ru.fizteh.fivt.students.ekaterina_pogodina.filemap;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.io.File;
import java.nio.file.Files;
import java.io.ByteArrayOutputStream;
import java.util.Map;

public class DataBase {
    private Path dBasePath;
    private HashMap<String, String> dBase;
    public DataBase(String name) throws Exception {
        dBasePath = Paths.get(name);
        dBase = new HashMap<String, String>();
        File file = new File(name);
        if (file.isDirectory()) {
            throw new Exception("It is a directory");
        }
        if (file.exists()) {
            try {
                RandomAccessFile dbFile = new RandomAccessFile(dBasePath.toString(), "r");
                if (dbFile.length() > 0) {
                    while (dbFile.getFilePointer() < dbFile.length()) {
                        readDbFromFile(dbFile);
                    }
                }
                dbFile.close();
            } catch (Exception e) {
                throw new Exception();
            }
        } else {
            try {
                Files.createFile(file.toPath());
            } catch (IOException e) {
                throw new Exception("MakeDbFile: some errors");
            }
        }
    }

    public void list(String[] args) throws Exception {
        if (args.length > 1) {
            throw new Exception("list: too much arguments");
        }
        try {
            boolean flag = true;
            Iterator it = dBase.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                System.out.print(entry.getKey() + " ");
                flag = false;
            }
            if (!flag) {
                System.out.println("");
            }
        } catch (Exception e) {
            throw new  Exception(e.getMessage());
        }
    }

    public void put(String[] args) throws Exception {
        if (args.length < 3) {
            throw new Exception("put: missing operand");
        } else {
            if (args.length > 3) {
                throw new Exception("put: too much  arguments");
            }
            try {
                String key = args[1];
                String value = args[2];
                if (dBase.containsKey(key)) {
                    System.out.println("overwrite");
                    System.out.println(dBase.get(key));
                    dBase.remove(key);
                    dBase.put(key, value);
                } else {
                    System.out.println("new");
                    dBase.put(key, value);
                }
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }

    }
    public void put(String key, String value) throws Exception {
        try {
            if (dBase.containsKey(key)) {
                System.out.println(dBase.get(key));
                dBase.remove(key);
                dBase.put(key, value);
            } else {
                dBase.put(key, value);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    public void get(String[] args) throws Exception {
        if (args.length > 2) {
            throw new Exception("get: too much arguments");
        }
        try {
            String key = args[1];
            if (dBase.containsKey(key)) {
                System.out.println("found");
                System.out.println(dBase.get(key));
            } else {
                System.out.println("not found");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    public void remove(String[] args) throws Exception {
        if (args.length > 2) {
            throw new Exception("remove: too much arguments");
        }
        try {
            String key = args[1];
            if (dBase.containsKey(key)) {
                System.out.println("removed");
                dBase.remove(key);
            } else {
                System.out.println("not found");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    private void writeDbToFile(final RandomAccessFile dbFile) throws Exception {
        dbFile.setLength(0);
        Set<String> keys = dBase.keySet();
        List<Integer> offsetsPos = new LinkedList<Integer>();
        for (String currentKey : keys) {
            dbFile.write(currentKey.getBytes("UTF-8"));
            dbFile.write('\0');
            offsetsPos.add((int) dbFile.getFilePointer());
            dbFile.writeInt(0);
        }
        List<Integer> offsets = new LinkedList<Integer>();
        for (String currentKey : keys) {
            offsets.add((int) dbFile.getFilePointer());
            dbFile.write(dBase.get(currentKey).getBytes("UTF-8"));
        }
        Iterator<Integer> offIter = offsets.iterator();
        for (int offsetPos : offsetsPos) {
            dbFile.seek(offsetPos);
            dbFile.writeInt(offIter.next());
        }
    }
    private void readDbFromFile(final RandomAccessFile dbFile) throws Exception {
        ByteArrayOutputStream bytesBuffer = new ByteArrayOutputStream();
        List<Integer> offsets = new LinkedList<Integer>();
        List<String> keys = new LinkedList<String>();
        byte b;
        int bytesCounter = 0;
        int firstOffset = -1;
        do {
            while ((b = dbFile.readByte()) != 0) {
                bytesCounter++;
                bytesBuffer.write(b);
            }
            bytesCounter++;
            if (firstOffset == -1) {
                firstOffset = dbFile.readInt();
            } else {
                offsets.add(dbFile.readInt());
            }
            bytesCounter += 4;
            keys.add(bytesBuffer.toString("UTF-8"));
            bytesBuffer.reset();
        } while (bytesCounter < firstOffset);
        offsets.add((int) dbFile.length());
        Iterator<String> keyIter = keys.iterator();
        for (int nextOffset : offsets) {
            while (bytesCounter < nextOffset) {
                bytesBuffer.write(dbFile.readByte());
                bytesCounter++;
            }
            if (bytesBuffer.size() > 0) {
                put(keyIter.next(), bytesBuffer.toString("UTF-8"));
                bytesBuffer.reset();
            } else {
                throw new Exception();
            }
        }
        bytesBuffer.close();
    }
    public void close() throws Exception {
        try (RandomAccessFile dbFile = new RandomAccessFile(dBasePath.toString(), "rw")) {
            writeDbToFile(dbFile);
        } catch (Exception e) {
            throw new Exception("Error writing database to file");
        }
    }
}

