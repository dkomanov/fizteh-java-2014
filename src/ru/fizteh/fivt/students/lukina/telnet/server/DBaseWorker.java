package ru.fizteh.fivt.students.lukina.telnet.server;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class DBaseWorker {
    public PrintWriter out;
    private TableProvider tableProvider;
    private Table table = null;

    public DBaseWorker(TableProvider tableProvider, PrintWriter out) {
        this.tableProvider = tableProvider;
        this.out = out;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public void stopUsingTable() {
        table = null;
    }


    public void commit() throws Exception {
        if (table == null) {
            out.println("choose a table");
            return;
        }
        int changesNumber = table.commit();
        out.println(changesNumber);
    }

    public void create(String[] args) throws Exception {
        if (table != null && table.getNumberOfUncommittedChanges() != 0) {
            out.println(table.getNumberOfUncommittedChanges() + " unsaved changes");
            return;
        }
        String tableName = args[1];
        List<Class<?>> types = parseTypes(args);
        out.println("before");
        System.out.println(tableProvider == null);
        Table createdTable = tableProvider.createTable(tableName, types);
        out.println("after");

        if (createdTable == null) {
            out.println(tableName + " exists");
        } else {
            out.println("created");
        }
        stopUsingTable();
    }

    public void drop(String[] args) throws Exception {
        if (table != null && table.getNumberOfUncommittedChanges() != 0) {
            out.println(table.getNumberOfUncommittedChanges() + " unsaved changes");
            return;
        }
        String tableName = args[1];
        try {
            tableProvider.removeTable(tableName);
            out.println("dropped");
        } catch (IllegalStateException e) {
            out.println(tableName + "not exists");
        }
        stopUsingTable();
    }

    public void rollback() throws Exception {
        if (table == null) {
            out.println("choose a table");
            return;
        }
        int changesNumber = table.rollback();
        out.println(changesNumber);
    }

    public void show(String[] args) throws Exception {
        if (!args[1].equals("tables")) {
            throw new Exception("The name of this command is \"show tables\"");
        }
        List<String> tableNames = tableProvider.getTableNames();
        for (String tableName : tableNames) {
            int valuesNumber = tableProvider.getTable(tableName).size();
            out.println(tableName + " " + valuesNumber);
        }
    }

    public void use(String[] args) throws Exception {
        if (table != null && table.getNumberOfUncommittedChanges() != 0) {
            out.println(table.getNumberOfUncommittedChanges() + " unsaved changes");
            return;
        }
        String tableName = args[1];
        setTable(tableProvider.getTable(tableName));
        if (table == null) {
            out.println(tableName + " not exists");
        } else {
            out.println("using " + tableName);
        }
    }

    public void get(String[] args) {
        if (table == null) {
            out.println("choose a table");
            return;
        }
        String key = args[1];
        Storeable tableRawValue = table.get(key);
        if (tableRawValue != null) {
            out.println("found");
            String stringValue = tableProvider.serialize(table, tableRawValue);
            out.println(stringValue);
        } else {
            out.println("not found");
        }
    }

    public void list() throws Exception {
        if (table == null) {
            out.println("choose a table");
            return;
        }
        List<String> keys = table.list();
        String allKeys = "";
        for (String key : keys) {
            if (allKeys.length() > 0) {
                allKeys += ", ";
            }
            allKeys += key;
        }
        out.println(allKeys);
    }

    public void put(String[] args) throws ParseException {
        if (table == null) {
            out.println("choose a table");
            return;
        }
        String key = args[1];
        String stringValue = "";
        for (int argsIndex = 2; argsIndex < args.length; ++argsIndex) {
            stringValue += (args[argsIndex]);
        }
        Storeable tableRawValue = tableProvider.deserialize(table, stringValue);
        Storeable tableRawPrevValue = table.put(key, tableRawValue);
        if (tableRawPrevValue == null) {
            out.println("new");
        } else {
            out.println("owerwrite");
            String stringOldValue = tableProvider.serialize(table, tableRawPrevValue);
            out.println(stringOldValue);
        }
    }

    public void remove(String[] args) {
        if (table == null) {
            out.println("choose a table");
            return;
        }
        String key = args[1];
        Storeable value = table.remove(key);
        if (value == null) {
            out.println("not found");
        } else {
            out.println("removed");
        }
    }

    public void size() throws Exception {
        if (table == null) {
            out.println("choose a table");
            return;
        }
        int size = table.size();
        out.println(size);
    }

    public Class<?> getClassFromString(String name) {
        switch (name) {
            case "int":
                return Integer.class;
            case "long":
                return Long.class;
            case "byte":
                return Byte.class;
            case "float":
                return Float.class;
            case "double":
                return Double.class;
            case "boolean":
                return Boolean.class;
            case "String":
                return String.class;
            default:
                throw new RuntimeException("Incorrect type " + name);
        }
    }

    private List<Class<?>> parseTypes(String[] args) throws Exception {
        List<Class<?>> types = new ArrayList<>();
        List<String> typeNames = new ArrayList<>();
        for (int argsIndex = 2; argsIndex < args.length; argsIndex++) {
            typeNames.add(args[argsIndex]);
            System.out.println(args[argsIndex]);
        }
        typeNames = deleteCommas(typeNames);
        for (String typeName : typeNames) {
            System.out.println(typeName);
            types.add(getClassFromString(typeName));
        }
        return types;
    }

    private List<String> deleteCommas(List<String> typeNames) throws Exception {
        int firstIndex = 0;
        out.println("in del commas");
        if (typeNames.get(firstIndex).startsWith("(")) {
            typeNames.add(firstIndex, typeNames.get(firstIndex).substring(1));
            typeNames.remove(firstIndex + 1);
        } else {
            throw new Exception("Wrong format of command");
        }
        int lastIndex = typeNames.size() - 1;
        if (typeNames.get(lastIndex).endsWith(")")) {
            int length = typeNames.get(lastIndex).length();
            typeNames.add(lastIndex, typeNames.get(lastIndex).substring(0, length - 1));
            typeNames.remove(lastIndex + 1);
        } else {
            throw new Exception("Wrong format of command");
        }
        return typeNames;
    }

}
