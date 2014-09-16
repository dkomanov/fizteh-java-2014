package ru.fizteh.fivt.students.AndrewFedorov.shell;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;

/**
 * This class contains special methods annotated with {@link ShellCommand}.<br/>
 * They are gathered by the {@link Shell} automatically.<br/>
 * Each method should be static and have such parameters: (Shell sh, String[]
 * args);<br/>
 * <br/>
 * sh - host shell from which the command is invoked.<br/>
 * args - array of arguments given to the command. args[0] is the command name.<br/>
 * 
 * @author phoenix
 * 
 */
public class Commands {
    /**
     * Handles an occured exception.
     * 
     * @param exc
     *            occured exception. If null, an {@link Exception} is
     *            constructed via {@link Exception#Exception(String)}.
     * @param message
     *            message that can be reported to user and is written to log.
     * @param reportToUser
     *            if true, message is printed to {@link System#err}.
     */
    private static void handleError(Throwable exc, String message,
	    boolean reportToUser) {
	if (reportToUser) {
	    System.err.println(message);
	}
	Log.log(Commands.class, exc, message);
	if (exc == null) {
	    exc = new Exception(message);
	}
	throw new HandledException(exc);
    }

    /**
     * Handles an occured exception.<br/>
     * Equivalent to handleError(null, message, true);
     * 
     * @param message
     *            message that can be reported to user and is written to log.
     */
    private static void handleError(String message) {
	handleError(null, message, true);
    }

    @ShellCommand(description = "\tmkdir <dir>\tcreates a new directory")
    public static void mkdir(Shell shell, String[] args) {
	if (args.length != 2) {
	    handleError("method 'mkdir' requires only one parameter: directory");
	}

	Path path = shell.getWorkingDirectory().resolve(args[1]).normalize();

	if (Files.exists(path)) {
	    handleError(String.format(
		    "mkdir: %s: file or directory already exists", args[1]));
	} else {
	    try {
		Files.createDirectory(path);
	    } catch (Exception exc) {
		handleError(exc, String.format(
			"mkdir: directory '%s' not created", path), false);
	    }
	}
    }

    @ShellCommand(description = "\thelp\tprints this help message")
    public static void help(Shell shell, String[] args) {
	if (args.length == 1) {
	    System.out
		    .println("This is a simple shell that can execute some commands");

	    // outputting each method's own manual
	    Iterator<String> methodNames = shell.getSupportedCommands();
	    while (methodNames.hasNext()) {
		System.out.println(shell.getCommandAnnotation(
			methodNames.next()).description());
	    }
	}
    }

