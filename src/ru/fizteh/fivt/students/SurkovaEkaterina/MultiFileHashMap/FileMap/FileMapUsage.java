package ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.FileMap;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Set;

public class FileMapUsage {
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
}
