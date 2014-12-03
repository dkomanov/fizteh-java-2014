package ru.fizteh.fivt.students.theronsg.multifilehashmap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TableList {
    
    private Map<String, Table> dB;
    private String dBPath;
    private String curTable = null;
    
    TableList(final String path) throws IOException {
        dBPath = path;
        dB = new HashMap<>();
        File userDir = new File(dBPath);
        if (userDir.exists()) {
            for (String file: userDir.list()) {
                dB.put(file, new Table(Paths.get(path).resolve(file).toString()));
            }
        } else {
            try {
                userDir.mkdir();
            } catch (SecurityException e) {
                System.err.println("Can't create the following directory: \"" + userDir.getName() + "\"");
            }
            userDir.mkdir();
        }
    }
    
    public void create(final String name) throws IOException {
        if (dB.containsKey(name)) {
            System.out.println(name + " exists");
        } else {
            dB.put(name, new Table(name));
            System.out.println("created");
        }
    }
    
    public void drop(final String name) {
        if (dB.containsKey(name)) {
            dB.get(name).drop();
            if (curTable != null) {
                if (curTable.equals(name)) {
                    curTable = null;
                }
            }
            dB.remove(name);
            System.out.println("dropped");
        } else {
            System.out.println(name +  " not exists");
        }
    }
    
    public void use(final String name) {
        if (dB.containsKey(name)) {
            curTable = name;
            System.out.println("using " + name);
        } else {
            System.out.println(name + " not exists");
        }
    }
    
    public void put(final String key, final String value) throws IOException {
        if (curTable != null) {
            dB.get(curTable).put(key, value);
        } else {
            System.err.println("no table");
        }
    }
    
    public void get(final String key) {
        if (curTable != null) {
            dB.get(curTable).get(key);
        } else {
            System.err.println("no table");
        }
    }
    
    public void showTables() {
        int[] summ = new int[dB.size()];
        int i = 0;
        for (String s: dB.keySet()) {
            summ[i] = dB.get(s).size();
            System.out.println(s + " " + summ [i]);
            i++;
        }
    }
    
    public void remove(final String key) {
        if (curTable != null) {
            dB.get(curTable).remove(key);
        } else {
            System.err.println("no table");
        }
    }

    public void list() {
        if (curTable != null) {
            dB.get(curTable).list();
        } else {
            System.err.println("no table");
        }
    }

    public void exit() throws IOException {
        saveDB();
        System.exit(0);
    }

    private void saveDB() throws IOException {
        for (Table t: dB.values()) {
            t.saveTable();
        }
    }

    public boolean checkNameCorrection(final String name) {
        Pattern pattern = Pattern.compile("[~#@*+%{}<>\\[\\]|\"\\_^]|?/:*");
        Matcher matcher = pattern.matcher(name);
        return !matcher.find();
    }
}
