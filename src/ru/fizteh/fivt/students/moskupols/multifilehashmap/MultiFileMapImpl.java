package ru.fizteh.fivt.students.moskupols.multifilehashmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;

/**
 * The MultiFileMap class is to implement Table interface somewhen.
 *
 * Stores key-value pairs in 64 files in 16 directories under specified tableRoot.
 * Uses FileMap class for storing.
 *
 * Caches up to cacheSize chunks in RAM.
 */
public class MultiFileMapImpl implements MultiFileMap {
    private final Path tableRoot;
    private int totalSize;
    private LastAccessedChunksCache chunksCache;
    private Set<ChunkID> chunksPresent;

    public MultiFileMapImpl(Path tableRoot) throws IOException {
        this(tableRoot, 4);
    }

    /**
     * @param tableRoot root directory for the file structure.
     * @param cacheSize defaults to 4.
     * @throws IOException it tableRoot is not directory or some chunk is corrupted.
     * @throws java.io.FileNotFoundException if there is no tableRoot directory.
     */
    protected MultiFileMapImpl(Path tableRoot, int cacheSize) throws IOException {
        this.tableRoot = tableRoot;
        totalSize = 0;
        chunksCache = new LastAccessedChunksCache(cacheSize);
        chunksPresent = new HashSet<>();

        if (!Files.exists(this.tableRoot)) {
            throw new FileNotFoundException(String.format("Directory %s does not exist", this.tableRoot));
        }
        if (!Files.isDirectory(this.tableRoot)) {
            throw new IOException(String.format("%s is not directory", this.tableRoot));
        }

        reinitialize();
    }

    /**
     *
     * @throws IOException if there is something with tableRoot directory structure.
     */
    protected void reinitialize() throws IOException {
        totalSize = 0;
        chunksCache.clear();
        chunksPresent.clear();

        File root = tableRoot.toFile();

        final File[] dirs = root.listFiles();
        if (dirs == null) {
            throw new IOException(String.format("%s is not directory", tableRoot));
        }

        for (File dir : dirs) {
            final File[] files = dir.listFiles();
            if (files == null) {
                continue;
            }

            for (File file : files) {
                final ChunkID id = ChunkID.fromPath(file.toPath());
                chunksPresent.add(id);

                try {
                    totalSize += chunksCache.getChunkWithID(id).size();
                } catch (IOException e) {
                    throw new IOException(
                            String.format("Chunk at %s is corrupted: %s", file.toPath(), e.getMessage()), e);
                }
            }
        }
    }

    @Override
    public String getName() {
        return tableRoot.getFileName().toString();
    }

    @Override
    public String get(String key) throws IOException {
        return chunksCache.getChunkWithKey(key).get(key);
    }

    @Override
    public String put(String key, String value) throws IOException {
        final String ret = chunksCache.getChunkWithKey(key).put(key, value);
        if (null == ret) {
            totalSize += 1;
        }
        return ret;
    }

    @Override
    public String remove(String key) throws IOException {
        final String ret = chunksCache.getChunkWithKey(key).remove(key);
        if (ret != null) {
            totalSize -= 1;
        }
        return ret;
    }

    @Override
    public int size() {
        return totalSize;
    }

    @Override
    public List<String> list() throws IOException {
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
                throw new IOException(String.format("Chunk %s is corrupted", id), e);
            }
        }
        return ret;
    }

    @Override
    public void clear() throws IOException {
        final File[] dirs = tableRoot.toFile().listFiles();
        if (dirs == null) {
            throw new IOException(String.format("Couldn't clear %s", tableRoot));
        }
        for (File dir : dirs) {
            final File[] files = dir.listFiles();
            if (files == null) {
                if (!dir.delete()) {
                    throw new IOException(String.format("Couldn't delete %s", dir.getAbsolutePath()));
                }
                continue;
            }
            for (File file : files) {
                if (!file.delete()) {
                    throw new IOException(String.format("Couldn't delete %s", file.getAbsolutePath()));
                }
            }
            if (!dir.delete()) {
                throw new IOException(String.format("Couldn't delete %s", dir.getAbsolutePath()));
            }
        }
        reinitialize();
    }

    @Override
    public void flush() throws IOException {
        chunksCache.flush();
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
                } catch (IOException e) {
                    throw new IllegalStateException(
                            String.format("Couldn't save %s: %s", getName(), e.getMessage()), e);
                }
                return true;
            }
            return false;
        }

        private FileMap getChunkWithKey(String key) throws IOException {
            return getChunkWithID(ChunkID.forKey(key));
        }

        private FileMap getChunkWithID(ChunkID id) throws IOException {
            FileMap ret = get(id);
            if (null == ret) {
                try {
                    ret = new FileMap(id.getFilePath(tableRoot), new ChunkKeyChecker(id));
                } catch (Exception e) {
                    throw new IOException(e.getMessage(), e);
                }
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
                if (!Files.exists(dirPath)) {
                    Files.createDirectory(dirPath);
                }
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
        static final int DIR_COUNT = 16;
        static final int DIR_FILE_COUNT = 16;

        final int dirNum;
        final int fileNum;

        public ChunkID(int dirNum, int fileNum) {
            this.dirNum = dirNum;
            this.fileNum = fileNum;
        }

        public static ChunkID forKey(String key) {
            final int hashCode = key.hashCode();
            return new ChunkID(hashCode % DIR_COUNT, hashCode / DIR_COUNT % DIR_FILE_COUNT);
        }

        public static ChunkID fromPath(Path chunkPath) throws IllegalArgumentException {
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
                    Integer.valueOf(dirName.substring(0, dirName.indexOf('.'))),
                    Integer.valueOf(fileName.substring(0, fileName.indexOf('.'))));
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

        @Override
        public boolean equals(Object that) {
            if (that instanceof ChunkID) {
                final ChunkID thatChunk = (ChunkID) that;
                return dirNum == thatChunk.dirNum && fileNum == thatChunk.fileNum;
            }
            return super.equals(that);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new int[]{dirNum, fileNum});
        }
    }

    class ChunkKeyChecker implements Predicate<String> {
        private final ChunkID desired;

        public ChunkKeyChecker(ChunkID desired) {
            this.desired = desired;
        }

        @Override
        public boolean test(String key) {
            return ChunkID.forKey(key).equals(desired);
        }
    }
}
