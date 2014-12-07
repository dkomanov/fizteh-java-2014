package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.TypeTransformer;
import ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.XmlSerializer;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

/**
 * @author AlexeyZhuravlev
 */
class MyRemoteTableProvider implements RemoteTableProvider {

    Scanner in;
    PrintStream out;
    Socket socket;
    String host;
    int port;
    HashSet<MyRemoteTable> providedTables;

    MyRemoteTableProvider(String passedHost, int passedPort) throws IOException {
        host = passedHost;
        port = passedPort;
        socket = new Socket(host, port);
        in =  new Scanner(socket.getInputStream());
        out = new PrintStream(socket.getOutputStream());
        providedTables = new HashSet<>();
    }

    @Override
    public void close() throws IOException {
        for (MyRemoteTable table: providedTables) {
            try {
                table.close();
            } catch (Exception e) {
                //do nothing
            }
        }
        socket.close();
    }

    @Override
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (getTableNames().contains(name)) {
            try {
                MyRemoteTable table = new MyRemoteTable(host, port, name, this);
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
        if (name == null || columnTypes == null) {
            throw new IllegalArgumentException();
        } else {
            String types = TypeTransformer.stringFromTypeList(columnTypes);
            out.println("create " + name + " (" + types + ")");
            String result = in.nextLine();
            if (result.equals("created")) {
                return getTable(name);
            } else {
                return null;
            }
        }
    }

    @Override
    public void removeTable(String name) throws IOException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        out.println("drop " + name);
        in.nextLine();
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        out.println("describe " + table.getName());
        String signature = in.nextLine();
        try {
            List<Class<?>> types = TypeTransformer.typeListFromString(signature);
            List<Object> values = XmlSerializer.deserializeString(value, types);
            return createFor(table, values);
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        try {
            out.println("describe " + table.getName());
            String signature = in.nextLine();
            List<Class<?>> types = TypeTransformer.typeListFromString(signature);
            int expected = types.size();
            int found = ((RemoteStoreableValue) value).getValues().size();
            if (expected != found) {
                throw new ColumnFormatException("Incorrect number of values to serialize for table "
                        + table.getName() + ": " + expected
                        + " expected, " + ((RemoteStoreableValue) value).getValues().size() + " found");
            }
            for (int i = 0; i < table.getColumnsCount(); i++) {
                Class own = types.get(i);
                Class passed = ((RemoteStoreableValue) value).getColumnType(i);
                String representation;
                if (value.getColumnAt(i) == null) {
                    representation = "null";
                } else {
                    representation = value.getColumnAt(i).toString();
                }
                if (own != passed) {
                    throw new ColumnFormatException("Column format on position " + i + " is "
                            + own.getSimpleName() + ". " + representation + " is " + passed.getSimpleName());
                }
            }
            return XmlSerializer.serializeObjectList(((RemoteStoreableValue) value).getValues());
        } catch (Exception e) {
            throw new ColumnFormatException(e.getMessage());
        }
    }

    @Override
    public Storeable createFor(Table table) {
        out.println("describe " + table.getName());
        String signature = in.nextLine();
        try {
            List<Class<?>> types = TypeTransformer.typeListFromString(signature);
            return new RemoteStoreableValue(types);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        out.println("describe " + table.getName());
        String signature = in.nextLine();
        try {
            List<Class<?>> types = TypeTransformer.typeListFromString(signature);
            Storeable result = new RemoteStoreableValue(types);
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

    @Override
    public List<String> getTableNames() {
        List<String> result = new ArrayList<>();
        out.println("show tables");
        int n = Integer.parseInt(in.nextLine());
        for (int i = 0; i < n; i++) {
            result.add(in.nextLine().split(" ")[0]);
        }
        return result;
    }
}
