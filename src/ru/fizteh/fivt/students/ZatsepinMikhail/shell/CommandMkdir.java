package ru.fizteh.fivt.students.ZatsepinMikhail.shell;

import sun.management.FileSystem;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by mikhail on 20.09.14.
 */
public class CommandMkdir extends Command{
    public CommandMkdir(){
        name = "mkdir";
        numberOfArguments = 1;
    }
    public boolean run(String[] arguments){
        if (arguments.length - 1 != numberOfArguments)
            return false;
        try{
            Files.createDirectory(Paths.get(new File("").getAbsolutePath() + "/" + arguments[1]));
            System.out.println(Paths.get(new File("").getAbsolutePath() + "/" + arguments[1]));
        }
        catch (Exception e)
        {
            System.out.println("IOException");
        }
        return true;
    }
}
