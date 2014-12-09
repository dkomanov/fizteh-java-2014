package ru.fizteh.fivt.students.VasilevKirill.multimap.db.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by Kirill on 23.09.2014.
 */
public class CpCommand implements Command {
    @Override
    public boolean checkArgs(String[] args) {
        return false;
    }

    @Override
    public int execute(String[] args, Status status) throws IOException {
        if (args.length < 2) {
            return 1;
        }
        if (args[1] == null || args[2] == null) {
            return 1;
        }
        if (args[1].equals("-r")) {
            if (args[2].equals(args[3])) {
                return 1;
            }
            if (args.length > 4) {
                return 1;
            }
            File source = new File(Shell.currentPath + File.separator + args[2]);
            if (!source.exists()) {
                System.out.println("cp: cannot copy '" + args[2] + "': No such file or directory");
                return 1;
            }
            File destination = new File(Shell.currentPath + File.separator + args[3] + File.separator + args[2]);
            if (destination.exists()) {
                String prevPath = Shell.currentPath;
                String[] cdArgs = {"cd", Shell.currentPath + File.separator + args[3]};
                new CdCommand().execute(cdArgs, status);
                String[] rmArgs = {"rm", "-r", destination.getName()};
                new RmCommand().execute(rmArgs, status);
                String[] cdArgs2 = {"cd", prevPath};
                new CdCommand().execute(cdArgs2, status);
            }
            File destinationDirectory = new File(Shell.currentPath + File.separator + args[3]);
            if (!destinationDirectory.exists()) {
                if (!destinationDirectory.mkdir()) {
                    return 1;
                }
            }
            if (!source.isDirectory()) {
                Files.copy(source.toPath(), destination.toPath());
                return 0;
            }
            Files.copy(source.toPath(), destination.toPath());
            File[] listFiles = source.listFiles();
            for (File f : listFiles) {
                String[] newArgs = {"cp", "-r", f.getName(), destination.getAbsolutePath()};
                if (cpRecursive(newArgs, source.getAbsolutePath(), status) != 0) {
                    return 1;
                }
            }
        } else {
            if (args[1].equals(args[2])) {
                return 1;
            }
            if (args.length > 3) {
                return 1;
            }
            File source = new File(Shell.currentPath + File.separator + args[1]);
            if (!source.exists()) {
                System.out.println("cp: cannot copy '" + args[1] + "': No such file or directory");
                return 1;
            }
            if (source.isDirectory()) {
                System.out.println("cp: " + args[1] + " is a directory (not copied).");
                return 1;
            }
            File destination = new File(Shell.currentPath + File.separator + args[2]);
            if (destination.exists()) {
                if (destination.isDirectory()) {
                    String path = Shell.currentPath + File.separator + args[2] + File.separator + args[1];
                    File newDestination = new File(path);
                    Files.copy(source.toPath(), newDestination.toPath());
                } else {
                    return 1;
                }
            } else {
                destination.createNewFile();
            }
        }
        return 0;
    }

    private int cpRecursive(String[] args, String directory, Status status) throws IOException {
        if (args.length < 2) {
            return 1;
        }
        File source = new File(directory + File.separator + args[2]);
        if (!source.exists()) {
            return 1;
        }
        File destination = new File(args[3] + File.separator + args[2]);
        if (destination.exists()) {
            String[] rmArgs = {"rm", "-r", destination.getName()};
            new RmCommand().execute(rmArgs, status);
        }
        if (!source.isDirectory()) {
            Files.copy(source.toPath(), destination.toPath());
            return 0;
        }
        Files.copy(source.toPath(), destination.toPath());
        File[] listFiles = source.listFiles();
        if (listFiles.length == 0) {
            return 0;
        }
        for (File f : listFiles) {
            String[] newArgs = {"cp", "-r", f.getName(), destination.getAbsolutePath()};
            cpRecursive(newArgs, source.getAbsolutePath(), status);
        }
        return 0;
    }

    @Override
    public String toString() {
        return "cp";
    }
}
