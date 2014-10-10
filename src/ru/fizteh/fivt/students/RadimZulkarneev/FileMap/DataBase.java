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
    public DataBase(final String DbName)
            throws MapExcept {
        try {
            dBasePath = Paths.get(DbName);
            Functions.MakeDbFile(DbName);
            dBase = new HashMap<String, String>();
        } catch (MapExcept ex) {
            if (ex.toString().equals(
                    "MakeDbFile: File already exist")) {
                dBase = new HashMap<String, String>();
                try {
                    RandomAccessFile DbFile = new
                            RandomAccessFile(
                        dBasePath.toString(), "r");
                    if (DbFile.length() > 0) {
                        while (DbFile.
                                getFilePointer()
                            < DbFile.length()) {
                            String key =
                            readFromDataBase(
                                DbFile);
                            String value =
                            readFromDataBase(
                                    DbFile);
                            dBase.put(key, value);

                        }
                    }
                    DbFile.close();
                } catch (Exception ex1) {
                    throw new MapExcept(
                            "DataBase: "
                    + "I don't know-exception");
                }
            }
        }
    }

    private  String readFromDataBase(
            final RandomAccessFile DbFile)
                    throws MapExcept {

        try {
            int wordlen = DbFile.readInt();
            byte[] word = new byte[wordlen];
            DbFile.read(word, 0, wordlen);
            return new String(word);
        } catch (IOException e) {
            throw new MapExcept("Can't read from database");
        }
    }


    private void writeToDataBase(final RandomAccessFile DbFile,
            final String word) throws MapExcept {
        try {
            DbFile.writeInt(word.getBytes("UTF-8").length);
            DbFile.write(word.getBytes("UTF-8"));
        } catch (Exception ex) {
            throw new MapExcept("can't write in file");
        }

    }

    final public void addValue(final String key,
            final String value) throws MapExcept {
        try {
            dBase.put(key, value);
        } catch (Exception ex) {
            throw new MapExcept("Database addValue:"
                    + " Unknown exception");
        }
    }
    final public void listCommand() {
        Set<Entry<String, String>> baseSet = dBase.entrySet();
        Iterator<Entry<String, String>> it = baseSet.iterator();
        while (it.hasNext()) {
            Entry<String, String> current =
                    (Entry<String, String>) it.next();
            System.out.print(current.getKey() + " ");
        }
        System.out.println("");

    }
    final public void writeInFile() throws MapExcept {
        try {
            Set<Entry<String, String>> baseSet = dBase.entrySet();
            Iterator<Entry<String, String>> it = baseSet.iterator();
            Functions.MakeDbFileHard(dBasePath.toString());
            RandomAccessFile DbFile = new
                    RandomAccessFile(dBasePath.toString(), "rw");
            while (it.hasNext()) {
                Entry<String, String> current =
                        (Entry<String, String>) it.next();
                writeToDataBase(DbFile, current.getKey());
                writeToDataBase(DbFile, current.getValue());
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            throw new MapExcept("DataBase: cant write");
        }
    }
    final public void put(final String key, final String value) {
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

    final public void get(final String key) {
        if (dBase.containsKey(key)) {
            System.out.println("found");
            System.out.println(dBase.get(key));
        } else {
            System.out.println("not found");
        }
    }
    final public void remove(final String key) {
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

