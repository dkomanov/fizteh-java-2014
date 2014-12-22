package ru.fizteh.fivt.students.ilivanov.Telnet;


import org.json.JSONArray;
import org.json.JSONException;
import ru.fizteh.fivt.students.ilivanov.Telnet.Interfaces.ColumnFormatException;
import ru.fizteh.fivt.students.ilivanov.Telnet.Interfaces.RemoteTableProvider;
import ru.fizteh.fivt.students.ilivanov.Telnet.Interfaces.Storeable;
import ru.fizteh.fivt.students.ilivanov.Telnet.Interfaces.Table;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoteFileMapProvider implements RemoteTableProvider, AutoCloseable {
    private Socket socket;
    private BufferedReader reader;
    private PrintStream writer;
    private RemoteFileUsing currentActive;
    private boolean valid;
    private HashMap<String, RemoteFileUsing> used;
    private String host;
    private int port;

    public RemoteFileMapProvider(Socket socket, String host, int port) throws IOException {
        this.socket = socket;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintStream(socket.getOutputStream());
        valid = true;
        currentActive = null;
        used = new HashMap<>();
        this.port = port;
        this.host = host;
    }

    private void checkState() {
        if (!valid) {
            throw new IllegalStateException("Provider is closed");
        }
    }

    private boolean badSymbolCheck(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) <= 31) {
                return false;
            }
            if (string.charAt(i) == '\\') {
                return false;
            }
            if (string.charAt(i) == '/') {
                return false;
            }
            if (string.charAt(i) == '*') {
                return false;
            }
            if (string.charAt(i) == ':') {
                return false;
            }
            if (string.charAt(i) == '<') {
                return false;
            }
            if (string.charAt(i) == '>') {
                return false;
            }
            if (string.charAt(i) == '"') {
                return false;
            }
            if (string.charAt(i) == '|') {
                return false;
            }
            if (string.charAt(i) == '?') {
                return false;
            }
        }
        return true;
    }

    public String getHost() {
        checkState();
        return host;
    }

    public int getPort() {
        checkState();
        return port;
    }

    public RemoteFileUsing getTable(String name) {
        checkState();
        if (name == null) {
            throw new IllegalArgumentException("Null name");
        }
        if (name.equals("")) {
            throw new IllegalArgumentException("Empty name");
        }
        if (!badSymbolCheck(name)) {
            throw new RuntimeException("Illegal characters");
        }
        if (socket.isClosed()) {
            throw new IllegalStateException("Socket is closed");
        }
        RemoteFileUsing newMap = used.get(name);
        if (newMap != null && !newMap.isClosed()) {
            return newMap;
        } else {
            writer.println(String.format("describe %s", name));
            ArrayList<Class<?>> columnTypes = null;
            try {
                String message = StringUtils.readLine(reader);
                if (message.equals(String.format("%s not exists", name))) {
                    return null;
                }
                columnTypes = StringUtils.stringToClassList(message);
            } catch (IOException e) {
                throw new RuntimeException("Error reading from socket: ", e);
            }
            try {
                newMap = new RemoteFileUsing(name, socket, this, columnTypes);
            } catch (IOException e) {
                throw new RuntimeException("Error assigning streams: ", e);
            }
            used.put(name, newMap);
            return newMap;
        }
    }

    public void activate(RemoteFileUsing table) {
        checkState();
        if (table == null) {
            throw new IllegalArgumentException("null table");
        }
        if (socket.isClosed()) {
            throw new IllegalStateException("Socket is closed");
        }
        writer.println(String.format("use %s", table.getName()));
        if (currentActive != null) {
            currentActive.setActive(false);
        }
        try {
            String message = StringUtils.readLine(reader);
            if (message.equals(String.format("using %s", table.getName()))) {
                table.setActive(true);
                return;
            } else if (message.equals(String.format("%s not exists", table.getName()))) {
                throw new IllegalStateException("Table no longer exists");
            } else if (message.contains("unsaved changes")) {
                throw new UnsavedChangesException(message);
            } else {
                throw new RuntimeException(String.format("Server side exception: %s", message));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading from socket: ", e);
        }
    }

    public RemoteFileUsing createTable(String name, List<Class<?>> columnTypes) throws IOException {
        checkState();
        if (name == null) {
            throw new IllegalArgumentException("Null name");
        }
        if (columnTypes == null) {
            throw new IllegalArgumentException("Null columnTypes");
        }
        if (columnTypes.size() == 0) {
            throw new ColumnFormatException("Can't create table with no columns");
        }
        for (Class<?> columnType : columnTypes) {
            boolean check = false;
            if (columnType == null) {
                throw new IllegalArgumentException("Null doesn't specify a type");
            }
            for (Class<?> allowedClass : Row.CLASSES) {
                if (columnType == allowedClass) {
                    check = true;
                    break;
                }
            }
            if (!check) {
                throw new IllegalArgumentException(String.format("wrong type (%s not supported)",
                        columnType.toString()));
            }
        }
        if (name.equals("")) {
            throw new IllegalArgumentException("Empty name");
        }
        if (!badSymbolCheck(name)) {
            throw new RuntimeException("Illegal characters");
        }
        if (socket.isClosed()) {
            throw new IllegalStateException("Socket is closed");
        }
        writer.println(String.format("create %s (%s)", name, StringUtils.classListToString(columnTypes)));
        try {
            String message = StringUtils.readLine(reader);
            if (message.equals(String.format("%s exists", name))) {
                RemoteFileUsing table = used.get(name);
                if (table == null || table.isClosed()) {
                    table = new RemoteFileUsing(name, socket, this, columnTypes);
                    used.put(name, table);
                    return table;
                } else {
                    return null;
                }
            } else if (message.equals("created")) {
                RemoteFileUsing table = new RemoteFileUsing(name, socket, this, columnTypes);
                used.put(name, table);
                return table;
            } else {
                throw new RuntimeException(String.format("Server side exception: %s", message));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading from socket: ", e);
        }
    }

    public void removeTable(String name) throws IOException {
        checkState();
        if (name == null) {
            throw new IllegalArgumentException("Null name");
        }
        if (name.equals("")) {
            throw new IllegalArgumentException("Empty name");
        }
        if (!badSymbolCheck(name)) {
            throw new RuntimeException("Illegal characters");
        }
        if (socket.isClosed()) {
            throw new IllegalStateException("Socket is closed");
        }
        RemoteFileUsing table = used.get(name);
        if (table != null) {
            table.close();
            used.remove(name);
        }
        writer.println(String.format("drop %s", name));
        try {
            String message = StringUtils.readLine(reader);
            if (message.equals(String.format("%s not exists", name))) {
                throw new IllegalStateException(String.format("%s not exists", name));
            } else if (!message.equals("dropped")) {
                throw new RuntimeException(String.format("Server side exception: %s", message));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading from socket: ", e);
        }
    }

    public Row deserialize(Table table, String value) throws ParseException {
        checkState();
        if (value == null) {
            throw new IllegalArgumentException("Null string as argument");
        }
        try {
            ArrayList<Class<?>> columnTypes = new ArrayList<>();
            int columnCount = table.getColumnsCount();
            for (int i = 0; i < columnCount; i++) {
                columnTypes.add(table.getColumnType(i));
            }
            JSONArray array = new JSONArray(value);
            if (columnCount != array.length()) {
                throw new ParseException("Array size mismatch", 0);
            }
            Row newList = new Row(columnTypes);
            for (int i = 0; i < columnCount; i++) {
                Object object = array.get(i);
                if (object.equals(null)) {
                    newList.setColumnAt(i, null);
                } else if (columnTypes.get(i) == Integer.class) {
                    if (object.getClass() == Integer.class) {
                        newList.setColumnAt(i, object);
                    } else {
                        throw new ParseException(String.format("Type mismatch: %s expected, %s found",
                                columnTypes.get(i).toString(), object.getClass().toString()), i);
                    }
                } else if (columnTypes.get(i) == Long.class) {
                    if (object.getClass() == Long.class) {
                        newList.setColumnAt(i, object);
                    } else if (object.getClass() == Integer.class) {
                        newList.setColumnAt(i, Long.valueOf(((Integer) object).longValue()));
                    } else {
                        throw new ParseException(String.format("Type mismatch: %s expected, %s found",
                                columnTypes.get(i).toString(), object.getClass().toString()), i);
                    }
                } else if (columnTypes.get(i) == Byte.class) {
                    if (object.getClass() == Integer.class) {
                        Integer number = (Integer) object;
                        if (number > Byte.MAX_VALUE || number < Byte.MIN_VALUE) {
                            throw new ParseException(String.format("Type mismatch: %s expected, %s found",
                                    columnTypes.get(i).toString(), object.getClass().toString()), i);
                        }
                        newList.setColumnAt(i, Byte.valueOf(number.byteValue()));
                    } else {
                        throw new ParseException(String.format("Type mismatch: %s expected, %s found",
                                columnTypes.get(i).toString(), object.getClass().toString()), i);
                    }
                } else if (columnTypes.get(i) == Boolean.class) {
                    if (object.getClass() == Boolean.class) {
                        newList.setColumnAt(i, object);
                    } else {
                        throw new ParseException(String.format("Type mismatch: %s expected, %s found",
                                columnTypes.get(i).toString(), object.getClass().toString()), i);
                    }
                } else if (columnTypes.get(i) == Float.class) {
                    if (object.getClass() == Double.class) {
                        newList.setColumnAt(i, Float.valueOf(((Double) object).floatValue()));
                    } else if (object.getClass() == Integer.class) {
                        newList.setColumnAt(i, Float.valueOf(((Integer) object).floatValue()));
                    } else {
                        throw new ParseException(String.format("Type mismatch: %s expected, %s found",
                                columnTypes.get(i).toString(), object.getClass().toString()), i);
                    }
                } else if (columnTypes.get(i) == Double.class) {
                    if (object.getClass() == Double.class) {
                        newList.setColumnAt(i, object);
                    } else if (object.getClass() == Integer.class) {
                        newList.setColumnAt(i, Float.valueOf(((Integer) object).floatValue()));
                    } else {
                        throw new ParseException(String.format("Type mismatch: %s expected, %s found",
                                columnTypes.get(i).toString(), object.getClass().toString()), i);
                    }
                } else if (columnTypes.get(i) == String.class) {
                    if (object.getClass() != String.class) {
                        throw new ParseException(String.format("Type mismatch: %s expected, %s found",
                                columnTypes.get(i).toString(), object.getClass().toString()), i);
                    } else {
                        newList.setColumnAt(i, object);
                    }
                }
            }
            return newList;
        } catch (JSONException e) {
            throw new ParseException(e.getMessage(), 0);
        } catch (RuntimeException e) {
            throw new RuntimeException(String.format("Error parsing string %s", value), e);
        }
    }

    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        checkState();
        int columnCount = table.getColumnsCount();
        Object[] objects = new Object[columnCount];
        for (int i = 0; i < columnCount; i++) {
            objects[i] = value.getColumnAt(i);
            if (objects[i] != null && objects[i].getClass() != table.getColumnType(i)) {
                throw new ColumnFormatException(String.format("Wrong format: %s expected, %s found",
                        table.getColumnType(i).toString(), objects[i].getClass().toString()));
            }
        }
        JSONArray array = null;
        try {
            array = new JSONArray(objects);
        } catch (JSONException e) {
            throw new ColumnFormatException(e.getMessage());
        }
        return array.toString();
    }

    public Row createFor(Table table) {
        checkState();
        ArrayList<Class<?>> columnTypes = new ArrayList<>();
        int columnCount = table.getColumnsCount();
        for (int i = 0; i < columnCount; i++) {
            columnTypes.add(table.getColumnType(i));
        }
        return new Row(columnTypes);
    }

    public Row createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        checkState();
        ArrayList<Class<?>> columnTypes = new ArrayList<>();
        int columnCount = table.getColumnsCount();
        for (int i = 0; i < columnCount; i++) {
            columnTypes.add(table.getColumnType(i));
        }
        Row newList = new Row(columnTypes);
        if (values.size() != columnCount) {
            throw new IndexOutOfBoundsException("Array size mismatch");
        }
        for (int i = 0; i < values.size(); i++) {
            newList.setColumnAt(i, values.get(i));
        }
        return newList;
    }

    public void close() throws IOException {
        if (!valid) {
            return;
        }
        try {
            for (Map.Entry<String, RemoteFileUsing> entry : used.entrySet()) {
                entry.getValue().close();
            }
            used = null;
            currentActive = null;
            socket.close();
        } finally {
            valid = false;
        }
    }

    public class UnsavedChangesException extends RuntimeException {
        public UnsavedChangesException(String message) {
            super(message);
        }
    }
}
