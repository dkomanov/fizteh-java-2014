package ru.fizteh.fivt.students.kotsurba.shell.shellcommands;

import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommand;
import ru.fizteh.fivt.students.kotsurba.shell.context.*;

public class ShellPwd extends SimpleShellCommand {
    private Context context;

    public ShellPwd(final Context newContext) {
        context = newContext;
        setName("pwd");
        setNumberOfArgs(1);
        setHint("usage: pwd");
    }

    @Override
    public void run() {
        System.out.println(context.getCurrentDir());
    }
}
