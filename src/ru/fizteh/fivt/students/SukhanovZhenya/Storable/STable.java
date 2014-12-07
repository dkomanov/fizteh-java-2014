package ru.fizteh.fivt.students.SukhanovZhenya.Storable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class STable implements Table {
    private Map<String, String> fMap;
    private File tableDir;
    private List<Class<?>> typesList;
    private File typesFile;
    private int changes;

    STable(String path, List<Class<?>> columnTypes) {
        fMap = new HashMap<>();
        try {
            tableDir = new File(path);
            typesFile = new File(path + "/" + "signature.tsv");
            if (!typesFile.createNewFile()) {
                System.err.println("Can't create signature.tsv");
                System.exit(1);
            }

            typesList = new Vector<>(columnTypes.size());
            FileWriter writer = new FileWriter(typesFile);
            for (Class<?> type : columnTypes) {
                writer.write(type.getSimpleName() + " ");
                typesList.add(type);
            }


        } catch (SecurityException e) {
            System.err.println(e.getMessage());
            incorrectFile();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        changes = 0;
    }

    STable(String path) {
        fMap = new HashMap<>();
        try {
            tableDir = new File(path);
            if (!tableDir.exists()) {
                System.err.println("Incorrect Table");
                System.exit(1);
            }
            typesFile = new File(path + "/" + "signature.tsv");
            if (!typesFile.createNewFile()) {
                System.err.println("Can't create signature.tsv");
                System.exit(1);
            }

            getFile();

        } catch (SecurityException e) {
            System.err.println(e.getMessage());
            incorrectFile();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        changes = 0;
    }

    @Override
    public String getName() {
        return tableDir.getName();
    }


    @Override
    public Storeable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Null argument!");
        } else {
            return new SStoreable(XMLCoder.deserializeString(key, typesList), typesList);
        }
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Null argument!");
        } else {
            ++changes;
            return new SStoreable(XMLCoder.deserializeString(fMap.put(key,
                            XMLCoder.serializeObjects(((SStoreable) value).getObjects())),
                    ((SStoreable) value).getTypes()), typesList);
        }
    }


    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Null argument!");
        }
        ++changes;
        return new SStoreable(XMLCoder.deserializeString(fMap.remove(key),
                typesList), typesList);
    }

    @Override
    public List<String> list() {
        List<String> keys = fMap.keySet().stream().collect(Collectors.toList());
        return keys;
    }

    void getFile() {
        if (tableDir == null || tableDir.list() == null) {
            return;
        }
        fMap.clear();
        typesFile = new File(tableDir.getAbsolutePath() + "/" + "signature.tsv");
        if (!typesFile.exists() || !typesFile.isFile()) {
            System.err.println("Wrong type");
            System.exit(1);
        }

        try {
            Scanner reader = new Scanner(new FileReader(typesFile));
            String[] getTypes = reader.nextLine().split(" +");
            typesList = new Vector<>(getTypes.length);
            for (String name : getTypes) {
                typesList.add(Class.forName(name));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("Wrong type");
            System.exit(1);
        }


        for (String dirName : tableDir.list()) {
            File subDir = new File(tableDir.getAbsolutePath() + "/" + dirName);
            if (subDir.list() != null && subDir.isDirectory()) {
                for (String fileName : subDir.list()) {
                    readFile(subDir.getAbsolutePath() + "/" + fileName, Integer.parseInt(dirName.split(".dir")[0]),
                            Integer.parseInt(fileName.split(".dat")[0]));
                }
            }
        }
        changes = 0;
    }

    private void readFile(String path, int firstHash, int secondHash) {
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(path, "rw");
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            incorrectFile();
            System.exit(1);
        }
        try {
            int sizeLength;
            long readied = 1;
            try {
                while (true) {
                    sizeLength = randomAccessFile.readInt();
                    readied += 4;

                    byte[] key = new byte[sizeLength];

                    try {
                        randomAccessFile.readFully(key);
                        readied += sizeLength;
                        sizeLength = randomAccessFile.readInt();
                        readied += 4;
                        byte[] value = new byte[sizeLength];
                        randomAccessFile.readFully(value);
                        readied += sizeLength;
                        if (Math.abs((new String(key, "UTF-8")).hashCode()) % 16 != firstHash
                                || (Math.abs((new String(key, "UTF-8")).hashCode()) / 16) % 16 != secondHash) {
                            System.err.println("Incorrect files");
                            System.err.println(new String(key, "UTF-8"));
                            System.err.println(firstHash + " " + secondHash);
                            System.exit(1);
                        }
                        fMap.put(new String(key, "UTF-8"), new String(value, "UTF-8"));
                    } catch (EOFException e) {
                        System.err.println(e.getMessage());
                        randomAccessFile.close();
                        incorrectFile();
                    }
                }
            } catch (EOFException e) {
                if (readied < randomAccessFile.length()) {
                    System.err.println(randomAccessFile.length());
                    System.err.println(readied);
                    System.err.println(e.getMessage());
                    incorrectFile();
                }
                randomAccessFile.close();
            }
        } catch (IOException | OutOfMemoryError ioe) {
            System.err.println(ioe.getMessage());
            incorrectFile();
        }
    }

    @Override
    public int commit() {
        if (tableDir == null) {
            return 0;
        }
        try {
            for (int i = 0; i < 16; ++i) {
                for  (int j = 0; j < 16; ++j) {
                    File dir = new File(tableDir.getAbsolutePath() + "/" + i + ".dir");
                    if (!dir.exists()) {
                        if (!dir.mkdir()) {
                            System.err.println("Can not create a directory");
                            incorrectFile();
                        }
                    }
                    RandomAccessFile tmp =
                            new RandomAccessFile(tableDir.getAbsoluteFile() + "/" + i + ".dir/" + j + ".dat", "rw");
                    tmp.setLength(0);
                    tmp.close();
                }
            }

            for (Map.Entry<String, String> pair : fMap.entrySet()) {
                if (pair.getValue() != null) {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(tableDir.getAbsoluteFile() + "/"
                            + (Math.abs(pair.getKey().hashCode()) % 16) + ".dir/"
                            + ((Math.abs(pair.getKey().hashCode()) / 16) % 16) + ".dat", "rw");

                    randomAccessFile.setLength(0);
                    randomAccessFile.writeInt(pair.getKey().getBytes().length);
                    randomAccessFile.write(pair.getKey().getBytes("UTF-8"));
                    randomAccessFile.writeInt(pair.getValue().getBytes("UTF-8").length);
                    randomAccessFile.write(pair.getValue().getBytes("UTF-8"));
                    randomAccessFile.close();
                }
            }

            deleteEmptyFiles();
        } catch (IOException | OutOfMemoryError ioe) {
            System.err.println(ioe.getMessage());
            incorrectFile();
        }
        changes = 0;
        return size();
    }

    private void deleteEmptyFiles() {
        if (tableDir.list() == null) {
            return;
        }

        for (String dirName : tableDir.list()) {
            File dir = new File(tableDir.getAbsolutePath() + "/" + dirName);
            if (dir.list() == null) {
                return;
            }
            boolean isEmpty = true;
            for (String fileName : dir.list()) {
                File hashFile = new File(dir.getAbsolutePath() + "/" + fileName);
                if (hashFile.length() == 0) {
                    if (!hashFile.delete()) {
                        System.err.println("Can not remove file!");
                        System.err.println(hashFile.getAbsolutePath());
                        System.exit(1);
                    }
                } else {
                    isEmpty = false;
                }
            }
            if (isEmpty) {
                if (!dir.delete()) {
                    System.err.println("Can not remove directory!");
                    System.err.println(dir.getAbsolutePath());
                    System.exit(1);
                }
            }
        }
    }

    private void incorrectFile() {
        System.err.println("Incorrect files");
        System.exit(1);
    }

    @Override
    public int size() {
        return fMap.size();
    }

    @Override
    public int rollback() {
        int result = changes;
        getFile();
        return result;
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return changes;
    }

    @Override
    public int getColumnsCount() {
        return typesList.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex >= typesList.size()) {
            throw new IndexOutOfBoundsException("Incorrect index");
        }
        return typesList.get(columnIndex);
    }

    public List<Class<?>> getTypesList() {
        return typesList;
    }

    public void remove() {
        tableDelete(tableDir.getAbsolutePath());
    }

    private void tableDelete(String path) {
        File tmp = new File(path);
        if (tmp.isDirectory() && tmp.list() != null) {
            for (String name : tmp.list()) {
                tableDelete(tmp.getAbsolutePath() + "/" + name);
            }
        }
        if (!tmp.delete()) {
            System.err.println("Can not delete file");
            System.err.println(tmp.getAbsoluteFile());
            System.exit(1);
        }
    }
}
