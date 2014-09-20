package ru.fizteh.fivt.students.ZatsepinMikhail.shell;

import java.nio.file.FileSystems;
import java.nio.file.Files;

/**
 * Created by mikhail on 19.09.14.
 */
public class CommandCat extends Command{
    public CommandCat()
    {
        name = "cat";
        numberOfArguments = 1;
    }
    public boolean run(String[] arguments)
    {
        if (arguments.length - 1 != numberOfArguments)
            return false;
        try{
            //System.out.println("*      " + arguments[0]);
            //System.out.println("*      " + arguments[1]);
            Files.copy(FileSystems.getDefault().getPath(arguments[1]), System.out);
        }
        catch(Exception e){
            System.out.println(name + ": " + arguments[1] + ": No such file in directory");
        }
        return true;
    }


}

