package ru.fizteh.fivt.students.dsalnikov.shell;

import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;
import ru.fizteh.fivt.students.dsalnikov.utils.ShellState;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.FileAlreadyExistsException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Shell<State extends ShellState> {

    private final Parser parser;
    private ShellState state;
    private Map<String, Command> commandMap = new HashMap<>();
    private InputStream inputStream;
    private PrintStream outputStream;
    private PrintStream errorStream;


    //годный шелл
    //стандартные потоки ввода-вывода
    public Shell(Parser withParser) {
        state = new ShellState();
        parser = withParser;
        inputStream = System.in;
        outputStream = System.out;
        errorStream = System.err;
    }

    public Shell(Parser withParser, InputStream inputStream, PrintStream outputStream, PrintStream errorStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.errorStream = errorStream;
        state = new ShellState();
        parser = withParser;
    }


    public Shell(State o, Parser withParser) {
        state = o;
        parser = withParser;
    }

    public void setCommands(List<Command> cs) throws IllegalArgumentException {
        if (cs == null) {
            throw new IllegalArgumentException("this is madness");
        }
        for (Command command : cs) {
            commandMap.put(command.getName(), command);
        }
    }

    public void execute(String[] args) throws Exception {
        String[] commands = parser.parseCommandArguments(args);
        for (String command : commands) {
            String[] cmdArgs =
                    parser.splitSingleCommandByDelimeter(command);
            if (cmdArgs.length == 0 || cmdArgs[0].equals("")) {
            } else {
                Command c = commandMap.get(cmdArgs[0]);
                if (c == null) {
                    throw new IllegalArgumentException("no such Command declared: " + cmdArgs[0]);
                }
                if (c.getArgsCount() != cmdArgs.length - 1) {
                    throw new IllegalArgumentException(String.format("%s command: wrong amount of arguments", c.getName()));
                }
                c.execute(cmdArgs, inputStream, outputStream);
            }
        }
    }

    public void interactiveMode() throws Exception {
        boolean flag = true;
        Scanner sc = new Scanner(inputStream);
        while (flag) {
            outputStream.print("$ ");

            String[] cmd = new String[1];

            cmd[0] = sc.nextLine();
            if (cmd[0].isEmpty()) {
                continue;
            }
            try {
                this.execute(cmd);
            } catch (FileAlreadyExistsException fae) {
                errorStream.println(fae.getMessage());
            } catch (FileNotFoundException fnf) {
                errorStream.println(fnf.getMessage());
            } catch (IOException ioe) {
                errorStream.println(ioe.getMessage());
            } catch (IllegalArgumentException iae) {
                errorStream.println(iae.getMessage());
            } catch (RuntimeException e) {
                errorStream.println(e.getMessage());
            } catch (Exception e) {
                errorStream.println(e.getMessage());
            }
        }
    }

    public void batchMode(String[] args) throws Exception {
        try {
            this.execute(args);
        } catch (FileAlreadyExistsException fae) {
            errorStream.println(fae.getMessage());
            System.exit(1);
        } catch (FileNotFoundException fnf) {
            errorStream.println(fnf.getMessage());
            System.exit(1);
        } catch (IOException ioe) {
            errorStream.println(ioe.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException iae) {
            errorStream.println(iae.getMessage());
            System.exit(1);
        } catch (RuntimeException e) {
            e.getMessage();
            System.exit(1);
        } catch (Exception e) {
            errorStream.println(e.getMessage());
            System.exit(1);
        }
    }

    public void run(String[] args) {
        try {
            if (args.length == 0) {
                interactiveMode();
            } else {
                batchMode(args);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run(String args) {
        run(parser.splitStringArgsByDelimeter(args));
    }

    public ShellState getState() {
        return state;
    }
}


