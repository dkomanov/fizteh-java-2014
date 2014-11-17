package ru.fizteh.fivt.students.kotsurba.shell.shellcommands;

import ru.fizteh.fivt.students.kotsurba.filemap.shell.InvalidCommandException;
import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommandWithParameter;
import ru.fizteh.fivt.students.kotsurba.shell.context.Context;

import java.io.IOException;

public class ShellCp extends SimpleShellCommandWithParameter {
    private Context context;

    public ShellCp(final Context newContext) {
        context = newContext;
        setName("cp");
        setNumberOfArgs(3);
        setParameter("-r");
        setHint("usage: cp <source path> <destination path> or cp -r <source path> <destination path>");
    }

    @Override
    public void run() {
        try {
            if (getParameter().equals(getArg(1))) {
                context.copyWithParameter(getArg(2), getArg(3));
            } else {
                context.copy(getArg(1), getArg(2));
            }
        } catch (IOException e) {
            throw new InvalidCommandException(getName() + " " + e.getMessage());
        }
    }
}
