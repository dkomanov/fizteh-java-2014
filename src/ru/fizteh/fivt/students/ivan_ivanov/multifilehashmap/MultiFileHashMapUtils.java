package ru.fizteh.fivt.students.ivan_ivanov.multifilehashmap;

import ru.fizteh.fivt.students.ivan_ivanov.filemap.FileMapState;
import ru.fizteh.fivt.students.ivan_ivanov.filemap.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MultiFileHashMapUtils {

    private static final int MAX = 16;

    public static void read(final File currentDir, final Map<String, String> currentMap) throws IOException {

        for (int directNumber = 0; directNumber < MAX; ++directNumber) {
            File subDir = new File(currentDir, directNumber + ".dir");
            if (!subDir.exists()) {
                continue;
            }
            if (!subDir.isDirectory()) {
                throw new IOException(subDir.getName() + "isn't directory");
            }

            for (int fileNumber = 0; fileNumber < MAX; ++fileNumber) {
                File currentFile = new File(subDir, fileNumber + ".dat");
                if (!currentFile.exists()) {
                    continue;
                }
                FileMapState state = new FileMapState(currentFile);
                state.setDataBase(currentMap);
                Utils.readDataBase(state);
            }
        }
    }

    public static void deleteDirectory(final File directory) throws IOException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File f : files) {
                deleteDirectory(f);
            }
        }
        boolean success = directory.delete();
        if (!success) {
            throw new IOException("cannot remove " + directory.getName() + ": unknown error");
        }
    }

    public static void write(final File currentDir, final Map<String, String> currentMap) throws IOException {

        Map<String, String>[][] arrayOfMap = new Map[MAX][MAX];
        for (String key : currentMap.keySet()) {
            int byteOfKey = key.getBytes(StandardCharsets.UTF_8)[0];
            int nDirectory = byteOfKey % MAX;
            int nFile = byteOfKey / MAX % MAX;
            if (arrayOfMap[nDirectory][nFile] == null) {
                arrayOfMap[nDirectory][nFile] = new HashMap<String, String>();
            }
            arrayOfMap[nDirectory][nFile].put(key, currentMap.get(key));
        }

        for (int i = 0; i < MAX; i++) {
            File dir = new File(currentDir, i + ".dir");
            for (int j = 0; j < MAX; j++) {
                File file = new File(dir, j + ".dat");
                if (null == arrayOfMap[i][j]) {
                    if (file.exists()) {
                        file.delete();
                    }
                    continue;
                }

                if (!dir.exists()) {
                    dir.mkdir();
                }

                if (!file.exists()) {
                    file.createNewFile();
                }
                Utils.write(arrayOfMap[i][j], file);
            }

            if (dir.exists()) {
                if (dir.listFiles().length == 0) {
                    deleteDirectory(dir);
                }
            }
        }

    }
}
