package ru.fizteh.fivt.students.anastasia_ermolaeva.multifilehashmap;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ru.fizteh.fivt.students.anastasia_ermolaeva.
        multifilehashmap.util.ExitException;
public class Table implements Map<String, String>, AutoCloseable {
    static final int DIR_AMOUNT = 16;
    static final int FILES_AMOUNT = 16;
    private Map<String, String> allRecords = null;
    private Path dbPath = null; // The table's directory.
    private Path rootPath; //Root's directory.
    private String name;

    public Table(final Path rootPath, final String name) throws ExitException {
        this.rootPath = rootPath;
        this.name = name;
        this.allRecords = new HashMap<>();
        create();
    }
    public static void main() {

    }
    public Map<String, String> getAllRecords() {
        return allRecords;
    }
    public Path getTablePath() {
        return dbPath;
    }
    public final void create() throws ExitException {
        try {
            String path = rootPath.toAbsolutePath().toString()
                    + File.separator + name;
            dbPath = Paths.get(path);
            read();
        } catch (IOException e) {
            System.err.println("Can't create a database file");
            throw new ExitException(-1);
        }
    }
    private String readUtil(DataInputStream inStream) throws IOException {
        int length = inStream.readInt();
        byte[] wordArray = new byte[length];
        inStream.readFully(wordArray);
        return new String(wordArray, "UTF-8");
    }

    private void read() throws IOException, ExitException {
        File pathDirectory =  dbPath.toFile();
        File[] tableDirectories = pathDirectory.listFiles();
        for (File t: tableDirectories) {
            // Checking subdirectories.
            if (!t.isDirectory()) {
                System.err.println("Table subdirectories " +
                        "are not actually directories");
                throw new ExitException(-1);
            }
        }
        for (File directory: tableDirectories) {
            File[] directoryFiles = directory.listFiles();
            int k = directory.getName().indexOf('.');
            try {
                /*
                Delete .dir and check(automatically )
                if the subdirectory has the suitable name.
                If not, then parseInt throws NumberFormatException,
                error message is shown.
                Then program would finish with exit code != 0.
                 */
                int nDirectory = Integer.parseInt(
                        directory.getName().substring(0, k));
                for (File file : directoryFiles) {
                    try {
                        k = file.getName().indexOf('.');
                        /*
                        Checking files' names the same way
                        we did with directories earlier.
                        */
                        int nFile = Integer.parseInt(
                                file.getName().substring(0, k));
                        DataInputStream inStream = new
                                DataInputStream(new FileInputStream(file.getAbsolutePath()));
                        boolean end = false;
                        while (!end) {
                            try {
                                String key = readUtil(inStream);
                                String value = readUtil(inStream);
                                allRecords.put(key, value);
                            } catch (IOException e) {
                                end = true;
                            }
                        }
                        inStream.close();
                    } catch (NumberFormatException t) {
                        System.err.println("Subdirectories' files "
                                + "have wrong names, "
                                + "expected(0.dat-15.dat)");
                        throw new ExitException(-1);
                    }
                }
            } catch (NumberFormatException t) {
                System.err.println("Subdirectories' names are wrong, "
                        + "expected(0.dir - 15.dir)");
                throw new ExitException(-1);
            }
        }

    }

    private void write() throws Exception {
        Map<String, String>[][] db = new Map[DIR_AMOUNT][FILES_AMOUNT];
        for (int i = 0; i < DIR_AMOUNT; i++) {
            for (int j = 0; j < FILES_AMOUNT; j++) {
                db[i][j] = new HashMap<>();
            }
        }
        for (Map.Entry<String, String> entry: allRecords.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            int nDirectory = Math.abs(key.getBytes("UTF-8")[0] % DIR_AMOUNT);;
            int nFile = Math.abs((key.getBytes("UTF-8")[0] / DIR_AMOUNT) % FILES_AMOUNT);
            db[nDirectory][nFile].put(key, value);
        }
        for (int i = 0; i < DIR_AMOUNT; i++) {
            for (int j = 0; j < FILES_AMOUNT; j++) {
                try {
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
                                throw new Exception("Cannot create directory");
                            }
                        }
                        String newFilePath = directory.getAbsolutePath()
                                + File.separator
                                + nFile.toString()
                                + ".dat";
                        File file = new File(newFilePath);
                        if (!file.exists()) {
                            if (!file.createNewFile()) {
                                throw new Exception("Cannot create file");
                            }
                        }
                        DataOutputStream outStream = new DataOutputStream(
                                new FileOutputStream(newFilePath));
                        for (Map.Entry<String, String> entry
                                : db[i][j].entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            try {
                                writeUtil(key, outStream);
                                writeUtil(value, outStream);
                            } catch (SecurityException e) {
                                throw new Exception("Cannot create "
                                        + "directory: "
                                        + "access denied");
                            }
                        }
                        outStream.close();
                    } else {
                        //Deleting empty files.
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
                            Files.deleteIfExists(file.toPath());
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error with files");
                }
            }
        }

    }
    public final void close() throws Exception {
        write();
        //Deleting empty directories.
        File pathDirectory =  dbPath.toFile();
        File[] tableDirectories = pathDirectory.listFiles();
        for (File directory: tableDirectories) {
            File[] directoryFiles = directory.listFiles();
            if (directoryFiles.length == 0) {
                try {
                    Files.delete(directory.toPath());
                } catch (IOException | SecurityException e) {
                    System.err.println(e);
                }
            }
        }
    }
    private void writeUtil(final String word,
                           DataOutputStream outStream) throws IOException {
        byte[] byteWord = word.getBytes("UTF-8");
        outStream.writeInt(byteWord.length);
        outStream.write(byteWord);
        outStream.flush();
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

    }

    @Override
    public void clear() {

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
