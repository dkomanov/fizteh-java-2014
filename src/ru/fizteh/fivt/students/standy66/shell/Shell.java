package ru.fizteh.fivt.students.standy66.shell;


import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Created by astepanov on 20.09.14.
 */
public final class Shell {
    /**
     *
     */
    private Scanner scanner;
    private boolean interactive;

    /**
     * @param inputStream
     */
    public Shell(final InputStream inputStream, final boolean isInteractive) {
        if (inputStream == null) {
            throw new IllegalArgumentException("inputStream shouldn't be null");
        }
        scanner = new Scanner(inputStream);
        interactive = isInteractive;
    }

    /**
     * Run a shell
     */
    public void run() {
        while (true) {
            if (interactive) {
                System.out.print("$ ");
                System.out.flush();
            }
            String line;
            try {
                line = scanner.nextLine();
            } catch (NoSuchElementException e) {
                return;
            }
            String[] actions = line.split(";");

            for (int i = 0; i < actions.length; i++) {
                String[] args = actions[i].split(" ");
                Action action = null;
                switch (args[0]) {
                    case "cd":
                        action = new ChangeDirectoryAction(args);
                        break;
                    case "pwd":
                        action = new PrintWorkingDirectoryAction(args);
                        break;
                    case "mkdir":
                        action = new MakeDirectoryAction(args);
                        break;
                    case "rm":
                        action = new RemoveAction(args);
                        break;
                    case "cp":
                        action = new CopyAction(args);
                        break;
                    case "mv":
                        action = new MoveAction(args);
                        break;
                    case "ls":
                        action = new ListAction(args);
                        break;
                    case "exit":
                        System.exit(0);
                        break;
                    case "cat":
                        action = new CatAction(args);
                        break;
                    default:
                        System.err.println("Unknown command");
                        System.err.flush();
                        if (!interactive) {
                            System.exit(-1);
                        }
                        continue;
                }
                if (!action.run() && !interactive) {
                    System.exit(-1);
                }
            }
        }
    }
}
