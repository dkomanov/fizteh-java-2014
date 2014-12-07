package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.TypeTransformer;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * @author AlexeyZhuravlev
 */
class MyRemoteTable implements Table, Closeable {


    String name;
    Scanner in;
    PrintStream out;
    RemoteTableProvider provider;
    Socket socket;

    MyRemoteTable(String passedHost, int passedPort,
                  String passedName, RemoteTableProvider passedProvider) throws IOException {
        name = passedName;
        socket = new Socket(passedHost, passedPort);
        in =  new Scanner(socket.getInputStream());
        out = new PrintStream(socket.getOutputStream());
        provider = passedProvider;
        out.println("use " + name);
        in.nextLine();
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        String serialized = provider.serialize(this, value);
        out.println("put " + key + " " + serialized);
        String result = in.nextLine();
        if (result.equals("new")) {
            return null;
        }
        if (result.equals("overwrite")) {
            String old = in.nextLine();
            try {
                return provider.deserialize(this, old);
            } catch (ParseException e) {
                throw new ColumnFormatException(e.getMessage());
            }
        } else {
            result = result.substring(11, result.length() - 1);
            throw new ColumnFormatException(result);
        }
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        out.println("remove " + key);
        String result = in.nextLine();
        switch (result) {
            case "not found":
                return null;
            case "removed":
                String old = in.nextLine();
                try {
                    return provider.deserialize(this, old);
                } catch (ParseException e) {
                    return null;
                }
            default:
                return null;
        }
    }

    @Override
    public int size() {
        out.println("size");
        String result = in.nextLine();
        return Integer.parseInt(result);
    }

    @Override
    public List<String> list() {
        out.println("list");
        String list = in.nextLine();
        List<String> result = new ArrayList<>();
        Collections.addAll(result, list.split(", "));
        return result;
    }

    @Override
    public int commit() throws IOException {
        out.println("commit");
        String result = in.nextLine();
        return Integer.parseInt(result);
    }

    @Override
    public int rollback() {
        out.println("rollback");
        String result = in.nextLine();
        return Integer.parseInt(result);
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        out.println("use " + name);
        String answer = in.nextLine();
        if (answer.equals("using " + name)) {
            return 0;
        } else {
            return Integer.parseInt(answer.split(" ")[0]);
        }
    }

    @Override
    public int getColumnsCount() {
        out.println("describe " + name);
        String signature = in.nextLine();
        return signature.split(" ").length;
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        out.println("describe " + name);
        String signature = in.nextLine();
        try {
            List<Class<?>> types = TypeTransformer.typeListFromString(signature);
            return types.get(columnIndex);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Storeable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        out.println("get " + key);
        String answer = in.nextLine();
        if (answer.equals("not found")) {
            return null;
        } else {
            String value = in.nextLine();
            try {
                return provider.deserialize(this, value);
            } catch (ParseException e) {
                return null;
            }
        }
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

}
