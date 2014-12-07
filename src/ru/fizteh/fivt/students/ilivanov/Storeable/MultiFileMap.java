package ru.fizteh.fivt.students.ilivanov.Storeable;

import ru.fizteh.fivt.students.ilivanov.Storeable.Interfaces.ColumnFormatException;
import ru.fizteh.fivt.students.ilivanov.Storeable.Interfaces.Index;
import ru.fizteh.fivt.students.ilivanov.Storeable.Interfaces.Storeable;
import ru.fizteh.fivt.students.ilivanov.Storeable.Interfaces.Table;

import java.io.*;
import java.text.ParseException;
import java.util.*;

public class MultiFileMap implements Table, Index {
    private File location;
    private FileUsing[][] map;
    private HashMap<String, Storeable> oldValues;
    private HashSet<String> newKeys;
    private ArrayList<Class<?>> columnTypes;
    private FileMapProvider tableProvider;
    private final int arraySize = 16;

    public MultiFileMap(File location, FileMapProvider tableProvider) {
        if (location == null) {
            throw new IllegalArgumentException("null location");
        }
        if (tableProvider == null) {
            throw new IllegalArgumentException("null tableProvider");
        }
        this.tableProvider = tableProvider;
        this.location = location;
        columnTypes = new ArrayList<>();
        map = new FileUsing[arraySize][arraySize];
        for (int i = 0; i < arraySize; i++) {
            for (int j = 0; j < arraySize; j++) {
                String relative = String.format("%d.dir/%d.dat", i, j);
                File path = new File(location, relative);
                map[i][j] = new FileUsing(path);
            }
        }
        oldValues = new HashMap<>();
        newKeys = new HashSet<>();
    }

    public MultiFileMap(File location, FileMapProvider tableProvider, List<Class<?>> columnTypes) {
        this(location, tableProvider);
        this.columnTypes = new ArrayList<>(columnTypes);
    }

