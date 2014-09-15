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
    @ShellCommand(description = "\tmkdir <dir>\tcreates a new directory")
    public static void mkdir(Shell shell, String[] args) {
	if (args.length != 2) {
	    throw new IllegalArgumentException("method 'mkdir' requires only one parameter: directory");
	}
	
	Path path = shell.getWorkingDirectory().resolve(args[1]);
	
	try {
	    Files.createDirectory(path);
	}
	catch (Exception exc) {
	    Log.log(Commands.class, exc, "Failed to create a new directory: " + path);
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
    public static boolean cp(Shell shell, String[] args) {
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
		    throw new IllegalArgumentException(
			    "Extra arguments given to cp; Invocation: cp [-r] <src> <dst>");
		}
	    }
	}

	if (srcName == null || dstName == null) {
	    throw new IllegalArgumentException((srcName == null ? "Source"
		    : "Destination") + " path not given");
	}

	final Path sourcePath = shell.getWorkingDirectory().resolve(srcName);
	final Path targetPath = shell.getWorkingDirectory().resolve(dstName);
		
	try {
	    if (Files.isDirectory(sourcePath)) {
		if (recursive) {
		    Files.walkFileTree(sourcePath, new FileVisitor<Path>() {
			@Override
			public FileVisitResult postVisitDirectory(Path path,
				IOException exc) throws IOException {
			    return visitFileFailed(path, exc);
			}

			@Override
			public FileVisitResult preVisitDirectory(
				Path visitPath, BasicFileAttributes attrs) throws IOException {
			    try {
				Path relativePath = sourcePath.relativize(visitPath);
				Path destDir = targetPath.resolve(relativePath);
				Log.log(Commands.class, String.format("\tCopy dir '%s' -> '%s'",
					visitPath, destDir));
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
				Path destPath = targetPath.resolve(rel);
				Log.log(Commands.class, String.format("\tCopy file '%s' -> '%s'",
					visitPath, destPath));
				Files.copy(visitPath, destPath);

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
			    }
			    else {
				throw exc;
			    }
			}
		    });
		} else {
		    Log.log(Commands.class, null, String.format("Attempt to copy %s -> %s without -r option", srcName, dstName));
		    System.err.println(String.format("cp: %s is a directory (not copied).", srcName));
		    return false;
		}
	    } else {
		Files.copy(sourcePath, targetPath);
	    }
	    
	    return true;
	} catch (Exception exc) {
	    Log.log(Commands.class, exc, String.format("Failed to copy files from %s to %s", sourcePath, targetPath));
	    System.err.println(String.format("cp: cannot copy '%s' to '%s'", srcName, dstName));
	    return false;
	}
    }

    @ShellCommand(description = "\tls\tprints current directory contents")
    public static void ls(Shell shell, String[] args) {
	if (args.length > 1) {
	    throw new IllegalArgumentException(
		    "method 'ls' requires no arguments");
	}

	Path home = shell.getWorkingDirectory();

	try (DirectoryStream<Path> dirstream = Files.newDirectoryStream(home)) {
	    Iterator<Path> paths = dirstream.iterator();
	    while (paths.hasNext()) {
		Path path = paths.next();
		System.out.println(home.relativize(path));
	    }
	} catch (Exception exc) {
	    Log.log(Commands.class, exc, "Could not list directory " + home);
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
		Log.log(Commands.class, null,
			"Too many path parameters given to mv");
		System.err.println("Too many path parameters given to mv");
	    }
	}
	
	if (cp(shell, new String[] { "cp", "-r", src, dst })) {
	    rm(shell, new String[] { "rm", "-r", src });
	}
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
		Log.log(Commands.class, null, "Duplicate path argument for rm");
		System.err
			.println("Bad arguments given to rm. Invocation example: rm [-r] <path>");
	    }
	}

	if (removeName == null) {
	    throw new IllegalArgumentException("Path missing");
	}

	final Path removePath = shell.getWorkingDirectory().resolve(removeName);

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
				return visitFileFailed(path, exc);
			    }
			    return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult preVisitDirectory(
				Path visitPath, BasicFileAttributes arg1) throws IOException {
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
			    }
			    else {
				throw exc;
			    }
			}
		    });
		} else {
		    Log.log(Commands.class, null, String.format("Attempt to remove directory '%s' without -r option", removePath));
		    System.err.println(String.format("rm: %s: is a directory", removeName));
		}
	    } else {
		Files.delete(removePath);
	    }
	} catch (Exception exc) {
	    Log.log(Commands.class, exc, "Failed to remove files from " + removePath);
	    System.err.println(String.format("rm: cannot remove '%s': No such file or directory", removeName));
	}
    }

    @ShellCommand(description = "\tcat\toutputs file contents")
    public static void cat(Shell shell, String[] args) {
	if (args.length != 2) {
	    throw new IllegalArgumentException(
		    "Method 'cat' requires only 1 argument: path to file");
	}

	Path catName = Paths.get(args[1]);
	Path catPath = shell.getWorkingDirectory().resolve(catName);

	try (BufferedReader reader = new BufferedReader(new FileReader(
		catPath.toString()), shell.getReadBufferSize())) {
	    while (reader.ready()) {
		String s = reader.readLine();
		System.out.println(s);
	    }
	} catch (Exception exc) {
	    Log.log(Commands.class, exc, "Failed to cat file: " + catPath);
	    System.err.println(String.format("cat: %s: No such file or directory", catName));
	}
    }

    @ShellCommand(description = "\texit\tcloses this shell")
    public static void exit(Shell shell, String[] args) {
	if (args.length != 1) {
	    throw new IllegalArgumentException(
		    "method 'exit' requires no arguments");
	}

	Log.log("Shell exitting");
	Log.close();
	System.exit(0);
    }

    @ShellCommand(description = "\tpwd\tprints working directory")
    public static void pwd(Shell shell, String[] args) {
	if (args.length != 1) {
	    throw new IllegalArgumentException(
		    "method 'pwd' requires no arguments");
	}
	System.out.println(shell.getWorkingDirectory());
    }

    @ShellCommand(description = "\tcd <dir>\tchange working directory")
    public static void cd(Shell shell, String[] args) {
	if (args.length != 2) {
	    throw new IllegalArgumentException(
		    "method 'cd' requires one argument");
	}
	Path dir = shell.getWorkingDirectory();
	Path newdir = dir.resolve(args[1]);
	if (Files.exists(newdir) && Files.isDirectory(newdir)) {
	    shell.setWorkingDirectory(newdir);
	    Log.log(Commands.class, "Set working dir: " + newdir);
	} else {
	    Log.log(Commands.class, String.format(
		    "Path does not exist or is not a directory: %s", newdir));
	    System.err.println(String.format("cd: '%s': No such file or directory", args[1]));
	}
    }
}
