package ru.fizteh.fivt.students.VasilevKirill.Storeable.junit.multimap;

import org.json.JSONArray;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.VasilevKirill.Storeable.MyStorable;
import ru.fizteh.fivt.students.VasilevKirill.Storeable.StoreableParser;
import ru.fizteh.fivt.students.VasilevKirill.Storeable.junit.multimap.db.FileMap;
import ru.fizteh.fivt.students.VasilevKirill.Storeable.junit.multimap.db.GetCommand;
import ru.fizteh.fivt.students.VasilevKirill.Storeable.junit.multimap.db.PutCommand;
import ru.fizteh.fivt.students.VasilevKirill.Storeable.junit.multimap.db.RemoveCommand;
import ru.fizteh.fivt.students.VasilevKirill.Storeable.junit.multimap.db.shell.Command;
import ru.fizteh.fivt.students.VasilevKirill.Storeable.junit.multimap.db.shell.Shell;
import ru.fizteh.fivt.students.VasilevKirill.Storeable.junit.multimap.db.shell.Status;

import java.io.*;
import java.text.ParseException;
import java.util.*;

/**
 * Created by Kirill on 19.10.2014.
 */
public class MultiTable implements Table {
    private File tableDirectory;
    private FileMap[][] files;
    private Map<String, Storeable> data;
    private Map<String, Storeable> oldData;
    private Map<String, Storeable> prevCommitData;
    private int numUnsavedChanges;
    private MultiMap multiMap;
    private Class[] typeList;

    public MultiTable(File tableDirectory, MultiMap multiMap, Class[] typeList) throws IOException {
        this.multiMap = multiMap;
        this.tableDirectory = tableDirectory;
        this.typeList = typeList;
        files = new FileMap[16][16];
        File[] directories = tableDirectory.listFiles();
        if (directories == null) {
            return;
        }
        for (File it : directories) {
            if (!it.isDirectory()) {
                continue;
            }
            if (!it.getName().contains(".")) {
                throw new IOException("Incorrect directory name in table");
            }
            int numDirectory = Integer.parseInt(it.getName().substring(0, it.getName().indexOf(".")));
            File[] datFiles = it.listFiles();
            if (datFiles == null) {
                continue;
            }
            for (File datIt : datFiles) {
                int numFile = Integer.parseInt(datIt.getName().substring(0, datIt.getName().indexOf(".")));
                files[numDirectory][numFile] = new FileMap(datIt.getCanonicalPath(), typeList);
            }
        }
        data = getData();
        oldData = getData();
        prevCommitData = getData();
        numUnsavedChanges = 0;
        writeSignatures();
    }

    public MultiTable(File tableDirectory, MultiMap multiMap, List<Class<?>> typeList) throws IOException {
        Class[] typeArray = new Class[typeList.size()];
        for (int i = 0; i < typeArray.length; ++i) {
            typeArray[i] = typeList.get(i);
        }
        new MultiTable(tableDirectory, multiMap, typeArray);
    }

    @Override
    public String getName() {
        return tableDirectory.getName();
    }

    @Override
    public Storeable get(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        return data.get(key);
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        Storeable retValue = data.get(key);
        data.put(key, value);
        numUnsavedChanges++;
        return retValue;
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        Storeable retValue = data.get(key);
        if (retValue == null) {
            return null;
        }
        data.remove(key);
        numUnsavedChanges++;
        return retValue;
    }

