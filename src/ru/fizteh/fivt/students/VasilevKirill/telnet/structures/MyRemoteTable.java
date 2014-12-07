package ru.fizteh.fivt.students.VasilevKirill.telnet.structures;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kirill on 07.12.2014.
 */
public class MyRemoteTable implements Table {
    private Socket socket;
    private DataOutputStream out;
    private String name;
    private Map<String, Storeable> data = new HashMap<>();
    int numUnsavedChanges = 0;
    private List<Class<?>> typeList;

    MyRemoteTable(Socket socket, String name, List<Class<?>> typeList) throws IOException {
        this.socket = socket;
        this.name = name;
        this.typeList = typeList;
        out = new DataOutputStream(socket.getOutputStream());
    }

    public void fulfilData(String key, Storeable value) {
        data.put(key, value);
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        Storeable retValue = data.get(key);
        data.put(key, value);
        try {
            out.writeUTF("put " + key + " " + value.toString());
        } catch (IOException e) {
            System.out.println("Put: failed to send data");
        }
        numUnsavedChanges++;
        return retValue;
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        Storeable retValue = data.get(key);
        if (retValue == null) {
            return null;
        }
        data.remove(key);
        try {
            out.writeUTF("remove " + key);
        } catch (IOException e) {
            System.out.println("Remove: failed to send data");
        }
        numUnsavedChanges++;
        return retValue;
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public List<String> list() {
        return new ArrayList<>(data.keySet());
    }

    @Override
    public int commit() throws IOException {
        try {
            out.writeUTF("commit");
        } catch (IOException e) {
            System.out.println("Commit: failed to send data");
            throw new IOException(e.getMessage());
        }
        int retValue = numUnsavedChanges;
        numUnsavedChanges = 0;
        return retValue;
    }

    @Override
    public int rollback() {
        try {
            out.writeUTF("rollback");
        } catch (IOException e) {
            System.out.println("Rollback: failed to send data");
        }
        int retValue = numUnsavedChanges;
        numUnsavedChanges = 0;
        return retValue;
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return numUnsavedChanges;
    }

    @Override
    public int getColumnsCount() {
        return typeList.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= typeList.size()) {
            throw new IndexOutOfBoundsException();
        }
        return typeList.get(columnIndex);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Storeable get(String key) {
        return data.get(key);
    }
}
