package ru.fizteh.fivt.students.Oktosha.Shell;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

import ru.fizteh.fivt.students.Oktosha.Command.Command;
import ru.fizteh.fivt.students.Oktosha.ConsoleUtility.CommandArgumentSyntaxException;
import ru.fizteh.fivt.students.Oktosha.ConsoleUtility.CommandIsNotSupportedException;
import ru.fizteh.fivt.students.Oktosha.ConsoleUtility.ConsoleUtility;
import ru.fizteh.fivt.students.Oktosha.ConsoleUtility.ConsoleUtilityRuntimeException;
import ru.fizteh.fivt.students.Oktosha.Executor.InteractiveExecutor;
import ru.fizteh.fivt.students.Oktosha.Executor.PackageExecutor;

public class Shell implements ConsoleUtility {

    private Path workingDirectory;

    Shell() {
        workingDirectory = Paths.get(System.getProperty("user.dir"));
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            InteractiveExecutor.execute(new Shell());
        } else {
            PackageExecutor.execute(new Shell(), args);
        }
    }

    public void run(Command cmd) throws CommandIsNotSupportedException,
                                        CommandArgumentSyntaxException,
                                        ConsoleUtilityRuntimeException {
        switch (cmd.name) {
            case "cd":
                cd(cmd.args);
                break;
            case "exit":
                exit(cmd.args);
                break;
            case "pwd":
                pwd(cmd.args);
                break;
            case "":
                break;
            default:
                throw new CommandIsNotSupportedException(cmd.name + ": command isn't supported");
        }
    }

    private void exit(String[] args) throws CommandArgumentSyntaxException {
        if (args.length != 0) {
            throw new CommandArgumentSyntaxException("exit: too many arguments");
        }
        System.exit(0);
    }

    private void pwd(String[] args) throws CommandArgumentSyntaxException {
        if (args.length != 0) {
            throw new CommandArgumentSyntaxException("pwd: too many arguments");
        }
        System.out.println(workingDirectory);
    }

    private void cd(String[] args) throws  CommandArgumentSyntaxException,
                                           ConsoleUtilityRuntimeException {
        if (args.length != 1) {
            throw new CommandArgumentSyntaxException("cd: expected 1 argument, found " + args.length);
        }
        try {
            Path destinationPath = Paths.get(args[0]);
            Path newWorkingDirectory = workingDirectory.resolve(destinationPath);
            newWorkingDirectory = newWorkingDirectory.toRealPath();
            if (!Files.isDirectory(newWorkingDirectory)) {
                throw new ConsoleUtilityRuntimeException("cd: '" + args[0] + "': Not a directory");
            }
            workingDirectory = newWorkingDirectory;
        } catch (InvalidPathException e) {
            throw new ConsoleUtilityRuntimeException("cd: '" + args[0] + "': Invalid path", e);
        } catch (IOException e) {
            throw new ConsoleUtilityRuntimeException("cd: '" + args[0] + "': No such file or directory", e);
        }
    }
}
