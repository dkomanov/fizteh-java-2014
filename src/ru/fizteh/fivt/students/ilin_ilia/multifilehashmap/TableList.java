package ru.fizteh.fivt.students.theronsg.multifilehashmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

public class TableList {
    
    private Map<String, Table> dB;
    private String dBPath;
    private String curTable = null;
    
    TableList(final String path) throws IOException {
        dBPath = path;
        dB = new TreeMap<>();
        File userDir = new File(dBPath);
        if (userDir.exists()) {
            for (String file: userDir.list()) {
                dB.put(file, new Table(Paths.get(path).resolve(file).toString()));
            }
        } else {
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
    
    /**
    * Function drop has two different realization.
    * I don't want to remove one of them.
    * So one will be commented.
    **/
    
    public void drop(final String name) {
        if (dB.containsKey(name)) {
            dB.get(name).drop();
        	//rm(name);  	
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

    public void exit() throws FileNotFoundException {
        saveDB();
        System.exit(0);
    }

    private void saveDB() throws FileNotFoundException {
        for (Table t: dB.values()) {
            t.saveTable();
        }
    }
    
    public static void rm(final String name) {
        File fileName = new File(name);
        if (fileName.isFile()) {
            fileName.delete();
            return;
        }
        if (fileName.isDirectory()) {
            if (fileName.list().length == 0) {
            	fileName.delete();
                return;
            } else {
                for (String s: fileName.list()) {
                    rm(Paths.get(fileName.getAbsolutePath()).resolve(s).toString());
                }
                fileName.delete();
                return;
            }
        }
    }
}
