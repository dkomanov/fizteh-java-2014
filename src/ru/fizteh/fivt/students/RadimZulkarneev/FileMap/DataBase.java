package ru.fizteh.fivt.students.RadimZulkarneev.FileMap;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class DataBase {
    public DataBase(final String dbName)
            throws MapExcept {
        try {
            dBasePath = Paths.get(dbName);
            dBase = new HashMap<String, String>();
            Functions.makeDbFile(dbName);
        } catch (MapExcept ex) {
            if (ex.toString().equals(
                    "MakeDbFile: File already exist")) {
                try {
                    RandomAccessFile dbFile = new
                            RandomAccessFile(
                        dBasePath.toString(), "r");
                    if (dbFile.length() > 0) {
                        while (dbFile.
                                getFilePointer()
                            < dbFile.length()) {
                            String key =
                            readFromDataBase(
                                dbFile);
                            String value =
                            readFromDataBase(
                                    dbFile);
                            dBase.put(key, value);
                        }
                    }
                    dbFile.close();
                } catch (Exception ex1) {
                    throw new MapExcept(
                            "DataBase: "
                    + "I don't know-exception");
                }
            } 
            if (ex.toString().equals("It is a directory")) {
                System.out.println("It is a directory");
                System.exit(1);
            }
        }
    }

    private  String readFromDataBase(
            final RandomAccessFile dbFile)
                    throws MapExcept {

        try {
            int wordlen = dbFile.readInt();
            byte[] word = new byte[wordlen];
            dbFile.read(word, 0, wordlen);
            return new String(word);
        } catch (IOException e) {
            throw new MapExcept("Can't read from database");
        }
    }


    private void writeToDataBase(final RandomAccessFile dbFile,
            final String word) throws MapExcept {
        try {
            dbFile.writeInt(word.getBytes("UTF-8").length);
            dbFile.write(word.getBytes("UTF-8"));
        } catch (Exception ex) {
            throw new MapExcept("can't write in file");
        }

    }

    public final void addValue(final String key,
            final String value) throws MapExcept {
        try {
            dBase.put(key, value);
        } catch (Exception ex) {
            throw new MapExcept("Database addValue:"
                    + " Unknown exception");
        }
    }
    public final void listCommand() {
        Set<Entry<String, String>> baseSet = dBase.entrySet();
        Iterator<Entry<String, String>> it = baseSet.iterator();
        while (it.hasNext()) {
            Entry<String, String> current =
                    (Entry<String, String>) it.next();
            System.out.print(current.getKey() + " ");
        }
        System.out.println("");

    }
    public final void writeInFile() throws MapExcept {
        try {
            Set<Entry<String, String>> baseSet = dBase.entrySet();
            Iterator<Entry<String, String>> it = baseSet.iterator();
            Functions.makeDbFileHard(dBasePath.toString());
            RandomAccessFile dbFile = new
                    RandomAccessFile(dBasePath.toString(), "rw");
            while (it.hasNext()) {
                Entry<String, String> current =
                        (Entry<String, String>) it.next();
                writeToDataBase(dbFile, current.getKey());
                writeToDataBase(dbFile, current.getValue());
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            throw new MapExcept("DataBase: cant write");
        }
    }
    public final void put(final String key, final String value) {
        if (dBase.containsKey(key)) {
            System.out.println("overwrite");
            System.out.println(dBase.get(key));
            dBase.remove(key);
            dBase.put(key, value);
        } else {
            System.out.println("new");
            dBase.put(key, value);
        }
    }

    public final void get(final String key) {
        if (dBase.containsKey(key)) {
            System.out.println("found");
            System.out.println(dBase.get(key));
        } else {
            System.out.println("not found");
        }
    }
    public final void remove(final String key) {
        if (dBase.containsKey(key)) {
            System.out.println("removed");
            dBase.remove(key);
        } else {
            System.out.println("not found");
        }
    }
    private Map<String, String> dBase;
    private Path dBasePath;
}

