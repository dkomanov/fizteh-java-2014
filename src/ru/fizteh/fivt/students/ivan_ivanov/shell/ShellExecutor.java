package ru.fizteh.fivt.students.ivan_ivanov.shell;

public class ShellExecutor extends Executor {

    public ShellExecutor() {

        list();
    }

    public final void list() {

        Command pwd = new Pwd();
        mapOfCmd.put(pwd.getName(), pwd);
        Command cd = new Cd();
        mapOfCmd.put(cd.getName(), cd);
        Command mkdir = new Mkdir();
        mapOfCmd.put(mkdir.getName(), mkdir);
        Command cp = new Cp();
        mapOfCmd.put(cp.getName(), cp);
        Command mv = new Mv();
        mapOfCmd.put(mv.getName(), mv);
        Command ls = new Ls();
        mapOfCmd.put(ls.getName(), ls);
        Command rm = new Rm();
        mapOfCmd.put(rm.getName(), rm);
        Command exit = new Exit();
        mapOfCmd.put(exit.getName(), exit);
        Command cat = new Cat();
        mapOfCmd.put(cat.getName(), cat);
    }
}
