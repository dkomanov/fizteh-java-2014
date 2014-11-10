package ru.fizteh.fivt.students.lukina.JUnit;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class DBaseLoader {
    private File rootDir;
    private TableProvider provider;
    private HashMap<String, Storeable> currentHashMap;
    private DBase currentTable;

    public DBaseLoader(String dir, TableProvider prov) {
        rootDir = new File(dir);
        provider = prov;
        currentHashMap = new HashMap<>();
    }

    public HashMap<String, DBase> loadBase() {
        HashMap<String, DBase> base = new HashMap<>();
        for (File currFile : rootDir.listFiles()) {
            if (!currFile.isDirectory()) {
                throw new RuntimeException("Incorrect DataBase: illegal file " + currFile.getAbsolutePath());
            }
            base.put(currFile.getName(), loadTable(currFile));
        }
        return base;
    }

    public DBase loadTable(File tableDir) {
        if (!tableDir.exists()) {
            return null;
        }
        currentHashMap.clear();
        currentTable = new DBase(tableDir.getName(), rootDir.getAbsolutePath(), provider);
        File signature = new File(tableDir + File.separator + "signature.tsv");
        ArrayList<Class<?>> types = scanSignature(signature);
        currentTable.setTypes(types);
        for (File currFile : tableDir.listFiles()) {
            if (currFile.isFile() && !currFile.getName().equals(signature.getName())) {
                throw new RuntimeException("Incorrect DataBase: illegal file " + currFile.getAbsolutePath());
            } else if (currFile.isDirectory()) {
                File[] list = currFile.listFiles();
                if (list.length == 0) {
                    throw new RuntimeException("Incorrect DataBase: empty dir " + currFile.getAbsolutePath());
                }
                for (File file : list) {
                    if (!file.isFile()) {
                        throw new RuntimeException("Incorrect DataBase: illegal file " + currFile.getAbsolutePath());
                    }
                    loadFile(getDirNumFromPath(currFile.getAbsolutePath()), file);
                }
            }
        }
        if (types == null) {
            throw new RuntimeException("Incorrect DataBase: have no signature file");
        }
        currentTable.setHashMap(currentHashMap);
        return currentTable;
    }

    private int getFileNumFromPath(String path) {
        if (!path.endsWith(".dat")) {
            throw new RuntimeException("Incorrect DataBase: illegal file in base " + path);
        }
        path = path.replace(".dat", "");
        String[] arr = path.split(File.separator);
        Integer res = new Integer(arr[arr.length - 1]);
        return res;
    }

    private int getDirNumFromPath(String path) {
        if (!path.endsWith(".dir")) {
            throw new RuntimeException("Incorrect DataBase: illegal dir in base " + path);
        }
        path = path.replace(".dir", "");
        String[] arr = path.split(File.separator);
        Integer res = new Integer(arr[arr.length - 1]);
        return res;
    }

    private void loadFile(int dirNum, File file) {
        RandomAccessFile reader = null;
        try {
            reader = new RandomAccessFile(file, "rw");
            if (reader.length() == 0) {
                throw new RuntimeException("Incorrect DataBase: empty file " + file.getAbsolutePath());
            }
            reader.seek(0);
            while (reader.getFilePointer() != reader.length()) {
                loadKeyAndValue(reader, dirNum, getFileNumFromPath(file.getAbsolutePath()), file);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Cannot open file " + file.getAbsolutePath(), e);
        } catch (IOException e) {
            throw new RuntimeException("Cannot use file " + file.getAbsolutePath(), e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Throwable e) {
// not OK
                }
            }
        }
    }

    private void loadKeyAndValue(RandomAccessFile dataFile, int dirNum, int fileNum, File file) {
        try {
            int keyLong = dataFile.readInt();
            int valueLong = dataFile.readInt();
            if (keyLong <= 0 || valueLong <= 0) {
                throw new RuntimeException("Incorrect DataBase: negative long of key or value in file "
                        + file.getAbsolutePath());
            } else {
                byte[] keyBytes = new byte[keyLong];
                byte[] valueBytes = new byte[valueLong];
                dataFile.read(keyBytes);
                dataFile.read(valueBytes);
                byte b = 0;
                b = (byte) Math.abs(keyBytes[0]);
                int ndirectory = b % 16;
                int nfile = b / 16 % 16;
                if (ndirectory != dirNum || nfile != fileNum) {
                    throw new RuntimeException("Incorrect DataBase: illegal key in file " + file.getAbsolutePath());
                }
                String key = new String(keyBytes);
                String value = new String(valueBytes);
                try {
                    Storeable val = provider.deserialize(currentTable, value);
                    currentHashMap.put(key, val);
                } catch (ParseException e) {
                    throw new RuntimeException("Incorrect DataBase: cannot parse value in " + file.getAbsolutePath());
                }
            }
        } catch (IOException | OutOfMemoryError e) {
            throw new RuntimeException("Cannot read file " + file.getAbsolutePath(), e);
        }
    }

    private ArrayList<Class<?>> scanSignature(File currFile) {
        RandomAccessFile signatureFile = null;
        ArrayList<Class<?>> res = new ArrayList<>();
        try {
            signatureFile = new RandomAccessFile(currFile, "rw");
            if (signatureFile.length() == 0) {
                throw new RuntimeException("Incorrect DataBase: signature file is empty");
            }
            FileInputStream in = new FileInputStream(currFile);
            Scanner sc = new Scanner(in);
            while (sc.hasNext()) {
                res.add(getClassFromString(sc.next()));
            }
            sc.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Incorrect DataBase: signature file not found", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (signatureFile != null) {
                    signatureFile.close();
                }
            } catch (Throwable e) {
// not OK
            }
        }
        return res;
    }

    private Class<?> getClassFromString(String str) throws IOException {
        switch (str) {
            case "int":
                return Integer.class;
            case "long":
                return Long.class;
            case "byte":
                return Byte.class;
            case "float":
                return Float.class;
            case "double":
                return Double.class;
            case "boolean":
                return Boolean.class;
            case "String":
                return String.class;
            default:
                throw new IOException("Incorrect DataBase: incorrect type in signature file");
        }
    }
}

