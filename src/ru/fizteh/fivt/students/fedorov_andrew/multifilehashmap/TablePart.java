package ru.fizteh.fivt.students.fedorov_andrew.multifilehashmap;

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
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import ru.fizteh.fivt.students.fedorov_andrew.multifilehashmap.exception.DBFileCorruptException;
import ru.fizteh.fivt.students.fedorov_andrew.multifilehashmap.exception.DatabaseException;
import ru.fizteh.fivt.students.fedorov_andrew.multifilehashmap.exception.TerminalException;
import ru.fizteh.fivt.students.fedorov_andrew.multifilehashmap.support.Utility;

/**
 * This class represents a table part implemented as usual {@link HashMap} and
 * stored in a separate file.
 * 
 * @author phoenix
 * 
 */
public class TablePart {
    private final static int INITIAL_BUFFER_SIZE = 2048;

    private Path tablePartFilePath;

    // Used HashMap instead of Map, because method clone() is used in the code.
    private HashMap<String, String> tablePartMap;

    /**
     * Private constructor for cloning
     */
    private TablePart() {

    }

    /**
     * Initalizes a new filemap object assigned to the specified file.<br/>
     * 
     * @param dbFileName
     *            path to database file. If it does not exist, a new empty file
     *            is created.
     * @throws TerminalException
     *             if failed to create database file or {@code dbFileName} is
     *             null.
     */
    public TablePart(Path tablePartFilePath) throws DatabaseException {
	if (tablePartFilePath == null) {
	    throw new IllegalArgumentException("Please specify database file path");
	}

	this.tablePartFilePath = tablePartFilePath;

	// if db file not exists, create new empty file
	if (!Files.exists(tablePartFilePath)) {
	    try {
		// creating parent directories
		Files.createDirectories(tablePartFilePath.getParent());
		Files.createFile(tablePartFilePath);
	    } catch (IOException exc) {
		throw new DatabaseException("Cannot establish proper db connection",
					    exc);
	    }
	}
    }

    /**
     * Silently clones the object - no changes in file system stated in
     * {@link #TablePart(String)} are made.
     */
    @SuppressWarnings("unchecked")
    @Override
    public TablePart clone() {
	TablePart fmap = new TablePart();
	fmap.tablePartMap = (HashMap<String, String>) this.tablePartMap.clone();
	fmap.tablePartFilePath = this.tablePartFilePath;
	return fmap;
    }

    public String get(String key) {
	return tablePartMap.get(key);
    }

    public Path getTablePartFilePath() {
	return tablePartFilePath;
    }

    HashMap<String, String> getTablePartMap() {
	return tablePartMap;
    }

    public Set<String> keySet() {
	return tablePartMap.keySet();
    }

    public String put(String key, String value) {
	return tablePartMap.put(key, value);
    }

    /**
     * Reads database from given file (all previous data is purged).<br/>
     * If an error occurs the state before this operation is recovered.
     * 
     * @param filename
     * @throws DBFileCorruptException
     */
    @SuppressWarnings("unchecked")
    public void readFromFile() throws DBFileCorruptException {
	/*
	 * If an exception occurs and database is cloned, recover.
	 * If cloned object is null - no recover is performed.
	 */
	HashMap<String, String> cloneDBMap = null;

	if (tablePartMap == null) {
	    tablePartMap = new HashMap<String, String>();
	} else {
	    cloneDBMap = (HashMap<String, String>) tablePartMap.clone();
	    tablePartMap.clear();
	}

	try (DataInputStream stream = new DataInputStream(new FileInputStream(tablePartFilePath.toString()))) {
	    /*
	     * Structure: (no spaces or newlines) <key 1 bytes>00<4
	     * bytes:offset> <key 2 bytes>00<4 bytes:offset> ... <value 1 bytes>
	     * <value 2 bytes>...
	     */

	    byte[] buffer = new byte[INITIAL_BUFFER_SIZE];
	    int bufferSize = 0;

	    byte[] temporaryBuffer = new byte[Shell.IO_BUFFER_SIZE];

	    while (true) {
		int read = stream.read(temporaryBuffer);
		if (read < 0) {
		    break;
		}

		buffer = Utility.insertArray(temporaryBuffer,
					     0,
					     read,
					     buffer,
					     bufferSize);
		bufferSize += read;
	    }

	    TreeMap<Integer, String> offsets = new TreeMap<>();

	    int bufferOffset = 0;

	    int nextValue = Integer.MAX_VALUE;

	    // Reading keys and value shift information.
	    for (int i = 0; i < bufferSize;) {
		if (i == nextValue) {
		    throw new DBFileCorruptException(String.format("Attempt to read key part from %s to %s, but value should start here",
								   bufferOffset,
								   i));
		}
		if (buffer[i] == 0) {
		    String currentKey = new String(buffer,
						   bufferOffset,
						   i - bufferOffset,
						   "UTF-8");
		    bufferOffset = i + 1;
		    if (i + 4 >= bufferSize) {
			throw new DBFileCorruptException(String.format("There is no value offset for key '%s' after byte %s",
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
			throw new DBFileCorruptException(String.format("Value shift for key '%s' is to early: %s; current position: %s",
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
	    String currentKey = offsets.get(nextValue);
	    // Value matching this key is now being built.
	    offsets.remove(nextValue); // Next value start boundary.

	    // Reading up to the last value (exclusive).
	    while (!offsets.isEmpty()) {
		nextValue = offsets.firstKey();

		String value = new String(buffer,
					  bufferOffset,
					  nextValue - bufferOffset);
		tablePartMap.put(currentKey, value);

		bufferOffset = nextValue;
		currentKey = offsets.get(nextValue);

		offsets.remove(nextValue);
	    }

	    // Putting the last value.
	    String value = new String(buffer,
				      bufferOffset,
				      bufferSize - bufferOffset);
	    tablePartMap.put(currentKey, value);
	} catch (IOException exc) {
	    // Recover.
	    if (cloneDBMap != null) {
		tablePartMap = cloneDBMap;
	    }
	    throw new DBFileCorruptException(exc);
	}
    }

    public String remove(String key) {
	return tablePartMap.remove(key);
    }

    /**
     * Sets the given table part map instead of that we had.
     * 
     * @param databaseMap
     */
    void setTablePartMap(HashMap<String, String> databaseMap) {
	if (databaseMap == null) {
	    throw new NullPointerException("I do not want null hashmaps here");
	}
	this.tablePartMap = databaseMap;
    }

    public int size() {
	return tablePartMap.size();
    }

    public void writeToFile() throws IOException {
	ByteArrayOutputStream stream = new ByteArrayOutputStream(Shell.IO_BUFFER_SIZE);

	Charset UTF8 = Charset.forName("UTF-8");

	int[] shiftPositions = new int[tablePartMap.size()];

	byte[] int_zero = new byte[] { 0, 0, 0, 0 };

	int keyID = 0;

	for (Entry<String, String> entry : tablePartMap.entrySet()) {
	    stream.write(entry.getKey().getBytes(UTF8));
	    shiftPositions[keyID] = stream.size();
	    keyID++;
	    stream.write(int_zero);
	}

	int[] links = new int[tablePartMap.size()];

	keyID = 0;
	for (Entry<String, String> entry : tablePartMap.entrySet()) {
	    links[keyID] = stream.size();
	    keyID++;
	    stream.write(entry.getValue().getBytes(UTF8));
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

	try (DataOutputStream output = new DataOutputStream(new FileOutputStream(tablePartFilePath.toString()))) {
	    output.write(bytes);
	}
    }
}
