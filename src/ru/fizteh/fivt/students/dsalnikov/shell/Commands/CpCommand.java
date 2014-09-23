package ru.fizteh.fivt.students.dsalnikov.shell.Commands;

import ru.fizteh.fivt.students.dsalnikov.shell.Shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;


public class CpCommand implements Command {

    private Shell link;

    public CpCommand(Shell s) {
        link = s;
    }

    public String getName() {
        return "cp";
    }

    public int getArgsCount() {
        return 2;
    }

    private void recursiveCopy(File startPoint, File destination) throws Exception {
        File[] listOfFiles = startPoint.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    File tempDestination = new File(destination.getAbsolutePath(), file.getName());
                    Files.copy(file.toPath(), tempDestination.toPath());
                } else {
                    File newDir = new File(destination, file.getName()).toPath().normalize().toFile();
                    newDir = newDir.toPath().normalize().toFile();
                    if (!newDir.getCanonicalFile().mkdirs()) {
                        throw new Exception("error creating directory");
                    }
                    recursiveCopy(file, newDir);
                }
            }
        }
    }

    private void checkPaths(File source, File destination) throws IOException {
        String compare = destination.toPath().relativize(source.toPath()).toString();
        if (compare.matches("[\\\\./]+") || compare.equals("")) {
            throw new IOException("Can not copy folder(file) to itself or to its child-folder");
        }
        if (!source.exists()) {
            throw new FileNotFoundException("Source file or directory do not exist");
        }
        if (destination.isFile() && destination.exists()) {
            if (source.isFile()) {
                throw new IOException("Can not copy file to existing file");
            }
            throw new IOException("Can not copy directory to existing file");
        }
        if (!destination.getParentFile().equals(source.getParentFile())) {
            throw new IOException("Incorrect destination path");
        }
    }

    private void copyFile(File source, File destination) throws IOException {
        if (source.isFile() && destination.isDirectory()) {
            destination = new File(destination.getAbsolutePath(), source.getName());
            Files.copy(source.toPath(), destination.toPath());
        } else {
            Files.copy(source.toPath(), destination.toPath());
        }
    }

    public void execute(String[] str) throws Exception {
        if (str.length != 3 && str.length != 4) {
            throw new IllegalArgumentException("Illegal arguments");
        } else {
            if (str.length == 4) {
                File source = new File(link.getState().getState(), str[2]);
                source = source.toPath().normalize().toFile();
                File destination = new File(link.getState().getState(), str[3]);
                destination = destination.toPath().normalize().toFile();
                if (str[1].equals("-r")) {
                    checkPaths(source, destination);
                    if (source.isFile() && destination.isDirectory()) {
                        destination = new File(destination.getAbsolutePath(), source.getName());
                        Files.copy(source.toPath(), destination.toPath());
                    } else if (source.isFile()) {
                        copyFile(source, destination);
                    } else {
                        File newDir = new File(destination, str[2]);
                        newDir = newDir.toPath().normalize().toFile();
                        if (!newDir.getAbsoluteFile().mkdirs()) {
                            throw new Exception("error creating directory");
                        }
                        recursiveCopy(source, newDir);
                    }
                } else {
                    throw new IllegalArgumentException("Flag " + str[1] + " is not supported in this command");
                }
            } else if (str.length == 3) {
                File source = new File(link.getState().getState(), str[1]);
                source = source.toPath().normalize().toFile();
                File destination = new File(link.getState().getState(), str[2]);
                destination = destination.toPath().normalize().toFile();
                if (source.isDirectory()) {
                    if (source.list().length != 0) {
                        throw new DirectoryNotEmptyException("Directory isn't empty. Use -r flag to copy.");
                    } else {
                        File dir = new File(destination, str[1]);
                        checkPaths(source, new File(destination, str[2]));
                        dir.mkdir();
                    }
                } else {
                    checkPaths(source, destination);
                    copyFile(source, destination);
                }
            }
        }
    }
}
