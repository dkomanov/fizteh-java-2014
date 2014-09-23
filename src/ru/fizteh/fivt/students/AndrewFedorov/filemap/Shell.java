package ru.fizteh.fivt.students.AndrewFedorov.filemap;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

//java -Ddb.file=/home/phoenix/test/db.dat ru.fizteh.fivt.students.AndrewFedorov.filemap.Shell

/**
 * Class that represents a shell that can execute commands defined as classes
 * that implement {@link Command} interface in {@link Commands}.
 * 
 * @author phoenix
 * @see Command
 * @see Commands
 */
public class Shell {
    public final static String DB_FILE_PROPERTY_NAME = "db.file";

    public final static int READ_BUFFER_SIZE = 16 * 1024;

    public static void main(String[] args) {
	if (args.length == 0) {
	    new Shell().run(System.in);
	} else {
	    new Shell().run(args);
	}
    }

    /**
     * Database file location given as environment property.
     */
    private String dbFileName;

    /**
     * Available commands for invocation.
     */
    private Map<String, Class<?>> classesMap;

    /**
     * Map that represents stored file map.
     */
    private HashMap<String, String> databaseMap;

    /**
     * If the user is entering commands or it is package mode.
     */
    private boolean interactive;

    public Shell() {
	init();
    }

    /**
     * Executes command in this shell
     * 
     * @param command
     *            some shell command
     * @return returns true if execution finished correctly; false otherwise;
     */
    public boolean execute(String command) {
	String[] args = command.trim().split("[ ]{1,}");
	if (args[0].isEmpty()) {
	    return true;
	}

	Log.log(Shell.class, "Invocation request: " + Arrays.toString(args));

	Class<?> commandClass = classesMap.get(args[0]);
	if (commandClass == null) {
	    Log.log(Shell.class,
		    String.format("Command not found by name %s", args[0]));
	    System.err.println("Sorry, this command is missing");
	    return false;
	} else {
	    try {
		Command commandInstance = (Command) commandClass.newInstance();
		commandInstance.execute(this, args);

		return true;
	    } catch (Throwable exc) {
		if (!(exc instanceof HandledException)) {
		    // unhandled exception
		    Log.log(Shell.class, exc, String.format(
			    "Error during execution of %s", args[0]));
		    System.err.println(String.format(
			    "%s: Method execution error", args[0]));
		}
		// throw new RuntimeException(exc);
		return false;
	    }
	}
    }

    public String get(String key) {
	return databaseMap.get(key);
    }

    Map<String, String> getDatabaseMap() {
	return databaseMap;
    }

    public String getDbFileName() {
	return dbFileName;
    }

    /**
     * Prepares shell for further command interpretation
     */
    private void init() {
	Log.log(Shell.class, "Shell starting");

	// setting db file name
	dbFileName = System.getProperty(DB_FILE_PROPERTY_NAME);

	// reading map from db
	databaseMap = new HashMap<>();

	try {
	    Path dbPath = Paths.get(dbFileName);

	    if (!Files.exists(dbPath)) {
		try {
		    Files.createFile(dbPath);
		} catch (IOException exc) {
		    Utility.handleError(exc,
			    "Cannot establish proper db connection", true);
		}
	    }

	    readDatabaseMap();
	} catch (HandledException exc) {
	    System.exit(1);
	}

	// collecting commands
	Class<?>[] classes = Commands.class.getDeclaredClasses();
	classesMap = new HashMap<>(classes.length);

	for (int i = 0, len = classes.length; i < len; i++) {
	    boolean isShellCommand = true;
	    try {
		classes[i].asSubclass(Command.class);
	    } catch (ClassCastException exc) {
		isShellCommand = false;
	    }

	    if (isShellCommand) {
		String simpleName = Utility.simplifyClassName(classes[i]
			.getSimpleName());

		classesMap.put(simpleName, classes[i]);
		Log.log(Shell.class, String.format(
			"Class registered: %s as '%s'", classes[i].getName(),
			simpleName));
	    }
	}
    }

    public boolean isInteractive() {
	return interactive;
    }

    public Iterator<Entry<String, Class<?>>> listCommands() {
	return classesMap.entrySet().iterator();
    }

    /**
     * Reads database from given file. Any exception is wrapped into
     * {@link HandledException}.
     * 
     * @throws HandledException
     */
    void readDatabaseMap() throws HandledException {
	try {
	    readDatabaseMap(dbFileName);
	} catch (Throwable exc) {
	    if (exc instanceof HandledException) {
		throw (HandledException) exc;
	    } else {
		Utility.handleError(exc, "Cannot read database from file", true);
	    }
	}
    }

