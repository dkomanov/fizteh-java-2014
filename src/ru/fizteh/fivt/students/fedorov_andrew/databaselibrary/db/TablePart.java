package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.db;

import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DBFileCorruptIOException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseIOException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Utility;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * This class represents a table part implemented as usual {@link java.util.HashMap} and stored in a separate
 * file.<br/>
 * This class is not thread-safe.
 * @author phoenix
 */
public class TablePart {
    public static final int READ_BUFFER_SIZE = 16 * 1024;
    /**
     * A pair (key, value) describes put. A pair (key, null) describes removal.
     */
    private final ThreadLocal<Map<String, String>> diffMap =
            ThreadLocal.withInitial(() -> new HashMap<String, String>());
    private Path tablePartFilePath;
    /**
     * Map with last changes that are written to the file system.<br/>
     */
    private Map<String, String> lastCommittedMap;

    /**
     * Private constructor for cloning.
     */
    private TablePart() {

    }

    /**
     * Initalizes a new filemap object assigned to the specified file.<br/>
     * @param tablePartFilePath
     *         path to database file. If it does not exist, a new empty file is created.
     */
    public TablePart(Path tablePartFilePath) {
        if (tablePartFilePath == null) {
            throw new IllegalArgumentException("Please specify database file path");
        }

        this.tablePartFilePath = tablePartFilePath;

        lastCommittedMap = new HashMap<>();
    }

    public String get(String key) {
        if (diffMap.get().containsKey(key)) {
            String value = diffMap.get().get(key);
            return value;
        } else {
            return lastCommittedMap.get(key);
        }
    }

    public Path getTablePartFilePath() {
        return tablePartFilePath;
    }

    public Set<String> keySet() {
        return makeNewActualVersion().keySet();
    }

    public String put(String key, String value) {
        String oldValue = get(key);
        diffMap.get().put(key, value);
        return oldValue;
    }

    /**
     * Reads database from file system (all previous data is purged).<br/>
     * If an error occurs the state before this operation is recovered.<br/>
     * Thread-local uncommitted diffs are not effected.<br/>
     * @throws ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DBFileCorruptIOException
     */
    @SuppressWarnings("unchecked")
    public void readFromFile() throws DBFileCorruptIOException {
        // For recover purposes.
        Map<String, String> oldLastCommittedMap = lastCommittedMap;
        lastCommittedMap = new HashMap<>();

        try (DataInputStream stream = new DataInputStream(
                new FileInputStream(
                        tablePartFilePath.toString()))) {
            // Structure: (no spaces or newlines) <key 1 bytes>00<4
            // bytes:offset> <key 2 bytes>00<4 bytes:offset> ... <value 1 bytes>
            // <value 2 bytes>...

            byte[] buffer = new byte[1024];
            int bufferSize = 0;

            byte[] temporaryBuffer = new byte[READ_BUFFER_SIZE];

            while (true) {
                int read = stream.read(temporaryBuffer);
                if (read < 0) {
                    break;
                }

                buffer = Utility.insertArray(temporaryBuffer, 0, read, buffer, bufferSize);
                bufferSize += read;
            }

            TreeMap<Integer, String> offsets = new TreeMap<>();

            int bufferOffset = 0;

            int nextValue = bufferSize - 1;

            // reading keys and value shift information
            for (int i = 0; i < bufferSize; ) {
                if (i == nextValue) {
                    throw new DBFileCorruptIOException(
                            String.format(
                                    "Attempt to read key part from %s to %s, but value should "
                                    + "start here", bufferOffset, i));
                }
                if (buffer[i] == 0) {
                    String currentKey = new String(buffer, bufferOffset, i - bufferOffset, "UTF-8");
                    if (i + 4 >= bufferSize) {
                        throw new DBFileCorruptIOException(
                                String.format(
                                        "There is no value offset for key '%s' after byte %s",
                                        currentKey,
                                        i));
                    }
                    int valueShift = 0;
                    for (int j = 0; j < 4; j++, i++) {
                        valueShift = (valueShift << 8) | (buffer[i] & 0xFF);
                    }
                    bufferOffset = i;

                    offsets.put(valueShift, currentKey);

                    nextValue = offsets.firstKey();

                    if (i > nextValue) {
                        throw new DBFileCorruptIOException(
                                String.format(
                                        "Value shift for key '%s' is to early: %s; current " + "position: %s",
                                        currentKey,
                                        valueShift,
                                        i));
                    } else if (i == nextValue) {
                        break;
                    }
                } else {
                    i++;
                }
            }

            // Empty map.
            if (offsets.isEmpty()) {
                return;
            }

            // Reading values.

            // Value matching this key is now being built.
            String currentKey = offsets.get(nextValue);

            // Next value start boundary.
            offsets.remove(nextValue);

            // Reading up to the last value (exclusive).
            while (!offsets.isEmpty()) {
                nextValue = offsets.firstKey();

                String value = new String(buffer, bufferOffset, nextValue - bufferOffset);
                lastCommittedMap.put(currentKey, value);

                bufferOffset = nextValue;
                currentKey = offsets.get(nextValue);

                offsets.remove(nextValue);
            }

            // Putting the last value.
            String value = new String(buffer, bufferOffset, bufferSize - bufferOffset);
            lastCommittedMap.put(currentKey, value);
        } catch (IOException exc) {
            // Recover.
            lastCommittedMap = oldLastCommittedMap;
            throw new DBFileCorruptIOException(
                    "Failed to read data from file: " + tablePartFilePath.toString(), exc);
        }

        // Everything went ok.
    }

