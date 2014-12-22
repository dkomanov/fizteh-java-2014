package ru.fizteh.fivt.students.YaronskayaLiubov.parallel;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import javax.xml.stream.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by luba_yaronskaya on 16.11.14.
 */

public class StoreableDataTableProvider implements TableProvider {
    public String dbDir;
    protected Map<String, StoreableDataTable> tables;
    private ReadWriteLock rwlLock = new ReentrantReadWriteLock();

    protected StoreableDataTableProvider(String dir) throws IllegalArgumentException {
        if (dir == null) {
            throw new IllegalArgumentException("directory name is null");
        }

        this.dbDir = dir;
        if (!Files.exists(Paths.get(dbDir))) {
            try {
                Files.createDirectory(Paths.get(dbDir));
            } catch (IOException e) {
                throw new IllegalArgumentException(dbDir + " illegal name of directory");
            }
        }

        tables = new HashMap<>();
        String[] tableNames = new File(dbDir).list();
        for (String s : tableNames) {
            Path tableName = Paths.get(dbDir).resolve(s);
            if (Files.isDirectory(tableName)) {
                tables.put(s, new StoreableDataTable(this, tableName.toString()));
            }
        }
    }

    @Override
    public Table getTable(String name) {
        CheckParameters.checkTableName(name);

        rwlLock.readLock().lock();
        Table table;
        try {
            table = tables.get(name);
        } finally {
            rwlLock.readLock().unlock();
        }

        return table;
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) {
        CheckParameters.checkTableName(name);
        CheckParameters.checkColumnTypesList(columnTypes);
        StoreableDataTable newTable;
        rwlLock.writeLock().lock();
        try {
            if (!(tables.get(name) == null)) {
                return null;
            }
            String tableDir = dbDir + File.separator + name;
            createSignatureFile(tableDir, "signature.tsv", columnTypes);
            newTable = new StoreableDataTable(this, tableDir);
            tables.put(name, newTable);
        } finally {
            rwlLock.writeLock().unlock();
        }
        return newTable;
    }

