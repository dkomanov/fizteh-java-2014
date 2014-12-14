package ru.fizteh.fivt.students.sautin1.parallel.storeable;

import ru.fizteh.fivt.students.sautin1.parallel.filemap.GeneralTable;
import ru.fizteh.fivt.students.sautin1.parallel.filemap.GeneralTableProvider;
import ru.fizteh.fivt.students.sautin1.parallel.filemap.TableIOTools;
import ru.fizteh.fivt.students.sautin1.parallel.shell.FileUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.text.ParseException;
import java.util.Map;

/**
 * Tools for reading tables of strings from file and writing to file.
 * Created by sautin1 on 10/12/14.
 */
public class TableIOToolsMultipleFiles<MappedValue, T extends GeneralTable<MappedValue>,
        P extends GeneralTableProvider<MappedValue, T>> extends TableIOTools<MappedValue, T, P> {
    private int dirQuantity;
    private int fileQuantity;
    private final String encoding;
    private final String fileExtension;
    private final String dirExtension;

    public TableIOToolsMultipleFiles(int dirQuantity, int fileQuantity, String encoding,
                                     String fileExtension, String dirExtension) {
        this.dirQuantity = dirQuantity;
        this.fileQuantity = fileQuantity;
        this.encoding = encoding;
        this.fileExtension = fileExtension;
        this.dirExtension = dirExtension;
    }

    public TableIOToolsMultipleFiles(int dirQuantity, int fileQuantity, String encoding) {
        this(dirQuantity, fileQuantity, encoding, "dat", "dir");
    }

    public TableIOToolsMultipleFiles() {
        this(16, 16, "UTF-8");
    }

    /**
     * Reads string from the file.
     * @param inStream - input stream connected with the file.
     * @return string read from file.
     * @throws java.io.IOException if any IO error occurs.
     */
    protected String readEncodedString(DataInputStream inStream) throws IOException {
        String string = null;
        int stringSize;
        try {
            stringSize = inStream.readInt();
        } catch (IOException e) {
            throw new IOException("File is corrupted");
        }
        byte[] stringByteBuf = new byte[stringSize];
        int bytesRead;
        try {
            bytesRead = inStream.read(stringByteBuf, 0, stringSize);
        } catch (IOException e) {
            throw new IOException("Read error: " + e.getMessage());
        }
        if (bytesRead < stringSize) {
            throw new IOException("File is corrupted");
        }
        string = new String(stringByteBuf, encoding);
        return string;
    }

    /**
     *
     * Writes string to the file.
     * @param outStream - output stream connected with the file.
     * @param string - string to write to file.
     * @throws java.io.IOException if any IO error occurs.
     */
    protected void writeEncodedString(DataOutputStream outStream, String string) throws IOException {
        try {
            byte[] stringBytes = string.getBytes(encoding);
            outStream.writeInt(stringBytes.length);
            outStream.write(stringBytes);
        } catch (IOException e) {
            throw new IOException("Write error: " + e.getMessage());
        }
    }

    /**
     * Generates number of the file by the key.
     * @param key - key.
     * @return number of the file.
     */
    protected int generateFileNumber(String key) {
        return Math.abs(key.hashCode()) / dirQuantity % fileQuantity;
    }

    /**
     * Generates number of the dir by the key.
     * @param key - key.
     * @return number of the dir.
     */
    protected int generateDirNumber(String key) {
        return Math.abs(key.hashCode()) % dirQuantity;
    }

    /**
     * Returns an integer, stored in the name of the file by path filePath.
     * @param filePath - path to the file.
     * @return number, encoded in file name.
     */
    protected int fileNameToNumber(Path filePath) {
        String[] tokens = filePath.getFileName().toString().split("\\.");
        return Integer.parseInt(tokens[0]);
    }

    /**
     * Checks whether key is in the right directory and file.
     * @param key - checked test.
     * @param filePath - path to the file, where the key was stored.
     * @return true, if this path is correct for given key; false - otherwise.
     */
    protected boolean checkKeyPlace(String key, Path filePath) {
        boolean rightPlace = (fileNameToNumber(filePath) == generateFileNumber(key));
        rightPlace = rightPlace && (fileNameToNumber(filePath.getParent()) == generateDirNumber(key));
        return rightPlace;
    }

    /**
     * Loads one file to table. Name of the file is correct.
     * @param filePath - path to the file to read from.
     * @param table - table to be written to.
     * @throws java.io.IOException if any IO error occurs.
     */
    public int loadFileToTable(P provider, Path filePath, T table) throws IOException, ParseException {
        int loadedEntriesNumber = 0;
        try (DataInputStream inStream = new DataInputStream(Files.newInputStream(filePath))) {
            while (inStream.available() > 0) {
                String key = readEncodedString(inStream);
                if (!checkKeyPlace(key, filePath)) {
                    String wrongPathString = filePath.getParent().getFileName().toString();
                    wrongPathString += "/" + filePath.getFileName().toString();
                    throw new IOException("Wrong path " + wrongPathString + " for key " + key);
                }
                String value = readEncodedString(inStream);
                if (value == null) {
                    String exceptionMessage = "File " + filePath.getFileName().toString() + " is corrupted";
                    throw new IOException(exceptionMessage);
                }
                table.put(key, provider.deserialize(table, value));
                ++loadedEntriesNumber;
            }
        }
        return loadedEntriesNumber;
    }

    /**
     * Loads all files from directory XX.[dirExtension] to table.
     * @param dirPath - path to the directory to read from.
     * @param table - table to be written to.
     * @throws java.io.IOException if any IO error occurs.
     */
    public int loadDirectoryToTable(P provider, Path dirPath, T table) throws IOException, ParseException {
        int loadedEntriesNumber = 0;
        for (int fileIndex = 0; fileIndex < fileQuantity; ++fileIndex) {
            String fileName = "" + fileIndex + "." + fileExtension;
            Path filePath = dirPath.resolve(fileName);
            if (!Files.exists(filePath)) {
                continue;
            }
            if (Files.isDirectory(filePath)) {
                throw new DirectoryNotEmptyException(fileName + " is a directory");
            }
            int loadedFromFile = loadFileToTable(provider, filePath, table);
            if (loadedFromFile == 0) {
                throw new IOException("Database is corrupted: " + filePath.getFileName().toString() + " is empty");
            } else {
                loadedEntriesNumber += loadedFromFile;
            }
        }
        return loadedEntriesNumber;
    }

    /**
     * Read the whole table of strings from the file.
     * @param rootPath - path to the root directory.
     * @param tableName - name of the source table of strings.  @return filled table.
     * @throws java.io.IOException if any IO error occurs.
     */
    @Override
    public T readTable(P provider, Path rootPath, String tableName, Object[] args) throws IOException, ParseException {
        T table = provider.establishTable(tableName, args);
        if (fileQuantity == 0 || dirQuantity == 0) {
            return table;
        }
        Path tablePath = rootPath.resolve(table.getName());
        if (Files.exists(tablePath) && !Files.isDirectory(tablePath)) {
            throw new FileAlreadyExistsException("File " + table.getName() + "  exists");
        }
        if (!Files.exists(tablePath)) {
            throw new FileNotFoundException("No table with such name");
        }
        int totalEntriesLoaded = 0;
        for (int dirIndex = 0; dirIndex < dirQuantity; ++dirIndex) {
            String dirName = "" + dirIndex + "." + dirExtension;
            Path dirPath = tablePath.resolve(dirName);
            if (!Files.exists(dirPath)) {
                continue;
            }
            if (!Files.isDirectory(dirPath)) {
                throw new NotDirectoryException(dirName + " is not a directory");
            }
            int loadedEntriesNumber = loadDirectoryToTable(provider, dirPath, table);
            totalEntriesLoaded += loadedEntriesNumber;
            if (loadedEntriesNumber == 0) {
                throw new IOException("Database is corrupted: " + dirPath.getFileName().toString() + " is empty");
            }
        }
        if (totalEntriesLoaded == 0) {
            throw new IOException("Database is corrupted: " + tablePath.getFileName().toString() + " is empty");
        }
        return table;
    }

    /**
     * Write the whole table of strings to the file.
     * @param rootPath - path to the root directory.
     * @param table - string table to write.
     */
    @Override
    public void writeTable(P provider, Path rootPath, T table) throws IOException {
        if (fileQuantity == 0 || dirQuantity == 0) {
            return;
        }
        Path tablePath = rootPath.resolve(table.getName());
        if (!Files.exists(tablePath)) {
            Files.createDirectory(tablePath);
        }
        try {
            FileUtils.clearDirectory(tablePath);
        } catch (FileNotFoundException e) {
            throw new IOException("Cannot create directory " + e.getMessage());
        }

        for (Map.Entry<String, MappedValue> entry : table) {
            String key = entry.getKey();
            MappedValue value = entry.getValue();
            int dirNumber  = generateDirNumber(key);
            int fileNumber = generateFileNumber(key);
            String fileName = "" + fileNumber + "." + fileExtension;
            String dirName  = "" + dirNumber  + "." + dirExtension;
            Path dirPath = tablePath.resolve(dirName);
            if (Files.exists(dirPath) && !Files.isDirectory(dirPath)) {
                throw new FileAlreadyExistsException("File " + dirName + "  exists");
            }
            if (!Files.exists(dirPath)) {
                Files.createDirectory(dirPath);
            }
            Path filePath = dirPath.resolve(fileName);
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
            try (DataOutputStream outStream = new DataOutputStream(Files.newOutputStream(filePath))) {
                writeEncodedString(outStream, key);
                String serializedString;
                try {
                    serializedString = provider.serialize(table, value);
                } catch (Exception e) {
                    throw new IOException(e.getMessage());
                }
                writeEncodedString(outStream, serializedString);
            }
        }
        if (table.size() == 0) {
            FileUtils.removeDirectory(tablePath);
        }
    }


}
