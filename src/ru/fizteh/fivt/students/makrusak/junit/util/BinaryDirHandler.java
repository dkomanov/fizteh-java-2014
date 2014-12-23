package junit.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class BinaryDirHandler {
    private File path;
    private HashMap<String, BinaryFileHandler> binaryFileHandlers;

    public void backSync() throws IOException {
        if (getCount() != 0) {
            path.mkdirs();
        }
        for (BinaryFileHandler curr : binaryFileHandlers.values()) {
            curr.backSync();
        }
        if (getCount() == 0) {
            path.delete();
        }
    }

    public BinaryDirHandler(File getPath, boolean onCreate) throws IOException {
        path = getPath;
        binaryFileHandlers = new HashMap<String, BinaryFileHandler>();
        if (onCreate) {
            if (!path.isDirectory()) {
                throw new IOException("Binary directory is not directory");
            }
            String[] parts = path.getName().split("\\.");
            try {
                int num = Integer.parseInt(parts[0]);
                if (parts.length != 2 || !parts[1].equals("dir") || num < 0
                        || num >= 16) {
                    throw new Exception();
                }
            } catch (Exception e) {
                throw new IOException("Incorrect directory name in table dir.");
            }
            if (path.listFiles() != null) {
                for (File inFile : path.listFiles()) {
                    binaryFileHandlers.put(inFile.getName(),
                            new BinaryFileHandler(inFile, true));
                }
            }
        }
    }

    public Map<String, String> getMap() {
        Map<String, String> res = new HashMap<String, String>();
        for (BinaryFileHandler handler : binaryFileHandlers.values()) {
            res.putAll(handler.getMap());
        }
        return res;
    }

    public String work(String[] tokens) throws IOException {
        if (tokens.length >= 2) {
            String key = tokens[1];
            int file = key.hashCode() / 16 % 16;
            String fileStr = Integer.toString(file) + ".dat";
            File fileFile = new File(path, fileStr);
            if (!binaryFileHandlers.containsKey(fileStr)) {
                binaryFileHandlers.put(fileStr, new BinaryFileHandler(fileFile,
                        false));
            }
            String res = binaryFileHandlers.get(fileStr).work(tokens);
            return res;
        }
        throw new IOException("Incorrect command");
    }

    public Set<String> getList() {
        Set<String> res = new TreeSet<String>();
        for (BinaryFileHandler cur : binaryFileHandlers.values()) {
            res.addAll(cur.getList());
        }
        return res;
    }

    public int getCount() {
        int res = 0;
        for (BinaryFileHandler cur : binaryFileHandlers.values()) {
            res += cur.getCount();
        }
        return res;
    }
}
