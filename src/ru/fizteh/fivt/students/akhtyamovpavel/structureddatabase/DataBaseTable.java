package ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase;


import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase.commands.fileshell.RemoveCommand;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user1 on 30.09.2014.
 */
public class DataBaseTable implements Table {
    public static final int NUMBER_OF_FILES = 16;
    public static final int NUMBER_OF_DIRECTORIES = 16;
    Path dataBasePath;
    String tableName;
    HashMap<String, Storeable> tableData = new HashMap<>();
    HashMap<String, Storeable> tempData = new HashMap<>();

    private TableRowSerializer serializer;
    private ArrayList<Class<?>> signature = new ArrayList<>();

    int unsavedSize = 0;



    public DataBaseTable(Path path, String tableName, TableRowSerializer serializer) throws Exception {
        dataBasePath = Paths.get(path.toString(), tableName);

        this.tableName = tableName;
        this.serializer = serializer;
        readSignature();
        loadMap();
    }

    public DataBaseTable(Path path, String tableName, List<Class<?>> signature,
                         TableRowSerializer serializer) throws Exception {
        dataBasePath = Paths.get(path.toString(), tableName);

        this.tableName = tableName;
        this.serializer = serializer;
        this.signature = new ArrayList<>(signature);
        writeSignature();
        saveMap();
    }

    private void writeSignature() throws IOException {
        PrintWriter out = new PrintWriter(dataBasePath.resolve("signature.tsv").toString());

        for (Class<?> type: signature) {
            out.print(TableRowSerializer.classToString(type));
        }
        out.close();
    }

    private void readSignature() throws IOException {
        signature.clear();

        try (BufferedReader br = Files.newBufferedReader(dataBasePath.resolve("signature.tsv"))) {
            String line = br.readLine();
            for (String type: line.split(" ")) {
                signature.add(TableRowSerializer.stringToClass(type));
            }
        }
    }

    public void loadMap() throws IOException {
        for (int directoryIndex = 0; directoryIndex < NUMBER_OF_DIRECTORIES; ++directoryIndex) {
            String directoryName = String.format("%02d.dir", directoryIndex);
            Path directoryPath = Paths.get(dataBasePath.toString(), directoryName);
            if (!Files.exists(directoryPath)) {
                continue;
            }
            if (!Files.isDirectory(directoryPath)) {
                throw new IOException("connect: database is broken");
            }
            if (!Files.isWritable(directoryPath) || !Files.isReadable(directoryPath)) {
                throw new IOException("connect: permission denied");
            }
            for (int fileIndex = 0; fileIndex < NUMBER_OF_FILES; ++fileIndex) {
                Path filePath = Paths.get(directoryPath.toString(), String.format("%02d.dat", fileIndex));
                if (!Files.exists(filePath)) {
                    continue;
                }
                if (!Files.isRegularFile(filePath)) {
                    throw new IOException("connect: database is broken");
                }
                if (!Files.isReadable(filePath) || !Files.isWritable(filePath)) {
                    throw new IOException("connect: permission denied");
                }
                try (DataInputStream inputStream = new DataInputStream(Files.newInputStream(filePath))) {
                    while (inputStream.available() > 0) {
                        int keyLength = inputStream.readInt();
                        byte[] keyByteFormat = new byte[keyLength];
                        int readKeyBytes = inputStream.read(keyByteFormat, 0, keyLength);
                        if (readKeyBytes < keyByteFormat.length) {
                            throw new IOException("read from database failed");
                        }
                        int valueLength = inputStream.readInt();
                        byte[] valueByteFormat = new byte[valueLength];
                        int readBytes = inputStream.read(valueByteFormat, 0, valueLength);
                        if (readBytes < valueByteFormat.length) {
                            throw new IOException("read from database failed");
                        }
                        TableRow currentValue = serializer.deserialize(this, new String(valueByteFormat, "UTF-8"));
                        tableData.put(new String(keyByteFormat, "UTF-8"), currentValue);
                    }
                } catch (IOException ioe) {
                    throw new IOException("read from database failed");
                } catch (ParseException pe) {
                    throw new IOException("deserialization failed");
                }
            }
        }
        tempData = new HashMap<>(tableData);
    }

