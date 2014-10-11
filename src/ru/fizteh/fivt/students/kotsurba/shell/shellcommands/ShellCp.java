package ru.fizteh.fivt.students.kotsurba.shell.shellcommands;

import ru.fizteh.fivt.students.kotsurba.filemap.shell.InvalidCommandException;
import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommand;
import ru.fizteh.fivt.students.kotsurba.shell.Context.*;

import java.io.IOException;

public class ShellCp extends SimpleShellCommand {
    private Context context;

    public ShellCp(final Context newContext) {
        context = newContext;
        setName("cp");
        setNumberOfArgs(3);
        setHint("usage: cp <source path> <destination path>");
    }

    @Override
    public void run() {
        try {
            context.copy(getArg(1), getArg(2));
        } catch (IOException e) {
            throw new InvalidCommandException(getName() + " " + e.getMessage());
        }
    }
}
