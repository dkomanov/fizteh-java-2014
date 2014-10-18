package ru.fizteh.fivt.students.andreyzakharov.stringfilemap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class FileMap extends HashMap<String, String> {
    Path dbPath;

    public FileMap(Path path) {
        dbPath = path;
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                //
            }
        }
    }

    void readKeyValue(DataInputStream is) throws IOException {
        int keyLen = is.readInt();
        byte[] key = new byte[keyLen];
        is.read(key, 0, keyLen);
        int valLen = is.readInt();
        byte[] value = new byte[valLen];
        is.read(value, 0, valLen);
        put(new String(key, "UTF-8"), new String(value, "UTF-8"));
    }

    public void load() throws ConnectionInterruptException {
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                try (DataInputStream stream = new DataInputStream(
                        Files.newInputStream(dbPath.resolve(i + ".dir/" + j + ".dat")))) {
                    while (stream.available() > 0) {
                        readKeyValue(stream);
                    }
                } catch (IOException e) {
                    // empty folder is a valid table
                }
            }
        }
    }

    void writeKeyValue(DataOutputStream os, String keyString, String valueString) throws IOException {
        byte[] key = keyString.getBytes("UTF-8");
        byte[] value = valueString.getBytes("UTF-8");
        os.writeInt(key.length);
        os.write(key);
        os.writeInt(value.length);
        os.write(value);
    }

    public void unload() throws ConnectionInterruptException {
        try {
            clearFiles();
        } catch (ConnectionInterruptException e) {
            //
        }

        boolean[] dirUsed = new boolean[16];
        boolean[][] fileUsed = new boolean[16][16];
        DataOutputStream[][] streams = new DataOutputStream[16][16];
        try {
            if (!Files.exists(dbPath)) {
                Files.createDirectory(dbPath);
            }
            for (HashMap.Entry<String, String> entry : entrySet()) {
                int hash = entry.getKey().hashCode();
                int d = (hash % 16 < 0) ? hash % 16 + 16 : hash % 16;
                hash /= 16;
                int f = (hash % 16 < 0) ? hash % 16 + 16 : hash % 16;
                if (!fileUsed[d][f]) {
                    if (!dirUsed[d]) {
                        Files.createDirectory(dbPath.resolve(d + ".dir/"));
                        dirUsed[d] = true;
                    }
                    streams[d][f] = new DataOutputStream(
                            Files.newOutputStream(dbPath.resolve(d + ".dir/" + f + ".dat")));
                    fileUsed[d][f] = true;
                }
                writeKeyValue(streams[d][f], entry.getKey(), entry.getValue());
            }
        } catch (IOException e) {
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    if (streams[i][j] != null) {
                        try {
                            streams[i][j].close();
                        } catch (IOException e1) {
                            continue;
                        }
                    }
                }
            }
            throw new ConnectionInterruptException("database: writing to disk failed");
        } finally {
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    if (streams[i][j] != null) {
                        try {
                            streams[i][j].close();
                        } catch (IOException e) {
                            continue;
                        }
                    }
                }
            }
        }
    }

    public void clearFiles() throws ConnectionInterruptException {
        try {
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    if (Files.exists(dbPath.resolve(i + ".dir/" + j + ".dat"))) {
                        Files.delete(dbPath.resolve(i + ".dir/" + j + ".dat"));
                    }
                }
                if (Files.exists(dbPath.resolve(i + ".dir/"))) {
                    Files.delete(dbPath.resolve(i + ".dir/"));
                }
            }
        } catch (IOException e) {
            throw new ConnectionInterruptException("database: deleting table failed");
        }
    }
}
