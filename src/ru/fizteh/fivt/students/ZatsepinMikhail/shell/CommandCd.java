package ru.fizteh.fivt.students.ZatsepinMikhail.shell;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Stack;

/**
 * Created by mikhail on 20.09.14.
 */
public class CommandCd extends Command {
    public CommandCd(){
        name = "cd";
        numberOfArguments = 1;
    }
    public boolean run(String[] arguments){
        if (arguments.length == 1){
            System.setProperty("user.dir", "/");
            return true;
        }
        String[] startPath;
        Stack<String> newPathStack = new Stack<String>();
        String newPath = "";
        if (arguments[1].charAt(0) != '/')
            startPath = (System.getProperty("user.dir") + "/" + arguments[1]).split("/");
        else
            startPath = arguments[1].split("/");

        for (String oneDirectory: startPath){
            if (!oneDirectory.equals(".") & !oneDirectory.equals(".."))
                newPathStack.push(oneDirectory);
            if (oneDirectory.equals("..") & !newPathStack.empty())
                newPathStack.pop();
        }
        while(!newPathStack.empty()){
            if (newPath.isEmpty())
                newPath = newPathStack.pop();
            else
                newPath = newPathStack.pop() + "/" + newPath;
        }

        if (Files.isDirectory(Paths.get(newPath)))
            System.setProperty("user.dir", newPath);
        else
           System.out.println(name + ": \'" + arguments[1] + "\': No such file or directory");
        return true;
    }
}
