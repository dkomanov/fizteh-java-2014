package ru.fizteh.fivt.students.dmitry_morozov.multifilehashmap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class FileMap {
    private Map<String, String> table;
    private File dbFile;

    public FileMap(String path) throws Exception {
        table = new HashMap<String, String>();
        dbFile = new File(path);
        if (!dbFile.exists()) {
            if (!dbFile.createNewFile()) {
                throw new Exception("Couldldn't create db file");
            }
        } else {
            if (!dbFile.isFile()) {
                throw new Exception("Is not a file");
            }
        }
        if (!(dbFile.setReadable(true)) && dbFile.setWritable(true)) {
            throw new Exception("Couldn't set rw options");
        }
        DataInputStream in = new DataInputStream(new FileInputStream(dbFile));

        final int sizeOfInt = 4;

        while (true) {
            int len;
            String key = "";
            String value = "";
            if (in.available() >= sizeOfInt) {
                len = in.readInt();
                if (0 != len % 2 || in.available() < len) {
                    in.close();
                    throw new Exception("File was damaged");
                }
                len /= 2;
                while (len > 0) {
                    char curChar = in.readChar();
                    key += curChar;
                    len--;
                }
            } else {
                break;
            }
            if (in.available() < sizeOfInt) {
                in.close();
                throw new Exception("Couldn't set rw options");
            }
            len = in.readInt();
            if (0 != len % 2 || in.available() < len) {
                in.close();
                throw new Exception("File was damaged");
            }
            len /= 2;
            while (len > 0) {
                char curChar = in.readChar();
                value += curChar;
                len--;
            }
            table.put(key, value);
        }
        in.close();

    }

    public String put(String key, String value) {
        String res = "";
        if (table.containsKey(key)) {
            res = "overwrite\n";
            res += table.get(key);
            table.put(key, value);
        } else {
            res = "new";
            table.put(key, value);
        }
        return res;
    }

    public String get(String key) {
        String val = table.get(key);
        String res = "";
        if (val != null) {
            res = "found\n" + val;
        } else {
            res = "not found";
        }
        return res;
    }

    public void list(PrintWriter pw) {
        Set<Entry<String, String>> tableSet = table.entrySet();
        Iterator<Entry<String, String>> checkLast = tableSet.iterator();
        if (checkLast.hasNext()) {
            checkLast.next();
        }
        for (Entry<String, String> i : tableSet) {
            if (checkLast.hasNext()) {
                pw.print(i.getKey() + ", ");
                checkLast.next();
            } else {
                pw.print(i.getKey());
            }
        }
        // pw.println();
        pw.flush();
    }

    public String remove(String key) {
        String val = table.remove(key);
        if (val == null) {
            return "not found";
        } else {
            return "removed";
        }
    }

    public void exit() throws IOException {
        DataOutputStream out = new DataOutputStream(
                new FileOutputStream(dbFile));
        Set<Entry<String, String>> tableSet = table.entrySet();
        for (Entry<String, String> it : tableSet) {
            String key = it.getKey();
            String value = it.getValue();
            int len = key.length();
            out.writeInt(len * 2);
            out.writeChars(key);
            len = value.length();
            out.writeInt(len * 2);
            out.writeChars(value);
            // out.flush();
        }
        out.flush();
        out.close();
    }

    public boolean isEmpty() {
        return table.isEmpty();
    }
}
