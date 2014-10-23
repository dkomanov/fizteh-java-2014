package ru.fizteh.fivt.students.moskupols.multifilehashmap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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

    private final Path tableRoot;
    private int totalSize;
    private LastAccessedChunksCache chunksCache;
    private Set<ChunkID> chunksPresent;

    public MultiFileMap(Path tableRoot) {
        this(tableRoot, 4);
    }

    public MultiFileMap(Path tableRoot, int cacheSize) {
        this.tableRoot = tableRoot;
        totalSize = 0;
        chunksCache = new LastAccessedChunksCache(cacheSize);
        chunksPresent = new HashSet<>();

        if (!Files.exists(this.tableRoot))
            throw new IllegalStateException(String.format("Directory %s does not exist", this.tableRoot));
        if (!Files.isDirectory(this.tableRoot))
            throw new IllegalStateException(String.format("%s is not directory", this.tableRoot));

        reinitialize();
    }

    public void reinitialize() {
        totalSize = 0;
        chunksCache.clear();
        chunksPresent.clear();

        File root = tableRoot.toFile();
        if (!root.isDirectory())
            throw new IllegalStateException(String.format("%s is not directory", tableRoot));

        for (int dirNum = 0; dirNum < DIR_COUNT; dirNum++) {
            Path dirPath = tableRoot.resolve(ChunkID.dirNameForNum(dirNum));
            if (!Files.isDirectory(dirPath))
                throw new IllegalStateException(String.format("%s is not directory", dirPath));

            for (int fileNum = 0; fileNum < DIR_FILE_COUNT; fileNum++) {
                Path filePath = tableRoot.resolve(ChunkID.fileNameForNum(fileNum));
                if (!Files.exists(filePath))
                    continue;
                if (!Files.isRegularFile(filePath))
                    throw new IllegalStateException(String.format("%s is not file", filePath));

                chunksPresent.add(new ChunkID(dirNum, fileNum));

                try {
                    totalSize += new FileMap(filePath).size();
                } catch (Exception e) {
                    throw new IllegalStateException(String.format("Chunk at %s is corrupted", filePath), e);
                }
            }
        }
    }

    public String getName() {
        return tableRoot.getFileName().toString();
    }

    public String get(String key) throws Exception {
        return getChunkWithKey(key).get(key);
    }

    public String put(String key, String value) throws Exception {
        final String ret = getChunkWithKey(key).put(key, value);
        if (null == ret)
            totalSize += 1;
        return ret;
    }

    public String remove(String key) throws Exception {
        final String ret = getChunkWithKey(key).remove(key);
        if (ret != null)
            totalSize -= 1;
        return ret;
    }

    public int size() {
        return totalSize;
    }

    public List<String> list() {
        List<String> ret = new ArrayList<>();
        for (FileMap chunk : chunksCache.values()) {
            ret.addAll(chunk.list());
        }
        Set<ChunkID> toProcess = new HashSet<>(chunksPresent);
        toProcess.removeAll(chunksCache.keySet());
        for (ChunkID id : toProcess) {
            try {
                ret.addAll(new FileMap(id.getFilePath(tableRoot)).list());
            } catch (Exception e) {
                throw new IllegalStateException(String.format("Chunk %s is corrupted", id), e);
            }
        }
        return ret;
    }

    private FileMap getChunkWithKey(String key) throws Exception {
        ChunkID id = ChunkID.forKey(key);
        FileMap ret = chunksCache.get(id);
        if (null == ret) {
            ret = new FileMap(id.getFilePath(tableRoot));
            chunksPresent.add(id);
            chunksCache.put(id, ret);
        }
        return ret;
    }

    public void flush() throws IOException {
        chunksCache.flush();
    }

    @Override
    protected void finalize() throws Throwable {
        flush();
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
                    flushChunk(eldest.getKey(), eldest.getValue());
                } catch (IOException ignored) {
                }
                return true;
            }
            return false;
        }

        private void flushChunk(ChunkID id, FileMap chunk) throws IOException {
            final Path filePath = chunk.targetPath;
            final Path dirPath = filePath.getParent();
            if (chunk.size() == 0) {
                chunksPresent.remove(id);
                if (Files.deleteIfExists(filePath)) {
                    dirPath.toFile().delete();
                }
            } else {
                Files.createDirectory(dirPath);
                chunk.flush();
            }
        }

        protected void flush() throws IOException {
            for (Map.Entry<ChunkID, FileMap> entry : entrySet()) {
                flushChunk(entry.getKey(), entry.getValue());
            }
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

        public static String dirNameForNum(int dirNum) {
            return String.valueOf(dirNum) + ".dir";
        }

        public static String fileNameForNum(int fileNum) {
            return String.valueOf(fileNum) + ".dat";
        }

        public Path getFilePath(Path rootPath) {
            return rootPath
                    .resolve(dirNameForNum(dirNum))
                    .resolve(fileNameForNum(fileNum));
        }

        @Override
        public String toString() {
            return String.format("(%d, %d)", dirNum, fileNum);
        }

        public boolean equals(ChunkID that) {
            return dirNum == that.dirNum && fileNum == that.fileNum;
        }
    }
}
