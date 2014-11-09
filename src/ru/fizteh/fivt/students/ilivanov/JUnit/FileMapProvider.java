package ru.fizteh.fivt.students.ilivanov.JUnit;

import ru.fizteh.fivt.students.ilivanov.JUnit.TableInterfaces.TableProvider;

import java.io.File;
import java.util.HashMap;

public class FileMapProvider implements TableProvider {
    private File location;
    private HashMap<String, MultiFileMap> used;

    public FileMapProvider(final File location) {
        if (location == null) {
            throw new IllegalArgumentException("null location");
        }
        this.location = location;
        used = new HashMap<>();
    }

    private boolean badSymbolCheck(final String string) {
        String badSymbols = "\\/*:<>\"|?";
        char[] array = badSymbols.toCharArray();
        for (int i = 0; i < string.length(); i++) {
            for (char ch : array) {
                if (string.charAt(i) == ch) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isValidLocation() {
        return location.isDirectory();
    }

    public boolean isValidContent() {
        if (!isValidLocation()) {
            return false;
        }
        File[] list = location.listFiles();
        if (list != null) {
            for (File file : list) {
                if (!file.isDirectory()) {
                    return false;
                }
            }
        }
        return true;
    }

    public MultiFileMap getTable(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("null name");
        }
        if (name.equals("")) {
            throw new IllegalArgumentException("empty name");
        }
        if (!badSymbolCheck(name)) {
            throw new IllegalArgumentException("illegal characters");
        }
        if (!isValidLocation()) {
            throw new RuntimeException("database location is invalid");
        }
        File dir = new File(location, name);
        if (!dir.exists()) {
            return null;
        }
        if (dir.exists() && !dir.isDirectory()) {
            throw new RuntimeException(String.format("%s is not a directory", name));
        }
        MultiFileMap newMap = used.get(name);
        if (newMap != null) {
            return newMap;
        } else {
            newMap = new MultiFileMap(dir);
            newMap.loadFromDisk();
            return newMap;
        }
    }

    public MultiFileMap createTable(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("null name");
        }
        if (name.equals("")) {
            throw new IllegalArgumentException("empty name");
        }
        if (!badSymbolCheck(name)) {
            throw new IllegalArgumentException("illegal characters");
        }
        if (!isValidLocation()) {
            throw new RuntimeException("database location is invalid");
        }
        File dir = new File(location, name);
        if (dir.exists() && !dir.isDirectory()) {
            throw new RuntimeException(String.format("%s is not a directory", name));
        }
        if (dir.exists()) {
            return null;
        }
        if (!dir.mkdir()) {
            throw new RuntimeException("can't create table's directory");
        }
        MultiFileMap result = new MultiFileMap(dir);
        used.put(name, result);
        return result;
    }

    public void removeTable(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("null name");
        }
        if (name.equals("")) {
            throw new IllegalArgumentException("empty name");
        }
        if (!badSymbolCheck(name)) {
            throw new IllegalArgumentException("illegal characters");
        }
        if (!isValidLocation()) {
            throw new RuntimeException("database location is invalid");
        }
        File dir = new File(location, name);
        if (!dir.exists()) {
            throw new IllegalStateException("table doesn't exist");
        }
        if (dir.exists() && !dir.isDirectory()) {
            throw new RuntimeException(String.format("%s is not a directory", name));
        }
        if (!deleteFileRecursively(dir)) {
            throw new RuntimeException("can't delete some files");
        }
        used.remove(name);
    }

    private boolean deleteFileRecursively(final File file) {
        boolean result = true;
        if (file.isDirectory()) {
            File[] list = file.listFiles();
            if (list != null) {
                for (File f : list) {
                    result = result && deleteFileRecursively(f);
                }
            }
        }
        return file.delete() && result;
    }

    public void showTables() {
        if (!isValidLocation() || !isValidContent()) {
            System.err.println("show tables: location error");
            return;
        }
        File[] list = location.listFiles();
        if (list != null) {
            for (File file : list) {
                System.out.println(file.getName());
            }
        }
    }
//    public int size() {
//        int result = 0;
//        for (Map.Entry<String, MultiFileMap> entry : used.entrySet()) {
//            result += entry.getValue().size();
//        }
//        return result;
//    }
}

