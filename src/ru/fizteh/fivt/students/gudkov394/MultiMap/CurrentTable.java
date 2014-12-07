package ru.fizteh.fivt.students.gudkov394.MultiMap;

import ru.fizteh.fivt.students.gudkov394.shell.CurrentDirectory;
import ru.fizteh.fivt.students.gudkov394.shell.RemoveDirectory;

import java.io.File;
import java.util.HashMap;
import java.util.IllegalFormatCodePointException;
import java.util.Map;
import java.util.Set;

public class CurrentTable {
    String name;
    Map currentTable = new HashMap<String, String>();
    private int number = 0;

    public CurrentTable(String nameTmp) {
        name = nameTmp;
        //   create();
    }

    public CurrentTable() {
        name = null;
    }

    public String getName() {
        return name;
    }

    public String getHomeDirectory() {
        return System.getProperty("fizteh.db.dir") + File.separator + getName();
    }

    void write() {
        if (System.getProperty("fizteh.db.dir") == null) {
            System.err.println("You forgot directory");
            System.exit(4);
        }
        String newPath = System.getProperty("fizteh.db.dir") + File.separator + getName();
        File f = new File(newPath);
        Write w = new Write(this, f);
    }

    public Set<String> keySet() {
        return currentTable.keySet();
    }

    public String get(String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }
        if (!currentTable.containsKey(s)) {
            return null;
        }
        return String.valueOf(currentTable.get(s));
    }

    public String put(String key, String value) {
        ++number;
        String oldValue = null;
        if (currentTable.containsKey(key)) {
            oldValue = get(key);
        }
        currentTable.put(key, value);
        return oldValue;

    }

    public boolean containsKey(String currentArg) {
        return currentTable.containsKey(currentArg);
    }

    public String remove(String currentArg) {
        --number;
        String oldValue = null;
        if (!currentTable.containsKey(currentArg)) {
            throw new IllegalFormatCodePointException(2);
        } else {
            oldValue = get(currentArg);
            currentTable.remove(currentArg);
            write();
        }
        return oldValue;
    }


    public void create() {
        String s = getHomeDirectory();
        File f = new File(getHomeDirectory());
        if (f.exists()) {
            System.out.println("tablename exists");
        } else if (!f.mkdirs()) {
            System.err.println("I can't create the directory");
            System.exit(1);
        } else {
            System.out.println("created");
        }
    }

    public void delete() {
        String[] s = new String[]{"remove", "-r", getName()};
        CurrentDirectory cd = new CurrentDirectory();
        cd.changeCurrentDirectory(System.getProperty("fizteh.db.dir"));
        RemoveDirectory removeDirectory = new RemoveDirectory(s, cd);
        System.out.println("Deleted");
    }

    public int size() {
        return number;
    }

    public void clear() {
        currentTable.clear();
    }

    public void init() {
        File f = new File(getHomeDirectory());
        if (f.exists()) {
            File[] files = f.listFiles();
            if (files != null) {
                for (File tmp : files) {
                    Init z = new Init(currentTable, tmp.toString());
                }
            }

        }
        number = currentTable.size();
    }
}
