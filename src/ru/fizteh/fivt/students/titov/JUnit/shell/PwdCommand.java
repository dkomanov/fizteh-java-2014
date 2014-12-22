package ru.fizteh.fivt.students.titov.JUnit.shell;

public class PwdCommand extends Command {
    public PwdCommand() {
        name = "pwd";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(final String[] args) {
        if (args.length != numberOfArguments) {
            System.err.println("wrong number of arguments");
            return false;
        }
        try {
            System.err.println(System.getProperty("user.dir"));
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
