package ru.fizteh.fivt.students.SergeyAksenov.JUnit;


import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class JUnitTable implements Table {

    public JUnitTable(String name, String tablePath) {
        dataBasePath = Paths.get(tablePath);
        File table = dataBasePath.toFile();
        changesCounter = 0;
        dataBase = new HashMap<>();
        if (table.exists()) {
            read();
            return;
        }
        if (!table.mkdir()) {
            System.out.println("Cannot create directory for new table");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int size() {
        return dataBase.size();
    }

    public String get(String key)
            throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("get");
        }
        if (dataBase.containsKey(key)) {
            return dataBase.get(key);
        } else {
            return null;
        }
    }

    public String put(String key, String value)
            throws IllegalArgumentException {
        if (key == null || value == null) {
            throw new IllegalArgumentException("put");
        }
        String ret = null;
        if (dataBase.containsKey(key)) {
            ret = dataBase.get(key);
            dataBase.remove(key);
        }
        dataBase.put(key, value);
        changesCounter++;
        return ret;
    }

    public String remove(String key)
            throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("remove");
        }
        String ret = null;
        if (dataBase.containsKey(key)) {
            ret = dataBase.get(key);
            dataBase.remove(key);
            changesCounter++;
           /* try {
                clear();
                write();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            */
        } else {
            ret = null;
        }
        return ret;
    }

    public List<String> list() {
        Set<String> keySet = dataBase.keySet();
        return new ArrayList<>(keySet);
    }

    public int commit() {
        try {
            clear();
            write();
            int ret = dataBase.size();
            changesCounter = 0;
            return ret;
        } catch (Exception e) {
            System.out.println("Error in writing to file");
            return -1;
        }
    }

    public int rollback() {
        int ret = changesCounter;
        dataBase.clear();
        read();
        changesCounter = 0;
        return ret;
    }

    public int getChangesCounter() {
        return changesCounter;
    }


    private String readUtil(DataInputStream inStream) throws IOException {
        int length = inStream.readInt();
        byte[] word = new byte[length];
        inStream.readFully(word);
        return new String(word, "UTF-8");
    }

    private void read() {
        File[] directories = dataBasePath.toFile().listFiles();
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

    private void writeUtil(DataOutputStream outStream, String str) throws IOException {
        byte[] byteStr = str.getBytes("UTF-8");
        outStream.writeInt(str.length());
        outStream.write(byteStr);
        outStream.flush();
        //   outStream.close();
    }

    private void write() throws Exception {
        for (Map.Entry<String, String> entry : dataBase.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            int hashcode = key.hashCode();
            int ndirectory = hashcode % 16;
            int nfile = hashcode / 16 % 16;
            File directory = new File(dataBasePath.toString() + File.separator + ndirectory + ".dir");
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
        File[] files = dataBasePath.toFile().listFiles();
        for (File file : files) {
            Executor.delete(file);
        }
    }

    private HashMap<String, String> dataBase;

    private Path dataBasePath;

    private int changesCounter;

    private String name;

}
