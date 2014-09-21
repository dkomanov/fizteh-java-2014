package ru.fizteh.fivt.students.ilivanov.shell;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class CommandRm implements Command {
    private ArrayList<String> parameters;
    private boolean recursively;

    CommandRm(ArrayList<String> parameters) throws Exception {
        if (parameters.size() != 2 && parameters.size() != 3)
            throw new Exception("wrong number of parameters");
        else if (parameters.size() == 3 && !parameters.get(1).equals("-r"))
            throw new Exception("wrong parameters");
        else{
            this.parameters = new ArrayList<>(parameters);
            recursively = (parameters.size() == 3);
        }
    }
    @Override
    public int execute() {
        String filename;
        if (!recursively)
            filename = parameters.get(1);
        else
            filename = parameters.get(2);
        try {
            Path path = Paths.get(Shell.currentDirectory.getCanonicalPath());
            path = path.resolve(filename);
            File file = new File(path.toAbsolutePath().toString());
            if (!file.exists()) {
                System.err.println("rm: \"" + filename + "\": no such file");
                return -1;
            } else {
                if (!recursively) {
                    if (!file.delete()) {
                        System.err.println("rm: \"" + filename + "\": can't delete");
                        return -1;
                    }
                }
                else {
                    if (!deleteFileRecursively(file)){
                        System.err.println("rm: \"" + filename + "\": can't delete some file inside");
                        return -1;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("rm: \""+ filename +"\": "+e.getMessage());
            return -1;
        }
        return 0;
    }

    private boolean deleteFileRecursively(File file){
        boolean result = true;
        if (file.isDirectory()) {
            File[] Content = file.listFiles();
            if (Content == null)
                return false;
            for (File f : Content)
                result = result && deleteFileRecursively(f);
        }
        return file.delete() && result;
    }
}
