package ru.fizteh.fivt.students.Sibgatullin_Damir.Shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Lenovo on 01.10.2014.
 */
public class CdCommand implements Commands {

        public void execute(String[] args) throws MyException {
            if (args.length == 1) { return; }
            try {
                Path newPath = Shell.currentPath.resolve(args[1]).toRealPath();
                if (Files.isDirectory(newPath)) {
                    Shell.currentPath = newPath;
                } else {
                    System.out.println("cd: '" + args[1] + "': Not a directory");
                }
            } catch (IOException e) {
               throw new MyException("cd: '" + args[1] + "': No such file or directory");
            }
        }

        public String getName() {
            return "cd";
        }
    }

