package ru.fizteh.fivt.students.vadim_mazaev.DataBase;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.fizteh.fivt.storage.structured.Storeable;

/**
 * Class represents a single file of type *.dir/*.dat.
 */
final class TablePart {
    private Map<String, Storeable> data = new HashMap<>();
    // Keep all data, which this .dat file contains.
    private boolean changed;
    // Using this flag, the method learns whether to update the data on disk.
    private DbTable table; // Link to table, which stores this .dat file.
    private Path tablePartDirectoryPath; // Path to table directory.
    private int fileNumber; // Number of file in directory, which contains data
    private int directoryNumber; // Number of directory containing file.

    /**
     * Constructs a TablePart. Read data from file if it exists.
     * 
     * @param table
     *            Table, which part is this TablePart. Expects being non-null.
     * @param tableDirectoryPath
     *            Path to the directory of table. Expects being non-null.
     * @param directoryNumber
     *            Directory number.
     * @param fileNumber
     *            File number.
     * @throws DataBaseIOException
     *             If file reading or checking failed.
     */
    public TablePart(DbTable table, Path tableDirectoryPath,
            int directoryNumber, int fileNumber) throws DataBaseIOException {
        tablePartDirectoryPath = Paths.get(tableDirectoryPath.toString(),
                directoryNumber + ".dir", fileNumber + ".dat");
        this.directoryNumber = directoryNumber;
        this.fileNumber = fileNumber;
        this.table = table;
        if (tablePartDirectoryPath.toFile().exists()) {
            try {
                readFile();
            } catch (IOException e) {
                throw new DataBaseIOException("Error reading file '"
                        + tablePartDirectoryPath.toString() + "': "
                        + e.getMessage(), e);
            }
        }
    }

    /**
     * Copy all data from memory to disk.
     * 
     * @throws DataBaseIOException
     *             If {@link TablePart#writeToFile writeToFile} has thrown an
     *             I/O Exception.
     */
    public void commit() throws DataBaseIOException {
        if (changed) {
            changed = false;
            if (getNumberOfRecords() == 0) {
                tablePartDirectoryPath.toFile().delete();
                tablePartDirectoryPath.getParent().toFile().delete();
            } else {
                try {
                    writeToFile();
                } catch (IOException e) {
                    throw new DataBaseIOException("Error writing to file '"
                            + tablePartDirectoryPath.toString() + "': "
                            + e.getMessage(), e);
                }
            }
        }
    }

    /**
     * @param key
     *            Key.
     * @return The value associated with the key or null if this
     *         {@link TablePart} doesn't contain such key.
     * @throws UnsupportedEncodingException
     *             If {@link #checkKey checkKey} method throws it.
     * @throws IllegalArgumentException
     *             If key is a null-string or can't be found in this file.
     */
    public Storeable get(String key) throws UnsupportedEncodingException {
        checkKey(key);
        return data.get(key);
    }

    /**
     * @param key
     *            Key.
     * @param value
     *            Value.
     * @return The value which was associated with this key before or null if
     *         the key is a new one.
     * @throws UnsupportedEncodingException
     *             If {@link #keyIsValidForFile keyIsValidForFile} method throws
     *             it.
     * @throws IllegalArgumentException
     *             If key or value is null or key can't be placed to this file.
     */
    public Storeable put(String key, Storeable value)
            throws UnsupportedEncodingException {
        checkKey(key);
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        changed = true;
        return data.put(key, value);
    }

