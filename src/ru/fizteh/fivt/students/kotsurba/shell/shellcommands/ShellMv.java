package ru.fizteh.fivt.students.kotsurba.shell.shellcommands;

import ru.fizteh.fivt.students.kotsurba.shell.context.*;
import ru.fizteh.fivt.students.kotsurba.filemap.shell.InvalidCommandException;
import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommand;

import java.io.IOException;

public class ShellMv extends SimpleShellCommand {
    private Context context;

    public ShellMv(final Context newContext) {
        context = newContext;
        setName("mv");
        setNumberOfArgs(3);
        setHint("usage: mv <source path> <destination path>");
    }

    @Override
    public void run() {
        try {
            context.move(getArg(1), getArg(2));
        } catch (IOException e) {
            throw new InvalidCommandException(getName() + " bad arguments " + getArg(1) + " " + getArg(2));
        }
    }
}
