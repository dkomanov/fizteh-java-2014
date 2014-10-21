package ru.fizteh.fivt.students.VasilevKirill.multimap;

import ru.fizteh.fivt.students.VasilevKirill.multimap.db.*;
import ru.fizteh.fivt.students.VasilevKirill.multimap.db.shell.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Kirill on 19.10.2014.
 */
public class MultiTable {
    private File tableDirectory;
    private FileMap[][] files;

    public MultiTable(File tableDirectory) throws IOException {
        this.tableDirectory = tableDirectory;
        files = new FileMap[16][16];
        File[] directories = tableDirectory.listFiles();
        if (directories == null) {
            return;
        }
        for (File it : directories) {
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
    }

    public File getTableDirectory() {
        return tableDirectory;
    }

    public void handle(String[] args) throws IOException {
        if (args[0].equals("put")) {
            if (args[1] == null || args[2] == null) {
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
            if (args[1] == null) {
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
}
