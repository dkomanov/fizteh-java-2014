package ru.fizteh.fivt.students.dmitry_morozov.multifilehashmap;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.BitSet;
import java.util.TreeSet;

public class MultiFileHashMap {

    private final int fileMapsAm = 256;
    private final int dirsAm = 16;
    // private final int filesByDir = 16;
    private File rootDir;
    private BitSet openedMaps;
    private FileMap[] maps;

    private File safeMkDir(String path) throws Exception {
        File dir = new File(path);
        if (dir.exists()) {
            if (!dir.isDirectory()) {
                throw new Exception("File " + path
                        + " already exists and it's not a directory");
            }
        } else {
            dir.mkdir();
        }
        return dir;
    }

    public MultiFileHashMap(String path) throws Exception {
        rootDir = safeMkDir(path);
        openedMaps = new BitSet(fileMapsAm);
        openedMaps.clear();
        maps = new FileMap[fileMapsAm];
        for (int i = 0; i < dirsAm; ++i) {
            String suffix = "/";
            if (i < 10) {
                suffix += "0";
            }
            suffix += Integer.toString(i);
            suffix += ".dir";
            safeMkDir(path + suffix);
        }
    }

    public String put(String key, String value) throws Exception {
        int mapNum = Math.abs(key.hashCode() % fileMapsAm);
        if (openedMaps.get(mapNum)) {
            return maps[mapNum].put(key, value);
        } else {
            maps[mapNum] = new FileMap(getPath(mapNum)); // Exception can be
                                                         // thrown.
            openedMaps.set(mapNum);
            return maps[mapNum].put(key, value);
        }
    }

    public String get(String key) throws Exception {
        int mapNum = Math.abs(key.hashCode() % fileMapsAm);
        if (openedMaps.get(mapNum)) {
            return maps[mapNum].get(key);
        } else {
            maps[mapNum] = new FileMap(getPath(mapNum)); // Exception can be
                                                         // thrown.
            openedMaps.set(mapNum);
            return maps[mapNum].get(key);
        }
    }

    public String remove(String key) throws Exception {
        int mapNum = Math.abs(key.hashCode() % fileMapsAm);
        if (openedMaps.get(mapNum)) {
            return maps[mapNum].remove(key);
        } else {
            maps[mapNum] = new FileMap(getPath(mapNum)); // Exception can be
                                                         // thrown.
            openedMaps.set(mapNum);
            return maps[mapNum].remove(key);
        }
    }

    public void list(PrintWriter pw) throws Exception {
        boolean printed = false;
        for (int i = 0; i < fileMapsAm; i++) {
            if (openedMaps.get(i)) {
                if (printed && !maps[i].isEmpty()) {
                    pw.print(", ");
                }
                maps[i].list(pw);
            } else {
                maps[i] = new FileMap(getPath(i)); // Exception can be
                                                   // thrown.
                if (printed && !maps[i].isEmpty()) {
                    pw.print(", ");
                }
                openedMaps.set(i);
                maps[i].list(pw);
            }
            if (!printed) {
                printed = !maps[i].isEmpty();
            }
        }
        if (printed) {
            pw.println();
        }        
        pw.flush();
    }

    public void exit() throws IOException {
        TreeSet<Integer> toDelete = new TreeSet<Integer>();
        for (int i = 0; i < fileMapsAm; i++) {
            if (openedMaps.get(i)) {
                if (maps[i].isEmpty()) {
                    toDelete.add(i);
                }
                maps[i].exit();
            }
        }
        for (Integer num : toDelete) {
            File tdel = new File(getPath(num));
            tdel.delete();
        }
        for (int i = 0; i < dirsAm; i++) {
            String suffix = "/";
            if (i < 10) {
                suffix += "0";
            }
            suffix += Integer.toString(i);
            suffix += ".dir";
            File tdir = new File(rootDir.getAbsolutePath() + suffix);
            tdir.delete(); // Won't be deleted if there're any files.
        }
    }

    private String getPath(int hash) { // Returns path to db-file by number of
                                       // database.
        int ndir = Math.abs(hash % dirsAm);
        int nfile = Math.abs(hash / dirsAm % dirsAm);
        String path = rootDir.getAbsolutePath();
        String suffix = "/";
        if (ndir < 10) {
            suffix += "0";
        }
        suffix += Integer.toString(ndir) + ".dir/";
        if (nfile < 10) {
            suffix += "0";
        }
        suffix += Integer.toString(nfile) + ".dat";
        path += suffix;

        return path;
    }
}
