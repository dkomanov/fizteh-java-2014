package ru.fizteh.fivt.students.LebedevAleksey.Shell01;

import java.io.*;
import java.nio.file.*;
import java.util.List;

public class Shell extends CommandParser {
    private String currentFolder;

    public Shell() {
        currentFolder = System.getProperty("user.dir");
    }

    String getCurrentFolder() {
        return currentFolder;
    }

    @Override
    protected boolean invokeCommands(List<ParsedCommand> commands) throws ParserException {
        for (ParsedCommand command : commands) {
            if (command.getCommandName() != null) {
                //command.getCommandName()!=null -> empty command - ignored
                CommandNames commandType = CommandNames.getCommand(command.getCommandName());
                Command action;
                switch (commandType) {
                    case CMD_LS:
                        action = new CommandLs(command.getArguments());
                        break;
                    case CMD_PWD:
                        action = new CommandPwd(command.getArguments());
                        break;
                    case CMD_EXIT:
                        return exit();
                    case CMD_CD:
                        action = new CommandCd(command.getArguments());
                        break;
                    case CMD_CAT:
                        action = new CommandCat(command.getArguments());
                        break;
                    case CMD_MKDIR:
                        action = new CommandMkdir(command.getArguments());
                        break;
                    case CMD_MV:
                        action = new CommandMv(command.getArguments());
                        break;
                    case CMD_CP:
                        action = new CommandCp(command.getArguments());
                        break;
                    case CMD_RM:
                        action = new CommandRm(command.getArguments());
                        break;
                    default:
                        throw new CannotParseCommandException("Unknown command");
                }
                action.invoke();
            }
        }
        return true;
    }

    private File getCurrentFolderAsFile() {
        return new File(getCurrentFolder());
    }

    private Path getCurrentFolderAsPath() {
        return getCurrentFolderAsFile().toPath();
    }

    private enum CommandNames {
        CMD_LS("ls"),
        CMD_PWD("pwd"),
        CMD_CD("cd"),
        CMD_CAT("cat"),
        CMD_MKDIR("mkdir"),
        CMD_MV("mv"),
        CMD_CP("cp"),
        CMD_RM("rm"),
        CMD_EXIT("exit");
        private String typeValue;

        private CommandNames(String type) {
            typeValue = type;
        }

