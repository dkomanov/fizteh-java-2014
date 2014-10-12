package ru.fizteh.fivt.students.kotsurba.shell.shellcommands;

import ru.fizteh.fivt.students.kotsurba.filemap.shell.InvalidCommandException;
import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommand;
import ru.fizteh.fivt.students.kotsurba.shell.Context.*;

import java.io.IOException;

public class ShellRm extends SimpleShellCommand {
    private Context context;

    public ShellRm(final Context newContext) {
        context = newContext;
        setName("rm");
        setNumberOfArgs(2);
        setHint("usage: rm <something>");
    }

    @Override
    public void run() {
        try {
            context.remove(getArg(1));
        } catch (IOException e) {
            throw new InvalidCommandException(getName() + " argument " + getArg(1) + " " + e.getMessage());
        }
    }
}
