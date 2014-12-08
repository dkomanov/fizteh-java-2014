package ru.fizteh.fivt.students.ilivanov.Telnet;

import ru.fizteh.fivt.students.ilivanov.Telnet.Interfaces.ColumnFormatException;
import ru.fizteh.fivt.students.ilivanov.Telnet.Interfaces.Storeable;
import ru.fizteh.fivt.students.ilivanov.Telnet.Interfaces.Table;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class RemoteFileUsing implements Table, AutoCloseable {
    private Socket socket;
    private BufferedReader reader;
    private PrintStream writer;
    private boolean active;
    private String name;
    private RemoteFileMapProvider provider;
    private ArrayList<Class<?>> columnTypes;
    private boolean valid;

    public RemoteFileUsing(String name, Socket socket, RemoteFileMapProvider provider) throws IOException {
        active = false;
        this.socket = socket;
        this.name = name;
        this.provider = provider;
        valid = true;
        columnTypes = new ArrayList<>();
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintStream(socket.getOutputStream());
    }

    public RemoteFileUsing(String name, Socket socket, RemoteFileMapProvider provider, List<Class<?>> columnTypes)
            throws IOException {
        this(name, socket, provider);
        this.columnTypes = new ArrayList<>(columnTypes);
    }

    private void checkState() {
        if (!valid) {
            throw new IllegalStateException("Table is closed");
        }
    }

    public boolean checkColumnTypes(Storeable list) {
        checkState();
        try {
            for (int i = 0; i < columnTypes.size(); i++) {
                if (list.getColumnAt(i) != null && columnTypes.get(i) != list.getColumnAt(i).getClass()) {
                    return false;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        try {
            list.getColumnAt(columnTypes.size());
            return false;
        } catch (IndexOutOfBoundsException e) {
            return true;
        }
    }

    private boolean newLineCheck(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '\n') {
                return false;
            }
        }
        return true;
    }

    private boolean whiteSpaceCheck(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (Character.isWhitespace(string.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public void setActive(boolean value) {
        checkState();
        active = value;
    }

    public boolean isActive() {
        checkState();
        return active;
    }

    public String getName() {
        checkState();
        return name;
    }

    public void setColumnTypes(List<Class<?>> columnTypes) {
        checkState();
        this.columnTypes = new ArrayList<>(columnTypes);
    }

    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        checkState();
        if (socket.isClosed()) {
            throw new IllegalStateException("Socket is closed");
        }
        if (key == null) {
            throw new IllegalArgumentException("Null pointer instead of string");
        }
        if (value == null) {
            throw new IllegalArgumentException("Null value");
        }
        if (key.equals("")) {
            throw new IllegalArgumentException("Empty key");
        }
        if (!newLineCheck(key)) {
            throw new IllegalArgumentException("New-line in key or value");
        }
        if (!checkColumnTypes(value)) {
            throw new ColumnFormatException("Type mismatch");
        }
        if (!whiteSpaceCheck(key)) {
            throw new IllegalArgumentException("Whitespace not allowed in key");
        }
        if (!active) {
            provider.activate(this);
        }
        String serializedValue = provider.serialize(this, value);
        writer.println(String.format("put %s %s", key, serializedValue));
        String message = null;
        try {
            message = StringUtils.readLine(reader);
            if (message.equals("new")) {
                return null;
            }
            if (message.equals("overwrite")) {
                message = StringUtils.readLine(reader);
            } else {
                throw new RuntimeException(String.format("Server side exception: %s", message));
            }
            return provider.deserialize(this, message);
        } catch (IOException e) {
            throw new RuntimeException("Error reading from socket: ", e);
        } catch (ParseException e) {
            throw new RuntimeException(String.format("Server side exception: %s", message));
        }
    }

    public Storeable get(String key) {
        checkState();
        if (socket.isClosed()) {
            throw new IllegalStateException("Socket is closed");
        }
        if (key == null) {
            throw new IllegalArgumentException("Null pointer instead of string");
        }
        if (key.equals("")) {
            throw new IllegalArgumentException("Empty key");
        }
        if (!active) {
            provider.activate(this);
        }
        writer.println(String.format("get %s", key));
        String message = null;
        try {
            message = StringUtils.readLine(reader);
            if (message.equals("not found")) {
                return null;
            }
            if (message.equals("found")) {
                message = StringUtils.readLine(reader);
            } else {
                throw new RuntimeException(String.format("Server side exception: %s", message));
            }
            return provider.deserialize(this, message);
        } catch (IOException e) {
            throw new RuntimeException("Error reading from socket: ", e);
        } catch (ParseException e) {
            throw new RuntimeException(String.format("Server side exception: %s", message));
        }
    }

    public Storeable remove(String key) {
        checkState();
        if (socket.isClosed()) {
            throw new IllegalStateException("Socket is closed");
        }
        if (key == null) {
            throw new IllegalArgumentException("Null pointer instead of string");
        }
        if (key.equals("")) {
            throw new IllegalArgumentException("Empty key");
        }
        if (!active) {
            provider.activate(this);
        }
        writer.println(String.format("get %s", key));
        String message = null;
        try {
            message = StringUtils.readLine(reader);
            if (message.equals("not found")) {
                return null;
            }
            if (message.equals("found")) {
                message = StringUtils.readLine(reader);
            } else {
                throw new RuntimeException(String.format("Server side exception: %s", message));
            }
            Storeable result = provider.deserialize(this, message);
            writer.println(String.format("remove %s", key));
            message = StringUtils.readLine(reader);
            if (message.equals("removed") || message.equals("not found")) {
                return result;
            } else {
                throw new RuntimeException(String.format("Server side exception: %s", message));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading from socket: ", e);
        } catch (ParseException e) {
            throw new RuntimeException(String.format("Server side exception: %s", message));
        }
    }

    public int size() {
        checkState();
        if (socket.isClosed()) {
            throw new IllegalStateException("Socket is closed");
        }
        if (!active) {
            provider.activate(this);
        }
        writer.println("size");
        String message = null;
        try {
            message = StringUtils.readLine(reader);
            return Integer.parseInt(message);
        } catch (IOException e) {
            throw new RuntimeException("Error reading from socket: ", e);
        } catch (NumberFormatException e) {
            throw new RuntimeException(String.format("Server side exception: %s", message));
        }
    }

    public int commit() throws IOException {
        checkState();
        if (socket.isClosed()) {
            throw new IllegalStateException("Socket is closed");
        }
        if (!active) {
            provider.activate(this);
        }
        writer.println("commit");
        String message = null;
        try {
            message = StringUtils.readLine(reader);
            return Integer.parseInt(message);
        } catch (IOException e) {
            throw new RuntimeException("Error reading from socket: ", e);
        } catch (NumberFormatException e) {
            throw new RuntimeException(String.format("Server side exception: %s", message));
        }
    }

    public int rollback() {
        checkState();
        if (socket.isClosed()) {
            throw new IllegalStateException("Socket is closed");
        }
        if (!active) {
            provider.activate(this);
        }
        writer.println("rollback");
        String message = null;
        try {
            message = StringUtils.readLine(reader);
            return Integer.parseInt(message);
        } catch (IOException e) {
            throw new RuntimeException("Error reading from socket: ", e);
        } catch (NumberFormatException e) {
            throw new RuntimeException(String.format("Server side exception: %s", message));
        }
    }

    public int getColumnsCount() {
        checkState();
        return columnTypes.size();
    }

    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        checkState();
        if (columnIndex >= getColumnsCount() || columnIndex < 0) {
            throw new IndexOutOfBoundsException(String.format("Index out of bounds: array size %d, found %d",
                    columnTypes.size(), columnIndex));
        }
        return columnTypes.get(columnIndex);
    }

    public boolean isClosed() {
        return !valid;
    }

    public void close() {
        if (!valid) {
            return;
        }
        try {
            if (!socket.isClosed()) {
                rollback();
            }
        } finally {
            valid = false;
        }
    }
}