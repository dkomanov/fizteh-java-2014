package ru.fizteh.fivt.students.lukina.DataBase;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import javax.xml.stream.*;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

public class DBaseProvider implements TableProvider, AutoCloseable {
    DBaseLoader loader;
    private HashMap<String, DBase> tableBase;
    private String rootDir;
    private String oldPath;
    private volatile boolean isClosed = false;

    public DBaseProvider(String dir) {
        if (dir == null || dir.isEmpty()) {
            throw new IllegalArgumentException("Empty directory name");
        }
        if (!(new File(dir).exists())) {
            throw new IllegalStateException("Directory not exists");
        }
        if (dir.endsWith(File.separator)) {
            rootDir = dir;
        } else {
            rootDir = dir + File.separatorChar;
        }
        oldPath = dir;
        loader = new DBaseLoader(dir, this);
        tableBase = loader.loadBase();
    }

    public static void doDelete(File currFile) throws RuntimeException {
        RuntimeException e = new RuntimeException("Cannot remove file");
        if (currFile.exists()) {
            if (!currFile.isDirectory() || currFile.listFiles().length == 0) {
                if (!currFile.delete()) {
                    throw e;
                }
            } else {
                while (currFile.listFiles().length != 0) {
                    doDelete(currFile.listFiles()[0]);
                }
                if (!currFile.delete()) {
                    throw e;
                }
            }
        }
    }

    public void checkClosed() {
        if (isClosed) {
            throw new IllegalStateException("TableProvider is closed");
        }
    }

    public boolean checkTableName(String tableName) {
        if ((tableName == null) || tableName.isEmpty()
                || tableName.matches(".*[.|;|/|\\\\].*")) {
            return false;
        }
        return true;
    }

    @Override
    public Table getTable(String name) throws IllegalArgumentException {
        checkClosed();
        if (!checkTableName(name)) {
            throw new IllegalArgumentException("name is incorrect");
        }
        DBase getTable = null;
        getTable = tableBase.get(name);
        if (getTable == null) {
            getTable = loader.loadTable(new File(rootDir + name));
        }
        return getTable;
    }

