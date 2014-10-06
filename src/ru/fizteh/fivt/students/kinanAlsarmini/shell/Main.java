package ru.fizteh.fivt.students.kinanAlsarmini.shell;

import java.io.IOException;

class Main {
    public static void main(String[] args) throws IOException {
        ExternalCommand[] possibleCommands = new ExternalCommand[] {
                new CopyCommand(),
                new MakeDirCommand(),
                new MoveCommand(),
                new RemoveCommand(),
                new ExitCommand(),
                new DirCommand(),
                new ChangeDirCommand(),
                new PrintDirCommand(),
                new CatCommand()
        };

        Shell shell = new Shell(possibleCommands);

        if (args.length == 0) {
            shell.startInteractive();
        } else {
            StringBuilder commands = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                commands.append(args[i]);
                if (i < args.length - 1) {
                    commands.append(" ");
                }
            }

            shell.startBatch(commands.toString());
        }
    }
}

