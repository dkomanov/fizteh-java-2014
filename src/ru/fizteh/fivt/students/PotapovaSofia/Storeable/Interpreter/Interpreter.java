package ru.fizteh.fivt.students.PotapovaSofia.storeable.Interpreter;

import ru.fizteh.fivt.students.PotapovaSofia.storeable.StoreableMain;
import ru.fizteh.fivt.students.PotapovaSofia.storeable.TableState;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Interpreter {
    public static final String PROMPT = "$ ";
    public static final String STATEMENT_DELIMITER = ";";

    private InputStream in;
    private PrintStream out;

    private final Map<String, Command> commands;
    private final TableState state;

    public Interpreter(TableState state, Command[] commands, InputStream in, PrintStream out) {
        if (in == null || out == null) {
            throw new IllegalArgumentException("Input or Output stream is null");
        }
        this.in = in;
        this.out = out;
        this.state = state;
        this.commands = new HashMap<>();
        for (Command command : commands) {
            this.commands.put(command.getName(), command);
        }
    }

    public Interpreter(TableState state, Command[] commands) {
        this.in = System.in;
        this.out = System.out;
        this.state = state;
        this.commands = new HashMap<>();
        for (Command command : commands) {
            this.commands.put(command.getName(), command);
        }
    }

    public void run(String[] args) {
        try {
            if (args.length == 0) {
                runInteractiveMode();
            } else {
                runBatchMode(args);
            }
        } catch (StopInterpretationException e) {
            // Just stop the interpretation.
        }
    }

    private void runBatchMode(String[] args) throws StopInterpretationException {
        executeLine(String.join(" ", args));
    }

    private void runInteractiveMode() throws StopInterpretationException {
        Scanner in = new Scanner(this.in);
        while (true) {
            out.print(PROMPT);
            try {
                String line = in.nextLine();
                executeLine(line);
            } catch (NoSuchElementException e) {
                break;
            }
        }
    }

    private int executeLine(String line) throws StopInterpretationException {
        String[] cmds = line.split(STATEMENT_DELIMITER + StoreableMain.IGNORE_SYMBOLS_IN_DOUBLE_QUOTES_REGEX);
        Pattern p = Pattern.compile(StoreableMain.SPLIT_BY_SPACES_NOT_IN_BRACKETS_REGEX);
        List<String> tokens = new LinkedList<>();
        for (String current : cmds) {
            tokens.clear();
            Matcher m = p.matcher(current.trim());
            while (m.find()) {
                tokens.add(m.group().trim());
            }
            parse(tokens.toArray(new String[tokens.size()]));
        }
        return 0;
    }

    private void parse(String[] cmdWithArgs) throws StopInterpretationException {
        if (cmdWithArgs.length > 0 && !cmdWithArgs[0].isEmpty()) {
            String commandName = cmdWithArgs[0];
            if (commandName.equals("exit")) {
                StoreableMain.exit(this.state);
            }
            Command command = commands.get(commandName);
            if (command == null) {
                out.println("Wrong command: " + commandName);
            } else {
                String[] args = new String[cmdWithArgs.length - 1];
                for (int i = 1; i < cmdWithArgs.length; i++) {
                    if (cmdWithArgs[i].charAt(0) == '"'
                            && cmdWithArgs[i].charAt(cmdWithArgs[i].length() - 1) == '"') {
                        args[i - 1] = cmdWithArgs[i].substring(1, cmdWithArgs[i].length() - 1);
                    } else {
                        args[i - 1] = cmdWithArgs[i];
                    }
                }
                command.execute(this.state, args);
            }
        }
    }
}
