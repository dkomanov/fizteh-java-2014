package ru.fizteh.fivt.students.kotsurba.filemap.shell;

public class SimpleShellCommandWithParameter extends SimpleShellCommand {
    private String parameter;

    public boolean isMyCommand(final CommandString command) {
        int i = 0;
        if (parameter.equals(command.getArg(1))) {
            i++;
        }
        if (name.equals(command.getArg(0))) {
            if (command.length() > numberOfArgs + i) {
                throw new InvalidCommandException(name + ": too many arguments");
            }
            if (command.length() < numberOfArgs + i) {
                throw new InvalidCommandException(name + " " + hint);
            }
            args = command;
            return true;
        }
        return false;
    }

    public final void setParameter(String newParameter) {
        parameter = newParameter;
    }

    public final String getParameter() {
        return parameter;
    }
}
