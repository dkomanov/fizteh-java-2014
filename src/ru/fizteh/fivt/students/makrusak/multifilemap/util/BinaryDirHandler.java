package multifilemap.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class BinaryDirHandler {
    private File path;
    private HashMap<String, BinaryFileHandler> binaryFileHandlers;

    public BinaryDirHandler(File getPath) throws IOException {
        path = getPath;
        binaryFileHandlers = new HashMap<String, BinaryFileHandler>();
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
                binaryFileHandlers.put(inFile.getName(), new BinaryFileHandler(
                        inFile));
            }
        }
    }

    public String work(String[] tokens) throws IOException {
        if (tokens.length >= 2) {
            String key = tokens[1];
            int file = key.hashCode() / 16 % 16;
            String fileStr = Integer.toString(file) + ".dat";
            File fileFile = new File(path, fileStr);
            if (!binaryFileHandlers.containsKey(fileStr)) {
                fileFile.createNewFile();
                binaryFileHandlers
                        .put(fileStr, new BinaryFileHandler(fileFile));
            }
            String res = binaryFileHandlers.get(fileStr).work(tokens);
            if (binaryFileHandlers.get(fileStr).getCount() == 0) {
                if (fileFile.listFiles() != null) {
                    for (File inFile : fileFile.listFiles()) {
                        inFile.delete();
                    }
                }
                fileFile.delete();
                binaryFileHandlers.remove(fileStr);
            }
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
