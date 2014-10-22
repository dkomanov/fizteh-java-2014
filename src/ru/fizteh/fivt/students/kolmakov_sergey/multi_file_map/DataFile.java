package ru.fizteh.fivt.students.kolmakov_sergey.multi_file_map;

import java.util.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class DataFile { // Interacts with .dat file.
    private Map<String, String> fileMap;
    private int size;
    private Path filePath;
    private Coordinates folderFileIndexes;
    private boolean actuality; // True, if fileMap stores right information.

    public DataFile(Path tablePath, Coordinates folderFileIndexes) throws IOException, IllegalArgumentException {
        fileMap = new TreeMap<>();
        filePath = makeAbsolutePath(tablePath, folderFileIndexes);
        this.folderFileIndexes = folderFileIndexes;
        actuality = false;
        // Now get size.
        size = 0;
        if (!filePath.toFile().exists()) {
            filePath.getParent().toFile().mkdir();
            filePath.toFile().createNewFile();
            return; // If new, size = 0.
        }
        // If not empty, count number of records.
        try (RandomAccessFile dataBaseFile = new RandomAccessFile(filePath.toString(), "r")) {
            int counter = 0;
            int firstOffset = -1;
            int lastOffset;
            byte b;
            do {
                // First byte check.
                b = dataBaseFile.readByte();
                ++counter;
                if (folderFileIndexes.fileIndex != Math.abs((Byte.valueOf(b) / TableManager.MAGIC_NUMBER) % TableManager.MAGIC_NUMBER)
                        || folderFileIndexes.folderIndex != Math.abs(Byte.valueOf(b) % TableManager.MAGIC_NUMBER)) {
                    throw new IllegalArgumentException("Unexpected key in file " + filePath.toString());
                }
                // Get others bytes of key.
                do {
                    ++counter;
                } while (dataBaseFile.readByte() != 0);
                lastOffset = dataBaseFile.readInt(); // LastOffset == max(offsets) due to writing operation.
                counter += 4;
                if (firstOffset == -1) {
                    firstOffset = lastOffset; // FirstOffset == min(offsets) due to writing operation.
                }
                size++;
            } while (counter < firstOffset);
            if (lastOffset >= dataBaseFile.length()) {
                throw new IllegalArgumentException("Unexpected end of file in " + filePath.toString());
            }
        }
    }

    protected void putData() throws IOException {
        if (size == 0) {
            filePath.toFile().delete();
            filePath.getParent().toFile().delete();
        } else if (actuality) {
            filePath.getParent().toFile().mkdir();
            try (RandomAccessFile file = new RandomAccessFile(filePath.toString(), "rw")) {
                file.setLength(0);
                Set<String> keys = fileMap.keySet();
                List<Integer> reserved = new LinkedList<>();
                List<Integer> offsets = new LinkedList<>();
                for (String currentKey : keys) {
                    file.write(currentKey.getBytes("UTF-8"));
                    file.write('\0');
                    reserved.add((int) file.getFilePointer());
                    file.writeInt(0); // Place reserved for offset.
                }
                for (String currentKey : keys) { // Write values.
                    offsets.add((int) file.getFilePointer());
                    file.write(fileMap.get(currentKey).getBytes("UTF-8"));
                }
                Iterator<Integer> offsetIterator = offsets.iterator();
                for (int offsetPos : reserved) { // Write offsets in reserved places.
                    file.seek(offsetPos);
                    file.writeInt(offsetIterator.next());
                }
            } catch (FileNotFoundException e) {
                throw new IOException("File not found: " + filePath.toString());
            } catch (UnsupportedEncodingException e) {
                throw new IOException("Can't encode file: " + filePath.toString());
            }
            fileMap.clear();
            actuality = false;
        }
    }

    private void getData() throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(filePath.toString(), "r")) {
            if (file.length() == 0) {
                actuality = true;
                return;
            }
            List<String> keys = new LinkedList<>();
            List<Integer> offsets = new LinkedList<>();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            byte b;
            int counter = 0;
            do { // Read keys.
                while ((b = file.readByte()) != 0) {
                    counter++;
                    bytes.write(b);
                }
                ++counter;
                offsets.add(file.readInt());
                counter += 4;
                String key = bytes.toString("UTF-8");
                bytes.reset();
                checkKey(key);
                keys.add(key);
            } while (counter < offsets.get(0));

            offsets.add((int) file.length());
            offsets.remove(0); // It's current position in file, we don't need it in list.
            Iterator<String> keyIterator = keys.iterator();
            for (int nextOffset : offsets) { // Read values.
                while (counter < nextOffset) {
                    bytes.write(file.readByte());
                    counter++;
                }
                if (bytes.size() > 0) {
                    fileMap.put(keyIterator.next(), bytes.toString("UTF-8"));
                    bytes.reset();
                } else {
                    throw new IOException("Data corrupted in file " + filePath.toString());
                }
            }
            bytes.close();
        } catch (FileNotFoundException e) {
            throw new IOException("File not found: " + filePath.toString());
        } catch (UnsupportedEncodingException e) {
            throw new IOException("Can't encode file: " + filePath.toString());
        }
        actuality = true;
    }

    protected String putValue(String key, String value) throws IOException {
        checkKey(key);
        if (value == null) {
            throw new IllegalArgumentException("Error: value == null");
        }
        if (!actuality) {
            getData();
        }
        String oldValue = fileMap.put(key, value);
        if (oldValue == null) {
            ++size;
        }
        return oldValue;
    }

    protected String getValue(String key) throws IOException {
        checkKey(key);
        if (!actuality) {
            getData();
        }
        return fileMap.get(key);
    }

    protected String remove(String key) throws IOException {
        checkKey(key);
        if (!actuality) {
            getData();
        }
        String oldValue = fileMap.remove(key);
        if (oldValue != null) {
            size--;
        }
        return oldValue;
    }

    protected Set<String> list() throws IOException {
        if (!actuality) {
            getData();
        }
        return fileMap.keySet();
    }

    protected int getSize() {
        return size;
    }

    private Path makeAbsolutePath(Path tablePath, Coordinates folderFileIndexes) {
        return tablePath.resolve(
                Paths.get(Integer.toString(folderFileIndexes.folderIndex) + ".dir",
                        Integer.toString(folderFileIndexes.fileIndex) + ".dat"));
    }

    private void checkKey(String key) throws UnsupportedEncodingException, IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("Error: key == null");
        }
        if (!(folderFileIndexes.folderIndex == Math.abs(key.getBytes("UTF-8")[0] % TableManager.MAGIC_NUMBER)
                && folderFileIndexes.fileIndex == Math.abs((key.getBytes("UTF-8")[0] / TableManager.MAGIC_NUMBER)
                % TableManager.MAGIC_NUMBER))) {
            throw new IllegalArgumentException("Wrong key in file " + filePath.toString());
        }
    }
}