    @ShellCommand(description = "\tcp [-r] <src> <dst>\tcopies file from <src> to <dst>. If -r option is mentioned, directory can be copied (recursively)")
    public static void cp(Shell shell, String[] args) {
	boolean recursive = false;
	Path srcName = null;
	Path dstName = null;

	for (int i = 1, len = args.length; i < len; i++) {
	    if ("-r".equals(args[i])) {
		recursive = true;
	    } else {
		if (srcName == null) {
		    srcName = Paths.get(args[i]);
		} else if (dstName == null) {
		    dstName = Paths.get(args[i]);
		} else {
		    handleError("Extra arguments given to cp; Invocation: cp [-r] <src> <dst>");
		}
	    }
	}

	if (srcName == null || dstName == null) {
	    handleError((srcName == null ? "Source" : "Destination")
		    + " path not given");
	}

	final Path sourcePath = shell.getWorkingDirectory().resolve(srcName)
		.normalize();
	final Path targetPath = shell.getWorkingDirectory().resolve(dstName)
		.normalize();

	if (!Files.exists(sourcePath)) {
	    handleError(String.format("%s: %s: No such file or directory",
		    args[0], srcName));
	}

	if (sourcePath.startsWith(targetPath)
		|| targetPath.startsWith(sourcePath)) {
	    handleError(String
		    .format("%s: Cannot %s '%s' to '%s', because one is prefix of the other",
			    args[0], "cp".equals(args[0]) ? "copy" : "move",
			    srcName, dstName));
	}

	try {
	    if (Files.isDirectory(sourcePath)) {
		if (recursive) {
		    Files.walkFileTree(sourcePath, new FileVisitor<Path>() {
			@Override
			public FileVisitResult postVisitDirectory(Path path,
				IOException exc) throws IOException {
			    if (exc == null) {
				return FileVisitResult.CONTINUE;
			    } else {
				return visitFileFailed(path, exc);
			    }
			}

			@Override
			public FileVisitResult preVisitDirectory(
				Path visitPath, BasicFileAttributes attrs)
				throws IOException {
			    try {
				Path relativePath = sourcePath
					.relativize(visitPath);
				Path destDir = targetPath.resolve(relativePath)
					.normalize();
				Log.log(Commands.class, String.format(
					"\tCopy dir '%s' -> '%s'", visitPath,
					destDir));
				if (!Files.exists(destDir)) {
				    Files.createDirectory(destDir);
				}
			    } catch (IOException exc) {
				visitFileFailed(visitPath, exc);
			    }
			    return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path visitPath,
				BasicFileAttributes attrs) throws IOException {
			    try {
				Path rel = sourcePath.relativize(visitPath);
				Path destPath = targetPath.resolve(rel)
					.normalize();
				Log.log(Commands.class, String.format(
					"\tCopy file '%s' -> '%s'", visitPath,
					destPath));
				Files.copy(visitPath, destPath,
					StandardCopyOption.REPLACE_EXISTING);

				return FileVisitResult.CONTINUE;
			    } catch (IOException exc) {
				return visitFileFailed(visitPath, exc);
			    }
			}

			@Override
			public FileVisitResult visitFileFailed(Path visitPath,
				IOException exc) throws IOException {
			    throw exc;
			}
		    });
		} else {
		    // cannot occur when moving
		    handleError(String.format(
			    "cp: %s is a directory (not copied).", srcName));
		}
	    } else {
		Files.copy(sourcePath, targetPath,
			StandardCopyOption.REPLACE_EXISTING);
	    }
	} catch (HandledException exc) {
	    throw exc;
	} catch (Exception exc) {
	    handleError(exc, String.format("%s: cannot %s '%s' to '%s'",
		    args[0], "cp".equals(args[0]) ? "copy" : "move", srcName,
		    dstName), true);
	}
    }

    @ShellCommand(description = "\tls\tprints current directory contents")
    public static void ls(Shell shell, String[] args) {
	if (args.length > 1) {
	    handleError("method 'ls' requires no arguments");
	}

	Path home = shell.getWorkingDirectory();

	try (DirectoryStream<Path> dirstream = Files.newDirectoryStream(home)) {
	    Iterator<Path> paths = dirstream.iterator();
	    while (paths.hasNext()) {
		Path path = paths.next();
		System.out.println(home.relativize(path));
	    }
	} catch (Exception exc) {
	    handleError(exc, "Could not list directory " + home, false);
	}
    }

    @ShellCommand(description = "\tmv <src> <dst>\tmoves/renames file (file tree) under <src> to <dst>. Actually this command is combination of 'cp' and 'rm'")
    public static void mv(Shell shell, String[] args) {
	String src = null, dst = null;

	for (int i = 1, len = args.length; i < len; i++) {
	    if (src == null) {
		src = args[i];
	    } else if (dst == null) {
		dst = args[i];
	    } else {
		handleError("mv: too many parameters given");
	    }
	}

	cp(shell, new String[] { "mv", "-r", src, dst });
	rm(shell, new String[] { "rm", "-r", src });

    }

