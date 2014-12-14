package ru.fizteh.fivt.students.Bulat_Galiev.junit.InterpreterPackage;

import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Interpreter {
    private static final int SLEEPNUMBER = 15;
    private final TableProvider provider;
    private static Map<String, Command> commandMap;
    private InputStream in;
    private PrintStream out;
    private PrintStream err;

    public Interpreter(final TableProvider singleProvider,
            final Command[] commands, final InputStream inStream,
            final PrintStream outStream, final PrintStream errStream)
            throws IOException {
        if (inStream == null || outStream == null || errStream == null) {
            throw new IllegalArgumentException("Invalid steams");
        }
        provider = singleProvider;
        commandMap = new HashMap<String, Command>();
        for (Command command : commands) {
            commandMap.put(command.getName(), command);
        }
        in = inStream;
        out = outStream;
        err = errStream;
    }

    public Interpreter(final TableProvider singleProvider,
            final Command[] commands) throws IOException {
        this(singleProvider, commands, System.in, System.out, System.err);
    }

    public final void run(final String[] args) throws Exception {
        try {
            if (args.length == 0) {
                interactiveMode();
            } else {
                batchMode(args);
            }
        } catch (ExitException e) {
            System.exit(e.getStatus());
        }
    }

    public final void interactiveMode() throws IOException {
        try {
            String input = "";
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    this.in));
            do {
                try {
                    Thread.sleep(SLEEPNUMBER);
                } catch (InterruptedException e1) {
                    this.err.print(e1.getMessage());
                }
                this.out.print("$ ");
                try {
                    input = in.readLine();
                } catch (IOException e) {
                    this.err.print(e.getMessage());
                    throw new ExitException(-1);
                }
                parser(input, false);
            } while (true);
        } catch (NullPointerException e) {
            throw new ExitException(-1);
        }
    }

    public final void batchMode(final String[] input) throws IOException {
        try {
            StringBuilder cmd = new StringBuilder();
            for (String argument : input) {
                if (cmd.length() != 0) {
                    cmd.append(' ');
                }
                cmd.append(argument);
            }
            String arg = cmd.toString();
            this.parser(arg, true);
        } catch (NullPointerException e) {
            throw new ExitException(-1);
        }
    }

    public final void parser(final String input, final boolean mode)
            throws IOException {
        final StringTokenizer tok = new StringTokenizer(input, ";", false);
        while (tok.hasMoreTokens()) {
            int i = 0;
            StringTokenizer argtok = new StringTokenizer(tok.nextToken(), " ",
                    false);
            if (argtok.countTokens() == 0) {
                this.err.println("null command");
                continue;
            }
            String[] arg = new String[argtok.countTokens()];
            while (argtok.hasMoreTokens()) {
                arg[i++] = argtok.nextToken();
            }
            Command command = commandMap.get(arg[0]);

            if (command == null) {
                this.err.println(arg[0] + " is incorrect command");
                if (mode) {
                    throw new ExitException(-1);
                }
                continue;
            } else {
                try {
                    command.execute(this.provider, arg);
                } catch (Exception e) {
                    this.err.println(e.getMessage());
                    if (mode) {
                        throw new ExitException(-1);
                    }
                }
            }
        }
        if (mode) {
            throw new ExitException(0);
        }
    }

}
