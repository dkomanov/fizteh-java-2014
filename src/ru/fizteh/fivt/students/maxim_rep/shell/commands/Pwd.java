package ru.fizteh.fivt.students.maxim_rep.shell.commands;

public class Pwd implements ShellCommand {

    String currentPath;

    public Pwd(String currentPath) {
        this.currentPath = currentPath;
    }

    /**
     * 
     * @return
     */
    @Override
    public boolean execute() {
        System.out.println(currentPath);
        return true;
    }

}
