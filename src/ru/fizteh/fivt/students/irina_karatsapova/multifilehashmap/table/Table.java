package ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.table;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {
    public static File dir;
    public static Map<String, String> map = new HashMap<String, String>();
    public static List<String>[][] keys = new ArrayList[16][16];
    public static boolean loaded = false;

    public static void addKey(String key) {
        byte keyHash = key.getBytes()[0];
        int dir = keyHash % 16;
        int file = keyHash / 16 % 16;
        Table.keys[dir][file].add(key);
    }

    public static int countValues() {
        int keysNumber = 0;
        for (int dir = 0; dir < 16; dir++) {
            for (int file = 0; file < 16; file++) {
                keysNumber += Table.keys[dir][file].size();
            }
        }
        return keysNumber;
    }

    static void keysClear() {
        for (int dir = 0; dir < 16; dir++) {
            for (int file = 0; file < 16; file++) {
                Table.keys[dir][file].clear();
            }
        }
    }

    public static void initKeysArray() {
        for (int dir = 0; dir < 16; dir++) {
            for (int file = 0; file < 16; file++) {
                Table.keys[dir][file] = new ArrayList<String>();
            }
        }
    }
}
