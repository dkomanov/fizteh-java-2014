package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell;

import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.TerminalException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Log;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;


/**
 * Class that represents a terminal which can execute some commands that work with some data.
 * @param <ShellStateImpl>
 *         Concrete implementation of {@link ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.ShellState}
 *         the shell works with.
 * @author phoenix
 * @see Command
 * @see ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.ShellState
 */
public class Shell<ShellStateImpl extends ShellState<ShellStateImpl>> {
    public static final int READ_BUFFER_SIZE = 16 * 1024;

    /**
     * Object encapsulating commands and data they work with.
     */
    private final ShellStateImpl shellState;

    /**
     * Available commands for invocation.
     */
    private Map<String, Command<ShellStateImpl>> commandMap;

    /**
     * If the user is entering commands or it is package mode.
     */
    private boolean interactive;

    public Shell(ShellStateImpl shellState) {
        this.shellState = shellState;
        try {
            init();
        } catch (TerminalException exc) {
            //already handled
        }
    }


    /**
     * Executes command in this shell
     * @param commandStr
     *         some shell command
     * @return returns true if execution finished correctly; false otherwise;
     */
    public boolean execute(String commandStr) {
        String[] args = commandStr.trim().split("[ \t]{1,}");
        if (args[0].isEmpty()) {
            return true;
        }

        Log.log(Shell.class, "Invocation request: " + Arrays.toString(args));

        Command<ShellStateImpl> command = commandMap.get(args[0]);
        if (command == null) {
            Log.log(Shell.class, String.format("Command not found by name %s", args[0]));
            System.err.println("Sorry, this command is missing");
            return false;
        } else {
            try {
                command.execute(shellState, args);

                return true;
            } catch (Throwable exc) {
                /*
                    If it is TerminalException, error report is already written.
                    Otherwise we should catch all type of throwables and write detailed
                    error message to log, short error message to user.
                  */
                if (!(exc instanceof TerminalException)) {
                    Log.log(
                            Shell.class,
                            exc,
                            String.format("Error during execution of %s", args[0]));
                    System.err.println(String.format("%s: Method execution error", args[0]));
                }
                return false;
            }
        }
    }

    /**
     * Called from {@link ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.ShellState}
     * when the shell should exit.
     * @param exitCode
     */
    public void onExitRequest(int exitCode) {
        Log.close();
        System.exit(exitCode);
    }

    /**
     * Prepares shell for further command interpretation
     */
    private void init() throws TerminalException {
        Log.log(Shell.class, "Shell starting");

        try {
            shellState.init(this);
        } catch (Exception exc) {
            Utility.handleError(exc.getMessage(), exc, true);
        }

        commandMap = shellState.getCommands();
    }

    public boolean isInteractive() {
        return interactive;
    }

    /**
     * Execute commands from input stream. Commands are awaited until the-end-of-stream.
     * @param stream
     */
    public void run(InputStream stream) {
        interactive = true;

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream), READ_BUFFER_SIZE);
        try {
            while (true) {
                System.out.println(shellState.getGreetingString());
                String str = reader.readLine();

                // end of stream
                if (str == null) {
                    break;
                }

                String[] commands = str.split(";");
                for (int i = 0, len = commands.length; i < len; i++) {
                    boolean correct = execute(commands[i]);
                    if (!correct) {
                        break;
                    }
                }
            }
        } catch (IOException exc) {
            Log.log(Shell.class, exc, "Cannot parse inputstream for shell");
        }

        try {
            shellState.persist();
        } catch (Exception exc) {
            shellState.exit(1);
        }
    }

    /**
     * Execute commands from command line arguments. Note that command line arguments are first
     * concatenated into a single line then split and parsed.
     * @param args
     */
    public void run(String[] args) {
        interactive = false;

        StringBuilder sb = new StringBuilder();
        for (int i = 0, len = args.length; i < len; i++) {
            sb.append((i == 0 ? "" : " ")).append(args[i]);
        }
        String cmds = sb.toString();
        String[] commands = cmds.split(";");

        for (int i = 0, len = commands.length; i < len; i++) {
            boolean correct = execute(commands[i]);
            if (!correct) {
                shellState.exit(1);
            }
        }

        try {
            shellState.persist();
        } catch (Exception exc) {
            shellState.exit(1);
        }
        shellState.exit(0);
    }
}