    @Override
    public int size() {
        return data.size();
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
                Storeable value = oldData.get(pair.getKey());
                if (value == null) {
                    number++;
                    JSONArray inputValue = new JSONArray(((MyStorable) pair.getValue()).dataToList());
                    String[] args = {"put", (String) pair.getKey(),  inputValue.toString() };
                    handle(args);
                }
            }
            for (Map.Entry pair : oldData.entrySet()) {
                Storeable value = data.get(pair.getKey());
                if (value == null) {
                    number++;
                    String[] args = {"remove", (String) pair.getKey()};
                    handle(args);
                }
            }
            newOutput.close();
            System.setOut(oldOutput);
            prevCommitData = new HashMap<>(oldData);
            oldData = new HashMap<>(data);
            numUnsavedChanges = 0;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            if (e.getMessage().equals("")) {
                System.out.println(e);
            } else {
                System.out.println(e.getMessage());
            }
        }
        return number;
    }

    @Override
    public int rollback() {
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
                Storeable value = data.get(pair.getKey());
                if (value == null) {
                    numChanges++;
                    JSONArray inputValue = new JSONArray(((MyStorable) pair.getValue()).dataToList());
                    String[] args = {"put", (String) pair.getKey(),  inputValue.toString() };
                    handle(args);
                }
            }
            for (Map.Entry pair : data.entrySet()) {
                Storeable value = prevCommitData.get(pair.getKey());
                if (value == null) {
                    numChanges++;
                    String[] args = {"remove", (String) pair.getKey()};
                    handle(args);
                }
            }
            newOutput.close();
            System.setOut(oldOutput);
            oldData = prevCommitData;
            data = prevCommitData;
            numUnsavedChanges = 0;

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            if (e.getMessage().equals("")) {
                System.out.println(e);
            } else {
                System.out.println(e.getMessage());
            }
        }
        return numChanges;
    }

    @Override
    public int getColumnsCount() {
        return typeList.length;
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= typeList.length) {
            throw new IndexOutOfBoundsException("MultiTable: incorrect index");
        }
        return typeList[columnIndex];
    }

    public List<String> list() throws IOException {
        Map<String, Storeable> keyMap = getData();
        List<String> retList = new ArrayList<>();
        for (Map.Entry pair : keyMap.entrySet()) {
            retList.add(pair.getKey().toString());
        }
        return retList;
    }

    public File getTableDirectory() {
        return tableDirectory;
    }

    //Old version of method. Saved for compatibility.
    public void handle(String[] args) throws IOException {
        if (args[0].equals("put")) {
            if (args.length != 3) {
                throw new IOException("Filemap: Wrong arguments");
            }
            int numDirectory = args[1].hashCode() % 16;
            int numFile = args[1].hashCode() / 16 % 16;
            String currentDirectoryPath = tableDirectory.getCanonicalPath() + File.separator + numDirectory + ".dir";
            File currentDirectory = new File(currentDirectoryPath);
            if (!currentDirectory.exists()) {
                if (!currentDirectory.mkdir()) {
                    throw new IOException("Can't create " + currentDirectory.getName());
                }
            }
            File currentFile = new File(currentDirectory.getCanonicalFile() + File.separator + numFile + ".dat");
            if (!currentFile.exists()) {
                if (!currentFile.createNewFile()) {
                    throw new IOException("Can't create " + currentFile.getName());
                }
            }
            files[numDirectory][numFile] = new FileMap(currentFile.getCanonicalPath(), typeList);
            Status status = new Status(files[numDirectory][numFile]);
            new PutCommand().execute(args, status);
            files[numDirectory][numFile].close();
        }
        if (args[0].equals("get") || args[0].equals("remove")) {
            if (args.length != 2) {
                throw new IOException("Filemap: Wrong arguments");
            }
            int numDirectory = args[1].hashCode() % 16;
            int numFile = args[1].hashCode() / 16 % 16;
            String currentDirectoryPath = tableDirectory.getCanonicalPath() + File.separator + numDirectory + ".dir";
            File currentDirectory = new File(currentDirectoryPath);
            if (!currentDirectory.exists()) {
                System.out.println("not found");
                return;
            }
            File currentFile = new File(currentDirectory.getCanonicalFile() + File.separator + numFile + ".dat");
            if (!currentFile.exists()) {
                System.out.println("not found");
                return;
            }
            files[numDirectory][numFile] = new FileMap(currentFile.getCanonicalPath(), typeList);
            Status status = new Status(files[numDirectory][numFile]);
            Map<String, Command> cmds = new HashMap<String, Command>();
            cmds.put(new GetCommand().toString(), new GetCommand());
            cmds.put(new RemoveCommand().toString(), new RemoveCommand());
            new Shell(cmds, status).handle(args);
            files[numDirectory][numFile].close();
            removeEmptyFiles();
        }
        if (args[0].equals("list")) {
            if (args.length != 1) {
                throw new IOException("Filemap: Wrong arguments");
            }
            Set<String> keys = data.keySet();
            /*Set<String> keys = new HashSet<String>();
            File[] directories = tableDirectory.listFiles();
            for (File it : directories) {
                if (!it.isDirectory()) {
                    continue;
                }
                int numDirectory = Integer.parseInt(it.getName().substring(0, it.getName().indexOf(".")));
                if (numDirectory < 0 || numDirectory > 15) {
                    continue;
                }
                File[] datFiles = it.listFiles();
                for (File datIt : datFiles) {
                    int numFile = Integer.parseInt(datIt.getName().substring(0, datIt.getName().indexOf(".")));
                    if (numFile < 0 || numFile > 15) {
                        continue;
                    }
                    files[numDirectory][numFile] = new FileMap(datIt.getCanonicalPath(), typeList);
                    Set<String> currentFileKeySet = files[numDirectory][numFile].getKeys();
                    for (String keyIt : currentFileKeySet) {
                        keys.add(keyIt);
                    }
                }
            }*/
            for (String keyIt : keys) {
                System.out.print(keyIt + " ");
            }
            System.out.println();
        }
        if (args[0].equals("size")) {
            System.out.println(size());
        }
        if (args[0].equals("commit")) {
            System.out.println(commit());
        }
        if (args[0].equals("rollback")) {
            System.out.println(rollback());
        }
    }

    public void removeEmptyFiles() throws IOException {
        File[] directories = tableDirectory.listFiles();
        if (directories == null) {
            return;
        }
        for (File it : directories) {
            File[] datFiles = it.listFiles();
            if (datFiles == null) {
                continue;
            }
            if (datFiles.length == 0) {
                it.delete();
            }
            for (File datIt : datFiles) {
                if (datIt.length() == 0) {
                    datIt.delete();
                }
            }
            datFiles = it.listFiles();
            if (datFiles.length == 0) {
                it.delete();
            }
        }
    }

    public int getNumKeys() {
        int numKeys = 0;
        for (FileMap[] it : files) {
            for (FileMap it2 : it) {
                if (it2 != null) {
                    numKeys += it2.getKeys().size();
                }
            }
        }
        return numKeys;
    }

    public Map<String, Storeable> getData() throws IOException {
        Map<String, Storeable> data = new HashMap<>();
        for (FileMap[] it : files) {
            for (FileMap it2 : it) {
                if (it2 == null) {
                    continue;
                }
                Map<String, String> currentFileData = it2.getMap();
                try {
                    for (Map.Entry pair : currentFileData.entrySet()) {
                        Storeable inputValue = StoreableParser.stringToStoreable((String) pair.getValue(), typeList);
                        data.put((String) pair.getKey(), inputValue);
                        //data.put((String) pair.getKey(), (String) pair.getValue());
                    }
                } catch (ParseException e) {
                    throw new IOException("MultiTable: can't parse the data");
                }
            }
        }
        return data;
    }

    public int getNumUnsavedChanges() {
        return numUnsavedChanges;
    }

    public Class[] getTypeList() {
        return typeList;
    }

    private void writeSignatures() throws IOException {
        File sigFile = new File(tableDirectory.getCanonicalPath() + File.separator + "signature.tsv");
        if (!sigFile.exists()) {
            if (!sigFile.createNewFile()) {
                throw new IOException("MultiTable: unable to create signatures file");
            }
        }
        FileWriter writer = new FileWriter(sigFile);
        for (int i = 0; i < typeList.length; ++i) {
            String value = typeList[i].getSimpleName();
            writer.write(typeList[i].getSimpleName() + " ");
        }
        writer.close();
    }


}
