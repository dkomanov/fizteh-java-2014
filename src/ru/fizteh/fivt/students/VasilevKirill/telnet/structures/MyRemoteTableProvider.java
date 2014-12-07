package ru.fizteh.fivt.students.VasilevKirill.telnet.structures;

import org.json.JSONArray;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Storeable.StoreableParser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.ParseException;
import java.util.*;

/**
 * Created by Kirill on 07.12.2014.
 */
public class MyRemoteTableProvider implements RemoteTableProvider {
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private String workingTable;

    MyRemoteTableProvider(Socket socket) throws IOException {
        this.socket = socket;
        try {
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new IOException("RemoteTableProvider: failed to open output stream");
        }
    }

    @Override
    public void close() throws IOException {
        out.writeUTF("close");
        out.flush();
        out.close();
    }

    @Override
    public Table getTable(String name) {
        MyRemoteTable table = null;
        if (name == null) {
            throw new IllegalArgumentException();
        }
        try {
            out.writeUTF("get " + name);
            out.flush();
            int numRows = in.readInt(); //number
            if (numRows == -1) {
                String receivedError = in.readUTF();
                throw new IOException(receivedError);
            }
            String typeStr = in.readUTF(); //type1 type2 type3
            String[] typesStr = typeStr.split("\\s+");
            Class[] types = new Class[typesStr.length];
            for (int i = 0; i < types.length; ++i) {
                types[i] = Class.forName(typesStr[i]);
            }
            table = new MyRemoteTable(socket, name, Arrays.asList(types));
            for (int i = 0; i < numRows; ++i) {
                String row = in.readUTF(); //key [value1,value2,value3]
                String[] splitValue = row.split("\\s+");
                String key = splitValue[0];
                Storeable value = StoreableParser.stringToStoreable(splitValue[1], types);
                table.fulfilData(key, value);
            }
        } catch (ParseException e) {
            System.out.println("RemoteTableProvider: failed to parse values");
        } catch (ClassNotFoundException e) {
            System.out.println("RemoteTableProvider: failed to cast classes");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return table;
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        StringBuilder types = new StringBuilder();
        for (Class it : columnTypes) {
            types.append(it.getSimpleName());
        }
        try {
            out.writeUTF("create " + name + new String(types));
            int num = in.readInt();
            if (num == -1) {
                String receivedError = in.readUTF();
                throw new IOException(receivedError);
            }
        } catch (IOException e) {
            System.out.println("Create: failed to send data");
        }
        return null;
    }

    @Override
    public void removeTable(String name) throws IOException {
        out.writeUTF("remove " + name);
        int num = in.readInt();
        if (num == -1) {
            String receivedError = in.readUTF();
            throw new IOException(receivedError);
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        Class[] typeList = new Class[table.getColumnsCount()];
        for (int i = 0; i < typeList.length; ++i) {
            typeList[i] = table.getColumnType(i);
        }
        return StoreableParser.stringToStoreable(value, typeList);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        List<Object> data = new ArrayList<>();
        int columnsCount = table.getColumnsCount();
        value.getColumnAt(columnsCount - 1); //for checking columns number
        for (int i = 0; i < columnsCount; ++i) {
            data.add(value.getColumnAt(i));
        }
        JSONArray arr = new JSONArray(data);
        return arr.toString();
    }

    @Override
    public Storeable createFor(Table table) {
        Class[] typeList = new Class[table.getColumnsCount()];
        for (int i = 0; i < typeList.length; ++i) {
            typeList[i] = table.getColumnType(i);
        }
        return new MyStorable(typeList);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        Class[] typeList = new Class[table.getColumnsCount()];
        for (int i = 0; i < typeList.length; ++i) {
            typeList[i] = table.getColumnType(i);
        }
        Storeable result = new MyStorable(typeList);
        if (typeList.length != values.size()) {
            throw new IndexOutOfBoundsException();
        }
        for (int i = 0; i < values.size(); ++i) {
            result.setColumnAt(i, values.get(i));
        }
        return result;
    }

    @Override
    public List<String> getTableNames() {
        try {
            List<String> retValue = new ArrayList<>();
            out.writeUTF("show");
            int numTables = in.readInt();
            if (numTables == -1) {
                throw new IOException(in.readUTF());
            }
            for (int i = 0; i < numTables; ++i) {
                retValue.add(in.readUTF());
            }
            return retValue;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Table getWorkingTable() {
        return getTable(workingTable);
    }

    public void setWorkingTable(String name) {
        if (name == workingTable) {
            return;
        }
        System.out.println(getTable(workingTable).getNumberOfUncommittedChanges() + " unsaved");
        System.out.println("using " + name);
        workingTable = name;
    }

    public void handleTable(String[] args) throws IOException {
        if (workingTable == null) {
            System.out.println("no table");
            return;
        }
        Table workingTable = getWorkingTable();
        Class[] typeList = new Class[workingTable.getColumnsCount()];
        for (int i = 0; i < typeList.length; ++i) {
            typeList[i] = workingTable.getColumnType(i);
        }
        try {
            //I was too lazy when I was doing it
            Storeable result = null;
            switch (args[0]) {
                case "put":
                    if (args.length != 3) {
                        throw new IOException("Put: wrong arguments");
                    }
                    result = workingTable.put(args[1], StoreableParser.stringToStoreable(args[2], typeList));
                    if (result == null) {
                        System.out.println("new");
                    } else {
                        System.out.println("overwrite");
                    }
                    break;
                case "get":
                    if (args.length != 2) {
                        throw new IOException("Get: wrong arguments");
                    }
                    result = workingTable.get(args[1]);
                    if (result == null) {
                        System.out.println("not found");
                    } else {
                        System.out.println("found\n" + result.toString());
                    }
                    break;
                case "remove":
                    if (args.length != 2) {
                        throw new IOException("Remove: wrong arguments");
                    }
                    result = workingTable.remove(args[1]);
                    if (result == null) {
                        System.out.println("not found");
                    } else {
                        System.out.println("removed");
                    }
                    break;
                case "size":
                    if (args.length != 1) {
                        throw new IOException("Size: wrong arguments");
                    }
                    System.out.println(workingTable.size());
                    break;
                case "commit":
                    if (args.length != 1) {
                        throw new IOException("Commit: wrong arguments");
                    }
                    System.out.println(workingTable.commit());
                    break;
                case "rollback":
                    if (args.length != 1) {
                        throw new IOException("Rollback: wrong arguments");
                    }
                    System.out.println(workingTable.rollback());
                    break;
                case "list":
                    if (args.length != 1) {
                        throw new IOException("List: wrong arguments");
                    }
                    List<String> keys = workingTable.list();
                    for (String key : keys) {
                        System.out.print(key + " ");
                    }
                    break;
                default:
            }
        } catch (ParseException e) {
            System.out.println("Failed to parse");
        }
    }
}
