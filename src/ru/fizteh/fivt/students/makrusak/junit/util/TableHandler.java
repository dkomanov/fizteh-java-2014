package junit.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class TableHandler {
    private File path;
    private HashMap<String, BinaryDirHandler> binaryDirHandlers;
    private ArrayList<ArrayList<String>> commands;

    public String getName() {
        return path.getName();
    }

    public int getCommandsCount() {
        return commands.size();
    }

    private void backSync() throws IOException {
        for (BinaryDirHandler curr : binaryDirHandlers.values()) {
            curr.backSync();
        }
    }

    private void build() throws IOException {
        binaryDirHandlers = new HashMap<String, BinaryDirHandler>();
        if (!path.isDirectory()) {
            throw new IOException("Table is not directory");
        }
        if (path.listFiles() != null) {
            for (File inFile : path.listFiles()) {
                binaryDirHandlers.put(inFile.getName(), new BinaryDirHandler(
                        inFile, true));
            }
        }

    }

    public TableHandler(File getPath) throws IOException {
        path = getPath;
        commands = new ArrayList<ArrayList<String>>();
        build();
    }

    public Set<String> getList() {
        Set<String> res = new TreeSet<String>();
        for (BinaryDirHandler cur : binaryDirHandlers.values()) {
            res.addAll(cur.getList());
        }
        return res;
    }

    private String list() {
        String resString = "";
        Set<String> res = getList();
        String[] keys = res.toArray(new String[res.size()]);

        if (keys.length == 0) {
            return resString;
        }
        resString += keys[0];
        for (int i = 1; i < keys.length; i++) {
            resString += ", " + keys[i];
        }
        return resString;
    }

    public String rollback() throws IOException {
        int revertedCount = commands.size();
        commands.clear();
        build();
        return Integer.toString(revertedCount);
    }

    public Map<String, String> getMap() {
        Map<String, String> resMap = new HashMap<String, String>();
        for (BinaryDirHandler handler : binaryDirHandlers.values()) {
            resMap.putAll(handler.getMap());
        }
        return resMap;
    }

    private int diff(Map<String, String> startMap, Map<String, String> endMap) {
        Set<String> keys = new TreeSet<String>(startMap.keySet());
        keys.addAll(endMap.keySet());
        int count = 0;
        for (String key : keys) {
            if (!startMap.containsKey(key) || !endMap.containsKey(key)
                    || !endMap.get(key).equals(startMap.get(key))) {
                count += 1;
            }
        }
        return count;
    }

    public String commit() throws IOException {
        build();
        Map<String, String> startMap = getMap();
        ArrayList<ArrayList<String>> runCommands = new ArrayList<ArrayList<String>>();
        runCommands.addAll(commands);
        for (ArrayList<String> command : runCommands) {
            String[] tokens = command.toArray(new String[command.size()]);
            work(tokens);
        }
        Map<String, String> endMap = getMap();
        int count = diff(startMap, endMap);
        backSync();
        commands.clear();
        return Integer.toString(count);
    }

    private String delegate(String[] tokens) throws IOException {
        String key = tokens[1];
        int dir = key.hashCode() % 16;
        String dirStr = Integer.toString(dir) + ".dir";
        File dirFile = new File(path, dirStr);
        if (!binaryDirHandlers.containsKey(dirStr)) {
            binaryDirHandlers.put(dirStr, new BinaryDirHandler(dirFile, false));
        }
        String res = binaryDirHandlers.get(dirStr).work(tokens);
        return res;
    }

    public String work(String[] tokens) throws IOException {
        if (tokens.length == 1 && tokens[0].equals("list")) {
            return list();
        } else if (tokens.length == 1 && tokens[0].equals("rollback")) {
            return rollback();
        } else if (tokens.length == 1 && tokens[0].equals("commit")) {
            return commit();
        } else if (tokens.length >= 2) {
            if (tokens[0].equals("remove") || tokens[0].equals("put")) {
                commands.add(new ArrayList<String>(Arrays.asList(tokens)));
            }
            return delegate(tokens);
        }
        throw new IOException("Incorrect command");
    }

    public int getCount() {
        int res = 0;
        for (BinaryDirHandler cur : binaryDirHandlers.values()) {
            res += cur.getCount();
        }
        return res;
    }
}
