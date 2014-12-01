package ru.fizteh.fivt.students.titov.JUnit.ShellPackage;

public class CommandPwd extends Command {
    public CommandPwd() {
        name = "pwd";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(final String[] args) {
        if (args.length != numberOfArguments) {
            System.out.println("wrong number of arguments");
            return false;
        }
        try {
            System.out.println(System.getProperty("user.dir"));
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
