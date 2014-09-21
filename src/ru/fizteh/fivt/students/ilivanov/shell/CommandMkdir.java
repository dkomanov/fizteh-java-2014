package ru.fizteh.fivt.students.ilivanov.shell;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

public class CommandMkdir implements Command {
    private ArrayList<String> parameters;

    CommandMkdir(ArrayList<String> parameters) throws Exception{
        if (parameters.size() != 2)
            throw new Exception("wrong number of parameters");
        else
            this.parameters = new ArrayList<>(parameters);
    }
    @Override
    public int execute() {
        try {
            Path path = Shell.currentDirectory.toPath();
            path = path.resolve(parameters.get(1));
            File newDir = path.toFile();
            if (!newDir.mkdir()) {
                System.err.println("mkdir: \""+parameters.get(1)+"\": hasn't been created");
                return -1;
            }
        } catch (Exception e) {
            System.err.println("mkdir: \""+parameters.get(1)+"\": "+e.getMessage());
            return -1;
        }
        return 0;
    }
}