        public static CommandNames getCommand(String pType) throws CannotParseCommandException {
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

    private abstract class Command {
        public static final String WRONG_ARGUMENTS = "Wrong arguments!";
        protected static final String FILE_NOT_FOUND_MESSAGE = "': No such file or directory";
        protected static final String ACCESS_DENIED_ERROR = "': Can't open file or directory";
        private String[] arguments;

        protected Command(String[] args) {
            arguments = args;
        }

        protected Command() {
            arguments = null;
        }

        protected String[] getArguments() {
            return arguments;
        }

        abstract void invoke() throws CommandInvokeException;

        protected String getFirstArgument() throws CommandInvokeException {
            if (arguments.length == 1) {
                return arguments[0];
            } else {
                throw new CommandInvokeException(WRONG_ARGUMENTS, CommandNames.CMD_CD.getName());
            }
        }

        protected void assertNoArgs(String[] args, String command) throws CommandInvokeException {
            if (args.length > 0) {
                throw new CommandInvokeException("This command have no arguments.", command);
            }
        }

        protected String resolve(String filename) {
            return getCurrentFolderAsPath().resolve(filename).
                    toAbsolutePath().toFile().getAbsolutePath();
        }

        protected Path resolvePath(String filename, CommandNames name) throws CommandInvokeException {
            try {
                return getCurrentFolderAsPath().resolve(filename);
            } catch (InvalidPathException ex) {
                throw new CommandInvokeException("invalid path", name.getName(), ex);
            }
        }

        protected void get2or3Args(String commandName, ResultOfGetting2Args result) throws CommandInvokeException {
            if (arguments.length < 2) {
                throw new CommandInvokeException(WRONG_ARGUMENTS, commandName);
            } else {
                int changeIndex = 0;
                if (arguments[0].equals("-r")) {
                    if (arguments.length != 3) {
                        throw new CommandInvokeException(WRONG_ARGUMENTS, commandName);
                    }
                    changeIndex = 1;
                    result.setRecursive(true);
                } else {
                    if (arguments.length != 2) {
                        throw new CommandInvokeException(WRONG_ARGUMENTS, commandName);
                    }
                    result.setRecursive(false);
                }
                result.setArg1(arguments[changeIndex]);
                result.setArg2(arguments[changeIndex + 1]);
            }
        }
    }

    private class CommandLs extends Command {
        CommandLs(String[] args) throws CommandInvokeException {
            super(args);
            assertNoArgs(args, CommandNames.CMD_LS.getName());
        }

        @Override
        void invoke() throws CommandInvokeException {
            File[] files = getCurrentFolderAsFile().listFiles();
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
            String arg = getFirstArgument();
            try {
                Path path = getCurrentFolderAsPath();
                File newPath = path.resolve(arg).toAbsolutePath().toFile();
                if (newPath.exists() && newPath.isDirectory()) {
                    currentFolder = newPath.getCanonicalPath();
                } else {
                    throw new CommandInvokeException("'" + arg + FILE_NOT_FOUND_MESSAGE,
                            CommandNames.CMD_CD.getName());
                }
            } catch (SecurityException | IOError ex) {
                throw new CommandInvokeException("'" + arg + ACCESS_DENIED_ERROR, CommandNames.CMD_CD.getName(), ex);
            } catch (InvalidPathException | UnsupportedOperationException | IOException ex) {
                throw new CommandInvokeException("'" + arg + FILE_NOT_FOUND_MESSAGE, CommandNames.CMD_CD.getName(), ex);
            }

        }
    }

    private class CommandCat extends Command {
        CommandCat(String[] args) {
            super(args);
        }

        @Override
        void invoke() throws CommandInvokeException {
            String filename = getFirstArgument();
            try (FileInputStream stream = new FileInputStream(resolve(filename))) {
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
                throw new CommandInvokeException("'" + filename + FILE_NOT_FOUND_MESSAGE,
                        CommandNames.CMD_CAT.getName(), ex);
            }
        }
    }


    private class CommandMkdir extends Command {
        CommandMkdir(String[] args) {
            super(args);
        }

        @Override
        void invoke() throws CommandInvokeException {
            String filename = getFirstArgument();
            try {
                Files.createDirectory(resolvePath(filename, CommandNames.CMD_MKDIR));
            } catch (FileAlreadyExistsException ex) {
                throw new CommandInvokeException("Can't create directory '" + filename + "': it already exists",
                        CommandNames.CMD_CAT.getName(), ex);
            } catch (SecurityException ex) {
                throw new CommandInvokeException("Can't create directory '" + filename + "': access denied",
                        CommandNames.CMD_CAT.getName(), ex);
            } catch (IOException | UnsupportedOperationException ex) {
                throw new CommandInvokeException("Can't create directory '" + filename + "': access denied",
                        CommandNames.CMD_CAT.getName(), ex);
            }
        }
    }

    private class CommandPwd extends Command {
        CommandPwd(String[] args) throws CommandInvokeException {
            super(args);
            assertNoArgs(args, CommandNames.CMD_PWD.getName());
        }

        @Override
        void invoke() throws CommandInvokeException {
            System.out.println(getCurrentFolder());
        }
    }

    private class CommandMv extends Command {

        CommandMv(String[] args) {
            super(args);
        }

        @Override
        void invoke() throws CommandInvokeException {
            String[] args = getArguments();
            if (args.length == 2) {
                try {
                    Files.move(resolvePath(args[0], CommandNames.CMD_MV), resolvePath(args[1], CommandNames.CMD_MV),
                            StandardCopyOption.ATOMIC_MOVE);
                } catch (FileAlreadyExistsException | DirectoryNotEmptyException ex) {
                    throw new CommandInvokeException("Can't move to '" + args[1] + "': wrong target"
                            , CommandNames.CMD_MV.getName(), ex);
                } catch (SecurityException ex) {
                    throw new CommandInvokeException("Can't move: access denied", CommandNames.CMD_MV.getName(), ex);
                } catch (UnsupportedOperationException | IOException ex) {
                    throw new CommandInvokeException("Can't move " + ex.getMessage(), CommandNames.CMD_MV.getName(),
                            ex);
                }
            } else {
                throw new CommandInvokeException(WRONG_ARGUMENTS, CommandNames.CMD_MV.getName());
            }
        }
    }

    private class CommandCp extends Command {
        CommandCp(String[] args) {
            super(args);
        }

        @Override
        void invoke() throws CommandInvokeException {
            ResultOfGetting2Args args = new ResultOfGetting2Args();
            get2or3Args(CommandNames.CMD_CP.getName(), args);
            try {
                Path source = resolvePath(args.getArg1(), CommandNames.CMD_CP);
                Path target = resolvePath(args.getArg2(), CommandNames.CMD_CP);
                if (source.toFile().exists()) {
                    if (target.toFile().exists()) {
                        throw new CommandInvokeException("'" + target + "' already exists",
                                CommandNames.CMD_CP.getName());
                    } else {
                        boolean canCopy = ((!source.toFile().isDirectory()) || args.isRecursive());
                        if (canCopy) {
                            copy(args, source.toFile(), target);
                        } else {
                            throw new CommandInvokeException("'" + source + "' is a directory (not copied).",
                                    CommandNames.CMD_CP.getName());
                        }
                    }
                } else {
                    throw new CommandInvokeException("'" + source + FILE_NOT_FOUND_MESSAGE,
                            CommandNames.CMD_CP.getName());
                }
            } catch (SecurityException ex) {
                throw new CommandInvokeException("Can't copy: access denied", CommandNames.CMD_CP.getName(), ex);
            }
        }

        private void copy(ResultOfGetting2Args args, File source, Path target) throws CommandInvokeException {
            try {
                if (source.isDirectory()) {
                    Files.createDirectory(target);
                    File[] files = source.listFiles();
                    if (files == null) {
                        throw new CommandInvokeException("'" + source + "': can't copy.",
                                CommandNames.CMD_CP.getName());
                    } else {
                        for (File file : files) {
                            copy(args, file, target.resolve(file.getName()));
                        }
                    }
                } else {
                    Files.copy(source.toPath(), target, StandardCopyOption.COPY_ATTRIBUTES);
                }
            } catch (FileAlreadyExistsException | DirectoryNotEmptyException ex) {
                throw new CommandInvokeException("Can't copy to '" + args.getArg2() + "': wrong target"
                        , CommandNames.CMD_CP.getName(), ex);
            } catch (SecurityException ex) {
                throw new CommandInvokeException("Can't copy: access denied", CommandNames.CMD_CP.getName(), ex);
            } catch (UnsupportedOperationException | IOException ex) {
                throw new CommandInvokeException("Can't copy " + ex.getMessage(), CommandNames.CMD_CP.getName(),
                        ex);
            }
        }
    }

    private class CommandRm extends Command {
        CommandRm(String[] args) {
            super(args);
        }

        @Override
        void invoke() throws CommandInvokeException {
            String[] args = getArguments();
            String path;
            boolean recursive = false;
            if (args.length == 1 && (!args[0].equals("-r"))) {
                path = args[0];
            } else {
                if (args.length == 2 && args[0].equals("-r")) {
                    path = args[1];
                    recursive = true;
                } else {
                    throw new CommandInvokeException(WRONG_ARGUMENTS, CommandNames.CMD_MV.getName());
                }
            }
            try {
                Path source = resolvePath(path, CommandNames.CMD_RM);
                if (source.toFile().exists()) {
                    boolean canRemove = ((!source.toFile().isDirectory()) || recursive);
                    if (canRemove) {
                        remove(source.toFile());
                    } else {
                        throw new CommandInvokeException("'" + source + "' is a directory (not removed).",
                                CommandNames.CMD_RM.getName());
                    }
                } else {
                    throw new CommandInvokeException("'" + source + FILE_NOT_FOUND_MESSAGE,
                            CommandNames.CMD_RM.getName());
                }
            } catch (SecurityException ex) {
                throw new CommandInvokeException("Can't remove: access denied", CommandNames.CMD_RM.getName(), ex);
            }
        }

        private void remove(File source) throws CommandInvokeException {
            try {
                if (source.isDirectory()) {
                    File[] files = source.listFiles();
                    if (files == null) {
                        throw new CommandInvokeException("'" + source + "': can't remove ",
                                CommandNames.CMD_RM.getName());
                    } else {
                        for (File file : files) {
                            remove(file);
                        }
                    }
                }
                if (!source.delete()) {
                    throw new CommandInvokeException("'" + source + "': can't remove ", CommandNames.CMD_RM.getName());
                }
            } catch (SecurityException ex) {
                throw new CommandInvokeException("Can't remove: access denied", CommandNames.CMD_RM.getName(), ex);
            } catch (UnsupportedOperationException ex) {
                throw new CommandInvokeException("Can't remove " + ex.getMessage(), CommandNames.CMD_RM.getName(),
                        ex);
            }
        }
    }
}

class ResultOfGetting2Args {
    private String arg1;
    private String arg2;
    private boolean recursive;

    public String getArg1() {
        return arg1;
    }

    public void setArg1(String arg1) {
        this.arg1 = arg1;
    }

    public String getArg2() {
        return arg2;
    }

    public void setArg2(String arg2) {
        this.arg2 = arg2;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }
}
