package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage;

import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.StoreablePackage.TypesUtils;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.Exceptions.StopExecutionException;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class RealRemoteTable implements Table, Closeable {
    private String name;
    private Socket server;
    private Scanner inputStream;
    private PrintStream outputStream;
    private RemoteTableProvider parentProvider;


    public RealRemoteTable(String name, String hostname, int port, RemoteTableProvider parent) throws IOException {
        this.name = name;
        this.parentProvider = parent;
        server = new Socket(InetAddress.getByName(hostname), port);
        inputStream = new Scanner(server.getInputStream());
        outputStream = new PrintStream(server.getOutputStream());
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key == null || value == null) {
            throw new IllegalArgumentException("null argument");
        }
        outputStream.println("put " + name + " " + key + " " + Serializator.serialize(this, value));
        if (!inputStream.hasNextLine()) {
            throw new StopExecutionException();
        }
        String answerFromServer = inputStream.nextLine();
        if ("new".equals(answerFromServer)) {
            return null;
        } else {
            String oldValue = inputStream.nextLine();
            try {
                return parentProvider.deserialize(this, oldValue);
            } catch (ParseException e) {
                throw new ColumnFormatException(e.getMessage());
            }
        }
    }

    @Override
    public Storeable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("null argument");
        }
        outputStream.println("get " + name + " " + key);
        if (!inputStream.hasNextLine()) {
            throw new StopExecutionException();
        }
        String answerFromServer = inputStream.nextLine();
        if ("found".equals(answerFromServer)) {
            String value = inputStream.nextLine();
            try {
                return parentProvider.deserialize(this, value);
            } catch (ParseException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("null argument");
        }
        outputStream.println("remove " + name + " " + key);
        if (!inputStream.hasNextLine()) {
            throw new StopExecutionException();
        }
        String answerFromServer = inputStream.nextLine();
        if ("not found".equals(answerFromServer)) {
            return null;
        } else {
            String value = inputStream.nextLine();
            System.err.println(value);
            try {
                return parentProvider.deserialize(this, value);
            } catch (ParseException e) {
                return null;
            }
        }
    }

    @Override
    public int size() {
        outputStream.println("size " + name);
        if (!inputStream.hasNextLine()) {
            throw new StopExecutionException();
        }
        String answerFromServer = inputStream.nextLine();
        return Integer.parseInt(answerFromServer);
    }

    @Override
    public List<String> list() {
        outputStream.println("list " + name);
        if (!inputStream.hasNextLine()) {
            throw new StopExecutionException();
        }
        String answerFromServer = inputStream.nextLine();
        String[] parsedKeys = answerFromServer.split(", ");
        List<String> result = new ArrayList<>();
        result.addAll(Arrays.asList(parsedKeys));
        return result;
    }

    @Override
    public int commit() throws IOException {
        outputStream.println("commit " + name);
        if (!inputStream.hasNextLine()) {
            throw new StopExecutionException();
        }
        String answerFromServer = inputStream.nextLine();
        return Integer.parseInt(answerFromServer);
    }

    @Override
    public int rollback() {
        outputStream.println("rollback " + name);
        if (!inputStream.hasNextLine()) {
            throw new StopExecutionException();
        }
        String answerFromServer = inputStream.nextLine();
        return Integer.parseInt(answerFromServer);
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        outputStream.println("uncomm-changes " + name);
        if (!inputStream.hasNextLine()) {
            throw new StopExecutionException();
        }
        String answerFromServer = inputStream.nextLine();
        return Integer.parseInt(answerFromServer);
    }

    @Override
    public int getColumnsCount() {
        outputStream.println("describe " + name);
        if (!inputStream.hasNextLine()) {
            throw new StopExecutionException();
        }
        String answerFromServer = inputStream.nextLine();
        return answerFromServer.split(" ").length;
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        outputStream.println("describe " + name);
        if (!inputStream.hasNextLine()) {
            throw new StopExecutionException();
        }
        String answerFromServer = inputStream.nextLine();
        String[] types = answerFromServer.split(" ");
        return TypesUtils.toTypeList(types).get(columnIndex);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void close() throws IOException {
        rollback();
        server.close();
    }

    public TableProvider getTableProvider() {
        return parentProvider;
    }
}
