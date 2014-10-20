package ru.fizteh.fivt.students.kotsurba.shell.shellcommands;

import ru.fizteh.fivt.students.kotsurba.filemap.shell.InvalidCommandException;
import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommandWithParameter;
import ru.fizteh.fivt.students.kotsurba.shell.context.Context;

import java.io.IOException;

public class ShellRm extends SimpleShellCommandWithParameter {
    private Context context;

    public ShellRm(final Context newContext) {
        context = newContext;
        setName("rm");
        setNumberOfArgs(2);
        setHint("usage: rm <something>");
        setParameter("-r");
    }

    @Override
    public void run() {
        try {
            if (getParameter().equals(getArg(1))) {
                context.removeWithParameter(getArg(2));
            } else {
                context.remove(getArg(1));
            }
        } catch (IOException e) {
            throw new InvalidCommandException(getName() + " argument " + getArg(1) + " " + e.getMessage());
        }
    }
}
