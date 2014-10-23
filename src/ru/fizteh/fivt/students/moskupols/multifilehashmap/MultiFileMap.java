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

        final File[] dirs = root.listFiles();
        if (dirs == null)
            throw new IllegalStateException(String.format("%s is not directory", tableRoot));

        for (File dir : dirs) {
            final File[] files = dir.listFiles();
            if (files == null)
                throw new IllegalStateException(String.format("%s is not directory", dir.toPath()));

            for (File file : files) {
                final ChunkID id = ChunkID.fromPath(file.toPath());
                chunksPresent.add(id);

                try {
                    totalSize += chunksCache.getChunkWithID(id).size();
                } catch (Exception e) {
                    throw new IllegalStateException(String.format("Chunk at %s is corrupted", file.toPath()), e);
                }
            }
        }
    }

    public String getName() {
        return tableRoot.getFileName().toString();
    }

    public String get(String key) throws Exception {
        return chunksCache.getChunkWithKey(key).get(key);
    }

    public String put(String key, String value) throws Exception {
        final String ret = chunksCache.getChunkWithKey(key).put(key, value);
        if (null == ret)
            totalSize += 1;
        return ret;
    }

    public String remove(String key) throws Exception {
        final String ret = chunksCache.getChunkWithKey(key).remove(key);
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

    public void clear() {
        try {
            for (File dir : tableRoot.toFile().listFiles()) {
                for (File file : dir.listFiles()) {
                    assert file.delete();
                }
                assert dir.delete();
            }
        } catch (NullPointerException | AssertionError e) {
            throw new IllegalStateException(String.format("Couldn't clear %s", tableRoot));
        }
        reinitialize();
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

        private FileMap getChunkWithKey(String key) throws Exception {
            return getChunkWithID(ChunkID.forKey(key));
        }

        private FileMap getChunkWithID(ChunkID id) throws Exception {
            FileMap ret = get(id);
            if (null == ret) {
                ret = new FileMap(id.getFilePath(tableRoot));
                chunksPresent.add(id);
                put(id, ret);
            }
            return ret;
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
        static int DIR_COUNT = 16;
        static int DIR_FILE_COUNT = 16;

        final int dirNum, fileNum;

        public ChunkID(int dirNum, int fileNum) {
            this.dirNum = dirNum;
            this.fileNum = fileNum;
        }

        public static ChunkID forKey(String key) {
            final int hashCode = key.hashCode();
            return new ChunkID(hashCode % DIR_COUNT, hashCode / DIR_COUNT % DIR_FILE_COUNT);
        }

        public static ChunkID fromPath(Path chunkPath) {
            if (!Files.isRegularFile(chunkPath)) {
                throw new IllegalArgumentException(String.format("%s is not file", chunkPath));
            }

            final String fileName = chunkPath.getFileName().toString();
            if (!fileName.matches("([0-9]|1[0-5])\\.dat")) {
                throw new IllegalArgumentException(String.format("%s is not chunk", chunkPath));
            }

            final Path dirPath = chunkPath.normalize().getParent();
            if (dirPath == null) {
                throw new IllegalArgumentException(String.format("Couldn't determine parent for %s", chunkPath));
            }
            final String dirName = dirPath.getFileName().toString();
            if (!dirName.matches("([0-9]|1[0-5])\\.dir")) {
                throw new IllegalArgumentException(String.format("%s is not chunk dir", dirPath));
            }

            return new ChunkID(
                    Integer.valueOf(dirName.substring(0, 2)),
                    Integer.valueOf(fileName.substring(0, 2)));
        }

        public Path getFilePath(Path rootPath) {
            return rootPath
                    .resolve(String.valueOf(dirNum) + ".dir")
                    .resolve(String.valueOf(fileNum) + ".dat");
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
