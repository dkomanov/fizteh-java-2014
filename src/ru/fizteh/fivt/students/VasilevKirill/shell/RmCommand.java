package ru.fizteh.fivt.students.VasilevKirill.shell;

import java.io.File;
import java.io.IOException;

/**
 * Created by Kirill on 23.09.2014.
 */
public class RmCommand implements Command {
    @Override
    public void execute(String[] args) throws IOException {
        if (args.length < 2) return;
        if (args[1] == null) return;
        if (args[1].equals("-r")) {
            File file = new File(Shell.currentPath + File.separator + args[2]);
            if (!file.exists()) {
                System.out.println("rm: cannot remove '" + args[2] + "': No such file or directory\"");
                return;
            }
            if (file.isDirectory()) {
                File[] listFiles = file.listFiles();
                if (listFiles.length == 0)
                    file.delete();
                else {
                    for (File f : listFiles) {
                        String[] new_args = {"rm", "-r", f.getName()};
                        rm_recursive(new_args, Shell.currentPath + File.separator + file.getName());
                    }
                }
                if (!file.delete()) return;
            } else {
                if (!file.delete()) return;
            }
        } else {
            File file = new File(Shell.currentPath + File.separator + args[1]);
            if (!file.exists()) {
                System.out.println("rm: cannot remove '" + args[1] + "': No such file or directory");
                return;
            }
            if (file.isDirectory()) {
                System.out.println("rm: " + args[1] + ": is a directory");
                return;
            }
            if (!file.delete()) return;
        }
    }

    private void rm_recursive(String[] args, String directory)
    {
        if (args.length < 2) return;
        if (args[1] == null) return;
        if (args[1].equals("-r")) {
            File file = new File(directory + File.separator + args[2]);
            if (!file.exists()) {
                System.out.println("rm: cannot remove '" + args[2] + "': No such file or directory\"");
                return;
            }
            if (file.isDirectory()) {
                File[] listFiles = file.listFiles();
                if (listFiles.length == 0)
                    if (!file.delete()) return;
                else {
                    for (File f : listFiles) {
                        String[] new_args = {"rm", "-r", f.getName()};
                        rm_recursive(new_args, directory + File.separator + file.getName());
                    }
                }
                if(!file.delete()) return;
            } else {
                if (!file.delete()) return ;
            }
        } else {
            File file = new File(Shell.currentPath + File.separator + args[1]);
            if (!file.exists()) {
                System.out.println("rm: cannot remove '" + args[1] + "': No such file or directory");
                return;
            }
            if (file.isDirectory()) {
                System.out.println("rm: " + args[1] + ": is a directory");
                return;
            }
            file.delete();
        }
    }

    @Override
    public String toString() {
        return "rm";
    }
}
