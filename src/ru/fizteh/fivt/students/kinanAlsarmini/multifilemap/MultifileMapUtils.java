package ru.fizteh.fivt.students.kinanAlsarmini.multifilemap;

import ru.fizteh.fivt.students.kinanAlsarmini.filemap.base.AbstractStorage;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultifileMapUtils {
    private static final Pattern DIR_PATTERN = Pattern.compile("([^\\.]+).dir");
    private static final Pattern FILE_PATTERN = Pattern.compile("([^\\.]+).dat");

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

    public static int parseCurrentBucketNumber(File bucket) {
        String name = bucket.getName();
        Matcher matcher = DIR_PATTERN.matcher(name);

        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }

        throw new IllegalArgumentException("incorrect bucket name");
    }

    public static int parseCurrentFileNumber(File file)
    {
        String name = file.getName();
        Matcher matcher = FILE_PATTERN.matcher(name);

        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }

        throw new IllegalArgumentException("incorrect file name");
    }

    static int getDirNumber(String key) {
        byte[] bytes = key.getBytes(AbstractStorage.CHARSET);
        int firstSymbol = Math.abs(bytes[0]);

        return firstSymbol % DistributedSaver.BUCKET_COUNT;
    }

    static int getFileNumber(String key) {
        byte[] bytes = key.getBytes(AbstractStorage.CHARSET);
        int firstSymbol = Math.abs(bytes[0]);

        return firstSymbol / DistributedSaver.BUCKET_COUNT % DistributedSaver.FILES_PER_DIR;
    }

    public static void checkKeyPlacement(String key, int currentBucket, int currentFile)
    {
        if (currentBucket != getDirNumber(key)
                || currentFile != getFileNumber(key)) {

            throw new IllegalArgumentException("invalid key placement");
        }
    }

    public static String parseTableName(String params) {
        int index = params.indexOf(' ');

        if (index == -1) {
            return params;
        }

        return params.substring(0, index);
    }
}
