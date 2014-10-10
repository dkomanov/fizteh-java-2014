package ru.fizteh.fivt.students.vadim_mazaev.multifilemap;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public final class TablePart {
    public TablePart(Path tableDirPath, int dirNumber, int fileNumber) throws IOException {
        Path subpath = Paths.get(Integer.valueOf(dirNumber).toString() + ".dir");
        subpath = subpath.resolve(Integer.valueOf(fileNumber).toString() + ".dat");
        tablePartDirPath = tableDirPath.resolve(subpath);
        this.dirNumber = dirNumber;
        this.fileNumber = fileNumber;
        data = new TreeMap<String, String>();
        readFile();
    }
    
    public String get(String key) {
        return data.get(key);
    }
    
    public String put(String key, String value) {
        return data.put(key, value);
    }
    
    public String remove(String key) {
        return data.remove(key);
    }
    
    public Set<String> list() {
        return data.keySet();
    }
    public static int getNumberOfRecords(Path tableDirPath,
            int dirNumber, int fileNumber) throws IOException {
        int numberOfRecords = 0;
        Path filePath = tableDirPath.resolve(Integer.valueOf(dirNumber).toString() + ".dir");
        filePath = filePath.resolve(Integer.valueOf(fileNumber).toString() + ".dat");
        try (RandomAccessFile dbFile
                = new RandomAccessFile(filePath.toString(), "r")) {
            if (dbFile.length() == 0) {
                throw new IOException();
            }
            int bytesCounter = 0;
            int firstOffset = -1;
            int lastOffset = -1;
            boolean firstByte = true;
            byte b;
            do {
                while ((b = dbFile.readByte()) != 0) {
                    if (firstByte) {
                        if (fileNumber != Math.abs((Byte.valueOf(b) / 16) % 16)
                                || dirNumber != Math.abs(Byte.valueOf(b) % 16)) {
                            throw new IOException();
                        }
                        firstByte = false;
                        numberOfRecords++;
                    }
                    bytesCounter++;
                }
                bytesCounter++;
                lastOffset = dbFile.readInt();
                bytesCounter += 4;
                if (firstOffset == -1) {
                    firstOffset = lastOffset;
                }
                firstByte = true;
            } while (bytesCounter < firstOffset);
            if (lastOffset >= dbFile.length()) {
                throw new IOException();
            }
        }
        return numberOfRecords;
    }
    
    private void readFile() throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(tablePartDirPath.toString(), "r")) {
            ByteArrayOutputStream bytesBuffer = new ByteArrayOutputStream();
            List<Integer> offsets = new LinkedList<Integer>();
            List<String> keys = new LinkedList<String>();
            int bytesCounter = 0;
            byte b;
            //reading keys and offsets until reaching
            //the byte with first offset number
            do {
                while ((b = file.readByte()) != 0) {
                    bytesCounter++;
                    bytesBuffer.write(b);
                }
                bytesCounter++;
                offsets.add(file.readInt());
                bytesCounter += 4;
                String key = bytesBuffer.toString("UTF-8");
                bytesBuffer.reset();
                if (!keyIsInValidPath(key)) {
                    //if key is in a wrong file or directory
                    throw new IOException();
                }
                keys.add(key);
            } while (bytesCounter < offsets.get(0));
            //reading values until reaching the end of file
            offsets.add((int) file.length());
            offsets.remove(0);
            Iterator<String> keyIter = keys.iterator();
            for (int nextOffset : offsets) {
                while (bytesCounter < nextOffset) {
                    bytesBuffer.write(file.readByte());
                    bytesCounter++;
                }
                if (bytesBuffer.size() > 0) {
                    data.put(keyIter.next(), bytesBuffer.toString("UTF-8"));
                    bytesBuffer.reset();
                } else {
                    //if file ends before reading last value
                    throw new IOException();
                }
            }
            bytesBuffer.close();
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException();
        } catch (UnsupportedEncodingException e) {
            //if key or value can't be encoded to UTF-8
            throw new IOException();
        }
    }
    
    private boolean keyIsInValidPath(String key)
            throws UnsupportedEncodingException {
        int expectedDirNumber = Math.abs(key.getBytes("UTF-8")[0] % 16);
        int expectedFileNumber = Math.abs((key.getBytes("UTF-8")[0] / 16) % 16);
        return (dirNumber == expectedDirNumber && fileNumber == expectedFileNumber);
    }
    
    public void writeToFile() throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(tablePartDirPath.toString(), "rw")) {
            Set<String> keys = data.keySet();
            List<Integer> offsetsPos = new LinkedList<Integer>();
            for (String currentKey : keys) {
                file.write(currentKey.getBytes("UTF-8"));
                file.write('\0');
                offsetsPos.add((int) file.getFilePointer());
                file.writeInt(0);
            }
            List<Integer> offsets = new LinkedList<Integer>();
            for (String currentKey : keys) {
                offsets.add((int) file.getFilePointer());
                file.write(data.get(currentKey).getBytes("UTF-8"));
            }
            Iterator<Integer> offIter = offsets.iterator();
            for (int offsetPos : offsetsPos) {
                file.seek(offsetPos);
                file.writeInt(offIter.next());
            }
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException();
        }
    }
    
    private Map<String, String> data;
    private Path tablePartDirPath;
    private int fileNumber;
    private int dirNumber;
}
