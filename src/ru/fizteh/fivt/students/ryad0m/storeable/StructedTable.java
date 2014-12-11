package ru.fizteh.fivt.students.ryad0m.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.io.*;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class StructedTable {

    private DirTable dirTable;
    private Path location;
    private List<Class<?>> columnTypes;

    public StructedTable(Path path) throws IOException {
        location = path;
        dirTable = new DirTable(path);
        File columnFile = location.resolve("signature.tsv").toFile();
        try {
            FileInputStream fis = new FileInputStream(columnFile);
            Scanner scanner = new Scanner(fis);
            columnTypes = Typer.typeListFromString(scanner.nextLine());
            scanner.close();
            fis.close();
        } catch (Exception ex) {
            throw new IOException("can't load signature.tsv");
        }
    }

    public StructedTable(Path path, List<Class<?>> columnTypes) throws IOException {
        location = path;
        deleteData();
        dirTable = new DirTable(path);
        this.columnTypes = columnTypes;
        save();
    }


    public String getName() {
        return location.getFileName().toString();
    }

    private void deleteDir(File dir) {
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteDir(f);
                } else {
                    f.delete();
                }
            }
        }
        dir.delete();
    }

    public void deleteData() {
        deleteDir(location.toFile());
    }

    public void save() throws IOException {
        deleteData();
        location.toFile().mkdirs();
        dirTable.save();
        File columnFile = location.resolve("signature.tsv").toFile();
        try {
            FileOutputStream fos = new FileOutputStream(columnFile);
            PrintStream printStream = new PrintStream(fos);
            printStream.print(Typer.stringFromTypeList(columnTypes));
            printStream.close();
            fos.close();
        } catch (Exception ex) {
            throw new IOException("can't save signature.tsv");
        }
    }

    public void put(String key, Storeable value) {
        if (value == null) {
            dirTable.put(key, null);
        } else {
            checkIntegrity(value);
            dirTable.put(key, XmlSerializer.serializeObjectList(((MyStorable) value).getValues()));
        }
    }

    public void checkIntegrity(Storeable value) {
        List<Class<?>> passed = ((MyStorable) value).getTypes();
        if (passed.size() != columnTypes.size()) {
            throw new ColumnFormatException("Incorrect number of values to serialize for table.");
        } else {
            for (int i = 0; i < passed.size(); ++i) {
                if (passed.get(i) != columnTypes.get(i)) {
                    throw new ColumnFormatException("Incorrect types.");
                }
            }
        }
    }

    public List<Class<?>> getColumnTypes() {
        return columnTypes;
    }

    public boolean containKey(String key) {
        return dirTable.containKey(key);
    }

    public Storeable get(String key) throws ParseException {
        if (dirTable.containKey(key)) {
            return new MyStorable(columnTypes, XmlSerializer.deserializeString(dirTable.get(key), columnTypes));
        } else {
            return null;
        }
    }

    public void remove(String key) {
        dirTable.remove(key);
    }

    public int getSize() {
        return dirTable.getSize();
    }

    public Set<String> getKeys() {
        return dirTable.getKeys();
    }
}
