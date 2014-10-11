package ru.fizteh.fivt.students.kotsurba.shell.shellcommands;

import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommand;
import ru.fizteh.fivt.students.kotsurba.shell.Context.Context;

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
        for (int i = 0; i < content.length; ++i) {
            System.out.println(content[i]);
        }
    }

}
