package ru.fizteh.fivt.students.kotsurba.filemap.shell;

public abstract class SimpleShellCommand implements ShellCommand {
    protected String name;
    protected int numberOfArgs;
    protected CommandString args;
    protected String hint;

    @Override
    public void run() {
    }

    @Override
    public boolean isMyCommand(final CommandString command) {
        if (name.equals(command.getArg(0))) {
            if (command.length() > numberOfArgs) {
                throw new InvalidCommandException(name + ": too many arguments");
            }
            if (command.length() < numberOfArgs) {
                throw new InvalidCommandException(name + " " + hint);
            }
            args = command;
            return true;
        }
        return false;
    }

    public final String getHint() {
        return hint;
    }

    public final void setHint(String newHint) {
        hint = newHint;
    }

    public final String getName() {
        return name;
    }

    public final int getNumberOfArgs() {
        return numberOfArgs;
    }

    public void setName(final String newName) {
        name = newName;
    }

    public void setNumberOfArgs(final int newNumberOfArgs) {
        numberOfArgs = newNumberOfArgs;
    }

    public String getArg(final int index) {
        return args.getArg(index);
    }

    public String getSpacedArg(final int index) {
        return args.getSpacedArg(index);
    }
}
