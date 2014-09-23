package ru.fizteh.fivt.students.ZatsepinMikhail.shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by mikhail on 20.09.14.
 */
public class CommandRm extends Command {
    public CommandRm(){
        name = "rm";
        numberOfArguments = 3;
    }
    @Override
    public boolean run(String[] arguments){
        if (arguments.length != 3 & arguments.length != 2)
            return false;
        boolean recursive = (arguments.length == numberOfArguments & arguments[1].equals("-r"));
        if (!recursive)
            generalDelete(arguments);
        else
            recursiveDelete(arguments);
        return true;

    }
    private boolean generalDelete(String[] arguments){
        String filePath = FilesFunction.toAbsolutePathString(arguments[1]);
        try {
            if (!Files.deleteIfExists(Paths.get(filePath)))
                System.out.println(name + ": cannot remove \'" + arguments[1] + "\'" +
                    ": No such file or directory");
        }
        catch(Exception e){
            if (Files.isDirectory(Paths.get(filePath)))
                System.out.println(name + "; cannot remove \'" + arguments[1] + "\'" +
                     ": Is a directory");
            else
                System.out.println("IOException");
            return false;
        }
        return true;
    }

    private boolean recursiveDelete(String[] arguments){
        String filePath = FilesFunction.toAbsolutePathString(arguments[2]);

        FileVisitorDelete myFileVisitorDelete;
        myFileVisitorDelete = new FileVisitorDelete();

        if (myFileVisitorDelete != null){
            try {
                Files.walkFileTree(Paths.get(filePath), myFileVisitorDelete);
            }
            catch(IOException e) {
                System.out.println("Error while walkingFileTree");
                return false;
            }
        }
        return true;
    }
}
