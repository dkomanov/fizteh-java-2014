package ru.fizteh.fivt.students.alexpodkin.shell;

import java.io.IOException;
import java.util.HashSet;

public class Shell {

    public static HashSet<String> commands;

    public static void main(String[] args) throws IOException {
        commands = new HashSet<>();
        commands.add("cd");
        commands.add("mkdir");
        commands.add("pwd");
        commands.add("rm");
        commands.add("cp");
        commands.add("mv");
        commands.add("ls");
        commands.add("exit");
        commands.add("cat");

        Launcher launcher = new Launcher(commands, new StringParser() {
            @Override
            public String[] parse(String string) {
                return string.trim().split("[\\s]+");
            }
        });

        try {
            if (!launcher.launch(args)) {
                System.exit(1);
            }
        } catch (IOException e) {
            System.err.print(e.getMessage());
            System.exit(1);
        }
    }
}


