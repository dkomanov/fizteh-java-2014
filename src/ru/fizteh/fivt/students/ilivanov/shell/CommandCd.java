package ru.fizteh.fivt.students.ilivanov.shell;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class CommandCd implements Command {
    private ArrayList<String> parameters;

    CommandCd(final ArrayList<String> parameters) throws Exception {
        if (parameters.size() != 2) {
            throw new Exception("wrong number of parameters");
        } else {
            this.parameters = new ArrayList<>(parameters);
        }
    }

    @Override
    public int execute() {
        try {
            Path path = Paths.get(Shell.currentDirectory.getCanonicalPath());
            path = path.resolve(parameters.get(1));
            File newDir = new File(path.toAbsolutePath().toString());
            if (!newDir.exists() || !newDir.isDirectory()) {
                System.err.println("cd: \"" + parameters.get(1) + "\": no such directory");
                return -1;
            } else {
                Shell.currentDirectory = newDir.getCanonicalFile();
            }
        } catch (Exception e) {
            System.err.println("cd: \"" + parameters.get(1) + "\": " + e.getMessage());
            return -1;
        }
        return 0;
    }
}
