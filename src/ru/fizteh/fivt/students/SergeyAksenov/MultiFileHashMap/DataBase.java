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

    private String readUtil(File file) throws IOException {
        DataInputStream inStream = new DataInputStream(new FileInputStream(file));
        int length = inStream.readInt();
        byte[] word = new byte[length];
        inStream.readFully(word);
        return new String(word, "UTF-8");
    }

    private void read() throws Exception {
     //   boolean end = false;
     //   File tableDirectory = new File(dataBasePath.toString() + currentTablePath.toString());
        File tableDirectory = new File(currentTablePath.toString());
        File[] directories = tableDirectory.listFiles();
        for (File dir : directories) {
            File[] files = dir.listFiles();
            for (File file : files) {
                boolean end = false;
                while (!end) {
                    try {
                        String key = readUtil(file);
                        String value = readUtil(file);
                        dataBase.put(key, value);
                    } catch (IOException e) {
                        end = true;
                    }
                }
            }
        }
    }

    public void write() throws Exception {
        for (Map.Entry<String, String> entry : dataBase.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            int hashcode = key.hashCode();
            int ndirectory = hashcode % 16;
            int nfile = hashcode / 16 % 16;
            File directory = new File(currentTablePath.toString() + File.separator + ndirectory + ".dir");
            if (!directory.exists()) {
                directory.mkdir();
            }
            File file = new File(directory.getCanonicalPath() + File.separator +
                    nfile + ".dat");
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

    private void writeUtil(DataOutputStream outStream, String str) throws IOException {
        byte[] byteStr = str.getBytes("UTF-8");
        outStream.writeInt(str.length());
        outStream.write(byteStr);
    }

    public String getDataBasePath() {
        return dataBasePath.toString();
    }

    public void setUsingTable(String tablename) {
        try {
            write();
            if (currentTablePath.toFile().listFiles().length == 0) {
                Executor.delete(currentTablePath.toFile());
            } else {
                File[] files = currentTablePath.toFile().listFiles();
                for (File file : files) {
                    if (file.length() == 0) {
                        Executor.delete(file);
                    }
                }
            }
            currentTablePath = Paths.get(dataBasePath.toString() + File.separator + tablename);
            dataBase.clear();
            read();
        } catch (Exception e) {
            System.err.println("Cannot write table to files");
        }
    }

    public static HashMap<String, String> getDataBase() {
        return dataBase;
    }

    public void close() {
        try {
            if (currentTablePath.toFile().listFiles().length == 0) {
                Executor.delete(currentTablePath.toFile());
            } else {
                File[] files = currentTablePath.toFile().listFiles();
                for (File file : files) {
                    if (file.length() == 0) {
                        Executor.delete(file);
                    }
                }
            }
            write();
        } catch (Exception e) {
            System.err.println("Cannot write database to file");
        }
    }

    public void drop (String tablename) {
        String tablePath = dataBasePath + File.separator + tablename;
        File fileToRem = new File(tablePath);
        if (!fileToRem.getParentFile().toString().equals(dataBasePath.toString()) ||
                !fileToRem.exists()) {
            System.out.println(tablename + " not exists");
        }
        if (!(tablePath).equals(currentTablePath.toString())) {
            Executor.delete(fileToRem);
        } else {
            dataBase.clear();
            Executor.delete(fileToRem);
        }
    }

    public String[] getTableNames() {
       return dataBasePath.toFile().list();
    }

    private static HashMap<String, String> dataBase;
    private static Path dataBasePath;
    private static Path currentTablePath;
}