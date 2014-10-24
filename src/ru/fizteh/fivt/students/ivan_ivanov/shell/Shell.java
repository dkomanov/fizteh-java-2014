package ru.fizteh.fivt.students.ivan_ivanov.shell;

import java.io.IOException;
import java.io.File;
import java.util.Scanner;
import java.nio.file.Path;

public class Shell {

    private ShellState state;

    public Shell(final File currentDirectory) {
        state = new ShellState();
        state.setPath((currentDirectory.getAbsoluteFile().toPath()));
    }

    public Shell() {
    }
   

    final ShellState getState() {
        return state;
    }

    final void setState(final Path inState) {

        state.setPath(inState);
    }

    public final void batchState(final String[] args, final Executor exec) throws IOException {

        StringBuilder tmp = new StringBuilder();
        for (String arg : args) {
            tmp.append(arg).append(" ");
        }

        String[] command = tmp.toString().split("\\;");

        String cmd = "";

        for (int i = 0; i < command.length; ++i) {
            cmd = command[i].trim();
            try {
                exec.execute(this, cmd);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
        exec.execute(this, "exit");
    }

    public final void interactiveState(final Executor exec) throws IOException {

        try (Scanner scanner = new Scanner(System.in)) {

            String[] cmd;
            while (true) {
                System.out.print("$ ");
                cmd = scanner.nextLine().trim().split("\\s*;\\s*");


                try {
                    for (String aCmd : cmd) {
                    exec.execute(this, aCmd);
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }

            }
        }
    }
}
