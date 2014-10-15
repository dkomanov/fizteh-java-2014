package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.MultiFileHashMap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Дмитрий on 07.10.14.
 */
public class MFHMap extends HashMap<String, String> {
    Path dbPath;

    public MFHMap(Path path) {
        dbPath = path.normalize();
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                System.err.println("can't create directory: " + path.toString());
                System.exit(-1);
            }
        }
    }

    void readFromFile(DataInputStream iStream) {
        try {
            int keySize = iStream.readInt();
            byte[] key = new byte[keySize];
            iStream.read(key, 0, keySize);

            int valueSize = iStream.readInt();
            byte[] value = new byte[valueSize];
            iStream.read(value, 0, valueSize);

            put(new String(key, "UTF-8"), new String(value, "UTF-8"));
        } catch (IOException e) {
            System.err.println("Error in reading: " + e.getMessage());
            System.exit(-1);
        }
    }

    public void load() {
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                try {
                    DataInputStream stream = new DataInputStream(Files.newInputStream(
                            dbPath.resolve(i + ".dir" + File.separator + j + ".dat")));
                        while (stream.available() > 0) {
                            readFromFile(stream);
                        }
                    } catch (IOException e) {
                        System.err.println("can't create DataInputStream : " + e.getMessage());
                        System.exit(-1);
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
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    if (Files.exists(dbPath.resolve(i + ".dir" + File.separator + j + ".dat"))) {
                        Files.delete(dbPath.resolve(i + ".dir" + File.separator + j + ".dat"));
                    }
                }
                if (Files.exists(dbPath.resolve(i + ".dir" + File.separator))) {
                    Files.delete(dbPath.resolve(i + ".dir" + File.separator));
                }
            }
        } catch (IOException e) {
            System.err.println("Can't delete from disk: " + e.getMessage());
            System.exit(-1);
        }
    }

    public void unload() {
        deleteFiles();
        boolean[] dir = new boolean[16];
        boolean[][] file = new boolean[16][16];
        DataOutputStream[][] streams = new DataOutputStream[16][16];
        try {

            if (!Files.exists(dbPath)) {
                Files.createDirectory(dbPath);
            }
            for (Map.Entry<String, String> entry : entrySet()) {
                int hashCode = entry.getKey().hashCode();
                int d = hashCode % 16;
                int f = hashCode / 16 % 16;
                if (!file[d][f]) {
                    if (!dir[d]) {
                        Files.createDirectory(dbPath.resolve(d + ".dir/"));
                        dir[d] = true;
                    }
                    streams[d][f] = new DataOutputStream(Files.newOutputStream(
                            dbPath.resolve(d + ".dir" + File.separator + f + ".dat")));
                    file[d][f] = true;
                }
                writeToFile(streams[d][f], entry.getKey(), entry.getValue());
            }

        } catch (IOException e) {

            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    if (streams[i][j] != null) {
                        try {
                            streams[i][j].close();
                        } catch (IOException ignored) {
                            continue;
                        }
                    }
                }
            }
            System.err.println("Can't write to disk: " + e.getMessage());
            System.exit(-1);

        } finally {

            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
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
