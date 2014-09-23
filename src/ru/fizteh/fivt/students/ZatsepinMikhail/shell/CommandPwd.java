package ru.fizteh.fivt.students.ZatsepinMikhail.shell;

/**
 * Created by mikhail on 20.09.14.
 */
public class CommandPwd extends Command{
    public CommandPwd(){
        name = "pwd";
        numberOfArguments = 1;
    }
    public boolean run(String[] args){
        if (args.length != numberOfArguments)
            return false;
        try{
            System.out.println(System.getProperty("user.dir"));
        }
        catch (Exception e){
            System.out.println("IOException");
        }
        return true;
    }
}
