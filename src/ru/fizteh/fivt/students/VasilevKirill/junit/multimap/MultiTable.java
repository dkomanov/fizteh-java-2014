package ru.fizteh.fivt.students.VasilevKirill.junit.multimap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.VasilevKirill.junit.multimap.db.FileMap;
import ru.fizteh.fivt.students.VasilevKirill.junit.multimap.db.GetCommand;
import ru.fizteh.fivt.students.VasilevKirill.junit.multimap.db.PutCommand;
import ru.fizteh.fivt.students.VasilevKirill.junit.multimap.db.RemoveCommand;
import ru.fizteh.fivt.students.VasilevKirill.junit.multimap.db.shell.Command;
import ru.fizteh.fivt.students.VasilevKirill.junit.multimap.db.shell.Shell;
import ru.fizteh.fivt.students.VasilevKirill.junit.multimap.db.shell.Status;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;

/**
 * Created by Kirill on 19.10.2014.
 */
public class MultiTable implements Table {
    private File tableDirectory;
    private FileMap[][] files;
    private Map<String, String> data;
    private Map<String, String> oldData;
    private Map<String, String> prevCommitData;
    private int numUnsavedChanges;

    public MultiTable(File tableDirectory) throws IOException {
        this.tableDirectory = tableDirectory;
        files = new FileMap[16][16];
        File[] directories = tableDirectory.listFiles();
        if (directories == null) {
            return;
        }
        for (File it : directories) {
            if (!it.isDirectory()) {
                continue;
            }
            int numDirectory = Integer.parseInt(it.getName().substring(0, it.getName().indexOf(".")));
            File[] datFiles = it.listFiles();
            if (datFiles == null) {
                continue;
            }
            for (File datIt : datFiles) {
                int numFile = Integer.parseInt(datIt.getName().substring(0, datIt.getName().indexOf(".")));
                files[numDirectory][numFile] = new FileMap(datIt.getCanonicalPath());
            }
        }
        data = getData();
        oldData = getData();
        prevCommitData = getData();
        numUnsavedChanges = 0;
    }

    @Override
    public String getName() {
        return tableDirectory.getName();
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
        numUnsavedChanges++;
        return retValue;
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        String retValue = data.get(key);
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
                String value = oldData.get(pair.getKey());
                if (value == null) {
                    number++;
                    String[] args = {"put", (String) pair.getKey(), (String) pair.getValue() };
                    handle(args);
                }
            }
            for (Map.Entry pair : oldData.entrySet()) {
                String value = data.get(pair.getKey());
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
            System.out.println(e);
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
                String value = data.get(pair.getKey());
                if (value == null) {
                    numChanges++;
                    String[] args = {"put", (String) pair.getKey(), (String) pair.getValue() };
                    handle(args);
                }
            }
            for (Map.Entry pair : data.entrySet()) {
                String value = prevCommitData.get(pair.getKey());
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
            System.out.println(e);
        }
        return numChanges;
    }

    @Override
    public List<String> list() {
        Map<String, String> keyMap = getData();
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
            files[numDirectory][numFile] = new FileMap(currentFile.getCanonicalPath());
            Status status = new Status(files[numDirectory][numFile]);
            Map<String, Command> cmds = new HashMap<String, Command>();
            cmds.put(new PutCommand().toString(), new PutCommand());
            new Shell(cmds, status).handle(args);
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
            files[numDirectory][numFile] = new FileMap(currentFile.getCanonicalPath());
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
            Set<String> keys = new HashSet<String>();
            File[] directories = tableDirectory.listFiles();
            for (File it : directories) {
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
                    files[numDirectory][numFile] = new FileMap(datIt.getCanonicalPath());
                    Set<String> currentFileKeySet = files[numDirectory][numFile].getKeys();
                    for (String keyIt : currentFileKeySet) {
                        keys.add(keyIt);
                    }
                }
            }
            for (String keyIt : keys) {
                System.out.print(keyIt + " ");
            }
            System.out.println();
        }
        if (args[0].equals("size")) {
            System.out.println(size());
        }
        if (args[0].equals("commit")) {
            commit();
        }
        if (args[0].equals("rollback")) {
            rollback();
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

    public Map<String, String> getData() {
        Map<String, String> data = new HashMap<>();
        for (FileMap[] it : files) {
            for (FileMap it2 : it) {
                if (it2 == null) {
                    continue;
                }
                Map<String, String> currentFileData = it2.getMap();
                for (Map.Entry pair : currentFileData.entrySet()) {
                    data.put((String) pair.getKey(), (String) pair.getValue());
                }
            }
        }
        return data;
    }

    public String getTableName() {
        return null;
    }

    public int getNumUnsavedChanges() {
        return numUnsavedChanges;
    }
}
