package ru.fizteh.fivt.students.VasilevKirill.parallel.Storeable.junit.multimap;

import org.json.JSONArray;
import org.json.JSONException;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.VasilevKirill.parallel.SharedMyTable;
import ru.fizteh.fivt.students.VasilevKirill.parallel.Storeable.MyStorable;
import ru.fizteh.fivt.students.VasilevKirill.parallel.Storeable.StoreableParser;
import ru.fizteh.fivt.students.VasilevKirill.parallel.Storeable.junit.multimap.db.shell.RmCommand;
import ru.fizteh.fivt.students.VasilevKirill.parallel.Storeable.junit.multimap.db.shell.Status;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Kirill on 19.10.2014.
 */
public class MultiMap implements TableProvider {
    private String workingDirectory;
    private String workingTable;
    private Map<String, SharedMyTable> tables;
    private ReentrantReadWriteLock providerLock;

    public MultiMap(String directory) throws IOException {
        workingDirectory = directory == null ? new File("").getCanonicalPath() : directory;
        File workingFile = new File(workingDirectory);
        if (!workingFile.exists()) {
            if (!workingFile.mkdir()) {
                throw new IOException("Can't create the directory");
            }
        }
        if (!workingFile.isDirectory()) {
            throw new IOException(directory + " is not a directory");
        }
        tables = new HashMap<String, SharedMyTable>();
        providerLock = new ReentrantReadWriteLock();
        File[] tableDirectories = new File(workingDirectory).listFiles();
        for (File it : tableDirectories) {
            File signatures = new File(it.getCanonicalPath() + File.separator + "signature.tsv");
            if (!signatures.exists()) {
                throw new IOException("No signature file for table " + it.getName());
            }
            Class[] sig = readSignatures(signatures);
            MultiTable inputTable = new MultiTable(it, this, sig);
            tables.put(it.getName(), new SharedMyTable(inputTable));
        }
    }

