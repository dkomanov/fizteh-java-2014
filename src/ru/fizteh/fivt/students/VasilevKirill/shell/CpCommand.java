package ru.fizteh.fivt.students.VasilevKirill.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by Kirill on 23.09.2014.
 */
public class CpCommand implements Command {
    @Override
    public void execute(String[] args) throws IOException{
        if (args.length < 2) return;
        if (args[1] == null) return;
        if (args[1].equals("-r")){
            File source = new File(Shell.currentPath + File.separator + args[2]);
            if (!source.exists()) {
                System.out.println("cp: cannot copy '" + args[2] + "': No such file or directory");
                return;
            }
            File destination = new File(Shell.currentPath + File.separator + args[3] + File.separator + args[2]);
            if (destination.exists()) {
                String prevPath = Shell.currentPath;
                String[] cdArgs = {"cd", Shell.currentPath + File.separator + args[3]};
                new CdCommand().execute(cdArgs);
                String[] rmArgs = {"rm","-r",destination.getName()};
                new RmCommand().execute(rmArgs);
                String[] cdArgs2 = {"cd", prevPath};
                new CdCommand().execute(cdArgs2);
            }
            File destinationDirectory = new File(Shell.currentPath + File.separator + args[3]);
            if (!destinationDirectory.exists()){
                if (!destinationDirectory.mkdir()) return;
            }
            if (!source.isDirectory()) {
                Files.copy(source.toPath(), destination.toPath());
                return;
            }
            Files.copy(source.toPath(), destination.toPath());
            File[] listFiles = source.listFiles();
            for (File f : listFiles){
                String[] new_args = {"cp","-r",f.getName(),destination.getAbsolutePath()};
                cp_recursive(new_args, source.getAbsolutePath());
            }
        } else {
            File source = new File(Shell.currentPath + File.separator + args[1]);
            if (!source.exists()) {
                System.out.println("cp: cannot copy '" + args[1] + "': No such file or directory");
                return;
            }
            if (source.isDirectory()){
                System.out.println("cp: " + args[2] + " is a directory (not copied).");
                return;
            }
            File destination = new File(Shell.currentPath + File.separator + args[2] + File.separator + args[1]);
            if (destination.exists()) {
                destination.delete();
            }
            Files.copy(source.toPath(), destination.toPath());
        }
    }

    private void cp_recursive(String[] args, String directory) throws IOException
    {
        if (args.length < 2) return;
        if (args[1] == null) return;
        File source = new File(directory + File.separator + args[2]);
        if (!source.exists()) return;
        File destination = new File(args[3] + File.separator + args[2]);
        if (destination.exists()){
            String[] rmArgs = {"rm","-r",destination.getName()};
            new RmCommand().execute(rmArgs);
        }
        if (!source.isDirectory()){
            Files.copy(source.toPath(),destination.toPath());
            return;
        }
        Files.copy(source.toPath(), destination.toPath());
        File[] listFiles = source.listFiles();
        if (listFiles.length == 0){
            return;
        }
        for (File f : listFiles)
        {
            String[] new_args = {"cp","-r",f.getName(),destination.getAbsolutePath()};
            cp_recursive(new_args,source.getAbsolutePath());
        }
    }

    @Override
    public String toString() {
        return "cp";
    }
}