    @ShellCommand(description = "\trm [-r] <path>\tdeletes file located at <path>. If -r option is mentioned, directory subtree will be removed (recursively)")
    public static void rm(Shell shell, String[] args) {
	boolean recursive = false;
	Path removeName = null;

	for (int i = 1, len = args.length; i < len; i++) {
	    if ("-r".equals(args[i])) {
		recursive = true;
	    } else if (removeName == null) {
		removeName = Paths.get(args[i]);
	    } else {
		handleError("Extra arguments given to rm; Invocation: rm [-r] <path>");
	    }
	}

	if (removeName == null) {
	    handleError("rm: path not given");
	}

	final Path removePath = shell.getWorkingDirectory().resolve(removeName)
		.normalize();

	try {
	    if (Files.isDirectory(removePath)) {
		if (recursive) {
		    Files.walkFileTree(removePath, new FileVisitor<Path>() {
			@Override
			public FileVisitResult postVisitDirectory(Path path,
				IOException exc) throws IOException {
			    if (exc != null) {
				return visitFileFailed(path, exc);
			    }
			    try {
				Files.delete(path);
			    } catch (IOException ex) {
				return visitFileFailed(path, ex);
			    }
			    return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult preVisitDirectory(
				Path visitPath, BasicFileAttributes arg1)
				throws IOException {
			    return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path visitPath,
				BasicFileAttributes arg1) throws IOException {
			    try {
				Files.delete(visitPath);
				return FileVisitResult.CONTINUE;
			    } catch (IOException exc) {
				return visitFileFailed(visitPath, exc);
			    }
			}

			@Override
			public FileVisitResult visitFileFailed(Path visitPath,
				IOException exc) throws IOException {
			    if (exc == null) {
				return FileVisitResult.CONTINUE;
			    } else {
				throw exc;
			    }
			}
		    });
		} else {
		    handleError(String.format("rm: %s: is a directory",
			    removeName));
		}
	    } else {
		Files.delete(removePath);
	    }
	} catch (HandledException exc) {
	    throw exc;
	} catch (Exception exc) {
	    handleError(exc, String.format(
		    "rm: cannot remove '%s': No such file or directory",
		    removeName), true);
	}
    }

    @ShellCommand(description = "\tcat\toutputs file contents")
    public static void cat(Shell shell, String[] args) {
	if (args.length != 2) {
	    handleError("Invalid arguments count; Invocation: cat <path>");
	}

	Path catName = Paths.get(args[1]);
	Path catPath = shell.getWorkingDirectory().resolve(catName).normalize();

	if (Files.isDirectory(catPath)) {
	    handleError(String.format("cat: %s: Is a directory", catName));
	} else {
	    try (BufferedReader reader = new BufferedReader(new FileReader(
		    catPath.toString()), shell.getReadBufferSize())) {
		while (reader.ready()) {
		    String s = reader.readLine();
		    System.out.println(s);
		}
	    } catch (Exception exc) {
		handleError(String.format("cat: %s: No such file or directory",
			catName));
	    }
	}
    }

    @ShellCommand(description = "\texit\tcloses this shell")
    public static void exit(Shell shell, String[] args) {
	if (args.length != 1) {
	    handleError("Invalid arguments count; Invocation: exit");
	}

	Log.log("Shell exitting");
	Log.close();
	System.exit(0);
    }

    @ShellCommand(description = "\tpwd\tprints working directory")
    public static void pwd(Shell shell, String[] args) {
	if (args.length != 1) {
	    handleError("Invalid arguments count; Invocation: pwd");
	}
	System.out.println(shell.getWorkingDirectory());
    }

    @ShellCommand(description = "\tcd <dir>\tchange working directory")
    public static void cd(Shell shell, String[] args) {
	if (args.length != 2) {
	    handleError("Invalid arguments count; Invocation: cd <dir>");
	}
	Path dir = shell.getWorkingDirectory();
	Path newdir = dir.resolve(args[1]).normalize();

	if (Files.isDirectory(newdir)) {
	    shell.setWorkingDirectory(newdir);
	    Log.log(Commands.class, "Set working dir: " + newdir);
	} else if (Files.exists(newdir)) {
	    handleError(String.format("cd: %s: Is not a directory", newdir));
	} else {
	    handleError(String.format("cd: %s: No such file or directory",
		    newdir));
	}
    }
}
