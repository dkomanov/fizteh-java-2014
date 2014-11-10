package ru.fizteh.fivt.students.AlexeyZhuravlev.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit.HybridTable;
import ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit.JUnitDataBaseDir;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author AlexeyZhuravlev
 */
public class StructuredTableProvider implements TableProvider {

    HashMap<String, Table> tables;
    File mainDirectory;

    private String readSignature(File signature) throws IOException {
        Charset charset = Charset.forName("UTF-8");
        Path path = signature.toPath();
        String line;
        try (BufferedReader stream = Files.newBufferedReader(path, charset)) {
            line = stream.readLine();
        }
        if (line == null) {
            throw new IOException("Empty signature file");
        }
        return line;
    }

    protected StructuredTableProvider(String path) throws IOException {
        try {
            JUnitDataBaseDir unstructuredDbDir = new JUnitDataBaseDir(path);
            mainDirectory = new File(path);
            tables = new HashMap<>();
            for (Map.Entry<String, HybridTable> entry: unstructuredDbDir.tables.entrySet()) {
                File signature = new File(entry.getValue().virginTable.mainDir, "signature.tsv");
                if (!signature.exists()) {
                    throw new IOException("No signature file specified");
                }
                String typesInString = readSignature(signature);
                List<Class<?>> types = TypeTransformer.typeListFromString(typesInString);
                StructuredTable table = new StructuredTable(entry.getValue(), types, entry.getKey(), this);
                tables.put(entry.getKey(), table);
            }
        } catch (Exception e) {
            throw new IOException("Problems while reading from database directory");
        }
    }

    @Override
    public Table getTable(String name) {
        return null;
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        return null;
    }

    @Override
    public void removeTable(String name) throws IOException {

    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        return null;
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        return null;
    }

    @Override
    public Storeable createFor(Table table) {
        return null;
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        return null;
    }
}
