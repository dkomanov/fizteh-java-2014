package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell;

import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.ExitRequest;
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

    public Shell(ShellStateImpl shellState) throws TerminalException {
        this.shellState = shellState;
        init();
    }

    /**
     * Executes command in this shell
     * @param commandStr
     *         some shell command
     * @throws ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.TerminalException
     */
    private void execute(String commandStr) throws TerminalException, ExitRequest {
        String[] args = commandStr.trim().split("[ \t]{1,}");
        if (args[0].isEmpty()) {
            return;
        }

        Log.log(Shell.class, "Invocation request: " + Arrays.toString(args));

        Command<ShellStateImpl> command = commandMap.get(args[0]);
        if (command == null) {
            Utility.handleError(args[0] + ": command is missing", null, true);
        } else {
            try {
                command.execute(shellState, args);
            } catch (TerminalException | ExitRequest exc) {
                // If it is TerminalException, error report is already written.
                throw exc;
            } catch (Throwable exc) {
                Utility.handleError(args[0] + ": Method execution error", exc, true);
            }
        }
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
    public int run(InputStream stream) throws TerminalException {
        interactive = true;

        if (stream == null) {
            throw new IllegalArgumentException("Input stream must not be null");
        }

        boolean exitRequested = false;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream), READ_BUFFER_SIZE)) {
            while (true) {
                System.out.print(shellState.getGreetingString());
                String str = reader.readLine();

                // End of stream.
                if (str == null) {
                    break;
                }

                String[] commands = str.split(";");
                for (int i = 0, len = commands.length; i < len; i++) {
                    try {
                        execute(commands[i]);
                    } catch (ExitRequest request) {
                        throw request;
                    } catch (TerminalException exc) {
                        // Exception is already handled.
                        break;
                    }
                }
            }
        } catch (IOException exc) {
            Utility.handleError("Cannot read input stream: " + exc.getMessage(), exc, true);
        } catch (ExitRequest request) {
            exitRequested = true;
            return request.getCode();
        } finally {
            if (!exitRequested) {
                try {
                    persistSafelyAndPrepareToExit();
                } catch (ExitRequest request) {
                    return request.getCode();
                }
            }
        }

        // If all contracts are honoured, this line is unreachable.
        throw new AssertionError("No exit request performed");
    }

    /**
     * Execute commands from command line arguments. Note that command line arguments are first
     * concatenated into a single line then split and parsed.
     * @param args
     *         Array of commands. If an error happens during execution of one of the commands in
     *         the
     *         sequence, next commands will not be executed.
     * @return Exit code. 0 means normal status, anything else - abnormal termination (error).
     */
    public int run(String[] args) {
        try {
            interactive = false;

            StringBuilder sb = new StringBuilder();
            for (int i = 0, len = args.length; i < len; i++) {
                sb.append((i == 0 ? "" : " ")).append(args[i]);
            }
            String allCommands = sb.toString();
            String[] commands = allCommands.split(";");

            try {
                for (int i = 0, len = commands.length; i < len; i++) {
                    execute(commands[i]);
                }
            } catch (ExitRequest request) {
                throw request;
            } catch (TerminalException exc) {
                // Exception already handled.
                shellState.prepareToExit(1);
            }
            persistSafelyAndPrepareToExit();
        } catch (ExitRequest request) {
            return request.getCode();
        }

        // If all contracts are honoured, this line is unreachable.
        throw new AssertionError("No exit request performed");
    }

    /**
     * Persists shell state. If fails, calls {@link ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.ShellState#prepareToExit(int)}
     * with non zero exit code.
     */
    private void persistSafelyAndPrepareToExit() throws ExitRequest {
        try {
            shellState.persist();
            shellState.prepareToExit(0);
        } catch (ExitRequest request) {
            throw request;
        } catch (Exception exc) {
            Log.log(Shell.class, exc, "Failed to persist shell state");
            shellState.prepareToExit(1);
        }
    }
}
