package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table;

import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.utils.TypeStringTranslator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

final class TableLoaderDumper {
    private static final String TABLE_SIGNATURE_FILENAME = "signature.tsv";
    private static final String DIRS_EXTENSION = ".dir";
    private static final String FILES_EXTENSION = ".dat";
    private static final String ENCODING = "UTF-8";

    public static void createTable(final File tableDir, final List<Class<?>> columnTypes) throws IOException {
        dumpSignatureFile(tableDir, columnTypes);
    }

    public static void loadTable(final File tableDir,
                                 List<List<Map<String, String>>> tableHashMap,
                                 List<Class<?>> columnTypes) throws IOException {
        columnTypes = readSignatureFile(tableDir);
        File[] tableDirectories = tableDir.listFiles();
        if (tableDirectories == null) {
            throw new NotDirectoryException(tableDir.getName());
        }
        for (int i = 0; i < tableDirectories.length; ++i) {
            if (!tableDirectories[i].getName().equals(TABLE_SIGNATURE_FILENAME)) {
                int indexOfDir = parseNum(tableDirectories[i].toPath());
                loadDirectory(tableDirectories[i], tableHashMap.get(indexOfDir));
            }
        }
    }

    private static List<Class<?>> readSignatureFile(final File tableDir) throws IOException {
        File signatureFile = new File(tableDir, TABLE_SIGNATURE_FILENAME);
            try (BufferedReader bufReader
                         = new BufferedReader(new InputStreamReader(new FileInputStream(signatureFile), ENCODING))) {
                List<Class<?>> res = new ArrayList<>();
                String[] types = bufReader.readLine().split("\\s+");
                for (String type : types) {
                    res.add(TypeStringTranslator.getTypeByStringName(type));
                }
                return res;
            } catch (FileNotFoundException e) {
                return null;
            }
    }

    private static void dumpSignatureFile(final File tableDir, final List<Class<?>> columnTypes) throws IOException {
        File signatureFIle = new File(tableDir, TABLE_SIGNATURE_FILENAME);
        try (BufferedWriter bufWriter
                     = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(signatureFIle), ENCODING))) {
            for (Class<?> type : columnTypes) {
                bufWriter.write(TypeStringTranslator.getStringNameByType(type));
                bufWriter.write("\t");
            }
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
