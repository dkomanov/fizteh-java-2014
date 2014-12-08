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
import java.util.List;
import java.util.Scanner;

public class RealRemoteTable implements Table, Closeable {
    private String name;
    private Socket server;
    private Scanner input;
    private PrintStream output;
    private RemoteTableProvider parent;


    public RealRemoteTable(String name, String hostname, int port, RemoteTableProvider parent) throws IOException {
        this.name = name;
        this.parent = parent;
        server = new Socket(InetAddress.getByName(hostname), port);
        input = new Scanner(server.getInputStream());
        output = new PrintStream(server.getOutputStream());
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key == null || value == null) {
            throw new IllegalArgumentException("null argument");
        }
        output.println("put " + key + " " + Serializator.serialize(this, value));
        String message = input.nextLine();
        if ("new".equals(message)) {
            return null;
        } else {
            String oldValue = input.nextLine();
            try {
                return parent.deserialize(this, oldValue);
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
        output.println("get " + key);
        String message = input.nextLine();
        if ("found".equals(message)) {
            String value = input.nextLine();
            try {
                return parent.deserialize(this, value);
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
        output.println("remove " + key);
        String message = input.nextLine();
        if ("not found".equals(message)) {
            return null;
        } else {
            String value = input.nextLine();
            try {
                return parent.deserialize(this, value);
            } catch (ParseException e) {
                return null;
            }
        }
    }

    @Override
    public int size() {
        output.println("size");
        return Integer.parseInt(input.nextLine());
    }

    @Override
    public List<String> list() {
        return null;
    }

    @Override
    public int commit() throws IOException {
        output.println("commit");
        return Integer.parseInt(input.nextLine());
    }

    @Override
    public int rollback() {
        output.println("rollback");
        return Integer.parseInt(input.nextLine());
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return 0; //tmp
    }

    @Override
    public int getColumnsCount() {
        output.println("describe " + name);
        String message = input.nextLine();
        return message.split(" ").length;
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        output.println("describe " + name);
        String message = input.nextLine();
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
