package ru.fizteh.fivt.students.kotsurba.shell.shellcommands;

import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommand;
import ru.fizteh.fivt.students.kotsurba.shell.Context.Context;

public class ShellCat extends SimpleShellCommand {
    private Context context;

    public ShellCat(final Context newContext) {
        context = newContext;
        setName("cat");
        setNumberOfArgs(2);
        setHint("usage: cat <file>");
    }

    @Override
    public void run() {
        context.cat(getArg(1));
    }
}
