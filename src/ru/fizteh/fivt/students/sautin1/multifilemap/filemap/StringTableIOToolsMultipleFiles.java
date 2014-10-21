package ru.fizteh.fivt.students.sautin1.multifilemap.filemap;

import ru.fizteh.fivt.students.sautin1.multifilemap.shell.FileUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.util.Map;

/**
 * Tools for reading tables of strings from file and writing to file.
 * Created by sautin1 on 10/12/14.
 */
public class StringTableIOToolsMultipleFiles implements TableIOTools<String, StringTable> {
    private int dirQuantity;
    private int fileQuantity;
    private final String encoding;
    private final String fileExtension;
    private final String dirExtension;

    public StringTableIOToolsMultipleFiles(int dirQuantity, int fileQuantity, String encoding,
                                           String fileExtension, String dirExtension) {
        this.dirQuantity = dirQuantity;
        this.fileQuantity = fileQuantity;
        this.encoding = encoding;
        this.fileExtension = fileExtension;
        this.dirExtension = dirExtension;
    }

    public StringTableIOToolsMultipleFiles(int dirQuantity, int fileQuantity, String encoding) {
        this(dirQuantity, fileQuantity, encoding, "dat", "dir");
    }

    /**
     * Converts integer number to byte array.
     * @param number - integer number.
     * @return byte array.
     */
    protected byte[] intToByteArray(int number) {
        return ByteBuffer.allocate(4).putInt(number).array();
    }

    /**
     * Converts byte array to integer number.
     * @param byteArr - byte array.
     * @return integer number.
     */
    protected int byteArrayToInt(byte[] byteArr) {
        ByteBuffer wrapper = ByteBuffer.wrap(byteArr);
        return wrapper.getInt();
    }

    /**
     * Reads string from the file.
     * @param inStream - input stream connected with the file.
     * @return string read from file.
     * @throws java.io.IOException if any IO error occurs.
     */
    protected String readEncodedString(InputStream inStream) throws IOException {
        String string = null;
        try {
            byte[] intByteBuf = new byte[4];
            int bytesRead = inStream.read(intByteBuf, 0, 4);
            if (bytesRead < 4) {
                if (bytesRead == -1) {
                    return null;
                } else {
                    throw new IOException("File is corrupted");
                }
            }
            int stringSize = byteArrayToInt(intByteBuf);
            byte[] stringByteBuf = new byte[stringSize];
            bytesRead = inStream.read(stringByteBuf, 0, stringSize);
            if (bytesRead < stringSize) {
                throw new IOException("File is corrupted");
            }
            string = new String(stringByteBuf, encoding);
        } catch (IOException e) {
            throw new IOException("Read error: " + e.getMessage());
        }
        return string;
    }

    /**
     *
     * Writes string to the file.
     * @param outStream - output stream connected with the file.
     * @param string - string to write to file.
     * @throws java.io.IOException if any IO error occurs.
     */
    protected void writeEncodedString(OutputStream outStream, String string) throws IOException {
        try {
            byte[] stringBytes = string.getBytes(encoding);
            outStream.write(intToByteArray(stringBytes.length));
            outStream.write(stringBytes);
        } catch (IOException e) {
            throw new IOException("Write error: " + e.getMessage());
        }
    }

    /**
     * Loads one file to table. Name of the file is correct.
     * @param filePath - path to the file to read from.
     * @param table - table to be written to.
     * @throws IOException if any IO error occurs.
     */
    public int loadFileToTable(Path filePath, StringTable table) throws IOException {
        int loadedEntriesNumber = 0;
        try (InputStream inStream = Files.newInputStream(filePath)) {
            while (true) {
                String key = readEncodedString(inStream);
                if (key == null) {
                    break;
                }
                String value = readEncodedString(inStream);
                if (value == null) {
                    throw new IOException("File is corrupted");
                }
                table.put(key, value);
                ++loadedEntriesNumber;
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
        return loadedEntriesNumber;
    }

    /**
     * Loads all files from directory XX.[dirExtension] to table.
     * @param dirPath - path to the directory to read from.
     * @param table - table to be written to.
     * @throws IOException if any IO error occurs.
     */
    public int loadDirectoryToTable(Path dirPath, StringTable table) throws IOException {
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
            int loadedFromFile = loadFileToTable(filePath, table);
            if (loadedFromFile == 0) {
                FileUtils.removeRecursively(filePath);
            } else {
                loadedEntriesNumber += loadedFromFile;
            }
        }
        return loadedEntriesNumber;
    }

    /**
     * Read the whole table of strings from the file.
     * @param rootPath - path to the root directory.
     * @param provider - provider.
     *@param tableName - name of the source table of strings.  @return filled table.
     * @throws java.io.IOException if any IO error occurs.
     */
    @Override
    public StringTable readTable(Path rootPath, GeneralTableProvider<String, StringTable> provider, String tableName)
            throws IOException {
        StringTable table = provider.establishTable(tableName);
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
        for (int dirIndex = 0; dirIndex < dirQuantity; ++dirIndex) {
            String dirName = "" + dirIndex + "." + dirExtension;
            Path dirPath = tablePath.resolve(dirName);
            if (!Files.exists(dirPath)) {
                continue;
            }
            if (!Files.isDirectory(dirPath)) {
                throw new NotDirectoryException(dirName + " is not a directory");
            }
            int loadedEntriesNumber = loadDirectoryToTable(dirPath, table);
            if (loadedEntriesNumber == 0) {
                FileUtils.removeDirectory(dirPath);
            }
        }
        return table;
    }

    /**
     * Write the whole table of strings to the file.
     * @param rootPath - path to the root directory.
     * @param table - string table to write.
     */
    @Override
    public void writeTable(Path rootPath, StringTable table) throws IOException {
        if (fileQuantity == 0 || dirQuantity == 0) {
            return;
        }
        Path tablePath = rootPath.resolve(table.getName());
        if (Files.exists(tablePath) && !Files.isDirectory(tablePath)) {
            throw new FileAlreadyExistsException("File " + table.getName() + "  exists");
        }
        if (!Files.exists(tablePath)) {
            Files.createDirectory(tablePath);
        }

        for (Map.Entry<String, String> entry : table) {
            String key = entry.getKey();
            String value = entry.getValue();
            int dirNumber  = key.hashCode() % 16;
            int fileNumber = key.hashCode() / 16 % 16;
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
            try (OutputStream outStream = Files.newOutputStream(filePath)) {
                writeEncodedString(outStream, entry.getKey());
                writeEncodedString(outStream, entry.getValue());
            }
        }
    }


}
