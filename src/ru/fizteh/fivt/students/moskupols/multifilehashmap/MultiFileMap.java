package ru.fizteh.fivt.students.moskupols.multifilehashmap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The MultiFileMap class is to implement Table interface somewhen.
 *
 * Stores key-value pairs in 64 files in 16 directories under specified tableRoot.
 * Uses FileMap class for storing.
 *
 * Caches up to cacheSize chunks in RAM.
 */
public class MultiFileMap {
    static int DIR_COUNT = 16;
    static int DIR_FILE_COUNT = 16;

    private final String name;
    private final Path rootPath;
    private LastAccessedChunksCache lastAccessedChunks;

    public MultiFileMap(String name, Path tableRoot) {
        this(name, tableRoot, 4);
    }

    public MultiFileMap(String name, Path tableRoot, int cacheSize) {
        // TODO: add checks on directory structure (including root.isDir)

        this.name = name;
        this.rootPath = tableRoot;
        lastAccessedChunks = new LastAccessedChunksCache(cacheSize);
    }

    public String getName() {
        return name;
    }

    public String get(String key) throws Exception {
        return getChunkWithKey(key).get(key);
    }

    public String put(String key, String value) throws Exception {
        return getChunkWithKey(key).put(key, value);
    }

    public String remove(String key) throws Exception {
        return getChunkWithKey(key).remove(key);
    }

    public int size() {
        // TODO
        return 0;
    }

    public List<String> list() {
        // TODO
        return null;
    }

    private FileMap getChunkWithKey(String key) throws Exception {
        ChunkID id = ChunkID.forKey(key);
        FileMap ret = lastAccessedChunks.get(id);
        if (null == ret) {
            ret = new FileMap(id.getFilePath(rootPath));
            lastAccessedChunks.put(id, ret);
        }
        return ret;
    }

    private void dumpChunk(FileMap chunk) throws IOException {
        final Path filePath = chunk.targetPath;
        final Path dirPath = filePath.getParent();
        if (chunk.size() == 0) {
            if (Files.deleteIfExists(filePath)) {
                dirPath.toFile().delete();
            }
        } else {
            Files.createDirectory(dirPath);
            chunk.dump();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        lastAccessedChunks.finalize();
        super.finalize();
    }

    class LastAccessedChunksCache extends LinkedHashMap<ChunkID, FileMap> {
        private final int maxSize;

        public LastAccessedChunksCache(int maxSize) {
            // Defaults according to http://docs.oracle.com/javase/6/docs/api/java/util/LinkedHashMap.html
            super(Integer.min(16, maxSize), (float) 0.75, true);
            this.maxSize = maxSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<ChunkID, FileMap> eldest) {
            if (size() > maxSize) {
                try {
                    dumpChunk(eldest.getValue());
                } catch (IOException ignored) {
                }
                return true;
            }
            return false;
        }

        @Override
        protected void finalize() throws Throwable {
            for (FileMap f: values()) {
                dumpChunk(f);
            }
            super.finalize();
        }
    }

    private static class ChunkID {
        final int dirNum, fileNum;

        public ChunkID(int dirNum, int fileNum) {
            this.dirNum = dirNum;
            this.fileNum = fileNum;
        }

        public static ChunkID forKey(String key) {
            final int hashCode = key.hashCode();
            return new ChunkID(hashCode % DIR_COUNT, hashCode / DIR_COUNT % DIR_FILE_COUNT);
        }

        public Path getFilePath(Path rootPath) {
            return rootPath
                    .resolve(String.valueOf(dirNum) + ".dir")
                    .resolve(String.valueOf(fileNum));
        }

        public boolean equals(ChunkID that) {
            return dirNum == that.dirNum && fileNum == that.fileNum;
        }
    }
}
