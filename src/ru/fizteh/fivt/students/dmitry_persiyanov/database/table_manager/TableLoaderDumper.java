package ru.fizteh.fivt.students.dmitry_persiyanov.database.table_manager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

final class TableLoaderDumper {
    public static final String DIRS_EXTENSION = ".dir";
    public static final String FILES_EXTENSION = ".dat";
    public static final String ENCODING = "UTF-8";

    // Table loading methods.
    public static void loadTable(final File tableFile,
                                 List<List<Map<String, String>>> tableHashMap) throws IOException {
        File[] tableDirectories = tableFile.listFiles();
        if (tableDirectories == null) {
            throw new NotDirectoryException(tableFile.getName());
        }
        for (int i = 0; i < tableDirectories.length; ++i) {
            int indexOfDir = parseNum(tableDirectories[i].toPath());
            loadDirectory(tableDirectories[i], tableHashMap.get(indexOfDir));
        }
    }

    private static void loadDirectory(final File directory, List<Map<String, String>> dirHashMap) throws IOException {
        File[] dirFiles = directory.listFiles();
        if (dirFiles == null) {
            throw new NotDirectoryException(directory.getPath());
        }
        for (int i = 0; i < dirFiles.length; ++i) {
            int fileNum = parseNum(dirFiles[i].toPath());
            loadFile(dirFiles[i], dirHashMap.get(fileNum));
        }
    }

    private static void loadFile(final File file, Map<String, String> fileHashMap) throws IOException {
        RandomAccessFile raFile = new RandomAccessFile(file, "r");
        if (raFile.length() != 0) {
            try (ByteArrayOutputStream buf = new ByteArrayOutputStream()) {
                long currentSeek = 0;
                LinkedList<String> keys = new LinkedList<>();
                LinkedList<Integer> offsets = new LinkedList<>();
                // Reading keys and offsets.
                currentSeek = readKeyAndOffset(raFile, currentSeek, keys, offsets);
                int firstOffset = offsets.getFirst();
                while (currentSeek != firstOffset) {
                    currentSeek = readKeyAndOffset(raFile, currentSeek, keys, offsets);
                }
                // End of reading keys and offsets.
                // Reading values and filling hashmap.
                Iterator<String> keysIter = keys.iterator();
                Iterator<Integer> offsetIter = offsets.iterator();
                offsetIter.next();
                while (offsetIter.hasNext()) {
                    int nextOffset = offsetIter.next();
                    while (currentSeek != nextOffset) {
                        buf.write(raFile.readByte());
                        currentSeek++;
                    }
                    fileHashMap.put(keysIter.next(), buf.toString(ENCODING));
                    buf.reset();
                }
                // Reading last value.
                while (currentSeek != raFile.length()) {
                    buf.write(raFile.readByte());
                    currentSeek++;
                }
                fileHashMap.put(keysIter.next(), buf.toString(ENCODING));
                buf.reset();
            }
        }
    }

    private static long readKeyAndOffset(RandomAccessFile raFile, long currentSeek, LinkedList<String> keys,
                                        LinkedList<Integer> offsets) throws IOException {
        try (ByteArrayOutputStream buf = new ByteArrayOutputStream()) {
            int b = -1;
            while ((b = (int) raFile.readByte()) != 0) {
                currentSeek++;
                buf.write(b);
            }
            currentSeek++;
            keys.add(buf.toString(ENCODING));
            buf.reset();
            int offset = raFile.readInt();
            offsets.add(offset);
            currentSeek += 4;
        }
        return currentSeek;
    }

    // Table dumping methods.
    public static void dumpTable(final File tableDir,
                                  List<List<Map<String, String>>> tableHashMap) throws IOException {
        for (int i = 0; i < tableHashMap.size(); ++i) {
            dumpDirectory(makeDirPath(tableDir.toPath(), i), tableHashMap.get(i));
        }
    }

    private static void dumpDirectory(final Path dirPath, List<Map<String, String>> dirHashMap) throws IOException {
        if (dirHashMap == null || isDirHashMapEmpty(dirHashMap)) {
            Files.deleteIfExists(dirPath);
        } else {
            if (!Files.exists(dirPath)) {
                Files.createDirectory(dirPath);
            }
            for (int i = 0; i < dirHashMap.size(); ++i) {
                dumpFile(makeFilePathFromDirPath(dirPath, i), dirHashMap.get(i));
            }
        }
    }

    private static boolean isDirHashMapEmpty(final List<Map<String, String>> dirHashMap) {
        for (Map<String, String> map : dirHashMap) {
            if (!map.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static void dumpFile(final Path filePath, Map<String, String> fileHashMap) throws IOException {
        if (fileHashMap == null || fileHashMap.size() == 0) {
            Files.deleteIfExists(filePath);
        } else {
            try (RandomAccessFile raFile = new RandomAccessFile(filePath.toString(), "rw")) {
                raFile.setLength(0);
                Set<String> keys = fileHashMap.keySet();
                LinkedList<Integer> offsetsPlaces = new LinkedList<>();
                for (String key : keys) {
                    raFile.write(key.getBytes(ENCODING));
                    raFile.write('\0');
                    offsetsPlaces.add((int) raFile.getFilePointer());
                    raFile.writeInt(0);
                }
                Collection<String> values = fileHashMap.values();
                int currentOffset = (int) raFile.getFilePointer();
                Iterator<Integer> offsetsPlacesIter = offsetsPlaces.iterator();
                for (String val : values) {
                    byte[] valBytes = val.getBytes(ENCODING);
                    raFile.write(valBytes);
                    int offsetPlace = offsetsPlacesIter.next();
                    raFile.seek(offsetPlace);
                    raFile.writeInt(currentOffset);
                    currentOffset += valBytes.length;
                    raFile.seek(currentOffset);
                }
            }
        }
    }

    public static int countKeys(final Path tablePath) throws IOException {
        int res = 0;
        File[] dirs = new File(tablePath.toString()).listFiles();
        for (File dir : dirs) {
            File[] files = dir.listFiles();
            for (File file : files) {
                res += countKeysInFile(file);
            }
        }
        return res;
    }

    private static int countKeysInFile(final File file) throws IOException {
        int res = 0;
        byte b;
        try (RandomAccessFile raFile = new RandomAccessFile(file, "r")) {
            while (true) {
                b = raFile.readByte();
                if (b == 0) {
                    res++;
                    raFile.readInt();
                }
            }
        } catch (EOFException e) {
            // Catching eof.
        }
        return res;
    }

    private static Path makeDirPath(final Path tablePath, Integer dirNum) {
        return Paths.get(tablePath.toString(), dirNum.toString() + DIRS_EXTENSION).normalize();
    }

    private static Path makeFilePathFromDirPath(final Path dirPath, Integer fileNum) {
        return Paths.get(dirPath.toString(), fileNum.toString() + FILES_EXTENSION).normalize();
    }

    private static int parseNum(final Path path) {
        String pathStr = path.getFileName().toString();
        int extensionIndex = pathStr.indexOf('.');
        return Integer.parseInt(pathStr.substring(0, extensionIndex));
    }
}
