package ru.fizteh.fivt.students.RadimZulkarneev.MultiFileHashMap;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Table {
    private Map<String, String> dBase;
    private Path dBasePath;
    public Table(final String dbName)
            throws MapException, DataBaseCorrupt {
        try {
            dBasePath = Paths.get(dbName);
            dBase = new HashMap<String, String>();
            Functions.makeDbFile(dbName);
        } catch (MapException ex) {
            if (ex.toString().equals(
                    "MakeDbFile: File already exist")) {
                try {
                    RandomAccessFile dbFile = new RandomAccessFile(dBasePath.toString(), "r");
                    if (dbFile.length() > 0) {
                        while (dbFile.getFilePointer() < dbFile.length()) {
                            String key = readFromTable(dbFile);
                            isValid(key);
                            String value = readFromTable(dbFile);
                            dBase.put(key, value);
                        }
                    }
                    dbFile.close();
                } catch (DataBaseCorrupt ex2) {
                    throw new DataBaseCorrupt(ex2.toString());
                } catch (Exception ex1) {
                    throw new MapException("Table: " + ex1.toString());
                }
            } 
            if (ex.toString().equals("It is a directory")) {
                System.out.println("It is a directory");
                System.exit(1);
            }
        }
    }

    private  String readFromTable(final RandomAccessFile dbFile) throws MapException {
        try {
            int wordlen = dbFile.readInt();
            byte[] word = new byte[wordlen];
            dbFile.read(word, 0, wordlen);
            return new String(word, "UTF-8");
        } catch (IOException e) {
            throw new MapException("Can't read from Table");
        }
    }


    private void writeToTable(final RandomAccessFile dbFile,
            final String word) throws MapException {
        try {
            dbFile.writeInt(word.getBytes("UTF-8").length);
            dbFile.write(word.getBytes("UTF-8"));
        } catch (Exception ex) {
            throw new MapException("can't write in file");
        }

    }

    public final void addValue(final String key,
            final String value) throws MapException {
        try {
            dBase.put(key, value);
        } catch (Exception ex) {
            throw new MapException("Table addValue:"
                    + " Unknown exception");
        }
    }
    
    public final void listCommand() {
        for (Entry<String, String> current : dBase.entrySet()) {
            System.out.print(current.getKey() + " ");
        }
    }
    public final void writeInFile() throws MapException, IOException {
        if (dBase.size() == 0) {
            Files.delete(dBasePath);
            return;
        }
        try (RandomAccessFile dbFile = new
                RandomAccessFile(dBasePath.toString(), "rw")) {

            dbFile.setLength(0);
            for (Entry<String, String> current : dBase.entrySet()) {
                writeToTable(dbFile, current.getKey());
                writeToTable(dbFile, current.getValue());
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            throw new MapException("Table: cant write");
        }
    }
    //returns TRUE if overwrite, FALSE else
    public final boolean put(final String key, final String value) {
        if (dBase.containsKey(key)) {
            System.out.println("overwrite");
            System.out.println(dBase.get(key));
            dBase.remove(key);
            dBase.put(key, value);
            return true;
        } else {
            System.out.println("new");
            dBase.put(key, value);
            return false;
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
    // returns TRUE if el removed, FALSE else
    public final boolean remove(final String key) {
        if (dBase.containsKey(key)) {
            System.out.println("removed");
            dBase.remove(key);
            return true;
        } else {
            System.out.println("not found");
            return false;
        }
    }
    
    private boolean isValid(String key) throws DataBaseCorrupt {
        int hCode = Math.abs(key.hashCode());
        int nDir = hCode % 16;
        int nFile = hCode / 16 % 16;
        String nameDir = new String(nDir + ".dir");
        String nameFile = new String(nFile + ".dat");
        if (nameFile.equals(dBasePath.getFileName().toString()) 
                && nameDir.equals(dBasePath.getParent().getFileName().toString())) {
            return true;
        } else {
            throw new DataBaseCorrupt("Key hash does not matches with table");
        }
    }
    public int getRowCount() {
        return dBase.size();
    }
   
    public String retCode() {
        String a = "";
        try {
            Path tableParentName = dBasePath.getParent().getFileName();
            String current = tableParentName.toString().substring(0, tableParentName.toString().length() - 4);
            
            if (current.matches("[0-9]")) {
                a = (0 + current);
            } else {
                a = (current);
            }
            current = dBasePath.getFileName().toString().substring(0, dBasePath.getFileName().toString().length() - 4);
            if (current.matches("[0-9]")) {
                a += (0 + current);
            } else {
                a += (current);
            }
        } catch (Exception ex) {
            System.out.println("Some errors " + ex.toString());
        }
       
        return a;
    }
    
    public Path tablePath() {
        return dBasePath;
    }

}
