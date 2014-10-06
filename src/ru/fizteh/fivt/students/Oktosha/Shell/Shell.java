package ru.fizteh.fivt.students.Oktosha.Shell;

import static java.nio.file.StandardCopyOption.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;

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
            case "cat":
                cat(cmd.args);
                break;
            case "cd":
                cd(cmd.args);
                break;
            case "cp":
                cp(cmd.args);
                break;
            case "exit":
                exit(cmd.args);
                break;
            case "ls":
                ls(cmd.args);
                break;
            case "mkdir":
                mkdir(cmd.args);
                break;
            case "mv":
                mv(cmd.args);
                break;
            case "pwd":
                pwd(cmd.args);
                break;
            case "rm":
                rm(cmd.args);
                break;
            case "":
                break;
            default:
                throw new CommandIsNotSupportedException(cmd.name + ": command isn't supported");
        }
    }

    private void cat(String[] args) throws CommandArgumentSyntaxException,
                                           ConsoleUtilityRuntimeException {
        if (args.length != 1) {
            throw new CommandArgumentSyntaxException("cat: expected 1 argument, found " + args.length);
        }
        try {
            Path filePath = workingDirectory.resolve(Paths.get(args[0])).toRealPath();
            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (AccessDeniedException e) {
                throw new ConsoleUtilityRuntimeException("cat: '" + args[0] + "': Permission denied", e);
            } catch (IOException e) {
                throw new ConsoleUtilityRuntimeException("cat: something really bad happened " + e.toString(), e);
            }
        } catch (IOException e) {
            throw new ConsoleUtilityRuntimeException("cat: " + args[0] + "': No such file or directory", e);
        }
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
        } catch (AccessDeniedException e) {
            throw new ConsoleUtilityRuntimeException("cd: '" + args[0] + "': Permission denied", e);
        } catch (NoSuchFileException | InvalidPathException e) {
            throw new ConsoleUtilityRuntimeException("cd: '" + args[0] + "': No such file or directory", e);
        } catch (IOException e) {
            throw new ConsoleUtilityRuntimeException("cd: something really bad happened " + e.toString(), e);
        }
    }

    private void cp(String[] args) throws CommandArgumentSyntaxException, ConsoleUtilityRuntimeException {
        if ((args.length < 2) || (args.length > 3) || ((args.length == 3) && !(args[0].equals("-r")))) {
            throw new CommandArgumentSyntaxException("cp: Invalid syntax, usage: cp [-r] <source> <destination>");
        }
        boolean recursively = (args.length == 3);
        try {
            Path source = workingDirectory.resolve(Paths.get((args.length == 3) ? args[1] : args[0])).toRealPath();
            Path target = workingDirectory.resolve(Paths.get((args.length == 3) ? args[2] : args[1]));

            if (Files.exists(target) && source.equals(target.toRealPath())) {
                return;
            }

            if (Files.isDirectory(source)) {
                if (recursively) {
                    if (Files.exists(target)) {
                        if (Files.isDirectory(target)) {
                            Files.walkFileTree(source, new CpFileVisitor(source, target.resolve(source.getFileName())));
                        } else {
                            throw new ConsoleUtilityRuntimeException("cp:" + args[2] + ": Not a directory");
                        }
                    } else {
                        Files.walkFileTree(source, new CpFileVisitor(source, target));
                    }
                } else {
                    throw new ConsoleUtilityRuntimeException("cp:" + args[1] + ": Is a directory");
                }
            } else {
                if (Files.exists(target) && Files.isDirectory(target)) {
                    Files.copy(source, target.resolve(source.getFileName()), REPLACE_EXISTING);
                } else {
                    Files.copy(source, target, REPLACE_EXISTING);
                }
            }
        } catch (NoSuchFileException e) {
            throw new ConsoleUtilityRuntimeException("cp: " + e.getMessage() + ": No such file or directory", e);
        } catch (AccessDeniedException e) {
            throw new ConsoleUtilityRuntimeException("cp: " + e.getMessage() + ": Permission denied", e);
        } catch (IOException | SecurityException e) {
            throw new ConsoleUtilityRuntimeException("cp: something really bad happened " + e.toString(), e);
        }

    }

    private void exit(String[] args) throws CommandArgumentSyntaxException {
        if (args.length != 0) {
            throw new CommandArgumentSyntaxException("exit: too many arguments");
        }
        System.exit(0);
    }

    private void ls(String[] args) throws CommandArgumentSyntaxException,
                                          ConsoleUtilityRuntimeException {
        if (args.length != 0) {
            throw new CommandArgumentSyntaxException("ls: too many arguments");
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(workingDirectory)) {
            for (Path file : stream) {
                System.out.println(file.getFileName());
            }
        } catch (NoSuchFileException e) {
            throw new ConsoleUtilityRuntimeException("ls: Working directory doesn't exist", e);
        } catch (AccessDeniedException e) {
            throw new ConsoleUtilityRuntimeException("ls: Permission denied", e);
        } catch (IOException | DirectoryIteratorException e) {
            throw new ConsoleUtilityRuntimeException("ls: something really bad happened " + e.toString(), e);
        }
    }

    private void mkdir(String[] args) throws CommandArgumentSyntaxException,
                                             ConsoleUtilityRuntimeException {
        if (args.length != 1) {
            throw new CommandArgumentSyntaxException("mkdir: expected 1 argument, found " + args.length);
        }
        Path newDirectoryPath = workingDirectory.resolve(Paths.get(args[0]));
        try {
            Files.createDirectory(newDirectoryPath);
        } catch (FileAlreadyExistsException e) {
            throw new ConsoleUtilityRuntimeException("mkdir: " + args[0] + ": File exists", e);
        } catch (AccessDeniedException e) {
            throw new ConsoleUtilityRuntimeException("mkdir: " + args[0] + ": Permission denied", e);
        } catch (NoSuchFileException e) {
            throw new ConsoleUtilityRuntimeException("mkdir: "
                                                     + Paths.get(args[0]).getParent()
                                                     + ": No such file or directory", e);
        } catch (IOException | SecurityException e) {
            throw new ConsoleUtilityRuntimeException("mkdir: something really bad happened " + e.toString(), e);
        }
    }

    private void mv(String[] args) throws CommandArgumentSyntaxException, ConsoleUtilityRuntimeException {
        if (args.length != 2) {
            throw new CommandArgumentSyntaxException("mv: expected 2 arguments, found " + args.length);
        }
        try {
            Path source = workingDirectory.resolve(Paths.get(args[0])).toRealPath();
            Path target = workingDirectory.resolve(Paths.get(args[1]));

            if (Files.exists(target) && source.equals(target.toRealPath())) {
                return;
            }

            if (Files.isDirectory(source)) {
                if (Files.exists(target)) {
                    if (Files.isDirectory(target)) {
                    Files.walkFileTree(source, new MvFileVisitor(source, target.resolve(source.getFileName())));
                    } else {
                        throw new ConsoleUtilityRuntimeException("mv: rename '"
                                + args[0] + "' to '" + args[1]
                                + ": Not a directory");
                    }
                } else {
                    Files.walkFileTree(source, new MvFileVisitor(source, target));
                }
            } else {
                if (Files.exists(target) && Files.isDirectory(target)) {
                    Files.move(source, target.resolve(source.getFileName()), REPLACE_EXISTING);
                } else {
                    Files.move(source, target, REPLACE_EXISTING);
                }
            }
        } catch (NoSuchFileException e) {
            throw new ConsoleUtilityRuntimeException("mv: " + e.getMessage() + ": No such file or directory", e);
        } catch (AccessDeniedException e) {
            throw new ConsoleUtilityRuntimeException("mv: " + e.getMessage() + ": Permission denied", e);
        } catch (IOException | SecurityException e) {
            throw new ConsoleUtilityRuntimeException("mv: something really bad happened " + e.toString(), e);
        }
    }

    private void pwd(String[] args) throws CommandArgumentSyntaxException {
        if (args.length != 0) {
            throw new CommandArgumentSyntaxException("pwd: too many arguments");
        }
        System.out.println(workingDirectory);
    }

    private void rm(String[] args) throws CommandArgumentSyntaxException, ConsoleUtilityRuntimeException {
        if ((args.length == 0) || (args.length > 2) || ((args.length == 2) && !(args[0].equals("-r")))) {
            throw new CommandArgumentSyntaxException("rm: Invalid syntax, usage: rm [-r] <file|dir>");
        }
        boolean recursively = (args.length == 2);
        try {
            Path target = workingDirectory.resolve(Paths.get((args.length == 2) ? args[1] : args[0])).toRealPath();
            if (!Files.isDirectory(target)) {
                Files.delete(target);
            } else {
                if (recursively) {
                    Files.walkFileTree(target, new RmFileVisitor());
                } else {
                    throw new ConsoleUtilityRuntimeException("rm: " + args[0] + ": Is a directory");
                }
            }
        } catch (NoSuchFileException e) {
            throw new ConsoleUtilityRuntimeException("rm: " + e.getMessage() + ": No such file or directory", e);
        } catch (AccessDeniedException e) {
            throw new ConsoleUtilityRuntimeException("rm: " + e.getMessage() + ": Permission denied", e);
        } catch (IOException | SecurityException e) {
            throw new ConsoleUtilityRuntimeException("rm: something really bad happened " + e.toString(), e);
        }
    }
}
