package ru.fizteh.fivt.students.sautin1.telnet.storeable;

import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.sautin1.telnet.filemap.GeneralTableProvider;
import ru.fizteh.fivt.students.sautin1.telnet.filemap.TableIOTools;
import ru.fizteh.fivt.students.sautin1.telnet.shell.FileUtils;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sautin1 on 12/10/14.
 */
public class StoreableTableProvider extends GeneralTableProvider<Storeable, StoreableTable>
        implements TableProvider, AutoCloseable {
    private final String signatureFileName = "signature.tsv";
    private boolean isClosed;

    public StoreableTableProvider(Path rootDir, boolean autoCommit, TableIOTools tableIOTools) throws IOException {
        super(rootDir, autoCommit, tableIOTools);
        isClosed = false;
    }

    @Override
    public Path getRootDir() {
        checkClosed();
        return super.getRootDir();
    }

    @Override
    public StoreableTable establishTable(String name, Object[] args) {
        checkClosed();
        return new StoreableTable(name, autoCommit, (List<Class<?>>) args[0], this);
    }

    void createSignatureFile(String tableName, List<Class<?>> columnTypes) {
        Path signatureFilePath = getRootDir().resolve(tableName).resolve(signatureFileName);
        try {
            Files.createFile(signatureFilePath);
            String signatureString = StoreableXMLUtils.buildSignatureString(columnTypes);
            FileUtils.printToFile(signatureString, signatureFilePath);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public StoreableTable createTable(String name, Object[] args) {
        checkClosed();
        try {
            StoreableTable table = createTable(name, (List<Class<?>>) args[0]);
            return table;
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public StoreableTable createTable(String name, List<Class<?>> columnTypes) throws IOException {
        checkClosed();
        StoreableTable newTable = super.createTable(name, new Object[]{columnTypes});
        if (newTable == null) {
            return null;
        }
        if (columnTypes == null || columnTypes.isEmpty()) {
            throw new IllegalArgumentException("No column types provided");
        }

        createSignatureFile(name, columnTypes);
        return newTable;
    }

    @Override
    public StoreableTable getTable(String name) {
        StoreableTable table = super.getTable(name);
        if (table == null) {
            try {
                table = loadTable(getRootDir(), name);
            } catch (IOException e) {
                throw new IllegalArgumentException("Such table does not exist: " + e.getMessage());
            } catch (ParseException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        return table;
    }

    @Override
    public void removeTable(String name) {
        checkClosed();
        super.removeTable(name);
    }

    @Override
    public int commitTable(String tableName) throws IOException {
        checkClosed();
        return super.commitTable(tableName);
    }

    public void closeTable(String tableName) {
        checkClosed();
        tableMap.remove(tableName);
    }

    @Override
    public Storeable deserialize(Table table, String serializedValue) throws ParseException {
        checkClosed();
        List<Object> rawObjectList = new ArrayList<>();
        try (StoreableTableXMLReader xmlReader = new StoreableTableXMLReader(serializedValue)) {
            for (int columnIndex = 0; columnIndex < table.getColumnsCount(); ++columnIndex) {
                rawObjectList.add(xmlReader.deserializeColumn(table.getColumnType(columnIndex)));
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e.getMessage());
        }
        return createFor(table, rawObjectList);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        checkClosed();
        StoreableValidityChecker.checkValueFormat(table, value);
        String serializedValue;
        try {
            StoreableTableXMLWriter xmlWriter = new StoreableTableXMLWriter();
            serializedValue = xmlWriter.serializeStoreable(value);
        } catch (IOException | XMLStreamException e) {
            throw new RuntimeException(e.getMessage());
        }
        return serializedValue;
    }

    @Override
    public Storeable deserialize(StoreableTable table, String serialized) throws ParseException {
        return deserialize((Table) table, serialized);
    }

    @Override
    public String serialize(StoreableTable table, Storeable value) throws ColumnFormatException {
        return serialize((Table) table, value);
    }

    @Override
    public Storeable createFor(Table table) {
        checkClosed();
        List<Class<?>> valueTypes = new ArrayList<>(table.getColumnsCount());
        for (int valueIndex = 0; valueIndex < table.getColumnsCount(); ++valueIndex) {
            valueTypes.add(valueIndex, table.getColumnType(valueIndex));
        }
        return new TableRow(valueTypes);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        checkClosed();
        if (values.size() != table.getColumnsCount()) {
            throw new IndexOutOfBoundsException("Wrong number of columns provided");
        }
        Storeable tableRow = createFor(table);
        for (int valueIndex = 0; valueIndex < table.getColumnsCount(); ++valueIndex) {
            tableRow.setColumnAt(valueIndex, values.get(valueIndex));
        }
        return tableRow;
    }

    @Override
    public List<String> getTableNames() {
        checkClosed();
        return new ArrayList<String>(tableMap.keySet());
    }

    /**
     * Load table from the file.
     *
     * @param root      - path to the root directory.
     * @param tableName - name of the table to load.
     * @throws java.io.IOException if any IO error occured.
     */
    @Override
    public StoreableTable loadTable(Path root, String tableName) throws IOException, ParseException {
        checkClosed();
        String signature;
        try {
            signature = FileUtils.readFromFile(root.resolve(tableName).resolve(signatureFileName));
        } catch (IOException e) {
            throw new IOException("Cannot access signature file for table " + tableName);
        }
        List<Class<?>> valueTypes = StoreableXMLUtils.parseSignatureString(signature);
        StoreableTable table = tableIOTools.readTable(this, root, tableName, new Object[]{valueTypes});
        table.commit();
        tableMap.put(table.getName(), table);
        return table;
    }

    /**
     * Save table to file.
     * @param root - path to the root directory.
     * @param tableName - name of the table to save.
     */
    @Override
    public void saveTable(Path root, String tableName) throws IOException {
        checkClosed();
        StoreableTable table = tableMap.get(tableName);
        super.saveTable(root, tableName);
        Path tablePath = root.resolve(tableName);
        if (Files.exists(tablePath)) {
            // directory was not deleted because of save
            createSignatureFile(tableName, table.getColumnTypes());
        }
    }

    @Override
    public void saveAllTables() throws IOException {
        checkClosed();
        super.saveAllTables();
    }

    @Override
    public void loadAllTables() throws IOException, ParseException {
        checkClosed();
        super.loadAllTables();
    }

    @Override
    public List<StoreableTable> listTables() {
        checkClosed();
        return super.listTables();
    }

    @Override
    public void close() {
        if (isClosed) {
            return;
        }
        for (Map.Entry<String, StoreableTable> entry : tableMap.entrySet()) {
            entry.getValue().close();
        }
        isClosed = true;
    }

    private void checkClosed() {
        if (isClosed) {
            throw new IllegalStateException("table provider is closed");
        }
    }

    @Override
    public String toString() {
        checkClosed();
        StringBuilder builder = new StringBuilder();

        builder.append(getClass().getSimpleName());
        builder.append("[");
        builder.append(getRootDir().toString());
        builder.append("]");

        return builder.toString();
    }
}
