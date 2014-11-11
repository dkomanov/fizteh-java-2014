package ru.fizteh.fivt.students.AliakseiSemchankau.junit;

import javafx.util.Pair;
import ru.fizteh.fivt.storage.strings.Table;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Aliaksei Semchankau on 09.11.2014.
 */
public class DatabaseTable implements Table {

    Path pathToTable;
    HashMap<String, String> dbMap;
    HashMap<String, String> localDBMap;
    int unsavedChanges;

    Path fatherDirectory;
    String tableName;

    DatabaseTable(Path pathTable, Path directory) {
        pathToTable = pathTable;
        pathToTable.normalize();

        dbMap = new HashMap<String, String>();
        localDBMap = new HashMap<String, String>();
        unsavedChanges = 0;

        fatherDirectory = directory;
    }

    public void openTable() {

        DirectoryStream<Path> listOfDirs;

        try {
            listOfDirs = Files.newDirectoryStream(pathToTable);
        } catch (IOException ioexc) {
            throw new DatabaseException(pathToTable + ": can't make a list of directories");

        }

        for (Path innerDirection : listOfDirs) {
            if (!Files.isDirectory(innerDirection)) {
                throw new DatabaseException(innerDirection + ": isn't a directiion");
            }

            openDirection(innerDirection);
        }

        try {
            listOfDirs.close();
        } catch (IOException ioexc) {
            throw new DatabaseException("can't close list of directories of " + pathToTable.toString());
        }

        localDBMap = new HashMap<String, String>(dbMap);

    }

    public void openDirection(Path innerDirection) {

        DirectoryStream<Path> listOfFiles;
        try {
            listOfFiles = Files.newDirectoryStream(innerDirection);
        } catch (IOException ioexc) {
            throw new DatabaseException(pathToTable + ": can't make a list of files");
        }

        for (Path innerFile : listOfFiles) {
            if (!Files.exists(innerFile)) {
                throw new DatabaseException(innerFile + ": isn't a file");
            }
            openFile(innerFile);
        }

        try {
            listOfFiles.close();
        } catch (IOException ioexc) {
            throw new DatabaseException("can't close list of directories of " + innerDirection.toString());
        }
    }

    public void openFile(Path innerFile) {

        if (!Files.exists(innerFile)) {
            throw new DatabaseException("database doesnt exist");
        }

        try {
            DataInputStream databaseFile = new DataInputStream(Files.newInputStream(innerFile));

            while (databaseFile.available() > 0) {
                int byteKeyLength = databaseFile.readInt();

                // System.out.println(byteKeyLength);

                if (databaseFile.available() < byteKeyLength) {
                    throw new DatabaseException("incorrect bytelength of key");
                }

                byte[] key = new byte[byteKeyLength];
                databaseFile.read(key, 0, byteKeyLength);

                // System.out.println(Key.toString());

                int byteValueLength = databaseFile.readInt();

                // System.out.println(byteValueLength);

                if (databaseFile.available() < byteValueLength) {
                    throw new DatabaseException("incorrect bytelength of value");
                }

                byte[] value = new byte[byteValueLength];
                databaseFile.read(value, 0, byteValueLength);

                // System.out.println(Value);

                dbMap.put(new String(key, "UTF-8"), new String(value, "UTF-8"));
            }

            try {
                databaseFile.close();
            } catch (IOException ioexc) {
                throw new DatabaseException("can't close databaseFile " + innerFile.toString());
            }
        } catch (IOException ioexc) {
            throw new DatabaseException("can't open file as an input stream " + innerFile);
        }

    }

    public void writeTable() {

        Vector<Vector<Vector<Pair<String, String>>>> mapDirectoriesFiles =
                new Vector<Vector<Vector<Pair<String, String>>>>();

        for (int i = 0; i < 16; ++i) {
            mapDirectoriesFiles.add(new Vector<Vector<Pair<String, String>>>());
            for (int j = 0; j < 16; ++j) {
                mapDirectoriesFiles.elementAt(i).add(new Vector<Pair<String, String>>());
            }
        }

        if (!Files.exists(pathToTable)) {
            try {
                Files.createDirectory(pathToTable);
            } catch (IOException ioexc) {
                throw new DatabaseException("can't create " + pathToTable);
            }
        }

        DirectoryStream<Path> listOfDirectories;        // --------- delete all directories

        try {
            listOfDirectories = Files.newDirectoryStream(pathToTable);

            for (Path directoryToDelete : listOfDirectories) {
                //System.out.println(directoryToDelete.toString());
                DeleteFunctions.deleteDirectory(directoryToDelete);
            }
            try {
                listOfDirectories.close();
            } catch (IOException ioexc2) {
                throw new DatabaseException("can't close listOfDirectories " + pathToTable.toString());
            }
        } catch (IOException ioexc) {
            throw new DatabaseException("cant open list of directories " + pathToTable);
        }

        // ------------ delete all directories

        for (Map.Entry<String, String> entry : dbMap.entrySet()) {
            int hash = entry.getKey().hashCode();
            int nDirectory = ((hash % 16) + 16) % 16;
            int nFile = (((hash / 16) % 16) + 16) % 16;
            /*System.out.println(entry.getKey());
            System.out.println(hash);
            System.out.println(nDirectory);
            System.out.println(nFile);
            System.out.println(pathToTable.toString());*/
            mapDirectoriesFiles.get(nDirectory).get(nFile).
                    add(new Pair<String, String>(entry.getKey(), entry.getValue()));

            if (!Files.exists(pathToTable)) {
                try {
                    Files.createDirectory(pathToTable);
                } catch (IOException ioexc) {
                    throw new DatabaseException("couldnt create directory for table " + pathToTable.toString());
                }
            }

            for (int i = 0; i < 16; ++i) {
                String directoryName = Integer.toString(i) + ".dir";
                if (i > 0 && i < 10) {
                    directoryName = "0" + directoryName;
                }
                writeDirectory(i, directoryName, mapDirectoriesFiles);
            }
        }

    }

