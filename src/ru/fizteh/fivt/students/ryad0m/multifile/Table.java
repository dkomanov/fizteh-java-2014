package ru.fizteh.fivt.students.ryad0m.multifile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Table {
    private static int constMAX = 16;
    private TableNode[] tableNodes = new TableNode[constMAX * constMAX];
    private Path location;


    public Table(Path path) {
        location = path;
        for (int i = 0; i < constMAX; ++i) {
            for (int j = 0; j < constMAX; ++j) {
                Path nodePath = path.resolve(Integer.toString(i) + ".dir/" + Integer.toString(j) + ".dat");
                tableNodes[i * constMAX + j] = new TableNode(nodePath.normalize());
            }
        }
    }

    public void save() throws IOException {
        for (int i = 0; i < constMAX; ++i) {
            for (int j = 0; j < constMAX; ++j) {
                tableNodes[i * constMAX + j].save();
            }
        }
    }

    public void load() throws IOException, BadFormatException {
        for (int i = 0; i < constMAX; ++i) {
            for (int j = 0; j < constMAX; ++j) {
                tableNodes[i * constMAX + j].load();
            }
        }
    }

    private TableNode getNode(String key) {
        int hash = key.hashCode();
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

    public String[] getKeys() {
        List<String> str = new ArrayList<>();
        for (int i = 0; i < constMAX; ++i) {
            for (int j = 0; j < constMAX; ++j) {
                String[] set = tableNodes[i * constMAX + j].getKeys();
                for (String s : set) {
                    str.add(s);
                }

            }
        }
        return (String[]) str.toArray();
    }
}
