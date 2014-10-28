package ru.fizteh.fivt.students.vladislav_korzun.MultiFileHashMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Map;

public class Table {
    
    private Path dir;
    
    public Table(Path dbdir) {
        dir = dbdir;
    }

    void create(String path) {
        try {
            Path tabledir = dir.resolve(path);
            if (!tabledir.toFile().mkdir()) {
                System.out.println(path + "exists");
            } else {
                System.out.println("created");
            }
        } catch (UnsupportedOperationException e) {
            System.err.println("Path is not associated"
                    + "with the default provider");
        } catch (InvalidPathException e) {
            System.err.println("Invalid path");
        }
    }
    
    void drop(String path, Path olddir, Map<String, String> filemap) {
        try {
            Path tabledir = dir.resolve(path);
            if (!tabledir.toFile().exists()) {
                System.out.println(path + " not exists");
            } else {
                if (tabledir == olddir) {
                    FileManager filemanager = new FileManager();
                    filemanager.filemap = filemap;
                    filemanager.writeTable(olddir);
                } else if (tabledir.toFile().isDirectory()) {
                    removedat(tabledir);
                    tabledir.toFile().delete();
                    System.out.println("dropped");
                }
            }
        } catch (InvalidPathException e) {
            System.err.println("Invalid path");
        } catch (UnsupportedOperationException e) {
            System.err.println("Path is not associated"
                    + "with the default provider");
        }
    }
    
    void removedat(Path tabledir) {
        File table = new File(tabledir.toString());
        File[] dirs = table.listFiles();
        for (File dir : dirs) {
            File[] fls = dir.listFiles();
            for (File fl : fls) {
                fl.delete();
            }
            dir.delete();
        }
    }
    
    FileManager use(String path, Path olddir, Map<String, String> filemap) {
        try {
            Path tabledir = dir.resolve(path);
            
            if (!tabledir.toFile().exists()) {
                System.out.println(path + " not exists");
            } else {
                if (tabledir.toFile().isDirectory()) {
                    FileManager filemanager = new FileManager();
                    if (olddir != dir) {
                        filemanager.filemap = filemap;
                        filemanager.writeTable(olddir);
                    }
                    filemanager.readTable(tabledir);
                    System.out.println("using " + path);
                    return filemanager;
                } else {
                    throw new IOException();
                }
            }
        } catch (IOException e) {
            System.err.println("Path is File");
        } catch (InvalidPathException e) {
            System.err.println("Invalid path");
        } catch (UnsupportedOperationException e) {
            System.err.println("Path is not associated"
                    + "with the default provider");
        }
        return null;
    }
    
    void showTables(Path olddir, Map<String, String> filemap) {
        try {            
            if (!dir.toFile().exists()) {
                System.out.println("Directory not exists");
            } else {
                if (dir.toFile().isDirectory()) {
                    File[] tabledirs = dir.toFile().listFiles();
                    FileManager filemanager = new FileManager();
                    for (int i = 0; i < tabledirs.length; i++) {
                        if (tabledirs[i].isDirectory()) {
                            System.out.print(tabledirs[i].getName() + " ");
                            if (!tabledirs[i].getName().equals(olddir.toFile().getName())) {
                                filemanager.readTable(tabledirs[i].toPath());
                                System.out.println(filemanager.filemap.size()); 
                            } else {
                                System.out.println(filemap.size()); 
                            }
                        }
                    }
                }
            }
        } catch (InvalidPathException e) {
            System.err.println("Invalid path");
        } catch (UnsupportedOperationException e) {
            System.err.println("Path is not associated"
                    + "with the default provider");
        }
    }
}
