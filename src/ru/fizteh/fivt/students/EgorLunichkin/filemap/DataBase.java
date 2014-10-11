package ru.fizteh.fivt.students.EgorLunichkin.filemap;

import java.io.*;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;

public class DataBase {
    public DataBase(String path) throws FileMapException {
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

    private String dbPath;
    private HashMap<String, String> db;

    public HashMap<String, String> getDataBase() {
        return db;
    }

    private String readElement(DataInputStream in) throws FileMapException {
        try {
            short length = in.readShort();
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < length; ++i) {
                str.append(in.readChar());
            }
            return str.toString();
        } catch (IOException ex) {
            throw new FileMapException(ex.getMessage());
        }
    }

    private HashMap<String, String> readDataBase() throws FileMapException {
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
        return ans;
    }

    private void writeElement(DataOutputStream out, String element) throws FileMapException {
        try {
            out.writeInt(element.length());
            out.writeChars(element);
        } catch (IOException ex) {
            throw new FileMapException(ex.getMessage());
        }
    }

    public void writeDataBase() throws FileMapException {
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
    }
}
