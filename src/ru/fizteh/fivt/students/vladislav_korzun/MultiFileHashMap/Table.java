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
                System.out.println("tablename exists");
            } else {
                throw new IOException();
            }
        } catch (UnsupportedOperationException e) {
            System.err.println("Path is not associated"
                    + "with the default provider");
        } catch (InvalidPathException e) {
            System.err.println("Invalid path");
        } catch (IOException e) {
            System.err.println("Can't delete data or table");
        }
    }
    
    void drop(String path) {
        try {
            Path tabledir = dir.resolve(path);
            if (!tabledir.toFile().exists()) {
                System.out.println("tablename not exists");
            } else {
                if (tabledir.toFile().isDirectory()) {
                    File[] dbfiles = tabledir.toFile().listFiles();
                    for (int i = 0; i < dbfiles.length; i++) {
                        if (!dbfiles[i].delete()) {
                            throw new IOException();
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Can't delete data or table");
        } catch (InvalidPathException e) {
            System.err.println("Invalid path");
        } catch (UnsupportedOperationException e) {
            System.err.println("Path is not associated"
                    + "with the default provider");
        }
    }
    
    Map<String, String> use(String path, Path olddir) {
        try {
            Path tabledir = dir.resolve(path);
            
            if (!tabledir.toFile().exists()) {
                System.out.println("tablename not exists");
            } else {
                if (tabledir.toFile().isDirectory()) {
                    FileManager filemanager = new FileManager();
                    if (olddir != dir) {
                        filemanager.writeTable(olddir);
                    }
                    filemanager.readTable(tabledir);
                    return filemanager.filemap;
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
    
    void showTables() {
        try {            
            if (!dir.toFile().exists()) {
                System.out.println("Directory not exists");
            } else {
                if (dir.toFile().isDirectory()) {
                    File[] tabledirs = dir.toFile().listFiles();
                    for (int i = 0; i < tabledirs.length; i++) {
                        if (tabledirs[i].isDirectory()) {
                            System.out.print(tabledirs[i].getName() + " ");
                            FileManager filemanager = new FileManager();
                            filemanager.readTable(tabledirs[i].toPath());
                            System.out.println(filemanager.filemap.size());
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
