package ru.fizteh.fivt.students.SergeyAksenov.MultiFileHashMap;


import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public final class DataBase {

    public DataBase() {
        dataBase = new HashMap<>();
        try {
            dataBasePath = Paths.get(System.getProperty("fizteh.db.dir"));
            if (!dataBasePath.toFile().exists()) {
                System.out.println("Database does not exist");
                System.exit(-1);
            }
        } catch (Exception e) {
            System.err.println("Cannot create a database");
        }
    }
    private String readUtil(DataInputStream inStream) throws IOException {
        int length = inStream.readInt();
        byte[] word = new byte[length];
        inStream.readFully(word);
        return new String(word, "UTF-8");
    }

    private void read() {
        File[] directories = currentTablePath.toFile().listFiles();
        for (File dir : directories) {
            File[] files = dir.listFiles();
            try {
                for (File file : files) {
                    boolean end = false;
                    DataInputStream stream = new DataInputStream(new FileInputStream(file));
                    while (!end) {
                        try {
                            String key = readUtil(stream);
                            String value = readUtil(stream);
                            dataBase.put(key, value);
                        } catch (IOException e) {
                            end = true;
                        }
                    }

                }
            } catch (FileNotFoundException e) {
                System.out.println("Error in reading");
                return;
            }
        }
    }

    private void write() throws Exception {
        for (Map.Entry<String, String> entry : dataBase.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            int hashcode = key.hashCode();
            int ndirectory = hashcode % 16;
            int nfile = hashcode / 16 % 16;
            File directory = new File(currentTablePath.toString() + File.separator + ndirectory + ".dir");
            if (!directory.exists()) {
                if (!directory.mkdir()) {
                    throw new Exception("Error in writing");
                }
            }
            File file = new File(directory.getCanonicalPath() + File.separator
                    + nfile + ".dat");
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    throw new Exception("Error in writing");
                }
            }
            try (DataOutputStream stream = new DataOutputStream(new FileOutputStream(file))) {
                writeUtil(stream, key);
                writeUtil(stream, value);
            } catch (Exception e) {
                throw new Exception("Error in writing");
            }
        }
    }

    private void clear() {
        File[] files = currentTablePath.toFile().listFiles();
        for (File file : files) {
            Executor.delete(file);
        }
    }

    private void writeUtil(DataOutputStream outStream, String str) throws IOException {
        byte[] byteStr = str.getBytes("UTF-8");
        outStream.writeInt(str.length());
        outStream.write(byteStr);
    }

    public String getDataBasePath() {
        return dataBasePath.toString();
    }

    public void setUsingTable(String tablename) {
        if (currentTablePath != null) {
            try {
                clear();
                write();
            } catch (Exception e) {
                System.err.println("Cannot write table to files");
                return;
            }
        }
        currentTablePath = Paths.get(dataBasePath.toString() + File.separator + tablename);
        dataBase.clear();
        try {
            read();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static HashMap<String, String> getDataBase() {
        return dataBase;
    }

    public void close() {
        try {
            if (currentTablePath != null) {
                clear();
                write();
            }
        } catch (Exception e) {
            System.err.println("Cannot write database to file");
        }
    }

    public void drop(String tablename) {
        String tablePath = dataBasePath + File.separator + tablename;
        File fileToRem = new File(tablePath);
        if (!fileToRem.getParentFile().toString().equals(dataBasePath.toString()) ||
                !fileToRem.exists()) {
            System.out.println(tablename + " not exists");
        }
        if (currentTablePath != null) {
            if (tablePath.equals(currentTablePath.toString())) {
                dataBase.clear();
                currentTablePath = null;
            }
        }
        Executor.delete(fileToRem);
    }

    public String[] getTableNames() {
        return dataBasePath.toFile().list();
    }

    public String getCurrentTablePath () {
        if (currentTablePath == null) {
            return null;
        }
        return currentTablePath.toString();
    }
    private static HashMap<String, String> dataBase;
    private static Path dataBasePath;
    private static Path currentTablePath;
}