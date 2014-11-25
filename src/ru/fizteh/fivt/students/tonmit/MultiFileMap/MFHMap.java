package ru.fizteh.fivt.students.tonmit.MultiFileMap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class MFHMap extends HashMap<String, String> {
    protected Path dbPath;
    protected Map<String, Integer> changedFiles = new TreeMap<>();

    protected static final int FILES_COUNT = 16;
    protected static final int DIRECTORIES_COUNT = 16;

    private void printErrorAndExit(String errorStr) {
        System.err.println(errorStr);
        System.exit(-1);
    }
    
    public MFHMap(Path path) {
        dbPath = path.normalize();
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                printErrorAndExit("can't create directory: " + path.toString());
            }
        }
    }

    private Path pathToFile(int i, int j) {
        return dbPath.resolve(i + ".dir" + File.separator + j + ".dat");
    }

    private Path pathToDirectory(int i) {
        return dbPath.resolve(i + ".dir" + File.separator);
    }

    public  SimpleEntry<String, SimpleEntry<Integer, Integer>> whereToSave(String value) {
        int hashCode = value.hashCode();
        int d = hashCode % DIRECTORIES_COUNT;
        int f = hashCode / DIRECTORIES_COUNT % FILES_COUNT;
        return new SimpleEntry<>(
                pathToFile(d, f).toString(),
                new SimpleEntry<>(d, f));
    }

    private void endOfStream() {
        System.err.println("there is no more data because the end of the stream has been reached.");
    }

    void readFromFile(DataInputStream iStream, int i, int j) {
        try {
            int keySize = iStream.readInt();
            if (keySize > Integer.bitCount(Integer.MAX_VALUE)) {
                System.err.println("Bad data in " + pathToFile(i, j).toString());
                iStream.close();
                return;
            }
            byte[] key = new byte[keySize];
            if (iStream.read(key, 0, keySize) == -1) {
                endOfStream();
            }

            int valueSize = iStream.readInt();
            byte[] value = new byte[valueSize];
            if (iStream.read(value, 0, valueSize) == -1) {
                endOfStream();
            }

            put(new String(key, "UTF-8"), new String(value, "UTF-8"));
        } catch (IOException e) {
            printErrorAndExit("Error in reading: " + e.getMessage());
        }
    }

    public void load() {
        for (int i = 0; i < DIRECTORIES_COUNT; ++i) {
            for (int j = 0; j < FILES_COUNT; ++j) {
                try (DataInputStream stream = new DataInputStream(Files.newInputStream(
                        pathToFile(i, j)))) {
                        while (stream.available() > 0) {
                            readFromFile(stream, i, j);
                        }
                    } catch (IOException ignore) {
                        continue;
                    }
            }
        }
    }

    void writeToFile(DataOutputStream oStream, String key, String value) throws IOException {
        byte[] keyInBytes = key.getBytes("UTF-8");
        byte[] valueInBytes = value.getBytes("UTF-8");

        oStream.writeInt(keyInBytes.length);
        oStream.write(keyInBytes);

        oStream.writeInt(valueInBytes.length);
        oStream.write(valueInBytes);
    }

    public void deleteFiles() {
        try {
            for (int i = 0; i < DIRECTORIES_COUNT; ++i) {
                for (int j = 0; j < FILES_COUNT; ++j) {
                    if (changedFiles.containsKey(pathToFile(i, j).toString())) {
                        if (Files.exists(pathToFile(i, j))) {
                            Files.delete(pathToFile(i, j));
                        }
                        if (Files.exists(pathToDirectory(i)) && pathToDirectory(i).toFile().list().length == 0) {
                            Files.delete(pathToDirectory(i));
                        }
                    }
                }
            }
        } catch (IOException e) {
            printErrorAndExit("Can't delete from disk: " + e.getMessage());
        }
    }

    public void deleteAllFiles() {
        try {
            for (int i = 0; i < DIRECTORIES_COUNT; ++i) {
                for (int j = 0; j < FILES_COUNT; ++j) {
                    if (Files.exists(pathToFile(i, j))) {
                        Files.delete(pathToFile(i, j));
                    }
                    if (Files.exists(pathToDirectory(i)) && pathToDirectory(i).toFile().list().length == 0) {
                        Files.delete(pathToDirectory(i));
                    }
                }
            }
        } catch (IOException e) {
            printErrorAndExit("Cannot delete all files, an error occured");
        }
    }
    
    public void unload() {
        deleteFiles();
        boolean[] dir = new boolean[DIRECTORIES_COUNT];
        boolean[][] file = new boolean[DIRECTORIES_COUNT][FILES_COUNT];
        DataOutputStream[][] streams = new DataOutputStream[DIRECTORIES_COUNT][FILES_COUNT];
        try {

            if (!Files.exists(dbPath)) {
                Files.createDirectory(dbPath);
            }
            for (Map.Entry<String, String> entry : entrySet()) {
                SimpleEntry<String, SimpleEntry<Integer, Integer>> pathOfFile = whereToSave(entry.getKey());
                if (changedFiles.containsKey(pathOfFile.getKey())) {
                    int d = pathOfFile.getValue().getKey();
                    int f = pathOfFile.getValue().getValue();
                    if (!file[d][f]) {
                        if (!dir[d]) {
                            if (!Files.exists(pathToDirectory(d))) {
                                Files.createDirectory(pathToDirectory(d));
                            }
                            dir[d] = true;
                        }
                        streams[d][f] = new DataOutputStream(Files.newOutputStream(
                                pathToFile(d, f)));
                        file[d][f] = true;
                    }
                    writeToFile(streams[d][f], entry.getKey(), entry.getValue());
                    Integer collisionCount = changedFiles.get(pathOfFile.getKey());
                    if (collisionCount != 0) {
                        --collisionCount;
                        changedFiles.put(pathOfFile.getKey(), collisionCount);
                    } else {
                        changedFiles.remove(pathOfFile.getKey());
                    }
                }
            }

        } catch (IOException e) {
            changedFiles.clear();
            printErrorAndExit("Can't write to disk: " + e.getMessage());
        } finally {
            for (int i = 0; i < DIRECTORIES_COUNT; ++i) {
                for (int j = 0; j < FILES_COUNT; ++j) {
                    if (streams[i][j] != null) {
                        try {
                            streams[i][j].close();
                        } catch (IOException ignored) {
                            continue;
                        }
                    }
                }
            }
        }
    }
}
