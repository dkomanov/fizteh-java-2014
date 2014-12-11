package ru.fizteh.fivt.students.ryad0m.parallel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class DirTable {
    private static int constMAX = 16;
    private TableNode[] tableNodes = new TableNode[constMAX * constMAX];
    private Path location;


    public DirTable(Path path) throws IOException {
        location = path;
        for (int i = 0; i < constMAX; ++i) {
            for (int j = 0; j < constMAX; ++j) {
                Path nodePath = path.resolve(Integer.toString(i) + ".dir/" + Integer.toString(j) + ".dat");
                tableNodes[i * constMAX + j] = new TableNode(nodePath.normalize());
            }
        }

    }

    private void deleteDir(File dir) {
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteDir(f);
                } else {
                    f.delete();
                }
            }
        }
        dir.delete();
    }

    public void deleteData() {
        for (int i = 0; i < constMAX; ++i) {
            deleteDir(location.resolve(Integer.toString(i) + ".dir").toFile());
        }
    }

    public void save() throws IOException {
        deleteData();
        for (int i = 0; i < constMAX; ++i) {
            for (int j = 0; j < constMAX; ++j) {
                tableNodes[i * constMAX + j].save();
            }
        }
    }

    private TableNode getNode(String key) {
        int hash = Math.abs(key.hashCode());
        int ndir = hash % 16;
        int nfile = hash / 16 % 16;
        return tableNodes[ndir * constMAX + nfile];
    }

    public void put(String key, String value) {
        getNode(key).put(key, value);
    }

    public boolean containKey(String key) {
        return getNode(key).containKey(key);
    }

    public String get(String key) {
        return getNode(key).get(key);
    }

    public void remove(String key) {
        getNode(key).remove(key);
    }

    public int getSize() {
        int res = 0;
        for (int i = 0; i < constMAX; ++i) {
            for (int j = 0; j < constMAX; ++j) {
                res += tableNodes[i * constMAX + j].getSize();
            }
        }
        return res;
    }

    public Set<String> getKeys() {
        Set<String> str = new HashSet<>();
        for (int i = 0; i < constMAX; ++i) {
            for (int j = 0; j < constMAX; ++j) {
                str.addAll(tableNodes[i * constMAX + j].getKeys());
            }
        }
        return str;
    }
}