    /**
     * Reads database from given file
     * 
     * @param filename
     * @throws Exception
     */
    void readDatabaseMap(String filename) throws Exception {
	try (DataInputStream stream = new DataInputStream(new FileInputStream(
		filename))) {
	    /*
	     * structure: (no spaces or newlines) <key 1 bytes>00<4
	     * bytes:offset> <key 2 bytes>00<4 bytes:offset> ... <value 1 bytes>
	     * <value 2 bytes>...
	     */

	    byte[] buffer = new byte[1024];
	    int bufferSize = 0;

	    byte[] temporaryBuffer = new byte[READ_BUFFER_SIZE];

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
	    for (int i = 0; i < bufferSize;) {
		if (i == nextValue) {
		    throw new Exception(
			    String.format(
				    "DB file is corrupt: attempt to read key part from %s to %s, but value should start here",
				    bufferOffset, i));
		}
		if (buffer[i] == 0) {
		    String currentKey = new String(buffer, bufferOffset, i
			    - bufferOffset, "UTF-8");
		    bufferOffset = i + 1;
		    if (i + 4 >= bufferSize) {
			throw new Exception(
				String.format(
					"DB file is corrupt: there is no value offset for key '%s' after byte %s",
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
			throw new Exception(
				String.format(
					"DB file is corrupt: value shift for key '%s' is to early: %s; current position: %s",
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
	} catch (Exception exc) {
	    throw exc;
	}
    }

    /**
     * Execute commands from input stream. Commands are awaited forever.
     * 
     * @param stream
     */
    @SuppressWarnings("unchecked")
    public void run(InputStream stream) {
	interactive = true;

	BufferedReader reader = new BufferedReader(
		new InputStreamReader(stream), READ_BUFFER_SIZE);
	try {
	    while (true) {
		System.out.print(String.format("FileMap: %s $ ", dbFileName));

		String str = reader.readLine();

		// end of stream
		if (str == null) {
		    break;
		}

		// clone of the map before modifications
		HashMap<String, String> mapClone = (HashMap<String, String>) databaseMap
			.clone();

		String[] commands = str.split(";");
		for (int i = 0, len = commands.length; i < len; i++) {
		    boolean correct = execute(commands[i]);
		    if (!correct) {
			databaseMap = mapClone;
			try {
			    writeDatabaseMap(dbFileName);
			} catch (Exception exc) {
			    System.exit(1);
			}
			break;
		    }
		}
	    }
	} catch (IOException exc) {
	    Log.log(Shell.class, exc, "Cannot parse inputstream for shell");
	}

	try {
	    writeDatabaseMap();
	} catch (HandledException exc) {
	    System.exit(1);
	}
    }

    /**
     * Execute commands from command line arguments. Note that command line
     * arguments are first concatenated into a single line then split and
     * parsed.
     * 
     * @param args
     */
    public void run(String[] args) {
	interactive = false;

	StringBuilder sb = new StringBuilder();
	for (int i = 0, len = args.length; i < len; i++) {
	    sb.append((i == 0 ? "" : " ")).append(args[i]);
	}
	String cmds = sb.toString();
	String[] commands = cmds.split(";");

	for (int i = 0, len = commands.length; i < len; i++) {
	    boolean correct = execute(commands[i]);
	    if (!correct) {
		System.exit(1);
	    }
	}

	try {
	    writeDatabaseMap();
	} catch (HandledException exc) {
	    System.exit(1);
	}
	Log.close();
    }

    void setDatabaseMap(HashMap<String, String> databaseMap) {
	this.databaseMap = databaseMap;
    }

    /**
     * Writes database to given file. Any exception is wrapped into
     * {@link HandledException}.
     * 
     * @throws HandledException
     */
    void writeDatabaseMap() throws HandledException {
	try {
	    writeDatabaseMap(dbFileName);
	} catch (Throwable exc) {
	    if (exc instanceof HandledException) {
		throw (HandledException) exc;
	    } else {
		Utility.handleError(exc, "Cannot write database to file", true);
	    }
	}
    }

    void writeDatabaseMap(String filename) throws IOException {
	ByteArrayOutputStream stream = new ByteArrayOutputStream(1024);
	Iterator<String> keyIterator = databaseMap.keySet().iterator();

	Charset UTF8 = Charset.forName("UTF-8");

	int[] shiftPositions = new int[databaseMap.size()];

	byte[] int_zero = new byte[] { 0, 0, 0, 0 };

	int keyID = 0;

	while (keyIterator.hasNext()) {
	    stream.write(keyIterator.next().getBytes(UTF8));
	    shiftPositions[keyID] = stream.size();
	    keyID++;
	    stream.write(int_zero);
	}

	int[] links = new int[databaseMap.size()];

	keyID = 0;
	keyIterator = databaseMap.keySet().iterator();
	while (keyIterator.hasNext()) {
	    links[keyID] = stream.size();
	    keyID++;
	    stream.write(databaseMap.get(keyIterator.next()).getBytes(UTF8));
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
		new FileOutputStream(filename))) {
	    output.write(bytes);
	}
    }
}
