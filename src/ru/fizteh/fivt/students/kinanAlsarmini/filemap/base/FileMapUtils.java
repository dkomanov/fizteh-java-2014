package ru.fizteh.fivt.students.kinanAlsarmini.filemap.base;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Set;

public class FileMapUtils {
    public static boolean checkFileExists(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static int getByteCount(String string, Charset charset) {
        return string.getBytes(charset).length;
    }

    public static int getKeysLength(Set<String> keys, Charset charset) {
        int keysLength = 0;

        for (final String key : keys) {
            int keyLength = FileMapUtils.getByteCount(key, charset);

            keysLength += keyLength + 5;
        }

        return keysLength;
    }
}
