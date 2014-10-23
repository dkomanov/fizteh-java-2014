package ru.fizteh.fivt.students.fedorov_andrew.filemap;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class FileMap {
    /**
     * Path to database file
     */
    private String dbFileName;

    private HashMap<String, String> databaseMap;

    /**
     * Private constructor for cloning
     */
    private FileMap() {

    }

    /**
     * Initalizes a new filemap object assigned to specified database file.<br/>
     * 
     * @param dbFileName
     *            path to database file. If it does not exist, a new empty file
     *            is created.
     * @throws HandledException
     *             if failed to create database file or {@code dbFileName} is
     *             null.
     */
    public FileMap(String dbFileName) throws HandledException {
        if (dbFileName == null) {
            Utility.handleError("Please specify database file path");
        }

        Path dbPath = Paths.get(dbFileName);

        // if db file not exists, create new empty file
        if (!Files.exists(dbPath)) {
            try {
                Files.createFile(dbPath);
            } catch (IOException exc) {
                Utility.handleError(exc,
                        "Cannot establish proper db connection", true);
            }
        }

        this.dbFileName = dbFileName;
    }

    /**
     * Silently clones the object - no changes in file system stated in
     * {@link #FileMap(String)} are made.
     */
    @SuppressWarnings("unchecked")
    @Override
    public FileMap clone() {
        FileMap fmap = new FileMap();
        fmap.databaseMap = (HashMap<String, String>) this.databaseMap.clone();
        fmap.dbFileName = this.dbFileName;
        return fmap;
    }

    public String get(String key) {
        return databaseMap.get(key);
    }

    HashMap<String, String> getDatabaseMap() {
        return databaseMap;
    }

    public String getDbFileName() {
        return dbFileName;
    }

    public Set<String> keySet() {
        return databaseMap.keySet();
    }

    public String put(String key, String value) {
        return databaseMap.put(key, value);
    }

    /**
     * Reads database from given file (all previous data is purged).<br/>
     * If an error occurs the state before this operation is recovered.
     * 
     * @param filename
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void readDatabaseMap() throws IOException, DBFileCorruptException {
        /*
         * if an exception occurs and database is cloned, recover if cloned
         * object is null - no recover is performed.
         */
        HashMap<String, String> cloneDBMap = null;

        if (databaseMap == null) {
            databaseMap = new HashMap<String, String>();
        } else {
            cloneDBMap = (HashMap<String, String>) databaseMap.clone();
            databaseMap.clear();
        }

        try (DataInputStream stream = new DataInputStream(new FileInputStream(
                dbFileName))) {
            /*
             * structure: (no spaces or newlines) <key 1 bytes>00<4
             * bytes:offset> <key 2 bytes>00<4 bytes:offset> ... <value 1 bytes>
             * <value 2 bytes>...
             */

            byte[] buffer = new byte[1024];
            int bufferSize = 0;

            byte[] temporaryBuffer = new byte[Shell.READ_BUFFER_SIZE];

            while (true) {
                int read = stream.read(temporaryBuffer);
                if (read < 0) {
                    break;
                }

                buffer = Utility.insertArray(temporaryBuffer, 0, read, buffer,
                        bufferSize);
                bufferSize += read;
            }

            TreeMap<Integer, String> offsets = new TreeMap<>();

            int bufferOffset = 0;

            int nextValue = Integer.MAX_VALUE;

            // reading keys and value shift information
            for (int i = 0; i < bufferSize; ) {
                if (i == nextValue) {
                    throw new DBFileCorruptException(
                            String.format(
                                    "attempt to read key"
                                            + " part from %s to %s, but value should start here",
                                    bufferOffset, i));
                }
                if (buffer[i] == 0) {
                    String currentKey = new String(buffer, bufferOffset, i
                            - bufferOffset, "UTF-8");
                    bufferOffset = i + 1;
                    if (i + 4 >= bufferSize) {
                        throw new DBFileCorruptException(
                                String.format(
                                        "there is no value offset for key '%s' after byte %s",
                                        currentKey, i));
                    }
                    int valueShift = 0;
                    for (int j = 0; j < 4; j++, i++) {
                        valueShift = (valueShift << 8) | (buffer[i] & 0xFF);
                    }
                    bufferOffset = i;

                    offsets.put(valueShift, currentKey);

                    nextValue = offsets.firstKey();

                    if (i > nextValue) {
                        throw new DBFileCorruptException(
                                String.format(
                                        "value shift for key '%s' is to early: %s; current position: %s",
                                        currentKey, valueShift, i));
                    } else if (i == nextValue) {
                        break;
                    }
                } else {
                    i++;
                }
            }

            // empty map
            if (offsets.isEmpty()) {
                return;
            }

            // reading values
            String currentKey = offsets.get(nextValue); // value matching this
            // key is now being
            // built
            offsets.remove(nextValue); // next value start boundary

            // reading up to the last value (exclusive)
            while (!offsets.isEmpty()) {
                nextValue = offsets.firstKey();

                String value = new String(buffer, bufferOffset, nextValue
                        - bufferOffset);
                databaseMap.put(currentKey, value);

                bufferOffset = nextValue;
                currentKey = offsets.get(nextValue);

                offsets.remove(nextValue);
            }

            // putting the last value
            String value = new String(buffer, bufferOffset, bufferSize
                    - bufferOffset);
            databaseMap.put(currentKey, value);
        } catch (Throwable exc) {
            // recover
            if (cloneDBMap != null) {
                databaseMap = cloneDBMap;
            }
            throw exc;
        }
    }

    public String remove(String key) {
        return databaseMap.remove(key);
    }

    /**
     * Sets the given database map instead of that we had.
     * 
     * @param databaseMap
     */
    void setDatabaseMap(HashMap<String, String> databaseMap) {
        if (databaseMap == null) {
            throw new NullPointerException("I do not want null hashmaps here");
        }
        this.databaseMap = databaseMap;
    }

    public void writeDatabaseMap() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(1024);
        Iterator<String> keyIterator = databaseMap.keySet().iterator();

        Charset charset = Charset.forName("UTF-8");

        int[] shiftPositions = new int[databaseMap.size()];

        byte[] intZero = new byte[] {0, 0, 0, 0 };

        int keyID = 0;

        while (keyIterator.hasNext()) {
            stream.write(keyIterator.next().getBytes(charset));
            shiftPositions[keyID] = stream.size();
            keyID++;
            stream.write(intZero);
        }

        int[] links = new int[databaseMap.size()];

        keyID = 0;
        keyIterator = databaseMap.keySet().iterator();
        while (keyIterator.hasNext()) {
            links[keyID] = stream.size();
            keyID++;
            stream.write(databaseMap.get(keyIterator.next()).getBytes(charset));
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

        try (DataOutputStream output = new DataOutputStream(
                new FileOutputStream(dbFileName))) {
            output.write(bytes);
        }
    }
}
