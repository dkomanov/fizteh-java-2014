package ru.fizteh.fivt.students.ZatsepinMikhail.shell;

/**
 * Created by mikhail on 20.09.14.
 */

import java.io.File;

public class CommandLs extends Command {
    public CommandLs(){
        name = "ls";
        numberOfArguments = 0;
    }
    public boolean run(String[] arguments){
        String[] listOfFiles = new File(System.getProperty("user.dir")).list();
        for (String oneFileName: listOfFiles){
            System.out.println(oneFileName);
        }
        return true;
    }
}
