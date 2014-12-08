package ru.fizteh.fivt.students.ZatsepinMikhail.shell;

public class MainClass {
    public static void main(String[] args) {
        Shell myShell = new Shell();
        myShell.addCommand(new CommandCat());
        myShell.addCommand(new CommandMkdir());
        myShell.addCommand(new CommandPwd());
        myShell.addCommand(new CommandCd());
        myShell.addCommand(new CommandLs());
        myShell.addCommand(new CommandMv());
        myShell.addCommand(new CommandRm());
        myShell.addCommand(new CommandCp());

        boolean resultOfExecution;
        if (args.length > 0) {
            resultOfExecution = myShell.packetMode(args);
        } else {
            resultOfExecution = myShell.interactiveMode();
        }
        if (resultOfExecution) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }
}
