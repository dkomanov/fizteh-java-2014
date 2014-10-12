package ru.fizteh.fivt.students.kotsurba.shell.shellcommands;

import ru.fizteh.fivt.students.kotsurba.filemap.shell.InvalidCommandException;
import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommand;

import ru.fizteh.fivt.students.kotsurba.shell.Context.*;

import java.io.IOException;

public class ShellMkdir extends SimpleShellCommand {
    private Context context;

    public ShellMkdir(final Context newContext) {
        context = newContext;
        setName("mkdir");
        setNumberOfArgs(2);
        setHint("usage: mkdir <directory name>");
    }

    @Override
    public void run() {
        try {
            context.makeDir(getArg(1));
        } catch (IOException e) {
            throw new InvalidCommandException(getName() + " argument " + getArg(1));
        }
    }
}
