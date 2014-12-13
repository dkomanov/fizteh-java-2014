package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.StoreablePackage.Serializator;
import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.StoreablePackage.TypesUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
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
        outputStream.println("put " + key + " " + Serializator.serialize(this, value));
        String message = inputStream.nextLine();
        if ("new".equals(message)) {
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
        outputStream.println("get " + key);
        String message = inputStream.nextLine();
        if ("found".equals(message)) {
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
        outputStream.println("remove " + key);
        String message = inputStream.nextLine();
        if ("not found".equals(message)) {
            return null;
        } else {
            String value = inputStream.nextLine();
            try {
                return parentProvider.deserialize(this, value);
            } catch (ParseException e) {
                return null;
            }
        }
    }

    @Override
    public int size() {
        outputStream.println("size");
        return Integer.parseInt(inputStream.nextLine());
    }

    @Override
    public List<String> list() {
        outputStream.println("list " + name);
        String answerFromServer = inputStream.nextLine();
        System.err.println(answerFromServer);
        String[] parsedKeys = answerFromServer.split(", ");
        List<String> result = new ArrayList<String>();
        for (String oneKey : parsedKeys) {
            result.add(oneKey);
        }
        return result;
    }

    @Override
    public int commit() throws IOException {
        outputStream.println("commit");
        return Integer.parseInt(inputStream.nextLine());
    }

    @Override
    public int rollback() {
        outputStream.println("rollback");
        return Integer.parseInt(inputStream.nextLine());
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return 0; //tmp
    }

    @Override
    public int getColumnsCount() {
        outputStream.println("describe " + name);
        String message = inputStream.nextLine();
        return message.split(" ").length;
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        outputStream.println("describe " + name);
        String message = inputStream.nextLine();
        String[] types = message.split(" ");
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
}
