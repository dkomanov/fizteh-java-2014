package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage;

import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.StoreablePackage.AbstractStoreable;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.StoreablePackage.Serializator;
import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.StoreablePackage.TypesUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.text.ParseException;
import java.util.*;

public class RealRemoteTableProvider implements RemoteTableProvider {//, TableProviderExtended {
    private HashMap<String, RealRemoteTable> tables;
    private Socket server;
    private Scanner input;
    private PrintStream output;
    private String hostName;
    private int port;

    public String getHostName() {
        return hostName;
    }

    public int getPort() {
        return port;
    }

    public RealRemoteTable getCurrentTable() {
        output.println("current");
        String answerFromServer = input.nextLine();
        if (answerFromServer.equals("\n")) {
            System.out.println("empty");
        } else {
            System.out.println("there is current table");
        }
        return null;
    }

    public void setCurrentTable(String name) throws IllegalStateException {
        output.println("use " + name);
        String answerFromServer = input.nextLine();
        String[] parsedAnswer = answerFromServer.split(" ");
        if (!parsedAnswer[0].equals("using")) {
            throw new IllegalStateException(answerFromServer);
        }
    }

    public RealRemoteTableProvider(String hostName, int port) throws IOException {
        tables = new HashMap<>();
        server = new Socket(InetAddress.getByName(hostName), port);
        input = new Scanner(server.getInputStream());
        output = new PrintStream(server.getOutputStream());
        this.hostName = hostName;
        this.port = port;
    }

    @Override
    public Table getTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("null argument");
        }
        output.println("show tables");
        input.nextLine();
        int numberOfTables = Integer.parseInt(input.nextLine());
        for (int i = 0; i < numberOfTables; ++i) {
            String tableName = input.nextLine().split(" ")[0];
            try {
                tables.put(tableName, new RealRemoteTable(tableName, hostName, port, this));
            } catch (IOException e) {
                System.err.println("error while creating real remote table");
            }
        }
        return tables.get(name);
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException, IllegalArgumentException {
        if (name == null || columnTypes == null) {
            throw new IllegalArgumentException("null argument");
        }
        TypesUtils.checkTypes(columnTypes);
        if (tables.containsKey(name)) {
            return null;
        } else {
            output.println("create " + name + " (" + TypesUtils.toFileSignature(columnTypes) + ")");
            String message = input.nextLine();
            if ("created".equals(message)) {
                try {
                    tables.put(name, new RealRemoteTable(name, hostName, port, this));
                } catch (IOException e) {
                    System.err.println("error while creating real remote table: " + e.getMessage());
                }
            }
            return tables.get(name);
        }
    }

    @Override
    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException, IOException {
        if (name == null) {
            throw new IllegalArgumentException("null argument");
        }
        output.println("remove " + name);
        String message = input.nextLine();
        if (!"dropped".equals(message)) {
            throw new IllegalStateException("table \'" + name + "\' doesn't exist");
        }
    }

    @Override
    public Storeable createFor(Table table) {
        Object[] startValues = new Object[table.getColumnsCount()];
        return new AbstractStoreable(startValues, table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        if (table.getColumnsCount() != values.size()) {
            throw new IndexOutOfBoundsException("number of types");
        }
        List<Object> objValues = new ArrayList<>(values);
        List<Class<?>> typeList = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            if (objValues.get(i).getClass() != (table.getColumnType(i))) {
                throw new ColumnFormatException("mismatch column type");
            }
            typeList.add(table.getColumnType(i));
        }
        TypesUtils.checkNewStorableValue(typeList, objValues);
        return new AbstractStoreable(objValues.toArray(), table);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        if (table.getColumnsCount() != TypesUtils.getSizeOfStoreable(value)) {
            throw new ColumnFormatException("wrong size");
        }
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            if (!table.getColumnType(i).equals(value.getColumnAt(i).getClass())) {
                throw new ColumnFormatException("need: " + table.getColumnType(i)
                        + ", but got:" + value.getColumnAt(i).getClass());
            }
        }
        return Serializator.serialize(table, value);
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        return Serializator.deserialize(table, value);
    }

    @Override
    public List<String> getTableNames() {
        getTable("simple");
        List<String> result = new ArrayList<>();
        Collection<RealRemoteTable> filemaps = tables.values();
        for (RealRemoteTable oneTable : filemaps) {
            result.add(oneTable.getName());
        }
        return result;
    }

    public List<String> showTables() {
        output.println("show tables");
        List<String> result = new ArrayList<>();
        input.nextLine();
        int size = Integer.parseInt(input.nextLine());
        for (int i = 0; i < size; ++i) {
            result.add(input.nextLine());
        }
        return result;
    }

    public String describeTable(String name) {
        output.println("describe " + name);
        return input.nextLine();
    }

    @Override
    public void close() throws IOException {
        for (RealRemoteTable oneTable : tables.values()) {
            oneTable.close();
        }
        server.close();
    }
}
