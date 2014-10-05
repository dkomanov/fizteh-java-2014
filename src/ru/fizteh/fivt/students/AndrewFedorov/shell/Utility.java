package ru.fizteh.fivt.students.AndrewFedorov.shell;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

public class Utility {

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
    abstract static class MyTreeWalker<T> implements FileVisitor<T> {
        @Override
        public FileVisitResult visitFileFailed(T visitPath, IOException exc)
            throws IOException {
            throw exc;
        }
    
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
    }

    /**
     * File visitor that copies file subtree from one directory to another.<br/>
     * Execution is stopped on exception.<br/>
     * Symbolic links are not followed.
     * 
     * @author phoenix
     * 
     */
    static class FileTreeCopier extends MyTreeWalker<Path> {
        /**
         * Path to directory that is root of source tree
         */
        private Path sourceRoot;
    
        /**
         * Path to directory that is root of target tree
         */
        private Path targetRoot;
    
        /**
         * Constructs this subtree copier.
         * 
         * @param sourceRoot
         *            directory that is the root of existing subtree
         * @param targetRoot
         *            directory that will be the root of copied subtree
         */
        public FileTreeCopier(Path sourceRoot, Path targetRoot) {
            this.sourceRoot = sourceRoot;
            this.targetRoot = targetRoot;
        }
    
        @Override
        public FileVisitResult preVisitDirectory(Path visitPath,
            BasicFileAttributes attrs) throws IOException {
            try {
            Path relativePath = sourceRoot.relativize(visitPath);
            Path destDir = targetRoot.resolve(relativePath).normalize();
            Log.log(Commands.class, String.format(
                "\tCopy dir '%s' -> '%s'", visitPath, destDir));
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
            Path rel = sourceRoot.relativize(visitPath);
            Path destPath = targetRoot.resolve(rel).normalize();
            Log.log(Commands.class, String.format(
                "\tCopy file '%s' -> '%s'", visitPath, destPath));
            Files.copy(visitPath, destPath,
                StandardCopyOption.REPLACE_EXISTING);
    
            return FileVisitResult.CONTINUE;
            } catch (IOException exc) {
            return visitFileFailed(visitPath, exc);
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
    static class FileTreeRemover extends MyTreeWalker<Path> {
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
    static void handleError(Throwable exc, String message,
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
    static void handleError(String message) {
        handleError(null, message, true);
    }

}
