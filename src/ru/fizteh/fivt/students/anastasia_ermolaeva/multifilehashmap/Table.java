package ru.fizteh.fivt.students.anastasia_ermolaeva.multifilehashmap;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import ru.fizteh.fivt.students.anastasia_ermolaeva.
        multifilehashmap.util.ExitException;
public class Table implements Map<String, String>, AutoCloseable {
    static final int DIR_AMOUNT = 16;
    static final int FILES_AMOUNT = 16;
    private Map<String, String> allRecords = null;
    /*
    * Path to table's directory.
    */
    private Path dbPath;
    /* 
    * Path to root directory.
    */
    private Path rootPath; 
    private String name;

    public Table(final Path rootPath, final String name)  {
        this.rootPath = rootPath;
        String path = rootPath.toAbsolutePath().toString()
                + File.separator + name;
        dbPath = Paths.get(path);
        this.name = name;
        this.allRecords = new HashMap<>();
        create();
    }
    public Table(final Path rootPath, final String name, Map<String, String> records) {
        this.rootPath = rootPath;
        String path = rootPath.toAbsolutePath().toString()
                + File.separator + name;
        dbPath = Paths.get(path);
        this.name = name;
        this.allRecords = Collections.synchronizedMap(records);
    }
    private void create() {
        try {
            read();
        } catch (ExitException e) {
            System.exit(e.getStatus());
        }
    }

    public static void main() {

    }
    public Map<String, String> getAllRecords() {
        return allRecords;
    }
    private String readUtil(final RandomAccessFile dbFile) throws ExitException {
        try {
            int wordLength = dbFile.readInt();
            byte[] word = new byte[wordLength];
            dbFile.read(word, 0, wordLength);
            return new String(word, "UTF-8");
        } catch (IOException | SecurityException e) {
            System.err.println("Error reading the table");
            throw new ExitException(1);
        }
    }
    private void read() throws ExitException {
        File pathDirectory =  dbPath.toFile();
        if (pathDirectory.list().length == 0) {
            return;
        }
        File[] tableDirectories = pathDirectory.listFiles();
        for (File t: tableDirectories) {
            /*
            * Checking subdirectories.
            */
            if (!t.isDirectory()) {
                System.err.println("Table subdirectories "
                        + "are not actually directories");
                throw new ExitException(1);
            }
        }
        for (File directory: tableDirectories) {
            File[] directoryFiles = directory.listFiles();
            int k = directory.getName().indexOf('.');
            if ((k < 0) || !(directory.getName().substring(k).equals(".dir"))) {
                System.err.println("Table subdirectories doesn't "
                        + "have appropriate name");
                throw new ExitException(1);
            }
            try {
                /*
                Delete .dir and check(automatically )
                if the subdirectory has the suitable name.
                If not, then parseInt throws NumberFormatException,
                error message is shown.
                Then program would finish with exit code != 0.
                 */
                if (directory.list().length == 0) {
                    System.err.println("Table has the wrong format");
                    throw new ExitException(1);
                }
                int nDirectory = Integer.parseInt(
                        directory.getName().substring(0, k));
                for (File file : directoryFiles) {
                    try {
                        k = file.getName().indexOf('.');
                        /*
                        Checking files' names the same way
                        we did with directories earlier.
                        */
                        if ((k < 0) || !(file.getName().substring(k).equals(".dat"))) {
                            System.err.println("Table subdirectory's files doesn't "
                                    + "have appropriate name");
                            throw new ExitException(1);
                        }
                        int nFile = Integer.parseInt(
                                file.getName().substring(0, k));
                        try (RandomAccessFile dbFile = new RandomAccessFile(file.getAbsolutePath(), "r")) {
                            if (dbFile.length() > 0) {
                                while (dbFile.getFilePointer() < dbFile.length()) {
                                    String key = readUtil(dbFile);
                                    String value = readUtil(dbFile);
                                    allRecords.put(key, value);
                                }
                            }
                            dbFile.close();
                        } catch (IOException e) {
                            System.err.println("Error reading to table");
                            throw new ExitException(1);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Subdirectories' files "
                                + "have wrong names, "
                                + "expected(0.dat-15.dat)");
                        throw new ExitException(1);
                    }
                }
            } catch (NumberFormatException e) {
                System.err.println("Subdirectories' names are wrong, "
                        + "expected(0.dir - 15.dir)");
                throw new ExitException(1);
            }
        }
    }