    public void writeDirectory(int numberOfDirectory, String arg,
                               Vector<Vector<Vector<Pair<String, String>>>> mapDirectoriesFiles) {

        int keysInDirectory = 0;
        Path currentDirectory = Paths.get(pathToTable.toString()).resolve(arg);

        for (int i = 0; i < 16; ++i) {
            keysInDirectory += mapDirectoriesFiles.get(numberOfDirectory).get(i).size();
        }

        if (!Files.exists(currentDirectory) && keysInDirectory == 0) {
            return;
        }

        if (!Files.exists(currentDirectory) && keysInDirectory > 0) {
            try {
                Files.createDirectory(currentDirectory);
            } catch (IOException ioexc) {
                throw new DatabaseException("couldnt create " + currentDirectory.toString());
            }
        }

        for (int i = 0; i < 16; ++i) {
            String fileName = Integer.toString(i) + ".dat";
            if (i > 0 && i < 10) {
                fileName = "0" + fileName;
            }
            writeFile(numberOfDirectory, i, fileName, mapDirectoriesFiles, currentDirectory);
        }

      /*  if (keysInDirectory == 0){
            try {
                Files.delete(currentDirectory);
            } catch (IOException ioexc) {
                throw new DatabaseException("cant delete empty direction " + currentDirectory.toString());
            }
        }*/
    }

    public void writeFile(int numberOfDirectory, int numberOfFile, String arg,
                          Vector<Vector<Vector<Pair<String, String>>>> mapDirectoriesFiles, Path currentDirectory) {

        int keysInFile = 0;
        Path currentFile = Paths.get(currentDirectory.toString()).resolve(arg);

        keysInFile = mapDirectoriesFiles.get(numberOfDirectory).get(numberOfFile).size();

        if (!Files.exists(currentFile) && keysInFile == 0) {
            return;
        }

        if (!Files.exists(currentFile) && keysInFile > 0) {
            try {
                Files.createFile(currentFile);
            } catch (IOException ioexc) {
                throw new DatabaseException("couldnt create " + currentFile.toString());
            }
        }

      /*  if (keysInFile == 0){
            try {
                Files.delete(currentFile);
            } catch (IOException ioexc) {
                throw new DatabaseException("cant delete empty file " + currentFile.toString());
            }
            return;
        }*/

        try {
            DataOutputStream fileToWrite = new DataOutputStream(Files.newOutputStream(currentFile));

            for (Pair<String, String> currentPair : mapDirectoriesFiles.get(numberOfDirectory).get(numberOfFile)) {
                String key = currentPair.getKey();
                String value = currentPair.getValue();
                byte[] byteKey = key.getBytes("UTF-8");
                byte[] byteValue = value.getBytes("UTF-8");
                fileToWrite.writeInt(byteKey.length);
                fileToWrite.write(byteKey);
                fileToWrite.writeInt(byteValue.length);
                fileToWrite.write(byteValue);
                try  {
                    fileToWrite.close();
                } catch (IOException ioexc) {
                    throw new DatabaseException("can't close DataOutputStream for" + currentFile.toString());
                }
            }
        } catch (IOException iexc) {
            throw new DatabaseException("something wrong with writing to file");
        }
    }

    @Override
    public String getName() {
        return tableName;
    }

    @Override
    public String get(String key) {

        if (key == null) {
            throw new IllegalArgumentException("key cannot be a null for get");
        }

       if (localDBMap.containsKey(key)) {
           return localDBMap.get(key);
       }

        return null;

    }

    @Override
    public String put(String key, String value) {

        if (key == null || value == null) {
            throw new IllegalArgumentException("arguments for put can't be nullpointers");
        }

        ++unsavedChanges;

        if (localDBMap.containsKey(key)) {
           // System.out.println("contained" + key);
            String oldValue = localDBMap.get(key);
            localDBMap.put(key, value);
           // System.out.println("oldValue " + oldValue);
            return oldValue;
        }

        localDBMap.put(key, value);
        return null;

    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be a null for a remove");
        }

        ++unsavedChanges;

       if (localDBMap.containsKey(key)) {
           String value = localDBMap.get(key);
           localDBMap.remove(key);
           return key;
       }

       return null;
    }

    @Override
    public int size() {
        return (localDBMap.size());
    }

    @Override
    public int commit() {
        dbMap = new HashMap<String, String>(localDBMap);
        writeTable();
        int changesToReturn = unsavedChanges;
        unsavedChanges = 0;
        return changesToReturn;
    }

    @Override
    public int rollback() {
        localDBMap = new HashMap<String, String>(dbMap);
        int changesToReturn = unsavedChanges;
        unsavedChanges = 0;
        return changesToReturn;
    }

    @Override
    public List<String> list() {
        List<String> listOfKeys = new LinkedList<String>();

        for (String currentKey : localDBMap.keySet()) {
            listOfKeys.add(currentKey);
        }
        return listOfKeys;
    }

}
