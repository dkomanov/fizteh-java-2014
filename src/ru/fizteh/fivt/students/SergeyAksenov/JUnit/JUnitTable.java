package ru.fizteh.fivt.students.SergeyAksenov.JUnit;


import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class JUnitTable implements Table {

    public JUnitTable(String name, String tablePath) {
        DataBasePath = Paths.get(tablePath);
        File table = DataBasePath.toFile();
        ChangesCounter = 0;
        DataBase = new HashMap<>();
        if (table.exists()) {
            read();
            return;
        }
        if (!table.mkdir()) {
            System.out.println("Cannot create directory for new table");
        }
        Name = name;
    }

    public String getName() {
        return Name;
    }

    public int size() {
        return DataBase.size();
    }

    public String get(String key)
            throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("get");
        }
        if (DataBase.containsKey(key)) {
            return DataBase.get(key);
        } else return null;
    }

    public String put(String key, String value)
            throws IllegalArgumentException {
        if (key == null || value == null) {
            throw new IllegalArgumentException("put");
        }
        String ret = null;
        if (DataBase.containsKey(key)) {
            ret = DataBase.get(key);
            DataBase.remove(key);
        }
        DataBase.put(key, value);
        ChangesCounter++;
        return ret;
    }

    public String remove(String key)
            throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("remove");
        }
        String ret = null;
        if (DataBase.containsKey(key)) {
            ret = DataBase.get(key);
            DataBase.remove(key);
            ChangesCounter++;
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
        Set<String> keySet = DataBase.keySet();
        return new ArrayList<>(keySet);
    }

    public int commit() {
        try {
            clear();
            write();
            int ret = DataBase.size();
            ChangesCounter = 0;
            return ret;
        } catch (Exception e) {
            System.out.println("Error in writing to file");
            return -1;
        }
    }

    public int rollback() {
        int ret = ChangesCounter;
        DataBase.clear();
        read();
        ChangesCounter = 0;
        return ret;
    }

    public int getChangesCounter() {
        return ChangesCounter;
    }


    private String readUtil(DataInputStream inStream) throws IOException {
        // DataInputStream inStream = new DataInputStream(new FileInputStream(file));
        int length = inStream.readInt();
        byte[] word = new byte[length];
        inStream.readFully(word);
        return new String(word, "UTF-8");
    }

    private void read() {
        File[] directories = DataBasePath.toFile().listFiles();
        for (File dir : directories) {
            File[] files = dir.listFiles();
            try {
                for (File file : files) {
                    boolean end = false;
                    DataInputStream stream = new DataInputStream(new FileInputStream(file));
                    while (!end) {
                        //DataInputStream stream = new DataInputStream(new FileInputStream(file));
                        try /*(DataInputStream stream = new DataInputStream(new FileInputStream(file)))*/ {
                            String key = readUtil(stream);
                            String value = readUtil(stream);
                            DataBase.put(key, value);
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
        for (Map.Entry<String, String> entry : DataBase.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            int hashcode = key.hashCode();
            int ndirectory = hashcode % 16;
            int nfile = hashcode / 16 % 16;
            File directory = new File(DataBasePath.toString() + File.separator + ndirectory + ".dir");
            if (!directory.exists()) {
                if (!directory.mkdir()) {
                    throw new Exception("Error in writing");
                }
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

    private void clear() {
        File[] files = DataBasePath.toFile().listFiles();
        for (File file : files) {
            Executor.delete(file);
        }
    }

    private HashMap<String, String> DataBase;

    private Path DataBasePath;

    private int ChangesCounter;

    private String Name;

}
