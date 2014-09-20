package ru.fizteh.fivt.students.ZatsepinMikhail.shell;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by mikhail on 20.09.14.
 */
public class CommandPwd extends Command{
    public CommandPwd(){
        name = "pwd";
        numberOfArguments = 0;
    }
    public boolean run(String[] args){
        if (args.length - 1 != numberOfArguments)
            return false;
        try{
            System.out.println(Paths.get(new File("").getAbsolutePath()));
        }
        catch (Exception e)
        {
            System.out.println("IOException");
        }
        return true;
    }
}
