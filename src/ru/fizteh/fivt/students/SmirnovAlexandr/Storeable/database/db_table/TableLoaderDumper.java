package ru.fizteh.fivt.students.SmirnovAlexandr.Storeable.database.db_table;

import ru.fizteh.fivt.students.SmirnovAlexandr.Storeable.database.db_table_provider.utils.TypeStringTranslator;
import ru.fizteh.fivt.students.SmirnovAlexandr.Storeable.database.db_table_provider.utils.Utility;
import ru.fizteh.fivt.students.SmirnovAlexandr.Storeable.database.exceptions.TableCorruptedException;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public final class TableLoaderDumper {
    private static final String TABLE_SIGNATURE_FILENAME = "signature.tsv";
    private static final String DIRS_EXTENSION = ".dir";
    private static final String FILES_EXTENSION = ".dat";
    private static final String ENCODING = "UTF-8";

    public static void createTable(final Path tableDir, final List<Class<?>> columnTypes) throws IOException {
        dumpSignatureFile(tableDir, columnTypes);
    }

    public static void loadTable(final Path tableDir,
                                 List<List<Map<String, String>>> tableHashMap,
                                 List<Class<?>> columnTypes) throws IOException {
        readSignatureFile(tableDir, columnTypes);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(tableDir)) {
            for (Path dir : stream) {
                if (!Utility.getNameByPath(dir).equals(TABLE_SIGNATURE_FILENAME)) {
                    int indexOfDir = parseNum(dir);
                    loadDirectory(dir, tableHashMap.get(indexOfDir));
                }
            }
        } catch (NumberFormatException e) {
            throw new TableCorruptedException(Utility.getNameByPath(tableDir));
        }
    }

    private static List<Class<?>> readSignatureFile(final Path tableDir,
                                                    List<Class<?>> columnTypes) throws IOException {
            try (BufferedReader bufReader
                         = Files.newBufferedReader(getSignatureFilePath(tableDir), Charset.forName(ENCODING))) {
                String[] types = bufReader.readLine().split("\\s+");
                for (String stringType : types) {
                    Class<?> type = TypeStringTranslator.getTypeByStringName(stringType);
                    if (type == null) {
                        throw new TableCorruptedException(Utility.getNameByPath(tableDir));
                    } else {
                        columnTypes.add(type);
                    }
                }
                return columnTypes;
            } catch (FileNotFoundException e) {
                return null;
            }
    }

    private static void dumpSignatureFile(final Path tableDir, final List<Class<?>> columnTypes) throws IOException {
        try (BufferedWriter bufWriter
                     = Files.newBufferedWriter(getSignatureFilePath(tableDir), Charset.forName(ENCODING))) {
            for (Class<?> type : columnTypes) {
                bufWriter.write(TypeStringTranslator.getStringNameByType(type));
                bufWriter.write("\t");
            }
        }
    }

    private static void loadDirectory(final Path directory, List<Map<String, String>> dirHashMap) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path file : stream) {
                int fileNum = parseNum(file);
                loadFile(file, dirHashMap.get(fileNum));
            }
        }
    }

    private static void loadFile(final Path file, Map<String, String> fileHashMap) throws IOException {
        try (RandomAccessFile raFile = new RandomAccessFile(file.toFile(), "r")) {
            if (raFile.length() != 0) {
                try (ByteArrayOutputStream buf = new ByteArrayOutputStream()) {
                    long currentSeek = 0;
                    LinkedList<String> keys = new LinkedList<>();
                    LinkedList<Integer> offsets = new LinkedList<>();
                    currentSeek = readKeyAndOffset(raFile, currentSeek, keys, offsets);
                    int firstOffset = offsets.getFirst();
                    while (currentSeek != firstOffset) {
                        currentSeek = readKeyAndOffset(raFile, currentSeek, keys, offsets);
                    }
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
                    while (currentSeek != raFile.length()) {
                        buf.write(raFile.readByte());
                        currentSeek++;
                    }
                    fileHashMap.put(keysIter.next(), buf.toString(ENCODING));
                    buf.reset();
                }
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

    public static void dumpTable(final Path tableDir,
                                  List<List<Map<String, String>>> tableHashMap) throws IOException {
        for (int i = 0; i < tableHashMap.size(); ++i) {
            dumpDirectory(makeDirPath(tableDir, i), tableHashMap.get(i));
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

    public static void checkTableForCorruptness(final Path tablePath) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(tablePath)) {
            for (Path file : stream) {
                if (!Utility.getNameByPath(file).equals(TABLE_SIGNATURE_FILENAME)) {
                    checkInnerDirectory(file);
                }
            }
        } catch (IOException | TableCorruptedException e) {
            throw new TableCorruptedException(tablePath.toString());
        }
    }

    private static void checkInnerDirectory(final Path dir) {
        if (!Files.isDirectory(dir) || parseNum(dir) == -1) {
            throw new TableCorruptedException();
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path file : stream) {
                parseNum(file);
            }
        } catch (IOException | NumberFormatException e) {
            throw new TableCorruptedException();
        }
    }


    private static Path makeDirPath(final Path tablePath, Integer dirNum) {
        return tablePath.resolve(dirNum + DIRS_EXTENSION);
    }

    private static Path makeFilePathFromDirPath(final Path dirPath, Integer fileNum) {
        return Paths.get(dirPath.toString(), fileNum.toString() + FILES_EXTENSION).normalize();
    }

    private static int parseNum(final Path path) {
        String pathStr = path.getFileName().toString();
        int extensionIndex = pathStr.indexOf('.');
        try {
            return Integer.parseInt(pathStr.substring(0, extensionIndex));
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            return -1;
        }
    }

    private static Path getSignatureFilePath(final Path tableDir) {
        return tableDir.resolve(TABLE_SIGNATURE_FILENAME);
    }
}
