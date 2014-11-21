package ru.fizteh.fivt.students.standy66_new;

import ru.fizteh.fivt.students.standy66_new.commands.Command;
import ru.fizteh.fivt.students.standy66_new.exceptions.InterpreterInterruptionException;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.stream.Stream;

/** Simple command interpreter.
 * @author astepanov
 * Created by astepanov on 20.10.14.
 */
public class Interpreter {
    private final Map<String, Command> commands;
    private final boolean isInteractive;
    private final Scanner scanner;

    public Interpreter(InputStream inputStream, Map<String, Command> supportedCommands, boolean isInteractive) {
        this.commands = new HashMap<>(supportedCommands);
        this.isInteractive = isInteractive;
        scanner = new Scanner(inputStream);
    }

    public boolean execute() {
        while (true) {
            if (isInteractive) {
                System.out.print("$ ");
                System.out.flush();
            }
            String line;

            try {
                line = scanner.nextLine();
            } catch (NoSuchElementException ignored) {
                return true;
            }
            String[] actions = line.split(";");

            for (String action : actions) {
                String[] args =
                        Stream.of(action.split(" "))
                                .filter((s) -> (!s.isEmpty()))
                                .toArray((size) -> (new String[size]));
                if (args.length == 0) {
                    continue;
                }
                String commandName = args[0];
                Command commandRunnable = commands.get(commandName);
                if (commandRunnable == null) {
                    System.err.println("Unknown command");
                    if (!isInteractive) {
                        return false;
                    }
                    continue;
                }
                try {
                    commandRunnable.execute();
                } catch (InterpreterInterruptionException ignored) {
                    return isInteractive;
                } catch (Exception e) {
                    System.err.printf("%s: %s%n", commandName, e.getMessage());
                    if (!isInteractive) {
                        return false;
                    }
                }
            }
        }
    }
}
