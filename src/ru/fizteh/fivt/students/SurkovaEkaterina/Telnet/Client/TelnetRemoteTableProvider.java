package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Client;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.StoreableUsage;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.TableSystem.DatabaseTableRow;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Server.XmlOperations.XmlDeserializer;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Server.XmlOperations.XmlSerializer;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.TypesParser;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

class TelnetRemoteTableProvider implements RemoteTableProvider {
    String host;
    int port;
    Socket socket;
    Scanner inputStream;
    PrintStream outputStream;
    HashSet<TelnetRemoteTable> providedTables;

    TelnetRemoteTableProvider(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        this.socket = new Socket(host, port);
        this.inputStream =  new Scanner(socket.getInputStream());
        this.outputStream = new PrintStream(socket.getOutputStream());
        this.providedTables = new HashSet<>();
    }

    @Override
    public void close() throws IOException {
        for (TelnetRemoteTable table: providedTables) {
            try {
                table.close();
            } catch (IOException e) {
                //
            }
        }
        socket.close();
    }

    @Override
    public List<String> getTableNames() {
        List<String> names = new ArrayList<>();
        outputStream.println("show tables");
        int numberOfTables = Integer.parseInt(inputStream.nextLine());
        for (int i = 0; i < numberOfTables; i++) {
            names.add(inputStream.nextLine().split(" ")[0]);
        }
        System.out.println(names);
        return names;
    }

    void showTables() {
        outputStream.println("show tables");
        int numberOfTables = Integer.parseInt(inputStream.nextLine());
        System.out.println(numberOfTables);
        for (int i = 0; i < numberOfTables; i++) {
            String table = inputStream.nextLine().split(" ")[0];
            System.out.println(table);
        }
    }

    @Override
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Table's name cannot be empty");
        }

        checkTableName(name);

        if (getTableNames().contains(name)) {
            try {
                TelnetRemoteTable table = new TelnetRemoteTable(name, host, port, this);
                providedTables.add(table);
                return table;
            } catch (IOException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Table's name cannot be null!");
        }

        checkTableName(name);

        if (columnTypes == null || columnTypes.isEmpty()) {
            throw new IllegalArgumentException("Column types cannot be null!");
        }

        checkColumnTypes(columnTypes);

        if (getTableNames().contains(name)) {
            return null;
        }

        String types = StoreableUsage.concatenateListEntries(columnTypes);
        outputStream.println("create " + name + " (" + types + ")");
        String result = inputStream.nextLine();
        System.out.println(result);
        if (result.equals("created")) {
            return getTable(name);
        } else {
            return null;
        }
    }

    @Override
    public void removeTable(String name) throws IOException {
        if (name == null) {
            throw new IllegalArgumentException("Table's name cannot be null!");
        }
        outputStream.println("drop " + name);
        String result = inputStream.nextLine();
        System.out.println(result);
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        XmlDeserializer deserializer = new XmlDeserializer(value);
        Storeable result;
        List<Object> values = new ArrayList<Object>(table.getColumnsCount());

        for (int index = 0; index < table.getColumnsCount(); ++index) {
            try {
                Class<?> expectedType = table.getColumnType(index);
                Object columnValue = deserializer.getNext(expectedType);
                values.add(columnValue);
            } catch (ColumnFormatException e) {
                throw new ParseException("Incompatible type: " + e.getMessage(), index);
            } catch (IndexOutOfBoundsException e) {
                throw new ParseException("Xml representation doesn't match the !", index);
            }
        }
        try {
            deserializer.close();
            result = createFor(table, values);
        } catch (ColumnFormatException e) {
            throw new ParseException("Incompatible types: " + e.getMessage(), 0);
        } catch (IndexOutOfBoundsException e) {
            throw new ParseException("Xml representation doesn't match the format!", 0);
        } catch (IOException e) {
            throw new ParseException(e.getMessage(), 0);
        }
        return result;
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null!");
        }

        try {
            outputStream.println("describe " + table.getName());
            String signature = inputStream.nextLine();
            List<Class<?>> types = TypesParser.getListFromString(signature);

            int expectedSize = types.size();
            int actualSize = ((DatabaseTableRow) value).getColumnsSize();

            if (expectedSize != actualSize) {
                throw new ColumnFormatException("Incorrect number of values to serialize for table "
                        + table.getName() + ": expected: " + expectedSize + ", actual: " + actualSize);
            }

            for (int i = 0; i < table.getColumnsCount(); ++i) {
                Class actualType = types.get(i);
                Class passedType = ((DatabaseTableRow) value).getColumnTypeAt(i);
                if (actualType != passedType) {
                    throw new ColumnFormatException("Passed value type on "
                            + i + "th place does not match actual type inputStream table.");
                }
            }
        } catch (Exception e) {
            throw new ColumnFormatException(e.getMessage());
        }

        try {
            XmlSerializer xmlSerializer = new XmlSerializer();
            for (int index = 0; index < table.getColumnsCount(); ++index) {
                xmlSerializer.write(value.getColumnAt(index));
            }
            xmlSerializer.close();
            return xmlSerializer.getRepresentation();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Incorrect value!");
        }
        return null;
    }

    @Override
    public Storeable createFor(Table table) {
        outputStream.println("describe " + table.getName());
        String signature = inputStream.nextLine();
        try {
            List<Class<?>> types = TypesParser.getListFromString(signature);
            return new DatabaseTableRow(types);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Storeable createFor(Table table, List<?> values)
            throws ColumnFormatException, IndexOutOfBoundsException {
        outputStream.println("describe " + table.getName());
        String signature = inputStream.nextLine();
        try {
            List<Class<?>> types = TypesParser.getListFromString(signature);
            Storeable result = new DatabaseTableRow(types);
            if (values.size() != types.size()) {
                throw new IndexOutOfBoundsException();
            }
            for (int i = 0; i < values.size(); i++) {
                result.setColumnAt(i, values.get(i));
            }
            return result;
        } catch (Exception e) {
            throw new ColumnFormatException(e.getMessage());
        }
    }

    public void exit() {

    }

    private void checkTableName(String name) {
        if (!name.matches("[0-9A-Za-zА-Яа-я]+")) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Bad symbol!");
        }
    }

    private void checkColumnTypes(List<Class<?>> columnTypes) {
        for (final Class<?> columnType : columnTypes) {
            if (columnType == null) {
                throw new IllegalArgumentException(this.getClass().getSimpleName() + ": Unknown column type!");
            }
            TypesParser.getNameByType(columnType);
        }
    }
}
