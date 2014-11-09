package ru.fizteh.fivt.students.VasilevKirill.junit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.VasilevKirill.junit.multimap.MultiTable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Kirill on 09.11.2014.
 */

public class MyTable implements Table {
    private MultiTable table;
    private Map<String, String> data;
    private Map<String, String> oldData;
    private Map<String, String> prevCommitData;
    private int numCommits;

    MyTable(MultiTable table) {
        try {
            this.table = table;
            data = this.table.getData();
            oldData = this.table.getData();
            prevCommitData = this.table.getData();
            numCommits = 0;
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public String getName() {
        return table.getTableName();
    }

    @Override
    public String get(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        return data.get(key);
    }

    @Override
    public String put(String key, String value) throws IllegalArgumentException {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        String retValue = data.get(key);
        data.put(key, value);
        return retValue;
    }

    @Override
    public String remove(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        String retValue = data.get(key);
        if (retValue == null) {
            return null;
        }
        data.remove(key);
        return retValue;
    }

    @Override
    public int commit() {
        int number = 0;
        try {
            PrintStream oldOutput = System.out;
            PrintStream newOutput = new PrintStream(new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                }
            });
            System.setOut(newOutput);
            for (Map.Entry pair : data.entrySet()) {
                String value = oldData.get(pair.getKey());
                if (value == null) {
                    number++;
                    String[] args = {"put", (String) pair.getKey(), (String) pair.getValue() };
                    table.handle(args);
                }
            }
            for (Map.Entry pair : oldData.entrySet()) {
                String value = data.get(pair.getKey());
                if (value == null) {
                    number++;
                    String[] args = {"remove", (String) pair.getKey()};
                    table.handle(args);
                }
            }
            newOutput.close();
            System.setOut(oldOutput);
            prevCommitData = oldData;
            oldData = data;
            numCommits++;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e);
        }
        return number;
    }

    @Override
    public int size() {
        return oldData.size();
    }

    @Override
    public int rollback() {
        if (numCommits == 0) {
            return 0;
        }
        int numChanges = 0;
        try {
            PrintStream oldOutput = System.out;
            PrintStream newOutput = new PrintStream(new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                }
            });
            System.setOut(newOutput);
            for (Map.Entry pair : prevCommitData.entrySet()) {
                String value = data.get(pair.getKey());
                if (value == null) {
                    numChanges++;
                    String[] args = {"put", (String) pair.getKey(), (String) pair.getValue() };
                    table.handle(args);
                }
            }
            for (Map.Entry pair : data.entrySet()) {
                String value = prevCommitData.get(pair.getKey());
                if (value == null) {
                    numChanges++;
                    String[] args = {"remove", (String) pair.getKey()};
                    table.handle(args);
                }
            }
            newOutput.close();
            System.setOut(oldOutput);
            oldData = prevCommitData;
            data = prevCommitData;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e);
        }
        return numChanges;
    }

    @Override
    public List<String> list() {
        Map<String, String> keyMap = table.getData();
        List<String> retList = new ArrayList<>();
        for (Map.Entry pair : keyMap.entrySet()) {
            retList.add(pair.getKey().toString());
        }
        return retList;
    }

}
