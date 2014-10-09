package ru.fizteh.fivt.students.Sibgatullin_Damir.Shell;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Lenovo on 01.10.2014.
 */
public class CatCommand implements Commands {
    public void execute(String[] args) throws MyException {
        if (args.length == 1) {
            throw new MyException("cat: not enough arguments");
        }
        if (args.length > 2) {
            throw new MyException("cat: too many arguments");
        }

        Path path = Paths.get(Shell.currentPath.resolve(args[1]).toString());
        if (!Files.exists(path)) { throw new MyException("cat: " + path.toString() + ": No such file or directory"); }
        if (Files.isDirectory(path)) { throw new MyException("cat: '" + path.toString() + "': is a directory"); }
        if (!Files.isReadable(path)) { throw new MyException("cat: '" + path.toString() + "': not readable"); }

        BufferedReader bufreader;
        try {
            bufreader = new BufferedReader(new FileReader(path.toString()));
            String line;
            while ((line = bufreader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            throw new MyException("cd: an exception has occurred");
        }
    }

    public String getName() {
        return "cat";
    }
}
