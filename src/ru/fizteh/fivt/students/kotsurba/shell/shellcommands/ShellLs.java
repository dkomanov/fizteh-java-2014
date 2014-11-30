package ru.fizteh.fivt.students.kotsurba.shell.shellcommands;

import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommand;
import ru.fizteh.fivt.students.kotsurba.shell.context.Context;

public final class ShellLs extends SimpleShellCommand {
    private Context context;

    public ShellLs(final Context newContext) {
        context = newContext;
        setName("ls");
        setNumberOfArgs(1);
        setHint("usage: ls");
    }

    @Override
    public void run() {
        String[] content = context.getDirContent();
        for (String aContent : content) {
            System.out.println(aContent);
        }
    }

}