    /**
     * @param key
     *            Key.
     * @return The value which was associated with this key before removing or
     *         null-string if there wasn't such key in this {@link TablePart}.
     * @throws UnsupportedEncodingException
     *             If {@link #checkKey checkKey} method throws it.
     * @throws IllegalArgumentException
     *             If key is a null-string or cannot be found in this file.
     */
    public Storeable remove(String key) throws UnsupportedEncodingException {
        checkKey(key);
        changed = true;
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
     * Reads file from disk.
     * 
     * @throws IOException
     *             If file is missing, corrupted or contains wrong data.
     */
    private void readFile() throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(
                tablePartDirectoryPath.toString(), "r")) {
            ByteArrayOutputStream bytesBuffer = new ByteArrayOutputStream();
            List<Integer> offsets = new LinkedList<>();
            List<String> keys = new LinkedList<>();
            // File structure is:
            // key1</0>offset[1](4-bytes)key2</0>offset[2](4-bytes)...[1]value1[2]value2...
            byte b;
            // Reading key bytes and offsets until reaching the byte with number
            // equals to first offset.
            do {
                // Every key ends by </0>.
                while ((b = file.readByte()) != 0) {
                    bytesBuffer.write(b);
                }
                // Every offset is 4-bytes integer.
                offsets.add(file.readInt());
                // Construct string from bytes encoded to specified in Helper
                // class encoding.
                String key = bytesBuffer.toString(Helper.ENCODING);
                bytesBuffer.reset();
                // Key must correspond to file. See checkKey method for more
                // info.
                checkKey(key);
                keys.add(key);
            } while (file.getFilePointer() < offsets.get(0));
            // Last offset is the number of last byte of file + 1.
            offsets.add((int) file.length());
            // We now we are on the byte with number of the first offset. Remove
            // it from list.
            offsets.remove(0);
            // Reading values until reaching the end of file.
            Iterator<String> keyIter = keys.iterator();
            for (int nextOffset : offsets) {
                // Value ends when the next value starts.
                while (file.getFilePointer() < nextOffset) {
                    bytesBuffer.write(file.readByte());
                }
                // Value can't be empty string.
                if (bytesBuffer.size() > 0) {
                    // Encode value bytes. Construct string.
                    String serializedValue = bytesBuffer
                            .toString(Helper.ENCODING);
                    // Value stores in JSON format. Deserialize JSON.
                    Storeable value = table.getManager().deserialize(table,
                            serializedValue);
                    // Put key, value pair to data map. Key can't repeat in
                    // file.
                    if (data.put(keyIter.next(), value) != null) {
                        throw new IllegalArgumentException(
                                "Key repeats in file");
                    }
                    bytesBuffer.reset();
                } else {
                    throw new EOFException();
                }
            }
            bytesBuffer.close();
        } catch (UnsupportedEncodingException e) {
            throw new IOException("Key or value can't be encoded to "
                    + Helper.ENCODING, e);
        } catch (IllegalArgumentException | ParseException e) {
            throw new IOException(e.getMessage(), e);
        } catch (EOFException e) {
            throw new IOException("File breaks unexpectedly", e);
        } catch (IOException e) {
            throw new IOException("Unable read from file", e);
        }
    }

    /**
     * Key places in file with number equals to (key.bytes[0] / NUMBER_OF_FILES)
     * % NUMBER_OF_FILES, and file places in directory with number equals to
     * key.bytes[0] % NUMBER_OF_FILES.
     * 
     * @param key
     *            Key.
     * @throws UnsupportedEncodingException
     *             If key bytes cannot be encode to {@value #ENCODING}.
     * @throws IllegalArgumentException
     *             If key can't be stored in this {@link TablePart}.
     */
    private void checkKey(String key) throws UnsupportedEncodingException {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }
        int expectedDirNumber = Math.abs(key.getBytes(Helper.ENCODING)[0]
                % Helper.NUMBER_OF_PARTITIONS);
        int expectedFileNumber = Math
                .abs((key.getBytes(Helper.ENCODING)[0] / Helper.NUMBER_OF_PARTITIONS)
                        % Helper.NUMBER_OF_PARTITIONS);
        if (directoryNumber != expectedDirNumber
                || fileNumber != expectedFileNumber) {
            throw new IllegalArgumentException("'" + key
                    + "' can't be placed to this file");
        }
    }

    /**
     * Writes all data from memory to file on disk.
     * 
     * @throws IOException
     *             If method can't write to file.
     */
    private void writeToFile() throws IOException {
        // File structure described in readFile() method.
        tablePartDirectoryPath.getParent().toFile().mkdir();
        try (RandomAccessFile file = new RandomAccessFile(
                tablePartDirectoryPath.toString(), "rw")) {
            file.setLength(0);
            Set<String> keys = data.keySet();
            List<Integer> offsetsPos = new LinkedList<>();
            for (String currentKey : keys) {
                file.write(currentKey.getBytes(Helper.ENCODING));
                file.write('\0');
                offsetsPos.add((int) file.getFilePointer());
                file.writeInt(0);
            }
            List<Integer> offsets = new LinkedList<>();
            for (String key : keys) {
                offsets.add((int) file.getFilePointer());
                Storeable value = data.get(key);
                String serializedValue = table.getManager().serialize(table,
                        value);
                file.write(serializedValue.getBytes(Helper.ENCODING));
            }
            Iterator<Integer> offIter = offsets.iterator();
            for (int offsetPos : offsetsPos) {
                file.seek(offsetPos);
                file.writeInt(offIter.next());
            }
        } catch (FileNotFoundException e) {
            throw new IOException("Unable to create file", e);
        } catch (UnsupportedEncodingException e) {
            throw new IOException("Key or value can't be encoded to "
                    + Helper.ENCODING, e);
        } catch (IOException e) {
            throw new IOException("Unable to write to file", e);
        }
    }
}