    private void write() throws  ExitException {
        Map<String, String>[][] db = new Map[DIR_AMOUNT][FILES_AMOUNT];
        for (int i = 0; i < DIR_AMOUNT; i++) {
            for (int j = 0; j < FILES_AMOUNT; j++) {
                db[i][j] = new HashMap<>();
            }
        }
        for (Entry<String, String> entry: allRecords.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            try {
                int nDirectory = Math.abs(key.getBytes("UTF-8")[0] % DIR_AMOUNT);
                int nFile = Math.abs((key.getBytes("UTF-8")[0] / DIR_AMOUNT) % FILES_AMOUNT);
                db[nDirectory][nFile].put(key, value);
            } catch (UnsupportedEncodingException e) {
                System.err.println("Can't encode the record");
                throw new ExitException(1);
            }
        }
        for (int i = 0; i < DIR_AMOUNT; i++) {
            for (int j = 0; j < FILES_AMOUNT; j++) {
                if (!db[i][j].isEmpty()) {
                    Integer nDirectory = i;
                    Integer nFile = j;
                    String newPath = dbPath.toAbsolutePath().toString()
                            + File.separator
                            + nDirectory.toString()
                            + ".dir";
                    File directory = new File(newPath);
                    if (!directory.exists()) {
                        if (!directory.mkdir()) {
                            System.err.println("Cannot create directory");
                            throw new ExitException(1);
                        }
                    }
                    String newFilePath = directory.getAbsolutePath()
                                + File.separator
                                + nFile.toString()
                                + ".dat";
                    File file = new File(newFilePath);
                    try {
                        file.createNewFile();
                    } catch (IOException | SecurityException e) {
                        System.err.println(e);
                        throw new ExitException(1);
                    }
                    try (RandomAccessFile dbFile = new
                                RandomAccessFile(file, "rw")) {
                        dbFile.setLength(0);
                        for (Entry<String, String> entry
                                    : db[i][j].entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            writeUtil(key, dbFile);
                            writeUtil(value, dbFile);
                        }
                        dbFile.close();
                    } catch (IOException e) {
                            System.err.println(e);
                            throw new ExitException(1);
                        }
                } else {
                /*
                * Deleting empty files and directories.
                */
                    Integer nDirectory = i;
                    Integer nFile = j;
                    String newPath = dbPath.toAbsolutePath().toString()
                                + File.separator
                                + nDirectory.toString()
                                + ".dir";
                    File directory = new File(newPath);
                    if (directory.exists()) {
                        String newFilePath = directory.getAbsolutePath()
                                + File.separator
                                + nFile.toString()
                                + ".dat";
                        File file = new File(newFilePath);
                        try {
                            Files.deleteIfExists(file.toPath());
                        } catch (IOException | SecurityException e) {
                            System.err.println(e);
                            throw new ExitException(1);
                        }
                        if (directory.list().length == 0) {
                            try {
                                Files.delete(directory.toPath());
                            } catch (IOException e) {
                                System.err.println(e);
                                throw new ExitException(1);
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < DIR_AMOUNT; i++) {
            for (int j = 0; j < FILES_AMOUNT; j++) {
                db[i][j].clear();
            }
        }
    }
    public final void close() throws ExitException {
        write();
    }

    private void writeUtil(final String word,
                           final RandomAccessFile dbFile) throws ExitException {
        try {
            dbFile.writeInt(word.getBytes("UTF-8").length);
            dbFile.write(word.getBytes("UTF-8"));
        } catch (IOException e) {
            System.err.println(e);
            throw new ExitException(1);
        }
    }
    @Override
    public int size() {
        return allRecords.size();
    }

    @Override
    public boolean isEmpty() {
        return allRecords.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return allRecords.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return allRecords.containsValue(value);
    }

    @Override
    public String get(Object key) {
        return allRecords.get(key);
    }

    @Override
    public String put(String key, String value) {
        return allRecords.put(key, value);
    }

    @Override
    public String remove(Object key) {
        return allRecords.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        allRecords.putAll(m);
    }

    @Override
    public void clear() {
        allRecords.clear();
    }

    @Override
    public Set<String> keySet() {
        return allRecords.keySet();
    }

    @Override
    public Collection<String> values() {
        return allRecords.values();
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return allRecords.entrySet();
    }
}
