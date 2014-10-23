package ru.fizteh.fivt.students.fedorov_andrew.multifilehashmap.support;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;

import ru.fizteh.fivt.students.fedorov_andrew.multifilehashmap.Commands;
import ru.fizteh.fivt.students.fedorov_andrew.multifilehashmap.exception.TerminalException;

public class Utility {

    /**
     * File visitor that deletes empty files and empty folders.
     * 
     * @author phoenix
     * 
     */
    public static class EmptyFilesRemover extends FileTreeRemover {
	private Path rootDirectory;

	/**
	 * 
	 * @param rootDirectory
	 *            root directory that will be never deleted
	 */
	public EmptyFilesRemover(Path rootDirectory) {
	    this.rootDirectory = rootDirectory;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path path, IOException exc)
		throws IOException {
	    if (exc != null) {
		visitFileFailed(path, exc);
	    }
	    if (path.equals(rootDirectory)) {
		return FileVisitResult.CONTINUE;
	    }
	    try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(path)) {
		Iterator<Path> pathIterator = dirStream.iterator();
		if (!pathIterator.hasNext()) {
		    return super.postVisitDirectory(path, exc);
		} else {
		    return FileVisitResult.CONTINUE;
		}
	    }
	}

	@Override
	public FileVisitResult visitFile(Path visitPath,
		BasicFileAttributes attrs) throws IOException {
	    if (Files.size(visitPath) == 0) {
		return super.visitFile(visitPath, attrs);
	    } else {
		return FileVisitResult.CONTINUE;
	    }
	}
    }

    /**
     * File visitor that deletes file subtree.<br/>
     * Symbolic links are not followed.
     * 
     * @author phoenix
     * 
     */
    public static class FileTreeRemover extends MyTreeWalker<Path> {
	@Override
	public FileVisitResult postVisitDirectory(Path path, IOException exc)
		throws IOException {
	    FileVisitResult superResult = super.postVisitDirectory(path, exc);

	    if (superResult != FileVisitResult.TERMINATE) {
		try {
		    Files.delete(path);
		} catch (IOException ex) {
		    return visitFileFailed(path, ex);
		}
		return FileVisitResult.CONTINUE;
	    } else {
		return superResult;
	    }
	}

	@Override
	public FileVisitResult visitFile(Path visitPath,
		BasicFileAttributes attrs) throws IOException {
	    try {
		Files.delete(visitPath);
		return FileVisitResult.CONTINUE;
	    } catch (IOException exc) {
		return visitFileFailed(visitPath, exc);
	    }
	}
    }

    /**
     * Partial implementation of {@link FileVisitor}, that has the following
     * piculiarities:<br/>
     * <ul>
     * <li>If an exception occurs, file tree traverse is stopped</li>
     * <li>Nothing is done before and after directory visit</li>
     * <li>{@link FileVisitor#visitFile(Object, BasicFileAttributes)} is not
     * implemented.
     * </ul>
     * 
     * @author phoenix
     * @see FileVisitor
     */
    public static abstract class MyTreeWalker<T> implements FileVisitor<T> {
	@Override
	public FileVisitResult postVisitDirectory(T path, IOException exc)
		throws IOException {
	    if (exc == null) {
		return FileVisitResult.CONTINUE;
	    } else {
		return visitFileFailed(path, exc);
	    }
	}

	@Override
	public FileVisitResult preVisitDirectory(T visitPath,
		BasicFileAttributes attrs) throws IOException {
	    return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(T visitPath, IOException exc)
		throws IOException {
	    throw exc;
	}
    }

    /**
     * Checks if {@code directory} and all its children having distance from
     * {@code directory} up to {@code depth} contain only directories
     * 
     * @param directory
     *            directory, root of local subtree
     * @param depth
     *            distance from root
     * @return link to problematic file (that is in no-files directory) or
     *         {@code null} if everything is ok.
     * @throws IOException
     */
    public static Path checkDirectoryContainsOnlyDirectories(
	    final Path directory, final int depth) throws IOException {
	class DirChecker extends MyTreeWalker<Path> {
	    Path invalidPath = null;

	    @Override
	    public FileVisitResult postVisitDirectory(Path dir, IOException exc)
		    throws IOException {
		if (exc != null) {
		    return visitFileFailed(dir, exc);
		}
		return FileVisitResult.CONTINUE;
	    }

	    @Override
	    public FileVisitResult preVisitDirectory(Path dir,
		    BasicFileAttributes attrs) throws IOException {
		/*
		 * Example: dir=<diretory>/dirA/dirB, depth = 2 -> do not go
		 * inside dirB
		 */

		Path relPath = directory.resolve(dir);
		if (relPath.getNameCount() >= depth) {
		    return FileVisitResult.SKIP_SUBTREE;
		} else {
		    return FileVisitResult.CONTINUE;
		}
	    }

	    @Override
	    public FileVisitResult visitFile(Path file,
		    BasicFileAttributes attrs) throws IOException {
		// if we find file, it is bad
		invalidPath = file;
		return FileVisitResult.TERMINATE;
	    }
	}

	DirChecker checker = new DirChecker();
	Files.walkFileTree(directory, checker);

	return checker.invalidPath;
    }

    /**
     * Checks whether given table name is correct.
     * 
     * @param databaseRoot
     *            path to database root. Used only as a path example. No checks
     *            in file system are made.
     * @param tableName
     *            potential name of the table
     * @throws IllegalArgumentException
     *             if name is not correct or null
     */
    public static void checkTableNameIsCorrect(String tableName)
	    throws IllegalArgumentException {
	if (tableName == null) {
	    throw new IllegalArgumentException("Table name must not be null");
	}
	Path tableNamePath = Paths.get(tableName).normalize();
	if (!Paths.get(tableName).equals(tableNamePath) || tableNamePath.getParent() != null) {
	    throw new IllegalArgumentException("Table name is not correct");
	}
    }

    /**
     * Handles an occured exception.<br/>
     * Equivalent to handleError(null, message, true);
     * 
     * @param message
     *            message that can be reported to user and is written to log.
     */
    public static void handleError(String message) throws TerminalException {
	handleError(null, message, true);
    }

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
    public static void handleError(Exception exc, String message,
	    boolean reportToUser) throws TerminalException {
	if (reportToUser) {
	    System.err.println(message == null ? exc.getMessage() : message);
	}
	Log.log(Commands.class, exc, message);
	if (exc == null) {
	    throw new TerminalException(message);
	} else {
	    throw new TerminalException(exc);
	}
    }

    public static byte[] insertArray(byte[] source, int sourceOffset,
	    int sourceSize, byte[] target, int targetOffset) {
	if (sourceSize + targetOffset > target.length) {
	    int newLength = Math.max(target.length * 2,
				     sourceSize + targetOffset);
	    if (newLength < 0) {
		newLength = Integer.MAX_VALUE;
	    }
	    if (newLength <= target.length) {
		throw new RuntimeException("Cannot allocate such big array");
	    }

	    byte[] newTarget = new byte[newLength];
	    System.arraycopy(target, 0, newTarget, 0, targetOffset);
	    target = newTarget;
	}

	System.arraycopy(source, sourceOffset, target, targetOffset, sourceSize);
	return target;
    }

    /**
     * Safely performs some action. Exceptions thrown while its execution are
     * given to handler.
     * 
     * @param action
     *            action to be performed
     * @param handler
     *            exception handler
     * @param additionalData
     *            some additional data used by handler to form messages
     */
    public static <T> void performAccurately(AccurateAction action,
	    AccurateExceptionHandler<T> handler, T additionalData)
	    throws TerminalException {
	try {
	    action.perform();
	} catch (Exception exc) {
	    handler.handleException(exc, additionalData);
	}
    }

    public static void removeEmptyFilesAndFolders(Path rootDirectory)
	    throws IOException {
	Files.walkFileTree(rootDirectory, new EmptyFilesRemover(rootDirectory));
    }

    /**
     * Removes the whole subtree under given path
     * 
     * @param removePath
     * @param invoker
     *            name of the invoker; used in error reports.
     */
    public static void rm(final Path removePath, final String invoker)
	    throws TerminalException {

	try {
	    if (Files.isDirectory(removePath)) {
		Files.walkFileTree(removePath, new Utility.FileTreeRemover());
	    } else {
		Files.delete(removePath);
	    }
	} catch (Exception exc) {
	    handleError(exc,
			String.format("%s: cannot remove '%s': No such file or directory",
				      invoker,
				      removePath),
			true);
	}
    }

    public static String simplifyClassName(String name) {
	name = name.substring(name.lastIndexOf('.') + 1);
	name = name.substring(name.lastIndexOf('$') + 1);
	name = name.toLowerCase();
	return name;
    }
}
