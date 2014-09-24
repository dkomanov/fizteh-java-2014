package ru.fizteh.fivt.students.andreyzakharov.shell;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Shell {
    // System.getProperty("user.dir");
    Path wd = null;
    boolean interactive;
    private final Map<String, AbstractCommand> commandMap = new HashMap<>();

    public Shell(boolean interactive) {
        try {
            wd = Paths.get("").toRealPath();
        } catch (IOException e) {
            error("Shell: i/o error on init");
            System.exit(1);
        }
        this.interactive = interactive;

        class KeyValuePair {
            String key;
            AbstractCommand value;

            KeyValuePair(String key, AbstractCommand value) {
                this.key = key;
                this.value = value;
            }
        }
        KeyValuePair[] commands = {
                new KeyValuePair("pwd", new PwdCommand(this)),
                new KeyValuePair("cd", new CdCommand(this)),
                new KeyValuePair("mkdir", new MkdirCommand(this)),
                new KeyValuePair("rm", new RmCommand(this)),
                new KeyValuePair("cp", new CpCommand(this)),
                new KeyValuePair("mv", new MvCommand(this)),
                new KeyValuePair("ls", new LsCommand(this)),
                new KeyValuePair("cat", new CatCommand(this)),
                new KeyValuePair("exit", new ExitCommand(this))
        };
        for (KeyValuePair pair : commands) {
            commandMap.put(pair.key, pair.value);
        }
    }

    public Path getWd() {
        return wd;
    }

    public void setWd(Path newWd) {
        wd = newWd;
    }

    /**
     * Interprets a string array as a command to the shell.
     *
     * @param cmd First element must be a valid command, the rest is passed as
     *            arguments to it.
     */
    public void interpret(String[] cmd) {
        AbstractCommand command = commandMap.get(cmd[0]);
        if (command != null) {
            command.execute(cmd);
        } else {
            error("Shell: " + cmd[0] + ": command not found");
        }
    }

    /**
     * Output a message ot stdout.
     *
     * @param msg A message to display
     */
    public void output(String msg) {
        System.out.println(msg);
    }

    /**
     * Output an error message to stderr and abort if in batch mode.
     *
     * @param err A message to display
     */
    public void error(String err) {
        System.err.println(err);
        if (!interactive) {
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            String argsc = null;
            for (String s : args) {
                argsc += s + " ";
            }
            String[] cmds = argsc.split(";");
            Shell shell = new Shell(false);

            for (String s : cmds) {
                shell.interpret(s.trim().split("\\s"));
            }
        } else {
            Scanner scanner = new Scanner(System.in);
            scanner.useDelimiter("\\s*[;\\n]\\s*");
            Shell shell = new Shell(true);

            System.out.print("$ ");
            while (scanner.hasNext()) {
                shell.interpret(scanner.next().trim().split("\\s+"));
                System.out.print("$ ");
            }

            scanner.close();
        }
    }
}
