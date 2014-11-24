package ru.fizteh.fivt.students.gudkov394.Junit.src;

import javafx.util.Pair;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.gudkov394.shell.CurrentDirectory;
import ru.fizteh.fivt.students.gudkov394.shell.RemoveDirectory;

import java.io.File;
import java.util.*;

public class CurrentTable implements Table {
    String name;
    Map currentTable = new HashMap<String, String>();
    Map newKey = new HashMap<String, String>();
    Map removedKey = new HashMap<String, String>();
    private int number = 0;
    int alreadyAdded = 0;

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
        return System.getProperty("db.file") + File.separator + getName();
    }

    private ArrayList<Pair<CurrentTable, File>> currentChanges = new ArrayList<Pair<CurrentTable, File>>();

    void write() {
        if (System.getProperty("db.file") == null) {
            System.err.println("You forgot directory");
            System.exit(4);
        }
        String newPath = System.getProperty("db.file") + File.separator + getName();
        File f = new File(newPath);
        currentChanges.add(new Pair<CurrentTable, File>(this, f));
    }

    @Override
    public int commit() {
        for (Pair<CurrentTable, File> tmp : currentChanges) {
            Write write = new Write(tmp.getKey(), tmp.getValue());
        }
        return shadeOperationsRemindChanges();
    }

    private int shadeOperationsRemindChanges() {
        currentChanges.clear();
        newKey.clear();
        int oldAlreadyAdded = alreadyAdded;
        alreadyAdded = 0;
        return oldAlreadyAdded;
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
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        ++number;
        String oldValue = null;
        if (removedKey.containsKey(key)) {
            removedKey.remove(key);
        }
        if (!newKey.containsKey(key)) {
            ++alreadyAdded;
            newKey.put(key, value);
            write();
        }
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
        if (currentArg == null) {
            throw new IllegalArgumentException();
        }
        --number;
        if (!newKey.containsKey(currentArg) && currentTable.containsKey(currentArg)) {
            removedKey.put(currentArg, currentTable.get(currentArg));
        }
        if (newKey.containsKey(currentArg)) {
            --alreadyAdded;
            newKey.remove(currentArg);
        }
        String oldValue = null;
        if (!currentTable.containsKey(currentArg)) {
            return null;
        } else {
            oldValue = get(currentArg);
            currentTable.remove(currentArg);
            write();
        }
        return oldValue;
    }


    @Override
    public int rollback() {
        for (Object s : newKey.keySet()) {
            currentTable.remove(s);
        }
        for (Object s : removedKey.keySet()) {
            currentTable.put(s, removedKey.get(s));
        }
        return shadeOperationsRemindChanges();
    }

    @Override
    public List<String> list() {
        return (List<String>) keySet();
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
        cd.changeCurrentDirectory(System.getProperty("db.file"));
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
            for (File tmp : files) {
                Init z = new Init(currentTable, tmp.toString());
            }
        }
        number = currentTable.size();
    }
}
