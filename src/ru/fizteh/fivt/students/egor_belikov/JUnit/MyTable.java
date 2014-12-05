/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.egor_belikov.JUnit;

/**
 *
 * @author egor
 */

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.fizteh.fivt.storage.strings.Table;
import static ru.fizteh.fivt.students.egor_belikov.JUnit.JUnit.countChanges;
import static ru.fizteh.fivt.students.egor_belikov.JUnit.JUnit.currentFileMap;
import static ru.fizteh.fivt.students.egor_belikov.JUnit.JUnit.putCurrentMapToDirectory;
import static ru.fizteh.fivt.students.egor_belikov.JUnit.JUnit.savedFileMap;

public class MyTable implements Table{
    public static String nameTable;
    
    public MyTable(String name) {
        nameTable = name;
    }

    @Override
    public String getName() {
        return nameTable;
    }

    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("get: null key");
        }
        if (!JUnit.currentFileMap.containsKey(key)) {
            return null;
        }
        return JUnit.currentFileMap.get(key);
    }

    @Override
    public String put(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("put: null");
        }
        if (JUnit.currentFileMap.containsKey(key)) {
            String temp = this.get(key);
            JUnit.currentFileMap.put(key, value);
            return temp;
        } else {
            JUnit.currentFileMap.put(key, value);
            return null;
        }
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("remove: null key");
        }
        if (!JUnit.currentFileMap.containsKey(key)) {
            return null;
        } 
        String temp = this.get(key);
        JUnit.currentFileMap.remove(key);
        return temp;
    }

    @Override
    public int size() {
        return JUnit.currentFileMap.size();
    }

    @Override
    public int commit() {
        try {
            putCurrentMapToDirectory();
        } catch (Exception ex) {
            Logger.getLogger(MyTable.class.getName()).log(Level.SEVERE, null, ex);
        }
        int result = JUnit.countChanges();
        JUnit.savedFileMap = new TreeMap(currentFileMap);
        return result;
    }

    @Override
    public int rollback() {
        int result = countChanges();
        currentFileMap = new TreeMap(savedFileMap);
        return result;
    }

    @Override
    public List<String> list() {
        List<String> result;
        result = new ArrayList<String>();
        JUnit.currentFileMap.keySet().stream().forEach((i) -> {
            result.add(i);
        });
        return result;
    }
    
}
