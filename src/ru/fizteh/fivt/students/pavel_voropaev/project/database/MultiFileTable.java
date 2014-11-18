package ru.fizteh.fivt.students.pavel_voropaev.project.database;

import ru.fizteh.fivt.students.pavel_voropaev.project.Utils;
import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.ContainsWrongFilesException;
import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.NullArgumentException;
import ru.fizteh.fivt.students.pavel_voropaev.project.master.Table;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;

public class MultiFileTable implements Table {

    class MultiFileMap {
        public Map<String, String> map;

        public MultiFileMap() {
            map = new HashMap<>();
        }
    }

    private static final int FOLDERS = 16;
    private static final int FILES = 16;
    private static final String ENCODING = "UTF-8";

    private String name;
    private Path directory;
    private int size = 0;
    private MultiFileMap[] content;
    private Map<String, String> diff = new HashMap<>();

    public MultiFileTable(Path databaseDirectory, String tableName) throws IOException {
        name = tableName;
        if (!Files.exists(databaseDirectory)) {
            throw new RuntimeException(databaseDirectory.toString() + ": database doesn't exist");
        }
        directory = databaseDirectory.resolve(tableName);
        content = new MultiFileMap[FOLDERS * FILES];
        for (int i = 0; i < FOLDERS * FILES; ++i) {
            content[i] = new MultiFileMap();
        }

        if (!Files.exists(directory)) {
            Files.createDirectory(directory);
        } else {
            load();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String get(String key) {
        if (key == null) {
            throw new NullArgumentException("get");
        }
        if (diff.containsKey(key)) {
            return diff.get(key);
        }
        return content[getPlace(key)].map.get(key);
    }

    @Override
    public String put(String key, String value) {
        if (key == null || value == null) {
            throw new NullArgumentException("put");
        }

        String oldValue;
        if (!diff.containsKey(key) && content[getPlace(key)].map.containsKey(key)) {
            oldValue = content[getPlace(key)].map.get(key);
            if (oldValue.equals(value)) {
                return oldValue;
            }
            diff.put(key, value);
            return oldValue;
        }

        oldValue = diff.put(key, value);
        if (oldValue == null) {
            ++size;
        }
        return oldValue;

    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new NullArgumentException("remove");
        }

        String oldValue;
        if (!diff.containsKey(key)) {
            if (!content[getPlace(key)].map.containsKey(key)) { // No such key.
                return null;
            } else { // Key is saved on disk.
                oldValue = content[getPlace(key)].map.get(key);
                diff.put(key, null);
            }
        } else { // Some unsaved changes with this key.
            if (!content[getPlace(key)].map.containsKey(key)) {
                oldValue = diff.remove(key);
            } else {
                oldValue = diff.put(key, null);
            }
        }

        if (oldValue != null) {
            --size;
        }
        return oldValue;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int commit() {
        for (Entry<String, String> entry : diff.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (value == null) {
                content[getPlace(key)].map.remove(key);
            } else {
                content[getPlace(key)].map.put(key, value);
            }
        }

        int retVal = diff.size();
        diff.clear();

        try {
            save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return retVal;
    }

    @Override
    public int rollback() {
        for (Entry<String, String> entry : diff.entrySet()) {
            if (content[getPlace(entry.getKey())].map.containsKey(entry.getKey())) {
                if (entry.getValue() == null) {
                    ++size;
                }
            } else {
                --size;
            }
        }

        int retVal = diff.size();
        diff.clear();
        return retVal;
    }

    @Override
    public List<String> list() {
        Set<String> keySet = new HashSet<>();
        for (MultiFileMap pair : content) {
            keySet.addAll(pair.map.keySet());
        }
        for (Entry<String, String> pair : diff.entrySet()) {
            if (pair.getValue() == null) {
                keySet.remove(pair.getKey());
            } else {
                keySet.add(pair.getKey());
            }
        }
        List<String> list = new LinkedList<>();
        list.addAll(keySet);
        return list;
    }

    @Override
    public List<String> getDiff() {
        List<String> list = new LinkedList<>();
        list.addAll(diff.keySet());
        return list;
    }

    private void load() throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path entry : stream) {
                if (!Files.isDirectory(entry)) {
                    throw new ContainsWrongFilesException(directory.toString());
                }
                boolean correctDir = false;
                for (int i = 0; i < FOLDERS; ++i) {
                    if (entry.endsWith(Integer.toString(i) + ".dir")) {
                        readDir(entry, i);
                        correctDir = true;
                        break;
                    }
                }
                if (!correctDir) {
                    throw new ContainsWrongFilesException(directory.toString());
                }
            }
        }
    }