    private void fillTypesFile(File types, List<Class<?>> columnTypes)
            throws IOException {
        try (FileOutputStream out = new FileOutputStream(types)) {
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
                            throw new RuntimeException("Incorrect type in file " + name);
                    }
                }
                res.setLength(res.length() - 1);
                out.write((res.toString()).getBytes("UTF-8"));
            } catch (IOException e) {
                throw new IOException("Cannot create file "
                        + types.getAbsolutePath(), e);
            }
        }
    }

    private ArrayList<Class<?>> checkColumnTypes(List<Class<?>> types) {
        ArrayList<Class<?>> res = new ArrayList<Class<?>>();
        for (Class<?> type : types) {
            if (type == null) {
                throw new IllegalArgumentException("Have empty type");
            }
            if (type.equals(int.class) || type.equals(Integer.class)) {
                res.add(Integer.class);
            } else if (type.equals(long.class) || type.equals(Long.class)) {
                res.add(Long.class);
            } else if (type.equals(byte.class) || type.equals(Byte.class)) {
                res.add(Byte.class);
            } else if (type.equals(float.class) || type.equals(Float.class)) {
                res.add(Float.class);
            } else if (type.equals(double.class) || type.equals(Double.class)) {
                res.add(Double.class);
            } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
                res.add(Boolean.class);
            } else if (type.equals(String.class)) {
                res.add(String.class);
            } else {
                throw new IllegalArgumentException("Incorrect class " + type);
            }
        }
        return res;
    }

    @Override
    public Table createTable(String name, List<Class<?>> typesList)
            throws IOException {
        checkClosed();
        if (!checkTableName(name)) {
            throw new IllegalArgumentException("name is incorrect");
        }
        if (typesList == null) {
            throw new IllegalArgumentException("have no types list");
        }
        if (typesList.isEmpty()) {
            throw new IllegalArgumentException("empty types list");
        }
        ArrayList<Class<?>> columnTypes = checkColumnTypes(typesList);
        File fileTable = new File(rootDir + name);
        if (!fileTable.exists()) {
            if (!fileTable.mkdir()) {
                throw new IOException("Cannot create directory "
                        + fileTable.getAbsolutePath());
            }
            File types = new File(rootDir + name + File.separator
                    + "signature.tsv");
            if (!types.createNewFile()) {
                throw new IOException("Cannot create file "
                        + types.getAbsolutePath());
            }
            fillTypesFile(types, columnTypes);
            DBase table = new DBase(name, rootDir, this);
            table.setTypes(columnTypes);
            tableBase.put(name, table);
            return table;
        }
        return null;
    }

    @Override
    public void removeTable(String name) throws IOException {
        checkClosed();
        if (!checkTableName(name)) {
            throw new IllegalArgumentException("name is incorrect");
        }
        File fileTable = new File(rootDir + name);
        if (!fileTable.exists() && tableBase.get(name) == null) {
            throw new IllegalStateException("table not exists");
        }
        doDelete(fileTable);
        tableBase.get(name).setRemoved();
        tableBase.remove(name);
    }

    protected void closeTable(String name) {
        tableBase.remove(name);
    }

    public Class<?> getClassFromString(String name) {
        switch (name) {
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
                throw new RuntimeException("Incorrect type " + name);
        }
    }

    public Object getObjectFromString(String text, Class<?> type)
            throws IOException {
        switch (type.getSimpleName()) {
            case "Integer":
                return Integer.parseInt(text);
            case "Long":
                return Long.parseLong(text);
            case "Byte":
                return Byte.parseByte(text);
            case "Float":
                return Float.parseFloat(text);
            case "Double":
                return Double.parseDouble(text);
            case "Boolean":
                return Boolean.parseBoolean(text);
            case "String":
                return text;
            default:
                throw new IOException("Incorrect type");
        }
    }

    @Override
    public Storeable deserialize(Table table, String value)
            throws ParseException {
        checkClosed();
        DBaseRow row = new DBaseRow(table);
        XMLInputFactory xmlFactory = XMLInputFactory.newFactory();
        StringReader str = new StringReader(value);
        XMLStreamReader reader = null;
        try {
            reader = xmlFactory.createXMLStreamReader(str);
            if (!reader.hasNext()) {
                throw new ParseException("Input value is empty", 0);
            }
            reader.nextTag();
            if (!reader.getLocalName().equals("row")) {
                throw new ParseException("Incorrect xml format", reader
                        .getLocation().getCharacterOffset());
            }
            int columnIndex = 0;
            while (reader.hasNext()) {
                reader.nextTag();
                if (reader.getLocalName().equals("null")) {
                    row.setColumnAt(columnIndex, null);
                    reader.nextTag();
                } else {
                    if (!reader.getLocalName().equals("col")) {
                        if (!reader.isEndElement()) {
                            throw new ParseException("Incorrect xml format",
                                    reader.getLocation().getCharacterOffset());
                        }
                        if (reader.isEndElement()
                                && reader.getLocalName().equals("row")) {
                            break;
                        }
                    }
                    String text = reader.getElementText();
                    row.setColumnAt(
                            columnIndex,
                            getObjectFromString(text,
                                    table.getColumnType(columnIndex)));
                }
                columnIndex++;
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot parse string " + value, e);
        } catch (XMLStreamException e) {
            throw new RuntimeException("Cannot parse string " + value, e);
        } finally {
            try {
                reader.close();
            } catch (Throwable e) {
                // not OK
            }
        }
        return row;
    }

    public void checkColumns(Table t, Storeable value) {
        if (value == null) {
            throw new IllegalArgumentException("Empty value");
        }
        try {
            for (int i = 0; i < t.getColumnsCount(); ++i) {
                Object valueObj = value.getColumnAt(i);
                if (valueObj != null
                        && !valueObj.getClass().equals(t.getColumnType(i))) {
                    throw new ColumnFormatException("incorrect column format");
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new ColumnFormatException("less columns in value", e);
        }
        try {
            value.getColumnAt(t.getColumnsCount());
            throw new ColumnFormatException("more columns in value");
        } catch (IndexOutOfBoundsException e) {
            // it's OK
        }
    }

    @Override
    public String serialize(Table table, Storeable value)
            throws ColumnFormatException {
        checkClosed();
        checkColumns(table, value);
        XMLOutputFactory xmlFactory = XMLOutputFactory.newFactory();
        StringWriter str = new StringWriter();
        try {
            XMLStreamWriter writer = xmlFactory.createXMLStreamWriter(str);
            try {
                writer.writeStartElement("row");
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
        } finally {
            try {
                str.close();
            } catch (Throwable e) {
                // not OK
            }
        }
        return str.toString();
    }

    @Override
    public Storeable createFor(Table table) {
        checkClosed();
        return new DBaseRow(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values)
            throws ColumnFormatException, IndexOutOfBoundsException {
        checkClosed();
        DBaseRow row = new DBaseRow(table);
        if (values.size() != table.getColumnsCount()) {
            throw new ColumnFormatException("Incorrect num of columns");
        }
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            row.setColumnAt(i, values.get(i));
        }
        return row;
    }

    @Override
    public void close() throws Exception {
        for (Entry<String, DBase> entry : tableBase.entrySet()) {
            entry.getValue().close();
        }
        isClosed = true;
    }

    @Override
    public List<String> getTableNames() {
        List<String> tableNames = new Vector<String>();
        Set<String> fileKeySet = tableBase.keySet();
        for (String name : fileKeySet) {
            tableNames.add(name);
        }
        return tableNames;
    }

}
