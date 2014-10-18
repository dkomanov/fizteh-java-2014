package ru.fizteh.fivt.students.vadim_mazaev.JUnit.DataBase;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class represents a single file of type *.dir/*.dat.
 * @author Vadim Mazaev
 */
public final class TablePart {
    private Map<String, String> data;
    private Path tablePartDirPath;
    private int fileNumber;
    private int dirNumber;
    private boolean isConnected;
    private int numberOfRecords;
    
    /**
     * Constructs a TablePart. Don't get any data from disk, only check
     * file and count number of records.
     * @param tableDirPath Path to represented file in such format:
     * "[path to the table directory]/*.dir/*.dat".
     * @param dirNumber Directory number.
     * @param fileNumber File number.
     * @throws IOException If file checking failed.
     */
    public TablePart(Path tableDirPath, int dirNumber, int fileNumber) throws IOException {
        tablePartDirPath = tableDirPath.resolve(
                Paths.get(Integer.valueOf(dirNumber).toString() + ".dir",
                Integer.valueOf(fileNumber).toString() + ".dat"));
        this.dirNumber = dirNumber;
        this.fileNumber = fileNumber;
        isConnected = false;
        numberOfRecords = 0;
        data = new HashMap<String, String>();
        if (!setNumberOfRecords()) {
            throw new IOException();
        }
    }
    
    /**
     * Reads data from disk and saves it in memory. 
     * @throws IOException If {@link TablePart#readFile readFile}
     * has thrown an I/O Exception.
     */
    private void connect() throws IOException {
        try {
            readFile();
        } catch (IOException e) {
            throw new IOException("Cannot read file: " + tablePartDirPath.toString(), e);
        }
        isConnected = true;
    }
    
    /**
     * Closes working session, move all data from memory to disk.
     * @throws IOException If {@link TablePart#writeToFile writeToFile}
     * has thrown an I/O Exception.
     */
    public void disconnect() throws IOException {
        if (numberOfRecords == 0) {
            tablePartDirPath.toFile().delete();
            tablePartDirPath.getParent().toFile().delete();
        } else if (isConnected) {
            try {
                writeToFile();
            } catch (IOException e) {
                throw new IOException("Cannot write to file: " + tablePartDirPath.toString(), e);
            }
            data.clear();
            isConnected = false;
        }
    }
    
    /**
     * Method will automatically call {@link TablePart#connect connect}
     * if data hasn't been read from disk yet.
     * @param key Key.
     * @return The value associated with the key or null if this
     * {@link TablePart} doesn't contain such key. 
     * @throws IOException If {@link TablePart#connect connect} method fails.
     * @throws IllegalArgumentException If key is a null-string or cannot be in this file.
     */
    public String get(String key) throws IOException {
        if (key == null || !keyIsInValidPath(key)) {
            throw new IllegalArgumentException(key + " cannot be in this file.");
        }
        if (!isConnected) {
            connect();
        }
        return data.get(key);
    }
    
    /**
     * Method will automatically call {@link TablePart#connect connect}
     * if data hasn't been read from disk yet.
     * @param key Key.
     * @param value Value.
     * @return The value which was associated with this key before
     * or null if the key is a new one.
     * @throws IOException If {@link TablePart#connect connect} method fails.
     * @throws IllegalArgumentException If key is a null-string or cannot be in this file.
     */
    public String put(String key, String value) throws IOException {
        if (key == null || !keyIsInValidPath(key)) {
            throw new IllegalArgumentException(key + " cannot be in this file.");
        }
        if (!isConnected) {
            connect();
        }
        String oldValue = data.put(key, value);
        if (oldValue == null) {
            numberOfRecords++;
        }
        return oldValue;
    }
    
    /**
     * Method will automatically call {@link TablePart#connect connect}
     * if data hasn't been read from disk yet.
     * @param key Key.
     * @return The value which was associated with this key before removing
     * or null-string if there wasn't such key in this {@link TablePart}. 
     * @throws IOException If {@link TablePart#connect connect} method fails.
     * @throws IllegalArgumentException If key is a null-string or cannot be in this file.
     */
    public String remove(String key) throws IOException {
        if (key == null || !keyIsInValidPath(key)) {
            throw new IllegalArgumentException(key + " cannot be in this file.");
        }
        if (!isConnected) {
            connect();
        }
        String removedValue = data.remove(key);
        if (removedValue != null) {
            numberOfRecords--;
        }
        return removedValue;
    }
    
    /**
     * Method will automatically call {@link TablePart#connect connect}
     * if data hasn't been read from disk yet.
     * @return Set of keys which are stored in this {@link TablePart}.
     * @throws IOException If {@link TablePart#connect connect} method fails.
     */
    public Set<String> list() throws IOException {
        if (!isConnected) {
            connect();
        }
        return data.keySet();
    }
    
    /**
     * This method doesn't require to read data from disk.
     * @return Number of Records stored in this {@link TablePart}.
     */
    public int getNumberOfRecords() {
        return numberOfRecords;
    }
    
    /**
     * Counts the number of records stored in file on disk.
     * @return True if file looks good.
     */
    private boolean setNumberOfRecords() {
        if (!tablePartDirPath.toFile().exists()) {
            try {
                tablePartDirPath.getParent().toFile().mkdir();
                tablePartDirPath.toFile().createNewFile();
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        try (RandomAccessFile dbFile
                = new RandomAccessFile(tablePartDirPath.toString(), "r")) {
            if (dbFile.length() == 0) {
                return false;
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
                            return false;
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
                return false;
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }
    
    /**
     * Reads file from disk and saves all data into memory.
     * @throws IOException If file was corrupted.
     * @throws IllegalArgumentException If this {@link TablePart} represents a missing file.
     */
    private void readFile() throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(tablePartDirPath.toString(), "r")) {
            if (file.length() == 0) {
                return;
            }
            ByteArrayOutputStream bytesBuffer = new ByteArrayOutputStream();
            List<Integer> offsets = new LinkedList<Integer>();
            List<String> keys = new LinkedList<String>();
            int bytesCounter = 0;
            byte b;
            //Reading keys and offsets until reaching
            //the byte with first offset number.
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
                    throw new IOException("Key is in a wrong file or directory");
                }
                keys.add(key);
            } while (bytesCounter < offsets.get(0));
            //Reading values until reaching the end of file.
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
                    throw new IOException("File ends before expected last value.");
                }
            }
            bytesBuffer.close();
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(e);
        } catch (UnsupportedEncodingException e) {
            throw new IOException("Key or value can't be encoded to UTF-8.", e);
        }
    }
    
    /**
     * @param key Key.
     * @return True if key can be in this {@link TablePart}.
     * @throws UnsupportedEncodingException If key bytes cannot be encode to UTF-8.
     */
    private boolean keyIsInValidPath(String key)
            throws UnsupportedEncodingException {
        int expectedDirNumber = Math.abs(key.getBytes("UTF-8")[0] % 16);
        int expectedFileNumber = Math.abs((key.getBytes("UTF-8")[0] / 16) % 16);
        return (dirNumber == expectedDirNumber && fileNumber == expectedFileNumber);
    }
    
    /**
     * Writes all data from memory to file on disk.
     * @throws IOException If cannot write to file.
     * @throws IllegalArgumentException If file doesn't exist and cannot be created.
     */
    private void writeToFile() throws IOException {
        tablePartDirPath.getParent().toFile().mkdir();
        try (RandomAccessFile file = new RandomAccessFile(tablePartDirPath.toString(), "rw")) {
            file.setLength(0);
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
            throw new IllegalArgumentException(e);
        }
    }
}
