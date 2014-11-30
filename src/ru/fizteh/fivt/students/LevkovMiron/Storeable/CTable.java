package ru.fizteh.fivt.students.LevkovMiron.Storeable;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Мирон on 08.11.2014 ru.fizteh.fivt.students.LevkovMiron.Storeable.
 */
public class CTable implements Table{
    private HashMap<String, Storeable>[][] tableAfterChanges = new HashMap[20][20];
    private HashMap<String, Storeable>[][] tableBeforeChanges = new HashMap[20][20];

    private ArrayList<Class<?>> signature = new ArrayList<>();
    private Path dbPath;
    private String name;

    private static Pattern fileNamePattern = Pattern.compile("^([0-9]|1[0-5])\\.dat$");
    private static Pattern directoryNamePattern = Pattern.compile("^([0-9]|1[0-5])\\.dir$");

    public CTable(Path path) throws IOException {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                tableAfterChanges[i][j] = new HashMap<>();
                tableBeforeChanges[i][j] = new HashMap<>();
            }
        }
        dbPath = path;
        name = path.getFileName().toString();
        try {
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
        } catch (IOException e) {
            throw new IOException("Directory creation failed: " + e.getMessage());
        }
        loadData();
    }

    public CTable(Path path, List<Class<?>> columnTypes) throws IOException {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                tableAfterChanges[i][j] = new HashMap<>();
                tableBeforeChanges[i][j] = new HashMap<>();
            }
        }
        dbPath = path;
        name = path.getFileName().toString();
        try {
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
        } catch (IOException e) {
            throw new IOException("Directory creation failed: " + e.getMessage());
        }
        loadData();
        signature = new ArrayList<>(columnTypes);
        writeSignature();
    }

    @Override
    public int commit() throws IOException {
        int result = changesNumber();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                tableBeforeChanges[i][j].clear();
            }
        }
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (Map.Entry<String, Storeable> pair : tableAfterChanges[i][j].entrySet()) {
                    tableBeforeChanges[i][j].put(pair.getKey(), pair.getValue());
                }
            }
        }
        drop();
        try {
            rewrite();
        } catch (IOException e) {
            throw new IOException("Can't commit");
        }
        System.out.println(result + " changes made");
        return result;
    }

    public Storeable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        int h1 = key.hashCode() % 16;
        int h2 = (key.hashCode() / 16) % 16;
        Storeable value = tableAfterChanges[h1][h2].get(key);
        if (value != null) {
            System.out.println(new Parser().serialize(this, value));
        } else {
            System.out.println("Doesn't exist");
        }
        return value;
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        int h1 = key.hashCode() % 16;
        int h2 = (key.hashCode() / 16) % 16;
        value = tableAfterChanges[h1][h2].put(key, value);
        if (value == null) {
            System.out.println("new");
        } else {
            System.out.println("replaced value " + new Parser().serialize(this, value));
        }
        return value;
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        int h1 = key.hashCode() % 16;
        int h2 = (key.hashCode() / 16) % 16;
        Storeable value = tableAfterChanges[h1][h2].remove(key);
        if (value != null) {
            System.out.println(new Parser().serialize(this, value));
            System.out.println("removed");
        } else {
            System.out.println("doesn't exist");
        }
        return value;
    }

    public String getName() {
        System.out.println(name);
        return name;
    }

    @Override
    public int size() {
        int sz = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                sz += tableAfterChanges[i][j].size();
            }
        }
        System.out.println(sz);
        return sz;
    }

    @Override
    public int getColumnsCount() {
        return signature.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return signature.get(columnIndex);
    }

    @Override
    public int rollback() {
        int result = changesNumber();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                tableAfterChanges[i][j].clear();
            }
        }
        int size = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (Map.Entry<String, Storeable> pair : tableBeforeChanges[i][j].entrySet()) {
                    tableAfterChanges[i][j].put(pair.getKey(), pair.getValue());
                    size++;
                }
            }
        }
        System.out.println(result);
        return result;
    }

    int changesNumber() {
        int res = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (Map.Entry<String, Storeable> pair : tableAfterChanges[i][j].entrySet()) {
                    if (!pair.getValue().equals(tableBeforeChanges[i][j].get(pair.getKey()))) {
                        res++;
                    }
                }
                for (Map.Entry<String, Storeable> pair : tableBeforeChanges[i][j].entrySet()) {
                    if (!pair.getValue().equals(tableAfterChanges[i][j].get(pair.getKey()))) {
                        res++;
                    }
                }
            }
        }
        return res;
    }

    int getKeysCount() {
        int res = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                res += tableAfterChanges[i][j].size();
            }
        }
        return res;
    }

    public List<String> list() {
        Set<String> res = new HashSet<String>();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                res.addAll(tableAfterChanges[i][j].keySet());
            }
        }
        for (String s : res) {
            System.out.print(s + ", ");
        }
        System.out.println();
        return new ArrayList<String>(res);
    }

    public void drop() throws IOException {
        try {
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    if (Files.exists(dbPath.resolve(i + ".dir/" + j + ".dat"))) {
                        Files.delete(dbPath.resolve(i + ".dir/" + j + ".dat"));
                    }
                }
                if (Files.exists(dbPath.resolve(i + ".dir/"))) {
                    Files.delete(dbPath.resolve(i + ".dir/"));
                }
            }
            if (Files.exists(dbPath.resolve("signature.tsv"))) {
                Files.delete(dbPath.resolve("signature.tsv"));
            }
            if (Files.exists(dbPath)) {
                Files.delete(dbPath);
            }
        } catch (IOException e) {
            throw new IOException("Dropping failed: " + e.getMessage());
        }
    }

    private String readString(DataInputStream inStream, int size) throws IOException {
        ArrayList<Byte> tempData = new ArrayList<Byte>();
        for (int i = 0; i < size; i++) {
            byte[] oneByte = new byte[1];
            if (inStream.read(oneByte) == -1) {
                throw new IOException("Unexpected end of file.");
            }
            tempData.add(oneByte[0]);
        }
        try {
            byte[] utfData = new byte[size];
            for (int i = 0; i < tempData.size(); i++) {
                utfData[i] = tempData.get(i);
            }
            return new String(utfData, "UTF-8");
        } catch (OutOfMemoryError e) {
            throw new IOException("Data is too large.");
        }
    }

    private int readInt(DataInputStream inStream) throws IOException {
        byte[] utfData = new byte[4];
        if (inStream.read(utfData) < 4) {
            throw new IOException("Unexpected end of file.");
        }
        int value = ByteBuffer.wrap(utfData).getInt();
        if (value < 0) {
            throw new IOException();
        }
        return value;
    }

    private Storeable readStoreable(DataInputStream inStream, int size) throws IOException {
        String value = readString(inStream, size);
        try {
            return new Parser().deserialize(this, value);
        } catch (ParseException e) {
            throw new IOException("Invalid JSON format");
        }
    }

    private void writeIntAndString(FileOutputStream stream, String key) throws IOException {
        byte[] data = ByteBuffer.allocate(4).putInt(key.length()).array();
        stream.write(data);
        stream.write(key.getBytes("UTF-8"));
    }

    private void writeStoreable(FileOutputStream stream, Storeable value) throws IOException {
        String stringValue = new Parser().serialize(this, value);
        writeIntAndString(stream, stringValue);
    }

    private void readSignature() throws IOException {
        Map<String, Class> mp = new HashMap<>();
        mp.put("int", Integer.class);
        mp.put("long", Long.class);
        mp.put("float", Float.class);
        mp.put("double", Double.class);
        mp.put("byte", Byte.class);
        mp.put("boolean", Boolean.class);
        mp.put("String", String.class);
        signature.clear();
        try (Scanner scanner = new Scanner(new FileInputStream(dbPath.resolve("signature.tsv").toString()))) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                for (String linePart : line.split("\t")) {
                    signature.add(mp.get(linePart));
                }
            }
        }
    }

    private void writeSignature() throws IOException {
        Map<Class, String> mp = new HashMap<>();
        mp.put(Integer.class, "int");
        mp.put(Long.class, "long");
        mp.put(Float.class, "float");
        mp.put(Double.class, "double");
        mp.put(Byte.class, "byte");
        mp.put(Boolean.class, "boolean");
        mp.put(String.class, "String");
        try (PrintWriter writer = new PrintWriter(dbPath.resolve("signature.tsv").toString())) {
            for (Class<?> cl : signature) {
                writer.print(mp.get(cl));
                writer.print("\t");
            }
        }
    }

    private void loadData() throws IOException {
        try {
            if (!Files.exists(dbPath.resolve("signature.tsv"))) {
                Files.createFile(dbPath.resolve("signature.tsv"));
            }
            readSignature();
        } catch (IOException e) {
            throw new IOException("Failed while reading: " + e.getMessage());
        }
        try (DirectoryStream<Path> tableStream = Files.newDirectoryStream(dbPath)) {
            for (Path dir : tableStream) {
                if (dir.getFileName().toString().equals("signature.tsv")) {
                    continue;
                }
                Matcher matcher = directoryNamePattern.matcher(dir.getFileName().toString());
                if (!Files.isDirectory(dir) || !matcher.find()) {
                    throw new IOException("Other files in the table directory");
                }
                int d = Integer.decode(matcher.group(1));
                try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(dir)) {
                    for (Path file : dirStream) {
                        Matcher fileMatcher = fileNamePattern.matcher(file.getFileName().toString());
                        if (!fileMatcher.find()) {
                            throw new IOException("Other files in the table directory");
                        }
                        int f = Integer.decode(fileMatcher.group(1));
                        try (DataInputStream stream = new DataInputStream(Files.newInputStream(file))) {
                            while (stream.available() > 0) {
                                int sz = readInt(stream);
                                String key = readString(stream, sz);
                                sz = readInt(stream);
                                Storeable value = readStoreable(stream, sz);
                                int h1 = key.hashCode() % 16;
                                int h2 = (key.hashCode() / 16) % 16;
                                tableBeforeChanges[h1][h2].put(key, value);
                                tableAfterChanges[h1][h2].put(key, value);
                                if (d != h1 || f != h2) {
                                    throw new IOException("Wrong key type found in the file");
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new IOException("Failed while reading: " + e.getMessage());
        }
    }

    private void rewrite() throws IOException {
        drop();
        Files.createDirectory(dbPath);
        Files.createFile(dbPath.resolve("signature.tsv"));
        writeSignature();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (tableAfterChanges[i][j].size() > 0) {
                    if (!Files.exists(dbPath.resolve(i + ".dir"))) {
                        Files.createDirectory(dbPath.resolve(i + ".dir"));
                    }
                    Files.createFile(dbPath.resolve(i + ".dir/" + j + ".dat"));
                    File file = new File(dbPath.resolve(i + ".dir/" + j + ".dat").toString());
                    rewriteFile(file, i, j);
                }
            }
        }
    }
    private void rewriteFile(File file, int i, int j) throws IOException, OutOfMemoryError {
        try (FileOutputStream outStream = new FileOutputStream(file)) {
            for (Map.Entry<String, Storeable> pair : tableBeforeChanges[i][j].entrySet()) {
                writeIntAndString(outStream, pair.getKey());
                writeStoreable(outStream, pair.getValue());
            }
        }
    }
}
