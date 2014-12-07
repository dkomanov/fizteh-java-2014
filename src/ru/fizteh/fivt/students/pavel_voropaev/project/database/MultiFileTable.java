package ru.fizteh.fivt.students.pavel_voropaev.project.database;

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

    private class MultiFileMap {
        public Map<String, String> map;

        public MultiFileMap() {
            map = new HashMap<>();
        }
    }

    private static final int FOLDERS = 16;
    private static final String FOLDERS_REGEXP = "(1[0-5]|[0-9])\\.dir";
    private static final int FILES = 16;
    private static final String FILES_REGEXP = "(1[0-5]|[0-9])\\.dat";
    private static final String ENCODING = "UTF-8";

    private String name;
    private Path directory;
    private int size;
    private MultiFileMap[] content;
    private Map<String, String> diff;

    public MultiFileTable(Path databaseDirectory, String tableName) throws IOException {
        name = tableName;
        if (!Files.exists(databaseDirectory)) {
            throw new RuntimeException(databaseDirectory.toString() + ": database doesn't exist");
        }
        size = 0;
        directory = databaseDirectory.resolve(tableName);
        content = new MultiFileMap[FOLDERS * FILES];
        for (int i = 0; i < FOLDERS * FILES; ++i) {
            content[i] = new MultiFileMap();
        }

        if (!Files.exists(directory)) {
            try {
                Files.createDirectory(directory);
            } catch (IOException | SecurityException e) {
                throw new IOException("Cannot create " + directory.getFileName());
            }

        } else {
            load();
        }

        diff = new HashMap<>();
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

        int place = getPlace(key);
        String oldValue;
        if (!diff.containsKey(key) && content[place].map.containsKey(key)) {
            oldValue = content[place].map.get(key);
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

        int place = getPlace(key);
        String oldValue;
        if (!diff.containsKey(key)) {
            if (!content[place].map.containsKey(key)) { // No such key.
                return null;
            } else { // Key is saved on disk.
                oldValue = content[place].map.get(key);
                diff.put(key, null);
            }
        } else { // Some unsaved changes with this key.
            if (!content[place].map.containsKey(key)) {
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
    public int commit() throws IOException {
        Set<Integer> changedFiles = new HashSet<>();
        for (Entry<String, String> entry : diff.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            changedFiles.add(getPlace(key));
            int place = getPlace(key);

            if (value == null) {
                content[place].map.remove(key);
            } else {
                content[place].map.put(key, value);
            }
        }

        try {
            save(changedFiles);
        } catch (SecurityException e) {
            throw new IOException(e);
        }

        int retVal = diff.size();
        diff.clear();
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
    public int getNumberOfUncommittedChanges() {
        return diff.size();
    }

    private void load() throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path entry : stream) {
                String directoryName = entry.getFileName().toString();
                if (directoryName.matches(FOLDERS_REGEXP) && Files.isDirectory(entry)) {
                    int fileNumber = Integer.parseInt(directoryName.substring(0, directoryName.indexOf('.')));
                    if (fileNumber < FOLDERS) {
                        readDir(entry, fileNumber);
                    } else {
                        throw new ContainsWrongFilesException(directory.toString());
                    }
                } else {
                    throw new ContainsWrongFilesException(directory.toString());
                }
            }
        }
    }

    private void readDir(Path entry, int dirNum) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(entry)) {
            for (Path path : stream) {
                String fileName = entry.getFileName().toString();
                if (fileName.matches(FILES_REGEXP) && Files.isRegularFile(path)) {
                    int fileNumber = Integer.parseInt(fileName.substring(0, fileName.indexOf('.')));
                    if (fileNumber < FILES) {
                        readFile(path.toString(), dirNum, fileNumber);
                    } else {
                        throw new ContainsWrongFilesException(directory.toString());
                    }
                } else {
                    throw new ContainsWrongFilesException(directory.toString());
                }
            }
        }
    }

    private void readFile(String fileName, int dirNum, int fileNum) throws IOException {
        try (RandomAccessFile stream = new RandomAccessFile(new File(fileName), "r")) {
            while (stream.getFilePointer() < stream.length()) {
                String key = readWord(stream);
                String value = readWord(stream);
                int hashCode = Math.abs(key.hashCode());
                if (hashCode % FOLDERS != dirNum || hashCode / FOLDERS % FILES != fileNum) {
                    throw new ContainsWrongFilesException(directory.toString());
                }

                if (content[dirNum * FILES + fileNum].map.put(key, value) != null) { // The same key. Twice.
                    throw new ContainsWrongFilesException(directory.toString());
                }
                ++size;
            }
        } catch (EOFException e) {
            throw new ContainsWrongFilesException(directory.toString());
        }
    }

    private String readWord(RandomAccessFile stream) throws IOException {
        int length = stream.readInt();
        try {
            byte[] word = new byte[length];
            stream.readFully(word);
            return new String(word, ENCODING);
        } catch (OutOfMemoryError | NegativeArraySizeException e) {
            throw new ContainsWrongFilesException(directory.toString());
        }
    }

    private void save(Set<Integer> changedFiles) throws IOException {
        for (int number : changedFiles) {
            Path subdirectoryPath = getDirectoryPath(number);
            if (!Files.exists(subdirectoryPath)) {
                Files.createDirectory(subdirectoryPath);
            }

            if (content[number].map.size() > 0) {
                writeFile(number);
            } else {
                if (Files.exists(getFilePath(number))) {
                    Files.delete(getFilePath(number));
                }
            }
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path entry : stream) {
                if (entry.toFile().list().length == 0) {
                    Files.delete(entry);
                }
            }
        }
    }

    private void writeFile(int fileNumber) throws IOException {
        Path filePath = getFilePath(fileNumber);
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }

        Set<String> keyList = content[fileNumber].map.keySet();
        try (FileOutputStream output = new FileOutputStream(filePath.toString())) {
            for (String entry : keyList) {
                byte[] keyByte = entry.getBytes(ENCODING);
                byte[] valueByte = content[fileNumber].map.get(entry).getBytes(ENCODING);

                writeBytes(output, keyByte);
                writeBytes(output, valueByte);
            }
        } catch (Exception e) {
            throw new IOException("Cannot write into a file: " + filePath.toString());
        }
    }

    private void writeBytes(FileOutputStream output, byte[] bytes) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4);

        output.write(buffer.putInt(0, bytes.length).array());
        output.write(bytes);
    }

    private int getPlace(String key) {
        int hashCode = Math.abs(key.hashCode());
        return hashCode % FOLDERS * FILES + hashCode / FOLDERS % FILES;
    }

    private Path getDirectoryPath(int fileNumber) {
        return directory.resolve(String.valueOf(fileNumber / FOLDERS) + ".dir");
    }

    private Path getFilePath(int fileNumber) {
        return getDirectoryPath(fileNumber).resolve(String.valueOf(fileNumber % FOLDERS) + ".dat");
    }
}
