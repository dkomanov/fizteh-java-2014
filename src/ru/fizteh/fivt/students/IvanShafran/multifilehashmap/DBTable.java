package ru.fizteh.fivt.students.IvanShafran.multifilehashmap;


import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;

public class DBTable {
    private File workingDirectory;
    private HashMap<Integer, HashMap<Integer, DBFile>> mapOfDBFiles;

    public HashMap<Integer, HashMap<Integer, DBFile>> getMapOfDBFiles()
    {
        return mapOfDBFiles;
    }

    private void checkSubDirectory(String path) throws Exception {
        String exceptionText = workingDirectory.getAbsolutePath() + " is not a database table";

        File subDirectory;
        try {
            subDirectory = new File(Paths.get(workingDirectory.getAbsolutePath(), path).toString());
        }
        catch (Exception e) {
            throw new Exception(exceptionText);
        }

        if (!subDirectory.isDirectory()) {
            throw new Exception(exceptionText);
        }

        HashSet<String> goodFiles = new HashSet<>();
        for (int i = 0; i < 16; ++i) {
            goodFiles.add(Integer.toString(i) + ".dat");
        }

        for (String filename : subDirectory.list()) {
            if (!goodFiles.contains(filename)) {
                throw new Exception(exceptionText);
            }

            File file;
            try {
                file = new File(Paths.get(workingDirectory.getAbsolutePath(), filename).toString());
            }
            catch (Exception e) {
                throw new Exception(exceptionText);
            }

            if (file.isDirectory()) {
                throw new Exception(exceptionText);
            }
        }
    }

    private void checkWorkingDirectory() throws Exception {
        HashSet<String> goodPaths = new HashSet<>();
        for (int i = 0; i < 16; ++i) {
            goodPaths.add(Integer.toString(i) + ".dir");
        }

        for (String directory : workingDirectory.list()) {
            if (!goodPaths.contains(directory)) {
                throw new Exception(workingDirectory.getAbsolutePath() + " is not a database table");
            }

            try {
                checkSubDirectory(directory);
            }
            catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }
    }

    private void initDBTable() throws Exception {
        for (int i = 0; i < 16; ++i) {
            mapOfDBFiles.put(i, new HashMap<>());

            for (int j = 0; j < 16; ++j) {
                File file;
                try {
                    file = new File(Paths.get(workingDirectory.getAbsolutePath(), Integer.toString(i) + ".dir",
                            Integer.toString(j) + ".dat").toString());
                }
                catch (Exception e) {
                    throw new Exception("error during database table initialization");
                }

                mapOfDBFiles.get(i).put(j, new DBFile(file));
            }
        }
    }

    DBTable(File file) throws Exception {
        workingDirectory = file;
        try {
            checkWorkingDirectory();
        }
        catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        try {
            initDBTable();
        }
        catch (Exception e) {
            throw new Exception("error during database table initialization");
        }
    }
}
