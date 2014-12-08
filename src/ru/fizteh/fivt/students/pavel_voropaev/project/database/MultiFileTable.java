package ru.fizteh.fivt.students.pavel_voropaev.project.database;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.ContainsWrongFilesException;
import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.NullArgumentException;
import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.TableDoesNotExistException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;

public class MultiFileTable implements Table {
    private static final int FOLDERS = 16;
    private static final String FOLDERS_REGEXP = "(1[0-5]|[0-9])\\.dir";
    private static final int FILES = 16;
    private static final String FILES_REGEXP = "(1[0-5]|[0-9])\\.dat";
    private static final String ENCODING = "UTF-8";

    private Map<String, Storeable>[] content;
    private Map<String, Storeable> diff;
    private Path directory;
    private String name;
    private TableProvider parent;
    private int size;
    List<Class<?>> signature;
    private boolean wasRemoved;

    public MultiFileTable(Path directory, String name, TableProvider parent) throws IOException {
        if (!Files.exists(directory)) {
            throw new RuntimeException(directory.toString() + ": database doesn't exist");
        }

        content = new Map[FOLDERS * FILES];
        for (int i = 0; i < FOLDERS * FILES; ++i) {
            content[i] = new HashMap<>();
        }
        diff = new HashMap<>();
        this.directory = directory.resolve(name);
        this.name = name;
        this.parent = parent;
        size = 0;
        signature = new ArrayList<>();
        loadSignature();
        wasRemoved = false;

        if (!Files.exists(directory)) {
            throw new IOException("Cannot read " + directory.getFileName());
        }

        load();
    }

    @Override
    public String getName() {
        if (wasRemoved) {
            throw new TableDoesNotExistException(name);
        }
        return name;
    }

    @Override
    public Storeable get(String key) {
        if (wasRemoved) {
            throw new TableDoesNotExistException(name);
        }
        if (key == null) {
            throw new NullArgumentException("get");
        }

        if (diff.containsKey(key)) {
            return diff.get(key);
        }
        return content[getPlace(key)].get(key);
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (wasRemoved) {
            throw new TableDoesNotExistException(name);
        }
        if (key == null || value == null) {
            throw new NullArgumentException("put");
        }

        checkStoreable(value);
        int place = getPlace(key);
        Storeable oldValue;
        if (!diff.containsKey(key) && content[place].containsKey(key)) {
            oldValue = content[place].get(key);
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
    public Storeable remove(String key) {
        if (wasRemoved) {
            throw new TableDoesNotExistException(name);
        }
        if (key == null) {
            throw new NullArgumentException("remove");
        }

        int place = getPlace(key);
        Storeable oldValue;
        if (!diff.containsKey(key)) {
            if (!content[place].containsKey(key)) { // No such key.
                return null;
            } else { // Key is saved on disk.
                oldValue = content[place].get(key);
                diff.put(key, null);
            }
        } else { // Some unsaved changes with this key.
            if (!content[place].containsKey(key)) {
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
        if (wasRemoved) {
            throw new TableDoesNotExistException(name);
        }
        return size;
    }

    @Override
    public int commit() throws IOException {
        if (wasRemoved) {
            throw new TableDoesNotExistException(name);
        }
        Set<Integer> changedFiles = new HashSet<>();
        for (Entry<String, Storeable> entry : diff.entrySet()) {
            String key = entry.getKey();
            Storeable value = entry.getValue();
            changedFiles.add(getPlace(key));
            int place = getPlace(key);

            if (value == null) {
                content[place].remove(key);
            } else {
                content[place].put(key, value);
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
        if (wasRemoved) {
            throw new TableDoesNotExistException(name);
        }
        for (Entry<String, Storeable> entry : diff.entrySet()) {
            if (content[getPlace(entry.getKey())].containsKey(entry.getKey())) {
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
    public int getColumnsCount() {
        if (wasRemoved) {
            throw new TableDoesNotExistException(name);
        }
        return signature.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (wasRemoved) {
            throw new TableDoesNotExistException(name);
        }
        return signature.get(columnIndex);
    }

    @Override
    public List<String> list() {
        if (wasRemoved) {
            throw new TableDoesNotExistException(name);
        }
        Set<String> keySet = new HashSet<>();
        for (Map pair : content) {
            keySet.addAll(pair.keySet());
        }
        for (Entry<String, Storeable> pair : diff.entrySet()) {
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
        if (wasRemoved) {
            throw new TableDoesNotExistException(name);
        }
        return diff.size();
    }

    protected void destroy() {
        wasRemoved = true;
    }

    private void load() throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path entry : stream) {
                String directoryName = entry.getFileName().toString();
                if (directoryName.equals("signature.tsv") && Files.isRegularFile(entry)) {
                    continue;
                }
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
                String fileName = path.getFileName().toString();
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
                Storeable deserializedValue = parent.deserialize(this, value);

                int hashCode = Math.abs(key.hashCode());
                if (hashCode % FOLDERS != dirNum || hashCode / FOLDERS % FILES != fileNum) {
                    throw new ContainsWrongFilesException(directory.toString());
                }

                if (content[dirNum * FILES + fileNum].put(key, deserializedValue) != null) { //The same key.
                    throw new ContainsWrongFilesException(directory.toString());
                }
                ++size;
            }
        } catch (ParseException | EOFException e) {
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

            if (content[number].size() > 0) {
                writeFile(number);
            } else {
                if (Files.exists(getFilePath(number))) {
                    Files.delete(getFilePath(number));
                }
            }
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry) && entry.toFile().list().length == 0) {
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

        Set<String> keyList = content[fileNumber].keySet();
        try (FileOutputStream output = new FileOutputStream(filePath.toString())) {
            for (String entry : keyList) {
                byte[] keyByte = entry.getBytes(ENCODING);
                Storeable deserializedValue = content[fileNumber].get(entry);
                byte[] valueByte = parent.serialize(this, deserializedValue).getBytes(ENCODING);

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

    private void loadSignature() throws IOException {
        Path signaturePath = directory.resolve(Serializer.SIGNATURE_FILE_NAME);
        if (!Files.isRegularFile(signaturePath)) {
            throw new IOException("Wrong signature file");
        }
        try (Scanner scanner = new Scanner(signaturePath)) {
            String[] types = scanner.nextLine().split("\\s+");
            for (String entry : types) {
                Class<?> type = Serializer.SUPPORTED_TYPES.get(entry);
                if (type == null) {
                    throw new IOException("Unsupported type " + entry + " in " + signaturePath);
                }
                signature.add(type);
            }
        } catch (IOException | NoSuchElementException e) {
            throw new IOException("Unable to read signature " + signaturePath, e);
        }
    }

    private void checkStoreable(Storeable value) throws ColumnFormatException {
        int i = 0;
        try {
            for (i = 0; i < signature.size(); ++i) {
                Object column = value.getColumnAt(i);
                if (column != null && signature.get(i) != column.getClass()) {
                    throw new ColumnFormatException("Expected: " + signature.get(i) + ", found: " + column.getClass());
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new ColumnFormatException("Expected: " + signature.get(i) + ", found: <null>");
        }
    }

}
