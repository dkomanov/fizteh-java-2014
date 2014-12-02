package ru.fizteh.fivt.students.sautin1.junit.filemap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Created by sautin1 on 10/20/14.
 */
public class StringTableIOToolsSingleFile extends StringTableIOToolsMultipleFiles {
    public StringTableIOToolsSingleFile(String encoding) {
        super(0, 1, encoding);
    }

    public StringTableIOToolsSingleFile() {
        this("UTF-8");
    }

    /**
     * Read the whole table of strings from one file.
     * @param rootPath - path to the root directory.
     * @param tableName - name of source table of strings.
     * @param provider - string table provider.
     * @return filled table.
     * @throws java.io.IOException if any IO error occurs.
     */
    @Override
    public StringTable readTable(Path rootPath, GeneralTableProvider<String, StringTable> provider, String tableName)
            throws IOException {
        StringTable table = provider.establishTable(tableName);
        if (Files.isDirectory(rootPath)) {
            throw new IllegalArgumentException("Wrong file");
        }
        if (!Files.exists(rootPath)) {
            Files.createFile(rootPath);
        }
        try (DataInputStream inStream = new DataInputStream(Files.newInputStream(rootPath))) {
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
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
        return table;
    }

    /**
     * Write the whole table of strings to the file.
     * @param rootPath - path to the root directory.
     * @param table - string table to write.
     */
    @Override
    public void writeTable(Path rootPath, StringTable table) {
        if (Files.isDirectory(rootPath)) {
            throw new IllegalArgumentException("Wrong file");
        }
        if (!Files.exists(rootPath)) {
            try {
                Files.createFile(rootPath);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        try (DataOutputStream outStream = new DataOutputStream(Files.newOutputStream(rootPath))) {
            for (Map.Entry<String, String> entry : table) {
                writeEncodedString(outStream, entry.getKey());
                writeEncodedString(outStream, entry.getValue());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }


}
