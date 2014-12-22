package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support;

import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.TerminalException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.Commands;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public final class Utility {

    // not for constructing :)
    private Utility() {

    }

    public static void checkAllTypesAreSupported(Collection<Class<?>> checkTypes,
                                                 Collection<Class<?>> supportedTypes)
            throws IllegalArgumentException {
        for (Class<?> type : checkTypes) {
            if (!supportedTypes.contains(type)) {
                throw new IllegalArgumentException(
                        "wrong type (" + type.getSimpleName() + " is not supported)");
            }
        }
    }

    /**
     * Handles an occurred exception.
     * @param cause
     *         occurred exception. If null, an {@link Exception} is constructed via {@link
     *         Exception#Exception(String)}.
     * @param message
     *         message that can be reported to user and is written to log.
     * @param reportToUser
     *         if true, message is printed to {@link System#err}.
     */
    public static void handleError(String message, Throwable cause, boolean reportToUser)
            throws TerminalException {
        if (reportToUser) {
            System.err.println(message == null ? cause.getMessage() : message);
        }
        Log.log(Commands.class, cause, message);
        if (cause == null) {
            throw new TerminalException(message);
        } else {
            throw new TerminalException(message, cause);
        }
    }

    public static byte[] insertArray(byte[] source,
                                     int sourceOffset,
                                     int sourceSize,
                                     byte[] target,
                                     int targetOffset) {
        if (sourceSize + targetOffset > target.length) {
            int newLength = Math.max(target.length * 2, sourceSize + targetOffset);
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

    public static void removeEmptyFilesAndFolders(Path rootDirectory) throws IOException {
        Files.walkFileTree(rootDirectory, new EmptyFilesRemover(rootDirectory));
    }

    /**
     * Removes the whole subtree under given path
     * @throws java.io.IOException
     */
    public static void rm(final Path removePath) throws IOException {
        if (Files.isDirectory(removePath)) {
            Files.walkFileTree(removePath, new FileTreeRemover());
        } else {
            Files.delete(removePath);
        }
    }

    public static String simplifyFieldName(String name) {
        name = name.toLowerCase();
        return name;
    }

    /**
     * Counts differences between source and target map (count of different values mapped to the same keys +
     * count of keys that are found in one map and are missing in the other and vice versa). Null values are
     * not supported - when {@link java.util.Map#get(Object)} return null, it is interpreted as missing of
     * the
     * given key.
     * @param source
     *         Source map. There are no differences between source and target map.
     * @param target
     *         Target map.
     * @param <K>
     *         Key type.
     * @param <V>
     *         Value type.
     */
    public static <K, V> int countDifferences(Map<K, V> source, Map<K, V> target) {
        if (target.isEmpty() || source.isEmpty()) {
            return Math.max(target.size(), source.size());
        }

        int diffs = 0;

        // counting objects that are added or modified
        for (Entry<K, V> entry : target.entrySet()) {
            V sourceValue = source.get(entry.getKey());
            V targetValue = entry.getValue();
            if ((sourceValue == null ? targetValue != null : (!sourceValue.equals(targetValue)))) {
                diffs++;
            }
        }

        // counting objects that are removed
        for (K key : source.keySet()) {
            if (!target.containsKey(key)) {
                diffs++;
            }
        }

        return diffs;
    }

    /**
     * Checks whether given table name is correct.
     * @param tableName
     *         potential name of the table
     * @throws IllegalArgumentException
     *         if name is not correct or null
     */
    public static void checkTableNameIsCorrect(String tableName) throws IllegalArgumentException {
        checkNotNull(tableName, "Table name");

        Path tableNamePath = Paths.get(tableName).normalize();
        Path sampleParent = Paths.get("sample");
        if (tableNamePath.getParent() != null || !sampleParent.resolve(tableName).normalize().getFileName()
                                                              .toString().equals(tableName)) {
            throw new IllegalArgumentException("Table name is not correct");
        }
    }

    /**
     * Safely performs some action. Exceptions thrown while its execution are given to handler.
     * @param action
     *         action to be performed
     * @param handler
     *         exception handler
     * @param additionalData
     *         some additional data used by handler to form messages
     */
    public static <T> void performAccurately(AccurateAction action,
                                             AccurateExceptionHandler<T> handler,
                                             T additionalData) throws TerminalException {
        try {
            action.perform();
        } catch (Exception exc) {
            handler.handleException(exc, additionalData);
        }
    }

    /**
     * Checks whether the given variable is null.
     * @param variable
     *         Variable to check against null value.
     * @param name
     *         Name of the variable to include into message.
     * @throws IllegalArgumentException
     *         If variable is null. Message: "(name) must no be null".
     */
    public static void checkNotNull(Object variable, String name) throws IllegalArgumentException {
        if (variable == null) {
            throw new IllegalArgumentException(name + " must not be null");
        }
    }

    /**
     * Inverses key-value mapping to value-key mapping.
     * @param map
     *         Source map.
     * @param <K>
     *         Key type in the source map.
     * @param <V>
     *         Value type in the source map.
     * @return An inversed map. It is not guaranteed that it is instance of the same class as source map has.
     * @throws IllegalArgumentException
     *         If there are two keys having the same values.
     * @see Object#equals(Object)
     */
    public static <K, V> Map<V, K> inverseMap(Map<K, V> map) throws IllegalArgumentException {
        Map<V, K> inversed = new HashMap<>(map.size());

        for (Entry<K, V> e : map.entrySet()) {
            if (inversed.containsKey(e.getValue())) {
                throw new IllegalArgumentException("Source map contains at least two duplicate values");
            }
            inversed.put(e.getValue(), e.getKey());
        }

        return inversed;
    }

    /**
     * Returns string between two quotes. All quote and escape sequences inside the string are escaped by
     * escape sequence.
     * @param s
     *         String to quote.
     * @param quoteSequence
     *         Quotes.
     * @param escapeSequence
     *         Escape sequence. Quotes and this sequence occurrences will be prepended by escape sequence.
     * @return Endcoded string inside quotes. Returns null for null string.
     * @see Utility#unquoteString(String,
     * String, String)
     */
    public static String quoteString(String s, String quoteSequence, String escapeSequence) {
        if (s == null) {
            return null;
        }
        s = s.replace(escapeSequence, escapeSequence + escapeSequence);
        s = s.replace(quoteSequence, escapeSequence + quoteSequence);
        return quoteSequence + s + quoteSequence;
    }

    /**
     * Decodes a quoted via {@link ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support
     * .Utility#quoteString(String,
     * String, String)} method string.
     * @param s
     *         Quoted string (must start and end with quote sequence).
     * @param quoteSequence
     *         Quotes.
     * @param escapeSequence
     *         Escape sequence to escape quotes and itself.
     * @return Decoded string.
     */
    public static String unquoteString(String s, String quoteSequence, String escapeSequence)
            throws IllegalArgumentException {
        if (!s.startsWith(quoteSequence) || !s.endsWith(quoteSequence)) {
            throw new IllegalArgumentException("String must be in quotes");
        }

        s = s.substring(1, s.length() - 1);

        s = s.replace(
                escapeSequence + "" + quoteSequence, quoteSequence);
        s = s.replace(
                escapeSequence + escapeSequence, escapeSequence);
        return s;
    }

    public static int findClosingQuotes(String string,
                                        int begin,
                                        int end,
                                        char quoteCharacter,
                                        char escapeCharacter) throws ParseException {
        // Indicates that the symbol at current (index) position is escaped by previous symbol.
        boolean escaped = false;

        for (int index = begin; index < end; index++) {
            char c = string.charAt(index);

            if (c == quoteCharacter) {
                if (!escaped) {
                    return index;
                }
                escaped = false;
            } else if (c == escapeCharacter) {
                escaped = !escaped;
            } else {
                if (escaped) {
                    throw new ParseException(
                            "Unexpected escaped symbol at position " + index + ": '" + c + "'", index);
                }
            }

        }

        return -1;
    }

    /**
     * File visitor that deletes empty files and empty folders.
     * @author phoenix
     */
    public static class EmptyFilesRemover extends FileTreeRemover {
        private final Path rootDirectory;

        /**
         * @param rootDirectory
         *         root directory that will be never deleted
         */
        public EmptyFilesRemover(Path rootDirectory) {
            this.rootDirectory = rootDirectory;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path path, IOException exc) throws IOException {
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
        public FileVisitResult visitFile(Path visitPath, BasicFileAttributes attrs) throws IOException {
            if (Files.size(visitPath) == 0) {
                return super.visitFile(visitPath, attrs);
            } else {
                return FileVisitResult.CONTINUE;
            }
        }
    }

    /**
     * File visitor that deletes file subtree.<br/> Symbolic links are not followed.
     * @author phoenix
     */
    public static class FileTreeRemover extends MyTreeWalker<Path> {
        @Override
        public FileVisitResult postVisitDirectory(Path path, IOException exc) throws IOException {
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
        public FileVisitResult visitFile(Path visitPath, BasicFileAttributes attrs) throws IOException {
            try {
                Files.delete(visitPath);
                return FileVisitResult.CONTINUE;
            } catch (IOException exc) {
                return visitFileFailed(visitPath, exc);
            }
        }
    }

    /**
     * Partial implementation of {@link java.nio.file.FileVisitor}, that has the following
     * piculiarities:<br/>
     * <ul> <li>If an exception occurs, file tree traverse is stopped</li> <li>Nothing is done
     * before and after directory visit</li> <li>{@link java.nio.file.FileVisitor#visitFile(Object,
     * java.nio.file.attribute.BasicFileAttributes)} is not implemented. </ul>
     * @author phoenix
     * @see java.nio.file.FileVisitor
     */
    public abstract static class MyTreeWalker<T> implements FileVisitor<T> {
        @Override
        public FileVisitResult preVisitDirectory(T visitPath, BasicFileAttributes attrs) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(T path, IOException exc) throws IOException {
            if (exc == null) {
                return FileVisitResult.CONTINUE;
            } else {
                return visitFileFailed(path, exc);
            }
        }

        @Override
        public FileVisitResult visitFileFailed(T visitPath, IOException exc) throws IOException {
            throw exc;
        }
    }
}
