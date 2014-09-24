package ru.fizteh.fivt.students.LebedevAleksey.Shell01;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Shell {
    private static String WrongQuoteStructureMessage = "Wrong quote structure.";
    private boolean hasCorrectTerminated = false;

    static String GetCurrentFolder() {
        return System.getProperty("user.dir");
    }

    private static List<ParsedCommand> parseCommand(String input) throws ShellException {
        List<CommandToken> tokensByQuote = splitCommandByQuote(" " + input.replace("\" \"", "\"  \"") + " ");
        ArrayList<ArrayList<CommandToken>> commandsTokens = splitCommands(tokensByQuote);
        return getParsedCommands(commandsTokens);
    }

    private static ArrayList<ArrayList<CommandToken>> splitCommands(List<CommandToken> tokensByQuote) {
        ArrayList<ArrayList<CommandToken>> commandsTokens = new ArrayList<ArrayList<CommandToken>>(tokensByQuote.size());
        commandsTokens.add(new ArrayList<CommandToken>());
        for (int i = 0; i < tokensByQuote.size(); ++i) {
            if (tokensByQuote.get(i).isWasInQuotes()) {
                commandsTokens.get(commandsTokens.size() - 1).add(tokensByQuote.get(i));
            } else {
                String[] tokens = tokensByQuote.get(i).getValue().split(";", -1);
                for (int j = 0; j < tokens.length; ++j) {
                    if (j > 0)
                        commandsTokens.add(new ArrayList<CommandToken>());
                    commandsTokens.get(commandsTokens.size() - 1).add(new CommandToken(tokens[j], false));
                }
            }
        }
        return commandsTokens;
    }

    private static List<CommandToken> splitCommandByQuote(String input) throws CannotParseCommandException {
        String[] tokensByQuote = input.split(" \"", -1);
        List<CommandToken> result = new ArrayList<CommandToken>();
        if (tokensByQuote[0].split("\" ", -1).length != 1)
            throw new CannotParseCommandException(WrongQuoteStructureMessage);
        result.add(new CommandToken(tokensByQuote[0], false));
        for (int i = 1; i < tokensByQuote.length; ++i) {
            String[] parts = tokensByQuote[i].split("\"[; ]", -1);
            if (parts.length != 2) {
                throw new CannotParseCommandException(WrongQuoteStructureMessage);
            }
            result.add(new CommandToken(parts[0], true));
            if (tokensByQuote[i].charAt(parts[0].length() + 1) == ';')
                parts[1] = ';' + parts[1];
            if (!parts[1].equals(""))
                result.add(new CommandToken(parts[1], false));
        }
        return result;
    }

    private static List<ParsedCommand> getParsedCommands(ArrayList<ArrayList<CommandToken>> commandsTokens) {
        List<ParsedCommand> commands = new ArrayList<ParsedCommand>(commandsTokens.size());
        for (ArrayList<CommandToken> currentCommand : commandsTokens) {
            ArrayList<String> arguments = splitArguments(currentCommand);
            ParsedCommand result = new ParsedCommand();
            result.setCommandName(((arguments.size() > 0) ? arguments.get(0) : null));
            String[] realArguments = new String[(arguments.size() > 0) ? arguments.size() - 1 : 0];
            for (int i = 1; i < arguments.size(); ++i)
                realArguments[i - 1] = arguments.get(i);
            result.setArguments(realArguments);
            commands.add(result);
        }
        System.out.println();

        //TODO Remove
        Log.println("--------------------");
        for (int i = 0; i < commands.size(); i++) {
            ParsedCommand command = commands.get(i);
            Log.print(command.getCommandName() + " | ");
            for (String str : command.getArguments()) {
                Log.print("'" + str + "' ");
            }
            Log.println();
        }

        return commands;
    }

    private static ArrayList<String> splitArguments(ArrayList<CommandToken> currentCommand) {
        ArrayList<String> arguments = new ArrayList<String>();
        for (CommandToken token : currentCommand) {
            if (token.isWasInQuotes()) {
                arguments.add(token.getValue());
            } else {
                arguments.addAll(Arrays.asList(token.getValue().split(" ", -1)));
            }
        }
        while (arguments.size() > 0 && (arguments.get(arguments.size() - 1).equals(""))) {
            arguments.remove(arguments.size() - 1);
        }
        int trimStartIndex = 0;
        while (trimStartIndex < arguments.size() && (arguments.get(trimStartIndex).equals("")))
            ++trimStartIndex;
        for (int i = trimStartIndex; i < arguments.size(); ++i)
            arguments.set(i - trimStartIndex, arguments.get(i));
        for (int i = 0; i < trimStartIndex; ++i)
            arguments.remove(arguments.size() - 1);
        return arguments;
    }

    public boolean isCorrectTerminated() {
        return hasCorrectTerminated;
    }

    public boolean invokeCommand(String input) {
        try {
            List<ParsedCommand> commands = parseCommand(input);
            //return invokeCommands(commands);
            return false;//TODO
        } catch (ShellException ex) {
            System.err.println("Error: " + ex.getMessage());
            return false;
        }
    }

    private boolean invokeCommands(List<ParsedCommand> commands) throws ShellException {
        try {
            for (ParsedCommand command : commands) {
                if (command.getCommandName() != null) {
                    //command.getCommandName()!=null -> empty command - ignored
                    CommandNames commandType = CommandNames.getCommand(command.getCommandName());
                    switch (commandType) {
                        case CMD_LS:
                            new CommandLs().invoke();
                            break;
                        case CMD_PWD:
                            new CommandPwd().invoke();
                            break;
                        case CMD_EXIT:
                            this.hasCorrectTerminated = true;
                            return false;
                        case CMD_CD:
                            new CommandCd(command.getArguments()).invoke();
                            break;
                        case CMD_CAT:
                            new CommandCat(command.getArguments()).invoke();
                            break;
                        case CMD_MKDIR:
                            new CommandMkdir(command.getArguments()).invoke();
                            break;
                    }
                }
            }
        } catch (CommandInvokeException ex) {
            System.err.println(new StringBuilder().append(ex.commandName).append(": ") + ex.getMessage());
        }
        return true;
    }

    private enum CommandNames {
        CMD_LS("ls"),
        CMD_PWD("pwd"),
        CMD_CD("cd"),
        CMD_CAT("cat"),
        CMD_MKDIR("mkdir"),
        CMD_EXIT("exit");
        private String typeValue;

        private CommandNames(String type) {
            typeValue = type;
        }

        static public CommandNames getCommand(String pType) throws CannotParseCommandException {
            for (CommandNames type : CommandNames.values()) {
                if (type.getName().equals(pType)) {
                    return type;
                }
            }
            throw new CannotParseCommandException("Unknown command");
        }

        public String getName() {
            return typeValue;
        }

    }

    static private class ParsedCommand {
        private String[] Arguments;
        private String CommandName;

        public String getCommandName() {
            return CommandName;
        }

        public void setCommandName(String commandName) {
            CommandName = commandName;
        }

        public String[] getArguments() {
            return Arguments;
        }

        public void setArguments(String[] arguments) {
            Arguments = arguments;
        }
    }

    static private class CommandToken {
        private String value;
        private boolean wasInQuotes;

        CommandToken(String value, boolean wasInQuotes) {
            this.value = value;
            this.wasInQuotes = wasInQuotes;
        }

        String getValue() {
            return value;
        }

        boolean isWasInQuotes() {
            return wasInQuotes;
        }

    }

    private static class CannotParseCommandException extends ShellException {
        CannotParseCommandException(String message) {
            super(message);
        }
    }

    private class CommandInvokeException extends ShellException {
        private String commandName;

        CommandInvokeException(String message, String commandName) {
            super(message);
            this.commandName = commandName;
        }

        CommandInvokeException(String message, String commandName, Throwable ex) {
            super(message, ex);
            this.commandName = commandName;
        }

        public String getCommandName() {
            return commandName;
        }
    }

    abstract private class Command {
        protected final static String FileNotFoundMessage = "': No such file or directory";
        private String[] arguments;

        protected Command(String[] args) {
            arguments = args;
        }

        protected Command() {
            arguments = null;
        }

        abstract void invoke() throws CommandInvokeException;

        protected String GetFirstArgument() throws CommandInvokeException {
            if (arguments.length == 1) {
                return arguments[0];
            } else {
                throw new CommandInvokeException("Wrong arguments!", CommandNames.CMD_CD.getName());
            }
        }
    }

    private class CommandLs extends Command {
        @Override
        void invoke() throws CommandInvokeException {
            File[] files = new File(Shell.GetCurrentFolder()).listFiles();
            if (files == null) {
                throw new CommandInvokeException("Can't found current directory.", CommandNames.CMD_LS.getName());
            } else {
                for (File file : files) {
                    System.out.println(file.getName());
                }
            }
        }
    }

    private class CommandCd extends Command {
        CommandCd(String[] args) {
            super(args);
        }

        @Override
        void invoke() throws CommandInvokeException {
            String arg = GetFirstArgument();
            String currentPath = Shell.GetCurrentFolder();
            Path path = new File(currentPath).toPath();
            File newPath = path.resolve(arg).toAbsolutePath().toFile();
            if (newPath.exists() && newPath.isDirectory()) {
                try {
                    System.setProperty("user.dir", newPath.getCanonicalPath());
                } catch (IOException ex) {
                    throw new CommandInvokeException("Can't access directory \"" + arg + "\".",
                            CommandNames.CMD_CD.getName(), ex);
                }
            } else {
                throw new CommandInvokeException("'" + arg + FileNotFoundMessage,
                        CommandNames.CMD_CD.getName());
            }
        }
    }

    private class CommandCat extends Command {
        CommandCat(String[] args) {
            super(args);
        }

        @Override
        void invoke() throws CommandInvokeException {
            String filename = GetFirstArgument();
            try (FileInputStream stream = new FileInputStream(filename)) {
                try (InputStreamReader reader = new InputStreamReader(stream)) {
                    boolean canRead = true;
                    while (canRead) {
                        int symbol = reader.read();
                        if (symbol < 0) {
                            canRead = false;
                        } else {
                            System.out.print((char) symbol);
                        }
                    }
                    System.out.println();
                }
            } catch (Throwable ex) {
                throw new CommandInvokeException("'" + filename + FileNotFoundMessage, CommandNames.CMD_CAT.getName());
            }
        }
    }

    private class CommandMkdir extends Command {
        CommandMkdir(String[] args) {
            super(args);
        }

        @Override
        void invoke() throws CommandInvokeException {
            String filename = GetFirstArgument();
            try {
                Files.createDirectory(new File(Shell.GetCurrentFolder()).toPath().resolve(filename));
            } catch (Throwable ex) {
                throw new CommandInvokeException("'" + filename + FileNotFoundMessage, CommandNames.CMD_CAT.getName());
            }
        }
    }

    private class CommandPwd extends Command {
        @Override
        void invoke() throws CommandInvokeException {
            System.out.println(Shell.GetCurrentFolder());
        }
    }
}

class ShellException extends Exception {
    ShellException(String message) {
        super(message);
    }

    ShellException(String message, Throwable ex) {
        super(message, ex);
    }

}

class Log {
    static void println() {
        println("");
    }

    static void println(String line) {
        System.out.println(line);
    }

    static void print(String line) {
        System.out.print(line);
    }
}
