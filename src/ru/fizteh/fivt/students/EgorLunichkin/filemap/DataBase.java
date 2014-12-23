package ru.fizteh.fivt.students.EgorLunichkin.filemap;

import java.io.*;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;

public class DataBase {
    public DataBase(String path) throws Exception {
        File file = new File(path).getAbsoluteFile();
        dbPath = file.getPath();
        if (file.exists()) {
            db = readDataBase();
        } else {
            try {
                file.createNewFile();
            } catch (Exception ex) {
                throw new FileMapException(ex.getMessage());
            }
            db = new HashMap<String, String>();
        }
    }

    public DataBase() {}

    public String dbPath;
    protected HashMap<String, String> db;

    public HashMap<String, String> getDataBase() {
        return db;
    }

    public int dbSize() {
        return db.size();
    }

    private String readElement(DataInputStream in) throws FileMapException {
        try {
            int length = in.readInt();
            byte[] element = new byte[length];
            in.readFully(element);
            return new String(element, "UTF-8");
        } catch (IOException ex) {
            throw new FileMapException(ex.getMessage());
        }
    }

    private HashMap<String, String> readDataBase() throws Exception {
        HashMap<String, String> ans = new HashMap<String, String>();
        DataInputStream in;
        try {
            in = new DataInputStream(new FileInputStream(dbPath));
        } catch (FileNotFoundException ex) {
            throw new FileMapException(ex.getMessage());
        }
        while (true) {
            try {
                String key = readElement(in);
                String value = readElement(in);
                ans.put(key, value);
            } catch (Exception ex) {
                break;
            }
        }
        in.close();
        return ans;
    }

    private void writeElement(DataOutputStream out, String element) throws FileMapException {
        try {
            byte[] byteElement = element.getBytes("UTF-8");
            out.writeInt(byteElement.length);
            out.write(byteElement);
            out.flush();
        } catch (IOException ex) {
            throw new FileMapException(ex.getMessage());
        }
    }

    public void writeDataBase() throws Exception {
        DataOutputStream out;
        try {
            out = new DataOutputStream(new FileOutputStream(dbPath));
        } catch (FileNotFoundException ex) {
            throw new FileMapException(ex.getMessage());
        }
        Set<String> keys = db.keySet();
        Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
            String key = it.next();
            writeElement(out, key);
            writeElement(out, db.get(key));
        }
        out.close();
    }
}