    public boolean checkColumnTypes(Storeable list) {
        try {
            for (int i = 0; i < columnTypes.size(); i++) {
                if (list.getColumnAt(i) != null && columnTypes.get(i) != list.getColumnAt(i).getClass()) {
                    return false;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        try {
            list.getColumnAt(columnTypes.size());
            return false;
        } catch (IndexOutOfBoundsException e) {
            return true;
        }
    }

    private boolean newLineCheck(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '\n') {
                return false;
            }
        }
        return true;
    }

    private boolean whiteSpaceCheck(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (Character.isWhitespace(string.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public String getName() {
        return location.getName();
    }

    private void clear() {
        for (int i = 0; i < arraySize; i++) {
            for (int j = 0; j < arraySize; j++) {
                map[i][j].clear();
            }
        }
        oldValues.clear();
        newKeys.clear();
    }

    public int size() {
        int size = 0;

        for (int i = 0; i < arraySize; i++) {
            for (int j = 0; j < arraySize; j++) {
                size += map[i][j].size();
            }
        }

        return size;
    }

    private boolean validateData() {
        for (int i = 0; i < arraySize; i++) {
            for (int j = 0; j < arraySize; j++) {
                for (String key : map[i][j].getKeysList()) {
                    int hashCode = Math.abs(key.hashCode());
                    int dir = (hashCode % 16 + 16) % 16;
                    int file = ((hashCode / 16 % 16) + 16) % 16;
                    if (dir != i || file != j) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void validateDirectory() {
        File[] files = location.listFiles();
        if (files == null) {
            throw new RuntimeException("Path specifies invalid location");
        }
        for (File f : files) {
            if (f.getName().equals("signature.tsv")) {
                continue;
            }
            if (!f.getName().matches("((1[0-5])|[0-9])\\.dir")) {
                throw new RuntimeException("Directory is invalid: unexpected files or directories found");
            } else {
                File[] subfiles = f.listFiles();
                if (subfiles == null) {
                    throw new RuntimeException("Path specifies invalid location");
                }
                if (subfiles.length == 0) {
                    throw new RuntimeException("Directory shouldn't be empty");
                }
                for (File sf : subfiles) {
                    if (!sf.getName().matches("((1[0-5])|[0-9])\\.dat")) {
                        throw new RuntimeException("Directory is invalid: unexpected files or directories found");
                    } else {
                        if (sf.length() == 0) {
                            throw new RuntimeException("File shouldn't be empty");
                        }
                    }
                }
            }
        }
    }

    /**
     * Method is not synchronized, use methods of TableFactory instead
     */
    public void loadFromDisk() throws IOException, ParseException {
        columnTypes.clear();
        clear();
        if (!location.getParentFile().exists() || !location.getParentFile().isDirectory()) {
            throw new RuntimeException("Unable to create a table in specified directory: directory doesn't exist");
        }
        if (!location.exists()) {
            return;
        }
        if (location.exists() && !location.isDirectory()) {
            throw new RuntimeException("Specified location is not a directory");
        }
        validateDirectory();
        File signature = new File(location, "signature.tsv");
        try (BufferedReader reader = new BufferedReader(new FileReader(signature))) {
            String[] typeNames = reader.readLine().split("\\s+");
            for (String typeName : typeNames) {
                if (typeName.equals("int")) {
                    columnTypes.add(Integer.class);
                } else if (typeName.equals("long")) {
                    columnTypes.add(Long.class);
                } else if (typeName.equals("byte")) {
                    columnTypes.add(Byte.class);
                } else if (typeName.equals("float")) {
                    columnTypes.add(Float.class);
                } else if (typeName.equals("double")) {
                    columnTypes.add(Double.class);
                } else if (typeName.equals("boolean")) {
                    columnTypes.add(Boolean.class);
                } else if (typeName.equals("String")) {
                    columnTypes.add(String.class);
                } else {
                    throw new RuntimeException(String.format("Unknown type %s", typeName));
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No signature file found");
        } catch (IOException e) {
            throw new IOException("Error reading a signature file", e);
        }
        for (int dir = 0; dir < arraySize; dir++) {
            String relative = String.format("%d.dir", dir);
            File directory = new File(location, relative);
            if (directory.exists() && !directory.isDirectory()) {
                throw new RuntimeException(String.format("%s is not a directory", relative));
            }
            if (directory.exists()) {
                for (int file = 0; file < arraySize; file++) {
                    File db = map[dir][file].getFile();
                    if (db.exists()) {
                        try {
                            map[dir][file].loadFromDisk(this, tableProvider);
                        } catch (RuntimeException e) {
                            throw new RuntimeException(String.format("Error in file %d.dir/%d.dat", dir, file), e);
                        }
                    }
                }
            }
        }
        if (!validateData()) {
            throw new RuntimeException("Wrong data format: key distribution among files is incorrect");
        }
        oldValues.clear();
        newKeys.clear();
    }

    public void writeToDisk() throws IOException {
        if (location.exists() && !location.isDirectory()) {
            throw new RuntimeException("Database can't be written to the specified location");
        }
        if (!location.exists()) {
            if (!location.mkdir()) {
                throw new IOException("Unable to create a directory for database");
            }
        }
        File signature = new File(location, "signature.tsv");
        if (!signature.exists()) {
            if (!signature.createNewFile()) {
                throw new IOException("Unable to create a file");
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(signature))) {
            for (int i = 0; i < columnTypes.size(); i++) {
                if (columnTypes.get(i) == Integer.class) {
                    writer.write("int");
                } else if (columnTypes.get(i) == Long.class) {
                    writer.write("long");
                } else if (columnTypes.get(i) == Byte.class) {
                    writer.write("byte");
                } else if (columnTypes.get(i) == Float.class) {
                    writer.write("float");
                } else if (columnTypes.get(i) == Double.class) {
                    writer.write("double");
                } else if (columnTypes.get(i) == Boolean.class) {
                    writer.write("boolean");
                } else if (columnTypes.get(i) == String.class) {
                    writer.write("String");
                }
                if (i != columnTypes.size() - 1) {
                    writer.write(" ");
                }
            }
        } catch (IOException e) {
            throw new IOException("Error writing a signature file", e);
        }
        for (int dir = 0; dir < arraySize; dir++) {
            boolean dirRequired = false;
            for (int file = 0; file < arraySize; file++) {
                if (!map[dir][file].empty()) {
                    dirRequired = true;
                    break;
                }
            }
            String relative = String.format("%d.dir", dir);
            File directory = new File(location, relative);
            if (directory.exists() && !directory.isDirectory()) {
                throw new RuntimeException(String.format("%s is not a directory", relative));
            }
            if (!directory.exists() && dirRequired) {
                if (!directory.mkdir()) {
                    throw new RuntimeException(String.format("Can't create directory %s", relative));
                }
            }
            if (directory.exists()) {
                for (int file = 0; file < arraySize; file++) {
                    File db = map[dir][file].getFile();
                    if (map[dir][file].empty()) {
                        if (db.exists()) {
                            if (!db.delete()) {
                                throw new RuntimeException(String.format("Can't delete file %s",
                                        db.getCanonicalPath()));
                            }
                        }
                    } else {
                        try {
                            map[dir][file].writeToDisk(this, tableProvider);
                        } catch (RuntimeException e) {
                            throw new RuntimeException(String.format("Error in file %d.dir/%d.dat", dir, file), e);
                        }
                    }
                }
                File[] files = directory.listFiles();
                if (files != null && directory.listFiles().length == 0) {
                    if (!directory.delete()) {
                        throw new RuntimeException(String.format("Can't delete directory %s",
                                directory.getCanonicalPath()));
                    }
                }
            }
        }
        oldValues.clear();
        newKeys.clear();
    }

    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key == null) {
            throw new IllegalArgumentException("Null pointer instead of string");
        }
        if (value == null) {
            throw new IllegalArgumentException("Null value");
        }
        if (key.equals("")) {
            throw new IllegalArgumentException("Empty key");
        }
        if (!newLineCheck(key)) {
            throw new IllegalArgumentException("New-line in key or value");
        }
        if (!checkColumnTypes(value)) {
            throw new ColumnFormatException("Type mismatch");
        }
        if (!whiteSpaceCheck(key)) {
            throw new IllegalArgumentException("Whitespace not allowed in key");
        }
        int hashCode = Math.abs(key.hashCode());
        int dir = (hashCode % 16 + 16) % 16;
        int file = ((hashCode / 16 % 16) + 16) % 16;

        Storeable result = map[dir][file].put(key, value);
        if (result != null) {
            if (!newKeys.contains(key)) {
                Storeable diffValue = oldValues.get(key);
                if (diffValue == null) {
                    if (!result.equals(value)) {
                        oldValues.put(key, result);
                    }
                } else {
                    if (diffValue.equals(value)) {
                        oldValues.remove(key);
                    }
                }
            }
        } else {
            Storeable diffValue = oldValues.get(key);
            if (diffValue == null) {
                newKeys.add(key);
            } else {
                if (diffValue.equals(value)) {
                    oldValues.remove(key);
                }
            }
        }
        return result;
    }

    public Storeable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Null pointer instead of string");
        }
        if (key.equals("")) {
            throw new IllegalArgumentException("Empty key");
        }
        int hashCode = Math.abs(key.hashCode());
        int dir = (hashCode % 16 + 16) % 16;
        int file = ((hashCode / 16 % 16) + 16) % 16;

        return map[dir][file].get(key);
    }

    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Null pointer instead of string");
        }
        if (key.equals("")) {
            throw new IllegalArgumentException("Empty key");
        }
        int hashCode = Math.abs(key.hashCode());
        int dir = (hashCode % 16 + 16) % 16;
        int file = ((hashCode / 16 % 16) + 16) % 16;

        Storeable result = map[dir][file].remove(key);
        if (result != null) {
            if (newKeys.contains(key)) {
                newKeys.remove(key);
            } else if (oldValues.get(key) == null) {
                oldValues.put(key, result);
            }
        }
        return result;
    }

    public int uncommittedChanges() {
        return newKeys.size() + oldValues.size();
    }

    public int commit() throws IOException {
        int changes =  uncommittedChanges();
        writeToDisk();
        return changes;
    }

    public int rollback() {
        int changes = uncommittedChanges();
        try {
            loadFromDisk();
        } catch (IOException | ParseException e) {
            System.err.println(e.getMessage());
        }
        return changes;
    }

    public int getColumnsCount() {
        return columnTypes.size();
    }

    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex >= getColumnsCount() || columnIndex < 0) {
            throw new IndexOutOfBoundsException(String.format("Index out of bounds: array size %d, found %d",
                    columnTypes.size(), columnIndex));
        }
        return columnTypes.get(columnIndex);
    }

    @Override
    public String toString() {
        try {
            return String.format("%s[%s]", this.getClass().getSimpleName(), location.getCanonicalPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public List<String> list() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < arraySize; i++) {
            for (int j = 0; j < arraySize; j++) {
                list.addAll(Arrays.asList(map[i][j].getKeysList()));
            }
        }
        return list;
    }
}
