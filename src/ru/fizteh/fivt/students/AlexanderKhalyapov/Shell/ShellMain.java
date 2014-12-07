package ru.fizteh.fivt.students.AlexanderKhalyapov.Shell;

import java.io.File;
import java.io.IOException;

final class ShellMain {
    private ShellMain() {
    }
    public static void main(final String[] args) throws IOException {
        File currentDirectory = new File("");
        Shell shell = new Shell(currentDirectory);
        ShellExecutor exec = new ShellExecutor();
        if (args.length != 0) {
            shell.batchState(args, exec);
        } else {
            shell.interactiveState(exec);
        }
        System.exit(0);
    }
}
