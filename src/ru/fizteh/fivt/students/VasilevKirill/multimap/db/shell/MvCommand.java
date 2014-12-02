package ru.fizteh.fivt.students.VasilevKirill.multimap.db.shell;

import java.io.File;
import java.io.IOException;

/**
 * Created by Kirill on 24.09.2014.
 */
public class MvCommand implements Command {
    @Override
    public boolean checkArgs(String[] args) {
        return false;
    }

    @Override
    public int execute(String[] args, Status status) throws IOException {
        if (args.length < 2 || args.length > 3) {
            return 1;
        }
        if (args[1] == null || args[2] == null) {
            return 1;
        }
        File source = new File(Shell.currentPath + File.separator + args[1]);
        if (!source.exists()) {
            System.out.println("mv: '" + args[1] + "': No such file or directory");
            return 1;
        }
        File[] listFiles = new File(Shell.currentPath).listFiles();
        boolean isContain = false;
        for (File f : listFiles) {
            if (f.getName().equals(args[2])) {
                isContain = true;
            }
        }
        if (!args[2].contains(File.separator) && !isContain) {
            source.renameTo(new File(Shell.currentPath + File.separator + args[2]));
            return 0;
        }
        File destination = new File(Shell.currentPath + File.separator + args[2]);
        if (!destination.exists()) {
            System.out.println("mv: '" + args[2] + "': No such file or directory");
            return 1;
        }
        if (source.isDirectory()) {
            String[] cpArgs = {"cp", "-r", args[1], args[2]};
            new CpCommand().execute(cpArgs, status);
            String[] rmArgs = {"rm", "-r", args[1]};
            new RmCommand().execute(rmArgs, status);
        } else {
            String[] cpArgs = {"cp", args[1], args[2]};
            new CpCommand().execute(cpArgs, status);
            String[] rmArgs = {"rm", args[1]};
            new RmCommand().execute(rmArgs, status);
        }
        return 0;
    }

    @Override
    public String toString() {
        return "mv";
    }
}