    public String remove(String key) {
        if (diffMap.get().containsKey(key)) {
            String oldValue = diffMap.get().get(key);
            // Already removed.
            if (oldValue == null) {
                return null;
            } else {
                // Postponed put will be cancelled.
                diffMap.get().remove(key);
                return oldValue;
            }
        } else {
            diffMap.get().put(key, null);
            return lastCommittedMap.get(key);
        }
    }

    /**
     * Makes a separate up-to-date version which is a commit of the thread diff to the clone of {@link
     * #lastCommittedMap}.
     * @return Separate actual version. Changes in this instance have not effect on true database state.
     */
    private Map<String, String> makeNewActualVersion() {
        return makeActualVersion(new HashMap<String, String>(lastCommittedMap));
    }

    /**
     * Convenience method for private actual version supplying.
     * @param actualVersion
     *         Map to commit thread diffs to.
     * @return actualVersion (the same instance) with committed diffs.
     */
    private Map<String, String> makeActualVersion(Map<String, String> actualVersion) {
        for (Entry<String, String> e : diffMap.get().entrySet()) {
            if (e.getValue() == null) {
                actualVersion.remove(e.getKey());
            } else {
                actualVersion.put(e.getKey(), e.getValue());
            }
        }

        return actualVersion;
    }

    public int size() {
        return makeNewActualVersion().size();
    }

    /**
     * Writes changes to the file.
     * @throws IOException
     */
    private void writeToFile() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(1024);
        Iterator<String> keyIterator = lastCommittedMap.keySet().iterator();

        Charset charset = Charset.forName("UTF-8");

        int[] shiftPositions = new int[lastCommittedMap.size()];

        byte[] intZero = new byte[] {0, 0, 0, 0};

        int keyID = 0;

        while (keyIterator.hasNext()) {
            stream.write(keyIterator.next().getBytes(charset));
            shiftPositions[keyID] = stream.size();
            keyID++;
            stream.write(intZero);
        }

        int[] links = new int[lastCommittedMap.size()];

        keyID = 0;
        keyIterator = lastCommittedMap.keySet().iterator();
        while (keyIterator.hasNext()) {
            links[keyID] = stream.size();
            keyID++;
            stream.write(lastCommittedMap.get(keyIterator.next()).getBytes(charset));
        }

        byte[] bytes = stream.toByteArray();

        for (int i = 0, len = links.length; i < len; i++) {
            int pos = shiftPositions[i];
            int value = links[i];

            bytes[pos] = (byte) ((value >>> 24) & 0xFF);
            bytes[pos + 1] = (byte) ((value >>> 16) & 0xFF);
            bytes[pos + 2] = (byte) ((value >>> 8) & 0xFF);
            bytes[pos + 3] = (byte) (value & 0xFF);
        }

        // If db file not exists, create new empty file.
        if (!Files.exists(tablePartFilePath)) {
            // Not creating file that will be empty.
            if (bytes.length == 0) {
                return;
            }

            // Creating parent directories.
            Path tablePartFileParent = tablePartFilePath.getParent();
            if (!Files.exists(tablePartFileParent)) {
                Files.createDirectory(tablePartFileParent);
            }
            Files.createFile(tablePartFilePath);
        }

        try (DataOutputStream output = new DataOutputStream(
                new FileOutputStream(
                        tablePartFilePath.toString()))) {
            output.write(bytes);
        }
    }

    public int commit() throws DatabaseIOException {
        int diffsCount = getUncommittedChangesCount();

        if (diffsCount > 0) {
            makeActualVersion(lastCommittedMap);
            diffMap.get().clear();
            try {
                writeToFile();
            } catch (IOException exc) {
                throw new DatabaseIOException("Failed to persist table", exc);
            }
        }

        return diffsCount;
    }

    public int rollback() {
        int diffsCount = getUncommittedChangesCount();
        diffMap.get().clear();
        return diffsCount;
    }

    public int getUncommittedChangesCount() {
        return diffMap.get().size();
    }
}
