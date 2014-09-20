package ru.fizteh.fivt.students.ZatsepinMikhail.shell;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by mikhail on 20.09.14.
 */
public class CommandMv extends Command{
    public CommandMv(){
        name = "mv";
        numberOfArguments = 2;
    }
    public boolean run(String[] arguments){
        if (arguments.length - 1 != numberOfArguments)
            return false;
        String startPath;
        String destinationPath;
        String[] parsedFile = arguments[1].split("/");
        String fileName = parsedFile[parsedFile.length - 1];

        if (arguments[1].charAt(0) == '/')
            startPath = arguments[1];
        else
            startPath = System.getProperty("user.dir") + "/" + arguments[1];

        if (arguments[2].charAt(0) == '/')
            destinationPath = arguments[2];
        else
            destinationPath = System.getProperty("user.dir") + "/" + arguments[2];

        if (!Files.exists(Paths.get(startPath))){
            System.out.println(name + ": cannot stat \'" + fileName + "\': No such file or directory");
            return false;
        }

        if (Files.isDirectory(Paths.get(destinationPath)))
            destinationPath = destinationPath + "/" + fileName;
        else if(destinationPath.charAt(destinationPath.length() - 1) == '/'){
            System.out.println(name + ": cannot move \'" + fileName + "\' to \'" +
                               arguments[2] + "\': Not a directory");
            return false;
        }

        try{
            Files.move(Paths.get(startPath), Paths.get(destinationPath));
        }
        catch(Exception e){
            System.out.println("IOException");
        }
        return true;
    }
}
