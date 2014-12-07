package ru.fizteh.fivt.students.AliakseiSemchankau.filemap2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by Aliaksei Semchankau on 09.11.2014.
 */
public class DatabaseTable {

    Path pathToTable;
    HashMap<String, String> dbMap;

    //Path fatherDirectory;
    String tableName;

    DatabaseTable(Path pathTable) {
        pathToTable = pathTable;
        pathToTable.normalize();

        dbMap = new HashMap<String, String>();
       // fatherDirectory = directory;
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

    public void writeFile(Path pathToFile) {

        int keysInFile = 0;
        Path currentFile = pathToFile;

        keysInFile = dbMap.size();

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

            for (Map.Entry<String, String> entry : dbMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                byte[] byteKey = key.getBytes("UTF-8");
                byte[] byteValue = value.getBytes("UTF-8");
                fileToWrite.writeInt(byteKey.length);
                fileToWrite.write(byteKey);
                fileToWrite.writeInt(byteValue.length);
                fileToWrite.write(byteValue);

            }
            try  {
                fileToWrite.close();
            } catch (IOException ioexc) {
                throw new DatabaseException("can't close DataOutputStream for" + currentFile.toString());
            }
        } catch (IOException iexc) {
            throw new DatabaseException("something wrong with writing to file");
        }
    }

    public String getName() {
        return tableName;
    }

    public String get(String key) {

        if (key == null) {
            throw new IllegalArgumentException("key cannot be a null for get");
        }

        if (dbMap.containsKey(key)) {
            return dbMap.get(key);
        }

        return null;

    }

    public String put(String key, String value) {

        if (key == null || value == null) {
            throw new IllegalArgumentException("arguments for put can't be nullpointers");
        }

        if (dbMap.containsKey(key)) {
            // System.out.println("contained" + key);
            String oldValue = dbMap.get(key);
            dbMap.put(key, value);
            // System.out.println("oldValue " + oldValue);
            return oldValue;
        }

        dbMap.put(key, value);
        return null;

    }

    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be a null for a remove");
        }

        if (dbMap.containsKey(key)) {
            String value = dbMap.get(key);
            dbMap.remove(key);
            return value;
        }

        return null;
    }

    public int size() {
        return (dbMap.size());
    }

    public List<String> list() {
        List<String> listOfKeys = new LinkedList<String>();

        for (String currentKey : dbMap.keySet()) {
            listOfKeys.add(currentKey);
        }
        return listOfKeys;
    }

}
