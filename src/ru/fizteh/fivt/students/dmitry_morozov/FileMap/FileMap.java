package ru.fizteh.fivt.students.dmitry_morozov.filemap;

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
    private Map<String, String> _table;
    private File _dbFile;

    public FileMap(String path) throws IOException, Throwable {
        _table = new HashMap<String, String>();
        _dbFile = new File(path);
        if (!_dbFile.exists()) {
            if (!_dbFile.createNewFile()) {
                throw new Throwable("Couldldn't create db file");
            }
        } else {
            if (!_dbFile.isFile()) {
                throw new Throwable("Is not a file");
            }
        }
        if (!(_dbFile.setReadable(true)) && _dbFile.setWritable(true)) {
            throw new Throwable("Couldn't set rw options");
        }
        DataInputStream in = new DataInputStream(new FileInputStream(_dbFile));

        final int sizeOfInt = 4;

        while (true) {
            int len;
            String key = "", value = "";
            if (in.available() >= sizeOfInt) {
                len = in.readInt();
                if (0 != len % 2 || in.available() < len) {
                    throw new Throwable("File was damaged");
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
                throw new Throwable("Couldn't set rw options");
            }
            len = in.readInt();
            if (0 != len % 2 || in.available() < len) {
                throw new Throwable("File was damaged");
            }
            len /= 2;
            while (len > 0) {
                char curChar = in.readChar();
                value += curChar;
                len--;
            }
            _table.put(key, value);
        }
        in.close();

    }

    public String put(String key, String value) {
        String res = "";
        if (_table.containsKey(key)) {
            res = "overwrite\n";
            res += _table.get(key);
            _table.put(key, value);
        } else {
            res = "new";
            _table.put(key, value);
        }
        return res;
    }

    public String get(String key) {
        String val = _table.get(key);
        String res = "";
        if (null != val) {
            res = "found\n" + val;
        } else {
            res = "not found";
        }
        return res;
    }

    public void list(PrintWriter pw) {
        Set<Entry<String, String>> _tableSet = _table.entrySet();
        Iterator<Entry<String, String>> it = _tableSet.iterator();
        Iterator<Entry<String, String>> checkLast = _tableSet.iterator();
        if (checkLast.hasNext()) {
            checkLast.next();
        }
        while (it.hasNext()) {
            if (checkLast.hasNext()) {
                pw.print(it.next().getKey() + ", ");
                checkLast.next();
            } else {
                pw.print(it.next().getKey());
            }
        }
        pw.println();
        pw.flush();
    }

    public String remove(String key) {
        String val = _table.remove(key);
        if (null == val) {
            return "not found";
        } else {
            return "removed";
        }
    }

    public void exit() throws IOException {
        DataOutputStream out = new DataOutputStream(new FileOutputStream(
                _dbFile));
        Set<Entry<String, String>> _tableSet = _table.entrySet();
        Iterator<Entry<String, String>> it = _tableSet.iterator();
        while (it.hasNext()) {
            Entry<String, String> cur = it.next();
            String key = cur.getKey();
            String value = cur.getValue();
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
}
