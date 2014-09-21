package ru.fizteh.fivt.students.ilivanov.shell;


import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class CommandMv implements Command {
    private ArrayList<String> parameters;

    CommandMv(ArrayList<String> parameters) throws Exception{
        if (parameters.size() != 3)
            throw new Exception("wrong number of parameters");
        else
            this.parameters = new ArrayList<>(parameters);
    }

    @Override
    public int execute() {
        String source = parameters.get(1);
        String destination = parameters.get(2);

        try {
            Path srcPath = Paths.get(Shell.currentDirectory.getCanonicalPath());
            srcPath = srcPath.resolve(source);
            File srcFile = new File(srcPath.toAbsolutePath().toString());
            if (!srcFile.exists()) {
                System.err.println("mv: \"" + source + "\": no such file");
                return -1;
            } else {

                Path destPath = Paths.get(Shell.currentDirectory.getCanonicalPath());
                destPath = destPath.resolve(destination);
                File destFile = new File(destPath.toAbsolutePath().toString());
                if (!destFile.exists() || !destFile.isDirectory()){
                    System.err.println("mv: \"" + destination + "\": no such directory");
                    return -1;
                }
                if (destFile.equals(srcFile)){
                    System.err.println("mv: source and destination are the same");
                    return -1;
                }
                if (new File(destFile,source).exists()){
                    source = source + "_copy";
                    while (new File(destFile,source).exists())
                        source = source + "_";
                }
                if (!srcFile.renameTo(new File(destFile,source))){
                    System.err.println("mv: can't move: \""+source+"\"");
                    return -1;
                }

            }
        } catch (Exception e) {
            System.err.println("mv: "+e.getMessage());
            return -1;
        }
        return 0;
    }
}
