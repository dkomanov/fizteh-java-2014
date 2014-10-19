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
    private int fileIndex;
    private int directoryIndex;
    private boolean actuality; // True, if fileMap stores right information.

    public DataFile(Path tablePath, int directoryIndex, int fileIndex) throws IOException, IllegalArgumentException {
        fileMap = new TreeMap<>();
        filePath = makeAbsolutePath(tablePath, directoryIndex, fileIndex);
        this.fileIndex = fileIndex;
        this.directoryIndex = directoryIndex;
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
                if (this.fileIndex != Math.abs((Byte.valueOf(b) / TableManager.magicNumber) % TableManager.magicNumber)
                        || this.directoryIndex != Math.abs(Byte.valueOf(b) % TableManager.magicNumber)) {
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
        if (value == null){
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

    private Path makeAbsolutePath (Path tablePath, int dirIndex, int argFileIndex ){
        return tablePath.resolve(
                Paths.get(Integer.toString(dirIndex) + ".dir",
                        Integer.toString(argFileIndex) + ".dat"));
    }

    private void checkKey(String key) throws UnsupportedEncodingException, IllegalArgumentException {
        if (key == null){
            throw new IllegalArgumentException("Error: key == null");
        }
        if (!(directoryIndex == Math.abs(key.getBytes("UTF-8")[0] % TableManager.magicNumber) &&
                fileIndex == Math.abs((key.getBytes("UTF-8")[0] / TableManager.magicNumber) %
                        TableManager.magicNumber))){
            throw new IllegalArgumentException("Wrong key in file " + filePath.toString());
        }
    }
}