    @Override
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        providerLock.readLock().lock();
        Table retValue = tables.get(name);
        providerLock.readLock().unlock();
        return retValue;
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) {
        if (name == null || columnTypes == null) {
            throw new IllegalArgumentException();
        }
        Class[] typeList = new Class[columnTypes.size()];
        for (int i = 0; i < columnTypes.size(); ++i) {
            typeList[i] = columnTypes.get(i);
        }
        providerLock.writeLock().lock();
        try {
            if (!addTable(name, typeList)) {
                return null;
            }
            MultiTable retTable = new MultiTable(new File(workingDirectory + File.separator + name), this, typeList);
            tables.put(name, new SharedMyTable(retTable));
            return retTable;
        } catch (IOException e) {
            if (e.getMessage().substring(0, 5).equals("Can't")) {
                throw new IllegalArgumentException();
            }
        } finally {
            providerLock.writeLock().unlock();
        }
        return null;
    }

    @Override
    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (tables.get(name) == null) {
            throw new IllegalStateException();
        }
        providerLock.writeLock().lock();
        try {
            oldRemoveTable(name);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            providerLock.writeLock().unlock();
        }
    }

    public void setTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (tables.get(name) == null) {
            throw new IllegalStateException();
        } else {
            providerLock.writeLock().lock();
            try {
                if (workingTable != null) {
                    SharedMyTable currentTable = tables.get(workingTable);
                    if (currentTable == null) {
                        throw new IOException("Multimap: current table is null");
                    }
                    if (currentTable.getNumberOfUncommittedChanges() != 0) {
                        System.out.println(currentTable.getNumberOfUncommittedChanges() + " unsaved changes");
                    }
                }
                setWorkingTable(name);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } finally {
                providerLock.writeLock().unlock();
            }
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

    //Old version of method. Saved for compatibility.
    public boolean addTable(String name, Class[] typeList) throws IOException {
        if (name == null) {
            throw new IOException("Wrong arguments");
        }
        if (!tables.containsKey(name)) {
            File newDir = new File(workingDirectory + File.separator + name);
            if (!newDir.exists()) {
                if (!newDir.mkdir()) {
                    throw new IOException("Can't create the directory: " + newDir.getName());
                }
            }
            MultiTable inputTable = new MultiTable(newDir, this, typeList);
            tables.put(name, new SharedMyTable(inputTable));
            return true;
        } else {
            return false;
        }
    }

    //Old version of method. Saved for compatibility.
    public boolean oldRemoveTable(String name) throws IOException {
        if (name == null) {
            throw new IOException("Wrong argument");
        }
        if (tables.containsKey(name)) {
            String[] rmArgs = {"rm", "-r", tables.get(name).getMultiTable().getTableDirectory().getAbsolutePath()};
            Status status = null;
            if (new RmCommand().execute(rmArgs, status) == 1) {
                throw new IOException("Can't delete the table");
            }
            tables.remove(name);
            return true;
        } else {
            return false;
        }
    }

    //Old version of method. Saved for compatibility.
    public boolean setWorkingTable(String name) throws IOException {
        if (name == null) {
            throw new IOException("Wrong argument");
        }
        if (tables.containsKey(name)) {
            workingTable = name;
            return true;
        } else {
            return false;
        }
    }

    //Old version of method. Saved for compatibility.
    public boolean handleTable(String[] args) throws IOException {
        if (workingTable == null) {
            return false;
        }
        SharedMyTable multiTable = tables.get(workingTable);
        if (multiTable == null) {
            throw new IOException("MultiMap: Can't handle the table");
        }
        Class[] typeList = multiTable.getMultiTable().getTypeList();
        Storeable result = null;
        switch (args[0]) {
            case "put":
                try {
                    StringBuilder buffer = new StringBuilder();
                    for (int i = 2; i < args.length; ++i) {
                        buffer.append(args[i]);
                    }
                    String inputValue = new String(buffer);
                    Storeable input = StoreableParser.stringToStoreable(inputValue, typeList);
                    result = multiTable.put(args[1], input);
                    if (result == null) {
                        System.out.println("new");
                    } else {
                        System.out.println("overwrite\n" + result);
                    }
                } catch (ParseException e) {
                    throw new IOException("wrong type (" + e.getMessage() + ")");
                } catch (JSONException e) {
                    throw new IOException("wrong type (" + e.getMessage() + ")");
                }
                break;
            case "get":
                result = multiTable.get(args[1]);
                if (result == null) {
                    System.out.println("not found");
                } else {
                    System.out.println("found\n" + result);
                }
                break;
            case "remove":
                result = multiTable.remove(args[1]);
                if (result == null) {
                    System.out.println("not found");
                } else {
                    System.out.println("removed");
                }
                break;
            default:
                multiTable.getMultiTable().handle(args);
        }
        return true;
    }

    public void showTables() throws IOException {
        for (Map.Entry entry : tables.entrySet()) {
            Object value = entry.getValue();
            MultiTable multiTable = null;
            if (value instanceof MultiTable) {
                multiTable = (MultiTable) value;
            }
            if (multiTable != null) {
                System.out.println(entry.getKey() + " " + multiTable.getNumKeys());
            }
        }
    }

    public SharedMyTable getMultiTable(String name) {
        return tables.get(name);
    }

    private Class[] readSignatures(File file) throws IOException {
        if (!file.exists()) {
            throw new IOException("MultiMap: can't find the file with signatures");
        }
        FileReader reader = new FileReader(file);
        StringBuilder buffer = new StringBuilder("");
        while (reader.ready()) {
            buffer.append((char) reader.read());
        }
        reader.close();
        String types = new String(buffer);
        String[] typeList = types.split("\\s+");
        Class[] result = new Class[typeList.length];
        try {
            for (int i = 0; i < result.length; ++i) {
                result[i] = Class.forName("java.lang." + typeList[i]);
            }
        } catch (ClassNotFoundException e) {
            throw new IOException("Failed to read signatures");
        }
        return result;
    }

    public List<String> getTableNames() {
        List<String> result = new ArrayList<>();
        for (Map.Entry pair : tables.entrySet()) {
            result.add(pair.getKey().toString());
        }
        return result;
    }
}
