package ru.fizteh.fivt.students.AlexanderKhalyapov.Telnet;

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

final class TablePart {
    private Map<String, Storeable> data = new HashMap<>();
    private boolean changed;
    private DbTable table;
    private Path tablePartDirectoryPath;
    private int fileNumber;
    private int directoryNumber;

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

    public Storeable get(String key) throws UnsupportedEncodingException {
        checkKey(key);
        return data.get(key);
    }

    public Storeable put(String key, Storeable value)
            throws UnsupportedEncodingException {
        checkKey(key);
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        changed = true;
        return data.put(key, value);
    }

    public Storeable remove(String key) throws UnsupportedEncodingException {
        checkKey(key);
        changed = true;
        return data.remove(key);
    }

    public Set<String> list() {
        return data.keySet();
    }

    public int getNumberOfRecords() {
        return data.size();
    }

    private void readFile() throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(
                tablePartDirectoryPath.toString(), "r")) {
            ByteArrayOutputStream bytesBuffer = new ByteArrayOutputStream();
            List<Integer> offsets = new LinkedList<>();
            List<String> keys = new LinkedList<>();
            byte b;
            do {
                while ((b = file.readByte()) != 0) {
                    bytesBuffer.write(b);
                }
                offsets.add(file.readInt());
                String key = bytesBuffer.toString(Helper.ENCODING);
                bytesBuffer.reset();
                checkKey(key);
                keys.add(key);
            } while (file.getFilePointer() < offsets.get(0));
            offsets.add((int) file.length());
            offsets.remove(0);
            Iterator<String> keyIter = keys.iterator();
            for (int nextOffset : offsets) {
                while (file.getFilePointer() < nextOffset) {
                    bytesBuffer.write(file.readByte());
                }
                if (bytesBuffer.size() > 0) {
                    String serializedValue = bytesBuffer
                            .toString(Helper.ENCODING);
                    Storeable value = table.getManager().deserialize(table,
                            serializedValue);
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

    private void writeToFile() throws IOException {
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