    public void saveMap() throws Exception {
        ArrayList<ArrayList<ArrayList<String>>> listOfKeys = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<Storeable>>> listOfValues = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_DIRECTORIES; ++i) {
            listOfKeys.add(new ArrayList<>());
            listOfValues.add(new ArrayList<>());
            for (int j = 0; j < NUMBER_OF_DIRECTORIES; ++j) {
                listOfKeys.get(i).add(new ArrayList<>());
                listOfValues.get(i).add(new ArrayList<>());
            }
        }
        for (HashMap.Entry<String, Storeable> entry : tableData.entrySet()) {
            int hashCode = entry.getKey().hashCode();
            int directoryNumber = hashCode % 16;
            int fileNumber = hashCode / 16 % 16;
            listOfKeys.get(directoryNumber).get(fileNumber).add(entry.getKey());
            listOfValues.get(directoryNumber).get(fileNumber).add(entry.getValue());
        }

        clearGarbage();


        for (int directoryIndex = 0; directoryIndex < NUMBER_OF_DIRECTORIES; ++directoryIndex) {
            String directoryName = String.format("%02d.dir", directoryIndex);
            Path directoryPath = Paths.get(dataBasePath.toString(), directoryName);
            int size = 0;
            for (int fileIndex = 0; fileIndex < NUMBER_OF_FILES; ++fileIndex) {
                size += listOfKeys.get(directoryIndex).get(fileIndex).size();
            }
            if (size > 0) {
                Files.createDirectory(directoryPath);
                for (int fileIndex = 0; fileIndex < NUMBER_OF_FILES; ++fileIndex) {
                    Path filePath = Paths.get(directoryPath.toString(), String.format("%02d.dat", fileIndex));
                    if (!listOfKeys.get(directoryIndex).get(fileIndex).isEmpty()) {
                        Files.createFile(filePath);
                    } else {
                        continue;
                    }
                    try (DataOutputStream outputStream = new DataOutputStream(Files.newOutputStream(filePath))) {
                        for (int index = 0; index < listOfKeys.get(directoryIndex).get(fileIndex).size(); ++index) {
                            byte[] key = listOfKeys.get(directoryIndex).get(fileIndex).get(index).getBytes("UTF-8");
                            String serialized  = serializer.serialize(this,
                                    listOfValues.get(directoryIndex).get(fileIndex).get(index));
                            byte[] value = serialized.getBytes("UTF-8");
                            outputStream.writeInt(key.length);
                            outputStream.write(key);
                            outputStream.writeInt(value.length);
                            outputStream.write(value);
                        }
                    } catch (IOException e) {
                        throw new Exception("writing to database failed");
                    }
                }
            }

        }
    }

    private void clearGarbage() throws Exception {
        String[] filesList = dataBasePath.toFile().list();
        for (String fileName : filesList) {
            if (!fileName.equals("signature.tsv")) {
                ArrayList<String> arguments = new ArrayList<>();
                arguments.add("-r");
                arguments.add(fileName);
                new RemoveCommand(dataBasePath).executeCommand(arguments);
            }
        }
    }

    @Override
    public String getName() {
        return tableName;
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key == null || value == null) {
            throw new IllegalArgumentException("null key or value");
        }
        if (tempData.containsKey(key)) {
            Storeable oldValue = tempData.get(key);
            tempData.put(key, value);
            unsavedSize++;
            return oldValue;
        } else {
            tempData.put(key, value);
            unsavedSize++;
            return null;
        }
    }

    @Override
    public Storeable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("null key");
        }
        return tempData.get(key);
    }



    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("null key");
        }
        if (tempData.containsKey(key)) {
            unsavedSize++;
            return tempData.remove(key);
        } else {
            return null;
        }
    }

    @Override
    public int size() {
        return tempData.size();
    }

    @Override
    public int commit() throws IOException {
        int commitSize = unsavedSize;
        tableData = new HashMap<>(tempData);
        try {
            saveMap();
        } catch (Exception e) {
            throw new IOException("i/o error in writing to database");
        }
        int size = unsavedSize;
        unsavedSize = 0;
        return size;
    }

    @Override
    public int rollback() {
        tempData = new HashMap<>(tableData);
        int resultSize = unsavedSize;
        unsavedSize = 0;
        return resultSize;
    }

    @Override
    public int getColumnsCount() {
        return signature.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return signature.get(columnIndex);
    }

    public List<String> list() {
        ArrayList<String> nameList = new ArrayList<>();
        for (String currentKey : tempData.keySet()) {
            nameList.add(currentKey);
        }
        return nameList;
    }




    public boolean containsKey(String key) {
        return tempData.containsKey(key);
    }

    public boolean hasUnsavedChanges() {
        return unsavedSize > 0;
    }


    public int getNumberOfChanges() {
        return unsavedSize;
    }
}
