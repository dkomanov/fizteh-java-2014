package ru.fizteh.fivt.students.dsalnikov.shell.Commands;

import ru.fizteh.fivt.students.dsalnikov.shell.Shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class CpCommand implements Command {
    public String getName() {
        return "cp";
    }

    public int getArgsCount() {
        return 2;
    }

    private void recursiceCopy(File startPoint, File destination) throws Exception {
        File[] listOfFiles = startPoint.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    File tempDestination = new File(destination.getAbsolutePath(), file.getName());
                    Files.copy(file.toPath(), tempDestination.toPath());
                } else {
                    File newDir = new File(destination, file.getName());
                    newDir = newDir.toPath().normalize().toFile();
                    if (!newDir.getCanonicalFile().mkdir()) {
                        throw new Exception("Can not create directory");
                    }
                    recursiceCopy(file, newDir);
                }
            }
        }
    }

    public void execute(String[] str) throws Exception {
        if (str.length != 3) {
            throw new IllegalArgumentException("Illegal arguments");
        } else {
            File source = new File(link.getState().getState(), str[1]);
            source = source.toPath().normalize().toFile();
            File destination = new File(link.getState().getState(), str[2]);
            destination = destination.toPath().normalize().toFile();

            String compare = destination.toPath().relativize(source.toPath()).toString();
            if (compare.matches("[\\\\./]+") || compare.equals("")) {
                throw new IOException("Can not copy folder(file) to itself or to its child-folder");
            }
            if (!source.exists()) {
                throw new IOException("Source file or directory do not exist");
            }
            if (destination.isFile() && destination.exists()) {
                if (source.isFile()) {
                    throw new IOException("Can not copy file to existed file");
                }
                throw new IOException("Can not copy directory to existed file");
            }
            if (source.isFile() && destination.isDirectory()) {
                destination = new File(destination.getAbsolutePath(), source.getName());
                Files.copy(source.toPath(), destination.toPath());
            }

            if (destination.isDirectory()) {
                File newDir = new File(destination, str[1]);
                newDir = newDir.toPath().normalize().toFile();
                if (!newDir.getAbsoluteFile().mkdir()) {
                    throw new Exception("Can not create directory: " + destination.getName());
                }
                recursiceCopy(source, newDir);
            }
        }
    }


    public CpCommand(Shell s) {
        link = s;
    }

    private Shell link;
}
