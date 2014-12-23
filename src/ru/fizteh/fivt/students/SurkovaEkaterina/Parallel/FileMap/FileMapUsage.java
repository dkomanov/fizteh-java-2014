package ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.FileMap;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileMapUsage {
    private static final int MAX_DIRECTORIES_NUMBER = 16;
    private static final int MAX_FILES_NUMBER = 16;

    public static boolean checkFileExists(final String path) {
        File file = new File(path);
        return file.exists();
    }

    public static int getBytesNumber(final String string,
                                     final Charset charset) {
        return string.getBytes(charset).length;
    }

    public static int getKeysLength(final Set<String> keys,
                                    final Charset charset) {
        int keysLength = 0;
        for (final String key : keys) {
            int keyLength = FileMapUsage.getBytesNumber(key, charset);
            keysLength += keyLength + 5;
        }
        return keysLength;
    }
    public static int parseCurrentBucketNumber(File bucket) {
        String name = bucket.getName();
        Matcher matcher = Pattern.compile("([^\\.]+).dir").matcher(name);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }
        throw new IllegalArgumentException("Incorrect bucket name!");
    }

    public static int parseCurrentFileNumber(File file) {
        String name = file.getName();
        Matcher matcher = Pattern.compile("([^\\.]+).dat").matcher(name);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }
        throw new IllegalArgumentException("Incorrect file name!");
    }

    public static int getDirNumber(final String key) {
        int hashCode = key.hashCode();
        return hashCode % MAX_DIRECTORIES_NUMBER;
    }

    public static int getFileNumber(final String key) {
        int hashCode = key.hashCode();
        return hashCode / MAX_DIRECTORIES_NUMBER % MAX_FILES_NUMBER;
    }

    public static void checkKeyPlacement(String key, int currentBucket, int currentFile)
    {
        if (currentBucket != getDirNumber(key)
                || currentFile != getFileNumber(key)) {
            throw new IllegalArgumentException("Invalid key placement!");
        }
    }

    public static void deleteFile(File fileToDelete) {
        if (!fileToDelete.exists()) {
            return;
        }
        if (fileToDelete.isDirectory()) {
            for (final File file : fileToDelete.listFiles()) {
                deleteFile(file);
            }
        }
        fileToDelete.delete();
    }

    public static boolean compareValues(Object val1, Object val2) {
        if (val1 == null && val2 == null) {
            return true;
        }
        if (val1 == null || val2 == null) {
            return false;
        }
        return val1.equals(val2);
    }

    public static DatabaseFileDescriptor makeDescriptor(String key) {
        return new DatabaseFileDescriptor(getDirNumber(key), getFileNumber(key));
    }
}
