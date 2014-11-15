package ru.fizteh.fivt.students.vadim_mazaev.DataBase;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
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
final class TablePart {
    private Map<String, String> data;
    private Path tablePartDirPath;
    private int fileNumber;
    private int dirNumber;
    public static final String CODING = "UTF-8";
    public static final int NUMBER_OF_PARTITIONS = 16;
    
    /**
     * Constructs a TablePart. Read data from file if it exists.
     * @param tableDirPath Path to the directory of table.
     * @param dirNumber Directory number.
     * @param fileNumber File number.
     * @throws DataBaseIOException If file reading or checking failed.
     */
    public TablePart(Path tableDirPath, int dirNumber, int fileNumber)
            throws DataBaseIOException {
        tablePartDirPath = Paths.get(tableDirPath.toString(),
                dirNumber + ".dir", fileNumber + ".dat");
        this.dirNumber = dirNumber;
        this.fileNumber = fileNumber;
        data = new HashMap<>();
        if (tablePartDirPath.toFile().exists()) {
            try {
                readFile();
            } catch (IOException e) {
                throw new DataBaseIOException("Error reading file '"
                        + tablePartDirPath.toString() + "': " + e.getMessage(), e);
            }
        }
    }
    
    /**
     * Copy all data from memory to disk.
     * @throws DataBaseIOException If {@link TablePart#writeToFile writeToFile}
     * has thrown an I/O Exception.
     */
    public void commit() throws DataBaseIOException {
        if (getNumberOfRecords() == 0) {
            tablePartDirPath.toFile().delete();
            tablePartDirPath.getParent().toFile().delete();
        } else {
            try {
                writeToFile();
            } catch (IOException e) {
                throw new DataBaseIOException("Error writing to file '"
                        + tablePartDirPath.toString() + "': " + e.getMessage(), e);
            }
        }
    }
    
    /**
     * @param key Key.
     * @return The value associated with the key or null if this
     * {@link TablePart} doesn't contain such key. 
     * @throws UnsupportedEncodingException If {@link #keyIsValidForFile keyIsValidForFile}
     * method throws it.
     * @throws IllegalArgumentException If key is a null-string or can't be found in this file.
     */
    public String get(String key) throws UnsupportedEncodingException {
        if (key == null || !keyIsValidForFile(key)) {
            throw new IllegalArgumentException("'" + key + "' can't be found in this file");
        }
        return data.get(key);
    }
    
    /**
     * @param key Key.
     * @param value Value.
     * @return The value which was associated with this key before
     * or null if the key is a new one.
     * @throws UnsupportedEncodingException If {@link #keyIsValidForFile keyIsValidForFile}
     * method throws it.
     * @throws IllegalArgumentException If key or value is a null-string
     * or key can't be placed to this file.
     */
    public String put(String key, String value) throws UnsupportedEncodingException {
        if (key == null || !keyIsValidForFile(key)) {
            throw new IllegalArgumentException("'" + key + "' can't be placed to this file");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        return data.put(key, value);
    }
    
    /**
     * @param key Key.
     * @return The value which was associated with this key before removing
     * or null-string if there wasn't such key in this {@link TablePart}. 
     * @throws UnsupportedEncodingException If {@link #keyIsValidForFile keyIsValidForFile}
     * method throws it.
     * @throws IllegalArgumentException If key is a null-string or cannot be found in this file.
     */
    public String remove(String key) throws UnsupportedEncodingException {
        if (key == null || !keyIsValidForFile(key)) {
            throw new IllegalArgumentException("'" + key + "' can't be found in this file");
        }
        return data.remove(key);
    }
    
    /**
     * @return Set of keys which are stored in this {@link TablePart}.
     */
    public Set<String> list() {
        return data.keySet();
    }
    
    /**
     * @return Number of Records stored in this {@link TablePart}.
     */
    public int getNumberOfRecords() {
        return data.size();
    }
    
    /**
     * Reads file from disk into memory.
     * @throws IOException If file is missing, corrupted or contains wrong data.
     */
    private void readFile() throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(tablePartDirPath.toString(), "r")) {
            ByteArrayOutputStream bytesBuffer = new ByteArrayOutputStream();
            List<Integer> offsets = new LinkedList<>();
            List<String> keys = new LinkedList<>();
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
                String key = bytesBuffer.toString(CODING);
                bytesBuffer.reset();
                if (!keyIsValidForFile(key)) {
                    throw new IllegalArgumentException();
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
                    if (data.put(keyIter.next(), bytesBuffer.toString(CODING)) != null) {
                        throw new IllegalArgumentException("Key repeats in file");
                    }
                    bytesBuffer.reset();
                } else {
                    throw new EOFException();
                }
            }
            bytesBuffer.close();
        } catch (UnsupportedEncodingException e) {
            throw new IOException("Key or value can't be encoded to " + CODING, e);
        } catch (IllegalArgumentException e) {
            throw new IOException("Key is in a wrong file or directory", e);
        } catch (EOFException e) {
            throw new IOException("File breaks unexpectedly", e);
        } catch (IOException e) {
            throw new IOException("Unable read from file", e);
        }
    }
    
    /**
     * @param key Key.
     * @return True if key can be in this {@link TablePart}.
     * @throws UnsupportedEncodingException If key bytes cannot be encode to {@value #CODING}.
     */
    private boolean keyIsValidForFile(String key)
            throws UnsupportedEncodingException {
        int expectedDirNumber = Math.abs(key.getBytes(CODING)[0] % NUMBER_OF_PARTITIONS);
        int expectedFileNumber = Math.abs((key.getBytes(CODING)[0] / NUMBER_OF_PARTITIONS)
                % NUMBER_OF_PARTITIONS);
        return (dirNumber == expectedDirNumber && fileNumber == expectedFileNumber);
    }
    
    /**
     * Writes all data from memory to file on disk.
     * @throws IOException If method can't write to file.
     */
    private void writeToFile() throws IOException {
        tablePartDirPath.getParent().toFile().mkdir();
        try (RandomAccessFile file = new RandomAccessFile(tablePartDirPath.toString(), "rw")) {
            file.setLength(0);
            Set<String> keys = data.keySet();
            List<Integer> offsetsPos = new LinkedList<>();
            for (String currentKey : keys) {
                file.write(currentKey.getBytes(CODING));
                file.write('\0');
                offsetsPos.add((int) file.getFilePointer());
                file.writeInt(0);
            }
            List<Integer> offsets = new LinkedList<>();
            for (String currentKey : keys) {
                offsets.add((int) file.getFilePointer());
                file.write(data.get(currentKey).getBytes(CODING));
            }
            Iterator<Integer> offIter = offsets.iterator();
            for (int offsetPos : offsetsPos) {
                file.seek(offsetPos);
                file.writeInt(offIter.next());
            }
        } catch (FileNotFoundException e) {
            throw new IOException("Unable to create file", e);
        } catch (UnsupportedEncodingException e) {
            throw new IOException("Key or value can't be encoded to " + CODING, e);
        } catch (IOException e) {
            throw new IOException("Unable to write to file", e);
        }
    }
}
