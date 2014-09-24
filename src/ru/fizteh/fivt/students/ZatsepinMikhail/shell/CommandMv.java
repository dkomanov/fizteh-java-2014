package ru.fizteh.fivt.students.ZatsepinMikhail.shell;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by mikhail on 20.09.14.
 */
public class CommandMv extends Command{
    public CommandMv(){
        name = "mv";
        numberOfArguments = 3;
    }
    @Override
    public boolean run(String[] arguments){
        if (arguments.length != numberOfArguments)
            return false;
        Path startPath = FilesFunction.toAbsolutePathString(arguments[1]);
        Path destinationPath = FilesFunction.toAbsolutePathString(arguments[2]);
        Path fileName = startPath.getFileName();

        if (!Files.exists(startPath)){
            System.out.println(name + ": cannot stat \'" + arguments[1] + "\': No such file or directory");
            return false;
        }

        if (Files.isDirectory(destinationPath)) {
            destinationPath = destinationPath.resolve(fileName);
        }
        else if(destinationPath.toString().endsWith("/")){
            System.out.println(name + ": cannot move \'" + arguments[1] + "\' to \'" +
                               arguments[2] + "\': Not a directory");
            return false;
        }

        try{
            Files.move(startPath, destinationPath);
        }
        catch(Exception e){
            System.out.println("IOException");
        }
        return true;
    }
}