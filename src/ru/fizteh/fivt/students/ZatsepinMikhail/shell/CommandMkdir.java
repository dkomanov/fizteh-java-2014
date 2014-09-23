package ru.fizteh.fivt.students.ZatsepinMikhail.shell;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by mikhail on 20.09.14.
 */
public class CommandMkdir extends Command{
    public CommandMkdir(){
        name = "mkdir";
        numberOfArguments = 2;
    }
    public boolean run(String[] arguments){
        if (arguments.length != numberOfArguments)
            return false;
        try{
            Files.createDirectory(Paths.get(System.getProperty("user.dir") + "/" + arguments[1]));
        }
        catch (Exception e){
            System.out.println("IOException");
        }
        return true;
    }
}
