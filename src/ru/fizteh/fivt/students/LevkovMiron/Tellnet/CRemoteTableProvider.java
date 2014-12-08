package ru.fizteh.fivt.students.LevkovMiron.Tellnet;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Мирон on 07.12.2014 ru.fizteh.fivt.students.LevkovMiron.Tellnet.
 */
public class CRemoteTableProvider implements RemoteTableProvider {

    private Client client;
    private boolean closed = false;
    private String host;
    private int port;
    private Parser parser;

    public CRemoteTableProvider(String h, int p) throws IOException {
        client = new Client();
        host = h;
        port = p;
        client.connect(host, port);
        parser = new Parser();
    }

    @Override
    public void close() throws IOException {
        client.disconnect();
        closed = true;
    }

    @Override
    public Table getTable(String name) throws NullPointerException {
        if (name == null) {
            throw new NullPointerException();
        }
        try {
            client.send("GETTABLE " + name);
            String s = client.read();
            if (s.equals("1")) {
                return new CRemoteTable(host, port, name);
            }
            return null;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
            return null;
        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        if (name == null) {
            throw new NullPointerException();
        }
        try {
            StringBuilder builder = new StringBuilder("");
            builder.append("CREATETABLE " + name);
            for (Class cl : columnTypes) {
                builder.append(" " + cl.toString());
            }
            client.send(builder.toString());
            String s = client.read();
            if (s.equals("1")) {
                return new CRemoteTable(host, port, name);
            }
            return null;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
            return null;
        }
    }

    @Override
    public void removeTable(String name) throws IOException {
        client.send("drop " + name);
        client.read();
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        return parser.deserialize(table, value);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        return parser.serialize(table, value);
    }

    @Override
    public Storeable createFor(Table table) {
        if (closed) {
            throw new IllegalStateException("Provider is closed");
        }
        List<Object> values = new ArrayList<>(table.getColumnsCount());
        return new CStoreable(values);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        if (closed) {
            throw new IllegalStateException("Provider is closed");
        }
        List<Object> listObjects = new ArrayList<>(values);
        if (listObjects.size() != table.getColumnsCount()) {
            throw new ColumnFormatException("Wrong number of columns");
        }
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            if (listObjects.get(i).getClass() != (table.getColumnType(i))) {
                throw new ColumnFormatException("Illegal column type: column " + i
                        + " should be " + table.getColumnType(1));
            }
        }
        return new CStoreable(listObjects);
    }

}
