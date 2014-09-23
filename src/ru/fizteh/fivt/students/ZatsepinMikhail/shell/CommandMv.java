package ru.fizteh.fivt.students.ZatsepinMikhail.shell;

import java.nio.file.Files;
import java.nio.file.Paths;

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
        String startPath = FilesFunction.toAbsolutePathString(arguments[1]);
        String destinationPath = FilesFunction.toAbsolutePathString(arguments[2]);
        String[] parsedFile = arguments[1].split(System.getProperty("file.separator"));
        String fileName = parsedFile[parsedFile.length - 1];

        if (!Files.exists(Paths.get(startPath))){
            System.out.println(name + ": cannot stat \'" + fileName + "\': No such file or directory");
            return false;
        }

        if (Files.isDirectory(Paths.get(destinationPath))) {
            destinationPath = destinationPath + System.getProperty("file.separator") + fileName;
        }
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