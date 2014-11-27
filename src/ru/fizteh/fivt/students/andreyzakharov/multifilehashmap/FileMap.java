package ru.fizteh.fivt.students.andreyzakharov.multifilehashmap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileMap extends HashMap<String, String> {
    Path dbPath;
    private static Pattern fileNamePattern = Pattern.compile("^([0-9]|1[0-5])\\.dat$");
    private static Pattern directoryNamePattern = Pattern.compile("^([0-9]|1[0-5])\\.dir$");

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

    private static class DfPair {
        public int d;
        public int f;
    }

    private static DfPair getHash(String key) {
        DfPair p = new DfPair();
        int hash = key.hashCode();
        p.d = (hash % 16 < 0) ? hash % 16 + 16 : hash % 16;
        hash /= 16;
        p.f = (hash % 16 < 0) ? hash % 16 + 16 : hash % 16;
        return p;
    }

    private String readKeyValue(DataInputStream is) throws IOException, ConnectionInterruptException {
        int keyLen = is.readInt();
        byte[] key = new byte[keyLen];
        int keyRead = is.read(key, 0, keyLen);
        if (keyRead != keyLen) {
            throw new ConnectionInterruptException("database: db file is invalid");
        }
        int valLen = is.readInt();
        byte[] value = new byte[valLen];
        int valRead = is.read(value, 0, valLen);
        if (valRead != valLen) {
            throw new ConnectionInterruptException("database: db file is invalid");
        }

        put(new String(key, "UTF-8"), new String(value, "UTF-8"));
        return new String(key, "UTF-8");
    }

    public void load() throws ConnectionInterruptException {
        clear();
        try (DirectoryStream<Path> tableStream = Files.newDirectoryStream(dbPath)) {
            for (Path dir : tableStream) {
                Matcher dirMatcher = directoryNamePattern.matcher(dir.getFileName().toString());
                if (!dirMatcher.find()) {
                    throw new ConnectionInterruptException("database: extra directories in table folder");
                }
                if (!Files.isDirectory(dir)) {
                    throw new ConnectionInterruptException("database: extra files in table folder");
                }
                int d = Integer.decode(dirMatcher.group(1));
                try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(dir)) {
                    for (Path file : dirStream) {
                        Matcher fileMatcher = fileNamePattern.matcher(file.getFileName().toString());
                        if (!fileMatcher.find()) {
                            throw new ConnectionInterruptException("database: extra files in table folder");
                        }
                        int f = Integer.decode(fileMatcher.group(1));
                        try (DataInputStream fileStream = new DataInputStream(
                                Files.newInputStream(dir.resolve(file)))) {
                            while (fileStream.available() > 0) {
                                String key = readKeyValue(fileStream);
                                DfPair p = getHash(key);
                                if (d != p.d || f != p.f) {
                                    throw new ConnectionInterruptException(
                                            "database: key/file correspondence is invalid");
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new ConnectionInterruptException("database: read failed: " + e.getMessage());
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
                DfPair p = getHash(entry.getKey());
                if (!fileUsed[p.d][p.f]) {
                    if (!dirUsed[p.d]) {
                        Files.createDirectory(dbPath.resolve(p.d + ".dir/"));
                        dirUsed[p.d] = true;
                    }
                    streams[p.d][p.f] = new DataOutputStream(
                            Files.newOutputStream(dbPath.resolve(p.d + ".dir/" + p.f + ".dat")));
                    fileUsed[p.d][p.f] = true;
                }
                writeKeyValue(streams[p.d][p.f], entry.getKey(), entry.getValue());
            }
        } catch (IOException e) {
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    if (streams[i][j] != null) {
                        try {
                            streams[i][j].close();
                        } catch (IOException e1) {
                            //
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
                            //
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
