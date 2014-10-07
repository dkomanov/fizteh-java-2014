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
<<<<<<< HEAD
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
=======
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
>>>>>>> parent of 7dfbcf9... FileMap commit. Fix
                } catch (Exception ex1) {
                    throw new MapExcept(
                            "DataBase: "
                    + "I don't know-exception");
                }
<<<<<<< HEAD
            } 
            if (ex.toString().equals("It is a directory")) {
                System.out.println("It is a directory");
                System.exit(1);
=======
>>>>>>> parent of 7dfbcf9... FileMap commit. Fix
            }
        }
    }

    private  String readFromDataBase(
<<<<<<< HEAD
            final RandomAccessFile dbFile)
                    throws MapExcept {

        try {
            int wordlen = dbFile.readInt();
            byte[] word = new byte[wordlen];
            dbFile.read(word, 0, wordlen);
=======
            final RandomAccessFile DbFile)
                    throws MapExcept {

        try {
            int wordlen = DbFile.readInt();
            byte[] word = new byte[wordlen];
            DbFile.read(word, 0, wordlen);
>>>>>>> parent of 7dfbcf9... FileMap commit. Fix
            return new String(word);
        } catch (IOException e) {
            throw new MapExcept("Can't read from database");
        }
    }


<<<<<<< HEAD
    private void writeToDataBase(final RandomAccessFile dbFile,
            final String word) throws MapExcept {
        try {
            
            dbFile.writeInt(word.getBytes("UTF-8").length);
            dbFile.write(word.getBytes("UTF-8"));
=======
    private void writeToDataBase(final RandomAccessFile DbFile,
            final String word) throws MapExcept {
        try {
            DbFile.writeInt(word.getBytes("UTF-8").length);
            DbFile.write(word.getBytes("UTF-8"));
>>>>>>> parent of 7dfbcf9... FileMap commit. Fix
        } catch (Exception ex) {
            throw new MapExcept("can't write in file");
        }

    }

<<<<<<< HEAD
    public final void addValue(final String key,
=======
    final public void addValue(final String key,
>>>>>>> parent of 7dfbcf9... FileMap commit. Fix
            final String value) throws MapExcept {
        try {
            dBase.put(key, value);
        } catch (Exception ex) {
            throw new MapExcept("Database addValue:"
                    + " Unknown exception");
        }
    }
<<<<<<< HEAD
    public final void listCommand() {
=======
    final public void listCommand() {
>>>>>>> parent of 7dfbcf9... FileMap commit. Fix
        Set<Entry<String, String>> baseSet = dBase.entrySet();
        Iterator<Entry<String, String>> it = baseSet.iterator();
        while (it.hasNext()) {
            Entry<String, String> current =
                    (Entry<String, String>) it.next();
            System.out.print(current.getKey() + " ");
        }
        System.out.println("");

    }
<<<<<<< HEAD
    public final void writeInFile() throws MapExcept {
        try {
            Set<Entry<String, String>> baseSet = dBase.entrySet();
            Iterator<Entry<String, String>> it = baseSet.iterator();
       //     Functions.makeDbFileHard(dBasePath.toString());
            RandomAccessFile dbFile = new
                    RandomAccessFile(dBasePath.toString(), "rw");
            dbFile.setLength(0);
            while (it.hasNext()) {
                Entry<String, String> current =
                        (Entry<String, String>) it.next();
              //  System.out.println(current.getKey() + " " + current.getKey().hashCode() % 16 + " " 
                //        + current.getKey().hashCode() / 16 % 16);
                
                writeToDataBase(dbFile, current.getKey());
                writeToDataBase(dbFile, current.getValue());
=======
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
>>>>>>> parent of 7dfbcf9... FileMap commit. Fix
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            throw new MapExcept("DataBase: cant write");
        }
    }
<<<<<<< HEAD
    public final void put(final String key, final String value) {
=======
    final public void put(final String key, final String value) {
>>>>>>> parent of 7dfbcf9... FileMap commit. Fix
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

<<<<<<< HEAD
    public final void get(final String key) {
=======
    final public void get(final String key) {
>>>>>>> parent of 7dfbcf9... FileMap commit. Fix
        if (dBase.containsKey(key)) {
            System.out.println("found");
            System.out.println(dBase.get(key));
        } else {
            System.out.println("not found");
        }
    }
<<<<<<< HEAD
    public final void remove(final String key) {
=======
    final public void remove(final String key) {
>>>>>>> parent of 7dfbcf9... FileMap commit. Fix
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

