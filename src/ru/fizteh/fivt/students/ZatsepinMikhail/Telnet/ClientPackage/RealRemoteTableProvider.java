package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage;

import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FileMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.StoreablePackage.AbstractStoreable;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.StoreablePackage.Serializator;
import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.StoreablePackage.TypesUtils;
import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.shell.FileUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RealRemoteTableProvider implements RemoteTableProvider {
    private HashMap<String, RealRemoteTable> tables;
    private Socket server;
    private Scanner input;
    private PrintStream output;

    public RealRemoteTableProvider(String hostname, int port) throws IOException {
        tables = new HashMap<>();
        server = new Socket(InetAddress.getByName(hostname), port);
        server = new Socket(InetAddress.getByName(hostname), port);
        input = new Scanner(server.getInputStream());
        output = new PrintStream(server.getOutputStream());
    }

    @Override
    public Table getTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("null argument");
        }
        output.println("show tables");
        int numberOfTables = Integer.parseInt(input.nextLine());
        for (int i = 0; i < numberOfTables; ++i) {

        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException, IllegalArgumentException {
        if (name == null || columnTypes == null) {
            throw new IllegalArgumentException("null argument");
        }
        TypesUtils.checkTypes(columnTypes);
        if (tables.containsKey(name)) {
            return null;
        } else {
            output.println("create " + name + " (" + TypesUtils.toFileSignature(columnTypes) + ")");
            String message = input.nextLine();
        }
    }

    @Override
    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException, IOException {
        if (name == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (tables.containsKey(name)) {
            Path pathForRemoveTable = Paths.get(dataBaseDirectory, name);
            tables.remove(name);
            currentTable = null;
            FileUtils.rmdir(pathForRemoveTable);
        } else {
            throw new IllegalStateException("table \'" + name + "\' doesn't exist");
        }
    }

    @Override
    public Storeable createFor(Table table) {
        Object[] startValues = new Object[table.getColumnsCount()];
        return new ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.StoreablePackage.AbstractStoreable(startValues, table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        if (table.getColumnsCount() != values.size()) {
            throw new IndexOutOfBoundsException("number of types");
        }
        List<Object> objValues = new ArrayList<>(values);
        List<Class<?>> typeList = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            if (objValues.get(i).getClass() != (table.getColumnType(i))) {
                throw new ColumnFormatException("mismatch column type");
            }
            typeList.add(table.getColumnType(i));
        }
        TypesUtils.checkNewStorableValue(typeList, objValues);
        return new ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.StoreablePackage.AbstractStoreable(objValues.toArray(), table);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        if (table.getColumnsCount() != TypesUtils.getSizeOfStoreable(value)) {
            throw new ColumnFormatException("wrong size");
        }
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            if (!table.getColumnType(i).equals(value.getColumnAt(i).getClass())) {
                throw new ColumnFormatException("need: " + table.getColumnType(i)
                        + ", but got:" + value.getColumnAt(i).getClass());
            }
        }
        return ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.StoreablePackage.Serializator.serialize(table, value);
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        return ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.StoreablePackage.Serializator.deserialize(table, value);
    }

    @Override
    public List<String> getTableNames() {
        List<String> result = new ArrayList<>();
        Collection<ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.FileMap.FileMap> filemaps = tables.values();
        for (ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.FileMap.FileMap oneTable : filemaps) {
            result.add(oneTable.getName());
        }
        return result;
    }

    public void showTables() {
        Set<Entry<String, ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.FileMap.FileMap>> pairSet = tables.entrySet();
        for (Entry<String, ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.FileMap.FileMap> oneTable: pairSet) {
            System.out.println(oneTable.getKey() + " "
                    + oneTable.getValue().size());
        }
    }

    public void setCurrentTable(ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.FileMap.FileMap newCurrentTable) {
        currentTable = newCurrentTable;
    }

    public ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.FileMap.FileMap getCurrentTable() {
        return currentTable;
    }

    public boolean init() {
        String[] listOfFiles = new File(dataBaseDirectory).list();
        for (String oneFile: listOfFiles) {
            Path oneTablePath = Paths.get(dataBaseDirectory, oneFile);
            Path oneTableSignaturePath = Paths.get(dataBaseDirectory, oneFile, "signature.tsv");
            if (Files.isDirectory(oneTablePath) & Files.exists(oneTableSignaturePath)) {
                try (Scanner input = new Scanner(oneTableSignaturePath)) {
                    String[] types;
                    if (input.hasNext()) {
                        types = input.nextLine().trim().split("\\s+");
                        List<Class<?>> newTypeList = TypesUtils.toTypeList(types);
                        if (newTypeList != null) {
                            tables.put(oneFile, new ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.FileMap.FileMap(oneTablePath.toString(), newTypeList, this));
                        }
                    }
                } catch (FileNotFoundException e) {
                    return false;
                } catch (IOException e) {
                    return false;
                }
            }
        }
        boolean allRight = true;
        Set<Entry<String, ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.FileMap.FileMap>> pairSet = tables.entrySet();
        for (Entry<String, ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.FileMap.FileMap> oneFileMap: pairSet) {
            if  (!oneFileMap.getValue().init()) {
                allRight = false;
            }
        }
        return allRight;
    }
}
