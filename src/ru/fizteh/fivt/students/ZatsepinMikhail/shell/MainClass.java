package ru.fizteh.fivt.students.ZatsepinMikhail.shell;

/**
 * Created by mikhail on 19.09.14.
 */
public class MainClass{
        public static void main(String[] args){
            Shell myShell = new Shell();
            myShell.addCommand(new CommandCat());
            myShell.addCommand(new CommandMkdir());
            myShell.addCommand(new CommandPwd());
            myShell.addCommand(new CommandCd());
            myShell.addCommand(new CommandLs());
            myShell.addCommand(new CommandMv());
            myShell.addCommand(new CommandRm());
            myShell.addCommand(new CommandCp());
            if (args.length > 0){
                myShell.packetMode(args);
            }
            else{
                myShell.interactiveMode();
            }
        }
}

/*
 проверка на директорию в рекурсивных copy и remove
 */
