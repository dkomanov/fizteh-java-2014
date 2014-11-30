package ru.fizteh.fivt.students.gudkov394.Storable.src;

import javafx.util.Pair;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.gudkov394.shell.CurrentDirectory;
import ru.fizteh.fivt.students.gudkov394.shell.RemoveDirectory;

import java.io.*;
import java.util.*;

public class CurrentTable implements Table {
    private ArrayList<Class<?>> signature = new ArrayList<>();
    private String name;
    private Map currentTable = new HashMap<String, Storeable>();
    private Map newKey = new HashMap<String, Storeable>();
    private Map removedKey = new HashMap<String, Storeable>();
    private int number = 0;
    int alreadyAdded = 0;
    TableProviderClass tableProviderClass = null;


    public CurrentTable(String nameTmp, ArrayList<Class<?>> signatureTmp) {
        name = nameTmp;
        signature = signatureTmp;
        //   create();
    }

    public CurrentTable(String nameTmp) {
        name = nameTmp;
    }

    public void setSignature(ArrayList<Class<?>> signatureTmp) {
        signature = signatureTmp;
    }

    public String getName() {
        return name;
    }

    public String getHomeDirectory() {
        return System.getProperty("fizteh.db.dir") + File.separator + getName();
    }

    private ArrayList<Pair<CurrentTable, File>> currentChanges = new ArrayList<Pair<CurrentTable, File>>();

    public void writeSignature(String s) {
        if (System.getProperty("fizteh.db.dir") == null) {
            System.err.println("You forgot directory");
            System.exit(4);
        }
        String newPathDirectory = System.getProperty("fizteh.db.dir") + getName();
        String newPath = newPathDirectory + File.separator + "signature.tsv";
        File dir = new File(newPathDirectory);
        File f = new File(newPath);
        FileOutputStream output = null;
        if (!dir.exists()) {
            dir.mkdir();
        }
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                System.err.println("Sorry, I can't create file for signature");
                System.exit(1);
            }
        }
        try {
            output = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            System.err.println("We have a problem");
            System.exit(1);
        }
        try {
            for (int i = 0; i < s.length(); ++i) {
                output.write(s.charAt(i));
            }
        } catch (IOException e) {
            System.err.println("We have a problem, I can't write to file");
            System.exit(1);
        }

    }

    void write() {
        if (System.getProperty("fizteh.db.dir") == null) {
            System.err.println("You forgot directory");
            System.exit(4);
        }
        String newPath = System.getProperty("fizteh.db.dir") + File.separator + getName();
        File f = new File(newPath);
        currentChanges.add(new Pair<CurrentTable, File>(this, f));
    }

    @Override
    public int commit() {
        for (Pair<CurrentTable, File> tmp : currentChanges) {
            Write write = new Write(tmp.getKey(), tmp.getValue(), this, tableProviderClass);
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

    @Override
    public Storeable get(String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }
        if (!currentTable.containsKey(s)) {
            return null;
        }
        return (Storeable) currentTable.get(s);
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        ++number;
        Storeable oldValue = null;
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


    public Storeable remove(String currentArg) {
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
        Storeable oldValue = null;
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
    public int getNumberOfUncommittedChanges() {
        return 0;
    }

    @Override
    public int getColumnsCount() {
        return signature.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 && columnIndex >= signature.size()) {
            throw new IndexOutOfBoundsException("inaccessible column index");
        }
        return signature.get(columnIndex);
    }

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

    public void init(TableProviderClass tableProviderClassTmp) {
        tableProviderClass = tableProviderClassTmp;
        File f = new File(getHomeDirectory());
        if (f.exists()) {
            File[] files = f.listFiles();
            if (files != null) {
                for (File tmp : files) {
                    Init z = new Init(currentTable, tmp.toString(), tableProviderClass, this);
                }
            }
        }
        number = currentTable.size();
    }
    public TableProviderClass getTableProviderClass() {
        return tableProviderClass;
    }
}
