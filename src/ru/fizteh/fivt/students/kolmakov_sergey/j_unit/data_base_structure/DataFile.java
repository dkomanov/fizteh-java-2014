package ru.fizteh.fivt.students.kolmakov_sergey.j_unit.data_base_structure;

import ru.fizteh.fivt.students.kolmakov_sergey.j_unit.data_base_exceptions.DatabaseCorruptedException;

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
    private Path filePath;
    private Coordinates folderFileIndexes;

    public DataFile(Path tablePath, Coordinates coordinates) throws IOException, DatabaseCorruptedException {
        fileMap = new HashMap<>();
        filePath = makeAbsolutePath(tablePath, coordinates);
        folderFileIndexes = coordinates;
        if (filePath.toFile().exists()) {
            update();
        }
    }

    protected void commit() throws IOException {
        if (fileMap.size() == 0) {
            filePath.toFile().delete();
            filePath.getParent().toFile().delete();
        } else {
            filePath.getParent().toFile().mkdir();
            try (RandomAccessFile file = new RandomAccessFile(filePath.toString(), "rw")) {
                file.setLength(0);
                Set<String> keys = fileMap.keySet();
                List<Integer> reserved = new LinkedList<>();
                List<Integer> offsets = new LinkedList<>();
                for (String currentKey : keys) {
                    file.write(currentKey.getBytes(TableManager.CODE_FORMAT));
                    file.write('\0');
                    reserved.add((int) file.getFilePointer());
                    file.writeInt(0); // Place reserved for offset.
                }
                for (String currentKey : keys) { // Write values.
                    offsets.add((int) file.getFilePointer());
                    file.write(fileMap.get(currentKey).getBytes(TableManager.CODE_FORMAT));
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
        }
    }

    private void update() throws DatabaseCorruptedException, IOException {
        try (RandomAccessFile file = new RandomAccessFile(filePath.toString(), "r")) {
            if (file.length() == 0) {
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
                String key = bytes.toString(TableManager.CODE_FORMAT);
                bytes.reset();
                if (!checkKey(key)){
                    throw new DatabaseCorruptedException("Wrong key found in file " + filePath.toString());
                }
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
                    fileMap.put(keyIterator.next(), bytes.toString(TableManager.CODE_FORMAT));
                    bytes.reset();
                } else {
                    throw new DatabaseCorruptedException("Data corrupted in file " + filePath.toString());
                }
            }
            bytes.close();
        }
    }

    protected String put(String key, String value) {
        if (!checkKey(key)){
            throw new IllegalArgumentException("trying to put unexpected key in file " + filePath.toString());
        }
        if (value == null) {
            throw new IllegalArgumentException("trying to put null value in file " + filePath.toString());
        }
        return fileMap.put(key, value);
    }

    protected String get(String key) {
        if (!checkKey(key)){
            throw new IllegalArgumentException("trying to read unexpected key in file " + filePath.toString());
        }
        return fileMap.get(key);
    }

    protected String remove(String key) {
        if (!checkKey(key)){
            throw new IllegalArgumentException("trying to remove unexpected key from file " + filePath.toString());
        }
        return fileMap.remove(key);
    }

    protected Set<String> list() {
        return fileMap.keySet();
    }

    protected int size() {
        return fileMap.size();
    }

    private Path makeAbsolutePath(Path tablePath, Coordinates folderFileIndexes) {
        return tablePath.resolve(
                Paths.get(Integer.toString(folderFileIndexes.folderIndex) + ".dir",
                        Integer.toString(folderFileIndexes.fileIndex) + ".dat"));
    }

    private boolean checkKey(String key) {
        if (key == null) {
            throw new IllegalArgumentException("checkKey: key == null");
        }
        try {
            if ((folderFileIndexes.folderIndex == Math.abs(key.getBytes(TableManager.CODE_FORMAT)[0]
                    % TableManager.NUMBER_OF_PARTITIONS) && folderFileIndexes.fileIndex
                    == Math.abs((key.getBytes(TableManager.CODE_FORMAT)[0] / TableManager.NUMBER_OF_PARTITIONS)
                    % TableManager.NUMBER_OF_PARTITIONS))) {
                return true;
            } else {
                return false;
            }
        } catch (UnsupportedEncodingException e) {
            return false;
        }
    }
}
