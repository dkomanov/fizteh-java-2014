package junit.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BinaryFileHandler {

    private File path;
    private Map<String, String> map;

    public BinaryFileHandler(File getPath, boolean onCreate) throws IOException {
        path = getPath;
        map = new HashMap<String, String>();
        if (onCreate) {
            if (!path.isFile()) {
                throw new IOException("Binary file is not file.");
            }

            String[] parts = path.getName().split("\\.");
            try {
                int num = Integer.parseInt(parts[0]);
                if (parts.length != 2 || !parts[1].equals("dat") || num < 0
                        || num >= 16) {
                    throw new Exception();
                }
            } catch (Exception e) {
                throw new IOException("Incorrect file name for binary file.");
            }
            sync();
        }
    }

    private String readString(int bytes, DataInputStream in) throws IOException {
        byte[] b = new byte[bytes];
        for (int i = 0; i < bytes; i++) {
            int c = in.readByte();
            b[i] = (byte) c;
        }
        return new String(b, "UTF-8");
    }

    private void writeString(String s, DataOutputStream out) throws IOException {
        byte[] b = s.getBytes("UTF-8");
        out.writeInt(b.length);
        for (int i = 0; i < b.length; i++) {
            out.writeByte(b[i]);
        }
    }

    private void sync() throws IOException {
        try (BufferedInputStream bufferedStream = new BufferedInputStream(
                new FileInputStream(path));
                DataInputStream dataStream = new DataInputStream(bufferedStream)) {
            map = new HashMap<String, String>();
            while (bufferedStream.available() > 0) {
                int lenKey = dataStream.readInt();
                String stringKey = readString(lenKey, dataStream);
                int lenVal = dataStream.readInt();
                String stringVal = readString(lenVal, dataStream);
                map.put(stringKey, stringVal);
            }
        }
    }

    public void backSync() throws IOException {
        if (path.isFile() && getCount() == 0) {
            path.delete();
            return;
        }
        if (getCount() != 0) {
            if (!path.isFile()) {
                path.createNewFile();
            }
            try (BufferedOutputStream bufferedStream = new BufferedOutputStream(
                    new FileOutputStream(path));
                    DataOutputStream dataStream = new DataOutputStream(
                            bufferedStream)) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    writeString(entry.getKey(), dataStream);
                    writeString(entry.getValue(), dataStream);
                }
            }
        }
    }

    public Map<String, String> getMap() {
        return map;
    }

    private String put(String key, String value) {
        if (map.containsKey(key)) {
            String oldVal = map.get(key);
            map.put(key, value);
            return "overwrite\n" + oldVal;
        } else {
            map.put(key, value);
            return "new";
        }
    }

    private String get(String key) {
        if (map.containsKey(key)) {
            return "found\n" + map.get(key);
        } else {
            return "not found";
        }
    }

    private String remove(String key) {
        if (map.containsKey(key)) {
            map.remove(key);
            return "removed";
        } else {
            return "not found";
        }
    }

    public String work(String[] parts) throws IOException {
        if (parts.length == 3 && parts[0].equals("put")) {
            return put(parts[1], parts[2]);
        }
        if (parts.length == 2) {
            if (parts[0].equals("get")) {
                return get(parts[1]);
            }
            if (parts[0].equals("remove")) {
                return remove(parts[1]);
            }
        }
        throw new IOException("Incorrect command");
    }

    public Set<String> getList() {
        return map.keySet();
    }

    public int getCount() {
        return map.keySet().size();
    }
}
