package ru.fizteh.fivt.students.Sibgatullin_Damir.Shell;

/**
 * Created by Lenovo on 01.10.2014.
 */
public class PwdCommand implements Commands {
    public void execute(String[] args) { System.out.println(Shell.currentPath); }

    public String getName() {
        return "pwd";
    }
}