    private void readDir(Path entry, int dirNum) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(entry)) {
            for (Path path : stream) {
                boolean correctFile = false;
                for (int i = 0; i < FILES; ++i) {
                    if (path.endsWith(Integer.toString(i) + ".dat")
                            && Files.isRegularFile(path)) {
                        readFile(path.toString(), dirNum, i);
                        correctFile = true;
                        break;
                    }
                }
                if (!correctFile) {
                    throw new ContainsWrongFilesException(directory.toString());
                }
            }
        }
    }

    private void readFile(String fileName, int dirNum, int fileNum) throws IOException {
        try (DataInputStream stream = new DataInputStream(new FileInputStream(fileName))) {
            while (true) {
                String key = readWord(stream);
                String value = readWord(stream);
                int hashCode = Math.abs(key.hashCode());
                if (hashCode % FOLDERS != dirNum || hashCode / FOLDERS % FILES != fileNum) {
                    throw new ContainsWrongFilesException(directory.toString());
                }

                content[dirNum * FILES + fileNum].map.put(key, value);
                ++size;
            }
        } catch (EOFException e) {
            // File is read.
        }
    }

    private String readWord(DataInputStream stream) throws IOException {
        int length = stream.readInt();
        try {
            byte[] word = new byte[length];
            stream.readFully(word);
            return new String(word, ENCODING);
        } catch (OutOfMemoryError e) {
            throw new ContainsWrongFilesException(directory.toString());
        }
    }

    private void save() throws IOException {
        Utils.rm(directory);
        Files.createDirectory(directory);
        for (int i = 0; i < FOLDERS * FILES; ++i) {
            if (content[i].map.size() > 0) {
                Path dir = getDirectoryPath(i);
                if (!Files.exists(dir)) {
                    Files.createDirectory(dir);
                }
                writeFile(i);
            }
        }
    }

    private void writeFile(int fileNum) throws IOException {
        Path filePath = getFilePath(fileNum);
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }

        Set<String> keyList = content[fileNum].map.keySet();
        ByteBuffer buffer = ByteBuffer.allocate(4);
        Iterator<String> it = keyList.iterator();
        try (FileOutputStream output = new FileOutputStream(getFilePath(fileNum).toString())) {
            while (it.hasNext()) {
                String key = it.next();
                byte[] keyByte = key.getBytes(ENCODING);
                byte[] valueByte = content[fileNum].map.get(key).getBytes(ENCODING);

                output.write(buffer.putInt(0, keyByte.length).array());
                output.write(keyByte);

                output.write(buffer.putInt(0, valueByte.length).array());
                output.write(valueByte);
            }
        } catch (Exception e) {
            throw new IOException("Cannot write into a file: " + getFilePath(fileNum).toString(),
                    e);
        }
    }

    private int getPlace(String key) {
        int hashCode = Math.abs(key.hashCode());
        return hashCode % FOLDERS * FILES + hashCode / FOLDERS % FILES;
    }

    private Path getDirectoryPath(int fileNumber) {
        String directoryName = new StringBuilder().append(fileNumber / FOLDERS).append(".dir")
                .toString();
        return directory.resolve(directoryName);
    }

    private Path getFilePath(int fileNumber) {
        String fileName = new StringBuilder().append(fileNumber % FOLDERS).append(".dat")
                .toString();
        return getDirectoryPath(fileNumber).resolve(fileName);
    }
}
