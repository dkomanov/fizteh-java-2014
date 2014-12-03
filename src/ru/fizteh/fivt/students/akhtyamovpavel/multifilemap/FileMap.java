package ru.fizteh.fivt.students.akhtyamovpavel.multifilemap;


import ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.commands.fileshell.RemoveCommand;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user1 on 30.09.2014.
 */
public class FileMap extends HashMap<String, String> {
    Path dataBasePath;

    public static final int NUMBER_OF_FILES = 16;
    public static final int NUMBER_OF_DIRECTORIES = 16;

    public FileMap(Path path, String tableName) throws Exception {
        dataBasePath = Paths.get(path.toString(), tableName);
        loadMap();
    }

    public void loadMap() throws Exception {
        for (int directoryIndex = 0; directoryIndex < NUMBER_OF_DIRECTORIES; ++directoryIndex) {
            String directoryName = String.format("%02d.dir", directoryIndex);
            Path directoryPath = Paths.get(dataBasePath.toString(), directoryName);
            if (!Files.exists(directoryPath)) {
                continue;
            }
            if (!Files.isDirectory(directoryPath)) {
                throw new Exception("connect: database is broken");
            }
            if (!Files.isWritable(directoryPath) || !Files.isReadable(directoryPath)) {
                throw new Exception("connect: permission denied");
            }
            for (int fileIndex = 0; fileIndex < NUMBER_OF_FILES; ++fileIndex) {
                Path filePath = Paths.get(directoryPath.toString(), String.format("%02d.dat", fileIndex));
                if (!Files.exists(filePath)) {
                    continue;
                }
                if (!Files.isRegularFile(filePath)) {
                    throw new Exception("connect: database is broken");
                }
                if (!Files.isReadable(filePath) || !Files.isWritable(filePath)) {
                    throw new Exception("connect: permission denied");
                }
                try (DataInputStream inputStream = new DataInputStream(Files.newInputStream(filePath))) {
                    while (inputStream.available() > 0) {
                        int keyLength = inputStream.readInt();
                        byte[] keyByteFormat = new byte[keyLength];
                        int readKeyBytes = inputStream.read(keyByteFormat, 0, keyLength);
                        if (readKeyBytes < keyByteFormat.length) {
                            throw new Exception("read from database failed");
                        }
                        int valueLength = inputStream.readInt();
                        byte[] valueByteFormat = new byte[valueLength];
                        int readBytes = inputStream.read(valueByteFormat, 0, valueLength);
                        if (readBytes < valueByteFormat.length) {
                            throw new Exception("read from database failed");
                        }
                        put(new String(keyByteFormat, "UTF-8"), new String(valueByteFormat, "UTF-8"));
                    }
                } catch (IOException ioe) {
                    throw new Exception("read from database failed");
                }
            }
        }

    }

    public void saveMap() throws Exception {
        ArrayList<ArrayList<ArrayList<String>>> listOfKeys = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<String>>> listOfValues = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_DIRECTORIES; ++i) {
            listOfKeys.add(new ArrayList<>());
            listOfValues.add(new ArrayList<>());
            for (int j = 0; j < NUMBER_OF_DIRECTORIES; ++j) {
                listOfKeys.get(i).add(new ArrayList<>());
                listOfValues.get(i).add(new ArrayList<>());
            }
        }
        for (Entry<String, String> entry: entrySet()) {
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
                            byte[] value = listOfValues.get(directoryIndex).get(fileIndex).get(index).getBytes("UTF-8");
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
        for (String fileName: filesList) {
            ArrayList<String> arguments = new ArrayList<>();
            arguments.add("-r");
            arguments.add(fileName);
            new RemoveCommand(dataBasePath).executeCommand(arguments);
        }
    }

}