    @Override
    public void removeTable(String name) {
        CheckParameters.checkTableName(name);

        rwlLock.writeLock().lock();
        try {
            if (tables.remove(name) == null) {
                throw new IllegalStateException("table '" + name + "' does not exist");
            }
            try {
                StoreableDataTable.fileDelete(new File(Paths.get(dbDir).resolve(name).toString()));
            } catch (NullPointerException e) {
                //do something?
            }
        } finally {
            rwlLock.writeLock().unlock();
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        TableItem row = new TableItem(table);
        try {
            XMLStreamReader xmlReader = XMLInputFactory.newInstance()
                    .createXMLStreamReader(new StringReader(value));
            if (!xmlReader.hasNext()) {
                throw new ParseException("value is empty", 0);
            }

            int nodeType = xmlReader.next();
            if (nodeType == XMLStreamConstants.START_ELEMENT && xmlReader.getName().getLocalPart().equals("row")) {
                int columnIndex = 0;
                while (xmlReader.hasNext()) {
                    int subElementNodeType = xmlReader.nextTag();
                    if (xmlReader.getLocalName().equals("row")) {
                        break;
                    }
                    try {
                        if (xmlReader.getLocalName().equals("null")) {
                            row.setColumnAt(columnIndex, null);
                        } else {
                            if (!xmlReader.getLocalName().equals("col")) {
                                throw new ParseException("Incorrect tag name",
                                        xmlReader.getLocation().getCharacterOffset());
                            }
                            row.setColumnAt(columnIndex,
                                    parseXxx(xmlReader.getElementText(), table.getColumnType(columnIndex)));
                        }
                    } catch (NumberFormatException e) {
                        throw new ParseException("Incorrect xml format", xmlReader.getLocation().getCharacterOffset());
                    }
                    ++columnIndex;
                }
                if (columnIndex != table.getColumnsCount()) {
                    throw new ParseException("Incorrect xml format", 0);
                }

            } else {
                throw new ParseException("Incorrect xml format", 0);
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException("error reading " + value);
        } catch (IOException e) {
            throw new RuntimeException("error parsing " + value);
        }
        return row;
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        CheckParameters.checkMatchItemToTable(table, value);
        StringWriter stringWriter = new StringWriter();
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        try {
            XMLStreamWriter writer = factory.createXMLStreamWriter(stringWriter);
            writer.writeStartElement("row");
            try {
                for (int i = 0; i < table.getColumnsCount(); ++i) {
                    if (value.getColumnAt(i) == null) {
                        writer.writeEmptyElement("null");
                    } else {
                        writer.writeStartElement("col");
                        writer.writeCharacters(value.getColumnAt(i).toString());
                        writer.writeEndElement();
                    }
                }
                writer.writeEndElement();
            } finally {
                writer.close();
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return stringWriter.toString();
    }

    @Override
    public Storeable createFor(Table table) {
        return new TableItem(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        TableItem row = new TableItem(table);
        if (table.getColumnsCount() != values.size()) {
            throw new IndexOutOfBoundsException("Incorrect values count");
        }
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            row.setColumnAt(i, values.get(i));
        }
        return row;
    }

    @Override
    public List<String> getTableNames() {
        return null;
    }

    public static Object parseXxx(String value, Class<?> type) throws IOException {
        switch (type.getSimpleName()) {
            case "Integer":
                return Integer.parseInt(value);
            case "Long":
                return Long.parseLong(value);
            case "Byte":
                return Byte.parseByte(value);
            case "Float":
                return Float.parseFloat(value);
            case "Double":
                return Double.parseDouble(value);
            case "Boolean":
                return Boolean.parseBoolean(value);
            case "String":
                return value;
            default:
                throw new IOException("Undefined type of value");
        }
    }

    public static Class<?> typenameToClass(String typeName) throws UndefinedColumnTypeException {
        switch (typeName) {
            case "int":
                return Integer.class;
            case "long":
                return Long.class;
            case "byte":
                return Byte.class;
            case "float":
                return Float.class;
            case "double":
                return Double.class;
            case "boolean":
                return Boolean.class;
            case "String":
                return String.class;
            default:
                throw new UndefinedColumnTypeException("Undefined column type '" + typeName + "'");
        }
    }

    public static List<Class<?>> typelistToClassList(List<String> stringList) throws UndefinedColumnTypeException {
        List<Class<?>> columnTypes = new ArrayList<>();
        for (int i = 0; i < stringList.size(); ++i) {
            String type = stringList.get(i);
            Class<?> columnClass = typenameToClass(type);
            columnTypes.add(columnClass);
        }
        return columnTypes;
    }

    public Map<String, StoreableDataTable> getTables() {
        return tables;
    }

    public static void createSignatureFile(String dir, String fileName, List<Class<?>> columnTypes) {
        File signatureFile = new File(dir + File.separator + "signature.tsv");
        if (!signatureFile.exists()) {
            try {
                Files.createDirectory(Paths.get(dir));
                signatureFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("error creating signature file: " + e.getMessage());
            }
        }
        try (FileOutputStream out = new FileOutputStream(signatureFile)) {

            try {
                StringBuffer res = new StringBuffer();
                for (Class<?> type : columnTypes) {
                    String name = type.getSimpleName();
                    switch (name) {
                        case "Integer":
                            res.append("int ");
                            break;
                        case "Long":
                            res.append("long ");
                            break;
                        case "Byte":
                            res.append("byte ");
                            break;
                        case "Float":
                            res.append("float ");
                            break;
                        case "Double":
                            res.append("double ");
                            break;
                        case "Boolean":
                            res.append("boolean ");
                            break;
                        case "String":
                            res.append("String ");
                            break;
                        default:
                            throw new RuntimeException("Incorrect type");
                    }
                }
                res.setLength(res.length() - 1);
                out.write((res.toString()).getBytes("UTF-8"));
            } catch (IOException e) {
                throw new RuntimeException("error writing signature");
            }
        } catch (FileNotFoundException e) {
            throw new IllegalStateException();
        } catch (IOException e) {
            //
        }
    }
}
